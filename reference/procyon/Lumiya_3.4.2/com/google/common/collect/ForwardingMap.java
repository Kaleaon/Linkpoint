// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import com.google.common.base.Objects;
import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V>
{
    protected ForwardingMap() {
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
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
    protected abstract Map<K, V> delegate();
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.delegate().entrySet();
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
    public V get(@Nullable final Object o) {
        return this.delegate().get(o);
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
    public V put(final K k, final V v) {
        return this.delegate().put(k, v);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.delegate().putAll(map);
    }
    
    @Override
    public V remove(final Object o) {
        return this.delegate().remove(o);
    }
    
    @Override
    public int size() {
        return this.delegate().size();
    }
    
    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }
    
    @Beta
    protected boolean standardContainsKey(@Nullable final Object o) {
        return Maps.containsKeyImpl(this, o);
    }
    
    protected boolean standardContainsValue(@Nullable final Object o) {
        return Maps.containsValueImpl(this, o);
    }
    
    protected boolean standardEquals(@Nullable final Object o) {
        return Maps.equalsImpl(this, o);
    }
    
    protected int standardHashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }
    
    protected boolean standardIsEmpty() {
        boolean b = false;
        if (!this.entrySet().iterator().hasNext()) {
            b = true;
        }
        return b;
    }
    
    protected void standardPutAll(final Map<? extends K, ? extends V> map) {
        Maps.putAllImpl((Map<Object, Object>)this, map);
    }
    
    @Beta
    protected V standardRemove(@Nullable Object value) {
        final Iterator<Entry<K, V>> iterator = this.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<Object, V> entry = (Entry<Object, V>)iterator.next();
            if (Objects.equal(entry.getKey(), value)) {
                value = entry.getValue();
                iterator.remove();
                return (V)value;
            }
        }
        return null;
    }
    
    protected String standardToString() {
        return Maps.toStringImpl(this);
    }
    
    @Override
    public Collection<V> values() {
        return this.delegate().values();
    }
    
    @Beta
    protected abstract class StandardEntrySet extends EntrySet<K, V>
    {
        public StandardEntrySet() {
        }
        
        @Override
        Map<K, V> map() {
            return ForwardingMap.this;
        }
    }
    
    @Beta
    protected class StandardKeySet extends KeySet<K, V>
    {
        public StandardKeySet() {
            super(ForwardingMap.this);
        }
    }
    
    @Beta
    protected class StandardValues extends Values<K, V>
    {
        public StandardValues() {
            super(ForwardingMap.this);
        }
    }
}
