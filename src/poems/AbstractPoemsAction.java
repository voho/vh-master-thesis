package poems;

import module.Module;
import poems.action1.RotatePoemsAction;
import poems.action2.ExchangeNodePoemsAction;
import poems.action2.ExchangeValuePoemsAction;
import poems.action2.FlipPoemsAction;
import poems.action2.MirrorPoemsAction;
import poems.action3.HangNodePoemsAction;
import program.Logger;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;

/**
 * Abstract POEMS action.
 * 
 * @author Vojtěch Hordějčuk
 */
abstract public class AbstractPoemsAction
{
  /**
   * logger
   */
  private static final Logger LOG = Logger.create(AbstractPoemsAction.class);
  /**
   * active action flag
   */
  private final boolean enabled;
  
  /**
   * Creates a new instance.
   * 
   * @param enabled enabled flag
   */
  protected AbstractPoemsAction(final boolean enabled)
  {
    this.enabled = enabled;
  }
  
  /**
   * Returns the niche. Niche is defined as a number of active actions (not-NOP
   * actions) in individual. For single actions returns 0 or 1, for sequence
   * returns a number of active actions.
   * 
   * @return niche (number of active actions)
   */
  public int getNiche()
  {
    return this.enabled
        ? 1
        : 0;
  }
  
  /**
   * Returns the enabled flag.
   * 
   * @return the enabled flag
   */
  public boolean isEnabled()
  {
    return this.enabled;
  }
  
  /**
   * Applies the action to input and produces the output.
   * 
   * @param input input tree
   * @return output tree
   */
  final public BTree<NumberedValue<Module>> apply(final BTree<NumberedValue<Module>> input)
  {
    if (this.enabled)
    {
      return this.applyActive(input);
    }
    
    return input;
  }
  
  /**
   * Applies the action to input and produces the output.
   * 
   * @param input input tree
   * @return output tree
   */
  abstract protected BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input);
  
  /**
   * Creates a random action based on this action.
   * 
   * @param lastNumber the last number
   * @return a random action based on this action
   */
  abstract protected AbstractPoemsAction randomize(int lastNumber);
  
  /**
   * Returns the string representation of the action when active form.
   * 
   * @return the string
   */
  abstract protected String toActiveString();
  
  @Override
  public String toString()
  {
    if (this.enabled)
    {
      return this.toActiveString();
    }
    
    return "nop";
  }
  
  /**
   * Creates a random action. Can return NOP action.
   * 
   * @param lastNumber the last number
   * @param enabled enable the action created
   * @return random action
   */
  public static AbstractPoemsAction randomAction(final int lastNumber, final boolean enabled)
  {
    final AbstractPoemsAction action;
    
    switch (Utility.random(0, 5))
    {
    case 0:
      action = FlipPoemsAction.random(enabled, lastNumber);
      break;
    case 1:
      action = RotatePoemsAction.random(enabled, lastNumber);
      break;
    case 2:
      action = ExchangeNodePoemsAction.random(enabled, lastNumber);
      break;
    case 3:
      action = ExchangeValuePoemsAction.random(enabled, lastNumber);
      break;
    case 4:
      action = MirrorPoemsAction.random(enabled, lastNumber);
      break;
    case 5:
      action = HangNodePoemsAction.random(enabled, lastNumber);
      break;
    default:
      throw new IndexOutOfBoundsException();
    }
    
    AbstractPoemsAction.LOG.created(action);
    
    return action;
  }
}
