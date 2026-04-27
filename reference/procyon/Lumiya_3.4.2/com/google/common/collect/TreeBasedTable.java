// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;
import com.google.common.base.Function;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(serializable = true)
public class TreeBasedTable<R, C, V> extends StandardRowSortedTable<R, C, V>
{
    private static final long serialVersionUID = 0L;
    private final Comparator<? super C> columnComparator;
    
    TreeBasedTable(final Comparator<? super R> comparator, final Comparator<? super C> columnComparator) {
        super(new TreeMap(comparator), new Factory(columnComparator));
        this.columnComparator = columnComparator;
    }
    
    public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create() {
        return new TreeBasedTable<R, C, V>(Ordering.natural(), Ordering.natural());
    }
    
    public static <R, C, V> TreeBasedTable<R, C, V> create(final TreeBasedTable<R, C, ? extends V> treeBasedTable) {
        final TreeBasedTable treeBasedTable2 = new TreeBasedTable((Comparator<? super R>)treeBasedTable.rowComparator(), (Comparator<? super C>)treeBasedTable.columnComparator());
        treeBasedTable2.putAll(treeBasedTable);
        return treeBasedTable2;
    }
    
    public static <R, C, V> TreeBasedTable<R, C, V> create(final Comparator<? super R> comparator, final Comparator<? super C> comparator2) {
        Preconditions.checkNotNull(comparator);
        Preconditions.checkNotNull(comparator2);
        return new TreeBasedTable<R, C, V>(comparator, comparator2);
    }
    
    public Comparator<? super C> columnComparator() {
        return this.columnComparator;
    }
    
    @Override
    Iterator<C> createColumnKeyIterator() {
        final Comparator<? super C> columnComparator = this.columnComparator();
        return new AbstractIterator<C>() {
            C lastValue;
            final /* synthetic */ Iterator val$merged = Iterators.mergeSorted(Iterables.transform((Iterable<Map<C, V>>)TreeBasedTable.this.backingMap.values(), (Function<? super Map<C, V>, ? extends Iterator<?>>)new Function<Map<C, V>, Iterator<C>>(this) {
                @Override
                public Iterator<C> apply(final Map<C, V> map) {
                    return map.keySet().iterator();
                }
            }), (Comparator<? super Object>)columnComparator);
            
            @Override
            protected C computeNext() {
                while (this.val$merged.hasNext()) {
                    final C next = this.val$merged.next();
                    int n;
                    if (this.lastValue != null && columnComparator.compare(next, this.lastValue) == 0) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                    if (n == 0) {
                        return this.lastValue = next;
                    }
                }
                this.lastValue = null;
                return this.endOfData();
            }
        };
    }
    
    @Override
    public SortedMap<C, V> row(final R r) {
        return new TreeRow(r);
    }
    
    public Comparator<? super R> rowComparator() {
        return this.rowKeySet().comparator();
    }
    
    @Override
    public SortedSet<R> rowKeySet() {
        return super.rowKeySet();
    }
    
    @Override
    public SortedMap<R, Map<C, V>> rowMap() {
        return super.rowMap();
    }
    
    private static class Factory<C, V> implements Supplier<TreeMap<C, V>>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final Comparator<? super C> comparator;
        
        Factory(final Comparator<? super C> comparator) {
            this.comparator = comparator;
        }
        
        @Override
        public TreeMap<C, V> get() {
            return new TreeMap<C, V>(this.comparator);
        }
    }
    
    private class TreeRow extends Row implements SortedMap<C, V>
    {
        @Nullable
        final C lowerBound;
        @Nullable
        final C upperBound;
        transient SortedMap<C, V> wholeRow;
        
        TreeRow(final TreeBasedTable treeBasedTable, final R r) {
            this(r, null, null);
        }
        
        TreeRow(final R r, @Nullable final C lowerBound, @Nullable final C upperBound) {
            boolean b = false;
            TreeBasedTable.this.super(r);
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            if (lowerBound == null || upperBound == null || this.compare(lowerBound, upperBound) <= 0) {
                b = true;
            }
            Preconditions.checkArgument(b);
        }
        
        SortedMap<C, V> backingRowMap() {
            return (SortedMap)super.backingRowMap();
        }
        
        @Override
        public Comparator<? super C> comparator() {
            return TreeBasedTable.this.columnComparator();
        }
        
        int compare(final Object o, final Object o2) {
            return this.comparator().compare((Object)o, (Object)o2);
        }
        
        SortedMap<C, V> computeBackingRowMap() {
            Object o = this.wholeRow();
            if (o == null) {
                return null;
            }
            if (this.lowerBound != null) {
                o = ((SortedMap<C, Object>)o).tailMap(this.lowerBound);
            }
            if (this.upperBound != null) {
                o = ((SortedMap<C, Object>)o).headMap(this.upperBound);
            }
            return (SortedMap<C, V>)o;
        }
        
        @Override
        public boolean containsKey(final Object o) {
            boolean b = false;
            if (this.rangeContains(o) && super.containsKey(o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public C firstKey() {
            if (this.backingRowMap() != null) {
                return this.backingRowMap().firstKey();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public SortedMap<C, V> headMap(final C c) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c)));
            return new TreeRow((R)this.rowKey, this.lowerBound, c);
        }
        
        @Override
        public SortedSet<C> keySet() {
            return new Maps.SortedKeySet<C, Object>(this);
        }
        
        @Override
        public C lastKey() {
            if (this.backingRowMap() != null) {
                return this.backingRowMap().lastKey();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        void maintainEmptyInvariant() {
            if (this.wholeRow() != null && this.wholeRow.isEmpty()) {
                TreeBasedTable.this.backingMap.remove(this.rowKey);
                this.wholeRow = null;
                this.backingRowMap = null;
            }
        }
        
        @Override
        public V put(final C c, final V v) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c)));
            return super.put(c, v);
        }
        
        boolean rangeContains(@Nullable final Object o) {
            boolean b = false;
            if (o != null) {
                if (this.lowerBound == null || this.compare(this.lowerBound, o) <= 0) {
                    if (this.upperBound == null || this.compare(this.upperBound, o) > 0) {
                        b = true;
                    }
                }
            }
            return b;
        }
        
        @Override
        public SortedMap<C, V> subMap(final C c, final C c2) {
            boolean b = false;
            if (this.rangeContains(Preconditions.checkNotNull(c)) && this.rangeContains(Preconditions.checkNotNull(c2))) {
                b = true;
            }
            Preconditions.checkArgument(b);
            return new TreeRow((R)this.rowKey, c, c2);
        }
        
        @Override
        public SortedMap<C, V> tailMap(final C c) {
            Preconditions.checkArgument(this.rangeContains(Preconditions.checkNotNull(c)));
            return new TreeRow((R)this.rowKey, c, this.upperBound);
        }
        
        SortedMap<C, V> wholeRow() {
            if (this.wholeRow != null) {
                if (!this.wholeRow.isEmpty()) {
                    return this.wholeRow;
                }
                if (!TreeBasedTable.this.backingMap.containsKey(this.rowKey)) {
                    return this.wholeRow;
                }
            }
            this.wholeRow = (SortedMap)TreeBasedTable.this.backingMap.get(this.rowKey);
            return this.wholeRow;
        }
    }
}
