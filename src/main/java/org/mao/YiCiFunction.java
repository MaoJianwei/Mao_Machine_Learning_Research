package org.mao;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.valueOf;
import static java.lang.Math.abs;

/**
 * Hello world!
 */
public class YiCiFunction {
    public static void main(String[] args) throws IOException {
        File dataFile = new File("D:\\MaoDev\\Dataset1.csv");
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

        threadPool.execute(new CheckYiCi(xyPair, 0, 160, 0, 10000, 0.0001, 0.001, 0.9));
        threadPool.execute(new CheckYiCi(xyPair, 160, 320, 0, 10000, 0.0001, 0.001, 0.9));
        threadPool.execute(new CheckYiCi(xyPair, 320, 480, 0, 10000, 0.0001, 0.001, 0.9));
        threadPool.execute(new CheckYiCi(xyPair, 480, 640, 0, 10000, 0.0001, 0.001, 0.9));
        threadPool.execute(new CheckYiCi(xyPair, 640, 800, 0, 10000, 0.0001, 0.001, 0.9));
        threadPool.execute(new CheckYiCi(xyPair, 800, 1000, 0, 10000, 0.0001, 0.001, 0.9));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Mao shutdown now ...");
        }
    }

    private static class CheckYiCi implements Runnable {

        final Map datas;
        double aFrom;
        double aTo;
        double bFrom;
        double bTo;
        double accuracy;
        double tolerance;
        double accept;

        CheckYiCi(final Map datas,
                  double aFrom, double aTo, double bFrom, double bTo,
                  double accuracy, double tolerance, double accept) {
            this.datas = datas;
            this.aFrom = aFrom;
            this.aTo = aTo;
            this.bFrom = bFrom;
            this.bTo = bTo;
            this.accuracy = accuracy;
            this.tolerance = tolerance;
            this.accept = accept;
        }

        // y = a x + b
        @Override
        public void run() {
            Set<Map.Entry<Double, Double>> dataSet = datas.entrySet();
            int totalCount = datas.size();
            int currectCount = 0;
            for (double a = aFrom; a <= aTo; a += accuracy) {
                for (double b = bFrom; b <= bTo; b += accuracy) {
                    currectCount = 0;
                    for (Map.Entry<Double, Double> e : dataSet) {
                        if (abs((a * e.getKey() + b) - e.getValue()) < tolerance) {
                            currectCount++;
                        }
                    }
                    if (((double) currectCount / (double) totalCount) > accept) {
                        System.out.println("y = " + a + " x + " + b + " , " + ((double) currectCount / (double) totalCount));
                    }
                }
            }
        }
    }
}
