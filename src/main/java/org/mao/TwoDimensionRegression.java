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
 * <p>
 * Model: R = g(x) = w2 * X^2 + w1 * X^1 + w0
 * <p>
 * To find out w2, w1, w0
 *
 * @author Jianwei Mao
 *         <p>
 *         Created by mao on 2017/4/17.
 */
public class TwoDimensionRegression {

    public static void main(String[] args) throws IOException {

        Map<Double, Double> xyPair = new HashMap();

        readDataset(xyPair);


        ExecutorService threadPool = Executors.newFixedThreadPool(7, new ThreadFactory() {
            int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "CPU_" + (++count));
            }
        });

        double start = System.nanoTime();

        Future<Double> fAvgX = threadPool.submit(new calAvgX(xyPair));
        Future<Double> fAvgXPower2 = threadPool.submit(new calAvgXPower2(xyPair));
        Future<Double> fAvgXPower3 = threadPool.submit(new calAvgXPower3(xyPair));
        Future<Double> fAvgXPower4 = threadPool.submit(new calAvgXPower4(xyPair));
        Future<Double> fAvgR = threadPool.submit(new calAvgR(xyPair));
        Future<Double> fAvgXR = threadPool.submit(new calAvgXR(xyPair));
        Future<Double> fAvgXPower2R = threadPool.submit(new calAvgXPower2R(xyPair));


        double avgX, avgXPower2, avgXPower3, avgXPower4, avgR, avgXR, avgXPower2R;
        try {
            avgX = fAvgX.get();
            avgXPower2 = fAvgXPower2.get();
            avgXPower3 = fAvgXPower3.get();
            avgXPower4 = fAvgXPower4.get();
            avgR = fAvgR.get();
            avgXR = fAvgXR.get();
            avgXPower2R = fAvgXPower2R.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            shutdownThreadpool(threadPool);
            return;
        }

        double stop = System.nanoTime();

        System.out.println(String.format("Running Time: %s\n", stop-start));



        double A, B, C, D, E, F;
        A = -avgXPower4 + avgXPower2 * avgXPower2;
        B = -avgXPower3 + avgXPower2 * avgX;
        C = avgXPower2R - avgR * avgXPower2;
        D = B;
        E = -avgXPower2 + avgX * avgX;
        F = avgXR - avgR * avgX;


        double w2, w1, w0;
        w2 = (E * C - B * F) / (B * D - A * E);
        w1 = (-C - A * w2) / B;
        w0 = avgR - w2 * avgXPower2 - w1 * avgX;


        System.out.println(String.format("avgX = %s\navgXPower2 = %s\navgXPower3 = %s\navgXPower4 = %s\navgR = %s\navgXR = %s\navgXPower2R = %s\n",
                avgX, avgXPower2, avgXPower3, avgXPower4, avgR, avgXR, avgXPower2R));
        System.out.println(String.format("A = %s\nB = %s\nC = %s\nD = %s\nE = %s\nF = %s\n", A, B, C, D, E, F));
        System.out.println(String.format("w2 = %s\nw1 = %s\nw0 = %s\n", w2, w1, w0));
        System.out.println("\n-------------------------------------------\n");

        System.out.println(String.format("Amazing! Our Function is:\nR = g(x) = %10.8f * X^2 + %10.8f * X^1 + %10.8f\nJianwei Mao: 'Great Machine Learning!'\n", w2, w1, w0));



        shutdownThreadpool(threadPool);
    }

    private static void shutdownThreadpool(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Mao forcibly shutdown now ...");
        } finally {
            threadPool.shutdownNow();
        }
    }

    private static void readDataset(Map result) throws IOException {

        File dataFile = new File("D:\\MaoDev\\Mao_Machine_Learning_Research\\Datasets\\TwoDimensionRegression_20170417.csv");
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));

        final String COMMA = ",";
        String data;
        String[] datas;

        int count = 0;

        while (true && count++ < 2800) {
            data = fin.readLine();
            if (data == null) {
                break;
            }

            datas = data.split(COMMA);

            result.put(valueOf(datas[0]), valueOf(datas[2]));
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xSum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xPower2Sum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                xPower2Sum += (xy.getKey() * xy.getKey());
            }

            return xPower2Sum / xySet.size();
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xPower3Sum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                xPower3Sum += (xy.getKey() * xy.getKey() * xy.getKey());
            }

            return xPower3Sum / xySet.size();
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xPower4Sum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                xPower4Sum += (xy.getKey() * xy.getKey() * xy.getKey() * xy.getKey());
            }

            return xPower4Sum / xySet.size();
        }
    }

    /**
     * Calculates the average of R.
     */
    private static class calAvgR implements Callable<Double> {

        final Map xyPairs;

        calAvgR(final Map xyPairs) {
            this.xyPairs = xyPairs;
        }

        @Override
        public Double call() throws Exception {

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double rSum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                rSum += xy.getValue();
            }

            return rSum / xySet.size();
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xrSum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                xrSum += (xy.getKey() * xy.getValue());
            }

            return xrSum / xySet.size();
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

            System.out.println(String.format("--- %s working ---", Thread.currentThread().getName()));

            Set<Map.Entry<Double, Double>> xySet = xyPairs.entrySet();

            double xPower2RSum = 0;

            for (Map.Entry<Double, Double> xy : xySet) {
                xPower2RSum += (xy.getKey() * xy.getKey() * xy.getValue());
            }

            return xPower2RSum / xySet.size();
        }
    }
}
