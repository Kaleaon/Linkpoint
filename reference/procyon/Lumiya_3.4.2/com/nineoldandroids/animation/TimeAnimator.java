// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

public class TimeAnimator extends ValueAnimator
{
    private TimeListener mListener;
    private long mPreviousTime;
    
    public TimeAnimator() {
        this.mPreviousTime = -1L;
    }
    
    @Override
    void animateValue(final float n) {
    }
    
    @Override
    boolean animationFrame(final long n) {
        long n2 = 0L;
        final int n3 = 1;
        if (this.mPlayingState == 0) {
            this.mPlayingState = 1;
            int n4;
            if (this.mSeekTime >= 0L) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 == 0) {
                this.mStartTime = n;
            }
            else {
                this.mStartTime = n - this.mSeekTime;
                this.mSeekTime = -1L;
            }
        }
        if (this.mListener != null) {
            final long mStartTime = this.mStartTime;
            int n5;
            if (this.mPreviousTime >= 0L) {
                n5 = n3;
            }
            else {
                n5 = 0;
            }
            if (n5 != 0) {
                n2 = n - this.mPreviousTime;
            }
            this.mPreviousTime = n;
            this.mListener.onTimeUpdate(this, n - mStartTime, n2);
        }
        return false;
    }
    
    @Override
    void initAnimation() {
    }
    
    public void setTimeListener(final TimeListener mListener) {
        this.mListener = mListener;
    }
    
    public interface TimeListener
    {
        void onTimeUpdate(final TimeAnimator p0, final long p1, final long p2);
    }
}
