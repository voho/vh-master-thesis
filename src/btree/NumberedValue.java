package btree;

import btree.utility.Counter;

/**
 * Numbered B*-Tree value.
 * 
 * @author Vojtěch Hordějčuk
 * @param <V> internal value class
 */
public class NumberedValue<V> implements BTreeValue
{
  /**
   * counter instance
   */
  private final Counter counter;
  /**
   * node number
   */
  private final int number;
  /**
   * node internal value
   */
  private final V value;
  
  /**
   * Creates a new instance.
   * 
   * @param <V> value class
   * @param parent parent value
   * @param value value
   * @return new numbered value
   */
  public static <V> NumberedValue<V> create(final NumberedValue<V> parent, final V value)
  {
    return new NumberedValue<V>(parent.counter, parent.number, value);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param <V> value class
   * @param counter counter
   * @param value value
   * @return new numbered value
   */
  public static <V> NumberedValue<V> create(final Counter counter, final V value)
  {
    return new NumberedValue<V>(counter, counter.next(), value);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param counter counter
   * @param number current number
   * @param value value
   */
  private NumberedValue(final Counter counter, final int number, final V value)
  {
    if (value == null)
    {
      throw new NullPointerException("Value cannot be NULL.");
    }
    
    this.counter = counter;
    this.number = number;
    this.value = value;
  }
  
  /**
   * Returns the number.
   * 
   * @return the number
   */
  public int getNumber()
  {
    return this.number;
  }
  
  /**
   * Returns the last number.
   * 
   * @return the last number
   */
  public int getLastNumber()
  {
    return this.counter.last();
  }
  
  /**
   * Checks if the value is numbered by the given number.
   * 
   * @param n number
   * @return TRUE if the value is numbered by the given number
   */
  public boolean hasNumber(final int n)
  {
    return this.number == n;
  }
  
  /**
   * Returns the value.
   * 
   * @return the value
   */
  public V getValue()
  {
    return this.value;
  }
  
  @Override
  public String toString()
  {
    return String.format("%d/%d|%s", this.number, this.counter.last(), this.value.toString());
  }
  
  @Override
  public int hashCode()
  {
    return super.hashCode();
  }
  
  @Override
  public boolean equals(final Object that)
  {
    if (this == that)
    {
      return true;
    }
    
    if (that == null)
    {
      return false;
    }
    
    if (!(that instanceof NumberedValue<?>))
    {
      return false;
    }
    
    final NumberedValue<?> other = (NumberedValue<?>) that;
    
    if (other.number != this.number)
    {
      return false;
    }
    
    return true;
  }
}
