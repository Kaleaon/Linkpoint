// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.List;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.base.MoreObjects;
import java.util.Set;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ImmutableTable<R, C, V> extends AbstractTable<R, C, V>
{
    private static final ImmutableTable<Object, Object, Object> EMPTY;
    
    static {
        EMPTY = new SparseImmutableTable<Object, Object, Object>(ImmutableList.of(), ImmutableSet.of(), ImmutableSet.of());
    }
    
    ImmutableTable() {
    }
    
    public static <R, C, V> Builder<R, C, V> builder() {
        return new Builder<R, C, V>();
    }
    
    static <R, C, V> Cell<R, C, V> cellOf(final R r, final C c, final V v) {
        return Tables.immutableCell((R)Preconditions.checkNotNull((R)r), (C)Preconditions.checkNotNull((C)c), (V)Preconditions.checkNotNull((V)v));
    }
    
    public static <R, C, V> ImmutableTable<R, C, V> copyOf(final Table<? extends R, ? extends C, ? extends V> table) {
        if (table instanceof ImmutableTable) {
            return (ImmutableTable<R, C, V>)table;
        }
        final int size = table.size();
        switch (size) {
            default: {
                final ImmutableSet.Builder builder = new ImmutableSet.Builder(size);
                for (final Table.Cell<R, C, V> cell : table.cellSet()) {
                    builder.add((Object)cellOf((Object)cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
                }
                return (ImmutableTable<R, C, V>)RegularImmutableTable.forCells((Iterable<Cell<Object, Object, Object>>)builder.build());
            }
            case 0: {
                return of();
            }
            case 1: {
                final Cell cell2 = (Cell)Iterables.getOnlyElement(table.cellSet());
                return of((R)cell2.getRowKey(), cell2.getColumnKey(), cell2.getValue());
            }
        }
    }
    
    public static <R, C, V> ImmutableTable<R, C, V> of() {
        return (ImmutableTable<R, C, V>)ImmutableTable.EMPTY;
    }
    
    public static <R, C, V> ImmutableTable<R, C, V> of(final R r, final C c, final V v) {
        return new SingletonImmutableTable<R, C, V>(r, c, v);
    }
    
    @Override
    final UnmodifiableIterator<Cell<R, C, V>> cellIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public ImmutableSet<Cell<R, C, V>> cellSet() {
        return (ImmutableSet)super.cellSet();
    }
    
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableMap<R, V> column(final C c) {
        Preconditions.checkNotNull(c);
        return MoreObjects.firstNonNull(this.columnMap().get(c), ImmutableMap.of());
    }
    
    @Override
    public ImmutableSet<C> columnKeySet() {
        return this.columnMap().keySet();
    }
    
    @Override
    public abstract ImmutableMap<C, Map<R, V>> columnMap();
    
    @Override
    public boolean contains(@Nullable final Object o, @Nullable final Object o2) {
        return this.get(o, o2) != null;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.values().contains(o);
    }
    
    @Override
    abstract ImmutableSet<Cell<R, C, V>> createCellSet();
    
    @Override
    abstract ImmutableCollection<V> createValues();
    
    @Deprecated
    @Override
    public final V put(final R r, final C c, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void putAll(final Table<? extends R, ? extends C, ? extends V> table) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final V remove(final Object o, final Object o2) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableMap<C, V> row(final R r) {
        Preconditions.checkNotNull(r);
        return MoreObjects.firstNonNull(this.rowMap().get(r), ImmutableMap.of());
    }
    
    @Override
    public ImmutableSet<R> rowKeySet() {
        return this.rowMap().keySet();
    }
    
    @Override
    public abstract ImmutableMap<R, Map<C, V>> rowMap();
    
    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection)super.values();
    }
    
    @Override
    final Iterator<V> valuesIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    public static final class Builder<R, C, V>
    {
        private final List<Cell<R, C, V>> cells;
        private Comparator<? super C> columnComparator;
        private Comparator<? super R> rowComparator;
        
        public Builder() {
            this.cells = (List<Cell<R, C, V>>)Lists.newArrayList();
        }
        
        public ImmutableTable<R, C, V> build() {
            switch (this.cells.size()) {
                default: {
                    return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
                }
                case 0: {
                    return ImmutableTable.of();
                }
                case 1: {
                    return new SingletonImmutableTable<R, C, V>(Iterables.getOnlyElement((Iterable<Table.Cell>)this.cells));
                }
            }
        }
        
        public Builder<R, C, V> orderColumnsBy(final Comparator<? super C> comparator) {
            this.columnComparator = Preconditions.checkNotNull(comparator);
            return this;
        }
        
        public Builder<R, C, V> orderRowsBy(final Comparator<? super R> comparator) {
            this.rowComparator = Preconditions.checkNotNull(comparator);
            return this;
        }
        
        public Builder<R, C, V> put(final Cell<? extends R, ? extends C, ? extends V> cell) {
            if (!(cell instanceof Tables.ImmutableCell)) {
                this.put((R)cell.getRowKey(), (C)cell.getColumnKey(), (V)cell.getValue());
            }
            else {
                Preconditions.checkNotNull(cell.getRowKey());
                Preconditions.checkNotNull(cell.getColumnKey());
                Preconditions.checkNotNull(cell.getValue());
                this.cells.add((Cell<R, C, V>)cell);
            }
            return this;
        }
        
        public Builder<R, C, V> put(final R r, final C c, final V v) {
            this.cells.add(ImmutableTable.cellOf(r, c, v));
            return this;
        }
        
        public Builder<R, C, V> putAll(final Table<? extends R, ? extends C, ? extends V> table) {
            final Iterator<Table.Cell<? extends R, ? extends C, ? extends V>> iterator = table.cellSet().iterator();
            while (iterator.hasNext()) {
                this.put(iterator.next());
            }
            return this;
        }
    }
}
