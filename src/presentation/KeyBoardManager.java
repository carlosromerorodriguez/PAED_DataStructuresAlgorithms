package presentation;

import java.util.InputMismatchException;
import java.util.Scanner;

public class KeyBoardManager {
    private static final Scanner scanner = new Scanner(System.in);

    public KeyBoardManager() {}

    public static int askForPositiveInteger(String message, int max) {
        int option = 0;
        do {
            System.out.print(message);
            try {
                option = scanner.nextInt();
                if (option < 1 || option > max) {
                    System.out.println("\033[31mERROR: the entered option is not a valid option!\033[0m\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\033[31mERROR: Please enter an integer.\033[0m\n");
            } finally {
                scanner.nextLine();
            }
        } while (option < 1 || option > max);

        System.out.println();
        return option;
    }

    public static float askForPositiveFloat(String message, float max) {
        float option = 0;
        do {
            System.out.print(message);
            try {
                option = scanner.nextFloat();
                if (option < 0.0001 || option > max) {
                    System.out.println("\033[31mERROR: the entered option is not a valid option!\033[0m\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\033[31mERROR: Please enter a float.\033[0m\n");
            } finally {
                scanner.nextLine();
            }
        } while (option < 0.0001 || option > max);
        return option;
    }

    public static float askForFloat(String message) {
        while (true) {
            System.out.print(message);
            try {
                return scanner.nextFloat();
            } catch (InputMismatchException e) {
                System.out.println("\033[31mERROR: Please enter a float.\033[0m\n");
            } finally {
                scanner.nextLine();
            }
        }
    }


    public static Character askForCharacter(String message, char min, char max) {
        char option = 0;
        do {
            System.out.print(message);
            try {
                option = scanner.next().charAt(0);
                if (option < min  || option > max) {
                    System.out.println("\033[31mERROR: the entered option is not a valid option!\033[0m\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("\033[31mERROR: Please enter a character.\033[0m\n");
            } finally {
                scanner.nextLine();
            }
        } while ((option) < (min)  || (option) > (max));
        System.out.println();
        return option;
    }

    public static boolean askForBoolean(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.next().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("sí")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("\033[31mERROR: Please enter a valid option.\033[0m\n");
            }
        }
    }

    public static String askForType(String msg) {
        while (true) {
            System.out.print(msg);
            String type = scanner.next().trim();
            if (type.equalsIgnoreCase("CIRCLE") || type.equalsIgnoreCase("SQUARE")) {
                return type.toUpperCase();
            } else {
                System.out.println("ERROR: Please enter a valid option. [CIRCLE/SQUARE]\n");
            }
        }
    }

    public static String askForColor(String s) {
        while (true) {
            System.out.print(s);
            String color = scanner.next().trim();
            // #4bf859
            if (color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                return color.toLowerCase();
            } else {
                System.out.println("ERROR: Please enter a valid option.\n");
            }
        }
    }

    public static String askForPoint(String s) {
        while (true) {
            System.out.print(s);
            String point = scanner.next().trim();
            if (point.matches("^-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?$")) {
                return point;
            } else {
                System.out.println("ERROR: Please enter a valid point [latitud,longitud].\n");
            }
        }
    }
}
