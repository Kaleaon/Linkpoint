// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.view;

import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.content.Context;
import android.util.Log;
import android.view.ViewConfiguration;
import android.os.Build$VERSION;
import java.lang.reflect.Method;

@Deprecated
public final class ViewConfigurationCompat
{
    private static final String TAG = "ViewConfigCompat";
    private static Method sGetScaledScrollFactorMethod;
    
    static {
        if (Build$VERSION.SDK_INT == 25) {
            try {
                ViewConfigurationCompat.sGetScaledScrollFactorMethod = ViewConfiguration.class.getDeclaredMethod("getScaledScrollFactor", (Class<?>[])new Class[0]);
            }
            catch (final Exception ex) {
                Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
            }
        }
    }
    
    private ViewConfigurationCompat() {
    }
    
    private static float getLegacyScrollFactor(final ViewConfiguration obj, final Context context) {
        Label_0008: {
            if (Build$VERSION.SDK_INT >= 25 && ViewConfigurationCompat.sGetScaledScrollFactorMethod != null) {
                try {
                    return (float)(int)ViewConfigurationCompat.sGetScaledScrollFactorMethod.invoke(obj, new Object[0]);
                }
                catch (final Exception ex) {
                    Log.i("ViewConfigCompat", "Could not find method getScaledScrollFactor() on ViewConfiguration");
                    break Label_0008;
                }
                final TypedValue typedValue;
                return typedValue.getDimension(context.getResources().getDisplayMetrics());
            }
        }
        final TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(16842829, typedValue, true)) {
            return 0.0f;
        }
        return typedValue.getDimension(context.getResources().getDisplayMetrics());
    }
    
    public static float getScaledHorizontalScrollFactor(@NonNull final ViewConfiguration viewConfiguration, @NonNull final Context context) {
        if (Build$VERSION.SDK_INT < 26) {
            return getLegacyScrollFactor(viewConfiguration, context);
        }
        return viewConfiguration.getScaledHorizontalScrollFactor();
    }
    
    @Deprecated
    public static int getScaledPagingTouchSlop(final ViewConfiguration viewConfiguration) {
        return viewConfiguration.getScaledPagingTouchSlop();
    }
    
    public static float getScaledVerticalScrollFactor(@NonNull final ViewConfiguration viewConfiguration, @NonNull final Context context) {
        if (Build$VERSION.SDK_INT < 26) {
            return getLegacyScrollFactor(viewConfiguration, context);
        }
        return viewConfiguration.getScaledVerticalScrollFactor();
    }
    
    @Deprecated
    public static boolean hasPermanentMenuKey(final ViewConfiguration viewConfiguration) {
        return viewConfiguration.hasPermanentMenuKey();
    }
}
