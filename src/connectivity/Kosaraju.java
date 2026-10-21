package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class Kosaraju {
    private static void dfs1(
            Graph graph,
            int vertex,
            boolean[] visited,
            List<Integer> order
    ) {
        visited[vertex] = true;

        for (Edge edge : graph.adjacency.get(vertex)) {
            if (!visited[edge.to]) {
                dfs1(graph, edge.to, visited, order);
            }
        }

        order.add(vertex);
    }

    private static void dfs2(
            Graph graph,
            int vertex,
            boolean[] visited,
            List<Integer> component
    ) {
        visited[vertex] = true;
        component.add(vertex);

        for (Edge edge : graph.adjacency.get(vertex)) {
            if (!visited[edge.to]) {
                dfs2(graph, edge.to, visited, component);
            }
        }
    }

    public static List<List<Integer>> findSCC(Graph graph) {
        boolean[] visited = new boolean[graph.vertices];
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) {
                dfs1(graph, i, visited, order);
            }
        }

        Graph reversed = graph.reversed();
        Arrays.fill(visited, false);

        Collections.reverse(order);

        List<List<Integer>> components = new ArrayList<>();

        for (int vertex : order) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                dfs2(reversed, vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }
}