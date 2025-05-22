package com.nbugaenco.graph.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphService;
import com.nbugaenco.graph.util.CommonGraphUtil;

/**
 * Service class for graph coloring using the DSatur algorithm.
 * <p>
 * DSatur (Degree of Saturation) is a heuristic graph coloring algorithm.
 * It iteratively selects the node with the highest saturation degree to color next.
 * The saturation degree of a node is the number of distinct colors in its neighborhood.
 * <p>
 * The algorithm operates as follows:
 * <ol>
 *   <li><b>Initialization:</b> All nodes are initially uncolored.
 *       The color map is empty.</li>
 *   <li><b>Node Selection:</b> While there are uncolored nodes:
 *     <ol>
 *       <li>Select the uncolored node with the highest saturation degree.</li>
 *       <li>If there's a tie in saturation degree, select the node with the highest degree
 *           in the subgraph of uncolored nodes.</li>
 *       <li>If there's still a tie, select the node with the smallest ID (for deterministic behavior).</li>
 *     </ol>
 *   </li>
 *   <li><b>Color Assignment:</b> Assign the selected node the smallest possible color
 *       (i.e., the smallest positive integer) that is not used by any of its already colored neighbors.</li>
 *   <li><b>Termination:</b> The algorithm terminates when all nodes have been colored.</li>
 * </ol>
 * The result includes the color assigned to each node and the total number of distinct colors used.
 */
public class DSaturService implements GraphService {

  public static final String RESET = "\u001B[0m";

  public static final List<String> COLOR_CODES = List.of("\u001B[31m", // 1 - Red
      "\u001B[32m", // 2 - Green
      "\u001B[33m", // 3 - Yellow
      "\u001B[34m", // 4 - Blue
      "\u001B[35m", // 5 - Magenta
      "\u001B[36m"  // 6 - Cyan
  );

  /**
   * Processes the given graph to color its nodes using the DSatur algorithm.
   * <p>
   * This method orchestrates the coloring process by calling the {@code dSatur} method
   * to perform the algorithm and then the {@code formatResult} method to present the outcome.
   * If the input graph is null or empty, it returns a message indicating that coloring is not possible.
   *
   * @param graph
   *     A map representing the graph, where keys are node IDs and values are {@link Node} objects.
   *     The graph should not be null.
   *
   * @return A string detailing the node colors and the total number of colors used,
   * or an error message if the graph is empty or if the algorithm encounters an issue
   * (e.g., cannot select a node when uncolored nodes still exist, though this is unlikely with a valid graph).
   */
  @Override
  public String process(final Map<Integer, Node> graph) {
    return Optional
        .of(graph)
        .map(this::dSatur)
        .map(this::formatResult)
        .orElse("Cannot find a starting node for DSatur algorithm.");
  }

  /**
   * Applies the DSatur algorithm to color the graph.
   * <p>
   * It initializes a map to store colors for each node and a set of uncolored nodes.
   * The algorithm proceeds by iteratively selecting the next node to color based on DSatur criteria
   * (highest saturation degree, then highest degree in the uncolored subgraph) and assigning it
   * the smallest available color not used by its colored neighbors.
   *
   * @param graph
   *     The graph to be colored, represented as a map of node IDs to {@link Node} objects.
   *
   * @return A map where keys are {@link Node} objects and values are their assigned colors (integers).
   */
  public Map<Node, Integer> dSatur(final Map<Integer, Node> graph) {
    Map<Node, Integer> colors = new HashMap<>();
    Set<Node> uncoloredNodes = new HashSet<>(graph.values());

    while (!uncoloredNodes.isEmpty()) {
      Node nodeToColor = selectNextNode(uncoloredNodes, colors);

      if (nodeToColor == null) {
        break;
      }

      int smallestAvailableColor = findSmallestAvailableColor(nodeToColor, colors);
      colors.put(nodeToColor, smallestAvailableColor);
      uncoloredNodes.remove(nodeToColor);
    }

    return colors;
  }

  /**
   * Formats the result of the DSatur coloring into a human-readable string.
   * <p>
   * The output string lists each node and its assigned color, sorted by node ID.
   * It also includes the total number of distinct colors used in the coloring.
   * If the color map is empty (e.g., an empty graph was processed), it reflects this.
   *
   * @param colors
   *     A map where keys are {@link Node} objects and values are their assigned colors.
   *
   * @return A formatted string representing the graph coloring result.
   */
  private String formatResult(final Map<Node, Integer> colors) {
    String coloringResult = colors
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().getId()))
        .map(entry -> {
          int colorIdx = Math.clamp(entry.getValue(), 1, COLOR_CODES.size()) - 1;
          String colorCode = COLOR_CODES.get(colorIdx);
          return "Node %d: %sColor %d%s".formatted(entry.getKey().getId(), colorCode, entry.getValue(), RESET);
        })
        .collect(Collectors.joining("\n"));

    final OptionalInt maxColorsUsed = colors.values().stream().mapToInt(i -> i).max();

    return "Graph Coloring Result (DSatur Algorithm):%n%s%n%nTotal colors used: %d".formatted(coloringResult,
        maxColorsUsed.isPresent()
            ? maxColorsUsed.getAsInt()
            : 0);
  }

  /**
   * Selects the next node to color based on DSatur criteria.
   * <p>
   * The selection priority is as follows:
   * <ol>
   *   <li>The node with the highest saturation degree (number of distinct colors among its already colored
   *   neighbors).</li>
   *   <li>If there's a tie, the node with the highest degree in the subgraph of currently uncolored nodes.</li>
   *   <li>If there's still a tie, the node with the smallest ID is chosen to ensure deterministic behavior.
   *       This is implicitly handled by pre-sorting candidates by ID.</li>
   * </ol>
   *
   * @param uncoloredNodes
   *     A set of {@link Node} objects that have not yet been colored.
   * @param colors
   *     A map of already colored {@link Node} objects to their assigned color (integer).
   *
   * @return The {@link Node} selected to be colored next, or {@code null} if no node can be selected
   * (e.g., if {@code uncoloredNodes} is empty).
   */
  private Node selectNextNode(Set<Node> uncoloredNodes, Map<Node, Integer> colors) {
    Node selectedNode = null;
    long maxSaturationDegree = -1;
    long maxDegreeInUncolored = -1;

    List<Node> candidates = uncoloredNodes.stream().sorted(Comparator.comparingInt(Node::getId)).toList();

    for (Node currentNode : candidates) {
      long currentSaturationDegree = calculateSaturationDegree(currentNode, colors);
      long currentDegreeInUncolored = calculateDegreeInUncoloredSubgraph(currentNode, uncoloredNodes);

      if (selectedNode == null) { // First candidate being considered
        selectedNode = currentNode;
        maxSaturationDegree = currentSaturationDegree;
        maxDegreeInUncolored = currentDegreeInUncolored;
      } else if (currentSaturationDegree > maxSaturationDegree) {
        maxSaturationDegree = currentSaturationDegree;
        maxDegreeInUncolored = currentDegreeInUncolored;
        selectedNode = currentNode;
      } else if (currentSaturationDegree == maxSaturationDegree && currentDegreeInUncolored > maxDegreeInUncolored) {
        maxDegreeInUncolored = currentDegreeInUncolored;
        selectedNode = currentNode;
        // If still tied (saturation and degreeInUncolored are equal),
        // 'selectedNode' (which has a smaller ID due to the sorted list and this selection logic) is kept.
      }
    }

    return selectedNode;
  }

  /**
   * Finds the smallest available color for a given node.
   * <p>
   * An available color is a positive integer that is not currently used by any of the node's
   * already colored neighbors. The colors are typically 1-indexed (e.g., 1, 2, 3,...).
   *
   * @param node
   *     The {@link Node} for which to find the smallest available color.
   * @param colors
   *     A map of already colored {@link Node} objects to their assigned color.
   *
   * @return The smallest positive integer color available for the specified node.
   */
  private int findSmallestAvailableColor(Node node, Map<Node, Integer> colors) {
    Set<Integer> adjacentColors = CommonGraphUtil
        .getAdjacentNodes(node)
        .stream()
        .filter(colors::containsKey) // Check if neighbor is already colored
        .map(colors::get)
        .collect(Collectors.toSet());

    int color = 1;
    while (adjacentColors.contains(color)) {
      color++;
    }

    return color;
  }

  /**
   * Calculates the saturation degree of a node.
   * <p>
   * The saturation degree is defined as the number of distinct colors
   * assigned to the neighbors of the given node that have already been colored.
   *
   * @param node
   *     The {@link Node} for which to calculate the saturation degree.
   * @param colors
   *     A map of already colored {@link Node} objects to their assigned color.
   *
   * @return The saturation degree (a non-negative long integer) of the node.
   */
  private long calculateSaturationDegree(Node node, Map<Node, Integer> colors) {
    return CommonGraphUtil
        .getAdjacentNodes(node)
        .stream()
        .filter(colors::containsKey) // Check if neighbor is already colored
        .map(colors::get)
        .distinct()
        .count();
  }

  /**
   * Calculates the degree of a node within the subgraph of uncolored nodes.
   * <p>
   * This degree is the count of its neighbors that are present in the {@code uncoloredNodes} set.
   * It's used as a tie-breaker in the DSatur algorithm when selecting the next node to color.
   *
   * @param node
   *     The {@link Node} for which to calculate the degree in the uncolored subgraph.
   * @param uncoloredNodes
   *     A set of {@link Node} objects that have not yet been colored.
   *
   * @return The degree (a non-negative long integer) of the node in the uncolored subgraph.
   */
  private long calculateDegreeInUncoloredSubgraph(Node node, Set<Node> uncoloredNodes) {
    return CommonGraphUtil
        .getAdjacentNodes(node)
        .stream()
        .filter(uncoloredNodes::contains) // Check if neighbor is in the set of uncolored nodes
        .count();
  }

}
