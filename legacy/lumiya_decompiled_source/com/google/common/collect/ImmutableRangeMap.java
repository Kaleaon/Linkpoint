package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SortedLists;
import java.io.Serializable;
import java.lang.Comparable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
@Beta
@GwtIncompatible("NavigableMap")
/* loaded from: classes.dex */
public class ImmutableRangeMap<K extends Comparable<?>, V> implements RangeMap<K, V>, Serializable {
    private static final ImmutableRangeMap<Comparable<?>, Object> EMPTY = new ImmutableRangeMap<>(ImmutableList.of(), ImmutableList.of());
    private static final long serialVersionUID = 0;
    private final transient ImmutableList<Range<K>> ranges;
    private final transient ImmutableList<V> values;

    /* loaded from: classes.dex */
    public static final class Builder<K extends Comparable<?>, V> {
        private final RangeSet<K> keyRanges = TreeRangeSet.create();
        private final RangeMap<K, V> rangeMap = TreeRangeMap.create();

        public ImmutableRangeMap<K, V> build() {
            Map<Range<K>, V> asMapOfRanges = this.rangeMap.asMapOfRanges();
            ImmutableList.Builder builder = new ImmutableList.Builder(asMapOfRanges.size());
            ImmutableList.Builder builder2 = new ImmutableList.Builder(asMapOfRanges.size());
            for (Map.Entry<Range<K>, V> entry : asMapOfRanges.entrySet()) {
                builder.add((ImmutableList.Builder) entry.getKey());
                builder2.add((ImmutableList.Builder) entry.getValue());
            }
            return new ImmutableRangeMap<>(builder.build(), builder2.build());
        }

        public Builder<K, V> put(Range<K> range, V v) {
            Preconditions.checkNotNull(range);
            Preconditions.checkNotNull(v);
            Preconditions.checkArgument(!range.isEmpty(), "Range must not be empty, but was %s", range);
            if (!this.keyRanges.complement().encloses(range)) {
                for (Map.Entry<Range<K>, V> entry : this.rangeMap.asMapOfRanges().entrySet()) {
                    Range<K> key = entry.getKey();
                    if (key.isConnected(range) && !key.intersection(range).isEmpty()) {
                        throw new IllegalArgumentException("Overlapping ranges: range " + range + " overlaps with entry " + entry);
                    }
                }
            }
            this.keyRanges.add(range);
            this.rangeMap.put(range, v);
            return this;
        }

        public Builder<K, V> putAll(RangeMap<K, ? extends V> rangeMap) {
            for (Map.Entry<Range<K>, ? extends V> entry : rangeMap.asMapOfRanges().entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
            return this;
        }
    }

    /* loaded from: classes.dex */
    private static class SerializedForm<K extends Comparable<?>, V> implements Serializable {
        private static final long serialVersionUID = 0;
        private final ImmutableMap<Range<K>, V> mapOfRanges;

        SerializedForm(ImmutableMap<Range<K>, V> immutableMap) {
            this.mapOfRanges = immutableMap;
        }

        /* JADX WARN: Multi-variable type inference failed */
        Object createRangeMap() {
            Builder builder = new Builder();
            Iterator it = this.mapOfRanges.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                builder.put((Range) entry.getKey(), entry.getValue());
            }
            return builder.build();
        }

        Object readResolve() {
            return !this.mapOfRanges.isEmpty() ? createRangeMap() : ImmutableRangeMap.of();
        }
    }

    ImmutableRangeMap(ImmutableList<Range<K>> immutableList, ImmutableList<V> immutableList2) {
        this.ranges = immutableList;
        this.values = immutableList2;
    }

    public static <K extends Comparable<?>, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> copyOf(RangeMap<K, ? extends V> rangeMap) {
        if (rangeMap instanceof ImmutableRangeMap) {
            return (ImmutableRangeMap) rangeMap;
        }
        Map<Range<K>, ? extends V> asMapOfRanges = rangeMap.asMapOfRanges();
        ImmutableList.Builder builder = new ImmutableList.Builder(asMapOfRanges.size());
        ImmutableList.Builder builder2 = new ImmutableList.Builder(asMapOfRanges.size());
        for (Map.Entry<Range<K>, ? extends V> entry : asMapOfRanges.entrySet()) {
            builder.add((ImmutableList.Builder) entry.getKey());
            builder2.add((ImmutableList.Builder) entry.getValue());
        }
        return new ImmutableRangeMap<>(builder.build(), builder2.build());
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of() {
        return (ImmutableRangeMap<K, V>) EMPTY;
    }

    public static <K extends Comparable<?>, V> ImmutableRangeMap<K, V> of(Range<K> range, V v) {
        return new ImmutableRangeMap<>(ImmutableList.of(range), ImmutableList.of(v));
    }

    @Override // com.google.common.collect.RangeMap
    public ImmutableMap<Range<K>, V> asDescendingMapOfRanges() {
        return !this.ranges.isEmpty() ? new ImmutableSortedMap(new RegularImmutableSortedSet(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse()), this.values.reverse()) : ImmutableMap.of();
    }

    @Override // com.google.common.collect.RangeMap
    public ImmutableMap<Range<K>, V> asMapOfRanges() {
        return !this.ranges.isEmpty() ? new ImmutableSortedMap(new RegularImmutableSortedSet(this.ranges, Range.RANGE_LEX_ORDERING), this.values) : ImmutableMap.of();
    }

    @Override // com.google.common.collect.RangeMap
    public void clear() {
        throw new IllegalStateException();
    }

    @Override // com.google.common.collect.RangeMap
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof RangeMap) {
            return asMapOfRanges().equals(((RangeMap) obj).asMapOfRanges());
        }
        return false;
    }

    @Override // com.google.common.collect.RangeMap
    @Nullable
    public V get(K k) {
        int binarySearch = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(k), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (binarySearch != -1 && this.ranges.get(binarySearch).contains(k)) {
            return this.values.get(binarySearch);
        }
        return null;
    }

    @Override // com.google.common.collect.RangeMap
    @Nullable
    public Map.Entry<Range<K>, V> getEntry(K k) {
        int binarySearch = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), Cut.belowValue(k), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (binarySearch != -1) {
            Range<K> range = this.ranges.get(binarySearch);
            if (range.contains(k)) {
                return Maps.immutableEntry(range, this.values.get(binarySearch));
            }
            return null;
        }
        return null;
    }

    @Override // com.google.common.collect.RangeMap
    public int hashCode() {
        return asMapOfRanges().hashCode();
    }

    @Override // com.google.common.collect.RangeMap
    public void put(Range<K> range, V v) {
        throw new IllegalStateException();
    }

    @Override // com.google.common.collect.RangeMap
    public void putAll(RangeMap<K, V> rangeMap) {
        throw new IllegalStateException();
    }

    @Override // com.google.common.collect.RangeMap
    public void remove(Range<K> range) {
        throw new IllegalStateException();
    }

    @Override // com.google.common.collect.RangeMap
    public Range<K> span() {
        if (this.ranges.isEmpty()) {
            throw new NoSuchElementException();
        }
        return Range.create(this.ranges.get(0).lowerBound, this.ranges.get(this.ranges.size() - 1).upperBound);
    }

    @Override // com.google.common.collect.RangeMap
    public ImmutableRangeMap<K, V> subRangeMap(final Range<K> range) {
        if (((Range) Preconditions.checkNotNull(range)).isEmpty()) {
            return of();
        }
        if (this.ranges.isEmpty() || range.encloses(span())) {
            return this;
        }
        final int binarySearch = SortedLists.binarySearch(this.ranges, Range.upperBoundFn(), range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        int binarySearch2 = SortedLists.binarySearch(this.ranges, Range.lowerBoundFn(), range.upperBound, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        if (binarySearch < binarySearch2) {
            final int i = binarySearch2 - binarySearch;
            return (ImmutableRangeMap<K, V>) new ImmutableRangeMap<K, V>(new ImmutableList<Range<K>>() { // from class: com.google.common.collect.ImmutableRangeMap.1
                @Override // java.util.List
                public Range<K> get(int i2) {
                    Preconditions.checkElementIndex(i2, i);
                    return (i2 == 0 || i2 == i + (-1)) ? ((Range) ImmutableRangeMap.this.ranges.get(binarySearch + i2)).intersection(range) : (Range) ImmutableRangeMap.this.ranges.get(binarySearch + i2);
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                @Override // com.google.common.collect.ImmutableCollection
                public boolean isPartialView() {
                    return true;
                }

                @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
                public int size() {
                    return i;
                }
            }, this.values.subList(binarySearch, binarySearch2)) { // from class: com.google.common.collect.ImmutableRangeMap.2
                @Override // com.google.common.collect.ImmutableRangeMap, com.google.common.collect.RangeMap
                public /* bridge */ /* synthetic */ Map asDescendingMapOfRanges() {
                    return super.asDescendingMapOfRanges();
                }

                @Override // com.google.common.collect.ImmutableRangeMap, com.google.common.collect.RangeMap
                public /* bridge */ /* synthetic */ Map asMapOfRanges() {
                    return super.asMapOfRanges();
                }

                @Override // com.google.common.collect.ImmutableRangeMap, com.google.common.collect.RangeMap
                public ImmutableRangeMap<K, V> subRangeMap(Range<K> range2) {
                    return !range.isConnected(range2) ? ImmutableRangeMap.of() : this.subRangeMap((Range) range2.intersection(range));
                }
            };
        }
        return of();
    }

    @Override // com.google.common.collect.RangeMap
    public String toString() {
        return asMapOfRanges().toString();
    }

    Object writeReplace() {
        return new SerializedForm(asMapOfRanges());
    }
}
