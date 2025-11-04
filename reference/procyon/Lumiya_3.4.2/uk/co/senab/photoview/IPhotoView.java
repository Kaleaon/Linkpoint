// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview;

import android.view.View$OnLongClickListener;
import android.view.GestureDetector$OnDoubleTapListener;
import android.graphics.Bitmap;
import android.widget.ImageView$ScaleType;
import android.graphics.RectF;
import android.graphics.Matrix;

public interface IPhotoView
{
    public static final float DEFAULT_MAX_SCALE = 3.0f;
    public static final float DEFAULT_MID_SCALE = 1.75f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    public static final int DEFAULT_ZOOM_DURATION = 200;
    
    boolean canZoom();
    
    void getDisplayMatrix(final Matrix p0);
    
    RectF getDisplayRect();
    
    IPhotoView getIPhotoViewImplementation();
    
    float getMaximumScale();
    
    float getMediumScale();
    
    float getMinimumScale();
    
    float getScale();
    
    ImageView$ScaleType getScaleType();
    
    Bitmap getVisibleRectangleBitmap();
    
    void setAllowParentInterceptOnEdge(final boolean p0);
    
    boolean setDisplayMatrix(final Matrix p0);
    
    void setMaximumScale(final float p0);
    
    void setMediumScale(final float p0);
    
    void setMinimumScale(final float p0);
    
    void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener p0);
    
    void setOnLongClickListener(final View$OnLongClickListener p0);
    
    void setOnMatrixChangeListener(final PhotoViewAttacher.OnMatrixChangedListener p0);
    
    void setOnPhotoTapListener(final PhotoViewAttacher.OnPhotoTapListener p0);
    
    void setOnScaleChangeListener(final PhotoViewAttacher.OnScaleChangeListener p0);
    
    void setOnSingleFlingListener(final PhotoViewAttacher.OnSingleFlingListener p0);
    
    void setOnViewTapListener(final PhotoViewAttacher.OnViewTapListener p0);
    
    void setRotationBy(final float p0);
    
    void setRotationTo(final float p0);
    
    void setScale(final float p0);
    
    void setScale(final float p0, final float p1, final float p2, final boolean p3);
    
    void setScale(final float p0, final boolean p1);
    
    void setScaleLevels(final float p0, final float p1, final float p2);
    
    void setScaleType(final ImageView$ScaleType p0);
    
    void setZoomTransitionDuration(final int p0);
    
    void setZoomable(final boolean p0);
}
