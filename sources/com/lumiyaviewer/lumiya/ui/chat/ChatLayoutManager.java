package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import javax.annotation.Nonnull;

public class ChatLayoutManager extends LinearLayoutManager {
    private static final float SMOOTH_SCROLL_FAST_SPEED = 20.0f;
    private static final float SMOOTH_SCROLL_SPEED = 1000.0f;
    @Nonnull
    private final SmoothScroller fastSmoothScroller;
    private boolean isFast = false;
    @Nonnull
    private final SmoothScroller smoothScroller;

    private abstract class SmoothScroller extends LinearSmoothScroller {
        private final float scrollDp = 200.0f;

        public SmoothScroller(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return getScrollMs() / TypedValue.applyDimension(1, 200.0f, displayMetrics);
        }

        public PointF computeScrollVectorForPosition(int i) {
            return ChatLayoutManager.this.computeScrollVectorForPosition(i);
        }

        /* access modifiers changed from: protected */
        public abstract float getScrollMs();
    }

    public ChatLayoutManager(Context context, int i, boolean z) {
        super(context, i, z);
        this.smoothScroller = new SmoothScroller(this, context) {
            /* access modifiers changed from: protected */
            public float getScrollMs() {
                return ChatLayoutManager.SMOOTH_SCROLL_SPEED;
            }
        };
        this.fastSmoothScroller = new SmoothScroller(this, context) {
            /* access modifiers changed from: protected */
            public float getScrollMs() {
                return 20.0f;
            }
        };
    }

    public void setScrollMode(boolean z) {
        this.isFast = z;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
        SmoothScroller smoothScroller2 = this.isFast ? this.fastSmoothScroller : this.smoothScroller;
        smoothScroller2.setTargetPosition(i);
        startSmoothScroll(smoothScroller2);
    }
}
