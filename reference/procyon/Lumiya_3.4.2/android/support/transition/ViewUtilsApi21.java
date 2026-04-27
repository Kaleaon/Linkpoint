// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.reflect.InvocationTargetException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.graphics.Matrix;
import android.view.View;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(21)
class ViewUtilsApi21 extends ViewUtilsApi19
{
    private static final String TAG = "ViewUtilsApi21";
    private static Method sSetAnimationMatrixMethod;
    private static boolean sSetAnimationMatrixMethodFetched;
    private static Method sTransformMatrixToGlobalMethod;
    private static boolean sTransformMatrixToGlobalMethodFetched;
    private static Method sTransformMatrixToLocalMethod;
    private static boolean sTransformMatrixToLocalMethodFetched;
    
    private void fetchSetAnimationMatrix() {
        if (!ViewUtilsApi21.sSetAnimationMatrixMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi21.sSetAnimationMatrixMethod = View.class.getDeclaredMethod("setAnimationMatrix", Matrix.class)).setAccessible(true);
                    ViewUtilsApi21.sSetAnimationMatrixMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi21", "Failed to retrieve setAnimationMatrix method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    private void fetchTransformMatrixToGlobalMethod() {
        if (!ViewUtilsApi21.sTransformMatrixToGlobalMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi21.sTransformMatrixToGlobalMethod = View.class.getDeclaredMethod("transformMatrixToGlobal", Matrix.class)).setAccessible(true);
                    ViewUtilsApi21.sTransformMatrixToGlobalMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi21", "Failed to retrieve transformMatrixToGlobal method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    private void fetchTransformMatrixToLocalMethod() {
        if (!ViewUtilsApi21.sTransformMatrixToLocalMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi21.sTransformMatrixToLocalMethod = View.class.getDeclaredMethod("transformMatrixToLocal", Matrix.class)).setAccessible(true);
                    ViewUtilsApi21.sTransformMatrixToLocalMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi21", "Failed to retrieve transformMatrixToLocal method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public void setAnimationMatrix(@NonNull final View obj, final Matrix matrix) {
        this.fetchSetAnimationMatrix();
        if (ViewUtilsApi21.sSetAnimationMatrixMethod != null) {
            try {
                ViewUtilsApi21.sSetAnimationMatrixMethod.invoke(obj, matrix);
            }
            catch (final InvocationTargetException ex) {}
            catch (final IllegalAccessException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
    
    @Override
    public void transformMatrixToGlobal(@NonNull final View obj, @NonNull final Matrix matrix) {
        this.fetchTransformMatrixToGlobalMethod();
        if (ViewUtilsApi21.sTransformMatrixToGlobalMethod != null) {
            try {
                ViewUtilsApi21.sTransformMatrixToGlobalMethod.invoke(obj, matrix);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
    
    @Override
    public void transformMatrixToLocal(@NonNull final View obj, @NonNull final Matrix matrix) {
        this.fetchTransformMatrixToLocalMethod();
        if (ViewUtilsApi21.sTransformMatrixToLocalMethod != null) {
            try {
                ViewUtilsApi21.sTransformMatrixToLocalMethod.invoke(obj, matrix);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
}
