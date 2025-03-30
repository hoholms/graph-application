package com.nbugaenco.graph.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
   * Each valid line in the file contains two integer values separated by a comma:
   * <ul>
   *   <li>The first integer is the {@code 'from'} node ID.</li>
   *   <li>The second integer is the {@code 'to'} node ID.</li>
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
        String[] parts = line.split(",");
        int from = Integer.parseInt(parts[0].trim());
        int to = Integer.parseInt(parts[1].trim());

        graph.putIfAbsent(from, new Node(from));
        graph.putIfAbsent(to, new Node(to));

        Node fromNode = graph.get(from);
        Node toNode = graph.get(to);

        fromNode.getEdges().add(new Edge(toNode));
        toNode.getEdges().add(new Edge(fromNode));
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("An error occurred while reading the file.");
    }

    return graph;
  }

}
