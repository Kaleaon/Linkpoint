// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.reflect.InvocationTargetException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(18)
class ViewGroupUtilsApi18 extends ViewGroupUtilsApi14
{
    private static final String TAG = "ViewUtilsApi18";
    private static Method sSuppressLayoutMethod;
    private static boolean sSuppressLayoutMethodFetched;
    
    private void fetchSuppressLayoutMethod() {
        if (!ViewGroupUtilsApi18.sSuppressLayoutMethodFetched) {
            while (true) {
                try {
                    (ViewGroupUtilsApi18.sSuppressLayoutMethod = ViewGroup.class.getDeclaredMethod("suppressLayout", Boolean.TYPE)).setAccessible(true);
                    ViewGroupUtilsApi18.sSuppressLayoutMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi18", "Failed to retrieve suppressLayout method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public ViewGroupOverlayImpl getOverlay(@NonNull final ViewGroup viewGroup) {
        return new ViewGroupOverlayApi18(viewGroup);
    }
    
    @Override
    public void suppressLayout(@NonNull final ViewGroup obj, final boolean b) {
        this.fetchSuppressLayoutMethod();
        if (ViewGroupUtilsApi18.sSuppressLayoutMethod != null) {
            try {
                ViewGroupUtilsApi18.sSuppressLayoutMethod.invoke(obj, b);
            }
            catch (final IllegalAccessException ex) {
                Log.i("ViewUtilsApi18", "Failed to invoke suppressLayout method", (Throwable)ex);
            }
            catch (final InvocationTargetException ex2) {
                Log.i("ViewUtilsApi18", "Error invoking suppressLayout method", (Throwable)ex2);
            }
        }
    }
}
