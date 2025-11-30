/**
 * @file prim.c
 * @brief Implementation of Prim's algorithm for minimum spanning tree
 */

#include "prim.h"
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * @brief Priority queue entry for Prim's algorithm
 */
typedef struct PQEntry {
    int src;       /**< Source node ID */
    int dest;      /**< Destination node ID */
    int weight;    /**< Edge weight */
} PQEntry;

/**
 * @brief Min-heap based priority queue
 */
typedef struct PriorityQueue {
    PQEntry *entries;
    int count;
    int capacity;
} PriorityQueue;

static PriorityQueue *pq_create(int capacity) {
    PriorityQueue *pq = malloc(sizeof(PriorityQueue));
    if (!pq)
        return NULL;

    pq->entries = malloc(capacity * sizeof(PQEntry));
    if (!pq->entries) {
        free(pq);
        return NULL;
    }

    pq->count = 0;
    pq->capacity = capacity;
    return pq;
}

static void pq_free(PriorityQueue *pq) {
    if (pq) {
        free(pq->entries);
        free(pq);
    }
}

static bool pq_is_empty(const PriorityQueue *pq) {
    return pq->count == 0;
}

static void pq_swap(PQEntry *a, PQEntry *b) {
    PQEntry temp = *a;
    *a = *b;
    *b = temp;
}

static void pq_heapify_up(PriorityQueue *pq, int idx) {
    while (idx > 0) {
        int parent = (idx - 1) / 2;
        if (pq->entries[parent].weight > pq->entries[idx].weight) {
            pq_swap(&pq->entries[parent], &pq->entries[idx]);
            idx = parent;
        } else {
            break;
        }
    }
}

static void pq_heapify_down(PriorityQueue *pq, int idx) {
    while (true) {
        int smallest = idx;
        int left = 2 * idx + 1;
        int right = 2 * idx + 2;

        if (left < pq->count && pq->entries[left].weight < pq->entries[smallest].weight)
            smallest = left;

        if (right < pq->count && pq->entries[right].weight < pq->entries[smallest].weight)
            smallest = right;

        if (smallest != idx) {
            pq_swap(&pq->entries[idx], &pq->entries[smallest]);
            idx = smallest;
        } else {
            break;
        }
    }
}

static bool pq_insert(PriorityQueue *pq, int src, int dest, int weight) {
    if (pq->count >= pq->capacity) {
        /* Check for potential overflow before multiplying */
        if (pq->capacity > INT_MAX / 2)
            return false;
        int new_capacity = pq->capacity * 2;
        PQEntry *new_entries = realloc(pq->entries, new_capacity * sizeof(PQEntry));
        if (!new_entries)
            return false;
        pq->entries = new_entries;
        pq->capacity = new_capacity;
    }

    pq->entries[pq->count].src = src;
    pq->entries[pq->count].dest = dest;
    pq->entries[pq->count].weight = weight;
    pq_heapify_up(pq, pq->count);
    pq->count++;
    return true;
}

static PQEntry pq_extract_min(PriorityQueue *pq) {
    PQEntry min = pq->entries[0];
    pq->entries[0] = pq->entries[--pq->count];
    pq_heapify_down(pq, 0);
    return min;
}

MST *prim_mst(const Graph *graph) {
    if (!graph || graph->node_count == 0)
        return NULL;

    MST *mst = malloc(sizeof(MST));
    if (!mst)
        return NULL;

    mst->edges = malloc((graph->node_count - 1) * sizeof(MSTEdge));
    if (!mst->edges) {
        free(mst);
        return NULL;
    }

    mst->edge_count = 0;
    mst->total_weight = 0;
    mst->visited_count = 0;

    /* Track visited nodes */
    bool *visited = calloc(graph->node_count, sizeof(bool));
    if (!visited) {
        free(mst->edges);
        free(mst);
        return NULL;
    }

    /* Create priority queue */
    PriorityQueue *pq = pq_create(graph->node_count * 2);
    if (!pq) {
        free(visited);
        free(mst->edges);
        free(mst);
        return NULL;
    }

    /* Start from the first node */
    int start_idx = 0;
    visited[start_idx] = true;
    mst->visited_count = 1;

    /* Add all edges from start node to priority queue */
    Edge *edge = graph->nodes[start_idx].edges;
    while (edge) {
        pq_insert(pq, graph->nodes[start_idx].id, edge->dest, edge->weight);
        edge = edge->next;
    }

    /* Build MST */
    while (!pq_is_empty(pq) && mst->edge_count < graph->node_count - 1) {
        PQEntry min = pq_extract_min(pq);

        /* Find destination node index */
        int dest_idx = graph_find_node_index(graph, min.dest);
        if (dest_idx < 0)
            continue;

        /* If already visited, try the other endpoint */
        if (visited[dest_idx]) {
            int src_idx = graph_find_node_index(graph, min.src);
            if (src_idx < 0 || visited[src_idx])
                continue;
            dest_idx = src_idx;
        }

        /* Add to MST */
        visited[dest_idx] = true;
        mst->visited_count++;
        mst->edges[mst->edge_count].src = min.src;
        mst->edges[mst->edge_count].dest = min.dest;
        mst->edges[mst->edge_count].weight = min.weight;
        mst->edge_count++;
        mst->total_weight += min.weight;

        /* Add all edges from newly visited node */
        edge = graph->nodes[dest_idx].edges;
        while (edge) {
            int adj_idx = graph_find_node_index(graph, edge->dest);
            if (adj_idx >= 0 && !visited[adj_idx]) {
                pq_insert(pq, graph->nodes[dest_idx].id, edge->dest, edge->weight);
            }
            edge = edge->next;
        }
    }

    pq_free(pq);
    free(visited);

    return mst;
}

void mst_free(MST *mst) {
    if (mst) {
        free(mst->edges);
        free(mst);
    }
}

char *format_mst(const Graph *graph, const MST *mst) {
    if (!graph || !mst)
        return NULL;

    /* Estimate buffer size */
    size_t buf_size = 512 + mst->edge_count * 64;
    char *buffer = malloc(buf_size);
    if (!buffer)
        return NULL;

    int offset = 0;

    /* Check if graph is connected */
    if (mst->visited_count != graph->node_count && graph->node_count > 1) {
        offset +=
            sprintf(buffer + offset, "Graph might not be connected!\nNodes in MST: %d/%d\n\n",
                    mst->visited_count, graph->node_count);
    }

    offset += sprintf(buffer + offset, "Minimum Spanning Tree (Prim's Algorithm):\n");
    offset += sprintf(buffer + offset, "Edges:\n");

    for (int i = 0; i < mst->edge_count; i++) {
        offset += sprintf(buffer + offset, "(%d - %d, w:%d)\n", mst->edges[i].src,
                          mst->edges[i].dest, mst->edges[i].weight);
    }

    offset += sprintf(buffer + offset, "Total Weight: %d", mst->total_weight);

    return buffer;
}
