package graph;

import java.util.*;

public class Graph {
    public final int vertices;
    public final boolean directed;
    public final List<Edge> edges;
    public final List<List<Edge>> adjacency;

    public Graph(int vertices, boolean directed) {
        if (vertices < 1) {
            throw new IllegalArgumentException("Graph must have at least one vertex");
        }

        this.vertices = vertices;
        this.directed = directed;
        this.edges = new ArrayList<>();
        this.adjacency = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= vertices || to < 0 || to >= vertices) {
            throw new IllegalArgumentException("Invalid vertex");
        }

        if (weight < 0) {
            throw new IllegalArgumentException("Negative weight");
        }

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adjacency.get(from).add(edge);

        if (!directed) {
            adjacency.get(to).add(new Edge(to, from, weight));
        }
    }

    public Graph reversed() {
        Graph reversed = new Graph(vertices, directed);

        for (Edge edge : edges) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
        }

        return reversed;
    }
}