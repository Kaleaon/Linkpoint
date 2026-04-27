// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.view;

import com.nineoldandroids.view.animation.AnimatorProxy;
import android.view.View;

public final class ViewHelper
{
    private ViewHelper() {
    }
    
    public static float getAlpha(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getAlpha(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getAlpha();
        }
        return n;
    }
    
    public static float getPivotX(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getPivotX(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getPivotX();
        }
        return n;
    }
    
    public static float getPivotY(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getPivotY(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getPivotY();
        }
        return n;
    }
    
    public static float getRotation(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getRotation(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getRotation();
        }
        return n;
    }
    
    public static float getRotationX(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getRotationX(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getRotationX();
        }
        return n;
    }
    
    public static float getRotationY(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getRotationY(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getRotationY();
        }
        return n;
    }
    
    public static float getScaleX(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getScaleX(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getScaleX();
        }
        return n;
    }
    
    public static float getScaleY(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getScaleY(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getScaleY();
        }
        return n;
    }
    
    public static float getScrollX(final View view) {
        float scrollX;
        if (!AnimatorProxy.NEEDS_PROXY) {
            scrollX = Honeycomb.getScrollX(view);
        }
        else {
            scrollX = (float)AnimatorProxy.wrap(view).getScrollX();
        }
        return scrollX;
    }
    
    public static float getScrollY(final View view) {
        float scrollY;
        if (!AnimatorProxy.NEEDS_PROXY) {
            scrollY = Honeycomb.getScrollY(view);
        }
        else {
            scrollY = (float)AnimatorProxy.wrap(view).getScrollY();
        }
        return scrollY;
    }
    
    public static float getTranslationX(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getTranslationX(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getTranslationX();
        }
        return n;
    }
    
    public static float getTranslationY(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getTranslationY(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getTranslationY();
        }
        return n;
    }
    
    public static float getX(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getX(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getX();
        }
        return n;
    }
    
    public static float getY(final View view) {
        float n;
        if (!AnimatorProxy.NEEDS_PROXY) {
            n = Honeycomb.getY(view);
        }
        else {
            n = AnimatorProxy.wrap(view).getY();
        }
        return n;
    }
    
    public static void setAlpha(final View view, final float alpha) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setAlpha(view, alpha);
        }
        else {
            AnimatorProxy.wrap(view).setAlpha(alpha);
        }
    }
    
    public static void setPivotX(final View view, final float pivotX) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setPivotX(view, pivotX);
        }
        else {
            AnimatorProxy.wrap(view).setPivotX(pivotX);
        }
    }
    
    public static void setPivotY(final View view, final float pivotY) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setPivotY(view, pivotY);
        }
        else {
            AnimatorProxy.wrap(view).setPivotY(pivotY);
        }
    }
    
    public static void setRotation(final View view, final float rotation) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotation(view, rotation);
        }
        else {
            AnimatorProxy.wrap(view).setRotation(rotation);
        }
    }
    
    public static void setRotationX(final View view, final float rotationX) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotationX(view, rotationX);
        }
        else {
            AnimatorProxy.wrap(view).setRotationX(rotationX);
        }
    }
    
    public static void setRotationY(final View view, final float rotationY) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setRotationY(view, rotationY);
        }
        else {
            AnimatorProxy.wrap(view).setRotationY(rotationY);
        }
    }
    
    public static void setScaleX(final View view, final float scaleX) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScaleX(view, scaleX);
        }
        else {
            AnimatorProxy.wrap(view).setScaleX(scaleX);
        }
    }
    
    public static void setScaleY(final View view, final float scaleY) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScaleY(view, scaleY);
        }
        else {
            AnimatorProxy.wrap(view).setScaleY(scaleY);
        }
    }
    
    public static void setScrollX(final View view, final int scrollX) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScrollX(view, scrollX);
        }
        else {
            AnimatorProxy.wrap(view).setScrollX(scrollX);
        }
    }
    
    public static void setScrollY(final View view, final int scrollY) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setScrollY(view, scrollY);
        }
        else {
            AnimatorProxy.wrap(view).setScrollY(scrollY);
        }
    }
    
    public static void setTranslationX(final View view, final float translationX) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setTranslationX(view, translationX);
        }
        else {
            AnimatorProxy.wrap(view).setTranslationX(translationX);
        }
    }
    
    public static void setTranslationY(final View view, final float translationY) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setTranslationY(view, translationY);
        }
        else {
            AnimatorProxy.wrap(view).setTranslationY(translationY);
        }
    }
    
    public static void setX(final View view, final float x) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setX(view, x);
        }
        else {
            AnimatorProxy.wrap(view).setX(x);
        }
    }
    
    public static void setY(final View view, final float y) {
        if (!AnimatorProxy.NEEDS_PROXY) {
            Honeycomb.setY(view, y);
        }
        else {
            AnimatorProxy.wrap(view).setY(y);
        }
    }
    
    private static final class Honeycomb
    {
        static float getAlpha(final View view) {
            return view.getAlpha();
        }
        
        static float getPivotX(final View view) {
            return view.getPivotX();
        }
        
        static float getPivotY(final View view) {
            return view.getPivotY();
        }
        
        static float getRotation(final View view) {
            return view.getRotation();
        }
        
        static float getRotationX(final View view) {
            return view.getRotationX();
        }
        
        static float getRotationY(final View view) {
            return view.getRotationY();
        }
        
        static float getScaleX(final View view) {
            return view.getScaleX();
        }
        
        static float getScaleY(final View view) {
            return view.getScaleY();
        }
        
        static float getScrollX(final View view) {
            return (float)view.getScrollX();
        }
        
        static float getScrollY(final View view) {
            return (float)view.getScrollY();
        }
        
        static float getTranslationX(final View view) {
            return view.getTranslationX();
        }
        
        static float getTranslationY(final View view) {
            return view.getTranslationY();
        }
        
        static float getX(final View view) {
            return view.getX();
        }
        
        static float getY(final View view) {
            return view.getY();
        }
        
        static void setAlpha(final View view, final float alpha) {
            view.setAlpha(alpha);
        }
        
        static void setPivotX(final View view, final float pivotX) {
            view.setPivotX(pivotX);
        }
        
        static void setPivotY(final View view, final float pivotY) {
            view.setPivotY(pivotY);
        }
        
        static void setRotation(final View view, final float rotation) {
            view.setRotation(rotation);
        }
        
        static void setRotationX(final View view, final float rotationX) {
            view.setRotationX(rotationX);
        }
        
        static void setRotationY(final View view, final float rotationY) {
            view.setRotationY(rotationY);
        }
        
        static void setScaleX(final View view, final float scaleX) {
            view.setScaleX(scaleX);
        }
        
        static void setScaleY(final View view, final float scaleY) {
            view.setScaleY(scaleY);
        }
        
        static void setScrollX(final View view, final int scrollX) {
            view.setScrollX(scrollX);
        }
        
        static void setScrollY(final View view, final int scrollY) {
            view.setScrollY(scrollY);
        }
        
        static void setTranslationX(final View view, final float translationX) {
            view.setTranslationX(translationX);
        }
        
        static void setTranslationY(final View view, final float translationY) {
            view.setTranslationY(translationY);
        }
        
        static void setX(final View view, final float x) {
            view.setX(x);
        }
        
        static void setY(final View view, final float y) {
            view.setY(y);
        }
    }
}
