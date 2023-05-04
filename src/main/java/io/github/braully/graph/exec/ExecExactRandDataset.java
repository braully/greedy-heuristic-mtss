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
import java.util.HashMap;
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
        HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();
        HNV0 hnv0 = new HNV0();
        CCMPanizi ccm = new CCMPanizi();

        hnv1.setVerbose(true);
        UndirectedSparseGraphTO<Integer, Integer> graph = null;

        String strFile = "data/rand/grafos-rand-densall-n5-100.txt";

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            //            opf,
            //            tip,
            //            tss,
            hnv0,
            //            hnv1,
            //            hnv2
            ccm
        };
        String[] grupo = new String[]{
            //            "Optm",
            //            "Decomp",
            //            "TSS",
            //            "HNV",
            "HNV",
            "HNV",
            "CCM"
        };
        Map<String, Boolean> piorou = new HashMap<>();
        Integer[] delta = new Integer[operations.length];
        int contMelhorGlobal = 0, contPiorGlobal = 0, contIgualGlobal = 0;
        int[] contMelhor = new int[operations.length];
        int[] contPior = new int[operations.length];
        int[] contIgual = new int[operations.length];
        for (int i = 0; i < operations.length; i++) {
            contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Integer[] result = new Integer[operations.length];
        long totalTime[] = new long[operations.length];

        for (int k = 1; k <= 9; k++) {
            for (String op : new String[]{
                "k",
                "r",
                "m"
            }) {
                if (op.equals("r")) {
                    tss.setR(k);
                    opf.setR(k);
                    hnv1.setR(k);
                    tip.setR(k);
                    hnv2.setR(k);
                    hnv0.setR(k);
                    ccm.setR(k);
                    System.out.println("-------------\n\nR: " + k);
                } else if (op.equals("m")) {
                    op = "m";
                    double perc = ((float) k) / 10.0;
                    tss.setPercent(perc);
                    hnv2.setPercent(perc);
                    hnv1.setPercent(perc);
                    tip.setPercent(perc);
                    hnv0.setPercent(perc);
                    ccm.setPercent(perc);
                    System.out.println("-------------\n\nm: " + k);
                } else {
                    op = "k";
                    opf.setK(k);
                    tss.setK(k);
                    hnv2.setK(k);
                    hnv1.setK(k);
                    hnv0.setK(k);
                    tip.setK(k);
                    ccm.setK(k);
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
                                + "\t" + op + "\t" + k + "\t" + grupo[i] + "\t" + operations[i].getName()
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
                            contIgualGlobal++;
                        } else if (delta[i] > 0) {
                            contMelhor[i]++;
                            contMelhorGlobal++;
                            if (k == 2) {
                                piorou.put(id, true);
                            }
                        } else {
                            contPior[i]++;
                            contPiorGlobal++;
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

        System.out.println("\n\nResumo Global");
        for (int i = 1; i < operations.length; i++) {
            System.out.println("Operacao: " + operations[i].getName());
            System.out.println("Melhor: " + contMelhorGlobal);
            System.out.println("Pior: " + contPiorGlobal);
            System.out.println("Igual: " + contIgualGlobal);

            System.out.println("------------");
            int total = contMelhorGlobal + contPiorGlobal + contIgualGlobal;
            if (total > 0) {
                System.out.println("Melhor: " + (contMelhorGlobal * 100 / total) + "pct");
                System.out.println("Igual: " + ((total - (contMelhorGlobal + contPiorGlobal))
                        * 100 / total) + "pct"
                );
                System.out.println("Pior: " + (contPiorGlobal * 100 / total) + "pct");
            }
        }
        for (int i = 0; i < operations.length; i++) {
            System.out.println(operations[i].getName());
            System.out.print("time: ");
            printTimeFormated(totalTime[i]);
//            System.out.println();

        }

    }
}
