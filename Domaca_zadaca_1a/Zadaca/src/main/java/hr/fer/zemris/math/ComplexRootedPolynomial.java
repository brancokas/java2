package hr.fer.zemris.math;

import java.util.LinkedList;
import java.util.List;

public class ComplexRootedPolynomial {
    private Complex constant;
    private List<Complex> nultocke;

    public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
        this.constant = constant;
        nultocke = new LinkedList<>();
        for (Complex root : roots) {
            nultocke.add(root);
        }
    }

    public Complex apply(Complex z) {
        Complex f = constant;
        for (Complex nultocka : nultocke) {
            f = f.multiply(z.sub(nultocka));
        }
        return f;
    }

    public ComplexPolynomial toComplexPolynom() {
        ComplexPolynomial polynomial = new ComplexPolynomial(constant);
        for (Complex nultocka : nultocke) {
            ComplexPolynomial complexPolynomial = new ComplexPolynomial(nultocka.negate(), Complex.ONE);
            polynomial = polynomial.multiply(complexPolynomial);
        }
        return polynomial;
    }

    public int indexOfClosestRootFor(Complex z, double treshold) {
        int index = -1;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < nultocke.size(); i++) {
            Complex z2 = nultocke.get(i);
            double razlika = z2.sub(z).module();
            if (razlika < min && razlika <= treshold) {
                min = razlika;
                index=i;
            }
        }
        return index;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(String.format("(%s)",constant));
        for (Complex nultocka : nultocke) {
            stringBuilder.append(String.format("*(z-(%s))",nultocka));
        }
        return stringBuilder.toString();
    }

}
