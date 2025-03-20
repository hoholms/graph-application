package com.nbugaenco.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
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
import com.nbugaenco.graph.util.GraphUtil;

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
 * <p>
 *   Using the Bron-Kerbosch algorithm, the output could be the largest independent set found,
 *   such as {@code {3, 6, 8}}.
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

    String result = "";
    switch (arguments.getSearchMethod()) {
      case BFS -> result = bfs(graph, arguments.getStartNodeId())
          .stream()
          .map(Node::getId)
          .map(Object::toString)
          .collect(Collectors.joining(" -> "));
      case DFS -> result = dfs(graph, arguments.getStartNodeId())
          .stream()
          .map(Node::getId)
          .map(Object::toString)
          .collect(Collectors.joining(" -> "));
      case BK -> {
        Collection<Node> res = bronKerbosch(graph);
        result = "Largest independent set of size (%d) and elements: %s".formatted(res.size(), res
            .stream()
            .map(Node::getId)
            .sorted()
            .map(Object::toString)
            .collect(Collectors.joining(", ")));
      }
    }

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
   *   <li>Start with the starting node x, which is currently the current node.</li>
   *   <li>Vertex x is visited. Its first unvisited neighbor y is determined and becomes the current node.</li>
   *   <li>Then the first unvisited neighbor of y is visited, and so on, going deeper until we reach a node that
   *   has no more unvisited neighbors. When we reach such a node, we return to its "parent" node - the one we
   *   came from.</li>
   *   <li>If this node still has unvisited neighbors, we choose the next unvisited one and continue the
   *   traversal in the same way.</li>
   *   <li>If the node has no more unvisited neighbors, we return to its parent node and continue the same way
   *   until all nodes reachable from the starting node have been visited.</li>
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

  /**
   * Implementation of the Bron-Kerbosch algorithm to find all maximal independent sets in a graph.
   * Returns a set containing the largest maximal independent set found.
   * <p>
   * The Bron-Kerbosch algorithm works recursively to expand an independent set step by step.
   * It maintains three sets:
   * <ul>
   *     <li><b>S</b> – the independent set being constructed at the current recursion step.</li>
   *     <li><b>Q⁺</b> – the set of nodes that can extend S.</li>
   *     <li><b>Q⁻</b> – the set of nodes that have already been used to extend S in previous recursion steps.</li>
   * </ul>
   * The algorithm starts with an empty independent set: S = ∅, Q⁺ = X (all nodes).
   * <p>
   * The recursive procedure BronKerbosch(Q⁺, Q⁻) executes as follows:
   * <ol>
   *     <li>While Q⁺ is not empty and there are no nodes in Q⁻ that are disconnected from all of Q⁺:</li>
   *     <li>Select a node v from Q⁺ and add it to S.</li>
   *     <li>Construct Q⁺_new and Q⁻_new by removing nodes connected to v from Q⁺ and Q⁻.</li>
   *     <li>If both Q⁺_new and Q⁻_new are empty, S is a maximal independent set.</li>
   *     <li>Otherwise, recursively call BronKerbosch(Q⁺_new, Q⁻_new).</li>
   *     <li>Remove v from S and Q⁺, then continue.</li>
   * </ol>
   *
   * @param graph
   *     A map representing the graph, where the key is the node identifier and the value is a Node object.
   *
   * @return A set containing the largest maximal independent set.
   */
  private static List<Node> bronKerbosch(final Map<Integer, Node> graph) {
    return bronKerbosch(new HashSet<>(), new ArrayDeque<>(graph.values()), new HashSet<>(), new HashSet<>())
        .stream()
        .toList();
  }

  /**
   * Recursive helper method for the Bron-Kerbosch algorithm.
   *
   * @param independent
   *     The current independent set being constructed.
   * @param candidates
   *     The queue of candidate nodes to be added to the independent set.
   * @param excluded
   *     The set of nodes that have been excluded from the current independent set.
   * @param result
   *     The set containing the largest maximal independent set found so far.
   *
   * @return The set containing the largest maximal independent set.
   */
  private static Set<Node> bronKerbosch(final Set<Node> independent, final Queue<Node> candidates,
      final Set<Node> excluded, final Set<Node> result) {
    // Continue exploring while there are candidates and all excluded nodes
    // still have at least one adjacent node in the candidate set (complete adjacency).
    while (!candidates.isEmpty() && hasCompleteAdjacency(excluded, candidates)) {
      // Pick the next candidate to add to the independent set.
      final Node current = candidates.poll();
      independent.add(current);

      // Build a new list of candidates by removing nodes adjacent to the current node (including current).
      final Queue<Node> newCandidates = new ArrayDeque<>(candidates)
          .stream()
          .filter(node -> !node.isAdjacent(current))
          .collect(Collectors.toCollection(ArrayDeque::new));

      // Create a new excluded set to track nodes that cannot be in this independent set.
      final Set<Node> newExcluded = new HashSet<>(excluded)
          .stream()
          .filter(node -> !node.isAdjacent(current) && !node.equals(current))
          .collect(Collectors.toSet());

      // If there are no new candidates or excluded nodes, check if this set is larger
      // than the previously recorded best result and update it if so.
      if (newCandidates.isEmpty() && newExcluded.isEmpty()) {
        if (independent.size() > result.size()) {
          result.clear();
          result.addAll(independent);
        }
      } else {
        // Recursively explore adding the next candidate.
        bronKerbosch(independent, newCandidates, newExcluded, result);
      }

      // Remove the current node from the independent set
      // and move it to excluded, so we don't process it again in this branch.
      independent.remove(current);
      excluded.add(current);
    }

    // Return the recorded best result (largest independent set) found so far.
    return result;
  }

  /**
   * Checks if all nodes in the first collection have at least one adjacent node in the second collection.
   *
   * @param nodes1
   *     The first collection of nodes.
   * @param nodes2
   *     The second collection of nodes.
   *
   * @return True if all nodes in the first collection have at least one adjacent node in the second collection, false
   * otherwise.
   */
  private static boolean hasCompleteAdjacency(final Collection<Node> nodes1, final Collection<Node> nodes2) {
    return nodes1.stream().allMatch(node -> nodes2.stream().anyMatch(node::isAdjacent));
  }

}
