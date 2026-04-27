// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.lumiyaviewer.lumiya.Debug;

public class SafeDrawerLayout extends DrawerLayout
{

    public SafeDrawerLayout(Context context)
    {
        super(context);
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        try
        {
            flag = super.onInterceptTouchEvent(motionevent);
        }
        // Misplaced declaration of an exception variable
        catch (MotionEvent motionevent)
        {
            Debug.Warning(motionevent);
            return false;
        }
        return flag;
    }
}
