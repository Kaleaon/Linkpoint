// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface GhostViewImpl
{
    void reserveEndViewTransition(final ViewGroup p0, final View p1);
    
    void setVisibility(final int p0);
    
    public interface Creator
    {
        GhostViewImpl addGhost(final View p0, final ViewGroup p1, final Matrix p2);
        
        void removeGhost(final View p0);
    }
}
