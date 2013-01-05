package poems;

import java.util.Arrays;

import module.Module;
import program.Logger;
import program.Setup;
import program.Utility;
import btree.BTree;
import btree.NumberedValue;

/**
 * POEMS action sequence.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PoemsActionSequence extends AbstractPoemsAction
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(PoemsActionSequence.class);
  /**
   * array of actions
   */
  private final AbstractPoemsAction[] actions;
  /**
   * niche (number of active actions)
   */
  private final int niche;
  
  /**
   * Creates a random sequence.
   * 
   * @param lastNumber the last number
   * @param length sequence length
   * @param niche sequence niche
   * @return a random action sequence
   */
  public static PoemsActionSequence random(final int lastNumber, final int length, final int niche)
  {
    PoemsActionSequence.LOG.log("Creating a random sequence of size %d in niche %d...", length, niche);
    
    // create the action array
    
    final AbstractPoemsAction[] newActions = new AbstractPoemsAction[length];
    
    for (int i = 0; i < length; i++)
    {
      newActions[i] = AbstractPoemsAction.randomAction(
          lastNumber,
          i < niche);
    }
    
    // create a sequence from the field
    
    return new PoemsActionSequence(newActions);
  }
  
  /**
   * Creates a new sequence by crossover.
   * 
   * @param mother first parent
   * @param father second parent
   * @return a new sequence based on the two parents provided
   */
  public static PoemsActionSequencePair crossover(final PoemsActionSequence mother, final PoemsActionSequence father)
  {
    if (mother == father)
    {
      PoemsActionSequence.LOG.log("Crossover mother and father are the same.");
      
      return new PoemsActionSequencePair(mother, father);
    }
    
    PoemsActionSequence.LOG.log("Creating a sequence by crossover...");
    PoemsActionSequence.LOG.log("Mother: %s", mother);
    PoemsActionSequence.LOG.log("Father: %s", father);
    
    // create the action array
    
    final AbstractPoemsAction[] newActions1 = new AbstractPoemsAction[mother.actions.length];
    final AbstractPoemsAction[] newActions2 = new AbstractPoemsAction[mother.actions.length];
    
    for (int i = 0; i < mother.actions.length; i++)
    {
      if (Utility.randomBoolean())
      {
        newActions1[i] = mother.actions[i];
        newActions2[i] = father.actions[i];
      }
      else
      {
        newActions1[i] = father.actions[i];
        newActions2[i] = mother.actions[i];
      }
    }
    
    // create a sequence from the field
    
    return new PoemsActionSequencePair(
        new PoemsActionSequence(newActions1),
        new PoemsActionSequence(newActions2));
  }
  
  /**
   * Creates a new sequence by mutation.
   * 
   * @param lastNumber the last number
   * @param parent original sequence
   * @return a new sequence based on the parent provided
   */
  public static PoemsActionSequence mutate(final int lastNumber, final PoemsActionSequence parent)
  {
    PoemsActionSequence.LOG.log("Creating a sequence by mutation...");
    PoemsActionSequence.LOG.log("Parent: %s", parent);
    
    if (Math.random() < Setup.PROB_BIG_ACTION_CHANGE)
    {
      // mutate actions
      
      return PoemsActionSequence.mutateType(parent, lastNumber);
    }
    
    // mutate action order
    
    return PoemsActionSequence.mutateOrder(parent);
  }
  
  /**
   * Changes the order of actions in the sequence.
   * 
   * @param parent parent sequence
   * @return new sequence with altered action order
   */
  private static PoemsActionSequence mutateOrder(final PoemsActionSequence parent)
  {
    PoemsActionSequence.LOG.log("Mutating sequence order...");
    
    // shuffle actions
    
    final AbstractPoemsAction[] newActions = Arrays.copyOf(parent.actions, parent.actions.length);
    Utility.shuffleArray(newActions);
    return new PoemsActionSequence(newActions);
  }
  
  /**
   * Changes the type and parameters of actions in the sequence.
   * 
   * @param parent parent sequence
   * @param lastNumber the last number
   * @return new sequence with altered action type and parameters
   */
  private static PoemsActionSequence mutateType(final PoemsActionSequence parent, final int lastNumber)
  {
    PoemsActionSequence.LOG.log("Mutating sequence action types...");
    
    // create the action array
    
    final AbstractPoemsAction[] newActions = new AbstractPoemsAction[parent.actions.length];
    
    for (int i = 0; i < newActions.length; i++)
    {
      newActions[i] = Utility.randomBoolean(Setup.PROB_ACTION_CHANGE)
          ? parent.actions[i].randomize(lastNumber)
          : parent.actions[i];
    }
    
    // create a sequence from the field
    
    return new PoemsActionSequence(newActions);
  }
  
  @Override
  public AbstractPoemsAction randomize(final int lastNumber)
  {
    return PoemsActionSequence.mutate(lastNumber, this);
  }
  
  /**
   * Creates a new action sequence with defined actions.
   * 
   * @param actions input actions to put into the sequence
   * @return action sequence
   */
  public static PoemsActionSequence create(final AbstractPoemsAction[] actions)
  {
    return new PoemsActionSequence(actions);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param newActions array of actions (will be used and modified!)
   */
  private PoemsActionSequence(final AbstractPoemsAction[] newActions)
  {
    super(true);
    
    PoemsActionSequence.LOG.log("Creating a new sequence: %s", newActions.toString());
    
    // save the action array
    
    this.actions = newActions;
    
    int counter = 0;
    
    for (final AbstractPoemsAction action : this.actions)
    {
      counter += action.getNiche();
    }
    
    this.niche = counter;
  }
  
  /**
   * Returns the actions in the sequence.
   * 
   * @return array of actions in the sequence
   */
  public AbstractPoemsAction[] getActions()
  {
    return this.actions;
  }
  
  @Override
  public int getNiche()
  {
    return this.niche;
  }
  
  /**
   * Returns the action sequence length.
   * 
   * @return the sequence length
   */
  public int getLength()
  {
    return this.actions.length;
  }
  
  @Override
  protected String toActiveString()
  {
    return "N" + this.niche + ": " + Arrays.toString(this.actions);
  }
  
  @Override
  public String toString()
  {
    return this.toActiveString();
  }
  
  @Override
  public BTree<NumberedValue<Module>> applyActive(final BTree<NumberedValue<Module>> input)
  {
    BTree<NumberedValue<Module>> temp = input;
    
    for (final AbstractPoemsAction action : this.actions)
    {
      final int sizeBefore = temp.size();
      temp = action.apply(temp);
      final int sizeAfter = temp.size();
      
      if (sizeBefore != sizeAfter)
      {
        throw new IllegalStateException(String.format("Invalid tree size (was %d, is %s).", sizeBefore, sizeAfter));
      }
    }
    
    return temp;
  }
}
