// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.reflect.InvocationTargetException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(19)
class ViewUtilsApi19 extends ViewUtilsApi18
{
    private static final String TAG = "ViewUtilsApi19";
    private static Method sGetTransitionAlphaMethod;
    private static boolean sGetTransitionAlphaMethodFetched;
    private static Method sSetTransitionAlphaMethod;
    private static boolean sSetTransitionAlphaMethodFetched;
    
    private void fetchGetTransitionAlphaMethod() {
        if (!ViewUtilsApi19.sGetTransitionAlphaMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi19.sGetTransitionAlphaMethod = View.class.getDeclaredMethod("getTransitionAlpha", (Class<?>[])new Class[0])).setAccessible(true);
                    ViewUtilsApi19.sGetTransitionAlphaMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi19", "Failed to retrieve getTransitionAlpha method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    private void fetchSetTransitionAlphaMethod() {
        if (!ViewUtilsApi19.sSetTransitionAlphaMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi19.sSetTransitionAlphaMethod = View.class.getDeclaredMethod("setTransitionAlpha", Float.TYPE)).setAccessible(true);
                    ViewUtilsApi19.sSetTransitionAlphaMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi19", "Failed to retrieve setTransitionAlpha method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public void clearNonTransitionAlpha(@NonNull final View view) {
    }
    
    @Override
    public float getTransitionAlpha(@NonNull final View obj) {
        this.fetchGetTransitionAlphaMethod();
        if (ViewUtilsApi19.sGetTransitionAlphaMethod != null) {
            try {
                return (float)ViewUtilsApi19.sGetTransitionAlphaMethod.invoke(obj, new Object[0]);
            }
            catch (final InvocationTargetException ex) {
                throw new RuntimeException(ex.getCause());
            }
            catch (final IllegalAccessException ex2) {}
        }
        return super.getTransitionAlpha(obj);
    }
    
    @Override
    public void saveNonTransitionAlpha(@NonNull final View view) {
    }
    
    @Override
    public void setTransitionAlpha(@NonNull final View obj, final float n) {
        this.fetchSetTransitionAlphaMethod();
        if (ViewUtilsApi19.sSetTransitionAlphaMethod == null) {
            obj.setAlpha(n);
        }
        else {
            try {
                ViewUtilsApi19.sSetTransitionAlphaMethod.invoke(obj, n);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
}
