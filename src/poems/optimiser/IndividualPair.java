package poems.optimiser;

/**
 * Immutable pair of individuals.
 * 
 * @author Vojtěch Hordějčuk
 */
public class IndividualPair
{
  /**
   * first individual
   */
  private final Individual first;
  /**
   * second individual
   */
  private final Individual second;
  
  /**
   * Creates a new instance.
   * 
   * @param first first individual
   * @param second second individual
   */
  protected IndividualPair(final Individual first, final Individual second)
  {
    this.first = first;
    this.second = second;
  }
  
  /**
   * Returns the first individual.
   * 
   * @return the first individual
   */
  public Individual getFirst()
  {
    return this.first;
  }
  
  /**
   * Returns the second individual.
   * 
   * @return the second individual
   */
  public Individual getSecond()
  {
    return this.second;
  }
}
