package program;

import java.util.Arrays;

/**
 * Utility class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Utility
{
  /**
   * logger instance
   */
  private static Logger LOG = Logger.create(Utility.class);
  
  /**
   * Creation is not allowed.
   */
  private Utility()
  {
    // nothing
  }
  
  /**
   * Returns a random number in the range specified (bounds included).
   * 
   * @param min minimum allowed (included)
   * @param max maximum allowed (included)
   * @return a random number within the range specified
   */
  public static int random(final int min, final int max)
  {
    if (min == max)
    {
      return min;
    }
    
    if (max < min)
    {
      throw new IllegalArgumentException("Maximum must be greater than or equal to minimum.");
    }
    
    return min + (int) (Math.random() * ((max - min) + 1));
  }
  
  /**
   * Increments a given value by a random increment. The resulting value never
   * exceeds the maximum value specified.
   * 
   * @param value value to increment
   * @param range range (0 to range will be added to the value)
   * @param max value maximum
   * @return value incremented by a random increment
   */
  public static int randomIncrement(final int value, final int range, final int max)
  {
    return Utility.increment(value, Utility.random(0, range), max);
  }
  
  /**
   * Increments a given value. If the incremented value grows over the maximum,
   * it is first read modulo maximum and then returned.
   * 
   * @param value value to increment
   * @param increment increment to be added to the value
   * @param max value maximum
   * @return incremented value
   */
  public static int increment(final int value, final int increment, final int max)
  {
    if (increment == 0)
    {
      return value;
    }
    
    if (value > max)
    {
      throw new IllegalArgumentException("Value must be less or equal to maximum.");
    }
    
    if (value < 0)
    {
      throw new IllegalArgumentException("Value must be greater or equal zero.");
    }
    
    if (max == 0)
    {
      return 0;
    }
    
    return (value + increment) % max;
  }
  
  /**
   * Shuffles the array randomly.
   * 
   * @param array array to be shuffled
   */
  public static void shuffleArray(final Object[] array)
  {
    Utility.shuffleArray(array, Utility.random(1, array.length));
  }
  
  /**
   * Shuffles the array randomly with the number of shakes specified.
   * 
   * @param array array to be shuffled
   * @param shakes number of shakes
   */
  private static void shuffleArray(final Object[] array, final int shakes)
  {
    Utility.LOG.log("Shuffling array: " + Arrays.toString(array));
    
    int remains = 0;
    
    if (array.length == 2)
    {
      if (Utility.randomBoolean())
      {
        remains = 1;
      }
    }
    else
    {
      remains = shakes;
    }
    
    while (remains > 0)
    {
      // generate two random indices
      
      final int ri1 = Utility.random(0, array.length - 1);
      final int ri2 = Utility.random(0, array.length - 1);
      
      if (ri1 != ri2)
      {
        // random indices are different, swap and decrement remaining shakes
        
        final Object temp = array[ri1];
        array[ri1] = array[ri2];
        array[ri2] = temp;
        remains--;
      }
    }
    
    Utility.LOG.log("Shuffled array: " + Arrays.toString(array));
  }
  
  /**
   * Returns a random boolean.
   * 
   * @return random boolean
   */
  public static boolean randomBoolean()
  {
    return Utility.randomBoolean(0.5);
  }
  
  /**
   * Returns a random boolean with weighted probability of TRUE.
   * 
   * @param trueProbability probability of true probability
   * @return random weighted boolean
   */
  public static boolean randomBoolean(final double trueProbability)
  {
    return (Math.random() < trueProbability);
  }
}
