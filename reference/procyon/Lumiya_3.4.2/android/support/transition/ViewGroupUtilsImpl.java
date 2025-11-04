// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface ViewGroupUtilsImpl
{
    ViewGroupOverlayImpl getOverlay(@NonNull final ViewGroup p0);
    
    void suppressLayout(@NonNull final ViewGroup p0, final boolean p1);
}
