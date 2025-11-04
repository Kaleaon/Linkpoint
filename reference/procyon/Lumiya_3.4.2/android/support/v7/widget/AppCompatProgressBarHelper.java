// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.util.AttributeSet;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.Shader;
import android.graphics.BitmapShader;
import android.graphics.Shader$TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.Drawable;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.widget.ProgressBar;
import android.graphics.Bitmap;

class AppCompatProgressBarHelper
{
    private static final int[] TINT_ATTRS;
    private Bitmap mSampleTile;
    private final ProgressBar mView;
    
    static {
        TINT_ATTRS = new int[] { 16843067, 16843068 };
    }
    
    AppCompatProgressBarHelper(final ProgressBar mView) {
        this.mView = mView;
    }
    
    private Shape getDrawableShape() {
        return (Shape)new RoundRectShape(new float[] { 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f }, (RectF)null, (float[])null);
    }
    
    private Drawable tileify(final Drawable drawable, final boolean b) {
        final int n = 0;
        if (!(drawable instanceof DrawableWrapper)) {
            if (drawable instanceof LayerDrawable) {
                final LayerDrawable layerDrawable = (LayerDrawable)drawable;
                final int numberOfLayers = layerDrawable.getNumberOfLayers();
                final Drawable[] array = new Drawable[numberOfLayers];
                for (int i = 0; i < numberOfLayers; ++i) {
                    final int id = layerDrawable.getId(i);
                    array[i] = this.tileify(layerDrawable.getDrawable(i), id == 16908301 || id == 16908303);
                }
                final LayerDrawable layerDrawable2 = new LayerDrawable(array);
                for (int j = n; j < numberOfLayers; ++j) {
                    layerDrawable2.setId(j, layerDrawable.getId(j));
                }
                return (Drawable)layerDrawable2;
            }
            if (drawable instanceof BitmapDrawable) {
                final BitmapDrawable bitmapDrawable = (BitmapDrawable)drawable;
                final Bitmap bitmap = bitmapDrawable.getBitmap();
                if (this.mSampleTile == null) {
                    this.mSampleTile = bitmap;
                }
                Object o = new ShapeDrawable(this.getDrawableShape());
                ((ShapeDrawable)o).getPaint().setShader((Shader)new BitmapShader(bitmap, Shader$TileMode.REPEAT, Shader$TileMode.CLAMP));
                ((ShapeDrawable)o).getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
                if (b) {
                    o = new ClipDrawable((Drawable)o, 3, 1);
                }
                return (Drawable)o;
            }
        }
        else {
            final Drawable wrappedDrawable = ((DrawableWrapper)drawable).getWrappedDrawable();
            if (wrappedDrawable != null) {
                ((DrawableWrapper)drawable).setWrappedDrawable(this.tileify(wrappedDrawable, b));
            }
        }
        return drawable;
    }
    
    private Drawable tileifyIndeterminate(Drawable drawable) {
        int i = 0;
        if (drawable instanceof AnimationDrawable) {
            final AnimationDrawable animationDrawable = (AnimationDrawable)drawable;
            final int numberOfFrames = animationDrawable.getNumberOfFrames();
            drawable = (Drawable)new AnimationDrawable();
            ((AnimationDrawable)drawable).setOneShot(animationDrawable.isOneShot());
            while (i < numberOfFrames) {
                final Drawable tileify = this.tileify(animationDrawable.getFrame(i), true);
                tileify.setLevel(10000);
                ((AnimationDrawable)drawable).addFrame(tileify, animationDrawable.getDuration(i));
                ++i;
            }
            ((AnimationDrawable)drawable).setLevel(10000);
        }
        return drawable;
    }
    
    Bitmap getSampleTime() {
        return this.mSampleTile;
    }
    
    void loadFromAttributes(final AttributeSet set, final int n) {
        final TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), set, AppCompatProgressBarHelper.TINT_ATTRS, n, 0);
        final Drawable drawableIfKnown = obtainStyledAttributes.getDrawableIfKnown(0);
        if (drawableIfKnown != null) {
            this.mView.setIndeterminateDrawable(this.tileifyIndeterminate(drawableIfKnown));
        }
        final Drawable drawableIfKnown2 = obtainStyledAttributes.getDrawableIfKnown(1);
        if (drawableIfKnown2 != null) {
            this.mView.setProgressDrawable(this.tileify(drawableIfKnown2, false));
        }
        obtainStyledAttributes.recycle();
    }
}
