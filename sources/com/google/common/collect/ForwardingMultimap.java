package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingMultimap<K, V> extends ForwardingObject implements Multimap<K, V> {
    protected ForwardingMultimap() {
    }

    public Map<K, Collection<V>> asMap() {
        return delegate().asMap();
    }

    public void clear() {
        delegate().clear();
    }

    public boolean containsEntry(@Nullable Object obj, @Nullable Object obj2) {
        return delegate().containsEntry(obj, obj2);
    }

    public boolean containsKey(@Nullable Object obj) {
        return delegate().containsKey(obj);
    }

    public boolean containsValue(@Nullable Object obj) {
        return delegate().containsValue(obj);
    }

    /* access modifiers changed from: protected */
    public abstract Multimap<K, V> delegate();

    public Collection<Map.Entry<K, V>> entries() {
        return delegate().entries();
    }

    public boolean equals(@Nullable Object obj) {
        return obj == this || delegate().equals(obj);
    }

    public Collection<V> get(@Nullable K k) {
        return delegate().get(k);
    }

    public int hashCode() {
        return delegate().hashCode();
    }

    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    public Set<K> keySet() {
        return delegate().keySet();
    }

    public Multiset<K> keys() {
        return delegate().keys();
    }

    public boolean put(K k, V v) {
        return delegate().put(k, v);
    }

    public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
        return delegate().putAll(multimap);
    }

    public boolean putAll(K k, Iterable<? extends V> iterable) {
        return delegate().putAll(k, iterable);
    }

    public boolean remove(@Nullable Object obj, @Nullable Object obj2) {
        return delegate().remove(obj, obj2);
    }

    public Collection<V> removeAll(@Nullable Object obj) {
        return delegate().removeAll(obj);
    }

    public Collection<V> replaceValues(K k, Iterable<? extends V> iterable) {
        return delegate().replaceValues(k, iterable);
    }

    public int size() {
        return delegate().size();
    }

    public Collection<V> values() {
        return delegate().values();
    }
}
