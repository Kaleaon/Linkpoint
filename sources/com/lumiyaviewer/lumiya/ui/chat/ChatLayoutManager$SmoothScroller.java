// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;
import android.util.TypedValue;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatLayoutManager

private abstract class this._cls0 extends LinearSmoothScroller
{

    private final float scrollDp = 200F;
    final ChatLayoutManager this$0;

    protected float calculateSpeedPerPixel(DisplayMetrics displaymetrics)
    {
        return getScrollMs() / TypedValue.applyDimension(1, 200F, displaymetrics);
    }

    public PointF computeScrollVectorForPosition(int i)
    {
        return ChatLayoutManager.this.computeScrollVectorForPosition(i);
    }

    protected abstract float getScrollMs();

    public (Context context)
    {
        this$0 = ChatLayoutManager.this;
        super(context);
    }
}
