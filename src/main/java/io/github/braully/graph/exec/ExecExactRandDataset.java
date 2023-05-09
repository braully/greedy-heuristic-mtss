package io.github.braully.graph.exec;

import io.github.braully.graph.operation.*;
import io.github.braully.graph.UndirectedSparseGraphTO;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import static io.github.braully.graph.util.UtilProccess.printTimeFormated;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exec Exact Algorithm for Rand
 *
 * @author Braully Rocha da Silva
 */
public class ExecExactRandDataset {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        TSSBruteForceOptm opf = new TSSBruteForceOptm();
        TSSCordasco tss = new TSSCordasco();
        TIPDecomp tip = new TIPDecomp();
        HNVA hnva = new HNVA();
        HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();
        HNV0 hnv0 = new HNV0();
        CCMPanizi ccm = new CCMPanizi();
        GreedyCordasco gc = new GreedyCordasco();
        GreedyDegree gd = new GreedyDegree();
        GreedyDeltaTss gdt = new GreedyDeltaTss();
        GreedyBonusDist gdit = new GreedyBonusDist();
        GreedyDifTotal gdft = new GreedyDifTotal();
        GreedyDeltaXDifTotal gdxd = new GreedyDeltaXDifTotal();
        hnv1.setVerbose(true);
        UndirectedSparseGraphTO<Integer, Integer> graph = null;

        String strFile = "data/rand/grafos-rand-densall-n5-100.txt";

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            //            opf,
            //            tip,
            tss,
            hnv0,
            gdft, //            gd, gdt, gdit,
            //            hnv1,
            //            hnv1,
            //            hnv2
            ccm,
            //            hnva
            gdxd
        };
        String[] grupo = new String[]{
            "Optm",
            "Decomp",
            "TSS",
            "HNV",
            "HNV",
            "HNV",
            "CCM"
        };
        Map<String, Boolean> piorou = new HashMap<>();
        Integer[] delta = new Integer[operations.length];
        int[] contMelhorGlobal = new int[operations.length];
        int[] contPiorGlobal = new int[operations.length];
        int[] contIgualGlobal = new int[operations.length];
        int[] contMelhor = new int[operations.length];
        int[] contPior = new int[operations.length];
        int[] contIgual = new int[operations.length];
        for (int i = 0;
                i < operations.length;
                i++) {
            contMelhorGlobal[i] = contPiorGlobal[i] = contIgualGlobal[i] = contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Integer[] result = new Integer[operations.length];
        long totalTime[] = new long[operations.length];
        List<String> ops = Arrays.asList(new String[]{
            "k",
//            "r",
//            "m"
        });
        for (int k = 1;
                k <= 6; k++) {
            for (String op : ops) {
                if (op.equals("r")) {
                    for (AbstractHeuristic ab : operations) {
                        ab.setR(k);
                    }
                    System.out.println("-------------\n\nR: " + k);
                } else if (op.equals("m")) {
                    op = "m";
                    double perc = ((double) k) / 10.0;
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
                    String id = strFile + "-" + density + "-" + contgraph;

                    for (int i = 0; i < operations.length; i++) {
                        Map<String, Object> doOperation = null;
                        UtilProccess.startTime();
                        doOperation = operations[i].doOperation(graph);
                        totalTime[i] += UtilProccess.endTime();

                        result[i] = (Integer) doOperation.get(IGraphOperation.DEFAULT_PARAM_NAME_RESULT);

                        String out = "Rand\t" + gname + "\t" + graph.getVertexCount() + "\t"
                                + graph.getEdgeCount()
                                + "\t" + op + "\t" + k + "\t"
                                //                                + grupo[i]
                                + "grupo"
                                + "\t" + operations[i].getName()
                                + "\t" + result[i] + "\t" + totalTime[i] + "\n";

//                        System.out.print("xls: " + out);
                        System.out.print(out);
                        if (doOperation != null) {
                            boolean checkIfHullSet = operations[i].checkIfHullSet(graph, ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
                            if (!checkIfHullSet) {
                                System.out.println("ALERT: ----- RESULTADO ANTERIOR IS NOT HULL SET");
                                System.out.println(line);
                            }
                        }
                        if (i == 0) {
                            delta[i] = 0;
                        } else {
                            delta[i] = result[0] - result[i];
                        }
                        if (delta[i] == 0) {
                            contIgual[i]++;
                            contIgualGlobal[i]++;
                        } else if (delta[i] > 0) {
                            contMelhor[i]++;
                            contMelhorGlobal[i]++;
                            if (k == 2) {
                                piorou.put(id, true);
                            }
                        } else {
                            contPior[i]++;
                            contPiorGlobal[i]++;
                            Boolean get1 = piorou.get(id);
                            if (get1 != null && get1) {
//                                    System.out.println("grafo piorou: " + id + " em k: " + k);
//                                    System.out.println(graphES.getEdgeString());
                                piorou.remove(id);
                            }
                        }
                    }
                }
            }

            if (true) {
                System.out.println("Resumo parcial: " + k);
                for (int i = 1; i < operations.length; i++) {
                    int total = contMelhor[i] + contPior[i] + contIgual[i];
                    System.out.println("==========");
                    System.out.println("Operacao: " + operations[i].getName());
                    System.out.println("Melhor: " + contMelhor[i]);
                    System.out.println("Pior: " + contPior[i]);
                    System.out.println("Igual: " + contIgual[i]);
                    System.out.println("Total: " + total);
                    System.out.println("------------");
                    System.out.println("Melhor: " + (contMelhor[i] * 100 / total) + "pct");
                    System.out.println("Igual: " + ((total - (contMelhor[i] + contPior[i]))
                            * 100 / total) + "pct"
                    );
                    System.out.println("Pior: " + (contPior[i] * 100 / total) + "pct");
                }
                for (int i = 0; i < operations.length; i++) {
                    contMelhor[i] = contPior[i] = contIgual[i] = 0;
                }
            }

        }

        System.out.println(
                "\n\nResumo Global");
        System.out.println("OPs:" + ops);
        for (int i = 1;
                i < operations.length;
                i++) {
            System.out.println("***************");
            System.out.println("Operacao: " + operations[i].getName());
            if (contMelhorGlobal[i] > contPiorGlobal[i]) {
                System.out.println("GOOD " + operations[0].getName());
            } else {
                System.out.println("WORST " + operations[0].getName());
            }

            System.out.println("Melhor: " + contMelhorGlobal[i]);
            System.out.println("Pior: " + contPiorGlobal[i]);
            System.out.println("Igual: " + contIgualGlobal[i]);

            System.out.println("------------");
            int total = contMelhorGlobal[i] + contPiorGlobal[i] + contIgualGlobal[i];
            if (total > 0) {
                System.out.println("Melhor: " + (contMelhorGlobal[i] * 100 / total) + "pct");
                System.out.println("Igual: " + ((total - (contMelhorGlobal[i] + contPiorGlobal[i]))
                        * 100 / total) + "pct"
                );
                System.out.println("Pior: " + (contPiorGlobal[i] * 100 / total) + "pct");
            }
        }
        for (int i = 0;
                i < operations.length;
                i++) {
            System.out.println(operations[i].getName());
            System.out.print("time: ");
            printTimeFormated(totalTime[i]);
//            System.out.println();

        }

    }
}
