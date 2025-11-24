package io.github.braully.graph.operation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.util.CombinationsFacade;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;

/**
 * TSS Exact - Brute Force Optimized
 *
 * @author Braully Rocha da Silva
 */
public class TSSBruteForceOptmParalelo
        extends HNVEx implements IGraphOperation {

    static final String description = "TSS-BF-Optm";

    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Integer hullNumber = -1;
        Set<Integer> minHullSet = null;

        try {
            minHullSet = findHullSet(graph);
            hullNumber = minHullSet.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            // log.error(null, ex);
        }

        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();
        response.put(PARAM_NAME_HULL_NUMBER, hullNumber);
        response.put(PARAM_NAME_HULL_SET, minHullSet);
        response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, hullNumber);
        return response;
    }

    @Override
    public Set<Integer> findHullSet(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> ceilling = calcCeillingHullNumberGraph(graph);
        Set<Integer> hullSet = ceilling;
        if (graph == null || graph.getVertices().isEmpty()) {
            return ceilling;
        }

        int minSizeSet = 2;
        int currentSize = ceilling.size() - 1;
        int countOneNeigh = 0;

        if (currentSize > 0) {
            Collection<Integer> vertices = graph.getVertices();

            // for (Integer i : vertices) {
            // if (graph.degree(i) == 1) {
            // countOneNeigh++;
            // }
            // }
            // minSizeSet = Math.max(minSizeSet, countOneNeigh);
            if (verbose) {
                System.out.println(" - Teto heuristico: " + ceilling.size());
            }
            // System.out.println("Find hull number: min val " + minSizeSet);
            while (currentSize >= minSizeSet) {
                // System.out.println("Find hull number: current founded " + (currentSize + 1));
                // System.out.println("Find hull number: trying find " + currentSize);

                // System.out.println("trying : " + currentSize);
                Set<Integer> hs = findHullSetBruteForce(graph, currentSize);
                if (hs != null && !hs.isEmpty()) {
                    hullSet = hs;
                } else {
                    // System.out.println("not find break ");
                    break;
                }
                currentSize--;
            }
            if (verbose) {
                int delta = hullSet.size() - ceilling.size();
                if (delta == 0) {
                    System.out.println(" - Heuristica match");
                } else {
                    System.out.println(" - Heuristica fail by: " + delta);

                }
            }
        }
        return hullSet;
    }

    private Set<Integer> calcCeillingHullNumberGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> ceilling = new HashSet<>();
        if (graph != null) {
            Set<Integer> optimizedHullSet = super.buildHullSet(graph);
            if (optimizedHullSet != null) {
                ceilling.addAll(optimizedHullSet);
            }
        }
        return ceilling;
    }

    AtomicBoolean found = new AtomicBoolean(false);

    public Set<Integer> findHullSetBruteForce(UndirectedSparseGraphTO<Integer, Integer> graph, int currentSetSize) {
        Set<Integer> hullSet = null;
        if (graph == null || graph.getVertexCount() <= 0) {
            return hullSet;
        }
        int tamanhoAlvo = graph.getVertexCount();
        Set<Integer> obg = new HashSet<>();
        List<Integer> verticesElegiveis = new ArrayList<>();
        Collection<Integer> vertices = graph.getVertices();
        for (Integer v : vertices) {
            if (kr[v] > 0) {
                if (degree[v] >= kr[v]) {
                    verticesElegiveis.add(v);
                } else {
                    obg.add(v);
                }
            }
        }
        currentSetSize = currentSetSize - obg.size();
        int size = verticesElegiveis.size();
        if (size == 0 || currentSetSize <= 0) {
            return hullSet;
        }
        long maxCombinations = CombinationsFacade.maxCombinations(size, currentSetSize);
        long inicio = 0;
        long fim = maxCombinations;
        found.set(false);

        hullSet = findTssRangeParallel(currentSetSize, size, inicio, fim, verticesElegiveis, obg, graph, tamanhoAlvo);
        return hullSet;
    }

    private Set<Integer> findTssRangeParallel(int currentSetSize, int size, long inicio, long fim, 
            List<Integer> verticesElegiveis, Set<Integer> obg, 
            UndirectedSparseGraphTO<Integer, Integer> graph, int tamanhoAlvo) {
        
        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Set<Integer>>> futures = new ArrayList<>();
        
        long totalCombinations = fim - inicio;
        long chunkSize = totalCombinations / numThreads;
        
        // Dividir o intervalo em chunks para cada thread
        for (int i = 0; i < numThreads; i++) {
            final long chunkInicio = inicio + (i * chunkSize);
            final long chunkFim = (i == numThreads - 1) ? fim : chunkInicio + chunkSize;
            
            Callable<Set<Integer>> task = () -> {
                return findTssRange(currentSetSize, size, chunkInicio, chunkFim, 
                        verticesElegiveis, obg, graph, tamanhoAlvo);
            };
            
            futures.add(executor.submit(task));
        }
        
        Set<Integer> result = null;
        
        // Coletar resultados das threads
        for (Future<Set<Integer>> future : futures) {
            try {
                Set<Integer> partialResult = future.get();
                if (partialResult != null && result == null) {
                    result = partialResult;
                    // Cancelar as demais threads quando encontrar resultado
                    for (Future<Set<Integer>> f : futures) {
                        f.cancel(true);
                    }
                    break;
                }
            } catch (Exception e) {
                // Thread foi cancelada ou erro ocorreu
            }
        }
        
        executor.shutdownNow();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return result;
    }

    private Set<Integer> findTssRange(int currentSetSize, int size, long inicio, long fim, List<Integer> verticesElegiveis, Set<Integer> obg, UndirectedSparseGraphTO<Integer, Integer> graph, int tamanhoAlvo) {
        Set<Integer> hullSet = null;
        int[] aux = new int[auxb.length];
        Queue<Integer> mustBeIncluded = new ArrayDeque<>();
        int[] currentSet = new int[currentSetSize];
        CombinationsFacade.initialCombination(size, currentSetSize, currentSet, inicio);
        for (long combIndex = inicio; combIndex < fim && !found.get(); combIndex++) {
            // int[] currentSet = combinationsIterator.next();
            mustBeIncluded.clear();
            for (int i = 0; i < aux.length; i++) {
                aux[i] = 0;
                if (kr[i] == 0) {
                    mustBeIncluded.add(i);
                }
            }
            int contadd = 0;
            for (Integer i : currentSet) {
                Integer iv = verticesElegiveis.get(i);
                mustBeIncluded.add(iv);
                aux[iv] = kr[iv];
            }
            for (Integer iv : obg) {
                mustBeIncluded.add(iv);
                aux[iv] = kr[iv];
            }

            while (!mustBeIncluded.isEmpty()) {
                Integer verti = mustBeIncluded.remove();
                contadd++;
                Collection<Integer> neighbors = graph.getNeighborsUnprotected(verti);
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
                hullSet = new HashSet<>(currentSetSize);
                hullSet.addAll(obg);
                for (int i : currentSet) {
                    hullSet.add(verticesElegiveis.get(i));
                }
                found.set(true);
                break;
            }
            CombinationsFacade.nextCombination(size, currentSetSize, currentSet);
        }
        return hullSet;
    }

    public String getName() {
        return description;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        TSSBruteForceOptmParalelo opf = new TSSBruteForceOptmParalelo();
        TSSCordasco tss = new TSSCordasco();
        HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();
        UndirectedSparseGraphTO<Integer, Integer> graph = null;

        graph = UtilGraph.loadGraphES(
                "0-12,0-27,1-7,1-10,1-16,2-17,2-21,2-24,3-7,3-17,3-23,4-6,4-9,4-12,5-8,5-29,6-9,6-16,7-17,7-23,10-15,10-18,11-18,11-23,11-28,12-28,13-22,13-28,14-16,15-17,15-18,16-20,17-26,18-21,19-27,19-28,21-24,22-27,23-27,24-28,24-29,27-29,25,");
        opf.setK(2);
        Set<Integer> findMinHullSetGraph = opf.findHullSet(graph);
        boolean checkIfHullSet = opf.checkIfHullSet(graph, findMinHullSetGraph);
        if (!checkIfHullSet) {
            System.out.println("ALERT: ----- RESULTADO ANTERIOR IS NOT HULL SET");
            System.err.println("ALERT: ----- RESULTADO ANTERIOR IS NOT HULL SET");
            throw new IllegalStateException("CORDASSO IS NOT HULL SET");
        }
        Set<Integer> optmHullSet = null;
        String strFile = "data/rand/grafos-rand-densall-n5-100.txt";

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            opf,
            tss,
            hnv1,
            hnv2
        };
        String[] grupo = new String[]{
            "Optm",
            "TSS",
            "HNV",
            "HNV"
        };
        Integer[] result = new Integer[operations.length];
        long totalTime[] = new long[operations.length];

        for (String op : new String[]{
            "m", 
            "r",
            "k",
        }) {
            for (int k = 1; k <= 5; k++) {
                if (op.equals("r")) {
                    tss.setR(k);
                    opf.setR(k);
                    hnv2.setR(k);
                    hnv1.setR(k);

                    System.out.println("-------------\n\nR: " + k);
                } else if (op.equals("m")) {
                    op = "m";
                    double percent = ((double) k) / 10.0;
                    opf.setPercent(percent);
                    tss.setPercent(percent);
                    hnv2.setPercent(percent);
                    hnv1.setPercent(percent);
                    System.out.println("-------------\n\nm: " + k);
                } else {
                    op = "k";
                    opf.setK(k);
                    tss.setK(k);
                    hnv2.setK(k);
                    hnv1.setK(k);
                    System.out.println("-------------\n\nk: " + k);
                }

                BufferedReader files = new BufferedReader(new FileReader(strFile));
                String line = null;
                int contgraph = 0;
                int density = 1;

                while (null != (line = files.readLine())) {
                    graph = UtilGraph.loadGraphES(line);
                    graph.setName("rand-n5-100-dens0" + density + "-cont-" + contgraph);
                    String gname = graph.getName();
                    contgraph++;
                    if ((contgraph % 20) == 0) {
                        density++;
                    }

                    for (int i = 0; i < operations.length; i++) {
                        Map<String, Object> doOperation = null;
                        UtilProccess.startTime();
                        doOperation = operations[i].doOperation(graph);
                        totalTime[i] += UtilProccess.endTime();

                        result[i] = (Integer) doOperation.get(IGraphOperation.DEFAULT_PARAM_NAME_RESULT);

                        String out = "Rand\t" + gname + "\t" + graph.getVertexCount() + "\t"
                                + graph.getEdgeCount()
                                + "\t" + op + "\t" + k + "\t" + grupo[i] + "\t" + operations[i].getName()
                                + "\t" + result[i] + "\t" + totalTime[i] + "\n";

                        System.out.print("xls: " + out);

                        if (doOperation != null) {
                            checkIfHullSet = operations[i].checkIfHullSet(graph,
                                    ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
                            if (!checkIfHullSet) {
                                System.out.println("ALERT: ----- RESULTADO ANTERIOR IS NOT HULL SET");
                                System.out.println(line);
                            }
                        }
                    }
                }
            }
        }
    }
}
