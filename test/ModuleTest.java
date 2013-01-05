import module.Module;

import org.junit.Assert;
import org.junit.Test;

/**
 * Module test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class ModuleTest
{
  /**
   * test
   */
  @Test
  public void testFlip()
  {
    final Module m = new Module("hello", 10, 100);
    
    Assert.assertEquals(10, m.getWidth());
    Assert.assertEquals(100, m.getHeight());
    
    final Module n = m.flip();
    
    Assert.assertEquals(100, n.getWidth());
    Assert.assertEquals(10, n.getHeight());
    
    final Module o = n.flip();
    
    Assert.assertEquals(10, o.getWidth());
    Assert.assertEquals(100, o.getHeight());
  }
  
  /**
   * test
   */
  @Test
  public void testGetName()
  {
    final Module m = new Module("hello", 10, 100);
    
    Assert.assertEquals("hello", m.getName());
  }
  
  /**
   * test
   */
  @Test
  public void testIsFlipped()
  {
    final Module m = new Module("hello", 10, 100);
    final Module n = m.flip();
    final Module o = n.flip();
    
    Assert.assertFalse(m.isFlipped());
    Assert.assertTrue(n.isFlipped());
    Assert.assertFalse(o.isFlipped());
  }
  
  /**
   * test
   */
  @Test
  public void testEquals1()
  {
    final Module m = new Module("a", 10, 20);
    final Module n = new Module("a", 20, 10);
    final Module o = new Module("b", 20, 10);
    
    Assert.assertEquals(m, m);
    Assert.assertEquals(n, n);
    Assert.assertEquals(o, o);
    Assert.assertEquals(m, n);
    Assert.assertEquals(n, m);
    Assert.assertFalse(m.equals(o));
    Assert.assertFalse(o.equals(m));
    Assert.assertFalse(n.equals(o));
    Assert.assertFalse(o.equals(n));
    
    Assert.assertFalse(m.equals(null));
    Assert.assertFalse(n.equals(null));
    Assert.assertFalse(o.equals(null));
    
    Assert.assertFalse(m.equals(new Dummy()));
    Assert.assertFalse(n.equals(new Dummy()));
    Assert.assertFalse(o.equals(new Dummy()));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals2()
  {
    final Module a1 = new Module("a", 10, 20, false);
    final Module a2 = new Module("a", 10, 20, true);
    final Module a3 = a1.flip();
    final Module a4 = a3.flip();
    
    Assert.assertTrue(a1.equals(a1));
    Assert.assertTrue(a2.equals(a2));
    Assert.assertTrue(a3.equals(a3));
    Assert.assertFalse(a1.equals(a2));
    Assert.assertFalse(a2.equals(a1));
    Assert.assertFalse(a1.equals(a3));
    Assert.assertFalse(a3.equals(a1));
    Assert.assertTrue(a2.equals(a3));
    Assert.assertTrue(a3.equals(a2));
    Assert.assertTrue(a1.equals(a4));
    Assert.assertTrue(a4.equals(a1));
    Assert.assertFalse(a1.equals(a3));
    Assert.assertFalse(a1.equals(a3));
    Assert.assertFalse(a3.equals(a4));
    Assert.assertFalse(a4.equals(a3));
  }
}
