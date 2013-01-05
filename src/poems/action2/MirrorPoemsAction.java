package poems.action2;

import module.Module;
import poems.AbstractPoemsAction;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Finder;

public class MirrorPoemsAction extends AbstractPoemsAction
{
  private final int number;
  private final boolean recursive;
  
  public static MirrorPoemsAction random(final boolean enabled, final int lastNumber)
  {
    return new MirrorPoemsAction(
        enabled,
        Utility.random(0, lastNumber),
        Utility.randomBoolean());
  }
  
  public static MirrorPoemsAction create(final boolean enabled, final int number, final boolean recursive)
  {
    return new MirrorPoemsAction(
        enabled,
        number,
        recursive);
  }
  
  @Override
  public AbstractPoemsAction randomize(final int lastNumber)
  {
    return new MirrorPoemsAction(
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
  
  private MirrorPoemsAction(final boolean enabled, final int number, final boolean recursive)
  {
    super(enabled);
    
    this.number = number;
    this.recursive = recursive;
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    final BTree<NumberedValue<Module>> n = Finder.find(input, this.number);
    
    return MirrorPoemsAction.swap(input, n, this.recursive, false);
  }
  
  private static BTree<NumberedValue<Module>> swap(final BTree<NumberedValue<Module>> input, final BTree<NumberedValue<Module>> n, final boolean recursive, final boolean wasSwapped)
  {
    if (input == null)
    {
      return null;
    }
    
    final boolean swapNow = (input == n);
    
    return new BTree<NumberedValue<Module>>(
        input.getValue(),
        MirrorPoemsAction.swap(
            swapNow || (recursive && wasSwapped)
                ? input.hasRight()
                    ? input.getRight()
                    : null
                : input.hasLeft()
                    ? input.getLeft()
                    : null,
            n,
            recursive,
            swapNow || wasSwapped),
        MirrorPoemsAction.swap(
            swapNow || (recursive && wasSwapped)
                ? input.hasLeft()
                    ? input.getLeft()
                    : null
                : input.hasRight()
                    ? input.getRight()
                    : null,
            n,
            recursive,
            swapNow || wasSwapped));
  }
  
  @Override
  public String toActiveString()
  {
    return String.format("mirror(%d,%s)", this.number, this.recursive);
  }
}
