// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.log;

import android.util.Log;

public class LoggerDefault implements Logger
{
    @Override
    public int d(final String s, final String s2) {
        return Log.d(s, s2);
    }
    
    @Override
    public int d(final String s, final String s2, final Throwable t) {
        return Log.d(s, s2, t);
    }
    
    @Override
    public int e(final String s, final String s2) {
        return Log.e(s, s2);
    }
    
    @Override
    public int e(final String s, final String s2, final Throwable t) {
        return Log.e(s, s2, t);
    }
    
    @Override
    public int i(final String s, final String s2) {
        return Log.i(s, s2);
    }
    
    @Override
    public int i(final String s, final String s2, final Throwable t) {
        return Log.i(s, s2, t);
    }
    
    @Override
    public int v(final String s, final String s2) {
        return Log.v(s, s2);
    }
    
    @Override
    public int v(final String s, final String s2, final Throwable t) {
        return Log.v(s, s2, t);
    }
    
    @Override
    public int w(final String s, final String s2) {
        return Log.w(s, s2);
    }
    
    @Override
    public int w(final String s, final String s2, final Throwable t) {
        return Log.w(s, s2, t);
    }
}
