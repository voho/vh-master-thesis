import module.Module;

import org.junit.Assert;
import org.junit.Test;

import btree.BTree;
import btree.utility.Evaluation;
import btree.utility.Evaluator;
import btree.utility.Numberator;
import btree.utility.Placer;

/**
 * Evaluator test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class EvaluatorTest
{
  /**
   * test
   */
  @Test
  public void testEvaluate1()
  {
    final BTree<Module> c = new BTree<Module>(new Module("C", 10, 70), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 10), null, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 20, 40), c, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 30, 10), g, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 30, 20), null, f);
    final BTree<Module> d = new BTree<Module>(new Module("D", 50, 10), null, e);
    final BTree<Module> a = new BTree<Module>(new Module("A", 30, 20), b, d);
    
    final Evaluation result = Evaluator.evaluate(Placer.placeNumbered(Numberator.number(a)));
    
    Assert.assertEquals(60, result.getWidth());
    Assert.assertEquals(80, result.getHeight());
    Assert.assertEquals(280, result.getPerimeter());
    Assert.assertEquals(60 * 80, result.getTotalArea());
    Assert.assertEquals(20 * 20 + 20 * 30, result.getUnusedArea());
    Assert.assertEquals(60 * 80 - 20 * 20 - 20 * 30, result.getUsedArea());
    Assert.assertEquals(0.2632, result.getRelativeUnusedArea(), 0.0001);
  }
  
  /**
   * test
   */
  @Test
  public void testEvaluate2()
  {
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final Evaluation result = Evaluator.evaluate(Placer.placeNumbered(Numberator.number(a)));
    
    Assert.assertEquals(90, result.getWidth());
    Assert.assertEquals(50, result.getHeight());
    Assert.assertEquals(280, result.getPerimeter());
    Assert.assertEquals(90 * 50, result.getTotalArea());
    Assert.assertEquals(0, result.getUnusedArea());
    Assert.assertEquals(90 * 50, result.getUsedArea());
    Assert.assertEquals(0.0, result.getRelativeUnusedArea(), 0.000001);
  }
  
  /**
   * test
   */
  @Test
  public void testEvaluate3()
  {
    final BTree<Module> d = new BTree<Module>(new Module("E", 1, 1), null, null);
    final BTree<Module> c = new BTree<Module>(new Module("F", 1, 1), d, null);
    final BTree<Module> b = new BTree<Module>(new Module("G", 1, 1), c, null);
    final BTree<Module> a = new BTree<Module>(new Module("D", 1, 1), b, null);
    
    final Evaluation result = Evaluator.evaluate(Placer.placeNumbered(Numberator.number(a)));
    
    Assert.assertEquals(4, result.getWidth());
    Assert.assertEquals(1, result.getHeight());
    Assert.assertEquals(10, result.getPerimeter());
    Assert.assertEquals(4, result.getTotalArea());
    Assert.assertEquals(0, result.getUnusedArea());
    Assert.assertEquals(4, result.getUsedArea());
    Assert.assertEquals(0.0, result.getRelativeUnusedArea(), 0.000001);
  }
  
  /**
   * test
   */
  @Test
  public void testEvaluate4()
  {
    final BTree<Module> d = new BTree<Module>(new Module("E", 1, 1), null, null);
    final BTree<Module> c = new BTree<Module>(new Module("F", 1, 1), null, d);
    final BTree<Module> b = new BTree<Module>(new Module("G", 1, 1), null, c);
    final BTree<Module> a = new BTree<Module>(new Module("D", 1, 1), null, b);
    
    final Evaluation result = Evaluator.evaluate(Placer.placeNumbered(Numberator.number(a)));
    
    Assert.assertEquals(1, result.getWidth());
    Assert.assertEquals(4, result.getHeight());
    Assert.assertEquals(10, result.getPerimeter());
    Assert.assertEquals(4, result.getTotalArea());
    Assert.assertEquals(0, result.getUnusedArea());
    Assert.assertEquals(4, result.getUsedArea());
    Assert.assertEquals(0.0, result.getRelativeUnusedArea(), 0.001);
  }
  
  /**
   * test
   */
  @Test
  public void testEquals1()
  {
    final BTree<Dummy> a = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> b = new BTree<Dummy>(new Dummy(), null, null);
    
    Assert.assertFalse(Evaluator.equals(a, b));
    Assert.assertFalse(Evaluator.equals(b, a));
    
    final Dummy value = new Dummy();
    
    final BTree<Dummy> c = new BTree<Dummy>(value, null, null);
    final BTree<Dummy> d = new BTree<Dummy>(value, null, null);
    
    Assert.assertTrue(Evaluator.equals(c, d));
    Assert.assertTrue(Evaluator.equals(d, c));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals1l()
  {
    final Dummy value = new Dummy();
    
    final BTree<Dummy> x = new BTree<Dummy>(value, null, null);
    final BTree<Dummy> y = new BTree<Dummy>(value, x, null);
    final BTree<Dummy> z = new BTree<Dummy>(value, null, null);
    
    Assert.assertFalse(Evaluator.equals(y, z));
    Assert.assertFalse(Evaluator.equals(z, y));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals1r()
  {
    final Dummy value = new Dummy();
    
    final BTree<Dummy> x = new BTree<Dummy>(value, null, null);
    final BTree<Dummy> y = new BTree<Dummy>(value, null, x);
    final BTree<Dummy> z = new BTree<Dummy>(value, null, null);
    
    Assert.assertFalse(Evaluator.equals(y, z));
    Assert.assertFalse(Evaluator.equals(z, y));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals1b()
  {
    final Dummy value = new Dummy();
    
    final BTree<Dummy> x = new BTree<Dummy>(value, null, null);
    final BTree<Dummy> y = new BTree<Dummy>(value, x, x);
    final BTree<Dummy> z = new BTree<Dummy>(value, null, null);
    
    Assert.assertFalse(Evaluator.equals(y, z));
    Assert.assertFalse(Evaluator.equals(z, y));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals11l()
  {
    final Dummy aval = new Dummy();
    final Dummy bval = new Dummy();
    final Dummy cval = new Dummy();
    final Dummy dval = new Dummy();
    
    final BTree<Dummy> c = new BTree<Dummy>(cval, null, null);
    final BTree<Dummy> b = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> a = new BTree<Dummy>(aval, b, c);
    
    final BTree<Dummy> cc = new BTree<Dummy>(cval, null, null);
    final BTree<Dummy> bb = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> aa = new BTree<Dummy>(aval, bb, cc);
    
    Assert.assertFalse(Evaluator.equals(a, aa));
    Assert.assertFalse(Evaluator.equals(aa, a));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals11r()
  {
    final Dummy aval = new Dummy();
    final Dummy bval = new Dummy();
    final Dummy cval = new Dummy();
    final Dummy dval = new Dummy();
    
    final BTree<Dummy> c = new BTree<Dummy>(cval, null, null);
    final BTree<Dummy> b = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> a = new BTree<Dummy>(aval, b, c);
    
    final BTree<Dummy> cc = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> bb = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> aa = new BTree<Dummy>(aval, bb, cc);
    
    Assert.assertFalse(Evaluator.equals(a, aa));
    Assert.assertFalse(Evaluator.equals(aa, a));
  }
  
  /**
   * test
   */
  @Test
  public void testEquals2()
  {
    final Dummy aval = new Dummy();
    final Dummy bval = new Dummy();
    final Dummy cval = new Dummy();
    final Dummy dval = new Dummy();
    final Dummy eval = new Dummy();
    
    final BTree<Dummy> e = new BTree<Dummy>(eval, null, null);
    final BTree<Dummy> d = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> c = new BTree<Dummy>(cval, d, e);
    final BTree<Dummy> b = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> a = new BTree<Dummy>(aval, b, c);
    
    final BTree<Dummy> ee = new BTree<Dummy>(eval, null, null);
    final BTree<Dummy> dd = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> cc = new BTree<Dummy>(cval, dd, ee);
    final BTree<Dummy> bb = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> aa = new BTree<Dummy>(aval, bb, cc);
    
    final BTree<Dummy> eee = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> ddd = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> ccc = new BTree<Dummy>(new Dummy(), ddd, eee);
    final BTree<Dummy> bbb = new BTree<Dummy>(new Dummy(), null, null);
    final BTree<Dummy> aaa = new BTree<Dummy>(new Dummy(), bbb, ccc);
    
    Assert.assertTrue(Evaluator.equals(aa, a));
    Assert.assertTrue(Evaluator.equals(bb, b));
    Assert.assertTrue(Evaluator.equals(cc, c));
    Assert.assertTrue(Evaluator.equals(dd, d));
    Assert.assertTrue(Evaluator.equals(ee, e));
    
    final BTree<?>[] box1 = new BTree<?>[5];
    final BTree<?>[] box2 = new BTree<?>[5];
    final BTree<?>[] box3 = new BTree<?>[5];
    
    box1[0] = a;
    box1[1] = b;
    box1[2] = c;
    box1[3] = d;
    box1[4] = e;
    
    box2[0] = aa;
    box2[1] = bb;
    box2[2] = cc;
    box2[3] = dd;
    box2[4] = ee;
    
    box3[0] = aaa;
    box3[1] = bbb;
    box3[2] = ccc;
    box3[3] = ddd;
    box3[4] = eee;
    
    for (int i = 0; i < box1.length; i++)
    {
      for (int j = 0; j < box2.length; j++)
      {
        if (i == j)
        {
          Assert.assertTrue(Evaluator.equals(box1[i], box2[j]));
          Assert.assertTrue(Evaluator.equals(box2[j], box1[i]));
        }
        else
        {
          Assert.assertFalse(Evaluator.equals(box1[i], box2[j]));
          Assert.assertFalse(Evaluator.equals(box2[j], box1[i]));
        }
      }
    }
    
    for (final BTree<?> element : box1)
    {
      for (final BTree<?> element2 : box3)
      {
        Assert.assertFalse(Evaluator.equals(element, element2));
        Assert.assertFalse(Evaluator.equals(element2, element));
      }
    }
  }
  
  /**
   * test
   */
  @Test
  public void testEquals3()
  {
    final Dummy aval = new Dummy();
    final Dummy bval = new Dummy();
    final Dummy cval = new Dummy();
    final Dummy dval = new Dummy();
    final Dummy e1val = new Dummy();
    final Dummy e2val = new Dummy();
    
    final BTree<Dummy> e = new BTree<Dummy>(e1val, null, null);
    final BTree<Dummy> d = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> c = new BTree<Dummy>(cval, d, e);
    final BTree<Dummy> b = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> a = new BTree<Dummy>(aval, b, c);
    
    final BTree<Dummy> ee = new BTree<Dummy>(e2val, null, null);
    final BTree<Dummy> dd = new BTree<Dummy>(dval, null, null);
    final BTree<Dummy> cc = new BTree<Dummy>(cval, dd, ee);
    final BTree<Dummy> bb = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> aa = new BTree<Dummy>(aval, bb, cc);
    
    Assert.assertFalse(Evaluator.equals(a, aa));
    Assert.assertFalse(Evaluator.equals(aa, a));
    
    final BTree<Dummy> ccc = new BTree<Dummy>(cval, null, null);
    final BTree<Dummy> bbb = new BTree<Dummy>(bval, null, null);
    final BTree<Dummy> aaa = new BTree<Dummy>(aval, bbb, ccc);
    
    Assert.assertFalse(Evaluator.equals(a, aaa));
    Assert.assertFalse(Evaluator.equals(aaa, a));
    Assert.assertFalse(Evaluator.equals(aa, aaa));
    Assert.assertFalse(Evaluator.equals(aaa, aa));
  }
}
