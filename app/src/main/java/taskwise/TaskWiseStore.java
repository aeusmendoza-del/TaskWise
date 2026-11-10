package taskwise;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
public final class TaskWiseStore {
    private static final String PREFS = "taskwise_data";
    private static final String TASKS = "tasks";
    private static final String EVENTS = "events";
    private static final String REMINDERS = "reminders";
    private TaskWiseStore() {}
    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
    public static final class Task {
        public String id;
        public String title;
        public String subject;
        public long dueAt;
        public String priority;
        public boolean completed;
        public Task(String id, String title, String subject, long dueAt, String priority, boolean completed) {
            this.id = id == null || id.isEmpty() ? newId() : id;
            this.title = title == null ? "" : title;
            this.subject = subject == null ? "" : subject;
            this.dueAt = dueAt;
            this.priority = priority == null || priority.isEmpty() ? "Medium" : priority;
            this.completed = completed;
        }
    }
    public static final class Event {
        public String id;
        public String title;
        public long at;
        public Event(String id, String title, long at) {
            this.id = id == null || id.isEmpty() ? newId() : id;
            this.title = title == null ? "" : title;
            this.at = at;
        }
    }
    public static final class Reminder {
        public String id;
        public String title;
        public long at;
        public boolean enabled;
        public Reminder(String id, String title, long at, boolean enabled) {
            this.id = id == null || id.isEmpty() ? newId() : id;
            this.title = title == null ? "" : title;
            this.at = at;
            this.enabled = enabled;
        }
    }
    public static String newId() {
        return UUID.randomUUID().toString();
    }
    public static long dayAt(int offsetDays, int hour, int minute) {
        Calendar value = Calendar.getInstance();
        value.add(Calendar.DAY_OF_YEAR, offsetDays);
        value.set(Calendar.HOUR_OF_DAY, hour);
        value.set(Calendar.MINUTE, minute);
        value.set(Calendar.SECOND, 0);
        value.set(Calendar.MILLISECOND, 0);
        return value.getTimeInMillis();
    }
    public static void initialize(Context context) {
        SharedPreferences p = prefs(context);
        if (!p.contains(TASKS)) {
            ArrayList<Task> sample = new ArrayList<>();
            sample.add(new Task(newId(), "Work Sheet 5", "Computer Programming", dayAt(2, 17, 0), "High", false));
            sample.add(new Task(newId(), "Video", "General Science", dayAt(5, 17, 0), "Medium", false));
            sample.add(new Task(newId(), "Quiz 3", "Effective Communication", dayAt(0, 17, 0), "High", false));
            sample.add(new Task(newId(), "Assignment", "General Mathematics", dayAt(4, 17, 0), "Medium", false));
            saveTasks(context, sample);
        }
        if (!p.contains(EVENTS)) p.edit().putString(EVENTS, "[]").apply();
        if (!p.contains(REMINDERS)) p.edit().putString(REMINDERS, "[]").apply();
    }
    public static List<Task> getTasks(Context context) {
        ArrayList<Task> result = new ArrayList<>();
        try {
            JSONArray values = new JSONArray(prefs(context).getString(TASKS, "[]"));
            for (int i = 0; i < values.length(); i++) {
                JSONObject item = values.optJSONObject(i);
                if (item == null) continue;
                String title = item.optString("title", "").trim();
                if (!isValidTaskTitle(title)) continue;
                result.add(new Task(item.optString("id"), title,
                        item.optString("subject"), item.optLong("dueAt"),
                        item.optString("priority", "Medium"), item.optBoolean("completed")));
            }
        } catch (Exception ignored) { }
        return result;
    }
    public static void saveTasks(Context context, List<Task> tasks) {
        JSONArray values = new JSONArray();
        try {
            for (Task task : tasks) {
                if (task == null || !isValidTaskTitle(task.title)) continue;
                JSONObject item = new JSONObject();
                item.put("id", task.id);
                item.put("title", task.title);
                item.put("subject", task.subject);
                item.put("dueAt", task.dueAt);
                item.put("priority", task.priority);
                item.put("completed", task.completed);
                values.put(item);
            }
        } catch (Exception ignored) { }
        prefs(context).edit().putString(TASKS, values.toString()).apply();
    }
    public static void upsertTask(Context context, Task task) {
        if (task == null || !isValidTaskTitle(task.title)) return;
        ArrayList<Task> values = new ArrayList<>(getTasks(context));
        boolean replaced = false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).id.equals(task.id)) {
                values.set(i, task);
                replaced = true;
                break;
            }
        }
        if (!replaced) values.add(0, task);
        saveTasks(context, values);
    }
    public static void deleteTask(Context context, String id) {
        ArrayList<Task> values = new ArrayList<>(getTasks(context));
        for (int i = values.size() - 1; i >= 0; i--) {
            if (values.get(i).id.equals(id)) values.remove(i);
        }
        saveTasks(context, values);
    }
    public static List<Event> getEvents(Context context) {
        ArrayList<Event> result = new ArrayList<>();
        try {
            JSONArray values = new JSONArray(prefs(context).getString(EVENTS, "[]"));
            for (int i = 0; i < values.length(); i++) {
                JSONObject item = values.optJSONObject(i);
                if (item != null) result.add(new Event(item.optString("id"),
                        item.optString("title"), item.optLong("at")));
            }
        } catch (Exception ignored) { }
        return result;
    }
    public static void saveEvents(Context context, List<Event> events) {
        JSONArray values = new JSONArray();
        try {
            for (Event event : events) {
                JSONObject item = new JSONObject();
                item.put("id", event.id);
                item.put("title", event.title);
                item.put("at", event.at);
                values.put(item);
            }
        } catch (Exception ignored) { }
        prefs(context).edit().putString(EVENTS, values.toString()).apply();
    }
    public static void upsertEvent(Context context, Event event) {
        ArrayList<Event> values = new ArrayList<>(getEvents(context));
        boolean replaced = false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).id.equals(event.id)) {
                values.set(i, event);
                replaced = true;
                break;
            }
        }
        if (!replaced) values.add(event);
        saveEvents(context, values);
    }
    public static void deleteEvent(Context context, String id) {
        ArrayList<Event> values = new ArrayList<>(getEvents(context));
        for (int i = values.size() - 1; i >= 0; i--) {
            if (values.get(i).id.equals(id)) values.remove(i);
        }
        saveEvents(context, values);
    }
    public static List<Reminder> getReminders(Context context) {
        ArrayList<Reminder> result = new ArrayList<>();
        try {
            JSONArray values = new JSONArray(prefs(context).getString(REMINDERS, "[]"));
            for (int i = 0; i < values.length(); i++) {
                JSONObject item = values.optJSONObject(i);
                if (item != null) result.add(new Reminder(item.optString("id"),
                        item.optString("title"), item.optLong("at"), item.optBoolean("enabled", true)));
            }
        } catch (Exception ignored) { }
        return result;
    }
    public static void saveReminders(Context context, List<Reminder> reminders) {
        JSONArray values = new JSONArray();
        try {
            for (Reminder reminder : reminders) {
                JSONObject item = new JSONObject();
                item.put("id", reminder.id);
                item.put("title", reminder.title);
                item.put("at", reminder.at);
                item.put("enabled", reminder.enabled);
                values.put(item);
            }
        } catch (Exception ignored) { }
        prefs(context).edit().putString(REMINDERS, values.toString()).apply();
    }
    public static void upsertReminder(Context context, Reminder reminder) {
        ArrayList<Reminder> values = new ArrayList<>(getReminders(context));
        boolean replaced = false;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).id.equals(reminder.id)) {
                values.set(i, reminder);
                replaced = true;
                break;
            }
        }
        if (!replaced) values.add(reminder);
        saveReminders(context, values);
    }
    public static void deleteReminder(Context context, String id) {
        ArrayList<Reminder> values = new ArrayList<>(getReminders(context));
        for (int i = values.size() - 1; i >= 0; i--) {
            if (values.get(i).id.equals(id)) values.remove(i);
        }
        saveReminders(context, values);
    }
    public static boolean sameDay(long left, long right) {
        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        a.setTimeInMillis(left);
        b.setTimeInMillis(right);
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
    public static long startOfDay(long time) {
        Calendar value = Calendar.getInstance();
        value.setTimeInMillis(time);
        value.set(Calendar.HOUR_OF_DAY, 0);
        value.set(Calendar.MINUTE, 0);
        value.set(Calendar.SECOND, 0);
        value.set(Calendar.MILLISECOND, 0);
        return value.getTimeInMillis();
    }
    private static boolean isValidTaskTitle(String title) {
        if (title == null) return false;
        String value = title.trim();
        if (value.isEmpty()) return false;
        if (value.length() < 10 || !Character.isLetterOrDigit(value.charAt(0))) return true;
        char first = Character.toLowerCase(value.charAt(0));
        for (int i = 1; i < value.length(); i++) {
            if (!Character.isLetterOrDigit(value.charAt(i))
                    || Character.toLowerCase(value.charAt(i)) != first) return true;
        }
        return false;
    }
}
