package btree.utility;

import module.PlacedModule;
import btree.BTree;
import btree.NumberedValue;

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
    
    if (!Evaluator.equalValues(a.getValue(), b.getValue()))
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
   * Checks if two B*-Tree values equal each other. Numbered values are equal
   * only if they contain equal values, so a rotated module is not considered
   * to be the same as the original one.
   * 
   * @param a first value
   * @param b second value
   * @return TRUE if both values equal, FALSE otherwise
   */
  private static boolean equalValues(final Object a, final Object b)
  {
    if (!a.equals(b))
    {
      return false;
    }
    
    if ((a instanceof NumberedValue<?>) && (b instanceof NumberedValue<?>))
    {
      return ((NumberedValue<?>) a).getValue().equals(((NumberedValue<?>) b).getValue());
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
    final int[] box = new int[3];
    
    Evaluator.evaluate(btree, box);
    
    return new Evaluation(box[0], box[1], box[2]);
  }
  
  /**
   * Updates the bounding box and the used area by the given subtree.
   * 
   * @param btree subtree
   * @param box maximal X, maximal Y and the used area (updated)
   */
  private static void evaluate(final BTree<PlacedModule> btree, final int[] box)
  {
    final PlacedModule node = btree.getValue();
    
    box[0] = Math.max(box[0], node.getMaxPosition().getX());
    box[1] = Math.max(box[1], node.getMaxPosition().getY());
    box[2] += node.getModule().getArea();
    
    if (btree.hasLeft())
    {
      Evaluator.evaluate(btree.getLeft(), box);
    }
    
    if (btree.hasRight())
    {
      Evaluator.evaluate(btree.getRight(), box);
    }
  }
}
