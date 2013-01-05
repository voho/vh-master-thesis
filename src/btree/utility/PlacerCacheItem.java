package btree.utility;

/**
 * Places cache item.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PlacerCacheItem implements Comparable<PlacerCacheItem>
{
  /**
   * item position
   */
  private final int position;
  /**
   * item level
   */
  private final int level;
  /**
   * item dimension
   */
  private final int dimension;
  
  /**
   * Creates a new instance.
   * 
   * @param position item position
   * @param dimension item dimension
   * @param level item level
   */
  protected PlacerCacheItem(final int position, final int dimension, final int level)
  {
    this.position = position;
    this.dimension = dimension;
    this.level = level;
  }
  
  /**
   * Returns the item position.
   * 
   * @return the position
   */
  public int getPosition()
  {
    return this.position;
  }
  
  /**
   * Returns the item dimension.
   * 
   * @return the dimension
   */
  public int getDimension()
  {
    return this.dimension;
  }
  
  /**
   * Returns the item level.
   * 
   * @return the level
   */
  public int getLevel()
  {
    return this.level;
  }
  
  @Override
  public int compareTo(final PlacerCacheItem other)
  {
    if (other.level > this.level)
    {
      // higher level first
      return 1;
    }
    else if (other.level < this.level)
    {
      // lower level last
      return -1;
    }
    else
    {
      if (other != this)
      {
        // different items sort after each other
        return 1;
      }
      
      // items are considered to be the same
      return 0;
    }
  }
  
  @Override
  public String toString()
  {
    return String.format("contour: position = %d, dimension = %d, level = %d", this.position, this.dimension, this.level);
  }
}
