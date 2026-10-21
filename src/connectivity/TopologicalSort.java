package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class TopologicalSort {
    public static List<Integer> sort(Graph graph) {
        if (!graph.directed) {
            throw new IllegalArgumentException(
                    "Topological sort requires a directed graph"
            );
        }

        int[] indegree = new int[graph.vertices];

        for (Edge edge : graph.edges) {
            indegree[edge.to]++;
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < graph.vertices; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            order.add(vertex);

            for (Edge edge : graph.adjacency.get(vertex)) {
                indegree[edge.to]--;

                if (indegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        if (order.size() != graph.vertices) {
            return Collections.emptyList();
        }

        return order;
    }
}