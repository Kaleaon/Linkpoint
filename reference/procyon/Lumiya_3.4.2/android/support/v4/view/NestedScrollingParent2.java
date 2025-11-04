// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.view.View;

public interface NestedScrollingParent2 extends NestedScrollingParent
{
    void onNestedPreScroll(@NonNull final View p0, final int p1, final int p2, @Nullable final int[] p3, final int p4);
    
    void onNestedScroll(@NonNull final View p0, final int p1, final int p2, final int p3, final int p4, final int p5);
    
    void onNestedScrollAccepted(@NonNull final View p0, @NonNull final View p1, final int p2, final int p3);
    
    boolean onStartNestedScroll(@NonNull final View p0, @NonNull final View p1, final int p2, final int p3);
    
    void onStopNestedScroll(@NonNull final View p0, final int p1);
}
