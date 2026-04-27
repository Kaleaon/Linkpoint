// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Matrix;
import android.view.ViewGroup;
import android.view.View;
import android.os.Build$VERSION;

class GhostViewUtils
{
    private static final GhostViewImpl.Creator CREATOR;
    
    static {
        if (Build$VERSION.SDK_INT < 21) {
            CREATOR = new GhostViewApi14.Creator();
        }
        else {
            CREATOR = new GhostViewApi21.Creator();
        }
    }
    
    static GhostViewImpl addGhost(final View view, final ViewGroup viewGroup, final Matrix matrix) {
        return GhostViewUtils.CREATOR.addGhost(view, viewGroup, matrix);
    }
    
    static void removeGhost(final View view) {
        GhostViewUtils.CREATOR.removeGhost(view);
    }
}
