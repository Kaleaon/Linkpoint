// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.graphics.PointF;
import android.util.TypedValue;
import android.util.DisplayMetrics;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import javax.annotation.Nonnull;
import android.support.v7.widget.LinearLayoutManager;

public class ChatLayoutManager extends LinearLayoutManager
{
    private static final float SMOOTH_SCROLL_FAST_SPEED = 20.0f;
    private static final float SMOOTH_SCROLL_SPEED = 1000.0f;
    @Nonnull
    private final SmoothScroller fastSmoothScroller;
    private boolean isFast;
    @Nonnull
    private final SmoothScroller smoothScroller;
    
    public ChatLayoutManager(final Context context, final int n, final boolean b) {
        super(context, n, b);
        this.isFast = false;
        this.smoothScroller = (SmoothScroller)new SmoothScroller(this, context) {
            @Override
            protected float getScrollMs() {
                return 1000.0f;
            }
        };
        this.fastSmoothScroller = (SmoothScroller)new SmoothScroller(this, context) {
            @Override
            protected float getScrollMs() {
                return 20.0f;
            }
        };
    }
    
    public void setScrollMode(final boolean isFast) {
        this.isFast = isFast;
    }
    
    @Override
    public void smoothScrollToPosition(final RecyclerView recyclerView, final State state, final int targetPosition) {
        SmoothScroller smoothScroller;
        if (this.isFast) {
            smoothScroller = this.fastSmoothScroller;
        }
        else {
            smoothScroller = this.smoothScroller;
        }
        ((RecyclerView.SmoothScroller)smoothScroller).setTargetPosition(targetPosition);
        ((RecyclerView.LayoutManager)this).startSmoothScroll(smoothScroller);
    }
    
    private abstract class SmoothScroller extends LinearSmoothScroller
    {
        private final float scrollDp;
        
        public SmoothScroller(final Context context) {
            super(context);
            this.scrollDp = 200.0f;
        }
        
        @Override
        protected float calculateSpeedPerPixel(final DisplayMetrics displayMetrics) {
            return this.getScrollMs() / TypedValue.applyDimension(1, 200.0f, displayMetrics);
        }
        
        @Override
        public PointF computeScrollVectorForPosition(final int n) {
            return ChatLayoutManager.this.computeScrollVectorForPosition(n);
        }
        
        protected abstract float getScrollMs();
    }
}
