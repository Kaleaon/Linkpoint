// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator$AnimatorListener;
import android.widget.ImageView$ScaleType;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.support.annotation.RequiresApi;

@RequiresApi(14)
class ImageViewUtilsApi14 implements ImageViewUtilsImpl
{
    @Override
    public void animateTransform(final ImageView imageView, final Matrix imageMatrix) {
        imageView.setImageMatrix(imageMatrix);
    }
    
    @Override
    public void reserveEndAnimateTransform(final ImageView imageView, final Animator animator) {
        animator.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter() {
            public void onAnimationEnd(final Animator animator) {
                final ImageView$ScaleType scaleType = (ImageView$ScaleType)imageView.getTag(R.id.save_scale_type);
                imageView.setScaleType(scaleType);
                imageView.setTag(R.id.save_scale_type, (Object)null);
                if (scaleType == ImageView$ScaleType.MATRIX) {
                    imageView.setImageMatrix((Matrix)imageView.getTag(R.id.save_image_matrix));
                    imageView.setTag(R.id.save_image_matrix, (Object)null);
                }
                animator.removeListener((Animator$AnimatorListener)this);
            }
        });
    }
    
    @Override
    public void startAnimateTransform(final ImageView imageView) {
        final ImageView$ScaleType scaleType = imageView.getScaleType();
        imageView.setTag(R.id.save_scale_type, (Object)scaleType);
        if (scaleType != ImageView$ScaleType.MATRIX) {
            imageView.setScaleType(ImageView$ScaleType.MATRIX);
        }
        else {
            imageView.setTag(R.id.save_image_matrix, (Object)imageView.getImageMatrix());
        }
        imageView.setImageMatrix(MatrixUtils.IDENTITY_MATRIX);
    }
}
