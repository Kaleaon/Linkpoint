// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissTouchListener

class val.originalHeight extends AnimatorListenerAdapter
{

    final SwipeDismissTouchListener this$0;
    final android.view.smissTouchListener val$lp;
    final int val$originalHeight;

    public void onAnimationEnd(Animator animator)
    {
        SwipeDismissTouchListener._2D_get0(SwipeDismissTouchListener.this).onDismiss(SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this), SwipeDismissTouchListener._2D_get1(SwipeDismissTouchListener.this));
        SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setAlpha(1.0F);
        SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setTranslationX(0.0F);
        SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setTranslationY(0.0F);
        val$lp.t = val$originalHeight;
        SwipeDismissTouchListener._2D_get2(SwipeDismissTouchListener.this).setLayoutParams(val$lp);
    }

    smissCallbacks()
    {
        this$0 = final_swipedismisstouchlistener;
        val$lp = smisstouchlistener;
        val$originalHeight = I.this;
        super();
    }
}
