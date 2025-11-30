/**
 * @file prim.h
 * @brief Prim's algorithm for finding minimum spanning tree
 */

#ifndef PRIM_H
#define PRIM_H

#include "graph.h"

/**
 * @brief Structure representing an edge in the MST
 */
typedef struct MSTEdge {
    int src;       /**< Source node ID */
    int dest;      /**< Destination node ID */
    int weight;    /**< Edge weight */
} MSTEdge;

/**
 * @brief Structure to hold the minimum spanning tree result
 */
typedef struct MST {
    MSTEdge *edges;      /**< Array of edges in the MST */
    int edge_count;      /**< Number of edges */
    int total_weight;    /**< Total weight of the MST */
    int visited_count;   /**< Number of visited nodes */
} MST;

/**
 * @brief Finds the minimum spanning tree using Prim's algorithm
 *
 * @param graph The graph to analyze
 * @return Pointer to MST structure (caller must free with mst_free)
 */
MST *prim_mst(const Graph *graph);

/**
 * @brief Frees memory associated with MST
 *
 * @param mst The MST to free
 */
void mst_free(MST *mst);

/**
 * @brief Formats the MST result as a string
 *
 * @param graph The graph (for context)
 * @param mst The MST to format
 * @return Formatted string (caller must free), or NULL on failure
 */
char *format_mst(const Graph *graph, const MST *mst);

#endif /* PRIM_H */
