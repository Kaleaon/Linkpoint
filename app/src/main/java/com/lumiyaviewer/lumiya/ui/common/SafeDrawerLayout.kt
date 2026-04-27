package com.lumiyaviewer.lumiya.ui.common

import android.content.Context
import androidx.core.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import com.lumiyaviewer.lumiya.Debug

class SafeDrawerLayout : DrawerLayout {
    SafeDrawerLayout(Context context) {
        super(context)
    }

    SafeDrawerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet)
    }

    SafeDrawerLayout(Context context, AttributeSet attributeSet, Int i) {
        super(context, attributeSet, i)
    }

    Boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onInterceptTouchEvent(motionEvent)
        } catch (Exception e) {
            Debug.Warning(e)
            return false
        }
    }
}
