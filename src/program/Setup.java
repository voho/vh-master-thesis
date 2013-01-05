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
}
