import module.Position;

import org.junit.Assert;
import org.junit.Test;

/**
 * Position test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PositionTest
{
  /**
   * test
   */
  @Test
  public void testGetXY()
  {
    final Position p = new Position(10, 20);
    
    Assert.assertEquals(10, p.getX());
    Assert.assertEquals(20, p.getY());
  }
}
