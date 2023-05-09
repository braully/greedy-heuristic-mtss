package io.github.braully.graph.util;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class UtilBFS {

    public Integer[] bfs = null;
    private Queue<Integer> queue = null;
    Integer vertexCount;

    public static UtilBFS newBfsUtilSimple(int size) {
        return new UtilBFS(size);
    }

    public int discored = 0;

    private UtilBFS(int size) {
        bfs = new Integer[size];
        vertexCount = size;
        queue = new LinkedList<Integer>();
    }

    public void labelDistances(UndirectedSparseGraphTO graphTemplate, Integer v) {
        bfs(graphTemplate, v);
    }

    public Integer getDistance(UndirectedSparseGraphTO graphTemplate, Integer u) {
        return bfs[u];
    }

    public Integer getDistanceSafe(UndirectedSparseGraphTO graphTemplate, Integer u) {
        if (bfs[u] == null) {
            return -1;
        }
        return bfs[u];
    }

    public void labelDistances(UndirectedSparseGraphTO graphTemplate, Collection<Integer> vs) {
        bfsRanking(graphTemplate, vs);
    }

    public void bfsRanking(UndirectedSparseGraphTO<Integer, Integer> subgraph, Collection<Integer> vs) {
        for (int i = 0; i < bfs.length; i++) {
            bfs[i] = null;
        }
        queue.clear();
        for (Integer v : vs) {
            queue.add(v);
            bfs[v] = 0;
            revisitVertex(v, bfs, subgraph);
        }
    }

    public void visitVertexRanking(Integer v, Integer[] bfs,
            UndirectedSparseGraphTO<Integer, Integer> subgraph1) {
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            int depth = bfs[poll] + 1;
            Collection<Integer> ns = (Collection<Integer>) subgraph1.
                    getNeighborsUnprotected(poll);
            for (Integer nv : ns) {
                if (bfs[nv] == null) {
                    discored++;
                    bfs[nv] = depth;
                    queue.add(nv);
                }
            }
        }
    }

    public void revisitVertexRanking(Integer v, Integer[] bfs,
            UndirectedSparseGraphTO<Integer, Integer> subgraph1) {
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            int depth = bfs[poll] + 1;
            Collection<Integer> ns = (Collection<Integer>) subgraph1.
                    getNeighborsUnprotected(poll);
            for (Integer nv : ns) {
                if (bfs[nv] > depth) {
                    discored++;
                    bfs[nv] = depth;
                    queue.add(nv);
                }
            }
        }
    }

    void bfs(UndirectedSparseGraphTO<Integer, Integer> subgraph, Integer v) {
        bfsRanking(subgraph, v);
    }

    public void bfsRanking(UndirectedSparseGraphTO<Integer, Integer> subgraph, Integer v) {
        for (int i = 0; i < bfs.length; i++) {
            bfs[i] = null;
        }
        discored = 1;

        queue.clear();
        queue.add(v);
        bfs[v] = 0;
        visitVertexRanking(v, bfs, subgraph);
    }

    public void revisitVertex(UndirectedSparseGraphTO<Integer, Integer> subgraph1,
            Integer v) {
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            int depth = bfs[poll] + 1;
            Collection<Integer> ns = (Collection<Integer>) subgraph1.
                    getNeighborsUnprotected(poll);
            for (Integer nv : ns) {
                if (bfs[nv] == null) {
                    discored++;
                    bfs[nv] = depth;
                    queue.add(nv);
                } else if (bfs[nv] > depth) {
                    bfs[nv] = depth;
                    queue.add(nv);
                }
            }
        }
    }

    public void revisitVertex(Integer v, Integer[] bfs,
            UndirectedSparseGraphTO<Integer, Integer> subgraph1) {
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            int depth = bfs[poll] + 1;
            Collection<Integer> ns = (Collection<Integer>) subgraph1.
                    getNeighborsUnprotected(poll);
            for (Integer nv : ns) {
                if (bfs[nv] == null || bfs[nv] > depth) {
                    bfs[nv] = depth;
                    queue.add(nv);
                }
            }
        }
    }

    public void incBfs(UndirectedSparseGraphTO graph, Integer vroot, Integer newvert) {
        if (newvert != null) {
            bfs[newvert] = 1;
            queue.add(newvert);
            revisitVertexRanking(vroot, bfs, graph);
        }
    }

    public void incBfs(UndirectedSparseGraphTO graph, Integer newroowt) {
        if (bfs[newroowt] == null) {
            discored++;
        }
        bfs[newroowt] = 0;
        queue.add(newroowt);
        revisitVertex(graph, newroowt);
    }

    public boolean isEmpty(UndirectedSparseGraphTO graph, Integer newroowt) {
        return bfs[newroowt] == null;
    }

    public void clearBfs() {
        for (int i = 0; i < bfs.length; i++) {
            bfs[i] = null;
        }
    }

}
