package taskwise;

import com.example.taskwise.R;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class taskwise_home extends AppCompatActivity {
    private TextView focusCount;
    private TextView homeUserName;
    private TextView homeTodaySummary;
    private TextView homeTodayDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_home);

        homeUserName = findViewById(R.id.homeUserName);
        homeTodaySummary = findViewById(R.id.homeTodaySummary);
        homeTodayDetail = findViewById(R.id.homeTodayDetail);

        ImageButton checklistImage = findViewById(R.id.btnChecklistImage);
        ImageButton checklistArrow = findViewById(R.id.btnChecklistArrow);
        ImageButton deadlineImage = findViewById(R.id.btnDeadlineImage);
        ImageButton deadlineArrow = findViewById(R.id.btnDeadlineArrow);
        ImageButton notebookImage = findViewById(R.id.btnNotebookImage);
        ImageButton notebookArrow = findViewById(R.id.btnNotebookArrow);
        ImageButton calendarImage = findViewById(R.id.btnCalendarImage);
        ImageButton calendarArrow = findViewById(R.id.btnCalendarArrow);

        checklistImage.setOnClickListener(v -> openDeadline());
        checklistArrow.setOnClickListener(v -> openDeadline());
        deadlineImage.setOnClickListener(v -> openDeadline());
        deadlineArrow.setOnClickListener(v -> openDeadline());
        notebookImage.setOnClickListener(v -> openNotebook());
        notebookArrow.setOnClickListener(v -> openNotebook());
        calendarImage.setOnClickListener(v -> openCalendar());
        calendarArrow.setOnClickListener(v -> openCalendar());
        findViewById(R.id.checklistCard).setOnClickListener(v -> openDeadline());
        findViewById(R.id.deadlineCard).setOnClickListener(v -> openDeadline());
        findViewById(R.id.notebookCard).setOnClickListener(v -> openNotebook());
        findViewById(R.id.calendarCard).setOnClickListener(v -> openCalendar());
        findViewById(R.id.homeTodayCard).setOnClickListener(v -> openDeadline());

        ScrollView scroll = (ScrollView) ((LinearLayout) findViewById(R.id.homeRoot)).getChildAt(0);
        LinearLayout content = (LinearLayout) scroll.getChildAt(0);
        content.addView(createStudySearchCard(), new LinearLayout.LayoutParams(-1, dp(88)));
        content.addView(createFocusCard(), new LinearLayout.LayoutParams(-1, dp(116)));
        bindBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGreeting();
        refreshTodaySummary();
        refreshFocusCount();
    }

    private void refreshGreeting() {
        String savedName = getSharedPreferences("taskwise_user", MODE_PRIVATE)
                .getString("username", "").trim();
        String launchName = getIntent().getStringExtra("username");
        String name = savedName.isEmpty() ? launchName : savedName;
        if (name == null || name.trim().isEmpty()) name = "Student";
        homeUserName.setText(name.trim() + "!");
    }

    private void refreshTodaySummary() {
        int due = 0;
        int completed = 0;
        long today = TaskWiseStore.startOfDay(System.currentTimeMillis());
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this)) {
            if (task.completed) completed++;
            else if (TaskWiseStore.startOfDay(task.dueAt) <= today) due++;
        }
        homeTodaySummary.setText(due == 0 ? "You're clear for today"
                : due + " task" + (due == 1 ? "" : "s") + " due or overdue");
        homeTodayDetail.setText(completed + " completed so far  •  Tap to open your checklist");
    }

    private View createFocusCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(14), dp(14), dp(14));
        GradientDrawable background = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF193B5B, 0xFF267A8A});
        background.setCornerRadius(dp(22));
        card.setBackground(background);
        card.setElevation(dp(4));

        TextView icon = new TextView(this);
        icon.setText("25");
        icon.setTextColor(0xFFFFFFFF);
        icon.setTextSize(18);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        GradientDrawable badge = new GradientDrawable();
        badge.setShape(GradientDrawable.OVAL);
        badge.setColor(0xFF3F9BA2);
        icon.setBackground(badge);
        card.addView(icon, new LinearLayout.LayoutParams(dp(54), dp(54)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(12), 0, dp(8), 0);
        TextView title = new TextView(this);
        title.setText("Focus Sprint");
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        TextView subtitle = new TextView(this);
        subtitle.setText("25 minutes on. 5 minutes to reset.");
        subtitle.setTextColor(0xFFDCECF2);
        subtitle.setTextSize(11);
        focusCount = new TextView(this);
        focusCount.setTextColor(0xFFAEDDDC);
        focusCount.setTextSize(10);
        copy.addView(title);
        copy.addView(subtitle);
        copy.addView(focusCount);
        card.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));

        Button start = new Button(this);
        start.setText("Start");
        start.setTextColor(0xFF193B5B);
        start.setTextSize(12);
        start.setTypeface(null, Typeface.BOLD);
        GradientDrawable pill = new GradientDrawable();
        pill.setColor(0xFFFFFFFF);
        pill.setCornerRadius(dp(20));
        start.setBackground(pill);
        card.addView(start, new LinearLayout.LayoutParams(dp(78), dp(42)));

        card.setOnClickListener(v -> openFocus());
        start.setOnClickListener(v -> openFocus());
        refreshFocusCount();
        return card;
    }

    private View createStudySearchCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(10), dp(12), dp(10));
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFFF0FAFA);
        background.setCornerRadius(dp(20));
        background.setStroke(dp(1), 0xFFD2ECEB);
        card.setBackground(background);
        card.setElevation(dp(2));

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_resource_search);
        card.addView(icon, new LinearLayout.LayoutParams(dp(54), dp(54)));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setPadding(dp(12), 0, dp(8), 0);
        TextView title = new TextView(this);
        title.setText("Study Search");
        title.setTextColor(0xFF233B4D);
        title.setTextSize(15);
        title.setTypeface(null, Typeface.BOLD);
        TextView subtitle = new TextView(this);
        subtitle.setText("Google, YouTube & Wikipedia");
        subtitle.setTextColor(0xFF64788A);
        subtitle.setTextSize(11);
        copy.addView(title);
        copy.addView(subtitle);
        card.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));

        TextView open = new TextView(this);
        open.setText("Search  ›");
        open.setTextColor(0xFF2D8D98);
        open.setTextSize(12);
        open.setTypeface(null, Typeface.BOLD);
        card.addView(open, new LinearLayout.LayoutParams(-2, -2));
        card.setOnClickListener(v -> openStudySearch());
        return card;
    }

    private void refreshFocusCount() {
        if (focusCount == null) return;
        int count = getSharedPreferences("taskwise_focus", MODE_PRIVATE).getInt("sessions", 0);
        focusCount.setText(count + (count == 1 ? " round completed" : " rounds completed"));
    }

    private void openDeadline() {
        startActivity(new Intent(this, taskwise_deadline.class));
    }

    private void openCalendar() {
        startActivity(new Intent(this, taskwise_calendar.class));
    }

    private void openReminder() {
        startActivity(new Intent(this, taskwise_reminder.class));
    }

    private void openNotebook() {
        startActivity(new Intent(this, taskwise_notebook.class));
    }

    private void openProfile() {
        startActivity(new Intent(this, taskwise_profile.class));
    }

    private void openSubjects() {
        startActivity(new Intent(this, taskwise_subjects.class));
    }

    private void openFocus() {
        startActivity(new Intent(this, taskwise_focus.class));
    }

    private void openStudySearch() {
        startActivity(new Intent(this, taskwise_resources.class));
    }

    private void bindBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {});
        findViewById(R.id.navProfile).setOnClickListener(v -> openProfile());
        findViewById(R.id.navSubjects).setOnClickListener(v -> openSubjects());
        findViewById(R.id.navTasks).setOnClickListener(v -> openDeadline());
        findViewById(R.id.navMenu).setOnClickListener(v -> openReminder());
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
