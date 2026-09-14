package quarter2_Minipeta3;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Hardcoded credentials for demonstration

        final String CORRECT_USERNAME = "Student";

        final String CORRECT_PASSWORD = "Student123";

        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Java Console Login ---");

        System.out.print("Enter Username: ");

        String inputUsername = scanner.nextLine();

        System.out.print("Enter Password: ");

        String inputPassword = scanner.nextLine();

        // Validate the credentials using .equals()

        if (inputUsername.equals(CORRECT_USERNAME) && inputPassword.equals(CORRECT_PASSWORD)) {

            System.out.println("Login Successful! Welcome.");

        } else {

            System.out.println("Login Failed! Invalid credentials.");

        }

        scanner.close();

    }

}
public class hernandez_minipeta3 {
}
