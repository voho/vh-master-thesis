package poems.optimiser;

import module.Module;
import module.PlacedModule;
import poems.PoemsActionSequence;
import poems.PoemsActionSequencePair;
import program.Logger;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Placer;

/**
 * Immutable class representing an individual.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Individual
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(Individual.class);
  /**
   * action sequence associated with this individual
   */
  private final PoemsActionSequence sequence;
  /**
   * B*-Tree of unplaced modules
   */
  private final BTree<NumberedValue<Module>> buildPlan;
  /**
   * B*-Tree of placed modules
   */
  private final BTree<PlacedModule> buildResult;
  /**
   * fitness value
   */
  private final int fitness;
  
  /**
   * Creates a random individual.
   * 
   * @param prototype prototype
   * @param length action sequence length
   * @param niche target niché
   * @return a random individual
   */
  public static Individual random(final BTree<NumberedValue<Module>> prototype, final int length, final int niche)
  {
    final PoemsActionSequence newSequence = PoemsActionSequence.random(prototype.getValue().getLastNumber(), length, niche);
    
    Individual.LOG.log("Creating random individual with sequence: %s", newSequence);
    
    return new Individual(prototype, newSequence);
  }
  
  /**
   * Creates a new individual by a crossover operator.
   * 
   * @param prototype prototype
   * @param mother the first parent individual
   * @param father the second parent individual
   * @return a new individual
   */
  public static IndividualPair crossover(final BTree<NumberedValue<Module>> prototype, final Individual mother, final Individual father)
  {
    final PoemsActionSequencePair newSequence = PoemsActionSequence.crossover(mother.sequence, father.sequence);
    
    Individual.LOG.log("Creating individual with crossover: %s", newSequence);
    
    return new IndividualPair(
        new Individual(prototype, newSequence.getFirst()),
        new Individual(prototype, newSequence.getSecond()));
  }
  
  /**
   * Creates a new individual by a mutation operator.
   * 
   * @param prototype prototype
   * @param mutant the original individual
   * @return a new individual
   */
  public static Individual mutate(final BTree<NumberedValue<Module>> prototype, final Individual mutant)
  {
    final PoemsActionSequence newSequence = PoemsActionSequence.mutate(prototype.getValue().getLastNumber(), mutant.sequence);
    
    Individual.LOG.log("Creating individual with mutation: %s", newSequence);
    
    return new Individual(prototype, newSequence);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param prototype prototype
   * @param sequence action sequence
   */
  private Individual(final BTree<NumberedValue<Module>> prototype, final PoemsActionSequence sequence)
  {
    this(prototype, sequence.apply(prototype), sequence);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param original prototype
   * @param modified modified prototype
   * @param sequence action sequence
   */
  private Individual(final BTree<NumberedValue<Module>> original, final BTree<NumberedValue<Module>> modified, final PoemsActionSequence sequence)
  {
    // save the sequence
    
    this.sequence = sequence;
    
    // save the build plan
    
    this.buildPlan = modified;
    
    // place modules
    
    this.buildResult = Placer.placeNumbered(modified);
    
    // compute fitness
    
    this.fitness = Individual.evaluateFitness(original, this.buildPlan, this.buildResult);
  }
  
  /**
   * Evaluates a solution fitness.
   * 
   * @param original original B*-Tree
   * @param modified modified B*-Tree
   * @param placed placed B*-Tree
   * @return fitness value
   */
  private static int evaluateFitness(final BTree<NumberedValue<Module>> original, final BTree<NumberedValue<Module>> modified, final BTree<PlacedModule> placed)
  {
    if (Evaluator.equals(modified, original))
    {
      Individual.LOG.log("Equality of input and output trees, minimal fitness returned.");
      Individual.LOG.log("Original: %s", original.toString());
      Individual.LOG.log("Modified: %s", modified.toString());
      
      return Integer.MIN_VALUE;
    }
    
    return Individual.evaluateFitness(placed);
  }
  
  /**
   * Evaluates a solution fitness.
   * 
   * @param placed placed B*-Tree
   * @return fitness value
   */
  private static int evaluateFitness(final BTree<PlacedModule> placed)
  {
    return Evaluator.evaluate(placed).getFitness();
  }
  
  /**
   * Returns the sequence.
   * 
   * @return the sequence
   */
  public PoemsActionSequence getSequence()
  {
    return this.sequence;
  }
  
  /**
   * Returns the build plan.
   * 
   * @return the build plan
   */
  public BTree<NumberedValue<Module>> getBuildPlan()
  {
    return this.buildPlan;
  }
  
  /**
   * Returns the build result.
   * 
   * @return the build result
   */
  public BTree<PlacedModule> getBuildResult()
  {
    return this.buildResult;
  }
  
  /**
   * Returns the niché of this individual-
   * 
   * @return the niché of this individual
   */
  public int getNiche()
  {
    return this.sequence.getNiche();
  }
  
  /**
   * Returns the action sequence length.
   * 
   * @return the action sequence length
   */
  public int getLength()
  {
    return this.sequence.getLength();
  }
  
  /**
   * Returns the fitness value.
   * 
   * @return the fitness value
   */
  public int getFitness()
  {
    return this.fitness;
  }
  
  /**
   * Checks if the individual is worse than the given.
   * 
   * @param other other individual given
   * @return TRUE if this individual is worse than given
   */
  public boolean isWorse(final Individual other)
  {
    return (this.fitness < other.fitness);
  }
  
  /**
   * Checks if the individual is better than the given.
   * 
   * @param other other individual given
   * @return TRUE if this individual is better than given
   */
  public boolean isBetter(final Individual other)
  {
    return (this.fitness > other.fitness);
  }
  
  /**
   * Checks if the individual is worse or equal than the given.
   * 
   * @param other other individual given
   * @return TRUE if this individual is worse or equal to given
   */
  public boolean isWorseOrEqual(final Individual other)
  {
    return (this.fitness <= other.fitness);
  }
  
  /**
   * Checks if the individual is better than the given.
   * 
   * @param other other individual given
   * @return TRUE if this individual is better or equal to given
   */
  public boolean isBetterOrEqual(final Individual other)
  {
    return (this.fitness >= other.fitness);
  }
  
  @Override
  public String toString()
  {
    return String.format(
        "[f %d] sequence = %s",
        this.fitness,
        this.sequence.toString());
  }
}
