// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v7.widget;

import android.content.res.AssetManager;
import android.os.Build$VERSION;
import android.support.annotation.NonNull;
import android.content.Context;
import android.content.res.Resources$Theme;
import android.content.res.Resources;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.support.annotation.RestrictTo;
import android.content.ContextWrapper;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class TintContextWrapper extends ContextWrapper
{
    private static final Object CACHE_LOCK;
    private static ArrayList<WeakReference<TintContextWrapper>> sCache;
    private final Resources mResources;
    private final Resources$Theme mTheme;
    
    static {
        CACHE_LOCK = new Object();
    }
    
    private TintContextWrapper(@NonNull final Context context) {
        super(context);
        if (!VectorEnabledTintResources.shouldBeUsed()) {
            this.mResources = new TintResources((Context)this, context.getResources());
            this.mTheme = null;
        }
        else {
            this.mResources = new VectorEnabledTintResources((Context)this, context.getResources());
            (this.mTheme = this.mResources.newTheme()).setTo(context.getTheme());
        }
    }
    
    private static boolean shouldWrap(@NonNull final Context context) {
        boolean b = false;
        if (!(context instanceof TintContextWrapper) && !(context.getResources() instanceof TintResources) && !(context.getResources() instanceof VectorEnabledTintResources)) {
            if (Build$VERSION.SDK_INT < 21 || VectorEnabledTintResources.shouldBeUsed()) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    public static Context wrap(@NonNull final Context context) {
        if (!shouldWrap(context)) {
            return context;
        }
        TintContextWrapper tintContextWrapper = null;
    Label_0043_Outer:
        while (true) {
            while (true) {
                int index = 0;
            Label_0140:
                while (true) {
                    Label_0101: {
                        synchronized (TintContextWrapper.CACHE_LOCK) {
                            if (TintContextWrapper.sCache != null) {
                                index = TintContextWrapper.sCache.size() - 1;
                                if (index >= 0) {
                                    break Label_0101;
                                }
                                index = TintContextWrapper.sCache.size() - 1;
                                if (index >= 0) {
                                    break Label_0140;
                                }
                            }
                            else {
                                TintContextWrapper.sCache = new ArrayList<WeakReference<TintContextWrapper>>();
                            }
                            final TintContextWrapper referent = new TintContextWrapper(context);
                            TintContextWrapper.sCache.add(new WeakReference<TintContextWrapper>(referent));
                            return (Context)referent;
                        }
                    }
                    final WeakReference weakReference = TintContextWrapper.sCache.get(index);
                    if (weakReference == null || weakReference.get() == null) {
                        TintContextWrapper.sCache.remove(index);
                    }
                    --index;
                    continue Label_0043_Outer;
                }
                final WeakReference weakReference2 = TintContextWrapper.sCache.get(index);
                if (weakReference2 == null) {
                    tintContextWrapper = null;
                }
                else {
                    tintContextWrapper = (TintContextWrapper)weakReference2.get();
                }
                if (tintContextWrapper != null && tintContextWrapper.getBaseContext() == context) {
                    break;
                }
                --index;
                continue;
            }
        }
        final Object o;
        monitorexit(o);
        return (Context)tintContextWrapper;
    }
    
    public AssetManager getAssets() {
        return this.mResources.getAssets();
    }
    
    public Resources getResources() {
        return this.mResources;
    }
    
    public Resources$Theme getTheme() {
        Resources$Theme resources$Theme;
        if (this.mTheme != null) {
            resources$Theme = this.mTheme;
        }
        else {
            resources$Theme = super.getTheme();
        }
        return resources$Theme;
    }
    
    public void setTheme(final int theme) {
        if (this.mTheme != null) {
            this.mTheme.applyStyle(theme, true);
        }
        else {
            super.setTheme(theme);
        }
    }
}
