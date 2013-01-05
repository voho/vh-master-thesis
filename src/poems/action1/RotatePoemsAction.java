package poems.action1;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class RotatePoemsAction extends AbstractPoemsAction
{
  private final int number;
  
  public static RotatePoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new RotatePoemsAction(
        enabled,
        Utility.random(0, lastNumber));
  }
  
  public static RotatePoemsAction create(final boolean enabled, final int number)
  {
    return new RotatePoemsAction(
        enabled,
        number);
  }
  
  private RotatePoemsAction(final boolean enabled, final int number)
  {
    super(enabled);
    
    this.number = number;
  }
  
  @Override
  public AbstractPoemsAction randomize(final int lastNumber)
  {
    return new RotatePoemsAction(
        Utility.randomBoolean()
            ? this.isEnabled()
            : Utility.randomBoolean(),
        Utility.randomIncrement(
            this.number,
            Utility.randomBoolean(Setup.PROB_ACTION_PARAM_CHANGE)
                ? lastNumber / 2
                : 0,
            lastNumber));
  }
  
  @Override
  public String toActiveString()
  {
    return String.format("rotate(%d)", this.number);
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    final BTree<NumberedValue<Module>> pivot = Finder.find(input, this.number);
    
    return RotatePoemsAction.use(input, pivot);
  }
  
  private static BTree<NumberedValue<Module>> use(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> pivot)
  {
    if (input == pivot)
    {
      return RotatePoemsAction.rotate(input);
    }
    
    return new BTree<NumberedValue<Module>>(
        input.getValue(),
        input.hasLeft()
            ? RotatePoemsAction.use(
                input.getLeft(),
                pivot)
            : null,
        input.hasRight()
            ? RotatePoemsAction.use(
                input.getRight(),
                pivot)
            : null);
  }
  
  private static BTree<NumberedValue<Module>> rotate(final BTree<NumberedValue<Module>> input)
  {
    return new BTree<NumberedValue<Module>>(
        NumberedValue.create(
            input.getValue(),
            input.getValue().getValue().flip()),
        input.hasRight()
            ? RotatePoemsAction.rotate(input.getRight())
            : null,
        input.hasLeft()
            ? RotatePoemsAction.rotate(input.getLeft())
            : null);
  }
}
