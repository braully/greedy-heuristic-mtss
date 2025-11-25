package io.github.braully.graph.exec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.operation.AbstractHeuristic;
import io.github.braully.graph.operation.CCMPanizi;
import io.github.braully.graph.operation.GreedyDifTotal;
import io.github.braully.graph.operation.IGraphOperation;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.operation.TIPDecomp;
import io.github.braully.graph.operation.TSSCordasco;
import io.github.braully.graph.util.UtilDatabase;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;

/**
 *
 * @author strike
 */
public class ExecBigDatasetParallel {

    public static final Map<String, int[]> resultadoArquivado = new HashMap<>();

    public static final int NUM_BIG_DATA_SET = 10;

    static String[] dataSets = new String[] {
            "ca-GrQc",
            "ca-HepTh",
            "ca-CondMat",
            "ca-HepPh",
            "ca-AstroPh",
            "Douban",
            "Delicious",
            "BlogCatalog3",
            "BlogCatalog2",
            "BlogCatalog",
            "Livemocha",
            "BuzzNet",
            "Last.fm",
            "YouTube2",
            "Facebook-users",
            "amazon0302",
            "amazon0312",
            "amazon0505",
            "amazon0601",
            // "facebook_combined",
            // "com-dblp", 
        };
    static AbstractHeuristic[] operations = null;

    static long totalTime[];
    static Integer[] result;
    static Integer[] delta;
    static int[] contMelhor;
    static int[] contPior;
    static int[] contIgual;

    public static void main(String... args) throws FileNotFoundException, IOException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        TSSCordasco tss = new TSSCordasco();
        tss.setRefine(true);
        tss.setRefine2(true);

        CCMPanizi ccm = new CCMPanizi();
        ccm.setRefine2(true);
        ccm.setRefine2(true);

        TIPDecomp tip = new TIPDecomp();
        tip.setRefine(true);
        tip.setRefine2(true);

        GreedyDifTotal gdft = new GreedyDifTotal();
        gdft.setRefine(true);
        gdft.setRefine2(true);

        operations = new AbstractHeuristic[] {
                tss,
                tip,
                gdft, };
        totalTime = new long[operations.length];
        result = new Integer[operations.length];
        delta = new Integer[operations.length];
        contMelhor = new int[operations.length];
        contPior = new int[operations.length];
        contIgual = new int[operations.length];
        for (int i = 0; i < operations.length; i++) {
            contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Arrays.sort(dataSets);

        String strResultFile = "resultado-" + ExecBigDatasetParallel.class.getSimpleName() + ".txt";
        File resultFile = new File(strResultFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true));
        for (String op : new String[] {
                "r", // "k", // "random"
                "m", }) {
            if (op.equals("random")) {
                for (AbstractHeuristic ab : operations) {
                    ab.setR(null);
                }
                execOperations(op, 0, writer);

            } else {
                for (int k = 2; k <= 5; k++) {
                    if (op.equals("r")) {
                        for (AbstractHeuristic ab : operations) {
                            ab.setR(k);
                        }
                        System.out.println("-------------\n\nR: " + k);
                    } else if (op.equals("m")) {
                        op = "m";
                        double perc = ((float) k) / 10.0;
                        for (AbstractHeuristic ab : operations) {
                            ab.setPercent(perc);
                        }
                        System.out.println("-------------\n\nm: " + k);
                    } else {
                        op = "k";
                        for (AbstractHeuristic ab : operations) {
                            ab.setK(k);
                        }
                        System.out.println("-------------\n\nk: " + k);
                    }
                    execOperations(op, k, writer);
                    System.out.println(
                            " Partial ");
                    for (int i = 1; i < operations.length; i++) {
                        System.out.println(" -Operation: " + operations[i].getName());
                        System.out.println("   * Best: " + contMelhor[i]);
                        System.out.println("   * Worst: " + contPior[i]);
                        System.out.println("   * Equal: " + contIgual[i]);
                    }
                }
            }
        }

        writer.flush();

        writer.close();

        System.out.println(
                "RESUME  GERAL");
        for (int i = 1; i < operations.length; i++) {
            System.out.println(" - Operation: " + operations[i].getName());
            System.out.println("   * Best: " + contMelhor[i]);
            System.out.println("   * Worst: " + contPior[i]);
            System.out.println("   * Equal: " + contIgual[i]);
        }
    }

    static void execOperations(String op, int k, BufferedWriter writer) throws FileNotFoundException, IOException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (String s : dataSets) {
            System.out.println("\n-DATASET: " + s);

            final UndirectedSparseGraphTO<Integer, Integer> graphES = loadGraph(s);
            if (graphES == null) {
                System.err.println("Fail to Load GRAPH: " + s + " will be ignored!");
                continue;
            }
            System.out.println("Loaded Graph: " + s + " " + graphES.getVertexCount() + " " + graphES.getEdgeCount());
            if (op.equals("random")) {
                System.out.println("Random operation for graph " + s);
                operations[0].setRandomKr(graphES);
                System.out.print("kr: ");
                UtilProccess.printArray(operations[0].getKr());
                for (int i = 1; i < operations.length; i++) {
                    operations[i].setKr(operations[0].getKr());
                }
            }

            // Executar primeiro a operação 0 (baseline) de forma síncrona
            try {
                executarOperacao(0, op, k, s, graphES, writer);
            } catch (Exception e) {
                System.err.println("Erro ao executar operação 0: " + e.getMessage());
                e.printStackTrace();
            }

            // Executar as demais operações em paralelo
            if (operations.length > 1) {
                ExecutorService executor = Executors.newFixedThreadPool(operations.length - 1);
                List<Future<?>> futures = new ArrayList<>();

                for (int i = 1; i < operations.length; i++) {
                    final int index = i;
                    final String datasetName = s;
                    Future<?> future = executor.submit(() -> {
                        try {
                            executarOperacao(index, op, k, datasetName, graphES, writer);
                        } catch (Exception e) {
                            System.err.println("Erro ao executar operação " + index + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    futures.add(future);
                }

                // Aguardar conclusão de todas as tarefas
                for (Future<?> future : futures) {
                    try {
                        future.get();
                    } catch (Exception e) {
                        System.err.println("Erro ao aguardar conclusão da tarefa: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Erro ao aguardar término do executor: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private static synchronized void executarOperacao(int i, String op, int k, String s,
            UndirectedSparseGraphTO<Integer, Integer> graphES, BufferedWriter writer) throws IOException {
        String arquivadoStr = operations[i].getName() + "-" + op + k + "-" + s;
        Map<String, Object> doOperation = null;
        System.out.println("*************");
        System.out.print(" - EXEC: " + arquivadoStr + " g:" + s + " " + graphES.getVertexCount() + " ");
        int[] get = resultadoArquivado.get(arquivadoStr);
        if (get == null) {
            get = UtilDatabase.getResultCache(arquivadoStr);
        }
        if (get != null) {
            result[i] = get[0];
            totalTime[i] = get[1];
        } else {
            UtilProccess.printStartTime();
            doOperation = operations[i].doOperation(graphES);
            result[i] = (Integer) doOperation.get(IGraphOperation.DEFAULT_PARAM_NAME_RESULT);
            totalTime[i] += UtilProccess.printEndTime();
            System.out.println(" resultadoArquivado.put(\"" + arquivadoStr + "\", new int[]{" + result[i] + ", "
                    + totalTime[i] + "});");
        }
        System.out.println(" - Result: " + result[i]);

        String out = "Big\t" + s + "\t" + graphES.getVertexCount() + "\t"
                + graphES.getEdgeCount()
                + "\t" + op + "\t" + k + "\t" + " " + "\t" + operations[i].getName()
                + "\t" + result[i] + "\t" + totalTime[i] + "\n";

        System.out.print("xls: " + out);

        writer.write(out);
        writer.flush();

        if (doOperation != null) {
            boolean checkIfHullSet = operations[0].checkIfHullSet(graphES,
                    ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
            if (!checkIfHullSet) {
                System.out.println("ALERT: ----- THE RESULT IS NOT A HULL SET");
                // throw new IllegalStateException("IS NOT HULL SET");
            }
        }
        if (i == 0) {
            if (get == null) {
                delta[i] = 0;
            }
        } else {
            delta[i] = result[0] - result[i];

            long deltaTempo = totalTime[0] - totalTime[i];
            System.out.print(operations[i].getName() + " - g:" + s + " " + op + " " + k + "  tempo: ");

            if (deltaTempo >= 0) {
                System.out.print(" +FAST " + deltaTempo);
            } else {
                System.out.print(" +SLOW " + deltaTempo);
            }
            System.out.print(" - Delta: " + delta[i] + " ");
            if (delta[i] == 0) {
                System.out.print(" = same");
                contIgual[i]++;
            } else if (delta[i] > 0) {
                System.out.print(" +GOOD ");
                contMelhor[i]++;
            } else {
                System.out.print(" -BAD");
                contPior[i]++;
            }
            System.out.println(delta[i]);
        }
        System.out.println();
    }

    private static UndirectedSparseGraphTO<Integer, Integer> loadGraph(String s) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> graphES = null;
        for (int i = 0; i < NUM_BIG_DATA_SET; i++) {
            if (graphES != null) {
                break;
            }
            try {
                File file = new File("/home/strike/Workspace/tss/TSSGenetico/Instancias/" + s + "/" + s + ".txt");
                if (file.exists()) {
                    graphES = UtilGraph.loadBigDataset(new FileInputStream(file));
                } else {
                    URI urigraph = URI.create("jar:file:data/big/all-big" + i + ".zip!/" + s + "/" + s + ".txt");
                    InputStream streamgraph = urigraph.toURL().openStream();
                    graphES = UtilGraph.loadBigDataset(streamgraph);
                }
            } catch (FileNotFoundException e) {
                try {
                    URI urinode = URI.create("jar:file:data/big/all-big" + i + ".zip!/" + s + "/nodes.csv");
                    URI uriedges = URI.create("jar:file:data/big/all-big" + i + ".zip!/" + s + "/edges.csv");
                    InputStream streamnode = urinode.toURL().openStream();
                    InputStream streamedges = uriedges.toURL().openStream();
                    graphES = UtilGraph.loadBigDataset(streamnode,
                            streamedges);
                } catch (FileNotFoundException ex) {

                }
            }
        }
        return graphES;
    }
}
