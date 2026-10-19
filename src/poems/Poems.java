package poems;

import java.util.Arrays;
import java.util.Set;

import module.Module;
import poems.optimiser.Individual;
import poems.optimiser.Population;
import program.Logger;
import program.Setup;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;
import btree.utility.Prototyper;

/**
 * POEMS algorithm.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Poems
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(Poems.class);
  /**
   * number of iterations done in the last run
   */
  private static int iterations = 0;
  
  /**
   * Creates a new instance.
   */
  private Poems()
  {
    // NOP
  }
  
  /**
   * Solves a problem instance using the POEMS algorithm.
   * 
   * @param modules set of modules to place
   * @param i number of iterations
   * @param g number of generations
   * @param n niche count (length of action sequences)
   * @param s niche size (population group size)
   * @return the best solution found
   */
  public static Individual solve(final Set<Module> modules, final int i, final int g, final int n, final int s)
  {
    Poems.LOG.log("Solving instance using %d iterations, %d generations, %d(+1) niches, %d niche size...", i, g, n, s);
    
    // create prototype
    
    final BTree<Module> btree;
    
    if (Setup.BEST_FIT_PROTOTYPE)
    {
      btree = Prototyper.createBestFit(modules);
    }
    else
    {
      btree = Prototyper.createCompleteTree(modules);
    }
    
    final BTree<NumberedValue<Module>> prototype = Numberator.number(btree);
    
    Poems.LOG.log("Prototype: %s", btree);
    
    Logger.prototype(prototype);
    
    // the prototype is the best solution so far
    
    Individual best = Individual.prototype(prototype, n);
    Individual current = best;
    
    // fitness history for the late acceptance (if enabled)
    
    final int[] history = new int[Math.max(1, Setup.LATE_ACCEPTANCE)];
    Arrays.fill(history, current.getFitness());
    
    final long deadline = (Setup.TIME_LIMIT > 0)
        ? System.currentTimeMillis() + Setup.TIME_LIMIT
        : Long.MAX_VALUE;
    
    Poems.iterations = 0;
    
    for (int ii = 0; (ii < i) && (System.currentTimeMillis() < deadline); ii++)
    {
      Poems.iterations++;
      
      Poems.LOG.log("ITERATION %d", ii + 1);
      
      // create a new population
      
      final Population population = new Population(
          current.getBuildPlan(),
          n,
          s);
      
      // optimise the prototype
      
      final Individual temp = population.evolve(g, deadline, current.getFitness());
      
      Poems.LOG.log("Iteration %d brought: %s", ii, temp);
      
      // print new prototype if better
      
      Logger.iterated(temp);
      
      if (temp.isBetter(best))
      {
        Logger.improvement(temp, ii);
      }
      
      // replace old prototype if new better or equal (or if not worse than
      // the prototype several iterations ago, if late acceptance is enabled)
      
      final int slot = ii % history.length;
      
      if (temp.isBetterOrEqual(current) || ((Setup.LATE_ACCEPTANCE > 0) && (temp.getFitness() >= history[slot])))
      {
        Poems.LOG.log("New prototype accepted: %s", temp);
        
        current = temp;
      }
      
      history[slot] = current.getFitness();
      
      if (current.isBetterOrEqual(best))
      {
        best = current;
      }
    }
    
    return best;
  }
  
  /**
   * Returns the number of iterations done in the last run.
   * 
   * @return the number of iterations
   */
  public static int getIterations()
  {
    return Poems.iterations;
  }
}
