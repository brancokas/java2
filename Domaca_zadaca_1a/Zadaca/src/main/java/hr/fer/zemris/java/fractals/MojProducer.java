package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static hr.fer.zemris.java.fractals.NewtonP1.calculate;

public class MojProducer implements IFractalProducer {
    private Integer workers;
    private Integer tracks;
    private ComplexRootedPolynomial polynomial;
    private ExecutorService pool;

    public MojProducer(Integer workers, Integer tracks, ComplexRootedPolynomial polynomial) {
        this.workers = workers;
        this.tracks = tracks;
        this.polynomial = polynomial;
    }

    @Override
    public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo,
                        IFractalResultObserver iFractalResultObserver, AtomicBoolean atomicBoolean) {

        if (tracks.intValue() > height)
            tracks = Integer.valueOf(height);

        System.out.println("No of tracks: " + tracks);
        System.out.println("No of workers: " + workers);
        System.out.println("No of available workers: " + Runtime.getRuntime().availableProcessors());

        int m = 16*16*16;
        short[] data = new short[width * height];


        int brojYPoTraci = height / tracks.intValue();
        List<Future<?>> futures = new LinkedList<>();

        for(int i = 0; i < tracks; i++) {
            int yMin = i*brojYPoTraci;
            int yMax = (i+1)*brojYPoTraci-1;
            if(i==tracks-1) {
                yMax = height-1;
            }
            Posao posao =
                    new Posao(reMin, reMax, imMin, imMax, width, height, yMin, yMax, data, atomicBoolean,
                            polynomial);

            futures.add(pool.submit(posao));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }


        System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
        iFractalResultObserver.acceptResult(data, (short)m, requestNo);

    }

    @Override
    public void setup() {
        pool = Executors.newFixedThreadPool(workers.intValue());
    }

    @Override
    public void close() {
        pool.shutdown();
    }


    public static class Posao implements Runnable {
        private double reMin;
        private double reMax;
        private double imMin;
        private double imMax;
        private int width;
        private int height;
        private int ymin;
        private int ymax;
        private short[] data;
        private AtomicBoolean cancel;
        private ComplexRootedPolynomial polynomial;
        private double rootTreshold;
        private double convergenceTreshold;

        public Posao(double reMin, double reMax, double imMin, double imMax,
                     int width, int height, int ymin, int ymax, short[] data,
                     AtomicBoolean cancel, ComplexRootedPolynomial polynomial) {
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.height = height;
            this.ymin = ymin;
            this.ymax = ymax;
            this.data = data;
            this.cancel = cancel;
            this.polynomial = polynomial;
            this.rootTreshold = 0.001;
            this.convergenceTreshold = 0.002;
        }

        @Override
        public void run() {
            calculate(reMin, reMax, imMin, imMax, width, height, ymin, ymax, data, cancel, polynomial,
                    rootTreshold, convergenceTreshold);
        }
    }
}