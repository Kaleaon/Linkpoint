// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview;

import android.view.View$OnLongClickListener;
import android.view.GestureDetector$OnDoubleTapListener;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView$ScaleType;
import android.widget.ImageView;

public class PhotoView extends ImageView implements IPhotoView
{
    private PhotoViewAttacher mAttacher;
    private ImageView$ScaleType mPendingScaleType;
    
    public PhotoView(final Context context) {
        this(context, null);
    }
    
    public PhotoView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public PhotoView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        super.setScaleType(ImageView$ScaleType.MATRIX);
        this.init();
    }
    
    public boolean canZoom() {
        return this.mAttacher.canZoom();
    }
    
    public void getDisplayMatrix(final Matrix matrix) {
        this.mAttacher.getDisplayMatrix(matrix);
    }
    
    public RectF getDisplayRect() {
        return this.mAttacher.getDisplayRect();
    }
    
    public IPhotoView getIPhotoViewImplementation() {
        return this.mAttacher;
    }
    
    public Matrix getImageMatrix() {
        return this.mAttacher.getImageMatrix();
    }
    
    public float getMaximumScale() {
        return this.mAttacher.getMaximumScale();
    }
    
    public float getMediumScale() {
        return this.mAttacher.getMediumScale();
    }
    
    public float getMinimumScale() {
        return this.mAttacher.getMinimumScale();
    }
    
    public float getScale() {
        return this.mAttacher.getScale();
    }
    
    public ImageView$ScaleType getScaleType() {
        return this.mAttacher.getScaleType();
    }
    
    public Bitmap getVisibleRectangleBitmap() {
        return this.mAttacher.getVisibleRectangleBitmap();
    }
    
    protected void init() {
        if (this.mAttacher == null || this.mAttacher.getImageView() == null) {
            this.mAttacher = new PhotoViewAttacher(this);
        }
        if (this.mPendingScaleType != null) {
            this.setScaleType(this.mPendingScaleType);
            this.mPendingScaleType = null;
        }
    }
    
    protected void onAttachedToWindow() {
        this.init();
        super.onAttachedToWindow();
    }
    
    protected void onDetachedFromWindow() {
        this.mAttacher.cleanup();
        this.mAttacher = null;
        super.onDetachedFromWindow();
    }
    
    public void setAllowParentInterceptOnEdge(final boolean allowParentInterceptOnEdge) {
        this.mAttacher.setAllowParentInterceptOnEdge(allowParentInterceptOnEdge);
    }
    
    public boolean setDisplayMatrix(final Matrix displayMatrix) {
        return this.mAttacher.setDisplayMatrix(displayMatrix);
    }
    
    protected boolean setFrame(final int n, final int n2, final int n3, final int n4) {
        final boolean setFrame = super.setFrame(n, n2, n3, n4);
        if (this.mAttacher != null) {
            this.mAttacher.update();
        }
        return setFrame;
    }
    
    public void setImageDrawable(final Drawable imageDrawable) {
        super.setImageDrawable(imageDrawable);
        if (this.mAttacher != null) {
            this.mAttacher.update();
        }
    }
    
    public void setImageResource(final int imageResource) {
        super.setImageResource(imageResource);
        if (this.mAttacher != null) {
            this.mAttacher.update();
        }
    }
    
    public void setImageURI(final Uri imageURI) {
        super.setImageURI(imageURI);
        if (this.mAttacher != null) {
            this.mAttacher.update();
        }
    }
    
    public void setMaximumScale(final float maximumScale) {
        this.mAttacher.setMaximumScale(maximumScale);
    }
    
    public void setMediumScale(final float mediumScale) {
        this.mAttacher.setMediumScale(mediumScale);
    }
    
    public void setMinimumScale(final float minimumScale) {
        this.mAttacher.setMinimumScale(minimumScale);
    }
    
    public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
        this.mAttacher.setOnDoubleTapListener(onDoubleTapListener);
    }
    
    public void setOnLongClickListener(final View$OnLongClickListener onLongClickListener) {
        this.mAttacher.setOnLongClickListener(onLongClickListener);
    }
    
    public void setOnMatrixChangeListener(final PhotoViewAttacher.OnMatrixChangedListener onMatrixChangeListener) {
        this.mAttacher.setOnMatrixChangeListener(onMatrixChangeListener);
    }
    
    public void setOnPhotoTapListener(final PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener) {
        this.mAttacher.setOnPhotoTapListener(onPhotoTapListener);
    }
    
    public void setOnScaleChangeListener(final PhotoViewAttacher.OnScaleChangeListener onScaleChangeListener) {
        this.mAttacher.setOnScaleChangeListener(onScaleChangeListener);
    }
    
    public void setOnSingleFlingListener(final PhotoViewAttacher.OnSingleFlingListener onSingleFlingListener) {
        this.mAttacher.setOnSingleFlingListener(onSingleFlingListener);
    }
    
    public void setOnViewTapListener(final PhotoViewAttacher.OnViewTapListener onViewTapListener) {
        this.mAttacher.setOnViewTapListener(onViewTapListener);
    }
    
    public void setRotationBy(final float rotationBy) {
        this.mAttacher.setRotationBy(rotationBy);
    }
    
    public void setRotationTo(final float rotationTo) {
        this.mAttacher.setRotationTo(rotationTo);
    }
    
    public void setScale(final float scale) {
        this.mAttacher.setScale(scale);
    }
    
    public void setScale(final float n, final float n2, final float n3, final boolean b) {
        this.mAttacher.setScale(n, n2, n3, b);
    }
    
    public void setScale(final float n, final boolean b) {
        this.mAttacher.setScale(n, b);
    }
    
    public void setScaleLevels(final float n, final float n2, final float n3) {
        this.mAttacher.setScaleLevels(n, n2, n3);
    }
    
    public void setScaleType(final ImageView$ScaleType imageView$ScaleType) {
        if (this.mAttacher == null) {
            this.mPendingScaleType = imageView$ScaleType;
        }
        else {
            this.mAttacher.setScaleType(imageView$ScaleType);
        }
    }
    
    public void setZoomTransitionDuration(final int zoomTransitionDuration) {
        this.mAttacher.setZoomTransitionDuration(zoomTransitionDuration);
    }
    
    public void setZoomable(final boolean zoomable) {
        this.mAttacher.setZoomable(zoomable);
    }
}
