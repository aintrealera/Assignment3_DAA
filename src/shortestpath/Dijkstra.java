package shortestpath;

import graph.Edge;
import graph.Graph;

import java.util.*;

public class Dijkstra {
    public static class Result {
        public final long[] distances;
        public final int[] previous;
        public final long operations;

        public Result(long[] distances, int[] previous, long operations) {
            this.distances = distances;
            this.previous = previous;
            this.operations = operations;
        }

        public List<Integer> getPath(int target) {
            if (distances[target] == Long.MAX_VALUE) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();

            for (int v = target; v != -1; v = previous[v]) {
                path.add(v);
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Result run(Graph graph, int source) {
        if (source < 0 || source >= graph.vertices) {
            throw new IllegalArgumentException("Invalid source");
        }

        long[] distances = new long[graph.vertices];
        int[] previous = new int[graph.vertices];

        Arrays.fill(distances, Long.MAX_VALUE);
        Arrays.fill(previous, -1);

        PriorityQueue<long[]> queue = new PriorityQueue<>(
                Comparator.comparingLong(a -> a[1])
        );

        distances[source] = 0;
        queue.offer(new long[]{source, 0});

        long operations = 0;

        while (!queue.isEmpty()) {
            long[] current = queue.poll();

            int vertex = (int) current[0];
            long distance = current[1];

            if (distance != distances[vertex]) {
                continue;
            }

            for (Edge edge : graph.adjacency.get(vertex)) {
                operations++;

                long newDistance = distance + edge.weight;

                if (newDistance < distances[edge.to]) {
                    distances[edge.to] = newDistance;
                    previous[edge.to] = vertex;

                    queue.offer(new long[]{
                            edge.to,
                            newDistance
                    });
                }
            }
        }

        return new Result(distances, previous, operations);
    }
}