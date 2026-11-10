package taskwise;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class ReminderBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            TaskWiseStore.initialize(context);
            for (TaskWiseStore.Reminder reminder : TaskWiseStore.getReminders(context)) {
                ReminderScheduler.schedule(context, reminder);
            }
        }
    }
}