package android.support.transition;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

class ViewGroupUtils {
    private static final ViewGroupUtilsImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT < 18) {
            IMPL = new ViewGroupUtilsApi14();
        } else {
            IMPL = new ViewGroupUtilsApi18();
        }
    }

    ViewGroupUtils() {
    }

    static ViewGroupOverlayImpl getOverlay(@NonNull ViewGroup viewGroup) {
        return IMPL.getOverlay(viewGroup);
    }

    static void suppressLayout(@NonNull ViewGroup viewGroup, boolean z) {
        IMPL.suppressLayout(viewGroup, z);
    }
}
