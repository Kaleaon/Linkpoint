// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.view.GestureDetector;
import android.os.Message;
import android.view.ViewConfiguration;
import android.view.VelocityTracker;
import android.view.GestureDetector$OnDoubleTapListener;
import android.view.MotionEvent;
import android.os.Build$VERSION;
import android.os.Handler;
import android.view.GestureDetector$OnGestureListener;
import android.content.Context;

public final class GestureDetectorCompat
{
    private final GestureDetectorCompatImpl mImpl;
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener) {
        this(context, gestureDetector$OnGestureListener, null);
    }
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
        if (Build$VERSION.SDK_INT <= 17) {
            this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplBase(context, gestureDetector$OnGestureListener, handler);
        }
        else {
            this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplJellybeanMr2(context, gestureDetector$OnGestureListener, handler);
        }
    }
    
    public boolean isLongpressEnabled() {
        return this.mImpl.isLongpressEnabled();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.mImpl.onTouchEvent(motionEvent);
    }
    
    public void setIsLongpressEnabled(final boolean isLongpressEnabled) {
        this.mImpl.setIsLongpressEnabled(isLongpressEnabled);
    }
    
    public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
        this.mImpl.setOnDoubleTapListener(onDoubleTapListener);
    }
    
    interface GestureDetectorCompatImpl
    {
        boolean isLongpressEnabled();
        
        boolean onTouchEvent(final MotionEvent p0);
        
        void setIsLongpressEnabled(final boolean p0);
        
        void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener p0);
    }
    
    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl
    {
        private static final int DOUBLE_TAP_TIMEOUT;
        private static final int LONGPRESS_TIMEOUT;
        private static final int LONG_PRESS = 2;
        private static final int SHOW_PRESS = 1;
        private static final int TAP = 3;
        private static final int TAP_TIMEOUT;
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        MotionEvent mCurrentDownEvent;
        boolean mDeferConfirmSingleTap;
        GestureDetector$OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        final GestureDetector$OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;
        
        static {
            LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
            TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
            DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        }
        
        public GestureDetectorCompatImplBase(final Context context, final GestureDetector$OnGestureListener mListener, final Handler handler) {
            if (handler == null) {
                this.mHandler = new GestureHandler();
            }
            else {
                this.mHandler = new GestureHandler(handler);
            }
            this.mListener = mListener;
            if (mListener instanceof GestureDetector$OnDoubleTapListener) {
                this.setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)mListener);
            }
            this.init(context);
        }
        
        private void cancel() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            this.mIsDoubleTapping = false;
            this.mStillDown = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void cancelTaps() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mIsDoubleTapping = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void init(final Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            if (this.mListener != null) {
                this.mIsLongpressEnabled = true;
                final ViewConfiguration value = ViewConfiguration.get(context);
                final int scaledTouchSlop = value.getScaledTouchSlop();
                final int scaledDoubleTapSlop = value.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = value.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = value.getScaledMaximumFlingVelocity();
                this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
                this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
                return;
            }
            throw new IllegalArgumentException("OnGestureListener must not be null");
        }
        
        private boolean isConsideredDoubleTap(final MotionEvent motionEvent, final MotionEvent motionEvent2, final MotionEvent motionEvent3) {
            boolean b = false;
            if (!this.mAlwaysInBiggerTapRegion) {
                return false;
            }
            int n;
            if (motionEvent3.getEventTime() - motionEvent2.getEventTime() <= GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT) {
                n = 1;
            }
            else {
                n = 0;
            }
            if (n == 0) {
                return false;
            }
            final int n2 = (int)motionEvent.getX() - (int)motionEvent3.getX();
            final int n3 = (int)motionEvent.getY() - (int)motionEvent3.getY();
            if (n2 * n2 + n3 * n3 < this.mDoubleTapSlopSquare) {
                b = true;
            }
            return b;
        }
        
        void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
        
        @Override
        public boolean isLongpressEnabled() {
            return this.mIsLongpressEnabled;
        }
        
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            final int n = 1;
            final boolean b = false;
            final int action = motionEvent.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            boolean b2;
            if ((action & 0xFF) != 0x6) {
                b2 = false;
            }
            else {
                b2 = true;
            }
            int actionIndex;
            if (!b2) {
                actionIndex = -1;
            }
            else {
                actionIndex = motionEvent.getActionIndex();
            }
            float n2 = 0.0f;
            float n3 = 0.0f;
            final int pointerCount = motionEvent.getPointerCount();
            float n4;
            float n5;
            for (int i = 0; i < pointerCount; ++i, n3 = n4, n2 = n5) {
                n4 = n3;
                n5 = n2;
                if (actionIndex != i) {
                    n5 = n2 + motionEvent.getX(i);
                    n4 = n3 + motionEvent.getY(i);
                }
            }
            int n6;
            if (!b2) {
                n6 = pointerCount;
            }
            else {
                n6 = pointerCount - 1;
            }
            final float n7 = n2 / n6;
            final float n8 = n3 / n6;
            boolean b3 = b;
            switch (action & 0xFF) {
                default: {
                    b3 = b;
                    return b3;
                }
                case 3: {
                    this.cancel();
                    b3 = b;
                    return b3;
                }
                case 1: {
                    this.mStillDown = false;
                    final MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    if (!this.mIsDoubleTapping) {
                        if (!this.mInLongPress) {
                            if (!this.mAlwaysInTapRegion) {
                                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                                final int pointerId = motionEvent.getPointerId(0);
                                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                                final float yVelocity = mVelocityTracker.getYVelocity(pointerId);
                                final float xVelocity = mVelocityTracker.getXVelocity(pointerId);
                                int n9;
                                if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                                    n9 = 1;
                                }
                                else {
                                    n9 = 0;
                                }
                                b3 = ((n9 != 0 || Math.abs(xVelocity) > this.mMinimumFlingVelocity) && this.mListener.onFling(this.mCurrentDownEvent, motionEvent, xVelocity, yVelocity));
                            }
                            else {
                                final boolean b4 = b3 = this.mListener.onSingleTapUp(motionEvent);
                                if (this.mDeferConfirmSingleTap) {
                                    b3 = b4;
                                    if (this.mDoubleTapListener != null) {
                                        this.mDoubleTapListener.onSingleTapConfirmed(motionEvent);
                                        b3 = b4;
                                    }
                                }
                            }
                        }
                        else {
                            this.mHandler.removeMessages(3);
                            this.mInLongPress = false;
                            b3 = false;
                        }
                    }
                    else {
                        b3 = (this.mDoubleTapListener.onDoubleTapEvent(motionEvent) | false);
                    }
                    if (this.mPreviousUpEvent != null) {
                        this.mPreviousUpEvent.recycle();
                    }
                    this.mPreviousUpEvent = obtain;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mIsDoubleTapping = false;
                    this.mDeferConfirmSingleTap = false;
                    this.mHandler.removeMessages(1);
                    this.mHandler.removeMessages(2);
                    return b3;
                }
                case 0: {
                    boolean b5;
                    if (this.mDoubleTapListener == null) {
                        b5 = false;
                    }
                    else {
                        final boolean hasMessages = this.mHandler.hasMessages(3);
                        if (hasMessages) {
                            this.mHandler.removeMessages(3);
                        }
                        if (this.mCurrentDownEvent != null && this.mPreviousUpEvent != null && hasMessages && this.isConsideredDoubleTap(this.mCurrentDownEvent, this.mPreviousUpEvent, motionEvent)) {
                            this.mIsDoubleTapping = true;
                            b5 = (this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | false | this.mDoubleTapListener.onDoubleTapEvent(motionEvent));
                        }
                        else {
                            this.mHandler.sendEmptyMessageDelayed(3, (long)GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT);
                            b5 = false;
                        }
                    }
                    this.mLastFocusX = n7;
                    this.mDownFocusX = n7;
                    this.mLastFocusY = n8;
                    this.mDownFocusY = n8;
                    if (this.mCurrentDownEvent != null) {
                        this.mCurrentDownEvent.recycle();
                    }
                    this.mCurrentDownEvent = MotionEvent.obtain(motionEvent);
                    this.mAlwaysInTapRegion = true;
                    this.mAlwaysInBiggerTapRegion = true;
                    this.mStillDown = true;
                    this.mInLongPress = false;
                    this.mDeferConfirmSingleTap = false;
                    if (this.mIsLongpressEnabled) {
                        this.mHandler.removeMessages(2);
                        this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT + GestureDetectorCompatImplBase.LONGPRESS_TIMEOUT);
                    }
                    this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT);
                    b3 = (this.mListener.onDown(motionEvent) | b5);
                    return b3;
                }
                case 6: {
                    this.mLastFocusX = n7;
                    this.mDownFocusX = n7;
                    this.mLastFocusY = n8;
                    this.mDownFocusY = n8;
                    this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                    final int actionIndex2 = motionEvent.getActionIndex();
                    final int pointerId2 = motionEvent.getPointerId(actionIndex2);
                    final float xVelocity2 = this.mVelocityTracker.getXVelocity(pointerId2);
                    final float yVelocity2 = this.mVelocityTracker.getYVelocity(pointerId2);
                    int n10 = 0;
                    while (true) {
                        b3 = b;
                        if (n10 >= pointerCount) {
                            return b3;
                        }
                        if (n10 != actionIndex2) {
                            final int pointerId3 = motionEvent.getPointerId(n10);
                            if (this.mVelocityTracker.getYVelocity(pointerId3) * yVelocity2 + this.mVelocityTracker.getXVelocity(pointerId3) * xVelocity2 < 0.0f) {
                                break;
                            }
                        }
                        ++n10;
                    }
                    this.mVelocityTracker.clear();
                    b3 = b;
                    return b3;
                }
                case 5: {
                    this.mLastFocusX = n7;
                    this.mDownFocusX = n7;
                    this.mLastFocusY = n8;
                    this.mDownFocusY = n8;
                    this.cancelTaps();
                    b3 = b;
                }
                case 4: {
                    return b3;
                }
                case 2: {
                    b3 = b;
                    if (this.mInLongPress) {
                        return b3;
                    }
                    final float a = this.mLastFocusX - n7;
                    final float a2 = this.mLastFocusY - n8;
                    if (this.mIsDoubleTapping) {
                        b3 = (this.mDoubleTapListener.onDoubleTapEvent(motionEvent) | false);
                        return b3;
                    }
                    if (!this.mAlwaysInTapRegion) {
                        int n11;
                        if (Math.abs(a) >= 1.0f) {
                            n11 = n;
                        }
                        else {
                            n11 = 0;
                        }
                        if (n11 == 0) {
                            b3 = b;
                            if (Math.abs(a2) < 1.0f) {
                                return b3;
                            }
                        }
                        b3 = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, a, a2);
                        this.mLastFocusX = n7;
                        this.mLastFocusY = n8;
                        return b3;
                    }
                    final int n12 = (int)(n7 - this.mDownFocusX);
                    final int n13 = (int)(n8 - this.mDownFocusY);
                    final int n14 = n12 * n12 + n13 * n13;
                    if (n14 <= this.mTouchSlopSquare) {
                        b3 = false;
                    }
                    else {
                        b3 = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, a, a2);
                        this.mLastFocusX = n7;
                        this.mLastFocusY = n8;
                        this.mAlwaysInTapRegion = false;
                        this.mHandler.removeMessages(3);
                        this.mHandler.removeMessages(1);
                        this.mHandler.removeMessages(2);
                    }
                    if (n14 > this.mTouchSlopSquare) {
                        this.mAlwaysInBiggerTapRegion = false;
                    }
                    return b3;
                }
            }
        }
        
        @Override
        public void setIsLongpressEnabled(final boolean mIsLongpressEnabled) {
            this.mIsLongpressEnabled = mIsLongpressEnabled;
        }
        
        @Override
        public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener mDoubleTapListener) {
            this.mDoubleTapListener = mDoubleTapListener;
        }
        
        private class GestureHandler extends Handler
        {
            GestureHandler() {
            }
            
            GestureHandler(final Handler handler) {
                super(handler.getLooper());
            }
            
            public void handleMessage(final Message obj) {
                switch (obj.what) {
                    default: {
                        throw new RuntimeException("Unknown message " + obj);
                    }
                    case 1: {
                        GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        break;
                    }
                    case 2: {
                        GestureDetectorCompatImplBase.this.dispatchLongPress();
                        break;
                    }
                    case 3: {
                        if (GestureDetectorCompatImplBase.this.mDoubleTapListener == null) {
                            break;
                        }
                        if (GestureDetectorCompatImplBase.this.mStillDown) {
                            GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
                            break;
                        }
                        GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        break;
                    }
                }
            }
        }
    }
    
    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl
    {
        private final GestureDetector mDetector;
        
        public GestureDetectorCompatImplJellybeanMr2(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
            this.mDetector = new GestureDetector(context, gestureDetector$OnGestureListener, handler);
        }
        
        @Override
        public boolean isLongpressEnabled() {
            return this.mDetector.isLongpressEnabled();
        }
        
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            return this.mDetector.onTouchEvent(motionEvent);
        }
        
        @Override
        public void setIsLongpressEnabled(final boolean isLongpressEnabled) {
            this.mDetector.setIsLongpressEnabled(isLongpressEnabled);
        }
        
        @Override
        public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
            this.mDetector.setOnDoubleTapListener(onDoubleTapListener);
        }
    }
}
