package quarter2_Minipeta3;

import java.util.Scanner;


// Main class of the program
public class Bonacruz_Minipeta3 {

    private Bonacruz_Minipeta3() {
    }

    public static void main(String[] args) {

        // Create Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        /* Display
           the program
           title */
        System.out.println("====================");
        System.out.println("=== Task Status ===");
        System.out.println("====================");
        System.out.println("Hello! Good Day!.");

        // Ask the user to enter the task name
        System.out.print("Please Enter Your Task Name Here: ");
        String task = scanner.nextLine();

        // Ask the user to enter the task status
        System.out.print("Enter Task Status (Pending/In Progress/Completed): ");
        String status = scanner.nextLine();

        // Display the task information
        System.out.println();
        System.out.println("\n--- Task Information ---");
        System.out.println("-------------------------");
        System.out.println("Task: " + task);
        System.out.println("Status: " + status);
        System.out.println("-------------------------");

        /* Check the task status
           and display the
           appropriate message */
        if (status.equalsIgnoreCase("Completed")) {
            System.out.println("Ayan Very Good!, Bigyan Ng Chocolate Yan!.");
        } else if (status.equalsIgnoreCase("In Progress")) {
            System.out.println("Pagpatuloy Molang!.");
        } else {
            System.out.println("Galingan Mo Naman Sa Susunod.");
        }

        // Close the Scanner
        scanner.close();
    }

}
