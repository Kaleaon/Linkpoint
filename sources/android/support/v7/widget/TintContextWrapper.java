package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TintContextWrapper extends ContextWrapper {
    private static final Object CACHE_LOCK = new Object();
    private static ArrayList<WeakReference<TintContextWrapper>> sCache;
    private final Resources mResources;
    private final Resources.Theme mTheme;

    private TintContextWrapper(@NonNull Context context) {
        super(context);
        if (!VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new TintResources(this, context.getResources());
            this.mTheme = null;
            return;
        }
        this.mResources = new VectorEnabledTintResources(this, context.getResources());
        this.mTheme = this.mResources.newTheme();
        this.mTheme.setTo(context.getTheme());
    }

    private static boolean shouldWrap(@NonNull Context context) {
        if (!(context instanceof TintContextWrapper) && !(context.getResources() instanceof TintResources) && !(context.getResources() instanceof VectorEnabledTintResources)) {
            return Build.VERSION.SDK_INT < 21 || VectorEnabledTintResources.shouldBeUsed();
        }
        return false;
    }

    public static Context wrap(@NonNull Context context) {
        if (!shouldWrap(context)) {
            return context;
        }
        synchronized (CACHE_LOCK) {
            if (sCache != null) {
                for (int size = sCache.size() - 1; size >= 0; size--) {
                    WeakReference weakReference = sCache.get(size);
                    if (weakReference == null || weakReference.get() == null) {
                        sCache.remove(size);
                    }
                }
                for (int size2 = sCache.size() - 1; size2 >= 0; size2--) {
                    WeakReference weakReference2 = sCache.get(size2);
                    TintContextWrapper tintContextWrapper = weakReference2 == null ? null : (TintContextWrapper) weakReference2.get();
                    if (tintContextWrapper != null && tintContextWrapper.getBaseContext() == context) {
                        return tintContextWrapper;
                    }
                }
            } else {
                sCache = new ArrayList<>();
            }
            TintContextWrapper tintContextWrapper2 = new TintContextWrapper(context);
            sCache.add(new WeakReference(tintContextWrapper2));
            return tintContextWrapper2;
        }
    }

    public AssetManager getAssets() {
        return this.mResources.getAssets();
    }

    public Resources getResources() {
        return this.mResources;
    }

    public Resources.Theme getTheme() {
        return this.mTheme != null ? this.mTheme : super.getTheme();
    }

    public void setTheme(int i) {
        if (this.mTheme != null) {
            this.mTheme.applyStyle(i, true);
        } else {
            super.setTheme(i);
        }
    }
}
