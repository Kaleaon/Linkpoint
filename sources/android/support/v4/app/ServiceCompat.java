package android.support.v4.app;

import android.app.Service;
import android.os.Build;
import android.support.annotation.RestrictTo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ServiceCompat {
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StopForegroundFlags {
    }

    private ServiceCompat() {
    }

    public static void stopForeground(Service service, int i) {
        boolean z = false;
        if (Build.VERSION.SDK_INT < 24) {
            if ((i & 1) != 0) {
                z = true;
            }
            service.stopForeground(z);
            return;
        }
        service.stopForeground(i);
    }
}
