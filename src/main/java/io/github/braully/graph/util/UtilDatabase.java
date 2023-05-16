package io.github.braully.graph.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author strike
 */
public class UtilDatabase {

    static String resultFile = "/home/strike/Nuvem/Documentos/doutorado/artigo-p3-hull-heuristica/db-resultado-heuristica/resultado-big-todos.csv";

    public static int[] getResultCache(String key) {
        int[] result = null;

        try {
            BufferedReader bf = null;
            FileReader fileReader = new FileReader(resultFile);
//                resultadoArquivado.put("TSS-Cordasco-r1-BlogCatalog", new int[]{1, 79415});
            bf = new BufferedReader(fileReader);
            String readLine = null;
            while ((readLine = bf.readLine()) != null) {
                String[] split = readLine.split(",");
                String tkey = null;
                if (split.length >= 10) {
                    tkey = split[7] + "-" + split[4] + split[5] + "-" + split[1];
                }
                if (key.equals(tkey)) {
                    result = new int[]{
                        Integer.parseInt(split[8]),
                        Integer.parseInt(split[9])
                    };
//                    System.out.println("Find in cache");
                    break;
                }
            }

            bf.close();
            fileReader.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } finally {

        }
        return result;
    }
}
