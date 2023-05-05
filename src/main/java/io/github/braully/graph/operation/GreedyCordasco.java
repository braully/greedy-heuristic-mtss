package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class GreedyCordasco
        extends GenericGreedy implements IGraphOperation {

    static final Logger log = Logger.getLogger(GreedyCordasco.class.getSimpleName());
    static final String description = "Greedy-Cordasco";

    public String getDescription() {
        return description;
    }

    public Set<Integer> buildTargeSet(UndirectedSparseGraphTO<Integer, Integer> graph) {
        if (graph == null) {
            return null;
        }
        Set<Integer> vertices = new LinkedHashSet<>((List<Integer>) graph.getVertices());

        Set<Integer> targetSet = new LinkedHashSet<>();

        Integer maxVertex = (Integer) graph.maxVertex() + 1;
        int vertexCount = graph.getVertexCount();

        int[] delta = new int[maxVertex];
        int[] k = new int[maxVertex];
        degree = new int[maxVertex];
        Set<Integer>[] N = new Set[maxVertex];
        this.N = new Set[maxVertex];
        initKr(graph);

        for (Integer i : vertices) {
            degree[i] = graph.degree(i);
            delta[i] = graph.degree(i);
            k[i] = kr[i];
            N[i] = new LinkedHashSet<>(graph.getNeighborsUnprotected(i));
            this.N[i] = new LinkedHashSet<>(graph.getNeighborsUnprotected(i));
        }
        Set<Integer> U = vertices;
        Set<Integer> S = targetSet;
        while (U.size() > 0) {
            Integer v = null;
            int min_d = Integer.MAX_VALUE;
            for (Integer u : U) {
                if (k[u] < min_d) {
                    min_d = k[u];
                    v = u;
                }
            }
            if (k[v] > 0) {
                double max_x = -1;
                for (Integer u : U) {
                    double x = N[u].size();
                    if (x > max_x) {
                        max_x = x;
                        v = u;
                    }
                }
                S.add(v);
            }

            //v será dominado por seus vizinhos
            for (Integer u : N[v]) {
                k[u] = Math.max(0, k[u] - 1);
                N[u].remove(v);
                delta[u] = delta[u] - 1;
            }
            U.remove(v);
        }
        if (refine) {
            targetSet = refineResultStep1(graph, targetSet, vertexCount);
        }
        return targetSet;
    }

    public static void main(String... args) throws IOException {
        System.out.println("Execution Sample: BlogCatalog database R=2");
        UndirectedSparseGraphTO<Integer, Integer> graph = null;
        GreedyCordasco op = new GreedyCordasco();

//        URI urinode = URI.create("jar:file:data/big/all-big.zip!/Livemocha/nodes.csv");
//        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/Livemocha/edges.csv");
        URI urinode = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog/nodes.csv");
        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog/edges.csv");
        InputStream streamnode = urinode.toURL().openStream();
        InputStream streamedges = uriedges.toURL().openStream();

        graph = UtilGraph.loadBigDataset(streamnode, streamedges);

        op.setVerbose(true);

        op.setPercent(0.7);
        UtilProccess.printStartTime();
        Set<Integer> buildOptimizedHullSet = op.buildTargeSet(graph);
        UtilProccess.printEndTime();

        System.out.println(
                "S[" + buildOptimizedHullSet.size() + "]: "
                + buildOptimizedHullSet
        );
    }

}
