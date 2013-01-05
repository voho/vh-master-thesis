package module;

import btree.BTreeValue;

/**
 * Immutable unplaced module class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Module extends Rectangle implements BTreeValue
{
  /**
   * module name
   */
  private final String name;
  /**
   * flipped flag
   */
  private final boolean flipped;
  
  /**
   * Creates a new unflipped instance.
   * 
   * @param name name
   * @param width width
   * @param height height
   */
  public Module(final String name, final int width, final int height)
  {
    this(name, width, height, false);
  }
  
  /**
   * Creates a new instance.
   * 
   * @param name name
   * @param width width
   * @param height height
   * @param flipped flipped flag
   */
  public Module(final String name, final int width, final int height, final boolean flipped)
  {
    super(width, height);
    
    this.name = name;
    this.flipped = flipped;
  }
  
  /**
   * Creates a flipped module (with dimensions swapped).
   * 
   * @return flipped module
   */
  public Module flip()
  {
    return new Module(this.name, this.getHeight(), this.getWidth(), !this.flipped);
  }
  
  /**
   * Returns the name.
   * 
   * @return the name
   */
  public String getName()
  {
    return this.name;
  }
  
  /**
   * Returns the flipped flag.
   * 
   * @return FALSE = normal, TRUE = flipped
   */
  public boolean isFlipped()
  {
    return this.flipped;
  }
  
  @Override
  public String toString()
  {
    return this.flipped
        ? this.name + "*"
        : this.name;
  }
  
  @Override
  public int hashCode()
  {
    return this.name.hashCode();
  }
  
  @Override
  public boolean equals(final Object that)
  {
    if (this == that)
    {
      return true;
    }
    
    if (that == null)
    {
      return false;
    }
    
    if (!(that instanceof Module))
    {
      return false;
    }
    
    final Module other = (Module) that;
    
    if (this.flipped != other.flipped)
    {
      return false;
    }
    
    if (!this.name.equals(other.name))
    {
      return false;
    }
    
    return true;
  }
}
