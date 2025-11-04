// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.content.res;

import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.content.res.Resources;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import android.support.v7.widget.AppCompatDrawableManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.os.Build$VERSION;
import android.support.annotation.Nullable;
import android.content.res.ColorStateList;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.content.Context;
import java.util.WeakHashMap;
import android.util.TypedValue;

public final class AppCompatResources
{
    private static final String LOG_TAG = "AppCompatResources";
    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE;
    private static final Object sColorStateCacheLock;
    private static final WeakHashMap<Context, SparseArray<ColorStateListCacheEntry>> sColorStateCaches;
    
    static {
        TL_TYPED_VALUE = new ThreadLocal<TypedValue>();
        sColorStateCaches = new WeakHashMap<Context, SparseArray<ColorStateListCacheEntry>>(0);
        sColorStateCacheLock = new Object();
    }
    
    private AppCompatResources() {
    }
    
    private static void addColorStateListToCache(@NonNull final Context context, @ColorRes final int n, @NonNull final ColorStateList list) {
        synchronized (AppCompatResources.sColorStateCacheLock) {
            SparseArray value = AppCompatResources.sColorStateCaches.get(context);
            if (value == null) {
                value = new SparseArray();
                AppCompatResources.sColorStateCaches.put(context, (SparseArray<ColorStateListCacheEntry>)value);
            }
            value.append(n, (Object)new ColorStateListCacheEntry(list, context.getResources().getConfiguration()));
        }
    }
    
    @Nullable
    private static ColorStateList getCachedColorStateList(@NonNull final Context key, @ColorRes final int n) {
        ColorStateListCacheEntry colorStateListCacheEntry = null;
        Label_0078: {
            synchronized (AppCompatResources.sColorStateCacheLock) {
                final SparseArray sparseArray = AppCompatResources.sColorStateCaches.get(key);
                if (sparseArray != null && sparseArray.size() > 0) {
                    colorStateListCacheEntry = (ColorStateListCacheEntry)sparseArray.get(n);
                    if (colorStateListCacheEntry != null) {
                        if (colorStateListCacheEntry.configuration.equals(key.getResources().getConfiguration())) {
                            break Label_0078;
                        }
                        sparseArray.remove(n);
                    }
                }
                return null;
            }
        }
        final ColorStateList value = colorStateListCacheEntry.value;
        final Object o;
        monitorexit(o);
        return value;
    }
    
    public static ColorStateList getColorStateList(@NonNull final Context context, @ColorRes final int n) {
        if (Build$VERSION.SDK_INT >= 23) {
            return context.getColorStateList(n);
        }
        final ColorStateList cachedColorStateList = getCachedColorStateList(context, n);
        if (cachedColorStateList != null) {
            return cachedColorStateList;
        }
        final ColorStateList inflateColorStateList = inflateColorStateList(context, n);
        if (inflateColorStateList == null) {
            return ContextCompat.getColorStateList(context, n);
        }
        addColorStateListToCache(context, n, inflateColorStateList);
        return inflateColorStateList;
    }
    
    @Nullable
    public static Drawable getDrawable(@NonNull final Context context, @DrawableRes final int n) {
        return AppCompatDrawableManager.get().getDrawable(context, n);
    }
    
    @NonNull
    private static TypedValue getTypedValue() {
        TypedValue value = AppCompatResources.TL_TYPED_VALUE.get();
        if (value == null) {
            value = new TypedValue();
            AppCompatResources.TL_TYPED_VALUE.set(value);
        }
        return value;
    }
    
    @Nullable
    private static ColorStateList inflateColorStateList(final Context context, final int n) {
        Label_0031: {
            if (isColorInt(context, n)) {
                break Label_0031;
            }
            final Resources resources = context.getResources();
            final XmlResourceParser xml = resources.getXml(n);
            try {
                return AppCompatColorStateListInflater.createFromXml(resources, (XmlPullParser)xml, context.getTheme());
                return null;
            }
            catch (final Exception ex) {
                Log.e("AppCompatResources", "Failed to inflate ColorStateList, leaving it to the framework", (Throwable)ex);
                return null;
            }
        }
    }
    
    private static boolean isColorInt(@NonNull final Context context, @ColorRes final int n) {
        boolean b = true;
        final Resources resources = context.getResources();
        final TypedValue typedValue = getTypedValue();
        resources.getValue(n, typedValue, true);
        if (typedValue.type < 28 || typedValue.type > 31) {
            b = false;
        }
        return b;
    }
    
    private static class ColorStateListCacheEntry
    {
        final Configuration configuration;
        final ColorStateList value;
        
        ColorStateListCacheEntry(@NonNull final ColorStateList value, @NonNull final Configuration configuration) {
            this.value = value;
            this.configuration = configuration;
        }
    }
}
