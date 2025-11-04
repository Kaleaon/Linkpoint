// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya;

import java.nio.ByteBuffer;
import android.util.Log;

public class Debug
{
    private static final String LOG_TAG = "Lumiya";
    
    public static void AlwaysPrintf(final String format, final Object... args) {
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        final String className = stackTraceElement.getClassName();
        Log.d("Lumiya", "[" + className.substring(className.lastIndexOf(46) + 1) + "::" + stackTraceElement.getMethodName() + "] " + String.format(format, args));
    }
    
    public static void DumpBuffer(final String s, final ByteBuffer byteBuffer) {
    }
    
    public static void DumpBuffer(final String s, final byte[] array) {
    }
    
    public static void DumpBuffer(final String s, final byte[] array, final int n) {
    }
    
    public static void Log(final String s) {
    }
    
    public static void Printf(final String s, final Object... array) {
    }
    
    public static void Warning(final Throwable t) {
    }
    
    public static boolean isDebugBuild() {
        return false;
    }
}
