// 
// Decompiled by Procyon v0.6.0
// 

package com.nineoldandroids.view.animation;

import android.view.animation.Transformation;
import android.os.Build$VERSION;
import java.lang.ref.WeakReference;
import android.graphics.Matrix;
import android.graphics.Camera;
import android.graphics.RectF;
import android.view.View;
import java.util.WeakHashMap;
import android.view.animation.Animation;

public final class AnimatorProxy extends Animation
{
    public static final boolean NEEDS_PROXY;
    private static final WeakHashMap<View, AnimatorProxy> PROXIES;
    private final RectF mAfter;
    private float mAlpha;
    private final RectF mBefore;
    private final Camera mCamera;
    private boolean mHasPivot;
    private float mPivotX;
    private float mPivotY;
    private float mRotationX;
    private float mRotationY;
    private float mRotationZ;
    private float mScaleX;
    private float mScaleY;
    private final Matrix mTempMatrix;
    private float mTranslationX;
    private float mTranslationY;
    private final WeakReference<View> mView;
    
    static {
        NEEDS_PROXY = (Integer.valueOf(Build$VERSION.SDK) < 11);
        PROXIES = new WeakHashMap<View, AnimatorProxy>();
    }
    
    private AnimatorProxy(final View referent) {
        this.mCamera = new Camera();
        this.mAlpha = 1.0f;
        this.mScaleX = 1.0f;
        this.mScaleY = 1.0f;
        this.mBefore = new RectF();
        this.mAfter = new RectF();
        this.mTempMatrix = new Matrix();
        this.setDuration(0L);
        this.setFillAfter(true);
        referent.setAnimation((Animation)this);
        this.mView = new WeakReference<View>(referent);
    }
    
    private void computeRect(final RectF rectF, final View view) {
        rectF.set(0.0f, 0.0f, (float)view.getWidth(), (float)view.getHeight());
        final Matrix mTempMatrix = this.mTempMatrix;
        mTempMatrix.reset();
        this.transformMatrix(mTempMatrix, view);
        this.mTempMatrix.mapRect(rectF);
        rectF.offset((float)view.getLeft(), (float)view.getTop());
        if (rectF.right < rectF.left) {
            final float right = rectF.right;
            rectF.right = rectF.left;
            rectF.left = right;
        }
        if (rectF.bottom < rectF.top) {
            final float top = rectF.top;
            rectF.top = rectF.bottom;
            rectF.bottom = top;
        }
    }
    
    private void invalidateAfterUpdate() {
        final View view = this.mView.get();
        if (view != null && view.getParent() != null) {
            final RectF mAfter = this.mAfter;
            this.computeRect(mAfter, view);
            mAfter.union(this.mBefore);
            ((View)view.getParent()).invalidate((int)Math.floor(mAfter.left), (int)Math.floor(mAfter.top), (int)Math.ceil(mAfter.right), (int)Math.ceil(mAfter.bottom));
        }
    }
    
    private void prepareForUpdate() {
        final View view = this.mView.get();
        if (view != null) {
            this.computeRect(this.mBefore, view);
        }
    }
    
    private void transformMatrix(final Matrix matrix, final View view) {
        final float n = (float)view.getWidth();
        final float n2 = (float)view.getHeight();
        final boolean mHasPivot = this.mHasPivot;
        float mPivotX;
        if (!mHasPivot) {
            mPivotX = n / 2.0f;
        }
        else {
            mPivotX = this.mPivotX;
        }
        float mPivotY;
        if (!mHasPivot) {
            mPivotY = n2 / 2.0f;
        }
        else {
            mPivotY = this.mPivotY;
        }
        final float mRotationX = this.mRotationX;
        final float mRotationY = this.mRotationY;
        final float mRotationZ = this.mRotationZ;
        if (mRotationX != 0.0f || mRotationY != 0.0f || mRotationZ != 0.0f) {
            final Camera mCamera = this.mCamera;
            mCamera.save();
            mCamera.rotateX(mRotationX);
            mCamera.rotateY(mRotationY);
            mCamera.rotateZ(-mRotationZ);
            mCamera.getMatrix(matrix);
            mCamera.restore();
            matrix.preTranslate(-mPivotX, -mPivotY);
            matrix.postTranslate(mPivotX, mPivotY);
        }
        final float mScaleX = this.mScaleX;
        final float mScaleY = this.mScaleY;
        if (mScaleX != 1.0f || mScaleY != 1.0f) {
            matrix.postScale(mScaleX, mScaleY);
            matrix.postTranslate(-(mPivotX / n) * (mScaleX * n - n), -(mPivotY / n2) * (mScaleY * n2 - n2));
        }
        matrix.postTranslate(this.mTranslationX, this.mTranslationY);
    }
    
    public static AnimatorProxy wrap(final View view) {
        final AnimatorProxy animatorProxy = AnimatorProxy.PROXIES.get(view);
        AnimatorProxy animatorProxy2;
        if (animatorProxy != null && animatorProxy == view.getAnimation()) {
            animatorProxy2 = animatorProxy;
        }
        else {
            final AnimatorProxy value = new AnimatorProxy(view);
            AnimatorProxy.PROXIES.put(view, value);
            animatorProxy2 = value;
        }
        return animatorProxy2;
    }
    
    protected void applyTransformation(final float n, final Transformation transformation) {
        final View view = this.mView.get();
        if (view != null) {
            transformation.setAlpha(this.mAlpha);
            this.transformMatrix(transformation.getMatrix(), view);
        }
    }
    
    public float getAlpha() {
        return this.mAlpha;
    }
    
    public float getPivotX() {
        return this.mPivotX;
    }
    
    public float getPivotY() {
        return this.mPivotY;
    }
    
    public float getRotation() {
        return this.mRotationZ;
    }
    
    public float getRotationX() {
        return this.mRotationX;
    }
    
    public float getRotationY() {
        return this.mRotationY;
    }
    
    public float getScaleX() {
        return this.mScaleX;
    }
    
    public float getScaleY() {
        return this.mScaleY;
    }
    
    public int getScrollX() {
        final View view = this.mView.get();
        if (view != null) {
            return view.getScrollX();
        }
        return 0;
    }
    
    public int getScrollY() {
        final View view = this.mView.get();
        if (view != null) {
            return view.getScrollY();
        }
        return 0;
    }
    
    public float getTranslationX() {
        return this.mTranslationX;
    }
    
    public float getTranslationY() {
        return this.mTranslationY;
    }
    
    public float getX() {
        final View view = this.mView.get();
        if (view != null) {
            return view.getLeft() + this.mTranslationX;
        }
        return 0.0f;
    }
    
    public float getY() {
        final View view = this.mView.get();
        if (view != null) {
            return view.getTop() + this.mTranslationY;
        }
        return 0.0f;
    }
    
    public void setAlpha(final float mAlpha) {
        if (this.mAlpha != mAlpha) {
            this.mAlpha = mAlpha;
            final View view = this.mView.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }
    
    public void setPivotX(final float mPivotX) {
        if (!this.mHasPivot || this.mPivotX != mPivotX) {
            this.prepareForUpdate();
            this.mHasPivot = true;
            this.mPivotX = mPivotX;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setPivotY(final float mPivotY) {
        if (!this.mHasPivot || this.mPivotY != mPivotY) {
            this.prepareForUpdate();
            this.mHasPivot = true;
            this.mPivotY = mPivotY;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setRotation(final float mRotationZ) {
        if (this.mRotationZ != mRotationZ) {
            this.prepareForUpdate();
            this.mRotationZ = mRotationZ;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setRotationX(final float mRotationX) {
        if (this.mRotationX != mRotationX) {
            this.prepareForUpdate();
            this.mRotationX = mRotationX;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setRotationY(final float mRotationY) {
        if (this.mRotationY != mRotationY) {
            this.prepareForUpdate();
            this.mRotationY = mRotationY;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setScaleX(final float mScaleX) {
        if (this.mScaleX != mScaleX) {
            this.prepareForUpdate();
            this.mScaleX = mScaleX;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setScaleY(final float mScaleY) {
        if (this.mScaleY != mScaleY) {
            this.prepareForUpdate();
            this.mScaleY = mScaleY;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setScrollX(final int n) {
        final View view = this.mView.get();
        if (view != null) {
            view.scrollTo(n, view.getScrollY());
        }
    }
    
    public void setScrollY(final int n) {
        final View view = this.mView.get();
        if (view != null) {
            view.scrollTo(view.getScrollX(), n);
        }
    }
    
    public void setTranslationX(final float mTranslationX) {
        if (this.mTranslationX != mTranslationX) {
            this.prepareForUpdate();
            this.mTranslationX = mTranslationX;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setTranslationY(final float mTranslationY) {
        if (this.mTranslationY != mTranslationY) {
            this.prepareForUpdate();
            this.mTranslationY = mTranslationY;
            this.invalidateAfterUpdate();
        }
    }
    
    public void setX(final float n) {
        final View view = this.mView.get();
        if (view != null) {
            this.setTranslationX(n - view.getLeft());
        }
    }
    
    public void setY(final float n) {
        final View view = this.mView.get();
        if (view != null) {
            this.setTranslationY(n - view.getTop());
        }
    }
}
