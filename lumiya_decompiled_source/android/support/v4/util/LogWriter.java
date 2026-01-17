package android.support.v4.util;

import android.support.annotation.RestrictTo;
import android.util.Log;
import java.io.Writer;
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* loaded from: classes.dex */
public class LogWriter extends Writer {
    private StringBuilder mBuilder = new StringBuilder(128);
    private final String mTag;

    public LogWriter(String str) {
        this.mTag = str;
    }

    private void flushBuilder() {
        if (this.mBuilder.length() <= 0) {
            return;
        }
        Log.d(this.mTag, this.mBuilder.toString());
        this.mBuilder.delete(0, this.mBuilder.length());
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        flushBuilder();
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
        flushBuilder();
    }

    @Override // java.io.Writer
    public void write(char[] cArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            char c = cArr[i + i3];
            if (c != '\n') {
                this.mBuilder.append(c);
            } else {
                flushBuilder();
            }
        }
    }
}
