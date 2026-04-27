// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V>
{
    final C singleColumnKey;
    final R singleRowKey;
    final V singleValue;
    
    SingletonImmutableTable(final Cell<R, C, V> cell) {
        this((Object)cell.getRowKey(), cell.getColumnKey(), (V)cell.getValue());
    }
    
    SingletonImmutableTable(final R r, final C c, final V v) {
        this.singleRowKey = Preconditions.checkNotNull(r);
        this.singleColumnKey = Preconditions.checkNotNull(c);
        this.singleValue = Preconditions.checkNotNull(v);
    }
    
    @Override
    public ImmutableMap<R, V> column(final C c) {
        Preconditions.checkNotNull(c);
        Map<K, V> map;
        if (!this.containsColumn(c)) {
            map = (Map<K, V>)ImmutableMap.of();
        }
        else {
            map = (Map<K, V>)ImmutableMap.of(this.singleRowKey, this.singleValue);
        }
        return (ImmutableMap<R, V>)map;
    }
    
    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return (ImmutableMap<C, Map<R, V>>)ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
    }
    
    @Override
    ImmutableSet<Cell<R, C, V>> createCellSet() {
        return ImmutableSet.of(ImmutableTable.cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
    }
    
    @Override
    ImmutableCollection<V> createValues() {
        return ImmutableSet.of(this.singleValue);
    }
    
    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return (ImmutableMap<R, Map<C, V>>)ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
    }
    
    @Override
    public int size() {
        return 1;
    }
}
