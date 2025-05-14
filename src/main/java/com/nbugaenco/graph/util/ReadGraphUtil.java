package com.nbugaenco.graph.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Edge;
import com.nbugaenco.graph.model.Node;
import lombok.experimental.UtilityClass;

/**
 * Utility class for reading a graph from a file and building adjacency relationships.
 *
 * <p>The file is expected to have multiple lines, each containing two integer values separated
 * by a comma. The first column (from) is the source node ID. The second column (to) is the
 * destination node ID. Each line describes an undirected edge between two nodes.</p>
 *
 * <p>For example, an input file might look like:</p>
 * <pre>{@code
 * 1,2
 * 1,4
 * 2,3
 * 4,5
 * }</pre>
 *
 * <p>This indicates edges between nodes <b>1</b> and <b>2</b>, <b>1</b> and <b>4</b>, <b>2</b> and <b>3</b>, and
 * <b>4</b> and <b>5</b>
 * .</p>
 *
 * <pre>{@code
 * ┌─┐    ┌─┐    ┌─┐
 * │1┼────┼2┼────┼3│
 * └┬┘    └─┘    └─┘
 *  │
 * ┌┴┐    ┌─┐
 * │4├────┼5│
 * └─┘    └─┘
 * }</pre>
 */
@UtilityClass
public class ReadGraphUtil {

  /**
   * Reads a graph from the file specified in the application arguments.
   * <p>
   * Each valid line in the file is expected to contain two or three comma-separated values:
   * <ul>
   *   <li>The first value is the ID of the {@code from} node.</li>
   *   <li>The second value is the ID of the {@code to} node.</li>
   *   <li>The third value (optional) is the {@code weight} of the edge. If not provided, the default weight is 1.</li>
   * </ul>
   * This method will add the nodes into the map if they do not exist, and
   * create an undirected edge relationship between them.
   * </p>
   *
   * @param arguments
   *     the application arguments containing the file path
   *
   * @return a map representing the graph, where the key is the node ID and the value is the node
   *
   * @throws IllegalArgumentException
   *     if an error occurs while reading the file or parsing the IDs
   */
  public static Map<Integer, Node> readGraph(final ApplicationArguments arguments) {
    Map<Integer, Node> graph = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(arguments.getFilePath()))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.isBlank() || line.startsWith("//")) { // Add comment support
          continue;
        }

        parseLine(graph, line);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("An error occurred while reading the file: " + arguments.getFilePath(), e);
    }

    return graph;
  }

  /**
   * Parses a line from the input file and updates the graph with the nodes and edges described in the line.
   * <p>
   * The line is expected to contain two or three comma-separated values:
   * <ul>
   *   <li>The first value is the ID of the {@code from} node.</li>
   *   <li>The second value is the ID of the {@code to} node.</li>
   *   <li>The third value (optional) is the {@code weight} of the edge. If not provided, the default weight is 1.</li>
   * </ul>
   * This method ensures that both nodes exist in the graph and creates an undirected edge between them.
   * </p>
   *
   * @param graph
   *     the graph map where the key is the node ID and the value is the {@link Node} object
   * @param line
   *     the line from the input file to parse
   *
   * @throws IllegalArgumentException
   *     if the line contains non-integer values or is malformed
   */
  private static void parseLine(final Map<Integer, Node> graph, final String line) {
    try {
      String[] parts = splitLine(line);

      int from = Integer.parseInt(parts[0].trim());
      int to = Integer.parseInt(parts[1].trim());
      int weight = parts.length == 3 ? Integer.parseInt(parts[2].trim()) : 1; // Default weight is 1

      graph.putIfAbsent(from, new Node(from));
      graph.putIfAbsent(to, new Node(to));

      Node fromNode = graph.get(from);
      Node toNode = graph.get(to);

      fromNode.getEdges().add(new Edge(fromNode, toNode, weight));
      toNode.getEdges().add(new Edge(toNode, fromNode, weight));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Malformed line with non-integer node IDs or weight: " + line);
    }
  }

  /**
   * Splits a line from the input file into its components and validates the format.
   * <p>
   * The line is expected to contain two or three comma-separated values:
   * <ul>
   *   <li>The first value is the ID of the {@code from} node.</li>
   *   <li>The second value is the ID of the {@code to} node.</li>
   *   <li>The third value (optional) is the {@code weight} of the edge.</li>
   * </ul>
   * </p>
   *
   * @param line
   *     the line from the input file to split
   *
   * @return an array of strings containing the split components of the line
   *
   * @throws IllegalArgumentException
   *     if the line does not contain at least two or at most three parts
   */
  private static String[] splitLine(final String line) {
    String[] parts = line.split(",");
    if (parts.length < 2) { // At least two parts are required: from and to; weight is optional
      throw new IllegalArgumentException("Malformed line (expected at least two parts 'node1,node2,weight'): " + line);
    }
    if (parts.length > 3) { // More than three parts is invalid
      throw new IllegalArgumentException("Malformed line (expected at most three parts 'node1,node2,weight'): " + line);
    }
    return parts;
  }

}
