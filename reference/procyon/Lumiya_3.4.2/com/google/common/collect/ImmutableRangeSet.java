// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.primitives.Ints;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Comparator;
import com.google.common.base.Function;
import java.util.List;
import java.util.Collection;
import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public final class ImmutableRangeSet<C extends Comparable> extends AbstractRangeSet<C> implements Serializable
{
    private static final ImmutableRangeSet<Comparable<?>> ALL;
    private static final ImmutableRangeSet<Comparable<?>> EMPTY;
    private transient ImmutableRangeSet<C> complement;
    private final transient ImmutableList<Range<C>> ranges;
    
    static {
        EMPTY = new ImmutableRangeSet<Comparable<?>>(ImmutableList.of());
        ALL = new ImmutableRangeSet<Comparable<?>>((ImmutableList<Range<Comparable<?>>>)ImmutableList.of(Range.all()));
    }
    
    ImmutableRangeSet(final ImmutableList<Range<C>> ranges) {
        this.ranges = ranges;
    }
    
    private ImmutableRangeSet(final ImmutableList<Range<C>> ranges, final ImmutableRangeSet<C> complement) {
        this.ranges = ranges;
        this.complement = complement;
    }
    
    static <C extends Comparable> ImmutableRangeSet<C> all() {
        return (ImmutableRangeSet<C>)ImmutableRangeSet.ALL;
    }
    
    public static <C extends Comparable<?>> Builder<C> builder() {
        return new Builder<C>();
    }
    
    public static <C extends Comparable> ImmutableRangeSet<C> copyOf(final RangeSet<C> set) {
        Preconditions.checkNotNull(set);
        if (set.isEmpty()) {
            return of();
        }
        if (!set.encloses((Range)Range.all())) {
            if (set instanceof ImmutableRangeSet) {
                final ImmutableRangeSet set2 = (ImmutableRangeSet)set;
                if (!set2.isPartialView()) {
                    return set2;
                }
            }
            return new ImmutableRangeSet<C>(ImmutableList.copyOf((Collection<? extends Range<C>>)set.asRanges()));
        }
        return all();
    }
    
    private ImmutableList<Range<C>> intersectRanges(final Range<C> range) {
        int binarySearch = 0;
        if (this.ranges.isEmpty() || range.isEmpty()) {
            return ImmutableList.of();
        }
        if (range.encloses(this.span())) {
            return this.ranges;
        }
        if (range.hasLowerBound()) {
            binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<C>, Cut<Comparable>>)Range.upperBoundFn(), (Cut<Comparable>)range.lowerBound, SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        }
        int n;
        if (!range.hasUpperBound()) {
            n = this.ranges.size();
        }
        else {
            n = SortedLists.binarySearch(this.ranges, (Function<? super Range<C>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)range.upperBound, SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
        }
        final int n2 = n - binarySearch;
        if (n2 != 0) {
            return new ImmutableList<Range<C>>() {
                @Override
                public Range<C> get(final int n) {
                    Preconditions.checkElementIndex(n, n2);
                    if (n != 0 && n != n2 - 1) {
                        return (Range)ImmutableRangeSet.this.ranges.get(binarySearch + n);
                    }
                    return ((Range)ImmutableRangeSet.this.ranges.get(binarySearch + n)).intersection(range);
                }
                
                @Override
                boolean isPartialView() {
                    return true;
                }
                
                @Override
                public int size() {
                    return n2;
                }
            };
        }
        return ImmutableList.of();
    }
    
    public static <C extends Comparable> ImmutableRangeSet<C> of() {
        return (ImmutableRangeSet<C>)ImmutableRangeSet.EMPTY;
    }
    
    public static <C extends Comparable> ImmutableRangeSet<C> of(final Range<C> range) {
        Preconditions.checkNotNull(range);
        if (range.isEmpty()) {
            return of();
        }
        if (!range.equals(Range.all())) {
            return new ImmutableRangeSet<C>((ImmutableList<Range<C>>)ImmutableList.of(range));
        }
        return all();
    }
    
    @Override
    public void add(final Range<C> range) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addAll(final RangeSet<C> set) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableSet<Range<C>> asDescendingSetOfRanges() {
        if (!this.ranges.isEmpty()) {
            return new RegularImmutableSortedSet<Range<C>>(this.ranges.reverse(), Range.RANGE_LEX_ORDERING.reverse());
        }
        return ImmutableSet.of();
    }
    
    @Override
    public ImmutableSet<Range<C>> asRanges() {
        if (!this.ranges.isEmpty()) {
            return new RegularImmutableSortedSet<Range<C>>(this.ranges, Range.RANGE_LEX_ORDERING);
        }
        return ImmutableSet.of();
    }
    
    public ImmutableSortedSet<C> asSet(final DiscreteDomain<C> discreteDomain) {
        Preconditions.checkNotNull(discreteDomain);
        if (this.isEmpty()) {
            return ImmutableSortedSet.of();
        }
        final Range<C> canonical = this.span().canonical(discreteDomain);
        if (canonical.hasLowerBound()) {
            if (!canonical.hasUpperBound()) {
                try {
                    discreteDomain.maxValue();
                }
                catch (final NoSuchElementException ex) {
                    throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded above");
                }
            }
            return new AsSet(discreteDomain);
        }
        throw new IllegalArgumentException("Neither the DiscreteDomain nor this range set are bounded below");
    }
    
    @Override
    public ImmutableRangeSet<C> complement() {
        final ImmutableRangeSet<C> complement = this.complement;
        if (complement != null) {
            return complement;
        }
        if (this.ranges.isEmpty()) {
            return this.complement = all();
        }
        if (this.ranges.size() == 1 && this.ranges.get(0).equals(Range.all())) {
            return this.complement = of();
        }
        return this.complement = new ImmutableRangeSet<C>(new ComplementRanges(), this);
    }
    
    @Override
    public boolean encloses(final Range<C> range) {
        final int binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<C>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)range.lowerBound, Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        return binarySearch != -1 && ((Range<C>)this.ranges.get(binarySearch)).encloses(range);
    }
    
    @Override
    public boolean isEmpty() {
        return this.ranges.isEmpty();
    }
    
    boolean isPartialView() {
        return this.ranges.isPartialView();
    }
    
    @Override
    public Range<C> rangeContaining(final C c) {
        final int binarySearch = SortedLists.binarySearch(this.ranges, (Function<? super Range<C>, Cut<Comparable>>)Range.lowerBoundFn(), (Cut<Comparable>)Cut.belowValue(c), Ordering.natural(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER);
        if (binarySearch == -1) {
            return null;
        }
        Range range;
        if (!(range = this.ranges.get(binarySearch)).contains(c)) {
            range = null;
        }
        return range;
    }
    
    @Override
    public void remove(final Range<C> range) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void removeAll(final RangeSet<C> set) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Range<C> span() {
        if (!this.ranges.isEmpty()) {
            return Range.create((Cut<C>)this.ranges.get(0).lowerBound, (Cut<C>)this.ranges.get(this.ranges.size() - 1).upperBound);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public ImmutableRangeSet<C> subRangeSet(final Range<C> range) {
        if (!this.isEmpty()) {
            final Range<C> span = this.span();
            if (range.encloses(span)) {
                return this;
            }
            if (range.isConnected(span)) {
                return new ImmutableRangeSet<C>(this.intersectRanges(range));
            }
        }
        return of();
    }
    
    Object writeReplace() {
        return new SerializedForm((ImmutableList<Range<Comparable>>)this.ranges);
    }
    
    private final class AsSet extends ImmutableSortedSet<C>
    {
        private final DiscreteDomain<C> domain;
        private transient Integer size;
        
        AsSet(final DiscreteDomain<C> domain) {
            super(Ordering.natural());
            this.domain = domain;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            if (o == null) {
                return false;
            }
            try {
                return ImmutableRangeSet.this.contains((Comparable)o);
            }
            catch (final ClassCastException ex) {
                return false;
            }
        }
        
        @GwtIncompatible("NavigableSet")
        @Override
        public UnmodifiableIterator<C> descendingIterator() {
            return new AbstractIterator<C>() {
                Iterator<C> elemItr = Iterators.emptyIterator();
                final Iterator<Range<C>> rangeItr = ImmutableRangeSet.this.ranges.reverse().iterator();
                
                @Override
                protected C computeNext() {
                    while (!this.elemItr.hasNext()) {
                        if (!this.rangeItr.hasNext()) {
                            return this.endOfData();
                        }
                        this.elemItr = (Iterator<C>)ContiguousSet.create((Range<Comparable>)this.rangeItr.next(), AsSet.this.domain).descendingIterator();
                    }
                    return this.elemItr.next();
                }
            };
        }
        
        @Override
        ImmutableSortedSet<C> headSetImpl(final C c, final boolean b) {
            return this.subSet(Range.upTo(c, BoundType.forBoolean(b)));
        }
        
        @Override
        int indexOf(final Object o) {
            if (!this.contains(o)) {
                return -1;
            }
            final Comparable comparable = (Comparable)o;
            final Iterator iterator = ImmutableRangeSet.this.ranges.iterator();
            long n = 0L;
            while (iterator.hasNext()) {
                final Range range = (Range)iterator.next();
                if (range.contains(comparable)) {
                    return Ints.saturatedCast(ContiguousSet.create(range, this.domain).indexOf(comparable) + n);
                }
                n += ContiguousSet.create(range, this.domain).size();
            }
            throw new AssertionError((Object)"impossible");
        }
        
        @Override
        boolean isPartialView() {
            return ImmutableRangeSet.this.ranges.isPartialView();
        }
        
        @Override
        public UnmodifiableIterator<C> iterator() {
            return new AbstractIterator<C>() {
                Iterator<C> elemItr = Iterators.emptyIterator();
                final Iterator<Range<C>> rangeItr = ImmutableRangeSet.this.ranges.iterator();
                
                @Override
                protected C computeNext() {
                    while (!this.elemItr.hasNext()) {
                        if (!this.rangeItr.hasNext()) {
                            return this.endOfData();
                        }
                        this.elemItr = (Iterator<C>)ContiguousSet.create((Range<Comparable>)this.rangeItr.next(), AsSet.this.domain).iterator();
                    }
                    return this.elemItr.next();
                }
            };
        }
        
        @Override
        public int size() {
            Integer size = this.size;
            if (size == null) {
                final Iterator iterator = ImmutableRangeSet.this.ranges.iterator();
                long n = 0L;
                while (iterator.hasNext()) {
                    n += ContiguousSet.create((Range<C>)iterator.next(), this.domain).size();
                    int n2;
                    if (n < 2147483647L) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    if (n2 == 0) {
                        break;
                    }
                }
                size = Ints.saturatedCast(n);
                this.size = size;
            }
            return size;
        }
        
        ImmutableSortedSet<C> subSet(final Range<C> range) {
            return ImmutableRangeSet.this.subRangeSet(range).asSet(this.domain);
        }
        
        @Override
        ImmutableSortedSet<C> subSetImpl(final C c, final boolean b, final C c2, final boolean b2) {
            if (!b && !b2 && Range.compareOrThrow(c, c2) == 0) {
                return ImmutableSortedSet.of();
            }
            return this.subSet(Range.range(c, BoundType.forBoolean(b), c2, BoundType.forBoolean(b2)));
        }
        
        @Override
        ImmutableSortedSet<C> tailSetImpl(final C c, final boolean b) {
            return this.subSet(Range.downTo(c, BoundType.forBoolean(b)));
        }
        
        @Override
        public String toString() {
            return ImmutableRangeSet.this.ranges.toString();
        }
        
        @Override
        Object writeReplace() {
            return new AsSetSerializedForm(ImmutableRangeSet.this.ranges, (DiscreteDomain<Comparable>)this.domain);
        }
    }
    
    private static class AsSetSerializedForm<C extends Comparable> implements Serializable
    {
        private final DiscreteDomain<C> domain;
        private final ImmutableList<Range<C>> ranges;
        
        AsSetSerializedForm(final ImmutableList<Range<C>> ranges, final DiscreteDomain<C> domain) {
            this.ranges = ranges;
            this.domain = domain;
        }
        
        Object readResolve() {
            return new ImmutableRangeSet<C>(this.ranges).asSet(this.domain);
        }
    }
    
    public static class Builder<C extends Comparable<?>>
    {
        private final RangeSet<C> rangeSet;
        
        public Builder() {
            this.rangeSet = (RangeSet<C>)TreeRangeSet.create();
        }
        
        public Builder<C> add(final Range<C> obj) {
            if (obj.isEmpty()) {
                throw new IllegalArgumentException("range must not be empty, but was " + obj);
            }
            if (this.rangeSet.complement().encloses(obj)) {
                this.rangeSet.add(obj);
                return this;
            }
            for (final Range range : this.rangeSet.asRanges()) {
                Preconditions.checkArgument(!range.isConnected(obj) || range.intersection(obj).isEmpty(), "Ranges may not overlap, but received %s and %s", range, obj);
            }
            throw new AssertionError((Object)"should have thrown an IAE above");
        }
        
        public Builder<C> addAll(final RangeSet<C> set) {
            final Iterator<Range<C>> iterator = set.asRanges().iterator();
            while (iterator.hasNext()) {
                this.add(iterator.next());
            }
            return this;
        }
        
        public ImmutableRangeSet<C> build() {
            return ImmutableRangeSet.copyOf(this.rangeSet);
        }
    }
    
    private final class ComplementRanges extends ImmutableList<Range<C>>
    {
        private final boolean positiveBoundedAbove;
        private final boolean positiveBoundedBelow;
        private final int size;
        
        ComplementRanges() {
            this.positiveBoundedBelow = ((Range)ImmutableRangeSet.this.ranges.get(0)).hasLowerBound();
            this.positiveBoundedAbove = Iterables.getLast((Iterable<Range>)ImmutableRangeSet.this.ranges).hasUpperBound();
            int size = ImmutableRangeSet.this.ranges.size() - 1;
            if (this.positiveBoundedBelow) {
                ++size;
            }
            if (this.positiveBoundedAbove) {
                ++size;
            }
            this.size = size;
        }
        
        @Override
        public Range<C> get(final int n) {
            Preconditions.checkElementIndex(n, this.size);
            Object o;
            if (!this.positiveBoundedBelow) {
                o = ((Range)ImmutableRangeSet.this.ranges.get(n)).upperBound;
            }
            else if (n != 0) {
                o = ((Range)ImmutableRangeSet.this.ranges.get(n - 1)).upperBound;
            }
            else {
                o = Cut.belowAll();
            }
            Cut<Comparable> cut;
            if (this.positiveBoundedAbove && n == this.size - 1) {
                cut = (Cut<Comparable>)Cut.aboveAll();
            }
            else {
                final ImmutableList access$000 = ImmutableRangeSet.this.ranges;
                int n2;
                if (!this.positiveBoundedBelow) {
                    n2 = 1;
                }
                else {
                    n2 = 0;
                }
                cut = (Cut<Comparable>)((Range)access$000.get(n2 + n)).lowerBound;
            }
            return Range.create((Cut<C>)o, (Cut<C>)cut);
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    private static final class SerializedForm<C extends Comparable> implements Serializable
    {
        private final ImmutableList<Range<C>> ranges;
        
        SerializedForm(final ImmutableList<Range<C>> ranges) {
            this.ranges = ranges;
        }
        
        Object readResolve() {
            if (this.ranges.isEmpty()) {
                return ImmutableRangeSet.of();
            }
            if (!this.ranges.equals(ImmutableList.of(Range.all()))) {
                return new ImmutableRangeSet((ImmutableList<Range<Comparable>>)this.ranges);
            }
            return ImmutableRangeSet.all();
        }
    }
}
