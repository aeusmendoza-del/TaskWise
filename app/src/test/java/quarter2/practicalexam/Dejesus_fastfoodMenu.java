package practicalexam;

import java.util.Scanner;

public class Dejesus_fastfoodMenu {

    public static void run(Scanner scan) {

        int choice;
        int quantity;
        double price;
        double total;

        do {
            System.out.println("\n===== FAST FOOD MENU =====");
            System.out.println("1. Burger - P99");
            System.out.println("2. Fried Chicken - P120");
            System.out.println("3. French Fries - P60");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            choice = scan.nextInt();

            if (choice >= 1 && choice <= 3) {

                System.out.print("Enter quantity: ");
                quantity = scan.nextInt();

                if (choice == 1) {
                    price = 99;
                } else if (choice == 2) {
                    price = 120;
                } else {
                    price = 60;
                }

                total = price * quantity;

                System.out.println("Total: P" + total);

            } else if (choice == 4) {
                System.out.println("Thank you!");
            } else {
                System.out.println("Invalid choice.");
            }

        } while (choice != 4);
    }
}