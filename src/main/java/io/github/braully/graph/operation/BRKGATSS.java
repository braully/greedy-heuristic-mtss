/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.concurrent.ThreadLocalRandom;
import java.util.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author braully
 */
public class BRKGATSS extends AbstractHeuristic implements IGraphOperation {

    @Override
    public String getDescription() {
        return "BRKGATSS";
    }

    public static class Utils {

        public static double randomFloat() {
            return ThreadLocalRandom.current().nextDouble();
        }

        public static int sample(List<Double> cdf) {
            int n = cdf.size();
            double x = randomFloat();
            if (x < cdf.get(0)) {
                return 1;
            }

            int low = 0, high = n - 1;
            while (high - low > 1) {
                int mid = (low + high) >> 1;
                if (x > cdf.get(mid)) {
                    low = mid;
                } else {
                    high = mid;
                }
            }
            return high + 1;
        }

        public static Set<Integer> pickSet(int N, int k) {
            Set<Integer> elems = new HashSet<>();
            Random random = new Random();

            while (elems.size() < k) {
                elems.add(random.nextInt(N) + 1);
            }

            return elems;
        }

        public static List<Integer> pick(int N, int k) {
            Set<Integer> elems = pickSet(N, k);
            List<Integer> result = new ArrayList<>(elems);
            Collections.shuffle(result);
            return result;
        }

        public static <T> T selectRandomly(List<T> list) {
            if (list.isEmpty()) {
                return null;
            }
            Random random = new Random();
            return list.get(random.nextInt(list.size()));
        }

        public static void printVector(List<Integer> vector) {
            for (int x : vector) {
                System.out.print(x + " ");
            }
        }
    }

    public static class BRKGA extends AbstractHeuristic {

        protected List<List<Integer>> G;
        protected int n, m;
        protected double timeLimit = 100;
        protected int nInd = 46;
        protected double pe = 0.24, pm = 0.13, pelite = 0.69;
        protected boolean seed = true;
        protected List<Integer> deg;
        protected List<Integer> score;
        protected List<List<Double>> P;
        protected List<List<Double>> Pm;
        protected List<List<Double>> Pc;
        protected List<List<Integer>> result;
        protected int PmSize, PeSize, PcSize;

        public BRKGA() {
            // Default constructor
        }

        public static BRKGA create(UndirectedSparseGraphTO<Integer, Integer> graphr) {
            List<List<Integer>> graph = new ArrayList<>(graphr.getVertexCount());
            for (Integer v : (Collection<Integer>) graphr.getVertices()) {
                graph.add(new ArrayList(graphr.getNeighborsUnprotected(v)));
            }

//            // vértice 0 conectado a 1 e 2
//            graph.add(Arrays.asList(1, 2));
//            // vértice 1 conectado a 0 e 3
//            graph.add(Arrays.asList(0, 3));
//            // vértice 2 conectado a 0 e 4
//            graph.add(Arrays.asList(0, 4));
//            // vértice 3 conectado a 1
//            graph.add(Arrays.asList(1));
//            // vértice 4 conectado a 2
//            graph.add(Arrays.asList(2));
//
//            int n = 5;
//            int m = 4;
//            return new BRKGA(graph, n, m);
//            return new BRKGA(graph, graphr.getVertexCount(), graphr.getEdgeCount());
            return new FastBRKGA(graph, graphr.getVertexCount(), graphr.getEdgeCount());
        }

        public BRKGA(List<List<Integer>> graph, int v, int e) {
            G = graph;
            n = v;
            m = e;

            deg = new ArrayList<>(Collections.nCopies(n, 0));
            for (int i = 0; i < n; i++) {
                deg.set(i, G.get(i).size());
            }

            score = new ArrayList<>(Collections.nCopies(nInd, 0));
            P = new ArrayList<>();
            Pm = new ArrayList<>();
            Pc = new ArrayList<>();
            result = new ArrayList<>();

            for (int i = 0; i < nInd; i++) {
                P.add(new ArrayList<>(Collections.nCopies(n, 0.0)));
                Pm.add(new ArrayList<>(Collections.nCopies(n, 0.0)));
                Pc.add(new ArrayList<>(Collections.nCopies(n, 0.0)));
                result.add(new ArrayList<>(Collections.nCopies(n, 0)));
            }

            timeLimit = Math.max(100.0, (double) n / 100);

            for (int i = 0; i < nInd; i++) {
                for (int j = 0; j < n; j++) {
                    P.get(i).set(j, Utils.randomFloat());
                }
            }

            if (seed) {
                for (int j = 0; j < n; j++) {
                    P.get(0).set(j, 0.5);
                }
            }
        }

        public double getPe() {
            return pe;
        }

        public double getPm() {
            return pm;
        }

        public double getPelite() {
            return pelite;
        }

        public List<Integer> run(int threshold, String filename) {
            for (int i = 0; i < nInd; i++) {
                for (int j = 0; j < n; j++) {
                    P.get(i).set(j, Utils.randomFloat());
                }
            }

            int cnt = 0;
            int best = n, bestCnt = 0;
            if (seed) {
                for (int j = 0; j < n; j++) {
                    P.get(0).set(j, 0.5);
                }
            }

            Instant start = Instant.now();
            double bestTime = 0;
            List<Integer> bestRes = new ArrayList<>();

            while (true) {
                Instant end = Instant.now();
                Duration duration = Duration.between(start, end);
                double elapsedTime = duration.toMillis() / 1000.0;

                if (elapsedTime > timeLimit) {
                    break;
                }

                Pair<List<Integer>, List<Integer>> id = getEliteAndNonEliteId();
                List<Integer> eliteId = id.first;
                List<Integer> nonEliteId = id.second;

                mutate();
                PcSize = nInd - PmSize - eliteId.size();
                crossover(eliteId, nonEliteId);

                List<List<Double>> newPn = new ArrayList<>();
                for (int i : eliteId) {
                    newPn.add(new ArrayList<>(P.get(i)));
                }
                for (int i = 0; i < PmSize; i++) {
                    newPn.add(new ArrayList<>(Pm.get(i)));
                }
                for (int i = 0; i < PcSize; i++) {
                    newPn.add(new ArrayList<>(Pc.get(i)));
                }
                P = newPn;

                int bestVal = Collections.min(score);
                if (best > bestVal) {
                    best = bestVal;
                    bestTime = elapsedTime;
                    bestCnt = cnt;
                    int bestValId = score.indexOf(bestVal);
                    bestRes = new ArrayList<>(result.get(bestValId));

                    try (FileWriter fw = new FileWriter(filename, true)) {
                        fw.write(elapsedTime + " " + cnt + " " + best + " ");
                        for (int i = 0; i < n; i++) {
                            if (result.get(bestValId).get(i) == 1) {
                                fw.write(i + " ");
                            }
                        }
                        fw.write("\n");
                    } catch (IOException e) {
                        System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
                    }

                    if (best <= threshold) {
                        break;
                    }
                }
                cnt++;
            }

            try (FileWriter fw = new FileWriter(filename, true)) {
                fw.write("Finish " + bestTime + " " + best + " " + bestCnt + " ");
                for (int i = 0; i < n; i++) {
                    if (bestRes.get(i) == 1) {
                        fw.write(i + " ");
                    }
                }
                fw.write("\n");
            } catch (IOException e) {
                System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            }

            System.out.println("Best: " + best + " Time: " + bestTime);
            return bestRes;
        }

        public List<Integer> run() {
            return run(0, "BRKGA.txt");
        }

        public Pair<List<Integer>, List<Integer>> getEliteAndNonEliteId() {
            double curPelite = getPe();
            PeSize = (int) Math.ceil(curPelite * nInd);
            eval();

            List<Pair<Integer, Integer>> eid = new ArrayList<>();
            for (int i = 0; i < nInd; i++) {
                eid.add(new Pair<>(score.get(i), i));
            }
            eid.sort(Comparator.comparing(p -> p.first));

            List<Integer> res1 = new ArrayList<>();
            List<Integer> res2 = new ArrayList<>();

            for (int i = 0; i < PeSize; i++) {
                res1.add(eid.get(i).second);
            }
            for (int i = PeSize; i < nInd; i++) {
                res2.add(eid.get(i).second);
            }

            return new Pair<>(res1, res2);
        }

        public void mutate() {
            double curPm = getPm();
            PmSize = (int) Math.ceil(curPm * nInd);

            for (int i = 0; i < PmSize; i++) {
                for (int j = 0; j < n; j++) {
                    Pm.get(i).set(j, Utils.randomFloat());
                }
            }
        }

        public void crossover(List<Integer> elite, List<Integer> nonElite) {
            double curPe = getPelite();

            for (int i = 0; i < PcSize; i++) {
                int x = Utils.selectRandomly(elite);
                int y = Utils.selectRandomly(nonElite);

                for (int j = 0; j < n; j++) {
                    if (Utils.randomFloat() < curPe) {
                        Pc.get(i).set(j, P.get(x).get(j));
                    } else {
                        Pc.get(i).set(j, P.get(y).get(j));
                    }
                }
            }
        }

        public void eval() {
            List<Double> d = new ArrayList<>(Collections.nCopies(n, 0.0));

            for (int i = 0; i < nInd; i++) {
                for (int j = 0; j < n; j++) {
                    d.set(j, deg.get(j) * P.get(i).get(j));
                }
                result.set(i, MDG(d));
                int x = result.get(i).stream().mapToInt(Integer::intValue).sum();
                score.set(i, x);
            }
        }

        public List<Integer> MDG(List<Double> d) {
            List<Integer> Cov = new ArrayList<>(Collections.nCopies(n, 0));
            List<Integer> S = new ArrayList<>(Collections.nCopies(n, 0));

            while (true) {
                int sum = Cov.stream().mapToInt(Integer::intValue).sum();
                if (sum == n) {
                    break;
                }

                double maxd = -1;
                int id = -1;

                for (int i = 0; i < n; i++) {
                    if (Cov.get(i) == 0) {
                        if (d.get(i) > maxd) {
                            maxd = d.get(i);
                            id = i;
                        }
                    }
                }

                Cov.set(id, 1);
                S.set(id, 1);
                Cov = phi(Cov);
            }

            return S;
        }

        public List<Integer> phi(List<Integer> Cov) {
            Queue<Integer> q = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                if (Cov.get(i) == 1) {
                    q.add(i);
                }
            }

            List<Integer> d = new ArrayList<>(Collections.nCopies(n, 0));
            List<Integer> res = new ArrayList<>(Collections.nCopies(n, 0));

            while (!q.isEmpty()) {
                int u = q.poll();
                if (res.get(u) == 1) {
                    continue;
                }
                res.set(u, 1);

                for (int v : G.get(u)) {
                    d.set(v, d.get(v) + 1);
                    int degree = deg.get(v);
                    //Adaptation to variable treshold
                    int treshold = (deg.get(v) + 1) / 2;
                    if (rTreshold != null) {
                        treshold = Math.min(rTreshold, degree);
                    } else if (kTreshold != null) {
                        treshold = kTreshold;
                    } else if (percentTreshold != null) {
                        double ddgree = degree;
                        double ki = Math.ceil(percentTreshold * ddgree);
//                        int kii = (int) Math.ceil((double) degree / 2);
                        treshold = (int) ki;
                    }
                    if (d.get(v) == treshold) {
                        q.add(v);
                    }
                }
            }

            return res;
        }

        @Override
        public String toString() {
            return "n: " + n + " m: " + m;
        }

        @Override
        public String getDescription() {
            return "BRKGATSS";
        }

        @Override
        public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    public static class Pair<T, U> {

        public final T first;
        public final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    public static void main(String[] args) {
        // Exemplo de grafo com 5 vértices e 4 arestas
        List<List<Integer>> graph = new ArrayList<>();

        // vértice 0 conectado a 1 e 2
        graph.add(Arrays.asList(1, 2));
        // vértice 1 conectado a 0 e 3
        graph.add(Arrays.asList(0, 3));
        // vértice 2 conectado a 0 e 4
        graph.add(Arrays.asList(0, 4));
        // vértice 3 conectado a 1
        graph.add(Arrays.asList(1));
        // vértice 4 conectado a 2
        graph.add(Arrays.asList(2));

        int n = 5;
        int m = 4;

        BRKGA brkga = new BRKGA(graph, n, m);
        List<Integer> result = brkga.run();

        System.out.print("Resultado: ");
        Utils.printVector(result);
        System.out.println();
    }

    public static class FastBRKGA extends BRKGA {

        private final double beta = 1.5;
        private final List<Double> peDistribution, pePowerLaw;
        private final List<Double> pmDistribution, pmPowerLaw;
        private final List<Double> peliteDistribution, pelitePowerLaw;

        public FastBRKGA(List<List<Integer>> graph, int v, int e) {
            super(graph, v, e);

            // Initialize pe distribution
            peDistribution = new ArrayList<>();
            List<Double> prefixSum = new ArrayList<>();

            for (int i = 0; i < 16; i++) {
                double value = Math.pow(i + 1, -beta);
                peDistribution.add(value);
                if (i == 0) {
                    prefixSum.add(value);
                } else {
                    prefixSum.add(value + prefixSum.get(i - 1));
                }
            }

            pePowerLaw = new ArrayList<>();
            double lastSum = prefixSum.get(prefixSum.size() - 1);
            for (int i = 0; i < 16; i++) {
                pePowerLaw.add(prefixSum.get(i) / lastSum);
            }

            // Initialize pm distribution
            pmDistribution = new ArrayList<>();
            prefixSum = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                double value = Math.pow(i + 1, -beta);
                pmDistribution.add(value);
                if (i == 0) {
                    prefixSum.add(value);
                } else {
                    prefixSum.add(value + prefixSum.get(i - 1));
                }
            }

            pmPowerLaw = new ArrayList<>();
            lastSum = prefixSum.get(prefixSum.size() - 1);
            for (int i = 0; i < 20; i++) {
                pmPowerLaw.add(prefixSum.get(i) / lastSum);
            }

            // Initialize pelite distribution
            peliteDistribution = new ArrayList<>();
            prefixSum = new ArrayList<>();

            for (int i = 0; i < 30; i++) {
                double value = Math.pow(i + 1, -beta);
                peliteDistribution.add(value);
                if (i == 0) {
                    prefixSum.add(value);
                } else {
                    prefixSum.add(value + prefixSum.get(i - 1));
                }
            }

            pelitePowerLaw = new ArrayList<>();
            lastSum = prefixSum.get(prefixSum.size() - 1);
            for (int i = 0; i < 30; i++) {
                pelitePowerLaw.add(prefixSum.get(i) / lastSum);
            }
        }

        @Override
        public double getPe() {
            int x = Utils.sample(pePowerLaw);
            return 0.1 + 0.01 * (15 - x);
        }

        @Override
        public double getPm() {
            int x = Utils.sample(pmPowerLaw);
            return 0.1 + 0.01 * x;
        }

        @Override
        public double getPelite() {
            int x = Utils.sample(pelitePowerLaw);
            return 0.5 + 0.01 * x;
        }
    }

    public Set<Integer> brkgatss(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> result = new HashSet<>();

        return result;
    }

    @Override
    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {
        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();

        try {

            BRKGA brkga = BRKGA.create(graph);
            brkga.setK(this.kTreshold);
            brkga.setR(this.rTreshold);
            brkga.setPercent(this.percentTreshold);
            List<Integer> result = brkga.run();

            System.out.print("Resultado: ");
            Utils.printVector(result);
            System.out.println();

            response.put("BRKGA", result);
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_SET, result);
            response.put("|TSS|", result.size());
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, result.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

}
