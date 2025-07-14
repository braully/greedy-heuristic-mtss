package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import static io.github.braully.graph.operation.TSSCordasco.log;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.logging.Level;

public class VirAds extends AbstractHeuristic {

    @Override
    public String getDescription() {
        return "VirAds";
    }

    @Override
    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {

        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();
        Collection<Integer> s = null;
        int dmax = 100;

        for (int d = 1; d <= dmax; d += 10) {
            Collection<Integer> tmp = run(graph, d);
            if (s == null || tmp.size() < s.size()) {
                s = tmp;
            }
        }

        try {
            response.put("R", this.rTreshold);
            response.put("VirAds", "" + s);
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_SET, s);
            response.put("|VirAds|", s.size());
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, s.size());

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public Collection run(UndirectedSparseGraphTO<Integer, Integer> graph, int d) {

        int n = graph.getVertexCount();
        int[] nve = new int[n + 1];
        int[] nva = new int[n + 1];
        int[] rv = new int[n + 1];
        int[][] rvi = new int[n + 1][d + 2];
        boolean[] status = new boolean[n + 1];

        List<Integer> P = new ArrayList<>();

        for (Integer v : graph.getVertices()) {
            nve[v] = graph.degree(v);
//            nva[v] = (int) graph.getVertexAttribute(v, "threshold");
            nva[v] = kr[v];
            rv[v] = d + 1;
            status[v] = false;
        }

        long inactiveCount = n;

        while (inactiveCount > 0) {
            final int[] currentNve = nve;
            final int[] currentNva = nva;

            Optional<Integer> uOpt = graph.getVertices().stream()
                    .filter(v -> !status[v])
                    .max(Comparator.comparingInt(v -> currentNve[v] + currentNva[v]));

            if (!uOpt.isPresent()) {
                break;
            }
            int u = uOpt.get();

            P.add(u);
            nva[u] = 0;
            nve[u] = 0;
            status[u] = true;

            Queue<int[]> Q = new ArrayDeque<>();
            Q.add(new int[]{u, rv[u]});

            for (Integer neighborU : graph.getNeighbors(u)) {
                if (!status[neighborU]) {
                    nva[neighborU] = Math.max(nva[neighborU] - 1, 0);
                    if (nva[neighborU] == 0) {
                        status[neighborU] = true;
                    }
                }
            }

            while (!Q.isEmpty()) {
                int[] current = Q.poll();
                int tId = current[0];
                int tRv = current[1];

                for (Integer w : graph.getNeighbors(tId)) {
                    if (status[w]) {
                        continue;
                    }

                    int upperLimit = Math.min(tRv - 1, rv[w] - 2);
                    for (int i = rv[tId]; i <= upperLimit; i++) {
                        if (i + 1 < rvi[w].length) {
                            rvi[w][i + 1]++;
                            if (rvi[w][i + 1] >= kr[w]) {
//                            if (rvi[w][i + 1] >= (int) graph.getVertexAttribute(w, "threshold")) {
                                if (rv[w] >= d && i + 1 < d) {
                                    for (Integer neighborW : graph.getNeighbors(w)) {
                                        if (!P.contains(neighborW) && !status[neighborW]) {
                                            nva[neighborW] = Math.max(nva[neighborW] - 1, 0);
                                            if (nva[neighborW] == 0) {
                                                status[neighborW] = true;
                                            }
                                        }
                                    }
                                }
                                rv[w] = i + 1;
                                if (rv[w] < d) {
                                    boolean inQueue = Q.stream().anyMatch(item -> item[0] == w);
                                    if (!inQueue) {
                                        Q.add(new int[]{w, rv[w]});
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }

            inactiveCount = 0;
            for (Integer v : graph.getVertices()) {
                if (!status[v]) {
                    inactiveCount++;
                }
            }
            System.out.printf("VirAds Inactive: %d%n", inactiveCount);
        }

        long endTime = System.currentTimeMillis();
        return P;
    }
}
