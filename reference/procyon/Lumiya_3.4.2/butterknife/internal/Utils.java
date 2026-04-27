// 
// Decompiled by Procyon v0.6.0
// 

package butterknife.internal;

import java.util.List;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.UiThread;
import android.content.res.Resources$NotFoundException;
import android.support.annotation.DimenRes;
import android.content.Context;
import java.lang.reflect.Array;
import android.support.annotation.IdRes;
import android.view.View;
import android.util.TypedValue;

public final class Utils
{
    private static final TypedValue VALUE;
    
    static {
        VALUE = new TypedValue();
    }
    
    private Utils() {
        throw new AssertionError((Object)"No instances.");
    }
    
    @SafeVarargs
    public static <T> T[] arrayOf(final T... array) {
        return (T[])filterNull((Object[])array);
    }
    
    public static <T> T castParam(Object cast, final String str, final int n, final String str2, final int n2, final Class<T> clazz) {
        try {
            cast = clazz.cast(cast);
            return (T)cast;
        }
        catch (final ClassCastException cause) {
            throw new IllegalStateException("Parameter #" + (n + 1) + " of method '" + str + "' was of the wrong type for parameter #" + (n2 + 1) + " of method '" + str2 + "'. See cause for more info.", cause);
        }
    }
    
    public static <T> T castView(final View obj, @IdRes final int i, final String str, final Class<T> clazz) {
        try {
            return clazz.cast(obj);
        }
        catch (final ClassCastException cause) {
            throw new IllegalStateException("View '" + getResourceEntryName(obj, i) + "' with ID " + i + " for " + str + " was of the wrong type. See cause for more info.", cause);
        }
    }
    
    private static <T> T[] filterNull(final T[] array) {
        final int length = array.length;
        int i = 0;
        int length2 = 0;
        while (i < length) {
            final T t = array[i];
            if (t != null) {
                final int n = length2 + 1;
                array[length2] = t;
                length2 = n;
            }
            ++i;
        }
        if (length2 != length) {
            final Object[] array2 = (Object[])Array.newInstance(array.getClass().getComponentType(), length2);
            System.arraycopy(array, 0, array2, 0, length2);
            return (T[])array2;
        }
        return array;
    }
    
    public static <T> T findOptionalViewAsType(final View view, @IdRes final int n, final String s, final Class<T> clazz) {
        return (T)castView(view.findViewById(n), n, s, (Class<Object>)clazz);
    }
    
    public static View findRequiredView(final View view, @IdRes final int i, final String str) {
        final View viewById = view.findViewById(i);
        if (viewById == null) {
            throw new IllegalStateException("Required view '" + getResourceEntryName(view, i) + "' with ID " + i + " for " + str + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
        }
        return viewById;
    }
    
    public static <T> T findRequiredViewAsType(final View view, @IdRes final int n, final String s, final Class<T> clazz) {
        return (T)castView(findRequiredView(view, n, s), n, s, (Class<Object>)clazz);
    }
    
    @UiThread
    public static float getFloat(final Context context, @DimenRes final int i) {
        final TypedValue value = Utils.VALUE;
        context.getResources().getValue(i, value, true);
        if (value.type != 4) {
            throw new Resources$NotFoundException("Resource ID #0x" + Integer.toHexString(i) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
        }
        return value.getFloat();
    }
    
    private static String getResourceEntryName(final View view, @IdRes final int n) {
        if (!view.isInEditMode()) {
            return view.getContext().getResources().getResourceEntryName(n);
        }
        return "<unavailable while editing>";
    }
    
    @UiThread
    public static Drawable getTintedDrawable(final Context context, @DrawableRes final int n, @AttrRes final int i) {
        if (context.getTheme().resolveAttribute(i, Utils.VALUE, true)) {
            final Drawable wrap = DrawableCompat.wrap(ContextCompat.getDrawable(context, n).mutate());
            DrawableCompat.setTint(wrap, ContextCompat.getColor(context, Utils.VALUE.resourceId));
            return wrap;
        }
        throw new Resources$NotFoundException("Required tint color attribute with name " + context.getResources().getResourceEntryName(i) + " and attribute ID " + i + " was not found.");
    }
    
    @SafeVarargs
    public static <T> List<T> listOf(final T... array) {
        return new ImmutableList<T>(filterNull(array));
    }
}
