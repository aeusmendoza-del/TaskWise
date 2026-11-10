package taskwise;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
public final class ReminderScheduler {
    private ReminderScheduler() {}
    public static void schedule(Context context, TaskWiseStore.Reminder reminder) {
        if (reminder == null) return;
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarms == null) return;
        PendingIntent pending = pendingIntent(context, reminder);
        if (!reminder.enabled || reminder.at <= System.currentTimeMillis()) {
            alarms.cancel(pending);
            pending.cancel();
            return;
        }
        alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.at, pending);
    }
    public static void cancel(Context context, TaskWiseStore.Reminder reminder) {
        if (reminder == null) return;
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pending = pendingIntent(context, reminder);
        if (alarms != null) alarms.cancel(pending);
        pending.cancel();
    }
    private static PendingIntent pendingIntent(Context context, TaskWiseStore.Reminder reminder) {
        Intent intent = new Intent(context, ReminderAlarmReceiver.class);
        intent.putExtra("title", reminder.title);
        intent.putExtra("reminder_id", reminder.id);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(context, reminder.id.hashCode(), intent, flags);
    }
}