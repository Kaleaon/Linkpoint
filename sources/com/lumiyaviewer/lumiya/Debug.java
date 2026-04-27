// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.util.Log;
import java.nio.ByteBuffer;

public class Debug
{

    private static final String LOG_TAG = "Lumiya";

    public Debug()
    {
    }

    public static transient void AlwaysPrintf(String s, Object aobj[])
    {
        StackTraceElement stacktraceelement = Thread.currentThread().getStackTrace()[3];
        String s1 = stacktraceelement.getClassName();
        s1 = s1.substring(s1.lastIndexOf('.') + 1);
        android.util.Log.d("Lumiya", (new StringBuilder()).append("[").append(s1).append("::").append(stacktraceelement.getMethodName()).append("] ").append(String.format(s, aobj)).toString());
    }

    public static void DumpBuffer(String s, ByteBuffer bytebuffer)
    {
    }

    public static void DumpBuffer(String s, byte abyte0[])
    {
    }

    public static void DumpBuffer(String s, byte abyte0[], int i)
    {
    }

    public static void Log(String s)
    {
    }

    public static transient void Printf(String s, Object aobj[])
    {
    }

    public static void Warning(Throwable throwable)
    {
    }

    public static boolean isDebugBuild()
    {
        return false;
    }
}
