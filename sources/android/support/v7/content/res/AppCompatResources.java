package android.support.v7.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import java.util.WeakHashMap;

public final class AppCompatResources {
    private static final String LOG_TAG = "AppCompatResources";
    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal<>();
    private static final Object sColorStateCacheLock = new Object();
    private static final WeakHashMap<Context, SparseArray<ColorStateListCacheEntry>> sColorStateCaches = new WeakHashMap<>(0);

    private static class ColorStateListCacheEntry {
        final Configuration configuration;
        final ColorStateList value;

        ColorStateListCacheEntry(@NonNull ColorStateList colorStateList, @NonNull Configuration configuration2) {
            this.value = colorStateList;
            this.configuration = configuration2;
        }
    }

    private AppCompatResources() {
    }

    private static void addColorStateListToCache(@NonNull Context context, @ColorRes int i, @NonNull ColorStateList colorStateList) {
        synchronized (sColorStateCacheLock) {
            SparseArray sparseArray = sColorStateCaches.get(context);
            if (sparseArray == null) {
                sparseArray = new SparseArray();
                sColorStateCaches.put(context, sparseArray);
            }
            sparseArray.append(i, new ColorStateListCacheEntry(colorStateList, context.getResources().getConfiguration()));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000f, code lost:
        return null;
     */
    @android.support.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.content.res.ColorStateList getCachedColorStateList(@android.support.annotation.NonNull android.content.Context r6, @android.support.annotation.ColorRes int r7) {
        /*
            r5 = 0
            java.lang.Object r2 = sColorStateCacheLock
            monitor-enter(r2)
            java.util.WeakHashMap<android.content.Context, android.util.SparseArray<android.support.v7.content.res.AppCompatResources$ColorStateListCacheEntry>> r0 = sColorStateCaches     // Catch:{ all -> 0x0032 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x0032 }
            android.util.SparseArray r0 = (android.util.SparseArray) r0     // Catch:{ all -> 0x0032 }
            if (r0 != 0) goto L_0x0010
        L_0x000e:
            monitor-exit(r2)     // Catch:{ all -> 0x0032 }
            return r5
        L_0x0010:
            int r1 = r0.size()     // Catch:{ all -> 0x0032 }
            if (r1 <= 0) goto L_0x000e
            java.lang.Object r1 = r0.get(r7)     // Catch:{ all -> 0x0032 }
            android.support.v7.content.res.AppCompatResources$ColorStateListCacheEntry r1 = (android.support.v7.content.res.AppCompatResources.ColorStateListCacheEntry) r1     // Catch:{ all -> 0x0032 }
            if (r1 == 0) goto L_0x000e
            android.content.res.Configuration r3 = r1.configuration     // Catch:{ all -> 0x0032 }
            android.content.res.Resources r4 = r6.getResources()     // Catch:{ all -> 0x0032 }
            android.content.res.Configuration r4 = r4.getConfiguration()     // Catch:{ all -> 0x0032 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0032 }
            if (r3 != 0) goto L_0x0035
            r0.remove(r7)     // Catch:{ all -> 0x0032 }
            goto L_0x000e
        L_0x0032:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0032 }
            throw r0
        L_0x0035:
            android.content.res.ColorStateList r0 = r1.value     // Catch:{ all -> 0x0032 }
            monitor-exit(r2)     // Catch:{ all -> 0x0032 }
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.content.res.AppCompatResources.getCachedColorStateList(android.content.Context, int):android.content.res.ColorStateList");
    }

    public static ColorStateList getColorStateList(@NonNull Context context, @ColorRes int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColorStateList(i);
        }
        ColorStateList cachedColorStateList = getCachedColorStateList(context, i);
        if (cachedColorStateList != null) {
            return cachedColorStateList;
        }
        ColorStateList inflateColorStateList = inflateColorStateList(context, i);
        if (inflateColorStateList == null) {
            return ContextCompat.getColorStateList(context, i);
        }
        addColorStateListToCache(context, i, inflateColorStateList);
        return inflateColorStateList;
    }

    @Nullable
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int i) {
        return AppCompatDrawableManager.get().getDrawable(context, i);
    }

    @NonNull
    private static TypedValue getTypedValue() {
        TypedValue typedValue = TL_TYPED_VALUE.get();
        if (typedValue != null) {
            return typedValue;
        }
        TypedValue typedValue2 = new TypedValue();
        TL_TYPED_VALUE.set(typedValue2);
        return typedValue2;
    }

    @Nullable
    private static ColorStateList inflateColorStateList(Context context, int i) {
        if (isColorInt(context, i)) {
            return null;
        }
        Resources resources = context.getResources();
        try {
            return AppCompatColorStateListInflater.createFromXml(resources, resources.getXml(i), context.getTheme());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to inflate ColorStateList, leaving it to the framework", e);
            return null;
        }
    }

    private static boolean isColorInt(@NonNull Context context, @ColorRes int i) {
        Resources resources = context.getResources();
        TypedValue typedValue = getTypedValue();
        resources.getValue(i, typedValue, true);
        return typedValue.type >= 28 && typedValue.type <= 31;
    }
}
