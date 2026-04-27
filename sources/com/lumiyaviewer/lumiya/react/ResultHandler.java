// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;


public interface ResultHandler
{

    public abstract void onResultData(Object obj, Object obj1);

    public abstract void onResultError(Object obj, Throwable throwable);
}
