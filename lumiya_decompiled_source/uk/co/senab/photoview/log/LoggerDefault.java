package uk.co.senab.photoview.log;

import android.util.Log;
/* loaded from: classes.dex */
public class LoggerDefault implements Logger {
    @Override // uk.co.senab.photoview.log.Logger
    public int d(String str, String str2) {
        return Log.d(str, str2);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int d(String str, String str2, Throwable th) {
        return Log.d(str, str2, th);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int e(String str, String str2) {
        return Log.e(str, str2);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int e(String str, String str2, Throwable th) {
        return Log.e(str, str2, th);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int i(String str, String str2) {
        return Log.i(str, str2);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int i(String str, String str2, Throwable th) {
        return Log.i(str, str2, th);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int v(String str, String str2) {
        return Log.v(str, str2);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int v(String str, String str2, Throwable th) {
        return Log.v(str, str2, th);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int w(String str, String str2) {
        return Log.w(str, str2);
    }

    @Override // uk.co.senab.photoview.log.Logger
    public int w(String str, String str2, Throwable th) {
        return Log.w(str, str2, th);
    }
}
