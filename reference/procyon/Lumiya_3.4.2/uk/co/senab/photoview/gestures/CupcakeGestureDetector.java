// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.gestures;

import uk.co.senab.photoview.log.LogManager;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.content.Context;
import android.view.VelocityTracker;

public class CupcakeGestureDetector implements GestureDetector
{
    private static final String LOG_TAG = "CupcakeGestureDetector";
    private boolean mIsDragging;
    float mLastTouchX;
    float mLastTouchY;
    protected OnGestureListener mListener;
    final float mMinimumVelocity;
    final float mTouchSlop;
    private VelocityTracker mVelocityTracker;
    
    public CupcakeGestureDetector(final Context context) {
        final ViewConfiguration value = ViewConfiguration.get(context);
        this.mMinimumVelocity = (float)value.getScaledMinimumFlingVelocity();
        this.mTouchSlop = (float)value.getScaledTouchSlop();
    }
    
    float getActiveX(final MotionEvent motionEvent) {
        return motionEvent.getX();
    }
    
    float getActiveY(final MotionEvent motionEvent) {
        return motionEvent.getY();
    }
    
    @Override
    public boolean isDragging() {
        return this.mIsDragging;
    }
    
    @Override
    public boolean isScaling() {
        return false;
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        boolean mIsDragging = false;
        switch (motionEvent.getAction()) {
            case 0: {
                this.mVelocityTracker = VelocityTracker.obtain();
                if (this.mVelocityTracker == null) {
                    LogManager.getLogger().i("CupcakeGestureDetector", "Velocity tracker is null");
                }
                else {
                    this.mVelocityTracker.addMovement(motionEvent);
                }
                this.mLastTouchX = this.getActiveX(motionEvent);
                this.mLastTouchY = this.getActiveY(motionEvent);
                this.mIsDragging = false;
                break;
            }
            case 2: {
                final float activeX = this.getActiveX(motionEvent);
                final float activeY = this.getActiveY(motionEvent);
                final float n = activeX - this.mLastTouchX;
                final float n2 = activeY - this.mLastTouchY;
                if (!this.mIsDragging) {
                    if (Math.sqrt(n * n + n2 * n2) >= this.mTouchSlop) {
                        mIsDragging = true;
                    }
                    this.mIsDragging = mIsDragging;
                }
                if (!this.mIsDragging) {
                    break;
                }
                this.mListener.onDrag(n, n2);
                this.mLastTouchX = activeX;
                this.mLastTouchY = activeY;
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(motionEvent);
                    break;
                }
                break;
            }
            case 3: {
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    break;
                }
                break;
            }
            case 1: {
                if (this.mIsDragging && this.mVelocityTracker != null) {
                    this.mLastTouchX = this.getActiveX(motionEvent);
                    this.mLastTouchY = this.getActiveY(motionEvent);
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    final float xVelocity = this.mVelocityTracker.getXVelocity();
                    final float yVelocity = this.mVelocityTracker.getYVelocity();
                    if (Math.max(Math.abs(xVelocity), Math.abs(yVelocity)) >= this.mMinimumVelocity) {
                        this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -xVelocity, -yVelocity);
                    }
                }
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    break;
                }
                break;
            }
        }
        return true;
    }
    
    @Override
    public void setOnGestureListener(final OnGestureListener mListener) {
        this.mListener = mListener;
    }
}
