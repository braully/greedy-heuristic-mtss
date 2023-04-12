package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import static java.lang.Math.abs;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author strike
 */
public abstract class AbstractHeuristic implements IGraphOperation {

    public static final String type = "Contamination";
    public static final String PARAM_NAME_HULL_NUMBER = "number";
    public static final String PARAM_NAME_HULL_SET = "set";

    //
    public Integer K;
    public Integer R;
    public Integer marjority;
    //
    protected int[] kr;
    protected boolean verbose;

    protected int[] degree = null;

    public String getTypeProblem() {
        return type;
    }

    public void setK(Integer K) {
        this.K = K;
        this.marjority = null;
        this.R = null;
    }

    public void setR(Integer R) {
        this.R = R;
        this.K = null;
        this.marjority = null;
    }

    public void setMarjority(Integer marjority) {
        this.marjority = marjority;
        this.K = null;
        this.R = null;
    }

    public void initKr(UndirectedSparseGraphTO graph) {
        int vertexCount = (Integer) graph.maxVertex() + 1;
        kr = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            if (R != null) {
                kr[i] = Math.min(R, graph.degree(i));
            } else if (K != null) {
                kr[i] = K;
            } else if (marjority != null) {
                kr[i] = roundUp(graph.degree(i), marjority);
            }
        }
    }

    public static int roundUp(int num, int divisor) {
        int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
        return sign * (abs(num) + abs(divisor) - 1) / abs(divisor);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean checkIfHullSet(UndirectedSparseGraphTO<Integer, Integer> graph,
            Iterable<Integer> currentSet) {
        if (currentSet == null) {
            return false;
        }
        Queue<Integer> mustBeIncluded = new ArrayDeque<>();

        Set<Integer> fecho = new HashSet<>();

        int vertexCount = graph.getVertexCount();
        if (kr == null || kr.length < vertexCount) {
            initKr(graph);
        }
        int[] aux = new int[(Integer) graph.maxVertex() + 1];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = 0;
            if (kr[i] == 0) {
                mustBeIncluded.add(i);
            }
        }

        for (Integer iv : currentSet) {
            Integer v = iv;
            mustBeIncluded.add(v);
            aux[v] = kr[v];
        }
        while (!mustBeIncluded.isEmpty()) {
            Integer verti = mustBeIncluded.remove();
            Collection<Integer> neighbors = graph.getNeighborsUnprotected(verti);
            for (Integer vertn : neighbors) {
                if (vertn.equals(verti)) {
                    continue;
                }
                if ((++aux[vertn]) == kr[vertn]) {
                    mustBeIncluded.add(vertn);
                }
            }
            fecho.add(verti);
            aux[verti] += kr[verti];
        }
        return fecho.size() == graph.getVertexCount();
    }

}
