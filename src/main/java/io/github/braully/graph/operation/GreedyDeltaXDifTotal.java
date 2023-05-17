package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.util.UtilBFS;
import io.github.braully.graph.util.MapCountOpt;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreedyDeltaXDifTotal
        extends AbstractHeuristic implements IGraphOperation {

    static final Logger log = Logger.getLogger(GreedyDeltaXDifTotal.class.getSimpleName());
    static final String description = "GreedyDeltaXDifTotal";

    public String getDescription() {
        return description;
    }

    public String getName() {
        return description;
    }

    public GreedyDeltaXDifTotal() {
    }

    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Integer hullNumber = 0;
        Set<Integer> minHullSet = null;

        try {
            String inputData = graph.getInputData();
            if (inputData != null) {
                int parseInt = Integer.parseInt(inputData.trim());
                setR(parseInt);
            }
        } catch (Exception e) {

        }

        try {
            minHullSet = buildTargeSet(graph);
            if (minHullSet != null && !minHullSet.isEmpty()) {
                hullNumber = minHullSet.size();
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        }

        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();
        response.put("R", this.rTreshold);
        response.put(PARAM_NAME_HULL_NUMBER, hullNumber);
        response.put(PARAM_NAME_HULL_SET, minHullSet);
        response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, hullNumber);
        return response;
    }

    int[] skip = null;
    int[] auxb = null;
    //
    protected UtilBFS bdls;

    protected Queue<Integer> mustBeIncluded = new ArrayDeque<>();
    protected MapCountOpt auxCount;
    protected int bestVertice = -1;

    protected double maxDifTotal = 0;
    protected int maxDelta = 0;
    protected double maxBonusPartial = 0;
    protected int maxRank = 0;

    //has uncontaminated vertices on the current component
    protected boolean hasVerticesOnCC = false;

    public Set<Integer> buildTargeSet(UndirectedSparseGraphTO<Integer, Integer> graph) {
        if (graph == null) {
            return null;
        }
        List<Integer> vertices = new ArrayList<>((List<Integer>) graph.getVertices());
        //Sort vertice on reverse order of degree
        vertices.sort(Comparator
                .comparingInt((Integer v) -> -graph.degree(v))
        );
        //
        Set<Integer> targetSet = new LinkedHashSet<>();
        Set<Integer> saux = new LinkedHashSet<>();

        Integer maxVertex = (Integer) graph.maxVertex() + 1;

        int[] aux = new int[maxVertex];
        degree = new int[maxVertex];
        skip = new int[maxVertex];
        auxb = new int[maxVertex];
        N = new Set[maxVertex];

        for (Integer i : vertices) {
            aux[i] = 0;
            skip[i] = -1;
            N[i] = new LinkedHashSet<>(graph.getNeighborsUnprotected(i));
        }
        initKr(graph);

        int countContaminatedVertices = 0;
        //mandatory vertices
        for (Integer v : vertices) {
            degree[v] = graph.degree(v);

            if (degree[v] <= kr[v] - 1) {
                countContaminatedVertices = countContaminatedVertices + addVertToS(v, saux, graph, aux);
            }
            if (kr[v] == 0) {
                countContaminatedVertices = countContaminatedVertices + addVertToAux(v, graph, aux);
            }
        }

        int vertexCount = graph.getVertexCount();
        int offset = 0;

        //BFS for find vertices in current component
        bdls = UtilBFS.newBfsUtilSimple(maxVertex);
        bdls.labelDistances(graph, saux);

        bestVertice = -1;
        auxCount = new MapCountOpt(maxVertex);

        while (countContaminatedVertices < vertexCount) {
            if (bestVertice != -1) {
                bdls.incBfs(graph, bestVertice);
            }
            bestVertice = -1;
            maxDifTotal = 0;
            maxDelta = 0;
            maxBonusPartial = 0;

            for (Integer w : vertices) {
                //Ignore w if is already contamined OR skip review to next step
                if (aux[w] >= kr[w] || skip[w] >= countContaminatedVertices) {
                    continue;
                }
                // Ignore w if not acessible in current component of G
                int distanceForSaux = bdls.getDistanceSafe(graph, w);
                if (distanceForSaux == -1 && (countContaminatedVertices > 0 && !hasVerticesOnCC)) {
                    continue;
                }

                int wDelta = 0;
                double wPartialBonus = 0;
                int wDifDelta = 0;

                //Clear and init w contamined count aux variavles
                auxCount.clear();
                auxCount.setVal(w, kr[w]);
                mustBeIncluded.clear();
                mustBeIncluded.add(w);
                //Propagate w contamination
                while (!mustBeIncluded.isEmpty()) {
                    Integer verti = mustBeIncluded.remove();
                    Collection<Integer> neighbors = N[verti];
                    for (Integer vertn : neighbors) {
                        if ((aux[vertn] + auxCount.getCount(vertn)) >= kr[vertn]) {
                            continue;
                        }
                        Integer inc = auxCount.inc(vertn);
                        if ((inc + aux[vertn]) == kr[vertn]) {
                            mustBeIncluded.add(vertn);
                            skip[vertn] = countContaminatedVertices;
                        }
                    }
                    int currentDifficultyContamination = (kr[verti] - aux[verti]);
                    wDifDelta += currentDifficultyContamination;
                    wDelta++;
                }
                int wRank = wDelta * wDifDelta;
                if (bestVertice == -1 || wRank > maxRank) {
                    bestVertice = w;
                    maxDelta = wDelta;
                    maxDifTotal = wDifDelta;
                    maxRank = wRank;
                }
            }
            //Ended the current component of G
            if (bestVertice == -1) {
                hasVerticesOnCC = true;
                saux = refineResult(graph, saux, countContaminatedVertices - offset);

                offset = countContaminatedVertices;
                targetSet.addAll(saux);
                saux.clear();
                bdls.clearBfs();
                continue;
            }
            hasVerticesOnCC = false;
            //Add vert to S
            countContaminatedVertices = countContaminatedVertices + addVertToS(bestVertice, saux, graph, aux);
            bdls.incBfs(graph, bestVertice);
        }
        saux = refineResultStep1(graph, saux, countContaminatedVertices - offset);
//        saux = refineResultStep2(graph, saux, countContaminatedVertices - offset);

        targetSet.addAll(saux);
        saux.clear();
        return targetSet;
    }

    public int addVertToAux(Integer verti,
            UndirectedSparseGraphTO<Integer, Integer> graph,
            int[] aux) {
        int countIncluded = 0;
        if (verti == null) {
            return countIncluded;
        }
        if (kr[verti] > 0 && aux[verti] >= kr[verti]) {
            return countIncluded;
        }

        aux[verti] = aux[verti] + kr[verti];
        mustBeIncluded.clear();
        mustBeIncluded.add(verti);
        while (!mustBeIncluded.isEmpty()) {
            verti = mustBeIncluded.remove();
            Collection<Integer> neighbors = N[verti];
            for (Integer vertn : neighbors) {
                if ((++aux[vertn]) == kr[vertn]) {
                    mustBeIncluded.add(vertn);
                }
            }
            countIncluded++;
        }

        return countIncluded;
    }

    public int addVertToS(Integer verti, Set<Integer> s,
            UndirectedSparseGraphTO<Integer, Integer> graph,
            int[] aux) {
        int countIncluded = 0;
        if (verti == null) {
            return countIncluded;
        }
        if (kr[verti] > 0 && aux[verti] >= kr[verti]) {
            return countIncluded;
        }

        aux[verti] = aux[verti] + kr[verti];
        if (s != null) {
            s.add(verti);
        }
        mustBeIncluded.clear();
        mustBeIncluded.add(verti);
        while (!mustBeIncluded.isEmpty()) {
            verti = mustBeIncluded.remove();
            Collection<Integer> neighbors = N[verti];
            for (Integer vertn : neighbors) {
                if ((++aux[vertn]) == kr[vertn]) {
                    mustBeIncluded.add(vertn);
                }
            }
            countIncluded++;
        }

        return countIncluded;
    }

    protected int[] scount = null;

    public Set<Integer> refineResultStep1(UndirectedSparseGraphTO<Integer, Integer> graphRead,
            Set<Integer> tmp, int tamanhoAlvo) {
        Set<Integer> s = new LinkedHashSet<>(tmp);

        for (Integer v : tmp) {
            Collection<Integer> nvs = N[v];
            int scont = 0;
            for (Integer nv : nvs) {
                if (s.contains(nv)) {
                    scont++;
                }
            }
            if (scont >= kr[v]) {
                s.remove(v);
            }
        }
        return s;
    }

    public Set<Integer> refineResultStep2(UndirectedSparseGraphTO<Integer, Integer> graphRead,
            Set<Integer> tmp, int tamanhoAlvo) {
        Set<Integer> s = tmp;

        if (s.size() <= 1) {
            return s;
        }

        if (verbose) {
            System.out.println("tentando reduzir: " + s.size());
//            System.out.println("s: " + s);
        }
        int cont = 0;
        for (Integer v : tmp) {
            cont++;
            if (graphRead.degree(v) < kr[v]) {
                continue;
            }
            Set<Integer> t = new LinkedHashSet<>(s);
            t.remove(v);

            int contadd = 0;
            int[] aux = auxb;

            for (int i = 0; i < aux.length; i++) {
                aux[i] = 0;
            }

            mustBeIncluded.clear();
            for (Integer iv : t) {
                mustBeIncluded.add(iv);
                aux[iv] = kr[iv];
            }
            while (!mustBeIncluded.isEmpty()) {
                Integer verti = mustBeIncluded.remove();
                contadd++;
                Collection<Integer> neighbors = N[verti];
                for (Integer vertn : neighbors) {
                    if (aux[vertn] <= kr[vertn] - 1) {
                        aux[vertn] = aux[vertn] + 1;
                        if (aux[vertn] == kr[vertn]) {
                            mustBeIncluded.add(vertn);
                        }
                    }
                }
                aux[verti] += kr[verti];
            }

            if (contadd >= tamanhoAlvo) {
                if (verbose) {
                    System.out.println(" - removido: " + v + " na pos " + cont + "/" + s.size() + " det " + v + ": " + degree[v]
                            + "/" + kr[v] + " " + ((float) kr[v] * 100 / (float) degree[v]));

                }
                s = t;
            }
        }
        if (verbose) {
            int delt = tmp.size() - s.size();
            if (delt > 0) {
                System.out.println(tmp.size() + "/" + s.size() + " removido " + delt + " vertices");
            }
        }
        return s;
    }

    public Set<Integer> refineResult(UndirectedSparseGraphTO<Integer, Integer> graph, Set<Integer> s, int targetSize) {
        s = refineResultStep1(graph, s, targetSize);
//        s = refineResultStep2(graph, s, targetSize);
        return s;
    }

    public static void main(String... args) throws IOException {
        System.out.println("Execution Sample: Livemocha database R=2");
        UndirectedSparseGraphTO<Integer, Integer> graph = null;
        GreedyDeltaXDifTotal op = new GreedyDeltaXDifTotal();

//        URI urinode = URI.create("jar:file:data/big/all-big.zip!/Livemocha/nodes.csv");
//        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/Livemocha/edges.csv");
//        URI urinode = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog/nodes.csv");
//        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog/edges.csv");
        URI urinode = URI.create("jar:file:data/big/all-big.zip!/Last.fm/nodes.csv");
        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/Last.fm/edges.csv");
//        URI urinode = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog3/nodes.csv");
//        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/BlogCatalog3/edges.csv");
        InputStream streamnode = urinode.toURL().openStream();
        InputStream streamedges = uriedges.toURL().openStream();

        graph = UtilGraph.loadBigDataset(streamnode, streamedges);

        op.setVerbose(true);

        op.setPercent(0.1);
        UtilProccess.printStartTime();
        Set<Integer> buildOptimizedHullSet = op.buildTargeSet(graph);
        UtilProccess.printEndTime();

        System.out.println(
                "S[" + buildOptimizedHullSet.size() + "]: "
                + buildOptimizedHullSet
        );
    }

}
