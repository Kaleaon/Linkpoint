// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.animation;

import com.nineoldandroids.util.IntProperty;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.nineoldandroids.util.FloatProperty;
import android.view.View;
import com.nineoldandroids.util.Property;

final class PreHoneycombCompat
{
    static Property<View, Float> ALPHA;
    static Property<View, Float> PIVOT_X;
    static Property<View, Float> PIVOT_Y;
    static Property<View, Float> ROTATION;
    static Property<View, Float> ROTATION_X;
    static Property<View, Float> ROTATION_Y;
    static Property<View, Float> SCALE_X;
    static Property<View, Float> SCALE_Y;
    static Property<View, Integer> SCROLL_X;
    static Property<View, Integer> SCROLL_Y;
    static Property<View, Float> TRANSLATION_X;
    static Property<View, Float> TRANSLATION_Y;
    static Property<View, Float> X;
    static Property<View, Float> Y;
    
    static {
        PreHoneycombCompat.ALPHA = (Property<View, Float>)new FloatProperty<View>("alpha") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getAlpha();
            }
            
            @Override
            public void setValue(final View view, final float alpha) {
                AnimatorProxy.wrap(view).setAlpha(alpha);
            }
        };
        PreHoneycombCompat.PIVOT_X = (Property<View, Float>)new FloatProperty<View>("pivotX") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getPivotX();
            }
            
            @Override
            public void setValue(final View view, final float pivotX) {
                AnimatorProxy.wrap(view).setPivotX(pivotX);
            }
        };
        PreHoneycombCompat.PIVOT_Y = (Property<View, Float>)new FloatProperty<View>("pivotY") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getPivotY();
            }
            
            @Override
            public void setValue(final View view, final float pivotY) {
                AnimatorProxy.wrap(view).setPivotY(pivotY);
            }
        };
        PreHoneycombCompat.TRANSLATION_X = (Property<View, Float>)new FloatProperty<View>("translationX") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getTranslationX();
            }
            
            @Override
            public void setValue(final View view, final float translationX) {
                AnimatorProxy.wrap(view).setTranslationX(translationX);
            }
        };
        PreHoneycombCompat.TRANSLATION_Y = (Property<View, Float>)new FloatProperty<View>("translationY") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getTranslationY();
            }
            
            @Override
            public void setValue(final View view, final float translationY) {
                AnimatorProxy.wrap(view).setTranslationY(translationY);
            }
        };
        PreHoneycombCompat.ROTATION = (Property<View, Float>)new FloatProperty<View>("rotation") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getRotation();
            }
            
            @Override
            public void setValue(final View view, final float rotation) {
                AnimatorProxy.wrap(view).setRotation(rotation);
            }
        };
        PreHoneycombCompat.ROTATION_X = (Property<View, Float>)new FloatProperty<View>("rotationX") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getRotationX();
            }
            
            @Override
            public void setValue(final View view, final float rotationX) {
                AnimatorProxy.wrap(view).setRotationX(rotationX);
            }
        };
        PreHoneycombCompat.ROTATION_Y = (Property<View, Float>)new FloatProperty<View>("rotationY") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getRotationY();
            }
            
            @Override
            public void setValue(final View view, final float rotationY) {
                AnimatorProxy.wrap(view).setRotationY(rotationY);
            }
        };
        PreHoneycombCompat.SCALE_X = (Property<View, Float>)new FloatProperty<View>("scaleX") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getScaleX();
            }
            
            @Override
            public void setValue(final View view, final float scaleX) {
                AnimatorProxy.wrap(view).setScaleX(scaleX);
            }
        };
        PreHoneycombCompat.SCALE_Y = (Property<View, Float>)new FloatProperty<View>("scaleY") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getScaleY();
            }
            
            @Override
            public void setValue(final View view, final float scaleY) {
                AnimatorProxy.wrap(view).setScaleY(scaleY);
            }
        };
        PreHoneycombCompat.SCROLL_X = (Property<View, Integer>)new IntProperty<View>("scrollX") {
            public Integer get(final View view) {
                return AnimatorProxy.wrap(view).getScrollX();
            }
            
            @Override
            public void setValue(final View view, final int scrollX) {
                AnimatorProxy.wrap(view).setScrollX(scrollX);
            }
        };
        PreHoneycombCompat.SCROLL_Y = (Property<View, Integer>)new IntProperty<View>("scrollY") {
            public Integer get(final View view) {
                return AnimatorProxy.wrap(view).getScrollY();
            }
            
            @Override
            public void setValue(final View view, final int scrollY) {
                AnimatorProxy.wrap(view).setScrollY(scrollY);
            }
        };
        PreHoneycombCompat.X = (Property<View, Float>)new FloatProperty<View>("x") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getX();
            }
            
            @Override
            public void setValue(final View view, final float x) {
                AnimatorProxy.wrap(view).setX(x);
            }
        };
        PreHoneycombCompat.Y = (Property<View, Float>)new FloatProperty<View>("y") {
            public Float get(final View view) {
                return AnimatorProxy.wrap(view).getY();
            }
            
            @Override
            public void setValue(final View view, final float y) {
                AnimatorProxy.wrap(view).setY(y);
            }
        };
    }
    
    private PreHoneycombCompat() {
    }
}
