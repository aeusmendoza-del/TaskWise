package quarter2.practicalexam;

import java.util.Scanner;

public class Bonacruz_Arcade {

    // Main system and menu loop
    public void start(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ARCADE MENU ===");
            System.out.println("Choose an option:");
            System.out.println("1. Buy Tokens");
            System.out.println("2. Claim Prize");
            System.out.println("3. Exit");

            if (!scanner.hasNextInt()) {
                break;
            }

            int mainChoice = scanner.nextInt();

            switch (mainChoice) {
                case 1:
                    buyTokensOrder();
                    break;

                case 2:
                    claimPrizeOrder(scanner);
                    break;

                case 3:
                    System.out.println("Exiting arcade. Thank you for playing!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // Buy Tokens
    private void buyTokensOrder() {
        System.out.println("\nYou selected: Buy Tokens");
        System.out.println("Tokens purchased successfully!");
    }

    // Claim Prize
    private void claimPrizeOrder(Scanner scanner) {
        int requiredTickets = 500;

        System.out.println("\n--- Claim Prize ---");
        System.out.println("Required tickets for Teddy Bear: " + requiredTickets);
        System.out.print("Enter ticket count: ");

        if (scanner.hasNextInt()) {
            int tickets = scanner.nextInt();

            if (tickets < requiredTickets) {
                System.out.println("Keep Playing!");
            } else {
                System.out.println("Teddy Bear Won!");
            }
        } else {
            System.out.println("Invalid ticket count.");
            scanner.next();
        }
    }
}