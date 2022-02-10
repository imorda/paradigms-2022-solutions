package expression;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(new Add(
                    new Subtract(
                            new Multiply(new Variable("x"), new Variable("x")),
                            new Multiply(new Const(2), new Variable("x"))
                    ),
                    new Const(1)
            ).evaluate(scanner.nextInt()));
        } catch (InputMismatchException e) {
            System.err.println("Wrong input format. Please enter a number. " + e.getMessage());
        } catch (NoSuchElementException | IllegalStateException e) {
            System.err.println("Error reading from stdin. " + e.getMessage());
        }
    }
}