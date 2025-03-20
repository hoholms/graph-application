package com.nbugaenco.graph.model;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a node in a graph.
 * <p>
 * Each node has a unique identifier and a set of edges connecting it to other nodes.
 * </p>
 */
@Getter
@AllArgsConstructor
public class Node {

  /**
   * The unique identifier of the node.
   */
  private int id;

  /**
   * The set of edges connected to this node.
   * <p>
   * Initialized as a {@link LinkedHashSet} to maintain insertion order.
   * </p>
   */
  private Set<Edge> edges = new LinkedHashSet<>();

  // TODO: Add parents Map<Node, Edge> in future

  /**
   * Constructs a node with the specified identifier.
   * <p>
   * The edges set is initialized to an empty {@link LinkedHashSet}.
   * </p>
   *
   * @param id
   *     the unique identifier of the node
   */
  public Node(final int id) {
    this.id = id;
  }

  public boolean isAdjacent(Node node) {
    if (this.equals(node)) {
      return true;
    }

    return edges.stream().map(Edge::getAdjacent).anyMatch(node::equals);
  }

}
