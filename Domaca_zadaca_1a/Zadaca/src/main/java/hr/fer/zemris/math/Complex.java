package hr.fer.zemris.math;

import java.util.LinkedList;
import java.util.List;

public class Complex {
    private double re, im;

    public static final Complex ZERO = new Complex(0,0);
    public static final Complex ONE = new Complex(1,0);
    public static final Complex ONE_NEG = new Complex(-1,0);
    public static final Complex IM = new Complex(0,1);
    public static final Complex IM_NEG = new Complex(0,-1);

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double module() {
        return Math.sqrt(re * re + im * im);
    }

    public Complex multiply(Complex c) {
        return new Complex( re * c.re - im * c.im, re * c.im + im * c.re);
    }

    public Complex add(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex sub(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex divide(Complex c) {
        Complex brojnik = multiply(new Complex(c.re, -c.im));
        double nazivnik = c.re * c.re + c.im*c.im;
        brojnik.re = brojnik.re/nazivnik;
        brojnik.im = brojnik.im/nazivnik;
        return brojnik;
    }

    public Complex negate() {
        return new Complex(-re, -im);
    }

    private double toTheta(double re, double im) {
        if (re == 0) {
            if (im == 0) return 0;// 0 + 0i
            if (im > 0) return 90;// 0 + i
            return 270;// 0 + -i
        }
        if (im == 0 && re < 0) return 180; // 1 + 0i
        double theta = Math.toDegrees(Math.atan(im/re));
        if (re > 0 && im > 0) return theta; // 1. kvadrant
        if (re < 0 && im > 0) return 180 - theta; // 2. kvadrant
        if (re < 0 && im < 0) return 180 + theta;
        return 360 - theta; // 4. kvadrant
    }

    private Complex toComplex(double modul, double theta) {
        theta = Math.toRadians(theta);
        double re = modul * Math.cos(theta), im = modul * Math.sin(theta);
        return new Complex(re, im);
    }

    public Complex power(int n) {
        if (n < 0) throw new IllegalArgumentException("Exponent must be bigger than 1");
        if (n == 0) return Complex.ONE;
        Complex complex = Complex.ONE;
        for (int i = 0; i < n; i++) {
            complex = complex.multiply(this);
        }
        return complex;
    }

    public List<Complex> root(int n) {
        if (n < 1) throw new IllegalArgumentException("Square root must be bigger than 0");
        double modul = module(), theta = toTheta(re, im);
        modul = Math.pow(modul, 1.0/n);
        List<Complex> complexList = new LinkedList<>();
        for (int k = 0; k < n; k++) {
            complexList.add(toComplex(modul, (theta + 360 * k)/n));
        }
        return complexList;
    }

    @Override
    public String toString() {
        if (im < 0) return String.format("%.1f-i%.1f",re,-im);
        return String.format("%.1f+i%.1f",re,im);
    }


}

