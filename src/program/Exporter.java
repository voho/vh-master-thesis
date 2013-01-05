package program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import module.PlacedModule;
import btree.BTree;
import btree.utility.Evaluation;
import btree.utility.Evaluator;

/**
 * Exporter utility class.
 * 
 * @author Vojtěch Hordějčuk
 */
public class Exporter
{
  /**
   * Creation is not allowed.
   */
  private Exporter()
  {
    // NOP
  }
  
  /**
   * Creates a script for GNUPLOT.
   * 
   * @param data input data
   * @param output output file
   * @throws IOException IO exception
   */
  public static void listToPlot(final List<?> data, final File output) throws IOException
  {
    final File temp = new File(output.getParentFile(), "data_" + output.getName());
    
    final StringBuilder buffer = new StringBuilder(1024);
    
    for (final Object row : data)
    {
      buffer.append(row.toString() + "\n");
    }
    
    final String script = String.format(
        "set encoding default\n" +
            "set terminal pdf\n" +
            "set output '%s'\n" +
            "set grid\n" +
            "set key left\n" +
            "set border 1\n" +
            "plot '%s' notitle with linespoints lt 3 lw 4 pt 7\n",
        output.getName() + ".pdf",
        temp.getName());
    
    final FileWriter writer1 = new FileWriter(temp);
    writer1.write(buffer.toString().trim());
    writer1.close();
    
    final FileWriter writer2 = new FileWriter(output);
    writer2.write(script.trim());
    writer2.close();
  }
  
  /**
   * Creates a CSV file of two columns from two lists.
   * 
   * @param col1 first column data
   * @param col2 second column data
   * @param file output file
   * @throws IOException IO exception
   */
  public static void listsToCsv(final List<?> col1, final List<?> col2, final File file) throws IOException
  {
    if ((col1.size() != col2.size()) || col1.isEmpty() || col2.isEmpty())
    {
      throw new IllegalArgumentException("Both lists must have equal size and be non-empty.");
    }
    
    final StringBuilder buffer = new StringBuilder(1024);
    
    for (int i = 0; i < col1.size(); i++)
    {
      buffer.append(String.format(
          "\"%s\",\"%s\"\n",
          String.valueOf(col1.get(i)).replace("\"", "\\\""),
          String.valueOf(col2.get(i)).replace("\"", "\\\"")));
    }
    
    final FileWriter writer1 = new FileWriter(file);
    writer1.write(buffer.toString());
    writer1.close();
  }
  
  /**
   * Creates a SVG image from the floorplan.
   * 
   * @param btree input B*-Tree
   * @param bones draw floorplan structure
   * @param output output file
   * @throws IOException IO exception
   */
  public static void treeToSvg(final BTree<PlacedModule> btree, final boolean bones, final File output) throws IOException
  {
    int min = -1;
    
    for (final BTree<PlacedModule> node : btree)
    {
      final int temp = Math.min(
          node.getValue().getModule().getWidth(),
          node.getValue().getModule().getHeight());
      
      if ((min == -1) || (temp < min))
      {
        min = temp;
      }
    }
    
    min = Math.max(1, (int) (min * 0.25));
    final double line1 = Math.max(1.0, min * 0.005);
    final double line2 = line1 * 2.0;
    
    final Evaluation e = Evaluator.evaluate(btree);
    final StringBuilder buffer = new StringBuilder(1024);
    
    buffer.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
    buffer.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n");
    buffer.append("\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
    
    buffer.append(String.format(
        "<svg width=\"%d\" height=\"%d\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">\n\n",
        e.getWidth(),
        e.getHeight()));
    
    buffer.append(String.format(
        "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"fill:rgb(%s);\" />\n",
        0,
        0,
        e.getWidth(),
        e.getHeight(),
        bones ? "180,180,180" : "30,30,30"));
    
    for (final BTree<PlacedModule> node : btree)
    {
      buffer.append(String.format(
            "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" style=\"fill:rgb(255,255,255);stroke-width:%.1f;stroke:rgb(120,120,120);\" />\n",
            node.getValue().getPosition().getX(),
            node.getValue().getPosition().getY(),
            node.getValue().getModule().getWidth(),
            node.getValue().getModule().getHeight(),
            line1));
    }
    
    if (bones)
    {
      for (final BTree<PlacedModule> node : btree)
      {
        if (node.hasLeft())
        {
          buffer.append(String.format(
              "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:rgb(255,20,20);stroke-width:%.1f;stroke-opacity:0.9;\" />\n",
              node.getValue().getCenterPosition().getX(),
              node.getValue().getCenterPosition().getY(),
              node.getLeft().getValue().getCenterPosition().getX(),
              node.getLeft().getValue().getCenterPosition().getY(),
              line2));
        }
        
        if (node.hasRight())
        {
          buffer.append(String.format(
              "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"stroke:rgb(20,20,255);stroke-width:%.1f;stroke-opacity:0.9;\" />\n",
              node.getValue().getCenterPosition().getX(),
              node.getValue().getCenterPosition().getY(),
              node.getRight().getValue().getCenterPosition().getX(),
              node.getRight().getValue().getCenterPosition().getY(),
              line2));
        }
      }
      
      for (final BTree<PlacedModule> node : btree)
      {
        buffer.append(String.format(
            "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" style=\"fill:rgb(30,30,30);\" />\n",
            node.getValue().getCenterPosition().getX(),
            node.getValue().getCenterPosition().getY(),
            min));
      }
    }
    
    buffer.append("</svg>");
    
    final FileWriter writer = new FileWriter(output);
    writer.write(buffer.toString());
    writer.close();
  }
  
  /**
   * Creates a DOT script for drawing the given B*-Tree.
   * 
   * @param btree input B*-Tree
   * @param output output file
   * @throws IOException IO exception
   */
  public static void treeToDot(final BTree<?> btree, final File output) throws IOException
  {
    final StringBuilder buffer = new StringBuilder(1024);
    
    buffer.append("digraph {\n");
    
    for (final BTree<?> node : btree)
    {
      buffer.append(String.format(
          "\"%s\";\n",
          node.getValue().toString()));
      
      if (node.hasLeft())
      {
        buffer.append(String.format(
            "\"%s\" -> \"%s\";\n",
            node.getValue().toString(),
            node.getLeft().getValue().toString()));
      }
      
      if (node.hasRight())
      {
        buffer.append(String.format(
            "\"%s\" -> \"%s\";\n",
            node.getValue().toString(),
            node.getRight().getValue().toString()));
      }
    }
    
    buffer.append("}");
    
    final FileWriter writer = new FileWriter(output);
    writer.write(buffer.toString());
    writer.close();
  }
  
  /**
   * Dumps statistics to a file.
   * 
   * @param result evaluation
   * @param output output file
   * @throws IOException IO exception
   */
  public static void evaluationToTxt(final Evaluation result, final File output) throws IOException
  {
    final StringBuilder buffer = new StringBuilder(1024);
    
    buffer.append(String.format("Width: %d\n", result.getWidth()));
    buffer.append(String.format("Height: %d\n", result.getHeight()));
    buffer.append(String.format("Area: %d\n", result.getTotalArea()));
    buffer.append(String.format("Used area: %d\n", result.getUsedArea()));
    buffer.append(String.format("Unused area: %d\n", result.getUnusedArea()));
    buffer.append(String.format("Unused area: %.3f\n", result.getRelativeUnusedArea()));
    buffer.append(String.format("Perimeter: %d\n\n", result.getPerimeter()));
    buffer.append(String.format("Fitness: %d", result.getFitness()));
    
    final FileWriter writer = new FileWriter(output);
    writer.write(buffer.toString());
    writer.close();
  }
}
