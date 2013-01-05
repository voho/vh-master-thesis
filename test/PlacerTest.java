import module.Module;
import module.PlacedModule;

import org.junit.Test;

import btree.BTree;
import btree.utility.Numberator;
import btree.utility.Placer;

/**
 * Placer test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PlacerTest
{
  /**
   * test
   */
  @Test
  public void testPlace1()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 10), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 30, 10), g, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 30, 20), null, f);
    final BTree<Module> d = new BTree<Module>(new Module("D", 50, 10), null, e);
    final BTree<Module> c = new BTree<Module>(new Module("C", 10, 70), null, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 20, 40), c, null);
    final BTree<Module> a = new BTree<Module>(new Module("A", 30, 20), b, d);
    
    final BTree<PlacedModule> result = Placer.placeNumbered(Numberator.number(a));
    
    TestUtility.assertPosition(result, "A", 0, 0);
    TestUtility.assertPosition(result, "B", 30, 0);
    TestUtility.assertPosition(result, "C", 50, 0);
    TestUtility.assertPosition(result, "D", 0, 40);
    TestUtility.assertPosition(result, "E", 0, 50);
    TestUtility.assertPosition(result, "F", 0, 70);
    TestUtility.assertPosition(result, "G", 30, 70);
  }
  
  /**
   * test
   */
  @Test
  public void testPlace2()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<PlacedModule> result = Placer.placeNumbered(Numberator.number(a));
    
    TestUtility.assertPosition(result, "A", 0, 0);
    TestUtility.assertPosition(result, "B", 40, 0);
    TestUtility.assertPosition(result, "C", 0, 30);
    TestUtility.assertPosition(result, "D", 40, 20);
    TestUtility.assertPosition(result, "E", 70, 0);
    TestUtility.assertPosition(result, "F", 20, 30);
    TestUtility.assertPosition(result, "G", 60, 20);
  }
  
  /**
   * test
   */
  @Test
  public void testPlace3()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 40, 20), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 30, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), f, g);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 20), null, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 30, 30), d, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 40, 20), null, c);
    final BTree<Module> a = new BTree<Module>(new Module("A", 30, 40), b, e);
    
    final BTree<PlacedModule> result = Placer.placeNumbered(Numberator.number(a));
    
    TestUtility.assertPosition(result, "A", 0, 0);
    TestUtility.assertPosition(result, "B", 30, 0);
    TestUtility.assertPosition(result, "C", 30, 20);
    TestUtility.assertPosition(result, "D", 60, 20);
    TestUtility.assertPosition(result, "E", 0, 40);
    TestUtility.assertPosition(result, "F", 20, 50);
    TestUtility.assertPosition(result, "G", 0, 70);
  }
}
