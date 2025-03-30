package com.nbugaenco.graph.service;

import com.nbugaenco.graph.model.GraphOperation;
import com.nbugaenco.graph.service.impl.BFSService;
import com.nbugaenco.graph.service.impl.BronKerboschService;
import com.nbugaenco.graph.service.impl.DFSService;

/**
 * Factory class for creating and providing instances of graph services.
 * This class uses eager initialization to create instances of {@link BFSService}, {@link DFSService}, and
 * {@link BronKerboschService}.
 */
public class GraphServiceFactory {

  private final BFSService          bfsInstance;
  private final DFSService          dfsInstance;
  private final BronKerboschService bkInstance;

  /**
   * Constructor that initializes the service instances.
   */
  public GraphServiceFactory() {
    this.bfsInstance = new BFSService();
    this.dfsInstance = new DFSService();
    this.bkInstance = new BronKerboschService();
  }

  /**
   * Returns the appropriate graph service instance based on the provided search method.
   *
   * @param method
   *     the search method for which the service is required
   *
   * @return the corresponding graph service instance
   */
  public GraphService getService(final GraphOperation method) {
    return switch (method) {
      case BFS -> this.bfsInstance;
      case DFS -> this.dfsInstance;
      case BK -> this.bkInstance;
    };
  }

}
