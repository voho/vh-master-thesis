package module;

/**
 * Immutable plane position class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Position
{
  /**
   * X coordinate
   */
  private final int x;
  /**
   * Y coordinate
   */
  private final int y;
  
  /**
   * Creates a new instance.
   * 
   * @param x X coordinate
   * @param y Y coordinate
   */
  public Position(final int x, final int y)
  {
    this.x = x;
    this.y = y;
  }
  
  /**
   * Returns the X coordinate.
   * 
   * @return the X coordinate
   */
  public int getX()
  {
    return this.x;
  }
  
  /**
   * Returns the Y coordinate.
   * 
   * @return the Y coordinate
   */
  public int getY()
  {
    return this.y;
  }
  
  @Override
  public String toString()
  {
    return String.format("[%d,%d]", this.x, this.y);
  }
}
