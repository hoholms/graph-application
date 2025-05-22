package com.nbugaenco.graph.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.nbugaenco.graph.model.Node;
import lombok.experimental.UtilityClass;

/**
 * Utility class for common graph operations.
 */
@UtilityClass
public class CommonGraphUtil {

  /**
   * Converts a list of nodes to a string representation of their sequence.
   *
   * @param nodes
   *     The list of nodes to convert.
   *
   * @return A string representing the sequence of node IDs, separated by " -> ".
   */
  public static String toNodesSequence(final List<Node> nodes) {
    return nodes.stream().map(Node::getId).map(String::valueOf).reduce((a, b) -> a + " -> " + b).orElse("");
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
  public static boolean hasCompleteAdjacency(final Collection<Node> nodes1, final Collection<Node> nodes2) {
    return nodes1.stream().allMatch(node -> nodes2.stream().anyMatch(node::isAdjacent));
  }

  /**
   * Retrieves the set of adjacent nodes (neighbors) for a given node.
   * <p>
   * This method iterates through the edges of the specified node and collects
   * all unique adjacent nodes. It handles cases where the node or its edges might be null.
   *
   * @param node
   *     The {@link Node} whose adjacent nodes are to be retrieved.
   *
   * @return A {@link Set} of {@link Node} objects that are adjacent to the given node.
   * Returns an empty set if the node is null, has no edges, or if no valid adjacent nodes are found.
   */
  public static Set<Node> getAdjacentNodes(Node node) {
    if (node == null || node.getEdges() == null) {
      return Collections.emptySet();
    }

    return node
        .getEdges()
        .stream()
        .map(edge -> edge.getAdjacent(node))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

}
