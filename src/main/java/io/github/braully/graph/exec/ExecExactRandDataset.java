package io.github.braully.graph.exec;

import io.github.braully.graph.operation.*;
import io.github.braully.graph.UndirectedSparseGraphTO;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        UndirectedSparseGraphTO<Integer, Integer> graph = null;

        String strFile = "data/rand/grafos-rand-densall-n5-100.txt";

        AbstractHeuristic[] operations = new AbstractHeuristic[]{
            opf,
            tip,
            tss,
            hnv2,
            hnv1
        };
        String[] grupo = new String[]{
            "Optm",
            "Decomp",
            "TSS",
            "HNV",
            "HNV"
        };
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
                    System.out.println("-------------\n\nR: " + k);
                } else if (op.equals("m")) {
                    op = "m";
                    double perc = ((float) k) / 10.0;
                    tss.setPercent(perc);
                    hnv2.setPercent(perc);
                    hnv1.setPercent(perc);
                    tip.setPercent(perc);
                    System.out.println("-------------\n\nm: " + k);
                } else {
                    op = "k";
                    opf.setK(k);
                    tss.setK(k);
                    hnv2.setK(k);
                    hnv1.setK(k);
                    tip.setK(k);
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

//                        System.out.print("xls: " + out);
                        System.out.print(out);
                        if (doOperation != null) {
                            boolean checkIfHullSet = operations[i].checkIfHullSet(graph, ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
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
