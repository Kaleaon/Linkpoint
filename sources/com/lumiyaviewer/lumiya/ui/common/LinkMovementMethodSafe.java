// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkMovementMethodSafe extends LinkMovementMethod
{
    public static interface OnLinkOpenErrorListener
    {

        public abstract void OnLinkOpenError(String s);
    }


    private OnLinkOpenErrorListener onLinkOpenErrorListener;

    public LinkMovementMethodSafe()
    {
        onLinkOpenErrorListener = null;
    }

    public boolean onKeyDown(TextView textview, Spannable spannable, int i, KeyEvent keyevent)
    {
        boolean flag;
        try
        {
            flag = super.onKeyDown(textview, spannable, i, keyevent);
        }
        // Misplaced declaration of an exception variable
        catch (TextView textview)
        {
            if (onLinkOpenErrorListener != null)
            {
                onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
        return flag;
    }

    public boolean onKeyUp(TextView textview, Spannable spannable, int i, KeyEvent keyevent)
    {
        boolean flag;
        try
        {
            flag = super.onKeyUp(textview, spannable, i, keyevent);
        }
        // Misplaced declaration of an exception variable
        catch (TextView textview)
        {
            if (onLinkOpenErrorListener != null)
            {
                onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
        return flag;
    }

    public boolean onTouchEvent(TextView textview, Spannable spannable, MotionEvent motionevent)
    {
        boolean flag;
        try
        {
            flag = super.onTouchEvent(textview, spannable, motionevent);
        }
        // Misplaced declaration of an exception variable
        catch (TextView textview)
        {
            if (onLinkOpenErrorListener != null)
            {
                onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
        return flag;
    }

    public boolean onTrackballEvent(TextView textview, Spannable spannable, MotionEvent motionevent)
    {
        boolean flag;
        try
        {
            flag = super.onTrackballEvent(textview, spannable, motionevent);
        }
        // Misplaced declaration of an exception variable
        catch (TextView textview)
        {
            if (onLinkOpenErrorListener != null)
            {
                onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
        return flag;
    }

    public void setOnLinkOpenErrorListener(OnLinkOpenErrorListener onlinkopenerrorlistener)
    {
        onLinkOpenErrorListener = onlinkopenerrorlistener;
    }
}
