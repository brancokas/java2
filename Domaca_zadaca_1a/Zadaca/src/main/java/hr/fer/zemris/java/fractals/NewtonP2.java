package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.math.Complex;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static hr.fer.zemris.java.fractals.NewtonP1.parseToRootedPolynomial;
import static hr.fer.zemris.java.fractals.NewtonP1.toComplex;

public class NewtonP2 {
    public static void main(String[] args) {
        Integer mintracks = parseArguments(args);
        if (mintracks == null) {
            System.out.println("greska u unosu.");
            return;
        }

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

        FractalViewer.show(new MojProducer2(mintracks, parseToRootedPolynomial(complexes)));
    }

    private static Integer parseArguments(String[] args) {
        Integer mintracks = null;
        boolean m = false;
        if (args.length == 2) {
            if (!args[0].equals("-m"))
                return null;
            mintracks = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            if (!args[0].startsWith("--mintracks="))
                return null;
            mintracks = Integer.parseInt(args[0].substring(args[0].indexOf('=')));
        } else if (args.length > 2) {
            return null;
        } else {
            mintracks = 16;
        }
        return mintracks;
    }
}
