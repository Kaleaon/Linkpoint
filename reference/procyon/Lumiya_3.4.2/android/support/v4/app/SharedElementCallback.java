// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.Map;
import java.util.List;
import android.content.Context;
import android.widget.ImageView$ScaleType;
import android.os.Bundle;
import android.widget.ImageView;
import android.os.Parcelable;
import android.graphics.RectF;
import android.view.View;
import android.graphics.Rect;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.Matrix;

public abstract class SharedElementCallback
{
    private static final String BUNDLE_SNAPSHOT_BITMAP = "sharedElement:snapshot:bitmap";
    private static final String BUNDLE_SNAPSHOT_IMAGE_MATRIX = "sharedElement:snapshot:imageMatrix";
    private static final String BUNDLE_SNAPSHOT_IMAGE_SCALETYPE = "sharedElement:snapshot:imageScaleType";
    private static int MAX_IMAGE_SIZE;
    private Matrix mTempMatrix;
    
    static {
        SharedElementCallback.MAX_IMAGE_SIZE = 1048576;
    }
    
    private static Bitmap createDrawableBitmap(final Drawable drawable) {
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return null;
        }
        final float min = Math.min(1.0f, SharedElementCallback.MAX_IMAGE_SIZE / (float)(intrinsicWidth * intrinsicHeight));
        if (drawable instanceof BitmapDrawable && min == 1.0f) {
            return ((BitmapDrawable)drawable).getBitmap();
        }
        final int n = (int)(intrinsicWidth * min);
        final int n2 = (int)(intrinsicHeight * min);
        final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final Rect bounds = drawable.getBounds();
        final int left = bounds.left;
        final int top = bounds.top;
        final int right = bounds.right;
        final int bottom = bounds.bottom;
        drawable.setBounds(0, 0, n, n2);
        drawable.draw(canvas);
        drawable.setBounds(left, top, right, bottom);
        return bitmap;
    }
    
    public Parcelable onCaptureSharedElementSnapshot(final View view, final Matrix matrix, final RectF rectF) {
        if (view instanceof ImageView) {
            final ImageView imageView = (ImageView)view;
            final Drawable drawable = imageView.getDrawable();
            final Drawable background = imageView.getBackground();
            if (drawable != null && background == null) {
                final Bitmap drawableBitmap = createDrawableBitmap(drawable);
                if (drawableBitmap != null) {
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable("sharedElement:snapshot:bitmap", (Parcelable)drawableBitmap);
                    bundle.putString("sharedElement:snapshot:imageScaleType", imageView.getScaleType().toString());
                    if (imageView.getScaleType() == ImageView$ScaleType.MATRIX) {
                        final Matrix imageMatrix = imageView.getImageMatrix();
                        final float[] array = new float[9];
                        imageMatrix.getValues(array);
                        bundle.putFloatArray("sharedElement:snapshot:imageMatrix", array);
                    }
                    return (Parcelable)bundle;
                }
            }
        }
        final int round = Math.round(rectF.width());
        final int round2 = Math.round(rectF.height());
        Parcelable parcelable;
        if (round > 0 && round2 > 0) {
            final float min = Math.min(1.0f, SharedElementCallback.MAX_IMAGE_SIZE / (float)(round * round2));
            final int n = (int)(round * min);
            final int n2 = (int)(round2 * min);
            if (this.mTempMatrix == null) {
                this.mTempMatrix = new Matrix();
            }
            this.mTempMatrix.set(matrix);
            this.mTempMatrix.postTranslate(-rectF.left, -rectF.top);
            this.mTempMatrix.postScale(min, min);
            final Bitmap bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            canvas.concat(this.mTempMatrix);
            view.draw(canvas);
            parcelable = (Parcelable)bitmap;
        }
        else {
            parcelable = null;
        }
        return parcelable;
    }
    
    public View onCreateSnapshotView(final Context context, final Parcelable parcelable) {
        Object o;
        if (!(parcelable instanceof Bundle)) {
            if (!(parcelable instanceof Bitmap)) {
                o = null;
            }
            else {
                final Bitmap imageBitmap = (Bitmap)parcelable;
                o = new ImageView(context);
                ((ImageView)o).setImageBitmap(imageBitmap);
            }
        }
        else {
            final Bundle bundle = (Bundle)parcelable;
            final Bitmap imageBitmap2 = (Bitmap)bundle.getParcelable("sharedElement:snapshot:bitmap");
            if (imageBitmap2 == null) {
                return null;
            }
            o = new ImageView(context);
            ((ImageView)o).setImageBitmap(imageBitmap2);
            ((ImageView)o).setScaleType(ImageView$ScaleType.valueOf(bundle.getString("sharedElement:snapshot:imageScaleType")));
            if (((ImageView)o).getScaleType() == ImageView$ScaleType.MATRIX) {
                final float[] floatArray = bundle.getFloatArray("sharedElement:snapshot:imageMatrix");
                final Matrix imageMatrix = new Matrix();
                imageMatrix.setValues(floatArray);
                ((ImageView)o).setImageMatrix(imageMatrix);
            }
        }
        return (View)o;
    }
    
    public void onMapSharedElements(final List<String> list, final Map<String, View> map) {
    }
    
    public void onRejectSharedElements(final List<View> list) {
    }
    
    public void onSharedElementEnd(final List<String> list, final List<View> list2, final List<View> list3) {
    }
    
    public void onSharedElementStart(final List<String> list, final List<View> list2, final List<View> list3) {
    }
    
    public void onSharedElementsArrived(final List<String> list, final List<View> list2, final OnSharedElementsReadyListener onSharedElementsReadyListener) {
        onSharedElementsReadyListener.onSharedElementsReady();
    }
    
    public interface OnSharedElementsReadyListener
    {
        void onSharedElementsReady();
    }
}
