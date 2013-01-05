package program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import module.Module;
import poems.AbstractPoemsAction;
import poems.action1.RotatePoemsAction;
import poems.action2.ExchangeNodePoemsAction;
import poems.action2.ExchangeValuePoemsAction;
import poems.action2.FlipPoemsAction;
import poems.action2.MirrorPoemsAction;
import poems.action3.HangNodePoemsAction;
import poems.optimiser.Individual;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Placer;

/**
 * Logger utility class.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Logger
{
  private static long countNop = 0;
  private static long countHangNode = 0;
  private static long countFlip = 0;
  private static long countExchangeValue = 0;
  private static long countMirror = 0;
  private static long countRotate = 0;
  private static long countExchangeNode = 0;
  
  private static long countLastNop = 0;
  private static long countLastHangNode = 0;
  private static long countLastFlip = 0;
  private static long countLastExchangeValue = 0;
  private static long countLastMirror = 0;
  private static long countLastRotate = 0;
  private static long countLastExchangeNode = 0;
  
  private static long countBestNop = 0;
  private static long countBestHangNode = 0;
  private static long countBestFlip = 0;
  private static long countBestExchangeValue = 0;
  private static long countBestMirror = 0;
  private static long countBestRotate = 0;
  private static long countBestExchangeNode = 0;
  
  private static List<Double> fitAverage = new LinkedList<Double>();
  private static List<Double> fitBest = new LinkedList<Double>();
  private static List<Double> allFitness = new LinkedList<Double>();
  private static List<Double> allTimes = new LinkedList<Double>();
  private static long benchmarkStart = 0;
  private static File fileBenchmark = null;
  private static File dirOutput = null;
  private static StringBuilder logBuffer = new StringBuilder();
  
  private static Logger LOG = Logger.create(Logger.class);
  
  private final Class<?> c;
  
  // STATIC METHODS
  // ==============
  
  public static Logger create(final Class<?> c)
  {
    return new Logger(c);
  }
  
  public static void newBenchmark(final File file)
  {
    Logger.fileBenchmark = file;
    Logger.allFitness.clear();
    Logger.allTimes.clear();
    
    Logger.LOG.info("Starting benchmark '%s'...", file.getName());
  }
  
  public static void endBenchmark()
  {
    try
    {
      Exporter.listsToCsv(Logger.allFitness, Logger.allTimes, new File(Logger.dirOutput.getParentFile(), Logger.fileBenchmark.getName() + ".csv"));
    }
    catch (final IOException x)
    {
      x.printStackTrace();
      System.exit(-1);
    }
  }
  
  public static void newRun(final int run)
  {
    Logger.countNop = 0;
    Logger.countHangNode = 0;
    Logger.countFlip = 0;
    Logger.countExchangeValue = 0;
    Logger.countMirror = 0;
    Logger.countRotate = 0;
    Logger.countExchangeNode = 0;
    
    Logger.countLastNop = 0;
    Logger.countLastHangNode = 0;
    Logger.countLastFlip = 0;
    Logger.countLastExchangeValue = 0;
    Logger.countLastMirror = 0;
    Logger.countLastRotate = 0;
    Logger.countLastExchangeNode = 0;
    
    Logger.countBestNop = 0;
    Logger.countBestHangNode = 0;
    Logger.countBestFlip = 0;
    Logger.countBestExchangeValue = 0;
    Logger.countBestMirror = 0;
    Logger.countBestRotate = 0;
    Logger.countBestExchangeNode = 0;
    
    Logger.fitAverage.clear();
    Logger.fitBest.clear();
    Logger.benchmarkStart = System.currentTimeMillis();
    Logger.dirOutput = new File(Setup.DIR_OUTPUT, String.format("%s_run_%d", Logger.fileBenchmark.getName(), run));
    Logger.logBuffer = new StringBuilder();
    
    if (Logger.dirOutput.exists())
    {
      Logger.LOG.log("Output directory '%s' already exists.", Logger.dirOutput.getAbsolutePath());
      System.exit(-1);
    }
    
    if (!Logger.dirOutput.mkdirs())
    {
      Logger.LOG.log("Error creating directory '%s'.", Logger.dirOutput.getAbsolutePath());
      System.exit(-1);
    }
    
    Logger.LOG.info("Starting run '%d'...", run);
  }
  
  public static void endRun(final Individual result)
  {
    final long duration = System.currentTimeMillis() - Logger.benchmarkStart;
    
    Logger.allFitness.add(Double.valueOf(result.getFitness()));
    Logger.allTimes.add(Double.valueOf(duration));
    
    Logger.LOG.info("Total nop: " + Logger.countNop);
    Logger.LOG.info("Total flip: " + Logger.countFlip);
    Logger.LOG.info("Total mirror: " + Logger.countMirror);
    Logger.LOG.info("Total rotate: " + Logger.countRotate);
    Logger.LOG.info("Total exchange value: " + Logger.countExchangeValue);
    Logger.LOG.info("Total exchange node: " + Logger.countExchangeNode);
    Logger.LOG.info("Total hang node: " + Logger.countHangNode);
    
    Logger.LOG.info("Total LAST nop: " + Logger.countLastNop);
    Logger.LOG.info("Total LAST flip: " + Logger.countLastFlip);
    Logger.LOG.info("Total LAST mirror: " + Logger.countLastMirror);
    Logger.LOG.info("Total LAST rotate: " + Logger.countLastRotate);
    Logger.LOG.info("Total LAST exchange value: " + Logger.countLastExchangeValue);
    Logger.LOG.info("Total LAST exchange node: " + Logger.countLastExchangeNode);
    Logger.LOG.info("Total LAST hang node: " + Logger.countLastHangNode);
    
    Logger.LOG.info("Total BEST nop: " + Logger.countBestNop);
    Logger.LOG.info("Total BEST flip: " + Logger.countBestFlip);
    Logger.LOG.info("Total BEST mirror: " + Logger.countBestMirror);
    Logger.LOG.info("Total BEST rotate: " + Logger.countBestRotate);
    Logger.LOG.info("Total BEST exchange value: " + Logger.countBestExchangeValue);
    Logger.LOG.info("Total BEST exchange node: " + Logger.countBestExchangeNode);
    Logger.LOG.info("Total BEST hang node: " + Logger.countBestHangNode);
    
    Logger.LOG.info("Result: " + result);
    Logger.LOG.info("Duration: %d [ms]", duration);
    
    try
    {
      Exporter.listToPlot(Logger.fitAverage, new File(Logger.dirOutput, "plot_fit_avg.txt"));
      Exporter.listToPlot(Logger.fitBest, new File(Logger.dirOutput, "plot_fit_best.txt"));
      
      Exporter.evaluationToTxt(Evaluator.evaluate(result.getBuildResult()), new File(Logger.dirOutput, "result.txt"));
      Exporter.treeToDot(result.getBuildPlan(), new File(Logger.dirOutput, "result.dot"));
      Exporter.treeToSvg(result.getBuildResult(), false, new File(Logger.dirOutput, "floorplan.svg"));
      Exporter.treeToSvg(result.getBuildResult(), true, new File(Logger.dirOutput, "floorplan_s.svg"));
      
      final FileWriter writer = new FileWriter(new File(Logger.dirOutput, "stats.txt"));
      writer.write(Logger.logBuffer.toString());
      writer.close();
    }
    catch (final IOException x)
    {
      x.printStackTrace();
      System.exit(-1);
    }
  }
  
  public static void prototype(final BTree<NumberedValue<Module>> prototype)
  {
    try
    {
      Exporter.treeToDot(prototype, new File(Logger.dirOutput, "prototype.dot"));
      Exporter.treeToSvg(Placer.placeNumbered(prototype), false, new File(Logger.dirOutput, "prototype_f.svg"));
      Exporter.treeToSvg(Placer.placeNumbered(prototype), true, new File(Logger.dirOutput, "prototype_fs.svg"));
    }
    catch (final IOException x)
    {
      x.printStackTrace();
      System.exit(-1);
    }
  }
  
  public static void improvement(final Individual best, final int i)
  {
    try
    {
      Exporter.treeToSvg(best.getBuildResult(), false, new File(Logger.dirOutput, String.format("i_%08d.svg", i)));
    }
    catch (final IOException x)
    {
      x.printStackTrace();
      System.exit(-1);
    }
  }
  
  public static void iterated(final Individual temp)
  {
    Logger.fitBest.add(Double.valueOf(temp.getFitness()));
  }
  
  public static void evolved(final Individual[] population, final Individual best)
  {
    Double avg = null;
    
    for (final Individual i : population)
    {
      // compute avg
      
      if (avg == null)
      {
        avg = Double.valueOf(i.getFitness());
      }
      else
      {
        avg = (avg + i.getFitness()) / 2.0;
      }
      
      // increment actions
      
      for (final AbstractPoemsAction action : i.getSequence().getActions())
      {
        if (action.isEnabled())
        {
          if (action.getClass().equals(ExchangeNodePoemsAction.class))
          {
            Logger.countLastExchangeNode++;
          }
          else if (action.getClass().equals(ExchangeValuePoemsAction.class))
          {
            Logger.countLastExchangeValue++;
          }
          else if (action.getClass().equals(FlipPoemsAction.class))
          {
            Logger.countLastFlip++;
          }
          else if (action.getClass().equals(HangNodePoemsAction.class))
          {
            Logger.countLastHangNode++;
          }
          else if (action.getClass().equals(MirrorPoemsAction.class))
          {
            Logger.countLastMirror++;
          }
          else if (action.getClass().equals(RotatePoemsAction.class))
          {
            Logger.countLastRotate++;
          }
          else
          {
            throw new IllegalStateException();
          }
        }
        else
        {
          Logger.countLastNop++;
        }
      }
    }
    
    // increment actions for the best
    
    for (final AbstractPoemsAction action : best.getSequence().getActions())
    {
      if (action.isEnabled())
      {
        if (action.getClass().equals(ExchangeNodePoemsAction.class))
        {
          Logger.countBestExchangeNode++;
        }
        else if (action.getClass().equals(ExchangeValuePoemsAction.class))
        {
          Logger.countBestExchangeValue++;
        }
        else if (action.getClass().equals(FlipPoemsAction.class))
        {
          Logger.countBestFlip++;
        }
        else if (action.getClass().equals(HangNodePoemsAction.class))
        {
          Logger.countBestHangNode++;
        }
        else if (action.getClass().equals(MirrorPoemsAction.class))
        {
          Logger.countBestMirror++;
        }
        else if (action.getClass().equals(RotatePoemsAction.class))
        {
          Logger.countBestRotate++;
        }
        else
        {
          throw new IllegalStateException();
        }
      }
      else
      {
        Logger.countBestNop++;
      }
    }
    
    Logger.fitAverage.add(avg);
  }
  
  public void created(final AbstractPoemsAction action)
  {
    if (action.isEnabled())
    {
      if (action.getClass().equals(ExchangeNodePoemsAction.class))
      {
        Logger.countExchangeNode++;
      }
      else if (action.getClass().equals(ExchangeValuePoemsAction.class))
      {
        Logger.countExchangeValue++;
      }
      else if (action.getClass().equals(FlipPoemsAction.class))
      {
        Logger.countFlip++;
      }
      else if (action.getClass().equals(HangNodePoemsAction.class))
      {
        Logger.countHangNode++;
      }
      else if (action.getClass().equals(MirrorPoemsAction.class))
      {
        Logger.countMirror++;
      }
      else if (action.getClass().equals(RotatePoemsAction.class))
      {
        Logger.countRotate++;
      }
      else
      {
        throw new IllegalStateException();
      }
    }
    else
    {
      Logger.countNop++;
    }
  }
  
  // INSTANCE METHODS
  // ================
  
  /**
   * Creates a new instance.
   */
  private Logger(final Class<?> c)
  {
    this.c = c;
  }
  
  /**
   * Logs an event.
   * 
   * @param s event message
   */
  public void log(final String s)
  {
    if (Setup.VERBOSE)
    {
      System.out.println(String.format("%s: %s", this.c.getSimpleName(), s));
    }
  }
  
  /**
   * Logs an event and uses formatting on its arguments.
   * 
   * @param s event message with placeholders
   * @param objects arguments to replace placeholders
   */
  public void log(final String s, final Object... objects)
  {
    if (Setup.VERBOSE)
    {
      this.log(String.format(s, objects));
    }
  }
  
  /**
   * Logs an information.
   * 
   * @param s information message
   */
  private void info(final String s)
  {
    Logger.logBuffer.append(s + "\n");
    System.out.println(s);
  }
  
  /**
   * Logs an information and uses formatting on its arguments.
   * 
   * @param s information message
   * @param objects arguments to replace placeholders
   */
  private void info(final String s, final Object... objects)
  {
    this.info(String.format(s, objects));
  }
}
