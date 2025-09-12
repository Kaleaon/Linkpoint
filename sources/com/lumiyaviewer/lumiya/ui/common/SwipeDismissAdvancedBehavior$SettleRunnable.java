// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            SwipeDismissAdvancedBehavior

private class mDismiss
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

    er(View view, boolean flag)
    {
        this$0 = SwipeDismissAdvancedBehavior.this;
        super();
        mView = view;
        mDismiss = flag;
    }
}
