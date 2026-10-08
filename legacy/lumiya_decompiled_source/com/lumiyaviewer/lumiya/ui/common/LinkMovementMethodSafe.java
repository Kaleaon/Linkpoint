package com.lumiyaviewer.lumiya.ui.common;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
/* loaded from: classes.dex */
public class LinkMovementMethodSafe extends LinkMovementMethod {
    private OnLinkOpenErrorListener onLinkOpenErrorListener = null;

    /* loaded from: classes.dex */
    public interface OnLinkOpenErrorListener {
        void OnLinkOpenError(String str);
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onKeyDown(TextView textView, Spannable spannable, int i, KeyEvent keyEvent) {
        try {
            return super.onKeyDown(textView, spannable, i, keyEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
                return false;
            }
            return false;
        }
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onKeyUp(TextView textView, Spannable spannable, int i, KeyEvent keyEvent) {
        try {
            return super.onKeyUp(textView, spannable, i, keyEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
                return false;
            }
            return false;
        }
    }

    @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(textView, spannable, motionEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
                return false;
            }
            return false;
        }
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onTrackballEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        try {
            return super.onTrackballEvent(textView, spannable, motionEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener != null) {
                this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
                return false;
            }
            return false;
        }
    }

    public void setOnLinkOpenErrorListener(OnLinkOpenErrorListener onLinkOpenErrorListener) {
        this.onLinkOpenErrorListener = onLinkOpenErrorListener;
    }
}
