package module;

import btree.BTreeValue;

/**
 * Immutable placed module class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PlacedModule implements BTreeValue
{
  /**
   * module
   */
  private final Module module;
  /**
   * module position
   */
  private final Position position;
  /**
   * maximal position (X position + width, Y position + height)
   */
  private final Position maxPosition;
  /**
   * center position (X position + width / 2, Y position + height / 2)
   */
  private final Position centerPosition;
  
  /**
   * Creates a new instance.
   * 
   * @param module module
   * @param position position
   */
  public PlacedModule(final Module module, final Position position)
  {
    this.module = module;
    this.position = position;
    this.maxPosition = new Position(
        position.getX() + this.module.getWidth(),
        position.getY() + this.module.getHeight());
    this.centerPosition = new Position(
        position.getX() + this.module.getWidth() / 2,
        position.getY() + this.module.getHeight() / 2);
  }
  
  /**
   * Returns the module
   * 
   * @return the module
   */
  public Module getModule()
  {
    return this.module;
  }
  
  /**
   * Returns the position.
   * 
   * @return the position
   */
  public Position getPosition()
  {
    return this.position;
  }
  
  /**
   * Returns the maximal position (right bottom).
   * 
   * @return the maximal position
   */
  public Position getMaxPosition()
  {
    return this.maxPosition;
  }
  
  /**
   * Returns the center position.
   * 
   * @return the center position
   */
  public Position getCenterPosition()
  {
    return this.centerPosition;
  }
  
  @Override
  public String toString()
  {
    return String.format("%s%s", this.module.toString(), this.position.toString());
  }
}
