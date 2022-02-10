package expression.exceptions;

import expression.TripleExpression;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid arguments count, got " + args.length + " arguments");
            return;
        }

        TripleExpression parsed;
        ExpressionParser parser = new ExpressionParser();
        try {
            final String input = Files.readString(Paths.get(args[0]));
            try {
                parsed = parser.parse(input);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                System.out.print("Rest of input:'");
                while (e.getSource().hasNext()) {
                    System.out.write(e.getSource().next());
                }
                System.out.println("'");
                return;
            }
        } catch (IOException e) {
            System.out.println("Could not read from input file: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);

        int x, y, z;
        while (true) {
            try {
                System.out.println("Enter x");
                x = scanner.nextInt();
                System.out.println("Enter y");
                y = scanner.nextInt();
                System.out.println("Enter z");
                z = scanner.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Wrong input format. Enter a number.");
                scanner.next();
            } catch (IllegalStateException | NoSuchElementException e) {
                System.out.println("Input is broken: " + e.getMessage());
                return;
            }
        }

        System.out.printf("toString(): %s%n", parsed.toString());
        System.out.printf("toMiniString(): %s%n", parsed.toMiniString());
        try {
            System.out.printf("evaluate(%d, %d, %d): %s%n", x, y, z, parsed.evaluate(x, y, z));
        } catch (ArithmeticException e) {
            System.out.printf("Error evaluating: %s%n", e.getMessage());
        }
    }
}
