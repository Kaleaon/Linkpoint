package android.support.transition;

import android.animation.PropertyValuesHolder;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.Property;

class PropertyValuesHolderUtils {
    private static final PropertyValuesHolderUtilsImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT < 21) {
            IMPL = new PropertyValuesHolderUtilsApi14();
        } else {
            IMPL = new PropertyValuesHolderUtilsApi21();
        }
    }

    PropertyValuesHolderUtils() {
    }

    static PropertyValuesHolder ofPointF(Property<?, PointF> property, Path path) {
        return IMPL.ofPointF(property, path);
    }
}
