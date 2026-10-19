package poems.action3;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

/**
 * Move node action. Removes a single node from the tree (its children take
 * its place) and inserts it as the left or the right child of the target
 * node. The original child of the target node becomes the child of the moved
 * node on the same side. This is the standard B*-Tree perturbation, finer
 * than moving the whole subtree (see the hang node action).
 *
 * @author Vojtěch Hordějčuk
 */
public class MoveNodePoemsAction extends AbstractPoemsAction
{
  /**
   * insert as the left child (TRUE) or the right child (FALSE)
   */
  private final boolean left;
  /**
   * number of the node to move
   */
  private final int node1;
  /**
   * number of the target node
   */
  private final int node2;

  /**
   * Creates a new action.
   *
   * @param enabled enabled flag
   * @param left insert as the left child
   * @param from number of the node to move
   * @param to number of the target node
   * @param lastNumber the last number
   * @return a new action
   */
  public static AbstractPoemsAction create(final boolean enabled, final boolean left, final int from, final int to, final int lastNumber)
  {
    return new MoveNodePoemsAction(
        enabled,
        left,
        from,
        to,
        lastNumber);
  }

  /**
   * Creates a random action.
   *
   * @param enabled enabled flag
   * @param lastNumber the last number
   * @return a random action
   */
  public static AbstractPoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new MoveNodePoemsAction(
        enabled,
        Utility.randomBoolean(),
        Utility.random(0, lastNumber),
        Utility.random(0, lastNumber),
        lastNumber);
  }

  /**
   * Creates a new instance.
   *
   * @param enabled enabled flag
   * @param left insert as the left child
   * @param number1 number of the node to move
   * @param number2 number of the target node
   * @param lastNumber the last number
   */
  private MoveNodePoemsAction(final boolean enabled, final boolean left, final int number1, final int number2, final int lastNumber)
  {
    super(enabled);

    this.left = left;
    this.node1 = number1;
    this.node2 = (number1 != number2)
        ? number2
        : Utility.increment(
            number2,
            1,
            lastNumber);
  }

  @Override
  protected AbstractPoemsAction randomize(final int lastNumber)
  {
    return new MoveNodePoemsAction(
        Utility.randomBoolean()
            ? this.isEnabled()
            : Utility.randomBoolean(),
        Utility.randomBoolean()
            ? this.left
            : Utility.randomBoolean(),
        Utility.randomIncrement(
            this.node1,
            Utility.randomBoolean(Setup.PROB_ACTION_PARAM_CHANGE) ? lastNumber / 2 : 0,
            lastNumber),
        Utility.randomIncrement(
            this.node2,
            Utility.randomBoolean(Setup.PROB_ACTION_PARAM_CHANGE) ? lastNumber / 2 : 0,
            lastNumber),
        lastNumber);
  }

  @Override
  protected long getActiveSignature()
  {
    return AbstractPoemsAction.hash(AbstractPoemsAction.hash(AbstractPoemsAction.hash(7, this.left
        ? 1
        : 0), this.node1), this.node2);
  }

  @Override
  public String toActiveString()
  {
    return String.format("move(%s,%d,%d)", this.left ? "L" : "R", this.node1, this.node2);
  }

  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    if ((this.node1 == this.node2) || (input.size() < 2))
    {
      return input;
    }

    final BTree<NumberedValue<Module>> source = Finder.find(input, this.node1);
    final BTree<NumberedValue<Module>> removed = MoveNodePoemsAction.remove(input, source);
    final BTree<NumberedValue<Module>> target = Finder.find(removed, this.node2);

    return MoveNodePoemsAction.insert(removed, source.getValue(), target, this.left);
  }

  /**
   * Removes the source node from the tree.
   *
   * @param input input tree
   * @param source node to remove
   * @return the tree without the source node
   */
  private static BTree<NumberedValue<Module>> remove(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> source)
  {
    if (input == source)
    {
      return MoveNodePoemsAction.splice(input);
    }

    return new BTree<NumberedValue<Module>>(
        input.getValue(),
        input.hasLeft()
            ? MoveNodePoemsAction.remove(input.getLeft(), source)
            : null,
        input.hasRight()
            ? MoveNodePoemsAction.remove(input.getRight(), source)
            : null);
  }

  /**
   * Returns the subtree that replaces the removed node. If the node has both
   * children, the left child takes its place and the right subtree is hung
   * on the top of the right-most branch of the left child (it stays above).
   *
   * @param node removed node
   * @return replacement subtree or NULL
   */
  private static BTree<NumberedValue<Module>> splice(final BTree<NumberedValue<Module>> node)
  {
    if (!node.hasLeft())
    {
      return node.hasRight()
          ? node.getRight()
          : null;
    }

    if (!node.hasRight())
    {
      return node.getLeft();
    }

    return MoveNodePoemsAction.hangOnTop(node.getLeft(), node.getRight());
  }

  /**
   * Hangs the subtree as the right child of the last node of the right-most
   * branch (following the right children).
   *
   * @param tree tree
   * @param subtree subtree to hang
   * @return the new tree
   */
  private static BTree<NumberedValue<Module>> hangOnTop(final BTree<NumberedValue<Module>> tree, final BTree<NumberedValue<Module>> subtree)
  {
    return new BTree<NumberedValue<Module>>(
        tree.getValue(),
        tree.hasLeft()
            ? tree.getLeft()
            : null,
        tree.hasRight()
            ? MoveNodePoemsAction.hangOnTop(tree.getRight(), subtree)
            : subtree);
  }

  /**
   * Inserts the value as the child of the target node. The original child of
   * the target becomes the child of the new node on the same side.
   *
   * @param input input tree
   * @param value value to insert
   * @param target target node
   * @param left insert as the left child
   * @return the new tree
   */
  private static BTree<NumberedValue<Module>> insert(final BTree<NumberedValue<Module>> input, final NumberedValue<Module> value, final BTree<NumberedValue<Module>> target, final boolean left)
  {
    if (input == target)
    {
      final BTree<NumberedValue<Module>> oldLeft = input.hasLeft()
          ? input.getLeft()
          : null;
      final BTree<NumberedValue<Module>> oldRight = input.hasRight()
          ? input.getRight()
          : null;

      return left
          ? new BTree<NumberedValue<Module>>(
              input.getValue(),
              new BTree<NumberedValue<Module>>(value, oldLeft, null),
              oldRight)
          : new BTree<NumberedValue<Module>>(
              input.getValue(),
              oldLeft,
              new BTree<NumberedValue<Module>>(value, null, oldRight));
    }

    return new BTree<NumberedValue<Module>>(
        input.getValue(),
        input.hasLeft()
            ? MoveNodePoemsAction.insert(input.getLeft(), value, target, left)
            : null,
        input.hasRight()
            ? MoveNodePoemsAction.insert(input.getRight(), value, target, left)
            : null);
  }
}
