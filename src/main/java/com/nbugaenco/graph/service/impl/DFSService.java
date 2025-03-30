package com.nbugaenco.graph.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.nbugaenco.graph.model.ApplicationArguments;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphService;
import com.nbugaenco.graph.util.CommonGraphUtil;

/**
 * Service class for performing Depth-First Search (DFS) on a graph.
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
 */
public class DFSService implements GraphService {

  /**
   * Processes the graph and returns the DFS traversal sequence as a string.
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   *
   * @return a string representing the sequence of node IDs in DFS order
   */
  @Override
  public String process(final Map<Integer, Node> graph) {
    final ApplicationArguments applicationArguments = ApplicationArguments.getInstance();

    return CommonGraphUtil.toNodesSequence(dfs(graph, applicationArguments.getStartNodeId()));
  }

  /**
   * Performs a <b>Depth-First Search (DFS)</b> starting from the node with the given ID.
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   * @param startNodeId
   *     the start node's id
   *
   * @return a list of visited nodes in DFS order, or an empty list if the start node is invalid
   */
  public List<Node> dfs(final Map<Integer, Node> graph, final int startNodeId) {
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
  private List<Node> dfs(final Node start, final Set<Node> visited, final List<Node> result) {
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
