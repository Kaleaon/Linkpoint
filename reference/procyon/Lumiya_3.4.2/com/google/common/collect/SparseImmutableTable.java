// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
@Immutable
final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V>
{
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] iterationOrderColumn;
    private final int[] iterationOrderRow;
    private final ImmutableMap<R, Map<C, V>> rowMap;
    
    SparseImmutableTable(final ImmutableList<Cell<R, C, V>> list, final ImmutableSet<R> set, final ImmutableSet<C> set2) {
        final ImmutableMap<R, Integer> indexMap = Maps.indexMap(set);
        final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            linkedHashMap.put(iterator.next(), new LinkedHashMap<Object, Object>());
        }
        final LinkedHashMap<Object, Object> linkedHashMap2 = Maps.newLinkedHashMap();
        final Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            linkedHashMap2.put(iterator2.next(), new LinkedHashMap<Object, Object>());
        }
        final int[] iterationOrderRow = new int[list.size()];
        final int[] iterationOrderColumn = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            final Cell cell = (Cell)list.get(i);
            final Object rowKey = cell.getRowKey();
            final Object columnKey = cell.getColumnKey();
            final Object value = cell.getValue();
            iterationOrderRow[i] = (int)indexMap.get(rowKey);
            final Map map = (Map)linkedHashMap.get(rowKey);
            iterationOrderColumn[i] = map.size();
            final Object put = map.put(columnKey, value);
            if (put != null) {
                throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + put);
            }
            ((Map<Object, Object>)linkedHashMap2.get(columnKey)).put(rowKey, value);
        }
        this.iterationOrderRow = iterationOrderRow;
        this.iterationOrderColumn = iterationOrderColumn;
        final ImmutableMap.Builder builder = new ImmutableMap.Builder<R, ImmutableMap<Object, Object>>(linkedHashMap.size());
        for (final Map.Entry<Object, V> entry : linkedHashMap.entrySet()) {
            builder.put((R)entry.getKey(), ImmutableMap.copyOf((Map<?, ?>)entry.getValue()));
        }
        this.rowMap = (ImmutableMap<R, Map<C, V>>)builder.build();
        final ImmutableMap.Builder builder2 = new ImmutableMap.Builder<C, ImmutableMap<Object, Object>>(linkedHashMap2.size());
        for (final Map.Entry<Object, V> entry2 : linkedHashMap2.entrySet()) {
            builder2.put((C)entry2.getKey(), ImmutableMap.copyOf((Map<?, ?>)entry2.getValue()));
        }
        this.columnMap = (ImmutableMap<C, Map<R, V>>)builder2.build();
    }
    
    @Override
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }
    
    @Override
    Cell<R, C, V> getCell(int n) {
        final Map.Entry<K, ImmutableMap> entry = this.rowMap.entrySet().asList().get(this.iterationOrderRow[n]);
        final ImmutableMap immutableMap = entry.getValue();
        n = this.iterationOrderColumn[n];
        final Map.Entry entry2 = (Map.Entry)immutableMap.entrySet().asList().get(n);
        return ImmutableTable.cellOf((R)entry.getKey(), (C)entry2.getKey(), entry2.getValue());
    }
    
    @Override
    V getValue(int n) {
        final ImmutableMap immutableMap = this.rowMap.values().asList().get(this.iterationOrderRow[n]);
        n = this.iterationOrderColumn[n];
        return (V)immutableMap.values().asList().get(n);
    }
    
    @Override
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }
    
    @Override
    public int size() {
        return this.iterationOrderRow.length;
    }
}
