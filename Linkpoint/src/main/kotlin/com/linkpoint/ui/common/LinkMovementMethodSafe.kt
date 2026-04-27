package com.linkpoint.ui.common
import java.util.*

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView

class LinkMovementMethodSafe : LinkMovementMethod() {
    private OnLinkOpenErrorListener onLinkOpenErrorListener = null

    interface OnLinkOpenErrorListener {
        fun OnLinkOpenError(str: String)
    }

     public override fun onKeyDown(textView: TextView, spannable: Spannable, i: Int, keyEvent: KeyEvent): Boolean {
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

     public override fun onKeyUp(textView: TextView, spannable: Spannable, i: Int, keyEvent: KeyEvent): Boolean {
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

     public override fun onTouchEvent(textView: TextView, spannable: Spannable, motionEvent: MotionEvent): Boolean {
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

     public fun onTrackballEvent(textView: TextView, spannable: Spannable, motionEvent: MotionEvent): Boolean {
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

    fun setOnLinkOpenErrorListener(onLinkOpenErrorListener2: OnLinkOpenErrorListener) {
        this.onLinkOpenErrorListener = onLinkOpenErrorListener2
    }
}
