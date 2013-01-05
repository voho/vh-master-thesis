package btree.utility;

import module.PlacedModule;
import btree.BTree;

/**
 * Floorplan evaluator.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Evaluator
{
  /**
   * No creation allowed.
   */
  private Evaluator()
  {
    // NOP
  }
  
  /**
   * Checks if two B*-Trees equal each other. The equality means the same
   * structure and same node values (must be equal too.)
   * 
   * @param a first tree
   * @param b second tree
   * @return TRUE if both tree equals, FALSE otherwise
   */
  public static boolean equals(final BTree<?> a, final BTree<?> b)
  {
    if ((a == null) || (b == null))
    {
      throw new NullPointerException("Cannot compare tree with a NULL value.");
    }
    
    if (!a.getValue().equals(b.getValue()))
    {
      // values differ
      return false;
    }
    
    if (a.hasLeft() != b.hasLeft())
    {
      // left subtree differs
      return false;
    }
    
    if (a.hasRight() != b.hasRight())
    {
      // right subtree differs
      return false;
    }
    
    if (a.hasLeft() && !Evaluator.equals(a.getLeft(), b.getLeft()))
    {
      // left subtree differs
      return false;
    }
    
    if (a.hasRight() && !Evaluator.equals(a.getRight(), b.getRight()))
    {
      // right subtree differs
      return false;
    }
    
    return true;
  }
  
  /**
   * Evaluater a quality of a floorplan.
   * 
   * @param btree input floorplan in the form of B*-Tree
   * @return the quality evaluation
   */
  public static Evaluation evaluate(final BTree<PlacedModule> btree)
  {
    int xmax = 0;
    int ymax = 0;
    int area = 0;
    
    for (final BTree<PlacedModule> node : btree)
    {
      xmax = Math.max(xmax, node.getValue().getMaxPosition().getX());
      ymax = Math.max(ymax, node.getValue().getMaxPosition().getY());
      area += node.getValue().getModule().getArea();
    }
    
    return new Evaluation(xmax, ymax, area);
  }
}
