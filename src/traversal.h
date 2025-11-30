/**
 * @file traversal.h
 * @brief BFS and DFS graph traversal algorithms
 */

#ifndef TRAVERSAL_H
#define TRAVERSAL_H

#include "graph.h"

/**
 * @brief Performs Breadth-First Search starting from a given node
 *
 * @param graph The graph to traverse
 * @param start_id The starting node ID
 * @param result Array to store visited node IDs (must be pre-allocated with graph->node_count size)
 * @param result_count Pointer to store the number of visited nodes
 * @return true on success, false on failure
 */
bool bfs(const Graph *graph, int start_id, int *result, int *result_count);

/**
 * @brief Performs Depth-First Search starting from a given node
 *
 * @param graph The graph to traverse
 * @param start_id The starting node ID
 * @param result Array to store visited node IDs (must be pre-allocated with graph->node_count size)
 * @param result_count Pointer to store the number of visited nodes
 * @return true on success, false on failure
 */
bool dfs(const Graph *graph, int start_id, int *result, int *result_count);

/**
 * @brief Formats traversal result as a string
 *
 * @param result Array of visited node IDs
 * @param count Number of visited nodes
 * @return Formatted string (caller must free), or NULL on failure
 */
char *format_traversal_result(const int *result, int count);

#endif /* TRAVERSAL_H */
