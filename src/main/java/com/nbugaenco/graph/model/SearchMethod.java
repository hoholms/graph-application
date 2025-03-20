package com.nbugaenco.graph.model;

/**
 * Enum representing the search methods available for graph traversal.
 * <p>
 * This enum defines two search methods:
 * <ul>
 *   <li>{@code DFS} - Depth-First Search</li>
 *   <li>{@code BFS} - Breadth-First Search</li>
 * </ul>
 * </p>
 */
public enum SearchMethod {
  /**
   * Depth-First Search (DFS) method.
   */
  DFS,

  /**
   * Breadth-First Search (BFS) method.
   */
  BFS,

  /**
   * Bron-Kerbosch (BK) method.
   */
  BK,
}
