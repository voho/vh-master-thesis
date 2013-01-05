package btree.utility;

import java.util.SortedSet;
import java.util.TreeSet;

import module.Position;

/**
 * Placer cache used for faster searching for the Y coordinate.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PlacerCache
{
  /**
   * contour cache
   */
  private final SortedSet<PlacerCacheItem> contour;
  
  /**
   * Creates a new instance.
   */
  public PlacerCache()
  {
    this.contour = new TreeSet<PlacerCacheItem>();
  }
  
  /**
   * Places the item defined by the parameters.
   * 
   * @param x item X coordinate
   * @param width item width
   * @param height item height
   * @return item position
   */
  protected Position place(final int x, final int width, final int height)
  {
    return new Position(x, this.findY(x, width, height));
  }
  
  /**
   * Finds Y coordinate for the item defined by the parameters.
   * 
   * @param x item X coordinate
   * @param width item width
   * @param height item height
   * @return item Y coordinate
   */
  public int findY(final int x, final int width, final int height)
  {
    int level = 0;
    
    for (final PlacerCacheItem testing : this.contour)
    {
      if (PlacerCache.overlaps(x, width, testing.getPosition(), testing.getDimension()))
      {
        // first overlapping item found
        
        level = testing.getLevel();
        break;
      }
    }
    
    // update countour cache
    
    this.contour.add(new PlacerCacheItem(x, width, level + height));
    
    return level;
  }
  
  /**
   * Checks if two lines overlap.
   * 
   * @param x1 first line X
   * @param w1 first line width
   * @param x2 second line X
   * @param w2 second line width
   * @return TRUE if the lines overlap, FALSE otherwise
   */
  private static boolean overlaps(final int x1, final int w1, final int x2, final int w2)
  {
    if (x1 + w1 <= x2)
    {
      return false;
    }
    
    if (x1 >= x2 + w2)
    {
      return false;
    }
    
    return true;
  }
}
