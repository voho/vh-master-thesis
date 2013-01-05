package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import module.Module;
import poems.Poems;
import poems.optimiser.Individual;

/**
 * Main class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Main
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(Main.class);
  
  /**
   * Main application entry point.
   * 
   * @param args command line arguments
   * @throws FileNotFoundException file not found
   */
  public static void main(final String[] args) throws FileNotFoundException
  {
    Main.LOG.log("Starting the program...");
    
    if (args.length != 9)
    {
      System.out.println("Usage: {INPUT (dir)} {OUTPUT (dir)} {VERBOSE (true/false)} {#R} {#I} {#G} {#N} {#S} {BESTFIT (true/false)}");
      return;
    }
    
    // prepare input and output directories
    
    Setup.DIR_INPUT = new File(args[0]);
    Setup.DIR_OUTPUT = new File(args[1]);
    Setup.VERBOSE = Boolean.parseBoolean(args[2]);
    Setup.RUNS = Integer.parseInt(args[3]);
    Setup.ITERATIONS = Integer.parseInt(args[4]);
    Setup.GENERATIONS = Integer.parseInt(args[5]);
    Setup.NICHE_COUNT = Integer.parseInt(args[6]);
    Setup.NICHE_SIZE = Integer.parseInt(args[7]);
    Setup.BEST_FIT_PROTOTYPE = Boolean.parseBoolean(args[8]);
    
    Main.LOG.log("Input directory: %s", Setup.DIR_INPUT.getAbsolutePath());
    Main.LOG.log("Output directory: %s", Setup.DIR_OUTPUT.getAbsolutePath());
    Main.LOG.log("Verbose mode: %s", Setup.VERBOSE);
    Main.LOG.log("Runs: %d", Setup.RUNS);
    Main.LOG.log("Iterations: %d", Setup.ITERATIONS);
    Main.LOG.log("Generations: %d", Setup.GENERATIONS);
    Main.LOG.log("Niche count: %d", Setup.NICHE_COUNT);
    Main.LOG.log("Niche size: %d", Setup.NICHE_SIZE);
    Main.LOG.log("Best-fit prototype: %s", Setup.BEST_FIT_PROTOTYPE);
    
    if (!Setup.DIR_INPUT.exists())
    {
      Main.LOG.log("Benchmark directory does not exist.");
      return;
    }
    
    if (!Setup.DIR_OUTPUT.exists() && !Setup.DIR_OUTPUT.mkdirs())
    {
      Main.LOG.log("Result directory could not be created.");
      return;
    }
    
    if (!Setup.DIR_INPUT.canRead() || !Setup.DIR_OUTPUT.canWrite())
    {
      Main.LOG.log("Invalid access rights.");
      return;
    }
    
    // start the computation
    
    Main.LOG.log("Starting computation...");
    
    for (final File fileBench : Setup.DIR_INPUT.listFiles())
    {
      if (!fileBench.getName().endsWith("txt"))
      {
        continue;
      }
      
      // load problem
      
      final Set<Module> modules = Importer.loadProblem(fileBench);
      
      // start benchmark
      
      Logger.newBenchmark(fileBench);
      
      for (int i = 0; i < Setup.RUNS; i++)
      {
        // solve the problem
        
        Logger.newRun(i);
        
        final Individual result = Poems.solve(
            modules,
            Setup.ITERATIONS,
            Setup.GENERATIONS,
            Setup.NICHE_COUNT,
            Setup.NICHE_SIZE);
        
        Logger.endRun(result);
      }
      
      Logger.endBenchmark();
    }
    
    Main.LOG.log("Program finished, good bye.");
  }
}
