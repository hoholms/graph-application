package com.nbugaenco.graph.service.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphService;
import com.nbugaenco.graph.util.CommonGraphUtil;

/**
 * Service class for performing Breadth-First Search (BFS) on a graph.
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
 */
public class BFSService implements GraphService {

  /**
   * Processes the graph and returns the BFS traversal sequence as a string.
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   *
   * @return a string representing the sequence of node IDs in DFS order
   */
  @Override
  public String process(final Map<Integer, Node> graph) {
    final ApplicationArguments applicationArguments = ApplicationArguments.getInstance();

    return CommonGraphUtil.toNodesSequence(bfs(graph, applicationArguments.getStartNodeId()));
  }

  /**
   * Performs a <b>Breadth-First Search (BFS)</b> starting from the node with the given ID.
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   * @param startNodeId
   *     the start node's id
   *
   * @return a list of visited nodes in BFS order
   */
  public List<Node> bfs(final Map<Integer, Node> graph, final int startNodeId) {
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
  private void bfs(final Queue<Node> queue, final List<Node> result, final Set<Node> visited) {
    while (!queue.isEmpty()) {
      Optional
          .of(queue)
          .map(Queue::poll)
          .filter(result::add)
          .ifPresent(node -> node
              .getEdges()
              .stream()
              .map(edge -> edge.getAdjacent(node))
              .filter(visited::add)
              .forEach(queue::add));
    }
  }

}
