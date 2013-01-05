import module.Rectangle;

import org.junit.Assert;
import org.junit.Test;

/**
 * Rectangle test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class RectangleTest
{
  /**
   * test
   */
  @Test
  public void testGetWidth()
  {
    final Rectangle r = new Rectangle(100, 50);
    final Rectangle s = new Rectangle(50, 100);
    
    Assert.assertEquals(100, r.getWidth());
    Assert.assertEquals(50, s.getWidth());
  }
  
  /**
   * test
   */
  @Test
  public void testGetHeight()
  {
    final Rectangle r = new Rectangle(100, 50);
    final Rectangle s = new Rectangle(50, 100);
    
    Assert.assertEquals(50, r.getHeight());
    Assert.assertEquals(100, s.getHeight());
  }
  
  /**
   * test
   */
  @Test
  public void testGetArea()
  {
    final Rectangle r = new Rectangle(100, 50);
    final Rectangle s = new Rectangle(50, 100);
    
    Assert.assertEquals(5000, r.getArea());
    Assert.assertEquals(5000, s.getArea());
  }
  
  /**
   * test
   */
  @Test
  public void testGetPerimeter()
  {
    final Rectangle r = new Rectangle(100, 50);
    final Rectangle s = new Rectangle(20, 20);
    
    Assert.assertEquals(300, r.getPerimeter());
    Assert.assertEquals(80, s.getPerimeter());
  }
}
