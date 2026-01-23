package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleHelper {
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }


    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input. Please enter a valid number.");
            }
        }
    }


    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf(" Please enter a number between %d and %d.%n", min, max);
        }
    }


    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }


    public static LocalDate readDate(String prompt, LocalDate defaultValue) {
        System.out.print(prompt + " (YYYY-MM-DD, press Enter for " + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println(" Invalid date format. Using default: " + defaultValue);
            return defaultValue;
        }
    }


    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" Invalid input. Please enter a valid number.");
            }
        }
    }


    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf(" Please enter a number between %.1f and %.1f.%n", min, max);
        }
    }

    public static boolean confirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    public static void printHeader(String title) {
        clearScreen();
        System.out.println("╔" + "═".repeat(78) + "╗");
        System.out.printf("║ %-76s ║%n", title);
        System.out.println("╚" + "═".repeat(78) + "╝");
    }

    public static void printMenu(String[] options) {
        System.out.println("\n" + "─".repeat(80));
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%2d. %s%n", i + 1, options[i]);
        }
        System.out.println("─".repeat(80));
    }

    public static <T> void printTable(List<T> items, String header) {
        if (items.isEmpty()) {
            System.out.println("\n No items found.");
            return;
        }

        System.out.println("\n" + "─".repeat(80));
        System.out.println(header + " (" + items.size() + " items)");
        System.out.println("─".repeat(80));

        for (T item : items) {
            System.out.println(item);
        }

        System.out.println("─".repeat(80));
    }

    public static <T> T selectFromList(List<T> items, String prompt) {
        if (items.isEmpty()) {
            System.out.println(" No items available.");
            return null;
        }

        System.out.println("\n" + "─".repeat(80));
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("%2d. %s%n", i + 1, items.get(i).toString());
        }
        System.out.println("─".repeat(80));

        int choice = readInt(prompt, 1, items.size()) - 1;
        return items.get(choice);
    }

    public static void showProgressBar(int current, int total, int width) {
        int progress = (int) ((double) current / total * width);
        System.out.print("\r[");
        for (int i = 0; i < width; i++) {
            System.out.print(i < progress ? "=" : " ");
        }
        System.out.printf("] %d/%d", current, total);
        if (current == total) {
            System.out.println();
        }
    }
}