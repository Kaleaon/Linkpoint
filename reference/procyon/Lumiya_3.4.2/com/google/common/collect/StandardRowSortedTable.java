// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.Set;
import com.google.common.base.Supplier;
import java.util.Map;
import java.util.SortedMap;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class StandardRowSortedTable<R, C, V> extends StandardTable<R, C, V> implements RowSortedTable<R, C, V>
{
    private static final long serialVersionUID = 0L;
    
    StandardRowSortedTable(final SortedMap<R, Map<C, V>> sortedMap, final Supplier<? extends Map<C, V>> supplier) {
        super(sortedMap, supplier);
    }
    
    private SortedMap<R, Map<C, V>> sortedBackingMap() {
        return (SortedMap)this.backingMap;
    }
    
    @Override
    SortedMap<R, Map<C, V>> createRowMap() {
        return new RowSortedMap();
    }
    
    @Override
    public SortedSet<R> rowKeySet() {
        return (SortedSet)this.rowMap().keySet();
    }
    
    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return (SortedMap)super.rowMap();
    }
    
    private class RowSortedMap extends RowMap implements SortedMap<R, Map<C, V>>
    {
        @Override
        public Comparator<? super R> comparator() {
            return StandardRowSortedTable.this.sortedBackingMap().comparator();
        }
        
        SortedSet<R> createKeySet() {
            return new Maps.SortedKeySet<R, Object>(this);
        }
        
        @Override
        public R firstKey() {
            return StandardRowSortedTable.this.sortedBackingMap().firstKey();
        }
        
        @Override
        public SortedMap<R, Map<C, V>> headMap(final R r) {
            Preconditions.checkNotNull(r);
            return new StandardRowSortedTable((SortedMap<Object, Map<Object, Object>>)StandardRowSortedTable.this.sortedBackingMap().headMap(r), (Supplier<? extends Map<Object, Object>>)StandardRowSortedTable.this.factory).rowMap();
        }
        
        @Override
        public SortedSet<R> keySet() {
            return (SortedSet)super.keySet();
        }
        
        @Override
        public R lastKey() {
            return StandardRowSortedTable.this.sortedBackingMap().lastKey();
        }
        
        @Override
        public SortedMap<R, Map<C, V>> subMap(final R r, final R r2) {
            Preconditions.checkNotNull(r);
            Preconditions.checkNotNull(r2);
            return new StandardRowSortedTable((SortedMap<Object, Map<Object, Object>>)StandardRowSortedTable.this.sortedBackingMap().subMap(r, r2), (Supplier<? extends Map<Object, Object>>)StandardRowSortedTable.this.factory).rowMap();
        }
        
        @Override
        public SortedMap<R, Map<C, V>> tailMap(final R r) {
            Preconditions.checkNotNull(r);
            return new StandardRowSortedTable((SortedMap<Object, Map<Object, Object>>)StandardRowSortedTable.this.sortedBackingMap().tailMap(r), (Supplier<? extends Map<Object, Object>>)StandardRowSortedTable.this.factory).rowMap();
        }
    }
}
