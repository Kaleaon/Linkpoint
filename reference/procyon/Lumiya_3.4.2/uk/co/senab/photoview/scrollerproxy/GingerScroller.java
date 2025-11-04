// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.scrollerproxy;

import android.content.Context;
import android.widget.OverScroller;
import android.annotation.TargetApi;

@TargetApi(9)
public class GingerScroller extends ScrollerProxy
{
    protected final OverScroller mScroller;
    
    public GingerScroller(final Context context) {
        this.mScroller = new OverScroller(context);
    }
    
    @Override
    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }
    
    @Override
    public void fling(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10) {
        this.mScroller.fling(n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
    }
    
    @Override
    public void forceFinished(final boolean b) {
        this.mScroller.forceFinished(b);
    }
    
    @Override
    public int getCurrX() {
        return this.mScroller.getCurrX();
    }
    
    @Override
    public int getCurrY() {
        return this.mScroller.getCurrY();
    }
    
    @Override
    public boolean isFinished() {
        return this.mScroller.isFinished();
    }
}
