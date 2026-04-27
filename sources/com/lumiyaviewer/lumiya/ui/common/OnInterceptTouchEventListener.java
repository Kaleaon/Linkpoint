// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.MotionEvent;

public interface OnInterceptTouchEventListener
{

    public abstract boolean dispatchTouchEvent(MotionEvent motionevent);

    public abstract boolean onInterceptTouchEvent(MotionEvent motionevent);

    public abstract boolean onTouchEvent(MotionEvent motionevent);
}
