// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import android.animation.Animator;
import java.lang.reflect.InvocationTargetException;
import android.util.Log;
import android.graphics.Matrix;
import android.widget.ImageView;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class ImageViewUtilsApi21 implements ImageViewUtilsImpl
{
    private static final String TAG = "ImageViewUtilsApi21";
    private static Method sAnimateTransformMethod;
    private static boolean sAnimateTransformMethodFetched;
    
    private void fetchAnimateTransformMethod() {
        if (!ImageViewUtilsApi21.sAnimateTransformMethodFetched) {
            while (true) {
                try {
                    (ImageViewUtilsApi21.sAnimateTransformMethod = ImageView.class.getDeclaredMethod("animateTransform", Matrix.class)).setAccessible(true);
                    ImageViewUtilsApi21.sAnimateTransformMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ImageViewUtilsApi21", "Failed to retrieve animateTransform method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public void animateTransform(final ImageView obj, final Matrix matrix) {
        this.fetchAnimateTransformMethod();
        if (ImageViewUtilsApi21.sAnimateTransformMethod != null) {
            try {
                ImageViewUtilsApi21.sAnimateTransformMethod.invoke(obj, matrix);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
    
    @Override
    public void reserveEndAnimateTransform(final ImageView imageView, final Animator animator) {
    }
    
    @Override
    public void startAnimateTransform(final ImageView imageView) {
    }
}
