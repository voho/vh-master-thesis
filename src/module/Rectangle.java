package module;

/**
 * Immutable unplaced rectangle class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Rectangle
{
  /**
   * width
   */
  private final int width;
  /**
   * height
   */
  private final int height;
  /**
   * area
   */
  private final int area;
  /**
   * perimeter
   */
  private final int perimeter;
  
  /**
   * Creates a new instance.
   * 
   * @param width width
   * @param height height
   */
  public Rectangle(final int width, final int height)
  {
    if ((width <= 0) || (height <= 0))
    {
      throw new IllegalArgumentException("Invalid width or height. Both must be greater than 0.");
    }
    
    this.width = width;
    this.height = height;
    this.area = width * height;
    this.perimeter = width + width + height + height;
  }
  
  /**
   * Returns the width.
   * 
   * @return the width
   */
  public int getWidth()
  {
    return this.width;
  }
  
  /**
   * Returns the height.
   * 
   * @return the height
   */
  public int getHeight()
  {
    return this.height;
  }
  
  /**
   * Returns the area.
   * 
   * @return the area
   */
  public int getArea()
  {
    return this.area;
  }
  
  /**
   * Returns the perimeter.
   * 
   * @return the perimeter
   */
  public int getPerimeter()
  {
    return this.perimeter;
  }
  
  @Override
  public String toString()
  {
    return String.format("[%dx%d]", this.width, this.height);
  }
}
