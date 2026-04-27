// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.os.Build$VERSION;
import android.view.ViewGroup$MarginLayoutParams;

public final class MarginLayoutParamsCompat
{
    private MarginLayoutParamsCompat() {
    }
    
    public static int getLayoutDirection(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        int layoutDirection;
        if (Build$VERSION.SDK_INT < 17) {
            layoutDirection = 0;
        }
        else {
            layoutDirection = viewGroup$MarginLayoutParams.getLayoutDirection();
        }
        int n;
        if (layoutDirection == 0) {
            n = layoutDirection;
        }
        else if ((n = layoutDirection) != 1) {
            n = 0;
        }
        return n;
    }
    
    public static int getMarginEnd(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        if (Build$VERSION.SDK_INT < 17) {
            return viewGroup$MarginLayoutParams.rightMargin;
        }
        return viewGroup$MarginLayoutParams.getMarginEnd();
    }
    
    public static int getMarginStart(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        if (Build$VERSION.SDK_INT < 17) {
            return viewGroup$MarginLayoutParams.leftMargin;
        }
        return viewGroup$MarginLayoutParams.getMarginStart();
    }
    
    public static boolean isMarginRelative(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
        return Build$VERSION.SDK_INT >= 17 && viewGroup$MarginLayoutParams.isMarginRelative();
    }
    
    public static void resolveLayoutDirection(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams, final int n) {
        if (Build$VERSION.SDK_INT >= 17) {
            viewGroup$MarginLayoutParams.resolveLayoutDirection(n);
        }
    }
    
    public static void setLayoutDirection(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams, final int layoutDirection) {
        if (Build$VERSION.SDK_INT >= 17) {
            viewGroup$MarginLayoutParams.setLayoutDirection(layoutDirection);
        }
    }
    
    public static void setMarginEnd(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams, final int n) {
        if (Build$VERSION.SDK_INT < 17) {
            viewGroup$MarginLayoutParams.rightMargin = n;
        }
        else {
            viewGroup$MarginLayoutParams.setMarginEnd(n);
        }
    }
    
    public static void setMarginStart(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams, final int n) {
        if (Build$VERSION.SDK_INT < 17) {
            viewGroup$MarginLayoutParams.leftMargin = n;
        }
        else {
            viewGroup$MarginLayoutParams.setMarginStart(n);
        }
    }
}
