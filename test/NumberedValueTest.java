import org.junit.Assert;
import org.junit.Test;

import btree.NumberedValue;
import btree.utility.Counter;

/**
 * Numbered value test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class NumberedValueTest
{
  /**
   * test
   */
  @Test
  public void testCreate()
  {
    final Counter counter = new Counter();
    
    final NumberedValue<Dummy> parent = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> child = NumberedValue.create(parent, new Dummy());
    
    Assert.assertEquals(child.getNumber(), parent.getNumber());
    Assert.assertEquals(child.getLastNumber(), parent.getLastNumber());
    
    Assert.assertTrue(child.hasNumber(0));
    Assert.assertTrue(parent.hasNumber(0));
    Assert.assertEquals(0, child.getLastNumber());
    Assert.assertEquals(0, parent.getLastNumber());
    
    for (int i = -5; i < 5; i++)
    {
      Assert.assertEquals(child.hasNumber(i), parent.hasNumber(i));
    }
  }
  
  /**
   * test
   */
  @Test
  public void testGetLastNumber()
  {
    final Counter counter = new Counter();
    
    final NumberedValue<Dummy> node0 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node1 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node2 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node3 = NumberedValue.create(counter, new Dummy());
    
    Assert.assertEquals(3, node0.getLastNumber());
    Assert.assertEquals(3, node1.getLastNumber());
    Assert.assertEquals(3, node2.getLastNumber());
    Assert.assertEquals(3, node3.getLastNumber());
  }
  
  /**
   * test
   */
  @Test
  public void testGetNumber()
  {
    final Counter counter = new Counter();
    
    final NumberedValue<Dummy> node0 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node1 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node2 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node3 = NumberedValue.create(counter, new Dummy());
    
    Assert.assertEquals(0, node0.getNumber());
    Assert.assertEquals(1, node1.getNumber());
    Assert.assertEquals(2, node2.getNumber());
    Assert.assertEquals(3, node3.getNumber());
  }
  
  /**
   * test
   */
  @Test
  public void testHasNumber()
  {
    final Counter counter = new Counter();
    
    final NumberedValue<Dummy> node0 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node1 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node2 = NumberedValue.create(counter, new Dummy());
    final NumberedValue<Dummy> node3 = NumberedValue.create(counter, new Dummy());
    
    Assert.assertTrue(node0.hasNumber(0));
    Assert.assertTrue(node1.hasNumber(1));
    Assert.assertTrue(node2.hasNumber(2));
    Assert.assertTrue(node3.hasNumber(3));
  }
  
  /**
   * test
   */
  @Test
  public void testGetValue()
  {
    final Dummy value = new Dummy();
    final NumberedValue<Dummy> node = NumberedValue.create(new Counter(), value);
    Assert.assertEquals(value, node.getValue());
  }
  
  /**
   * test
   */
  @Test
  public void testEqualsObject()
  {
    final NumberedValue<Dummy> node1 = NumberedValue.create(new Counter(), new Dummy());
    
    Assert.assertTrue(node1.equals(node1));
    Assert.assertFalse(node1.equals(null));
    Assert.assertFalse(node1.equals("funky"));
    Assert.assertFalse(node1.equals(132));
    
    final Dummy value = new Dummy();
    
    final NumberedValue<Dummy> node1a = NumberedValue.create(node1, value);
    final NumberedValue<Dummy> node1b = NumberedValue.create(node1, value);
    
    Assert.assertTrue(node1a.equals(node1b));
    Assert.assertTrue(node1b.equals(node1a));
    Assert.assertTrue(node1a.equals(node1));
    Assert.assertTrue(node1b.equals(node1));
  }
}
