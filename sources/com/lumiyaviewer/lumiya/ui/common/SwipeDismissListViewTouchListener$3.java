// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.View;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissListViewTouchListener

class val.downPosition extends AnimatorListenerAdapter
{

    final SwipeDismissListViewTouchListener this$0;
    final int val$downPosition;
    final View val$downView;

    public void onAnimationEnd(Animator animator)
    {
        SwipeDismissListViewTouchListener._2D_wrap0(SwipeDismissListViewTouchListener.this, val$downView, val$downPosition);
    }

    ()
    {
        this$0 = final_swipedismisslistviewtouchlistener;
        val$downView = view;
        val$downPosition = I.this;
        super();
    }
}
