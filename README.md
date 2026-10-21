# Assignment 3 — Smart City Infrastructure and Route Planning

## 1. Overview

This project implements a graph-based system for modeling smart city infrastructure and solving network optimization problems.

The city is represented as a weighted graph:

* Vertices represent city districts or locations.
* Edges represent roads or infrastructure connections.
* Edge weights represent construction costs or travel distances.

The system provides algorithms for minimum spanning trees, connectivity analysis, topological ordering, strongly connected components, and shortest-path computation.

The main objective is to study the correctness, performance, and computational complexity of graph algorithms on graphs of different sizes.

## 2. Project Structure

```
assignment-3/
├── src/
│   ├── graph/
│   │   ├── Edge.java
│   │   ├── Graph.java
│   │   └── GraphGenerator.java
│   ├── dsu/
│   │   └── DSU.java
│   ├── mst/
│   │   ├── Kruskal.java
│   │   └── Prim.java
│   ├── connectivity/
│   │   ├── Kosaraju.java
│   │   └── TopologicalSort.java
│   ├── shortestpath/
│   │   └── Dijkstra.java
│   ├── benchmark/
│   │   └── Benchmark.java
│   └── Main.java
├── results/
│   ├── tables/
│   │   └── benchmark.csv
│   └── plots/
│       ├── execution_time_vs_vertices.svg
│       └── execution_time_vs_edges.svg
└── README.md
```

## 3. Implementation

### 3.1 Graph Representation

The graph is represented using an adjacency list and an edge list.

The adjacency list provides efficient access to neighboring vertices, while the edge list allows algorithms to process all graph edges.

The implementation supports directed and undirected graphs.

Edge weights are non-negative integers. This restriction is compatible with Dijkstra's shortest-path algorithm.

### 3.2 Disjoint Set Union (DSU)

The DSU data structure maintains a collection of disjoint sets.

It implements two operations:

* `find(x)` identifies the representative of the set containing vertex `x`.
* `union(a, b)` merges two sets if they are different.

Two optimizations are implemented:

**Path Compression**

During a find operation, vertices are connected directly to the representative of their set. This reduces the depth of the trees.

**Union by Rank**

When two sets are merged, the root of the smaller-rank tree is attached to the root of the larger-rank tree.

Together, these optimizations provide nearly constant amortized time per operation.

### 3.3 Kruskal's Algorithm

Kruskal's algorithm constructs a minimum spanning tree (MST).

Implementation steps:

1. Sort all edges in non-decreasing order of weight.
2. Initialize DSU with every vertex in its own set.
3. Process the sorted edges.
4. Add an edge if its endpoints belong to different components.
5. Merge the corresponding components using DSU.
6. Stop when the spanning tree contains `V - 1` edges.

The resulting spanning tree minimizes the total edge weight.

For a disconnected graph, the algorithm produces a minimum spanning forest rather than a spanning tree.

### 3.4 Prim's Algorithm

Prim's algorithm constructs a minimum spanning tree by growing a tree from a starting vertex.

Implementation steps:

1. Select an initial vertex.
2. Add its outgoing edges to a priority queue.
3. Select the minimum-weight edge leading to an unvisited vertex.
4. Add the edge and mark the new vertex as visited.
5. Insert the new vertex's eligible outgoing edges into the queue.
6. Continue until all vertices are visited or no eligible edges remain.

A priority queue is used to select the minimum-weight candidate edge.

For a disconnected graph, the implementation reports that the graph is not connected.

### 3.5 Strongly Connected Components — Kosaraju's Algorithm

Kosaraju's algorithm identifies strongly connected components (SCCs) in a directed graph.

Two depth-first search passes are performed:

1. Run DFS on the original graph and record vertices in finishing order.
2. Reverse the graph's edges.
3. Process vertices in reverse finishing order.
4. Run DFS on the reversed graph to identify each component.

Every vertex belongs to exactly one strongly connected component.

### 3.6 Topological Ordering

Topological ordering is implemented using Kahn's algorithm.

The algorithm:

1. Calculates the indegree of every vertex.
2. Adds all zero-indegree vertices to a queue.
3. Removes vertices from the queue and appends them to the ordering.
4. Decreases the indegree of their neighboring vertices.
5. Adds newly available zero-indegree vertices to the queue.

If every vertex is processed, the graph is a directed acyclic graph (DAG), and the resulting sequence is a valid topological ordering.

If some vertices remain unprocessed, the graph contains a directed cycle and no topological ordering exists.

### 3.7 Dijkstra's Algorithm

Dijkstra's algorithm calculates shortest-path distances from a selected source vertex to every reachable vertex.

A priority queue is used to process vertices in increasing order of their current tentative distances.

For each examined edge, the algorithm checks whether the path through the current vertex improves the known distance.

When an improvement is found, the distance and predecessor are updated.

The implementation returns:

* Shortest distances.
* Predecessor information.
* Reconstructed paths.
* The number of examined edges.

Unreachable vertices are represented by `Long.MAX_VALUE`.

Dijkstra's algorithm is not suitable for graphs with negative edge weights because a previously finalized shortest distance could later be improved by a path containing a negative-weight edge.

## 4. Complexity Analysis

Let:

* `V` denote the number of vertices.
* `E` denote the number of edges.
* `α(V)` denote the inverse Ackermann function.

The following complexities assume adjacency lists and a binary heap where applicable.

| Algorithm                   | Time Complexity     | Auxiliary Space                           |
| --------------------------- | ------------------- | ----------------------------------------- |
| DSU find                    | `O(α(V))` amortized | `O(V)` for the DSU structure              |
| DSU union                   | `O(α(V))` amortized | `O(V)` for the DSU structure              |
| Kruskal                     | `O(E log E)`        | `O(V + E)`                                |
| Prim with a binary heap     | `O(E log V)`        | `O(V + E)`                                |
| Kosaraju's algorithm        | `O(V + E)`          | `O(V + E)`                                |
| Topological ordering        | `O(V + E)`          | `O(V)` auxiliary, excluding graph storage |
| Dijkstra with a binary heap | `O((V + E) log V)`  | `O(V + E)`                                |

### 4.1 Effect of Graph Size

As the number of vertices increases, the algorithms must process additional locations and maintain more graph-related information.

As the number of edges increases:

* Kruskal processes and sorts more edges.
* Prim may examine more candidate edges.
* Kosaraju performs more adjacency-list traversal work.
* Topological ordering processes additional edges when updating indegrees.
* Dijkstra examines more edges during distance relaxation.

The actual execution time also depends on graph structure, implementation details, runtime conditions, and data structure overhead.

## 5. Experimental Setup

The experiments use three graph configurations specified in the assignment.

| Configuration | Vertices (V) | Edges (E) |
| ------------- | -----------: | --------: |
| Small         |           10 |        30 |
| Medium        |          100 |       500 |
| Large         |        1,000 |     5,000 |

### 5.1 Graph Generation

The graph generator uses a fixed random seed to make graph generation reproducible.

For the undirected MST experiments, a spanning tree is generated first. Additional edges are then added until the requested edge count is reached.

This ensures that the generated undirected graphs are connected.

Edge weights are positive integers between 1 and 100.

The directed graph generator creates directed edges with non-negative weights. Directed graphs may contain cycles.

### 5.2 Measurement Procedure

Execution time is measured using `System.nanoTime()`.

Each benchmark configuration is evaluated five times.

The recorded execution time is the arithmetic mean of the five measurements.

The benchmark records:

* Number of vertices.
* Number of edges.
* Average Kruskal execution time.
* Average Prim execution time.
* Average Dijkstra execution time.
* Kruskal operation count.
* Prim operation count.
* Dijkstra operation count.
* MST total weight.

The CSV output is saved to:

`results/tables/benchmark.csv`

The execution-time plots are saved to:

* `results/plots/execution_time_vs_vertices.svg`
* `results/plots/execution_time_vs_edges.svg`

### 5.3 Limitations of the Benchmark

The three configurations use the same graph for the five repetitions within each configuration.

This supports comparisons between algorithms on identical input data.

The reported measurements are subject to JVM warm-up, garbage collection, operating-system scheduling, and other runtime effects.

The benchmark currently measures Kruskal, Prim, and Dijkstra. Kosaraju's algorithm and topological ordering are implemented separately and should be benchmarked independently for a complete experimental comparison of all required algorithms.

## 6. Results

The benchmark writes measured results to `results/tables/benchmark.csv`.

The CSV file contains the average execution time and operation counts for each graph configuration.

### 6.1 Kruskal vs. Prim

Both algorithms compute a minimum spanning tree for connected, undirected, weighted graphs.

Their total MST weights should be identical, although the selected edges may differ when multiple minimum spanning trees exist.

Kruskal sorts edges globally and uses DSU to avoid cycles.

Prim expands a tree from a starting vertex and uses a priority queue to select the next candidate edge.

Their measured execution times should be compared using the generated CSV data rather than assumed in advance.

### 6.2 Dijkstra

Dijkstra's execution time is measured for each graph configuration using vertex `0` as the source.

The algorithm computes shortest paths to all reachable vertices.

Its performance depends on the graph's vertices, edges, priority queue operations, and distance-relaxation steps.

### 6.3 Execution-Time Plots

The generated SVG plots visualize execution time against:

* Number of vertices.
* Number of edges.

The plots include the measured execution times of Kruskal, Prim, and Dijkstra.

They provide a visual comparison of how the measured runtime changes as the graph grows.

Actual conclusions should be based on the values recorded in the CSV file and the generated plots.

## 7. Testing and Correctness

### 7.1 The MST Implementation

The implementation should be evaluated using the following test cases.

| Test Case                         | Expected Behavior                                    |
| --------------------------------- | ---------------------------------------------------- |
| Small connected undirected graph  | Both MST algorithms return a spanning tree           |
| Disconnected undirected graph     | MST algorithms report that the graph is disconnected |
| Graph with multiple valid MSTs    | Kruskal and Prim return the same total MST weight    |
| Graph with duplicate edge weights | MST construction remains correct                     |
| Single-vertex graph               | MST weight is zero and the graph is connected        |
| Directed graph with a cycle       | Topological ordering is rejected                     |
| Directed acyclic graph            | A valid topological ordering is returned             |
| Directed graph with SCCs          | Every vertex belongs to exactly one SCC              |
| Graph with unreachable vertices   | Dijkstra identifies unreachable vertices             |
| Graph with non-negative weights   | Dijkstra returns correct shortest distances          |

For connected, undirected, weighted graphs, the benchmark verifies that Kruskal and Prim both report a connected spanning tree and return the same total MST weight.

### 7.2 Additional Validation

Before submission, additional tests should verify:

* The exact edge count of each generated graph.
* Correct path reconstruction in Dijkstra.
* Correct SCC membership for graphs containing several components.
* The validity of every edge ordering in a topological result.
* Correct behavior on graphs containing self-loops if such inputs are supported by the intended test cases.
* The handling of a single vertex and disconnected graphs.

## 8. How to Run

### Requirements

* Java Development Kit (JDK).
* IntelliJ IDEA or another Java IDE.

### Running the Application

1. Open the project in IntelliJ IDEA.
2. Ensure all source files are located in their corresponding packages.
3. Open `src/Main.java`.
4. Run the `main()` method.
5. Inspect the console output.
6. Open `results/tables/benchmark.csv` to inspect the measurements.
7. Open the SVG files in `results/plots/` to view the charts.

The benchmark creates the output directories automatically if they do not exist.

The output paths are relative to the application's working directory.

## 9. Conclusion

This project demonstrates the application of fundamental graph algorithms to smart city infrastructure planning and route optimization.

Kruskal's and Prim's algorithms solve the minimum spanning tree problem using different strategies.

DSU efficiently maintains connected components during Kruskal's execution.

Kosaraju's algorithm identifies strongly connected components in directed networks, while topological ordering determines a valid dependency sequence when the graph is acyclic.

Dijkstra's algorithm calculates shortest paths when all edge weights are non-negative.

The experimental results allow the theoretical complexity of the algorithms to be compared with their measured execution times.

Together, these algorithms provide a foundation for analyzing infrastructure connectivity, minimizing network construction costs, and planning efficient routes.

## 10. Reproducibility

The project uses a fixed random seed for graph generation.

The benchmark configurations, graph-generation procedures, and timing method are documented in this report.

The source code and generated result files should be committed to the repository so that the implementation and experimental output can be inspected and reproduced.
