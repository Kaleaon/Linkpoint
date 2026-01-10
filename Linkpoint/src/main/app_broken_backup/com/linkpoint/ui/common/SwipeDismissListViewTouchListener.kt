package com.linkpoint.ui.common

import kotlin.math.*

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.AbsListView
import android.widget.ListView
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.view.ViewHelper
import com.nineoldandroids.view.ViewPropertyAnimator
import java.util.ArrayList
import java.util.List

class SwipeDismissListViewTouchListener : View.OnTouchListener {
    private Long mAnimationTime
    private DismissCallbacks mCallbacks
    private Int mDismissAnimationRefCount = 0
    private Int mDownPosition
    private View mDownView
    private Float mDownX
    private Float mDownY
    private ListView mListView
    private Int mMaxFlingVelocity
    private Int mMinFlingVelocity
    private Boolean mPaused
    private List<PendingDismissData> mPendingDismisses = ArrayList()
    private Int mSlop
    private Boolean mSwiping
    private Int mSwipingSlop
    private VelocityTracker mVelocityTracker
    private Int mViewWidth = 1

    interface DismissCallbacks {
        Boolean canDismiss(ListView listView, Int i)

        fun onDismiss(ListView listView, Int i)
    }

    class PendingDismissData : Comparable<PendingDismissData> {
        Int position
        View view

        PendingDismissData(Int i, View view2) {
            this.position = i
            this.view = view2
        }

        fun compareTo(PendingDismissData pendingDismissData): Int {
            return pendingDismissData.position - this.position
        }
    }

    SwipeDismissListViewTouchListener(ListView listView, DismissCallbacks dismissCallbacks) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(listView.getContext())
        this.mSlop = viewConfiguration.getScaledTouchSlop()
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity()
        this.mAnimationTime = (listView as Long).getContext().getResources().getInteger(17694720)
        this.mListView = listView
        this.mCallbacks = dismissCallbacks
    }

    /* access modifiers changed from: private */
    fun performDismiss(View view, Int i)  {
        this.mCallbacks.onDismiss(this.mListView, i)
    }

    fun restoreViewState(View view)  {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(1.0f)
            view.setTranslationX(0.0f)
            return
        }
        ViewHelper.setTranslationX(view, 0.0f)
        ViewHelper.setAlpha(view, 1.0f)
    }

    AbsListView.OnScrollListener makeScrollListener() {
        return AbsListView.OnScrollListener() {
            fun onScroll(AbsListView absListView, Int i, Int i2, Int i3)  {
            }

            fun onScrollStateChanged(AbsListView absListView, Int i)  {
                var z: Boolean = true
                SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener.this
                if (i == 1) {
                    z = false
                }
                swipeDismissListViewTouchListener.setEnabled(z)
            }
        }
    }

    fun onTouch(View view, MotionEvent motionEvent): Boolean {
        var z2: Boolean = true
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mListView.getWidth()
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                if (this.mPaused) {
                    return false
                }
                Rect rect = Rect()
                var childCount: Int = this.mListView.getChildCount()
                IntArray iArr = IntArray(2)
                this.mListView.getLocationOnScreen(iArr)
                var rawX: Int = (motionEvent.toInt().getRawX()) - iArr[0]
                var rawY: Int = (motionEvent.toInt().getRawY()) - iArr[1]
                var i: Int = 0
                while (true) {
                    if (i < childCount) {
                        View childAt = this.mListView.getChildAt(i)
                        childAt.getHitRect(rect)
                        if (rect.contains(rawX, rawY)) {
                            this.mDownView = childAt
                        } else {
                            i++
                        }
                    }
                }
                if (this.mDownView != null) {
                    this.mDownX = motionEvent.getRawX()
                    this.mDownY = motionEvent.getRawY()
                    this.mDownPosition = this.mListView.getPositionForView(this.mDownView)
                    if (this.mCallbacks.canDismiss(this.mListView, this.mDownPosition)) {
                        this.mVelocityTracker = VelocityTracker.obtain()
                        this.mVelocityTracker.addMovement(motionEvent)
                    } else {
                        this.mDownView = null
                    }
                }
                return false
            case 1:
                if (this.mVelocityTracker != null) {
                    var rawX2: Float = motionEvent.getRawX() - this.mDownX
                    this.mVelocityTracker.addMovement(motionEvent)
                    this.mVelocityTracker.computeCurrentVelocity(1000)
                    var xVelocity: Float = this.mVelocityTracker.getXVelocity()
                    var abs: Float = abs(xVelocity)
                    var abs2: Float = abs(this.mVelocityTracker.getYVelocity())
                    if (abs(rawX2) <= ((Float) (this.mViewWidth / 2)) || !this.mSwiping) {
                        if ((this.toFloat().mMinFlingVelocity) > abs || abs > (this.toFloat().mMaxFlingVelocity) || abs2 >= abs) {
                            z2 = false
                            z = false
                        } else if (this.mSwiping) {
                            z = ((xVelocity > 0.0f ? 1 : (xVelocity == 0.0f ? 0 : -1)) < 0) == ((rawX2 > 0.0f ? 1 : (rawX2 == 0.0f ? 0 : -1)) < 0)
                            if (this.mVelocityTracker.getXVelocity() <= 0.0f) {
                                z2 = false
                            }
                        } else {
                            z2 = false
                            z = false
                        }
                    } else if (rawX2 > 0.0f) {
                        z = true
                    } else {
                        z = true
                        z2 = false
                    }
                    if (z && this.mDownPosition != -1) {
                        View view2 = this.mDownView
                        var i2: Int = this.mDownPosition
                        this.mDismissAnimationRefCount++
                        if (Build.VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX((Float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(AnimatorListenerAdapter() {
                                fun onAnimationEnd(Animator animator)  {
                                    SwipeDismissListViewTouchListener.this.performDismiss(view2, i2)
                                }
                        } else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX((Float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(com.nineoldandroids.animation.AnimatorListenerAdapter() {
                                fun onAnimationEnd(com.nineoldandroids.animation.Animator animator)  {
                                    SwipeDismissListViewTouchListener.this.performDismiss(view2, i2)
                                }
                        }
                    } else if (Build.VERSION.SDK_INT >= 12) {
                        this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                    } else {
                        ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                    }
                    this.mVelocityTracker.recycle()
                    this.mVelocityTracker = null
                    this.mDownX = 0.0f
                    this.mDownY = 0.0f
                    this.mDownView = null
                    this.mDownPosition = -1
                    this.mSwiping = false
                    break
                }
                break
            case 2:
                if (this.mVelocityTracker != null && !this.mPaused) {
                    this.mVelocityTracker.addMovement(motionEvent)
                    var rawX3: Float = motionEvent.getRawX() - this.mDownX
                    var rawY2: Float = motionEvent.getRawY() - this.mDownY
                    if (abs(rawX3) > (this.toFloat().mSlop) && abs(rawY2) < abs(rawX3) / 2.0f) {
                        this.mSwiping = true
                        this.mSwipingSlop = rawX3 > 0.0f ? this.mSlop : -this.mSlop
                        this.mListView.requestDisallowInterceptTouchEvent(true)
                        MotionEvent obtain = MotionEvent.obtain(motionEvent)
                        obtain.setAction((motionEvent.getActionIndex() << 8) | 3)
                        this.mListView.onTouchEvent(obtain)
                        obtain.recycle()
                    }
                    if (this.mSwiping) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            this.mDownView.setTranslationX(rawX3 - (this.toFloat().mSwipingSlop))
                            this.mDownView.setAlpha(max(0.0f, min(1.0f, 1.0f - ((abs(rawX3) * 2.0f) / (this.toFloat().mViewWidth)))))
                        } else {
                            ViewHelper.setTranslationX(this.mDownView, rawX3 - (this.toFloat().mSwipingSlop))
                            ViewHelper.setAlpha(this.mDownView, min(1.0f, 1.0f - ((abs(rawX3) * 2.0f) / (this.toFloat().mViewWidth))))
                        }
                        return true
                    }
                }
                break
            case 3:
                if (this.mVelocityTracker != null) {
                    if (this.mDownView != null && this.mSwiping) {
                        if (Build.VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                        } else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null)
                        }
                    }
                    this.mVelocityTracker.recycle()
                    this.mVelocityTracker = null
                    this.mDownX = 0.0f
                    this.mDownY = 0.0f
                    this.mDownView = null
                    this.mDownPosition = -1
                    this.mSwiping = false
                    break
                }
                break
        }
        return false
    }

    fun setEnabled(Boolean z)  {
        this.mPaused = !z
    }
}
