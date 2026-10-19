package poems.optimiser;

import module.Module;
import poems.PoemsActionSequence;
import btree.ArrayBTree;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Placer;

/**
 * Evaluates action sequences using the array B*-Tree. The prototype is
 * converted to the array B*-Tree once (all individuals of a population share
 * the same prototype) and each sequence is applied to its copy in place. The
 * fitness is the same as when the sequence is applied to the immutable
 * B*-Tree. This class is not thread safe.
 *
 * @author Vojtěch Hordějčuk
 */
final class FastEvaluator
{
  /**
   * prototype of the cached array B*-Tree
   */
  private static BTree<NumberedValue<Module>> prototype = null;
  /**
   * prototype converted to the array B*-Tree
   */
  private static ArrayBTree original = null;
  /**
   * array B*-Tree the sequences are applied to
   */
  private static ArrayBTree scratch = null;
  /**
   * area of all modules
   */
  private static long used = 0;

  /**
   * Creation is not allowed.
   */
  private FastEvaluator()
  {
    // NOP
  }

  /**
   * Applies the sequence to the prototype and returns the fitness of the
   * result (the minimal fitness if the result equals the prototype).
   *
   * @param tree prototype
   * @param sequence action sequence
   * @return fitness value
   */
  static int evaluate(final BTree<NumberedValue<Module>> tree, final PoemsActionSequence sequence)
  {
    if (tree != FastEvaluator.prototype)
    {
      FastEvaluator.prototype = tree;
      FastEvaluator.original = new ArrayBTree(tree);
      FastEvaluator.scratch = new ArrayBTree(FastEvaluator.original);
      FastEvaluator.used = Placer.measureNumbered(tree).getUsedArea();
    }

    FastEvaluator.scratch.copyFrom(FastEvaluator.original);
    sequence.apply(FastEvaluator.scratch);

    if (FastEvaluator.scratch.sameAs(FastEvaluator.original))
    {
      return Integer.MIN_VALUE;
    }

    return (int) -(FastEvaluator.scratch.place() - FastEvaluator.used);
  }
}
