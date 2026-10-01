package practicalexam;

import java.util.Scanner;

public class Dejesus_fastfoodMenu {

    public static void run(Scanner scan) {

        int choice;

        do {
            System.out.println("\n===== FAST FOOD MENU =====");
            System.out.println("1. Burger");
            System.out.println("2. Fried Chicken");
            System.out.println("3. French Fries");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            choice = scan.nextInt();

        } while (choice != 4);
    }
}