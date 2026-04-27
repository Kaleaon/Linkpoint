// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Path;
import android.util.AttributeSet;
import android.content.Context;

public abstract class PathMotion
{
    public PathMotion() {
    }
    
    public PathMotion(final Context context, final AttributeSet set) {
    }
    
    public abstract Path getPath(final float p0, final float p1, final float p2, final float p3);
}
