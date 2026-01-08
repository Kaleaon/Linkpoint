package com.linkpoint.ui.common

import android.view.MotionEvent

interface OnInterceptTouchEventListener {
    fun dispatchTouchEvent(event: MotionEvent): Boolean

    fun onInterceptTouchEvent(event: MotionEvent): Boolean

    fun onTouchEvent(event: MotionEvent): Boolean
}
