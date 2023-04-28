package io.github.braully.graph.exec;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.operation.AbstractHeuristic;
import io.github.braully.graph.operation.TSSCordasco;
import io.github.braully.graph.operation.HNV1;
import io.github.braully.graph.operation.HNV2;
import io.github.braully.graph.operation.IGraphOperation;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author strike
 */
public class ExecBigDataSets {

    public static final Map<String, int[]> resultadoArquivado = new HashMap<>();

    public static void main(String... args) throws FileNotFoundException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String[] dataSets = new String[]{
            "ca-GrQc",
            "ca-HepTh",
            "ca-CondMat",
            "ca-HepPh",
            "ca-AstroPh",
            "Douban",
            "Delicious",
            "BlogCatalog3",
            "BlogCatalog2",
            "Livemocha",
            "BlogCatalog",
            "BuzzNet",
            "Last.fm", //            "YouTube2"
        };

        TSSCordasco tss = new TSSCordasco();
//        GraphTSSGreedy tssg = new GraphTSSGreedy();

        HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            //            tss,
            //            heur1,
            //            heur2, 
            //            heur3, heur4,
            //            heur5,
            //            heur5t,
            //            tssg,
            //            heur5t2
            //            optm,
            //            optm2,
            hnv1, //            hnv2
        };
        long totalTime[] = new long[operations.length];
        Integer[] result = new Integer[operations.length];
        Integer[] delta = new Integer[operations.length];
        int[] contMelhor = new int[operations.length];
        int[] contPior = new int[operations.length];
        int[] contIgual = new int[operations.length];
        for (int i = 0; i < operations.length; i++) {
            contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Arrays.sort(dataSets);

        String strResultFile = "resultado-" + ExecBigDataSets.class.getSimpleName() + ".txt";
        File resultFile = new File(strResultFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true));
        for (String op : new String[]{
            //            "m",
            "k",
            "r"
        }) {
            for (int k = 1; k <= 9; k++) {
                if (op.equals("r")) {
                    tss.setR(k);
                    hnv2.setR(k);
                    hnv1.setR(k);
                    System.out.println("-------------\n\nR: " + k);
                } else if (op.equals("m")) {
                    op = "m";
                    double perc = ((float) k) / 10.0;
                    tss.setPercent(perc);
                    hnv2.setPercent(perc);
                    hnv1.setPercent(perc);
                    System.out.println("-------------\n\nm: " + k);
                } else {
                    op = "k";
                    tss.setK(k);
                    hnv2.setK(k);
                    hnv1.setK(k);
                    System.out.println("-------------\n\nk: " + k);
                }
                for (String s : dataSets) {
                    System.out.println("\n-DATASET: " + s);

                    UndirectedSparseGraphTO<Integer, Integer> graphES
                            = null;

                    try {
                        URI urigraph = URI.create("jar:file:data/big/all-big.zip!/" + s + "/" + s + ".txt");
                        InputStream streamgraph = urigraph.toURL().openStream();
                        graphES = UtilGraph.loadBigDataset(streamgraph);
                    } catch (FileNotFoundException e) {
                        URI urinode = URI.create("jar:file:data/big/all-big.zip!/" + s + "/nodes.csv");
                        URI uriedges = URI.create("jar:file:data/big/all-big.zip!/" + s + "/edges.csv");

                        InputStream streamnode = urinode.toURL().openStream();
                        InputStream streamedges = uriedges.toURL().openStream();
                        graphES = UtilGraph.loadBigDataset(streamnode,
                                streamedges);
                    }
                    if (graphES == null) {
                        System.out.println("Fail to Load GRAPH: " + s);
                    }
                    System.out.println("Loaded Graph: " + s + " " + graphES.getVertexCount() + " " + graphES.getEdgeCount());

                    for (int i = 0; i < operations.length; i++) {
                        String arquivadoStr = operations[i].getName() + "-" + op + k + "-" + s;
                        Map<String, Object> doOperation = null;
                        System.out.println("*************");
                        System.out.print(" - EXEC: " + operations[i].getName() + "-" + op + ": " + k + " g:" + s + " " + graphES.getVertexCount() + " ");
                        int[] get = resultadoArquivado.get(arquivadoStr);
                        if (get != null) {
                            result[i] = get[0];
                            totalTime[i] = get[1];
                        } else {
                            UtilProccess.printStartTime();
                            doOperation = operations[i].doOperation(graphES);
                            result[i] = (Integer) doOperation.get(IGraphOperation.DEFAULT_PARAM_NAME_RESULT);
                            totalTime[i] += UtilProccess.printEndTime();
                            System.out.println(" - arquivar: resultadoArquivado.put(\"" + arquivadoStr + "\", new int[]{" + result[i] + ", " + totalTime[i] + "});");
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
                            boolean checkIfHullSet = operations[i].checkIfHullSet(graphES, ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
                            if (!checkIfHullSet) {
                                System.out.println("ALERT: ----- THE RESULT IS NOT A HULL SET");
//                            throw new IllegalStateException("IS NOT HULL SET");
                            }
                        }
                        if (i == 0) {
                            if (get == null) {
                                delta[i] = 0;
                            }
                        } else {
                            delta[i] = result[0] - result[i];

                            long deltaTempo = totalTime[0] - totalTime[i];
                            System.out.print(" - g:" + s + " " + op + " " + k + "  tempo: ");

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
                }
            }
        }
        writer.flush();
        writer.close();
        System.out.println("RESUME ");
        for (int i = 1; i < operations.length; i++) {
            System.out.println("Operation: " + operations[i].getName());
            System.out.println("Best: " + contMelhor[i]);
            System.out.println("Worst: " + contPior[i]);
            System.out.println("Equal: " + contIgual[i]);
        }
    }
}
