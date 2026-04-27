// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.Nullable;

public interface NestedScrollingChild2 extends NestedScrollingChild
{
    boolean dispatchNestedPreScroll(final int p0, final int p1, @Nullable final int[] p2, @Nullable final int[] p3, final int p4);
    
    boolean dispatchNestedScroll(final int p0, final int p1, final int p2, final int p3, @Nullable final int[] p4, final int p5);
    
    boolean hasNestedScrollingParent(final int p0);
    
    boolean startNestedScroll(final int p0, final int p1);
    
    void stopNestedScroll(final int p0);
}
