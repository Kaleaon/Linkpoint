// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingMultimap<K, V> extends ForwardingObject implements Multimap<K, V>
{
    protected ForwardingMultimap() {
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return this.delegate().asMap();
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
    }
    
    @Override
    public boolean containsEntry(@Nullable final Object o, @Nullable final Object o2) {
        return this.delegate().containsEntry(o, o2);
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.delegate().containsKey(o);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.delegate().containsValue(o);
    }
    
    @Override
    protected abstract Multimap<K, V> delegate();
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (o == this || this.delegate().equals(o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public Collection<V> get(@Nullable final K k) {
        return this.delegate().get(k);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }
    
    @Override
    public Set<K> keySet() {
        return this.delegate().keySet();
    }
    
    @Override
    public Multiset<K> keys() {
        return this.delegate().keys();
    }
    
    @Override
    public boolean put(final K k, final V v) {
        return this.delegate().put(k, v);
    }
    
    @Override
    public boolean putAll(final Multimap<? extends K, ? extends V> multimap) {
        return this.delegate().putAll(multimap);
    }
    
    @Override
    public boolean putAll(final K k, final Iterable<? extends V> iterable) {
        return this.delegate().putAll(k, iterable);
    }
    
    @Override
    public boolean remove(@Nullable final Object o, @Nullable final Object o2) {
        return this.delegate().remove(o, o2);
    }
    
    @Override
    public Collection<V> removeAll(@Nullable final Object o) {
        return this.delegate().removeAll(o);
    }
    
    @Override
    public Collection<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k, iterable);
    }
    
    @Override
    public int size() {
        return this.delegate().size();
    }
    
    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }
}
