package taskwise;

import com.example.taskwise.R;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class taskwise_deadline extends AppCompatActivity {
    private LinearLayout taskList;
    private String selectedFilter = "all";
    private String subjectFilter = "";
    private String searchQuery = "";
    private TextView deadlineTaskSummary;
    private TextView deadlineProgressLabel;
    private TextView deadlineReminderBadge;
    private ProgressBar deadlineProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_deadline);
        taskList = findViewById(R.id.deadlineTaskList);
        deadlineTaskSummary = findViewById(R.id.deadlineTaskSummary);
        deadlineProgressLabel = findViewById(R.id.deadlineProgressLabel);
        deadlineProgressBar = findViewById(R.id.deadlineProgressBar);
        deadlineReminderBadge = findViewById(R.id.deadlineReminderBadge);
        subjectFilter = getIntent().getStringExtra("subject_filter");
        if (subjectFilter == null) subjectFilter = "";
        EditText searchInput = findViewById(R.id.taskSearchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim();
                renderTasks();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        findViewById(R.id.deadlineBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.deadlineNotificationButton).setOnClickListener(v -> openReminders());
        findViewById(R.id.filterAll).setOnClickListener(v -> applyFilter("all"));
        findViewById(R.id.filterOverdue).setOnClickListener(v -> applyFilter("overdue"));
        findViewById(R.id.filterDueToday).setOnClickListener(v -> applyFilter("today"));
        findViewById(R.id.filterUpcoming).setOnClickListener(v -> applyFilter("upcoming"));
        findViewById(R.id.filterCompleted).setOnClickListener(v -> applyFilter("completed"));
        findViewById(R.id.addNewTaskCard).setOnClickListener(v -> showTaskEditor(null));
        findViewById(R.id.addNewTaskButton).setOnClickListener(v -> showTaskEditor(null));
        findViewById(R.id.addNewTaskArrow).setOnClickListener(v -> showTaskEditor(null));
        findViewById(R.id.deadlineStudySearchCard).setOnClickListener(v -> openStudySearch());
        bindBottomNavigation();
        applyFilter("all");
    }
    @Override protected void onResume() {
        super.onResume();
        if (taskList != null) renderTasks();
    }
    private void applyFilter(String filter) {
        selectedFilter = filter;
        setFilterStyle(R.id.filterAll, "all".equals(filter));
        setFilterStyle(R.id.filterOverdue, "overdue".equals(filter));
        setFilterStyle(R.id.filterDueToday, "today".equals(filter));
        setFilterStyle(R.id.filterUpcoming, "upcoming".equals(filter));
        setFilterStyle(R.id.filterCompleted, "completed".equals(filter));
        renderTasks();
    }
    private void setFilterStyle(int id, boolean selected) {
        TextView tab = findViewById(id);
        tab.setBackgroundResource(selected ? R.drawable.bg_tab_selected : R.drawable.bg_tab_unselected);
        tab.setTextColor(selected ? 0xFFFFFFFF : 0xFF536477);
        tab.setElevation(selected ? 3f : 0f);
    }
    private void renderTasks() {
        for (int i = taskList.getChildCount() - 1; i >= 0; i--) {
            View child = taskList.getChildAt(i);
            if ("taskwise.dynamic".equals(child.getTag())) taskList.removeViewAt(i);
        }
        List<TaskWiseStore.Task> all = TaskWiseStore.getTasks(this);
        int open = 0;
        int dueToday = 0;
        int overdue = 0;
        int completed = 0;
        ArrayList<TaskWiseStore.Task> visible = new ArrayList<>();
        long today = TaskWiseStore.startOfDay(System.currentTimeMillis());
        for (TaskWiseStore.Task task : all) {
            long dueDay = TaskWiseStore.startOfDay(task.dueAt);
            if (task.completed) {
                completed++;
            } else {
                open++;
                if (dueDay < today) overdue++;
                else if (dueDay == today) dueToday++;
            }
            boolean matchesSubject = subjectFilter.isEmpty()
                    || task.subject.toLowerCase(Locale.ROOT).contains(subjectFilter.toLowerCase(Locale.ROOT));
            String query = searchQuery.toLowerCase(Locale.ROOT);
            boolean matchesSearch = query.isEmpty()
                    || task.title.toLowerCase(Locale.ROOT).contains(query)
                    || task.subject.toLowerCase(Locale.ROOT).contains(query);
            boolean matchesFilter = "all".equals(selectedFilter)
                    || ("overdue".equals(selectedFilter) && !task.completed && dueDay < today)
                    || ("today".equals(selectedFilter) && !task.completed && dueDay == today)
                    || ("upcoming".equals(selectedFilter) && !task.completed && dueDay > today)
                    || ("completed".equals(selectedFilter) && task.completed);
            if (matchesSubject && matchesSearch && matchesFilter) visible.add(task);
        }
        visible.sort((left, right) -> {
            if (left.completed != right.completed) return left.completed ? 1 : -1;
            return Long.compare(left.dueAt, right.dueAt);
        });
        int percent = all.isEmpty() ? 0 : completed * 100 / all.size();
        deadlineTaskSummary.setText(open + " active  ·  " + dueToday + " due today"
                + (overdue == 0 ? "" : "  ·  " + overdue + " overdue"));
        deadlineProgressBar.setProgress(percent);
        deadlineProgressLabel.setText(percent + "%");
        TextView listTitle = findViewById(R.id.deadlineListTitle);
        listTitle.setText(subjectFilter.isEmpty() ? "Your tasks" : subjectFilter);
        int insertAt = 0;
        if (visible.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setTag("taskwise.dynamic");
            String emptyMessage;
            if (!searchQuery.isEmpty()) emptyMessage = "No tasks match “" + searchQuery + "”.";
            else if (!subjectFilter.isEmpty()) emptyMessage = "No tasks for " + subjectFilter + " yet.";
            else if ("overdue".equals(selectedFilter)) emptyMessage = "You’re all caught up. No overdue tasks.";
            else if ("today".equals(selectedFilter)) emptyMessage = "No tasks are due today.";
            else if ("upcoming".equals(selectedFilter)) emptyMessage = "No upcoming tasks yet.";
            else if ("completed".equals(selectedFilter)) emptyMessage = "Completed tasks will appear here.";
            else emptyMessage = "No tasks yet. Add your first task to get started.";
            empty.setText(emptyMessage);
            empty.setTextColor(0xFF738292);
            empty.setTextSize(14);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(18), dp(20), dp(18), dp(20));
            empty.setBackgroundResource(R.drawable.bg_task_card);
            LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(-1, -2);
            emptyParams.topMargin = dp(8);
            taskList.addView(empty, insertAt, emptyParams);
        } else {
            for (TaskWiseStore.Task task : visible) {
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1, dp(84));
                rowParams.topMargin = dp(8);
                taskList.addView(createTaskRow(task), insertAt++, rowParams);
            }
        }
        updateReminderBadge();
    }
    private void updateReminderBadge() {
        int count = 0;
        long now = System.currentTimeMillis();
        for (TaskWiseStore.Reminder reminder : TaskWiseStore.getReminders(this))
            if (reminder.enabled && reminder.at > now) count++;
        if (count == 0) {
            deadlineReminderBadge.setVisibility(View.GONE);
            return;
        }
        deadlineReminderBadge.setText(count > 9 ? "9+" : String.valueOf(count));
        deadlineReminderBadge.setVisibility(View.VISIBLE);
    }
    private View createTaskRow(TaskWiseStore.Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setTag("taskwise.dynamic");
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(8), dp(10), dp(8));
        card.setBackgroundResource(R.drawable.bg_task_card);
        card.setElevation(dp(2));
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = new TextView(this);
        title.setText(task.title);
        title.setTextColor(0xFF26364A);
        title.setTextSize(15);
        title.setTypeface(null, Typeface.BOLD);
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        TextView meta = new TextView(this);
        String subject = task.subject.isEmpty() ? "Personal" : task.subject;
        String due = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(task.dueAt);
        long dueDay = TaskWiseStore.startOfDay(task.dueAt);
        String dueLabel = task.completed ? "Completed" : dueDay < TaskWiseStore.startOfDay(System.currentTimeMillis())
                ? "Overdue · " + due : dueDay == TaskWiseStore.startOfDay(System.currentTimeMillis())
                ? "Due today" : "Due " + due;
        meta.setText(subject + "  ·  " + dueLabel + "  ·  " + task.priority + " priority");
        meta.setTextColor(0xFF718092);
        meta.setTextSize(11);
        meta.setMaxLines(1);
        meta.setEllipsize(TextUtils.TruncateAt.END);
        details.addView(title);
        details.addView(meta);
        card.addView(details, new LinearLayout.LayoutParams(0, -1, 1));
        CheckBox done = new CheckBox(this);
        done.setText("Done");
        done.setTextSize(12);
        done.setChecked(task.completed);
        done.setOnClickListener(v -> {
            task.completed = done.isChecked();
            TaskWiseStore.upsertTask(this, task);
            renderTasks();
        });
        card.addView(done, new LinearLayout.LayoutParams(-2, -2));
        card.setOnClickListener(v -> showTaskActions(task));
        return card;
    }
    private void showTaskActions(TaskWiseStore.Task task) {
        new AlertDialog.Builder(this)
                .setTitle(task.title)
                .setMessage((task.subject.isEmpty() ? "Personal" : task.subject)
                        + "\nDue " + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(task.dueAt)
                        + "\n" + task.priority + " priority")
                .setPositiveButton("Edit", (d, w) -> showTaskEditor(task))
                .setNegativeButton("Delete", (d, w) -> new AlertDialog.Builder(this)
                        .setTitle("Delete task?")
                        .setMessage("This task will be removed from your list.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d2, w2) -> {
                            TaskWiseStore.deleteTask(this, task.id);
                            renderTasks();
                        }).show())
                .setNeutralButton("Close", null)
                .show();
    }
    private void showTaskEditor(TaskWiseStore.Task existing) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(18);
        form.setPadding(pad, pad, pad, 0);
        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("Task name");
        title.setText(existing == null ? "" : existing.title);
        EditText subject = new EditText(this);
        subject.setSingleLine(true);
        subject.setHint("Subject or category");
        subject.setText(existing == null ? subjectFilter : existing.subject);
        Spinner priority = new Spinner(this);
        String[] priorities = {"High", "Medium", "Low"};
        priority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
        Calendar due = Calendar.getInstance();
        if (existing != null) due.setTimeInMillis(existing.dueAt);
        Button dueButton = new Button(this);
        updateDueButton(dueButton, due);
        dueButton.setOnClickListener(v -> new DatePickerDialog(this, (picker, year, month, day) -> {
            due.set(year, month, day);
            updateDueButton(dueButton, due);
        }, due.get(Calendar.YEAR), due.get(Calendar.MONTH), due.get(Calendar.DAY_OF_MONTH)).show());
        form.addView(title);
        form.addView(subject);
        form.addView(dueButton);
        form.addView(priority);
        if (existing != null) {
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].equalsIgnoreCase(existing.priority)) priority.setSelection(i);
            }
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Add New Task" : "Edit Task")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = title.getText().toString().trim();
            if (name.isEmpty()) {
                title.setError("Enter a task name");
                title.requestFocus();
                return;
            }
            due.set(Calendar.HOUR_OF_DAY, 23);
            due.set(Calendar.MINUTE, 59);
            due.set(Calendar.SECOND, 0);
            due.set(Calendar.MILLISECOND, 0);
            String id = existing == null ? null : existing.id;
            boolean completed = existing != null && existing.completed;
            TaskWiseStore.upsertTask(this, new TaskWiseStore.Task(id, name,
                    subject.getText().toString().trim(), due.getTimeInMillis(),
                    String.valueOf(priority.getSelectedItem()), completed));
            dialog.dismiss();
            renderTasks();
        }));
        dialog.show();
    }
    private void updateDueButton(Button button, Calendar date) {
        button.setText("Due date: " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                .format(date.getTime()) + "  (tap to change)");
    }
    private void openReminders() {
        startActivity(new Intent(this, taskwise_reminder.class));
    }
    private void openStudySearch() {
        startActivity(new Intent(this, taskwise_resources.class));
    }
    private void bindBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> startActivity(new Intent(this, taskwise_home.class)));
        findViewById(R.id.navTasks).setOnClickListener(v -> {});
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
        findViewById(R.id.navSubjects).setOnClickListener(v -> startActivity(new Intent(this, taskwise_subjects.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> openReminders());
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
