// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.RandomAccess;
import java.util.Set;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Collections;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V>
{
    static <R, C, V> RegularImmutableTable<R, C, V> forCells(final Iterable<Cell<R, C, V>> iterable) {
        return forCellsInternal(iterable, null, null);
    }
    
    static <R, C, V> RegularImmutableTable<R, C, V> forCells(final List<Cell<R, C, V>> list, @Nullable final Comparator<? super R> comparator, @Nullable final Comparator<? super C> comparator2) {
        Preconditions.checkNotNull(list);
        if (comparator != null || comparator2 != null) {
            Collections.sort((List<Object>)list, (Comparator<? super Object>)new Comparator<Cell<R, C, V>>() {
                @Override
                public int compare(final Cell<R, C, V> cell, final Cell<R, C, V> cell2) {
                    final int n = 0;
                    int compare;
                    if (comparator != null) {
                        compare = comparator.compare(cell.getRowKey(), cell2.getRowKey());
                    }
                    else {
                        compare = 0;
                    }
                    if (compare == 0) {
                        int compare2 = n;
                        if (comparator2 != null) {
                            compare2 = comparator2.compare(cell.getColumnKey(), cell2.getColumnKey());
                        }
                        return compare2;
                    }
                    return compare;
                }
            });
        }
        return forCellsInternal(list, comparator, comparator2);
    }
    
    private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(final Iterable<Cell<R, C, V>> iterable, @Nullable final Comparator<? super R> comparator, @Nullable final Comparator<? super C> comparator2) {
        boolean b = false;
        final LinkedHashSet set = new LinkedHashSet();
        final LinkedHashSet set2 = new LinkedHashSet();
        final ImmutableList<Object> copy = ImmutableList.copyOf((Iterable<?>)iterable);
        for (final Table.Cell<Object, C, V> cell : iterable) {
            set.add(cell.getRowKey());
            set2.add(cell.getColumnKey());
        }
        ImmutableSet<Object> set3;
        if (comparator != null) {
            set3 = ImmutableSet.copyOf((Collection<?>)Ordering.from(comparator).immutableSortedCopy((Iterable<Object>)set));
        }
        else {
            set3 = ImmutableSet.copyOf((Collection<?>)set);
        }
        ImmutableSet<Object> set4;
        if (comparator2 != null) {
            set4 = ImmutableSet.copyOf((Collection<?>)Ordering.from(comparator2).immutableSortedCopy((Iterable<Object>)set2));
        }
        else {
            set4 = ImmutableSet.copyOf((Collection<?>)set2);
        }
        if (copy.size() <= set3.size() * (long)set4.size() / 2L) {
            b = true;
        }
        Object o;
        if (!b) {
            o = new DenseImmutableTable<R, C, V>((ImmutableList<Cell<Object, Object, Object>>)copy, set3, set4);
        }
        else {
            o = new SparseImmutableTable<R, C, V>((ImmutableList<Cell<Object, Object, Object>>)copy, set3, set4);
        }
        return (RegularImmutableTable<R, C, V>)o;
    }
    
    @Override
    final ImmutableSet<Cell<R, C, V>> createCellSet() {
        Serializable of;
        if (!this.isEmpty()) {
            of = new CellSet();
        }
        else {
            of = ImmutableSet.of();
        }
        return (ImmutableSet<Cell<R, C, V>>)of;
    }
    
    @Override
    final ImmutableCollection<V> createValues() {
        RandomAccess of;
        if (!this.isEmpty()) {
            of = new Values();
        }
        else {
            of = ImmutableList.of();
        }
        return (ImmutableCollection<V>)of;
    }
    
    abstract Cell<R, C, V> getCell(final int p0);
    
    abstract V getValue(final int p0);
    
    private final class CellSet extends Indexed<Cell<R, C, V>>
    {
        @Override
        public boolean contains(@Nullable Object value) {
            boolean b = false;
            if (!(value instanceof Cell)) {
                return false;
            }
            final Cell cell = (Cell)value;
            value = RegularImmutableTable.this.get(cell.getRowKey(), cell.getColumnKey());
            if (value != null && value.equals(cell.getValue())) {
                b = true;
            }
            return b;
        }
        
        Cell<R, C, V> get(final int n) {
            return RegularImmutableTable.this.getCell(n);
        }
        
        @Override
        boolean isPartialView() {
            return false;
        }
        
        @Override
        public int size() {
            return RegularImmutableTable.this.size();
        }
    }
    
    private final class Values extends ImmutableList<V>
    {
        @Override
        public V get(final int n) {
            return RegularImmutableTable.this.getValue(n);
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return RegularImmutableTable.this.size();
        }
    }
}
