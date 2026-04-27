// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.RequiresApi;

@RequiresApi(18)
class ViewUtilsApi18 extends ViewUtilsApi14
{
    @Override
    public ViewOverlayImpl getOverlay(@NonNull final View view) {
        return new ViewOverlayApi18(view);
    }
    
    @Override
    public WindowIdImpl getWindowId(@NonNull final View view) {
        return new WindowIdApi18(view);
    }
}
