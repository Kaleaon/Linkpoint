// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.MotionEvent;

public interface OnInterceptTouchEventListener
{
    boolean dispatchTouchEvent(final MotionEvent p0);
    
    boolean onInterceptTouchEvent(final MotionEvent p0);
    
    boolean onTouchEvent(final MotionEvent p0);
}
