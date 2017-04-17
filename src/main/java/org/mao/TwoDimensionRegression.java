package org.mao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static java.lang.Double.valueOf;
import static java.lang.Math.abs;

/**
 * Two Dimension Regression.
 *
 * Model: R = g(x) = w2 * X^2 + w1 * X^1 + w0
 *
 * To find out w2, w1, w0
 *
 * @author Jianwei Mao
 *
 * Created by mao on 2017/4/17.
 */
public class TwoDimensionRegression {

    public static void main(String[] args) throws IOException {
        File dataFile = new File("D:\\MaoDev\\Mao_Machine_Learning_Research\\Dataset1.csv");
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));

        Map<Double, Double> xyPair = new HashMap();


        final String DOUHAO = ",";
        String data;
        String[] datas;
        while (true) {
            data = fin.readLine();
            if (data == null) {
                break;
            }

            datas = data.split(DOUHAO);

            xyPair.put(valueOf(datas[0]), valueOf(datas[1]));
        }



        ExecutorService threadPool = Executors.newFixedThreadPool(6, new ThreadFactory() {
            int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "CPU_" + (++count));
            }
        });

        Future<Double> fAvgX = threadPool.submit(new calAvgX(xyPair));


        double avgX;
        try {
            avgX = fAvgX.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // TODO - calculate A, B, C, D, E, F. and then w2, w1, w0.

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Mao forcibly shutdown now ...");
        }
    }

    /**
     * Calculates the average of X.
     */
    private static class calAvgX implements Callable<Double> {

        final Map xyPairs;

        calAvgX(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of X^2.
     */
    private static class calAvgXPower2 implements Callable<Double> {

        final Map xyPairs;

        calAvgXPower2(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of X^3.
     */
    private static class calAvgXPower3 implements Callable<Double> {

        final Map xyPairs;

        calAvgXPower3(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of X^4.
     */
    private static class calAvgXPower4 implements Callable<Double> {

        final Map xyPairs;

        calAvgXPower4(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of R.
     */
    private static class calAvgR implements Callable<Double> {

        final Map xyPairs;

        calAvgX(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of (X * R).
     */
    private static class calAvgXR implements Callable<Double> {

        final Map xyPairs;

        calAvgXR(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }

    /**
     * Calculates the average of (X^2 * R).
     */
    private static class calAvgXPower2R implements Callable<Double> {

        final Map xyPairs;

        calAvgXPower2R(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for(Map.Entry<Double, Double> xy : xySet) {
                xSum += xy.getKey();
            }

            return xSum / xySet.size();
        }
    }
}
