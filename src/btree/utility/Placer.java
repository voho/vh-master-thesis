package btree.utility;

import module.Module;
import module.PlacedModule;
import module.Position;
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
    final PlacerCache cache = new PlacerCache(btree.size() + 2);

    return Placer.createNumberedNode(
        btree,
        0,
        cache.getFirst(),
        cache);
  }

  /**
   * Places the numbered B*-Tree and evaluates the floorplan without creating
   * the tree of placed modules. This is faster than placing and evaluating
   * the tree, as only the bounding box is needed.
   *
   * @param btree input tree
   * @return the floorplan evaluation
   */
  public static Evaluation measureNumbered(final BTree<NumberedValue<Module>> btree)
  {
    final PlacerCache cache = new PlacerCache(btree.size() + 2);
    final int[] box = new int[3];

    Placer.measureNumberedNode(
        btree,
        0,
        cache.getFirst(),
        cache,
        box);

    return new Evaluation(box[0], box[1], box[2]);
  }

  /**
   * Creates and returns the new subtree of placed modules.
   *
   * @param btree input numbered tree
   * @param x input X coordinate
   * @param segment contour segment containing the X coordinate
   * @param cache cache used for this placement
   * @return subtree of placed modules
   */
  private static BTree<PlacedModule> createNumberedNode(final BTree<NumberedValue<Module>> btree, final int x, final int segment, final PlacerCache cache)
  {
    // place the module

    final Module module = btree.getValue().getValue();
    final int placed = cache.place(segment, x, module.getWidth(), module.getHeight());

    final PlacedModule value = new PlacedModule(
        module,
        new Position(
            x,
            cache.getLevel(placed) - module.getHeight()));

    // place the left subtree (next to the module)

    final BTree<PlacedModule> left = btree.hasLeft()
        ? Placer.createNumberedNode(
            btree.getLeft(),
            x + module.getWidth(),
            cache.getNext(placed),
            cache)
        : null;

    // place the right subtree (above the module)

    final BTree<PlacedModule> right = btree.hasRight()
        ? Placer.createNumberedNode(
            btree.getRight(),
            x,
            placed,
            cache)
        : null;

    // return the placed subtree

    return new BTree<PlacedModule>(
        value,
        left,
        right);
  }

  /**
   * Places the subtree and updates the bounding box and the used area.
   *
   * @param btree input numbered tree
   * @param x input X coordinate
   * @param segment contour segment containing the X coordinate
   * @param cache cache used for this placement
   * @param box maximal X, maximal Y and the used area (updated)
   */
  private static void measureNumberedNode(final BTree<NumberedValue<Module>> btree, final int x, final int segment, final PlacerCache cache, final int[] box)
  {
    final Module module = btree.getValue().getValue();
    final int placed = cache.place(segment, x, module.getWidth(), module.getHeight());

    box[0] = Math.max(box[0], x + module.getWidth());
    box[1] = Math.max(box[1], cache.getLevel(placed));
    box[2] += module.getArea();

    if (btree.hasLeft())
    {
      Placer.measureNumberedNode(
          btree.getLeft(),
          x + module.getWidth(),
          cache.getNext(placed),
          cache,
          box);
    }

    if (btree.hasRight())
    {
      Placer.measureNumberedNode(
          btree.getRight(),
          x,
          placed,
          cache,
          box);
    }
  }
}
