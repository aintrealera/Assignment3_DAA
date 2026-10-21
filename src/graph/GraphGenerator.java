package graph;

import java.util.*;

public class GraphGenerator {
    public static Graph connectedUndirected(int vertices, int edges, long seed) {
        if (vertices < 1 || edges < vertices - 1
                || edges > (long) vertices * (vertices - 1) / 2) {
            throw new IllegalArgumentException("Invalid graph configuration");
        }

        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        for (int i = 1; i < vertices; i++) {
            int parent = random.nextInt(i);
            int weight = random.nextInt(100) + 1;

            graph.addEdge(i, parent, weight);
            used.add(key(i, parent));
        }

        while (graph.edges.size() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from == to) {
                continue;
            }

            int a = Math.min(from, to);
            int b = Math.max(from, to);
            long key = key(a, b);

            if (used.add(key)) {
                graph.addEdge(a, b, random.nextInt(100) + 1);
            }
        }

        return graph;
    }

    public static Graph directed(int vertices, int edges, long seed) {
        if (vertices < 1 || edges < 0
                || edges > (long) vertices * (vertices - 1)) {
            throw new IllegalArgumentException("Invalid graph configuration");
        }

        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        while (graph.edges.size() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from == to) {
                continue;
            }

            long key = (long) from * vertices + to;

            if (used.add(key)) {
                graph.addEdge(from, to, random.nextInt(100) + 1);
            }
        }

        return graph;
    }

    private static long key(int a, int b) {
        return (long) a * 1_000_000L + b;
    }
}