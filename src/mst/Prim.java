package mst;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class Prim {
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
            throw new IllegalArgumentException("Prim requires an undirected graph");
        }

        boolean[] visited = new boolean[graph.vertices];

        PriorityQueue<Edge> queue = new PriorityQueue<>(
                Comparator.comparingInt(e -> e.weight)
        );

        List<Edge> mst = new ArrayList<>();
        long totalWeight = 0;
        long operations = 0;

        visited[0] = true;
        queue.addAll(graph.adjacency.get(0));

        while (!queue.isEmpty() && mst.size() < graph.vertices - 1) {
            Edge edge = queue.poll();
            operations++;

            if (visited[edge.to]) {
                continue;
            }

            visited[edge.to] = true;
            mst.add(edge);
            totalWeight += edge.weight;

            for (Edge next : graph.adjacency.get(edge.to)) {
                operations++;

                if (!visited[next.to]) {
                    queue.offer(next);
                }
            }
        }

        boolean connected = mst.size() == graph.vertices - 1;

        return new Result(mst, totalWeight, connected, operations);
    }
}