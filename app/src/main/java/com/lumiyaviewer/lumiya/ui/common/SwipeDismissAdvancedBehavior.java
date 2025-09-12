package com.lumiyaviewer.lumiya.ui.common;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SwipeDismissAdvancedBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
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
    /* access modifiers changed from: private */
    public float mAlphaEndSwipeDistance = 1.0f;
    /* access modifiers changed from: private */
    public float mAlphaStartSwipeDistance = 0.0f;
    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        private int mOriginalCapturedViewLeft;
        private int mOriginalCapturedViewTop;

        private boolean shouldDismiss(View view, float f, float f2) {
            float scaledMinimumFlingVelocity = (float) ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
            if (f < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                return true;
            }
            if (f > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                return true;
            }
            if (f2 < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0) {
                return true;
            }
            if (f2 > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                return true;
            }
            int left = view.getLeft() - this.mOriginalCapturedViewLeft;
            int round = Math.round(((float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
            if (left < (-round) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                return true;
            }
            if (left > round && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                return true;
            }
            int top = view.getTop() - this.mOriginalCapturedViewTop;
            int round2 = Math.round(((float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
            if (top >= (-round2) || (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) == 0) {
                return top > round2 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0;
            }
            return true;
        }

        public int clampViewPositionHorizontal(View view, int i, int i2) {
            int i3 = 0;
            if (view.getTop() != this.mOriginalCapturedViewTop) {
                return this.mOriginalCapturedViewLeft;
            }
            int width = this.mOriginalCapturedViewLeft - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0 ? view.getWidth() : 0);
            int i4 = this.mOriginalCapturedViewLeft;
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                i3 = view.getWidth();
            }
            return SwipeDismissAdvancedBehavior.clamp(width, i, i3 + i4);
        }

        public int clampViewPositionVertical(View view, int i, int i2) {
            int i3 = 0;
            if (view.getLeft() != this.mOriginalCapturedViewLeft) {
                return this.mOriginalCapturedViewTop;
            }
            int height = this.mOriginalCapturedViewTop - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0 ? view.getHeight() : 0);
            int i4 = this.mOriginalCapturedViewTop;
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                i3 = view.getHeight();
            }
            return SwipeDismissAdvancedBehavior.clamp(height, i, i3 + i4);
        }

        public int getViewHorizontalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0) {
                return view.getWidth();
            }
            return 0;
        }

        public int getViewVerticalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0) {
                return view.getWidth();
            }
            return 0;
        }

        public void onViewCaptured(View view, int i) {
            this.mOriginalCapturedViewLeft = view.getLeft();
            this.mOriginalCapturedViewTop = view.getTop();
        }

        public void onViewDragStateChanged(int i) {
            if (SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDragStateChanged(i);
            }
        }

        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            int i5 = 0;
            int abs = (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0 ? Math.abs(i - this.mOriginalCapturedViewLeft) : 0;
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0) {
                i5 = Math.abs(i2 - this.mOriginalCapturedViewTop);
            }
            if (abs == 0 && i5 == 0) {
                ViewCompat.setAlpha(view, 1.0f);
            } else {
                ViewCompat.setAlpha(view, 1.0f - Math.max(SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(((float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, ((float) view.getWidth()) * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (float) abs), 1.0f), SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(((float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, ((float) view.getHeight()) * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, (float) i5), 1.0f)));
            }
        }

        public void onViewReleased(View view, float f, float f2) {
            int i;
            int i2;
            boolean z;
            int width = view.getWidth();
            int height = view.getHeight();
            int left = view.getLeft();
            int top = view.getTop();
            if (shouldDismiss(view, f, f2)) {
                float scaledMinimumFlingVelocity = (float) ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
                if (f < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                    left = this.mOriginalCapturedViewLeft - width;
                } else if (f > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                    left = this.mOriginalCapturedViewLeft + width;
                } else if (f2 < (-scaledMinimumFlingVelocity) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0) {
                    top = this.mOriginalCapturedViewTop - height;
                } else if (f2 > scaledMinimumFlingVelocity && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0) {
                    top = this.mOriginalCapturedViewTop + height;
                }
                i = left;
                i2 = top;
                z = true;
            } else {
                i = this.mOriginalCapturedViewLeft;
                i2 = this.mOriginalCapturedViewTop;
                z = false;
            }
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper.settleCapturedViewAt(i, i2)) {
                ViewCompat.postOnAnimation(view, new SettleRunnable(view, z));
            } else if (z && SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(view);
            }
        }

        public boolean tryCaptureView(View view, int i) {
            return SwipeDismissAdvancedBehavior.this.canSwipeDismissView(view);
        }
    };
    /* access modifiers changed from: private */
    public float mDragDismissThreshold = 1.0f;
    private boolean mIgnoreEvents;
    /* access modifiers changed from: private */
    public OnDismissListener mListener;
    private float mSensitivity = 0.0f;
    private boolean mSensitivitySet;
    /* access modifiers changed from: private */
    public int mSwipeDirection = 15;
    /* access modifiers changed from: private */
    public ViewDragHelper mViewDragHelper;

    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    private class SettleRunnable implements Runnable {
        private final boolean mDismiss;
        private final View mView;

        SettleRunnable(View view, boolean z) {
            this.mView = view;
            this.mDismiss = z;
        }

        public void run() {
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper != null && SwipeDismissAdvancedBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            } else if (this.mDismiss && SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface SwipeDirection {
    }

    /* access modifiers changed from: private */
    public static float clamp(float f, float f2, float f3) {
        return Math.min(Math.max(f, f2), f3);
    }

    /* access modifiers changed from: private */
    public static int clamp(int i, int i2, int i3) {
        return Math.min(Math.max(i, i2), i3);
    }

    private void ensureViewDragHelper(ViewGroup viewGroup) {
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = this.mSensitivitySet ? ViewDragHelper.create(viewGroup, this.mSensitivity, this.mDragCallback) : ViewDragHelper.create(viewGroup, this.mDragCallback);
        }
    }

    static float fraction(float f, float f2, float f3) {
        return (f3 - f) / (f2 - f);
    }

    public boolean canSwipeDismissView(@NonNull View view) {
        return true;
    }

    public int getDragState() {
        if (this.mViewDragHelper != null) {
            return this.mViewDragHelper.getViewDragState();
        }
        return 0;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case 1:
            case 3:
                if (this.mIgnoreEvents) {
                    this.mIgnoreEvents = false;
                    return false;
                }
                break;
            default:
                this.mIgnoreEvents = !coordinatorLayout.isPointInChildBounds(v, (int) motionEvent.getX(), (int) motionEvent.getY());
                break;
        }
        if (this.mIgnoreEvents) {
            return false;
        }
        ensureViewDragHelper(coordinatorLayout);
        return this.mViewDragHelper.shouldInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        if (this.mViewDragHelper == null) {
            return false;
        }
        this.mViewDragHelper.processTouchEvent(motionEvent);
        return true;
    }

    public void setDragDismissDistance(float f) {
        this.mDragDismissThreshold = clamp(0.0f, f, 1.0f);
    }

    public void setEndAlphaSwipeDistance(float f) {
        this.mAlphaEndSwipeDistance = clamp(0.0f, f, 1.0f);
    }

    public void setListener(OnDismissListener onDismissListener) {
        this.mListener = onDismissListener;
    }

    public void setSensitivity(float f) {
        this.mSensitivity = f;
        this.mSensitivitySet = true;
    }

    public void setStartAlphaSwipeDistance(float f) {
        this.mAlphaStartSwipeDistance = clamp(0.0f, f, 1.0f);
    }

    public void setSwipeDirection(int i) {
        this.mSwipeDirection = i;
    }
}
