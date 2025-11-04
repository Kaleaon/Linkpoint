// 
// Decompiled by Procyon v0.6.0
// 

package uk.co.senab.photoview;

import android.content.Context;
import uk.co.senab.photoview.scrollerproxy.ScrollerProxy;
import android.annotation.SuppressLint;
import android.view.ViewParent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import uk.co.senab.photoview.log.LogManager;
import android.graphics.Matrix$ScaleToFit;
import android.graphics.drawable.Drawable;
import android.view.ViewTreeObserver;
import android.view.GestureDetector$OnDoubleTapListener;
import android.view.GestureDetector$OnGestureListener;
import android.view.View;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.GestureDetector$SimpleOnGestureListener;
import uk.co.senab.photoview.gestures.VersionedGestureDetector;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.util.Log;
import android.widget.ImageView$ScaleType;
import android.view.View$OnLongClickListener;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import java.lang.ref.WeakReference;
import android.view.GestureDetector;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import uk.co.senab.photoview.gestures.OnGestureListener;
import android.view.View$OnTouchListener;

public class PhotoViewAttacher implements IPhotoView, View$OnTouchListener, OnGestureListener, ViewTreeObserver$OnGlobalLayoutListener
{
    private static final boolean DEBUG;
    static final int EDGE_BOTH = 2;
    static final int EDGE_LEFT = 0;
    static final int EDGE_NONE = -1;
    static final int EDGE_RIGHT = 1;
    private static final String LOG_TAG = "PhotoViewAttacher";
    static int SINGLE_TOUCH;
    int ZOOM_DURATION;
    private boolean mAllowParentInterceptOnEdge;
    private final Matrix mBaseMatrix;
    private float mBaseRotation;
    private boolean mBlockParentIntercept;
    private FlingRunnable mCurrentFlingRunnable;
    private final RectF mDisplayRect;
    private final Matrix mDrawMatrix;
    private GestureDetector mGestureDetector;
    private WeakReference<ImageView> mImageView;
    private Interpolator mInterpolator;
    private int mIvBottom;
    private int mIvLeft;
    private int mIvRight;
    private int mIvTop;
    private View$OnLongClickListener mLongClickListener;
    private OnMatrixChangedListener mMatrixChangeListener;
    private final float[] mMatrixValues;
    private float mMaxScale;
    private float mMidScale;
    private float mMinScale;
    private OnPhotoTapListener mPhotoTapListener;
    private OnScaleChangeListener mScaleChangeListener;
    private uk.co.senab.photoview.gestures.GestureDetector mScaleDragDetector;
    private ImageView$ScaleType mScaleType;
    private int mScrollEdge;
    private OnSingleFlingListener mSingleFlingListener;
    private final Matrix mSuppMatrix;
    private OnViewTapListener mViewTapListener;
    private boolean mZoomEnabled;
    
    static {
        DEBUG = Log.isLoggable("PhotoViewAttacher", 3);
        PhotoViewAttacher.SINGLE_TOUCH = 1;
    }
    
    public PhotoViewAttacher(final ImageView imageView) {
        this(imageView, true);
    }
    
    public PhotoViewAttacher(final ImageView imageView, final boolean zoomable) {
        this.mInterpolator = (Interpolator)new AccelerateDecelerateInterpolator();
        this.ZOOM_DURATION = 200;
        this.mMinScale = 1.0f;
        this.mMidScale = 1.75f;
        this.mMaxScale = 3.0f;
        this.mAllowParentInterceptOnEdge = true;
        this.mBlockParentIntercept = false;
        this.mBaseMatrix = new Matrix();
        this.mDrawMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mDisplayRect = new RectF();
        this.mMatrixValues = new float[9];
        this.mScrollEdge = 2;
        this.mScaleType = ImageView$ScaleType.FIT_CENTER;
        this.mImageView = new WeakReference<ImageView>(imageView);
        imageView.setDrawingCacheEnabled(true);
        imageView.setOnTouchListener((View$OnTouchListener)this);
        final ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
        }
        setImageViewScaleTypeMatrix(imageView);
        if (!imageView.isInEditMode()) {
            this.mScaleDragDetector = VersionedGestureDetector.newInstance(imageView.getContext(), this);
            (this.mGestureDetector = new GestureDetector(imageView.getContext(), (GestureDetector$OnGestureListener)new GestureDetector$SimpleOnGestureListener() {
                public boolean onFling(final MotionEvent motionEvent, final MotionEvent motionEvent2, final float n, final float n2) {
                    return PhotoViewAttacher.this.mSingleFlingListener != null && PhotoViewAttacher.this.getScale() <= 1.0f && (MotionEventCompat.getPointerCount(motionEvent) <= PhotoViewAttacher.SINGLE_TOUCH && MotionEventCompat.getPointerCount(motionEvent2) <= PhotoViewAttacher.SINGLE_TOUCH) && PhotoViewAttacher.this.mSingleFlingListener.onFling(motionEvent, motionEvent2, n, n2);
                }
                
                public void onLongPress(final MotionEvent motionEvent) {
                    if (PhotoViewAttacher.this.mLongClickListener != null) {
                        PhotoViewAttacher.this.mLongClickListener.onLongClick((View)PhotoViewAttacher.this.getImageView());
                    }
                }
            })).setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)new DefaultOnDoubleTapListener(this));
            this.mBaseRotation = 0.0f;
            this.setZoomable(zoomable);
        }
    }
    
    private void cancelFling() {
        if (this.mCurrentFlingRunnable != null) {
            this.mCurrentFlingRunnable.cancelFling();
            this.mCurrentFlingRunnable = null;
        }
    }
    
    private void checkAndDisplayMatrix() {
        if (this.checkMatrixBounds()) {
            this.setImageViewMatrix(this.getDrawMatrix());
        }
    }
    
    private void checkImageViewScaleType() {
        final ImageView imageView = this.getImageView();
        if (imageView != null && !(imageView instanceof IPhotoView) && !ImageView$ScaleType.MATRIX.equals((Object)imageView.getScaleType())) {
            throw new IllegalStateException("The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher. You should call setScaleType on the PhotoViewAttacher instead of on the ImageView");
        }
    }
    
    private boolean checkMatrixBounds() {
        float n = 0.0f;
        final ImageView imageView = this.getImageView();
        if (imageView == null) {
            return false;
        }
        final RectF displayRect = this.getDisplayRect(this.getDrawMatrix());
        if (displayRect != null) {
            final float height = displayRect.height();
            final float width = displayRect.width();
            final int imageViewHeight = this.getImageViewHeight(imageView);
            float n2 = 0.0f;
            if (height <= imageViewHeight) {
                switch (this.mScaleType) {
                    default: {
                        n2 = (imageViewHeight - height) / 2.0f - displayRect.top;
                        break;
                    }
                    case FIT_START: {
                        n2 = -displayRect.top;
                        break;
                    }
                    case FIT_END: {
                        n2 = imageViewHeight - height - displayRect.top;
                        break;
                    }
                }
            }
            else if (displayRect.top > 0.0f) {
                n2 = -displayRect.top;
            }
            else if (displayRect.bottom < imageViewHeight) {
                n2 = imageViewHeight - displayRect.bottom;
            }
            else {
                n2 = 0.0f;
            }
            final int imageViewWidth = this.getImageViewWidth(imageView);
            if (width <= imageViewWidth) {
                switch (this.mScaleType) {
                    default: {
                        n = (imageViewWidth - width) / 2.0f - displayRect.left;
                        break;
                    }
                    case FIT_START: {
                        n = -displayRect.left;
                        break;
                    }
                    case FIT_END: {
                        n = imageViewWidth - width - displayRect.left;
                        break;
                    }
                }
                this.mScrollEdge = 2;
            }
            else if (displayRect.left > 0.0f) {
                this.mScrollEdge = 0;
                n = -displayRect.left;
            }
            else if (displayRect.right < imageViewWidth) {
                n = imageViewWidth - displayRect.right;
                this.mScrollEdge = 1;
            }
            else {
                this.mScrollEdge = -1;
            }
            this.mSuppMatrix.postTranslate(n, n2);
            return true;
        }
        return false;
    }
    
    private static void checkZoomLevels(final float n, final float n2, final float n3) {
        if (n >= n2) {
            throw new IllegalArgumentException("Minimum zoom has to be less than Medium zoom. Call setMinimumZoom() with a more appropriate value");
        }
        if (n2 >= n3) {
            throw new IllegalArgumentException("Medium zoom has to be less than Maximum zoom. Call setMaximumZoom() with a more appropriate value");
        }
    }
    
    private RectF getDisplayRect(final Matrix matrix) {
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                this.mDisplayRect.set(0.0f, 0.0f, (float)drawable.getIntrinsicWidth(), (float)drawable.getIntrinsicHeight());
                matrix.mapRect(this.mDisplayRect);
                return this.mDisplayRect;
            }
        }
        return null;
    }
    
    private Matrix getDrawMatrix() {
        this.mDrawMatrix.set(this.mBaseMatrix);
        this.mDrawMatrix.postConcat(this.mSuppMatrix);
        return this.mDrawMatrix;
    }
    
    private int getImageViewHeight(final ImageView imageView) {
        if (imageView != null) {
            return imageView.getHeight() - imageView.getPaddingTop() - imageView.getPaddingBottom();
        }
        return 0;
    }
    
    private int getImageViewWidth(final ImageView imageView) {
        if (imageView != null) {
            return imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
        }
        return 0;
    }
    
    private float getValue(final Matrix matrix, final int n) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[n];
    }
    
    private static boolean hasDrawable(final ImageView imageView) {
        return imageView != null && imageView.getDrawable() != null;
    }
    
    private static boolean isSupportedScaleType(final ImageView$ScaleType imageView$ScaleType) {
        if (imageView$ScaleType == null) {
            return false;
        }
        switch (imageView$ScaleType) {
            default: {
                return true;
            }
            case MATRIX: {
                throw new IllegalArgumentException(imageView$ScaleType.name() + " is not supported in PhotoView");
            }
        }
    }
    
    private void resetMatrix() {
        this.mSuppMatrix.reset();
        this.setRotationBy(this.mBaseRotation);
        this.setImageViewMatrix(this.getDrawMatrix());
        this.checkMatrixBounds();
    }
    
    private void setImageViewMatrix(final Matrix imageMatrix) {
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            this.checkImageViewScaleType();
            imageView.setImageMatrix(imageMatrix);
            if (this.mMatrixChangeListener != null) {
                final RectF displayRect = this.getDisplayRect(imageMatrix);
                if (displayRect != null) {
                    this.mMatrixChangeListener.onMatrixChanged(displayRect);
                }
            }
        }
    }
    
    private static void setImageViewScaleTypeMatrix(final ImageView imageView) {
        if (imageView != null && !(imageView instanceof IPhotoView) && !ImageView$ScaleType.MATRIX.equals((Object)imageView.getScaleType())) {
            imageView.setScaleType(ImageView$ScaleType.MATRIX);
        }
    }
    
    private void updateBaseMatrix(final Drawable drawable) {
        final ImageView imageView = this.getImageView();
        if (imageView != null && drawable != null) {
            final float n = (float)this.getImageViewWidth(imageView);
            final float n2 = (float)this.getImageViewHeight(imageView);
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            final int intrinsicHeight = drawable.getIntrinsicHeight();
            this.mBaseMatrix.reset();
            final float n3 = n / intrinsicWidth;
            final float n4 = n2 / intrinsicHeight;
            if (this.mScaleType != ImageView$ScaleType.CENTER) {
                if (this.mScaleType != ImageView$ScaleType.CENTER_CROP) {
                    if (this.mScaleType != ImageView$ScaleType.CENTER_INSIDE) {
                        RectF rectF = new RectF(0.0f, 0.0f, (float)intrinsicWidth, (float)intrinsicHeight);
                        final RectF rectF2 = new RectF(0.0f, 0.0f, n, n2);
                        if ((int)this.mBaseRotation % 180 != 0) {
                            rectF = new RectF(0.0f, 0.0f, (float)intrinsicHeight, (float)intrinsicWidth);
                        }
                        switch (this.mScaleType) {
                            case FIT_CENTER: {
                                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix$ScaleToFit.CENTER);
                                break;
                            }
                            case FIT_START: {
                                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix$ScaleToFit.START);
                                break;
                            }
                            case FIT_END: {
                                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix$ScaleToFit.END);
                                break;
                            }
                            case FIT_XY: {
                                this.mBaseMatrix.setRectToRect(rectF, rectF2, Matrix$ScaleToFit.FILL);
                                break;
                            }
                        }
                    }
                    else {
                        final float min = Math.min(1.0f, Math.min(n3, n4));
                        this.mBaseMatrix.postScale(min, min);
                        this.mBaseMatrix.postTranslate((n - intrinsicWidth * min) / 2.0f, (n2 - min * intrinsicHeight) / 2.0f);
                    }
                }
                else {
                    final float max = Math.max(n3, n4);
                    this.mBaseMatrix.postScale(max, max);
                    this.mBaseMatrix.postTranslate((n - intrinsicWidth * max) / 2.0f, (n2 - max * intrinsicHeight) / 2.0f);
                }
            }
            else {
                this.mBaseMatrix.postTranslate((n - intrinsicWidth) / 2.0f, (n2 - intrinsicHeight) / 2.0f);
            }
            this.resetMatrix();
        }
    }
    
    @Override
    public boolean canZoom() {
        return this.mZoomEnabled;
    }
    
    public void cleanup() {
        if (this.mImageView != null) {
            final ImageView imageView = this.mImageView.get();
            if (imageView != null) {
                final ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
                if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
                }
                imageView.setOnTouchListener((View$OnTouchListener)null);
                this.cancelFling();
            }
            if (this.mGestureDetector != null) {
                this.mGestureDetector.setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)null);
            }
            this.mMatrixChangeListener = null;
            this.mPhotoTapListener = null;
            this.mViewTapListener = null;
            this.mImageView = null;
        }
    }
    
    @Override
    public void getDisplayMatrix(final Matrix matrix) {
        matrix.set(this.getDrawMatrix());
    }
    
    @Override
    public RectF getDisplayRect() {
        this.checkMatrixBounds();
        return this.getDisplayRect(this.getDrawMatrix());
    }
    
    @Override
    public IPhotoView getIPhotoViewImplementation() {
        return this;
    }
    
    public Matrix getImageMatrix() {
        return this.mDrawMatrix;
    }
    
    public ImageView getImageView() {
        ImageView imageView = null;
        if (this.mImageView != null) {
            imageView = this.mImageView.get();
        }
        if (imageView == null) {
            this.cleanup();
            LogManager.getLogger().i("PhotoViewAttacher", "ImageView no longer exists. You should not use this PhotoViewAttacher any more.");
        }
        return imageView;
    }
    
    @Override
    public float getMaximumScale() {
        return this.mMaxScale;
    }
    
    @Override
    public float getMediumScale() {
        return this.mMidScale;
    }
    
    @Override
    public float getMinimumScale() {
        return this.mMinScale;
    }
    
    @Nullable
    OnPhotoTapListener getOnPhotoTapListener() {
        return this.mPhotoTapListener;
    }
    
    @Nullable
    OnViewTapListener getOnViewTapListener() {
        return this.mViewTapListener;
    }
    
    @Override
    public float getScale() {
        return (float)Math.sqrt((float)Math.pow(this.getValue(this.mSuppMatrix, 0), 2.0) + (float)Math.pow(this.getValue(this.mSuppMatrix, 3), 2.0));
    }
    
    @Override
    public ImageView$ScaleType getScaleType() {
        return this.mScaleType;
    }
    
    public void getSuppMatrix(final Matrix matrix) {
        matrix.set(this.mSuppMatrix);
    }
    
    @Override
    public Bitmap getVisibleRectangleBitmap() {
        Bitmap drawingCache = null;
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            drawingCache = imageView.getDrawingCache();
        }
        return drawingCache;
    }
    
    public void onDrag(final float f, final float f2) {
        if (!this.mScaleDragDetector.isScaling()) {
            if (PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d("PhotoViewAttacher", String.format("onDrag: dx: %.2f. dy: %.2f", f, f2));
            }
            final ImageView imageView = this.getImageView();
            this.mSuppMatrix.postTranslate(f, f2);
            this.checkAndDisplayMatrix();
            final ViewParent parent = imageView.getParent();
            if (this.mAllowParentInterceptOnEdge && !this.mScaleDragDetector.isScaling() && !this.mBlockParentIntercept) {
                Label_0120: {
                    if (this.mScrollEdge != 2) {
                        if (this.mScrollEdge == 0) {
                            int n;
                            if (f >= 1.0f) {
                                n = 1;
                            }
                            else {
                                n = 0;
                            }
                            if (n != 0) {
                                break Label_0120;
                            }
                        }
                        if (this.mScrollEdge != 1 || f > -1.0f) {
                            return;
                        }
                    }
                }
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
            else if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }
    
    public void onFling(final float f, final float f2, final float f3, final float f4) {
        if (PhotoViewAttacher.DEBUG) {
            LogManager.getLogger().d("PhotoViewAttacher", "onFling. sX: " + f + " sY: " + f2 + " Vx: " + f3 + " Vy: " + f4);
        }
        final ImageView imageView = this.getImageView();
        (this.mCurrentFlingRunnable = new FlingRunnable(imageView.getContext())).fling(this.getImageViewWidth(imageView), this.getImageViewHeight(imageView), (int)f3, (int)f4);
        imageView.post((Runnable)this.mCurrentFlingRunnable);
    }
    
    public void onGlobalLayout() {
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            if (!this.mZoomEnabled) {
                this.updateBaseMatrix(imageView.getDrawable());
            }
            else {
                final int top = imageView.getTop();
                final int right = imageView.getRight();
                final int bottom = imageView.getBottom();
                final int left = imageView.getLeft();
                if (top != this.mIvTop || bottom != this.mIvBottom || left != this.mIvLeft || right != this.mIvRight) {
                    this.updateBaseMatrix(imageView.getDrawable());
                    this.mIvTop = top;
                    this.mIvRight = right;
                    this.mIvBottom = bottom;
                    this.mIvLeft = left;
                }
            }
        }
    }
    
    public void onScale(final float f, final float f2, final float f3) {
        final int n = 1;
        if (PhotoViewAttacher.DEBUG) {
            LogManager.getLogger().d("PhotoViewAttacher", String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", f, f2, f3));
        }
        boolean b;
        if (this.getScale() < this.mMaxScale) {
            b = true;
        }
        else {
            b = false;
        }
        if (b || f < 1.0f) {
            int n2;
            if (this.getScale() > this.mMinScale) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0 || f > 1.0f) {
                if (this.mScaleChangeListener != null) {
                    this.mScaleChangeListener.onScaleChange(f, f2, f3);
                }
                this.mSuppMatrix.postScale(f, f, f2, f3);
                this.checkAndDisplayMatrix();
            }
        }
    }
    
    @SuppressLint({ "ClickableViewAccessibility" })
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final boolean b = false;
        boolean b2 = false;
        if (this.mZoomEnabled && hasDrawable((ImageView)view)) {
            final ViewParent parent = view.getParent();
            switch (motionEvent.getAction()) {
                default: {
                    b2 = false;
                    break;
                }
                case 0: {
                    if (parent == null) {
                        LogManager.getLogger().i("PhotoViewAttacher", "onTouch getParent() returned null");
                    }
                    else {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    this.cancelFling();
                    b2 = false;
                    break;
                }
                case 1:
                case 3: {
                    if (this.getScale() >= this.mMinScale) {
                        b2 = false;
                        break;
                    }
                    final RectF displayRect = this.getDisplayRect();
                    if (displayRect == null) {
                        b2 = false;
                        break;
                    }
                    view.post((Runnable)new AnimatedZoomRunnable(this.getScale(), this.mMinScale, displayRect.centerX(), displayRect.centerY()));
                    b2 = true;
                    break;
                }
            }
            if (this.mScaleDragDetector != null) {
                final boolean scaling = this.mScaleDragDetector.isScaling();
                final boolean dragging = this.mScaleDragDetector.isDragging();
                final boolean onTouchEvent = this.mScaleDragDetector.onTouchEvent(motionEvent);
                int n;
                if (!scaling && !this.mScaleDragDetector.isScaling()) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                final boolean b3 = !dragging && !this.mScaleDragDetector.isDragging();
                boolean mBlockParentIntercept;
                if (n == 0) {
                    mBlockParentIntercept = b;
                }
                else {
                    mBlockParentIntercept = b;
                    if (b3) {
                        mBlockParentIntercept = true;
                    }
                }
                this.mBlockParentIntercept = mBlockParentIntercept;
                b2 = onTouchEvent;
            }
            if (this.mGestureDetector != null && this.mGestureDetector.onTouchEvent(motionEvent)) {
                b2 = true;
            }
        }
        return b2;
    }
    
    @Override
    public void setAllowParentInterceptOnEdge(final boolean mAllowParentInterceptOnEdge) {
        this.mAllowParentInterceptOnEdge = mAllowParentInterceptOnEdge;
    }
    
    public void setBaseRotation(final float n) {
        this.mBaseRotation = n % 360.0f;
        this.update();
        this.setRotationBy(this.mBaseRotation);
        this.checkAndDisplayMatrix();
    }
    
    @Override
    public boolean setDisplayMatrix(final Matrix matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        final ImageView imageView = this.getImageView();
        if (imageView == null) {
            return false;
        }
        if (imageView.getDrawable() != null) {
            this.mSuppMatrix.set(matrix);
            this.setImageViewMatrix(this.getDrawMatrix());
            this.checkMatrixBounds();
            return true;
        }
        return false;
    }
    
    @Override
    public void setMaximumScale(final float mMaxScale) {
        checkZoomLevels(this.mMinScale, this.mMidScale, mMaxScale);
        this.mMaxScale = mMaxScale;
    }
    
    @Override
    public void setMediumScale(final float mMidScale) {
        checkZoomLevels(this.mMinScale, mMidScale, this.mMaxScale);
        this.mMidScale = mMidScale;
    }
    
    @Override
    public void setMinimumScale(final float mMinScale) {
        checkZoomLevels(mMinScale, this.mMidScale, this.mMaxScale);
        this.mMinScale = mMinScale;
    }
    
    @Override
    public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
        if (onDoubleTapListener == null) {
            this.mGestureDetector.setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)new DefaultOnDoubleTapListener(this));
        }
        else {
            this.mGestureDetector.setOnDoubleTapListener(onDoubleTapListener);
        }
    }
    
    @Override
    public void setOnLongClickListener(final View$OnLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
    }
    
    @Override
    public void setOnMatrixChangeListener(final OnMatrixChangedListener mMatrixChangeListener) {
        this.mMatrixChangeListener = mMatrixChangeListener;
    }
    
    @Override
    public void setOnPhotoTapListener(final OnPhotoTapListener mPhotoTapListener) {
        this.mPhotoTapListener = mPhotoTapListener;
    }
    
    @Override
    public void setOnScaleChangeListener(final OnScaleChangeListener mScaleChangeListener) {
        this.mScaleChangeListener = mScaleChangeListener;
    }
    
    @Override
    public void setOnSingleFlingListener(final OnSingleFlingListener mSingleFlingListener) {
        this.mSingleFlingListener = mSingleFlingListener;
    }
    
    @Override
    public void setOnViewTapListener(final OnViewTapListener mViewTapListener) {
        this.mViewTapListener = mViewTapListener;
    }
    
    @Override
    public void setRotationBy(final float n) {
        this.mSuppMatrix.postRotate(n % 360.0f);
        this.checkAndDisplayMatrix();
    }
    
    @Override
    public void setRotationTo(final float n) {
        this.mSuppMatrix.setRotate(n % 360.0f);
        this.checkAndDisplayMatrix();
    }
    
    @Override
    public void setScale(final float n) {
        this.setScale(n, false);
    }
    
    @Override
    public void setScale(final float n, final float n2, final float n3, final boolean b) {
        boolean b2 = false;
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            if (n < this.mMinScale) {
                b2 = true;
            }
            if (b2 || n > this.mMaxScale) {
                LogManager.getLogger().i("PhotoViewAttacher", "Scale must be within the range of minScale and maxScale");
                return;
            }
            if (!b) {
                this.mSuppMatrix.setScale(n, n, n2, n3);
                this.checkAndDisplayMatrix();
            }
            else {
                imageView.post((Runnable)new AnimatedZoomRunnable(this.getScale(), n, n2, n3));
            }
        }
    }
    
    @Override
    public void setScale(final float n, final boolean b) {
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            this.setScale(n, (float)(imageView.getRight() / 2), (float)(imageView.getBottom() / 2), b);
        }
    }
    
    @Override
    public void setScaleLevels(final float mMinScale, final float mMidScale, final float mMaxScale) {
        checkZoomLevels(mMinScale, mMidScale, mMaxScale);
        this.mMinScale = mMinScale;
        this.mMidScale = mMidScale;
        this.mMaxScale = mMaxScale;
    }
    
    @Override
    public void setScaleType(final ImageView$ScaleType mScaleType) {
        if (isSupportedScaleType(mScaleType) && mScaleType != this.mScaleType) {
            this.mScaleType = mScaleType;
            this.update();
        }
    }
    
    public void setZoomInterpolator(final Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }
    
    @Override
    public void setZoomTransitionDuration(int zoom_DURATION) {
        if (zoom_DURATION < 0) {
            zoom_DURATION = 200;
        }
        this.ZOOM_DURATION = zoom_DURATION;
    }
    
    @Override
    public void setZoomable(final boolean mZoomEnabled) {
        this.mZoomEnabled = mZoomEnabled;
        this.update();
    }
    
    public void update() {
        final ImageView imageView = this.getImageView();
        if (imageView != null) {
            if (!this.mZoomEnabled) {
                this.resetMatrix();
            }
            else {
                setImageViewScaleTypeMatrix(imageView);
                this.updateBaseMatrix(imageView.getDrawable());
            }
        }
    }
    
    private class AnimatedZoomRunnable implements Runnable
    {
        private final float mFocalX;
        private final float mFocalY;
        private final long mStartTime;
        private final float mZoomEnd;
        private final float mZoomStart;
        
        public AnimatedZoomRunnable(final float mZoomStart, final float mZoomEnd, final float mFocalX, final float mFocalY) {
            this.mFocalX = mFocalX;
            this.mFocalY = mFocalY;
            this.mStartTime = System.currentTimeMillis();
            this.mZoomStart = mZoomStart;
            this.mZoomEnd = mZoomEnd;
        }
        
        private float interpolate() {
            return PhotoViewAttacher.this.mInterpolator.getInterpolation(Math.min(1.0f, (System.currentTimeMillis() - this.mStartTime) * 1.0f / PhotoViewAttacher.this.ZOOM_DURATION));
        }
        
        @Override
        public void run() {
            final ImageView imageView = PhotoViewAttacher.this.getImageView();
            if (imageView != null) {
                final float interpolate = this.interpolate();
                PhotoViewAttacher.this.onScale((this.mZoomStart + (this.mZoomEnd - this.mZoomStart) * interpolate) / PhotoViewAttacher.this.getScale(), this.mFocalX, this.mFocalY);
                if (interpolate < 1.0f) {
                    Compat.postOnAnimation((View)imageView, this);
                }
            }
        }
    }
    
    private class FlingRunnable implements Runnable
    {
        private int mCurrentX;
        private int mCurrentY;
        private final ScrollerProxy mScroller;
        
        public FlingRunnable(final Context context) {
            this.mScroller = ScrollerProxy.getScroller(context);
        }
        
        public void cancelFling() {
            if (PhotoViewAttacher.DEBUG) {
                LogManager.getLogger().d("PhotoViewAttacher", "Cancel Fling");
            }
            this.mScroller.forceFinished(true);
        }
        
        public void fling(int n, int round, final int n2, final int n3) {
            final RectF displayRect = PhotoViewAttacher.this.getDisplayRect();
            if (displayRect != null) {
                final int round2 = Math.round(-displayRect.left);
                int round3;
                if (n < displayRect.width()) {
                    round3 = Math.round(displayRect.width() - n);
                    n = 0;
                }
                else {
                    round3 = round2;
                    n = round2;
                }
                final int round4 = Math.round(-displayRect.top);
                int n4;
                if (round < displayRect.height()) {
                    round = Math.round(displayRect.height() - round);
                    n4 = 0;
                }
                else {
                    round = round4;
                    n4 = round4;
                }
                this.mCurrentX = round2;
                this.mCurrentY = round4;
                if (PhotoViewAttacher.DEBUG) {
                    LogManager.getLogger().d("PhotoViewAttacher", "fling. StartX:" + round2 + " StartY:" + round4 + " MaxX:" + round3 + " MaxY:" + round);
                }
                if (round2 != round3 || round4 != round) {
                    this.mScroller.fling(round2, round4, n2, n3, n, round3, n4, round, 0, 0);
                }
            }
        }
        
        @Override
        public void run() {
            if (!this.mScroller.isFinished()) {
                final ImageView imageView = PhotoViewAttacher.this.getImageView();
                if (imageView != null && this.mScroller.computeScrollOffset()) {
                    final int currX = this.mScroller.getCurrX();
                    final int currY = this.mScroller.getCurrY();
                    if (PhotoViewAttacher.DEBUG) {
                        LogManager.getLogger().d("PhotoViewAttacher", "fling run(). CurrentX:" + this.mCurrentX + " CurrentY:" + this.mCurrentY + " NewX:" + currX + " NewY:" + currY);
                    }
                    PhotoViewAttacher.this.mSuppMatrix.postTranslate((float)(this.mCurrentX - currX), (float)(this.mCurrentY - currY));
                    PhotoViewAttacher.this.setImageViewMatrix(PhotoViewAttacher.this.getDrawMatrix());
                    this.mCurrentX = currX;
                    this.mCurrentY = currY;
                    Compat.postOnAnimation((View)imageView, this);
                }
            }
        }
    }
    
    public interface OnMatrixChangedListener
    {
        void onMatrixChanged(final RectF p0);
    }
    
    public interface OnPhotoTapListener
    {
        void onOutsidePhotoTap();
        
        void onPhotoTap(final View p0, final float p1, final float p2);
    }
    
    public interface OnScaleChangeListener
    {
        void onScaleChange(final float p0, final float p1, final float p2);
    }
    
    public interface OnSingleFlingListener
    {
        boolean onFling(final MotionEvent p0, final MotionEvent p1, final float p2, final float p3);
    }
    
    public interface OnViewTapListener
    {
        void onViewTap(final View p0, final float p1, final float p2);
    }
}
