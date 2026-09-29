package quarter2.practicalexam;

import java.util.Scanner;

public class Hernandez_CinemaMenu {

    private static final int MINIMUM_AGE = 18;
    private static final double TICKET_PRICE = 330.00;
    private static final double SNACK_COMBO_PRICE = 270.00;

    private int ticketsSold = 0;
    private int snacksSold = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CinemaMenu cinemaSystem = new CinemaMenu();
        cinemaSystem.start(scanner);
        scanner.close();
    }

    public void start(Scanner scanner) {
        boolean running = true;

        while (running) {
            displayMenu();

            if (!scanner.hasNextLine()) {
                System.out.println("No more input. Exiting system.");
                break;
            }

            String choice = scanner.nextLine().trim();
            System.out.println("Choice: " + choice);

            switch (choice) {
                case "1":
                    buyTicket(scanner);
                    break;
                case "2":
                    buySnacks();
                    break;
                case "3":
                    running = false;
                    printSummary();
                    System.out.println("Thank you for visiting! Goodbye.");
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.\n");
            }
        }
    }

    private void displayMenu() {
        System.out.println("=================================");
        System.out.println("        CINEMA TICKETING         ");
        System.out.println("=================================");
        System.out.println("1. Buy Ticket");
        System.out.println("2. Buy Snacks");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    private void buyTicket(Scanner scanner) {
        System.out.print("Enter your age: ");

        if (!scanner.hasNextLine()) {
            System.out.println("No age entered.\n");
            return;
        }

        String input = scanner.nextLine().trim();
        int age;

        try {
            age = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age entered.\n");
            return;
        }

        System.out.println("Age: " + age);

        if (age < 0) {
            System.out.println("Invalid age entered.\n");
        } else if (age < MINIMUM_AGE) {
            System.out.println("ACCESS DENIED: You must be at least " + MINIMUM_AGE + " years old.\n");
        } else {
            ticketsSold++;
            System.out.println("---------------------------------");
            System.out.println("          CINEMA TICKET          ");
            System.out.println("  Ticket No.: " + String.format("%03d", ticketsSold));
            System.out.println("  Age       : " + age);
            System.out.println("  Price     : PHP " + String.format("%.2f", TICKET_PRICE));
            System.out.println("---------------------------------");
            System.out.println("Ticket printed. Enjoy the movie!\n");
        }
    }

    private void buySnacks() {
        snacksSold++;
        System.out.println("You bought a Snack Combo (popcorn + drink).");
        System.out.println("Price: PHP " + String.format("%.2f", SNACK_COMBO_PRICE) + "\n");
    }

    private void printSummary() {
        double total = (ticketsSold * TICKET_PRICE) + (snacksSold * SNACK_COMBO_PRICE);
        System.out.println("\n--- SALES SUMMARY ---");
        System.out.println("Tickets sold : " + ticketsSold);
        System.out.println("Snacks sold  : " + snacksSold);
        System.out.println("Total sales  : PHP " + String.format("%.2f", total));
    }
}