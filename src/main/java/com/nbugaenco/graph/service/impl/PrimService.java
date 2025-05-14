package com.nbugaenco.graph.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.nbugaenco.graph.model.Edge;
import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphService;

/**
 * Implementation of the GraphService interface using Prim's algorithm
 * to find the Minimum Spanning Tree (MST) of a connected, undirected graph.
 * <p>
 * Prim's algorithm is a greedy algorithm that finds an MST for a weighted undirected graph.
 * This means it finds a subset of the edges that forms a tree that includes every node,
 * where the total weight of all the edges in the tree is minimized.
 * <p>
 * The algorithm operates in steps:
 * <ol>
 * <li><b>Initialization:</b> Choose an arbitrary starting node from the graph and create an initial tree
 * containing only this node.</li>
 * <li><b>Edge Selection:</b> Select the edge from the graph with the minimum weight such that one of its endpoints
 * is a node already in the tree constructed in the previous step, and the other endpoint is not. The selected edge
 * is added to the tree (and the new node is simultaneously added).</li>
 * <li><b>Tree Construction:</b> This process continues until $n-1$ edges have been added, where $n$ is the total
 * number of vertices in the graph. At this point, the tree spans all vertices and is the MST.</li>
 * </ol>
 * <p>
 * The algorithm uses a priority queue to efficiently select the minimum-weight edge at each step.
 */
public class PrimService implements GraphService {

  /**
   * Processes the given graph to find its Minimum Spanning Tree (MST) using Prim's algorithm.
   *
   * @param graph
   *     A map representing the graph, where keys are node IDs and values are Node objects.
   *
   * @return A string detailing the MST edges and total weight, or an error message if the graph is empty
   * or potentially disconnected.
   */
  @Override
  public String process(final Map<Integer, Node> graph) {
    return graph
        .values()
        .stream()
        .findFirst()
        .map(PrimState::new)
        .map(primState -> buildMST(primState, graph.size()))
        .map(primState -> formatResult(primState, graph.size()))
        .orElse("Cannot find a starting node for Prim's algorithm");
  }

  /**
   * Builds the Minimum Spanning Tree (MST) using the core logic of Prim's algorithm.
   * <p>
   * The algorithm continues as long as there are edges in the priority queue
   * and the number of edges in the MST is less than {@code n-1} (where {@code n} is the total number of nodes).
   * In each step, it extracts the minimum-weight edge from the priority queue.
   * If the edge connects to a node not yet in the MST, the node and the edge are added to the MST,
   * and all edges incident to the newly added node are added to the priority queue.
   *
   * @param state
   *     The current state of the Prim's algorithm, including MST edges, visited nodes,
   *     priority queue, and current total weight.
   * @param totalNodes
   *     The total number of nodes in the graph.
   *
   * @return The updated state of the Prim's algorithm after constructing the MST.
   */
  private PrimState buildMST(PrimState state, int totalNodes) {
    // Building the tree: The process continues until n-1 edges are added.
    // The loop runs as long as there are potential edges to consider and the MST is not yet complete.
    while (!state.minPriorityQueue.isEmpty() && state.mstEdges.size() < totalNodes - 1) {
      // Edge Selection: Extract the edge with the minimum weight from the priority queue.
      // This edge is the next candidate to be added to the MST.
      Edge minEdge = state.minPriorityQueue.poll();

      // The node being considered for addition to the MST.
      Node destination = minEdge.getAdjacent();

      // Check if the destination node is already in the MST.
      // If it's not visited, adding this edge will not create a cycle and will expand the MST.
      if (!state.visitedNodes.contains(destination)) {
        state.visitedNodes.add(destination);
        state.mstEdges.add(minEdge);
        state.totalWeight += minEdge.getWeight();

        // Add all edges incident to the newly added destination node to the priority queue.
        // Only add edges that connect to nodes not yet in the MST.
        destination
            .getEdges()
            .stream()
            .filter(edge -> !state.visitedNodes.contains(edge.getAdjacent()))
            .forEach(state.minPriorityQueue::add);
      }
    }

    return state;
  }

  /**
   * Formats the final result string based on the state of the Prim's algorithm.
   * It checks if a complete MST was found (i.e., if $n-1$ edges were added).
   * If not, it indicates that the graph might be disconnected and reports the MST found for the component.
   * Otherwise, it provides a formatted string listing the MST edges and the total weight.
   *
   * @param state
   *     The final state of the Prim's algorithm.
   * @param totalNodes
   *     The total number of nodes in the graph.
   *
   * @return The formatted result string.
   */
  private String formatResult(PrimState state, int totalNodes) {
    StringBuilder resultBuilder = new StringBuilder();

    // Check if the number of MST edges is less than n-1 for a graph with more than one node.
    // This indicates that the graph might not be connected.
    if (state.mstEdges.size() != totalNodes - 1 && totalNodes > 1) {
      resultBuilder.append(("Graph might not be connected!%nNodes in MST: %d/%d%n%n")
          .formatted(state.visitedNodes.size(), totalNodes));
    }

    resultBuilder.append("Minimum Spanning Tree (Prim's Algorithm):\n");
    resultBuilder.append("Edges:\n");
    // Append each edge in the MST to the result string.
    state.mstEdges.forEach(edge -> resultBuilder.append(edge).append("\n"));
    // Append the total weight of the MST.
    resultBuilder.append("Total Weight: ").append(state.totalWeight);

    return resultBuilder.toString();
  }

  /**
   * A simple private static inner class to hold the state of the Prim's algorithm
   * during its execution. This helps in passing multiple related pieces of data
   * between methods efficiently.
   */
  private static class PrimState {

    /**
     * List of edges currently included in the MST.
     */
    List<Edge>          mstEdges;
    /**
     * Set of nodes that have been visited (added to the MST).
     */
    Set<Node>           visitedNodes;
    /**
     * Priority queue storing candidate edges to be added to the MST, ordered by weight.
     */
    PriorityQueue<Edge> minPriorityQueue;
    /**
     * Total weight of the MST.
     */
    int                 totalWeight;

    /**
     * Initializes the state for Prim's algorithm. This includes:
     * - An empty list to store the edges of the MST.
     * - A set to keep track of nodes already included in the MST.
     * - A priority queue to store edges connecting nodes in the MST to unvisited nodes, ordered by weight.
     * - The initial total weight of the MST (starts at 0).
     * The starting node is added to the visited set, and all its incident edges are added to the priority queue.
     *
     * @param startNode
     *     The node to start the algorithm from.
     */
    PrimState(final Node startNode) {
      this.mstEdges = new ArrayList<>();
      this.visitedNodes = new HashSet<>();
      this.minPriorityQueue = new PriorityQueue<>(Comparator.comparing(Edge::getWeight));
      this.totalWeight = 0;

      // Add the starting node to the visited set and its edges to the priority queue.
      visitedNodes.add(startNode);
      minPriorityQueue.addAll(startNode.getEdges());
    }

  }

}
