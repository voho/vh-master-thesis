package btree.utility;

/**
 * Immutable floorplan evaluation result.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Evaluation
{
  /**
   * floorplan width
   */
  private final int width;
  /**
   * floorplan height
   */
  private final int height;
  /**
   * floorplan perimeter
   */
  private final int perimeter;
  /**
   * total floorplan area
   */
  private final int totalArea;
  /**
   * total area used by modules
   */
  private final int usedArea;
  /**
   * relative dead area
   */
  private final double relativeDeadArea;
  
  /**
   * Creates a new instance.
   * 
   * @param width floorplan width
   * @param height floorplan height
   * @param usedArea area used by modules
   */
  protected Evaluation(final int width, final int height, final int usedArea)
  {
    this.width = width;
    this.height = height;
    this.perimeter = width + height + width + height;
    this.totalArea = width * height;
    this.usedArea = usedArea;
    this.relativeDeadArea = ((double) this.totalArea / (double) this.usedArea) - 1.0;
  }
  
  /**
   * Returns the floorplan width.
   * 
   * @return the width
   */
  public int getWidth()
  {
    return this.width;
  }
  
  /**
   * Returns the floorplan height.
   * 
   * @return the height
   */
  public int getHeight()
  {
    return this.height;
  }
  
  /**
   * Returns the floorplan perimeter.
   * 
   * @return the perimeter
   */
  public int getPerimeter()
  {
    return this.perimeter;
  }
  
  /**
   * Returns the total floorplan area.
   * 
   * @return total floorplan area
   */
  public int getTotalArea()
  {
    return this.totalArea;
  }
  
  /**
   * Returns the total area used by modules.
   * 
   * @return the area used by modules
   */
  public int getUsedArea()
  {
    return this.usedArea;
  }
  
  /**
   * Returns the floorplan unused area.
   * 
   * @return the unused floorplan area
   */
  public int getUnusedArea()
  {
    return this.totalArea - this.usedArea;
  }
  
  /**
   * Returns the relative floorplan unused area.
   * 
   * @return relative unused area
   */
  public double getRelativeUnusedArea()
  {
    return this.relativeDeadArea;
  }
  
  /**
   * Returns the floorplan quality (fitness).
   * 
   * @return the fitness
   */
  public int getFitness()
  {
    return -this.getUnusedArea();
  }
}
