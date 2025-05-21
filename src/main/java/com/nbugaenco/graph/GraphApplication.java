package com.nbugaenco.graph;

import java.util.Map;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphServiceFactory;
import com.nbugaenco.graph.util.ReadGraphUtil;

/**
 * The main application class that demonstrates graph traversal using Breadth-First Search (BFS),
 * Depth-First Search (DFS), Bron-Kerbosch algorithm for finding maximal independent sets, and Prim's algorithm for
 * finding minimum spanning tree.
 * <p>
 * This class reads a graph from a file, selects an algorithm based on application arguments,
 * traverses the graph, and outputs the result.
 * </p>
 */
public class GraphApplication {

  /**
   * The entry point of the application.
   * <p>
   * This method performs the following:
   * <ul>
   *   <li>Parses command-line arguments to obtain the file path, start node, and search method.</li>
   *   <li>Reads the graph via {@link ReadGraphUtil#readGraph(ApplicationArguments)}.</li>
   *   <li>Selects one of the algorithms based on the parsed arguments.</li>
   *   <li>Traverses the graph and prints the result of the operation.</li>
   * </ul>
   * </p>
   *
   * @param args
   *     the command-line arguments (file path, start node ID, and optional search method)
   */
  public static void main(final String[] args) {
    ApplicationArguments arguments = ApplicationArguments.getInstance(args);
    Map<Integer, Node> graph = ReadGraphUtil.readGraph(arguments);
    GraphServiceFactory graphServiceFactory = new GraphServiceFactory();

    System.out.println(graphServiceFactory.getService(arguments.getOperation()).process(graph));
  }

}
