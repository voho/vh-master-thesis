package btree.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import module.Module;
import btree.BTree;

/**
 * Utility class for creating a floorplan prototype from modules.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Prototyper
{
  /**
   * No creation allowed.
   */
  private Prototyper()
  {
    // NOP
  }
  
  /**
   * Creates a prototype as a complete tree.
   * 
   * @param modules input set of modules
   * @return a prototype
   */
  public static BTree<Module> createCompleteTree(final Set<Module> modules)
  {
    final Module[] heap = new Module[modules.size()];
    Prototyper.createUnplacedQueue(Prototyper.flipToWide(modules)).toArray(heap);
    Arrays.sort(heap, Prototyper.createModuleComparator());
    return Prototyper.createFromArray(heap, 0);
  }
  
  /**
   * Creates a tree from the array starting from the given index.
   * 
   * @param input input array
   * @param index index of a root
   * @return a tree from an array
   */
  private static BTree<Module> createFromArray(final Module[] input, final int index)
  {
    if (index >= input.length)
    {
      return null;
    }
    
    return new BTree<Module>(
        input[index],
        Prototyper.createFromArray(input, 2 * index + 1),
        Prototyper.createFromArray(input, 2 * index + 2));
  }
  
  /**
   * Creates a prototype by a best-fit heuristic.
   * 
   * @param modules input set of modules
   * @return a prototype
   */
  @SuppressWarnings("null")
  public static BTree<Module> createBestFit(final Set<Module> modules)
  {
    // initialize collections
    
    final Set<Module> unplacedSet = Prototyper.flipToWide(modules);
    final Queue<Module> unplacedQueue = Prototyper.createUnplacedQueue(unplacedSet);
    
    // initialize variables
    
    final int defaultSpace = Prototyper.getWidthApproximation(unplacedSet);
    boolean newLevel = false;
    int space = defaultSpace;
    
    // initialize temporary tree nodes
    
    TempBTree root = null;
    TempBTree current = null;
    TempBTree firstOnLevel = null;
    
    // build tree recursively
    
    while (!unplacedQueue.isEmpty())
    {
      final Module module = Prototyper.pollBestFit(unplacedQueue, space);
      
      if (module == null)
      {
        // no fitting module found, increase level
        
        space = defaultSpace;
        newLevel = true;
        continue;
      }
      
      final TempBTree newNode = new TempBTree(module);
      
      if (root == null)
      {
        // first module created, mark as root
        
        root = newNode;
        current = newNode;
        firstOnLevel = newNode;
      }
      else
      {
        if (newLevel)
        {
          firstOnLevel.right = newNode;
          firstOnLevel = newNode;
          newLevel = false;
        }
        else
        {
          current.left = newNode;
        }
        
        current = newNode;
      }
      
      // make the space smaller
      
      space -= module.getWidth();
    }
    
    // convert the temporary tree to a regular B*-Tree
    
    return Prototyper.createBTreeFromProto(root);
  }
  
  /**
   * Converts a temporary tree to a regular B*-Tree.
   * 
   * @param tree the input temporary tree
   * @return a B*-Tree
   */
  private static BTree<Module> createBTreeFromProto(final TempBTree tree)
  {
    if (tree == null)
    {
      return null;
    }
    
    return new BTree<Module>(
        tree.getModule(),
        Prototyper.createBTreeFromProto(tree.left),
        Prototyper.createBTreeFromProto(tree.right));
  }
  
  /**
   * Picks the first module from a queue that fits into the spece provided. If
   * no such module exists, NULL is returned.
   * 
   * @param queue queue of modules
   * @param space space width
   * @return the best module from a queue that fits into the given space
   */
  private static Module pollBestFit(final Queue<Module> queue, final int space)
  {
    for (final Module module : queue)
    {
      if (module.getWidth() <= space)
      {
        queue.remove(module);
        return module;
      }
    }
    
    return null;
  }
  
  /**
   * Creates a priority queue of modules, where modules are sorted by the common
   * module comparator.
   * 
   * @param modules input modules
   * @return module queue
   */
  private static Queue<Module> createUnplacedQueue(final Set<Module> modules)
  {
    final Queue<Module> queue = new PriorityQueue<Module>(modules.size(), Prototyper.createModuleComparator());
    queue.addAll(modules);
    return queue;
  }
  
  /**
   * Creates and returns the module comparator.
   * 
   * @return module comparator
   */
  private static Comparator<Module> createModuleComparator()
  {
    return new Comparator<Module>()
    {
      @Override
      public int compare(final Module o1, final Module o2)
      {
        if (o1.getWidth() > o2.getWidth())
        {
          return -1;
        }
        else if (o1.getWidth() < o2.getWidth())
        {
          return 1;
        }
        else
        {
          if (o1.getHeight() > o2.getHeight())
          {
            return -1;
          }
          else if (o1.getHeight() < o2.getHeight())
          {
            return 1;
          }
          else
          {
            return 0;
          }
        }
      }
    };
  }
  
  /**
   * Creates a new set of modules by flipping the modules that are taller than
   * wider. This method does not modify the input module set.
   * 
   * @param modules input modules
   * @return modules flipped
   */
  private static Set<Module> flipToWide(final Iterable<Module> modules)
  {
    final Set<Module> flipped = new HashSet<Module>();
    
    for (final Module module : modules)
    {
      flipped.add(
          (module.getHeight() > module.getWidth())
              ? module.flip()
              : module);
    }
    
    return Collections.unmodifiableSet(flipped);
  }
  
  /**
   * Returns the floorplan width approximation for the given set of modules. The
   * width approximation equals to the maximal width of the module or a square
   * root of total block area.
   * 
   * @param modules modules to get width approximation of
   * @return the floorplan width approximation
   */
  private static int getWidthApproximation(final Set<Module> modules)
  {
    int usedArea = 0;
    int maxWidth = 0;
    
    for (final Module module : modules)
    {
      usedArea += module.getArea();
      
      if (module.getWidth() > maxWidth)
      {
        maxWidth = module.getWidth();
      }
    }
    
    final int areaWidth = (int) (Math.ceil(Math.sqrt(usedArea)));
    
    return Math.max(areaWidth, maxWidth);
  }
  
  /**
   * Temporary B*-Tree node.
   * 
   * @author Vojtěch Hordějčuk
   */
  private static class TempBTree
  {
    /**
     * left subtree
     */
    protected TempBTree left = null;
    /**
     * right subtree
     */
    protected TempBTree right = null;
    /**
     * assigned module
     */
    private final Module module;
    
    /**
     * Creates a new instance.
     * 
     * @param module module
     */
    public TempBTree(final Module module)
    {
      this.module = module;
    }
    
    /**
     * Returns the assigned module.
     * 
     * @return the module
     */
    public Module getModule()
    {
      return this.module;
    }
  }
}
