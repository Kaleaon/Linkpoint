// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.os.Build$VERSION;
import android.app.Service;

public final class ServiceCompat
{
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;
    
    private ServiceCompat() {
    }
    
    public static void stopForeground(final Service service, final int n) {
        boolean b = false;
        if (Build$VERSION.SDK_INT < 24) {
            if ((n & 0x1) != 0x0) {
                b = true;
            }
            service.stopForeground(b);
        }
        else {
            service.stopForeground(n);
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public @interface StopForegroundFlags {
    }
}
