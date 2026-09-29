package quarter2.practicalexam;

import java.util.Scanner;

public class ArcadeMenu {

    public void start(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== ARCADE MENU ===");
            System.out.println("Choose an option: ");
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

    private void buyTokensOrder() {
        System.out.println("\nYou selected: Buy Tokens");
    }

    private void claimPrizeOrder(Scanner scanner) {
        System.out.println("\n--- Claim Prize ---");
    }
}