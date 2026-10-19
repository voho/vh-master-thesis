package btree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Immutable class for representing a B*-Tree.
 * 
 * @author Vojtěch Hordějčuk
 * @param <V> value class
 */
public class BTree<V extends BTreeValue> implements Iterable<BTree<V>>
{
  /**
   * internal value
   */
  private final V value;
  /**
   * left subtree or NULL
   */
  private final BTree<V> left;
  /**
   * right subtree or NULL
   */
  private final BTree<V> right;
  /**
   * size of the tree
   */
  private final int size;
  
  /**
   * Creates a new instance.
   * 
   * @param value a value (cannot be NULL)
   * @param left left subtree or NULL
   * @param right right subtree or NULL
   */
  public BTree(final V value, final BTree<V> left, final BTree<V> right)
  {
    if (value == null)
    {
      throw new NullPointerException("Cannot have a NULL value.");
    }
    
    this.value = value;
    this.left = left;
    this.right = right;
    
    int temp = 1;
    
    if (this.left != null)
    {
      temp += this.left.size;
    }
    
    if (this.right != null)
    {
      temp += this.right.size;
    }
    
    this.size = temp;
  }
  
  /**
   * Returns the value.
   * 
   * @return the value
   */
  public V getValue()
  {
    return this.value;
  }
  
  /**
   * Returns the left subtree. Throws an exception if NULL.
   * 
   * @return left subtree
   */
  public BTree<V> getLeft()
  {
    if (this.left == null)
    {
      throw new NullPointerException("Does not have the left child.");
    }
    
    return this.left;
  }
  
  /**
   * Returns the right subtree. Throws an exception if NULL.
   * 
   * @return right subtree
   */
  public BTree<V> getRight()
  {
    if (this.right == null)
    {
      throw new NullPointerException("Does not have the right child.");
    }
    
    return this.right;
  }
  
  /**
   * Checks if the left subtree exists.
   * 
   * @return TRUE if and only if the left subtree exists
   */
  public boolean hasLeft()
  {
    return this.left != null;
  }
  
  /**
   * Checks if the left subtree exists.
   * 
   * @return TRUE if and only if the right subtree exists
   */
  public boolean hasRight()
  {
    return this.right != null;
  }
  
  /**
   * Returns the tree size.
   * 
   * @return tree size
   */
  public int size()
  {
    return this.size;
  }
  
  @Override
  public Iterator<BTree<V>> iterator()
  {
    final List<BTree<V>> list = new ArrayList<BTree<V>>(this.size);
    
    // this is a DFS enumeration (post-order)
    
    BTree.collect(this, list);
    
    return list.iterator();
  }
  
  /**
   * Adds all nodes of the subtree to the list in post-order.
   * 
   * @param <V> value class
   * @param node subtree root
   * @param list output list
   */
  private static <V extends BTreeValue> void collect(final BTree<V> node, final List<BTree<V>> list)
  {
    if (node.left != null)
    {
      BTree.collect(node.left, list);
    }
    
    if (node.right != null)
    {
      BTree.collect(node.right, list);
    }
    
    list.add(node);
  }
  
  @Override
  public String toString()
  {
    final String strleft = (this.left == null ? "_" : this.left.toString());
    final String strright = (this.right == null ? "_" : this.right.toString());
    return "(" + strleft + " - " + this.value.toString() + " - " + strright + ")";
  }
  
  @Override
  public int hashCode()
  {
    return this.value.hashCode();
  }
  
  @Override
  public boolean equals(final Object that)
  {
    if (this == that)
    {
      return true;
    }
    
    if (that == null)
    {
      return false;
    }
    
    if (!(that instanceof BTree<?>))
    {
      return false;
    }
    
    final BTree<?> other = (BTree<?>) that;
    
    if (!this.value.equals(other.value))
    {
      return false;
    }
    
    return true;
  }
}
