import org.junit.Assert;
import org.junit.Test;

import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;
import btree.utility.Numberator;

/**
 * Finder test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class FinderTest
{
  /**
   * test
   */
  @Test
  public void testFind1()
  {
    final BTree<Dummy> e = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> d = new BTree<Dummy>(new Dummy(), null, e);
    final BTree<Dummy> c = new BTree<Dummy>(new Dummy(), d, null);
    final BTree<Dummy> b = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> a = new BTree<Dummy>(new Dummy(), b, c);
    
    final BTree<NumberedValue<Dummy>> na = Numberator.number(a);
    
    Assert.assertSame(a.getValue(), Finder.find(na, 0).getValue().getValue());
    Assert.assertSame(b.getValue(), Finder.find(na, 1).getValue().getValue());
    Assert.assertSame(c.getValue(), Finder.find(na, 2).getValue().getValue());
    Assert.assertSame(d.getValue(), Finder.find(na, 3).getValue().getValue());
    Assert.assertSame(e.getValue(), Finder.find(na, 4).getValue().getValue());
  }
  
  /**
   * test
   */
  @Test
  public void testContains()
  {
    final BTree<Dummy> g = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> f = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> e = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> d = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> c = new BTree<Dummy>(new Dummy(), f, g);
    final BTree<Dummy> b = new BTree<Dummy>(new Dummy(), d, e);
    final BTree<Dummy> a = new BTree<Dummy>(new Dummy(), b, c);
    
    Assert.assertTrue(Finder.contains(a, a));
    Assert.assertTrue(Finder.contains(a, b));
    Assert.assertTrue(Finder.contains(a, c));
    Assert.assertTrue(Finder.contains(a, d));
    Assert.assertTrue(Finder.contains(a, e));
    Assert.assertTrue(Finder.contains(a, f));
    Assert.assertTrue(Finder.contains(a, g));
    
    Assert.assertFalse(Finder.contains(b, a));
    Assert.assertTrue(Finder.contains(b, b));
    Assert.assertFalse(Finder.contains(b, c));
    Assert.assertTrue(Finder.contains(b, d));
    Assert.assertTrue(Finder.contains(b, e));
    Assert.assertFalse(Finder.contains(b, f));
    Assert.assertFalse(Finder.contains(b, g));
    
    Assert.assertFalse(Finder.contains(c, a));
    Assert.assertFalse(Finder.contains(c, b));
    Assert.assertTrue(Finder.contains(c, c));
    Assert.assertFalse(Finder.contains(c, d));
    Assert.assertFalse(Finder.contains(c, e));
    Assert.assertTrue(Finder.contains(c, f));
    Assert.assertTrue(Finder.contains(c, g));
  }
  
  /**
   * test
   */
  @Test
  public void testDisjoint()
  {
    final BTree<Dummy> g = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> f = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> e = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> d = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> c = new BTree<Dummy>(new Dummy(), f, g);
    final BTree<Dummy> b = new BTree<Dummy>(new Dummy(), d, e);
    final BTree<Dummy> a = new BTree<Dummy>(new Dummy(), b, c);
    
    TestUtility.testDisjointHelp(false, a, a);
    TestUtility.testDisjointHelp(false, b, b);
    TestUtility.testDisjointHelp(false, c, c);
    
    TestUtility.testDisjointHelp(false, a, b);
    TestUtility.testDisjointHelp(false, a, c);
    TestUtility.testDisjointHelp(false, a, f);
    TestUtility.testDisjointHelp(false, a, g);
    TestUtility.testDisjointHelp(false, a, d);
    TestUtility.testDisjointHelp(false, a, e);
    
    TestUtility.testDisjointHelp(true, b, c);
    TestUtility.testDisjointHelp(true, d, e);
    TestUtility.testDisjointHelp(true, f, g);
    TestUtility.testDisjointHelp(true, d, f);
    TestUtility.testDisjointHelp(true, d, g);
    TestUtility.testDisjointHelp(true, e, f);
    TestUtility.testDisjointHelp(true, e, g);
    
    TestUtility.testDisjointHelp(true, b, f);
    TestUtility.testDisjointHelp(true, b, g);
    TestUtility.testDisjointHelp(true, c, d);
    TestUtility.testDisjointHelp(true, c, e);
  }
}
