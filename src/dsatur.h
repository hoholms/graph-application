/**
 * @file dsatur.h
 * @brief DSatur algorithm for graph coloring
 */

#ifndef DSATUR_H
#define DSATUR_H

#include "graph.h"

/**
 * @brief Structure to hold graph coloring result
 */
typedef struct ColoringResult {
    int *colors;         /**< Array of colors for each node (indexed by node index) */
    int node_count;      /**< Number of nodes */
    int colors_used;     /**< Number of distinct colors used */
} ColoringResult;

/**
 * @brief Colors a graph using the DSatur algorithm
 *
 * @param graph The graph to color
 * @return Pointer to ColoringResult (caller must free with coloring_result_free)
 */
ColoringResult *dsatur(const Graph *graph);

/**
 * @brief Frees memory associated with ColoringResult
 *
 * @param result The ColoringResult to free
 */
void coloring_result_free(ColoringResult *result);

/**
 * @brief Formats the coloring result as a string
 *
 * @param graph The graph
 * @param result The coloring result
 * @return Formatted string (caller must free), or NULL on failure
 */
char *format_coloring_result(const Graph *graph, const ColoringResult *result);

#endif /* DSATUR_H */
