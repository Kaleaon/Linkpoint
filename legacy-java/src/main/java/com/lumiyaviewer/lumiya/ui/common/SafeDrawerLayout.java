package com.lumiyaviewer.lumiya.ui.common;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.lumiyaviewer.lumiya.Debug;

/* loaded from: classes.dex */
public class SafeDrawerLayout extends DrawerLayout {
    public SafeDrawerLayout(Context context) {
        super(context);
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SafeDrawerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.support.v4.widget.DrawerLayout, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        try {
            return super.onInterceptTouchEvent(motionEvent);
        } catch (Exception e) {
            Debug.Warning(e);
            return false;
        }
    }
}
