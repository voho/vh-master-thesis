import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import module.Module;
import module.PlacedModule;

import org.junit.Assert;
import org.junit.Test;

import poems.hybrid.LatePoems;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Late acceptance POEMS test.
 *
 * @author Vojtěch Hordějčuk
 */
public class LatePoemsTest
{
  /**
   * test
   */
  @Test
  public void testSolve()
  {
    Utility.setSeed(3);

    final Set<Module> modules = new HashSet<Module>();

    for (int i = 0; i < 25; i++)
    {
      modules.add(new Module(String.valueOf(i), Utility.random(1, 40), Utility.random(1, 40)));
    }

    final int prototypeArea = Placer.measureNumbered(Numberator.number(Prototyper.createBestFit(modules))).getTotalArea();
    final BTree<NumberedValue<Module>> result = LatePoems.solve(modules, 60000, 50000, 3, 100);
    final BTree<PlacedModule> placed = Placer.placeNumbered(result);

    Assert.assertEquals(50000, LatePoems.getEvaluations());
    Assert.assertEquals(25, result.size());
    Assert.assertTrue(Evaluator.evaluate(placed).getTotalArea() <= prototypeArea);

    final List<PlacedModule> list = new ArrayList<PlacedModule>();
    final Set<String> names = new HashSet<String>();

    for (final BTree<PlacedModule> node : placed)
    {
      list.add(node.getValue());
      names.add(node.getValue().getModule().getName());
    }

    Assert.assertEquals(25, names.size());

    for (int p = 0; p < list.size(); p++)
    {
      for (int q = p + 1; q < list.size(); q++)
      {
        final PlacedModule m1 = list.get(p);
        final PlacedModule m2 = list.get(q);

        Assert.assertTrue((m1.getMaxPosition().getX() <= m2.getPosition().getX())
            || (m2.getMaxPosition().getX() <= m1.getPosition().getX())
            || (m1.getMaxPosition().getY() <= m2.getPosition().getY())
            || (m2.getMaxPosition().getY() <= m1.getPosition().getY()));
      }
    }
  }
}
