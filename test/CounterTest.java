import org.junit.Assert;
import org.junit.Test;

import btree.utility.Counter;

/**
 * Counter test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class CounterTest
{
  /**
   * test
   */
  @Test
  public void test()
  {
    final Counter counter1 = new Counter();
    
    for (int i = 0; i < 5; i++)
    {
      Assert.assertEquals(i, counter1.next());
    }
    
    Assert.assertEquals(4, counter1.last());
    
    final Counter counter2 = new Counter();
    
    for (int i = 0; i < 1000; i++)
    {
      Assert.assertEquals(i, counter2.next());
    }
    
    Assert.assertEquals(999, counter2.last());
    
    for (int i = 0; i < 5; i++)
    {
      Assert.assertEquals(5 + i, counter1.next());
    }
    
    for (int i = 0; i < 1000; i++)
    {
      Assert.assertEquals(1000 + i, counter2.next());
    }
    
    Assert.assertEquals(1999, counter2.last());
  }
}
