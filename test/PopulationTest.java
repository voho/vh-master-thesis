import java.util.HashSet;
import java.util.Set;

import module.Module;

import org.junit.Assert;
import org.junit.Test;

import poems.optimiser.Individual;
import poems.optimiser.Population;
import btree.BTree;
import btree.NumberedValue;
import btree.utility.Numberator;
import btree.utility.Prototyper;

/**
 * Population test.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PopulationTest
{
  /**
   * test
   */
  @Test
  public void testQuality()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    modules.add(new Module("A", 1, 6));
    modules.add(new Module("B", 2, 5));
    modules.add(new Module("C", 3, 4));
    modules.add(new Module("D", 4, 3));
    modules.add(new Module("E", 5, 2));
    modules.add(new Module("F", 6, 1));
    
    final BTree<NumberedValue<Module>> prototype = Numberator.number(Prototyper.createCompleteTree(modules));
    
    final Population p = new Population(prototype, 5, 10);
    
    final int f1 = p.evolve(1).getFitness();
    final int f2 = p.evolve(1).getFitness();
    final int f3 = p.evolve(1).getFitness();
    final int f4 = p.evolve(10).getFitness();
    final int f5 = p.evolve(100).getFitness();
    
    Assert.assertTrue(f2 >= f1);
    Assert.assertTrue(f3 >= f2);
    Assert.assertTrue(f4 >= f3);
    Assert.assertTrue(f5 >= f4);
  }
  
  /**
   * test
   */
  @Test
  public void testSelect()
  {
    final Set<Module> modules = new HashSet<Module>();
    
    modules.add(new Module("A", 1, 6));
    modules.add(new Module("B", 2, 5));
    modules.add(new Module("C", 3, 4));
    modules.add(new Module("D", 4, 3));
    modules.add(new Module("E", 5, 2));
    modules.add(new Module("F", 6, 1));
    
    final BTree<NumberedValue<Module>> prototype = Numberator.number(Prototyper.createCompleteTree(modules));
    
    final int n = 5;
    final int s = 100;
    
    final Population p = new Population(prototype, n, s);
    
    for (int i = 0; i < s; i++)
    {
      for (int j = 1; j <= n; j++)
      {
        final Individual picked = p.select(
            Population.getNicheLow(
                j,
                s),
            Population.getNicheHigh(
                j,
                s));
        
        Assert.assertTrue(String.format("[%d] %s", j, picked), picked.getNiche() >= j);
      }
    }
    
    p.evolve(1000);
    
    for (int i = 0; i < s; i++)
    {
      for (int j = 1; j <= n; j++)
      {
        Assert.assertTrue(p.select(
            Population.getNicheLow(
                j,
                s),
            Population.getNicheHigh(
                j,
                s)).getNiche() >= j);
      }
    }
  }
  
  /**
   * test
   */
  @Test
  public void testHighLow1()
  {
    Assert.assertEquals(0, Population.getNicheLow(1, 3));
    Assert.assertEquals(3, Population.getNicheLow(2, 3));
    Assert.assertEquals(6, Population.getNicheLow(3, 3));
    
    Assert.assertEquals(2, Population.getNicheHigh(1, 3));
    Assert.assertEquals(5, Population.getNicheHigh(2, 3));
    Assert.assertEquals(8, Population.getNicheHigh(3, 3));
  }
  
  /**
   * test
   */
  @Test
  public void testHighLow2()
  {
    Assert.assertEquals(0, Population.getNicheLow(1, 10));
    Assert.assertEquals(40, Population.getNicheLow(5, 10));
    Assert.assertEquals(90, Population.getNicheLow(10, 10));
    
    Assert.assertEquals(9, Population.getNicheHigh(1, 10));
    Assert.assertEquals(49, Population.getNicheHigh(5, 10));
    Assert.assertEquals(99, Population.getNicheHigh(10, 10));
  }
}
