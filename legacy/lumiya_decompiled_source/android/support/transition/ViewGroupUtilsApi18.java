package android.support.transition;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
@RequiresApi(18)
/* loaded from: classes.dex */
class ViewGroupUtilsApi18 extends ViewGroupUtilsApi14 {
    private static final String TAG = "ViewUtilsApi18";
    private static Method sSuppressLayoutMethod;
    private static boolean sSuppressLayoutMethodFetched;

    private void fetchSuppressLayoutMethod() {
        if (sSuppressLayoutMethodFetched) {
            return;
        }
        try {
            sSuppressLayoutMethod = ViewGroup.class.getDeclaredMethod("suppressLayout", Boolean.TYPE);
            sSuppressLayoutMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "Failed to retrieve suppressLayout method", e);
        }
        sSuppressLayoutMethodFetched = true;
    }

    @Override // android.support.transition.ViewGroupUtilsApi14, android.support.transition.ViewGroupUtilsImpl
    public ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup viewGroup) {
        return new ViewGroupOverlayApi18(viewGroup);
    }

    @Override // android.support.transition.ViewGroupUtilsApi14, android.support.transition.ViewGroupUtilsImpl
    public void suppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        fetchSuppressLayoutMethod();
        if (sSuppressLayoutMethod == null) {
            return;
        }
        try {
            sSuppressLayoutMethod.invoke(viewGroup, Boolean.valueOf(z));
        } catch (IllegalAccessException e) {
            Log.i(TAG, "Failed to invoke suppressLayout method", e);
        } catch (InvocationTargetException e2) {
            Log.i(TAG, "Error invoking suppressLayout method", e2);
        }
    }
}
