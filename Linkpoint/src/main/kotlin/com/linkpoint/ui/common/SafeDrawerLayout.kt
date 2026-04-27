package com.linkpoint.ui.common

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import com.linkpoint.Debug

class SafeDrawerLayout : DrawerLayout() {
    public SafeDrawerLayout(Context context) {
        super(context)
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

     public fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        try {
            return super.onInterceptTouchEvent(motionEvent)
        } catch (Exception e) {
            Debug.Warning(e)
            return false
        }
    }
}
