// 
// Decompiled by Procyon v0.6.0
// 

package android.support.transition;

import java.lang.reflect.InvocationTargetException;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;
import android.support.annotation.RequiresApi;

@RequiresApi(22)
class ViewUtilsApi22 extends ViewUtilsApi21
{
    private static final String TAG = "ViewUtilsApi22";
    private static Method sSetLeftTopRightBottomMethod;
    private static boolean sSetLeftTopRightBottomMethodFetched;
    
    @SuppressLint({ "PrivateApi" })
    private void fetchSetLeftTopRightBottomMethod() {
        if (!ViewUtilsApi22.sSetLeftTopRightBottomMethodFetched) {
            while (true) {
                try {
                    (ViewUtilsApi22.sSetLeftTopRightBottomMethod = View.class.getDeclaredMethod("setLeftTopRightBottom", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE)).setAccessible(true);
                    ViewUtilsApi22.sSetLeftTopRightBottomMethodFetched = true;
                }
                catch (final NoSuchMethodException ex) {
                    Log.i("ViewUtilsApi22", "Failed to retrieve setLeftTopRightBottom method", (Throwable)ex);
                    continue;
                }
                break;
            }
        }
    }
    
    @Override
    public void setLeftTopRightBottom(final View obj, final int i, final int j, final int k, final int l) {
        this.fetchSetLeftTopRightBottomMethod();
        if (ViewUtilsApi22.sSetLeftTopRightBottomMethod != null) {
            try {
                ViewUtilsApi22.sSetLeftTopRightBottomMethod.invoke(obj, i, j, k, l);
            }
            catch (final IllegalAccessException ex) {}
            catch (final InvocationTargetException ex2) {
                throw new RuntimeException(ex2.getCause());
            }
        }
    }
}
