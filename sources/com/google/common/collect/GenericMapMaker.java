package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@GwtCompatible(emulated = true)
@Deprecated
@Beta
abstract class GenericMapMaker<K0, V0> {
    @GwtIncompatible("To be supported")
    MapMaker.RemovalListener<K0, V0> removalListener;

    @GwtIncompatible("To be supported")
    enum NullListener implements MapMaker.RemovalListener<Object, Object> {
        INSTANCE;

        public void onRemoval(MapMaker.RemovalNotification<Object, Object> removalNotification) {
        }
    }

    GenericMapMaker() {
    }

    public abstract GenericMapMaker<K0, V0> concurrencyLevel(int i);

    /* access modifiers changed from: package-private */
    @GwtIncompatible("To be supported")
    public abstract GenericMapMaker<K0, V0> expireAfterAccess(long j, TimeUnit timeUnit);

    /* access modifiers changed from: package-private */
    public abstract GenericMapMaker<K0, V0> expireAfterWrite(long j, TimeUnit timeUnit);

    /* access modifiers changed from: package-private */
    @GwtIncompatible("To be supported")
    public <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
        return (MapMaker.RemovalListener) MoreObjects.firstNonNull(this.removalListener, NullListener.INSTANCE);
    }

    public abstract GenericMapMaker<K0, V0> initialCapacity(int i);

    /* access modifiers changed from: package-private */
    @GwtIncompatible("To be supported")
    public abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> equivalence);

    /* access modifiers changed from: package-private */
    @Deprecated
    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> function);

    /* access modifiers changed from: package-private */
    @GwtIncompatible("MapMakerInternalMap")
    public abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();

    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();

    /* access modifiers changed from: package-private */
    public abstract GenericMapMaker<K0, V0> maximumSize(int i);

    /* access modifiers changed from: package-private */
    @GwtIncompatible("java.lang.ref.SoftReference")
    @Deprecated
    public abstract GenericMapMaker<K0, V0> softValues();

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakKeys();

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakValues();
}
