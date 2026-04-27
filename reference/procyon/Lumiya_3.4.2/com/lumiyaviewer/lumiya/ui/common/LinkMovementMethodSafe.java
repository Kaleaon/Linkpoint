// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.MotionEvent;
import android.view.KeyEvent;
import android.text.Spannable;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;

public class LinkMovementMethodSafe extends LinkMovementMethod
{
    private OnLinkOpenErrorListener onLinkOpenErrorListener;
    
    public LinkMovementMethodSafe() {
        this.onLinkOpenErrorListener = null;
    }
    
    public boolean onKeyDown(final TextView textView, final Spannable spannable, final int n, final KeyEvent keyEvent) {
        try {
            return super.onKeyDown(textView, spannable, n, keyEvent);
        }
        catch (final Exception ex) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
    }
    
    public boolean onKeyUp(final TextView textView, final Spannable spannable, final int n, final KeyEvent keyEvent) {
        try {
            return super.onKeyUp(textView, spannable, n, keyEvent);
        }
        catch (final Exception ex) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
    }
    
    public boolean onTouchEvent(final TextView textView, final Spannable spannable, final MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(textView, spannable, motionEvent);
        }
        catch (final Exception ex) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
    }
    
    public boolean onTrackballEvent(final TextView textView, final Spannable spannable, final MotionEvent motionEvent) {
        try {
            return super.onTrackballEvent(textView, spannable, motionEvent);
        }
        catch (final Exception ex) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            }
            return false;
        }
    }
    
    public void setOnLinkOpenErrorListener(final OnLinkOpenErrorListener onLinkOpenErrorListener) {
        this.onLinkOpenErrorListener = onLinkOpenErrorListener;
    }
    
    public interface OnLinkOpenErrorListener
    {
        void OnLinkOpenError(final String p0);
    }
}
