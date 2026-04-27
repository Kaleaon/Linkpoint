// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.log;

public interface Logger
{
    int d(final String p0, final String p1);
    
    int d(final String p0, final String p1, final Throwable p2);
    
    int e(final String p0, final String p1);
    
    int e(final String p0, final String p1, final Throwable p2);
    
    int i(final String p0, final String p1);
    
    int i(final String p0, final String p1, final Throwable p2);
    
    int v(final String p0, final String p1);
    
    int v(final String p0, final String p1, final Throwable p2);
    
    int w(final String p0, final String p1);
    
    int w(final String p0, final String p1, final Throwable p2);
}
