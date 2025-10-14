package com.lumiyaviewer.lumiya.ui.common
import java.util.*

import android.support.annotation.NonNull
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class SwipeDismissAdvancedBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    private Float DEFAULT_ALPHA_END_DISTANCE = 1.0f
    private Float DEFAULT_ALPHA_START_DISTANCE = 0.0f
    private Float DEFAULT_DRAG_DISMISS_THRESHOLD = 1.0f
    Int STATE_DRAGGING = 1
    Int STATE_IDLE = 0
    Int STATE_SETTLING = 2
    Int SWIPE_DIRECTION_ANY = 15
    Int SWIPE_DIRECTION_DOWN = 8
    Int SWIPE_DIRECTION_LEFT = 1
    Int SWIPE_DIRECTION_RIGHT = 2
    Int SWIPE_DIRECTION_UP = 4
    Int SWIPE_DIRECTION_X = 3
    Int SWIPE_DIRECTION_Y = 12
    /* access modifiers changed from: private */
    Float mAlphaEndSwipeDistance = 1.0f
    /* access modifiers changed from: private */
    Float mAlphaStartSwipeDistance = 0.0f
    private ViewDragHelper.Callback mDragCallback = ViewDragHelper.Callback() {
        private Int mOriginalCapturedViewLeft
        private Int mOriginalCapturedViewTop

        private Boolean shouldDismiss(View view, Float f, Float f2) {
            Float scaledMinimumFlingVelocity = (Float) ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity()
            if (f < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                return true
            }
            if (f > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                return true
            }
            if (f2 < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0) {
                return true
            }
            if (f2 > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                return true
            }
            Int left = view.getLeft() - this.mOriginalCapturedViewLeft
            Int round = Math.round(((Float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold)
            if (left < (-round) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                return true
            }
            if (left > round && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                return true
            }
            Int top = view.getTop() - this.mOriginalCapturedViewTop
            Int round2 = Math.round(((Float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold)
            if (top >= (-round2) || (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) == 0) {
                return top > round2 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0
            }
            return true
        }

        Int clampViewPositionHorizontal(View view, Int i, Int i2) {
            Int i3 = 0
            if (view.getTop() != this.mOriginalCapturedViewTop) {
                return this.mOriginalCapturedViewLeft
            }
            Int width = this.mOriginalCapturedViewLeft - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0 ? view.getWidth() : 0)
            Int i4 = this.mOriginalCapturedViewLeft
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                i3 = view.getWidth()
            }
            return SwipeDismissAdvancedBehavior.clamp(width, i, i3 + i4)
        }

        Int clampViewPositionVertical(View view, Int i, Int i2) {
            Int i3 = 0
            if (view.getLeft() != this.mOriginalCapturedViewLeft) {
                return this.mOriginalCapturedViewTop
            }
            Int height = this.mOriginalCapturedViewTop - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0 ? view.getHeight() : 0)
            Int i4 = this.mOriginalCapturedViewTop
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                i3 = view.getHeight()
            }
            return SwipeDismissAdvancedBehavior.clamp(height, i, i3 + i4)
        }

        Int getViewHorizontalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0) {
                return view.getWidth()
            }
            return 0
        }

        Int getViewVerticalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0) {
                return view.getWidth()
            }
            return 0
        }

        Unit onViewCaptured(View view, Int i) {
            this.mOriginalCapturedViewLeft = view.getLeft()
            this.mOriginalCapturedViewTop = view.getTop()
        }

        Unit onViewDragStateChanged(Int i) {
            if (SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDragStateChanged(i)
            }
        }

        Unit onViewPositionChanged(View view, Int i, Int i2, Int i3, Int i4) {
            Int i5 = 0
            Int abs = (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0 ? Math.abs(i - this.mOriginalCapturedViewLeft) : 0
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0) {
                i5 = Math.abs(i2 - this.mOriginalCapturedViewTop)
            }
            if (abs == 0 && i5 == 0) {
                ViewCompat.setAlpha(view, 1.0f)
            } else {
                ViewCompat.setAlpha(view, 1.0f - Math.max(SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(((Float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, ((Float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (Float) abs), 1.0f), SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(((Float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, ((Float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (Float) i5), 1.0f)))
            }
        }

        Unit onViewReleased(View view, Float f, Float f2) {
            Int width = view.getWidth()
            Int height = view.getHeight()
            Int left = view.getLeft()
            Int top = view.getTop()
            if (shouldDismiss(view, f, f2)) {
                Float scaledMinimumFlingVelocity = (Float) ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity()
                if (f < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                    left = this.mOriginalCapturedViewLeft - width
                } else if (f > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                    left = this.mOriginalCapturedViewLeft + width
                } else if (f2 < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0) {
                    top = this.mOriginalCapturedViewTop - height
                } else if (f2 > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                    top = this.mOriginalCapturedViewTop + height
                }
                i = left
                i2 = top
                z = true
            } else {
                i = this.mOriginalCapturedViewLeft
                i2 = this.mOriginalCapturedViewTop
                z = false
            }
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper.settleCapturedViewAt(i, i2)) {
                ViewCompat.postOnAnimation(view, SettleRunnable(view, z))
            } else if (z && SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(view)
            }
        }

        Boolean tryCaptureView(View view, Int i) {
            return SwipeDismissAdvancedBehavior.this.canSwipeDismissView(view)
        }
    }
    /* access modifiers changed from: private */
    Float mDragDismissThreshold = 1.0f
    private Boolean mIgnoreEvents
    /* access modifiers changed from: private */
    OnDismissListener mListener
    private Float mSensitivity = 0.0f
    private Boolean mSensitivitySet
    /* access modifiers changed from: private */
    Int mSwipeDirection = 15
    /* access modifiers changed from: private */
    ViewDragHelper mViewDragHelper

    interface OnDismissListener {
        Unit onDismiss(View view)

        Unit onDragStateChanged(Int i)
    }

    private class SettleRunnable : Runnable {
        private Boolean mDismiss
        private View mView

        SettleRunnable(View view, Boolean z) {
            this.mView = view
            this.mDismiss = z
        }

        Unit run() {
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper != null && SwipeDismissAdvancedBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this)
            } else if (this.mDismiss && SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(this.mView)
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface SwipeDirection {
    }

    /* access modifiers changed from: private */
    Float clamp(Float f, Float f2, Float f3) {
        return Math.min(Math.max(f, f2), f3)
    }

    /* access modifiers changed from: private */
    Int clamp(Int i, Int i2, Int i3) {
        return Math.min(Math.max(i, i2), i3)
    }

    private Unit ensureViewDragHelper(ViewGroup viewGroup) {
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = this.mSensitivitySet ? ViewDragHelper.create(viewGroup, this.mSensitivity, this.mDragCallback) : ViewDragHelper.create(viewGroup, this.mDragCallback)
        }
    }

    Float fraction(Float f, Float f2, Float f3) {
        return (f3 - f) / (f2 - f)
    }

    Boolean canSwipeDismissView(@NonNull View view) {
        return true
    }

    Int getDragState() {
        if (this.mViewDragHelper != null) {
            return this.mViewDragHelper.getViewDragState()
        }
        return 0
    }

    Boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case 1:
            case 3:
                if (this.mIgnoreEvents) {
                    this.mIgnoreEvents = false
                    return false
                }
                break
            default:
                this.mIgnoreEvents = !coordinatorLayout.isPointInChildBounds(v, (Int) motionEvent.getX(), (Int) motionEvent.getY())
                break
        }
        if (this.mIgnoreEvents) {
            return false
        }
        ensureViewDragHelper(coordinatorLayout)
        return this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent)
    }

    Boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        if (this.mViewDragHelper == null) {
            return false
        }
        this.mViewDragHelper.processTouchEvent(motionEvent)
        return true
    }

    Unit setDragDismissDistance(Float f) {
        this.mDragDismissThreshold = clamp(0.0f, f, 1.0f)
    }

    Unit setEndAlphaSwipeDistance(Float f) {
        this.mAlphaEndSwipeDistance = clamp(0.0f, f, 1.0f)
    }

    Unit setListener(OnDismissListener onDismissListener) {
        this.mListener = onDismissListener
    }

    Unit setSensitivity(Float f) {
        this.mSensitivity = f
        this.mSensitivitySet = true
    }

    Unit setStartAlphaSwipeDistance(Float f) {
        this.mAlphaStartSwipeDistance = clamp(0.0f, f, 1.0f)
    }

    Unit setSwipeDirection(Int i) {
        this.mSwipeDirection = i
    }
}
