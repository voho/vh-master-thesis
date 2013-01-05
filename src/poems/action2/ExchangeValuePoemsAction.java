package poems.action2;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class ExchangeValuePoemsAction extends AbstractPoemsAction
{
  private final int node1;
  private final int node2;
  
  public static ExchangeValuePoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new ExchangeValuePoemsAction(
        enabled,
        Utility.random(0, lastNumber),
        Utility.random(0, lastNumber),
        lastNumber);
  }
  
  public static ExchangeValuePoemsAction create(final boolean enabled, final int number1, final int number2, final int lastNumber)
  {
    return new ExchangeValuePoemsAction(
        enabled,
        number1,
        number2,
        lastNumber);
  }
  
  @Override
  public AbstractPoemsAction randomize(final int lastNumber)
  {
    return new ExchangeValuePoemsAction(
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
  
  private ExchangeValuePoemsAction(final boolean enabled, final int number1, final int number2, final int lastNumber)
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
    return String.format("xchangeval(%d,%d)", this.node1, this.node2);
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    if (this.node1 == this.node2)
    {
      return input;
    }
    
    final BTree<NumberedValue<Module>> a = Finder.find(input, this.node1);
    final BTree<NumberedValue<Module>> b = Finder.find(input, this.node2);
    
    return ExchangeValuePoemsAction.exchange(input, a, b);
  }
  
  private static BTree<NumberedValue<Module>> exchange(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> a, final BTree<NumberedValue<Module>> b)
  {
    final NumberedValue<Module> value;
    
    if (input == a)
    {
      value = b.getValue();
    }
    else if (input == b)
    {
      value = a.getValue();
    }
    else
    {
      value = input.getValue();
    }
    
    return new BTree<NumberedValue<Module>>(
        value,
        input.hasLeft()
            ? ExchangeValuePoemsAction.exchange(
                input.getLeft(),
                a,
                b)
            : null,
        input.hasRight()
            ? ExchangeValuePoemsAction.exchange(
                input.getRight(),
                a,
                b)
            : null);
  }
}
