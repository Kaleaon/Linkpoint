// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.view.ViewParent;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ViewUtilsApi14 implements ViewUtilsImpl
{
    private float[] mMatrixValues;
    
    @Override
    public void clearNonTransitionAlpha(@NonNull final View view) {
        if (view.getVisibility() == 0) {
            view.setTag(R.id.save_non_transition_alpha, (Object)null);
        }
    }
    
    @Override
    public ViewOverlayImpl getOverlay(@NonNull final View view) {
        return ViewOverlayApi14.createFrom(view);
    }
    
    @Override
    public float getTransitionAlpha(@NonNull final View view) {
        final Float n = (Float)view.getTag(R.id.save_non_transition_alpha);
        if (n == null) {
            return view.getAlpha();
        }
        return view.getAlpha() / n;
    }
    
    @Override
    public WindowIdImpl getWindowId(@NonNull final View view) {
        return new WindowIdApi14(view.getWindowToken());
    }
    
    @Override
    public void saveNonTransitionAlpha(@NonNull final View view) {
        if (view.getTag(R.id.save_non_transition_alpha) == null) {
            view.setTag(R.id.save_non_transition_alpha, (Object)view.getAlpha());
        }
    }
    
    @Override
    public void setAnimationMatrix(@NonNull final View view, final Matrix matrix) {
        if (matrix != null && !matrix.isIdentity()) {
            float[] mMatrixValues = this.mMatrixValues;
            if (mMatrixValues == null) {
                mMatrixValues = new float[9];
                this.mMatrixValues = mMatrixValues;
            }
            matrix.getValues(mMatrixValues);
            final float n = mMatrixValues[3];
            final float n2 = (float)Math.sqrt(1.0f - n * n);
            int n3;
            if (mMatrixValues[0] < 0.0f) {
                n3 = -1;
            }
            else {
                n3 = 1;
            }
            final float n4 = n3 * n2;
            final float rotation = (float)Math.toDegrees(Math.atan2(n, n4));
            final float scaleX = mMatrixValues[0] / n4;
            final float scaleY = mMatrixValues[4] / n4;
            final float translationX = mMatrixValues[2];
            final float translationY = mMatrixValues[5];
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
            view.setTranslationX(translationX);
            view.setTranslationY(translationY);
            view.setRotation(rotation);
            view.setScaleX(scaleX);
            view.setScaleY(scaleY);
        }
        else {
            view.setPivotX((float)(view.getWidth() / 2));
            view.setPivotY((float)(view.getHeight() / 2));
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            view.setRotation(0.0f);
        }
    }
    
    @Override
    public void setLeftTopRightBottom(final View view, final int left, final int top, final int right, final int bottom) {
        view.setLeft(left);
        view.setTop(top);
        view.setRight(right);
        view.setBottom(bottom);
    }
    
    @Override
    public void setTransitionAlpha(@NonNull final View view, final float alpha) {
        final Float n = (Float)view.getTag(R.id.save_non_transition_alpha);
        if (n == null) {
            view.setAlpha(alpha);
        }
        else {
            view.setAlpha(n * alpha);
        }
    }
    
    @Override
    public void transformMatrixToGlobal(@NonNull final View view, @NonNull final Matrix matrix) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View) {
            final View view2 = (View)parent;
            this.transformMatrixToGlobal(view2, matrix);
            matrix.preTranslate((float)(-view2.getScrollX()), (float)(-view2.getScrollY()));
        }
        matrix.preTranslate((float)view.getLeft(), (float)view.getTop());
        final Matrix matrix2 = view.getMatrix();
        if (!matrix2.isIdentity()) {
            matrix.preConcat(matrix2);
        }
    }
    
    @Override
    public void transformMatrixToLocal(@NonNull final View view, @NonNull final Matrix matrix) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View) {
            final View view2 = (View)parent;
            this.transformMatrixToLocal(view2, matrix);
            matrix.postTranslate((float)view2.getScrollX(), (float)view2.getScrollY());
        }
        matrix.postTranslate((float)view.getLeft(), (float)view.getTop());
        final Matrix matrix2 = view.getMatrix();
        if (!matrix2.isIdentity()) {
            final Matrix matrix3 = new Matrix();
            if (matrix2.invert(matrix3)) {
                matrix.postConcat(matrix3);
            }
        }
    }
}
