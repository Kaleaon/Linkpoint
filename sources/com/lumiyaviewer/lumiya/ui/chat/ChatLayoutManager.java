// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class ChatLayoutManager extends LinearLayoutManager
{
    private abstract class SmoothScroller extends LinearSmoothScroller
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

        public SmoothScroller(Context context)
        {
            this$0 = ChatLayoutManager.this;
            super(context);
        }
    }


    private static final float SMOOTH_SCROLL_FAST_SPEED = 20F;
    private static final float SMOOTH_SCROLL_SPEED = 1000F;
    private final SmoothScroller fastSmoothScroller;
    private boolean isFast;
    private final SmoothScroller smoothScroller;

    public ChatLayoutManager(Context context, int i, boolean flag)
    {
        super(context, i, flag);
        isFast = false;
        smoothScroller = new SmoothScroller(this, context) {

            final ChatLayoutManager this$0;

            protected float getScrollMs()
            {
                return 1000F;
            }

            
            {
                this$0 = chatlayoutmanager1;
                super(context);
            }
        };
        fastSmoothScroller = new SmoothScroller(this, context) {

            final ChatLayoutManager this$0;

            protected float getScrollMs()
            {
                return 20F;
            }

            
            {
                this$0 = chatlayoutmanager1;
                super(context);
            }
        };
    }

    public void setScrollMode(boolean flag)
    {
        isFast = flag;
    }

    public void smoothScrollToPosition(RecyclerView recyclerview, android.support.v7.widget.RecyclerView.State state, int i)
    {
        if (isFast)
        {
            recyclerview = fastSmoothScroller;
        } else
        {
            recyclerview = smoothScroller;
        }
        recyclerview.setTargetPosition(i);
        startSmoothScroll(recyclerview);
    }
}
