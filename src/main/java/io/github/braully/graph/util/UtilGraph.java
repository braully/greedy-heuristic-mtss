package io.github.braully.graph.util;

import edu.uci.ics.jung.graph.util.Pair;
import io.github.braully.graph.UndirectedSparseGraphTO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Graphs utils
 *
 * @author Braully Rocha da Silva
 */
public class UtilGraph {

    private static final Logger log = Logger.getLogger(UtilGraph.class.getSimpleName());

    private static boolean verbose = false;

    public static void main(String... args) throws Exception {
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadBigDatasetRaw(InputStream edgesStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        if (edgesStream != null) {
            Map<Integer, Integer> vCount = new HashMap<>();
            TreeSet<Integer> mnodes = new TreeSet<>();
            List<Pair<Integer>> sedges = new ArrayList<>();

            int count = 0;
            String readLine = null;
            try {
                ret = new UndirectedSparseGraphTO<Integer, Integer>();

                BufferedReader redges = new BufferedReader(new InputStreamReader(edgesStream));
                readLine = null;
                while ((readLine = redges.readLine()) != null
                        && !(readLine = readLine.trim()).isEmpty()) {
                    if (readLine.startsWith("#")) {
                        continue;
                    }
                    String[] split = readLine.split("\t");
                    if (split.length >= 2) {
                        Integer v = Integer.parseInt(split[0].trim());
                        Integer t = Integer.parseInt(split[1].trim());
                        ret.addVertex(v);
                        ret.addVertex(t);
                        if (v != null && t != null && !v.equals(t)) {
                            ret.addEdge(v, t);
                        }
                    } else {
                        System.err.println("Line not parsed: " + readLine);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(readLine);
            }

        }
        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadBigDataset(InputStream edgesStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        if (edgesStream != null) {
            Map<Integer, Integer> vCount = new HashMap<>();
            TreeSet<Integer> mnodes = new TreeSet<>();
            List<Pair<Integer>> sedges = new ArrayList<>();

            int count = 0;
            String readLine = null;
            try {
                ret = new UndirectedSparseGraphTO<Integer, Integer>();

                BufferedReader redges = new BufferedReader(new InputStreamReader(edgesStream));
                readLine = null;
                while ((readLine = redges.readLine()) != null
                        && !(readLine = readLine.trim()).isEmpty()) {
                    if (readLine.startsWith("#")) {
                        continue;
                    }
                    String[] split = readLine.split("\t");
                    if (split.length >= 2) {
                        Integer v = Integer.parseInt(split[0].trim());
                        Integer t = Integer.parseInt(split[1].trim());
                        mnodes.add(t);
                        mnodes.add(v);
                        sedges.add(new Pair<Integer>(v, t));
                    } else {
                        System.err.println("Line not parsed: " + readLine);
                    }
                }
                for (Integer v : mnodes) {
                    vCount.put(v, count);
                    ret.addVertex(count);
                    count++;
                }

                for (Pair<Integer> p : sedges) {
                    Integer v = vCount.get(p.getFirst());
                    Integer t = vCount.get(p.getSecond());
                    if (v != null && t != null && !v.equals(t)) {
                        ret.addEdge(v, t);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(readLine);
            }

        }
        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadBigDataset(InputStream streamNodes, InputStream edgesStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        if (streamNodes != null && edgesStream != null) {
            Map<Integer, Integer> vCount = new HashMap<>();
            int count = 0;
            String readLine = null;
            try {
                ret = new UndirectedSparseGraphTO<Integer, Integer>();
                BufferedReader rnodes = new BufferedReader(new InputStreamReader(streamNodes));
                while ((readLine = rnodes.readLine()) != null
                        && !(readLine = readLine.trim()).startsWith("#")) {
                    Integer v = Integer.parseInt(readLine);
//                    ret.addVertex(v - 1);
                    vCount.put(v, count);
                    ret.addVertex(count);
                    count++;
                }
                BufferedReader redges = new BufferedReader(new InputStreamReader(edgesStream));
                readLine = null;
                while ((readLine = redges.readLine()) != null
                        && !(readLine = readLine.trim()).isEmpty()) {
                    if (readLine.startsWith("#")) {
                        continue;
                    }
                    String[] split = readLine.split(",");
                    if (split.length >= 2) {
//                        Integer v = Integer.parseInt(split[0].trim()) - 1;
//                        Integer t = Integer.parseInt(split[1].trim()) - 1;
                        Integer v = vCount.get(Integer.parseInt(split[0].trim()));
                        Integer t = vCount.get(Integer.parseInt(split[1].trim()));
                        if (v != null && t != null && !t.equals(v)) {
                            ret.addEdge(v, t);
                        } else {
                            if (verbose) {
                                System.err.println("Fail on edge v e t null: " + readLine);
                            }
                        }
                    } else {
                        if (verbose) {
                            System.err.println("Fail on edge: " + readLine);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(readLine);
            }
        }
        return ret;
    }

    static UndirectedSparseGraphTO<Integer, Integer> loadGraphCsr(InputStream uploadedInputStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        try {
            if (uploadedInputStream != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(uploadedInputStream));
                String csrColIdxsStr = null;
                String rowOffsetStr = null;

                String readLine = null;
                while ((readLine = r.readLine()) == null || readLine.trim().isEmpty() || readLine.trim().startsWith("#")) {
                }
                csrColIdxsStr = readLine;
                while ((readLine = r.readLine()) == null || readLine.trim().isEmpty() || readLine.trim().startsWith("#")) {
                }
                rowOffsetStr = readLine;
                if (csrColIdxsStr != null && !csrColIdxsStr.trim().isEmpty()
                        && rowOffsetStr != null && !rowOffsetStr.trim().isEmpty()) {
                    String[] csrColIdxsStrSplited = csrColIdxsStr.trim().split(" ");
                    String[] rowOffsetStrSplited = rowOffsetStr.trim().split(" ");
                    ret = new UndirectedSparseGraphTO<>();
                    int vertexCount = csrColIdxsStrSplited.length - 1;
                    int edgeCount = 0;
                    if (csrColIdxsStrSplited != null && csrColIdxsStrSplited.length > 0) {
                        for (int i = 0; i < vertexCount; i++) {
                            ret.addVertex(i);
                        }
                        for (int i = 0; i < vertexCount; i++) {
                            int ini = Integer.parseInt(csrColIdxsStrSplited[i]);
                            int fim = Integer.parseInt(csrColIdxsStrSplited[i + 1]);
                            for (; ini < fim; ini++) {
                                String strFim = rowOffsetStrSplited[ini];
                                ret.addEdge(edgeCount++, i, Integer.parseInt(strFim));
                            }
                        }
                    }
                }
//            System.out.println("CsrColIdxs: " + csrColIdxsStr);
//            System.out.println("RowOffset: " + rowOffsetStr);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "error", e);
        }
        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadGraphAdjMatrix(InputStream uploadedInputStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        try {
            if (uploadedInputStream != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(uploadedInputStream));
                List<String> lines = new ArrayList<>();
                String readLine = null;
                Integer verticeCount = 0;
                ret = new UndirectedSparseGraphTO<>();

                while ((readLine = r.readLine()) != null) {
                    if (!readLine.trim().isEmpty()
                            && !readLine.trim().startsWith("#")
                            && !readLine.trim().matches("\\D+.*")) {
                        lines.add(readLine);
//                        System.out.println(readLine);
                        ret.addVertex(verticeCount);
                        verticeCount = verticeCount + 1;
                    }
                }
                int edgeCount = 0;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line != null) {
                        String[] split = line.trim().split(" ");
                        if (split != null && split.length > 0 && line.trim().contains(" ")) {
                            for (int j = 0; j < split.length; j++) {
                                if ("1".equals(split[j])) {
                                    ret.addEdge(edgeCount++, i, j);
                                }
                            }
                        } else {
                            char[] charArray = line.trim().toCharArray();
                            if (charArray != null & charArray.length > 0) {
                                for (int j = 0; j < charArray.length; j++) {
                                    if ('1' == charArray[j]) {
                                        ret.addEdge(edgeCount++, i, j);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "error", e);
        }
        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadGraphAdjList(InputStream uploadedInputStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        try {
            if (uploadedInputStream != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(uploadedInputStream));
                List<String> lines = new ArrayList<>();
                String readLine = null;
                Integer verticeCount = 0;
                ret = new UndirectedSparseGraphTO<>();

                while ((readLine = r.readLine()) != null) {
                    if (!readLine.trim().isEmpty()
                            && !readLine.trim().startsWith("#")
                            && !readLine.trim().matches("\\D+.*")) {
                        readLine = readLine.replaceAll("\\s\\s+", " ");
                        lines.add(readLine);
//                        System.out.println(readLine);
                        String[] split = readLine.trim().split(" ");
                        if (split != null && split.length > 0) {
                            ret.addVertex(Integer.parseInt(split[0].trim()));
                            verticeCount = verticeCount + 1;
                        }
                    }
                }
                int edgeCount = 0;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
//                    System.out.println("Line: " + line);
                    if (line != null) {
                        String[] split = line.trim().split(" ");
//                        System.out.print("s= " + split[0]);
                        if (split != null && split.length > 1) {
                            Integer s = Integer.parseInt(split[0].trim());
//                            System.out.print("t= ");
                            for (int j = 1; j < split.length; j++) {
//                                System.out.print(split[j]);
//                                System.out.print(", ");
                                Integer t = Integer.parseInt(split[j].trim());
                                ret.addEdge(edgeCount++, s, t);
                            }
                        }
//                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "error", e);
        }
        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadGraphES(String readLine) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        ret = new UndirectedSparseGraphTO<>();
        int countEdge = 0;
        String[] edges = null;
        if (readLine != null && !readLine.isEmpty() && (edges = readLine.trim().split(",")) != null) {
            try {
                for (String stredge : edges) {
                    String[] vs = stredge.split("-");
                    if (vs != null && vs.length >= 2) {
                        Integer source = Integer.parseInt(vs[0].trim());
                        Integer target = Integer.parseInt(vs[1].trim());
                        ret.addEdge(countEdge++, source, target);
                    } else if (!stredge.isEmpty()) {
                        ret.addVertex(Integer.parseInt(stredge));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    public static UndirectedSparseGraphTO<Integer, Integer> loadGraphES(InputStream uploadedInputStream) throws IOException {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        if (uploadedInputStream != null) {
            ret = new UndirectedSparseGraphTO<>();
            BufferedReader r = new BufferedReader(new InputStreamReader(uploadedInputStream));
            String readLine = null;
            int countEdge = 0;
            while ((readLine = r.readLine()) != null) {
                String[] edges = null;
                if (readLine != null && !readLine.isEmpty() && (edges = readLine.trim().split(",")) != null) {
                    try {
                        for (String stredge : edges) {
                            String[] vs = stredge.split("-");
                            if (vs != null && vs.length >= 2) {
                                Integer source = Integer.parseInt(vs[0].trim());
                                Integer target = Integer.parseInt(vs[1].trim());
                                ret.addEdge(countEdge++, source, target);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return ret;
    }

    public static UndirectedSparseGraphTO loadGraph(File file) {
        UndirectedSparseGraphTO<Integer, Integer> ret = null;
        try {
            String fileName = file.getName();
            InputStream uploadedInputStream = new FileInputStream(file);
            if (fileName != null && !fileName.trim().isEmpty()) {
                String tmpFileName = fileName.trim().toLowerCase();
                if (tmpFileName.endsWith("csr")) {
                    ret = UtilGraph.loadGraphCsr(uploadedInputStream);
                } else if (tmpFileName.endsWith("mat")) {
                    ret = UtilGraph.loadGraphAdjMatrix(uploadedInputStream);
                } else if (tmpFileName.endsWith("es")) {
                    ret = UtilGraph.loadGraphES(uploadedInputStream);
                } else if (tmpFileName.endsWith("adj")) {
                    ret = UtilGraph.loadGraphAdjList(uploadedInputStream);
                }
                if (ret != null) {
                    ret.setName(fileName);
                }
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "fail on load graph", e);
        }
        return ret;
    }
}
