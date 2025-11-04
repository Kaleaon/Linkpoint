// 
// Decompiled by Procyon v0.6.0
// 

package android.support.design.widget;

import android.graphics.drawable.Drawable;

interface ShadowViewDelegate
{
    float getRadius();
    
    boolean isCompatPaddingEnabled();
    
    void setBackgroundDrawable(final Drawable p0);
    
    void setShadowPadding(final int p0, final int p1, final int p2, final int p3);
}
