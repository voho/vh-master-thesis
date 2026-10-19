package poems.hybrid;

import java.util.Arrays;

import module.Module;
import btree.BTree;
import btree.NumberedValue;

/**
 * Mutable B*-Tree stored in arrays, used by the late acceptance POEMS. The
 * tree is modified in place, which is much faster than creating a new
 * immutable tree for every modification.
 *
 * Each node holds one module. Modules are addressed by their numbers (the
 * same numbers as in the numbered prototype), as in the POEMS actions, where
 * the number moves together with the module.
 *
 * @author Vojtěch Hordějčuk
 */
public final class ArrayBTree
{
  /**
   * no node
   */
  private static final int NONE = -1;
  /**
   * number of nodes (modules)
   */
  private final int n;
  /**
   * module widths (not rotated)
   */
  private final int[] widths;
  /**
   * module heights (not rotated)
   */
  private final int[] heights;
  /**
   * original numbered module values (for the conversion back to a B*-Tree)
   */
  private final NumberedValue<Module>[] values;
  /**
   * left child of each node
   */
  private final int[] left;
  /**
   * right child of each node
   */
  private final int[] right;
  /**
   * parent of each node
   */
  private final int[] parent;
  /**
   * module held by each node
   */
  private final int[] module;
  /**
   * node holding each module
   */
  private final int[] node;
  /**
   * module rotation flags
   */
  private final boolean[] rotated;
  /**
   * root node
   */
  private int root;
  /**
   * contour segment starts (placement scratch)
   */
  private final int[] start;
  /**
   * contour segment ends (placement scratch)
   */
  private final int[] end;
  /**
   * contour segment levels (placement scratch)
   */
  private final int[] level;
  /**
   * contour segment successors (placement scratch)
   */
  private final int[] next;
  /**
   * DFS stack of nodes (placement scratch)
   */
  private final int[] stackNode;
  /**
   * DFS stack of X coordinates (placement scratch)
   */
  private final int[] stackX;
  /**
   * DFS stack of contour segments (placement scratch)
   */
  private final int[] stackSegment;
  /**
   * floorplan width (after the last placement)
   */
  private int width;
  /**
   * floorplan height (after the last placement)
   */
  private int height;

  /**
   * Creates a mutable copy of the numbered B*-Tree. The tree must be numbered
   * by numbers 0 to N-1.
   *
   * @param tree numbered B*-Tree
   */
  @SuppressWarnings("unchecked")
  public ArrayBTree(final BTree<NumberedValue<Module>> tree)
  {
    this.n = tree.size();
    this.widths = new int[this.n];
    this.heights = new int[this.n];
    this.values = new NumberedValue[this.n];
    this.left = new int[this.n];
    this.right = new int[this.n];
    this.parent = new int[this.n];
    this.module = new int[this.n];
    this.node = new int[this.n];
    this.rotated = new boolean[this.n];
    this.start = new int[this.n + 2];
    this.end = new int[this.n + 2];
    this.level = new int[this.n + 2];
    this.next = new int[this.n + 2];
    this.stackNode = new int[this.n];
    this.stackX = new int[this.n];
    this.stackSegment = new int[this.n];

    for (final BTree<NumberedValue<Module>> subtree : tree)
    {
      final int number = subtree.getValue().getNumber();
      final Module value = subtree.getValue().getValue();

      this.values[number] = subtree.getValue();
      this.widths[number] = value.getWidth();
      this.heights[number] = value.getHeight();

      // node I holds the module I at the beginning

      this.module[number] = number;
      this.node[number] = number;
      this.left[number] = subtree.hasLeft()
          ? subtree.getLeft().getValue().getNumber()
          : ArrayBTree.NONE;
      this.right[number] = subtree.hasRight()
          ? subtree.getRight().getValue().getNumber()
          : ArrayBTree.NONE;
      this.parent[number] = ArrayBTree.NONE;
    }

    for (int i = 0; i < this.n; i++)
    {
      if (this.left[i] != ArrayBTree.NONE)
      {
        this.parent[this.left[i]] = i;
      }

      if (this.right[i] != ArrayBTree.NONE)
      {
        this.parent[this.right[i]] = i;
      }
    }

    this.root = tree.getValue().getNumber();
  }

  /**
   * Creates a copy of the tree.
   *
   * @param other tree to copy
   */
  public ArrayBTree(final ArrayBTree other)
  {
    this.n = other.n;
    this.widths = other.widths;
    this.heights = other.heights;
    this.values = other.values;
    this.left = other.left.clone();
    this.right = other.right.clone();
    this.parent = other.parent.clone();
    this.module = other.module.clone();
    this.node = other.node.clone();
    this.rotated = other.rotated.clone();
    this.root = other.root;
    this.start = new int[this.n + 2];
    this.end = new int[this.n + 2];
    this.level = new int[this.n + 2];
    this.next = new int[this.n + 2];
    this.stackNode = new int[this.n];
    this.stackX = new int[this.n];
    this.stackSegment = new int[this.n];
    this.width = other.width;
    this.height = other.height;
  }

  /**
   * Makes this tree equal to the other tree (of the same problem).
   *
   * @param other tree to copy
   */
  public void copyFrom(final ArrayBTree other)
  {
    System.arraycopy(other.left, 0, this.left, 0, this.n);
    System.arraycopy(other.right, 0, this.right, 0, this.n);
    System.arraycopy(other.parent, 0, this.parent, 0, this.n);
    System.arraycopy(other.module, 0, this.module, 0, this.n);
    System.arraycopy(other.node, 0, this.node, 0, this.n);
    System.arraycopy(other.rotated, 0, this.rotated, 0, this.n);
    this.root = other.root;
    this.width = other.width;
    this.height = other.height;
  }

  /**
   * Returns the number of modules.
   *
   * @return the number of modules
   */
  public int size()
  {
    return this.n;
  }

  /**
   * Returns the floorplan width computed by the last placement.
   *
   * @return the floorplan width
   */
  public int getWidth()
  {
    return this.width;
  }

  /**
   * Returns the floorplan height computed by the last placement.
   *
   * @return the floorplan height
   */
  public int getHeight()
  {
    return this.height;
  }

  /**
   * Places all modules (B*-Tree packing using the contour) and returns the
   * area of the enclosing rectangle.
   *
   * @return the total floorplan area
   */
  public long place()
  {
    // one infinite segment at the ground level

    this.start[0] = 0;
    this.end[0] = Integer.MAX_VALUE;
    this.level[0] = 0;
    this.next[0] = ArrayBTree.NONE;

    int segments = 1;
    int maxX = 0;
    int maxY = 0;
    int top = 0;

    this.stackNode[0] = this.root;
    this.stackX[0] = 0;
    this.stackSegment[0] = 0;
    top++;

    while (top > 0)
    {
      top--;

      final int current = this.stackNode[top];
      final int x = this.stackX[top];
      final int first = this.stackSegment[top];
      final int m = this.module[current];
      final int w = this.rotated[m]
          ? this.heights[m]
          : this.widths[m];
      final int h = this.rotated[m]
          ? this.widths[m]
          : this.heights[m];

      // walk the contour segments covered by the module (the first segment
      // always starts at X) and find the highest one

      final int xr = x + w;
      int y = 0;
      int after = ArrayBTree.NONE;
      int segment = first;

      while (segment != ArrayBTree.NONE)
      {
        if (this.start[segment] >= xr)
        {
          after = segment;
          break;
        }

        if (this.level[segment] > y)
        {
          y = this.level[segment];
        }

        if (this.end[segment] > xr)
        {
          // keep the part of the segment which is not covered

          this.start[segments] = xr;
          this.end[segments] = this.end[segment];
          this.level[segments] = this.level[segment];
          this.next[segments] = this.next[segment];
          after = segments++;
          break;
        }

        segment = this.next[segment];
      }

      // the first segment is reused for the placed module

      this.end[first] = xr;
      this.level[first] = y + h;
      this.next[first] = after;

      if (xr > maxX)
      {
        maxX = xr;
      }

      if (y + h > maxY)
      {
        maxY = y + h;
      }

      // the right child is placed above (starts at the same segment), the left
      // child is placed next to the module (starts at the following segment)

      if (this.right[current] != ArrayBTree.NONE)
      {
        this.stackNode[top] = this.right[current];
        this.stackX[top] = x;
        this.stackSegment[top] = first;
        top++;
      }

      if (this.left[current] != ArrayBTree.NONE)
      {
        this.stackNode[top] = this.left[current];
        this.stackX[top] = xr;
        this.stackSegment[top] = after;
        top++;
      }
    }

    this.width = maxX;
    this.height = maxY;

    return (long) maxX * (long) maxY;
  }

  /**
   * Converts the tree back to the immutable numbered B*-Tree.
   *
   * @return numbered B*-Tree
   */
  public BTree<NumberedValue<Module>> toBTree()
  {
    return this.toBTree(this.root);
  }

  /**
   * Converts the subtree back to the immutable numbered B*-Tree.
   *
   * @param current subtree root
   * @return numbered B*-Tree
   */
  private BTree<NumberedValue<Module>> toBTree(final int current)
  {
    final int m = this.module[current];
    final NumberedValue<Module> original = this.values[m];

    return new BTree<NumberedValue<Module>>(
        NumberedValue.create(
            original,
            this.rotated[m]
                ? original.getValue().flip()
                : original.getValue()),
        (this.left[current] != ArrayBTree.NONE)
            ? this.toBTree(this.left[current])
            : null,
        (this.right[current] != ArrayBTree.NONE)
            ? this.toBTree(this.right[current])
            : null);
  }

  // ACTIONS
  // =======

  /**
   * Flips (rotates) the module, or all modules in its subtree.
   *
   * @param a module number
   * @param recursive flip the whole subtree
   */
  public void flip(final int a, final boolean recursive)
  {
    if (!recursive)
    {
      this.rotated[a] = !this.rotated[a];
      return;
    }

    final int[] stack = this.stackNode;
    int top = 0;
    stack[top++] = this.node[a];

    while (top > 0)
    {
      final int current = stack[--top];

      this.rotated[this.module[current]] = !this.rotated[this.module[current]];

      if (this.left[current] != ArrayBTree.NONE)
      {
        stack[top++] = this.left[current];
      }

      if (this.right[current] != ArrayBTree.NONE)
      {
        stack[top++] = this.right[current];
      }
    }
  }

  /**
   * Swaps the children of the node holding the module, or of all nodes in
   * its subtree. If requested, the modules are flipped too (this is the
   * rotate action).
   *
   * @param a module number
   * @param recursive mirror the whole subtree
   * @param flip flip the modules too
   */
  public void mirror(final int a, final boolean recursive, final boolean flip)
  {
    final int[] stack = this.stackNode;
    int top = 0;
    stack[top++] = this.node[a];

    while (top > 0)
    {
      final int current = stack[--top];
      final int temp = this.left[current];

      this.left[current] = this.right[current];
      this.right[current] = temp;

      if (flip)
      {
        this.rotated[this.module[current]] = !this.rotated[this.module[current]];
      }

      if (recursive)
      {
        if (this.left[current] != ArrayBTree.NONE)
        {
          stack[top++] = this.left[current];
        }

        if (this.right[current] != ArrayBTree.NONE)
        {
          stack[top++] = this.right[current];
        }
      }
    }
  }

  /**
   * Exchanges the modules of two nodes (the tree structure is kept).
   *
   * @param a first module number
   * @param b second module number
   */
  public void exchangeValues(final int a, final int b)
  {
    if (a == b)
    {
      return;
    }
    
    final int na = this.node[a];
    final int nb = this.node[b];

    this.module[na] = b;
    this.module[nb] = a;
    this.node[a] = nb;
    this.node[b] = na;
  }

  /**
   * Exchanges two disjoint subtrees. Nothing is done if one subtree contains
   * the other one.
   *
   * @param a first module number
   * @param b second module number
   */
  public void exchangeNodes(final int a, final int b)
  {
    if (a == b)
    {
      return;
    }
    
    final int na = this.node[a];
    final int nb = this.node[b];

    if (this.isInSubtree(na, nb) || this.isInSubtree(nb, na))
    {
      return;
    }

    final int pa = this.parent[na];
    final int pb = this.parent[nb];

    if (pa == pb)
    {
      // siblings

      this.left[pa] = this.right[pa];
      this.right[pa] = (this.left[pa] == na)
          ? nb
          : na;
      return;
    }

    final boolean aLeft = (this.left[pa] == na);
    final boolean bLeft = (this.left[pb] == nb);

    this.setChild(pa, aLeft, nb);
    this.setChild(pb, bLeft, na);
  }

  /**
   * Hangs the subtree of the source as the child of the target. The original
   * child of the target takes the original place of the source. Nothing is
   * done if the target lies in the source subtree or if the source already
   * lies in the target subtree on the given side.
   *
   * @param a source module number
   * @param b target module number
   * @param toLeft hang as the left child
   */
  public void hang(final int a, final int b, final boolean toLeft)
  {
    if (a == b)
    {
      return;
    }
    
    final int na = this.node[a];
    final int nb = this.node[b];

    if (this.isInSubtree(na, nb))
    {
      return;
    }

    final int child = toLeft
        ? this.left[nb]
        : this.right[nb];

    if ((child != ArrayBTree.NONE) && this.isInSubtree(child, na))
    {
      return;
    }

    // the source is not the root, as the root contains the target

    final int pa = this.parent[na];

    this.setChild(pa, this.left[pa] == na, child);
    this.setChild(nb, toLeft, na);
  }

  /**
   * Moves a single node: removes it from the tree (its children take its
   * place) and inserts it as the child of the target. The original child of
   * the target becomes the child of the moved node on the same side.
   *
   * @param a module number to move
   * @param b target module number
   * @param toLeft insert as the left child
   */
  public void move(final int a, final int b, final boolean toLeft)
  {
    if ((a == b) || (this.n < 2))
    {
      return;
    }
    
    final int na = this.node[a];
    final int nb = this.node[b];

    // remove the node: the left child takes its place and the right subtree
    // is hung on the top of the right-most branch of the left child

    final int l = this.left[na];
    final int r = this.right[na];
    final int replacement;

    if (l == ArrayBTree.NONE)
    {
      replacement = r;
    }
    else
    {
      replacement = l;

      if (r != ArrayBTree.NONE)
      {
        int top = l;

        while (this.right[top] != ArrayBTree.NONE)
        {
          top = this.right[top];
        }

        this.setChild(top, false, r);
      }
    }

    final int pa = this.parent[na];

    if (pa == ArrayBTree.NONE)
    {
      // the root always has a child here, as there are at least two nodes
      
      this.root = replacement;
      this.parent[replacement] = ArrayBTree.NONE;
    }
    else
    {
      this.setChild(pa, this.left[pa] == na, replacement);
    }

    // insert the node as the child of the target

    final int child = toLeft
        ? this.left[nb]
        : this.right[nb];

    this.left[na] = ArrayBTree.NONE;
    this.right[na] = ArrayBTree.NONE;
    this.setChild(nb, toLeft, na);
    this.setChild(na, toLeft, child);
  }

  /**
   * Sets the child of the node (and the parent of the child).
   *
   * @param p parent node
   * @param toLeft set the left child
   * @param child child node (or NONE)
   */
  private void setChild(final int p, final boolean toLeft, final int child)
  {
    if (toLeft)
    {
      this.left[p] = child;
    }
    else
    {
      this.right[p] = child;
    }

    if (child != ArrayBTree.NONE)
    {
      this.parent[child] = p;
    }
  }

  /**
   * Checks if the node lies in the subtree (the subtree root included).
   *
   * @param subtree subtree root
   * @param tested tested node
   * @return TRUE if the node lies in the subtree
   */
  private boolean isInSubtree(final int subtree, final int tested)
  {
    for (int current = tested; current != ArrayBTree.NONE; current = this.parent[current])
    {
      if (current == subtree)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if two trees are equal (same structure, modules and rotations).
   *
   * @param other other tree
   * @return TRUE if the trees are equal
   */
  public boolean sameAs(final ArrayBTree other)
  {
    return (this.root == other.root)
        && Arrays.equals(this.left, other.left)
        && Arrays.equals(this.right, other.right)
        && Arrays.equals(this.module, other.module)
        && Arrays.equals(this.rotated, other.rotated);
  }
}
