package android.support.v4.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

public final class AlarmManagerCompat {
    private AlarmManagerCompat() {
    }

    public static void setAlarmClock(AlarmManager alarmManager, long j, PendingIntent pendingIntent, PendingIntent pendingIntent2) {
        if (Build.VERSION.SDK_INT < 21) {
            setExact(alarmManager, 0, j, pendingIntent2);
        } else {
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(j, pendingIntent), pendingIntent2);
        }
    }

    public static void setAndAllowWhileIdle(AlarmManager alarmManager, int i, long j, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT < 23) {
            alarmManager.set(i, j, pendingIntent);
        } else {
            alarmManager.setAndAllowWhileIdle(i, j, pendingIntent);
        }
    }

    public static void setExact(AlarmManager alarmManager, int i, long j, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.set(i, j, pendingIntent);
        } else {
            alarmManager.setExact(i, j, pendingIntent);
        }
    }

    public static void setExactAndAllowWhileIdle(AlarmManager alarmManager, int i, long j, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT < 23) {
            setExact(alarmManager, i, j, pendingIntent);
        } else {
            alarmManager.setExactAndAllowWhileIdle(i, j, pendingIntent);
        }
    }
}
