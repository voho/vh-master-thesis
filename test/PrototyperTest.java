import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import module.Module;
import module.PlacedModule;

import org.junit.Test;

import btree.BTree;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;
import btree.utility.Prototyper;

/**
 * Prototyper test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PrototyperTest
{
  /**
   * test
   */
  @Test
  public void testPrototype()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    final Module a = new Module("A", 10, 10);
    final Module b = new Module("B", 9, 9);
    final Module c = new Module("C", 8, 8);
    final Module d = new Module("D", 7, 7);
    final Module e = new Module("E", 4, 6);
    final Module f = new Module("F", 3, 8);
    final Module g = new Module("G", 2, 12);
    final Module h = new Module("H", 1, 24);
    
    final BTree<Module> ht = new BTree<Module>(h.flip(), null, null);
    final BTree<Module> gt = new BTree<Module>(g.flip(), null, null);
    final BTree<Module> ft = new BTree<Module>(f.flip(), null, null);
    final BTree<Module> et = new BTree<Module>(e.flip(), null, null);
    final BTree<Module> dt = new BTree<Module>(d, ht, null);
    final BTree<Module> ct = new BTree<Module>(c, ft, gt);
    final BTree<Module> bt = new BTree<Module>(b, dt, et);
    final BTree<Module> at = new BTree<Module>(a, bt, ct);
    
    modules.add(a);
    modules.add(b);
    modules.add(c);
    modules.add(d);
    modules.add(e);
    modules.add(f);
    modules.add(g);
    modules.add(h);
    
    final BTree<Module> p = Prototyper.createCompleteTree(modules);
    TestUtility.testTree(p, et, bt, gt, ct, ht, ft, at, dt);
  }
  
  /**
   * test
   */
  @Test
  public void testBestFitPrototype()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    for (int i = 0; i < 9; i++)
    {
      modules.add(new Module(String.valueOf(i), 10, 10));
    }
    
    final BTree<Module> prototype = Prototyper.createBestFit(modules);
    final BTree<PlacedModule> placed = Placer.placeNumbered(Numberator.number(prototype));
    Assert.assertEquals(0, Evaluator.evaluate(placed).getUnusedArea());
  }
  
  /**
   * test
   */
  @Test
  public void testBestFitPrototypeNoException()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    for (int i = 0; i < 100; i++)
    {
      modules.add(new Module(String.valueOf(i), 1 + i * 10, 1 + i * 10));
    }
    
    Prototyper.createBestFit(modules);
  }
  
  /**
   * test
   */
  @Test
  public void testBestFitPrototypeHp()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    modules.add(new Module("1", 1036, 462));
    modules.add(new Module("2", 378, 700));
    modules.add(new Module("3", 980, 210));
    modules.add(new Module("4", 980, 210));
    modules.add(new Module("5", 980, 210));
    modules.add(new Module("6", 3304, 546));
    modules.add(new Module("7", 3304, 546));
    modules.add(new Module("8", 2016, 252));
    modules.add(new Module("9", 3080, 462));
    modules.add(new Module("10", 2016, 252));
    modules.add(new Module("11", 3080, 462));
    
    Prototyper.createBestFit(modules);
  }
}
