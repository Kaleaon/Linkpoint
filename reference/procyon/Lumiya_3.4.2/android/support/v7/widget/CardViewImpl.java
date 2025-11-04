// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.content.Context;
import android.content.res.ColorStateList;

interface CardViewImpl
{
    ColorStateList getBackgroundColor(final CardViewDelegate p0);
    
    float getElevation(final CardViewDelegate p0);
    
    float getMaxElevation(final CardViewDelegate p0);
    
    float getMinHeight(final CardViewDelegate p0);
    
    float getMinWidth(final CardViewDelegate p0);
    
    float getRadius(final CardViewDelegate p0);
    
    void initStatic();
    
    void initialize(final CardViewDelegate p0, final Context p1, final ColorStateList p2, final float p3, final float p4, final float p5);
    
    void onCompatPaddingChanged(final CardViewDelegate p0);
    
    void onPreventCornerOverlapChanged(final CardViewDelegate p0);
    
    void setBackgroundColor(final CardViewDelegate p0, @Nullable final ColorStateList p1);
    
    void setElevation(final CardViewDelegate p0, final float p1);
    
    void setMaxElevation(final CardViewDelegate p0, final float p1);
    
    void setRadius(final CardViewDelegate p0, final float p1);
    
    void updatePadding(final CardViewDelegate p0);
}
