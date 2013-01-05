package poems.optimiser;

import module.Module;
import program.Logger;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;

/**
 * Population with a genetic algorithm to evolve.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Population
{
  /**
   * logger
   */
  private static final Logger LOG = Logger.create(Population.class);
  /**
   * prototype
   */
  private final BTree<NumberedValue<Module>> prototype;
  /**
   * niche count (maximal number of active actions)
   */
  private final int nicheCount;
  /**
   * niche size (number of individuals in each niche)
   */
  private final int nicheSize;
  /**
   * population (array of individuals)
   */
  private final Individual population[];
  
  /**
   * Creates a new instance.
   * 
   * @param prototype prototype
   * @param nicheCount niche count (= length)
   * @param nicheSize niche size (= number of individuals in each niche)
   */
  public Population(final BTree<NumberedValue<Module>> prototype, final int nicheCount, final int nicheSize)
  {
    Population.LOG.log("Creating a population with %d niches, niche size %d...", nicheCount, nicheSize);
    
    this.prototype = prototype;
    this.nicheCount = nicheCount;
    this.nicheSize = nicheSize;
    this.population = new Individual[nicheCount * nicheSize];
    
    int index = 0;
    
    for (int n = 1; n <= nicheCount; n++)
    {
      Population.LOG.log("Populating niche %d...", n);
      
      for (int i = 0; i < nicheSize; i++)
      {
        // create random individual
        
        // n = niche (= count of active actions)
        // i = number of individual in the niche
        
        this.population[index++] = Individual.random(
            prototype,
            nicheCount,
            n);
      }
    }
  }
  
  /**
   * Starts an evolution and returns the best individual found.
   * 
   * @param generations generation count
   * @return the best individual
   */
  public Individual evolve(final int generations)
  {
    Population.LOG.log("Starting genetic algorithm with %d generations...", generations);
    
    Individual best = null;
    
    for (int ig = 0; ig < generations; ig++)
    {
      Population.LOG.log("GENERATION %d", ig + 1);
      
      // pick random niche
      
      final int low;
      final int high;
      
      if (Setup.NICHE_SELECTION)
      {
        final int niche = Utility.random(1, this.nicheCount);
        
        low = Population.getNicheLow(niche, this.nicheSize);
        high = Population.getNicheHigh(niche, this.nicheSize);
        
        Population.LOG.log("Picked random niche %d (bounds [%d, %d])...", niche, low, high);
      }
      else
      {
        low = 0;
        high = this.population.length - 1;
      }
      
      // in each generation, create new individual(s)
      
      if (Math.random() < Setup.PROB_CROSSOVER)
      {
        // by crossover
        
        Population.LOG.log("Doing crossover...");
        
        final Individual mother = this.select(low, high);
        final Individual father = this.select(low, high);
        
        final IndividualPair children = Individual.crossover(
            this.prototype,
            mother,
            father);
        
        this.accept(children.getFirst());
        this.accept(children.getSecond());
      }
      else
      {
        // by mutation
        
        Population.LOG.log("Doing mutation...");
        
        final Individual mutant = this.select(low, high);
        
        final Individual child = Individual.mutate(
            this.prototype,
            mutant);
        
        this.accept(child);
      }
      
      final Individual newBest = this.pickBest();
      
      if ((best == null) || newBest.isBetter(best))
      {
        best = newBest;
      }
    }
    
    // return the best solution
    
    Logger.evolved(this.population, best);
    
    Population.LOG.log("Evolution finished, returning best...");
    
    return best;
  }
  
  /**
   * Accept the new individual to the population. It may or may not be accepted
   * (depending on its fitness).
   * 
   * @param fresh fresh individual
   */
  private void accept(final Individual fresh)
  {
    Population.LOG.log("Accepting fresh individual with niche %d...", fresh.getNiche());
    
    if (fresh.getNiche() > 0)
    {
      // general case
      
      this.replaceWorse(fresh);
    }
    else
    {
      // the individual was all-NOP
      // replace by the random instead
      
      this.replaceWorse(Individual.random(
          this.prototype,
          fresh.getLength(),
          Utility.random(
              1,
              fresh.getLength())));
    }
  }
  
  /**
   * Replaces the individual with the same or lower fitness than the fresh one
   * and the same or lower number of active actions, by the fresh one.
   * 
   * @param fresh fresh individual
   */
  private void replaceWorse(final Individual fresh)
  {
    Population.LOG.log("Replacing worse by %s...", fresh.toString());
    
    for (int i = 0; i <= Population.getNicheHigh(fresh.getNiche(), this.nicheSize); i++)
    {
      if (this.population[i].isWorseOrEqual(fresh))
      {
        // replace worse
        
        Population.LOG.log("Replacing worse victim at %d...", i);
        
        this.population[i] = fresh;
        return;
      }
    }
    
    Population.LOG.log("No worse victim found.");
  }
  
  /**
   * Selects a random "good" individual from the population. The current
   * implementation uses a tournament selection.
   * 
   * @param low lowest index possible
   * @param high highest index possible
   * @return random "good" individual from the population
   */
  public Individual select(final int low, final int high)
  {
    Population.LOG.log("Selecting individual from the population (bounds [%d, %d])...", low, high);
    
    final Individual sparta = this.population[Utility.random(low, high)];
    final Individual slavia = this.population[Utility.random(low, high)];
    
    Population.LOG.log("Competitor 1: %s", sparta);
    Population.LOG.log("Competitor 2: %s", slavia);
    
    if (sparta == slavia)
    {
      return sparta;
    }
    
    if (sparta.isBetter(slavia))
    {
      // sparta is better
      
      Population.LOG.log("Competitor 1 is better.");
      
      return Utility.randomBoolean(0.05)
          ? slavia
          : sparta;
    }
    else if (slavia.isBetter(sparta))
    {
      // slavia is better
      
      Population.LOG.log("Competitor 2 is better.");
      
      return Utility.randomBoolean(0.05)
          ? sparta
          : slavia;
    }
    else
    {
      // both are equal
      
      Population.LOG.log("Both competitors equal, choosing random.");
      
      return Utility.randomBoolean()
          ? sparta
          : slavia;
    }
  }
  
  /**
   * Finds and returns the best individual in the population.
   * 
   * @return the best individual in the population
   */
  public Individual pickBest()
  {
    Population.LOG.log("Searching for the best individual...");
    
    Individual best = null;
    
    for (final Individual temp : this.population)
    {
      if ((best == null) || temp.isBetterOrEqual(best))
      {
        best = temp;
      }
    }
    
    if (best == null)
    {
      return null;
    }
    
    Population.LOG.log("Best individual found: %s", best.toString());
    Population.LOG.log("Best fitness: " + best.getFitness());
    
    return best;
  }
  
  /**
   * Returns the low niché index.
   * 
   * @param niche niché number
   * @param nicheSize niché size
   * @return the low niché index
   */
  public static int getNicheLow(final int niche, final int nicheSize)
  {
    return (niche - 1) * nicheSize;
  }
  
  /**
   * Returns the high niché index.
   * 
   * @param niche niché number
   * @param nicheSize niché size
   * @return the high niché index
   */
  public static int getNicheHigh(final int niche, final int nicheSize)
  {
    return niche * nicheSize - 1;
  }
}
