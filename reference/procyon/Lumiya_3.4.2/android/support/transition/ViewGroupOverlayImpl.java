// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface ViewGroupOverlayImpl extends ViewOverlayImpl
{
    void add(@NonNull final View p0);
    
    void remove(@NonNull final View p0);
}
