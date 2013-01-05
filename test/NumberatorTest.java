import org.junit.Test;

import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;

/**
 * Number test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class NumberatorTest
{
  /**
   * test
   */
  @Test
  public void testNumber()
  {
    final BTree<Dummy> d = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> e = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> f = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> g = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> b = new BTree<Dummy>(new Dummy(), d, e);
    final BTree<Dummy> c = new BTree<Dummy>(new Dummy(), f, g);
    final BTree<Dummy> a = new BTree<Dummy>(new Dummy(), b, c);
    
    final BTree<NumberedValue<Dummy>> result1 = Numberator.number(a);
    final BTree<NumberedValue<Dummy>> result2 = Numberator.number(c);
    final BTree<NumberedValue<Dummy>> result3 = Numberator.number(d);
    
    TestUtility.testNumbered(result1, 7);
    TestUtility.testNumbered(result2, 3);
    TestUtility.testNumbered(result3, 1);
  }
}
