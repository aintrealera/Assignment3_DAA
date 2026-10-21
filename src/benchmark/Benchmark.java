package benchmark;

import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class Benchmark {
    private static final int REPETITIONS = 5;

    private static final Path TABLES =
            Paths.get("results", "tables");

    private static final Path PLOTS =
            Paths.get("results", "plots");

    public static void run() {
        try {
            Files.createDirectories(TABLES);
            Files.createDirectories(PLOTS);

            Path csvPath = TABLES.resolve("benchmark.csv");
            Path verticesPlot = PLOTS.resolve(
                    "execution_time_vs_vertices.svg"
            );
            Path edgesPlot = PLOTS.resolve(
                    "execution_time_vs_edges.svg"
            );

            int[][] configurations = {
                    {10, 30},
                    {100, 500},
                    {1000, 5000}
            };

            try (BufferedWriter writer =
                         Files.newBufferedWriter(csvPath)) {

                writer.write(
                        "Vertices,Edges,Kruskal_ns,Prim_ns,"
                                + "Dijkstra_ns,Kruskal_ops,"
                                + "Prim_ops,Dijkstra_ops,MST_weight"
                );
                writer.newLine();

                long[] vertices = new long[configurations.length];
                long[] edges = new long[configurations.length];
                long[] kruskalTimes = new long[configurations.length];
                long[] primTimes = new long[configurations.length];
                long[] dijkstraTimes = new long[configurations.length];

                for (int i = 0; i < configurations.length; i++) {
                    int v = configurations[i][0];
                    int e = configurations[i][1];

                    Graph graph =
                            GraphGenerator.connectedUndirected(v, e, 42);

                    long kruskalTime = 0;
                    long primTime = 0;
                    long dijkstraTime = 0;

                    long kruskalOps = 0;
                    long primOps = 0;
                    long dijkstraOps = 0;

                    long mstWeight = -1;

                    for (int j = 0; j < REPETITIONS; j++) {
                        long start = System.nanoTime();
                        Kruskal.Result kruskal = Kruskal.run(graph);
                        kruskalTime += System.nanoTime() - start;

                        start = System.nanoTime();
                        Prim.Result prim = Prim.run(graph);
                        primTime += System.nanoTime() - start;

                        start = System.nanoTime();
                        Dijkstra.Result dijkstra =
                                Dijkstra.run(graph, 0);
                        dijkstraTime += System.nanoTime() - start;

                        if (!kruskal.connected || !prim.connected
                                || kruskal.totalWeight
                                != prim.totalWeight) {
                            throw new IllegalStateException(
                                    "MST verification failed"
                            );
                        }

                        kruskalOps += kruskal.operations;
                        primOps += prim.operations;
                        dijkstraOps += dijkstra.operations;

                        mstWeight = kruskal.totalWeight;
                    }

                    vertices[i] = v;
                    edges[i] = e;

                    kruskalTimes[i] = kruskalTime / REPETITIONS;
                    primTimes[i] = primTime / REPETITIONS;
                    dijkstraTimes[i] = dijkstraTime / REPETITIONS;

                    writer.write(String.format(
                            Locale.US,
                            "%d,%d,%d,%d,%d,%d,%d,%d,%d",
                            v,
                            e,
                            kruskalTimes[i],
                            primTimes[i],
                            dijkstraTimes[i],
                            kruskalOps / REPETITIONS,
                            primOps / REPETITIONS,
                            dijkstraOps / REPETITIONS,
                            mstWeight
                    ));

                    writer.newLine();

                    System.out.println(
                            "Completed: V=" + v + ", E=" + e
                    );
                }

                writePlot(
                        verticesPlot,
                        "Execution Time vs Number of Vertices",
                        "Number of Vertices",
                        vertices,
                        kruskalTimes,
                        primTimes,
                        dijkstraTimes
                );

                writePlot(
                        edgesPlot,
                        "Execution Time vs Number of Edges",
                        "Number of Edges",
                        edges,
                        kruskalTimes,
                        primTimes,
                        dijkstraTimes
                );
            }

            System.out.println("\nResults saved successfully:");
            System.out.println(csvPath.toAbsolutePath());
            System.out.println(verticesPlot.toAbsolutePath());
            System.out.println(edgesPlot.toAbsolutePath());

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not save benchmark results",
                    exception
            );
        }
    }

    private static void writePlot(
            Path path,
            String title,
            String xLabel,
            long[] xValues,
            long[] kruskal,
            long[] prim,
            long[] dijkstra
    ) throws IOException {

        int width = 900;
        int height = 550;

        int left = 100;
        int right = 40;
        int top = 80;
        int bottom = 90;

        int plotWidth = width - left - right;
        int plotHeight = height - top - bottom;

        long maxX = 1;
        long maxY = 1;

        for (long value : xValues) {
            maxX = Math.max(maxX, value);
        }

        for (int i = 0; i < xValues.length; i++) {
            maxY = Math.max(maxY, kruskal[i]);
            maxY = Math.max(maxY, prim[i]);
            maxY = Math.max(maxY, dijkstra[i]);
        }

        StringBuilder svg = new StringBuilder();

        svg.append(String.format(
                Locale.US,
                "<svg xmlns=\"http://www.w3.org/2000/svg\" "
                        + "width=\"%d\" height=\"%d\" "
                        + "viewBox=\"0 0 %d %d\">",
                width, height, width, height
        ));

        svg.append("<rect width=\"100%\" height=\"100%\" fill=\"white\"/>");

        svg.append("<text x=\"450\" y=\"35\" text-anchor=\"middle\" "
                        + "font-size=\"22\" font-family=\"Arial\">")
                .append(title)
                .append("</text>");

        for (int i = 0; i <= 5; i++) {
            int y = top + plotHeight - i * plotHeight / 5;
            long value = maxY * i / 5;

            svg.append(String.format(
                    Locale.US,
                    "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" "
                            + "stroke=\"#dddddd\"/>",
                    left, y, width - right, y
            ));

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%d\" y=\"%d\" text-anchor=\"end\" "
                            + "font-size=\"12\" font-family=\"Arial\">%d</text>",
                    left - 10, y + 4, value
            ));
        }

        svg.append(String.format(
                Locale.US,
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" "
                        + "stroke=\"black\"/>",
                left, top, left, top + plotHeight
        ));

        svg.append(String.format(
                Locale.US,
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" "
                        + "stroke=\"black\"/>",
                left, top + plotHeight,
                width - right, top + plotHeight
        ));

        String[] names = {"Kruskal", "Prim", "Dijkstra"};
        String[] colors = {"#e74c3c", "#2980b9", "#27ae60"};
        long[][] series = {kruskal, prim, dijkstra};

        for (int s = 0; s < series.length; s++) {
            StringBuilder points = new StringBuilder();

            for (int i = 0; i < xValues.length; i++) {
                int x = left + (int) (
                        (double) xValues[i] / maxX * plotWidth
                );

                int y = top + plotHeight - (int) (
                        (double) series[s][i] / maxY * plotHeight
                );

                points.append(x).append(',').append(y).append(' ');

                svg.append(String.format(
                        Locale.US,
                        "<circle cx=\"%d\" cy=\"%d\" r=\"5\" fill=\"%s\"/>",
                        x, y, colors[s]
                ));
            }

            svg.append("<polyline fill=\"none\" stroke=\"")
                    .append(colors[s])
                    .append("\" stroke-width=\"2\" points=\"")
                    .append(points)
                    .append("\"/>");
        }

        for (int i = 0; i < xValues.length; i++) {
            int x = left + (int) (
                    (double) xValues[i] / maxX * plotWidth
            );

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" "
                            + "font-size=\"12\" font-family=\"Arial\">%d</text>",
                    x, top + plotHeight + 25, xValues[i]
            ));
        }

        svg.append(String.format(
                Locale.US,
                "<text x=\"%d\" y=\"%d\" text-anchor=\"middle\" "
                        + "font-size=\"14\" font-family=\"Arial\">%s</text>",
                left + plotWidth / 2, height - 35, xLabel
        ));

        String[] legendNames = {"Kruskal", "Prim", "Dijkstra"};

        for (int i = 0; i < legendNames.length; i++) {
            int x = 230 + i * 170;

            svg.append(String.format(
                    Locale.US,
                    "<rect x=\"%d\" y=\"%d\" width=\"14\" height=\"14\" "
                            + "fill=\"%s\"/>",
                    x, height - 65, colors[i]
            ));

            svg.append(String.format(
                    Locale.US,
                    "<text x=\"%d\" y=\"%d\" font-size=\"13\" "
                            + "font-family=\"Arial\">%s</text>",
                    x + 20, height - 53, legendNames[i]
            ));
        }

        svg.append("</svg>");

        Files.writeString(path, svg.toString());
    }
}