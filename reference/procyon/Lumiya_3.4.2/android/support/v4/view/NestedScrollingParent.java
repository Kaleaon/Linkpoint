// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.NonNull;
import android.view.View;

public interface NestedScrollingParent
{
    int getNestedScrollAxes();
    
    boolean onNestedFling(@NonNull final View p0, final float p1, final float p2, final boolean p3);
    
    boolean onNestedPreFling(@NonNull final View p0, final float p1, final float p2);
    
    void onNestedPreScroll(@NonNull final View p0, final int p1, final int p2, @NonNull final int[] p3);
    
    void onNestedScroll(@NonNull final View p0, final int p1, final int p2, final int p3, final int p4);
    
    void onNestedScrollAccepted(@NonNull final View p0, @NonNull final View p1, final int p2);
    
    boolean onStartNestedScroll(@NonNull final View p0, @NonNull final View p1, final int p2);
    
    void onStopNestedScroll(@NonNull final View p0);
}
