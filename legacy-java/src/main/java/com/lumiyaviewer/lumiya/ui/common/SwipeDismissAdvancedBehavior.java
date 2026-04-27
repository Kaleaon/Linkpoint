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

/* loaded from: classes.dex */
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
    private boolean mIgnoreEvents;
    private OnDismissListener mListener;
    private boolean mSensitivitySet;
    private ViewDragHelper mViewDragHelper;
    private float mSensitivity = 0.0f;
    private int mSwipeDirection = 15;
    private float mDragDismissThreshold = 1.0f;
    private float mAlphaStartSwipeDistance = 0.0f;
    private float mAlphaEndSwipeDistance = 1.0f;
    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() { // from class: com.lumiyaviewer.lumiya.ui.common.SwipeDismissAdvancedBehavior.1
        private int mOriginalCapturedViewLeft;
        private int mOriginalCapturedViewTop;

        private boolean shouldDismiss(View view, float f, float f2) {
            float scaledMinimumFlingVelocity = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
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
            int round = Math.round(view.getWidth() * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
            if (left < (-round) && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0) {
                return true;
            }
            if (left > round && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0) {
                return true;
            }
            int top = view.getTop() - this.mOriginalCapturedViewTop;
            int round2 = Math.round(view.getHeight() * SwipeDismissAdvancedBehavior.this.mDragDismissThreshold);
            if (top >= (-round2) || (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) == 0) {
                return top > round2 && (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0;
            }
            return true;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionHorizontal(View view, int i, int i2) {
            if (view.getTop() != this.mOriginalCapturedViewTop) {
                return this.mOriginalCapturedViewLeft;
            }
            return SwipeDismissAdvancedBehavior.clamp(this.mOriginalCapturedViewLeft - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 1) != 0 ? view.getWidth() : 0), i, ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 2) != 0 ? view.getWidth() : 0) + this.mOriginalCapturedViewLeft);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int clampViewPositionVertical(View view, int i, int i2) {
            if (view.getLeft() != this.mOriginalCapturedViewLeft) {
                return this.mOriginalCapturedViewTop;
            }
            return SwipeDismissAdvancedBehavior.clamp(this.mOriginalCapturedViewTop - ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 4) != 0 ? view.getHeight() : 0), i, ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 8) != 0 ? view.getHeight() : 0) + this.mOriginalCapturedViewTop);
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int getViewHorizontalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0) {
                return view.getWidth();
            }
            return 0;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public int getViewVerticalDragRange(View view) {
            if ((SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0) {
                return view.getWidth();
            }
            return 0;
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewCaptured(View view, int i) {
            this.mOriginalCapturedViewLeft = view.getLeft();
            this.mOriginalCapturedViewTop = view.getTop();
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewDragStateChanged(int i) {
            if (SwipeDismissAdvancedBehavior.this.mListener != null) {
                SwipeDismissAdvancedBehavior.this.mListener.onDragStateChanged(i);
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            int abs = (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 3) != 0 ? Math.abs(i - this.mOriginalCapturedViewLeft) : 0;
            int abs2 = (SwipeDismissAdvancedBehavior.this.mSwipeDirection & 12) != 0 ? Math.abs(i2 - this.mOriginalCapturedViewTop) : 0;
            if (abs == 0 && abs2 == 0) {
                ViewCompat.setAlpha(view, 1.0f);
            } else {
                ViewCompat.setAlpha(view, 1.0f - Math.max(SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(view.getWidth() * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, view.getWidth() * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, abs), 1.0f), SwipeDismissAdvancedBehavior.clamp(0.0f, SwipeDismissAdvancedBehavior.fraction(view.getHeight() * SwipeDismissAdvancedBehavior.this.mAlphaStartSwipeDistance, view.getHeight() * SwipeDismissAdvancedBehavior.this.mAlphaEndSwipeDistance, abs2), 1.0f)));
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public void onViewReleased(View view, float f, float f2) {
            int i;
            int i2;
            boolean z;
            int width = view.getWidth();
            int height = view.getHeight();
            int left = view.getLeft();
            int top = view.getTop();
            if (shouldDismiss(view, f, f2)) {
                float scaledMinimumFlingVelocity = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
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
            } else {
                if (!z || SwipeDismissAdvancedBehavior.this.mListener == null) {
                    return;
                }
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(view);
            }
        }

        @Override // android.support.v4.widget.ViewDragHelper.Callback
        public boolean tryCaptureView(View view, int i) {
            return SwipeDismissAdvancedBehavior.this.canSwipeDismissView(view);
        }
    };

    /* loaded from: classes.dex */
    public interface OnDismissListener {
        void onDismiss(View view);

        void onDragStateChanged(int i);
    }

    /* loaded from: classes.dex */
    private class SettleRunnable implements Runnable {
        private final boolean mDismiss;
        private final View mView;

        SettleRunnable(View view, boolean z) {
            this.mView = view;
            this.mDismiss = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (SwipeDismissAdvancedBehavior.this.mViewDragHelper != null && SwipeDismissAdvancedBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.mView, this);
            } else {
                if (!this.mDismiss || SwipeDismissAdvancedBehavior.this.mListener == null) {
                    return;
                }
                SwipeDismissAdvancedBehavior.this.mListener.onDismiss(this.mView);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface SwipeDirection {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float clamp(float f, float f2, float f3) {
        return Math.min(Math.max(f, f2), f3);
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        switch (MotionEventCompat.getActionMasked(motionEvent)) {
            case 1:
            case 3:
                if (this.mIgnoreEvents) {
                    this.mIgnoreEvents = false;
                    return false;
                }
                break;
            case 2:
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

    @Override // android.support.design.widget.CoordinatorLayout.Behavior
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
