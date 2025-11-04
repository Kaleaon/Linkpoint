// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.view.View;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

public class NestedScrollingParentHelper
{
    private int mNestedScrollAxes;
    private final ViewGroup mViewGroup;
    
    public NestedScrollingParentHelper(@NonNull final ViewGroup mViewGroup) {
        this.mViewGroup = mViewGroup;
    }
    
    public int getNestedScrollAxes() {
        return this.mNestedScrollAxes;
    }
    
    public void onNestedScrollAccepted(@NonNull final View view, @NonNull final View view2, final int n) {
        this.onNestedScrollAccepted(view, view2, n, 0);
    }
    
    public void onNestedScrollAccepted(@NonNull final View view, @NonNull final View view2, final int mNestedScrollAxes, final int n) {
        this.mNestedScrollAxes = mNestedScrollAxes;
    }
    
    public void onStopNestedScroll(@NonNull final View view) {
        this.onStopNestedScroll(view, 0);
    }
    
    public void onStopNestedScroll(@NonNull final View view, final int n) {
        this.mNestedScrollAxes = 0;
    }
}
