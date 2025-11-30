/**
 * @file dsatur.c
 * @brief Implementation of DSatur algorithm for graph coloring
 */

#include "dsatur.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* ANSI color codes for terminal output */
#define RESET "\033[0m"
static const char *COLOR_CODES[] = {"\033[31m", /* 1 - Red */
                                    "\033[32m", /* 2 - Green */
                                    "\033[33m", /* 3 - Yellow */
                                    "\033[34m", /* 4 - Blue */
                                    "\033[35m", /* 5 - Magenta */
                                    "\033[36m"  /* 6 - Cyan */
};
#define NUM_COLOR_CODES 6

/**
 * @brief Calculates the saturation degree of a node
 *
 * The saturation degree is the number of distinct colors among the node's neighbors.
 */
static int calculate_saturation_degree(const Graph *graph, int node_idx, const int *colors) {
    bool used_colors[1024] = {false}; /* Track which colors are used by neighbors */
    int max_color = 0;

    Edge *edge = graph->nodes[node_idx].edges;
    while (edge) {
        int adj_idx = graph_find_node_index(graph, edge->dest);
        if (adj_idx >= 0 && colors[adj_idx] > 0) {
            int c = colors[adj_idx];
            if (c < 1024 && !used_colors[c]) {
                used_colors[c] = true;
                max_color = (c > max_color) ? c : max_color;
            }
        }
        edge = edge->next;
    }

    /* Count distinct colors */
    int saturation = 0;
    for (int i = 1; i <= max_color; i++) {
        if (used_colors[i])
            saturation++;
    }
    return saturation;
}

/**
 * @brief Calculates the degree of a node in the uncolored subgraph
 */
static int calculate_degree_in_uncolored(const Graph *graph, int node_idx, const int *colors) {
    int degree = 0;

    Edge *edge = graph->nodes[node_idx].edges;
    while (edge) {
        int adj_idx = graph_find_node_index(graph, edge->dest);
        if (adj_idx >= 0 && colors[adj_idx] == 0) {
            degree++;
        }
        edge = edge->next;
    }
    return degree;
}

/**
 * @brief Finds the smallest available color for a node
 */
static int find_smallest_available_color(const Graph *graph, int node_idx, const int *colors) {
    bool used[1024] = {false};

    Edge *edge = graph->nodes[node_idx].edges;
    while (edge) {
        int adj_idx = graph_find_node_index(graph, edge->dest);
        if (adj_idx >= 0 && colors[adj_idx] > 0 && colors[adj_idx] < 1024) {
            used[colors[adj_idx]] = true;
        }
        edge = edge->next;
    }

    /* Find smallest unused color */
    for (int c = 1; c < 1024; c++) {
        if (!used[c])
            return c;
    }
    return 1024; /* Fallback */
}

/**
 * @brief Selects the next node to color based on DSatur criteria
 */
static int select_next_node(const Graph *graph, const int *colors) {
    int selected = -1;
    int max_saturation = -1;
    int max_degree = -1;
    int min_id = INT_MAX;

    for (int i = 0; i < graph->node_count; i++) {
        if (colors[i] != 0)
            continue; /* Skip colored nodes */

        int saturation = calculate_saturation_degree(graph, i, colors);
        int degree = calculate_degree_in_uncolored(graph, i, colors);
        int node_id = graph->nodes[i].id;

        bool is_better = false;
        if (selected == -1) {
            is_better = true;
        } else if (saturation > max_saturation) {
            is_better = true;
        } else if (saturation == max_saturation && degree > max_degree) {
            is_better = true;
        } else if (saturation == max_saturation && degree == max_degree && node_id < min_id) {
            is_better = true;
        }

        if (is_better) {
            selected = i;
            max_saturation = saturation;
            max_degree = degree;
            min_id = node_id;
        }
    }

    return selected;
}

ColoringResult *dsatur(const Graph *graph) {
    if (!graph || graph->node_count == 0)
        return NULL;

    ColoringResult *result = malloc(sizeof(ColoringResult));
    if (!result)
        return NULL;

    result->colors = calloc(graph->node_count, sizeof(int));
    if (!result->colors) {
        free(result);
        return NULL;
    }

    result->node_count = graph->node_count;
    result->colors_used = 0;

    int uncolored = graph->node_count;

    while (uncolored > 0) {
        int node_idx = select_next_node(graph, result->colors);
        if (node_idx < 0)
            break;

        int color = find_smallest_available_color(graph, node_idx, result->colors);
        result->colors[node_idx] = color;

        if (color > result->colors_used)
            result->colors_used = color;

        uncolored--;
    }

    return result;
}

void coloring_result_free(ColoringResult *result) {
    if (result) {
        free(result->colors);
        free(result);
    }
}

char *format_coloring_result(const Graph *graph, const ColoringResult *result) {
    if (!graph || !result)
        return NULL;

    /* Estimate buffer size */
    size_t buf_size = 256 + result->node_count * 64;
    char *buffer = malloc(buf_size);
    if (!buffer)
        return NULL;

    /* Create sorted index array by node ID */
    int *sorted_indices = malloc(graph->node_count * sizeof(int));
    if (!sorted_indices) {
        free(buffer);
        return NULL;
    }

    for (int i = 0; i < graph->node_count; i++) {
        sorted_indices[i] = i;
    }

    /* Sort by node ID */
    for (int i = 0; i < graph->node_count - 1; i++) {
        for (int j = i + 1; j < graph->node_count; j++) {
            if (graph->nodes[sorted_indices[i]].id > graph->nodes[sorted_indices[j]].id) {
                int temp = sorted_indices[i];
                sorted_indices[i] = sorted_indices[j];
                sorted_indices[j] = temp;
            }
        }
    }

    int offset = sprintf(buffer, "Graph Coloring Result (DSatur Algorithm):\n");

    for (int i = 0; i < graph->node_count; i++) {
        int idx = sorted_indices[i];
        int color = result->colors[idx];
        int color_code_idx = (color - 1) % NUM_COLOR_CODES;
        if (color_code_idx < 0)
            color_code_idx = 0;

        offset += sprintf(buffer + offset, "Node %d: %sColor %d%s\n", graph->nodes[idx].id,
                          COLOR_CODES[color_code_idx], color, RESET);
    }

    offset += sprintf(buffer + offset, "\nTotal colors used: %d", result->colors_used);

    free(sorted_indices);
    return buffer;
}
