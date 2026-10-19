package poems.hybrid;

import java.util.Arrays;
import java.util.Set;

import module.Module;
import program.Logger;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;
import btree.utility.Prototyper;

/**
 * Late acceptance POEMS. The prototype is modified by short random sequences
 * of the POEMS actions (as in the POEMS niches, a sequence has 1 to the
 * maximal number of enabled actions). Every modified prototype is evaluated
 * immediately and it replaces the prototype if it is not worse than the
 * prototype or than the prototype a given number of steps ago (late
 * acceptance hill climbing). The tree is modified in place, so the evaluation
 * is much faster than with the immutable B*-Tree.
 *
 * @author Vojtěch Hordějčuk
 */
public final class LatePoems
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(LatePoems.class);
  /**
   * number of action types
   */
  private static final int ACTION_TYPES = 7;
  /**
   * number of evaluations done in the last run
   */
  private static long evaluations = 0;

  /**
   * Creation is not allowed.
   */
  private LatePoems()
  {
    // NOP
  }

  /**
   * Solves a problem instance.
   *
   * @param modules set of modules to place
   * @param timeLimit time limit in milliseconds
   * @param maxEvaluations maximal number of evaluations (0 = no limit)
   * @param length maximal number of actions applied in one step
   * @param historyLength length of the late acceptance history
   * @return the best floorplan found
   */
  public static BTree<NumberedValue<Module>> solve(final Set<Module> modules, final long timeLimit, final long maxEvaluations, final int length, final int historyLength)
  {
    LatePoems.LOG.log("Solving instance using late acceptance POEMS (length %d, history %d)...", length, historyLength);

    final BTree<NumberedValue<Module>> prototype = Numberator.number(Prototyper.createBestFit(modules));

    ArrayBTree current = new ArrayBTree(prototype);
    ArrayBTree candidate = new ArrayBTree(current);
    final ArrayBTree best = new ArrayBTree(current);

    long currentArea = current.place();
    long bestArea = currentArea;

    final long[] history = new long[historyLength];
    Arrays.fill(history, currentArea);

    final long deadline = System.currentTimeMillis() + timeLimit;
    final int last = current.size() - 1;
    int slot = 0;

    LatePoems.evaluations = 1;

    while ((maxEvaluations <= 0) || (LatePoems.evaluations < maxEvaluations))
    {
      if (((LatePoems.evaluations & 0xFF) == 0) && (System.currentTimeMillis() >= deadline))
      {
        break;
      }

      // modify the prototype by a random action sequence

      candidate.copyFrom(current);

      final int actions = Utility.random(1, length);

      for (int i = 0; i < actions; i++)
      {
        LatePoems.applyRandomAction(candidate, last);
      }

      final long area = candidate.place();

      LatePoems.evaluations++;

      // late acceptance

      if ((area <= currentArea) || (area <= history[slot]))
      {
        final ArrayBTree temp = current;
        current = candidate;
        candidate = temp;
        currentArea = area;

        if (area < bestArea)
        {
          best.copyFrom(current);
          bestArea = area;
        }
      }

      history[slot] = currentArea;

      if (++slot == historyLength)
      {
        slot = 0;
      }
    }

    LatePoems.LOG.log("Best area: %d", Long.valueOf(bestArea));

    return best.toBTree();
  }

  /**
   * Applies a random enabled action to the tree.
   *
   * @param tree tree to modify
   * @param last the last module number
   */
  private static void applyRandomAction(final ArrayBTree tree, final int last)
  {
    final int a = Utility.random(0, last);
    final int b = (last > 0)
        ? Utility.increment(a, Utility.random(1, last), last)
        : a;
    final boolean flag = Utility.randomBoolean();

    switch (Utility.random(0, LatePoems.ACTION_TYPES - 1))
    {
    case 0:
      tree.flip(a, flag);
      break;
    case 1:
      tree.mirror(a, true, true);
      break;
    case 2:
      tree.mirror(a, flag, false);
      break;
    case 3:
      tree.exchangeValues(a, b);
      break;
    case 4:
      tree.exchangeNodes(a, b);
      break;
    case 5:
      tree.hang(a, b, flag);
      break;
    default:
      tree.move(a, b, flag);
      break;
    }
  }

  /**
   * Returns the number of evaluations done in the last run.
   *
   * @return the number of evaluations
   */
  public static long getEvaluations()
  {
    return LatePoems.evaluations;
  }
}
