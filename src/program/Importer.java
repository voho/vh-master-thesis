package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import module.Module;

/**
 * Importer utility class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Importer
{
  /**
   * logger instance
   */
  private static final Logger LOG = Logger.create(Importer.class);
  
  /**
   * Creation is not allowed.
   */
  private Importer()
  {
    // NOP
  }
  
  /**
   * Loads a problem module set from a file.
   * 
   * @param input input file
   * @return problem assignement (set of modules)
   * @throws FileNotFoundException file not found
   */
  public static Set<Module> loadProblem(final File input) throws FileNotFoundException
  {
    Importer.LOG.log("Loading modules from file '%s'...", input.getAbsolutePath());
    
    // initialize
    
    final Scanner scanner = new Scanner(input);
    final Set<Module> result = new HashSet<Module>();
    
    // skip module count
    
    scanner.nextInt();
    
    while (scanner.hasNextDouble())
    {
      // read module dimensions
      
      final double width = scanner.nextDouble();
      final double height = scanner.nextDouble();
      
      // create module
      
      final Module module = new Module(String.valueOf(1 + result.size()), (int) width, (int) height);
      result.add(module);
    }
    
    Importer.LOG.log("Imported %d modules.", result.size());
    
    return Collections.unmodifiableSet(result);
  }
}
