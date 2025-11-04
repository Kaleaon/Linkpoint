// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview.scrollerproxy;

import android.content.Context;
import android.annotation.TargetApi;

@TargetApi(14)
public class IcsScroller extends GingerScroller
{
    public IcsScroller(final Context context) {
        super(context);
    }
    
    @Override
    public boolean computeScrollOffset() {
        return this.mScroller.computeScrollOffset();
    }
}
