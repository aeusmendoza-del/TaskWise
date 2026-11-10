package taskwise;

import com.example.taskwise.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class taskwise_profile extends AppCompatActivity {
    private TextView profileName;
    private TextView profileEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskwise_profile);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        refreshProfile();
        findViewById(R.id.editProfileButton).setOnClickListener(v -> showEditProfile());
        findViewById(R.id.profilePasswordButton).setOnClickListener(v -> showChangePassword());
        findViewById(R.id.profileBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.profileLogoutButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, login_taskwise.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        bindNavigation();
    }
    @Override protected void onResume() { super.onResume(); if (profileName != null) refreshProfile(); }
    private void refreshProfile() {
        SharedPreferences user = getSharedPreferences("taskwise_user", MODE_PRIVATE);
        profileName.setText(user.getString("username", "TaskWise User"));
        profileEmail.setText(user.getString("email", ""));
    }
    private void showEditProfile() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        form.setPadding(pad, pad, pad, 0);
        EditText name = new EditText(this);
        name.setHint("Username");
        name.setSingleLine(true);
        name.setText(profileName.getText());
        EditText email = new EditText(this);
        email.setHint("Email");
        email.setSingleLine(true);
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setText(profileEmail.getText());
        form.addView(name);
        form.addView(email);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Edit Profile")
                .setMessage("These details are saved locally on this device.")
                .setView(form).setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String userName = name.getText().toString().trim();
            String address = email.getText().toString().trim();
            if (userName.isEmpty()) {
                name.setError("Enter a username");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
                email.setError("Enter a valid email address");
                return;
            }
            getSharedPreferences("taskwise_user", MODE_PRIVATE).edit()
                    .putString("username", userName).putString("email", address).apply();
            refreshProfile();
            dialog.dismiss();
            Toast.makeText(this, "Profile saved.", Toast.LENGTH_SHORT).show();
        }));
        dialog.show();
    }
    private void showChangePassword() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (18 * getResources().getDisplayMetrics().density);
        form.setPadding(pad, pad, pad, 0);
        EditText current = passwordInput("Current password");
        EditText next = passwordInput("New password (at least 6 characters)");
        EditText confirm = passwordInput("Confirm new password");
        form.addView(current);
        form.addView(next);
        form.addView(confirm);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Change password")
                .setMessage("The new password will be saved on this device.")
                .setView(form).setNegativeButton("Cancel", null).setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            SharedPreferences user = getSharedPreferences("taskwise_user", MODE_PRIVATE);
            String savedHash = user.getString("password_hash", "");
            String currentValue = current.getText().toString();
            String nextValue = next.getText().toString();
            if (!savedHash.isEmpty() && !hashPassword(currentValue).equals(savedHash)) {
                current.setError("Current password does not match");
                return;
            }
            if (nextValue.length() < 6) {
                next.setError("Use at least 6 characters");
                return;
            }
            if (!nextValue.equals(confirm.getText().toString())) {
                confirm.setError("Passwords do not match");
                return;
            }
            user.edit().putString("password_hash", hashPassword(nextValue)).apply();
            dialog.dismiss();
            Toast.makeText(this, "Password updated.", Toast.LENGTH_SHORT).show();
        }));
        dialog.show();
    }
    private EditText passwordInput(String hint) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setSingleLine(true);
        field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return field;
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
    private void bindNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> finishTo(taskwise_home.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> {});
        findViewById(R.id.navSubjects).setOnClickListener(v -> startActivity(new Intent(this, taskwise_subjects.class)));
        findViewById(R.id.navTasks).setOnClickListener(v -> startActivity(new Intent(this, taskwise_deadline.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> startActivity(new Intent(this, taskwise_reminder.class)));
    }
    private void finishTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
