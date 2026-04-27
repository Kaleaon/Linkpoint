// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.primitives.Ints;
import com.google.common.base.Preconditions;
import java.util.SortedSet;
import java.util.Set;
import java.util.NavigableSet;
import javax.annotation.Nullable;
import java.util.Comparator;

final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
{
    private static final long[] ZERO_CUMULATIVE_COUNTS;
    private final transient long[] cumulativeCounts;
    private final transient RegularImmutableSortedSet<E> elementSet;
    private final transient int length;
    private final transient int offset;
    
    static {
        ZERO_CUMULATIVE_COUNTS = new long[] { 0L };
    }
    
    RegularImmutableSortedMultiset(final RegularImmutableSortedSet<E> elementSet, final long[] cumulativeCounts, final int offset, final int length) {
        this.elementSet = elementSet;
        this.cumulativeCounts = cumulativeCounts;
        this.offset = offset;
        this.length = length;
    }
    
    RegularImmutableSortedMultiset(final Comparator<? super E> comparator) {
        this.elementSet = ImmutableSortedSet.emptySet(comparator);
        this.cumulativeCounts = RegularImmutableSortedMultiset.ZERO_CUMULATIVE_COUNTS;
        this.offset = 0;
        this.length = 0;
    }
    
    private int getCount(final int n) {
        return (int)(this.cumulativeCounts[this.offset + n + 1] - this.cumulativeCounts[this.offset + n]);
    }
    
    @Override
    public int count(@Nullable final Object o) {
        int count = 0;
        final int index = this.elementSet.indexOf(o);
        if (index >= 0) {
            count = this.getCount(index);
        }
        return count;
    }
    
    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.elementSet;
    }
    
    @Override
    public Entry<E> firstEntry() {
        Entry<E> entry;
        if (!this.isEmpty()) {
            entry = this.getEntry(0);
        }
        else {
            entry = null;
        }
        return entry;
    }
    
    @Override
    Entry<E> getEntry(final int n) {
        return Multisets.immutableEntry(this.elementSet.asList().get(n), this.getCount(n));
    }
    
    ImmutableSortedMultiset<E> getSubMultiset(final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n2, this.length);
        if (n == n2) {
            return ImmutableSortedMultiset.emptyMultiset(this.comparator());
        }
        if (n == 0 && n2 == this.length) {
            return this;
        }
        return new RegularImmutableSortedMultiset((RegularImmutableSortedSet<Object>)this.elementSet.getSubSet(n, n2), this.cumulativeCounts, this.offset + n, n2 - n);
    }
    
    @Override
    public ImmutableSortedMultiset<E> headMultiset(final E e, final BoundType boundType) {
        return this.getSubMultiset(0, this.elementSet.headIndex(e, Preconditions.checkNotNull(boundType) == BoundType.CLOSED));
    }
    
    @Override
    boolean isPartialView() {
        boolean b = false;
        if (this.offset > 0 || this.length < this.cumulativeCounts.length - 1) {
            b = true;
        }
        return b;
    }
    
    @Override
    public Entry<E> lastEntry() {
        Entry<E> entry;
        if (!this.isEmpty()) {
            entry = this.getEntry(this.length - 1);
        }
        else {
            entry = null;
        }
        return entry;
    }
    
    @Override
    public int size() {
        return Ints.saturatedCast(this.cumulativeCounts[this.offset + this.length] - this.cumulativeCounts[this.offset]);
    }
    
    @Override
    public ImmutableSortedMultiset<E> tailMultiset(final E e, final BoundType boundType) {
        return this.getSubMultiset(this.elementSet.tailIndex(e, Preconditions.checkNotNull(boundType) == BoundType.CLOSED), this.length);
    }
}
