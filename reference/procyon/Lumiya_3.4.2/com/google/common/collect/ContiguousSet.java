// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.SortedSet;
import java.util.NavigableSet;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C>
{
    final DiscreteDomain<C> domain;
    
    ContiguousSet(final DiscreteDomain<C> domain) {
        super(Ordering.natural());
        this.domain = domain;
    }
    
    @Deprecated
    public static <E> Builder<E> builder() {
        throw new UnsupportedOperationException();
    }
    
    public static <C extends Comparable> ContiguousSet<C> create(final Range<C> range, final DiscreteDomain<C> discreteDomain) {
        while (true) {
            boolean b = false;
            Preconditions.checkNotNull(range);
            Preconditions.checkNotNull(discreteDomain);
        Label_0041_Outer:
            while (true) {
                while (true) {
                    try {
                        Range<C> range2;
                        if (range.hasLowerBound()) {
                            range2 = range;
                        }
                        else {
                            range2 = range.intersection(Range.atLeast(discreteDomain.minValue()));
                        }
                        if (!range.hasUpperBound()) {
                            range2 = range2.intersection(Range.atMost(discreteDomain.maxValue()));
                        }
                        if (range2.isEmpty()) {
                            b = true;
                            if (!b) {
                                final Serializable s = new RegularContiguousSet<Object>(range2, discreteDomain);
                                return (ContiguousSet<C>)s;
                            }
                            return new EmptyContiguousSet<C>(discreteDomain);
                        }
                    }
                    catch (final NoSuchElementException cause) {
                        throw new IllegalArgumentException(cause);
                    }
                    if (Range.compareOrThrow(range.lowerBound.leastValueAbove((DiscreteDomain<Comparable>)discreteDomain), range.upperBound.greatestValueBelow((DiscreteDomain<Comparable>)discreteDomain)) <= 0) {
                        continue;
                    }
                    break;
                }
                continue Label_0041_Outer;
            }
            final Serializable s = new EmptyContiguousSet<C>(discreteDomain);
            return (ContiguousSet<C>)s;
        }
    }
    
    @Override
    public ContiguousSet<C> headSet(final C c) {
        return this.headSetImpl(Preconditions.checkNotNull(c), false);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ContiguousSet<C> headSet(final C c, final boolean b) {
        return this.headSetImpl(Preconditions.checkNotNull(c), b);
    }
    
    @Override
    abstract ContiguousSet<C> headSetImpl(final C p0, final boolean p1);
    
    public abstract ContiguousSet<C> intersection(final ContiguousSet<C> p0);
    
    public abstract Range<C> range();
    
    public abstract Range<C> range(final BoundType p0, final BoundType p1);
    
    @Override
    public ContiguousSet<C> subSet(final C c, final C c2) {
        Preconditions.checkNotNull(c);
        Preconditions.checkNotNull(c2);
        Preconditions.checkArgument(this.comparator().compare((Object)c, (Object)c2) <= 0);
        return this.subSetImpl(c, true, c2, false);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ContiguousSet<C> subSet(final C c, final boolean b, final C c2, final boolean b2) {
        boolean b3 = false;
        Preconditions.checkNotNull(c);
        Preconditions.checkNotNull(c2);
        if (this.comparator().compare((Object)c, (Object)c2) <= 0) {
            b3 = true;
        }
        Preconditions.checkArgument(b3);
        return this.subSetImpl(c, b, c2, b2);
    }
    
    @Override
    abstract ContiguousSet<C> subSetImpl(final C p0, final boolean p1, final C p2, final boolean p3);
    
    @Override
    public ContiguousSet<C> tailSet(final C c) {
        return this.tailSetImpl(Preconditions.checkNotNull(c), true);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ContiguousSet<C> tailSet(final C c, final boolean b) {
        return this.tailSetImpl(Preconditions.checkNotNull(c), b);
    }
    
    @Override
    abstract ContiguousSet<C> tailSetImpl(final C p0, final boolean p1);
    
    @Override
    public String toString() {
        return this.range().toString();
    }
}
