package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ComplexPolynomial {
    private List<Complex> koeficijenti;

    public ComplexPolynomial(Complex ...factors) {
        koeficijenti = new LinkedList<>();
        for (Complex factor : factors)
            koeficijenti.add(factor);
    }

    private ComplexPolynomial(List<Complex> list) {
        koeficijenti = list;
    }

    public short order() {
        return (short)(koeficijenti.size()-1);
    }

    private void add(List<Complex> koeficijenti, Complex prvi, Complex drugi, int index) {
        if (koeficijenti.size() <= index) {
            koeficijenti.add(index, prvi.multiply(drugi));
        } else {
            koeficijenti.set(index, koeficijenti.get(index).add(prvi.multiply(drugi)));
        }
    }
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        List<Complex> noviKoeficijenti = new ArrayList<>(order() + p.order() + 2);
        for (int i = 0; i < p.koeficijenti.size(); i++) {
            Complex pKoef = p.koeficijenti.get(i);
            for (int j = 0; j < koeficijenti.size(); j++) {
                add(noviKoeficijenti, pKoef, koeficijenti.get(j), i+j);
            }
        }
        return new ComplexPolynomial(noviKoeficijenti);
    }
    public ComplexPolynomial derive() {
        List<Complex> derivate = new ArrayList<>(koeficijenti.size()-1);
        for (int i = 1; i < koeficijenti.size(); i++) {
            derivate.add(koeficijenti.get(i).multiply(new Complex(i,0)));
        }
        return new ComplexPolynomial(derivate);
    }

    public Complex apply(Complex z) {
        Complex complex = Complex.ZERO;
        for (int i = 0; i < koeficijenti.size(); i++) {
            Complex koef = koeficijenti.get(i).multiply(z.power(i));
            complex = complex.add(koef);
        }
        return complex;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = koeficijenti.size()-1; i >= 1; i--) {
            stringBuilder.append(String.format("(%s)*z^%d+",koeficijenti.get(i),i));
        }
        if (!koeficijenti.isEmpty())
            stringBuilder.append('(' + koeficijenti.get(0).toString() + ')');
        return stringBuilder.toString();
    }
}
