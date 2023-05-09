package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import static java.lang.Math.abs;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author strike
 */
public abstract class AbstractHeuristic implements IGraphOperation {

    public static final String type = "Contamination";
    public static final String PARAM_NAME_HULL_NUMBER = "number";
    public static final String PARAM_NAME_HULL_SET = "set";
    protected static Random randomUtil = new Random();

    //
    public Integer kTreshold;
    public Integer rTreshold;
    public Double percentTreshold;
    public Boolean randomTreshold;
    //
    protected int[] kr;
    protected boolean verbose;

    protected int[] degree = null;
    protected Set<Integer>[] N = null;

    public String getTypeProblem() {
        return type;
    }

    public void setK(Integer K) {
        this.kTreshold = K;
        this.percentTreshold = null;
        this.rTreshold = null;
        this.randomTreshold = null;
    }

    public void setR(Integer R) {
        this.rTreshold = R;
        this.kTreshold = null;
        this.percentTreshold = null;
        this.randomTreshold = null;
    }

    public void setPercent(Double marjority) {
        this.percentTreshold = marjority;
        this.kTreshold = null;
        this.rTreshold = null;
        this.randomTreshold = null;
    }

    public void setKr(int[] kr) {
        this.kr = kr;
    }

    public int[] getKr() {
        return kr;
    }

    public void setRandomKr(UndirectedSparseGraphTO<Integer, Integer> graph) {
        int vertexCount = (Integer) graph.maxVertex() + 1;
        kr = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            int degree = graph.degree(i);
            if (degree > 0) {
                int random = random(degree);
                kr[i] = random;
            } else {
                kr[i] = degree;
            }
        }
    }

    public void initKr(UndirectedSparseGraphTO graph) {
        if (rTreshold != null || kTreshold != null || percentTreshold != null) {
            int vertexCount = (Integer) graph.maxVertex() + 1;
            kr = new int[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                int degree = graph.degree(i);
                if (rTreshold != null) {
                    kr[i] = Math.min(rTreshold, graph.degree(i));
                } else if (kTreshold != null) {
                    kr[i] = kTreshold;
                } else if (percentTreshold != null) {
                    //                kr[i] = roundUp(degree, majority);
                    double ki = Math.ceil(percentTreshold * degree);
                    int kii = (int) Math.ceil(ki);
                    kr[i] = kii;
                } else if (randomTreshold != null) {
                    if (degree > 0) {
                        int random = random(degree);
                        kr[i] = random;
                    } else {
                        kr[i] = degree;
                    }
                }
            }
        }
    }

    public static int random(int num) {
        //Probability ignored, for future use, , Integer probability
        return randomUtil.nextInt(num + 1);
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
