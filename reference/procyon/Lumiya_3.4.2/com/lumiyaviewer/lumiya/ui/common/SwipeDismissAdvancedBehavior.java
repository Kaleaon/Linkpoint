// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.support.v4.view.ViewCompat;
import android.view.ViewConfiguration;
import android.support.v4.widget.ViewDragHelper;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

public class SwipeDismissAdvancedBehavior<V extends View> extends Behavior<V>
{
    private static final float DEFAULT_ALPHA_END_DISTANCE = 1.0f;
    private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0f;
    private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 1.0f;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    public static final int SWIPE_DIRECTION_ANY = 15;
    public static final int SWIPE_DIRECTION_DOWN = 8;
    public static final int SWIPE_DIRECTION_LEFT = 1;
    public static final int SWIPE_DIRECTION_RIGHT = 2;
    public static final int SWIPE_DIRECTION_UP = 4;
    public static final int SWIPE_DIRECTION_X = 3;
    public static final int SWIPE_DIRECTION_Y = 12;
    private float mAlphaEndSwipeDistance;
    private float mAlphaStartSwipeDistance;
    private final ViewDragHelper.Callback mDragCallback;
    private float mDragDismissThreshold;
    private boolean mIgnoreEvents;
    private OnDismissListener mListener;
    private float mSensitivity;
    private boolean mSensitivitySet;
    private int mSwipeDirection;
    private ViewDragHelper mViewDragHelper;
    
    public SwipeDismissAdvancedBehavior() {
        this.mSensitivity = 0.0f;
        this.mSwipeDirection = 15;
        this.mDragDismissThreshold = 1.0f;
        this.mAlphaStartSwipeDistance = 0.0f;
        this.mAlphaEndSwipeDistance = 1.0f;
        this.mDragCallback = new ViewDragHelper.Callback() {
            private int mOriginalCapturedViewLeft;
            private int mOriginalCapturedViewTop;
            
            private boolean shouldDismiss(final View view, final float n, final float n2) {
                final float n3 = (float)ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
                if (n < -n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x1) != 0x0) {
                    return true;
                }
                if (n > n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x2) != 0x0) {
                    return true;
                }
                if (n2 < -n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x4) != 0x0) {
                    return true;
                }
                if (n2 > n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x8) != 0x0) {
                    return true;
                }
                final int n4 = view.getLeft() - this.mOriginalCapturedViewLeft;
                final int round = Math.round(view.getWidth() * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
                if (n4 < -round && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x1) != 0x0) {
                    return true;
                }
                if (n4 > round && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x2) != 0x0) {
                    return true;
                }
                final int n5 = view.getTop() - this.mOriginalCapturedViewTop;
                final int round2 = Math.round(view.getHeight() * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
                return (n5 < -round2 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x4) != 0x0) || (n5 > round2 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x8) != 0x0);
            }
            
            @Override
            public int clampViewPositionHorizontal(final View view, final int n, int width) {
                int width2 = 0;
                if (view.getTop() == this.mOriginalCapturedViewTop) {
                    final int mOriginalCapturedViewLeft = this.mOriginalCapturedViewLeft;
                    if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x1) != 0x0) {
                        width = view.getWidth();
                    }
                    else {
                        width = 0;
                    }
                    final int mOriginalCapturedViewLeft2 = this.mOriginalCapturedViewLeft;
                    if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x2) != 0x0) {
                        width2 = view.getWidth();
                    }
                    return clamp(mOriginalCapturedViewLeft - width, n, width2 + mOriginalCapturedViewLeft2);
                }
                return this.mOriginalCapturedViewLeft;
            }
            
            @Override
            public int clampViewPositionVertical(final View view, final int n, int height) {
                int height2 = 0;
                if (view.getLeft() == this.mOriginalCapturedViewLeft) {
                    final int mOriginalCapturedViewTop = this.mOriginalCapturedViewTop;
                    if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x4) != 0x0) {
                        height = view.getHeight();
                    }
                    else {
                        height = 0;
                    }
                    final int mOriginalCapturedViewTop2 = this.mOriginalCapturedViewTop;
                    if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x8) != 0x0) {
                        height2 = view.getHeight();
                    }
                    return clamp(mOriginalCapturedViewTop - height, n, height2 + mOriginalCapturedViewTop2);
                }
                return this.mOriginalCapturedViewTop;
            }
            
            @Override
            public int getViewHorizontalDragRange(final View view) {
                if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x3) != 0x0) {
                    return view.getWidth();
                }
                return 0;
            }
            
            @Override
            public int getViewVerticalDragRange(final View view) {
                if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0xC) != 0x0) {
                    return view.getWidth();
                }
                return 0;
            }
            
            @Override
            public void onViewCaptured(final View view, final int n) {
                this.mOriginalCapturedViewLeft = view.getLeft();
                this.mOriginalCapturedViewTop = view.getTop();
            }
            
            @Override
            public void onViewDragStateChanged(final int n) {
                if (SwipeDismissAdvancedBehavior.this.mListener != null) {
                    SwipeDismissAdvancedBehavior.this.mListener.onDragStateChanged(n);
                }
            }
            
            @Override
            public void onViewPositionChanged(final View view, int abs, final int n, int abs2, final int n2) {
                abs2 = 0;
                if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x3) != 0x0) {
                    abs = Math.abs(abs - this.mOriginalCapturedViewLeft);
                }
                else {
                    abs = 0;
                }
                if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0xC) != 0x0) {
                    abs2 = Math.abs(n - this.mOriginalCapturedViewTop);
                }
                if (abs == 0 && abs2 == 0) {
                    ViewCompat.setAlpha(view, 1.0f);
                }
                else {
                    ViewCompat.setAlpha(view, 1.0f - Math.max(clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(view.getWidth() * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, view.getWidth() * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (float)abs), 1.0f), clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(view.getHeight() * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, view.getHeight() * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (float)abs2), 1.0f)));
                }
            }
            
            @Override
            public void onViewReleased(final View view, final float n, final float n2) {
                final int width = view.getWidth();
                final int height = view.getHeight();
                final int left = view.getLeft();
                final int top = view.getTop();
                boolean b;
                int mOriginalCapturedViewLeft;
                int mOriginalCapturedViewTop;
                if (this.shouldDismiss(view, n, n2)) {
                    final float n3 = (float)ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
                    int n4;
                    int n5;
                    if (n < -n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x1) != 0x0) {
                        n4 = this.mOriginalCapturedViewLeft - width;
                        n5 = top;
                    }
                    else if (n > n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x2) != 0x0) {
                        n4 = this.mOriginalCapturedViewLeft + width;
                        n5 = top;
                    }
                    else if (n2 < -n3 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x4) != 0x0) {
                        n5 = this.mOriginalCapturedViewTop - height;
                        n4 = left;
                    }
                    else {
                        n5 = top;
                        n4 = left;
                        if (n2 > n3) {
                            n5 = top;
                            n4 = left;
                            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 0x8) != 0x0) {
                                n5 = this.mOriginalCapturedViewTop + height;
                                n4 = left;
                            }
                        }
                    }
                    final int n6 = n5;
                    b = true;
                    mOriginalCapturedViewLeft = n4;
                    mOriginalCapturedViewTop = n6;
                }
                else {
                    mOriginalCapturedViewLeft = this.mOriginalCapturedViewLeft;
                    mOriginalCapturedViewTop = this.mOriginalCapturedViewTop;
                    b = false;
                }
                if (SwipeDismissAdvancedBehavior.this.mViewDragHelper.settleCapturedViewAt(mOriginalCapturedViewLeft, mOriginalCapturedViewTop)) {
                    ViewCompat.postOnAnimation(view, new SettleRunnable(view, b));
                }
                else if (b && SwipeDismissAdvancedBehavior.this.mListener != null) {
                    SwipeDismissAdvancedBehavior.this.mListener.onDismiss(view);
                }
            }
            
            @Override
            public boolean tryCaptureView(final View view, final int n) {
                return SwipeDismissAdvancedBehavior.this.canSwipeDismissView(view);
            }
        };
    }
    
    private static float clamp(final float a, final float b, final float b2) {
        return Math.min(Math.max(a, b), b2);
    }
    
    private static int clamp(final int a, final int b, final int b2) {
        return Math.min(Math.max(a, b), b2);
    }
    
    private void ensureViewDragHelper(final ViewGroup viewGroup) {
        if (this.mViewDragHelper == null) {
            ViewDragHelper mViewDragHelper;
            if (this.mSensitivitySet) {
                mViewDragHelper = ViewDragHelper.create(viewGroup, this.mSensitivity, this.mDragCallback);
            }
            else {
                mViewDragHelper = ViewDragHelper.create(viewGroup, this.mDragCallback);
            }
            this.mViewDragHelper = mViewDragHelper;
        }
    }
    
    static float fraction(final float n, final float n2, final float n3) {
        return (n3 - n) / (n2 - n);
    }
    
    public boolean canSwipeDismissView(@NonNull final View view) {
        return true;
    }
    
    public int getDragState() {
        int viewDragState;
        if (this.mViewDragHelper != null) {
            viewDragState = this.mViewDragHelper.getViewDragState();
        }
        else {
            viewDragState = 0;
        }
        return viewDragState;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final CoordinatorLayout coordinatorLayout, final V v, final MotionEvent motionEvent) {
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            default: {
                this.mIgnoreEvents = (coordinatorLayout.isPointInChildBounds(v, (int)motionEvent.getX(), (int)motionEvent.getY()) ^ true);
                break;
            }
            case 1:
            case 3: {
                if (this.mIgnoreEvents) {
                    return this.mIgnoreEvents = false;
                }
                break;
            }
        }
        if (this.mIgnoreEvents) {
            return false;
        }
        this.ensureViewDragHelper(coordinatorLayout);
        return this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
    }
    
    @Override
    public boolean onTouchEvent(final CoordinatorLayout coordinatorLayout, final V v, final MotionEvent motionEvent) {
        if (this.mViewDragHelper != null) {
            this.mViewDragHelper.processTouchEvent(motionEvent);
            return true;
        }
        return false;
    }
    
    public void setDragDismissDistance(final float n) {
        this.mDragDismissThreshold = clamp(0.0f, n, 1.0f);
    }
    
    public void setEndAlphaSwipeDistance(final float n) {
        this.mAlphaEndSwipeDistance = clamp(0.0f, n, 1.0f);
    }
    
    public void setListener(final OnDismissListener mListener) {
        this.mListener = mListener;
    }
    
    public void setSensitivity(final float mSensitivity) {
        this.mSensitivity = mSensitivity;
        this.mSensitivitySet = true;
    }
    
    public void setStartAlphaSwipeDistance(final float n) {
        this.mAlphaStartSwipeDistance = clamp(0.0f, n, 1.0f);
    }
    
    public void setSwipeDirection(final int mSwipeDirection) {
        this.mSwipeDirection = mSwipeDirection;
    }
    
    public interface OnDismissListener
    {
        void onDismiss(final View p0);
        
        void onDragStateChanged(final int p0);
    }
    
    private class SettleRunnable implements Runnable
    {
        private final boolean mDismiss;
        private final View mView;
        
        SettleRunnable(final View mView, final boolean mDismiss) {
            this.mView = mView;
            this.mDismiss = mDismiss;
        }
        
        @Override
        public void run() {
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper != null && SwipeDismissAdvancedBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            }
            else if (this.mDismiss && SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    private @interface SwipeDirection {
    }
}
