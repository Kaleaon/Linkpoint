// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewOverlay;
import android.support.annotation.RequiresApi;

@RequiresApi(18)
class ViewOverlayApi18 implements ViewOverlayImpl
{
    private final ViewOverlay mViewOverlay;
    
    ViewOverlayApi18(@NonNull final View view) {
        this.mViewOverlay = view.getOverlay();
    }
    
    @Override
    public void add(@NonNull final Drawable drawable) {
        this.mViewOverlay.add(drawable);
    }
    
    @Override
    public void clear() {
        this.mViewOverlay.clear();
    }
    
    @Override
    public void remove(@NonNull final Drawable drawable) {
        this.mViewOverlay.remove(drawable);
    }
}
