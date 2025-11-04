// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.app.AlarmManager$AlarmClockInfo;
import android.os.Build$VERSION;
import android.app.PendingIntent;
import android.app.AlarmManager;

public final class AlarmManagerCompat
{
    private AlarmManagerCompat() {
    }
    
    public static void setAlarmClock(final AlarmManager alarmManager, final long n, final PendingIntent pendingIntent, final PendingIntent pendingIntent2) {
        if (Build$VERSION.SDK_INT < 21) {
            setExact(alarmManager, 0, n, pendingIntent2);
        }
        else {
            alarmManager.setAlarmClock(new AlarmManager$AlarmClockInfo(n, pendingIntent), pendingIntent2);
        }
    }
    
    public static void setAndAllowWhileIdle(final AlarmManager alarmManager, final int n, final long n2, final PendingIntent pendingIntent) {
        if (Build$VERSION.SDK_INT < 23) {
            alarmManager.set(n, n2, pendingIntent);
        }
        else {
            alarmManager.setAndAllowWhileIdle(n, n2, pendingIntent);
        }
    }
    
    public static void setExact(final AlarmManager alarmManager, final int n, final long n2, final PendingIntent pendingIntent) {
        if (Build$VERSION.SDK_INT < 19) {
            alarmManager.set(n, n2, pendingIntent);
        }
        else {
            alarmManager.setExact(n, n2, pendingIntent);
        }
    }
    
    public static void setExactAndAllowWhileIdle(final AlarmManager alarmManager, final int n, final long n2, final PendingIntent pendingIntent) {
        if (Build$VERSION.SDK_INT < 23) {
            setExact(alarmManager, n, n2, pendingIntent);
        }
        else {
            alarmManager.setExactAndAllowWhileIdle(n, n2, pendingIntent);
        }
    }
}
