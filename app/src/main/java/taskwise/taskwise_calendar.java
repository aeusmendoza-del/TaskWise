package taskwise;

import com.example.taskwise.R;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
public class taskwise_calendar extends AppCompatActivity {
    private final Calendar displayedMonth = Calendar.getInstance();
    private final Calendar selectedDate = Calendar.getInstance();
    private LinearLayout calendarGrid;
    private LinearLayout eventList;
    private TextView monthYearText;
    private TextView selectedDateText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskWiseStore.initialize(this);
        setContentView(R.layout.taskwise_calendar);
        calendarGrid = findViewById(R.id.calendarGrid);
        eventList = findViewById(R.id.calendarEventList);
        monthYearText = findViewById(R.id.monthYearText);
        selectedDateText = findViewById(R.id.selectedDateText);
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
        // The add-event card is a sibling of the event list; leave it in place.
        eventList.removeAllViews();
        findViewById(R.id.calendarBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.previousMonthButton).setOnClickListener(v -> changeMonth(-1));
        findViewById(R.id.nextMonthButton).setOnClickListener(v -> changeMonth(1));
        findViewById(R.id.calendarTodayButton).setOnClickListener(v -> goToToday());
        findViewById(R.id.calendarHeaderButton).setOnClickListener(v -> goToToday());
        findViewById(R.id.calendarViewAll).setOnClickListener(v -> showAllEvents());
        findViewById(R.id.addNewEventCard).setOnClickListener(v -> showAddEventDialog());
        findViewById(R.id.addNewEventButton).setOnClickListener(v -> showAddEventDialog());
        bindBottomNavigation();
        render();
    }
    private void changeMonth(int amount) {
        displayedMonth.add(Calendar.MONTH, amount);
        selectedDate.set(displayedMonth.get(Calendar.YEAR), displayedMonth.get(Calendar.MONTH), 1);
        render();
    }
    private void goToToday() {
        Calendar today = Calendar.getInstance();
        selectedDate.setTimeInMillis(today.getTimeInMillis());
        displayedMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1);
        render();
    }
    private void render() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        monthYearText.setText(monthFormat.format(displayedMonth.getTime()));
        selectedDateText.setText(dateFormat.format(selectedDate.getTime()));
        renderGrid();
        renderItems();
    }
    private void renderGrid() {
        calendarGrid.removeAllViews();
        Calendar first = (Calendar) displayedMonth.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int offset = first.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        int daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            calendarGrid.addView(row, new LinearLayout.LayoutParams(-1, 0, 1f));
            for (int column = 0; column < 7; column++) {
                int index = rowIndex * 7 + column;
                Calendar date = (Calendar) first.clone();
                date.add(Calendar.DAY_OF_MONTH, index - offset);
                boolean inMonth = date.get(Calendar.MONTH) == displayedMonth.get(Calendar.MONTH);
                int day = date.get(Calendar.DAY_OF_MONTH);
                FrameLayout cell = new FrameLayout(this);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, -1, 1f);
                cellParams.setMargins(dp(1), dp(1), dp(1), dp(1));
                row.addView(cell, cellParams);
                TextView number = new TextView(this);
                number.setText(String.valueOf(day));
                number.setTextSize(12);
                number.setGravity(Gravity.CENTER);
                number.setTextColor(inMonth ? dayColor(date) : 0xFFB8C1CA);
                FrameLayout.LayoutParams numberParams = new FrameLayout.LayoutParams(dp(32), dp(32), Gravity.CENTER);
                if (inMonth && TaskWiseStore.sameDay(date.getTimeInMillis(), selectedDate.getTimeInMillis())) {
                    GradientDrawable circle = new GradientDrawable();
                    circle.setShape(GradientDrawable.OVAL);
                    circle.setColor(0xFF438FE0);
                    number.setBackground(circle);
                    number.setTextColor(Color.WHITE);
                    number.setTypeface(null, Typeface.BOLD);
                }
                cell.addView(number, numberParams);
                if (inMonth && hasItems(date.getTimeInMillis())) {
                    View dot = new View(this);
                    GradientDrawable dotBackground = new GradientDrawable();
                    dotBackground.setShape(GradientDrawable.OVAL);
                    dotBackground.setColor(0xFF42BFA6);
                    dot.setBackground(dotBackground);
                    FrameLayout.LayoutParams dotParams = new FrameLayout.LayoutParams(dp(5), dp(5),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    dotParams.bottomMargin = dp(3);
                    cell.addView(dot, dotParams);
                }
                if (inMonth) {
                    long millis = date.getTimeInMillis();
                    cell.setOnClickListener(v -> {
                        selectedDate.setTimeInMillis(millis);
                        render();
                    });
                }
            }
        }
    }
    private int dayColor(Calendar date) {
        int day = date.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.SUNDAY) return 0xFF4D91C6;
        if (day == Calendar.SATURDAY) return 0xFFC66C76;
        return 0xFF394B5C;
    }
    private boolean hasItems(long date) {
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this)) {
            if (TaskWiseStore.sameDay(task.dueAt, date)) return true;
        }
        for (TaskWiseStore.Event event : TaskWiseStore.getEvents(this)) {
            if (TaskWiseStore.sameDay(event.at, date)) return true;
        }
        return false;
    }
    private void renderItems() {
        for (int i = eventList.getChildCount() - 1; i >= 0; i--) {
            View child = eventList.getChildAt(i);
            if ("taskwise.calendar_item".equals(child.getTag())) eventList.removeViewAt(i);
        }
        ArrayList<Object> items = new ArrayList<>();
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this)) {
            if (TaskWiseStore.sameDay(task.dueAt, selectedDate.getTimeInMillis())) items.add(task);
        }
        for (TaskWiseStore.Event event : TaskWiseStore.getEvents(this)) {
            if (TaskWiseStore.sameDay(event.at, selectedDate.getTimeInMillis())) items.add(event);
        }
        items.sort((a, b) -> Long.compare(itemTime(a), itemTime(b)));
        int insertAt = eventList.getChildCount();
        if (items.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setTag("taskwise.calendar_item");
            empty.setText("No tasks or events scheduled for this day.");
            empty.setTextColor(0xFF738292);
            empty.setTextSize(13);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(16), dp(18), dp(16), dp(18));
            eventList.addView(empty, insertAt, new LinearLayout.LayoutParams(-1, -2));
        } else {
            for (Object item : items) {
                eventList.addView(createItemRow(item), insertAt++, new LinearLayout.LayoutParams(-1, dp(62)));
            }
        }
    }
    private long itemTime(Object item) {
        return item instanceof TaskWiseStore.Task
                ? ((TaskWiseStore.Task) item).dueAt : ((TaskWiseStore.Event) item).at;
    }
    private View createItemRow(Object item) {
        TextView row = new TextView(this);
        row.setTag("taskwise.calendar_item");
        row.setTextSize(13);
        row.setTextColor(0xFF34485B);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), 0, dp(14), 0);
        row.setBackgroundResource(R.drawable.bg_dynamic_reminder);
        String title = item instanceof TaskWiseStore.Task
                ? ((TaskWiseStore.Task) item).title : ((TaskWiseStore.Event) item).title;
        row.setText(title + "  |  " + DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                .format(itemTime(item)));
        if (item instanceof TaskWiseStore.Task) {
            TaskWiseStore.Task task = (TaskWiseStore.Task) item;
            row.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle(task.title)
                    .setMessage((task.subject.isEmpty() ? "Personal" : task.subject) + "\nDue task")
                    .setPositiveButton("Open tasks", (d, w) -> {
                        Intent intent = new Intent(this, taskwise_deadline.class);
                        intent.putExtra("subject_filter", task.subject);
                        startActivity(intent);
                    })
                    .setNegativeButton("Close", null).show());
        } else {
            TaskWiseStore.Event event = (TaskWiseStore.Event) item;
            row.setOnClickListener(v -> showEventActions(event));
        }
        return row;
    }
    private void showEventActions(TaskWiseStore.Event event) {
        new AlertDialog.Builder(this)
                .setTitle(event.title)
                .setMessage(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(event.at))
                .setPositiveButton("Delete", (d, w) -> new AlertDialog.Builder(this)
                        .setTitle("Delete event?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (d2, w2) -> {
                            TaskWiseStore.deleteEvent(this, event.id);
                            render();
                        }).show())
                .setNeutralButton("Close", null)
                .show();
    }
    private void showAllEvents() {
        ArrayList<String> lines = new ArrayList<>();
        for (TaskWiseStore.Task task : TaskWiseStore.getTasks(this)) {
            if (TaskWiseStore.sameDay(task.dueAt, selectedDate.getTimeInMillis()))
                lines.add(DateFormat.getTimeInstance(DateFormat.SHORT).format(task.dueAt) + "  " + task.title + " (task)");
        }
        for (TaskWiseStore.Event event : TaskWiseStore.getEvents(this)) {
            if (TaskWiseStore.sameDay(event.at, selectedDate.getTimeInMillis()))
                lines.add(DateFormat.getTimeInstance(DateFormat.SHORT).format(event.at) + "  " + event.title);
        }
        String message = lines.isEmpty() ? "No items on this date." : TextUtils.join("\n", lines);
        new AlertDialog.Builder(this).setTitle("Schedule").setMessage(message)
                .setPositiveButton("Close", null).show();
    }
    private void showAddEventDialog() {
        EditText input = new EditText(this);
        input.setHint("Event title");
        input.setSingleLine(true);
        new AlertDialog.Builder(this)
                .setTitle("Add New Event")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Choose time", (d, w) -> {
                    String title = input.getText().toString().trim();
                    if (title.isEmpty()) {
                        Toast.makeText(this, "Enter an event title first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Calendar time = (Calendar) selectedDate.clone();
                    new TimePickerDialog(this, (picker, hour, minute) -> {
                        time.set(Calendar.HOUR_OF_DAY, hour);
                        time.set(Calendar.MINUTE, minute);
                        time.set(Calendar.SECOND, 0);
                        time.set(Calendar.MILLISECOND, 0);
                        TaskWiseStore.upsertEvent(this,
                                new TaskWiseStore.Event(null, title, time.getTimeInMillis()));
                        render();
                        Toast.makeText(this, "Event saved.", Toast.LENGTH_SHORT).show();
                    }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), false).show();
                }).show();
    }
    private void bindBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> startActivity(new Intent(this, taskwise_home.class)));
        findViewById(R.id.navTasks).setOnClickListener(v -> startActivity(new Intent(this, taskwise_deadline.class)));
        findViewById(R.id.navMenu).setOnClickListener(v -> startActivity(new Intent(this, taskwise_reminder.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, taskwise_profile.class)));
        findViewById(R.id.navSubjects).setOnClickListener(v -> startActivity(new Intent(this, taskwise_subjects.class)));
    }
    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}