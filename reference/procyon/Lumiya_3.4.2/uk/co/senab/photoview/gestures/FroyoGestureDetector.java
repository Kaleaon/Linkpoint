// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.gestures;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector$OnScaleGestureListener;
import android.content.Context;
import android.view.ScaleGestureDetector;
import android.annotation.TargetApi;

@TargetApi(8)
public class FroyoGestureDetector extends EclairGestureDetector
{
    protected final ScaleGestureDetector mDetector;
    
    public FroyoGestureDetector(final Context context) {
        super(context);
        this.mDetector = new ScaleGestureDetector(context, (ScaleGestureDetector$OnScaleGestureListener)new ScaleGestureDetector$OnScaleGestureListener() {
            public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
                final float scaleFactor = scaleGestureDetector.getScaleFactor();
                if (!Float.isNaN(scaleFactor) && !Float.isInfinite(scaleFactor)) {
                    FroyoGestureDetector.this.mListener.onScale(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                    return true;
                }
                return false;
            }
            
            public boolean onScaleBegin(final ScaleGestureDetector scaleGestureDetector) {
                return true;
            }
            
            public void onScaleEnd(final ScaleGestureDetector scaleGestureDetector) {
            }
        });
    }
    
    @Override
    public boolean isScaling() {
        return this.mDetector.isInProgress();
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        try {
            this.mDetector.onTouchEvent(motionEvent);
            return super.onTouchEvent(motionEvent);
        }
        catch (final IllegalArgumentException ex) {
            return true;
        }
    }
}
