// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import java.util.Map;
import android.view.View;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.TypeEvaluator;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.util.Property;

public class ChangeImageTransform extends Transition
{
    private static final Property<ImageView, Matrix> ANIMATED_TRANSFORM_PROPERTY;
    private static final TypeEvaluator<Matrix> NULL_MATRIX_EVALUATOR;
    private static final String PROPNAME_BOUNDS = "android:changeImageTransform:bounds";
    private static final String PROPNAME_MATRIX = "android:changeImageTransform:matrix";
    private static final String[] sTransitionProperties;
    
    static {
        sTransitionProperties = new String[] { "android:changeImageTransform:matrix", "android:changeImageTransform:bounds" };
        NULL_MATRIX_EVALUATOR = (TypeEvaluator)new TypeEvaluator<Matrix>() {
            public Matrix evaluate(final float n, final Matrix matrix, final Matrix matrix2) {
                return null;
            }
        };
        ANIMATED_TRANSFORM_PROPERTY = new Property<ImageView, Matrix>(Matrix.class, "animatedTransform") {
            public Matrix get(final ImageView imageView) {
                return null;
            }
            
            public void set(final ImageView imageView, final Matrix matrix) {
                ImageViewUtils.animateTransform(imageView, matrix);
            }
        };
    }
    
    public ChangeImageTransform() {
    }
    
    public ChangeImageTransform(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    private void captureValues(final TransitionValues transitionValues) {
        final View view = transitionValues.view;
        if (!(view instanceof ImageView) || view.getVisibility() != 0) {
            return;
        }
        final ImageView imageView = (ImageView)view;
        if (imageView.getDrawable() != null) {
            final Map<String, Object> values = transitionValues.values;
            values.put("android:changeImageTransform:bounds", new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
            values.put("android:changeImageTransform:matrix", copyImageMatrix(imageView));
        }
    }
    
    private static Matrix centerCropMatrix(final ImageView imageView) {
        final Drawable drawable = imageView.getDrawable();
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int width = imageView.getWidth();
        final float a = width / (float)intrinsicWidth;
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        final int height = imageView.getHeight();
        final float max = Math.max(a, height / (float)intrinsicHeight);
        final float n = (float)intrinsicWidth;
        final float n2 = (float)intrinsicHeight;
        final int round = Math.round((width - n * max) / 2.0f);
        final int round2 = Math.round((height - n2 * max) / 2.0f);
        final Matrix matrix = new Matrix();
        matrix.postScale(max, max);
        matrix.postTranslate((float)round, (float)round2);
        return matrix;
    }
    
    private static Matrix copyImageMatrix(final ImageView imageView) {
        switch (imageView.getScaleType()) {
            default: {
                return new Matrix(imageView.getImageMatrix());
            }
            case FIT_XY: {
                return fitXYMatrix(imageView);
            }
            case CENTER_CROP: {
                return centerCropMatrix(imageView);
            }
        }
    }
    
    private ObjectAnimator createMatrixAnimator(final ImageView imageView, final Matrix matrix, final Matrix matrix2) {
        return ObjectAnimator.ofObject((Object)imageView, (Property)ChangeImageTransform.ANIMATED_TRANSFORM_PROPERTY, (TypeEvaluator)new TransitionUtils.MatrixEvaluator(), (Object[])new Matrix[] { matrix, matrix2 });
    }
    
    private ObjectAnimator createNullAnimator(final ImageView imageView) {
        return ObjectAnimator.ofObject((Object)imageView, (Property)ChangeImageTransform.ANIMATED_TRANSFORM_PROPERTY, (TypeEvaluator)ChangeImageTransform.NULL_MATRIX_EVALUATOR, (Object[])new Matrix[] { null, null });
    }
    
    private static Matrix fitXYMatrix(final ImageView imageView) {
        final Drawable drawable = imageView.getDrawable();
        final Matrix matrix = new Matrix();
        matrix.postScale(imageView.getWidth() / (float)drawable.getIntrinsicWidth(), imageView.getHeight() / (float)drawable.getIntrinsicHeight());
        return matrix;
    }
    
    @Override
    public void captureEndValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public void captureStartValues(@NonNull final TransitionValues transitionValues) {
        this.captureValues(transitionValues);
    }
    
    @Override
    public Animator createAnimator(@NonNull final ViewGroup viewGroup, final TransitionValues transitionValues, final TransitionValues transitionValues2) {
        boolean b = false;
        if (transitionValues == null || transitionValues2 == null) {
            return null;
        }
        final Rect rect = transitionValues.values.get("android:changeImageTransform:bounds");
        final Rect rect2 = transitionValues2.values.get("android:changeImageTransform:bounds");
        if (rect != null && rect2 != null) {
            Matrix identity_MATRIX = transitionValues.values.get("android:changeImageTransform:matrix");
            Matrix identity_MATRIX2 = transitionValues2.values.get("android:changeImageTransform:matrix");
            while (true) {
                Label_0159: {
                    if (identity_MATRIX == null && identity_MATRIX2 == null) {
                        break Label_0159;
                    }
                    if (identity_MATRIX != null) {
                        if (identity_MATRIX.equals((Object)identity_MATRIX2)) {
                            break Label_0159;
                        }
                    }
                    if (rect.equals((Object)rect2) && b) {
                        return null;
                    }
                    final ImageView imageView = (ImageView)transitionValues2.view;
                    final Drawable drawable = imageView.getDrawable();
                    final int intrinsicWidth = drawable.getIntrinsicWidth();
                    final int intrinsicHeight = drawable.getIntrinsicHeight();
                    ImageViewUtils.startAnimateTransform(imageView);
                    ObjectAnimator objectAnimator;
                    if (intrinsicWidth != 0 && intrinsicHeight != 0) {
                        if (identity_MATRIX == null) {
                            identity_MATRIX = MatrixUtils.IDENTITY_MATRIX;
                        }
                        if (identity_MATRIX2 == null) {
                            identity_MATRIX2 = MatrixUtils.IDENTITY_MATRIX;
                        }
                        ChangeImageTransform.ANIMATED_TRANSFORM_PROPERTY.set((Object)imageView, (Object)identity_MATRIX);
                        objectAnimator = this.createMatrixAnimator(imageView, identity_MATRIX, identity_MATRIX2);
                    }
                    else {
                        objectAnimator = this.createNullAnimator(imageView);
                    }
                    ImageViewUtils.reserveEndAnimateTransform(imageView, (Animator)objectAnimator);
                    return (Animator)objectAnimator;
                }
                b = true;
                continue;
            }
        }
        return null;
    }
    
    @Override
    public String[] getTransitionProperties() {
        return ChangeImageTransform.sTransitionProperties;
    }
}
