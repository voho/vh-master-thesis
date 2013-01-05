import org.junit.Assert;
import org.junit.Test;

import btree.utility.PlacerCache;

/**
 * Placer cache test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PlacerCacheTest
{
  /**
   * test
   */
  @Test
  public void testFindY1()
  {
    final PlacerCache unit = new PlacerCache();
    
    Assert.assertEquals(0, unit.findY(20, 20, 10));
    Assert.assertEquals(0, unit.findY(0, 20, 20));
    Assert.assertEquals(0, unit.findY(40, 20, 20));
    Assert.assertEquals(20, unit.findY(10, 40, 20));
    Assert.assertEquals(20, unit.findY(50, 10, 20));
    Assert.assertEquals(20, unit.findY(0, 10, 20));
    Assert.assertEquals(40, unit.findY(0, 10, 10));
    Assert.assertEquals(40, unit.findY(10, 10, 10));
    Assert.assertEquals(40, unit.findY(20, 10, 10));
    Assert.assertEquals(40, unit.findY(30, 10, 10));
    Assert.assertEquals(40, unit.findY(40, 10, 10));
    Assert.assertEquals(40, unit.findY(50, 10, 10));
    Assert.assertEquals(0, unit.findY(60, 10, 10));
  }
  
  /**
   * test
   */
  @Test
  public void testFindY2()
  {
    final PlacerCache unit = new PlacerCache();
    
    Assert.assertEquals(0, unit.findY(3, 1, 5));
    Assert.assertEquals(0, unit.findY(6, 1, 5));
    Assert.assertEquals(0, unit.findY(4, 2, 2));
    Assert.assertEquals(5, unit.findY(2, 2, 1));
    Assert.assertEquals(5, unit.findY(6, 2, 1));
    Assert.assertEquals(2, unit.findY(4, 2, 2));
    Assert.assertEquals(6, unit.findY(0, 3, 1));
    Assert.assertEquals(7, unit.findY(1, 5, 1));
  }
}
