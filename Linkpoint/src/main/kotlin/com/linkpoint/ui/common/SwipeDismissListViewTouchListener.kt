package com.linkpoint.ui.common

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
         fun canDismiss(listView: ListView, Int i): Boolean)

         fun onDismiss(listView: ListView, i: Int)
    }

    class PendingDismissData : Comparable<PendingDismissData> {
        public Int position
        public View view

        public PendingDismissData(Int i, View view2) {
            this.position = i
            this.view = view2
        }

         public fun compareTo(pendingDismissData: PendingDismissData): Int {
            return pendingDismissData.position - this.position
        }
    }

    public SwipeDismissListViewTouchListener(ListView listView, DismissCallbacks dismissCallbacks) {
        val viewConfiguration: ViewConfiguration = ViewConfiguration.get(listView.getContext())
        this.mSlop = viewConfiguration.getScaledTouchSlop()
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity()
        this.mAnimationTime = (Long) listView.getContext().getResources().getInteger(17694720)
        this.mListView = listView
        this.mCallbacks = dismissCallbacks
    }

    /* access modifiers changed from: private */
    fun performDismiss(view: View, i: Int) {
        this.mCallbacks.onDismiss(this.mListView, i)
    }

    @JvmStatic
     fun restoreViewState(view: View) {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(1.0f)
            view.setTranslationX(0.0f)
            return
        }
        ViewHelper.setTranslationX(view, 0.0f)
        ViewHelper.setAlpha(view, 1.0f)
    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return AbsListView.OnScrollListener() {
            fun onScroll(absListView: AbsListView, i: Int, i2: Int, i3: Int) {
            }

            fun onScrollStateChanged(absListView: AbsListView, i: Int) {
                val z: Boolean = true
                val swipeDismissListViewTouchListener: SwipeDismissListViewTouchListener = SwipeDismissListViewTouchListener.this
                if (i == 1) {
                    z = false
                }
                swipeDismissListViewTouchListener.setEnabled(z)
            }
        }
    }

     public fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val z2: Boolean = true
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mListView.getWidth()
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                if (this.mPaused) {
                    return false
                }
                val rect: Rect = Rect()
                val childCount: Int = this.mListView.getChildCount()
                val iArr: IntArray = Int[2]
                this.mListView.getLocationOnScreen(iArr)
                val rawX: Int = ((Int) motionEvent.getRawX()) - iArr[0]
                val rawY: Int = ((Int) motionEvent.getRawY()) - iArr[1]
                val i: Int = 0
                while (true) {
                    if (i < childCount) {
                        val childAt: View = this.mListView.getChildAt(i)
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
                    val rawX2: Float = motionEvent.getRawX() - this.mDownX
                    this.mVelocityTracker.addMovement(motionEvent)
                    this.mVelocityTracker.computeCurrentVelocity(1000)
                    val xVelocity: Float = this.mVelocityTracker.getXVelocity()
                    val abs: Float = Math.abs(xVelocity)
                    val abs2: Float = Math.abs(this.mVelocityTracker.getYVelocity())
                    if (Math.abs(rawX2) <= ((Float) (this.mViewWidth / 2)) || !this.mSwiping) {
                        if (((Float) this.mMinFlingVelocity) > abs || abs > ((Float) this.mMaxFlingVelocity) || abs2 >= abs) {
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
                        final View view2 = this.mDownView
                        final Int i2 = this.mDownPosition
                        this.mDismissAnimationRefCount++
                        if (Build.VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX((Float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(AnimatorListenerAdapter() {
                                fun onAnimationEnd(animator: Animator) {
                                    SwipeDismissListViewTouchListener.this.performDismiss(view2, i2)
                                }
                        } else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX((Float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(com.nineoldandroids.animation.AnimatorListenerAdapter() {
                                fun onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
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
                    val rawX3: Float = motionEvent.getRawX() - this.mDownX
                    val rawY2: Float = motionEvent.getRawY() - this.mDownY
                    if (Math.abs(rawX3) > ((Float) this.mSlop) && Math.abs(rawY2) < Math.abs(rawX3) / 2.0f) {
                        this.mSwiping = true
                        this.mSwipingSlop = rawX3 > 0.0f ? this.mSlop : -this.mSlop
                        this.mListView.requestDisallowInterceptTouchEvent(true)
                        val obtain: MotionEvent = MotionEvent.obtain(motionEvent)
                        obtain.setAction((motionEvent.getActionIndex() << 8) | 3)
                        this.mListView.onTouchEvent(obtain)
                        obtain.recycle()
                    }
                    if (this.mSwiping) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            this.mDownView.setTranslationX(rawX3 - ((Float) this.mSwipingSlop))
                            this.mDownView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawX3) * 2.0f) / ((Float) this.mViewWidth)))))
                        } else {
                            ViewHelper.setTranslationX(this.mDownView, rawX3 - ((Float) this.mSwipingSlop))
                            ViewHelper.setAlpha(this.mDownView, Math.min(1.0f, 1.0f - ((Math.abs(rawX3) * 2.0f) / ((Float) this.mViewWidth))))
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

    fun setEnabled(z: Boolean) {
        this.mPaused = !z
    }
}
