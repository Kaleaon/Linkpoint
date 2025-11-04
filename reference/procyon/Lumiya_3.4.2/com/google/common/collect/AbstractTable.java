// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Map;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractTable<R, C, V> implements Table<R, C, V>
{
    private transient Set<Cell<R, C, V>> cellSet;
    private transient Collection<V> values;
    
    abstract Iterator<Cell<R, C, V>> cellIterator();
    
    @Override
    public Set<Cell<R, C, V>> cellSet() {
        Set<Cell<R, C, V>> cellSet = this.cellSet;
        if (cellSet == null) {
            cellSet = this.createCellSet();
            this.cellSet = cellSet;
        }
        return cellSet;
    }
    
    @Override
    public void clear() {
        Iterators.clear(this.cellSet().iterator());
    }
    
    @Override
    public Set<C> columnKeySet() {
        return this.columnMap().keySet();
    }
    
    @Override
    public boolean contains(@Nullable final Object o, @Nullable final Object o2) {
        final Map<Object, Object> map = Maps.safeGet(this.rowMap(), o);
        return map != null && Maps.safeContainsKey(map, o2);
    }
    
    @Override
    public boolean containsColumn(@Nullable final Object o) {
        return Maps.safeContainsKey(this.columnMap(), o);
    }
    
    @Override
    public boolean containsRow(@Nullable final Object o) {
        return Maps.safeContainsKey(this.rowMap(), o);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        final Iterator<Map<C, V>> iterator = (Iterator<Map<C, V>>)this.rowMap().values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().containsValue(o)) {
                return true;
            }
        }
        return false;
    }
    
    Set<Cell<R, C, V>> createCellSet() {
        return new CellSet();
    }
    
    Collection<V> createValues() {
        return new Values();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return Tables.equalsImpl(this, o);
    }
    
    @Override
    public V get(@Nullable Object safeGet, @Nullable final Object o) {
        final Map<Object, Object> map = Maps.safeGet(this.rowMap(), safeGet);
        if (map != null) {
            safeGet = Maps.safeGet(map, o);
        }
        else {
            safeGet = null;
        }
        return (V)safeGet;
    }
    
    @Override
    public int hashCode() {
        return this.cellSet().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        boolean b = false;
        if (this.size() == 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public V put(final R r, final C c, final V v) {
        return this.row(r).put(c, v);
    }
    
    @Override
    public void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
        for (final Cell<R, C, V> cell : table.cellSet()) {
            this.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
    
    @Override
    public V remove(@Nullable Object safeRemove, @Nullable final Object o) {
        final Map<Object, Object> map = Maps.safeGet(this.rowMap(), safeRemove);
        if (map != null) {
            safeRemove = Maps.safeRemove(map, o);
        }
        else {
            safeRemove = null;
        }
        return (V)safeRemove;
    }
    
    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }
    
    @Override
    public String toString() {
        return this.rowMap().toString();
    }
    
    @Override
    public Collection<V> values() {
        Collection<V> values = this.values;
        if (values == null) {
            values = this.createValues();
            this.values = values;
        }
        return values;
    }
    
    Iterator<V> valuesIterator() {
        return new TransformedIterator<Cell<R, C, V>, V>(this.cellSet().iterator()) {
            @Override
            V transform(final Cell<R, C, V> cell) {
                return cell.getValue();
            }
        };
    }
    
    class CellSet extends AbstractSet<Cell<R, C, V>>
    {
        @Override
        public void clear() {
            AbstractTable.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Cell)) {
                return false;
            }
            final Cell cell = (Cell)o;
            final Map<Object, Object> map = Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
            return map != null && Collections2.safeContains(map.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
        }
        
        @Override
        public Iterator<Cell<R, C, V>> iterator() {
            return AbstractTable.this.cellIterator();
        }
        
        @Override
        public boolean remove(@Nullable final Object o) {
            if (!(o instanceof Cell)) {
                return false;
            }
            final Cell cell = (Cell)o;
            final Map<Object, Object> map = Maps.safeGet(AbstractTable.this.rowMap(), cell.getRowKey());
            return map != null && Collections2.safeRemove(map.entrySet(), Maps.immutableEntry(cell.getColumnKey(), cell.getValue()));
        }
        
        @Override
        public int size() {
            return AbstractTable.this.size();
        }
    }
    
    class Values extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            AbstractTable.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return AbstractTable.this.containsValue(o);
        }
        
        @Override
        public Iterator<V> iterator() {
            return AbstractTable.this.valuesIterator();
        }
        
        @Override
        public int size() {
            return AbstractTable.this.size();
        }
    }
}
