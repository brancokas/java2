package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class NewtonP1 {

    public static void main(String[] args) {

        Pair pair = parseArguments(args);

        if (pair == null)
            return;

        Integer workers = pair.getWorkers(), tracks = pair.getTracks();

        Scanner scanner = new Scanner(System.in);
        List<Complex> complexes = new LinkedList<>();
        int broj = 1;
        while (true) {
            System.out.print("Root " + broj + "> ");
            String ulaz = scanner.nextLine();
            if (ulaz.equals("done")) {
                if (complexes.size() < 2) {
                    System.out.println("Morate unjeti barem dvije nultocke");
                    continue;
                }
                break;
            }
            Complex c = toComplex(ulaz);
            complexes.add(c);
            broj++;
        }
        System.out.print("Image of the fractal will appear shortly. Thank you.");
        scanner.close();

        FractalViewer.show(new MojProducer(workers, tracks, parseToRootedPolynomial(complexes)));

    }

    public static ComplexRootedPolynomial parseToRootedPolynomial(List<Complex> complexList) {
        Complex[] complexes = new Complex[complexList.size()];
        for (int i = 0; i < complexList.size(); i++) {
            complexes[i] = complexList.get(i);
        }
        return new ComplexRootedPolynomial(Complex.ONE, complexes);
    }

    public static Pair parseArguments(String[] args) {
        Integer workers = null, tracks = null;
        boolean w = false, t = false;
        for (String arg : args) {
            if (arg.startsWith("--workers=")) {
                if (workers != null) {
                    System.out.println("Input workers once.");
                    return null;
                }
                workers = Integer.valueOf(arg.substring(arg.indexOf('=')));
            } else if (arg.startsWith("--tracks=")) {
                if (tracks != null) {
                    System.out.println("Input tracks once.");
                    return null;
                }
                tracks = Integer.valueOf(arg.substring(arg.indexOf('=')));
            } else if (arg.equals("-w")) {
                if (w) {
                    System.out.println("Wrong input.");
                    return null;
                }
                w = true;
            } else if (arg.equals("-t")) {
                if (t) {
                    System.out.println("Wrong input.");
                    return null;
                }
                t = true;
            } else if (w) {
                if (workers != null) {
                    System.out.println("Input workers once.");
                    return null;
                }
                w = false;
                workers = Integer.parseInt(arg);
            } else if (t) {
                if (tracks != null) {
                    System.out.println("Input workers once.");
                    return null;
                }
                t = false;
                tracks = Integer.parseInt(arg);
            }
        }

        if (w || t) {
            System.out.println("Wrong input of arguments.");
            return null;
        }

        if (workers == null) {
            workers = Integer.valueOf(Runtime.getRuntime().availableProcessors());
        }

        if (tracks == null) {
            tracks = Integer.valueOf(4 * Runtime.getRuntime().availableProcessors());
        } else if (tracks.intValue() < 1) {
            System.out.println("Wrong input for tracks.");
            return null;
        }

        return new Pair(workers, tracks);

    }
    private static boolean isDigit(char znak) {
        return znak >= '0' && znak <= '9';
    }

    private static boolean isBlank(char znak) {
        return znak == ' ' || znak == '\t';
    }

    public static Complex toComplex(String ulaz) {
        Double re = null, im = null;
        boolean complex = false, uBroju = false, imaPredznak = false, tocka = false;
        String broj = "";
        for (int i = 0; i < ulaz.length(); i++) {
            char znak = ulaz.charAt(i);
            if (isDigit(znak)) {
                tocka = false;
                uBroju = true;
                broj += znak;
            } else if (znak == '-' || znak == '+') {
                if (imaPredznak && !uBroju)
                    throw new IllegalArgumentException("Broj ne moze imati vise od jednog predznaka.");
                if (tocka) throw new IllegalArgumentException("Morate unjet valjan decimalni broj.");
                if (uBroju) {
                    if (!complex)
                        re = Double.valueOf(broj);
                    else {
                        if (broj.equals("-"))
                            im = Double.valueOf(-1);
                        else if (broj.equals("+") || broj.isEmpty())
                            im = Double.valueOf(1);
                        else
                            im = Double.valueOf(broj);
                    }
                    broj = "";
                }
                complex = false;
                uBroju = false;
                imaPredznak = true;
                broj += znak;
            } else if (znak == 'i') {
                if (tocka) throw new IllegalArgumentException("Unesite valjani decimalni broj.");
                complex = true;
            }
            else if (isBlank(znak)) {
                if (tocka) throw new IllegalArgumentException("Unesite valjani decimalni broj.");
                if (uBroju) {
                    if (!complex)
                        re = Double.valueOf(broj);
                    else {
                        if (broj.equals("-"))
                            im = Double.valueOf(-1);
                        else if (broj.equals("+") || broj.isEmpty())
                            im = Double.valueOf(1);
                        else
                            im = Double.valueOf(broj);
                    }
                    imaPredznak = false;
                    complex = false;
                    uBroju = false;
                    broj = "";
                }
            } else if (znak == '.') {
                if (tocka) throw new IllegalArgumentException("Krivi unos broja.");
                if (!uBroju) throw new IllegalArgumentException("Unesite ispravan decimalni broj.");
                tocka = true;
                broj += znak;
            } else
                throw new IllegalArgumentException("Unesen je ne dozvoljeni charachter.");
        }
        if (!broj.isEmpty()) {
            if (!complex) {
                if (broj.equals("+") || broj.equals("-"))
                    throw new IllegalArgumentException("Morate unjeti ispravan broj");
                re = Double.valueOf(broj);
            }
            else {
                if (broj.equals("-"))
                    im = Double.valueOf(-1);
                else if (broj.equals("+"))
                    im = Double.valueOf(1);
                else
                    im = Double.valueOf(broj);
            }
        } else if (complex) {
            im = Double.valueOf(1);
        }
        double rem = re == null ? 0 : re.doubleValue(),
                imm = im == null ? 0 : im.doubleValue();
        return new Complex(rem, imm);
    }


    public static void calculate(double reMin, double reMax, double imMin, double imMax, int width, int height,
                                 int ymin, int ymax, short[] data, AtomicBoolean cancel,
                                 ComplexRootedPolynomial polynomial, double rootTreshold,
                                 double convergenceTreshold) {

        int offset = ymin * width;
        for(int y = ymin; y <= ymax && !cancel.get(); ++y) {
            for (int x = 0; x < width; ++x) {

                double cre = x / (width-1.0) * (reMax - reMin) + reMin;
                double cim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;

                Complex zn =  new Complex(cre, cim);
                double module = 0.0;
                int iters = 0, maxIter = 500;
                ComplexPolynomial derivate = polynomial.toComplexPolynom().derive();
                do {
                    Complex numerator = polynomial.apply(zn);
                    Complex denominator = derivate.apply(zn);
                    Complex znold = zn;
                    Complex fraction = numerator.divide(denominator);
                    zn = zn.sub(fraction);
                    module = znold.sub(zn).module();
                    iters++;
                } while (iters < maxIter && module > convergenceTreshold);
                data[offset++] = (short)(polynomial.indexOfClosestRootFor(zn, rootTreshold)+1);
            }
        }
    }

}
