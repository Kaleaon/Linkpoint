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
/* JADX INFO: Access modifiers changed from: package-private */
@Beta
@GwtCompatible(emulated = true)
@Deprecated
/* loaded from: classes.dex */
public abstract class GenericMapMaker<K0, V0> {
    @GwtIncompatible("To be supported")
    MapMaker.RemovalListener<K0, V0> removalListener;

    @GwtIncompatible("To be supported")
    /* loaded from: classes.dex */
    enum NullListener implements MapMaker.RemovalListener<Object, Object> {
        INSTANCE;

        @Override // com.google.common.collect.MapMaker.RemovalListener
        public void onRemoval(MapMaker.RemovalNotification<Object, Object> removalNotification) {
        }
    }

    public abstract GenericMapMaker<K0, V0> concurrencyLevel(int i);

    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> expireAfterAccess(long j, TimeUnit timeUnit);

    abstract GenericMapMaker<K0, V0> expireAfterWrite(long j, TimeUnit timeUnit);

    /* JADX INFO: Access modifiers changed from: package-private */
    @GwtIncompatible("To be supported")
    public <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
        return (MapMaker.RemovalListener) MoreObjects.firstNonNull(this.removalListener, NullListener.INSTANCE);
    }

    public abstract GenericMapMaker<K0, V0> initialCapacity(int i);

    @GwtIncompatible("To be supported")
    abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> equivalence);

    @Deprecated
    abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> function);

    @GwtIncompatible("MapMakerInternalMap")
    abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();

    public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();

    abstract GenericMapMaker<K0, V0> maximumSize(int i);

    @GwtIncompatible("java.lang.ref.SoftReference")
    @Deprecated
    abstract GenericMapMaker<K0, V0> softValues();

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakKeys();

    @GwtIncompatible("java.lang.ref.WeakReference")
    public abstract GenericMapMaker<K0, V0> weakValues();
}
