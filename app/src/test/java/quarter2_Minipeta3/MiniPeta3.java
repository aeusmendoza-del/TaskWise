package quarter2_Minipeta3;

import java.util.Scanner;

public class MiniPeta3 {

    static Scanner input = new Scanner(System.in);

    static String studentUsername = "";
    static String studentPassword = "";

    static String teacherUsername = "";
    static String teacherPassword = "";

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n==============================");
            System.out.println("        MAIN MENU");
            System.out.println("==============================");
            System.out.println("1. Student Register");
            System.out.println("2. Teacher Register");
            System.out.println("3. Student Login");
            System.out.println("4. Teacher Login");
            System.out.println("5. Exit");
            System.out.println("==============================");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    studentRegister();
                    break;
                case 2:
                    teacherRegister();
                    break;
                case 3:
                    studentLogin();
                    break;
                case 4:
                    teacherLogin();
                    break;
                case 5:
                    System.out.println("\nThank you for using the system!");
                    break;
                default:
                    System.out.println("\nInvalid choice! Try again.");
            }
        } while (choice != 5);

        input.close();
    }

    // STUDENT REGISTRATION
    public static void studentRegister() {
        System.out.println("\n===== STUDENT REGISTRATION =====");

        System.out.print("Create Student Username: ");
        studentUsername = input.nextLine();

        System.out.print("Create Student Password: ");
        studentPassword = input.nextLine();

        System.out.println("\nStudent registration successful!");
    }

    // TEACHER REGISTRATION
    public static void teacherRegister() {
        System.out.println("\n===== TEACHER REGISTRATION =====");

        System.out.print("Create Teacher Username: ");
        teacherUsername = input.nextLine();

        System.out.print("Create Teacher Password: ");
        teacherPassword = input.nextLine();

        System.out.println("\nTeacher registration successful!");
    }

    // STUDENT LOGIN
    public static void studentLogin() {
        System.out.println("\n===== STUDENT LOGIN =====");

        System.out.print("Enter Student Username: ");
        String username = input.nextLine();

        System.out.print("Enter Student Password: ");
        String password = input.nextLine();

        if (username.equals(studentUsername) && password.equals(studentPassword) && !studentUsername.isEmpty()) {
            System.out.println("\nStudent Login Successful!");
            studentMenu();
        } else {
            System.out.println("\nInvalid Student Username or Password!");
        }
    }

    // TEACHER LOGIN
    public static void teacherLogin() {
        System.out.println("\n===== TEACHER LOGIN =====");

        System.out.print("Enter Teacher Username: ");
        String username = input.nextLine();

        System.out.print("Enter Teacher Password: ");
        String password = input.nextLine();

        if (username.equals(teacherUsername) && password.equals(teacherPassword) && !teacherUsername.isEmpty()) {
            System.out.println("\nTeacher Login Successful!");
            teacherMenu();
        } else {
            System.out.println("\nInvalid Teacher Username or Password!");
        }
    }

    // STUDENT FEATURES
    public static void studentMenu() {
        int choice;

        do {
            System.out.println("\n==============================");
            System.out.println("       STUDENT MENU");
            System.out.println("==============================");
            System.out.println("1. View Assignments");
            System.out.println("2. View Grades");
            System.out.println("3. Submit Assignment");
            System.out.println("4. View Schedule");
            System.out.println("5. Logout");
            System.out.println("==============================");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("\nViewing Student Assignments...");
                    break;
                case 2:
                    System.out.println("\nViewing Student Grades...");
                    break;
                case 3:
                    System.out.println("\nSubmitting Assignment...");
                    break;
                case 4:
                    System.out.println("\nViewing Student Schedule...");
                    break;
                case 5:
                    System.out.println("\nLogging out of Student account...");
                    break;
                default:
                    System.out.println("\nInvalid choice! Try again.");
            }
        } while (choice != 5);
    }

    // TEACHER FEATURES
    public static void teacherMenu() {
        int choice;

        do {
            System.out.println("\n==============================");
            System.out.println("       TEACHER MENU");
            System.out.println("==============================");
            System.out.println("1. Create Assignment");
            System.out.println("2. Grade Students");
            System.out.println("3. View Schedule");
            System.out.println("4. Logout");
            System.out.println("==============================");
            System.out.print("Enter your choice: ");

            choice = input.nextInt();
            input.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("\nCreating New Assignment...");
                    break;
                case 2:
                    System.out.println("\nGrading Student Submissions...");
                    break;
                case 3:
                    System.out.println("\nViewing Teacher Schedule...");
                    break;
                case 4:
                    System.out.println("\nLogging out of Teacher account...");
                    break;
                default:
                    System.out.println("\nInvalid choice! Try again.");
            }
        } while (choice != 4);
    }
}
