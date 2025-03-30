package com.nbugaenco.graph.util;

import java.util.Collection;
import java.util.List;

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

}
