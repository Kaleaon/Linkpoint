package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible
abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {

    private final class CellSet extends ImmutableSet.Indexed<Table.Cell<R, C, V>> {
        private CellSet() {
        }

        public boolean contains(@Nullable Object obj) {
            if (!(obj instanceof Table.Cell)) {
                return false;
            }
            Table.Cell cell = (Table.Cell) obj;
            Object obj2 = RegularImmutableTable.this.get(cell.getRowKey(), cell.getColumnKey());
            return obj2 != null && obj2.equals(cell.getValue());
        }

        /* access modifiers changed from: package-private */
        public Table.Cell<R, C, V> get(int i) {
            return RegularImmutableTable.this.getCell(i);
        }

        /* access modifiers changed from: package-private */
        public boolean isPartialView() {
            return false;
        }

        public int size() {
            return RegularImmutableTable.this.size();
        }
    }

    private final class Values extends ImmutableList<V> {
        private Values() {
        }

        public V get(int i) {
            return RegularImmutableTable.this.getValue(i);
        }

        /* access modifiers changed from: package-private */
        public boolean isPartialView() {
            return true;
        }

        public int size() {
            return RegularImmutableTable.this.size();
        }
    }

    RegularImmutableTable() {
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> iterable) {
        return forCellsInternal(iterable, (Comparator) null, (Comparator) null);
    }

    static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> list, @Nullable final Comparator<? super R> comparator, @Nullable final Comparator<? super C> comparator2) {
        Preconditions.checkNotNull(list);
        if (!(comparator == null && comparator2 == null)) {
            Collections.sort(list, new Comparator<Table.Cell<R, C, V>>() {
                public int compare(Table.Cell<R, C, V> cell, Table.Cell<R, C, V> cell2) {
                    int compare = comparator != null ? comparator.compare(cell.getRowKey(), cell2.getRowKey()) : 0;
                    if (compare != 0) {
                        return compare;
                    }
                    if (comparator2 != null) {
                        return comparator2.compare(cell.getColumnKey(), cell2.getColumnKey());
                    }
                    return 0;
                }
            });
        }
        return forCellsInternal(list, comparator, comparator2);
    }

    private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> iterable, @Nullable Comparator<? super R> comparator, @Nullable Comparator<? super C> comparator2) {
        boolean z = false;
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        LinkedHashSet linkedHashSet2 = new LinkedHashSet();
        ImmutableList<E> copyOf = ImmutableList.copyOf(iterable);
        for (Table.Cell next : iterable) {
            linkedHashSet.add(next.getRowKey());
            linkedHashSet2.add(next.getColumnKey());
        }
        ImmutableSet<E> copyOf2 = comparator != null ? ImmutableSet.copyOf(Ordering.from(comparator).immutableSortedCopy(linkedHashSet)) : ImmutableSet.copyOf(linkedHashSet);
        ImmutableSet<E> copyOf3 = comparator2 != null ? ImmutableSet.copyOf(Ordering.from(comparator2).immutableSortedCopy(linkedHashSet2)) : ImmutableSet.copyOf(linkedHashSet2);
        if (((long) copyOf.size()) <= (((long) copyOf2.size()) * ((long) copyOf3.size())) / 2) {
            z = true;
        }
        return !z ? new DenseImmutableTable<>(copyOf, copyOf2, copyOf3) : new SparseImmutableTable<>(copyOf, copyOf2, copyOf3);
    }

    /* access modifiers changed from: package-private */
    public final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
        return !isEmpty() ? new CellSet() : ImmutableSet.of();
    }

    /* access modifiers changed from: package-private */
    public final ImmutableCollection<V> createValues() {
        return !isEmpty() ? new Values() : ImmutableList.of();
    }

    /* access modifiers changed from: package-private */
    public abstract Table.Cell<R, C, V> getCell(int i);

    /* access modifiers changed from: package-private */
    public abstract V getValue(int i);
}
