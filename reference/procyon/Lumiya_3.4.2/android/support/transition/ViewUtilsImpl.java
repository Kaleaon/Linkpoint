// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
interface ViewUtilsImpl
{
    void clearNonTransitionAlpha(@NonNull final View p0);
    
    ViewOverlayImpl getOverlay(@NonNull final View p0);
    
    float getTransitionAlpha(@NonNull final View p0);
    
    WindowIdImpl getWindowId(@NonNull final View p0);
    
    void saveNonTransitionAlpha(@NonNull final View p0);
    
    void setAnimationMatrix(@NonNull final View p0, final Matrix p1);
    
    void setLeftTopRightBottom(final View p0, final int p1, final int p2, final int p3, final int p4);
    
    void setTransitionAlpha(@NonNull final View p0, final float p1);
    
    void transformMatrixToGlobal(@NonNull final View p0, @NonNull final Matrix p1);
    
    void transformMatrixToLocal(@NonNull final View p0, @NonNull final Matrix p1);
}
