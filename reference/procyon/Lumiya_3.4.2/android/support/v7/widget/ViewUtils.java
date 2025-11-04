// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import java.lang.reflect.InvocationTargetException;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.graphics.Rect;
import android.view.View;
import android.os.Build$VERSION;
import java.lang.reflect.Method;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class ViewUtils
{
    private static final String TAG = "ViewUtils";
    private static Method sComputeFitSystemWindowsMethod;
    
    static {
        if (Build$VERSION.SDK_INT >= 18) {
            try {
                ViewUtils.sComputeFitSystemWindowsMethod = View.class.getDeclaredMethod("computeFitSystemWindows", Rect.class, Rect.class);
                if (!ViewUtils.sComputeFitSystemWindowsMethod.isAccessible()) {
                    ViewUtils.sComputeFitSystemWindowsMethod.setAccessible(true);
                }
            }
            catch (final NoSuchMethodException ex) {
                Log.d("ViewUtils", "Could not find method computeFitSystemWindows. Oh well.");
            }
        }
    }
    
    private ViewUtils() {
    }
    
    public static void computeFitSystemWindows(final View obj, final Rect rect, final Rect rect2) {
        if (ViewUtils.sComputeFitSystemWindowsMethod != null) {
            try {
                ViewUtils.sComputeFitSystemWindowsMethod.invoke(obj, rect, rect2);
            }
            catch (final Exception ex) {
                Log.d("ViewUtils", "Could not invoke computeFitSystemWindows", (Throwable)ex);
            }
        }
    }
    
    public static boolean isLayoutRtl(final View view) {
        boolean b = true;
        if (ViewCompat.getLayoutDirection(view) != 1) {
            b = false;
        }
        return b;
    }
    
    public static void makeOptionalFitsSystemWindows(final View obj) {
        if (Build$VERSION.SDK_INT >= 16) {
            try {
                final Method method = obj.getClass().getMethod("makeOptionalFitsSystemWindows", (Class<?>[])new Class[0]);
                if (!method.isAccessible()) {
                    goto Label_0055;
                }
                method.invoke(obj, new Object[0]);
            }
            catch (final NoSuchMethodException ex) {
                Log.d("ViewUtils", "Could not find method makeOptionalFitsSystemWindows. Oh well...");
            }
            catch (final InvocationTargetException ex2) {
                Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", (Throwable)ex2);
            }
            catch (final IllegalAccessException ex3) {
                Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", (Throwable)ex3);
            }
        }
    }
}
