import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.Prim;
import connectivity.Kosaraju;
import connectivity.TopologicalSort;
import shortestpath.Dijkstra;
import benchmark.Benchmark;

public class Main {
    public static void main(String[] args) {
        Graph graph = GraphGenerator.connectedUndirected(6, 10, 42);

        System.out.println("Kruskal");
        Kruskal.Result kruskal = Kruskal.run(graph);

        for (Edge edge : kruskal.edges) {
            System.out.println(edge);
        }

        System.out.println("MST weight: " + kruskal.totalWeight);
        System.out.println("Connected: " + kruskal.connected);

        System.out.println("\nPrim");
        Prim.Result prim = Prim.run(graph);

        for (Edge edge : prim.edges) {
            System.out.println(edge);
        }

        System.out.println("MST weight: " + prim.totalWeight);
        System.out.println("Connected: " + prim.connected);

        System.out.println("\nDijkstra");
        Dijkstra.Result shortest = Dijkstra.run(graph, 0);

        for (int i = 0; i < graph.vertices; i++) {
            System.out.print("Vertex " + i + ": ");

            if (shortest.distances[i] == Long.MAX_VALUE) {
                System.out.println("unreachable");
            } else {
                System.out.println(
                        "distance=" + shortest.distances[i]
                                + ", path=" + shortest.getPath(i)
                );
            }
        }

        System.out.println("\nStrongly Connected Components");
        Graph directed = new Graph(6, true);

        directed.addEdge(0, 1, 1);
        directed.addEdge(1, 2, 1);
        directed.addEdge(2, 0, 1);
        directed.addEdge(2, 3, 1);
        directed.addEdge(3, 4, 1);
        directed.addEdge(4, 3, 1);
        directed.addEdge(4, 5, 1);

        System.out.println(Kosaraju.findSCC(directed));

        System.out.println("\nTopological Sort");
        Graph dag = new Graph(6, true);

        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        dag.addEdge(3, 4, 1);
        dag.addEdge(4, 5, 1);

        System.out.println(TopologicalSort.sort(dag));

        System.out.println("\nBenchmark");
        Benchmark.run();
    }
}