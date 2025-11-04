// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.io.Serializable;
import com.google.common.base.Objects;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import com.google.common.base.Function;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Tables
{
    private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER;
    
    static {
        UNMODIFIABLE_WRAPPER = new Function<Map<Object, Object>, Map<Object, Object>>() {
            @Override
            public Map<Object, Object> apply(final Map<Object, Object> m) {
                return Collections.unmodifiableMap((Map<?, ?>)m);
            }
        };
    }
    
    private Tables() {
    }
    
    static boolean equalsImpl(final Table<?, ?, ?> table, @Nullable final Object o) {
        return o == table || (o instanceof Table && table.cellSet().equals(((Table)o).cellSet()));
    }
    
    public static <R, C, V> Table.Cell<R, C, V> immutableCell(@Nullable final R r, @Nullable final C c, @Nullable final V v) {
        return new ImmutableCell<R, C, V>(r, c, v);
    }
    
    @Beta
    public static <R, C, V> Table<R, C, V> newCustomTable(final Map<R, Map<C, V>> map, final Supplier<? extends Map<C, V>> supplier) {
        Preconditions.checkArgument(map.isEmpty());
        Preconditions.checkNotNull(supplier);
        return new StandardTable<R, C, V>(map, supplier);
    }
    
    @Beta
    public static <R, C, V1, V2> Table<R, C, V2> transformValues(final Table<R, C, V1> table, final Function<? super V1, V2> function) {
        return (Table<R, C, V2>)new TransformedTable((Table<Object, Object, Object>)table, (Function<? super Object, Object>)function);
    }
    
    public static <R, C, V> Table<C, R, V> transpose(final Table<R, C, V> table) {
        Table<R, C, V> original;
        if (!(table instanceof TransposeTable)) {
            original = (Table<R, C, V>)new TransposeTable((Table<Object, Object, Object>)table);
        }
        else {
            original = ((TransposeTable)table).original;
        }
        return (Table<C, R, V>)original;
    }
    
    @Beta
    public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(final RowSortedTable<R, ? extends C, ? extends V> rowSortedTable) {
        return new UnmodifiableRowSortedMap<R, C, V>(rowSortedTable);
    }
    
    public static <R, C, V> Table<R, C, V> unmodifiableTable(final Table<? extends R, ? extends C, ? extends V> table) {
        return new UnmodifiableTable<R, C, V>(table);
    }
    
    private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper() {
        return (Function<Map<K, V>, Map<K, V>>)Tables.UNMODIFIABLE_WRAPPER;
    }
    
    abstract static class AbstractCell<R, C, V> implements Cell<R, C, V>
    {
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (o == this) {
                return true;
            }
            if (!(o instanceof Cell)) {
                return false;
            }
            final Cell cell = (Cell)o;
            if (!Objects.equal(this.getRowKey(), cell.getRowKey()) || !Objects.equal(this.getColumnKey(), cell.getColumnKey()) || !Objects.equal(this.getValue(), cell.getValue())) {
                b = false;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.getRowKey(), this.getColumnKey(), this.getValue());
        }
        
        @Override
        public String toString() {
            return "(" + this.getRowKey() + "," + this.getColumnKey() + ")=" + this.getValue();
        }
    }
    
    static final class ImmutableCell<R, C, V> extends AbstractCell<R, C, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final C columnKey;
        private final R rowKey;
        private final V value;
        
        ImmutableCell(@Nullable final R rowKey, @Nullable final C columnKey, @Nullable final V value) {
            this.rowKey = rowKey;
            this.columnKey = columnKey;
            this.value = value;
        }
        
        @Override
        public C getColumnKey() {
            return this.columnKey;
        }
        
        @Override
        public R getRowKey() {
            return this.rowKey;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
    }
    
    private static class TransformedTable<R, C, V1, V2> extends AbstractTable<R, C, V2>
    {
        final Table<R, C, V1> fromTable;
        final Function<? super V1, V2> function;
        
        TransformedTable(final Table<R, C, V1> table, final Function<? super V1, V2> function) {
            this.fromTable = Preconditions.checkNotNull(table);
            this.function = Preconditions.checkNotNull(function);
        }
        
        Function<Cell<R, C, V1>, Cell<R, C, V2>> cellFunction() {
            return new Function<Cell<R, C, V1>, Cell<R, C, V2>>() {
                @Override
                public Cell<R, C, V2> apply(final Cell<R, C, V1> cell) {
                    return Tables.immutableCell(cell.getRowKey(), cell.getColumnKey(), TransformedTable.this.function.apply(cell.getValue()));
                }
            };
        }
        
        @Override
        Iterator<Cell<R, C, V2>> cellIterator() {
            return Iterators.transform(this.fromTable.cellSet().iterator(), (Function<? super Table.Cell<R, C, V1>, ? extends Cell<R, C, V2>>)this.cellFunction());
        }
        
        @Override
        public void clear() {
            this.fromTable.clear();
        }
        
        @Override
        public Map<R, V2> column(final C c) {
            return Maps.transformValues(this.fromTable.column(c), this.function);
        }
        
        @Override
        public Set<C> columnKeySet() {
            return this.fromTable.columnKeySet();
        }
        
        @Override
        public Map<C, Map<R, V2>> columnMap() {
            return Maps.transformValues(this.fromTable.columnMap(), (Function<? super Map<R, V1>, Map<R, V2>>)new Function<Map<R, V1>, Map<R, V2>>() {
                @Override
                public Map<R, V2> apply(final Map<R, V1> map) {
                    return Maps.transformValues(map, TransformedTable.this.function);
                }
            });
        }
        
        @Override
        public boolean contains(final Object o, final Object o2) {
            return this.fromTable.contains(o, o2);
        }
        
        @Override
        Collection<V2> createValues() {
            return Collections2.transform(this.fromTable.values(), this.function);
        }
        
        @Override
        public V2 get(Object apply, final Object o) {
            if (!this.contains(apply, o)) {
                apply = null;
            }
            else {
                apply = this.function.apply(this.fromTable.get(apply, o));
            }
            return (V2)apply;
        }
        
        @Override
        public V2 put(final R r, final C c, final V2 v2) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Table<? extends R, ? extends C, ? extends V2> table) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V2 remove(Object apply, final Object o) {
            if (!this.contains(apply, o)) {
                apply = null;
            }
            else {
                apply = this.function.apply(this.fromTable.remove(apply, o));
            }
            return (V2)apply;
        }
        
        @Override
        public Map<C, V2> row(final R r) {
            return Maps.transformValues(this.fromTable.row(r), this.function);
        }
        
        @Override
        public Set<R> rowKeySet() {
            return this.fromTable.rowKeySet();
        }
        
        @Override
        public Map<R, Map<C, V2>> rowMap() {
            return Maps.transformValues(this.fromTable.rowMap(), (Function<? super Map<C, V1>, Map<C, V2>>)new Function<Map<C, V1>, Map<C, V2>>() {
                @Override
                public Map<C, V2> apply(final Map<C, V1> map) {
                    return Maps.transformValues(map, TransformedTable.this.function);
                }
            });
        }
        
        @Override
        public int size() {
            return this.fromTable.size();
        }
    }
    
    private static class TransposeTable<C, R, V> extends AbstractTable<C, R, V>
    {
        private static final Function<Cell<?, ?, ?>, Cell<?, ?, ?>> TRANSPOSE_CELL;
        final Table<R, C, V> original;
        
        static {
            TRANSPOSE_CELL = new Function<Cell<?, ?, ?>, Cell<?, ?, ?>>() {
                @Override
                public Cell<?, ?, ?> apply(final Cell<?, ?, ?> cell) {
                    return Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue());
                }
            };
        }
        
        TransposeTable(final Table<R, C, V> table) {
            this.original = Preconditions.checkNotNull(table);
        }
        
        @Override
        Iterator<Cell<C, R, V>> cellIterator() {
            return Iterators.transform(this.original.cellSet().iterator(), (Function<? super Table.Cell<R, C, V>, ? extends Cell<C, R, V>>)TransposeTable.TRANSPOSE_CELL);
        }
        
        @Override
        public void clear() {
            this.original.clear();
        }
        
        @Override
        public Map<C, V> column(final R r) {
            return this.original.row(r);
        }
        
        @Override
        public Set<R> columnKeySet() {
            return this.original.rowKeySet();
        }
        
        @Override
        public Map<R, Map<C, V>> columnMap() {
            return this.original.rowMap();
        }
        
        @Override
        public boolean contains(@Nullable final Object o, @Nullable final Object o2) {
            return this.original.contains(o2, o);
        }
        
        @Override
        public boolean containsColumn(@Nullable final Object o) {
            return this.original.containsRow(o);
        }
        
        @Override
        public boolean containsRow(@Nullable final Object o) {
            return this.original.containsColumn(o);
        }
        
        @Override
        public boolean containsValue(@Nullable final Object o) {
            return this.original.containsValue(o);
        }
        
        @Override
        public V get(@Nullable final Object o, @Nullable final Object o2) {
            return this.original.get(o2, o);
        }
        
        @Override
        public V put(final C c, final R r, final V v) {
            return this.original.put(r, c, v);
        }
        
        @Override
        public void putAll(final Table<? extends C, ? extends R, ? extends V> table) {
            this.original.putAll(Tables.transpose(table));
        }
        
        @Override
        public V remove(@Nullable final Object o, @Nullable final Object o2) {
            return this.original.remove(o2, o);
        }
        
        @Override
        public Map<R, V> row(final C c) {
            return this.original.column(c);
        }
        
        @Override
        public Set<C> rowKeySet() {
            return this.original.columnKeySet();
        }
        
        @Override
        public Map<C, Map<R, V>> rowMap() {
            return this.original.columnMap();
        }
        
        @Override
        public int size() {
            return this.original.size();
        }
        
        @Override
        public Collection<V> values() {
            return this.original.values();
        }
    }
    
    static final class UnmodifiableRowSortedMap<R, C, V> extends UnmodifiableTable<R, C, V> implements RowSortedTable<R, C, V>
    {
        private static final long serialVersionUID = 0L;
        
        public UnmodifiableRowSortedMap(final RowSortedTable<R, ? extends C, ? extends V> rowSortedTable) {
            super(rowSortedTable);
        }
        
        @Override
        protected RowSortedTable<R, C, V> delegate() {
            return (RowSortedTable)super.delegate();
        }
        
        @Override
        public SortedSet<R> rowKeySet() {
            return Collections.unmodifiableSortedSet(this.delegate().rowKeySet());
        }
        
        @Override
        public SortedMap<R, Map<C, V>> rowMap() {
            return Collections.unmodifiableSortedMap((SortedMap<R, ? extends Map<C, V>>)Maps.transformValues((SortedMap<K, Map<C, V>>)this.delegate().rowMap(), (Function<? super Map<C, V>, ? extends V>)unmodifiableWrapper()));
        }
    }
    
    private static class UnmodifiableTable<R, C, V> extends ForwardingTable<R, C, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Table<? extends R, ? extends C, ? extends V> delegate;
        
        UnmodifiableTable(final Table<? extends R, ? extends C, ? extends V> table) {
            this.delegate = Preconditions.checkNotNull(table);
        }
        
        @Override
        public Set<Cell<R, C, V>> cellSet() {
            return Collections.unmodifiableSet((Set<? extends Cell<R, C, V>>)super.cellSet());
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Map<R, V> column(@Nullable final C c) {
            return Collections.unmodifiableMap(super.column(c));
        }
        
        @Override
        public Set<C> columnKeySet() {
            return Collections.unmodifiableSet(super.columnKeySet());
        }
        
        @Override
        public Map<C, Map<R, V>> columnMap() {
            return Collections.unmodifiableMap((Map<? extends C, ? extends Map<R, V>>)Maps.transformValues(super.columnMap(), (Function<? super Map<Object, Object>, ? extends V>)unmodifiableWrapper()));
        }
        
        @Override
        protected Table<R, C, V> delegate() {
            return (Table<R, C, V>)this.delegate;
        }
        
        @Override
        public V put(@Nullable final R r, @Nullable final C c, @Nullable final V v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V remove(@Nullable final Object o, @Nullable final Object o2) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Map<C, V> row(@Nullable final R r) {
            return Collections.unmodifiableMap(super.row(r));
        }
        
        @Override
        public Set<R> rowKeySet() {
            return Collections.unmodifiableSet(super.rowKeySet());
        }
        
        @Override
        public Map<R, Map<C, V>> rowMap() {
            return Collections.unmodifiableMap((Map<? extends R, ? extends Map<C, V>>)Maps.transformValues(super.rowMap(), (Function<? super Map<Object, Object>, ? extends V>)unmodifiableWrapper()));
        }
        
        @Override
        public Collection<V> values() {
            return Collections.unmodifiableCollection(super.values());
        }
    }
}
