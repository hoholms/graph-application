package com.nbugaenco.graph.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents an edge in a graph.
 * <p>
 * Each edge connects to an adjacent node.
 * </p>
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = {"adjacent", "weight"})
public class Edge {

  private Node parent;
  private Node adjacent;
  private int  weight;

  // TODO: Add flag for directed/undirected edges in future

  public Node getAdjacent(final Node node) {
    if (parent.equals(node)) {
      return adjacent;
    } else if (adjacent.equals(node)) {
      return parent;
    }

    return null;
  }

  @Override
  public String toString() {
    String parentId = (parent != null) ? String.valueOf(parent.getId()) : "?";
    String adjacentId = (adjacent != null) ? String.valueOf(adjacent.getId()) : "?";

    return "(" + parentId + " - " + adjacentId + ", w:" + weight + ")";
  }

}
