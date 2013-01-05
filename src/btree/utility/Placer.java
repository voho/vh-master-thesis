package btree.utility;

import module.Module;
import module.PlacedModule;
import btree.BTree;
import btree.NumberedValue;

/**
 * Class for placing the floorplan encoded in a B*-Tree.
 * 
 * @author Vojtěch Hordějčuk
 */
final public class Placer
{
  /**
   * No creation is allowed.
   */
  private Placer()
  {
    // NOP
  }
  
  /**
   * Places the numbered B*-Tree.
   * 
   * @param btree input tree
   * @return placed input tree
   */
  public static BTree<PlacedModule> placeNumbered(final BTree<NumberedValue<Module>> btree)
  {
    return Placer.createNumberedNode(
        btree,
        0,
        new PlacerCache());
  }
  
  /**
   * Places the left subtree of the input numbered B*-Tree.
   * 
   * @param btree input tree
   * @param parent parent placed module
   * @param cache cache used for this placement
   * @return placed left subtree
   */
  private static BTree<PlacedModule> placeLeftNumbered(final BTree<NumberedValue<Module>> btree, final PlacedModule parent, final PlacerCache cache)
  {
    return Placer.createNumberedNode(
        btree,
        parent.getPosition().getX() + parent.getModule().getWidth(),
        cache);
  }
  
  /**
   * Places the right subtree of the input numbered B*-Tree.
   * 
   * @param btree input tree
   * @param parent parent placed module
   * @param cache cache used for this placement
   * @return placed right subtree
   */
  private static BTree<PlacedModule> placeRightNumbered(final BTree<NumberedValue<Module>> btree, final PlacedModule parent, final PlacerCache cache)
  {
    return Placer.createNumberedNode(
        btree,
        parent.getPosition().getX(),
        cache);
  }
  
  /**
   * Creates and returns the new subtree of placed modules.
   * 
   * @param btree input numbered tree
   * @param x input X coordinate
   * @param cache cache used for this placement
   * @return subtree of placed modules
   */
  private static BTree<PlacedModule> createNumberedNode(final BTree<NumberedValue<Module>> btree, final int x, final PlacerCache cache)
  {
    // place the module
    
    final PlacedModule value = Placer.createNodeValue(
        btree.getValue().getValue(),
        cache,
        x);
    
    // place the left subtree
    
    final BTree<PlacedModule> left = btree.hasLeft()
        ? Placer.placeLeftNumbered(
            btree.getLeft(),
            value,
            cache)
        : null;
    
    // place the right subtree
    
    final BTree<PlacedModule> right = btree.hasRight()
        ? Placer.placeRightNumbered(
            btree.getRight(),
            value,
            cache)
        : null;
    
    // return the placed subtree
    
    return new BTree<PlacedModule>(
        value,
        left,
        right);
  }
  
  /**
   * Creates a placed B*-Tree node value.
   * 
   * @param module module to be placed
   * @param cache cache used for this placement
   * @param x X coordinate
   * @return placed tree node value
   */
  private static PlacedModule createNodeValue(final Module module, final PlacerCache cache, final int x)
  {
    return new PlacedModule(
        module,
        cache.place(
            x,
            module.getWidth(),
            module.getHeight()));
  }
}
