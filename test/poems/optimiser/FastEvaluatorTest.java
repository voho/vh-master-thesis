package poems.optimiser;

import java.util.HashSet;
import java.util.Set;

import module.Module;

import org.junit.Assert;
import org.junit.Test;

import poems.PoemsActionSequence;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Tests that the evaluation of action sequences on the array B*-Tree gives the
 * same fitness as their application to the immutable B*-Tree.
 *
 * @author Vojtěch Hordějčuk
 */
public class FastEvaluatorTest
{
  /**
   * test
   */
  @Test
  public void testSameFitnessAsImmutableEvaluation()
  {
    for (int seed = 1; seed <= 5; seed++)
    {
      Utility.setSeed(seed);

      final Set<Module> modules = new HashSet<Module>();

      for (int i = 0; i < 30; i++)
      {
        modules.add(new Module(String.valueOf(i), Utility.random(1, 60), Utility.random(1, 60)));
      }

      final BTree<NumberedValue<Module>> prototype = Numberator.number(Prototyper.createBestFit(modules));
      final int last = prototype.size() - 1;
      int unchanged = 0;

      for (int i = 0; i < 2000; i++)
      {
        final int length = Utility.random(1, 4);
        final PoemsActionSequence sequence = PoemsActionSequence.random(last, length, Utility.random(1, length));
        final BTree<NumberedValue<Module>> modified = sequence.apply(prototype);
        final int expected = Evaluator.equals(modified, prototype)
            ? Integer.MIN_VALUE
            : Placer.measureNumbered(modified).getFitness();

        Assert.assertEquals(sequence.toString(), expected, FastEvaluator.evaluate(prototype, sequence));

        if (expected == Integer.MIN_VALUE)
        {
          unchanged++;
        }
      }

      // the identity check is exercised as well

      Assert.assertTrue(unchanged > 0);
    }
  }
}
