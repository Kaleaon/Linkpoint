package com.linkpoint.ui.common

import android.view.MotionEvent

interface OnInterceptTouchEventListener {
    fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean
    fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean
    fun onTouchEvent(motionEvent: MotionEvent): Boolean
}