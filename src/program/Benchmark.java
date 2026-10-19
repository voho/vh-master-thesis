package program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import module.Module;
import module.PlacedModule;
import poems.Poems;
import poems.hybrid.LatePoems;
import poems.optimiser.Individual;
import btree.BTree;
import btree.utility.Evaluation;
import btree.utility.Evaluator;
import btree.utility.Placer;

/**
 * Headless benchmark runner used for experiments. Solves a single problem
 * instance with a fixed seed and time limit and prints one CSV line with the
 * result.
 *
 * @author Vojtěch Hordějčuk
 */
public class Benchmark
{
  /**
   * Creation is not allowed.
   */
  private Benchmark()
  {
    // NOP
  }

  /**
   * Benchmark entry point.
   *
   * @param args command line arguments
   * @throws IOException file not found or not writable
   */
  public static void main(final String[] args) throws IOException
  {
    if (args.length != 7)
    {
      System.out.println("Usage: {INPUT (file)} {SEED} {TIME LIMIT (ms)} {#I} {#G} {#N} {#S}");
      return;
    }

    final File file = new File(args[0]);

    Setup.SEED = Long.parseLong(args[1]);
    Setup.TIME_LIMIT = Long.parseLong(args[2]);
    Setup.VERBOSE = false;
    Setup.EXPORT = false;

    final int i = Integer.parseInt(args[3]);
    final int g = Integer.parseInt(args[4]);
    final int n = Integer.parseInt(args[5]);
    final int s = Integer.parseInt(args[6]);

    Utility.setSeed(Setup.SEED);

    final Set<Module> modules = Importer.loadProblem(file);

    final String trace = System.getProperty("poems.trace");

    if (trace != null)
    {
      Logger.startTrace();
    }

    final long start = System.currentTimeMillis();
    final BTree<PlacedModule> placed;
    final long evaluations;
    final int iterations;

    if (Setup.isLatePoems())
    {
      // late acceptance POEMS: N = maximal number of actions in one step,
      // S = late acceptance history length

      placed = Placer.placeNumbered(LatePoems.solve(modules, Setup.TIME_LIMIT, Long.getLong("poems.evaluations", 0).longValue(), n, s));
      evaluations = LatePoems.getEvaluations();
      iterations = 0;
    }
    else
    {
      final Individual result = Poems.solve(modules, i, g, n, s);
      placed = result.getBuildResult();
      evaluations = Individual.getEvaluations();
      iterations = Poems.getIterations();
    }

    final long duration = System.currentTimeMillis() - start;

    if (trace != null)
    {
      // a directory: one trace file for each benchmark and seed

      final File traceFile = new File(trace).isDirectory()
          ? new File(trace, String.format("%s_%d.csv", file.getName().replace(".txt", ""), Setup.SEED))
          : new File(trace);
      final FileWriter writer = new FileWriter(traceFile);
      writer.write(Logger.getTrace());
      writer.close();
    }

    final Evaluation evaluation = Evaluator.evaluate(placed);
    final String valid = Benchmark.validate(modules, placed);

    System.out.println(String.format(
        Locale.US,
        "%s,%d,%.4f,%d,%d,%d,%d,%d,%d,%d,%d,%s",
        file.getName().replace(".txt", ""),
        Setup.SEED,
        100.0 * evaluation.getRelativeUnusedArea(),
        evaluation.getUnusedArea(),
        evaluation.getTotalArea(),
        evaluation.getUsedArea(),
        evaluation.getWidth(),
        evaluation.getHeight(),
        evaluations,
        iterations,
        duration,
        valid));
  }

  /**
   * Checks that the placement contains every module exactly once (possibly
   * rotated) and that no two modules overlap.
   *
   * @param modules problem modules
   * @param placed placed B*-Tree
   * @return "ok" or a description of the first problem found
   */
  private static String validate(final Set<Module> modules, final BTree<PlacedModule> placed)
  {
    final Map<String, Module> byName = new HashMap<String, Module>();

    for (final Module module : modules)
    {
      byName.put(module.getName(), module);
    }

    final List<PlacedModule> list = new ArrayList<PlacedModule>();

    for (final BTree<PlacedModule> node : placed)
    {
      final PlacedModule p = node.getValue();
      final Module original = byName.remove(p.getModule().getName());

      if (original == null)
      {
        return "duplicate-or-unknown-module";
      }

      final boolean same = (original.getWidth() == p.getModule().getWidth()) && (original.getHeight() == p.getModule().getHeight());
      final boolean rotated = (original.getWidth() == p.getModule().getHeight()) && (original.getHeight() == p.getModule().getWidth());

      if (!same && !rotated)
      {
        return "wrong-dimensions";
      }

      if ((p.getPosition().getX() < 0) || (p.getPosition().getY() < 0))
      {
        return "negative-position";
      }

      list.add(p);
    }

    if (!byName.isEmpty())
    {
      return "missing-modules";
    }

    for (int a = 0; a < list.size(); a++)
    {
      for (int b = a + 1; b < list.size(); b++)
      {
        final PlacedModule p = list.get(a);
        final PlacedModule q = list.get(b);

        final boolean apart = (p.getMaxPosition().getX() <= q.getPosition().getX())
            || (q.getMaxPosition().getX() <= p.getPosition().getX())
            || (p.getMaxPosition().getY() <= q.getPosition().getY())
            || (q.getMaxPosition().getY() <= p.getPosition().getY());

        if (!apart)
        {
          return "overlap";
        }
      }
    }

    return "ok";
  }
}
