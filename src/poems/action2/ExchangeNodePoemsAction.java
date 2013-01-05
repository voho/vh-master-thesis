package poems.action2;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class ExchangeNodePoemsAction extends AbstractPoemsAction
{
  private final int node1;
  private final int node2;
  
  public static ExchangeNodePoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new ExchangeNodePoemsAction(
        enabled,
        Utility.random(0, lastNumber),
        Utility.random(0, lastNumber),
        lastNumber);
  }
  
  public static ExchangeNodePoemsAction create(final boolean enabled, final int number1, final int number2, final int lastNumber)
  {
    return new ExchangeNodePoemsAction(
        enabled,
        number1,
        number2,
        lastNumber);
  }
  
  @Override
  public ExchangeNodePoemsAction randomize(final int lastNumber)
  {
    return new ExchangeNodePoemsAction(
        Utility.randomBoolean()
            ? this.isEnabled()
            : Utility.randomBoolean(),
        Utility.randomIncrement(
            this.node1,
            Utility.randomBoolean(Setup.PROB_ACTION_PARAM_CHANGE)
                ? lastNumber / 2
                : 0,
            lastNumber),
        Utility.randomIncrement(
            this.node2,
            Utility.randomBoolean(Setup.PROB_ACTION_PARAM_CHANGE)
                ? lastNumber / 2
                : 0,
            lastNumber),
        lastNumber);
  }
  
  private ExchangeNodePoemsAction(final boolean enabled, final int number1, final int number2, final int lastNumber)
  {
    super(enabled);
    
    this.node1 = number1;
    this.node2 = (number1 != number2)
        ? number2
        : Utility.increment(
            number2,
            1,
            lastNumber);
  }
  
  @Override
  public String toActiveString()
  {
    return String.format("xchangenode(%d,%d)", this.node1, this.node2);
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    if (this.node1 == this.node2)
    {
      return input;
    }
    
    final BTree<NumberedValue<Module>> node1 = Finder.find(input, this.node1);
    final BTree<NumberedValue<Module>> node2 = Finder.find(input, this.node2);
    
    if (!Finder.disjoint(node1, node2))
    {
      return input;
    }
    
    return this.apply(input, node1, node2);
  }
  
  private BTree<NumberedValue<Module>> apply(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> module1, final BTree<NumberedValue<Module>> module2)
  {
    final BTree<NumberedValue<Module>> pivot;
    
    if (input == module1)
    {
      pivot = module2;
    }
    else if (input == module2)
    {
      pivot = module1;
    }
    else
    {
      pivot = input;
    }
    
    return new BTree<NumberedValue<Module>>(
        pivot.getValue(),
        pivot.hasLeft()
            ? this.apply(
                pivot.getLeft(),
                module1,
                module2)
            : null,
        pivot.hasRight()
            ? this.apply(
                pivot.getRight(),
                module1,
                module2)
            : null);
  }
}
