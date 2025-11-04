// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import android.support.annotation.CallSuper;
import android.arch.lifecycle.ReportFragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleRegistry;
import android.support.v4.util.SimpleArrayMap;
import android.support.annotation.RestrictTo;
import android.arch.lifecycle.LifecycleOwner;
import android.app.Activity;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class SupportActivity extends Activity implements LifecycleOwner
{
    private SimpleArrayMap<Class<? extends ExtraData>, ExtraData> mExtraDataMap;
    private LifecycleRegistry mLifecycleRegistry;
    
    public SupportActivity() {
        this.mExtraDataMap = new SimpleArrayMap<Class<? extends ExtraData>, ExtraData>();
        this.mLifecycleRegistry = new LifecycleRegistry(this);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public <T extends ExtraData> T getExtraData(final Class<T> clazz) {
        return (T)this.mExtraDataMap.get(clazz);
    }
    
    public Lifecycle getLifecycle() {
        return this.mLifecycleRegistry;
    }
    
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        ReportFragment.injectIfNeededIn(this);
    }
    
    @CallSuper
    protected void onSaveInstanceState(final Bundle bundle) {
        this.mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        super.onSaveInstanceState(bundle);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public void putExtraData(final ExtraData extraData) {
        this.mExtraDataMap.put(extraData.getClass(), extraData);
    }
    
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static class ExtraData
    {
    }
}
