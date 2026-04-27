package com.lumiyaviewer.lumiya.ui.common;

import android.view.MotionEvent;

public interface OnInterceptTouchEventListener {
    boolean dispatchTouchEvent(MotionEvent motionEvent);

    boolean onInterceptTouchEvent(MotionEvent motionEvent);

    boolean onTouchEvent(MotionEvent motionEvent);
}
