/**
 * @file main.c
 * @brief Main entry point for the graph application
 *
 * This application demonstrates graph traversal using BFS, DFS,
 * Bron-Kerbosch algorithm for finding maximal independent sets,
 * Prim's algorithm for minimum spanning tree, and DSatur for graph coloring.
 */

#include "bron_kerbosch.h"
#include "dsatur.h"
#include "graph.h"
#include "prim.h"
#include "traversal.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * @brief Graph operations available
 */
typedef enum {
    OP_DFS,
    OP_BFS,
    OP_BK,
    OP_PRIM,
    OP_DSATUR,
    OP_UNKNOWN
} GraphOperation;

/**
 * @brief Application arguments
 */
typedef struct {
    const char *file_path;
    int start_node_id;
    GraphOperation operation;
} Arguments;

static void print_usage(const char *program_name) {
    fprintf(stderr, "Usage: %s <filePath> [operation] [startNodeId]\n", program_name);
    fprintf(stderr, "\n");
    fprintf(stderr, "Arguments:\n");
    fprintf(stderr, "  filePath      Path to the graph data file\n");
    fprintf(stderr, "  operation     Graph operation: DFS, BFS, BK, PRIM, or DSATUR\n");
    fprintf(stderr, "                (default: DFS)\n");
    fprintf(stderr, "  startNodeId   Starting node ID (required for DFS and BFS)\n");
    fprintf(stderr, "\n");
    fprintf(stderr, "Examples:\n");
    fprintf(stderr, "  %s graph.txt BFS 1\n", program_name);
    fprintf(stderr, "  %s graph.txt DFS 1\n", program_name);
    fprintf(stderr, "  %s graph.txt BK\n", program_name);
    fprintf(stderr, "  %s graph.txt PRIM\n", program_name);
    fprintf(stderr, "  %s graph.txt DSATUR\n", program_name);
}

static GraphOperation parse_operation(const char *str) {
    if (!str)
        return OP_DFS;

    if (strcasecmp(str, "DFS") == 0)
        return OP_DFS;
    if (strcasecmp(str, "BFS") == 0)
        return OP_BFS;
    if (strcasecmp(str, "BK") == 0)
        return OP_BK;
    if (strcasecmp(str, "PRIM") == 0)
        return OP_PRIM;
    if (strcasecmp(str, "DSATUR") == 0)
        return OP_DSATUR;

    return OP_UNKNOWN;
}

static bool is_integer(const char *str) {
    if (!str || !*str)
        return false;

    if (*str == '-')
        str++;

    while (*str) {
        if (*str < '0' || *str > '9')
            return false;
        str++;
    }
    return true;
}

static Arguments parse_arguments(int argc, char *argv[]) {
    Arguments args = {.file_path = NULL, .start_node_id = -1, .operation = OP_DFS};

    if (argc < 2) {
        print_usage(argv[0]);
        exit(1);
    }

    args.file_path = argv[1];

    if (argc == 2) {
        /* Just file path, default to DFS (will need start node) */
        fprintf(stderr, "Error: Please provide a start node ID for DFS.\n");
        print_usage(argv[0]);
        exit(1);
    }

    if (argc == 3) {
        /* Could be operation or start node */
        if (is_integer(argv[2])) {
            args.start_node_id = atoi(argv[2]);
            args.operation = OP_DFS;
        } else {
            args.operation = parse_operation(argv[2]);
            if (args.operation == OP_UNKNOWN) {
                fprintf(stderr, "Error: Unknown operation '%s'\n", argv[2]);
                print_usage(argv[0]);
                exit(1);
            }
            if (args.operation == OP_DFS || args.operation == OP_BFS) {
                fprintf(stderr, "Error: Please provide a start node ID for %s.\n", argv[2]);
                print_usage(argv[0]);
                exit(1);
            }
        }
    } else if (argc >= 4) {
        args.operation = parse_operation(argv[2]);
        if (args.operation == OP_UNKNOWN) {
            fprintf(stderr, "Error: Unknown operation '%s'\n", argv[2]);
            print_usage(argv[0]);
            exit(1);
        }
        args.start_node_id = atoi(argv[3]);
    }

    return args;
}

static int run_dfs(const Graph *graph, int start_node_id) {
    int *result = malloc(graph->node_count * sizeof(int));
    if (!result) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        return 1;
    }

    int result_count = 0;
    if (!dfs(graph, start_node_id, result, &result_count)) {
        free(result);
        return 1;
    }

    char *output = format_traversal_result(result, result_count);
    if (output) {
        printf("%s\n", output);
        free(output);
    }

    free(result);
    return 0;
}

static int run_bfs(const Graph *graph, int start_node_id) {
    int *result = malloc(graph->node_count * sizeof(int));
    if (!result) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        return 1;
    }

    int result_count = 0;
    if (!bfs(graph, start_node_id, result, &result_count)) {
        free(result);
        return 1;
    }

    char *output = format_traversal_result(result, result_count);
    if (output) {
        printf("%s\n", output);
        free(output);
    }

    free(result);
    return 0;
}

static int run_bron_kerbosch(const Graph *graph) {
    IndependentSets *sets = bron_kerbosch(graph);
    if (!sets) {
        fprintf(stderr, "Error: Bron-Kerbosch algorithm failed\n");
        return 1;
    }

    char *output = format_independent_sets(graph, sets);
    if (output) {
        printf("%s", output);
        free(output);
    }

    independent_sets_free(sets);
    return 0;
}

static int run_prim(const Graph *graph) {
    MST *mst = prim_mst(graph);
    if (!mst) {
        fprintf(stderr, "Error: Prim's algorithm failed\n");
        return 1;
    }

    char *output = format_mst(graph, mst);
    if (output) {
        printf("%s\n", output);
        free(output);
    }

    mst_free(mst);
    return 0;
}

static int run_dsatur(const Graph *graph) {
    ColoringResult *result = dsatur(graph);
    if (!result) {
        fprintf(stderr, "Error: DSatur algorithm failed\n");
        return 1;
    }

    char *output = format_coloring_result(graph, result);
    if (output) {
        printf("%s\n", output);
        free(output);
    }

    coloring_result_free(result);
    return 0;
}

int main(int argc, char *argv[]) {
    Arguments args = parse_arguments(argc, argv);

    Graph *graph = graph_read_from_file(args.file_path);
    if (!graph) {
        return 1;
    }

    int result = 0;

    switch (args.operation) {
    case OP_DFS:
        result = run_dfs(graph, args.start_node_id);
        break;
    case OP_BFS:
        result = run_bfs(graph, args.start_node_id);
        break;
    case OP_BK:
        result = run_bron_kerbosch(graph);
        break;
    case OP_PRIM:
        result = run_prim(graph);
        break;
    case OP_DSATUR:
        result = run_dsatur(graph);
        break;
    default:
        fprintf(stderr, "Error: Unknown operation\n");
        result = 1;
    }

    graph_free(graph);
    return result;
}
