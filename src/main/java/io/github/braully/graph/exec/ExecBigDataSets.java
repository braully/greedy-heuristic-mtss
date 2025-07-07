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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.operation.AbstractHeuristic;
import io.github.braully.graph.operation.CCMPanizi;
import io.github.braully.graph.operation.GreedyBonusDist;
import io.github.braully.graph.operation.GreedyCordasco;
import io.github.braully.graph.operation.GreedyDegree;
import io.github.braully.graph.operation.GreedyDeltaDifExperimento;
import io.github.braully.graph.operation.GreedyDeltaTss;
import io.github.braully.graph.operation.GreedyDeltaXDifTotal;
import io.github.braully.graph.operation.GreedyDifTotal;
import io.github.braully.graph.operation.GreedyDistAndDifDelta;
import io.github.braully.graph.operation.HNV1;
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
public class ExecBigDataSets {

    public static final Map<String, int[]> resultadoArquivado = new HashMap<>();

    static String[] dataSets = new String[]{
        "ca-GrQc",
        "ca-HepTh",
        "ca-CondMat",
        "ca-HepPh",
        "ca-AstroPh", // "Douban",
        // "Delicious",
        "BlogCatalog3", //     "BlogCatalog2",
        //     "Livemocha",
        //     "BlogCatalog",
        //     "BuzzNet",
        //     "Last.fm",
        // "YouTube2"
        // "Facebook-users",
        "amazon0302",
        "amazon0312",
        "amazon0505",
        "amazon0601",
        "facebook_combined",
        "com-dblp",};
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
        // GraphTSSGreedy tssg = new GraphTSSGreedy();
        // HNVA hnva = new HNVA();
        // HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();
        hnv1.setVerbose(true);
        // HNV0 hnv0 = new HNV0();
        CCMPanizi ccm = new CCMPanizi();

        TIPDecomp tip = new TIPDecomp();

        GreedyCordasco gc = new GreedyCordasco();
        GreedyDegree gd = new GreedyDegree();
        gd.setRefine2(true);
        GreedyDeltaTss gdt = new GreedyDeltaTss();
        gdt.setRefine2(true);
        GreedyBonusDist gdit = new GreedyBonusDist();
        GreedyDifTotal gdft = new GreedyDifTotal();
        gdft.setRefine(true);
        gdft.setRefine2(true);
        // gdft.setRefine2(true);
        GreedyDeltaDifExperimento heur1 = new GreedyDeltaDifExperimento();
        heur1.setProporcao(0.1);
        GreedyDeltaDifExperimento heur2 = new GreedyDeltaDifExperimento();
        heur2.setProporcao(0.2);
        GreedyDeltaDifExperimento heur3 = new GreedyDeltaDifExperimento();
        heur3.setProporcao(0.3);
        GreedyDeltaDifExperimento heur4 = new GreedyDeltaDifExperimento();
        heur4.setProporcao(0.4);
        GreedyDeltaDifExperimento heur5 = new GreedyDeltaDifExperimento();
        heur5.setProporcao(0.5);
        // GreedyDeltaDifExperimento heur5t = new GreedyDeltaDifExperimento();
        // heur5t.setProporcao(0.5);
        GreedyDeltaXDifTotal gdxd = new GreedyDeltaXDifTotal();
        gdxd.setRefine(true);
        gdxd.setRefine2(true);
        GreedyDistAndDifDelta gdd = new GreedyDistAndDifDelta();
        gdd.setRefine(true);
        gdd.setRefine2(true);

        GreedyDistAndDifDelta gdd1 = new GreedyDistAndDifDelta();
//        gdd1.setRefine2(false);

        ccm.setRefine(true);
        ccm.setRefine2(true);
        gd.setRefine(true);
        gd.setRefine2(true);

        GreedyDeltaDifExperimento heur10 = new GreedyDeltaDifExperimento();
        heur10.setProporcao(1);

        operations = new AbstractHeuristic[]{
            //            gdxd,
            tss,
            // heur1,
            // heur2,
            // heur3, heur4,
            // heur5,
            // heur5t,
            // tssg,
            // heur5t2
            // optm,
            // optm2,
            // tip,
            // hnv0, // hnv1,
            // hnv2
            // hnv0, gd, gdit,
            // hnva
            // ccm,
            // gd, // gdt
            // gc, gdt
            // gdft,
            // gdd1,
            // gdd,
            gdft, //            gdd1,
        //            heur1,
        //            heur2,
        //            heur3,
        //            heur4,
        //            heur5,
        //            heur10,
        };
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

        String strResultFile = "resultado-" + ExecBigDataSets.class.getSimpleName() + ".txt";
        File resultFile = new File(strResultFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true));
        for (String op : new String[]{
            "r", // "k", // "random"
            "m",}) {
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

            UndirectedSparseGraphTO<Integer, Integer> graphES = null;

            try {
                File file = new File("/home/strike/Workspace/tss/TSSGenetico/Instancias/" + s + "/" + s + ".txt");
                if (file.exists()) {
                    graphES = UtilGraph.loadBigDataset(new FileInputStream(file));
                } else {

                    URI urigraph = URI.create("jar:file:data/big/all-big.zip!/" + s + "/" + s + ".txt");
                    InputStream streamgraph = urigraph.toURL().openStream();
                    graphES = UtilGraph.loadBigDataset(streamgraph);
                }
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
            if (op.equals("random")) {
                System.out.println("Random operation for graph " + s);
                operations[0].setRandomKr(graphES);
                System.out.print("kr: ");
                UtilProccess.printArray(operations[0].getKr());
                for (int i = 1; i < operations.length; i++) {
                    operations[i].setKr(operations[0].getKr());
                }
            }
            for (int i = 0; i < operations.length; i++) {

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
        }
    }
}
