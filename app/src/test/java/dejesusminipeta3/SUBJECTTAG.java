package dejesusminipeta3;

import java.util.Scanner;

public class SUBJECTTAG {

    import java.util.Scanner;

    public class Main {

        public static void main(String[] args) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("===== SUBJECT TAG SYSTEM =====");

            System.out.print("Enter Student Name: ");
            String studentName = scanner.nextLine();

            System.out.print("Enter Subject: ");
            String subject = scanner.nextLine();

            System.out.print("Enter Subject Code: ");
            String subjectCode = scanner.nextLine();

            System.out.println("\n----- SUBJECT TAG -----");
            System.out.println("Student Name : " + studentName);
            System.out.println("Subject      : " + subject);
            System.out.println("Subject Code : " + subjectCode);

            if (subjectCode.startsWith("ICT")) {
                System.out.println("Status       : Valid Subject Tag");
            } else {
                System.out.println("Status       : Invalid Subject Tag");
            }

            scanner.close();
        }
    }
}
