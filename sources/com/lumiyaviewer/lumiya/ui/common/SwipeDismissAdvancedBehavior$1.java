// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.ViewConfiguration;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissAdvancedBehavior

class this._cls0 extends android.support.v4.widget.avior._cls1
{

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
        ViewCompat.postOnAnimation(view, new ttleRunnable(SwipeDismissAdvancedBehavior.this, view, flag));
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

    ttleRunnable()
    {
        this$0 = SwipeDismissAdvancedBehavior.this;
        super();
    }
}
