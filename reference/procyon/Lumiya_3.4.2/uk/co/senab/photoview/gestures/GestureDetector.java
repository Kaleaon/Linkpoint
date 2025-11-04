// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.gestures;

import android.view.MotionEvent;

public interface GestureDetector
{
    boolean isDragging();
    
    boolean isScaling();
    
    boolean onTouchEvent(final MotionEvent p0);
    
    void setOnGestureListener(final OnGestureListener p0);
}
