/**
 * @file traversal.c
 * @brief Implementation of BFS and DFS graph traversal algorithms
 */

#include "traversal.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * @brief Simple queue implementation for BFS
 */
typedef struct Queue {
    int *data;
    int front;
    int rear;
    int capacity;
} Queue;

static Queue *queue_create(int capacity) {
    Queue *q = malloc(sizeof(Queue));
    if (!q)
        return NULL;

    q->data = malloc(capacity * sizeof(int));
    if (!q->data) {
        free(q);
        return NULL;
    }

    q->front = 0;
    q->rear = 0;
    q->capacity = capacity;
    return q;
}

static void queue_free(Queue *q) {
    if (q) {
        free(q->data);
        free(q);
    }
}

static bool queue_is_empty(const Queue *q) {
    return q->front == q->rear;
}

static bool queue_enqueue(Queue *q, int value) {
    if (q->rear >= q->capacity)
        return false;
    q->data[q->rear++] = value;
    return true;
}

static int queue_dequeue(Queue *q) {
    if (queue_is_empty(q))
        return -1;
    return q->data[q->front++];
}

bool bfs(const Graph *graph, int start_id, int *result, int *result_count) {
    if (!graph || !result || !result_count)
        return false;

    *result_count = 0;

    int start_idx = graph_find_node_index(graph, start_id);
    if (start_idx < 0) {
        fprintf(stderr, "Error: Start node %d not found\n", start_id);
        return false;
    }

    /* Create visited array */
    bool *visited = calloc(graph->node_count, sizeof(bool));
    if (!visited)
        return false;

    /* Create queue */
    Queue *queue = queue_create(graph->node_count);
    if (!queue) {
        free(visited);
        return false;
    }

    /* Start BFS */
    visited[start_idx] = true;
    queue_enqueue(queue, start_idx);

    while (!queue_is_empty(queue)) {
        int current_idx = queue_dequeue(queue);
        result[(*result_count)++] = graph->nodes[current_idx].id;

        /* Visit all adjacent nodes */
        Edge *edge = graph->nodes[current_idx].edges;
        while (edge) {
            int adj_id = edge->dest;
            int adj_idx = graph_find_node_index(graph, adj_id);

            if (adj_idx >= 0 && !visited[adj_idx]) {
                visited[adj_idx] = true;
                queue_enqueue(queue, adj_idx);
            }
            edge = edge->next;
        }
    }

    queue_free(queue);
    free(visited);
    return true;
}

static void dfs_recursive(const Graph *graph, int node_idx, bool *visited, int *result,
                          int *result_count) {
    if (visited[node_idx])
        return;

    visited[node_idx] = true;
    result[(*result_count)++] = graph->nodes[node_idx].id;

    /* Visit all adjacent nodes */
    Edge *edge = graph->nodes[node_idx].edges;
    while (edge) {
        int adj_id = edge->dest;
        int adj_idx = graph_find_node_index(graph, adj_id);

        if (adj_idx >= 0 && !visited[adj_idx]) {
            dfs_recursive(graph, adj_idx, visited, result, result_count);
        }
        edge = edge->next;
    }
}

bool dfs(const Graph *graph, int start_id, int *result, int *result_count) {
    if (!graph || !result || !result_count)
        return false;

    *result_count = 0;

    int start_idx = graph_find_node_index(graph, start_id);
    if (start_idx < 0) {
        fprintf(stderr, "Error: Start node %d not found\n", start_id);
        return false;
    }

    /* Create visited array */
    bool *visited = calloc(graph->node_count, sizeof(bool));
    if (!visited)
        return false;

    /* Start DFS */
    dfs_recursive(graph, start_idx, visited, result, result_count);

    free(visited);
    return true;
}

char *format_traversal_result(const int *result, int count) {
    if (!result || count <= 0)
        return NULL;

    /* Estimate buffer size: each node ID can be up to 10 digits + " -> " (4 chars) */
    size_t buf_size = count * 15 + 1;
    char *buffer = malloc(buf_size);
    if (!buffer)
        return NULL;

    buffer[0] = '\0';
    size_t offset = 0;

    for (int i = 0; i < count; i++) {
        size_t remaining = buf_size - offset;
        if (remaining <= 1)
            break; /* Buffer full */

        int written;
        if (i > 0) {
            written = snprintf(buffer + offset, remaining, " -> ");
            if (written < 0 || (size_t)written >= remaining)
                break;
            offset += (size_t)written;
            remaining = buf_size - offset;
        }
        written = snprintf(buffer + offset, remaining, "%d", result[i]);
        if (written < 0 || (size_t)written >= remaining)
            break;
        offset += (size_t)written;
    }

    return buffer;
}
