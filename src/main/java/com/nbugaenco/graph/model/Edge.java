package com.nbugaenco.graph.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents an edge in a graph.
 * <p>
 * Each edge connects to an adjacent node.
 * </p>
 */
@Getter
@AllArgsConstructor
public class Edge {

  /**
   * The node that this edge is connected to.
   */
  private Node adjacent;

  // TODO: Add the weight field in future

}
