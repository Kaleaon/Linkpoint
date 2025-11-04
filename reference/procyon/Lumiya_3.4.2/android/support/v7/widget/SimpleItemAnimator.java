// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.view.View;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;

public abstract class SimpleItemAnimator extends ItemAnimator
{
    private static final boolean DEBUG = false;
    private static final String TAG = "SimpleItemAnimator";
    boolean mSupportsChangeAnimations;
    
    public SimpleItemAnimator() {
        this.mSupportsChangeAnimations = true;
    }
    
    public abstract boolean animateAdd(final ViewHolder p0);
    
    @Override
    public boolean animateAppearance(@NonNull final ViewHolder viewHolder, @Nullable final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2) {
        if (itemHolderInfo != null && (itemHolderInfo.left != itemHolderInfo2.left || itemHolderInfo.top != itemHolderInfo2.top)) {
            return this.animateMove(viewHolder, itemHolderInfo.left, itemHolderInfo.top, itemHolderInfo2.left, itemHolderInfo2.top);
        }
        return this.animateAdd(viewHolder);
    }
    
    public abstract boolean animateChange(final ViewHolder p0, final ViewHolder p1, final int p2, final int p3, final int p4, final int p5);
    
    @Override
    public boolean animateChange(@NonNull final ViewHolder viewHolder, @NonNull final ViewHolder viewHolder2, @NonNull final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2) {
        final int left = itemHolderInfo.left;
        final int top = itemHolderInfo.top;
        int n;
        int n2;
        if (!viewHolder2.shouldIgnore()) {
            n = itemHolderInfo2.left;
            n2 = itemHolderInfo2.top;
        }
        else {
            n = itemHolderInfo.left;
            n2 = itemHolderInfo.top;
        }
        return this.animateChange(viewHolder, viewHolder2, left, top, n, n2);
    }
    
    @Override
    public boolean animateDisappearance(@NonNull final ViewHolder viewHolder, @NonNull final ItemHolderInfo itemHolderInfo, @Nullable final ItemHolderInfo itemHolderInfo2) {
        final int left = itemHolderInfo.left;
        final int top = itemHolderInfo.top;
        final View itemView = viewHolder.itemView;
        int n;
        if (itemHolderInfo2 != null) {
            n = itemHolderInfo2.left;
        }
        else {
            n = itemView.getLeft();
        }
        int n2;
        if (itemHolderInfo2 != null) {
            n2 = itemHolderInfo2.top;
        }
        else {
            n2 = itemView.getTop();
        }
        if (!viewHolder.isRemoved() && (left != n || top != n2)) {
            itemView.layout(n, n2, itemView.getWidth() + n, itemView.getHeight() + n2);
            return this.animateMove(viewHolder, left, top, n, n2);
        }
        return this.animateRemove(viewHolder);
    }
    
    public abstract boolean animateMove(final ViewHolder p0, final int p1, final int p2, final int p3, final int p4);
    
    @Override
    public boolean animatePersistence(@NonNull final ViewHolder viewHolder, @NonNull final ItemHolderInfo itemHolderInfo, @NonNull final ItemHolderInfo itemHolderInfo2) {
        if (itemHolderInfo.left == itemHolderInfo2.left && itemHolderInfo.top == itemHolderInfo2.top) {
            this.dispatchMoveFinished(viewHolder);
            return false;
        }
        return this.animateMove(viewHolder, itemHolderInfo.left, itemHolderInfo.top, itemHolderInfo2.left, itemHolderInfo2.top);
    }
    
    public abstract boolean animateRemove(final ViewHolder p0);
    
    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull final ViewHolder viewHolder) {
        boolean b = false;
        if (!this.mSupportsChangeAnimations || viewHolder.isInvalid()) {
            b = true;
        }
        return b;
    }
    
    public final void dispatchAddFinished(final ViewHolder viewHolder) {
        this.onAddFinished(viewHolder);
        ((RecyclerView.ItemAnimator)this).dispatchAnimationFinished(viewHolder);
    }
    
    public final void dispatchAddStarting(final ViewHolder viewHolder) {
        this.onAddStarting(viewHolder);
    }
    
    public final void dispatchChangeFinished(final ViewHolder viewHolder, final boolean b) {
        this.onChangeFinished(viewHolder, b);
        ((RecyclerView.ItemAnimator)this).dispatchAnimationFinished(viewHolder);
    }
    
    public final void dispatchChangeStarting(final ViewHolder viewHolder, final boolean b) {
        this.onChangeStarting(viewHolder, b);
    }
    
    public final void dispatchMoveFinished(final ViewHolder viewHolder) {
        this.onMoveFinished(viewHolder);
        ((RecyclerView.ItemAnimator)this).dispatchAnimationFinished(viewHolder);
    }
    
    public final void dispatchMoveStarting(final ViewHolder viewHolder) {
        this.onMoveStarting(viewHolder);
    }
    
    public final void dispatchRemoveFinished(final ViewHolder viewHolder) {
        this.onRemoveFinished(viewHolder);
        ((RecyclerView.ItemAnimator)this).dispatchAnimationFinished(viewHolder);
    }
    
    public final void dispatchRemoveStarting(final ViewHolder viewHolder) {
        this.onRemoveStarting(viewHolder);
    }
    
    public boolean getSupportsChangeAnimations() {
        return this.mSupportsChangeAnimations;
    }
    
    public void onAddFinished(final ViewHolder viewHolder) {
    }
    
    public void onAddStarting(final ViewHolder viewHolder) {
    }
    
    public void onChangeFinished(final ViewHolder viewHolder, final boolean b) {
    }
    
    public void onChangeStarting(final ViewHolder viewHolder, final boolean b) {
    }
    
    public void onMoveFinished(final ViewHolder viewHolder) {
    }
    
    public void onMoveStarting(final ViewHolder viewHolder) {
    }
    
    public void onRemoveFinished(final ViewHolder viewHolder) {
    }
    
    public void onRemoveStarting(final ViewHolder viewHolder) {
    }
    
    public void setSupportsChangeAnimations(final boolean mSupportsChangeAnimations) {
        this.mSupportsChangeAnimations = mSupportsChangeAnimations;
    }
}
