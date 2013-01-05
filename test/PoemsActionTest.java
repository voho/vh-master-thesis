import module.Module;

import org.junit.Assert;
import org.junit.Test;

import poems.AbstractPoemsAction;
import poems.PoemsActionSequence;
import poems.action1.RotatePoemsAction;
import poems.action2.ExchangeNodePoemsAction;
import poems.action2.ExchangeValuePoemsAction;
import poems.action2.FlipPoemsAction;
import poems.action2.MirrorPoemsAction;
import poems.action3.HangNodePoemsAction;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Evaluator;
import btree.utility.Finder;
import btree.utility.Numberator;

/**
 * Poems action test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PoemsActionTest
{
  /**
   * test
   */
  @Test
  public void testRandom()
  {
    for (int i = 0; i < 100; i++)
    {
      final AbstractPoemsAction action = AbstractPoemsAction.randomAction(100, false);
      Assert.assertEquals(0, action.getNiche());
    }
  }
  
  /**
   * test
   */
  @Test
  public void testGetNiche1()
  {
    for (int i = 0; i < 100; i++)
    {
      final AbstractPoemsAction a1 = ExchangeNodePoemsAction.random(true, 0);
      final AbstractPoemsAction a2 = ExchangeValuePoemsAction.random(true, 0);
      final AbstractPoemsAction a3 = FlipPoemsAction.random(true, 0);
      final AbstractPoemsAction a4 = RotatePoemsAction.random(true, 0);
      final AbstractPoemsAction a5 = MirrorPoemsAction.random(true, 0);
      final AbstractPoemsAction a6 = HangNodePoemsAction.random(true, 0);
      
      Assert.assertTrue(1 == a1.getNiche());
      Assert.assertTrue(1 == a2.getNiche());
      Assert.assertTrue(1 == a3.getNiche());
      Assert.assertTrue(1 == a4.getNiche());
      Assert.assertTrue(1 == a5.getNiche());
      Assert.assertTrue(1 == a6.getNiche());
    }
    
    final AbstractPoemsAction b1 = ExchangeNodePoemsAction.create(true, 2, 1, 2);
    final AbstractPoemsAction b2 = ExchangeValuePoemsAction.create(true, 1, 2, 2);
    final AbstractPoemsAction b3 = FlipPoemsAction.create(true, 1, false);
    final AbstractPoemsAction b4 = RotatePoemsAction.create(true, 1);
    final AbstractPoemsAction b5 = MirrorPoemsAction.create(true, 1, true);
    final AbstractPoemsAction b6 = HangNodePoemsAction.create(true, true, 1, 2, 2);
    
    Assert.assertEquals(1, b1.getNiche());
    Assert.assertEquals(1, b2.getNiche());
    Assert.assertEquals(1, b3.getNiche());
    Assert.assertEquals(1, b4.getNiche());
    Assert.assertEquals(1, b5.getNiche());
    Assert.assertEquals(1, b6.getNiche());
  }
  
  /**
   * test
   */
  @Test
  public void testGetNiche2()
  {
    final AbstractPoemsAction a1 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    final AbstractPoemsAction a2 = ExchangeValuePoemsAction.create(true, 3, 2, 3);
    final AbstractPoemsAction a3 = FlipPoemsAction.create(true, 3, false);
    
    final PoemsActionSequence s1 = PoemsActionSequence.create(
        new AbstractPoemsAction[]
        { a1, a2, a3 });
    
    Assert.assertEquals(3, s1.getNiche());
    
    final AbstractPoemsAction b1 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    final AbstractPoemsAction b2 = FlipPoemsAction.create(true, 3, false);
    final AbstractPoemsAction b3 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    
    final PoemsActionSequence s2 = PoemsActionSequence.create(
        new AbstractPoemsAction[]
        { b1, b2, b3 });
    
    Assert.assertEquals(3, s2.getNiche());
    
    final AbstractPoemsAction c1 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    final AbstractPoemsAction c2 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    final AbstractPoemsAction c3 = ExchangeNodePoemsAction.create(true, 3, 0, 1);
    
    final PoemsActionSequence s3 = PoemsActionSequence.create(
        new AbstractPoemsAction[]
        { c1, c2, c3 });
    
    Assert.assertEquals(3, s3.getNiche());
    
    final AbstractPoemsAction d1 = ExchangeNodePoemsAction.create(true, 3, 2, 2);
    final AbstractPoemsAction d2 = ExchangeValuePoemsAction.create(true, 3, 1, 1);
    final AbstractPoemsAction d3 = HangNodePoemsAction.create(true, false, 3, 2, 3);
    final AbstractPoemsAction d4 = HangNodePoemsAction.create(true, true, 3, 1, 3);
    
    final PoemsActionSequence s4 = PoemsActionSequence.create(
        new AbstractPoemsAction[]
        { d1, d2, d3, d4 });
    
    Assert.assertEquals(4, s4.getNiche());
  }
  
  /**
   * test
   */
  @Test
  public void testNopAction()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    
    for (int i = 0; i < 1000; i++)
    {
      final BTree<NumberedValue<Module>> output = AbstractPoemsAction.randomAction(6, false).apply(input);
      
      Assert.assertEquals(input, output);
      
      TestUtility.testNumberedTree(output, e, g, d, b, f, c, a);
    }
  }
  
  /**
   * test
   */
  @Test
  public void testFlipAction()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> dflip = new BTree<Module>(d.getValue().flip(), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output1 = FlipPoemsAction.create(true, 3, false).apply(input);
    final BTree<NumberedValue<Module>> output2 = FlipPoemsAction.create(true, 3, false).apply(output1);
    
    TestUtility.testNumberedTree(input, e, g, d, b, f, c, a);
    Assert.assertFalse(Finder.find(input, 3).getValue().getValue().isFlipped());
    
    TestUtility.testNumberedTree(output1, e, g, dflip, b, f, c, a);
    Assert.assertTrue(Finder.find(output1, 3).getValue().getValue().isFlipped());
    
    TestUtility.testNumberedTree(output2, e, g, d, b, f, c, a);
    Assert.assertFalse(Finder.find(output2, 3).getValue().getValue().isFlipped());
  }
  
  /**
   * test
   */
  @Test
  public void testExchangeValueAction()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d1 = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> d2 = new BTree<Module>(new Module("D", 20, 20), e, d1);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), d2, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output1 = ExchangeValuePoemsAction.create(true, 1, 3, 3).apply(input);
    final BTree<NumberedValue<Module>> output2 = ExchangeValuePoemsAction.create(true, 1, 3, 3).apply(output1);
    
    TestUtility.testNumberedTree(input, e, g, d1, d2, f, c, a);
    TestUtility.testNumberedTree(output1, e, g, d2, d1, f, c, a);
    TestUtility.testNumberedTree(output2, e, g, d1, d2, f, c, a);
    
    Assert.assertEquals(d2.getValue(), Finder.find(input, 1).getValue().getValue());
    Assert.assertEquals(d1.getValue(), Finder.find(input, 3).getValue().getValue());
    
    Assert.assertEquals(d2.getValue(), Finder.find(output1, 1).getValue().getValue());
    Assert.assertEquals(d1.getValue(), Finder.find(output1, 3).getValue().getValue());
    
    Assert.assertEquals(d2.getValue(), Finder.find(output2, 1).getValue().getValue());
    Assert.assertEquals(d1.getValue(), Finder.find(output2, 3).getValue().getValue());
  }
  
  /**
   * test
   */
  @Test
  public void testSwapActionRoot()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output = MirrorPoemsAction.create(true, 0, false).apply(input);
    final BTree<NumberedValue<Module>> outputr = MirrorPoemsAction.create(true, 0, true).apply(input);
    
    TestUtility.testNumberedTree(input, e, g, d, b, f, c, a);
    TestUtility.testNumberedTree(output, f, c, e, g, d, b, a);
    TestUtility.testNumberedTree(outputr, f, c, g, d, e, b, a);
  }
  
  /**
   * test
   */
  @Test
  public void testSwapActionInternal()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output = MirrorPoemsAction.create(true, 1, false).apply(input);
    final BTree<NumberedValue<Module>> outputr = MirrorPoemsAction.create(true, 1, true).apply(input);
    
    TestUtility.testNumberedTree(input, e, g, d, b, f, c, a);
    TestUtility.testNumberedTree(output, g, d, e, b, f, c, a);
    TestUtility.testNumberedTree(outputr, g, d, e, b, f, c, a);
    
    Assert.assertTrue(Finder.find(output, 3).getLeft().getValue().hasNumber(4));
    Assert.assertFalse(Finder.find(output, 3).hasRight());
    
    Assert.assertFalse(Finder.find(outputr, 3).hasLeft());
    Assert.assertTrue(Finder.find(outputr, 3).getRight().getValue().hasNumber(4));
  }
  
  /**
   * test
   */
  @Test
  public void testSwapActionLeaf()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output = MirrorPoemsAction.create(true, 2, false).apply(input);
    final BTree<NumberedValue<Module>> outputr = MirrorPoemsAction.create(true, 2, true).apply(input);
    
    TestUtility.testNumberedTree(input, e, g, d, b, f, c, a);
    TestUtility.testNumberedTree(output, e, g, d, b, f, c, a);
    Assert.assertEquals(output, input);
    TestUtility.testNumberedTree(outputr, e, g, d, b, f, c, a);
    Assert.assertEquals(outputr, input);
  }
  
  /**
   * test
   */
  @Test
  public void testRotateActionRoot()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> gr = new BTree<Module>(new Module("G", 30, 30).flip(), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> fr = new BTree<Module>(new Module("F", 40, 20).flip(), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> er = new BTree<Module>(new Module("E", 20, 20).flip(), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), g, null);
    final BTree<Module> dr = new BTree<Module>(new Module("D", 20, 10).flip(), gr, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, null);
    final BTree<Module> cr = new BTree<Module>(new Module("C", 20, 20).flip(), fr, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), e, d);
    final BTree<Module> br = new BTree<Module>(new Module("B", 30, 20).flip(), er, dr);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    final BTree<Module> ar = new BTree<Module>(new Module("A", 40, 30).flip(), br, cr);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    final BTree<NumberedValue<Module>> output = RotatePoemsAction.create(true, 0).apply(input);
    
    TestUtility.testNumberedTree(input, e, g, d, b, f, c, a);
    TestUtility.testNumberedTree(output, fr, cr, gr, dr, er, br, ar);
    
    for (final BTree<Module> n1 : a)
    {
      Assert.assertFalse(n1.getValue().isFlipped());
    }
    
    for (final BTree<NumberedValue<Module>> n2 : output)
    {
      Assert.assertTrue(n2.getValue().getValue().isFlipped());
    }
  }
  
  /**
   * test
   */
  @Test
  public void testHangActionRoot()
  {
    final BTree<Module> h = new BTree<Module>(new Module("H", 30, 30), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), h, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, g);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), d, e);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    TestUtility.testNumberedTree(input, h, d, e, b, f, g, c, a);
    
    final BTree<NumberedValue<Module>> outputl = HangNodePoemsAction.create(true, true, 5, 0, 5).apply(input);
    TestUtility.testNumberedTree(outputl, f, g, c, h, d, e, b, a);
    Assert.assertFalse(Evaluator.equals(input, outputl));
    
    final BTree<NumberedValue<Module>> outputr = HangNodePoemsAction.create(true, false, 5, 0, 5).apply(input);
    TestUtility.testNumberedTree(outputr, h, d, e, b, f, g, c, a);
    Assert.assertTrue(Evaluator.equals(input, outputr));
  }
  
  /**
   * test
   */
  @Test
  public void testHangActionInternal()
  {
    final BTree<Module> h = new BTree<Module>(new Module("H", 30, 30), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), h, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, g);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), d, e);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    TestUtility.testNumberedTree(input, h, d, e, b, f, g, c, a);
    
    final BTree<NumberedValue<Module>> outputl = HangNodePoemsAction.create(true, true, 5, 2, 5).apply(input);
    TestUtility.testNumberedTree(outputl, f, g, c, d, e, b, h, a);
    
    final BTree<NumberedValue<Module>> outputr = HangNodePoemsAction.create(true, false, 5, 2, 5).apply(input);
    TestUtility.testNumberedTree(outputr, h, f, g, c, d, e, b, a);
  }
  
  /**
   * test
   */
  @Test
  public void testHangActionLeaf()
  {
    final BTree<Module> h = new BTree<Module>(new Module("H", 30, 30), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), h, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, g);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), d, e);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    TestUtility.testNumberedTree(input, h, d, e, b, f, g, c, a);
    
    final BTree<NumberedValue<Module>> outputl = HangNodePoemsAction.create(true, true, 3, 6, 6).apply(input);
    TestUtility.testNumberedTree(outputl, d, e, b, h, f, g, c, a);
    
    final BTree<NumberedValue<Module>> outputr = HangNodePoemsAction.create(true, false, 3, 6, 6).apply(input);
    TestUtility.testNumberedTree(outputr, d, e, b, h, f, g, c, a);
  }
  
  /**
   * test
   */
  @Test
  public void testHangActionHalfFail1()
  {
    final BTree<Module> h = new BTree<Module>(new Module("H", 30, 30), null, null);
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), null, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), null, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), h, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), f, g);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), d, e);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, c);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    TestUtility.testNumberedTree(input, h, d, e, b, f, g, c, a);
    
    final BTree<NumberedValue<Module>> outputl = HangNodePoemsAction.create(true, true, 1, 2, 2).apply(input);
    TestUtility.testNumberedTree(outputl, h, d, e, b, f, g, c, a);
    Assert.assertTrue(Evaluator.equals(input, outputl));
    
    final BTree<NumberedValue<Module>> outputr = HangNodePoemsAction.create(true, false, 1, 2, 2).apply(input);
    TestUtility.testNumberedTree(outputr, h, d, e, b, f, g, c, a);
    Assert.assertTrue(Evaluator.equals(input, outputr));
  }
  
  /**
   * test
   */
  @Test
  public void testHangActionHalfFail2()
  {
    final BTree<Module> g = new BTree<Module>(new Module("G", 30, 30), null, null);
    final BTree<Module> f = new BTree<Module>(new Module("F", 40, 20), g, null);
    final BTree<Module> e = new BTree<Module>(new Module("E", 20, 20), f, null);
    final BTree<Module> d = new BTree<Module>(new Module("D", 20, 10), e, null);
    final BTree<Module> c = new BTree<Module>(new Module("C", 20, 20), d, null);
    final BTree<Module> b = new BTree<Module>(new Module("B", 30, 20), c, null);
    final BTree<Module> a = new BTree<Module>(new Module("A", 40, 30), b, null);
    
    final BTree<NumberedValue<Module>> input = Numberator.number(a);
    TestUtility.testNumberedTree(input, g, f, e, d, c, b, a);
    
    final BTree<NumberedValue<Module>> outputl = HangNodePoemsAction.create(true, true, 5, 3, 5).apply(input);
    TestUtility.testNumberedTree(outputl, g, f, e, d, c, b, a);
    Assert.assertTrue(Evaluator.equals(input, outputl));
    
    final BTree<NumberedValue<Module>> outputr = HangNodePoemsAction.create(true, false, 5, 3, 5).apply(input);
    TestUtility.testNumberedTree(outputr, e, g, f, d, c, b, a);
    Assert.assertFalse(Evaluator.equals(input, outputr));
  }
}
