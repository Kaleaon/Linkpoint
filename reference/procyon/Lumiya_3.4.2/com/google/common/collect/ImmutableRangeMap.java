// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;
import com.google.common.base.Function;
import java.util.List;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
@GwtIncompatible("NavigableMap")
public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V>, Serializable
{
    private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY;
    private static final long serialVersionUID = 0L;
    private final transient ImmutableList<Range<K>> ranges;
    private final transient ImmutableList<V> values;
    
    static {
        EMPTY = new ImmutableRangeMap<Comparable<?>, Object>(ImmutableList.of(), ImmutableList.of());
    }
    
    ImmutableRangeMap(final ImmutableList<Range<K>> ranges, final ImmutableList<V> values) {
        this.ranges = ranges;
        this.values = values;
    }
    
    public static <K extends Comparable<?>, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(final RangeMap<K, ? extends V> rangeMap) {
        if (!(rangeMap instanceof ImmutableRangeMap)) {
            final Map<Range<Comparable>, Object> mapOfRanges = rangeMap.asMapOfRanges();
            final ImmutableList.Builder builder = new ImmutableList.Builder(mapOfRanges.size());
            final ImmutableList.Builder builder2 = new ImmutableList.Builder<Object>(mapOfRanges.size());
            for (final Map.Entry<Object, V> entry : mapOfRanges.entrySet()) {
                builder.add(entry.getKey());
                builder2.add((Object)entry.getValue());
            }
            return new ImmutableRangeMap<K, V>(builder.build(), builder2.build());
        }
        return (ImmutableRangeMap<K, V>)rangeMap;
    }
    
    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
        return (ImmutableRangeMap<K, V>)ImmutableRangeMap.EMPTY;
    }
    
    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(final Range<K> range, final V v) {
        return new ImmutableRangeMap<K, V>((ImmutableList<Range<K>>)ImmutableList.of((Range<K>)range), ImmutableList.of(v));
    }
    
    @Override
    public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
        if (!this.ranges.isEmpty()) {
            return new ImmutableSortedMap<Range<K>, V>(new RegularImmutableSortedSet<Range<K>>(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse()), this.values.reverse());
        }
        return ImmutableMap.of();
    }
    
    @Override
    public ImmutableMap<Range<K>, V> asMapOfRanges() {
        if (!this.ranges.isEmpty()) {
            return new ImmutableSortedMap<Range<K>, V>(new RegularImmutableSortedSet<Range<K>>(this.ranges, Range.RANGE_LEX_ORDERING), this.values);
        }
        return ImmutableMap.of();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o instanceof RangeMap && this.asMapOfRanges().equals(((RangeMap)o).asMapOfRanges());
    }
    
    @Nullable
    @Override
    public V get(final K k) {
        final int binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<K>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)Cut.belowValue(k), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (binarySearch != -1) {
            Object value;
            if (!((Range<K>)this.ranges.get(binarySearch)).contains(k)) {
                value = null;
            }
            else {
                value = this.values.get(binarySearch);
            }
            return (V)value;
        }
        return null;
    }
    
    @Nullable
    @Override
    public Map.Entry<Range<K>, V> getEntry(final K k) {
        final int binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<K>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)Cut.belowValue(k), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (binarySearch != -1) {
            final Range range = this.ranges.get(binarySearch);
            Object immutableEntry;
            if (!range.contains(k)) {
                immutableEntry = null;
            }
            else {
                immutableEntry = Maps.immutableEntry(range, this.values.get(binarySearch));
            }
            return (Map.Entry<Range<K>, V>)immutableEntry;
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        return this.asMapOfRanges().hashCode();
    }
    
    @Override
    public void put(final Range<K> range, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final RangeMap<K, V> rangeMap) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void remove(final Range<K> range) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Range<K> span() {
        if (!this.ranges.isEmpty()) {
            return Range.create((Cut<K>)this.ranges.get(0).lowerBound, (Cut<K>)this.ranges.get(this.ranges.size() - 1).upperBound);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public ImmutableRangeMap<K, V> subRangeMap(final Range<K> range) {
        if (Preconditions.checkNotNull(range).isEmpty()) {
            return of();
        }
        if (this.ranges.isEmpty() || range.encloses(this.span())) {
            return this;
        }
        final int binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<K>, Cut<Comparable>>)Range.upperBoundFn(), (Cut<Comparable>)range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        final int binarySearch2 = SortedLists.binarySearch(this.ranges, (Function<? super Range<K>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)range.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        if (binarySearch < binarySearch2) {
            return new ImmutableRangeMap<K, V>(new ImmutableList<Range<K>>() {
                final /* synthetic */ int val$len = binarySearch2 - binarySearch;
                
                @Override
                public Range<K> get(final int n) {
                    Preconditions.checkElementIndex(n, this.val$len);
                    if (n != 0 && n != this.val$len - 1) {
                        return (Range)ImmutableRangeMap.this.ranges.get(binarySearch + n);
                    }
                    return ((Range)ImmutableRangeMap.this.ranges.get(binarySearch + n)).intersection(range);
                }
                
                @Override
                boolean isPartialView() {
                    return true;
                }
                
                @Override
                public int size() {
                    return this.val$len;
                }
            }, this.values.subList(binarySearch, binarySearch2)) {
                @Override
                public ImmutableRangeMap<K, V> subRangeMap(final Range<K> range) {
                    if (!range.isConnected(range)) {
                        return ImmutableRangeMap.of();
                    }
                    return ImmutableRangeMap.this.subRangeMap(range.intersection(range));
                }
            };
        }
        return of();
    }
    
    @Override
    public String toString() {
        return this.asMapOfRanges().toString();
    }
    
    Object writeReplace() {
        return new SerializedForm(this.asMapOfRanges());
    }
    
    public static final class Builder<K extends Comparable<?>, V>
    {
        private final RangeSet<K> keyRanges;
        private final RangeMap<K, V> rangeMap;
        
        public Builder() {
            this.keyRanges = (RangeSet<K>)TreeRangeSet.create();
            this.rangeMap = (RangeMap<K, V>)TreeRangeMap.create();
        }
        
        public ImmutableRangeMap<K, V> build() {
            final Map<Range<K>, V> mapOfRanges = this.rangeMap.asMapOfRanges();
            final ImmutableList.Builder builder = new ImmutableList.Builder(mapOfRanges.size());
            final ImmutableList.Builder builder2 = new ImmutableList.Builder<Object>(mapOfRanges.size());
            for (final Map.Entry<Object, V> entry : mapOfRanges.entrySet()) {
                builder.add(entry.getKey());
                builder2.add((Object)entry.getValue());
            }
            return new ImmutableRangeMap<K, V>(builder.build(), builder2.build());
        }
        
        public Builder<K, V> put(final Range<K> obj, final V v) {
            Preconditions.checkNotNull(obj);
            Preconditions.checkNotNull(v);
            Preconditions.checkArgument(!obj.isEmpty(), "Range must not be empty, but was %s", obj);
            if (!this.keyRanges.complement().encloses(obj)) {
                for (final Map.Entry<Range, V> obj2 : this.rangeMap.asMapOfRanges().entrySet()) {
                    final Range range = obj2.getKey();
                    if (range.isConnected(obj) && !range.intersection(obj).isEmpty()) {
                        throw new IllegalArgumentException("Overlapping ranges: range " + obj + " overlaps with entry " + obj2);
                    }
                }
            }
            this.keyRanges.add(obj);
            this.rangeMap.put(obj, v);
            return this;
        }
        
        public Builder<K, V> putAll(final RangeMap<K, ? extends V> rangeMap) {
            for (final Map.Entry<Range<K>, V> entry : rangeMap.asMapOfRanges().entrySet()) {
                this.put((Range<K>)entry.getKey(), entry.getValue());
            }
            return this;
        }
    }
    
    private static class SerializedForm<K extends Comparable<?>, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final ImmutableMap<Range<K>, V> mapOfRanges;
        
        SerializedForm(final ImmutableMap<Range<K>, V> mapOfRanges) {
            this.mapOfRanges = mapOfRanges;
        }
        
        Object createRangeMap() {
            final Builder<Comparable, Object> builder = new Builder<Comparable, Object>();
            for (final Map.Entry<Range, V> entry : this.mapOfRanges.entrySet()) {
                builder.put(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
        
        Object readResolve() {
            if (!this.mapOfRanges.isEmpty()) {
                return this.createRangeMap();
            }
            return ImmutableRangeMap.of();
        }
    }
}
