package btree.utility;

/**
 * Counter that increments its value.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Counter
{
  /**
   * current counter value
   */
  private int value;
  
  /**
   * Creates a new counter starting from 0.
   */
  public Counter()
  {
    this.value = 0;
  }
  
  /**
   * Returns the current value and increments it.
   * 
   * @return current counter value
   */
  public int next()
  {
    return this.value++;
  }
  
  /**
   * Returns the last counter value.
   * 
   * @return the last counter value
   */
  public int last()
  {
    if (this.value == 0)
    {
      throw new IllegalStateException("Counter was not yet used.");
    }
    
    return this.value - 1;
  }
}
