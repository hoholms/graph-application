package com.nbugaenco.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Edge;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.model.SearchMethod;
import com.nbugaenco.graph.util.GraphUtil;

/**
 * The main application class that demonstrates graph traversal using Breadth-First Search (BFS)
 * and Depth-First Search (DFS).
 * <p>
 * This class reads a graph from a file, selects a search algorithm based on application arguments,
 * traverses the graph, and outputs the chain of visited node IDs.
 * </p>
 * <p>
 * <b>Algorithm Overview:</b>
 * <ul>
 *   <li>
 *     For DFS:
 *     <ol>
 *       <li>Start with the starting vertex x, which is currently the current vertex.</li>
 *       <li>Vertex x is visited. Its first unvisited neighbor y is determined and becomes the current vertex.</li>
 *       <li>Then the first unvisited neighbor of y is visited, and so on, going deeper until we reach a vertex that
 *       has no more unvisited neighbors. When we reach such a vertex, we return to its "parent" vertex - the one we
 *       came from.</li>
 *       <li>If this vertex still has unvisited neighbors, we choose the next unvisited one and continue the
 *       traversal in the same way.</li>
 *       <li>If the vertex has no more unvisited neighbors, we return to its parent vertex and continue the same way
 *       until all vertices reachable from the starting vertex have been visited.</li>
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
 * </ul>
 * </p>
 * <p>
 * <b>Example:</b><br/>
 * Given a graph defined in {@code graph.txt} as:
 * <pre>{@code
 * 1,2
 * 1,4
 * 2,3
 * 2,5
 * 4,5
 * 4,6
 * 5,7
 * 7,8
 * 7,9
 * 8,9
 * }</pre>
 * If the application is executed with start node 1 and BFS as the search method, the output
 * could be a chain like {@code 1 -> 2 -> 4 -> 3 -> 5 -> 6 -> 7 -> 8 -> 9}.
 * <p>
 *   With DFS, the output could be {@code 1 -> 2 -> 3 -> 5 -> 4 -> 6 -> 7 -> 8 -> 9}.
 * </p>
 * </p>
 */
public class GraphApplication {

  /**
   * The entry point of the application.
   * <p>
   * This method performs the following:
   * <ul>
   *   <li>Parses command-line arguments to obtain the file path, start node, and search method.</li>
   *   <li>Reads the graph via {@link com.nbugaenco.graph.util.GraphUtil#readGraph(ApplicationArguments)}.</li>
   *   <li>Selects BFS or DFS based on the parsed search method.</li>
   *   <li>Traverses the graph and prints the visited nodes chain.</li>
   * </ul>
   * </p>
   *
   * @param args
   *     the command-line arguments (file path, start node ID, and optional search method)
   */
  public static void main(final String[] args) {
    ApplicationArguments arguments = ApplicationArguments.getInstance(args);
    Map<Integer, Node> graph = GraphUtil.readGraph(ApplicationArguments.getInstance(args));

    String result = Optional
        .of(arguments)
        .map(ApplicationArguments::getSearchMethod)
        .filter(SearchMethod.BFS::equals)
        .map(searchMethod -> bfs(graph, arguments.getStartNodeId()))
        .orElseGet(() -> dfs(graph, arguments.getStartNodeId()))
        .stream()
        .map(Node::getId)
        .map(Object::toString)
        .collect(Collectors.joining(" -> "));

    System.out.println(result);
  }

  /**
   * Performs a <b>Breadth-First Search (BFS)</b> starting from the node with the given ID.
   * <p>
   * The algorithm:
   * <ol>
   *   <li>Add the initial node to the queue and mark it as visited.</li>
   *   <li>While the queue is not empty:
   *     <ol>
   *       <li>Remove the element from the queue.</li>
   *       <li>Determine its unvisited neighbors, mark them as visited, and add them to the queue.</li>
   *       <li>Remove the processed element from the queue.</li>
   *     </ol>
   *   </li>
   * </ol>
   * </p>
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   * @param startNodeId
   *     the start node's id
   *
   * @return a list of visited nodes in BFS order
   */
  private static List<Node> bfs(final Map<Integer, Node> graph, final int startNodeId) {
    List<Node> result = new ArrayList<>();
    Queue<Node> queue = new ArrayDeque<>();
    Set<Node> visited = new HashSet<>();

    Optional
        .of(startNodeId)
        .map(graph::get)
        .filter(visited::add)
        .filter(queue::add)
        .ifPresent(node -> bfs(queue, result, visited));

    return result;
  }

  /**
   * A helper method for the BFS algorithm.
   * <p>
   * This method continues to process the queue by:
   * <ol>
   *   <li>Polling the next node from the queue.</li>
   *   <li>Recording it in the result list.</li>
   *   <li>Enqueueing each unvisited adjacent node.</li>
   * </ol>
   * </p>
   *
   * @param queue
   *     the BFS queue containing nodes to be processed
   * @param result
   *     the list that collects visited nodes
   * @param visited
   *     the set used to keep track of visited nodes
   */
  private static void bfs(final Queue<Node> queue, final List<Node> result, final Set<Node> visited) {
    while (!queue.isEmpty()) {
      Optional
          .of(queue)
          .map(Queue::poll)
          .filter(result::add)
          .map(Node::getEdges)
          .stream()
          .flatMap(Set::stream)
          .map(Edge::getAdjacent)
          .filter(visited::add)
          .forEach(queue::add);
    }
  }

  /**
   * Performs a <b>Depth-First Search (DFS)</b> starting from the node with the given ID.
   * <p>
   * The DFS algorithm:
   * <ol>
   *   <li>Start with the starting vertex x, which is currently the current vertex.</li>
   *   <li>Vertex x is visited. Its first unvisited neighbor y is determined and becomes the current vertex.</li>
   *   <li>Then the first unvisited neighbor of y is visited, and so on, going deeper until we reach a vertex that
   *   has no more unvisited neighbors. When we reach such a vertex, we return to its "parent" vertex - the one we
   *   came from.</li>
   *   <li>If this vertex still has unvisited neighbors, we choose the next unvisited one and continue the
   *   traversal in the same way.</li>
   *   <li>If the vertex has no more unvisited neighbors, we return to its parent vertex and continue the same way
   *   until all vertices reachable from the starting vertex have been visited.</li>
   * </ol>
   * </p>
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   * @param startNodeId
   *     the start node's id
   *
   * @return a list of visited nodes in DFS order, or an empty list if the start node is invalid
   */
  private static List<Node> dfs(final Map<Integer, Node> graph, final int startNodeId) {
    return Optional
        .of(startNodeId)
        .map(graph::get)
        .map(node -> dfs(node, new HashSet<>(), new ArrayList<>()))
        .orElseGet(Collections::emptyList);
  }

  /**
   * A helper method for the DFS algorithm that recursively visits nodes.
   * <p>
   * The method:
   * <ol>
   *   <li>Marks the current node as visited and adds it to the result list.</li>
   *   <li>Recursively processes each adjacent node that has not been visited.</li>
   *   <li>Returns the updated result list after processing adjacent nodes.</li>
   * </ol>
   * </p>
   *
   * @param start
   *     the current node being processed
   * @param visited
   *     the set of already visited nodes
   * @param result
   *     the list that collects visited nodes
   *
   * @return the list of visited nodes after processing the current node and its neighbors
   */
  private static List<Node> dfs(final Node start, final Set<Node> visited, final List<Node> result) {
    Optional
        .of(start)
        .filter(Predicate.not(visited::contains))
        .filter(visited::add)
        .filter(result::add)
        .map(Node::getEdges)
        .ifPresent(edges -> edges.forEach(edge -> dfs(edge.getAdjacent(), visited, result)));

    return result;
  }

}
