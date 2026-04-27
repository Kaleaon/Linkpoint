// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.Nullable;

public interface NestedScrollingChild
{
    boolean dispatchNestedFling(final float p0, final float p1, final boolean p2);
    
    boolean dispatchNestedPreFling(final float p0, final float p1);
    
    boolean dispatchNestedPreScroll(final int p0, final int p1, @Nullable final int[] p2, @Nullable final int[] p3);
    
    boolean dispatchNestedScroll(final int p0, final int p1, final int p2, final int p3, @Nullable final int[] p4);
    
    boolean hasNestedScrollingParent();
    
    boolean isNestedScrollingEnabled();
    
    void setNestedScrollingEnabled(final boolean p0);
    
    boolean startNestedScroll(final int p0);
    
    void stopNestedScroll();
}
