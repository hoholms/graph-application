package com.nbugaenco.graph.model;

/**
 * Enum representing the operations available for graph.
 * <p>
 * This enum defines the following operations:
 * <ul>
 *   <li>{@code DFS} - Depth-First Search</li>
 *   <li>{@code BFS} - Breadth-First Search</li>
 *   <li>{@code BK} - Bron-Kerbosch Algorithm</li>
 * </ul>
 * </p>
 */
public enum GraphOperation {
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

  /**
   * Prim's algorithm (PRIM) for finding the minimum spanning tree.
   */
  PRIM,
}
