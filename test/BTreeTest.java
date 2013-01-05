import org.junit.Assert;
import org.junit.Test;

import btree.BTree;

/**
 * B*-Tree test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class BTreeTest
{
  /**
   * test
   */
  @Test
  public void testEquals()
  {
    final BTree<Dummy> tree = new BTree<Dummy>(new Dummy(), null, null);
    
    Assert.assertTrue(tree.equals(tree));
    Assert.assertFalse(tree.equals(null));
    Assert.assertFalse(tree.equals("funky"));
    Assert.assertFalse(tree.equals(132));
  }
  
  /**
   * test
   */
  @Test
  public void testHasChildren()
  {
    final BTree<Dummy> subtree = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> tree = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> treeL = new BTree<Dummy>(new Dummy(), subtree, null);
    final BTree<Dummy> treeR = new BTree<Dummy>(new Dummy(), null, subtree);
    final BTree<Dummy> treeLR = new BTree<Dummy>(new Dummy(), subtree, subtree);
    
    Assert.assertEquals(false, tree.hasLeft());
    Assert.assertEquals(false, tree.hasRight());
    
    Assert.assertEquals(true, treeL.hasLeft());
    Assert.assertEquals(false, treeL.hasRight());
    
    Assert.assertEquals(false, treeR.hasLeft());
    Assert.assertEquals(true, treeR.hasRight());
    
    Assert.assertEquals(true, treeLR.hasLeft());
    Assert.assertEquals(true, treeLR.hasRight());
  }
  
  /**
   * test
   */
  @Test
  public void testGetLeft()
  {
    final BTree<Dummy> subtree = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> treeL = new BTree<Dummy>(new Dummy(), subtree, null);
    final BTree<Dummy> treeLR = new BTree<Dummy>(new Dummy(), subtree, subtree);
    
    Assert.assertEquals(subtree, treeL.getLeft());
    Assert.assertEquals(subtree, treeLR.getLeft());
  }
  
  /**
   * test
   */
  @Test
  public void testGetRight()
  {
    final BTree<Dummy> subtree = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> treeR = new BTree<Dummy>(new Dummy(), null, subtree);
    final BTree<Dummy> treeLR = new BTree<Dummy>(new Dummy(), subtree, subtree);
    
    Assert.assertEquals(subtree, treeR.getRight());
    Assert.assertEquals(subtree, treeLR.getRight());
  }
  
  /**
   * test
   */
  @Test
  public void testGetValue()
  {
    final Dummy value = new Dummy();
    
    final BTree<Dummy> tree = new BTree<Dummy>(value, null, null);
    
    Assert.assertEquals(value, tree.getValue());
  }
  
  /**
   * test
   */
  @Test
  public void testSize()
  {
    final BTree<Dummy> t31 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t32 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t33 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t34 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t21 = new BTree<Dummy>(new Dummy(), t31, t32);
    final BTree<Dummy> t22 = new BTree<Dummy>(new Dummy(), t33, t34);
    final BTree<Dummy> t11 = new BTree<Dummy>(new Dummy(), t21, t22);
    
    Assert.assertEquals(7, t11.size());
    Assert.assertEquals(3, t21.size());
    Assert.assertEquals(3, t22.size());
    Assert.assertEquals(1, t31.size());
    Assert.assertEquals(1, t32.size());
    Assert.assertEquals(1, t33.size());
    Assert.assertEquals(1, t34.size());
  }
  
  /**
   * test
   */
  @Test
  public void testIterator()
  {
    final BTree<Dummy> t31 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t32 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t33 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t34 = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> t21 = new BTree<Dummy>(new Dummy(), t31, t32);
    final BTree<Dummy> t22 = new BTree<Dummy>(new Dummy(), t33, t34);
    final BTree<Dummy> t11 = new BTree<Dummy>(new Dummy(), t21, t22);
    
    TestUtility.testTree(t11, t31, t32, t21, t33, t34, t22, t11);
  }
}
