package com.linkpoint.ui.common
import java.util.*

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import com.linkpoint.Debug

@TargetApi(12)
class SwipeDismissTouchListener : OnInterceptTouchEventListener {
    private val Boolean canSwipeDown
    private val Boolean canSwipeLeft
    private val Boolean canSwipeRight
    private val Boolean canSwipeUp
    private val Boolean canSwipeX
    private val Boolean canSwipeY
    private Long mAnimationTime
    /* access modifiers changed from: private */
    public DismissCallbacks mCallbacks
    private Float mDownX
    private Float mDownY
    private Int mMaxFlingVelocity
    private Int mMinFlingVelocity
    private Int mSlop
    private Boolean mSwiping
    private Int mSwipingSlopX
    private Int mSwipingSlopY
    private Boolean mSwipingX
    private Boolean mSwipingY
    /* access modifiers changed from: private */
    public Object mToken
    private Float mTranslationX
    private Float mTranslationY
    private VelocityTracker mVelocityTracker
    /* access modifiers changed from: private */
    public View mView
    private Int mViewHeight = 1
    private Int mViewWidth = 1

    interface DismissCallbacks {
        Boolean canDismiss(Object obj)

        Unit onDismiss(View view, Object obj)
    }

    public SwipeDismissTouchListener(View view, Object obj, DismissCallbacks dismissCallbacks, Boolean z, Boolean z2, Boolean z3, Boolean z4) {
        Boolean z5 = true
        this.canSwipeUp = z
        this.canSwipeDown = z2
        this.canSwipeLeft = z3
        this.canSwipeRight = z4
        this.canSwipeX = z3 ? true : z4
        this.canSwipeY = !z ? z2 : z5
        ViewConfiguration viewConfiguration = ViewConfiguration.get(view.getContext())
        this.mSlop = viewConfiguration.getScaledTouchSlop()
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity()
        this.mAnimationTime = (Long) view.getContext().getResources().getInteger(17694720)
        this.mView = view
        this.mToken = obj
        this.mCallbacks = dismissCallbacks
    }

    /* access modifiers changed from: private */
    fun performDismiss() {
        final ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams()
        final Int height = this.mView.getHeight()
        ValueAnimator duration = ValueAnimator.ofInt(IntArray{height, 1}).setDuration(this.mAnimationTime)
        duration.addListener(AnimatorListenerAdapter() {
            fun onAnimationEnd(Animator animator) {
                SwipeDismissTouchListener.this.mCallbacks.onDismiss(SwipeDismissTouchListener.this.mView, SwipeDismissTouchListener.this.mToken)
                SwipeDismissTouchListener.this.mView.setAlpha(1.0f)
                SwipeDismissTouchListener.this.mView.setTranslationX(0.0f)
                SwipeDismissTouchListener.this.mView.setTranslationY(0.0f)
                layoutParams.height = height
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams)
            }
        duration.addUpdateListener(ValueAnimator.AnimatorUpdateListener() {
            fun onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height = ((Integer) valueAnimator.getAnimatedValue()).intValue()
                SwipeDismissTouchListener.this.mView.setLayoutParams(layoutParams)
            }
        duration.start()
    }

    public Boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false
    }

    public Boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Float f
        Float f2
        Boolean z3 = true
        motionEvent.offsetLocation(this.mTranslationX, this.mTranslationY)
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mView.getWidth()
        }
        if (this.mViewHeight < 2) {
            this.mViewHeight = this.mView.getHeight()
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mDownX = motionEvent.getRawX()
                this.mDownY = motionEvent.getRawY()
                Debug.Printf("SwipeSwipe: action down x %f y %f", Float.valueOf(this.mDownX), Float.valueOf(this.mDownY))
                if (this.mCallbacks.canDismiss(this.mToken)) {
                    this.mVelocityTracker = VelocityTracker.obtain()
                    this.mVelocityTracker.addMovement(motionEvent)
                }
                return true
            case 1:
                if (this.mVelocityTracker != null) {
                    Float rawX = motionEvent.getRawX() - this.mDownX
                    Float rawY = motionEvent.getRawY() - this.mDownY
                    this.mVelocityTracker.addMovement(motionEvent)
                    this.mVelocityTracker.computeCurrentVelocity(1000)
                    Float xVelocity = this.mVelocityTracker.getXVelocity()
                    Float yVelocity = this.mVelocityTracker.getYVelocity()
                    Float abs = Math.abs(xVelocity)
                    Float abs2 = Math.abs(yVelocity)
                    if (this.mSwiping && this.mSwipingX && this.canSwipeRight && rawX > ((Float) (this.mViewWidth / 2))) {
                        f2 = (Float) this.mViewWidth
                        f = 0.0f
                    } else if (this.mSwiping && this.mSwipingX && this.canSwipeLeft && rawX < ((Float) (-(this.mViewWidth / 2)))) {
                        f2 = (Float) (-this.mViewWidth)
                        f = 0.0f
                    } else if (this.mSwiping && this.mSwipingY && this.canSwipeDown && rawY > ((Float) (this.mViewHeight / 2))) {
                        f = (Float) this.mViewHeight
                        f2 = 0.0f
                    } else if (this.mSwiping && this.mSwipingY && this.canSwipeUp && rawY < ((Float) (-(this.mViewHeight / 2)))) {
                        f = (Float) (-this.mViewHeight)
                        f2 = 0.0f
                    } else if (((Float) this.mMinFlingVelocity) <= abs && abs <= ((Float) this.mMaxFlingVelocity) && abs2 < abs && this.mSwiping && this.mSwipingX) {
                        Boolean z4 = xVelocity < 0.0f ? this.canSwipeLeft : this.canSwipeRight
                        Boolean z5 = xVelocity < 0.0f
                        if (rawX >= 0.0f) {
                            z3 = false
                        }
                        z3 = z5 == z3 ? z4 : false
                        f2 = (Float) (xVelocity < 0.0f ? -this.mViewWidth : this.mViewWidth)
                        f = 0.0f
                    } else if (((Float) this.mMinFlingVelocity) > abs2 || abs2 > ((Float) this.mMaxFlingVelocity) || abs >= abs2 || !this.mSwiping) {
                        f = 0.0f
                        f2 = 0.0f
                        z3 = false
                    } else if (this.mSwipingY) {
                        Boolean z6 = yVelocity < 0.0f ? this.canSwipeUp : this.canSwipeDown
                        Boolean z7 = yVelocity < 0.0f
                        if (rawY >= 0.0f) {
                            z3 = false
                        }
                        z3 = z7 == z3 ? z6 : false
                        f = (Float) (yVelocity < 0.0f ? -this.mViewHeight : this.mViewHeight)
                        f2 = 0.0f
                    } else {
                        f = 0.0f
                        f2 = 0.0f
                        z3 = false
                    }
                    if (z3) {
                        this.mView.animate().translationX(f2).translationY(f).alpha(0.0f).setDuration(this.mAnimationTime).setListener(AnimatorListenerAdapter() {
                            fun onAnimationEnd(Animator animator) {
                                SwipeDismissTouchListener.this.performDismiss()
                            }
                    } else if (this.mSwiping) {
                        this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                    }
                    this.mVelocityTracker.recycle()
                    this.mVelocityTracker = null
                    this.mTranslationX = 0.0f
                    this.mTranslationY = 0.0f
                    this.mDownX = 0.0f
                    this.mDownY = 0.0f
                    this.mSwiping = false
                    this.mSwipingX = false
                    this.mSwipingY = false
                    break
                }
                break
            case 2:
                Debug.Printf("SwipeSwipe: action move x %f y %f", Float.valueOf(this.mDownX), Float.valueOf(this.mDownY))
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.addMovement(motionEvent)
                    Float rawX2 = motionEvent.getRawX() - this.mDownX
                    Float rawY2 = motionEvent.getRawY() - this.mDownY
                    if (!this.mSwiping) {
                        Boolean z8 = (rawX2 >= ((Float) (-this.mSlop)) || Math.abs(rawY2) >= Math.abs(rawX2) / 2.0f) ? false : this.canSwipeLeft
                        Boolean z9 = (rawX2 <= ((Float) this.mSlop) || Math.abs(rawY2) >= Math.abs(rawX2) / 2.0f) ? false : this.canSwipeRight
                        Boolean z10 = (rawY2 >= ((Float) (-this.mSlop)) || Math.abs(rawX2) >= Math.abs(rawY2) / 2.0f) ? false : this.canSwipeUp
                        Boolean z11 = (rawY2 <= ((Float) this.mSlop) || Math.abs(rawX2) >= Math.abs(rawY2) / 2.0f) ? false : this.canSwipeDown
                        if (z8) {
                            z9 = true
                        }
                        Boolean z12 = !z10 ? z11 : true
                        if (!z9) {
                            Boolean z13 = z12
                            z = z9
                            z2 = z13
                        } else if (!z12) {
                            Boolean z14 = z12
                            z = z9
                            z2 = z14
                        } else if (Math.abs(rawX2) >= Math.abs(rawY2)) {
                            z = z9
                            z2 = false
                        } else {
                            z2 = z12
                            z = false
                        }
                        if (z || z2) {
                            this.mSwiping = true
                            this.mSwipingX = z
                            this.mSwipingY = z2
                            this.mSwipingSlopX = z ? rawX2 > 0.0f ? this.mSlop : -this.mSlop : 0
                            this.mSwipingSlopY = z2 ? rawY2 > 0.0f ? this.mSlop : -this.mSlop : 0
                            this.mView.getParent().requestDisallowInterceptTouchEvent(true)
                            MotionEvent obtain = MotionEvent.obtain(motionEvent)
                            obtain.setAction((motionEvent.getActionIndex() << 8) | 3)
                            this.mView.onTouchEvent(obtain)
                            obtain.recycle()
                        }
                    }
                    if (this.mSwiping) {
                        this.mTranslationX = this.mSwipingX ? rawX2 : 0.0f
                        this.mTranslationY = this.mSwipingY ? rawY2 : 0.0f
                        this.mView.setTranslationX(this.mSwipingX ? rawX2 - ((Float) this.mSwipingSlopX) : 0.0f)
                        this.mView.setTranslationY(this.mSwipingY ? rawY2 - ((Float) this.mSwipingSlopY) : 0.0f)
                        if (this.mSwipingX) {
                            this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawX2) * 2.0f) / ((Float) this.mViewWidth)))))
                        } else if (this.mSwipingY) {
                            this.mView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawY2) * 2.0f) / ((Float) this.mViewHeight)))))
                        }
                        return true
                    }
                }
                break
            case 3:
                if (this.mVelocityTracker != null) {
                    this.mView.animate().translationX(0.0f).translationY(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                    this.mVelocityTracker.recycle()
                    this.mVelocityTracker = null
                    this.mTranslationX = 0.0f
                    this.mTranslationY = 0.0f
                    this.mDownX = 0.0f
                    this.mDownY = 0.0f
                    this.mSwiping = false
                    this.mSwipingX = false
                    this.mSwipingY = false
                    break
                }
                break
        }
        return false
    }

    public Boolean onTouchEvent(MotionEvent motionEvent) {
        return false
    }
}
