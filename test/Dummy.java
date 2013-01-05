import btree.BTreeValue;

/**
 * Dummy utility class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Dummy implements BTreeValue
{
  /**
   * common dummy counter counter
   */
  private static int counter = 1;
  /**
   * dummy number
   */
  private final int n;
  
  /**
   * Creates a new instance.
   */
  public Dummy()
  {
    this.n = Dummy.counter++;
  }
  
  @Override
  public String toString()
  {
    return "Dummy [" + this.n + "]";
  }
}
