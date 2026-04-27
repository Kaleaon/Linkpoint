// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.concurrent.ConcurrentMap;
import com.google.common.base.Function;
import com.google.common.base.Equivalence;
import com.google.common.base.MoreObjects;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Deprecated
@Beta
@GwtCompatible(emulated = true)
abstract class GenericMapMaker<K0, V0>
{
    @GwtIncompatible("To be supported")
    MapMaker.RemovalListener<K0, V0> removalListener;
    
    public abstract GenericMapMaker<K0, V0> concurrencyLevel(final int p0);
    
    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> expireAfterAccess(final long p0, final TimeUnit p1);
    
    abstract GenericMapMaker<K0, V0> expireAfterWrite(final long p0, final TimeUnit p1);
    
    @GwtIncompatible("To be supported")
     <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
        return (MapMaker.RemovalListener<K, V>)MoreObjects.firstNonNull((MapMaker.RemovalListener)this.removalListener, NullListener.INSTANCE);
    }
    
    public abstract GenericMapMaker<K0, V0> initialCapacity(final int p0);
    
    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> keyEquivalence(final Equivalence<Object> p0);
    
    @Deprecated
    abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(final Function<? super K, ? extends V> p0);
    
    @GwtIncompatible("MapMakerInternalMap")
    abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();
    
    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();
    
    abstract GenericMapMaker<K0, V0> maximumSize(final int p0);
    
    @Deprecated
    @GwtIncompatible("java.lang.ref.SoftReference")
    abstract GenericMapMaker<K0, V0> softValues();
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakKeys();
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakValues();
    
    @GwtIncompatible("To be supported")
    enum NullListener implements RemovalListener<Object, Object>
    {
        INSTANCE;
        
        @Override
        public void onRemoval(final RemovalNotification<Object, Object> removalNotification) {
        }
    }
}
