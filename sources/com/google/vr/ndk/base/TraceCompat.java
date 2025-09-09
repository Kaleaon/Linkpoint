package com.google.vr.ndk.base;

import android.os.Build;
import android.os.Trace;

class TraceCompat {
    TraceCompat() {
    }

    static void beginSection(String str) {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(str);
        }
    }

    static void endSection() {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.endSection();
        }
    }
}
