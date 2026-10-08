package android.support.transition;

import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewParent;
@RequiresApi(14)
/* loaded from: classes.dex */
class ViewUtilsApi14 implements ViewUtilsImpl {
    private float[] mMatrixValues;

    @Override // android.support.transition.ViewUtilsImpl
    public void clearNonTransitionAlpha(@NonNull View view) {
        if (view.getVisibility() != 0) {
            return;
        }
        view.setTag(R.id.save_non_transition_alpha, null);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public ViewOverlayImpl getOverlay(@NonNull View view) {
        return ViewOverlayApi14.createFrom(view);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public float getTransitionAlpha(@NonNull View view) {
        Float f = (Float) view.getTag(R.id.save_non_transition_alpha);
        return f == null ? view.getAlpha() : view.getAlpha() / f.floatValue();
    }

    @Override // android.support.transition.ViewUtilsImpl
    public WindowIdImpl getWindowId(@NonNull View view) {
        return new WindowIdApi14(view.getWindowToken());
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void saveNonTransitionAlpha(@NonNull View view) {
        if (view.getTag(R.id.save_non_transition_alpha) != null) {
            return;
        }
        view.setTag(R.id.save_non_transition_alpha, Float.valueOf(view.getAlpha()));
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setAnimationMatrix(@NonNull View view, Matrix matrix) {
        if (matrix == null || matrix.isIdentity()) {
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setTranslationX(0.0f);
            view.setTranslationY(0.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            view.setRotation(0.0f);
            return;
        }
        float[] fArr = this.mMatrixValues;
        if (fArr == null) {
            fArr = new float[9];
            this.mMatrixValues = fArr;
        }
        matrix.getValues(fArr);
        float f = fArr[3];
        float sqrt = (fArr[0] < 0.0f ? -1 : 1) * ((float) Math.sqrt(1.0f - (f * f)));
        float f2 = fArr[0] / sqrt;
        float f3 = fArr[4] / sqrt;
        float f4 = fArr[2];
        float f5 = fArr[5];
        view.setPivotX(0.0f);
        view.setPivotY(0.0f);
        view.setTranslationX(f4);
        view.setTranslationY(f5);
        view.setRotation((float) Math.toDegrees(Math.atan2(f, sqrt)));
        view.setScaleX(f2);
        view.setScaleY(f3);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setLeftTopRightBottom(View view, int i, int i2, int i3, int i4) {
        view.setLeft(i);
        view.setTop(i2);
        view.setRight(i3);
        view.setBottom(i4);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void setTransitionAlpha(@NonNull View view, float f) {
        Float f2 = (Float) view.getTag(R.id.save_non_transition_alpha);
        if (f2 == null) {
            view.setAlpha(f);
        } else {
            view.setAlpha(f2.floatValue() * f);
        }
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void transformMatrixToGlobal(@NonNull View view, @NonNull Matrix matrix) {
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View view2 = (View) parent;
            transformMatrixToGlobal(view2, matrix);
            matrix.preTranslate(-view2.getScrollX(), -view2.getScrollY());
        }
        matrix.preTranslate(view.getLeft(), view.getTop());
        Matrix matrix2 = view.getMatrix();
        if (matrix2.isIdentity()) {
            return;
        }
        matrix.preConcat(matrix2);
    }

    @Override // android.support.transition.ViewUtilsImpl
    public void transformMatrixToLocal(@NonNull View view, @NonNull Matrix matrix) {
        ViewParent parent = view.getParent();
        if (parent instanceof View) {
            View view2 = (View) parent;
            transformMatrixToLocal(view2, matrix);
            matrix.postTranslate(view2.getScrollX(), view2.getScrollY());
        }
        matrix.postTranslate(view.getLeft(), view.getTop());
        Matrix matrix2 = view.getMatrix();
        if (matrix2.isIdentity()) {
            return;
        }
        Matrix matrix3 = new Matrix();
        if (matrix2.invert(matrix3)) {
            matrix.postConcat(matrix3);
        }
    }
}
