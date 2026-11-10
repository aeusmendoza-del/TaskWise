package taskwise;

import com.example.taskwise.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
public class taskwise_subjects extends AppCompatActivity {
    private static final String SUBJECT_PREFS = "taskwise_subjects";
    private static final String SUBJECT_LIST = "saved_subjects";
    private LinearLayout subjectListContainer;
    private final String[] defaultSubjects = {"Computer Programming", "Effective Communication", "General Science", "General Mathematics"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_subjects);
        subjectListContainer = findViewById(R.id.subjectListContainer);
        findViewById(R.id.subjectsBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.addSubjectButton).setOnClickListener(v -> showSubjectEditor(null));
        findViewById(R.id.navHome).setOnClickListener(v -> finishTo(taskwise_home.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
        findViewById(R.id.navSubjects).setOnClickListener(v -> {});
        findViewById(R.id.navTasks).setOnClickListener(v -> startActivity(new Intent(this, taskwise_deadline.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> startActivity(new Intent(this, taskwise_reminder.class)));
        renderSubjects();
    }
    @Override protected void onResume() { super.onResume(); if (subjectListContainer != null) renderSubjects(); }
    private ArrayList<String> getSubjects() {
        SharedPreferences preferences = getSharedPreferences(SUBJECT_PREFS, MODE_PRIVATE);
        if (!preferences.contains(SUBJECT_LIST)) return new ArrayList<>(Arrays.asList(defaultSubjects));
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONArray values = new JSONArray(preferences.getString(SUBJECT_LIST, "[]"));
            for (int i = 0; i < values.length(); i++) {
                String subject = values.optString(i, "").trim();
                if (!subject.isEmpty()) result.add(subject);
            }
        } catch (Exception ignored) { }
        return result;
    }
    private void saveSubjects(ArrayList<String> subjects) {
        JSONArray values = new JSONArray();
        for (String subject : subjects) values.put(subject);
        getSharedPreferences(SUBJECT_PREFS, MODE_PRIVATE).edit().putString(SUBJECT_LIST, values.toString()).apply();
    }
    private void renderSubjects() {
        subjectListContainer.removeAllViews();
        ArrayList<String> subjects = getSubjects();
        if (subjects.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No subjects yet. Add one to organize your tasks.");
            empty.setTextColor(0xFF718092);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(16), dp(28), dp(16), dp(28));
            subjectListContainer.addView(empty);
            return;
        }
        for (String subject : subjects) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(76));
            params.topMargin = dp(8);
            subjectListContainer.addView(createSubjectRow(subject), params);
        }
    }
    private View createSubjectRow(String subject) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(8), dp(12), dp(8));
        row.setBackgroundResource(R.drawable.bg_task_card);
        ImageView icon = new ImageView(this);
        icon.setImageResource(iconFor(subject));
        row.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, dp(8), 0);
        TextView name = new TextView(this);
        name.setText(subject);
        name.setTextColor(0xFF26364A);
        name.setTextSize(14);
        name.setTypeface(null, Typeface.BOLD);
        TextView count = new TextView(this);
        int taskCount = 0;
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this))
            if (task.subject.equalsIgnoreCase(subject)) taskCount++;
        count.setText(taskCount + (taskCount == 1 ? " task" : " tasks"));
        count.setTextColor(0xFF718092);
        count.setTextSize(11);
        details.addView(name);
        details.addView(count);
        row.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
        TextView arrow = new TextView(this);
        arrow.setText("›");
        arrow.setTextColor(0xFF3998B4);
        arrow.setTextSize(28);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(24), -2));
        row.setOnClickListener(v -> showSubjectActions(subject));
        return row;
    }
    private int iconFor(String subject) {
        String name = subject.toLowerCase(Locale.ROOT);
        if (name.contains("program") || name.contains("computer") || name.contains("coding")) return R.drawable.ic_subject_code;
        if (name.contains("communication") || name.contains("english")) return R.drawable.ic_subject_speech;
        if (name.contains("science") || name.contains("biology") || name.contains("chemistry")) return R.drawable.ic_subject_science;
        if (name.contains("math")) return R.drawable.ic_subject_math;
        return R.drawable.ic_subject_custom;
    }
    private void showSubjectActions(String subject) {
        new AlertDialog.Builder(this).setTitle(subject)
                .setItems(new String[]{"View tasks", "Edit subject", "Remove subject"}, (dialog, which) -> {
                    if (which == 0) openSubjectTasks(subject);
                    else if (which == 1) showSubjectEditor(subject);
                    else confirmRemoveSubject(subject);
                }).show();
    }
    private void openSubjectTasks(String subject) {
        Intent intent = new Intent(this, taskwise_deadline.class);
        intent.putExtra("subject_filter", subject);
        startActivity(intent);
    }
    private void showSubjectEditor(String existing) {
        EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("Subject name");
        name.setPadding(dp(18), dp(12), dp(18), dp(12));
        if (existing != null) name.setText(existing);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(existing == null ? "Add subject" : "Edit subject")
                .setView(name).setNegativeButton("Cancel", null).setPositiveButton("Save", null).create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String value = name.getText().toString().trim();
                    if (value.isEmpty()) {
                        name.setError("Enter a subject name");
                        return;
                    }
                    ArrayList<String> subjects = getSubjects();
                    for (String subject : subjects) {
                        if (subject.equalsIgnoreCase(value) && (existing == null || !subject.equalsIgnoreCase(existing))) {
                            new AlertDialog.Builder(this).setMessage("That subject already exists.").setPositiveButton("OK", null).show();
                            return;
                        }
                    }
                    if (existing == null) subjects.add(value);
                    else {
                        int index = subjects.indexOf(existing);
                        if (index >= 0) subjects.set(index, value);
                        updateTaskSubject(existing, value);
                    }
                    saveSubjects(subjects);
                    renderSubjects();
                    dialog.dismiss();
                }));
        dialog.show();
    }
    private void confirmRemoveSubject(String subject) {
        new AlertDialog.Builder(this).setTitle("Remove subject?")
                .setMessage("Tasks in " + subject + " will stay saved as Personal tasks.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Remove", (dialog, which) -> {
                    ArrayList<String> subjects = getSubjects();
                    for (int i = subjects.size() - 1; i >= 0; i--)
                        if (subjects.get(i).equalsIgnoreCase(subject)) subjects.remove(i);
                    updateTaskSubject(subject, "");
                    saveSubjects(subjects);
                    renderSubjects();
                }).show();
    }
    private void updateTaskSubject(String oldName, String newName) {
        ArrayList<TaskWiseStore.Task> tasks = new ArrayList<>(TaskWiseStore.getTasks(this));
        for (TaskWiseStore.Task task : tasks)
            if (task.subject.equalsIgnoreCase(oldName)) task.subject = newName;
        TaskWiseStore.saveTasks(this, tasks);
    }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
    private void finishTo(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
