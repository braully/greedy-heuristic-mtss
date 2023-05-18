package io.github.braully.graph.exec;

import io.github.braully.graph.UndirectedSparseGraphTO;
import io.github.braully.graph.operation.AbstractHeuristic;
import io.github.braully.graph.operation.CCMPanizi;
import io.github.braully.graph.operation.GreedyBonusDist;
import io.github.braully.graph.operation.GreedyCordasco;
import io.github.braully.graph.operation.GreedyDegree;
import io.github.braully.graph.operation.GreedyDeltaTss;
import io.github.braully.graph.operation.GreedyDeltaXDifTotal;
import io.github.braully.graph.operation.GreedyDifTotal;
import io.github.braully.graph.operation.GreedyDistAndDifDelta;
import io.github.braully.graph.operation.HNV0;
import io.github.braully.graph.operation.TSSCordasco;
import io.github.braully.graph.operation.HNV1;
import io.github.braully.graph.operation.HNV2;
import io.github.braully.graph.operation.HNVA;
import io.github.braully.graph.operation.IGraphOperation;
import static io.github.braully.graph.operation.IGraphOperation.DEFAULT_PARAM_NAME_SET;
import io.github.braully.graph.operation.TIPDecomp;
import io.github.braully.graph.util.UtilDatabase;
import io.github.braully.graph.util.UtilGraph;
import io.github.braully.graph.util.UtilProccess;
import java.io.BufferedWriter;
import java.io.File;
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

/**
 *
 * @author strike
 */
public class ExecBigDataSets {

    public static final Map<String, int[]> resultadoArquivado = new HashMap<>();
//
//    static {
//
//        resultadoArquivado.put("TSS-Cordasco-r1-BlogCatalog", new int[]{1, 79415});
//        resultadoArquivado.put("TSS-Cordasco-r1-BlogCatalog2", new int[]{1, 136434});
//        resultadoArquivado.put("TSS-Cordasco-r1-BlogCatalog3", new int[]{1, 137299});
//        resultadoArquivado.put("TSS-Cordasco-r1-BuzzNet", new int[]{1, 278575});
//        resultadoArquivado.put("TSS-Cordasco-r1-Delicious", new int[]{65, 296271});
//        resultadoArquivado.put("TSS-Cordasco-r1-Douban", new int[]{1, 689520});
//        resultadoArquivado.put("TSS-Cordasco-r1-Last.fm", new int[]{55, 890668});
//        resultadoArquivado.put("TSS-Cordasco-r1-Livemocha", new int[]{1, 1056541});
//        resultadoArquivado.put("TSS-Cordasco-r1-ca-AstroPh", new int[]{297, 1065121});
//        resultadoArquivado.put("TSS-Cordasco-r1-ca-CondMat", new int[]{567, 1075946});
//        resultadoArquivado.put("TSS-Cordasco-r1-ca-GrQc", new int[]{379, 1076494});
//        resultadoArquivado.put("TSS-Cordasco-r1-ca-HepPh", new int[]{289, 1080797});
//        resultadoArquivado.put("TSS-Cordasco-r1-ca-HepTh", new int[]{451, 1082918});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog", new int[]{73, 1140581});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog2", new int[]{2, 1223056});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog3", new int[]{2, 1223920});
//        resultadoArquivado.put("TSS-Cordasco-r2-BuzzNet", new int[]{2, 1375164});
//        resultadoArquivado.put("TSS-Cordasco-r2-Delicious", new int[]{650, 1392129});
//        resultadoArquivado.put("TSS-Cordasco-r2-Douban", new int[]{241, 1400198});
//        resultadoArquivado.put("TSS-Cordasco-r2-Last.fm", new int[]{330, 1467438});
//        resultadoArquivado.put("TSS-Cordasco-r2-Livemocha", new int[]{17, 1495413});
//        resultadoArquivado.put("TSS-Cordasco-r2-ca-AstroPh", new int[]{807, 1515578});
//        resultadoArquivado.put("TSS-Cordasco-r2-ca-CondMat", new int[]{1704, 1536091});
//        resultadoArquivado.put("TSS-Cordasco-r2-ca-GrQc", new int[]{810, 1536937});
//        resultadoArquivado.put("TSS-Cordasco-r2-ca-HepPh", new int[]{778, 1547128});
//        resultadoArquivado.put("TSS-Cordasco-r2-ca-HepTh", new int[]{1104, 1550500});
//        resultadoArquivado.put("TSS-Cordasco-r3-BlogCatalog", new int[]{168, 1626270});
//        resultadoArquivado.put("TSS-Cordasco-r3-BlogCatalog2", new int[]{3, 1776008});
//        resultadoArquivado.put("TSS-Cordasco-r3-BlogCatalog3", new int[]{4, 1776895});
//        resultadoArquivado.put("TSS-Cordasco-r3-BuzzNet", new int[]{10, 1821609});
//        resultadoArquivado.put("TSS-Cordasco-r3-Delicious", new int[]{1602, 1841147});
//        resultadoArquivado.put("TSS-Cordasco-r3-Douban", new int[]{640, 1997250});
//        resultadoArquivado.put("TSS-Cordasco-r3-Last.fm", new int[]{1065, 2154272});
//        resultadoArquivado.put("TSS-Cordasco-r3-Livemocha", new int[]{87, 2209958});
//        resultadoArquivado.put("TSS-Cordasco-r3-ca-AstroPh", new int[]{1370, 2234810});
//        resultadoArquivado.put("TSS-Cordasco-r3-ca-CondMat", new int[]{3078, 2262949});
//        resultadoArquivado.put("TSS-Cordasco-r3-ca-GrQc", new int[]{1246, 2263786});
//        resultadoArquivado.put("TSS-Cordasco-r3-ca-HepPh", new int[]{1348, 2274448});
//        resultadoArquivado.put("TSS-Cordasco-r3-ca-HepTh", new int[]{1797, 2278232});
//        resultadoArquivado.put("TSS-Cordasco-r4-BlogCatalog", new int[]{288, 2347136});
//        resultadoArquivado.put("TSS-Cordasco-r4-BlogCatalog2", new int[]{4, 2492761});
//        resultadoArquivado.put("TSS-Cordasco-r4-BlogCatalog3", new int[]{8, 2493791});
//        resultadoArquivado.put("TSS-Cordasco-r4-BuzzNet", new int[]{37, 2562040});
//        resultadoArquivado.put("TSS-Cordasco-r4-Delicious", new int[]{2638, 2583018});
//        resultadoArquivado.put("TSS-Cordasco-r4-Douban", new int[]{1072, 2743493});
//        resultadoArquivado.put("TSS-Cordasco-r4-Last.fm", new int[]{2197, 2971567});
//        resultadoArquivado.put("TSS-Cordasco-r4-Livemocha", new int[]{238, 3034940});
//        resultadoArquivado.put("TSS-Cordasco-r4-ca-AstroPh", new int[]{1904, 3059475});
//        resultadoArquivado.put("TSS-Cordasco-r4-ca-CondMat", new int[]{4474, 3090629});
//        resultadoArquivado.put("TSS-Cordasco-r4-ca-GrQc", new int[]{1556, 3091292});
//        resultadoArquivado.put("TSS-Cordasco-r4-ca-HepPh", new int[]{1854, 3101156});
//        resultadoArquivado.put("TSS-Cordasco-r4-ca-HepTh", new int[]{2364, 3104278});
//        resultadoArquivado.put("TSS-Cordasco-r5-BlogCatalog", new int[]{439, 3159903});
//        resultadoArquivado.put("TSS-Cordasco-r5-BlogCatalog2", new int[]{5, 3288737});
//        resultadoArquivado.put("TSS-Cordasco-r5-BlogCatalog3", new int[]{15, 3289951});
//        resultadoArquivado.put("TSS-Cordasco-r5-BuzzNet", new int[]{78, 3364048});
//        resultadoArquivado.put("TSS-Cordasco-r5-Delicious", new int[]{3506, 3394200});
//        resultadoArquivado.put("TSS-Cordasco-r5-Douban", new int[]{1463, 3576176});
//        resultadoArquivado.put("TSS-Cordasco-r5-Last.fm", new int[]{3658, 3857304});
//        resultadoArquivado.put("TSS-Cordasco-r5-Livemocha", new int[]{482, 3953941});
//        resultadoArquivado.put("TSS-Cordasco-r5-ca-AstroPh", new int[]{2407, 3969314});
//        resultadoArquivado.put("TSS-Cordasco-r5-ca-CondMat", new int[]{5672, 3999053});
//        resultadoArquivado.put("TSS-Cordasco-r5-ca-GrQc", new int[]{1809, 3999662});
//        resultadoArquivado.put("TSS-Cordasco-r5-ca-HepPh", new int[]{2344, 4009832});
//        resultadoArquivado.put("TSS-Cordasco-r5-ca-HepTh", new int[]{2850, 4012774});
//        resultadoArquivado.put("TSS-Cordasco-r6-BlogCatalog", new int[]{603, 4077132});
//        resultadoArquivado.put("TSS-Cordasco-r6-BlogCatalog2", new int[]{10, 4206655});
//        resultadoArquivado.put("TSS-Cordasco-r6-BlogCatalog3", new int[]{23, 4207764});
//        resultadoArquivado.put("TSS-Cordasco-r6-BuzzNet", new int[]{198, 4296343});
//        resultadoArquivado.put("TSS-Cordasco-r6-Delicious", new int[]{4263, 4333239});
//        resultadoArquivado.put("TSS-Cordasco-r6-Douban", new int[]{1941, 4536667});
//        resultadoArquivado.put("TSS-Cordasco-r6-Last.fm", new int[]{5405, 4891118});
//        resultadoArquivado.put("TSS-Cordasco-r6-Livemocha", new int[]{829, 4998858});
//        resultadoArquivado.put("TSS-Cordasco-r6-ca-AstroPh", new int[]{2887, 5016678});
//        resultadoArquivado.put("TSS-Cordasco-r6-ca-CondMat", new int[]{6737, 5045188});
//        resultadoArquivado.put("TSS-Cordasco-r6-ca-GrQc", new int[]{1985, 5045741});
//        resultadoArquivado.put("TSS-Cordasco-r6-ca-HepPh", new int[]{2764, 5055840});
//        resultadoArquivado.put("TSS-Cordasco-r6-ca-HepTh", new int[]{3227, 5057884});
//        resultadoArquivado.put("TSS-Cordasco-r7-BlogCatalog", new int[]{810, 5127170});
//        resultadoArquivado.put("TSS-Cordasco-r7-BlogCatalog2", new int[]{14, 5277279});
//        resultadoArquivado.put("TSS-Cordasco-r7-BlogCatalog3", new int[]{30, 5278494});
//        resultadoArquivado.put("TSS-Cordasco-r7-BuzzNet", new int[]{411, 5407754});
//        resultadoArquivado.put("TSS-Cordasco-r7-Delicious", new int[]{4935, 5446874});
//        resultadoArquivado.put("TSS-Cordasco-r7-Douban", new int[]{2365, 5764842});
//        resultadoArquivado.put("TSS-Cordasco-r7-Last.fm", new int[]{7280, 6157012});
//        resultadoArquivado.put("TSS-Cordasco-r7-Livemocha", new int[]{1272, 6290435});
//        resultadoArquivado.put("TSS-Cordasco-r7-ca-AstroPh", new int[]{3311, 6314778});
//        resultadoArquivado.put("TSS-Cordasco-r7-ca-CondMat", new int[]{7660, 6342906});
//        resultadoArquivado.put("TSS-Cordasco-r7-ca-GrQc", new int[]{2121, 6343402});
//        resultadoArquivado.put("TSS-Cordasco-r7-ca-HepPh", new int[]{3104, 6354860});
//        resultadoArquivado.put("TSS-Cordasco-r7-ca-HepTh", new int[]{3530, 6357435});
//        resultadoArquivado.put("TSS-Cordasco-r8-BlogCatalog", new int[]{999, 6420862});
//        resultadoArquivado.put("TSS-Cordasco-r8-BlogCatalog2", new int[]{20, 6557269});
//        resultadoArquivado.put("TSS-Cordasco-r8-BlogCatalog3", new int[]{45, 6558925});
//        resultadoArquivado.put("TSS-Cordasco-r8-BuzzNet", new int[]{678, 6722429});
//        resultadoArquivado.put("TSS-Cordasco-r8-Delicious", new int[]{5523, 6759022});
//        resultadoArquivado.put("TSS-Cordasco-r8-Douban", new int[]{2834, 7081632});
//        resultadoArquivado.put("TSS-Cordasco-r8-Last.fm", new int[]{9189, 7508652});
//        resultadoArquivado.put("TSS-Cordasco-r8-Livemocha", new int[]{1793, 7680890});
//        resultadoArquivado.put("TSS-Cordasco-r8-ca-AstroPh", new int[]{3716, 7704914});
//        resultadoArquivado.put("TSS-Cordasco-r8-ca-CondMat", new int[]{8430, 7729986});
//        resultadoArquivado.put("TSS-Cordasco-r8-ca-GrQc", new int[]{2229, 7730478});
//        resultadoArquivado.put("TSS-Cordasco-r8-ca-HepPh", new int[]{3437, 7741305});
//        resultadoArquivado.put("TSS-Cordasco-r8-ca-HepTh", new int[]{3778, 7743636});
//        resultadoArquivado.put("TSS-Cordasco-r9-BlogCatalog", new int[]{1197, 7803788});
//        resultadoArquivado.put("TSS-Cordasco-r9-BlogCatalog2", new int[]{28, 7935102});
//        resultadoArquivado.put("TSS-Cordasco-r9-BlogCatalog3", new int[]{68, 7936635});
//        resultadoArquivado.put("TSS-Cordasco-r9-BuzzNet", new int[]{956, 8119012});
//        resultadoArquivado.put("TSS-Cordasco-r9-Delicious", new int[]{6011, 8159957});
//        resultadoArquivado.put("TSS-Cordasco-r9-Douban", new int[]{3230, 8495243});
//        resultadoArquivado.put("TSS-Cordasco-r9-Last.fm", new int[]{11239, 8951931});
//        resultadoArquivado.put("TSS-Cordasco-r9-Livemocha", new int[]{2425, 9145074});
//        resultadoArquivado.put("TSS-Cordasco-r9-ca-AstroPh", new int[]{4112, 9169811});
//        resultadoArquivado.put("TSS-Cordasco-r9-ca-CondMat", new int[]{9067, 9191774});
//        resultadoArquivado.put("TSS-Cordasco-r9-ca-GrQc", new int[]{2312, 9192188});
//        resultadoArquivado.put("TSS-Cordasco-r9-ca-HepPh", new int[]{3675, 9200811});
//        resultadoArquivado.put("TSS-Cordasco-r9-ca-HepTh", new int[]{3970, 9202949});
//        resultadoArquivado.put("TSS-Cordasco-r10-BlogCatalog", new int[]{1421, 9263480});
//        resultadoArquivado.put("TSS-Cordasco-r10-BlogCatalog2", new int[]{37, 9393311});
//        resultadoArquivado.put("TSS-Cordasco-r10-BlogCatalog3", new int[]{82, 9394579});
//        resultadoArquivado.put("TSS-Cordasco-r10-BuzzNet", new int[]{1199, 9543444});
//        resultadoArquivado.put("TSS-Cordasco-r10-Delicious", new int[]{6432, 9583728});
//        resultadoArquivado.put("TSS-Cordasco-r10-Douban", new int[]{3591, 9951753});
//        resultadoArquivado.put("TSS-Cordasco-r10-Last.fm", new int[]{13261, 10414111});
//        resultadoArquivado.put("TSS-Cordasco-r10-Livemocha", new int[]{3097, 10596719});
//        resultadoArquivado.put("TSS-Cordasco-r10-ca-AstroPh", new int[]{4476, 10621879});
//        resultadoArquivado.put("TSS-Cordasco-r10-ca-CondMat", new int[]{9598, 10642258});
//        resultadoArquivado.put("TSS-Cordasco-r10-ca-GrQc", new int[]{2376, 10642652});
//        resultadoArquivado.put("TSS-Cordasco-r10-ca-HepPh", new int[]{3895, 10651092});
//        resultadoArquivado.put("TSS-Cordasco-r10-ca-HepTh", new int[]{4130, 10653049});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog", new int[]{73, 43229});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog", new int[]{73, 43567});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog2", new int[]{2, 200004});
//        resultadoArquivado.put("TSS-Cordasco-r2-BlogCatalog3", new int[]{2, 201241});
//        resultadoArquivado.put("TSS-Cordasco-r2-BuzzNet", new int[]{2, 347545});
//        resultadoArquivado.put("TSS-Cordasco-r2-Delicious", new int[]{650, 379078});
//        resultadoArquivado.put("TSS-Cordasco-r2-Douban", new int[]{241, 412210});
//        resultadoArquivado.put("TSS-Cordasco-r2-Last.fm", new int[]{330, 502092});
//        resultadoArquivado.put("TSS-Cordasco-k1-BlogCatalog2", new int[]{1, 121942});
//        resultadoArquivado.put("TSS-Cordasco-k1-BuzzNet", new int[]{6, 279634});
//        resultadoArquivado.put("TSS-Cordasco-k1-Livemocha", new int[]{336, 458879});
//        resultadoArquivado.put("TSS-Cordasco-k2-BlogCatalog2", new int[]{27638, 482217});
//        resultadoArquivado.put("TSS-Cordasco-k2-BuzzNet", new int[]{7561, 485019});
//        resultadoArquivado.put("TSS-Cordasco-k2-Livemocha", new int[]{7182, 487730});
//        resultadoArquivado.put("TSS-Cordasco-k3-BlogCatalog2", new int[]{40662, 545022});
//        resultadoArquivado.put("TSS-Cordasco-k3-BuzzNet", new int[]{19630, 550552});
//        resultadoArquivado.put("TSS-Cordasco-k3-Livemocha", new int[]{13603, 554108});
//        resultadoArquivado.put("TSS-Cordasco-k4-BlogCatalog2", new int[]{48604, 631997});
//        resultadoArquivado.put("TSS-Cordasco-k4-BuzzNet", new int[]{23647, 636905});
//        resultadoArquivado.put("TSS-Cordasco-k4-Livemocha", new int[]{19345, 640528});
//        resultadoArquivado.put("TSS-Cordasco-k5-BlogCatalog2", new int[]{54171, 736839});
//        resultadoArquivado.put("TSS-Cordasco-k5-BuzzNet", new int[]{27242, 742056});
//        resultadoArquivado.put("TSS-Cordasco-k5-Livemocha", new int[]{24466, 761846});
//        resultadoArquivado.put("TSS-Cordasco-k6-BlogCatalog2", new int[]{58325, 797824});
//        resultadoArquivado.put("TSS-Cordasco-k6-BuzzNet", new int[]{30850, 805473});
//        resultadoArquivado.put("TSS-Cordasco-k6-Livemocha", new int[]{29079, 833801});
//        resultadoArquivado.put("TSS-Cordasco-k7-BlogCatalog2", new int[]{61589, 871786});
//        resultadoArquivado.put("TSS-Cordasco-k7-BuzzNet", new int[]{34503, 886278});
//        resultadoArquivado.put("TSS-Cordasco-k7-Livemocha", new int[]{33179, 924904});
//        resultadoArquivado.put("TSS-Cordasco-k8-BlogCatalog2", new int[]{64126, 1062920});
//        resultadoArquivado.put("TSS-Cordasco-k8-BuzzNet", new int[]{37815, 1073218});
//        resultadoArquivado.put("TSS-Cordasco-k8-Livemocha", new int[]{36928, 1102529});
//        resultadoArquivado.put("TSS-Cordasco-k9-BlogCatalog2", new int[]{66462, 1142551});
//        resultadoArquivado.put("TSS-Cordasco-k9-BuzzNet", new int[]{40725, 1155595});
//        resultadoArquivado.put("TSS-Cordasco-k9-Livemocha", new int[]{40275, 1221780});
//        resultadoArquivado.put("TSS-Cordasco-k10-BlogCatalog2", new int[]{68321, 1383763});
//        resultadoArquivado.put("TSS-Cordasco-k10-BuzzNet", new int[]{43122, 1412166});
//        resultadoArquivado.put("TSS-Cordasco-k10-Livemocha", new int[]{43439, 1473506});
//
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog", new int[]{109, 77442});
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog2", new int[]{92, 186160});
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog3", new int[]{50, 186740});
//        resultadoArquivado.put("TIPDecomp-m1-BuzzNet", new int[]{113, 292656});
//        resultadoArquivado.put("TIPDecomp-m1-Delicious", new int[]{81, 370983});
//        resultadoArquivado.put("TIPDecomp-m1-Douban", new int[]{40, 593865});
//        resultadoArquivado.put("TIPDecomp-m1-Last.fm", new int[]{90, 738812});
//        resultadoArquivado.put("TIPDecomp-m1-Livemocha", new int[]{101, 870911});
//        resultadoArquivado.put("TIPDecomp-m1-ca-AstroPh", new int[]{357, 872314});
//        resultadoArquivado.put("TIPDecomp-m1-ca-CondMat", new int[]{652, 874321});
//        resultadoArquivado.put("TIPDecomp-m1-ca-GrQc", new int[]{402, 874426});
//        resultadoArquivado.put("TIPDecomp-m1-ca-HepPh", new int[]{359, 875022});
//        resultadoArquivado.put("TIPDecomp-m1-ca-HepTh", new int[]{470, 875388});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog", new int[]{346, 948500});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog2", new int[]{309, 1059292});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog3", new int[]{140, 1059880});
//        resultadoArquivado.put("TIPDecomp-m2-BuzzNet", new int[]{386, 1173126});
//        resultadoArquivado.put("TIPDecomp-m2-Delicious", new int[]{159, 1244988});
//        resultadoArquivado.put("TIPDecomp-m2-Douban", new int[]{365, 1451760});
//        resultadoArquivado.put("TIPDecomp-m2-Last.fm", new int[]{220, 1595970});
//        resultadoArquivado.put("TIPDecomp-m2-Livemocha", new int[]{570, 1729698});
//        resultadoArquivado.put("TIPDecomp-m2-ca-AstroPh", new int[]{540, 1731204});
//        resultadoArquivado.put("TIPDecomp-m2-ca-CondMat", new int[]{910, 1733335});
//        resultadoArquivado.put("TIPDecomp-m2-ca-GrQc", new int[]{493, 1733436});
//        resultadoArquivado.put("TIPDecomp-m2-ca-HepPh", new int[]{528, 1734030});
//        resultadoArquivado.put("TIPDecomp-m2-ca-HepTh", new int[]{558, 1734390});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog", new int[]{939, 1819650});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog2", new int[]{772, 1926359});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog3", new int[]{241, 1926917});
//        resultadoArquivado.put("TIPDecomp-m3-BuzzNet", new int[]{1149, 2026385});
//        resultadoArquivado.put("TIPDecomp-m3-Delicious", new int[]{520, 2108526});
//        resultadoArquivado.put("TIPDecomp-m3-Douban", new int[]{1628, 2350759});
//        resultadoArquivado.put("TIPDecomp-m3-Last.fm", new int[]{646, 2488636});
//        resultadoArquivado.put("TIPDecomp-m3-Livemocha", new int[]{2012, 2622194});
//        resultadoArquivado.put("TIPDecomp-m3-ca-AstroPh", new int[]{899, 2623621});
//        resultadoArquivado.put("TIPDecomp-m3-ca-CondMat", new int[]{1558, 2625781});
//        resultadoArquivado.put("TIPDecomp-m3-ca-GrQc", new int[]{664, 2625882});
//        resultadoArquivado.put("TIPDecomp-m3-ca-HepPh", new int[]{820, 2626468});
//        resultadoArquivado.put("TIPDecomp-m3-ca-HepTh", new int[]{775, 2626828});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog", new int[]{2160, 2713101});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog2", new int[]{1856, 2818140});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog3", new int[]{496, 2818680});
//        resultadoArquivado.put("TIPDecomp-m4-BuzzNet", new int[]{2958, 2923577});
//        resultadoArquivado.put("TIPDecomp-m4-Delicious", new int[]{1625, 3006290});
//        resultadoArquivado.put("TIPDecomp-m4-Douban", new int[]{5753, 3219869});
//        resultadoArquivado.put("TIPDecomp-m4-Last.fm", new int[]{2201, 3352698});
//        resultadoArquivado.put("TIPDecomp-m4-Livemocha", new int[]{5219, 3482205});
//        resultadoArquivado.put("TIPDecomp-m4-ca-AstroPh", new int[]{1594, 3483699});
//        resultadoArquivado.put("TIPDecomp-m4-ca-CondMat", new int[]{2801, 3485822});
//        resultadoArquivado.put("TIPDecomp-m4-ca-GrQc", new int[]{962, 3485924});
//        resultadoArquivado.put("TIPDecomp-m4-ca-HepPh", new int[]{1302, 3486517});
//        resultadoArquivado.put("TIPDecomp-m4-ca-HepTh", new int[]{1311, 3486930});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog", new int[]{4598, 3553552});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog2", new int[]{4391, 3657915});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog3", new int[]{834, 3658442});
//        resultadoArquivado.put("TIPDecomp-m5-BuzzNet", new int[]{7080, 3756915});
//        resultadoArquivado.put("TIPDecomp-m5-Delicious", new int[]{4572, 3846674});
//        resultadoArquivado.put("TIPDecomp-m5-Douban", new int[]{32996, 4033259});
//        resultadoArquivado.put("TIPDecomp-m5-Last.fm", new int[]{6534, 4174739});
//        resultadoArquivado.put("TIPDecomp-m5-Livemocha", new int[]{11279, 4307600});
//        resultadoArquivado.put("TIPDecomp-m5-ca-AstroPh", new int[]{2569, 4308955});
//        resultadoArquivado.put("TIPDecomp-m5-ca-CondMat", new int[]{4252, 4311142});
//        resultadoArquivado.put("TIPDecomp-m5-ca-GrQc", new int[]{1173, 4311243});
//        resultadoArquivado.put("TIPDecomp-m5-ca-HepPh", new int[]{1911, 4311885});
//        resultadoArquivado.put("TIPDecomp-m5-ca-HepTh", new int[]{1796, 4312241});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog", new int[]{9599, 4385018});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog2", new int[]{10700, 4487277});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog3", new int[]{1362, 4487806});
//        resultadoArquivado.put("TIPDecomp-m6-BuzzNet", new int[]{15686, 4591171});
//        resultadoArquivado.put("TIPDecomp-m6-Delicious", new int[]{16083, 4661582});
//        resultadoArquivado.put("TIPDecomp-m6-Douban", new int[]{82937, 4811748});
//        resultadoArquivado.put("TIPDecomp-m6-Last.fm", new int[]{16460, 4952368});
//        resultadoArquivado.put("TIPDecomp-m6-Livemocha", new int[]{21147, 5080902});
//        resultadoArquivado.put("TIPDecomp-m6-ca-AstroPh", new int[]{4829, 5082462});
//        resultadoArquivado.put("TIPDecomp-m6-ca-CondMat", new int[]{7479, 5084604});
//        resultadoArquivado.put("TIPDecomp-m6-ca-GrQc", new int[]{1995, 5084699});
//        resultadoArquivado.put("TIPDecomp-m6-ca-HepPh", new int[]{3387, 5085342});
//        resultadoArquivado.put("TIPDecomp-m6-ca-HepTh", new int[]{3271, 5085682});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog", new int[]{20294, 5156373});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog2", new int[]{22917, 5260713});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog3", new int[]{1859, 5261211});
//        resultadoArquivado.put("TIPDecomp-m7-BuzzNet", new int[]{27970, 5354880});
//        resultadoArquivado.put("TIPDecomp-m7-Delicious", new int[]{26792, 5436976});
//        resultadoArquivado.put("TIPDecomp-m7-Douban", new int[]{100629, 5592194});
//        resultadoArquivado.put("TIPDecomp-m7-Last.fm", new int[]{31491, 5713470});
//        resultadoArquivado.put("TIPDecomp-m7-Livemocha", new int[]{34226, 5837026});
//        resultadoArquivado.put("TIPDecomp-m7-ca-AstroPh", new int[]{7587, 5838306});
//        resultadoArquivado.put("TIPDecomp-m7-ca-CondMat", new int[]{10981, 5840202});
//        resultadoArquivado.put("TIPDecomp-m7-ca-GrQc", new int[]{2629, 5840283});
//        resultadoArquivado.put("TIPDecomp-m7-ca-HepPh", new int[]{5283, 5840845});
//        resultadoArquivado.put("TIPDecomp-m7-ca-HepTh", new int[]{4689, 5841154});
//        resultadoArquivado.put("TIPDecomp-m8-BlogCatalog", new int[]{39774, 5902243});
//        resultadoArquivado.put("TIPDecomp-m8-BlogCatalog2", new int[]{44910, 5997914});
//        resultadoArquivado.put("TIPDecomp-m8-BlogCatalog3", new int[]{3130, 5998414});
//        resultadoArquivado.put("TIPDecomp-m8-BuzzNet", new int[]{52105, 6094937});
//        resultadoArquivado.put("TIPDecomp-m8-Delicious", new int[]{30925, 6183078});
//        resultadoArquivado.put("TIPDecomp-m8-Douban", new int[]{102753, 6309705});
//        resultadoArquivado.put("TIPDecomp-m8-Last.fm", new int[]{50489, 6410842});
//        resultadoArquivado.put("TIPDecomp-m8-Livemocha", new int[]{52538, 6509464});
//        resultadoArquivado.put("TIPDecomp-m8-ca-AstroPh", new int[]{10438, 6510525});
//        resultadoArquivado.put("TIPDecomp-m8-ca-CondMat", new int[]{13309, 6511893});
//        resultadoArquivado.put("TIPDecomp-m8-ca-GrQc", new int[]{2893, 6511969});
//        resultadoArquivado.put("TIPDecomp-m8-ca-HepPh", new int[]{6565, 6512480});
//        resultadoArquivado.put("TIPDecomp-m8-ca-HepTh", new int[]{5321, 6512751});
//        resultadoArquivado.put("TIPDecomp-m9-BlogCatalog", new int[]{64735, 6541090});
//        resultadoArquivado.put("TIPDecomp-m9-BlogCatalog2", new int[]{76367, 6587661});
//        resultadoArquivado.put("TIPDecomp-m9-BlogCatalog3", new int[]{5480, 6588091});
//        resultadoArquivado.put("TIPDecomp-m9-BuzzNet", new int[]{71646, 6657004});
//        resultadoArquivado.put("TIPDecomp-m9-Delicious", new int[]{30652, 6756657});
//        resultadoArquivado.put("TIPDecomp-m9-Douban", new int[]{97598, 6855736});
//        resultadoArquivado.put("TIPDecomp-m9-Last.fm", new int[]{62683, 6940067});
//        resultadoArquivado.put("TIPDecomp-m9-Livemocha", new int[]{89216, 6977704});
//        resultadoArquivado.put("TIPDecomp-m9-ca-AstroPh", new int[]{12837, 6978519});
//        resultadoArquivado.put("TIPDecomp-m9-ca-CondMat", new int[]{15128, 6979933});
//        resultadoArquivado.put("TIPDecomp-m9-ca-GrQc", new int[]{3100, 6980005});
//        resultadoArquivado.put("TIPDecomp-m9-ca-HepPh", new int[]{7636, 6980442});
//        resultadoArquivado.put("TIPDecomp-m9-ca-HepTh", new int[]{5711, 6980704});
//        resultadoArquivado.put("TIPDecomp-k1-BlogCatalog", new int[]{1, 7056276});
//        resultadoArquivado.put("TIPDecomp-k1-BlogCatalog2", new int[]{1, 7171800});
//        resultadoArquivado.put("TIPDecomp-k1-BlogCatalog3", new int[]{1, 7172382});
//        resultadoArquivado.put("TIPDecomp-k1-BuzzNet", new int[]{6, 7277449});
//        resultadoArquivado.put("TIPDecomp-k1-Delicious", new int[]{58877, 7318637});
//        resultadoArquivado.put("TIPDecomp-k1-Douban", new int[]{1, 7489851});
//        resultadoArquivado.put("TIPDecomp-k1-Last.fm", new int[]{4596, 7579890});
//        resultadoArquivado.put("TIPDecomp-k1-Livemocha", new int[]{336, 7663863});
//        resultadoArquivado.put("TIPDecomp-k1-ca-AstroPh", new int[]{298, 7664995});
//        resultadoArquivado.put("TIPDecomp-k1-ca-CondMat", new int[]{594, 7666578});
//        resultadoArquivado.put("TIPDecomp-k1-ca-GrQc", new int[]{380, 7666661});
//        resultadoArquivado.put("TIPDecomp-k1-ca-HepPh", new int[]{291, 7667122});
//        resultadoArquivado.put("TIPDecomp-k1-ca-HepTh", new int[]{453, 7667406});
//        resultadoArquivado.put("TIPDecomp-k2-BlogCatalog", new int[]{20291, 7729651});
//        resultadoArquivado.put("TIPDecomp-k2-BlogCatalog2", new int[]{27638, 7809703});
//        resultadoArquivado.put("TIPDecomp-k2-BlogCatalog3", new int[]{270, 7810205});
//        resultadoArquivado.put("TIPDecomp-k2-BuzzNet", new int[]{7561, 7890488});
//        resultadoArquivado.put("TIPDecomp-k2-Delicious", new int[]{76688, 7919749});
//        resultadoArquivado.put("TIPDecomp-k2-Douban", new int[]{103158, 7995492});
//        resultadoArquivado.put("TIPDecomp-k2-Last.fm", new int[]{13463, 8087345});
//        resultadoArquivado.put("TIPDecomp-k2-Livemocha", new int[]{7182, 8179470});
//        resultadoArquivado.put("TIPDecomp-k2-ca-AstroPh", new int[]{1868, 8180735});
//        resultadoArquivado.put("TIPDecomp-k2-ca-CondMat", new int[]{3608, 8182646});
//        resultadoArquivado.put("TIPDecomp-k2-ca-GrQc", new int[]{1690, 8182747});
//        resultadoArquivado.put("TIPDecomp-k2-ca-HepPh", new int[]{2019, 8183293});
//        resultadoArquivado.put("TIPDecomp-k2-ca-HepTh", new int[]{2742, 8183652});
//        resultadoArquivado.put("TIPDecomp-k3-BlogCatalog", new int[]{30770, 8236529});
//        resultadoArquivado.put("TIPDecomp-k3-BlogCatalog2", new int[]{40662, 8302817});
//        resultadoArquivado.put("TIPDecomp-k3-BlogCatalog3", new int[]{643, 8303300});
//        resultadoArquivado.put("TIPDecomp-k3-BuzzNet", new int[]{19630, 8379573});
//        resultadoArquivado.put("TIPDecomp-k3-Delicious", new int[]{84487, 8399947});
//        resultadoArquivado.put("TIPDecomp-k3-Douban", new int[]{125199, 8461287});
//        resultadoArquivado.put("TIPDecomp-k3-Last.fm", new int[]{21521, 8557715});
//        resultadoArquivado.put("TIPDecomp-k3-Livemocha", new int[]{13604, 8660109});
//        resultadoArquivado.put("TIPDecomp-k3-ca-AstroPh", new int[]{3631, 8661461});
//        resultadoArquivado.put("TIPDecomp-k3-ca-CondMat", new int[]{7026, 8663479});
//        resultadoArquivado.put("TIPDecomp-k3-ca-GrQc", new int[]{2702, 8663566});
//        resultadoArquivado.put("TIPDecomp-k3-ca-HepPh", new int[]{3815, 8664135});
//        resultadoArquivado.put("TIPDecomp-k3-ca-HepTh", new int[]{4632, 8664465});
//        resultadoArquivado.put("TIPDecomp-k4-BlogCatalog", new int[]{37617, 8715426});
//        resultadoArquivado.put("TIPDecomp-k4-BlogCatalog2", new int[]{48604, 8773468});
//        resultadoArquivado.put("TIPDecomp-k4-BlogCatalog3", new int[]{995, 8773966});
//        resultadoArquivado.put("TIPDecomp-k4-BuzzNet", new int[]{23647, 8847071});
//        resultadoArquivado.put("TIPDecomp-k4-Delicious", new int[]{88710, 8862464});
//        resultadoArquivado.put("TIPDecomp-k4-Douban", new int[]{133846, 8906442});
//        resultadoArquivado.put("TIPDecomp-k4-Last.fm", new int[]{28665, 9008568});
//        resultadoArquivado.put("TIPDecomp-k4-Livemocha", new int[]{19345, 9111839});
//        resultadoArquivado.put("TIPDecomp-k4-ca-AstroPh", new int[]{5147, 9113200});
//        resultadoArquivado.put("TIPDecomp-k4-ca-CondMat", new int[]{9857, 9115285});
//        resultadoArquivado.put("TIPDecomp-k4-ca-GrQc", new int[]{3348, 9115352});
//        resultadoArquivado.put("TIPDecomp-k4-ca-HepPh", new int[]{5158, 9115942});
//        resultadoArquivado.put("TIPDecomp-k4-ca-HepTh", new int[]{5853, 9116202});
//        resultadoArquivado.put("TIPDecomp-k5-BlogCatalog", new int[]{42441, 9157932});
//        resultadoArquivado.put("TIPDecomp-k5-BlogCatalog2", new int[]{54171, 9207646});
//        resultadoArquivado.put("TIPDecomp-k5-BlogCatalog3", new int[]{1369, 9208710});
//        resultadoArquivado.put("TIPDecomp-k5-BuzzNet", new int[]{27242, 9282277});
//        resultadoArquivado.put("TIPDecomp-k5-Delicious", new int[]{91433, 9295971});
//        resultadoArquivado.put("TIPDecomp-k5-Douban", new int[]{138187, 9331198});
//        resultadoArquivado.put("TIPDecomp-k5-Last.fm", new int[]{35045, 9428551});
//        resultadoArquivado.put("TIPDecomp-k5-Livemocha", new int[]{24466, 9531878});
//        resultadoArquivado.put("TIPDecomp-k5-ca-AstroPh", new int[]{6325, 9533271});
//        resultadoArquivado.put("TIPDecomp-k5-ca-CondMat", new int[]{12043, 9535002});
//        resultadoArquivado.put("TIPDecomp-k5-ca-GrQc", new int[]{3756, 9535055});
//        resultadoArquivado.put("TIPDecomp-k5-ca-HepPh", new int[]{6048, 9535559});
//        resultadoArquivado.put("TIPDecomp-k5-ca-HepTh", new int[]{6655, 9535762});
//        resultadoArquivado.put("TIPDecomp-k6-BlogCatalog", new int[]{46054, 9576793});
//        resultadoArquivado.put("TIPDecomp-k6-BlogCatalog2", new int[]{58325, 9630926});
//        resultadoArquivado.put("TIPDecomp-k6-BlogCatalog3", new int[]{1736, 9631495});
//        resultadoArquivado.put("TIPDecomp-k6-BuzzNet", new int[]{30851, 9723807});
//        resultadoArquivado.put("TIPDecomp-k6-Delicious", new int[]{93383, 9735824});
//        resultadoArquivado.put("TIPDecomp-k6-Douban", new int[]{140697, 9758757});
//        resultadoArquivado.put("TIPDecomp-k6-Last.fm", new int[]{40739, 9853912});
//        resultadoArquivado.put("TIPDecomp-k6-Livemocha", new int[]{29079, 9957911});
//        resultadoArquivado.put("TIPDecomp-k6-ca-AstroPh", new int[]{7275, 9959398});
//        resultadoArquivado.put("TIPDecomp-k6-ca-CondMat", new int[]{13778, 9960989});
//        resultadoArquivado.put("TIPDecomp-k6-ca-GrQc", new int[]{4029, 9961034});
//        resultadoArquivado.put("TIPDecomp-k6-ca-HepPh", new int[]{6685, 9961551});
//        resultadoArquivado.put("TIPDecomp-k6-ca-HepTh", new int[]{7230, 9961720});
//        resultadoArquivado.put("TIPDecomp-k7-BlogCatalog", new int[]{48925, 10001179});
//        resultadoArquivado.put("TIPDecomp-k7-BlogCatalog2", new int[]{61589, 10045745});
//        resultadoArquivado.put("TIPDecomp-k7-BlogCatalog3", new int[]{2090, 10046595});
//        resultadoArquivado.put("TIPDecomp-k7-BuzzNet", new int[]{34506, 10120449});
//        resultadoArquivado.put("TIPDecomp-k7-Delicious", new int[]{94701, 10129719});
//        resultadoArquivado.put("TIPDecomp-k7-Douban", new int[]{142373, 10156712});
//        resultadoArquivado.put("TIPDecomp-k7-Last.fm", new int[]{45849, 10242229});
//        resultadoArquivado.put("TIPDecomp-k7-Livemocha", new int[]{33180, 10344787});
//        resultadoArquivado.put("TIPDecomp-k7-ca-AstroPh", new int[]{8043, 10346239});
//        resultadoArquivado.put("TIPDecomp-k7-ca-CondMat", new int[]{15105, 10347589});
//        resultadoArquivado.put("TIPDecomp-k7-ca-GrQc", new int[]{4238, 10347625});
//        resultadoArquivado.put("TIPDecomp-k7-ca-HepPh", new int[]{7213, 10348096});
//        resultadoArquivado.put("TIPDecomp-k7-ca-HepTh", new int[]{7650, 10348239});
//        resultadoArquivado.put("TIPDecomp-k8-BlogCatalog", new int[]{51333, 10385084});
//        resultadoArquivado.put("TIPDecomp-k8-BlogCatalog2", new int[]{64126, 10424143});
//        resultadoArquivado.put("TIPDecomp-k8-BlogCatalog3", new int[]{2376, 10424746});
//        resultadoArquivado.put("TIPDecomp-k8-BuzzNet", new int[]{37820, 10497971});
//        resultadoArquivado.put("TIPDecomp-k8-Delicious", new int[]{95764, 10505502});
//        resultadoArquivado.put("TIPDecomp-k8-Douban", new int[]{143514, 10530392});
//        resultadoArquivado.put("TIPDecomp-k8-Last.fm", new int[]{50287, 10614844});
//        resultadoArquivado.put("TIPDecomp-k8-Livemocha", new int[]{36931, 10712981});
//        resultadoArquivado.put("TIPDecomp-k8-ca-AstroPh", new int[]{8724, 10714253});
//        resultadoArquivado.put("TIPDecomp-k8-ca-CondMat", new int[]{16216, 10715321});
//        resultadoArquivado.put("TIPDecomp-k8-ca-GrQc", new int[]{4381, 10715353});
//        resultadoArquivado.put("TIPDecomp-k8-ca-HepPh", new int[]{7610, 10715764});
//        resultadoArquivado.put("TIPDecomp-k8-ca-HepTh", new int[]{8010, 10715884});
//        resultadoArquivado.put("TIPDecomp-k9-BlogCatalog", new int[]{53354, 10759859});
//        resultadoArquivado.put("TIPDecomp-k9-BlogCatalog2", new int[]{66462, 10774466});
//        resultadoArquivado.put("TIPDecomp-k9-BlogCatalog3", new int[]{2701, 10775051});
//        resultadoArquivado.put("TIPDecomp-k9-BuzzNet", new int[]{40726, 10850077});
//        resultadoArquivado.put("TIPDecomp-k9-Delicious", new int[]{96617, 10857260});
//        resultadoArquivado.put("TIPDecomp-k9-Douban", new int[]{144334, 10880399});
//        resultadoArquivado.put("TIPDecomp-k9-Last.fm", new int[]{54318, 10966183});
//        resultadoArquivado.put("TIPDecomp-k9-Livemocha", new int[]{40279, 11023802});
//        resultadoArquivado.put("TIPDecomp-k9-ca-AstroPh", new int[]{9314, 11025092});
//        resultadoArquivado.put("TIPDecomp-k9-ca-CondMat", new int[]{17096, 11026085});
//        resultadoArquivado.put("TIPDecomp-k9-ca-GrQc", new int[]{4507, 11026113});
//        resultadoArquivado.put("TIPDecomp-k9-ca-HepPh", new int[]{7978, 11026517});
//        resultadoArquivado.put("TIPDecomp-k9-ca-HepTh", new int[]{8286, 11026621});
//        resultadoArquivado.put("TIPDecomp-r1-BlogCatalog", new int[]{1, 11079286});
//        resultadoArquivado.put("TIPDecomp-r1-BlogCatalog2", new int[]{1, 11101273});
//        resultadoArquivado.put("TIPDecomp-r1-BlogCatalog3", new int[]{1, 11101713});
//        resultadoArquivado.put("TIPDecomp-r1-BuzzNet", new int[]{1, 11174869});
//        resultadoArquivado.put("TIPDecomp-r1-Delicious", new int[]{65, 11233487});
//        resultadoArquivado.put("TIPDecomp-r1-Douban", new int[]{1, 11416212});
//        resultadoArquivado.put("TIPDecomp-r1-Last.fm", new int[]{55, 11502557});
//        resultadoArquivado.put("TIPDecomp-r1-Livemocha", new int[]{1, 11588236});
//        resultadoArquivado.put("TIPDecomp-r1-ca-AstroPh", new int[]{297, 11589326});
//        resultadoArquivado.put("TIPDecomp-r1-ca-CondMat", new int[]{594, 11590871});
//        resultadoArquivado.put("TIPDecomp-r1-ca-GrQc", new int[]{379, 11590954});
//        resultadoArquivado.put("TIPDecomp-r1-ca-HepPh", new int[]{289, 11591418});
//        resultadoArquivado.put("TIPDecomp-r1-ca-HepTh", new int[]{451, 11591704});
//        resultadoArquivado.put("TIPDecomp-r2-BlogCatalog", new int[]{75, 11651641});
//        resultadoArquivado.put("TIPDecomp-r2-BlogCatalog2", new int[]{2, 11720849});
//        resultadoArquivado.put("TIPDecomp-r2-BlogCatalog3", new int[]{2, 11721327});
//        resultadoArquivado.put("TIPDecomp-r2-BuzzNet", new int[]{2, 11797557});
//        resultadoArquivado.put("TIPDecomp-r2-Delicious", new int[]{674, 11855177});
//        resultadoArquivado.put("TIPDecomp-r2-Douban", new int[]{243, 12032648});
//        resultadoArquivado.put("TIPDecomp-r2-Last.fm", new int[]{343, 12115808});
//        resultadoArquivado.put("TIPDecomp-r2-Livemocha", new int[]{19, 12205065});
//        resultadoArquivado.put("TIPDecomp-r2-ca-AstroPh", new int[]{829, 12206225});
//        resultadoArquivado.put("TIPDecomp-r2-ca-CondMat", new int[]{1790, 12207909});
//        resultadoArquivado.put("TIPDecomp-r2-ca-GrQc", new int[]{871, 12207995});
//        resultadoArquivado.put("TIPDecomp-r2-ca-HepPh", new int[]{806, 12208481});
//        resultadoArquivado.put("TIPDecomp-r2-ca-HepTh", new int[]{1159, 12208777});
//        resultadoArquivado.put("TIPDecomp-r3-BlogCatalog", new int[]{172, 12268905});
//        resultadoArquivado.put("TIPDecomp-r3-BlogCatalog2", new int[]{3, 12338411});
//        resultadoArquivado.put("TIPDecomp-r3-BlogCatalog3", new int[]{4, 12338849});
//        resultadoArquivado.put("TIPDecomp-r3-BuzzNet", new int[]{13, 12411343});
//        resultadoArquivado.put("TIPDecomp-r3-Delicious", new int[]{1760, 12471109});
//        resultadoArquivado.put("TIPDecomp-r3-Douban", new int[]{858, 12611865});
//        resultadoArquivado.put("TIPDecomp-r3-Last.fm", new int[]{1129, 12701476});
//        resultadoArquivado.put("TIPDecomp-r3-Livemocha", new int[]{93, 12794916});
//        resultadoArquivado.put("TIPDecomp-r3-ca-AstroPh", new int[]{1423, 12796120});
//        resultadoArquivado.put("TIPDecomp-r3-ca-CondMat", new int[]{3250, 12797881});
//        resultadoArquivado.put("TIPDecomp-r3-ca-GrQc", new int[]{1351, 12797966});
//        resultadoArquivado.put("TIPDecomp-r3-ca-HepPh", new int[]{1422, 12798467});
//        resultadoArquivado.put("TIPDecomp-r3-ca-HepTh", new int[]{1950, 12798766});
//        resultadoArquivado.put("TIPDecomp-r4-BlogCatalog", new int[]{301, 12857758});
//        resultadoArquivado.put("TIPDecomp-r4-BlogCatalog2", new int[]{4, 12928903});
//        resultadoArquivado.put("TIPDecomp-r4-BlogCatalog3", new int[]{8, 12929396});
//        resultadoArquivado.put("TIPDecomp-r4-BuzzNet", new int[]{38, 13006424});
//        resultadoArquivado.put("TIPDecomp-r4-Delicious", new int[]{3079, 13067230});
//        resultadoArquivado.put("TIPDecomp-r4-Douban", new int[]{1845, 13201257});
//        resultadoArquivado.put("TIPDecomp-r4-Last.fm", new int[]{2366, 13291244});
//        resultadoArquivado.put("TIPDecomp-r4-Livemocha", new int[]{252, 13382579});
//        resultadoArquivado.put("TIPDecomp-r4-ca-AstroPh", new int[]{2007, 13383825});
//        resultadoArquivado.put("TIPDecomp-r4-ca-CondMat", new int[]{4757, 13385562});
//        resultadoArquivado.put("TIPDecomp-r4-ca-GrQc", new int[]{1722, 13385642});
//        resultadoArquivado.put("TIPDecomp-r4-ca-HepPh", new int[]{1987, 13386174});
//        resultadoArquivado.put("TIPDecomp-r4-ca-HepTh", new int[]{2598, 13386470});
//        resultadoArquivado.put("TIPDecomp-r5-BlogCatalog", new int[]{467, 13437441});
//        resultadoArquivado.put("TIPDecomp-r5-BlogCatalog2", new int[]{5, 13511798});
//        resultadoArquivado.put("TIPDecomp-r5-BlogCatalog3", new int[]{15, 13512278});
//        resultadoArquivado.put("TIPDecomp-r5-BuzzNet", new int[]{79, 13592951});
//        resultadoArquivado.put("TIPDecomp-r5-Delicious", new int[]{4365, 13654592});
//        resultadoArquivado.put("TIPDecomp-r5-Douban", new int[]{3202, 13786820});
//        resultadoArquivado.put("TIPDecomp-r5-Last.fm", new int[]{4004, 13876897});
//        resultadoArquivado.put("TIPDecomp-r5-Livemocha", new int[]{512, 13969809});
//        resultadoArquivado.put("TIPDecomp-r5-ca-AstroPh", new int[]{2560, 13971047});
//        resultadoArquivado.put("TIPDecomp-r5-ca-CondMat", new int[]{6092, 13972773});
//        resultadoArquivado.put("TIPDecomp-r5-ca-GrQc", new int[]{1979, 13972850});
//        resultadoArquivado.put("TIPDecomp-r5-ca-HepPh", new int[]{2538, 13973388});
//        resultadoArquivado.put("TIPDecomp-r5-ca-HepTh", new int[]{3173, 13973673});
//        resultadoArquivado.put("TIPDecomp-r6-BlogCatalog", new int[]{656, 14024185});
//        resultadoArquivado.put("TIPDecomp-r6-BlogCatalog2", new int[]{10, 14099617});
//        resultadoArquivado.put("TIPDecomp-r6-BlogCatalog3", new int[]{25, 14100095});
//        resultadoArquivado.put("TIPDecomp-r6-BuzzNet", new int[]{195, 14178054});
//        resultadoArquivado.put("TIPDecomp-r6-Delicious", new int[]{5581, 14236795});
//        resultadoArquivado.put("TIPDecomp-r6-Douban", new int[]{4949, 14387074});
//        resultadoArquivado.put("TIPDecomp-r6-Last.fm", new int[]{6047, 14484991});
//        resultadoArquivado.put("TIPDecomp-r6-Livemocha", new int[]{880, 14580297});
//        resultadoArquivado.put("TIPDecomp-r6-ca-AstroPh", new int[]{3069, 14581521});
//        resultadoArquivado.put("TIPDecomp-r6-ca-CondMat", new int[]{7290, 14583190});
//        resultadoArquivado.put("TIPDecomp-r6-ca-GrQc", new int[]{2194, 14583264});
//        resultadoArquivado.put("TIPDecomp-r6-ca-HepPh", new int[]{3057, 14583783});
//        resultadoArquivado.put("TIPDecomp-r6-ca-HepTh", new int[]{3657, 14584052});
//        resultadoArquivado.put("TIPDecomp-r7-BlogCatalog", new int[]{896, 14634404});
//        resultadoArquivado.put("TIPDecomp-r7-BlogCatalog2", new int[]{18, 14709122});
//        resultadoArquivado.put("TIPDecomp-r7-BlogCatalog3", new int[]{33, 14709604});
//        resultadoArquivado.put("TIPDecomp-r7-BuzzNet", new int[]{413, 14787746});
//        resultadoArquivado.put("TIPDecomp-r7-Delicious", new int[]{6721, 14849265});
//        resultadoArquivado.put("TIPDecomp-r7-Douban", new int[]{6949, 15004944});
//        resultadoArquivado.put("TIPDecomp-r7-Last.fm", new int[]{8249, 15096823});
//        resultadoArquivado.put("TIPDecomp-r7-Livemocha", new int[]{1358, 15188382});
//        resultadoArquivado.put("TIPDecomp-r7-ca-AstroPh", new int[]{3568, 15189635});
//        resultadoArquivado.put("TIPDecomp-r7-ca-CondMat", new int[]{8322, 15191236});
//        resultadoArquivado.put("TIPDecomp-r7-ca-GrQc", new int[]{2368, 15191307});
//        resultadoArquivado.put("TIPDecomp-r7-ca-HepPh", new int[]{3475, 15191822});
//        resultadoArquivado.put("TIPDecomp-r7-ca-HepTh", new int[]{4031, 15192094});
//        resultadoArquivado.put("TIPDecomp-r8-BlogCatalog", new int[]{1124, 15242416});
//        resultadoArquivado.put("TIPDecomp-r8-BlogCatalog2", new int[]{27, 15317833});
//        resultadoArquivado.put("TIPDecomp-r8-BlogCatalog3", new int[]{49, 15318307});
//        resultadoArquivado.put("TIPDecomp-r8-BuzzNet", new int[]{713, 15397534});
//        resultadoArquivado.put("TIPDecomp-r8-Delicious", new int[]{7760, 15452104});
//        resultadoArquivado.put("TIPDecomp-r8-Douban", new int[]{9221, 15588873});
//        resultadoArquivado.put("TIPDecomp-r8-Last.fm", new int[]{10621, 15678958});
//        resultadoArquivado.put("TIPDecomp-r8-Livemocha", new int[]{1948, 15770406});
//        resultadoArquivado.put("TIPDecomp-r8-ca-AstroPh", new int[]{4027, 15771669});
//        resultadoArquivado.put("TIPDecomp-r8-ca-CondMat", new int[]{9198, 15773259});
//        resultadoArquivado.put("TIPDecomp-r8-ca-GrQc", new int[]{2495, 15773327});
//        resultadoArquivado.put("TIPDecomp-r8-ca-HepPh", new int[]{3840, 15773836});
//        resultadoArquivado.put("TIPDecomp-r8-ca-HepTh", new int[]{4329, 15774085});
//        resultadoArquivado.put("TIPDecomp-r9-BlogCatalog", new int[]{1381, 15823186});
//        resultadoArquivado.put("TIPDecomp-r9-BlogCatalog2", new int[]{37, 15888664});
//        resultadoArquivado.put("TIPDecomp-r9-BlogCatalog3", new int[]{73, 15889136});
//        resultadoArquivado.put("TIPDecomp-r9-BuzzNet", new int[]{1014, 15969399});
//        resultadoArquivado.put("TIPDecomp-r9-Delicious", new int[]{8770, 16032015});
//        resultadoArquivado.put("TIPDecomp-r9-Douban", new int[]{11599, 16215528});
//        resultadoArquivado.put("TIPDecomp-r9-Last.fm", new int[]{13132, 16305694});
//        resultadoArquivado.put("TIPDecomp-r9-Livemocha", new int[]{2694, 16396587});
//        resultadoArquivado.put("TIPDecomp-r9-ca-AstroPh", new int[]{4463, 16397864});
//        resultadoArquivado.put("TIPDecomp-r9-ca-CondMat", new int[]{9939, 16399383});
//        resultadoArquivado.put("TIPDecomp-r9-ca-GrQc", new int[]{2600, 16399451});
//        resultadoArquivado.put("TIPDecomp-r9-ca-HepPh", new int[]{4112, 16399942});
//        resultadoArquivado.put("TIPDecomp-r9-ca-HepTh", new int[]{4590, 16400185});
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog", new int[]{109, 75445});
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog2", new int[]{92, 145571});
//        resultadoArquivado.put("TIPDecomp-m1-BlogCatalog3", new int[]{50, 145975});
//        resultadoArquivado.put("TIPDecomp-m1-BuzzNet", new int[]{113, 236701});
//        resultadoArquivado.put("TIPDecomp-m1-Delicious", new int[]{81, 294834});
//        resultadoArquivado.put("TIPDecomp-m1-Douban", new int[]{40, 460983});
//        resultadoArquivado.put("TIPDecomp-m1-Last.fm", new int[]{90, 562771});
//        resultadoArquivado.put("TIPDecomp-m1-Livemocha", new int[]{101, 656256});
//        resultadoArquivado.put("TIPDecomp-m1-ca-AstroPh", new int[]{357, 657326});
//        resultadoArquivado.put("TIPDecomp-m1-ca-CondMat", new int[]{652, 658861});
//        resultadoArquivado.put("TIPDecomp-m1-ca-GrQc", new int[]{402, 658939});
//        resultadoArquivado.put("TIPDecomp-m1-ca-HepPh", new int[]{359, 659408});
//        resultadoArquivado.put("TIPDecomp-m1-ca-HepTh", new int[]{470, 659692});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog", new int[]{346, 721617});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog2", new int[]{309, 800414});
//        resultadoArquivado.put("TIPDecomp-m2-BlogCatalog3", new int[]{140, 800866});
//        resultadoArquivado.put("TIPDecomp-m2-BuzzNet", new int[]{386, 898228});
//        resultadoArquivado.put("TIPDecomp-m2-Delicious", new int[]{159, 934536});
//        resultadoArquivado.put("TIPDecomp-m2-Douban", new int[]{365, 1109152});
//        resultadoArquivado.put("TIPDecomp-m2-Last.fm", new int[]{220, 1210003});
//        resultadoArquivado.put("TIPDecomp-m2-Livemocha", new int[]{570, 1310191});
//        resultadoArquivado.put("TIPDecomp-m2-ca-AstroPh", new int[]{540, 1311272});
//        resultadoArquivado.put("TIPDecomp-m2-ca-CondMat", new int[]{910, 1312819});
//        resultadoArquivado.put("TIPDecomp-m2-ca-GrQc", new int[]{493, 1312897});
//        resultadoArquivado.put("TIPDecomp-m2-ca-HepPh", new int[]{528, 1313357});
//        resultadoArquivado.put("TIPDecomp-m2-ca-HepTh", new int[]{558, 1313634});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog", new int[]{939, 1377628});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog2", new int[]{772, 1457179});
//        resultadoArquivado.put("TIPDecomp-m3-BlogCatalog3", new int[]{241, 1457587});
//        resultadoArquivado.put("TIPDecomp-m3-BuzzNet", new int[]{1149, 1539840});
//        resultadoArquivado.put("TIPDecomp-m3-Delicious", new int[]{520, 1597964});
//        resultadoArquivado.put("TIPDecomp-m3-Douban", new int[]{1628, 1772388});
//        resultadoArquivado.put("TIPDecomp-m3-Last.fm", new int[]{646, 1873000});
//        resultadoArquivado.put("TIPDecomp-m3-Livemocha", new int[]{2012, 1971855});
//        resultadoArquivado.put("TIPDecomp-m3-ca-AstroPh", new int[]{899, 1972883});
//        resultadoArquivado.put("TIPDecomp-m3-ca-CondMat", new int[]{1558, 1974400});
//        resultadoArquivado.put("TIPDecomp-m3-ca-GrQc", new int[]{664, 1974479});
//        resultadoArquivado.put("TIPDecomp-m3-ca-HepPh", new int[]{820, 1974937});
//        resultadoArquivado.put("TIPDecomp-m3-ca-HepTh", new int[]{775, 1975213});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog", new int[]{2160, 2035221});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog2", new int[]{1856, 2112505});
//        resultadoArquivado.put("TIPDecomp-m4-BlogCatalog3", new int[]{496, 2112952});
//        resultadoArquivado.put("TIPDecomp-m4-BuzzNet", new int[]{2958, 2198635});
//        resultadoArquivado.put("TIPDecomp-m4-Delicious", new int[]{1625, 2261272});
//        resultadoArquivado.put("TIPDecomp-m4-Douban", new int[]{5753, 2432017});
//        resultadoArquivado.put("TIPDecomp-m4-Last.fm", new int[]{2201, 2531231});
//        resultadoArquivado.put("TIPDecomp-m4-Livemocha", new int[]{5219, 2626061});
//        resultadoArquivado.put("TIPDecomp-m4-ca-AstroPh", new int[]{1594, 2627128});
//        resultadoArquivado.put("TIPDecomp-m4-ca-CondMat", new int[]{2801, 2628683});
//        resultadoArquivado.put("TIPDecomp-m4-ca-GrQc", new int[]{962, 2628760});
//        resultadoArquivado.put("TIPDecomp-m4-ca-HepPh", new int[]{1302, 2629217});
//        resultadoArquivado.put("TIPDecomp-m4-ca-HepTh", new int[]{1311, 2629493});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog", new int[]{4598, 2690677});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog2", new int[]{4391, 2768017});
//        resultadoArquivado.put("TIPDecomp-m5-BlogCatalog3", new int[]{834, 2768445});
//        resultadoArquivado.put("TIPDecomp-m5-BuzzNet", new int[]{7080, 2852536});
//        resultadoArquivado.put("TIPDecomp-m5-Delicious", new int[]{4572, 2914733});
//        resultadoArquivado.put("TIPDecomp-m5-Douban", new int[]{32996, 3042683});
//        resultadoArquivado.put("TIPDecomp-m5-Last.fm", new int[]{6534, 3134532});
//        resultadoArquivado.put("TIPDecomp-m5-Livemocha", new int[]{11279, 3230052});
//        resultadoArquivado.put("TIPDecomp-m5-ca-AstroPh", new int[]{2569, 3231100});
//        resultadoArquivado.put("TIPDecomp-m5-ca-CondMat", new int[]{4252, 3232619});
//        resultadoArquivado.put("TIPDecomp-m5-ca-GrQc", new int[]{1173, 3232693});
//        resultadoArquivado.put("TIPDecomp-m5-ca-HepPh", new int[]{1911, 3233130});
//        resultadoArquivado.put("TIPDecomp-m5-ca-HepTh", new int[]{1796, 3233401});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog", new int[]{9599, 3290189});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog2", new int[]{10700, 3361791});
//        resultadoArquivado.put("TIPDecomp-m6-BlogCatalog3", new int[]{1362, 3362213});
//        resultadoArquivado.put("TIPDecomp-m6-BuzzNet", new int[]{15686, 3431241});
//        resultadoArquivado.put("TIPDecomp-m6-Delicious", new int[]{16083, 3480274});
//        resultadoArquivado.put("TIPDecomp-m6-Douban", new int[]{82937, 3574330});
//        resultadoArquivado.put("TIPDecomp-m6-Last.fm", new int[]{16460, 3672351});
//        resultadoArquivado.put("TIPDecomp-m6-Livemocha", new int[]{21147, 3764319});
//        resultadoArquivado.put("TIPDecomp-m6-ca-AstroPh", new int[]{4829, 3765360});
//        resultadoArquivado.put("TIPDecomp-m6-ca-CondMat", new int[]{7479, 3766858});
//        resultadoArquivado.put("TIPDecomp-m6-ca-GrQc", new int[]{1995, 3766935});
//        resultadoArquivado.put("TIPDecomp-m6-ca-HepPh", new int[]{3387, 3767356});
//        resultadoArquivado.put("TIPDecomp-m6-ca-HepTh", new int[]{3271, 3767605});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog", new int[]{20294, 3822748});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog2", new int[]{22917, 3845352});
//        resultadoArquivado.put("TIPDecomp-m7-BlogCatalog3", new int[]{1859, 3845760});
//        resultadoArquivado.put("TSS-Cordasco-m9-BlogCatalog", new int[]{10178, 72907});
//        resultadoArquivado.put("TSS-Cordasco-m9-BlogCatalog2", new int[]{3227, 160709});
//        resultadoArquivado.put("TSS-Cordasco-m9-BlogCatalog3", new int[]{2241, 161321});
//        resultadoArquivado.put("TSS-Cordasco-m9-BuzzNet", new int[]{14539, 261439});
//        resultadoArquivado.put("TSS-Cordasco-m9-Delicious", new int[]{9743, 273047});
//        resultadoArquivado.put("TSS-Cordasco-m9-Douban", new int[]{8547, 328255});
//        resultadoArquivado.put("TSS-Cordasco-m9-Last.fm", new int[]{43751, 400877});
//        resultadoArquivado.put("TSS-Cordasco-m9-Livemocha", new int[]{25613, 476589});
//        resultadoArquivado.put("TSS-Cordasco-m9-ca-AstroPh", new int[]{10576, 477382});
//        resultadoArquivado.put("TSS-Cordasco-m9-ca-CondMat", new int[]{12997, 478397});
//        resultadoArquivado.put("TSS-Cordasco-m9-ca-GrQc", new int[]{2731, 478463});
//        resultadoArquivado.put("TSS-Cordasco-m9-ca-HepPh", new int[]{6543, 478805});
//        resultadoArquivado.put("TSS-Cordasco-m9-ca-HepTh", new int[]{4880, 479038});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog", new int[]{27, 74105});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog2", new int[]{27, 178405});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog3", new int[]{15, 179099});
//        resultadoArquivado.put("TSS-Cordasco-m1-BuzzNet", new int[]{1, 262238});
//        resultadoArquivado.put("TSS-Cordasco-m1-Delicious", new int[]{67, 279322});
//        resultadoArquivado.put("TSS-Cordasco-m1-Douban", new int[]{56, 451981});
//        resultadoArquivado.put("TSS-Cordasco-m1-Last.fm", new int[]{91, 557903});
//        resultadoArquivado.put("TSS-Cordasco-m1-Livemocha", new int[]{78, 657166});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-AstroPh", new int[]{375, 658503});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-CondMat", new int[]{684, 660419});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-GrQc", new int[]{409, 660541});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-HepPh", new int[]{370, 661150});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-HepTh", new int[]{472, 661499});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog", new int[]{118, 720593});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog2", new int[]{99, 802200});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog3", new int[]{42, 802801});
//        resultadoArquivado.put("TSS-Cordasco-m2-BuzzNet", new int[]{102, 894234});
//        resultadoArquivado.put("TSS-Cordasco-m2-Delicious", new int[]{228, 910302});
//        resultadoArquivado.put("TSS-Cordasco-m2-Douban", new int[]{438, 1132949});
//        resultadoArquivado.put("TSS-Cordasco-m2-Last.fm", new int[]{375, 1224982});
//        resultadoArquivado.put("TSS-Cordasco-m2-Livemocha", new int[]{415, 1316967});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-AstroPh", new int[]{551, 1318307});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-CondMat", new int[]{923, 1320227});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-GrQc", new int[]{392, 1320292});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-HepPh", new int[]{536, 1320849});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-HepTh", new int[]{571, 1321183});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog", new int[]{273, 1379988});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog2", new int[]{189, 1460127});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog3", new int[]{88, 1460825});
//        resultadoArquivado.put("TSS-Cordasco-m3-BuzzNet", new int[]{286, 1549093});
//        resultadoArquivado.put("TSS-Cordasco-m3-Delicious", new int[]{509, 1563300});
//        resultadoArquivado.put("TSS-Cordasco-m3-Douban", new int[]{2115, 1771012});
//        resultadoArquivado.put("TSS-Cordasco-m3-Last.fm", new int[]{1373, 1870568});
//        resultadoArquivado.put("TSS-Cordasco-m3-Livemocha", new int[]{962, 1960350});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-AstroPh", new int[]{930, 1962029});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-CondMat", new int[]{1547, 1964375});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-GrQc", new int[]{648, 1964487});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-HepPh", new int[]{845, 1965177});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-HepTh", new int[]{771, 1965587});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog", new int[]{545, 2027277});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog2", new int[]{332, 2107907});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog3", new int[]{163, 2108599});
//        resultadoArquivado.put("TSS-Cordasco-m4-BuzzNet", new int[]{725, 2195053});
//        resultadoArquivado.put("TSS-Cordasco-m4-Delicious", new int[]{1035, 2204352});
//        resultadoArquivado.put("TSS-Cordasco-m4-Douban", new int[]{3949, 2408326});
//        resultadoArquivado.put("TSS-Cordasco-m4-Last.fm", new int[]{3181, 2508543});
//        resultadoArquivado.put("TSS-Cordasco-m4-Livemocha", new int[]{1850, 2601122});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-AstroPh", new int[]{1583, 2602776});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-CondMat", new int[]{2604, 2605059});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-GrQc", new int[]{904, 2605169});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-HepPh", new int[]{1342, 2605822});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-HepTh", new int[]{1215, 2606213});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog", new int[]{977, 2666938});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog2", new int[]{527, 2746906});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog3", new int[]{274, 2747568});
//        resultadoArquivado.put("TSS-Cordasco-m5-BuzzNet", new int[]{1445, 2811791});
//        resultadoArquivado.put("TSS-Cordasco-m5-Delicious", new int[]{1725, 2826645});
//        resultadoArquivado.put("TSS-Cordasco-m5-Douban", new int[]{5155, 3017070});
//        resultadoArquivado.put("TSS-Cordasco-m5-Last.fm", new int[]{5913, 3110276});
//        resultadoArquivado.put("TSS-Cordasco-m5-Livemocha", new int[]{3227, 3201373});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-AstroPh", new int[]{2476, 3202944});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-CondMat", new int[]{3586, 3205084});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-GrQc", new int[]{1060, 3205188});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-HepPh", new int[]{1894, 3205819});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-HepTh", new int[]{1552, 3206199});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog", new int[]{1734, 3268219});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog2", new int[]{806, 3348705});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog3", new int[]{457, 3349411});
//        resultadoArquivado.put("TSS-Cordasco-m6-BuzzNet", new int[]{2661, 3438217});
//        resultadoArquivado.put("TSS-Cordasco-m6-Delicious", new int[]{3537, 3452668});
//        resultadoArquivado.put("TSS-Cordasco-m6-Douban", new int[]{6735, 3603712});
//        resultadoArquivado.put("TSS-Cordasco-m6-Last.fm", new int[]{10810, 3689258});
//        resultadoArquivado.put("TSS-Cordasco-m6-Livemocha", new int[]{5404, 3787058});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-AstroPh", new int[]{4242, 3788519});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-CondMat", new int[]{6129, 3790442});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-GrQc", new int[]{1684, 3790534});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-HepPh", new int[]{3131, 3791107});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-HepTh", new int[]{2649, 3791442});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog", new int[]{2984, 3849462});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog2", new int[]{1193, 3927466});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog3", new int[]{726, 3928149});
//        resultadoArquivado.put("TSS-Cordasco-m7-BuzzNet", new int[]{4504, 4016078});
//        resultadoArquivado.put("TSS-Cordasco-m7-Delicious", new int[]{5710, 4029427});
//        resultadoArquivado.put("TSS-Cordasco-m7-Douban", new int[]{7608, 4190460});
//        resultadoArquivado.put("TSS-Cordasco-m7-Last.fm", new int[]{18316, 4271207});
//        resultadoArquivado.put("TSS-Cordasco-m7-Livemocha", new int[]{8712, 4369199});
//        resultadoArquivado.put("TSS-Cordasco-m7-ca-AstroPh", new int[]{6198, 4370542});
//        resultadoArquivado.put("TSS-Cordasco-m7-ca-CondMat", new int[]{8661, 4372316});
//        resultadoArquivado.put("TSS-Cordasco-m7-ca-GrQc", new int[]{2158, 4372401});
//        resultadoArquivado.put("TSS-Cordasco-m7-ca-HepPh", new int[]{4384, 4372939});
//        resultadoArquivado.put("TSS-Cordasco-m7-ca-HepTh", new int[]{3618, 4373253});
//        resultadoArquivado.put("TSS-Cordasco-m8-BlogCatalog", new int[]{5243, 4438882});
//        resultadoArquivado.put("TSS-Cordasco-m8-BlogCatalog2", new int[]{1869, 4529220});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog", new int[]{27, 71494});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog2", new int[]{27, 178692});
//        resultadoArquivado.put("TSS-Cordasco-m1-BlogCatalog3", new int[]{15, 179412});
//        resultadoArquivado.put("TSS-Cordasco-m1-BuzzNet", new int[]{1, 235809});
//        resultadoArquivado.put("TSS-Cordasco-m1-Delicious", new int[]{67, 250450});
//        resultadoArquivado.put("TSS-Cordasco-m1-Douban", new int[]{56, 431760});
//        resultadoArquivado.put("TSS-Cordasco-m1-Last.fm", new int[]{91, 536755});
//        resultadoArquivado.put("TSS-Cordasco-m1-Livemocha", new int[]{78, 638671});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-AstroPh", new int[]{375, 640128});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-CondMat", new int[]{684, 642085});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-GrQc", new int[]{409, 642201});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-HepPh", new int[]{370, 642773});
//        resultadoArquivado.put("TSS-Cordasco-m1-ca-HepTh", new int[]{472, 643119});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog", new int[]{118, 703402});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog2", new int[]{99, 785625});
//        resultadoArquivado.put("TSS-Cordasco-m2-BlogCatalog3", new int[]{42, 786225});
//        resultadoArquivado.put("TSS-Cordasco-m2-BuzzNet", new int[]{102, 874228});
//        resultadoArquivado.put("TSS-Cordasco-m2-Delicious", new int[]{228, 886493});
//        resultadoArquivado.put("TSS-Cordasco-m2-Douban", new int[]{438, 1077713});
//        resultadoArquivado.put("TSS-Cordasco-m2-Last.fm", new int[]{375, 1177246});
//        resultadoArquivado.put("TSS-Cordasco-m2-Livemocha", new int[]{415, 1278555});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-AstroPh", new int[]{551, 1279929});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-CondMat", new int[]{923, 1281845});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-GrQc", new int[]{392, 1281912});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-HepPh", new int[]{536, 1282461});
//        resultadoArquivado.put("TSS-Cordasco-m2-ca-HepTh", new int[]{571, 1282796});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog", new int[]{273, 1345586});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog2", new int[]{189, 1426887});
//        resultadoArquivado.put("TSS-Cordasco-m3-BlogCatalog3", new int[]{88, 1427611});
//        resultadoArquivado.put("TSS-Cordasco-m3-BuzzNet", new int[]{286, 1509534});
//        resultadoArquivado.put("TSS-Cordasco-m3-Delicious", new int[]{509, 1522860});
//        resultadoArquivado.put("TSS-Cordasco-m3-Douban", new int[]{2115, 1704501});
//        resultadoArquivado.put("TSS-Cordasco-m3-Last.fm", new int[]{1373, 1803464});
//        resultadoArquivado.put("TSS-Cordasco-m3-Livemocha", new int[]{962, 1904149});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-AstroPh", new int[]{930, 1905851});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-CondMat", new int[]{1547, 1908202});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-GrQc", new int[]{648, 1908314});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-HepPh", new int[]{845, 1909008});
//        resultadoArquivado.put("TSS-Cordasco-m3-ca-HepTh", new int[]{771, 1909419});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog", new int[]{545, 1969647});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog2", new int[]{332, 2050049});
//        resultadoArquivado.put("TSS-Cordasco-m4-BlogCatalog3", new int[]{163, 2050765});
//        resultadoArquivado.put("TSS-Cordasco-m4-BuzzNet", new int[]{725, 2138343});
//        resultadoArquivado.put("TSS-Cordasco-m4-Delicious", new int[]{1035, 2151902});
//        resultadoArquivado.put("TSS-Cordasco-m4-Douban", new int[]{3949, 2321461});
//        resultadoArquivado.put("TSS-Cordasco-m4-Last.fm", new int[]{3181, 2419048});
//        resultadoArquivado.put("TSS-Cordasco-m4-Livemocha", new int[]{1850, 2516009});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-AstroPh", new int[]{1583, 2517644});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-CondMat", new int[]{2604, 2519900});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-GrQc", new int[]{904, 2520008});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-HepPh", new int[]{1342, 2520659});
//        resultadoArquivado.put("TSS-Cordasco-m4-ca-HepTh", new int[]{1215, 2521052});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog", new int[]{977, 2583177});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog2", new int[]{527, 2664324});
//        resultadoArquivado.put("TSS-Cordasco-m5-BlogCatalog3", new int[]{274, 2665026});
//        resultadoArquivado.put("TSS-Cordasco-m5-BuzzNet", new int[]{1445, 2749394});
//        resultadoArquivado.put("TSS-Cordasco-m5-Delicious", new int[]{1725, 2762218});
//        resultadoArquivado.put("TSS-Cordasco-m5-Douban", new int[]{5155, 2902537});
//        resultadoArquivado.put("TSS-Cordasco-m5-Last.fm", new int[]{5913, 2992325});
//        resultadoArquivado.put("TSS-Cordasco-m5-Livemocha", new int[]{3227, 3091218});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-AstroPh", new int[]{2476, 3092792});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-CondMat", new int[]{3586, 3094967});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-GrQc", new int[]{1060, 3095071});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-HepPh", new int[]{1894, 3095714});
//        resultadoArquivado.put("TSS-Cordasco-m5-ca-HepTh", new int[]{1552, 3096100});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog", new int[]{1734, 3155755});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog2", new int[]{806, 3236246});
//        resultadoArquivado.put("TSS-Cordasco-m6-BlogCatalog3", new int[]{457, 3236922});
//        resultadoArquivado.put("TSS-Cordasco-m6-BuzzNet", new int[]{2661, 3312707});
//        resultadoArquivado.put("TSS-Cordasco-m6-Delicious", new int[]{3537, 3323976});
//        resultadoArquivado.put("TSS-Cordasco-m6-Douban", new int[]{6735, 3461049});
//        resultadoArquivado.put("TSS-Cordasco-m6-Last.fm", new int[]{10810, 3551775});
//        resultadoArquivado.put("TSS-Cordasco-m6-Livemocha", new int[]{5404, 3648795});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-AstroPh", new int[]{4242, 3650233});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-CondMat", new int[]{6129, 3652152});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-GrQc", new int[]{1684, 3652244});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-HepPh", new int[]{3131, 3652812});
//        resultadoArquivado.put("TSS-Cordasco-m6-ca-HepTh", new int[]{2649, 3653148});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog", new int[]{2984, 3713964});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog2", new int[]{1193, 3759935});
//        resultadoArquivado.put("TSS-Cordasco-m7-BlogCatalog3", new int[]{726, 3760627});
//        resultadoArquivado.put("TSS-Cordasco-m7-BuzzNet", new int[]{4504, 3848889});
//        resultadoArquivado.put("TSS-Cordasco-m1-YouTube2", new int[]{1480, 3831156});
//        resultadoArquivado.put("TIPDecomp-m1-YouTube2", new int[]{2579, 12442973});
//        resultadoArquivado.put("GreedyDifTotal-m1-YouTube2", new int[]{1476, 141603});
//        resultadoArquivado.put("TSS-Cordasco-m2-YouTube2", new int[]{5560, 17642164});
//        resultadoArquivado.put("TIPDecomp-m2-YouTube2", new int[]{7406, 26222933});
//        resultadoArquivado.put("GreedyDifTotal-m2-YouTube2", new int[]{3760, 253677});
//        resultadoArquivado.put("TSS-Cordasco-m3-YouTube2", new int[]{15621, 30982938});
//
//        ///
//    }

    static String[] dataSets = new String[]{
        "ca-GrQc",
        "ca-HepTh",
        "ca-CondMat",
        "ca-HepPh",
        "ca-AstroPh",
        "Douban",
        "Delicious",
        "BlogCatalog3",
        "BlogCatalog2",
        "Livemocha",
        "BlogCatalog",
        "BuzzNet",
        "Last.fm", //        "YouTube2"
    };
    static AbstractHeuristic[] operations = null;

    static long totalTime[];
    static Integer[] result;
    static Integer[] delta;
    static int[] contMelhor;
    static int[] contPior;
    static int[] contIgual;

    public static void main(String... args) throws FileNotFoundException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        TSSCordasco tss = new TSSCordasco();
//        GraphTSSGreedy tssg = new GraphTSSGreedy();
        HNVA hnva = new HNVA();
        HNV2 hnv2 = new HNV2();
        HNV1 hnv1 = new HNV1();
        hnv1.setVerbose(true);
        HNV0 hnv0 = new HNV0();
        CCMPanizi ccm = new CCMPanizi();

        TIPDecomp tip = new TIPDecomp();

        GreedyCordasco gc = new GreedyCordasco();
        GreedyDegree gd = new GreedyDegree();
        gd.setRefine2(true);
        GreedyDeltaTss gdt = new GreedyDeltaTss();
        gdt.setRefine2(true);
        GreedyBonusDist gdit = new GreedyBonusDist();
        GreedyDifTotal gdft = new GreedyDifTotal();
        gdft.setRefine2(true);
        GreedyDeltaXDifTotal gdxd = new GreedyDeltaXDifTotal();
        GreedyDistAndDifDelta gdd = new GreedyDistAndDifDelta();
        gdd.setRefine(true);
        gdd.setRefine2(true);

        GreedyDistAndDifDelta gdd1 = new GreedyDistAndDifDelta();
        gdd1.setRefine2(false);

        ccm.setRefine(true);
        ccm.setRefine2(true);
        gd.setRefine(true);
        gd.setRefine2(true);

        operations = new AbstractHeuristic[]{
            tss,
            //            heur1,
            //            heur2, 
            //            heur3, heur4,
            //            heur5,
            //            heur5t,
            //            tssg,
            //            heur5t2
            //            optm,
            //            optm2,
            //            tip,
            //            hnv0, //            hnv1, 
            //            hnv2
            //            hnv0, gd, gdit, 
            //            hnva
            //            ccm,
            //            gd, //            gdt
            //                        gc,  gdt
            //            gdft,
            gdd1,
            gdd
        };
        totalTime = new long[operations.length];
        result = new Integer[operations.length];
        delta = new Integer[operations.length];
        contMelhor = new int[operations.length];
        contPior = new int[operations.length];
        contIgual = new int[operations.length];
        for (int i = 0;
                i < operations.length;
                i++) {
            contMelhor[i] = contPior[i] = contIgual[i] = 0;
        }

        Arrays.sort(dataSets);

        String strResultFile = "resultado-" + ExecBigDataSets.class.getSimpleName() + ".txt";
        File resultFile = new File(strResultFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile, true));
        for (String op : new String[]{
            "m", 
//            "r",
        //            "k", //            "random"
        }) {
            if (op.equals("random")) {
                for (AbstractHeuristic ab : operations) {
                    ab.setR(null);
                }
                execOperations(op, 0, writer);

            } else {
                for (int k = 1; k <= 7; k++) {
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
                    for (int i = 1;
                            i < operations.length;
                            i++) {
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
        for (int i = 1;
                i < operations.length;
                i++) {
            System.out.println(" - Operation: " + operations[i].getName());
            System.out.println("   * Best: " + contMelhor[i]);
            System.out.println("   * Worst: " + contPior[i]);
            System.out.println("   * Equal: " + contIgual[i]);
        }
    }

    static void execOperations(String op, int k, BufferedWriter writer) throws FileNotFoundException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (String s : dataSets) {
            System.out.println("\n-DATASET: " + s);

            UndirectedSparseGraphTO<Integer, Integer> graphES
                    = null;

            try {
                URI urigraph = URI.create("jar:file:data/big/all-big.zip!/" + s + "/" + s + ".txt");
                InputStream streamgraph = urigraph.toURL().openStream();
                graphES = UtilGraph.loadBigDataset(streamgraph);
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
                    System.out.println(" resultadoArquivado.put(\"" + arquivadoStr + "\", new int[]{" + result[i] + ", " + totalTime[i] + "});");
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
                    boolean checkIfHullSet = operations[0].checkIfHullSet(graphES, ((Set<Integer>) doOperation.get(DEFAULT_PARAM_NAME_SET)));
                    if (!checkIfHullSet) {
                        System.out.println("ALERT: ----- THE RESULT IS NOT A HULL SET");
//                            throw new IllegalStateException("IS NOT HULL SET");
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
