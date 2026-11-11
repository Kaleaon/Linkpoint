package com.lumiyaviewer.lumiya.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.Debug;
@TargetApi(12)
/* loaded from: classes.dex */
public class SwipeDismissTouchListener implements OnInterceptTouchEventListener {
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
    private int mViewWidth = 1;
    private int mViewHeight = 1;

    /* loaded from: classes.dex */
    public interface DismissCallbacks {
        boolean canDismiss(Object obj);

        void onDismiss(View view, Object obj);
    }

    public SwipeDismissTouchListener(View view, Object obj, DismissCallbacks dismissCallbacks, boolean z, boolean z2, boolean z3, boolean z4) {
        this.canSwipeUp = z;
        this.canSwipeDown = z2;
        this.canSwipeLeft = z3;
        this.canSwipeRight = z4;
        this.canSwipeX = z3 ? true : z4;
        this.canSwipeY = z ? true : z2;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext());
        this.mSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16;
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mAnimationTime = view.getContext().getResources().getInteger(17694720);
        this.mView = view;
        this.mToken = obj;
        this.mCallbacks = dismissCallbacks;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDismiss() {
        final ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
        final int height = this.mView.getHeight();
        ValueAnimator duration = ValueAnimator.ofInt(height, 1).setDuration(this.mAnimationTime);
        duration.addListener(new AnimatorListenerAdapter() { // from class: com.lumiyaviewer.lumiya.ui.common.SwipeDismissTouchListener.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwipeDismissTouchListener.this.mCallbacks.onDismiss(SwipeDismissTouchListener.this.mView, SwipeDismissTouchListener.this.mToken);
                SwipeDismissTouchListener.this.mView.setAlpha(1.0f);
                SwipeDismissTouchListener.this.mView.setTranslationX(0.0f);
                SwipeDismissTouchListener.this.mView.setTranslationY(0.0f);
                layoutParams.height = height;
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams);
            }
        });
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lumiyaviewer.lumiya.ui.common.SwipeDismissTouchListener.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height = ((Integer) valueAnimator.getAnimatedValue()).intValue();
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams);
            }
        });
        duration.start();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.OnInterceptTouchEventListener
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.OnInterceptTouchEventListener
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        float f;
        float f2;
        motionEvent.offsetLocation(this.mTranslationX, this.mTranslationY);
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mView.getWidth();
        }
        if (this.mViewHeight < 2) {
            this.mViewHeight = this.mView.getHeight();
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mDownX = motionEvent.getRawX();
                this.mDownY = motionEvent.getRawY();
                Debug.Printf("SwipeSwipe: action down x %f y %f", Float.valueOf(this.mDownX), Float.valueOf(this.mDownY));
                if (this.mCallbacks.canDismiss(this.mToken)) {
                    this.mVelocityTracker = VelocityTracker.obtain();
                    this.mVelocityTracker.addMovement(motionEvent);
                }
                return true;
            case 1:
                if (this.mVelocityTracker != null) {
                    float rawX = motionEvent.getRawX() - this.mDownX;
                    float rawY = motionEvent.getRawY() - this.mDownY;
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    float xVelocity = this.mVelocityTracker.getXVelocity();
                    float yVelocity = this.mVelocityTracker.getYVelocity();
                    float abs = Math.abs(xVelocity);
                    float abs2 = Math.abs(yVelocity);
                    if (this.mSwiping && this.mSwipingX && this.canSwipeRight && rawX > this.mViewWidth / 2) {
                        f2 = this.mViewWidth;
                        f = 0.0f;
                    } else if (this.mSwiping && this.mSwipingX && this.canSwipeLeft && rawX < (-(this.mViewWidth / 2))) {
                        f2 = -this.mViewWidth;
                        f = 0.0f;
                    } else if (this.mSwiping && this.mSwipingY && this.canSwipeDown && rawY > this.mViewHeight / 2) {
                        f = this.mViewHeight;
                        f2 = 0.0f;
                    } else if (this.mSwiping && this.mSwipingY && this.canSwipeUp && rawY < (-(this.mViewHeight / 2))) {
                        f = -this.mViewHeight;
                        f2 = 0.0f;
                    } else if (this.mMinFlingVelocity <= abs && abs <= this.mMaxFlingVelocity && abs2 < abs && this.mSwiping && this.mSwipingX) {
                        r7 = ((xVelocity > 0.0f ? 1 : (xVelocity == 0.0f ? 0 : -1)) < 0) == (rawX < 0.0f) ? xVelocity < 0.0f ? this.canSwipeLeft : this.canSwipeRight : false;
                        f2 = xVelocity < 0.0f ? -this.mViewWidth : this.mViewWidth;
                        f = 0.0f;
                    } else if (this.mMinFlingVelocity > abs2 || abs2 > this.mMaxFlingVelocity || abs >= abs2 || !this.mSwiping) {
                        f = 0.0f;
                        f2 = 0.0f;
                        r7 = false;
                    } else if (this.mSwipingY) {
                        r7 = ((yVelocity > 0.0f ? 1 : (yVelocity == 0.0f ? 0 : -1)) < 0) == (rawY < 0.0f) ? yVelocity < 0.0f ? this.canSwipeUp : this.canSwipeDown : false;
                        f = yVelocity < 0.0f ? -this.mViewHeight : this.mViewHeight;
                        f2 = 0.0f;
                    } else {
                        f = 0.0f;
                        f2 = 0.0f;
                        r7 = false;
                    }
                    if (r7) {
                        this.mView.animate().translationX(f2).translationY(f).alpha(0.0f).setDuration(this.mAnimationTime).setListener(new AnimatorListenerAdapter() { // from class: com.lumiyaviewer.lumiya.ui.common.SwipeDismissTouchListener.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                SwipeDismissTouchListener.this.performDismiss();
                            }
                        });
                    } else if (this.mSwiping) {
                        this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener(null);
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
            case 2:
                Debug.Printf("SwipeSwipe: action move x %f y %f", Float.valueOf(this.mDownX), Float.valueOf(this.mDownY));
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(motionEvent);
                    float rawX2 = motionEvent.getRawX() - this.mDownX;
                    float rawY2 = motionEvent.getRawY() - this.mDownY;
                    if (!this.mSwiping) {
                        boolean z3 = (rawX2 >= ((float) (-this.mSlop)) || Math.abs(rawY2) >= Math.abs(rawX2) / 2.0f) ? false : this.canSwipeLeft;
                        boolean z4 = (rawX2 <= ((float) this.mSlop) || Math.abs(rawY2) >= Math.abs(rawX2) / 2.0f) ? false : this.canSwipeRight;
                        boolean z5 = (rawY2 >= ((float) (-this.mSlop)) || Math.abs(rawX2) >= Math.abs(rawY2) / 2.0f) ? false : this.canSwipeUp;
                        boolean z6 = (rawY2 <= ((float) this.mSlop) || Math.abs(rawX2) >= Math.abs(rawY2) / 2.0f) ? false : this.canSwipeDown;
                        if (z3) {
                            z4 = true;
                        }
                        boolean z7 = !z5 ? z6 : true;
                        if (!z4) {
                            boolean z8 = z7;
                            z = z4;
                            z2 = z8;
                        } else if (!z7) {
                            boolean z9 = z7;
                            z = z4;
                            z2 = z9;
                        } else if (Math.abs(rawX2) >= Math.abs(rawY2)) {
                            z = z4;
                            z2 = false;
                        } else {
                            z2 = z7;
                            z = false;
                        }
                        if (z || z2) {
                            this.mSwiping = true;
                            this.mSwipingX = z;
                            this.mSwipingY = z2;
                            this.mSwipingSlopX = z ? rawX2 > 0.0f ? this.mSlop : -this.mSlop : 0;
                            this.mSwipingSlopY = z2 ? rawY2 > 0.0f ? this.mSlop : -this.mSlop : 0;
                            this.mView.getParent().requestDisallowInterceptTouchEvent(true);
                            MotionEvent obtain = MotionEvent.obtain(motionEvent);
                            obtain.setAction((motionEvent.getActionIndex() << 8) | 3);
                            this.mView.onTouchEvent(obtain);
                            obtain.recycle();
                        }
                    }
                    if (this.mSwiping) {
                        this.mTranslationX = this.mSwipingX ? rawX2 : 0.0f;
                        this.mTranslationY = this.mSwipingY ? rawY2 : 0.0f;
                        this.mView.setTranslationX(this.mSwipingX ? rawX2 - this.mSwipingSlopX : 0.0f);
                        this.mView.setTranslationY(this.mSwipingY ? rawY2 - this.mSwipingSlopY : 0.0f);
                        if (this.mSwipingX) {
                            this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawX2) * 2.0f) / this.mViewWidth))));
                        } else if (this.mSwipingY) {
                            this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawY2) * 2.0f) / this.mViewHeight))));
                        }
                        return true;
                    }
                }
                break;
            case 3:
                if (this.mVelocityTracker != null) {
                    this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener(null);
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
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.OnInterceptTouchEventListener
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }
}
