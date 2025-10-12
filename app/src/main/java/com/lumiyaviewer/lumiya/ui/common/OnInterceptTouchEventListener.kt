package com.lumiyaviewer.lumiya.ui.common

import android.view.MotionEvent

interface OnInterceptTouchEventListener {
    fun dispatchTouchEvent(event: MotionEvent): Boolean
    fun onInterceptTouchEvent(event: MotionEvent): Boolean
    fun onTouchEvent(event: MotionEvent): Boolean
}