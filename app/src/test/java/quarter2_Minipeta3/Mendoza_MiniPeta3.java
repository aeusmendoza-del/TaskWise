package quarter2_Minipeta3;

import java.util.Scanner;

    class Main {

    public Main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        // Registration ng student

        System.out.println("===== STUDENT REGISTRATION =====");

        System.out.print("Enter Student Name: ");
        String studentName = scanner.nextLine();

        System.out.print("Enter Section: ");
        String section = scanner.nextLine();

        System.out.print("Enter Grade Level: ");
        String gradeLevel = scanner.nextLine();

        System.out.print("Create Username: ");
        String registeredUsername = scanner.nextLine();

        System.out.print("Create Password: ");
        String registeredPassword = scanner.nextLine();

        System.out.println("\nRegistration Successful!");
        System.out.println("--------------------------------");

        // =========================
        // Login ng student
        // dapat kung ano ung nilagay na username ng student sa registration
        // tugma sa login
        // =========================
        System.out.println("\n===== STUDENT LOGIN =====");

        System.out.print("Username: ");
        String inputUsername = scanner.nextLine();

        System.out.print("Password: ");
        String inputPassword = scanner.nextLine();

        // Validate Login

        // kukunin nung systemprintout ung student name,section at grade level
        // para ma verify ung student kung tugma sa username na ininput nya sa registration
        if (inputUsername.equals(registeredUsername)
                && inputPassword.equals(registeredPassword)) {

            System.out.println("\n=================================");
            System.out.println("       LOGIN SUCCESSFUL!");
            System.out.println("=================================");
            System.out.println("Welcome, " + studentName + "!");
            System.out.println("Section     : " + section);
            System.out.println("Grade Level : " + gradeLevel);
            System.out.println("=================================");

        } else {

            System.out.println("\nLogin Failed!");
            System.out.println("Invalid Username or Password.");

        }

        scanner.close();
    }
}
