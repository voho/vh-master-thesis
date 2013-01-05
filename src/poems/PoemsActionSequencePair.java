package poems;

/**
 * POEMS action sequence pair.
 * 
 * @author Vojtěch Hordějčuk
 */
public class PoemsActionSequencePair
{
  /**
   * first sequence
   */
  private final PoemsActionSequence first;
  /**
   * second sequence
   */
  private final PoemsActionSequence second;
  
  /**
   * Creates a new instance.
   * 
   * @param first first sequence
   * @param second second sequence
   */
  protected PoemsActionSequencePair(final PoemsActionSequence first, final PoemsActionSequence second)
  {
    this.first = first;
    this.second = second;
  }
  
  /**
   * Returns the first sequence.
   * 
   * @return the first sequence
   */
  public PoemsActionSequence getFirst()
  {
    return this.first;
  }
  
  /**
   * Returns the second sequence.
   * 
   * @return the second sequence
   */
  public PoemsActionSequence getSecond()
  {
    return this.second;
  }
}
