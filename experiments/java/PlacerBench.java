import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import module.Module;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Measures the time of placing complete B*-Trees of increasing size. Compile it
 * against a build of the program and run "PlacerBench tree" (placement of the
 * immutable tree) or, with the array B*-Tree available, "PlacerBench array".
 * Prints the number of modules and the median time of one placement in
 * microseconds (9 batches of about 0.3 s after 1 s of warm-up).
 */
public class PlacerBench
{
  public static void main(final String[] args) throws Exception
  {
    final boolean array = (args.length > 0) && "array".equals(args[0]);

    for (int n = 100; n <= 25600; n *= 2)
    {
      Utility.setSeed(n);

      final Set<Module> modules = new HashSet<Module>();

      for (int i = 0; i < n; i++)
      {
        modules.add(new Module(String.valueOf(i), Utility.random(1, 100), Utility.random(1, 100)));
      }

      final BTree<NumberedValue<Module>> tree = Numberator.number(Prototyper.createCompleteTree(modules));
      final Runnable once;

      if (array)
      {
        final Object arrayTree = Class.forName("btree.ArrayBTree").getConstructor(BTree.class).newInstance(tree);
        final java.lang.reflect.Method place = arrayTree.getClass().getMethod("place");
        once = new Runnable()
        {
          public void run()
          {
            try
            {
              place.invoke(arrayTree);
            }
            catch (final Exception x)
            {
              throw new RuntimeException(x);
            }
          }
        };
      }
      else
      {
        once = new Runnable()
        {
          public void run()
          {
            Placer.placeNumbered(tree);
          }
        };
      }

      final long warmup = System.nanoTime();

      while (System.nanoTime() - warmup < 1000000000L)
      {
        once.run();
      }

      final double[] batches = new double[9];

      for (int b = 0; b < batches.length; b++)
      {
        int reps = 0;
        final long start = System.nanoTime();

        while (System.nanoTime() - start < 300000000L)
        {
          once.run();
          reps++;
        }

        batches[b] = (System.nanoTime() - start) / 1000.0 / reps;
      }

      Arrays.sort(batches);
      System.out.println(n + "," + batches[batches.length / 2]);
    }
  }
}
