package io.github.braully.graph.operation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;

/**
 *
 * Reference:
 */
public class RandP3 extends AbstractHeuristic {

    static final String type = "Target set selection";
    static final String description = "Rand-P3";

    @Override
    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph) {

        /* Processar a buscar pelo hullset e hullnumber */
        Map<String, Object> response = new HashMap<>();

        try {
            Set<Integer> tipDecomp = randP3(graph);
            response.put("RandP3", tipDecomp);
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_SET, tipDecomp);
            response.put("|TSS|", tipDecomp.size());
            response.put(IGraphOperation.DEFAULT_PARAM_NAME_RESULT, tipDecomp.size());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public Set<Integer> randP3(UndirectedSparseGraphTO<Integer, Integer> graph) {
        return randP3(graph, 42);
    }

    public Set<Integer> randP3(UndirectedSparseGraphTO<Integer, Integer> graph, int seed) {
        return randP3(graph, seed, 100, true, 1.2, 1.0);
    }

    public Set<Integer> randP3(UndirectedSparseGraphTO<Integer, Integer> graph, int seed,
            int samples, boolean weighted,
            double velocity, double coverage) {
        Random random = new Random(seed);
        Collection<Integer> vertices = graph.getVertices();
        List<Integer> nodes = new ArrayList<>(vertices);
        int n = nodes.size();

        int low = 1;
        int high = n;
        int coverageCountBest = (int) Math.round(coverage * n);

        // Inicializa pesos
        Map<Integer, Double> weights = new HashMap<>();
        for (Integer v : nodes) {
            weights.put(v, 1.0);
        }

        Set<Integer> best_hull = new LinkedHashSet<>();
        double best_cov = 0.0;

        Set<Integer> best_local = null;

        // Busca binária
        while (low <= high) {
            int k = (low + high) / 2;
            ExistsHullResult result = existsHull(graph, k, weights, samples, seed + k, random);
            Set<Integer> S = result.set;

            Set<Integer> H = p3Closure(graph, S);
            double cov = (double) H.size() / n;

            if (cov >= coverage) {
                best_local = S;

                // Refinar o conjunto
                if (best_hull.isEmpty()) {
                    best_hull = best_local;
                } else {
                    List<Integer> refined = new ArrayList<>(best_local);
                    Collections.sort(refined);

                    Iterator<Integer> it = refined.iterator();
                    while (it.hasNext()) {
                        Integer v = it.next();
                        List<Integer> test = new ArrayList<>(refined);
                        test.remove(v);
                        if (p3Closure(graph, new LinkedHashSet<>(test)).size() >= coverageCountBest) {
                            it.remove();
                        }
                    }
                    best_local = new LinkedHashSet<>(refined);
                }
                if (best_local.size() < best_hull.size()) {
                    best_hull = best_local;
                    System.out.println("Refinamento: novo melhor conjunto com tamanho " + best_hull.size());
                } else{
                    continue;
                }

                best_cov = cov;
                high = k - 1;

                // Atualiza pesos se weighted = true
                if (weighted) {
                    for (Integer v : S) {
                        weights.put(v, weights.get(v) * velocity);
                    }
                }
            } else {
                low = k + 1;
            }
        }


        // Refinamento: remove vértices desnecessários (sequencialmente, para
        // evitar remover em lote um conjunto de vértices que só são
        // individualmente redundantes, mas não em conjunto)
        if (best_cov >= coverage) {
            List<Integer> refined = new ArrayList<>(best_hull);
            Collections.sort(refined);

            Iterator<Integer> it = refined.iterator();
            while (it.hasNext()) {
                Integer v = it.next();
                List<Integer> test = new ArrayList<>(refined);
                test.remove(v);
                if (p3Closure(graph, new LinkedHashSet<>(test)).size() >= coverageCountBest) {
                    it.remove();
                }
            }
            best_hull = new LinkedHashSet<>(refined);
        }

        return best_hull;
    }

    /**
     * Calcula o fecho P3 de um conjunto S no grafo G. Um vértice é ativado se
     * tem pelo menos 2 vizinhos ativos.
     */
    protected Set<Integer> p3Closure(UndirectedSparseGraphTO<Integer, Integer> graph, Set<Integer> S) {
        // Set<Integer> activated = new LinkedHashSet<>(S);
        //
        // while (true) {
        // Set<Integer> newActive = new LinkedHashSet<>();
        // Collection<Integer> vertices = graph.getVertices();
        //
        // for (Integer v : vertices) {
        // if (activated.contains(v)) {
        // continue;
        // }
        //
        // // Conta vizinhos ativos
        // int activeNeighbors = 0;
        // Collection<Integer> neighbors = graph.getNeighborsUnprotected(v);
        // for (Integer u : neighbors) {
        // if (activated.contains(u)) {
        // activeNeighbors++;
        // if (activeNeighbors >= 2) {
        // break;
        // }
        // }
        // }
        //
        // if (activeNeighbors >= 2) {
        // newActive.add(v);
        // }
        // }
        //
        // if (newActive.isEmpty()) {
        // break;
        // }
        // activated.addAll(newActive);
        // }
        Set<Integer> activated = this.hullClosure(graph, S);

        return activated;
    }

    /**
     * Verifica probabilisticamente se existe um hull set de tamanho k.
     */
    protected ExistsHullResult existsHull(UndirectedSparseGraphTO<Integer, Integer> graph, int k,
            Map<Integer, Double> weights,
            int samples, int seed, Random random) {
        random.setSeed(seed);

        // Inicializa kr uma única vez; as threads apenas leem o snapshot.
        initKr(graph);
        final int[] krSnapshot = this.kr;
        final int vertexCount = graph.getVertexCount();

        int numThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), samples));
        final int chunk = (samples + numThreads - 1) / numThreads;

        final AtomicBoolean fullFound = new AtomicBoolean(false);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<ExistsHullResult>> futures = new ArrayList<>();

        for (int t = 0; t < numThreads; t++) {
            final int threadIndex = t;
            Callable<ExistsHullResult> task = () -> {
                Random localRandom = new Random(seed + threadIndex);
                int inicio = threadIndex * chunk;
                int fim = Math.min(samples, inicio + chunk);

                Set<Integer> bestSet = new LinkedHashSet<>();
                int bestClosureSize = 0;

                for (int i = inicio; i < fim && !fullFound.get(); i++) {
                    Set<Integer> S = new LinkedHashSet<>(weightedRandomSample(weights, k, localRandom));
                    Set<Integer> H = this.hullClosure(graph, S, krSnapshot);

                    if (H.size() > bestClosureSize) {
                        bestSet = S;
                        bestClosureSize = H.size();
                    }

                    if (H.size() == vertexCount) {
                        fullFound.set(true);
                        return new ExistsHullResult(true, S, H.size());
                    }
                }
                return new ExistsHullResult(bestClosureSize == vertexCount, bestSet, bestClosureSize);
            };
            futures.add(executor.submit(task));
        }

        Set<Integer> bestSet = new LinkedHashSet<>();
        int bestClosureSize = 0;

        for (Future<ExistsHullResult> future : futures) {
            try {
                ExistsHullResult partial = future.get();
                if (partial.found) {
                    bestSet = partial.set;
                    bestClosureSize = partial.closureSize;
                    break;
                }
                if (partial.closureSize > bestClosureSize) {
                    bestSet = partial.set;
                    bestClosureSize = partial.closureSize;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException e) {
                // Ignora a thread que falhou e continua com as demais.
            }
        }
        executor.shutdown();

        return new ExistsHullResult(bestClosureSize == vertexCount, bestSet);
    }

    /**
     * Amostragem aleatória ponderada usando o esquema Efraimidis-Spirakis.
     */
    protected List<Integer> weightedRandomSample(Map<Integer, Double> weights, int k, Random random) {
        List<KeyValue> keys = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : weights.entrySet()) {
            Integer v = entry.getKey();
            Double w = entry.getValue();

            if (w <= 0) {
                continue;
            }

            double r = random.nextDouble();
            double key = Math.pow(r, 1.0 / w);
            keys.add(new KeyValue(key, v));
        }

        // Ordena em ordem decrescente
        Collections.sort(keys, (a, b) -> Double.compare(b.key, a.key));

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < Math.min(k, keys.size()); i++) {
            result.add(keys.get(i).value);
        }

        return result;
    }

    // Classes auxiliares
    protected static class ExistsHullResult {

        boolean found;
        Set<Integer> set;
        int closureSize;

        public ExistsHullResult(boolean found, Set<Integer> set) {
            this(found, set, -1);
        }

        public ExistsHullResult(boolean found, Set<Integer> set, int closureSize) {
            this.found = found;
            this.set = set;
            this.closureSize = closureSize;
        }
    }

    protected static class KeyValue {

        double key;
        Integer value;

        public KeyValue(double key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public String getTypeProblem() {
        return type;
    }

    @Override
    public String getName() {
        return description;
    }

    public static void main(String... args) throws FileNotFoundException, IOException {
        RandP3 optss = new RandP3();
        optss.setK(2);

        UndirectedSparseGraphTO<Integer, Integer> graph = null;
        // graph = UtilGraph.loadGraphES("0-2,1-4,2-3,2-4,");
        // graph = UtilGraph.loadBigDataset(
        // new
        // FileInputStream("/home/strike/Workspace/tss/TSSGenetico/Instancias/Foursquare/nodes.csv"),
        // new
        // FileInputStream("/home/strike/Workspace/tss/TSSGenetico/Instancias/Foursquare/edges.csv"));

        try {
            int i = 0;
            URI urinode = URI.create("jar:file:data/big/all-big" + i + ".zip!/BlogCatalog3/nodes.csv");
            URI uriedges = URI.create("jar:file:data/big/all-big" + i + ".zip!/BlogCatalog3/edges.csv");
            InputStream streamnode = urinode.toURL().openStream();
            InputStream streamedges = uriedges.toURL().openStream();
            graph = UtilGraph.loadBigDataset(streamnode,
                    streamedges);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(graph.toResumedString());
        // optss.setPercent(0.5);
        UtilProccess.printStartTime();
        Set<Integer> buildOptimizedHullSet = optss.randP3(graph);
        System.out.println("buildOptimizedHullSet: " + buildOptimizedHullSet.size());
        UtilProccess.printStartTime();

        if (!optss.checkIfHullSet(graph, buildOptimizedHullSet)) {
            throw new IllegalStateException("NOT HULL SET");
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}

/*
// Codigo de referencia em python:
"""
Algoritmos para o problema P3-hull number
=========================================

Implementação dos principais algoritmos para o problema do número P3-hull
utilizados no paper:

"A Randomized Approximation Algorithm for P3-Hull Number
 in Social Network Analysis" (Ponciano et al., 2025).

Algoritmos implementados:
-----------------------
- RAND-P3: Algoritmo randomizado de aproximação usando busca binária
- TIP-DECOMP-R: Versão randomizada do TIP-DECOMP
- MTS-M: Heurística Modified Minimum Target Set
- DistDifDelta: Heurística refinada para encontrar conjunto alvo

Todos os algoritmos assumem grafo não-direcionado e sem pesos.
"""

from __future__ import annotations
import networkx as nx
import random
import os
import time
from typing import Set, Dict, Tuple, Optional, List
from collections import deque


# =====================================================================
# 1. Funções básicas e utilitárias
# =====================================================================

def p3_closure(G: nx.Graph, S: Set) -> Set:
    """
    Compute the P3-closure of a set S in graph G.

    A vertex becomes activated if it has at least two active neighbors.
    The process repeats until no new vertices can be activated.

    Parameters
    ----------
    G : networkx.Graph
        Input undirected graph.
    S : set
        Initial seed set.

    Returns
    -------
    H : set
        P3-closure of S (i.e., all activated vertices).
    """
    activated = set(S)
    while True:
        new_active = set()
        for v in G.nodes():
            if v in activated:
                continue
            active_neighbors = len([u for u in G.neighbors(v) if u in activated])
            if active_neighbors >= 2:
                new_active.add(v)
        if not new_active:
            break
        activated |= new_active
    return activated


def weighted_random_sample(weights: Dict, k: int) -> List:
    """
    Sample k distinct elements based on given weights using
    the Efraimidis–Spirakis weighted random sampling scheme.

    Parameters
    ----------
    weights : dict
        Mapping vertex -> weight (non-negative).
    k : int
        Sample size.

    Returns
    -------
    sample : list
        List of selected vertices.
    """
    keys = []
    for v, w in weights.items():
        if w <= 0:
            continue
        r = random.random()
        keys.append((r ** (1.0 / w), v))
    keys.sort(reverse=True)
    return [v for (_, v) in keys[:k]]


def exists_hull(G: nx.Graph, k: int, weights: Optional[Dict] = None,
                samples: int = 200, seed: int = 42) -> Tuple[bool, Set]:
    """
    Check (probabilistically) whether a P3-hull set of size k exists.

    Parameters
    ----------
    G : networkx.Graph
        Input graph.
    k : int
        Candidate hull size.
    weights : dict, optional
        Probability weights for vertex sampling.
    samples : int
        Number of random samples to test.
    seed : int
        Random seed for reproducibility.

    Returns
    -------
    exists : bool
        True if a hull set of size k was found.
    best_set : set
        Best (largest closure) set found.
    """
    random.seed(seed)
    nodes = list(G.nodes())
    if weights is None:
        weights = {v: 1.0 for v in nodes}

    best_set = set()
    best_closure_size = 0

    for _ in range(samples):
        S = set(weighted_random_sample(weights, k))
        H = p3_closure(G, S)
        if len(H) > best_closure_size:
            best_set = S
            best_closure_size = len(H)
        if len(H) == len(G):
            return True, S

    return best_closure_size == len(G), best_set


# =====================================================================
# 2. RAND-P3 Algorithm
# =====================================================================

def rand_p3(G: nx.Graph, low: int = 1, high: Optional[int] = None,
            samples: int = 1000, weighted: bool = True,
            velocity: float = 1.2, seed: int = 42,
            coverage: float = 1) -> Tuple[Set, int]:
    """
    RAND-P3 (corrected version):
    Randomized binary-search approximation with early stopping when
    closure covers ≥ coverage * |V|.
    """
    random.seed(seed)
    nodes = list(G.nodes())
    n = len(nodes)
    if high is None:
        high = n

    weights = {v: 1.0 for v in nodes}
    best_hull, best_k, best_cov = set(), n, 0.0

    while low <= high:
        k = (low + high) // 2
        found, S = exists_hull(G, k, weights, samples, seed + k)
        H = p3_closure(G, S)
        cov = len(H) / n

        if cov >= coverage:
            best_hull, best_k, best_cov = S, k, cov
            high = k - 1
            if weighted:
                for v in S:
                    weights[v] *= velocity
        else:
            low = k + 1

    if best_cov >= coverage:
        refined = sorted(best_hull)
        for v in list(refined):
            test = set(refined)
            test.remove(v)
            if len(p3_closure(G, test)) >= coverage * n:
                refined.remove(v)
        best_hull = set(refined)
        best_k = len(best_hull)

    return best_hull, best_k




# =====================================================================
# 8. Função principal
# =====================================================================

"""
Algoritmos para o problema P3-hull number
=========================================

Implementação completa com versões corrigidas do MTS e TIP-DECOMP-P3
que garantem ativação 100% da rede.
"""

import networkx as nx
import random
import os
import time
from typing import Set, Dict, Tuple, List
from collections import deque


# =====================================================================
# 1. Funções básicas e utilitárias
# =====================================================================

def threshold(v, G):
    """Função de limiar para P3-convexidade (threshold = 2)."""
    if G.degree[v] == 0:
        return 0
    if G.degree[v] == 1:
        return 1
    else:
        return 2


def p3_closure(G: nx.Graph, S: Set) -> Set:
    """
    Compute the P3-closure of a set S in graph G.
    A vertex becomes activated if it has at least two active neighbors.
    """
    activated = set(S)
    while True:
        new_active = set()
        for v in G.nodes():
            if v in activated:
                continue
            active_neighbors = len([u for u in G.neighbors(v) if u in activated])
            if active_neighbors >= 2:
                new_active.add(v)
        if not new_active:
            break
        activated |= new_active
    return activated


def init_contaminado(fecho):
    """Inicializa estrutura de controle para propagação."""
    contaminado = {}
    for v in fecho:
        contaminado[v] = None
    return contaminado


def contaminar(contaminado, vcontaminante, vcontaminado):
    """
    Função de contaminação para propagação P3.
    Retorna True se o vértice foi ativado.
    """
    if vcontaminado in contaminado:
        if contaminado[vcontaminado] is None:
            return False
        if len(contaminado[vcontaminado]) < 2 and vcontaminante not in contaminado[vcontaminado]:
            contaminado[vcontaminado].add(vcontaminante)
            if len(contaminado[vcontaminado]) == 2:
                return True
    else:
        contaminado[vcontaminado] = set()
        contaminado[vcontaminado].add(vcontaminante)
        if len(contaminado[vcontaminado]) == 2:
            return True
    return False


def get_novo_fecho(fecho, graph, contaminado):
    """Obtém novos vértices ativados na iteração atual."""
    novo_fecho = set()
    for v in fecho:
        if v in graph:
            for w in graph[v]:
                novo_contaminado = contaminar(contaminado, v, w)
                if novo_contaminado:
                    novo_fecho.add(w)
    return novo_fecho


# =====================================================================
# 5. Função para carregar grafo
# =====================================================================

def load_graph_from_file(filename: str) -> nx.Graph:
    """Carrega grafo não direcionado a partir de arquivo de arestas."""
    G = nx.Graph()

    if not os.path.isfile(filename):
        raise FileNotFoundError(f"Arquivo '{filename}' não encontrado.")

    with open(filename, 'r') as file:
        for line_num, line in enumerate(file, 1):
            line = line.strip()
            if not line or line.startswith('#'):
                continue

            try:
                vertices = line.split()
                if len(vertices) >= 2:
                    u, v = map(int, vertices[:2])
                    G.add_edge(u, v)
            except ValueError:
                print(f"Aviso: Linha {line_num} ignorada")

    return G


# =====================================================================
# 6. Função principal com medição de tempo
# =====================================================================

def main():
    """Função principal que carrega o grafo e executa TODOS os algoritmos."""

    # Nome do arquivo de entrada
    filename = '012.txt'

    try:
        # Carregar o grafo
        print(f"Carregando grafo do arquivo '{filename}'...")
        G = load_graph_from_file(filename)

        if G.number_of_nodes() == 0:
            print("Erro: O grafo está vazio!")
            return

        # Estatísticas do grafo
        print(f"\n{'='*60}")
        print("ESTATÍSTICAS DO GRAFO")
        print(f"{'='*60}")
        print(f"  Nós: {G.number_of_nodes()}")
        print(f"  Arestas: {G.number_of_edges()}")
        print(f"  Densidade: {nx.density(G):.4f}")
        print(f"  Componentes conectados: {nx.number_connected_components(G)}")

        degrees = [d for n, d in G.degree()]
        print(f"  Grau médio: {sum(degrees)/len(degrees):.2f}")
        print(f"  Grau máximo: {max(degrees)}")
        print(f"  Grau mínimo: {min(degrees)}")

        # Dicionário de limiares
        t = {v: threshold(v, G) for v in G.nodes()}

        # =============================================================
        # EXECUTAR TODOS OS ALGORITMOS
        # =============================================================
        print(f"\n{'='*60}")
        print("EXECUTANDO TODOS OS ALGORITMOS")
        print(f"{'='*60}")

        resultados = {}

        # 1. RAND-P3
        print("\n🔹 RAND-P3:")
        start = time.time()
        randp3_set, randp3_k = rand_p3(G, seed=42)
        randp3_time = time.time() - start
        randp3_closure = p3_closure(G, randp3_set)

        resultados['RAND-P3'] = {
            'set': randp3_set,
            'size': len(randp3_set),
            'time': randp3_time,
            'closure': randp3_closure,
            'activation': len(randp3_closure)
        }

        print(f"  Tamanho do conjunto: {len(randp3_set)}")
        print(f"  Tempo: {randp3_time:.2f} segundos")
        print(f"  Ativação: {len(randp3_closure)}/{len(G)} ({len(randp3_closure)/len(G)*100:.1f}%)")
        print(f"  Status: {'✅ COMPLETO' if len(randp3_closure) == len(G) else '❌ INCOMPLETO'}")

        

    except FileNotFoundError as e:
        print(f"Erro: {e}")
        print(f"Certifique-se de que o arquivo '{filename}' está no diretório correto.")
    except Exception as e:
        print(f"Erro inesperado: {e}")
        import traceback
        traceback.print_exc()


if __name__ == "__main__":
    main()
    */