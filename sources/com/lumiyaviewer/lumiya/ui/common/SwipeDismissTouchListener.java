// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import com.lumiyaviewer.lumiya.Debug;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            OnInterceptTouchEventListener

public class SwipeDismissTouchListener
    implements OnInterceptTouchEventListener
{
    public static interface DismissCallbacks
    {

        public abstract boolean canDismiss(Object obj);

        public abstract void onDismiss(View view, Object obj);
    }


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
    private int mViewHeight;
    private int mViewWidth;

    static DismissCallbacks _2D_get0(SwipeDismissTouchListener swipedismisstouchlistener)
    {
        return swipedismisstouchlistener.mCallbacks;
    }

    static Object _2D_get1(SwipeDismissTouchListener swipedismisstouchlistener)
    {
        return swipedismisstouchlistener.mToken;
    }

    static View _2D_get2(SwipeDismissTouchListener swipedismisstouchlistener)
    {
        return swipedismisstouchlistener.mView;
    }

    static void _2D_wrap0(SwipeDismissTouchListener swipedismisstouchlistener)
    {
        swipedismisstouchlistener.performDismiss();
    }

    public SwipeDismissTouchListener(View view, Object obj, DismissCallbacks dismisscallbacks, boolean flag, boolean flag1, boolean flag2, boolean flag3)
    {
        boolean flag4 = true;
        super();
        mViewWidth = 1;
        mViewHeight = 1;
        canSwipeUp = flag;
        canSwipeDown = flag1;
        canSwipeLeft = flag2;
        canSwipeRight = flag3;
        ViewConfiguration viewconfiguration;
        if (flag2)
        {
            flag3 = true;
        }
        canSwipeX = flag3;
        flag2 = flag4;
        if (!flag)
        {
            flag2 = flag1;
        }
        canSwipeY = flag2;
        viewconfiguration = ViewConfiguration.get(view.getContext());
        mSlop = viewconfiguration.getScaledTouchSlop();
        mMinFlingVelocity = viewconfiguration.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = viewconfiguration.getScaledMaximumFlingVelocity();
        mAnimationTime = view.getContext().getResources().getInteger(0x10e0000);
        mView = view;
        mToken = obj;
        mCallbacks = dismisscallbacks;
    }

    private void performDismiss()
    {
        final android.view.ViewGroup.LayoutParams lp = mView.getLayoutParams();
        final int originalHeight = mView.getHeight();
        ValueAnimator valueanimator = ValueAnimator.ofInt(new int[] {
            originalHeight, 1
        }).setDuration(mAnimationTime);
        valueanimator.addListener(new AnimatorListenerAdapter() {

            final SwipeDismissTouchListener this$0;
            final android.view.ViewGroup.LayoutParams val$lp;
            final int val$originalHeight;

            public void onAnimationEnd(Animator animator)
            {
                SwipeDismissTouchListener._2D_get0(SwipeDismissTouchListener.this).onDismiss(SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this), SwipeDismissTouchListener._2D_get1(SwipeDismissTouchListener.this));
                SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setAlpha(1.0F);
                SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setTranslationX(0.0F);
                SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setTranslationY(0.0F);
                lp.height = originalHeight;
                SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setLayoutParams(lp);
            }

            
            {
                this$0 = SwipeDismissTouchListener.this;
                lp = layoutparams;
                originalHeight = i;
                super();
            }
        });
        valueanimator.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {

            final SwipeDismissTouchListener this$0;
            final android.view.ViewGroup.LayoutParams val$lp;

            public void onAnimationUpdate(ValueAnimator valueanimator1)
            {
                lp.height = ((Integer)valueanimator1.getAnimatedValue()).intValue();
                SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setLayoutParams(lp);
            }

            
            {
                this$0 = SwipeDismissTouchListener.this;
                lp = layoutparams;
                super();
            }
        });
        valueanimator.start();
    }

    public boolean dispatchTouchEvent(MotionEvent motionevent)
    {
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        boolean flag;
        boolean flag2;
        boolean flag3;
        flag = true;
        flag2 = true;
        flag3 = true;
        motionevent.offsetLocation(mTranslationX, mTranslationY);
        if (mViewWidth < 2)
        {
            mViewWidth = mView.getWidth();
        }
        if (mViewHeight < 2)
        {
            mViewHeight = mView.getHeight();
        }
        motionevent.getActionMasked();
        JVM INSTR tableswitch 0 3: default 92
    //                   0 94
    //                   1 172
    //                   2 938
    //                   3 851;
           goto _L1 _L2 _L3 _L4 _L5
_L1:
        return false;
_L2:
        mDownX = motionevent.getRawX();
        mDownY = motionevent.getRawY();
        Debug.Printf("SwipeSwipe: action down x %f y %f", new Object[] {
            Float.valueOf(mDownX), Float.valueOf(mDownY)
        });
        if (mCallbacks.canDismiss(mToken))
        {
            mVelocityTracker = VelocityTracker.obtain();
            mVelocityTracker.addMovement(motionevent);
        }
        return true;
_L3:
        if (mVelocityTracker != null)
        {
            float f = motionevent.getRawX() - mDownX;
            float f1 = motionevent.getRawY() - mDownY;
            mVelocityTracker.addMovement(motionevent);
            mVelocityTracker.computeCurrentVelocity(1000);
            float f2 = mVelocityTracker.getXVelocity();
            float f3 = mVelocityTracker.getYVelocity();
            float f4 = Math.abs(f2);
            float f5 = Math.abs(f3);
            MotionEvent motionevent1;
            int k;
            boolean flag4;
            boolean flag5;
            boolean flag6;
            if (mSwiping && mSwipingX && canSwipeRight && f > (float)(mViewWidth / 2))
            {
                f1 = mViewWidth;
                f = 0.0F;
            } else
            if (mSwiping && mSwipingX && canSwipeLeft && f < (float)(-(mViewWidth / 2)))
            {
                f1 = -mViewWidth;
                f = 0.0F;
            } else
            if (mSwiping && mSwipingY && canSwipeDown && f1 > (float)(mViewHeight / 2))
            {
                f = mViewHeight;
                f1 = 0.0F;
            } else
            if (mSwiping && mSwipingY && canSwipeUp && f1 < (float)(-(mViewHeight / 2)))
            {
                f = -mViewHeight;
                f1 = 0.0F;
            } else
            if ((float)mMinFlingVelocity <= f4 && f4 <= (float)mMaxFlingVelocity && f5 < f4 && mSwiping && mSwipingX)
            {
                int i;
                if (f2 < 0.0F)
                {
                    flag3 = canSwipeLeft;
                } else
                {
                    flag3 = canSwipeRight;
                }
                if (f2 < 0.0F)
                {
                    i = 1;
                } else
                {
                    i = 0;
                }
                if (f >= 0.0F)
                {
                    flag = false;
                }
                if (i != flag)
                {
                    flag3 = false;
                }
                if (f2 < 0.0F)
                {
                    i = -mViewWidth;
                } else
                {
                    i = mViewWidth;
                }
                f1 = i;
                f = 0.0F;
            } else
            if ((float)mMinFlingVelocity <= f5 && f5 <= (float)mMaxFlingVelocity && f4 < f5 && mSwiping)
            {
                if (mSwipingY)
                {
                    int j;
                    boolean flag1;
                    if (f3 < 0.0F)
                    {
                        flag3 = canSwipeUp;
                    } else
                    {
                        flag3 = canSwipeDown;
                    }
                    if (f3 < 0.0F)
                    {
                        j = 1;
                    } else
                    {
                        j = 0;
                    }
                    if (f1 < 0.0F)
                    {
                        flag1 = flag2;
                    } else
                    {
                        flag1 = false;
                    }
                    if (j != flag1)
                    {
                        flag3 = false;
                    }
                    if (f3 < 0.0F)
                    {
                        j = -mViewHeight;
                    } else
                    {
                        j = mViewHeight;
                    }
                    f = j;
                    f1 = 0.0F;
                } else
                {
                    f = 0.0F;
                    f1 = 0.0F;
                    flag3 = false;
                }
            } else
            {
                f = 0.0F;
                f1 = 0.0F;
                flag3 = false;
            }
            if (flag3)
            {
                mView.animate().translationX(f1).translationY(f).alpha(0.0F).setDuration(mAnimationTime).setListener(new AnimatorListenerAdapter() {

                    final SwipeDismissTouchListener this$0;

                    public void onAnimationEnd(Animator animator)
                    {
                        SwipeDismissTouchListener._2D_wrap0(SwipeDismissTouchListener.this);
                    }

            
            {
                this$0 = SwipeDismissTouchListener.this;
                super();
            }
                });
            } else
            if (mSwiping)
            {
                mView.animate().translationX(0.0F).translationY(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mTranslationX = 0.0F;
            mTranslationY = 0.0F;
            mDownX = 0.0F;
            mDownY = 0.0F;
            mSwiping = false;
            mSwipingX = false;
            mSwipingY = false;
            return false;
        }
        continue; /* Loop/switch isn't completed */
_L5:
        if (mVelocityTracker != null)
        {
            mView.animate().translationX(0.0F).translationY(0.0F).alpha(1.0F).setDuration(mAnimationTime).setListener(null);
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mTranslationX = 0.0F;
            mTranslationY = 0.0F;
            mDownX = 0.0F;
            mDownY = 0.0F;
            mSwiping = false;
            mSwipingX = false;
            mSwipingY = false;
            return false;
        }
        if (true) goto _L1; else goto _L4
_L4:
        Debug.Printf("SwipeSwipe: action move x %f y %f", new Object[] {
            Float.valueOf(mDownX), Float.valueOf(mDownY)
        });
        if (mVelocityTracker != null)
        {
            mVelocityTracker.addMovement(motionevent);
            f1 = motionevent.getRawX() - mDownX;
            f2 = motionevent.getRawY() - mDownY;
            if (!mSwiping)
            {
                if (f1 < (float)(-mSlop) && Math.abs(f2) < Math.abs(f1) / 2.0F)
                {
                    flag5 = canSwipeLeft;
                } else
                {
                    flag5 = false;
                }
                if (f1 > (float)mSlop && Math.abs(f2) < Math.abs(f1) / 2.0F)
                {
                    flag3 = canSwipeRight;
                } else
                {
                    flag3 = false;
                }
                if (f2 < (float)(-mSlop) && Math.abs(f1) < Math.abs(f2) / 2.0F)
                {
                    flag6 = canSwipeUp;
                } else
                {
                    flag6 = false;
                }
                if (f2 > (float)mSlop && Math.abs(f1) < Math.abs(f2) / 2.0F)
                {
                    flag4 = canSwipeDown;
                } else
                {
                    flag4 = false;
                }
                if (flag5)
                {
                    flag3 = true;
                }
                if (flag6)
                {
                    flag4 = true;
                }
                if (flag3 && flag4)
                {
                    if (Math.abs(f1) >= Math.abs(f2))
                    {
                        flag4 = false;
                    } else
                    {
                        flag3 = false;
                    }
                }
                if (flag3 || flag4)
                {
                    mSwiping = true;
                    mSwipingX = flag3;
                    mSwipingY = flag4;
                    if (flag3)
                    {
                        if (f1 > 0.0F)
                        {
                            k = mSlop;
                        } else
                        {
                            k = -mSlop;
                        }
                    } else
                    {
                        k = 0;
                    }
                    mSwipingSlopX = k;
                    if (flag4)
                    {
                        if (f2 > 0.0F)
                        {
                            k = mSlop;
                        } else
                        {
                            k = -mSlop;
                        }
                    } else
                    {
                        k = 0;
                    }
                    mSwipingSlopY = k;
                    mView.getParent().requestDisallowInterceptTouchEvent(true);
                    motionevent1 = MotionEvent.obtain(motionevent);
                    motionevent1.setAction(motionevent.getActionIndex() << 8 | 3);
                    mView.onTouchEvent(motionevent1);
                    motionevent1.recycle();
                }
            }
            if (mSwiping)
            {
                if (mSwipingX)
                {
                    f = f1;
                } else
                {
                    f = 0.0F;
                }
                mTranslationX = f;
                if (mSwipingY)
                {
                    f = f2;
                } else
                {
                    f = 0.0F;
                }
                mTranslationY = f;
                motionevent = mView;
                if (mSwipingX)
                {
                    f = f1 - (float)mSwipingSlopX;
                } else
                {
                    f = 0.0F;
                }
                motionevent.setTranslationX(f);
                motionevent = mView;
                if (mSwipingY)
                {
                    f = f2 - (float)mSwipingSlopY;
                } else
                {
                    f = 0.0F;
                }
                motionevent.setTranslationY(f);
                if (mSwipingX)
                {
                    mView.setAlpha(Math.max(0.0F, Math.min(1.0F, 1.0F - (Math.abs(f1) * 2.0F) / (float)mViewWidth)));
                } else
                if (mSwipingY)
                {
                    mView.setAlpha(Math.max(0.0F, Math.min(1.0F, 1.0F - (Math.abs(f2) * 2.0F) / (float)mViewHeight)));
                    return true;
                }
                return true;
            }
        }
        if (true) goto _L1; else goto _L6
_L6:
    }

    public boolean onTouchEvent(MotionEvent motionevent)
    {
        return false;
    }
}
