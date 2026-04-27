package com.lumiyaviewer.lumiya.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import java.util.ArrayList;
import java.util.List;

public class SwipeDismissListViewTouchListener implements View.OnTouchListener {
    private long mAnimationTime;
    private DismissCallbacks mCallbacks;
    private int mDismissAnimationRefCount = 0;
    private int mDownPosition;
    private View mDownView;
    private float mDownX;
    private float mDownY;
    private ListView mListView;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private boolean mPaused;
    private List<PendingDismissData> mPendingDismisses = new ArrayList();
    private int mSlop;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mViewWidth = 1;

    public interface DismissCallbacks {
        boolean canDismiss(ListView listView, int i);

        void onDismiss(ListView listView, int i);
    }

    class PendingDismissData implements Comparable<PendingDismissData> {
        public int position;
        public View view;

        public PendingDismissData(int i, View view2) {
            this.position = i;
            this.view = view2;
        }

        public int compareTo(PendingDismissData pendingDismissData) {
            return pendingDismissData.position - this.position;
        }
    }

    public SwipeDismissListViewTouchListener(ListView listView, DismissCallbacks dismissCallbacks) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(listView.getContext());
        this.mSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity() * 16;
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mAnimationTime = (long) listView.getContext().getResources().getInteger(17694720);
        this.mListView = listView;
        this.mCallbacks = dismissCallbacks;
    }

    /* access modifiers changed from: private */
    public void performDismiss(View view, int i) {
        this.mCallbacks.onDismiss(this.mListView, i);
    }

    public static void restoreViewState(View view) {
        if (Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            return;
        }
        ViewHelper.setTranslationX(view, 0.0f);
        ViewHelper.setAlpha(view, 1.0f);
    }

    public AbsListView.OnScrollListener makeScrollListener() {
        return new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            public void onScrollStateChanged(AbsListView absListView, int i) {
                boolean z = true;
                SwipeDismissListViewTouchListener swipeDismissListViewTouchListener = SwipeDismissListViewTouchListener.this;
                if (i == 1) {
                    z = false;
                }
                swipeDismissListViewTouchListener.setEnabled(z);
            }
        };
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z2 = true;
        if (this.mViewWidth < 2) {
            this.mViewWidth = this.mListView.getWidth();
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                if (this.mPaused) {
                    return false;
                }
                Rect rect = new Rect();
                int childCount = this.mListView.getChildCount();
                int[] iArr = new int[2];
                this.mListView.getLocationOnScreen(iArr);
                int rawX = ((int) motionEvent.getRawX()) - iArr[0];
                int rawY = ((int) motionEvent.getRawY()) - iArr[1];
                int i = 0;
                while (true) {
                    if (i < childCount) {
                        View childAt = this.mListView.getChildAt(i);
                        childAt.getHitRect(rect);
                        if (rect.contains(rawX, rawY)) {
                            this.mDownView = childAt;
                        } else {
                            i++;
                        }
                    }
                }
                if (this.mDownView != null) {
                    this.mDownX = motionEvent.getRawX();
                    this.mDownY = motionEvent.getRawY();
                    this.mDownPosition = this.mListView.getPositionForView(this.mDownView);
                    if (this.mCallbacks.canDismiss(this.mListView, this.mDownPosition)) {
                        this.mVelocityTracker = VelocityTracker.obtain();
                        this.mVelocityTracker.addMovement(motionEvent);
                    } else {
                        this.mDownView = null;
                    }
                }
                return false;
            case 1:
                if (this.mVelocityTracker != null) {
                    float rawX2 = motionEvent.getRawX() - this.mDownX;
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    float xVelocity = this.mVelocityTracker.getXVelocity();
                    float abs = Math.abs(xVelocity);
                    float abs2 = Math.abs(this.mVelocityTracker.getYVelocity());
                    if (Math.abs(rawX2) <= ((float) (this.mViewWidth / 2)) || !this.mSwiping) {
                        if (((float) this.mMinFlingVelocity) > abs || abs > ((float) this.mMaxFlingVelocity) || abs2 >= abs) {
                            z2 = false;
                            z = false;
                        } else if (this.mSwiping) {
                            z = ((xVelocity > 0.0f ? 1 : (xVelocity == 0.0f ? 0 : -1)) < 0) == ((rawX2 > 0.0f ? 1 : (rawX2 == 0.0f ? 0 : -1)) < 0);
                            if (this.mVelocityTracker.getXVelocity() <= 0.0f) {
                                z2 = false;
                            }
                        } else {
                            z2 = false;
                            z = false;
                        }
                    } else if (rawX2 > 0.0f) {
                        z = true;
                    } else {
                        z = true;
                        z2 = false;
                    }
                    if (z && this.mDownPosition != -1) {
                        final View view2 = this.mDownView;
                        final int i2 = this.mDownPosition;
                        this.mDismissAnimationRefCount++;
                        if (Build.VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX((float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    SwipeDismissListViewTouchListener.this.performDismiss(view2, i2);
                                }
                        } else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX((float) (z2 ? this.mViewWidth : -this.mViewWidth)).alpha(0.0f).setDuration(this.mAnimationTime).setListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                                public void onAnimationEnd(com.nineoldandroids.animation.Animator animator) {
                                    SwipeDismissListViewTouchListener.this.performDismiss(view2, i2);
                                }
                        }
                    } else if (Build.VERSION.SDK_INT >= 12) {
                        this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null);
                    } else {
                        ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null);
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
            case 2:
                if (this.mVelocityTracker != null && !this.mPaused) {
                    this.mVelocityTracker.addMovement(motionEvent);
                    float rawX3 = motionEvent.getRawX() - this.mDownX;
                    float rawY2 = motionEvent.getRawY() - this.mDownY;
                    if (Math.abs(rawX3) > ((float) this.mSlop) && Math.abs(rawY2) < Math.abs(rawX3) / 2.0f) {
                        this.mSwiping = true;
                        this.mSwipingSlop = rawX3 > 0.0f ? this.mSlop : -this.mSlop;
                        this.mListView.requestDisallowInterceptTouchEvent(true);
                        MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        obtain.setAction((motionEvent.getActionIndex() << 8) | 3);
                        this.mListView.onTouchEvent(obtain);
                        obtain.recycle();
                    }
                    if (this.mSwiping) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            this.mDownView.setTranslationX(rawX3 - ((float) this.mSwipingSlop));
                            this.mDownView.setAlpha(Math.max(0.0f, Math.min(1.0f, 1.0f - ((Math.abs(rawX3) * 2.0f) / ((float) this.mViewWidth)))));
                        } else {
                            ViewHelper.setTranslationX(this.mDownView, rawX3 - ((float) this.mSwipingSlop));
                            ViewHelper.setAlpha(this.mDownView, Math.min(1.0f, 1.0f - ((Math.abs(rawX3) * 2.0f) / ((float) this.mViewWidth))));
                        }
                        return true;
                    }
                }
                break;
            case 3:
                if (this.mVelocityTracker != null) {
                    if (this.mDownView != null && this.mSwiping) {
                        if (Build.VERSION.SDK_INT >= 12) {
                            this.mDownView.animate().translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null);
                        } else {
                            ViewPropertyAnimator.animate(this.mDownView).translationX(0.0f).alpha(1.0f).setDuration(this.mAnimationTime).setListener((Animator.AnimatorListener) null);
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
        return false;
    }

    public void setEnabled(boolean z) {
        this.mPaused = !z;
    }
}
