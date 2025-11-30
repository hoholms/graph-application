/**
 * @file bron_kerbosch.c
 * @brief Implementation of Bron-Kerbosch algorithm for finding maximal independent sets
 */

#include "bron_kerbosch.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define INITIAL_SET_CAPACITY 16

static NodeSet *nodeset_create(int capacity) {
    NodeSet *set = malloc(sizeof(NodeSet));
    if (!set)
        return NULL;

    set->nodes = malloc(capacity * sizeof(int));
    if (!set->nodes) {
        free(set);
        return NULL;
    }

    set->count = 0;
    set->capacity = capacity;
    return set;
}

static void nodeset_free(NodeSet *set) {
    if (set) {
        free(set->nodes);
        free(set);
    }
}

static bool nodeset_add(NodeSet *set, int node_idx) {
    if (!set)
        return false;

    if (set->count >= set->capacity) {
        int new_capacity = set->capacity * 2;
        int *new_nodes = realloc(set->nodes, new_capacity * sizeof(int));
        if (!new_nodes)
            return false;
        set->nodes = new_nodes;
        set->capacity = new_capacity;
    }

    set->nodes[set->count++] = node_idx;
    return true;
}

static bool nodeset_remove(NodeSet *set, int node_idx) {
    if (!set)
        return false;

    for (int i = 0; i < set->count; i++) {
        if (set->nodes[i] == node_idx) {
            /* Shift remaining elements */
            memmove(&set->nodes[i], &set->nodes[i + 1], (set->count - i - 1) * sizeof(int));
            set->count--;
            return true;
        }
    }
    return false;
}

static IndependentSets *independent_sets_create(void) {
    IndependentSets *sets = malloc(sizeof(IndependentSets));
    if (!sets)
        return NULL;

    sets->sets = malloc(INITIAL_SET_CAPACITY * sizeof(NodeSet));
    if (!sets->sets) {
        free(sets);
        return NULL;
    }

    sets->count = 0;
    sets->capacity = INITIAL_SET_CAPACITY;
    return sets;
}

void independent_sets_free(IndependentSets *sets) {
    if (!sets)
        return;

    for (int i = 0; i < sets->count; i++) {
        free(sets->sets[i].nodes);
    }
    free(sets->sets);
    free(sets);
}

static bool independent_sets_add(IndependentSets *sets, const NodeSet *set) {
    if (!sets || !set)
        return false;

    if (sets->count >= sets->capacity) {
        int new_capacity = sets->capacity * 2;
        NodeSet *new_sets = realloc(sets->sets, new_capacity * sizeof(NodeSet));
        if (!new_sets)
            return false;
        sets->sets = new_sets;
        sets->capacity = new_capacity;
    }

    /* Copy the set */
    sets->sets[sets->count].nodes = malloc(set->count * sizeof(int));
    if (!sets->sets[sets->count].nodes)
        return false;

    memcpy(sets->sets[sets->count].nodes, set->nodes, set->count * sizeof(int));
    sets->sets[sets->count].count = set->count;
    sets->sets[sets->count].capacity = set->count;
    sets->count++;

    return true;
}

/**
 * @brief Checks if all nodes in excluded set have at least one adjacent node in candidates
 */
static bool has_complete_adjacency(const Graph *graph, const NodeSet *excluded,
                                   const NodeSet *candidates) {
    for (int i = 0; i < excluded->count; i++) {
        bool has_adjacent = false;
        for (int j = 0; j < candidates->count; j++) {
            if (graph_are_adjacent(graph, excluded->nodes[i], candidates->nodes[j])) {
                has_adjacent = true;
                break;
            }
        }
        if (!has_adjacent)
            return false;
    }
    return true;
}

/**
 * @brief Recursive Bron-Kerbosch implementation
 */
static void bron_kerbosch_recursive(const Graph *graph, NodeSet *independent, NodeSet *candidates,
                                    NodeSet *excluded, IndependentSets *result) {
    while (candidates->count > 0 && has_complete_adjacency(graph, excluded, candidates)) {
        /* Pick the first candidate */
        int current = candidates->nodes[0];
        nodeset_add(independent, current);

        /* Create new candidates by removing adjacent nodes */
        NodeSet *new_candidates = nodeset_create(candidates->capacity);
        if (!new_candidates)
            return;

        for (int i = 1; i < candidates->count; i++) {
            if (!graph_are_adjacent(graph, current, candidates->nodes[i])) {
                nodeset_add(new_candidates, candidates->nodes[i]);
            }
        }

        /* Create new excluded by removing adjacent nodes */
        NodeSet *new_excluded = nodeset_create(excluded->capacity > 0 ? excluded->capacity : 1);
        if (!new_excluded) {
            nodeset_free(new_candidates);
            return;
        }

        for (int i = 0; i < excluded->count; i++) {
            if (!graph_are_adjacent(graph, current, excluded->nodes[i])) {
                nodeset_add(new_excluded, excluded->nodes[i]);
            }
        }

        /* Check if we found a maximal independent set */
        if (new_candidates->count == 0 && new_excluded->count == 0) {
            independent_sets_add(result, independent);
        } else {
            bron_kerbosch_recursive(graph, independent, new_candidates, new_excluded, result);
        }

        /* Backtrack */
        nodeset_remove(independent, current);
        nodeset_remove(candidates, current);
        nodeset_add(excluded, current);

        nodeset_free(new_candidates);
        nodeset_free(new_excluded);
    }
}

IndependentSets *bron_kerbosch(const Graph *graph) {
    if (!graph || graph->node_count == 0)
        return NULL;

    IndependentSets *result = independent_sets_create();
    if (!result)
        return NULL;

    NodeSet *independent = nodeset_create(graph->node_count);
    NodeSet *candidates = nodeset_create(graph->node_count);
    NodeSet *excluded = nodeset_create(graph->node_count);

    if (!independent || !candidates || !excluded) {
        nodeset_free(independent);
        nodeset_free(candidates);
        nodeset_free(excluded);
        independent_sets_free(result);
        return NULL;
    }

    /* Initialize candidates with all nodes */
    for (int i = 0; i < graph->node_count; i++) {
        nodeset_add(candidates, i);
    }

    bron_kerbosch_recursive(graph, independent, candidates, excluded, result);

    nodeset_free(independent);
    nodeset_free(candidates);
    nodeset_free(excluded);

    return result;
}

/* Comparison function for sorting by size then by first element */
static int compare_sets(const void *a, const void *b) {
    const NodeSet *set_a = (const NodeSet *)a;
    const NodeSet *set_b = (const NodeSet *)b;

    /* First sort by size */
    if (set_a->count != set_b->count)
        return set_a->count - set_b->count;

    /* Then by first element (if sizes are equal) */
    if (set_a->count > 0 && set_b->count > 0)
        return set_a->nodes[0] - set_b->nodes[0];

    return 0;
}

char *format_independent_sets(const Graph *graph, const IndependentSets *sets) {
    if (!graph || !sets)
        return NULL;

    /* Sort the sets by size */
    qsort(sets->sets, sets->count, sizeof(NodeSet), compare_sets);

    /* Estimate buffer size */
    size_t buf_size = 256 + sets->count * 64;
    char *buffer = malloc(buf_size);
    if (!buffer)
        return NULL;

    int offset = sprintf(buffer, "All maximum independent sets (%d):\n", sets->count);

    for (int i = 0; i < sets->count; i++) {
        offset += sprintf(buffer + offset, "[");
        for (int j = 0; j < sets->sets[i].count; j++) {
            if (j > 0)
                offset += sprintf(buffer + offset, ", ");
            int node_idx = sets->sets[i].nodes[j];
            offset += sprintf(buffer + offset, "%d", graph->nodes[node_idx].id);
        }
        offset += sprintf(buffer + offset, "]");
        if (i < sets->count - 1)
            offset += sprintf(buffer + offset, ";");
        offset += sprintf(buffer + offset, "\n");
    }

    return buffer;
}
