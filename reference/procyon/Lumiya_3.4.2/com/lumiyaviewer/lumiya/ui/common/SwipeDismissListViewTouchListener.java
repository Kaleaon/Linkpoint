// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import android.animation.Animator$AnimatorListener;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView$OnScrollListener;
import com.nineoldandroids.view.ViewHelper;
import android.os.Build$VERSION;
import android.view.ViewConfiguration;
import java.util.ArrayList;
import android.view.VelocityTracker;
import java.util.List;
import android.widget.ListView;
import android.view.View;
import android.view.View$OnTouchListener;

public class SwipeDismissListViewTouchListener implements View$OnTouchListener
{
    private long mAnimationTime;
    private DismissCallbacks mCallbacks;
    private int mDismissAnimationRefCount;
    private int mDownPosition;
    private View mDownView;
    private float mDownX;
    private float mDownY;
    private ListView mListView;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private boolean mPaused;
    private List<PendingDismissData> mPendingDismisses;
    private int mSlop;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mViewWidth;
    
    public SwipeDismissListViewTouchListener(final ListView mListView, final DismissCallbacks mCallbacks) {
        this.mViewWidth = 1;
        this.mPendingDismisses = new ArrayList<PendingDismissData>();
        this.mDismissAnimationRefCount = 0;
        final ViewConfiguration value = ViewConfiguration.get(mListView.getContext());
        this.mSlop = value.getScaledTouchSlop();
        this.mMinFlingVelocity = value.getScaledMinimumFlingVelocity() * 16;
        this.mMaxFlingVelocity = value.getScaledMaximumFlingVelocity();
        this.mAnimationTime = mListView.getContext().getResources().getInteger(17694720);
        this.mListView = mListView;
        this.mCallbacks = mCallbacks;
    }
    
    private void performDismiss(final View view, final int n) {
        this.mCallbacks.onDismiss(this.mListView, n);
    }
    
    public static void restoreViewState(final View view) {
        if (Build$VERSION.SDK_INT >= 11) {
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
        }
        else {
            ViewHelper.setTranslationX(view, 0.0f);
            ViewHelper.setAlpha(view, 1.0f);
        }
    }
    
    public AbsListView$OnScrollListener makeScrollListener() {
        return (AbsListView$OnScrollListener)new AbsListView$OnScrollListener() {
            public void onScroll(final AbsListView absListView, final int n, final int n2, final int n3) {
            }
            
            public void onScrollStateChanged(final AbsListView absListView, final int n) {
                boolean enabled = true;
                final SwipeDismissListViewTouchListener this$0 = SwipeDismissListViewTouchListener.this;
                if (n == 1) {
                    enabled = false;
                }
                this$0.setEnabled(enabled);
            }
        };
    }
    
    public boolean onTouch(View mDownView, final MotionEvent motionEvent) {
        final int n = 1;
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mListView.getWidth();
        }
        switch (motionEvent.getActionMasked()) {
            case 0: {
                if (this.mPaused) {
                    return false;
                }
                final Rect rect = new Rect();
                final int childCount = this.mListView.getChildCount();
                final int[] array = new int[2];
                this.mListView.getLocationOnScreen(array);
                final int n2 = (int)motionEvent.getRawX();
                final int n3 = array[0];
                final int n4 = (int)motionEvent.getRawY();
                final int n5 = array[1];
                for (int i = 0; i < childCount; ++i) {
                    final View child = this.mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(n2 - n3, n4 - n5)) {
                        this.mDownView = child;
                        break;
                    }
                }
                if (this.mDownView != null) {
                    this.mDownX = motionEvent.getRawX();
                    this.mDownY = motionEvent.getRawY();
                    this.mDownPosition = this.mListView.getPositionForView(this.mDownView);
                    if (this.mCallbacks.canDismiss(this.mListView, this.mDownPosition)) {
                        (this.mVelocityTracker = VelocityTracker.obtain()).addMovement(motionEvent);
                    }
                    else {
                        this.mDownView = null;
                    }
                }
                return false;
            }
            case 3: {
                if (this.mVelocityTracker != null) {
                    if (this.mDownView != null && this.mSwiping) {
                        if (Build$VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)null);
                        }
                        else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener(null);
                        }
                    }
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    this.mDownX = 0.0f;
                    this.mDownY = 0.0f;
                    this.mDownView = null;
                    this.mDownPosition = -1;
                    this.mSwiping = false;
                    break;
                }
                break;
            }
            case 1: {
                if (this.mVelocityTracker != null) {
                    final float a = motionEvent.getRawX() - this.mDownX;
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    final float xVelocity = this.mVelocityTracker.getXVelocity();
                    final float abs = Math.abs(xVelocity);
                    final float abs2 = Math.abs(this.mVelocityTracker.getYVelocity());
                    int n6;
                    int n7;
                    if (Math.abs(a) > this.mViewWidth / 2 && this.mSwiping) {
                        if (a > 0.0f) {
                            n6 = 1;
                            n7 = n;
                        }
                        else {
                            n6 = 1;
                            n7 = 0;
                        }
                    }
                    else if (this.mMinFlingVelocity <= abs && abs <= this.mMaxFlingVelocity && abs2 < abs) {
                        if (this.mSwiping) {
                            int n8;
                            if (xVelocity < 0.0f) {
                                n8 = 1;
                            }
                            else {
                                n8 = 0;
                            }
                            int n9;
                            if (a < 0.0f) {
                                n9 = 1;
                            }
                            else {
                                n9 = 0;
                            }
                            int n10;
                            if (n8 == n9) {
                                n10 = 1;
                            }
                            else {
                                n10 = 0;
                            }
                            n6 = n10;
                            n7 = n;
                            if (this.mVelocityTracker.getXVelocity() <= 0.0f) {
                                n7 = 0;
                                n6 = n10;
                            }
                        }
                        else {
                            n7 = 0;
                            n6 = 0;
                        }
                    }
                    else {
                        n7 = 0;
                        n6 = 0;
                    }
                    if (n6 != 0 && this.mDownPosition != -1) {
                        mDownView = this.mDownView;
                        final int mDownPosition = this.mDownPosition;
                        ++this.mDismissAnimationRefCount;
                        if (Build$VERSION.SDK_INT >= 12) {
                            final android.view.ViewPropertyAnimator animate = this.mDownView.animate();
                            int mViewWidth;
                            if (n7 != 0) {
                                mViewWidth = this.mViewWidth;
                            }
                            else {
                                mViewWidth = -this.mViewWidth;
                            }
                            animate.translationX((float)mViewWidth).alpha(0.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                                public void onAnimationEnd(final android.animation.Animator animator) {
                                    SwipeDismissListViewTouchListener.this.performDismiss(mDownView, mDownPosition);
                                }
                            });
                        }
                        else {
                            final ViewPropertyAnimator animate2 = ViewPropertyAnimator.animate(this.mDownView);
                            int mViewWidth2;
                            if (n7 != 0) {
                                mViewWidth2 = this.mViewWidth;
                            }
                            else {
                                mViewWidth2 = -this.mViewWidth;
                            }
                            animate2.translationX((float)mViewWidth2).alpha(0.0f).setDuration(this.mAnimationTime).setListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(final Animator animator) {
                                    SwipeDismissListViewTouchListener.this.performDismiss(mDownView, mDownPosition);
                                }
                            });
                        }
                    }
                    else if (Build$VERSION.SDK_INT >= 12) {
                        this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)null);
                    }
                    else {
                        ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener(null);
                    }
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    this.mDownX = 0.0f;
                    this.mDownY = 0.0f;
                    this.mDownView = null;
                    this.mDownPosition = -1;
                    this.mSwiping = false;
                    break;
                }
                break;
            }
            case 2: {
                if (this.mVelocityTracker == null || this.mPaused) {
                    break;
                }
                this.mVelocityTracker.addMovement(motionEvent);
                final float n11 = motionEvent.getRawX() - this.mDownX;
                final float rawY = motionEvent.getRawY();
                final float mDownY = this.mDownY;
                if (Math.abs(n11) > this.mSlop && Math.abs(rawY - mDownY) < Math.abs(n11) / 2.0f) {
                    this.mSwiping = true;
                    int mSlop;
                    if (n11 > 0.0f) {
                        mSlop = this.mSlop;
                    }
                    else {
                        mSlop = -this.mSlop;
                    }
                    this.mSwipingSlop = mSlop;
                    this.mListView.requestDisallowInterceptTouchEvent(true);
                    final MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setAction(motionEvent.getActionIndex() << 8 | 0x3);
                    this.mListView.onTouchEvent(obtain);
                    obtain.recycle();
                }
                if (this.mSwiping) {
                    if (Build$VERSION.SDK_INT >= 11) {
                        this.mDownView.setTranslationX(n11 - this.mSwipingSlop);
                        this.mDownView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - Math.abs(n11) * 2.0f / this.mViewWidth)));
                    }
                    else {
                        ViewHelper.setTranslationX(this.mDownView, n11 - this.mSwipingSlop);
                        ViewHelper.setAlpha(this.mDownView, Math.min(1.0f, 1.0f - Math.abs(n11) * 2.0f / this.mViewWidth));
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public void setEnabled(final boolean b) {
        this.mPaused = (b ^ true);
    }
    
    public interface DismissCallbacks
    {
        boolean canDismiss(final ListView p0, final int p1);
        
        void onDismiss(final ListView p0, final int p1);
    }
    
    class PendingDismissData implements Comparable<PendingDismissData>
    {
        public int position;
        public View view;
        
        public PendingDismissData(final int position, final View view) {
            this.position = position;
            this.view = view;
        }
        
        @Override
        public int compareTo(final PendingDismissData pendingDismissData) {
            return pendingDismissData.position - this.position;
        }
    }
}
