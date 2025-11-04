// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.common;

import com.lumiyaviewer.lumiya.Debug;
import android.view.MotionEvent;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import android.animation.Animator$AnimatorListener;
import android.animation.Animator;
import android.view.ViewGroup$LayoutParams;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.ViewConfiguration;
import android.view.View;
import android.view.VelocityTracker;
import android.annotation.TargetApi;

@TargetApi(12)
public class SwipeDismissTouchListener implements OnInterceptTouchEventListener
{
    private final boolean canSwipeDown;
    private final boolean canSwipeLeft;
    private final boolean canSwipeRight;
    private final boolean canSwipeUp;
    private final boolean canSwipeX;
    private final boolean canSwipeY;
    private long mAnimationTime;
    private DismissCallbacks mCallbacks;
    private float mDownX;
    private float mDownY;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private int mSlop;
    private boolean mSwiping;
    private int mSwipingSlopX;
    private int mSwipingSlopY;
    private boolean mSwipingX;
    private boolean mSwipingY;
    private Object mToken;
    private float mTranslationX;
    private float mTranslationY;
    private VelocityTracker mVelocityTracker;
    private View mView;
    private int mViewHeight;
    private int mViewWidth;
    
    public SwipeDismissTouchListener(final View mView, final Object mToken, final DismissCallbacks mCallbacks, final boolean canSwipeUp, final boolean canSwipeDown, final boolean canSwipeLeft, boolean b) {
        final boolean b2 = true;
        this.mViewWidth = 1;
        this.mViewHeight = 1;
        this.canSwipeUp = canSwipeUp;
        this.canSwipeDown = canSwipeDown;
        this.canSwipeLeft = canSwipeLeft;
        this.canSwipeRight = b;
        if (canSwipeLeft) {
            b = true;
        }
        this.canSwipeX = b;
        boolean canSwipeY = b2;
        if (!canSwipeUp) {
            canSwipeY = canSwipeDown;
        }
        this.canSwipeY = canSwipeY;
        final ViewConfiguration value = ViewConfiguration.get(mView.getContext());
        this.mSlop = value.getScaledTouchSlop();
        this.mMinFlingVelocity = value.getScaledMinimumFlingVelocity() * 16;
        this.mMaxFlingVelocity = value.getScaledMaximumFlingVelocity();
        this.mAnimationTime = mView.getContext().getResources().getInteger(17694720);
        this.mView = mView;
        this.mToken = mToken;
        this.mCallbacks = mCallbacks;
    }
    
    private void performDismiss() {
        final ViewGroup$LayoutParams layoutParams = this.mView.getLayoutParams();
        final int height = this.mView.getHeight();
        final ValueAnimator setDuration = ValueAnimator.ofInt(new int[] { height, 1 }).setDuration(this.mAnimationTime);
        setDuration.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                SwipeDismissTouchListener.this.mCallbacks.onDismiss(SwipeDismissTouchListener.this.mView, SwipeDismissTouchListener.this.mToken);
                SwipeDismissTouchListener.this.mView.setAlpha(1.0f);
                SwipeDismissTouchListener.this.mView.setTranslationX(0.0f);
                SwipeDismissTouchListener.this.mView.setTranslationY(0.0f);
                layoutParams.height = height;
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams);
            }
        });
        setDuration.addUpdateListener((ValueAnimator$AnimatorUpdateListener)new ValueAnimator$AnimatorUpdateListener() {
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                layoutParams.height = (int)valueAnimator.getAnimatedValue();
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams);
            }
        });
        setDuration.start();
    }
    
    @Override
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        return false;
    }
    
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        int n = 1;
        final int n2 = 1;
        int n3 = 1;
        motionEvent.offsetLocation(this.mTranslationX, this.mTranslationY);
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mView.getWidth();
        }
        if (this.mViewHeight < 2) {
            this.mViewHeight = this.mView.getHeight();
        }
        switch (motionEvent.getActionMasked()) {
            case 0: {
                this.mDownX = motionEvent.getRawX();
                this.mDownY = motionEvent.getRawY();
                Debug.Printf("SwipeSwipe: action down x %f y %f", this.mDownX, this.mDownY);
                if (this.mCallbacks.canDismiss(this.mToken)) {
                    (this.mVelocityTracker = VelocityTracker.obtain()).addMovement(motionEvent);
                }
                return true;
            }
            case 1: {
                if (this.mVelocityTracker != null) {
                    final float n4 = motionEvent.getRawX() - this.mDownX;
                    final float n5 = motionEvent.getRawY() - this.mDownY;
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    final float xVelocity = this.mVelocityTracker.getXVelocity();
                    final float yVelocity = this.mVelocityTracker.getYVelocity();
                    final float abs = Math.abs(xVelocity);
                    final float abs2 = Math.abs(yVelocity);
                    float n6;
                    float n7;
                    if (this.mSwiping && this.mSwipingX && this.canSwipeRight && n4 > this.mViewWidth / 2) {
                        n6 = (float)this.mViewWidth;
                        n7 = 0.0f;
                    }
                    else if (this.mSwiping && this.mSwipingX && this.canSwipeLeft && n4 < -(this.mViewWidth / 2)) {
                        n6 = (float)(-this.mViewWidth);
                        n7 = 0.0f;
                    }
                    else if (this.mSwiping && this.mSwipingY && this.canSwipeDown && n5 > this.mViewHeight / 2) {
                        n7 = (float)this.mViewHeight;
                        n6 = 0.0f;
                    }
                    else if (this.mSwiping && this.mSwipingY && this.canSwipeUp && n5 < -(this.mViewHeight / 2)) {
                        n7 = (float)(-this.mViewHeight);
                        n6 = 0.0f;
                    }
                    else if (this.mMinFlingVelocity <= abs && abs <= this.mMaxFlingVelocity && abs2 < abs && this.mSwiping && this.mSwipingX) {
                        if (xVelocity < 0.0f) {
                            n3 = (this.canSwipeLeft ? 1 : 0);
                        }
                        else {
                            n3 = (this.canSwipeRight ? 1 : 0);
                        }
                        int n8;
                        if (xVelocity < 0.0f) {
                            n8 = 1;
                        }
                        else {
                            n8 = 0;
                        }
                        if (n4 >= 0.0f) {
                            n = 0;
                        }
                        if (n8 != n) {
                            n3 = 0;
                        }
                        int mViewWidth;
                        if (xVelocity < 0.0f) {
                            mViewWidth = -this.mViewWidth;
                        }
                        else {
                            mViewWidth = this.mViewWidth;
                        }
                        n6 = (float)mViewWidth;
                        n7 = 0.0f;
                    }
                    else if (this.mMinFlingVelocity <= abs2 && abs2 <= this.mMaxFlingVelocity && abs < abs2 && this.mSwiping) {
                        if (this.mSwipingY) {
                            if (yVelocity < 0.0f) {
                                n3 = (this.canSwipeUp ? 1 : 0);
                            }
                            else {
                                n3 = (this.canSwipeDown ? 1 : 0);
                            }
                            int n9;
                            if (yVelocity < 0.0f) {
                                n9 = 1;
                            }
                            else {
                                n9 = 0;
                            }
                            int n10;
                            if (n5 < 0.0f) {
                                n10 = n2;
                            }
                            else {
                                n10 = 0;
                            }
                            if (n9 != n10) {
                                n3 = 0;
                            }
                            int mViewHeight;
                            if (yVelocity < 0.0f) {
                                mViewHeight = -this.mViewHeight;
                            }
                            else {
                                mViewHeight = this.mViewHeight;
                            }
                            n7 = (float)mViewHeight;
                            n6 = 0.0f;
                        }
                        else {
                            n7 = 0.0f;
                            n6 = 0.0f;
                            n3 = 0;
                        }
                    }
                    else {
                        n7 = 0.0f;
                        n6 = 0.0f;
                        n3 = 0;
                    }
                    if (n3 != 0) {
                        this.mView.animate().translationX(n6).translationY(n7).alpha(0.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
                            public void onAnimationEnd(final Animator animator) {
                                SwipeDismissTouchListener.this.performDismiss();
                            }
                        });
                    }
                    else if (this.mSwiping) {
                        this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)null);
                    }
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    this.mTranslationX = 0.0f;
                    this.mTranslationY = 0.0f;
                    this.mDownX = 0.0f;
                    this.mDownY = 0.0f;
                    this.mSwiping = false;
                    this.mSwipingX = false;
                    this.mSwipingY = false;
                    break;
                }
                break;
            }
            case 3: {
                if (this.mVelocityTracker != null) {
                    this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator$AnimatorListener)null);
                    this.mVelocityTracker.recycle();
                    this.mVelocityTracker = null;
                    this.mTranslationX = 0.0f;
                    this.mTranslationY = 0.0f;
                    this.mDownX = 0.0f;
                    this.mDownY = 0.0f;
                    this.mSwiping = false;
                    this.mSwipingX = false;
                    this.mSwipingY = false;
                    break;
                }
                break;
            }
            case 2: {
                Debug.Printf("SwipeSwipe: action move x %f y %f", this.mDownX, this.mDownY);
                if (this.mVelocityTracker == null) {
                    break;
                }
                this.mVelocityTracker.addMovement(motionEvent);
                final float n11 = motionEvent.getRawX() - this.mDownX;
                final float n12 = motionEvent.getRawY() - this.mDownY;
                if (!this.mSwiping) {
                    final boolean b = n11 < -this.mSlop && Math.abs(n12) < Math.abs(n11) / 2.0f && this.canSwipeLeft;
                    boolean mSwipingX = n11 > this.mSlop && Math.abs(n12) < Math.abs(n11) / 2.0f && this.canSwipeRight;
                    final boolean b2 = n12 < -this.mSlop && Math.abs(n11) < Math.abs(n12) / 2.0f && this.canSwipeUp;
                    boolean mSwipingY = n12 > this.mSlop && Math.abs(n11) < Math.abs(n12) / 2.0f && this.canSwipeDown;
                    if (b) {
                        mSwipingX = true;
                    }
                    if (b2) {
                        mSwipingY = true;
                    }
                    if (mSwipingX) {
                        if (mSwipingY) {
                            if (Math.abs(n11) >= Math.abs(n12)) {
                                mSwipingY = false;
                            }
                            else {
                                mSwipingX = false;
                            }
                        }
                    }
                    if (mSwipingX || mSwipingY) {
                        this.mSwiping = true;
                        this.mSwipingX = mSwipingX;
                        this.mSwipingY = mSwipingY;
                        int mSlop;
                        if (mSwipingX) {
                            if (n11 > 0.0f) {
                                mSlop = this.mSlop;
                            }
                            else {
                                mSlop = -this.mSlop;
                            }
                        }
                        else {
                            mSlop = 0;
                        }
                        this.mSwipingSlopX = mSlop;
                        int mSlop2;
                        if (mSwipingY) {
                            if (n12 > 0.0f) {
                                mSlop2 = this.mSlop;
                            }
                            else {
                                mSlop2 = -this.mSlop;
                            }
                        }
                        else {
                            mSlop2 = 0;
                        }
                        this.mSwipingSlopY = mSlop2;
                        this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                        final MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.setAction(motionEvent.getActionIndex() << 8 | 0x3);
                        this.mView.onTouchEvent(obtain);
                        obtain.recycle();
                    }
                }
                if (this.mSwiping) {
                    float mTranslationX;
                    if (this.mSwipingX) {
                        mTranslationX = n11;
                    }
                    else {
                        mTranslationX = 0.0f;
                    }
                    this.mTranslationX = mTranslationX;
                    float mTranslationY;
                    if (this.mSwipingY) {
                        mTranslationY = n12;
                    }
                    else {
                        mTranslationY = 0.0f;
                    }
                    this.mTranslationY = mTranslationY;
                    final View mView = this.mView;
                    float translationX;
                    if (this.mSwipingX) {
                        translationX = n11 - this.mSwipingSlopX;
                    }
                    else {
                        translationX = 0.0f;
                    }
                    mView.setTranslationX(translationX);
                    final View mView2 = this.mView;
                    float translationY;
                    if (this.mSwipingY) {
                        translationY = n12 - this.mSwipingSlopY;
                    }
                    else {
                        translationY = 0.0f;
                    }
                    mView2.setTranslationY(translationY);
                    if (this.mSwipingX) {
                        this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - Math.abs(n11) * 2.0f / this.mViewWidth)));
                    }
                    else if (this.mSwipingY) {
                        this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - Math.abs(n12) * 2.0f / this.mViewHeight)));
                    }
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return false;
    }
    
    public interface DismissCallbacks
    {
        boolean canDismiss(final Object p0);
        
        void onDismiss(final View p0, final Object p1);
    }
}
