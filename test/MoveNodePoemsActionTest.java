import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.Module;
import module.PlacedModule;

import org.junit.Assert;
import org.junit.Test;

import poems.AbstractPoemsAction;
import poems.action3.MoveNodePoemsAction;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Move node action test.
 *
 * @author Vojtěch Hordějčuk
 */
public class MoveNodePoemsActionTest
{
  /**
   * test tree: A(B(D(H), E), C(F, G)), numbered A0 B1 D2 H3 E4 C5 F6 G7
   */
  private final BTree<Module> h = new BTree<Module>(new Module("H", 30, 30), null, null);
  /**
   * node G
   */
  private final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
  /**
   * node F
   */
  private final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
  /**
   * node E
   */
  private final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
  /**
   * node D
   */
  private final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), this.h, null);
  /**
   * node C
   */
  private final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), this.f, this.g);
  /**
   * node B
   */
  private final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), this.d, this.e);
  /**
   * node A
   */
  private final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), this.b, this.c);

  /**
   * test
   */
  @Test
  public void testMoveInternalNode()
  {
    final BTree<NumberedValue<Module>> input = Numberator.number(this.a);

    // B is replaced by D (with E on top of it) and B is inserted above C

    final BTree<NumberedValue<Module>> output = MoveNodePoemsAction.create(true, false, 1, 5, 7).apply(input);
    TestUtility.testNumberedTree(output, this.h, this.e, this.d, this.f, this.g, this.b, this.c, this.a);
    Assert.assertFalse(Evaluator.equals(input, output));
  }

  /**
   * test
   */
  @Test
  public void testMoveLeaf()
  {
    final BTree<NumberedValue<Module>> input = Numberator.number(this.a);

    // H is inserted between A and B

    final BTree<NumberedValue<Module>> output = MoveNodePoemsAction.create(true, true, 3, 0, 7).apply(input);
    TestUtility.testNumberedTree(output, this.d, this.e, this.b, this.h, this.f, this.g, this.c, this.a);
  }

  /**
   * test
   */
  @Test
  public void testMoveRoot()
  {
    final BTree<NumberedValue<Module>> input = Numberator.number(this.a);

    // B becomes the root (with C on top of E) and A is inserted above G

    final BTree<NumberedValue<Module>> output = MoveNodePoemsAction.create(true, false, 0, 7, 7).apply(input);
    TestUtility.testNumberedTree(output, this.h, this.d, this.f, this.a, this.g, this.c, this.e, this.b);
  }

  /**
   * test
   */
  @Test
  public void testDisabled()
  {
    final BTree<NumberedValue<Module>> input = Numberator.number(this.a);

    Assert.assertSame(input, MoveNodePoemsAction.create(false, false, 0, 7, 7).apply(input));
  }

  /**
   * test
   */
  @Test
  public void testRandomMovesKeepValidFloorplan()
  {
    Utility.setSeed(1);

    final Set<Module> modules = new HashSet<Module>();

    for (int i = 0; i < 30; i++)
    {
      modules.add(new Module(String.valueOf(i), Utility.random(1, 50), Utility.random(1, 50)));
    }

    BTree<NumberedValue<Module>> tree = Numberator.number(Prototyper.createBestFit(modules));

    for (int i = 0; i < 2000; i++)
    {
      final AbstractPoemsAction action = MoveNodePoemsAction.random(true, 29);
      tree = action.apply(tree);

      Assert.assertEquals(30, tree.size());

      final Set<Integer> numbers = new HashSet<Integer>();
      final List<PlacedModule> placed = new ArrayList<PlacedModule>();

      for (final BTree<NumberedValue<Module>> node : tree)
      {
        numbers.add(Integer.valueOf(node.getValue().getNumber()));
      }

      for (final BTree<PlacedModule> node : Placer.placeNumbered(tree))
      {
        placed.add(node.getValue());
      }

      Assert.assertEquals(30, numbers.size());

      for (int p = 0; p < placed.size(); p++)
      {
        for (int q = p + 1; q < placed.size(); q++)
        {
          final PlacedModule m1 = placed.get(p);
          final PlacedModule m2 = placed.get(q);

          Assert.assertTrue((m1.getMaxPosition().getX() <= m2.getPosition().getX())
              || (m2.getMaxPosition().getX() <= m1.getPosition().getX())
              || (m1.getMaxPosition().getY() <= m2.getPosition().getY())
              || (m2.getMaxPosition().getY() <= m1.getPosition().getY()));
        }
      }
    }
  }
}
