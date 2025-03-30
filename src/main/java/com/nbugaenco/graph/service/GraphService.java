package com.nbugaenco.graph.service;

import java.util.Map;

import com.nbugaenco.graph.model.Node;

/**
 * Interface for graph services that process graphs using specific algorithms.
 */
public interface GraphService {

  /**
   * Processes the graph using a specific algorithm.
   *
   * @param graph
   *     A map representing the graph, where the key is the node identifier and the value is a Node object.
   *
   * @return The result of the graph processing.
   */
  String process(final Map<Integer, Node> graph);

}
