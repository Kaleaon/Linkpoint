// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.scrollerproxy;

import android.os.Build$VERSION;
import android.content.Context;

public abstract class ScrollerProxy
{
    public static ScrollerProxy getScroller(final Context context) {
        if (Build$VERSION.SDK_INT < 9) {
            return new PreGingerScroller(context);
        }
        if (Build$VERSION.SDK_INT >= 14) {
            return new IcsScroller(context);
        }
        return new GingerScroller(context);
    }
    
    public abstract boolean computeScrollOffset();
    
    public abstract void fling(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    public abstract void forceFinished(final boolean p0);
    
    public abstract int getCurrX();
    
    public abstract int getCurrY();
    
    public abstract boolean isFinished();
}
