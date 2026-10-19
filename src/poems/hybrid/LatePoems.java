package poems.hybrid;

import java.util.Arrays;
import java.util.Set;

import module.Module;
import poems.optimiser.Individual;
import program.Logger;
import program.Setup;
import program.Utility;
import btree.ArrayBTree;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;
import btree.utility.Placer;
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
   * random numbers for the walk to a close module (at most 3 steps)
   */
  private static final double[] WALK = new double[3];
  /**
   * probabilities of the action types (adaptive selection)
   */
  private static final double[] PROBABILITY = new double[LatePoems.ACTION_TYPES];
  /**
   * number of steps with each action type in the current window
   */
  private static final long[] TRIES = new long[LatePoems.ACTION_TYPES];
  /**
   * number of improving steps with each action type in the current window
   */
  private static final long[] SUCCESSES = new long[LatePoems.ACTION_TYPES];
  /**
   * number of steps after which the action type probabilities are updated
   */
  private static final long WINDOW = 100000;
  /**
   * minimal probability of each action type
   */
  private static final double MIN_PROBABILITY = 0.05;
  /**
   * minimal automatic history length
   */
  private static final int MIN_HISTORY = 100;
  /**
   * maximal automatic history length
   */
  private static final int MAX_HISTORY = 10000000;

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
   * @param historyLength length of the late acceptance history (0 = chosen
   * automatically from the expected number of evaluations)
   * @return the best floorplan found
   */
  public static BTree<NumberedValue<Module>> solve(final Set<Module> modules, final long timeLimit, final long maxEvaluations, final int length, final int historyLength)
  {
    LatePoems.LOG.log("Solving instance using late acceptance POEMS (length %d, history %d)...", length, historyLength);

    final BTree<NumberedValue<Module>> prototype = Numberator.number(Setup.BEST_FIT_PROTOTYPE
        ? Prototyper.createBestFit(modules)
        : Prototyper.createCompleteTree(modules));

    Logger.prototype(prototype);

    final ArrayBTree start = new ArrayBTree(prototype);
    ArrayBTree current = new ArrayBTree(start);
    ArrayBTree candidate = new ArrayBTree(start);
    final ArrayBTree best = new ArrayBTree(start);

    final long startArea = start.place();
    long currentArea = startArea;
    long bestArea = currentArea;

    // the search restarts from the prototype if the best solution of the
    // current run does not improve for the given number of evaluations

    final long stall = (long) Setup.RESTART * (long) start.size();
    long runBestArea = currentArea;
    long runImproved = 0;

    // with the automatic history length, a short history is used until the
    // speed of the evaluation is measured (or the length is computed from the
    // maximal number of evaluations directly)

    final boolean automatic = (historyLength <= 0);
    boolean measured = !automatic || (maxEvaluations > 0);
    int size = automatic
        ? ((maxEvaluations > 0)
            ? LatePoems.getHistoryLength(maxEvaluations)
            : LatePoems.MIN_HISTORY)
        : historyLength;
    long[] history = new long[size];
    Arrays.fill(history, currentArea);

    final long begin = System.currentTimeMillis();
    final long deadline = begin + timeLimit;
    final int last = current.size() - 1;
    final boolean annealing = (Setup.ANNEALING_START > 0);
    final double used = Placer.measureNumbered(prototype).getUsedArea();

    Logger.trace((long) (bestArea - used));
    double temperature = Setup.ANNEALING_START * used;
    int slot = 0;

    LatePoems.evaluations = 1;

    // action types of the current step (and their success statistics)

    final int[] steps = new int[length];
    Arrays.fill(LatePoems.PROBABILITY, 1.0 / LatePoems.ACTION_TYPES);
    Arrays.fill(LatePoems.TRIES, 0);
    Arrays.fill(LatePoems.SUCCESSES, 0);

    while ((maxEvaluations <= 0) || (LatePoems.evaluations < maxEvaluations))
    {
      if ((LatePoems.evaluations & 0xFF) == 0)
      {
        final long now = System.currentTimeMillis();

        if (now >= deadline)
        {
          break;
        }

        if (!measured && ((now - begin) * 50 >= timeLimit))
        {
          // the expected number of evaluations is known after 2% of the time

          final double rate = (double) LatePoems.evaluations / Math.max(1, now - begin);

          size = LatePoems.getHistoryLength((long) (rate * timeLimit));
          history = new long[size];
          Arrays.fill(history, currentArea);
          slot = 0;
          measured = true;

          LatePoems.LOG.log("History length set to %d.", Integer.valueOf(size));
        }

        if (annealing)
        {
          // exponential cooling from the initial to the final temperature

          final double progress = (maxEvaluations > 0)
              ? (double) LatePoems.evaluations / (double) maxEvaluations
              : (double) (now - begin) / (double) timeLimit;

          temperature = used * Setup.ANNEALING_START * Math.pow(Setup.ANNEALING_END / Setup.ANNEALING_START, progress);
        }
      }

      // modify the prototype by a random action sequence

      candidate.copyFrom(current);

      final int actions = Utility.random(1, length);
      final int[] step = steps;

      for (int i = 0; i < actions; i++)
      {
        step[i] = LatePoems.applyRandomAction(candidate, last);
      }

      // the candidate is accepted if its area does not exceed the threshold:
      // late acceptance = not worse than now or the given number of steps ago,
      // annealing = worse by D with the probability exp(-D / temperature)

      final long threshold = annealing
          ? currentArea + (long) (-Math.log(1.0 - Utility.randomDouble()) * temperature)
          : Math.max(currentArea, history[slot]);

      final long area = candidate.place(threshold);

      LatePoems.evaluations++;

      if (Setup.ADAPTIVE)
      {
        LatePoems.learn(step, actions, area < currentArea);
      }

      if (area <= threshold)
      {
        final ArrayBTree temp = current;
        current = candidate;
        candidate = temp;
        currentArea = area;

        if (area < bestArea)
        {
          best.copyFrom(current);
          bestArea = area;
          Logger.trace((long) (area - used));
        }

        if (area < runBestArea)
        {
          runBestArea = area;
          runImproved = LatePoems.evaluations;
        }
      }

      if ((stall > 0) && (LatePoems.evaluations - runImproved > stall))
      {
        LatePoems.LOG.log("Restarting after %d evaluations...", Long.valueOf(LatePoems.evaluations));

        current.copyFrom(start);
        currentArea = startArea;
        runBestArea = startArea;
        runImproved = LatePoems.evaluations;
        Arrays.fill(history, startArea);
      }

      history[slot] = currentArea;

      if (++slot == size)
      {
        slot = 0;
      }
    }

    LatePoems.LOG.log("Best area: %d", Long.valueOf(bestArea));

    return best.toBTree();
  }

  /**
   * Returns the automatic late acceptance history length.
   *
   * @param evaluations expected number of evaluations
   * @return the history length
   */
  private static int getHistoryLength(final long evaluations)
  {
    return (int) Math.max(LatePoems.MIN_HISTORY, Math.min(LatePoems.MAX_HISTORY, evaluations / Setup.HISTORY_RATIO));
  }

  /**
   * Solves a problem instance and returns the result as an individual (with
   * no actions, the result is its prototype).
   *
   * @param modules set of modules to place
   * @param timeLimit time limit in milliseconds
   * @param maxEvaluations maximal number of evaluations (0 = no limit)
   * @param length maximal number of actions applied in one step
   * @param historyLength length of the late acceptance history
   * @return the best floorplan found
   */
  public static Individual solveIndividual(final Set<Module> modules, final long timeLimit, final long maxEvaluations, final int length, final int historyLength)
  {
    return Individual.prototype(LatePoems.solve(modules, timeLimit, maxEvaluations, length, historyLength), length);
  }

  /**
   * Applies a random enabled action to the tree.
   *
   * @param tree tree to modify
   * @param last the last module number
   */
  private static int applyRandomAction(final ArrayBTree tree, final int last)
  {
    final int a = Utility.random(0, last);
    int b = (last > 0)
        ? Utility.increment(a, Utility.random(1, last), last)
        : a;

    if ((Setup.LOCAL_PARTNER > 0) && Utility.randomBoolean(Setup.LOCAL_PARTNER))
    {
      // the second module is chosen close to the first one in the tree

      final double[] random = LatePoems.WALK;

      for (int i = 0; i < random.length; i++)
      {
        random[i] = Utility.randomDouble();
      }

      final int neighbour = tree.getNeighbour(a, Utility.random(1, random.length), random);

      if (neighbour != a)
      {
        b = neighbour;
      }
    }

    final boolean flag = Utility.randomBoolean();
    final int type = LatePoems.randomActionType();

    switch (type)
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

    return type;
  }

  /**
   * Updates the success statistics of the action types used in the step and
   * (once per window) the action type probabilities: they are proportional to
   * the success rates in the last window, but at least the minimal
   * probability.
   *
   * @param step action types used in the step
   * @param actions number of actions in the step
   * @param improved the step improved the prototype
   */
  private static void learn(final int[] step, final int actions, final boolean improved)
  {
    for (int i = 0; i < actions; i++)
    {
      LatePoems.TRIES[step[i]]++;

      if (improved)
      {
        LatePoems.SUCCESSES[step[i]]++;
      }
    }

    if (LatePoems.evaluations % LatePoems.WINDOW != 0)
    {
      return;
    }

    final double[] rate = new double[LatePoems.ACTION_TYPES];
    double sum = 0;

    for (int type = 0; type < LatePoems.ACTION_TYPES; type++)
    {
      rate[type] = (LatePoems.SUCCESSES[type] + 0.5) / (LatePoems.TRIES[type] + 1.0);
      sum += rate[type];
    }

    for (int type = 0; type < LatePoems.ACTION_TYPES; type++)
    {
      LatePoems.PROBABILITY[type] = LatePoems.MIN_PROBABILITY + ((1 - LatePoems.ACTION_TYPES * LatePoems.MIN_PROBABILITY) * rate[type] / sum);
    }

    Arrays.fill(LatePoems.TRIES, 0);
    Arrays.fill(LatePoems.SUCCESSES, 0);
  }

  /**
   * Returns a random action type, uniformly or by the configured weights.
   *
   * @return action type
   */
  private static int randomActionType()
  {
    if (Setup.ADAPTIVE)
    {
      double r = Utility.randomDouble();

      for (int type = 0; type < LatePoems.ACTION_TYPES - 1; type++)
      {
        r -= LatePoems.PROBABILITY[type];

        if (r < 0)
        {
          return type;
        }
      }

      return LatePoems.ACTION_TYPES - 1;
    }

    if (Setup.ACTION_WEIGHTS == null)
    {
      return Utility.random(0, LatePoems.ACTION_TYPES - 1);
    }

    double r = Utility.randomDouble();

    for (int type = 0; type < LatePoems.ACTION_TYPES - 1; type++)
    {
      r -= Setup.ACTION_WEIGHTS[type];

      if (r < 0)
      {
        return type;
      }
    }

    return LatePoems.ACTION_TYPES - 1;
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
