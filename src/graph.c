/**
 * @file graph.c
 * @brief Implementation of graph data structures and operations
 */

#include "graph.h"
#include <ctype.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define INITIAL_CAPACITY 16
#define MAX_LINE_LENGTH 256

Graph *graph_create(void) {
    Graph *graph = malloc(sizeof(Graph));
    if (!graph)
        return NULL;

    graph->nodes = malloc(INITIAL_CAPACITY * sizeof(Node));
    if (!graph->nodes) {
        free(graph);
        return NULL;
    }

    graph->node_count = 0;
    graph->capacity = INITIAL_CAPACITY;
    return graph;
}

void graph_free(Graph *graph) {
    if (!graph)
        return;

    for (int i = 0; i < graph->node_count; i++) {
        Edge *edge = graph->nodes[i].edges;
        while (edge) {
            Edge *next = edge->next;
            free(edge);
            edge = next;
        }
    }

    free(graph->nodes);
    free(graph);
}

int graph_find_node_index(const Graph *graph, int id) {
    if (!graph)
        return -1;

    for (int i = 0; i < graph->node_count; i++) {
        if (graph->nodes[i].id == id)
            return i;
    }
    return -1;
}

int graph_get_or_create_node(Graph *graph, int id) {
    if (!graph)
        return -1;

    /* Check if node already exists */
    int idx = graph_find_node_index(graph, id);
    if (idx >= 0)
        return idx;

    /* Expand capacity if needed */
    if (graph->node_count >= graph->capacity) {
        /* Check for potential overflow before multiplying */
        if (graph->capacity > INT_MAX / 2)
            return -1;
        int new_capacity = graph->capacity * 2;
        Node *new_nodes = realloc(graph->nodes, new_capacity * sizeof(Node));
        if (!new_nodes)
            return -1;
        graph->nodes = new_nodes;
        graph->capacity = new_capacity;
    }

    /* Create new node */
    idx = graph->node_count;
    graph->nodes[idx].id = id;
    graph->nodes[idx].edges = NULL;
    graph->nodes[idx].edge_count = 0;
    graph->node_count++;

    return idx;
}

static Edge *create_edge(int src, int dest, int weight) {
    Edge *edge = malloc(sizeof(Edge));
    if (!edge)
        return NULL;

    edge->src = src;
    edge->dest = dest;
    edge->weight = weight;
    edge->next = NULL;
    return edge;
}

bool graph_add_edge(Graph *graph, int src, int dest, int weight) {
    if (!graph)
        return false;

    int src_idx = graph_get_or_create_node(graph, src);
    int dest_idx = graph_get_or_create_node(graph, dest);

    if (src_idx < 0 || dest_idx < 0)
        return false;

    /* Add edge from src to dest */
    Edge *edge1 = create_edge(src, dest, weight);
    if (!edge1)
        return false;

    edge1->next = graph->nodes[src_idx].edges;
    graph->nodes[src_idx].edges = edge1;
    graph->nodes[src_idx].edge_count++;

    /* Add edge from dest to src (undirected graph) */
    Edge *edge2 = create_edge(dest, src, weight);
    if (!edge2) {
        /* Rollback edge1 */
        graph->nodes[src_idx].edges = edge1->next;
        graph->nodes[src_idx].edge_count--;
        free(edge1);
        return false;
    }

    edge2->next = graph->nodes[dest_idx].edges;
    graph->nodes[dest_idx].edges = edge2;
    graph->nodes[dest_idx].edge_count++;

    return true;
}

static bool parse_line(Graph *graph, const char *line) {
    /* Skip leading whitespace */
    while (*line && isspace((unsigned char)*line))
        line++;

    /* Skip empty lines and comments */
    if (!*line || (line[0] == '/' && line[1] == '/'))
        return true;

    int from, to, weight = 1;
    char rest[MAX_LINE_LENGTH];

    /* Try to parse with weight first */
    if (sscanf(line, "%d%*[, ]%d%*[, ]%d%255s", &from, &to, &weight, rest) >= 3) {
        return graph_add_edge(graph, from, to, weight);
    }

    /* Try to parse without weight */
    if (sscanf(line, "%d%*[, ]%d", &from, &to) >= 2) {
        return graph_add_edge(graph, from, to, 1);
    }

    fprintf(stderr, "Warning: Malformed line: %s\n", line);
    return false;
}

Graph *graph_read_from_file(const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        fprintf(stderr, "Error: Cannot open file: %s\n", filename);
        return NULL;
    }

    Graph *graph = graph_create();
    if (!graph) {
        fclose(file);
        return NULL;
    }

    char line[MAX_LINE_LENGTH];
    while (fgets(line, sizeof(line), file)) {
        /* Remove newline */
        size_t len = strlen(line);
        if (len > 0 && line[len - 1] == '\n')
            line[len - 1] = '\0';

        if (!parse_line(graph, line)) {
            graph_free(graph);
            fclose(file);
            return NULL;
        }
    }

    fclose(file);
    return graph;
}

bool graph_are_adjacent(const Graph *graph, int node_idx1, int node_idx2) {
    if (!graph || node_idx1 < 0 || node_idx2 < 0 || node_idx1 >= graph->node_count ||
        node_idx2 >= graph->node_count)
        return false;

    if (node_idx1 == node_idx2)
        return true;

    int id2 = graph->nodes[node_idx2].id;
    Edge *edge = graph->nodes[node_idx1].edges;
    while (edge) {
        if (edge->dest == id2)
            return true;
        edge = edge->next;
    }
    return false;
}

int edge_get_adjacent(const Edge *edge, int node_id) {
    if (!edge)
        return -1;

    if (edge->src == node_id)
        return edge->dest;
    if (edge->dest == node_id)
        return edge->src;
    return -1;
}
