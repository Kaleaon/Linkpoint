// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.Debug;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;

public class SafeDrawerLayout extends DrawerLayout
{
    public SafeDrawerLayout(final Context context) {
        super(context);
    }
    
    public SafeDrawerLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public SafeDrawerLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        try {
            return super.onInterceptTouchEvent(motionEvent);
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
            return false;
        }
    }
}
