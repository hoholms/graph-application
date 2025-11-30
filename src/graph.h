/**
 * @file graph.h
 * @brief Data structures and functions for graph representation
 *
 * This header defines the core data structures for representing a graph
 * with weighted edges and provides functions for graph creation, manipulation,
 * and destruction.
 */

#ifndef GRAPH_H
#define GRAPH_H

#include <stdbool.h>
#include <stdio.h>

/**
 * @brief Structure representing an edge in the graph
 */
typedef struct Edge {
    int src;           /**< Source node ID */
    int dest;          /**< Destination node ID */
    int weight;        /**< Edge weight */
    struct Edge *next; /**< Next edge in adjacency list */
} Edge;

/**
 * @brief Structure representing a node in the graph
 */
typedef struct Node {
    int id;         /**< Node identifier */
    Edge *edges;    /**< Pointer to first edge in adjacency list */
    int edge_count; /**< Number of edges connected to this node */
} Node;

/**
 * @brief Structure representing the entire graph
 */
typedef struct Graph {
    Node *nodes;    /**< Array of nodes */
    int node_count; /**< Number of nodes in the graph */
    int capacity;   /**< Current capacity of nodes array */
} Graph;

/**
 * @brief Creates a new empty graph
 *
 * @return Pointer to the new graph, or NULL on failure
 */
Graph *graph_create(void);

/**
 * @brief Frees all memory associated with a graph
 *
 * @param graph The graph to free
 */
void graph_free(Graph *graph);

/**
 * @brief Gets or creates a node with the given ID
 *
 * @param graph The graph
 * @param id The node ID
 * @return Index of the node in the nodes array, or -1 on failure
 */
int graph_get_or_create_node(Graph *graph, int id);

/**
 * @brief Adds an undirected edge between two nodes
 *
 * @param graph The graph
 * @param src Source node ID
 * @param dest Destination node ID
 * @param weight Edge weight
 * @return true on success, false on failure
 */
bool graph_add_edge(Graph *graph, int src, int dest, int weight);

/**
 * @brief Reads a graph from a file
 *
 * The file format is expected to have lines with:
 * node1,node2[,weight]
 * where weight defaults to 1 if not provided.
 * Lines starting with // are treated as comments.
 *
 * @param filename Path to the graph file
 * @return Pointer to the graph, or NULL on failure
 */
Graph *graph_read_from_file(const char *filename);

/**
 * @brief Finds the index of a node by its ID
 *
 * @param graph The graph
 * @param id The node ID to find
 * @return Index of the node, or -1 if not found
 */
int graph_find_node_index(const Graph *graph, int id);

/**
 * @brief Checks if two nodes are adjacent
 *
 * @param graph The graph
 * @param node_idx1 Index of the first node
 * @param node_idx2 Index of the second node
 * @return true if adjacent, false otherwise
 */
bool graph_are_adjacent(const Graph *graph, int node_idx1, int node_idx2);

/**
 * @brief Gets adjacent node ID from an edge relative to a given node
 *
 * @param edge The edge
 * @param node_id The node ID to get adjacent from
 * @return The adjacent node ID, or -1 if node_id is not part of the edge
 */
int edge_get_adjacent(const Edge *edge, int node_id);

#endif /* GRAPH_H */
