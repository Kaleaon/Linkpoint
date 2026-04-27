package com.lumiyaviewer.lumiya.ui.common;
import java.util.*;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkMovementMethodSafe extends LinkMovementMethod {
    private OnLinkOpenErrorListener onLinkOpenErrorListener = null;

    public interface OnLinkOpenErrorListener {
        void OnLinkOpenError(String str);
    }

    public boolean onKeyDown(TextView textView, Spannable spannable, int i, KeyEvent keyEvent) {
        try {
            return super.onKeyDown(textView, spannable, i, keyEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false;
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            return false;
        }
    }

    public boolean onKeyUp(TextView textView, Spannable spannable, int i, KeyEvent keyEvent) {
        try {
            return super.onKeyUp(textView, spannable, i, keyEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false;
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            return false;
        }
    }

    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        try {
            return super.onTouchEvent(textView, spannable, motionEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false;
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            return false;
        }
    }

    public boolean onTrackballEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        try {
            return super.onTrackballEvent(textView, spannable, motionEvent);
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false;
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.");
            return false;
        }
    }

    public void setOnLinkOpenErrorListener(OnLinkOpenErrorListener onLinkOpenErrorListener2) {
        this.onLinkOpenErrorListener = onLinkOpenErrorListener2;
    }
}
