package btree.utility;

import btree.BTree;
import btree.BTreeValue;
import btree.NumberedValue;

/**
 * Class for numbering the B*-Tree nodes.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Numberator
{
  /**
   * No creation is allowed.
   */
  private Numberator()
  {
    // NOP
  }
  
  /**
   * Numbers the given B*-Tree.
   * 
   * @param <V> tree value class
   * @param input tree to be numbered
   * @return numberd input tree
   */
  public static <V extends BTreeValue> BTree<NumberedValue<V>> number(final BTree<V> input)
  {
    return Numberator.number(input, new Counter());
  }
  
  /**
   * Numbers the given subtree.
   * 
   * @param <V> tree value class
   * @param input subtree to be numbered
   * @param counter counter used for numbering (hot potato)
   * @return numbered input subtree
   */
  private static <V extends BTreeValue> BTree<NumberedValue<V>> number(final BTree<V> input, final Counter counter)
  {
    return new BTree<NumberedValue<V>>(
        NumberedValue.create(
            counter,
            input.getValue()),
        input.hasLeft()
            ? Numberator.number(
                input.getLeft(),
                counter)
            : null,
        input.hasRight()
            ? Numberator.number(
                input.getRight(),
                counter)
            : null);
  }
}
