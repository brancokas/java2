package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

import static hr.fer.zemris.java.fractals.NewtonP1.calculate;

public class MojProducer2 implements IFractalProducer {
    private Integer mintracks;
    private ComplexRootedPolynomial polynomial;
    private ForkJoinPool pool;
    public MojProducer2(Integer mintracks, ComplexRootedPolynomial polynomial) {
        this.mintracks = mintracks;
        this.polynomial = polynomial;
    }

    @Override
    public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo,
                        IFractalResultObserver iFractalResultObserver, AtomicBoolean atomicBoolean) {

        int m = 16*16*16;
        short[] data = new short[width * height];

        Posao posao = new Posao(reMin, reMax, imMin, imMax, width, height, 0, height-1, data, atomicBoolean,
                polynomial, mintracks);
        pool.invoke(posao);
        iFractalResultObserver.acceptResult(data, (short)m, requestNo);

    }

    @Override
    public void setup() {
        pool = new ForkJoinPool();
    }

    @Override
    public void close() {
        pool.shutdown();
    }

    public static class Posao extends RecursiveAction {
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
        private int mintracks;

        public Posao(double reMin, double reMax, double imMin, double imMax,
                     int width, int height, int ymin, int ymax, short[] data,
                     AtomicBoolean cancel, ComplexRootedPolynomial polynomial, int mintracks) {
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
            this.mintracks = mintracks;
            this.rootTreshold = 0.001;
            this.convergenceTreshold = 0.002;
        }

        @Override
        protected void compute() {
            if (ymax - ymin <= mintracks) {
                calculate(reMin, reMax, imMin, imMax, width, height, ymin, ymax, data, cancel, polynomial,
                        rootTreshold, convergenceTreshold);
                return;
            }
            Posao p1 = new Posao(reMin, reMax, imMin, imMax, width, height, ymin, (ymax + ymin)/2-1, data, cancel,
                    polynomial, mintracks);
            Posao p2 = new Posao(reMin, reMax, imMin, imMax, width, height, (ymax + ymin)/2, ymax, data, cancel,
                    polynomial, mintracks);
            invokeAll(p1,p2);

        }
    }
}
