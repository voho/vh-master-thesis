import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import module.PlacedModule;

import org.junit.Assert;

import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

/**
 * Test utility.
 * 
 * @author Vojtěch Hordějčuk
 */
public class TestUtility
{
  /**
   * No creation is allowed.
   */
  private TestUtility()
  {
    // NOP
  }
  
  /**
   * utility method
   * 
   * @param output output
   * @param name name
   * @param x X coordinate
   * @param y Y coordinate
   */
  public static void assertPosition(final BTree<PlacedModule> output, final String name, final int x, final int y)
  {
    for (final BTree<PlacedModule> m : output)
    {
      if (m.getValue().getModule().getName().equals(name))
      {
        Assert.assertEquals(x, m.getValue().getPosition().getX());
        Assert.assertEquals(y, m.getValue().getPosition().getY());
      }
    }
  }
  
  /**
   * utility method
   * 
   * @param result result
   * @param a first tree
   * @param b second tree
   */
  public static void testDisjointHelp(final boolean result, final BTree<?> a, final BTree<?> b)
  {
    if (result)
    {
      Assert.assertTrue(Finder.disjoint(a, b));
      Assert.assertTrue(Finder.disjoint(b, a));
    }
    else
    {
      Assert.assertFalse(Finder.disjoint(a, b));
      Assert.assertFalse(Finder.disjoint(b, a));
    }
  }
  
  /**
   * utility method
   * 
   * @param btree B*-tree
   * @param nodes node sequence
   */
  public static void testNumberedTree(final BTree<? extends NumberedValue<?>> btree, final BTree<?>... nodes)
  {
    final BTree<?>[] correct = (BTree<?>[]) Arrays.asList(nodes).toArray();
    
    int i = 0;
    
    for (final BTree<? extends NumberedValue<?>> testing : btree)
    {
      Assert.assertEquals(correct[i++].getValue(), testing.getValue().getValue());
    }
  }
  
  /**
   * utility method
   * 
   * @param btree B*-tree
   * @param count count
   */
  public static void testNumbered(final BTree<? extends NumberedValue<?>> btree, final int count)
  {
    final Set<Integer> numbers = new HashSet<Integer>();
    
    for (final BTree<? extends NumberedValue<?>> temp : btree)
    {
      numbers.add(temp.getValue().getNumber());
    }
    
    Assert.assertEquals(count, numbers.size());
    
    for (int i = 0; i < count; i++)
    {
      Assert.assertTrue(numbers.contains(i));
    }
  }
  
  /**
   * utility method
   * 
   * @param btree B*-tree
   * @param nodes node sequence
   */
  public static void testTree(final BTree<?> btree, final BTree<?>... nodes)
  {
    final Object[] correct = Arrays.asList(nodes).toArray();
    
    int i = 0;
    
    for (final BTree<?> testing : btree)
    {
      Assert.assertEquals(correct[i++], testing);
    }
  }
}
