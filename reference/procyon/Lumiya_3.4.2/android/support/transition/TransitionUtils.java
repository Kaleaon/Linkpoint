// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.TypeEvaluator;
import android.animation.AnimatorSet;
import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Bitmap$Config;
import android.graphics.Bitmap;
import android.view.View$MeasureSpec;
import android.widget.ImageView$ScaleType;
import android.widget.ImageView;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;

class TransitionUtils
{
    private static final int MAX_IMAGE_SIZE = 1048576;
    
    static View copyViewImage(final ViewGroup viewGroup, final View view, final View view2) {
        final Matrix matrix = new Matrix();
        matrix.setTranslate((float)(-view2.getScrollX()), (float)(-view2.getScrollY()));
        ViewUtils.transformMatrixToGlobal(view, matrix);
        ViewUtils.transformMatrixToLocal((View)viewGroup, matrix);
        final RectF rectF = new RectF(0.0f, 0.0f, (float)view.getWidth(), (float)view.getHeight());
        matrix.mapRect(rectF);
        final int round = Math.round(rectF.left);
        final int round2 = Math.round(rectF.top);
        final int round3 = Math.round(rectF.right);
        final int round4 = Math.round(rectF.bottom);
        final ImageView imageView = new ImageView(view.getContext());
        imageView.setScaleType(ImageView$ScaleType.CENTER_CROP);
        final Bitmap viewBitmap = createViewBitmap(view, matrix, rectF);
        if (viewBitmap != null) {
            imageView.setImageBitmap(viewBitmap);
        }
        imageView.measure(View$MeasureSpec.makeMeasureSpec(round3 - round, 1073741824), View$MeasureSpec.makeMeasureSpec(round4 - round2, 1073741824));
        imageView.layout(round, round2, round3, round4);
        return (View)imageView;
    }
    
    private static Bitmap createViewBitmap(final View view, final Matrix matrix, final RectF rectF) {
        Bitmap bitmap = null;
        final int round = Math.round(rectF.width());
        final int round2 = Math.round(rectF.height());
        if (round > 0 && round2 > 0) {
            final float min = Math.min(1.0f, 1048576.0f / (round * round2));
            final int n = (int)(round * min);
            final int n2 = (int)(round2 * min);
            matrix.postTranslate(-rectF.left, -rectF.top);
            matrix.postScale(min, min);
            bitmap = Bitmap.createBitmap(n, n2, Bitmap$Config.ARGB_8888);
            final Canvas canvas = new Canvas(bitmap);
            canvas.concat(matrix);
            view.draw(canvas);
        }
        return bitmap;
    }
    
    static Animator mergeAnimators(final Animator animator, final Animator animator2) {
        if (animator == null) {
            return animator2;
        }
        if (animator2 != null) {
            final AnimatorSet set = new AnimatorSet();
            set.playTogether(new Animator[] { animator, animator2 });
            return (Animator)set;
        }
        return animator;
    }
    
    static class MatrixEvaluator implements TypeEvaluator<Matrix>
    {
        final float[] mTempEndValues;
        final Matrix mTempMatrix;
        final float[] mTempStartValues;
        
        MatrixEvaluator() {
            this.mTempStartValues = new float[9];
            this.mTempEndValues = new float[9];
            this.mTempMatrix = new Matrix();
        }
        
        public Matrix evaluate(final float n, final Matrix matrix, final Matrix matrix2) {
            matrix.getValues(this.mTempStartValues);
            matrix2.getValues(this.mTempEndValues);
            for (int i = 0; i < 9; ++i) {
                this.mTempEndValues[i] = (this.mTempEndValues[i] - this.mTempStartValues[i]) * n + this.mTempStartValues[i];
            }
            this.mTempMatrix.setValues(this.mTempEndValues);
            return this.mTempMatrix;
        }
    }
}
