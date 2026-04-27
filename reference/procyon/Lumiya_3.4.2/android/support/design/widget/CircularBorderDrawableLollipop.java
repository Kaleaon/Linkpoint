// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.graphics.Outline;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class CircularBorderDrawableLollipop extends CircularBorderDrawable
{
    public void getOutline(final Outline outline) {
        this.copyBounds(this.mRect);
        outline.setOval(this.mRect);
    }
}
