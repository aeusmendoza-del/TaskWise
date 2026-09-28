package quarter2.practicalexam;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

public class Mendoza_LibraryKioskTest {
    @Test
    public void testLibraryFlow() {
        StringBuilder automatedInput = new StringBuilder();
        System.out.println("--- GENERATING LIBRARY TEST DATA ---");
        // Step 1: Borrow book option
        automatedInput.append("1\n"); // Choose Borrow Book
        // Step 2: Test insufficient fine payment (< 15)
        automatedInput.append("2\n"); // Choose Pay Fines
        automatedInput.append("10\n"); // Enter payment 10 (Expected: Insufficient)
        // Step 3: Test sufficient fine payment (>= 15)
        automatedInput.append("2\n"); // Choose Pay Fines
        automatedInput.append("50\n"); // Enter payment 50 (Expected: Change calculation)
        // Step 4: Exit system
        automatedInput.append("3\n"); // Choose Exit
        System.out.println("--- TEST DATA GENERATION COMPLETE ---\n");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(automatedInput.toString().getBytes());
        Scanner scanner = new Scanner(inputStream);

        // Calls main menu loop
        Mendoza_LibraryKioskTest librarySystem = new Mendoza_LibraryKioskTest();
        librarySystem.start(scanner);
    }

    // main system/while loop and cases
    public void start(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== LIBRARY KIOSK MENU ===");
            System.out.println("Choose an option: ");
            System.out.println("1. Borrow Book");
            System.out.println("2. Pay Fines");
            System.out.println("3. Exit");

            if (!scanner.hasNextInt()) {
                break;
            }

            int mainChoice = scanner.nextInt();

            switch (mainChoice) {
                case 1:
                    borrowBookOrder();
                    break;
                case 2:
                    payFinesOrder(scanner);
                    break;
                case 3:
                    System.out.println("Exiting library kiosk. Thank you!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // routing
    private void borrowBookOrder() {
        System.out.println("\nYou selected: Borrow Book");
        System.out.println("Book borrowed successfully!");
    }

    private void payFinesOrder(Scanner scanner) {
        double fineAmount = 15.0;
        System.out.println("\n--- Pay Fines ---");
        System.out.println("Outstanding fine balance: $" + fineAmount);
        System.out.print("Enter payment amount: ");

        if (scanner.hasNextDouble()) {
            double payment = scanner.nextDouble();

            if (payment < fineAmount) {
                System.out.println("Insufficient payment. You still owe $" + (fineAmount - payment));
            } else {
                double change = payment - fineAmount;
                System.out.println("Payment successful!");
                System.out.println("Change: $" + change);
            }
        } else {
            System.out.println("Invalid amount entered.");
            scanner.next(); // clear invalid input
        }
    }

    // Optional main method to run standalone outside of JUnit
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Mendoza_LibraryKioskTest librarySystem = new Mendoza_LibraryKioskTest();
        librarySystem.start(scanner);
        scanner.close();
    }
}