/**
 * @file bron_kerbosch.h
 * @brief Bron-Kerbosch algorithm for finding maximal independent sets
 */

#ifndef BRON_KERBOSCH_H
#define BRON_KERBOSCH_H

#include "graph.h"

/**
 * @brief Structure to hold a set of node indices
 */
typedef struct NodeSet {
    int *nodes;    /**< Array of node indices */
    int count;     /**< Number of nodes in the set */
    int capacity;  /**< Capacity of the array */
} NodeSet;

/**
 * @brief Structure to hold all maximal independent sets found
 */
typedef struct IndependentSets {
    NodeSet *sets;     /**< Array of independent sets */
    int count;         /**< Number of sets */
    int capacity;      /**< Capacity of the array */
} IndependentSets;

/**
 * @brief Finds all maximal independent sets using the Bron-Kerbosch algorithm
 *
 * @param graph The graph to analyze
 * @return Pointer to IndependentSets structure (caller must free with independent_sets_free)
 */
IndependentSets *bron_kerbosch(const Graph *graph);

/**
 * @brief Frees memory associated with IndependentSets
 *
 * @param sets The IndependentSets to free
 */
void independent_sets_free(IndependentSets *sets);

/**
 * @brief Formats the independent sets result as a string
 *
 * @param graph The graph (for node ID lookup)
 * @param sets The independent sets to format
 * @return Formatted string (caller must free), or NULL on failure
 */
char *format_independent_sets(const Graph *graph, const IndependentSets *sets);

#endif /* BRON_KERBOSCH_H */
