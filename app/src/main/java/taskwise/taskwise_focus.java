package taskwise;

import com.example.taskwise.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class taskwise_focus extends AppCompatActivity {
    private static final long FOCUS_MILLIS = 25 * 60 * 1000L;
    private static final long BREAK_MILLIS = 5 * 60 * 1000L;
    private long remainingMillis = FOCUS_MILLIS;
    private boolean breakMode;
    private boolean running;
    private CountDownTimer timer;
    private TextView clockText;
    private TextView modeText;
    private TextView sessionsText;
    private Button startButton;
    private Button modeButton;
    private Spinner taskPicker;
    private final ArrayList<TaskWiseStore.Task> availableTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        TaskWiseStore.initialize(this);
        buildScreen();
        bindNavigation();
        refreshSessions();
        updateTimerText();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF4F7FB);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(6), dp(18), dp(6));
        TextView back = new TextView(this);
        back.setText("<");
        back.setTextColor(0xFF19324B);
        back.setTextSize(27);
        back.setGravity(Gravity.CENTER);
        header.addView(back, new LinearLayout.LayoutParams(dp(42), dp(46)));
        back.setOnClickListener(v -> finish());
        TextView heading = new TextView(this);
        heading.setText("Focus Sprint");
        heading.setTextColor(0xFF19324B);
        heading.setTextSize(22);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(dp(8), 0, 0, 0);
        header.addView(heading);
        root.addView(header, new LinearLayout.LayoutParams(-1, dp(58)));

        ScrollView scroll = new ScrollView(this);
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(22), dp(10), dp(22), dp(20));

        modeText = new TextView(this);
        modeText.setText("FOCUS TIME");
        modeText.setTextColor(0xFF438BDD);
        modeText.setTextSize(12);
        modeText.setTypeface(null, Typeface.BOLD);
        modeText.setLetterSpacing(0.12f);
        body.addView(modeText, new LinearLayout.LayoutParams(-2, dp(28)));

        FrameLayout timerCircle = new FrameLayout(this);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(0xFF183A5A);
        circle.setStroke(dp(8), 0xFFBFEDE8);
        timerCircle.setBackground(circle);
        timerCircle.setElevation(dp(6));
        clockText = new TextView(this);
        clockText.setGravity(Gravity.CENTER);
        clockText.setTextColor(Color.WHITE);
        clockText.setTextSize(48);
        clockText.setTypeface(null, Typeface.BOLD);
        timerCircle.addView(clockText, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(dp(252), dp(252));
        circleParams.topMargin = dp(6);
        body.addView(timerCircle, circleParams);

        TextView guide = new TextView(this);
        guide.setText("Work for 25 minutes, then take a 5-minute reset.");
        guide.setTextColor(0xFF718197);
        guide.setTextSize(13);
        guide.setGravity(Gravity.CENTER);
        guide.setPadding(0, dp(12), 0, dp(10));
        body.addView(guide);

        LinearLayout taskCard = new LinearLayout(this);
        taskCard.setOrientation(LinearLayout.VERTICAL);
        taskCard.setPadding(dp(16), dp(12), dp(16), dp(12));
        taskCard.setBackground(cardBackground());
        TextView taskLabel = new TextView(this);
        taskLabel.setText("WHAT ARE YOU WORKING ON?");
        taskLabel.setTextColor(0xFF718197);
        taskLabel.setTextSize(10);
        taskLabel.setTypeface(null, Typeface.BOLD);
        taskCard.addView(taskLabel);
        taskPicker = new Spinner(this);
        ArrayList<String> names = new ArrayList<>();
        names.add("Free focus session");
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this)) {
            if (!task.completed) {
                availableTasks.add(task);
                names.add(task.title);
            }
        }
        taskPicker.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
        taskCard.addView(taskPicker, new LinearLayout.LayoutParams(-1, dp(48)));
        LinearLayout.LayoutParams taskCardParams = new LinearLayout.LayoutParams(-1, -2);
        taskCardParams.topMargin = dp(12);
        body.addView(taskCard, taskCardParams);

        sessionsText = new TextView(this);
        sessionsText.setTextColor(0xFF19324B);
        sessionsText.setTextSize(13);
        sessionsText.setGravity(Gravity.CENTER);
        sessionsText.setPadding(0, dp(14), 0, dp(12));
        body.addView(sessionsText);

        startButton = makeButton("Start focus", 0xFF438BDD, Color.WHITE);
        body.addView(startButton, new LinearLayout.LayoutParams(-1, dp(52)));
        startButton.setOnClickListener(v -> toggleTimer());

        modeButton = makeButton("Switch to 5-minute break", 0xFFE5F4F3, 0xFF267D7A);
        LinearLayout.LayoutParams modeParams = new LinearLayout.LayoutParams(-1, dp(48));
        modeParams.topMargin = dp(9);
        body.addView(modeButton, modeParams);
        modeButton.setOnClickListener(v -> switchMode());

        Button reset = makeButton("Reset timer", 0xFFFFFFFF, 0xFF536477);
        LinearLayout.LayoutParams resetParams = new LinearLayout.LayoutParams(-1, dp(46));
        resetParams.topMargin = dp(8);
        body.addView(reset, resetParams);
        reset.setOnClickListener(v -> resetTimer());

        scroll.addView(body);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));
        View nav = getLayoutInflater().inflate(R.layout.bottom_navigation, root, false);
        root.addView(nav);
        setContentView(root);
    }

    private GradientDrawable cardBackground() {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(dp(18));
        shape.setStroke(dp(1), 0xFFE1EAF2);
        return shape;
    }

    private Button makeButton(String label, int background, int foreground) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(foreground);
        button.setTextSize(14);
        button.setTypeface(null, Typeface.BOLD);
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(background);
        shape.setCornerRadius(dp(16));
        button.setBackground(shape);
        return button;
    }

    private void toggleTimer() {
        if (running) {
            if (timer != null) timer.cancel();
            running = false;
            startButton.setText(breakMode ? "Resume break" : "Resume focus");
            return;
        }
        if (remainingMillis <= 0) remainingMillis = breakMode ? BREAK_MILLIS : FOCUS_MILLIS;
        running = true;
        startButton.setText("Pause");
        timer = new CountDownTimer(remainingMillis, 1000) {
            @Override public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                updateTimerText();
            }
            @Override public void onFinish() {
                running = false;
                remainingMillis = 0;
                if (!breakMode) {
                    recordFocusSession();
                    setBreakMode(true);
                    Toast.makeText(taskwise_focus.this, "Focus round complete. Take a 5-minute break.", Toast.LENGTH_LONG).show();
                } else {
                    setBreakMode(false);
                    Toast.makeText(taskwise_focus.this, "Break complete. Ready for another sprint?", Toast.LENGTH_LONG).show();
                }
            }
        }.start();
    }

    private void switchMode() {
        if (timer != null) timer.cancel();
        running = false;
        setBreakMode(!breakMode);
    }

    private void setBreakMode(boolean value) {
        breakMode = value;
        remainingMillis = value ? BREAK_MILLIS : FOCUS_MILLIS;
        modeText.setText(value ? "RESET TIME" : "FOCUS TIME");
        modeText.setTextColor(value ? 0xFF267D7A : 0xFF438BDD);
        modeButton.setText(value ? "Switch to 25-minute focus" : "Switch to 5-minute break");
        startButton.setText(value ? "Start break" : "Start focus");
        updateTimerText();
    }

    private void resetTimer() {
        if (timer != null) timer.cancel();
        running = false;
        remainingMillis = breakMode ? BREAK_MILLIS : FOCUS_MILLIS;
        startButton.setText(breakMode ? "Start break" : "Start focus");
        updateTimerText();
    }

    private void updateTimerText() {
        long seconds = Math.max(0, remainingMillis / 1000);
        clockText.setText(String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60));
    }

    private void recordFocusSession() {
        SharedPreferences prefs = getSharedPreferences("taskwise_focus", MODE_PRIVATE);
        int count = prefs.getInt("sessions", 0) + 1;
        SharedPreferences.Editor edit = prefs.edit().putInt("sessions", count);
        int selected = taskPicker.getSelectedItemPosition() - 1;
        if (selected >= 0 && selected < availableTasks.size()) {
            String key = "task_sessions_" + availableTasks.get(selected).id;
            edit.putInt(key, prefs.getInt(key, 0) + 1);
            edit.putString("last_task", availableTasks.get(selected).title);
        }
        edit.apply();
        refreshSessions();
    }

    private void refreshSessions() {
        int count = getSharedPreferences("taskwise_focus", MODE_PRIVATE).getInt("sessions", 0);
        sessionsText.setText(count + (count == 1 ? " focus round completed" : " focus rounds completed"));
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }

    private void bindNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> finishTo(taskwise_home.class));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
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

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}