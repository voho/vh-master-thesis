package program;

import java.io.File;

/**
 * Setup parameters.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Setup
{
  /**
   * probability of big action change
   */
  public static final double PROB_BIG_ACTION_CHANGE = 0.25;
  /**
   * probability of action change
   */
  public static final double PROB_ACTION_CHANGE = 0.4;
  /**
   * probability of action parameter change
   */
  public static final double PROB_ACTION_PARAM_CHANGE = 0.3;
  /**
   * probability of crossover
   */
  public static final double PROB_CROSSOVER = 0.7;
  /**
   * select from one niche
   */
  public static final boolean NICHE_SELECTION = true;
  /**
   * do not evaluate and insert individuals whose action sequence (enabled
   * actions only) is already present in the population
   */
  public static final boolean DEDUPLICATE = Setup.flag("poems.dedupe", false);
  /**
   * end the evolution after this number of generations without improvement
   * of the best individual (0 = never)
   */
  public static final int PATIENCE = Integer.getInteger("poems.patience", 0).intValue();
  /**
   * end the evolution as soon as an individual better than the prototype is
   * found
   */
  public static final boolean FIRST_IMPROVEMENT = Setup.flag("poems.first", false);
  /**
   * use the move node action (moves a single node, not the whole subtree)
   */
  public static final boolean MOVE_ACTION = Setup.flag("poems.move", false);
  /**
   * evaluate the action sequences using the array B*-Tree (the results are
   * the same, but the evaluation is much faster)
   */
  public static final boolean FAST_EVALUATION = Setup.flag("poems.fast", false);
  /**
   * length of the late acceptance history; a worse prototype is accepted if it
   * is not worse than the prototype this number of iterations ago (0 = only
   * better or equal prototypes are accepted)
   */
  public static final int LATE_ACCEPTANCE = Integer.getInteger("poems.lahc", 0).intValue();
  /**
   * relative weights of the action types used by the late acceptance POEMS
   * (flip, rotate, mirror, exchange value, exchange node, hang node, move
   * node), NULL = all types are equally likely
   */
  public static final double[] ACTION_WEIGHTS = Setup.weights("poems.weights");
  /**
   * initial temperature of the simulated annealing used by the late
   * acceptance POEMS instead of the late acceptance (relative to the area of
   * all modules, 0 = late acceptance is used)
   */
  public static final double ANNEALING_START = Double.parseDouble(System.getProperty("poems.t0", "0"));
  /**
   * final temperature of the simulated annealing (relative to the area of all
   * modules)
   */
  public static final double ANNEALING_END = Double.parseDouble(System.getProperty("poems.t1", "0.000001"));
  /**
   * the late acceptance POEMS restarts from the prototype if its best solution
   * does not improve for this number of evaluations per module (0 = never)
   */
  public static final int RESTART = Integer.getInteger("poems.restart", 0).intValue();
  
  // MUTABLE PARAMETERS
  // ==================
  
  /**
   * verbose mode enabled
   */
  public static boolean VERBOSE = false;
  /**
   * create prototype by best-fit heuristic
   */
  public static boolean BEST_FIT_PROTOTYPE = true;
  /**
   * number of test runs
   */
  public static int RUNS = 1;
  /**
   * number of iterations
   */
  public static int ITERATIONS = 30;
  /**
   * number of generations
   */
  public static int GENERATIONS = 30;
  /**
   * niché count
   */
  public static int NICHE_COUNT = 5;
  /**
   * niché size (number of individuals)
   */
  public static int NICHE_SIZE = 20;
  /**
   * algorithm: "poems" (POEMS with the genetic algorithm) or "lapoems" (late
   * acceptance POEMS)
   */
  public static String ALGORITHM = System.getProperty("poems.algorithm", "poems");
  /**
   * late acceptance history length (late acceptance POEMS)
   */
  public static int HISTORY = Integer.getInteger("poems.history", 1000).intValue();
  /**
   * seed of the random number generator
   */
  public static long SEED = 0;
  /**
   * time limit of a single run in milliseconds (0 = no limit)
   */
  public static long TIME_LIMIT = 0;
  /**
   * export results and statistics into the output directory
   */
  public static boolean EXPORT = true;
  /**
   * input directory with benchmarks
   */
  public static File DIR_INPUT = null;
  /**
   * output directory to results in
   */
  public static File DIR_OUTPUT = null;
  
  /**
   * Creation is not allowed.
   */
  private Setup()
  {
    // NOP
  }
  
  /**
   * Checks if the late acceptance POEMS is selected.
   *
   * @return TRUE if the late acceptance POEMS is selected
   */
  public static boolean isLatePoems()
  {
    return "lapoems".equals(Setup.ALGORITHM) || "lahc".equals(Setup.ALGORITHM);
  }

  /**
   * Reads comma separated weights from a system property and normalizes them
   * to sum to one.
   *
   * @param name property name
   * @return normalized weights or NULL if the property is not set
   */
  private static double[] weights(final String name)
  {
    final String value = System.getProperty(name);

    if (value == null)
    {
      return null;
    }

    final String[] parts = value.split(",");
    final double[] result = new double[parts.length];
    double sum = 0;

    for (int i = 0; i < parts.length; i++)
    {
      result[i] = Double.parseDouble(parts[i].trim());
      sum += result[i];
    }

    for (int i = 0; i < parts.length; i++)
    {
      result[i] /= sum;
    }

    return result;
  }

  /**
   * Reads a Boolean system property.
   * 
   * @param name property name
   * @param defaultValue value used if the property is not set
   * @return property value
   */
  private static boolean flag(final String name, final boolean defaultValue)
  {
    final String value = System.getProperty(name);
    
    return (value == null)
        ? defaultValue
        : Boolean.parseBoolean(value);
  }
}
