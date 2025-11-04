// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class SingletonImmutableBiMap<K, V> extends ImmutableBiMap<K, V>
{
    transient ImmutableBiMap<V, K> inverse;
    final transient K singleKey;
    final transient V singleValue;
    
    SingletonImmutableBiMap(final K singleKey, final V singleValue) {
        CollectPreconditions.checkEntryNotNull(singleKey, singleValue);
        this.singleKey = singleKey;
        this.singleValue = singleValue;
    }
    
    private SingletonImmutableBiMap(final K singleKey, final V singleValue, final ImmutableBiMap<V, K> inverse) {
        this.singleKey = singleKey;
        this.singleValue = singleValue;
        this.inverse = inverse;
    }
    
    @Override
    public boolean containsKey(@Nullable final Object obj) {
        return this.singleKey.equals(obj);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object obj) {
        return this.singleValue.equals(obj);
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return ImmutableSet.of(Maps.immutableEntry(this.singleKey, this.singleValue));
    }
    
    @Override
    ImmutableSet<K> createKeySet() {
        return ImmutableSet.of(this.singleKey);
    }
    
    @Override
    public V get(@Nullable Object singleValue) {
        if (!this.singleKey.equals(singleValue)) {
            singleValue = null;
        }
        else {
            singleValue = this.singleValue;
        }
        return (V)singleValue;
    }
    
    @Override
    public ImmutableBiMap<V, K> inverse() {
        final ImmutableBiMap<V, K> inverse = this.inverse;
        if (inverse != null) {
            return inverse;
        }
        return this.inverse = (ImmutableBiMap<V, K>)new SingletonImmutableBiMap(this.singleValue, this.singleKey, (ImmutableBiMap<Object, Object>)this);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return 1;
    }
}
