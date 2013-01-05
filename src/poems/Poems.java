package poems;

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
    
    Individual best = null;
    
    for (int ii = 0; ii < i; ii++)
    {
      Poems.LOG.log("ITERATION %d", ii + 1);
      
      // create a new population
      
      final Population population = new Population(
          best == null
              ? prototype
              : best.getBuildPlan(),
              n,
              s);
      
      // optimise the prototype
      
      final Individual temp = population.evolve(g);
      
      Poems.LOG.log("Iteration %d brought: %s", ii, temp.toString());
      
      // print new prototype if better
      
      Logger.iterated(temp);
      
      if ((best == null) || temp.isBetter(best))
      {
        Logger.improvement(temp, ii);
      }
      
      // replace old prototype if new better of equal
      
      if ((best == null) || temp.isBetterOrEqual(best))
      {
        Poems.LOG.log("Better prototype found: %s", temp);
        
        best = temp;
      }
    }
    
    return best;
  }
}
