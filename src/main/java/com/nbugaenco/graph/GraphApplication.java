package com.nbugaenco.graph;

import java.util.Map;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphServiceFactory;
import com.nbugaenco.graph.util.ReadGraphUtil;

/**
 * The main application class that demonstrates graph traversal using Breadth-First Search (BFS),
 * Depth-First Search (DFS), and the Bron-Kerbosch algorithm for finding maximal independent sets.
 * <p>
 * This class reads a graph from a file, selects a search algorithm based on application arguments,
 * traverses the graph, and outputs the result.
 * </p>
 * <p>
 * <b>Algorithm Overview:</b>
 * <ul>
 *   <li>
 *     For DFS:
 *     <ol>
 *       <li>Start with the starting node x, which is currently the current node.</li>
 *       <li>Vertex x is visited. Its first unvisited neighbor y is determined and becomes the current node.</li>
 *       <li>Then the first unvisited neighbor of y is visited, and so on, going deeper until we reach a node that
 *       has no more unvisited neighbors. When we reach such a node, we return to its "parent" node - the one we
 *       came from.</li>
 *       <li>If this node still has unvisited neighbors, we choose the next unvisited one and continue the
 *       traversal in the same way.</li>
 *       <li>If the node has no more unvisited neighbors, we return to its parent node and continue the same way
 *       until all nodes reachable from the starting node have been visited.</li>
 *     </ol>
 *   </li>
 *   <li>
 *     For BFS:
 *     <ol>
 *       <li>Add the initial node to the queue and mark it as visited.</li>
 *       <li>While the queue is not empty:
 *         <ol>
 *           <li>Remove the element from the queue.</li>
 *           <li>Determine its unvisited neighbors, mark them as visited, and add them to the queue.</li>
 *           <li>Remove the processed element from the queue.</li>
 *         </ol>
 *       </li>
 *     </ol>
 *   </li>
 *   <li>
 *     For Bron-Kerbosch:
 *     <ol>
 *       <li>The algorithm finds the largest maximal independent set of a graph.</li>
 *       <li>It recursively expands an independent set while maintaining a candidate set and an excluded set.</li>
 *       <li>When no more expansions are possible, the largest recorded independent set is returned.</li>
 *     </ol>
 *   </li>
 * </ul>
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
   *   <li>Selects BFS, DFS or BK based on the parsed search method.</li>
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
