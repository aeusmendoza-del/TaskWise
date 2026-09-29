package quarter2.minipeta3;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("====================");
        System.out.println("=== Task Status ===");
        System.out.println("====================");
        System.out.println("Hello! Good Day!");

        // Ask for task name
        System.out.print("Please Enter Your Task Name Here: ");
        String task = scanner.nextLine();

        // Ask for task status
        System.out.print("Enter Task Status (Pending/In Progress/Completed): ");
        String status = scanner.nextLine();

        // Display task information
        System.out.println();
        System.out.println("--- Task Information ---");
        System.out.println("-------------------------");
        System.out.println("Task: " + task);
        System.out.println("Status: " + status);
        System.out.println("-------------------------");

        // Check task status
        if (status.equalsIgnoreCase("Completed")) {
            System.out.println("Ayan, Very Good! Bigyan ng Chocolate Yan!");
        }
        else if (status.equalsIgnoreCase("In Progress")) {
            System.out.println("GO!");
        }
        else if (status.equalsIgnoreCase("Pending")) {
            System.out.println("Better Luck Next Time.");
        }
        else {
            System.out.println("Invalid task status.");
        }

        scanner.close();
    }
}