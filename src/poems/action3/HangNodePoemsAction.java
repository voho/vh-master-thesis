package poems.action3;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class HangNodePoemsAction extends AbstractPoemsAction
{
  private final boolean left;
  private final int node1;
  private final int node2;
  
  public static AbstractPoemsAction create(final boolean enabled, final boolean left, final int from, final int to, final int lastNumber)
  {
    return new HangNodePoemsAction(
        enabled,
        left,
        from,
        to,
        lastNumber);
  }
  
  public static AbstractPoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new HangNodePoemsAction(
        enabled,
        Utility.randomBoolean(),
        Utility.random(0, lastNumber),
        Utility.random(0, lastNumber),
        lastNumber);
  }
  
  private HangNodePoemsAction(final boolean enabled, final boolean left, final int number1, final int number2, final int lastNumber)
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
    return new HangNodePoemsAction(
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
  public String toActiveString()
  {
    return String.format("hang(%s,%d,%d)", this.left ? "L" : "R", this.node1, this.node2);
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    final BTree<NumberedValue<Module>> nodeFrom = Finder.find(input, this.node1);
    final BTree<NumberedValue<Module>> nodeTo = Finder.find(input, this.node2);
    
    if (Finder.contains(nodeFrom, nodeTo))
    {
      return input;
    }
    
    if (Finder.contains(nodeTo, nodeFrom, this.left))
    {
      return input;
    }
    
    return HangNodePoemsAction.use(input, nodeFrom, nodeTo, this.left);
  }
  
  private static BTree<NumberedValue<Module>> getOldTargetChild(final BTree<NumberedValue<Module>> targetNode, final boolean left)
  {
    return left
        ? targetNode.hasLeft()
            ? targetNode.getLeft()
            : null
        : targetNode.hasRight()
            ? targetNode.getRight()
            : null;
  }
  
  private static BTree<NumberedValue<Module>> createNewSource(final BTree<NumberedValue<Module>> targetNode, final boolean left)
  {
    return HangNodePoemsAction.getOldTargetChild(targetNode, left);
  }
  
  private static BTree<NumberedValue<Module>> createNewTarget(final BTree<NumberedValue<Module>> targetNode, final BTree<NumberedValue<Module>> sourceNode, final boolean left)
  {
    return new BTree<NumberedValue<Module>>(
        targetNode.getValue(),
        left
            ? sourceNode
            : targetNode.hasLeft()
                ? HangNodePoemsAction.use(targetNode.getLeft(), sourceNode, targetNode, left)
                : null,
        left
            ? targetNode.hasRight()
                ? HangNodePoemsAction.use(targetNode.getRight(), sourceNode, targetNode, left)
                : null
            : sourceNode);
  }
  
  private static BTree<NumberedValue<Module>> use(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> sourceNode, final BTree<NumberedValue<Module>> targetNode, final boolean left)
  {
    if (input == sourceNode)
    {
      return HangNodePoemsAction.createNewSource(targetNode, left);
    }
    else if (input == targetNode)
    {
      return HangNodePoemsAction.createNewTarget(targetNode, sourceNode, left);
    }
    else
    {
      return new BTree<NumberedValue<Module>>(
          input.getValue(),
          input.hasLeft()
              ? HangNodePoemsAction.use(
                  input.getLeft(),
                  sourceNode,
                  targetNode,
                  left)
              : null,
          input.hasRight()
              ? HangNodePoemsAction.use(
                  input.getRight(),
                  sourceNode,
                  targetNode,
                  left)
              : null);
    }
  }
}
