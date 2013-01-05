import org.junit.Assert;
import org.junit.Test;

import program.Utility;

/**
 * Utility test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class UtilityTest
{
  /**
   * test
   */
  @Test
  public void testRandom()
  {
    for (int i = 0; i < 10000; i++)
    {
      final int rnd = Utility.random(100, 200);
      Assert.assertTrue(rnd >= 100);
      Assert.assertTrue(rnd <= 200);
    }
    
    for (int i = -100; i < 100; i++)
    {
      final int rnd = Utility.random(-100, 20);
      Assert.assertTrue(rnd >= -100);
      Assert.assertTrue(rnd <= 20);
    }
    
    for (int i = -10; i < 10; i++)
    {
      for (int j = 0; j < 1000; j++)
      {
        final int rnd = Utility.random(i, i);
        Assert.assertEquals(i, rnd);
      }
    }
  }
  
  /**
   * test
   */
  @Test
  public void testRandomIncrement()
  {
    for (int i = 0; i < 100; i++)
    {
      for (int j = 0; j < 100; j++)
      {
        final int rnd = Utility.randomIncrement(i, 100, i + 100);
        Assert.assertTrue(rnd >= 0);
        Assert.assertTrue(rnd <= i + 100);
      }
    }
  }
}
