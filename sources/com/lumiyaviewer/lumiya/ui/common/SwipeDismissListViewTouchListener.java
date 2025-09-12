// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.ListView;
import com.nineoldandroids.view.ViewHelper;
import java.util.ArrayList;
import java.util.List;

public class SwipeDismissListViewTouchListener
    implements android.view.View.OnTouchListener
{
    public static interface DismissCallbacks
    {

        public abstract boolean canDismiss(ListView listview, int i);

        public abstract void onDismiss(ListView listview, int i);
    }

    class PendingDismissData
        implements Comparable
    {

        public int position;
        final SwipeDismissListViewTouchListener this$0;
        public View view;

        public int compareTo(PendingDismissData pendingdismissdata)
        {
            return pendingdismissdata.position - position;
        }

        public volatile int compareTo(Object obj)
        {
            return compareTo((PendingDismissData)obj);
        }

        public PendingDismissData(int i, View view1)
        {
            this$0 = SwipeDismissListViewTouchListener.this;
            super();
            position = i;
            view = view1;
        }
    }


    private long mAnimationTime;
    private DismissCallbacks mCallbacks;
    private int mDismissAnimationRefCount;
    private int mDownPosition;
    private View mDownView;
    private float mDownX;
    private float mDownY;
    private ListView mListView;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private boolean mPaused;
    private List mPendingDismisses;
    private int mSlop;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private int mViewWidth;

    static void _2D_wrap0(SwipeDismissListViewTouchListener swipedismisslistviewtouchlistener, View view, int i)
    {
        swipedismisslistviewtouchlistener.performDismiss(view, i);
    }

    public SwipeDismissListViewTouchListener(ListView listview, DismissCallbacks dismisscallbacks)
    {
        mViewWidth = 1;
        mPendingDismisses = new ArrayList();
        mDismissAnimationRefCount = 0;
        ViewConfiguration viewconfiguration = ViewConfiguration.get(listview.getContext());
        mSlop = viewconfiguration.getScaledTouchSlop();
        mMinFlingVelocity = viewconfiguration.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        mAnimationTime = listview.getContext().getResources().getInteger(0x10e0000);
        mListView = listview;
        mCallbacks = dismisscallbacks;
    }

    private void performDismiss(View view, int i)
    {
        mCallbacks.onDismiss(mListView, i);
    }

    public static void restoreViewState(View view)
    {
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            view.setAlpha(1.0F);
            view.setTranslationX(0.0F);
            return;
        } else
        {
            ViewHelper.setTranslationX(view, 0.0F);
            ViewHelper.setAlpha(view, 1.0F);
            return;
        }
    }

    public android.widget.AbsListView.OnScrollListener makeScrollListener()
    {
        return new android.widget.AbsListView.OnScrollListener() {

            final SwipeDismissListViewTouchListener this$0;

            public void onScroll(AbsListView abslistview, int i, int j, int k)
            {
            }

            public void onScrollStateChanged(AbsListView abslistview, int i)
            {
                boolean flag = true;
                abslistview = SwipeDismissListViewTouchListener.this;
                if (i == 1)
                {
                    flag = false;
                }
                abslistview.setEnabled(flag);
            }

            
            {
                this$0 = SwipeDismissListViewTouchListener.this;
                super();
            }
        };
    }

    public boolean onTouch(final View downView, MotionEvent motionevent)
    {
        int j1;
        j1 = 1;
        if (mViewWidth < 2)
        {
            mViewWidth = mListView.getWidth();
        }
        motionevent.getActionMasked();
        JVM INSTR tableswitch 0 3: default 56
    //                   0 58
    //                   1 390
    //                   2 896
    //                   3 265;
           goto _L1 _L2 _L3 _L4 _L5
_L1:
        return false;
_L2:
        if (mPaused)
        {
            return false;
        }
        downView = new Rect();
        int k = mListView.getChildCount();
        int ai[] = new int[2];
        mListView.getLocationOnScreen(ai);
        int i1 = (int)motionevent.getRawX();
        j1 = ai[0];
        int k1 = (int)motionevent.getRawY();
        int l1 = ai[1];
        int i = 0;
label0:
        do
        {
label1:
            {
                if (i < k)
                {
                    View view = mListView.getChildAt(i);
                    view.getHitRect(downView);
                    if (!downView.contains(i1 - j1, k1 - l1))
                    {
                        break label1;
                    }
                    mDownView = view;
                }
                if (mDownView != null)
                {
                    mDownX = motionevent.getRawX();
                    mDownY = motionevent.getRawY();
                    mDownPosition = mListView.getPositionForView(mDownView);
                    if (!mCallbacks.canDismiss(mListView, mDownPosition))
                    {
                        break label0;
                    }
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionevent);
                }
                return false;
            }
            i++;
        } while (true);
        mDownView = null;
        return false;
_L5:
        if (mVelocityTracker != null)
        {
            if (mDownView != null && mSwiping)
            {
                if (android.os.Build.VERSION.SDK_INT >= 12)
                {
                    mDownView.animate().translationX(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
                } else
                {
                    com.nineoldandroids.view.ViewPropertyAnimator.animate(mDownView).translationX(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
                }
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mDownX = 0.0F;
            mDownY = 0.0F;
            mDownView = null;
            mDownPosition = -1;
            mSwiping = false;
            return false;
        }
        continue; /* Loop/switch isn't completed */
_L3:
        if (mVelocityTracker != null)
        {
            float f = motionevent.getRawX() - mDownX;
            mVelocityTracker.addMovement(motionevent);
            mVelocityTracker.computeCurrentVelocity(1000);
            float f1 = mVelocityTracker.getXVelocity();
            float f2 = Math.abs(f1);
            float f3 = Math.abs(mVelocityTracker.getYVelocity());
            int j;
            int l;
            if (Math.abs(f) > (float)(mViewWidth / 2) && mSwiping)
            {
                if (f > 0.0F)
                {
                    j = 1;
                    l = j1;
                } else
                {
                    j = 1;
                    l = 0;
                }
            } else
            if ((float)mMinFlingVelocity <= f2 && f2 <= (float)mMaxFlingVelocity && f3 < f2)
            {
                if (mSwiping)
                {
                    if (f1 < 0.0F)
                    {
                        j = 1;
                    } else
                    {
                        j = 0;
                    }
                    if (f < 0.0F)
                    {
                        l = 1;
                    } else
                    {
                        l = 0;
                    }
                    if (j == l)
                    {
                        downPosition = 1;
                    } else
                    {
                        downPosition = 0;
                    }
                    j = downPosition;
                    l = j1;
                    if (mVelocityTracker.getXVelocity() <= 0.0F)
                    {
                        l = 0;
                        j = downPosition;
                    }
                } else
                {
                    l = 0;
                    j = 0;
                }
            } else
            {
                l = 0;
                j = 0;
            }
            if (j != 0 && mDownPosition != -1)
            {
                downView = mDownView;
                final int downPosition = mDownPosition;
                mDismissAnimationRefCount = mDismissAnimationRefCount + 1;
                if (android.os.Build.VERSION.SDK_INT >= 12)
                {
                    motionevent = mDownView.animate();
                    if (l != 0)
                    {
                        j = mViewWidth;
                    } else
                    {
                        j = -mViewWidth;
                    }
                    motionevent.translationX(j).alpha(0.0F).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {

                        final SwipeDismissListViewTouchListener this$0;
                        final int val$downPosition;
                        final View val$downView;

                        public void onAnimationEnd(Animator animator)
                        {
                            SwipeDismissListViewTouchListener._2D_wrap0(SwipeDismissListViewTouchListener.this, downView, downPosition);
                        }

            
            {
                this$0 = SwipeDismissListViewTouchListener.this;
                downView = view;
                downPosition = i;
                super();
            }
                    });
                } else
                {
                    motionevent = com.nineoldandroids.view.ViewPropertyAnimator.animate(mDownView);
                    if (l != 0)
                    {
                        j = mViewWidth;
                    } else
                    {
                        j = -mViewWidth;
                    }
                    motionevent.translationX(j).alpha(0.0F).setDuration(mAnimationTime).setListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {

                        final SwipeDismissListViewTouchListener this$0;
                        final int val$downPosition;
                        final View val$downView;

                        public void onAnimationEnd(com.nineoldandroids.animation.Animator animator)
                        {
                            SwipeDismissListViewTouchListener._2D_wrap0(SwipeDismissListViewTouchListener.this, downView, downPosition);
                        }

            
            {
                this$0 = SwipeDismissListViewTouchListener.this;
                downView = view;
                downPosition = i;
                super();
            }
                    });
                }
            } else
            if (android.os.Build.VERSION.SDK_INT >= 12)
            {
                mDownView.animate().translationX(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
            } else
            {
                com.nineoldandroids.view.ViewPropertyAnimator.animate(mDownView).translationX(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mDownX = 0.0F;
            mDownY = 0.0F;
            mDownView = null;
            mDownPosition = -1;
            mSwiping = false;
            return false;
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (mVelocityTracker != null && !mPaused)
        {
            mVelocityTracker.addMovement(motionevent);
            f = motionevent.getRawX() - mDownX;
            f1 = motionevent.getRawY();
            f2 = mDownY;
            if (Math.abs(f) > (float)mSlop && Math.abs(f1 - f2) < Math.abs(f) / 2.0F)
            {
                mSwiping = true;
                if (f > 0.0F)
                {
                    j = mSlop;
                } else
                {
                    j = -mSlop;
                }
                mSwipingSlop = j;
                mListView.requestDisallowInterceptTouchEvent(true);
                downView = MotionEvent.obtain(motionevent);
                downView.setAction(motionevent.getActionIndex() << 8 | 3);
                mListView.onTouchEvent(downView);
                downView.recycle();
            }
            if (mSwiping)
            {
                if (android.os.Build.VERSION.SDK_INT >= 11)
                {
                    mDownView.setTranslationX(f - (float)mSwipingSlop);
                    mDownView.setAlpha(Math.max(0.0F, Math.min(1.0F, 1.0F - (Math.abs(f) * 2.0F) / (float)mViewWidth)));
                    return true;
                } else
                {
                    ViewHelper.setTranslationX(mDownView, f - (float)mSwipingSlop);
                    ViewHelper.setAlpha(mDownView, Math.min(1.0F, 1.0F - (Math.abs(f) * 2.0F) / (float)mViewWidth));
                    return true;
                }
            }
        }
        if (true) goto _L1; else goto _L6
_L6:
    }

    public void setEnabled(boolean flag)
    {
        mPaused = flag ^ true;
    }
}
