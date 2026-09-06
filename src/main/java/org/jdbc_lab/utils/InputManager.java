package org.jdbc_lab.utils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void resetScanner() {
        scanner = new Scanner(System.in);
    }

    public static int getNextInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // очищаем буфер после числа
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Необходимо целое. Попробуй снова: ");
            }
        }
    }

    public static int getNextIntInRange(int min, int max) {
        while (true) {
            int input = getNextInt();
            if (input < min || input > max) {
                System.out.print("Введённое находится за пределами. Попробуй снова: ");
            } else {
                return input;
            }
        }
    }

    public static long getNextLong() {
        while (true) {
            try {
                long value = scanner.nextLong();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Необходимо длинное целое. Попробуй снова: ");
            }
        }
    }

    public static double getNextDouble() {
        while (true) {
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Необходимо вещественное. Попробуй снова: ");
            }
        }
    }

    public static String getNextLine() {
        return scanner.nextLine();
    }

    public static String getNextLineWithSkip() {
        String line = getNextLine();
        return line.equals("-") ? "" : line;
    }
}
