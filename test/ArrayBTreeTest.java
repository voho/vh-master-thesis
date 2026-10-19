import java.util.HashSet;
import java.util.Set;

import module.Module;

import org.junit.Assert;
import org.junit.Test;

import poems.AbstractPoemsAction;
import poems.action1.RotatePoemsAction;
import poems.action2.ExchangeNodePoemsAction;
import poems.action2.ExchangeValuePoemsAction;
import poems.action2.FlipPoemsAction;
import poems.action2.MirrorPoemsAction;
import poems.action3.HangNodePoemsAction;
import poems.action3.MoveNodePoemsAction;
import program.Utility;
import btree.ArrayBTree;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluation;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Array B*-Tree test. The actions must produce the same trees as the POEMS
 * actions working with the immutable B*-Tree.
 *
 * @author Vojtěch Hordějčuk
 */
public class ArrayBTreeTest
{
  /**
   * test
   */
  @Test
  public void testConversion()
  {
    final BTree<NumberedValue<Module>> tree = ArrayBTreeTest.createTree(40, 1);
    final ArrayBTree array = new ArrayBTree(tree);

    Assert.assertTrue(Evaluator.equals(tree, array.toBTree()));

    final Evaluation evaluation = Placer.measureNumbered(tree);
    Assert.assertEquals((long) evaluation.getTotalArea(), array.place());
    Assert.assertEquals(evaluation.getWidth(), array.getWidth());
    Assert.assertEquals(evaluation.getHeight(), array.getHeight());
  }

  /**
   * test
   */
  @Test
  public void testActionsMatchPoemsActions()
  {
    for (int seed = 1; seed <= 5; seed++)
    {
      BTree<NumberedValue<Module>> tree = ArrayBTreeTest.createTree(40, seed);
      final ArrayBTree array = new ArrayBTree(tree);
      final int last = tree.size() - 1;

      for (int i = 0; i < 2000; i++)
      {
        final int a = Utility.random(0, last);
        final int b = Utility.random(0, last);
        final boolean flag = Utility.randomBoolean();
        final AbstractPoemsAction action;

        switch (Utility.random(0, 6))
        {
        case 0:
          action = FlipPoemsAction.create(true, a, flag);
          array.flip(a, flag);
          break;
        case 1:
          action = RotatePoemsAction.create(true, a);
          array.mirror(a, true, true);
          break;
        case 2:
          action = MirrorPoemsAction.create(true, a, flag);
          array.mirror(a, flag, false);
          break;
        case 3:
          action = ExchangeValuePoemsAction.create(true, a, b, last);
          array.exchangeValues(a, (a != b) ? b : Utility.increment(b, 1, last));
          break;
        case 4:
          action = ExchangeNodePoemsAction.create(true, a, b, last);
          array.exchangeNodes(a, (a != b) ? b : Utility.increment(b, 1, last));
          break;
        case 5:
          action = HangNodePoemsAction.create(true, flag, a, b, last);
          array.hang(a, (a != b) ? b : Utility.increment(b, 1, last), flag);
          break;
        default:
          action = MoveNodePoemsAction.create(true, flag, a, b, last);
          array.move(a, (a != b) ? b : Utility.increment(b, 1, last), flag);
          break;
        }

        tree = action.apply(tree);

        Assert.assertTrue(action.toString(), Evaluator.equals(tree, array.toBTree()));
        Assert.assertEquals(action.toString(), (long) Placer.measureNumbered(tree).getTotalArea(), array.place());
      }
    }
  }

  /**
   * test
   */
  @Test
  public void testCopy()
  {
    final ArrayBTree original = new ArrayBTree(ArrayBTreeTest.createTree(20, 7));
    final ArrayBTree copy = new ArrayBTree(original);

    Assert.assertTrue(copy.sameAs(original));

    copy.move(3, 5, true);
    copy.flip(7, false);
    Assert.assertFalse(copy.sameAs(original));

    copy.copyFrom(original);
    Assert.assertTrue(copy.sameAs(original));
  }

  /**
   * Creates a random numbered prototype.
   *
   * @param count number of modules
   * @param seed random seed
   * @return numbered prototype
   */
  private static BTree<NumberedValue<Module>> createTree(final int count, final long seed)
  {
    Utility.setSeed(seed);

    final Set<Module> modules = new HashSet<Module>();

    for (int i = 0; i < count; i++)
    {
      modules.add(new Module(String.valueOf(i), Utility.random(1, 60), Utility.random(1, 60)));
    }

    return Numberator.number(Prototyper.createBestFit(modules));
  }
}
