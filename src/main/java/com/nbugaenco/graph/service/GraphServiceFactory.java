package com.nbugaenco.graph.service;

import com.nbugaenco.graph.model.GraphOperation;
import com.nbugaenco.graph.service.impl.BFSService;
import com.nbugaenco.graph.service.impl.BronKerboschService;
import com.nbugaenco.graph.service.impl.DFSService;
import com.nbugaenco.graph.service.impl.DSaturService;
import com.nbugaenco.graph.service.impl.PrimService;

/**
 * Factory class for creating and providing instances of graph services.
 * This class uses eager initialization to create instances of {@link BFSService}, {@link DFSService},
 * {@link BronKerboschService} and {@link PrimService}.
 */
public class GraphServiceFactory {

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
      case BFS -> new BFSService();
      case DFS -> new DFSService();
      case BK -> new BronKerboschService();
      case PRIM -> new PrimService();
      case DSATUR -> new DSaturService();
    };
  }

}
