// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.view.View;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.support.annotation.RequiresApi;

@RequiresApi(18)
class ViewGroupOverlayApi18 implements ViewGroupOverlayImpl
{
    private final ViewGroupOverlay mViewGroupOverlay;
    
    ViewGroupOverlayApi18(@NonNull final ViewGroup viewGroup) {
        this.mViewGroupOverlay = viewGroup.getOverlay();
    }
    
    @Override
    public void add(@NonNull final Drawable drawable) {
        this.mViewGroupOverlay.add(drawable);
    }
    
    @Override
    public void add(@NonNull final View view) {
        this.mViewGroupOverlay.add(view);
    }
    
    @Override
    public void clear() {
        this.mViewGroupOverlay.clear();
    }
    
    @Override
    public void remove(@NonNull final Drawable drawable) {
        this.mViewGroupOverlay.remove(drawable);
    }
    
    @Override
    public void remove(@NonNull final View view) {
        this.mViewGroupOverlay.remove(view);
    }
}
