// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import android.util.Log;

public class DaoLog
{
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    private static final String TAG = "greenDAO";
    public static final int VERBOSE = 2;
    public static final int WARN = 5;
    
    public static int d(final String s) {
        return Log.d("greenDAO", s);
    }
    
    public static int d(final String s, final Throwable t) {
        return Log.d("greenDAO", s, t);
    }
    
    public static int e(final String s) {
        return Log.w("greenDAO", s);
    }
    
    public static int e(final String s, final Throwable t) {
        return Log.e("greenDAO", s, t);
    }
    
    public static String getStackTraceString(final Throwable t) {
        return Log.getStackTraceString(t);
    }
    
    public static int i(final String s) {
        return Log.i("greenDAO", s);
    }
    
    public static int i(final String s, final Throwable t) {
        return Log.i("greenDAO", s, t);
    }
    
    public static boolean isLoggable(final int n) {
        return Log.isLoggable("greenDAO", n);
    }
    
    public static int println(final int n, final String s) {
        return Log.println(n, "greenDAO", s);
    }
    
    public static int v(final String s) {
        return Log.v("greenDAO", s);
    }
    
    public static int v(final String s, final Throwable t) {
        return Log.v("greenDAO", s, t);
    }
    
    public static int w(final String s) {
        return Log.w("greenDAO", s);
    }
    
    public static int w(final String s, final Throwable t) {
        return Log.w("greenDAO", s, t);
    }
    
    public static int w(final Throwable t) {
        return Log.w("greenDAO", t);
    }
}
