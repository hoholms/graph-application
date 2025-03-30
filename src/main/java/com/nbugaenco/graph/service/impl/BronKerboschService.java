package com.nbugaenco.graph.service.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.nbugaenco.graph.model.Node;
import com.nbugaenco.graph.service.GraphService;
import com.nbugaenco.graph.util.CommonGraphUtil;

/**
 * Service class for finding all maximal independent sets in a graph using the Bron-Kerbosch algorithm.
 * <p>
 * The Bron-Kerbosch algorithm works recursively to expand an independent set step by step.
 * It maintains three sets:
 * <ul>
 *     <li><b>S</b> – the independent set being constructed at the current recursion step.</li>
 *     <li><b>Q⁺</b> – the set of nodes that can extend S.</li>
 *     <li><b>Q⁻</b> – the set of nodes that have already been used to extend S in previous recursion steps.</li>
 * </ul>
 * The algorithm starts with an empty independent set: S = ∅, Q⁺ = X (all nodes).
 * <p>
 * The recursive procedure BronKerbosch(Q⁺, Q⁻) executes as follows:
 * <ol>
 *     <li>While Q⁺ is not empty and there are no nodes in Q⁻ that are disconnected from all of Q⁺:</li>
 *     <li>Select a node v from Q⁺ and add it to S.</li>
 *     <li>Construct Q⁺_new and Q⁻_new by removing nodes connected to v from Q⁺ and Q⁻.</li>
 *     <li>If both Q⁺_new and Q⁻_new are empty, S is a maximal independent set.</li>
 *     <li>Otherwise, recursively call BronKerbosch(Q⁺_new, Q⁻_new).</li>
 *     <li>Remove v from S and Q⁺, then continue.</li>
 * </ol>
 */
public class BronKerboschService implements GraphService {

  /**
   * Processes the graph and returns all maximal independent sets as a string.
   *
   * @param graph
   *     the graph map with node IDs as keys and {@link Node} objects as values
   *
   * @return a string representing the sequence of node IDs in DFS order
   */
  @Override
  public String process(final Map<Integer, Node> graph) {
    List<Set<Node>> res = bronKerbosch(graph);
    return "All maximum independent sets (%d):%n%s".formatted(res.size(), res
        .stream()
        .map(set -> set.stream().map(Node::getId).collect(Collectors.toSet()))
        .sorted(Comparator.comparing(Set::size))
        .map(Object::toString)
        .collect(Collectors.joining(";\n")));
  }

  /**
   * Implementation of the Bron-Kerbosch algorithm to find all maximal independent sets in a graph.
   * Returns all sets containing the largest maximal independent set found.
   *
   * @param graph
   *     A map representing the graph, where the key is the node identifier and the value is a Node object.
   *
   * @return A set containing the largest maximal independent set.
   */
  public List<Set<Node>> bronKerbosch(final Map<Integer, Node> graph) {
    return bronKerbosch(new HashSet<>(), new ArrayDeque<>(graph.values()), new HashSet<>(), new ArrayList<>());
  }

  /**
   * Recursive helper method for the Bron-Kerbosch algorithm.
   *
   * @param independent
   *     The current independent set being constructed.
   * @param candidates
   *     The queue of candidate nodes to be added to the independent set.
   * @param excluded
   *     The set of nodes that have been excluded from the current independent set.
   * @param result
   *     The set containing the largest maximal independent set found so far.
   *
   * @return The set containing the largest maximal independent set.
   */
  private List<Set<Node>> bronKerbosch(final Set<Node> independent, final Queue<Node> candidates,
      final Set<Node> excluded, final List<Set<Node>> result) {
    // Continue exploring while there are candidates and all excluded nodes
    // still have at least one adjacent node in the candidate set (complete adjacency).
    while (!candidates.isEmpty() && CommonGraphUtil.hasCompleteAdjacency(excluded, candidates)) {
      // Pick the next candidate to add to the independent set.
      final Node current = candidates.poll();
      independent.add(current);

      // Build a new list of candidates by removing nodes adjacent to the current node (including current).
      final Queue<Node> newCandidates = new ArrayDeque<>(candidates)
          .stream()
          .filter(node -> !node.isAdjacent(current))
          .collect(Collectors.toCollection(ArrayDeque::new));

      // Create a new excluded set to track nodes that cannot be in this independent set.
      final Set<Node> newExcluded = new HashSet<>(excluded)
          .stream()
          .filter(node -> !node.isAdjacent(current) && !node.equals(current))
          .collect(Collectors.toSet());

      // If there are no new candidates or excluded nodes, check if this set is larger
      // than the previously recorded best result and update it if so.
      if (newCandidates.isEmpty() && newExcluded.isEmpty()) {
        result.add(new HashSet<>(independent));
      } else {
        // Recursively explore adding the next candidate.
        bronKerbosch(independent, newCandidates, newExcluded, result);
      }

      // Remove the current node from the independent set
      // and move it to excluded, so we don't process it again in this branch.
      independent.remove(current);
      candidates.remove(current);
      excluded.add(current);
    }

    // Return the recorded best result (largest independent set) found so far.
    return result;
  }

}
