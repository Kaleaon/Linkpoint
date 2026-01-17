package android.support.v4.os;

import android.os.Build;
import android.os.Trace;
/* loaded from: classes.dex */
public final class TraceCompat {
    private TraceCompat() {
    }

    public static void beginSection(String str) {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        Trace.beginSection(str);
    }

    public static void endSection() {
        if (Build.VERSION.SDK_INT < 18) {
            return;
        }
        Trace.endSection();
    }
}
