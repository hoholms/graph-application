package com.nbugaenco.graph.model;

import lombok.Getter;

/**
 * Singleton class to hold and parse application arguments.
 */
@Getter
public class ApplicationArguments {

  // Singleton instance
  private static ApplicationArguments instance;

  // File path provided as the first argument
  private final String filePath;

  // Starting node ID provided as the second argument
  private final int startNodeId;

  // Search method (DFS or BFS) provided as the third argument, defaults to DFS
  private final SearchMethod searchMethod;

  /**
   * Private constructor to initialize the application arguments.
   *
   * @param filePath
   *     the file path
   * @param startNodeId
   *     the starting node ID
   * @param searchMethod
   *     the search method (DFS or BFS)
   */
  private ApplicationArguments(final String filePath, final int startNodeId, final SearchMethod searchMethod) {
    this.filePath = filePath;
    this.startNodeId = startNodeId;
    this.searchMethod = searchMethod;
  }

  /**
   * Returns the singleton instance of ApplicationArguments.
   *
   * @return the singleton instance
   *
   * @throws IllegalStateException
   *     if the instance has not been initialized
   */
  public static synchronized ApplicationArguments getInstance() {
    if (instance == null) {
      throw new IllegalStateException("The application arguments have not been set.");
    }
    return instance;
  }

  /**
   * Parses the command-line arguments and initializes the singleton instance.
   *
   * @param args
   *     the command-line arguments
   *
   * @return the singleton instance
   *
   * @throws IllegalArgumentException
   *     if the arguments are invalid
   */
  public static synchronized ApplicationArguments getInstance(final String[] args) {
    if (instance != null) {
      return instance;
    }

    if (args.length < 1) {
      throw new IllegalArgumentException("Please provide a file path as the first argument.");
    }

    String filePath = args[0];
    SearchMethod searchMethod = SearchMethod.DFS;
    int startNodeId = -1;

    if (args.length == 2) {
      if (args[1].matches("-?\\d+")) {
        startNodeId = Integer.parseInt(args[1]);
        return getInstance(filePath, startNodeId, searchMethod);
      }

      searchMethod = SearchMethod.valueOf(args[1].toUpperCase());
      if (searchMethod == SearchMethod.BK) {
        return getInstance(filePath, startNodeId, searchMethod);
      } else {
        throw new IllegalArgumentException("Please provide a start node ID for DFS or BFS.");
      }
    } else if (args.length == 3) {
      try {
        searchMethod = SearchMethod.valueOf(args[1].toUpperCase());
        startNodeId = Integer.parseInt(args[2]);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
            "The search method must be either DFS, BFS or BK, and the start node ID must be an integer.");
      }
    }

    return getInstance(filePath, startNodeId, searchMethod);
  }

  /**
   * Initializes the singleton instance with the provided arguments.
   *
   * @param filePath
   *     the file path
   * @param startNodeId
   *     the starting node ID
   * @param searchMethod
   *     the search method (DFS or BFS)
   *
   * @return the singleton instance
   */
  private static synchronized ApplicationArguments getInstance(final String filePath, final int startNodeId,
      final SearchMethod searchMethod) {
    if (instance == null) {
      instance = new ApplicationArguments(filePath, startNodeId, searchMethod);
    }
    return instance;
  }

}
