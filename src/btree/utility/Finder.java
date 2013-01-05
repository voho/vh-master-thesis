package btree.utility;

import java.util.NoSuchElementException;

import btree.BTree;
import btree.NumberedValue;

/**
 * Class for searching in numbered B*-Tree nodes by number.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Finder
{
  /**
   * No creation allowed.
   */
  private Finder()
  {
    // NOP
  }
  
  /**
   * Finds a subtree with the given number or throws an exception.
   * 
   * @param <T> value class
   * @param input input B*-Tree
   * @param n searched number
   * @return a subtree with the given number
   */
  public static <T extends NumberedValue<?>> BTree<T> find(final BTree<T> input, final int n)
  {
    for (final BTree<T> temp : input)
    {
      if (temp.getValue().hasNumber(n))
      {
        // node with the searched number was found
        return temp;
      }
    }
    
    throw new NoSuchElementException(String.format("Number %d was not found in tree %s.", n, input.toString()));
  }
  
  /**
   * Checks if one B*-Tree (needle) is contained in another (haystack).
   * Containment is defined as an equality of the root with a node in the
   * haystack.
   * 
   * @param haystack B*-Tree where to search in
   * @param needle B*-Tree which is searched for
   * @return TRUE if needle is contained in haystack, FALSE otherwise
   */
  public static boolean contains(final BTree<?> haystack, final BTree<?> needle)
  {
    for (final BTree<?> temp : haystack)
    {
      if (temp.equals(needle))
      {
        return true;
      }
    }
    
    return false;
  }
  
  /**
   * Checks if one B*-Tree (needle) is contained in another`s (haystack) subtree
   * (the direction is given by a parameter). Containment is checked by
   * specialized method.
   * 
   * @param haystack B*-Tree where to search in
   * @param needle B*-Tree which is searched for
   * @param inLeftSubtree LEFT = search in the left subtree, FALSE = search in
   * the right subtree
   * @return TRUE if needle is contained in the chosen subtree of haystack,
   * FALSE otherwise
   */
  public static boolean contains(final BTree<?> haystack, final BTree<?> needle, final boolean inLeftSubtree)
  {
    return inLeftSubtree
        ? haystack.hasLeft()
            ? Finder.contains(
                haystack.getLeft(),
                needle)
            : false
        : haystack.hasRight()
            ? Finder.contains(
                haystack.getRight(),
                needle)
            : false;
  }
  
  /**
   * Check if two B*-Trees are disjoint, that is, they have no common nodes.
   * 
   * @param node1 first tree
   * @param node2 second tree
   * @return TRUE if both trees are disjoint
   */
  public static boolean disjoint(final BTree<?> node1, final BTree<?> node2)
  {
    if (node1.equals(node2))
    {
      // both tree equals
      return false;
    }
    
    if (Finder.contains(node1, node2) || Finder.contains(node2, node1))
    {
      // one tree is contained in the other
      return false;
    }
    
    return true;
  }
}
