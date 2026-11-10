package taskwise;

import com.example.taskwise.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.security.MessageDigest;
public class login_taskwise extends AppCompatActivity {
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_login);
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        SharedPreferences savedUser = getSharedPreferences("taskwise_user", MODE_PRIVATE);
        usernameInput.setText(savedUser.getString("username", ""));
        emailInput.setText(savedUser.getString("email", ""));
        Button loginButton = findViewById(R.id.loginButton);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        TextView createAccountText = findViewById(R.id.createAccountText);
        loginButton.setOnClickListener(view -> login());
        forgotPassword.setOnClickListener(view -> showResetPasswordDialog());
        createAccountText.setOnClickListener(view -> showCreateAccountDialog());
    }
    private void login() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Enter your username");
            usernameInput.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            emailInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Enter your password");
            passwordInput.requestFocus();
            return;
        }
        SharedPreferences user = getSharedPreferences("taskwise_user", MODE_PRIVATE);
        String savedName = user.getString("username", "");
        String savedEmail = user.getString("email", "");
        String savedHash = user.getString("password_hash", "");
        if (savedEmail.isEmpty()) {
            Toast.makeText(this, "Create a local demo account first.", Toast.LENGTH_LONG).show();
            return;
        }
        if (savedHash.isEmpty()) {
            Toast.makeText(this, "Use Forgot Password to set a password for this older local account.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!email.equalsIgnoreCase(savedEmail) || !username.equalsIgnoreCase(savedName)
                || !hashPassword(password).equals(savedHash)) {
            passwordInput.setError("The username, email, or password does not match this device");
            passwordInput.requestFocus();
            return;
        }
        Intent intent = new Intent(this, taskwise_home.class);
        intent.putExtra("username", savedName);
        startActivity(intent);
        finish();
    }
    private void showCreateAccountDialog() {
        LinearLayout form = createForm();
        EditText name = input("Username", InputType.TYPE_CLASS_TEXT);
        EditText email = input("Email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText password = input("Password (at least 6 characters)",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        form.addView(name);
        form.addView(email);
        form.addView(password);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Create Local Demo Account")
                .setMessage("Account details stay on this device. This prototype has no online sign-in.")
                .setView(form).setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String username = name.getText().toString().trim();
            String address = email.getText().toString().trim();
            String secret = password.getText().toString();
            if (username.isEmpty()) {
                name.setError("Enter a username");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                email.setError("Enter a valid email address");
                return;
            }
            if (secret.length() < 6) {
                password.setError("Use at least 6 characters");
                return;
            }
            getSharedPreferences("taskwise_user", MODE_PRIVATE).edit()
                    .putString("username", username)
                    .putString("email", address)
                    .putString("password_hash", hashPassword(secret))
                    .apply();
            usernameInput.setText(username);
            emailInput.setText(address);
            passwordInput.setText("");
            dialog.dismiss();
            Toast.makeText(this, "Account saved. Log in with these details.", Toast.LENGTH_LONG).show();
        }));
        dialog.show();
    }
    private void showResetPasswordDialog() {
        LinearLayout form = createForm();
        EditText email = input("Saved account email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText password = input("New password (at least 6 characters)",
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        form.addView(email);
        form.addView(password);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset Local Password")
                .setMessage("This changes the password stored on this device. No email will be sent.")
                .setView(form).setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String address = email.getText().toString().trim();
            String saved = getSharedPreferences("taskwise_user", MODE_PRIVATE).getString("email", "");
            String secret = password.getText().toString();
            if (saved.isEmpty() || !address.equalsIgnoreCase(saved)) {
                email.setError("Enter the email saved on this device");
                return;
            }
            if (secret.length() < 6) {
                password.setError("Use at least 6 characters");
                return;
            }
            getSharedPreferences("taskwise_user", MODE_PRIVATE).edit()
                    .putString("password_hash", hashPassword(secret)).apply();
            emailInput.setText(address);
            passwordInput.setText("");
            dialog.dismiss();
            Toast.makeText(this, "Local password updated.", Toast.LENGTH_SHORT).show();
        }));
        dialog.show();
    }
    private LinearLayout createForm() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        form.setPadding(pad, pad, pad, 0);
        return form;
    }
    private EditText input(String hint, int type) {
        EditText value = new EditText(this);
        value.setHint(hint);
        value.setSingleLine(true);
        value.setInputType(type);
        return value;
    }
    private String hashPassword(String password) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"));
            StringBuilder value = new StringBuilder();
            for (byte b : digest) value.append(String.format("%02x", b & 0xff));
            return value.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
