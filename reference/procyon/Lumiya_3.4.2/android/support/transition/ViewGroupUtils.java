// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.os.Build$VERSION;

class ViewGroupUtils
{
    private static final ViewGroupUtilsImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT < 18) {
            IMPL = new ViewGroupUtilsApi14();
        }
        else {
            IMPL = new ViewGroupUtilsApi18();
        }
    }
    
    static ViewGroupOverlayImpl getOverlay(@NonNull final ViewGroup viewGroup) {
        return ViewGroupUtils.IMPL.getOverlay(viewGroup);
    }
    
    static void suppressLayout(@NonNull final ViewGroup viewGroup, final boolean b) {
        ViewGroupUtils.IMPL.suppressLayout(viewGroup, b);
    }
}
