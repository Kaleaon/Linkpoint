// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.annotation.Annotation;

public class SwipeDismissAdvancedBehavior extends android.support.design.widget.CoordinatorLayout.Behavior
{
    public static interface OnDismissListener
    {

        public abstract void onDismiss(View view);

        public abstract void onDragStateChanged(int i);
    }

    private class SettleRunnable
        implements Runnable
    {

        private final boolean mDismiss;
        private final View mView;
        final SwipeDismissAdvancedBehavior this$0;

        public void run()
        {
            if (SwipeDismissAdvancedBehavior._2D_get5(SwipeDismissAdvancedBehavior.this) != null && SwipeDismissAdvancedBehavior._2D_get5(SwipeDismissAdvancedBehavior.this).continueSettling(true))
            {
                ViewCompat.postOnAnimation(mView, this);
            } else
            if (mDismiss && SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this) != null)
            {
                SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this).onDismiss(mView);
                return;
            }
        }

        SettleRunnable(View view, boolean flag)
        {
            this$0 = SwipeDismissAdvancedBehavior.this;
            super();
            mView = view;
            mDismiss = flag;
        }
    }

    private static interface SwipeDirection
        extends Annotation
    {
    }


    private static final float DEFAULT_ALPHA_END_DISTANCE = 1F;
    private static final float DEFAULT_ALPHA_START_DISTANCE = 0F;
    private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 1F;
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
    private float mAlphaEndSwipeDistance;
    private float mAlphaStartSwipeDistance;
    private final android.support.v4.widget.ViewDragHelper.Callback mDragCallback = new android.support.v4.widget.ViewDragHelper.Callback() {

        private int mOriginalCapturedViewLeft;
        private int mOriginalCapturedViewTop;
        final SwipeDismissAdvancedBehavior this$0;

        private boolean shouldDismiss(View view, float f, float f1)
        {
            float f2 = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
            if (f < -f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 1) != 0)
            {
                return true;
            }
            if (f > f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 2) != 0)
            {
                return true;
            }
            if (f1 < -f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 4) != 0)
            {
                return true;
            }
            if (f1 > f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 8) != 0)
            {
                return true;
            }
            int i = view.getLeft() - mOriginalCapturedViewLeft;
            int j = Math.round((float)view.getWidth() * SwipeDismissAdvancedBehavior._2D_get2(SwipeDismissAdvancedBehavior.this));
            if (i < -j && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 1) != 0)
            {
                return true;
            }
            if (i > j && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 2) != 0)
            {
                return true;
            }
            i = view.getTop() - mOriginalCapturedViewTop;
            j = Math.round((float)view.getHeight() * SwipeDismissAdvancedBehavior._2D_get2(SwipeDismissAdvancedBehavior.this));
            if (i < -j && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 4) != 0)
            {
                return true;
            }
            return i > j && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 8) != 0;
        }

        public int clampViewPositionHorizontal(View view, int i, int j)
        {
            int k = 0;
            if (view.getTop() == mOriginalCapturedViewTop)
            {
                int l = mOriginalCapturedViewLeft;
                int i1;
                if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 1) != 0)
                {
                    j = view.getWidth();
                } else
                {
                    j = 0;
                }
                i1 = mOriginalCapturedViewLeft;
                if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 2) != 0)
                {
                    k = view.getWidth();
                }
                return SwipeDismissAdvancedBehavior._2D_wrap1(l - j, i, k + i1);
            } else
            {
                return mOriginalCapturedViewLeft;
            }
        }

        public int clampViewPositionVertical(View view, int i, int j)
        {
            int k = 0;
            if (view.getLeft() == mOriginalCapturedViewLeft)
            {
                int l = mOriginalCapturedViewTop;
                int i1;
                if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 4) != 0)
                {
                    j = view.getHeight();
                } else
                {
                    j = 0;
                }
                i1 = mOriginalCapturedViewTop;
                if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 8) != 0)
                {
                    k = view.getHeight();
                }
                return SwipeDismissAdvancedBehavior._2D_wrap1(l - j, i, k + i1);
            } else
            {
                return mOriginalCapturedViewTop;
            }
        }

        public int getViewHorizontalDragRange(View view)
        {
            if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 3) != 0)
            {
                return view.getWidth();
            } else
            {
                return 0;
            }
        }

        public int getViewVerticalDragRange(View view)
        {
            if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 0xc) != 0)
            {
                return view.getWidth();
            } else
            {
                return 0;
            }
        }

        public void onViewCaptured(View view, int i)
        {
            mOriginalCapturedViewLeft = view.getLeft();
            mOriginalCapturedViewTop = view.getTop();
        }

        public void onViewDragStateChanged(int i)
        {
            if (SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this) != null)
            {
                SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this).onDragStateChanged(i);
            }
        }

        public void onViewPositionChanged(View view, int i, int j, int k, int l)
        {
            k = 0;
            if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 3) != 0)
            {
                i = Math.abs(i - mOriginalCapturedViewLeft);
            } else
            {
                i = 0;
            }
            if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 0xc) != 0)
            {
                k = Math.abs(j - mOriginalCapturedViewTop);
            }
            if (i == 0 && k == 0)
            {
                ViewCompat.setAlpha(view, 1.0F);
                return;
            } else
            {
                ViewCompat.setAlpha(view, 1.0F - Math.max(SwipeDismissAdvancedBehavior._2D_wrap0(0.0F, SwipeDismissAdvancedBehavior.fraction((float)view.getWidth() * SwipeDismissAdvancedBehavior._2D_get1(SwipeDismissAdvancedBehavior.this), (float)view.getWidth() * SwipeDismissAdvancedBehavior._2D_get0(SwipeDismissAdvancedBehavior.this), i), 1.0F), SwipeDismissAdvancedBehavior._2D_wrap0(0.0F, SwipeDismissAdvancedBehavior.fraction((float)view.getHeight() * SwipeDismissAdvancedBehavior._2D_get1(SwipeDismissAdvancedBehavior.this), (float)view.getHeight() * SwipeDismissAdvancedBehavior._2D_get0(SwipeDismissAdvancedBehavior.this), k), 1.0F)));
                return;
            }
        }

        public void onViewReleased(View view, float f, float f1)
        {
            int i;
            int l;
            int i1;
            int j1;
            i = view.getWidth();
            j1 = view.getHeight();
            l = view.getLeft();
            i1 = view.getTop();
            if (!shouldDismiss(view, f, f1)) goto _L2; else goto _L1
_L1:
            float f2 = ViewConfiguration.get(view.getContext()).getScaledMinimumFlingVelocity();
            if (f >= -f2 || (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 1) == 0) goto _L4; else goto _L3
_L3:
            int k;
            k = mOriginalCapturedViewLeft - i;
            i = i1;
_L11:
            boolean flag;
            flag = true;
            l = k;
            k = i;
_L7:
            if (!SwipeDismissAdvancedBehavior._2D_get5(SwipeDismissAdvancedBehavior.this).settleCapturedViewAt(l, k)) goto _L6; else goto _L5
_L5:
            ViewCompat.postOnAnimation(view, new SettleRunnable(view, flag));
_L9:
            return;
_L4:
            if (f > f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 2) != 0)
            {
                k = mOriginalCapturedViewLeft + i;
                i = i1;
            } else
            if (f1 < -f2 && (SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 4) != 0)
            {
                i = mOriginalCapturedViewTop - j1;
                k = l;
            } else
            {
                i = i1;
                k = l;
                if (f1 > f2)
                {
                    i = i1;
                    k = l;
                    if ((SwipeDismissAdvancedBehavior._2D_get4(SwipeDismissAdvancedBehavior.this) & 8) != 0)
                    {
                        i = mOriginalCapturedViewTop + j1;
                        k = l;
                    }
                }
            }
            continue; /* Loop/switch isn't completed */
_L2:
            int j = mOriginalCapturedViewLeft;
            k = mOriginalCapturedViewTop;
            flag = false;
            l = j;
              goto _L7
_L6:
            if (!flag || SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this) == null) goto _L9; else goto _L8
_L8:
            SwipeDismissAdvancedBehavior._2D_get3(SwipeDismissAdvancedBehavior.this).onDismiss(view);
            return;
            if (true) goto _L11; else goto _L10
_L10:
        }

        public boolean tryCaptureView(View view, int i)
        {
            return canSwipeDismissView(view);
        }

            
            {
                this$0 = SwipeDismissAdvancedBehavior.this;
                super();
            }
    };
    private float mDragDismissThreshold;
    private boolean mIgnoreEvents;
    private OnDismissListener mListener;
    private float mSensitivity;
    private boolean mSensitivitySet;
    private int mSwipeDirection;
    private ViewDragHelper mViewDragHelper;

    static float _2D_get0(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mAlphaEndSwipeDistance;
    }

    static float _2D_get1(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mAlphaStartSwipeDistance;
    }

    static float _2D_get2(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mDragDismissThreshold;
    }

    static OnDismissListener _2D_get3(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mListener;
    }

    static int _2D_get4(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mSwipeDirection;
    }

    static ViewDragHelper _2D_get5(SwipeDismissAdvancedBehavior swipedismissadvancedbehavior)
    {
        return swipedismissadvancedbehavior.mViewDragHelper;
    }

    static float _2D_wrap0(float f, float f1, float f2)
    {
        return clamp(f, f1, f2);
    }

    static int _2D_wrap1(int i, int j, int k)
    {
        return clamp(i, j, k);
    }

    public SwipeDismissAdvancedBehavior()
    {
        mSensitivity = 0.0F;
        mSwipeDirection = 15;
        mDragDismissThreshold = 1.0F;
        mAlphaStartSwipeDistance = 0.0F;
        mAlphaEndSwipeDistance = 1.0F;
    }

    private static float clamp(float f, float f1, float f2)
    {
        return Math.min(Math.max(f, f1), f2);
    }

    private static int clamp(int i, int j, int k)
    {
        return Math.min(Math.max(i, j), k);
    }

    private void ensureViewDragHelper(ViewGroup viewgroup)
    {
        if (mViewDragHelper == null)
        {
            if (mSensitivitySet)
            {
                viewgroup = ViewDragHelper.create(viewgroup, mSensitivity, mDragCallback);
            } else
            {
                viewgroup = ViewDragHelper.create(viewgroup, mDragCallback);
            }
            mViewDragHelper = viewgroup;
        }
    }

    static float fraction(float f, float f1, float f2)
    {
        return (f2 - f) / (f1 - f);
    }

    public boolean canSwipeDismissView(View view)
    {
        return true;
    }

    public int getDragState()
    {
        if (mViewDragHelper != null)
        {
            return mViewDragHelper.getViewDragState();
        } else
        {
            return 0;
        }
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorlayout, View view, MotionEvent motionevent)
    {
        switch (MotionEventCompat.getActionMasked(motionevent))
        {
        case 2: // '\002'
        default:
            mIgnoreEvents = coordinatorlayout.isPointInChildBounds(view, (int)motionevent.getX(), (int)motionevent.getY()) ^ true;
            break;

        case 1: // '\001'
        case 3: // '\003'
            break MISSING_BLOCK_LABEL_62;
        }
_L1:
        if (mIgnoreEvents)
        {
            return false;
        } else
        {
            ensureViewDragHelper(coordinatorlayout);
            return mViewDragHelper.shouldInterceptTouchEvent(motionevent);
        }
        if (mIgnoreEvents)
        {
            mIgnoreEvents = false;
            return false;
        }
          goto _L1
    }

    public boolean onTouchEvent(CoordinatorLayout coordinatorlayout, View view, MotionEvent motionevent)
    {
        if (mViewDragHelper != null)
        {
            mViewDragHelper.processTouchEvent(motionevent);
            return true;
        } else
        {
            return false;
        }
    }

    public void setDragDismissDistance(float f)
    {
        mDragDismissThreshold = clamp(0.0F, f, 1.0F);
    }

    public void setEndAlphaSwipeDistance(float f)
    {
        mAlphaEndSwipeDistance = clamp(0.0F, f, 1.0F);
    }

    public void setListener(OnDismissListener ondismisslistener)
    {
        mListener = ondismisslistener;
    }

    public void setSensitivity(float f)
    {
        mSensitivity = f;
        mSensitivitySet = true;
    }

    public void setStartAlphaSwipeDistance(float f)
    {
        mAlphaStartSwipeDistance = clamp(0.0F, f, 1.0F);
    }

    public void setSwipeDirection(int i)
    {
        mSwipeDirection = i;
    }
}
