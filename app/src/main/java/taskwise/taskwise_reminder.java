package taskwise;

import com.example.taskwise.R;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
public class taskwise_reminder extends AppCompatActivity {
    private LinearLayout reminderListContainer;
    private TextView newReminderSummary;
    private Long selectedDateFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_reminder);
        reminderListContainer = findViewById(R.id.reminderListContainer);
        newReminderSummary = findViewById(R.id.newReminderSummary);
        reminderListContainer.removeAllViews();
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.calendarPickerButton).setOnClickListener(v -> pickFilterDate());
        findViewById(R.id.viewAllReminders).setOnClickListener(v -> {
            selectedDateFilter = null;
            renderReminders();
        });
        findViewById(R.id.featuredReminderCard).setOnClickListener(v -> showFeaturedReminder());
        findViewById(R.id.featuredNotificationButton).setOnClickListener(v -> toggleFeaturedReminder());
        findViewById(R.id.addReminderCard).setOnClickListener(v -> showReminderEditor(null));
        findViewById(R.id.addReminderImageButton).setOnClickListener(v -> showReminderEditor(null));
        findViewById(R.id.addReminderArrow).setOnClickListener(v -> showReminderEditor(null));
        bindBottomNavigation();
        renderReminders();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (reminderListContainer != null) renderReminders();
    }
    private void renderReminders() {
        reminderListContainer.removeAllViews();
        ArrayList<TaskWiseStore.Reminder> reminders =
                new ArrayList<>(TaskWiseStore.getReminders(this));
        reminders.sort(Comparator.comparingLong(item -> item.at));
        int shown = 0;
        for (TaskWiseStore.Reminder reminder : reminders) {
            if (selectedDateFilter != null
                    && !TaskWiseStore.sameDay(reminder.at, selectedDateFilter)) continue;
            shown++;
            reminderListContainer.addView(createReminderRow(reminder),
                    new LinearLayout.LayoutParams(-1, dp(68)));
        }
        if (shown == 0) {
            TextView empty = new TextView(this);
            empty.setText(selectedDateFilter == null
                    ? "No reminders yet. Add one to receive a notification."
                    : "No reminders on this date.");
            empty.setTextColor(0xFF738292);
            empty.setTextSize(13);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(16), dp(18), dp(16), dp(18));
            reminderListContainer.addView(empty, new LinearLayout.LayoutParams(-1, -2));
        }
        if (selectedDateFilter != null) {
            newReminderSummary.setText("Showing reminders for "
                    + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(selectedDateFilter));
            newReminderSummary.setVisibility(View.VISIBLE);
            findViewById(R.id.reminderSectionTitle).setContentDescription("Reminders filtered by date");
        } else {
            newReminderSummary.setVisibility(View.GONE);
            findViewById(R.id.reminderSectionTitle).setContentDescription("All reminders");
        }
        updateFeaturedReminder(reminders);
    }
    private void updateFeaturedReminder(List<TaskWiseStore.Reminder> reminders) {
        TaskWiseStore.Reminder next = nextReminder(reminders);
        TextView title = findViewById(R.id.featuredReminderTitle);
        TextView date = findViewById(R.id.featuredReminderDate);
        ImageButton alert = findViewById(R.id.featuredNotificationButton);
        if (next == null) {
            title.setText(reminders.isEmpty() ? "No reminders yet" : "No upcoming reminders");
            date.setText(reminders.isEmpty() ? "Add one to get started" : "Your saved reminders are in the list below");
            alert.setEnabled(false);
            alert.setAlpha(0.45f);
            alert.setContentDescription("No upcoming reminder to toggle");
            return;
        }
        title.setText(next.title);
        date.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault()).format(next.at)
                + (next.enabled ? "  ·  Alert on" : "  ·  Alert off"));
        alert.setEnabled(true);
        alert.setAlpha(next.enabled ? 1f : 0.55f);
        alert.setContentDescription(next.enabled ? "Turn off alert for " + next.title : "Turn on alert for " + next.title);
    }
    private TaskWiseStore.Reminder nextReminder(List<TaskWiseStore.Reminder> reminders) {
        long now = System.currentTimeMillis();
        for (TaskWiseStore.Reminder reminder : reminders) if (reminder.at > now) return reminder;
        return null;
    }
    private View createReminderRow(TaskWiseStore.Reminder reminder) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(6), dp(8), dp(6));
        row.setBackgroundResource(R.drawable.bg_dynamic_reminder);
        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(this);
        title.setText(reminder.title);
        title.setTextColor(0xFF34485B);
        title.setTextSize(14);
        title.setTypeface(null, Typeface.BOLD);
        TextView date = new TextView(this);
        date.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault())
                .format(reminder.at));
        date.setTextColor(0xFF738292);
        date.setTextSize(11);
        info.addView(title);
        info.addView(date);
        row.addView(info, new LinearLayout.LayoutParams(0, -1, 1));
        CheckBox alert = new CheckBox(this);
        alert.setText("Alert");
        alert.setTextSize(11);
        alert.setChecked(reminder.enabled);
        alert.setOnClickListener(v -> setReminderEnabled(reminder, alert.isChecked()));
        row.addView(alert, new LinearLayout.LayoutParams(-2, -2));
        row.setOnClickListener(v -> showReminderActions(reminder));
        return row;
    }
    private void setReminderEnabled(TaskWiseStore.Reminder reminder, boolean enabled) {
        reminder.enabled = enabled;
        TaskWiseStore.upsertReminder(this, reminder);
        ReminderScheduler.schedule(this, reminder);
        if (enabled) requestNotificationPermission();
        renderReminders();
    }
    private void showReminderActions(TaskWiseStore.Reminder reminder) {
        String status = reminder.enabled ? "Notifications are on." : "Notifications are off.";
        new AlertDialog.Builder(this)
                .setTitle(reminder.title)
                .setMessage(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())
                        .format(reminder.at) + "\n" + status)
                .setPositiveButton("Edit", (d, w) -> showReminderEditor(reminder))
                .setNeutralButton(reminder.enabled ? "Turn off" : "Turn on",
                        (d, w) -> setReminderEnabled(reminder, !reminder.enabled))
                .setNegativeButton("Delete", (d, w) -> new AlertDialog.Builder(this)
                        .setTitle("Delete reminder?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d2, w2) -> {
                            ReminderScheduler.cancel(this, reminder);
                            TaskWiseStore.deleteReminder(this, reminder.id);
                            renderReminders();
                        }).show())
                .show();
    }
    private void showReminderEditor(TaskWiseStore.Reminder existing) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(18), dp(18), dp(18), 0);
        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("Reminder title");
        title.setText(existing == null ? "" : existing.title);
        Calendar when = Calendar.getInstance();
        if (existing != null) when.setTimeInMillis(existing.at);
        else when.add(Calendar.HOUR_OF_DAY, 1);
        Button dateTime = new Button(this);
        updateDateTimeButton(dateTime, when);
        dateTime.setOnClickListener(v -> pickReminderDateTime(when, dateTime, existing == null));
        form.addView(title);
        form.addView(dateTime);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add New Reminder" : "Edit Reminder")
                .setMessage("The reminder is saved on this device. Notifications need permission on Android 13 and newer.")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String value = title.getText().toString().trim();
            if (value.isEmpty()) {
                title.setError("Enter a reminder title");
                title.requestFocus();
                return;
            }
            if (when.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Choose a future date and time.", Toast.LENGTH_SHORT).show();
                return;
            }
            TaskWiseStore.Reminder reminder = new TaskWiseStore.Reminder(
                    existing == null ? null : existing.id, value, when.getTimeInMillis(),
                    existing == null || existing.enabled);
            TaskWiseStore.upsertReminder(this, reminder);
            ReminderScheduler.schedule(this, reminder);
            dialog.dismiss();
            selectedDateFilter = null;
            renderReminders();
            newReminderSummary.setText("Saved: " + value);
            newReminderSummary.setVisibility(View.VISIBLE);
            requestNotificationPermission();
            Toast.makeText(this, "Reminder saved.", Toast.LENGTH_SHORT).show();
        }));
        dialog.show();
    }
    private void pickReminderDateTime(Calendar when, Button button, boolean isNew) {
        DatePickerDialog picker = new DatePickerDialog(this, (dateView, year, month, day) -> {
            when.set(year, month, day);
            new TimePickerDialog(this, (timeView, hour, minute) -> {
                when.set(Calendar.HOUR_OF_DAY, hour);
                when.set(Calendar.MINUTE, minute);
                when.set(Calendar.SECOND, 0);
                when.set(Calendar.MILLISECOND, 0);
                updateDateTimeButton(button, when);
            }, when.get(Calendar.HOUR_OF_DAY), when.get(Calendar.MINUTE), false).show();
        }, when.get(Calendar.YEAR), when.get(Calendar.MONTH), when.get(Calendar.DAY_OF_MONTH));
        if (isNew && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            picker.getDatePicker().setMinDate(System.currentTimeMillis());
        picker.show();
    }
    private void updateDateTimeButton(Button button, Calendar when) {
        button.setText("When: " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
                Locale.getDefault()).format(when.getTime()) + "  (tap to change)");
    }
    private void pickFilterDate() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (picker, year, month, day) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(year, month, day);
            selectedDateFilter = chosen.getTimeInMillis();
            renderReminders();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void showFeaturedReminder() {
        List<TaskWiseStore.Reminder> reminders = TaskWiseStore.getReminders(this);
        reminders.sort(Comparator.comparingLong(item -> item.at));
        TaskWiseStore.Reminder next = nextReminder(reminders);
        if (next == null) {
            showReminderEditor(null);
            return;
        }
        showReminderActions(next);
    }
    private void toggleFeaturedReminder() {
        List<TaskWiseStore.Reminder> reminders = TaskWiseStore.getReminders(this);
        reminders.sort(Comparator.comparingLong(item -> item.at));
        TaskWiseStore.Reminder next = nextReminder(reminders);
        if (next == null) {
            showReminderEditor(null);
            return;
        }
        setReminderEnabled(next, !next.enabled);
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 7001);
        }
    }
    private void bindBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> startActivity(new Intent(this, taskwise_home.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
        findViewById(R.id.navSubjects).setOnClickListener(v -> startActivity(new Intent(this, taskwise_subjects.class)));
        findViewById(R.id.navTasks).setOnClickListener(v -> startActivity(new Intent(this, taskwise_deadline.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> startActivity(new Intent(this, taskwise_calendar.class)));
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
