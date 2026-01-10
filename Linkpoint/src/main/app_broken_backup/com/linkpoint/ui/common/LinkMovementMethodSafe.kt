package com.linkpoint.ui.common
import java.util.*

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView

class LinkMovementMethodSafe : LinkMovementMethod {
    private OnLinkOpenErrorListener onLinkOpenErrorListener = null

    interface OnLinkOpenErrorListener {
        fun OnLinkOpenError(String str)
    }

    fun onKeyDown(TextView textView, Spannable spannable, Int i, KeyEvent keyEvent): Boolean {
        try {
            return super.onKeyDown(textView, spannable, i, keyEvent)
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.")
            return false
        }
    }

    fun onKeyUp(TextView textView, Spannable spannable, Int i, KeyEvent keyEvent): Boolean {
        try {
            return super.onKeyUp(textView, spannable, i, keyEvent)
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.")
            return false
        }
    }

    fun onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent): Boolean {
        try {
            return super.onTouchEvent(textView, spannable, motionEvent)
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.")
            return false
        }
    }

    fun onTrackballEvent(TextView textView, Spannable spannable, MotionEvent motionEvent): Boolean {
        try {
            return super.onTrackballEvent(textView, spannable, motionEvent)
        } catch (Exception e) {
            if (this.onLinkOpenErrorListener == null) {
                return false
            }
            this.onLinkOpenErrorListener.OnLinkOpenError("Failed to open selected URL.")
            return false
        }
    }

    fun setOnLinkOpenErrorListener(OnLinkOpenErrorListener onLinkOpenErrorListener2)  {
        this.onLinkOpenErrorListener = onLinkOpenErrorListener2
    }
}
