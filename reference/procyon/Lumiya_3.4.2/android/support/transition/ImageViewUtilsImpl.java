// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface ImageViewUtilsImpl
{
    void animateTransform(final ImageView p0, final Matrix p1);
    
    void reserveEndAnimateTransform(final ImageView p0, final Animator p1);
    
    void startAnimateTransform(final ImageView p0);
}
