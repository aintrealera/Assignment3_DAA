package mst;

import graph.Edge;
import graph.Graph;
import dsu.DSU;

import java.util.*;

public class Kruskal {
    public static class Result {
        public final List<Edge> edges;
        public final long totalWeight;
        public final boolean connected;
        public final long operations;

        public Result(List<Edge> edges, long totalWeight,
                      boolean connected, long operations) {
            this.edges = edges;
            this.totalWeight = totalWeight;
            this.connected = connected;
            this.operations = operations;
        }
    }

    public static Result run(Graph graph) {
        if (graph.directed) {
            throw new IllegalArgumentException("Kruskal requires an undirected graph");
        }

        List<Edge> sorted = new ArrayList<>(graph.edges);
        sorted.sort(Comparator.comparingInt(e -> e.weight));

        DSU dsu = new DSU(graph.vertices);
        List<Edge> mst = new ArrayList<>();

        long totalWeight = 0;
        long operations = 0;

        for (Edge edge : sorted) {
            operations++;

            if (dsu.union(edge.from, edge.to)) {
                mst.add(edge);
                totalWeight += edge.weight;
            }

            if (mst.size() == graph.vertices - 1) {
                break;
            }
        }

        boolean connected = dsu.getComponents() == 1;

        return new Result(mst, totalWeight, connected, operations);
    }
}