// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingTable<R, C, V> extends ForwardingObject implements Table<R, C, V>
{
    protected ForwardingTable() {
    }
    
    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return this.delegate().cellSet();
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
    }
    
    @Override
    public Map<R, V> column(final C c) {
        return this.delegate().column(c);
    }
    
    @Override
    public Set<C> columnKeySet() {
        return this.delegate().columnKeySet();
    }
    
    @Override
    public Map<C, Map<R, V>> columnMap() {
        return this.delegate().columnMap();
    }
    
    @Override
    public boolean contains(final Object o, final Object o2) {
        return this.delegate().contains(o, o2);
    }
    
    @Override
    public boolean containsColumn(final Object o) {
        return this.delegate().containsColumn(o);
    }
    
    @Override
    public boolean containsRow(final Object o) {
        return this.delegate().containsRow(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.delegate().containsValue(o);
    }
    
    @Override
    protected abstract Table<R, C, V> delegate();
    
    @Override
    public boolean equals(final Object o) {
        boolean b = false;
        if (o == this || this.delegate().equals(o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public V get(final Object o, final Object o2) {
        return this.delegate().get(o, o2);
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
    public V put(final R r, final C c, final V v) {
        return this.delegate().put(r, c, v);
    }
    
    @Override
    public void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
        this.delegate().putAll(table);
    }
    
    @Override
    public V remove(final Object o, final Object o2) {
        return this.delegate().remove(o, o2);
    }
    
    @Override
    public Map<C, V> row(final R r) {
        return this.delegate().row(r);
    }
    
    @Override
    public Set<R> rowKeySet() {
        return this.delegate().rowKeySet();
    }
    
    @Override
    public Map<R, Map<C, V>> rowMap() {
        return this.delegate().rowMap();
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
