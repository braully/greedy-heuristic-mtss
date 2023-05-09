package io.github.braully.graph.util;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Braully Rocha da Silva
 */
public class UtilProccess {

    //PT-BR
    private static final DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Queue<Integer> queue = new LinkedList<Integer>();

    public static long lastime;

    public static void printCurrentItme() {
        try {
            long current = System.currentTimeMillis();
            System.out.printf("time: %s\n", dateFormater.format(current));
            if (lastime > 0) {
                long delta = current - lastime;
                System.out.print("Delta: ");
                printTimeFormated(delta);
            }

            lastime = current;
        } catch (Exception e) {
        }
    }

    public static void printStartTime() {
        try {
            long current = System.currentTimeMillis();
//            System.out.printf("time: %s\n", dateFormater.format(current));
            lastime = current;
        } catch (Exception e) {
        }
    }

    public static void startTime() {
        try {
            long current = System.currentTimeMillis();
//            System.out.printf("time: %s\n", dateFormater.format(current));
            lastime = current;
        } catch (Exception e) {
        }
    }

    public static long printEndTime() {
        long delta = 0;
        try {
            long current = System.currentTimeMillis();
//            System.out.printf("time: %s\n", dateFormater.format(current));
            if (lastime > 0) {
                delta = current - lastime;
                System.out.print("Delta time: ");
                printTimeFormated(delta);
            }

            lastime = current;
        } catch (Exception e) {
        }
        return delta;
    }

    public static long endTime() {
        long delta = 0;
        try {
            long current = System.currentTimeMillis();
//            System.out.printf("time: %s\n", dateFormater.format(current));
            if (lastime > 0) {
                delta = current - lastime;
//                System.out.print("Delta time: ");
//                printTimeFormated(delta);
            }

            lastime = current;
        } catch (Exception e) {
        }
        return delta;
    }

    public static void printCurrentItmeAndEstimated(long trabalhoRestante) {
        try {
            long current = System.currentTimeMillis();
//            System.out.printf("time: %s\n", dateFormater.format(current));
            if (lastime > 0) {
                long delta = current - lastime;
                System.out.print("Estimated: ");
                printTimeFormated(delta * trabalhoRestante);
            }

            lastime = current;
        } catch (Exception e) {
        }
    }

    public static void printTimeFormated(long delta) {
        long millis = delta % 1000;
        long second = (delta / 1000) % 60;
        long minute = (delta / (1000 * 60)) % 60;
        long hour = (delta / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
        System.out.printf("%s\n", time);
    }

    public static void printArray(int[] arr) {
        int len = arr.length;
        System.out.print("[");
        for (int i = 0; i < len; i++) {
            System.out.print(arr[i]);
            if (i < len - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

//    public static InputStream openFile()
}
