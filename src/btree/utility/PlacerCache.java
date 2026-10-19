package btree.utility;

import java.util.Arrays;

import module.Position;

/**
 * Placer cache used for faster searching for the Y coordinate. It keeps the
 * contour (skyline) of already placed items as a linked list of horizontal
 * segments sorted from left to right. Each segment stores the top-most Y
 * coordinate (level) of the items placed above its interval.
 *
 * @author Vojtěch Hordějčuk
 */
public class PlacerCache
{
  /**
   * segment start X coordinates
   */
  private int[] start;
  /**
   * segment end X coordinates (exclusive)
   */
  private int[] end;
  /**
   * segment levels (top-most Y coordinates)
   */
  private int[] level;
  /**
   * indices of the following segments (-1 = last segment)
   */
  private int[] next;
  /**
   * number of segments allocated
   */
  private int size;

  /**
   * Creates a new instance.
   */
  public PlacerCache()
  {
    this(16);
  }

  /**
   * Creates a new instance.
   *
   * @param capacity expected number of segments
   */
  public PlacerCache(final int capacity)
  {
    this.start = new int[Math.max(2, capacity)];
    this.end = new int[this.start.length];
    this.level = new int[this.start.length];
    this.next = new int[this.start.length];
    this.size = 0;

    // one infinite segment at the ground level

    this.allocate(0, Integer.MAX_VALUE, 0, -1);
  }

  /**
   * Places the item defined by the parameters.
   *
   * @param x item X coordinate
   * @param width item width
   * @param height item height
   * @return item position
   */
  protected Position place(final int x, final int width, final int height)
  {
    return new Position(x, this.findY(x, width, height));
  }

  /**
   * Finds Y coordinate for the item defined by the parameters.
   *
   * @param x item X coordinate
   * @param width item width
   * @param height item height
   * @return item Y coordinate
   */
  public int findY(final int x, final int width, final int height)
  {
    int segment = this.getFirst();

    while (this.end[segment] <= x)
    {
      segment = this.next[segment];
    }

    return this.getLevel(this.place(segment, x, width, height)) - height;
  }

  /**
   * Places the item defined by the parameters. The search starts at the given
   * segment which must contain the item X coordinate. When placing B*-Tree
   * nodes in DFS order, this is the segment of the parent (right child) or the
   * segment following it (left child), so no searching is necessary.
   *
   * @param first segment containing the item X coordinate
   * @param x item X coordinate
   * @param width item width
   * @param height item height
   * @return segment of the placed item (its level is the item top)
   */
  public int place(final int first, final int x, final int width, final int height)
  {
    int segment = first;

    if (this.start[segment] < x)
    {
      // split the segment, so the item starts at the segment start

      final int split = this.allocate(x, this.end[segment], this.level[segment], this.next[segment]);
      this.end[segment] = x;
      this.next[segment] = split;
      segment = split;
    }

    // walk all segments overlapping the item and find the highest one

    final int right = x + width;
    int y = 0;
    int after = this.next[segment];
    int current = segment;

    while ((current != -1) && (this.start[current] < right))
    {
      y = Math.max(y, this.level[current]);

      if (this.end[current] > right)
      {
        // keep the part of the segment which is not covered

        after = this.allocate(right, this.end[current], this.level[current], this.next[current]);
        break;
      }

      current = this.next[current];
      after = current;
    }

    // the segment is reused for the placed item

    this.end[segment] = right;
    this.level[segment] = y + height;
    this.next[segment] = after;

    return segment;
  }

  /**
   * Returns the first (left-most) segment.
   *
   * @return the first segment
   */
  public int getFirst()
  {
    return 0;
  }

  /**
   * Returns the segment following the given one.
   *
   * @param segment segment
   * @return the following segment
   */
  public int getNext(final int segment)
  {
    return this.next[segment];
  }

  /**
   * Returns the segment level (the top-most Y coordinate).
   *
   * @param segment segment
   * @return the segment level
   */
  public int getLevel(final int segment)
  {
    return this.level[segment];
  }

  /**
   * Allocates a new segment.
   *
   * @param x1 segment start
   * @param x2 segment end
   * @param y segment level
   * @param following following segment
   * @return the new segment
   */
  private int allocate(final int x1, final int x2, final int y, final int following)
  {
    if (this.size == this.start.length)
    {
      final int capacity = this.size * 2;
      this.start = Arrays.copyOf(this.start, capacity);
      this.end = Arrays.copyOf(this.end, capacity);
      this.level = Arrays.copyOf(this.level, capacity);
      this.next = Arrays.copyOf(this.next, capacity);
    }

    this.start[this.size] = x1;
    this.end[this.size] = x2;
    this.level[this.size] = y;
    this.next[this.size] = following;

    return this.size++;
  }
}
