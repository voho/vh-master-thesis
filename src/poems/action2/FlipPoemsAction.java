package poems.action2;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class FlipPoemsAction extends AbstractPoemsAction
{
  private final int number;
  private final boolean recursive;
  
  public static FlipPoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new FlipPoemsAction(
        enabled,
        Utility.random(0, lastNumber),
        Utility.randomBoolean());
  }
  
  public static FlipPoemsAction create(final boolean enabled, final int number, final boolean recursive)
  {
    return new FlipPoemsAction(
        enabled,
        number,
        recursive);
  }
  
  @Override
  public AbstractPoemsAction randomize(final int lastNumber)
  {
    return new FlipPoemsAction(
        Utility.randomBoolean()
            ? this.isEnabled()
            : Utility.randomBoolean(),
        Utility.randomIncrement(
            this.number,
            Utility.randomBoolean(
                Setup.PROB_ACTION_PARAM_CHANGE)
                ? lastNumber / 2
                : 0,
            lastNumber),
        Utility.randomBoolean()
            ? this.recursive
            : Utility.randomBoolean());
  }
  
  private FlipPoemsAction(final boolean enabled, final int number, final boolean recursive)
  {
    super(enabled);
    
    this.number = number;
    this.recursive = recursive;
  }
  
  @Override
  public String toActiveString()
  {
    return String.format("flip(%d,%s)", this.number, this.recursive);
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    final BTree<NumberedValue<Module>> n = Finder.find(input, this.number);
    
    return FlipPoemsAction.flip(input, n, this.recursive, false);
  }
  
  private static BTree<NumberedValue<Module>> flip(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> n, final boolean recursive, final boolean wasFlipped)
  {
    if (input == null)
    {
      return null;
    }
    
    final boolean flipNow = (input == n);
    
    return new BTree<NumberedValue<Module>>(
        (flipNow || (recursive && wasFlipped))
            ? NumberedValue.create(
                input.getValue(),
                input.getValue().getValue().flip())
            : NumberedValue.create(
                input.getValue(),
                input.getValue().getValue()),
        input.hasLeft()
            ? FlipPoemsAction.flip(
                input.getLeft(),
                n,
                recursive,
                flipNow || wasFlipped)
            : null,
        input.hasRight()
            ? FlipPoemsAction.flip(
                input.getRight(),
                n,
                recursive,
                flipNow || wasFlipped)
            : null);
  }
}
