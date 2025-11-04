// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public interface DrawableWrapper
{
    Drawable getWrappedDrawable();
    
    void setWrappedDrawable(final Drawable p0);
}
