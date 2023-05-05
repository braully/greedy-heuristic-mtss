package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class GreedyDegree
        extends GenericGreedy implements IGraphOperation {

    static final Logger log = Logger.getLogger(GreedyDegree.class.getSimpleName());
    static final String description = "Greedy-Degree";

    public static String getDescription() {
        return description;
    }

    public GreedyDegree() {
    }

    @Override
    public void selectBestVertice(List<Integer> vertices, int[] aux) {
        bestVertice = -1;
        maxDegree = 0;

        for (Integer w : vertices) {
            //Ignore w if is already contamined OR skip review to next step
            if (aux[w] >= kr[w]) {
                continue;
            }
            int wDegree = this.degree[w];

            if (bestVertice == -1 || wDegree > maxDegree) {
                bestVertice = w;
                maxDegree = wDegree;
            }

        }
    }

    public static void main(String... args) throws IOException {
        System.out.println("Execution Sample: BlogCatalog database R=2");
        UndirectedSparseGraphTO<Integer, Integer> graph = null;
        GreedyDegree op = new GreedyDegree();

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
