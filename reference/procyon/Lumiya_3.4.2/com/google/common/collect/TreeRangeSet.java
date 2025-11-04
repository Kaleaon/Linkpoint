// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Predicate;
import com.google.common.base.MoreObjects;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.base.Preconditions;
import java.util.TreeMap;
import com.google.common.annotations.VisibleForTesting;
import java.util.NavigableMap;
import java.util.Set;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtIncompatible("uses NavigableMap")
public class TreeRangeSet<C extends Comparable<?>> extends AbstractRangeSet<C>
{
    private transient Set<Range<C>> asDescendingSetOfRanges;
    private transient Set<Range<C>> asRanges;
    private transient RangeSet<C> complement;
    @VisibleForTesting
    final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
    
    private TreeRangeSet(final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound) {
        this.rangesByLowerBound = rangesByLowerBound;
    }
    
    public static <C extends Comparable<?>> TreeRangeSet<C> create() {
        return new TreeRangeSet<C>(new TreeMap<Cut<C>, Range<C>>());
    }
    
    public static <C extends Comparable<?>> TreeRangeSet<C> create(final RangeSet<C> set) {
        final TreeRangeSet<Comparable> create = create();
        create.addAll((RangeSet)set);
        return (TreeRangeSet<C>)create;
    }
    
    @Nullable
    private Range<C> rangeEnclosing(final Range<C> range) {
        Preconditions.checkNotNull(range);
        final Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        Range<C> range2;
        if (floorEntry != null && floorEntry.getValue().encloses(range)) {
            range2 = floorEntry.getValue();
        }
        else {
            range2 = null;
        }
        return range2;
    }
    
    private void replaceRangeWithSameLowerBound(final Range<C> range) {
        if (!range.isEmpty()) {
            this.rangesByLowerBound.put(range.lowerBound, range);
        }
        else {
            this.rangesByLowerBound.remove(range.lowerBound);
        }
    }
    
    @Override
    public void add(final Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            final Cut<C> lowerBound = (Cut<C>)range.lowerBound;
            final Cut<C> upperBound = (Cut<C>)range.upperBound;
            final Map.Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry((Cut<C>)lowerBound);
            Cut<C> lowerBound2;
            Object upperBound2;
            if (lowerEntry == null) {
                lowerBound2 = (Cut<C>)lowerBound;
                upperBound2 = upperBound;
            }
            else {
                final Range range2 = lowerEntry.getValue();
                upperBound2 = upperBound;
                lowerBound2 = (Cut<C>)lowerBound;
                if (range2.upperBound.compareTo(lowerBound) >= 0) {
                    if (range2.upperBound.compareTo(upperBound) < 0) {
                        upperBound2 = upperBound;
                    }
                    else {
                        upperBound2 = range2.upperBound;
                    }
                    lowerBound2 = (Cut<C>)range2.lowerBound;
                }
            }
            final Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry((Cut<C>)upperBound2);
            Cut<C> upperBound3;
            if (floorEntry == null) {
                upperBound3 = (Cut<C>)upperBound2;
            }
            else {
                final Range range3 = floorEntry.getValue();
                upperBound3 = (Cut<C>)upperBound2;
                if (range3.upperBound.compareTo((Cut<C>)upperBound2) >= 0) {
                    upperBound3 = (Cut<C>)range3.upperBound;
                }
            }
            this.rangesByLowerBound.subMap(lowerBound2, upperBound3).clear();
            this.replaceRangeWithSameLowerBound(Range.create(lowerBound2, upperBound3));
        }
    }
    
    @Override
    public Set<Range<C>> asDescendingSetOfRanges() {
        Set<Range<C>> asDescendingSetOfRanges = this.asDescendingSetOfRanges;
        if (asDescendingSetOfRanges == null) {
            asDescendingSetOfRanges = new AsRanges(this.rangesByLowerBound.descendingMap().values());
            this.asDescendingSetOfRanges = asDescendingSetOfRanges;
        }
        return asDescendingSetOfRanges;
    }
    
    @Override
    public Set<Range<C>> asRanges() {
        Set<Range<C>> asRanges = this.asRanges;
        if (asRanges == null) {
            asRanges = new AsRanges(this.rangesByLowerBound.values());
            this.asRanges = asRanges;
        }
        return asRanges;
    }
    
    @Override
    public RangeSet<C> complement() {
        RangeSet<C> complement = this.complement;
        if (complement == null) {
            complement = new Complement();
            this.complement = complement;
        }
        return complement;
    }
    
    @Override
    public boolean encloses(final Range<C> range) {
        Preconditions.checkNotNull(range);
        final Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        return floorEntry != null && floorEntry.getValue().encloses(range);
    }
    
    @Nullable
    @Override
    public Range<C> rangeContaining(final C c) {
        Preconditions.checkNotNull(c);
        final Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(Cut.belowValue(c));
        if (floorEntry != null && floorEntry.getValue().contains(c)) {
            return floorEntry.getValue();
        }
        return null;
    }
    
    @Override
    public void remove(final Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            final Map.Entry<Cut<C>, Range> lowerEntry = this.rangesByLowerBound.lowerEntry(range.lowerBound);
            if (lowerEntry != null) {
                final Range range2 = lowerEntry.getValue();
                if (range2.upperBound.compareTo((Cut<C>)range.lowerBound) >= 0) {
                    if (range.hasUpperBound() && range2.upperBound.compareTo((Cut<C>)range.upperBound) >= 0) {
                        this.replaceRangeWithSameLowerBound(Range.create(range.upperBound, (Cut<C>)range2.upperBound));
                    }
                    this.replaceRangeWithSameLowerBound(Range.create((Cut<C>)range2.lowerBound, range.lowerBound));
                }
            }
            final Map.Entry<Cut<C>, Range> floorEntry = this.rangesByLowerBound.floorEntry(range.upperBound);
            if (floorEntry != null) {
                final Range range3 = floorEntry.getValue();
                if (range.hasUpperBound() && range3.upperBound.compareTo((Cut<C>)range.upperBound) >= 0) {
                    this.replaceRangeWithSameLowerBound(Range.create(range.upperBound, (Cut<C>)range3.upperBound));
                }
            }
            this.rangesByLowerBound.subMap(range.lowerBound, range.upperBound).clear();
        }
    }
    
    @Override
    public Range<C> span() {
        final Map.Entry<Cut<C>, Range> firstEntry = this.rangesByLowerBound.firstEntry();
        final Map.Entry<Cut<C>, Range> lastEntry = this.rangesByLowerBound.lastEntry();
        if (firstEntry != null) {
            return Range.create((Cut<C>)firstEntry.getValue().lowerBound, (Cut<C>)lastEntry.getValue().upperBound);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public RangeSet<C> subRangeSet(final Range<C> range) {
        TreeRangeSet set = this;
        if (!range.equals(Range.all())) {
            set = new SubRangeSet(range);
        }
        return set;
    }
    
    final class AsRanges extends ForwardingCollection<Range<C>> implements Set<Range<C>>
    {
        final Collection<Range<C>> delegate;
        
        AsRanges(final Collection<Range<C>> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        protected Collection<Range<C>> delegate() {
            return this.delegate;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return Sets.equalsImpl(this, o);
        }
        
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }
    
    private final class Complement extends TreeRangeSet<C>
    {
        Complement() {
            super(new ComplementRangesByLowerBound(TreeRangeSet.this.rangesByLowerBound), null);
        }
        
        @Override
        public void add(final Range<C> range) {
            TreeRangeSet.this.remove(range);
        }
        
        @Override
        public RangeSet<C> complement() {
            return TreeRangeSet.this;
        }
        
        @Override
        public boolean contains(final C c) {
            boolean b = false;
            if (!TreeRangeSet.this.contains(c)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public void remove(final Range<C> range) {
            TreeRangeSet.this.add(range);
        }
    }
    
    private static final class ComplementRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>>
    {
        private final Range<Cut<C>> complementLowerBoundWindow;
        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByUpperBound;
        
        ComplementRangesByLowerBound(final NavigableMap<Cut<C>, Range<C>> navigableMap) {
            this(navigableMap, Range.all());
        }
        
        private ComplementRangesByLowerBound(final NavigableMap<Cut<C>, Range<C>> positiveRangesByLowerBound, final Range<Cut<C>> complementLowerBoundWindow) {
            this.positiveRangesByLowerBound = positiveRangesByLowerBound;
            this.positiveRangesByUpperBound = (NavigableMap<Cut<C>, Range<C>>)new RangesByUpperBound((NavigableMap<Cut<Comparable>, Range<Comparable>>)positiveRangesByLowerBound);
            this.complementLowerBoundWindow = complementLowerBoundWindow;
        }
        
        private NavigableMap<Cut<C>, Range<C>> subMap(final Range<Cut<C>> range) {
            if (this.complementLowerBoundWindow.isConnected(range)) {
                return new ComplementRangesByLowerBound<Object>((NavigableMap<Cut<?>, Range<?>>)this.positiveRangesByLowerBound, (Range<Cut<?>>)range.intersection(this.complementLowerBoundWindow));
            }
            return (NavigableMap<Cut<C>, Range<C>>)ImmutableSortedMap.of();
        }
        
        @Override
        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }
        
        @Override
        public boolean containsKey(final Object o) {
            return this.get(o) != null;
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            boolean b = false;
            Cut<Comparable> aboveAll;
            if (!this.complementLowerBoundWindow.hasUpperBound()) {
                aboveAll = (Cut<Comparable>)Cut.aboveAll();
            }
            else {
                aboveAll = (Cut<Comparable>)this.complementLowerBoundWindow.upperEndpoint();
            }
            if (this.complementLowerBoundWindow.hasUpperBound() && this.complementLowerBoundWindow.upperBoundType() == BoundType.CLOSED) {
                b = true;
            }
            final PeekingIterator<Object> peekingIterator = Iterators.peekingIterator((Iterator<?>)this.positiveRangesByUpperBound.headMap((Cut<C>)aboveAll, b).descendingMap().values().iterator());
            Cut<C> lowerBound;
            if (!peekingIterator.hasNext()) {
                if (!this.complementLowerBoundWindow.contains(Cut.belowAll()) || this.positiveRangesByLowerBound.containsKey(Cut.belowAll())) {
                    return (Iterator<Entry<Cut<C>, Range<C>>>)Iterators.emptyIterator();
                }
                lowerBound = this.positiveRangesByLowerBound.higherKey(Cut.belowAll());
            }
            else if (peekingIterator.peek().upperBound != Cut.aboveAll()) {
                lowerBound = this.positiveRangesByLowerBound.higherKey((Cut<C>)peekingIterator.peek().upperBound);
            }
            else {
                lowerBound = (Cut<C>)peekingIterator.next().lowerBound;
            }
            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeUpperBound = this.val$firstComplementRangeUpperBound;
                final /* synthetic */ Cut val$firstComplementRangeUpperBound = MoreObjects.firstNonNull(lowerBound, Cut.aboveAll());
                
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (this.nextComplementRangeUpperBound != Cut.belowAll()) {
                        if (!peekingIterator.hasNext()) {
                            if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan((C)Cut.belowAll())) {
                                final Range<C> create = Range.create(Cut.belowAll(), this.nextComplementRangeUpperBound);
                                this.nextComplementRangeUpperBound = Cut.belowAll();
                                return Maps.immutableEntry(Cut.belowAll(), create);
                            }
                        }
                        else {
                            final Range range = peekingIterator.next();
                            final Range<C> create2 = Range.create((Cut<C>)range.upperBound, this.nextComplementRangeUpperBound);
                            this.nextComplementRangeUpperBound = (Cut<C>)range.lowerBound;
                            if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan((C)create2.lowerBound)) {
                                return Maps.immutableEntry(create2.lowerBound, create2);
                            }
                        }
                        return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                    }
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
            };
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            boolean b = false;
            Collection<Object> collection;
            if (!this.complementLowerBoundWindow.hasLowerBound()) {
                collection = this.positiveRangesByUpperBound.values();
            }
            else {
                final NavigableMap<Cut<C>, Range<C>> positiveRangesByUpperBound = this.positiveRangesByUpperBound;
                final Cut<C> lowerEndpoint = this.complementLowerBoundWindow.lowerEndpoint();
                if (this.complementLowerBoundWindow.lowerBoundType() == BoundType.CLOSED) {
                    b = true;
                }
                collection = positiveRangesByUpperBound.tailMap(lowerEndpoint, b).values();
            }
            final PeekingIterator<Object> peekingIterator = Iterators.peekingIterator((Iterator<?>)collection.iterator());
            Cut<Comparable> cut;
            if (this.complementLowerBoundWindow.contains(Cut.belowAll()) && (!peekingIterator.hasNext() || peekingIterator.peek().lowerBound != Cut.belowAll())) {
                cut = Cut.belowAll();
            }
            else {
                if (!peekingIterator.hasNext()) {
                    return (Iterator<Entry<Cut<C>, Range<C>>>)Iterators.emptyIterator();
                }
                cut = (Cut<Comparable>)peekingIterator.next().upperBound;
            }
            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeLowerBound = cut;
                
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (!ComplementRangesByLowerBound.this.complementLowerBoundWindow.upperBound.isLessThan((C)this.nextComplementRangeLowerBound) && this.nextComplementRangeLowerBound != Cut.aboveAll()) {
                        Predicate<C> predicate;
                        if (!peekingIterator.hasNext()) {
                            predicate = (Predicate<C>)Range.create(this.nextComplementRangeLowerBound, Cut.aboveAll());
                            this.nextComplementRangeLowerBound = Cut.aboveAll();
                        }
                        else {
                            final Range range = peekingIterator.next();
                            predicate = (Predicate<C>)Range.create((Cut<Comparable<?>>)this.nextComplementRangeLowerBound, (Cut<Comparable<?>>)range.lowerBound);
                            this.nextComplementRangeLowerBound = (Cut<C>)range.upperBound;
                        }
                        return (Entry<Cut<C>, Range<C>>)Maps.immutableEntry((Cut<C>)((Range)predicate).lowerBound, (Range)predicate);
                    }
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
            };
        }
        
        @Nullable
        @Override
        public Range<C> get(final Object o) {
            if (o instanceof Cut) {
                try {
                    final Cut cut = (Cut)o;
                    final Map.Entry<Cut<C>, Range<C>> firstEntry = this.tailMap(cut, true).firstEntry();
                    if (firstEntry != null && firstEntry.getKey().equals(cut)) {
                        return firstEntry.getValue();
                    }
                }
                catch (final ClassCastException ex) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> headMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(b)));
        }
        
        @Override
        public int size() {
            return Iterators.size(this.entryIterator());
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> subMap(final Cut<C> cut, final boolean b, final Cut<C> cut2, final boolean b2) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(b), cut2, BoundType.forBoolean(b2)));
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> tailMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(b)));
        }
    }
    
    @VisibleForTesting
    static final class RangesByUpperBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>>
    {
        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        private final Range<Cut<C>> upperBoundWindow;
        
        RangesByUpperBound(final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound) {
            this.rangesByLowerBound = rangesByLowerBound;
            this.upperBoundWindow = Range.all();
        }
        
        private RangesByUpperBound(final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound, final Range<Cut<C>> upperBoundWindow) {
            this.rangesByLowerBound = rangesByLowerBound;
            this.upperBoundWindow = upperBoundWindow;
        }
        
        private NavigableMap<Cut<C>, Range<C>> subMap(final Range<Cut<C>> range) {
            if (!range.isConnected(this.upperBoundWindow)) {
                return (NavigableMap<Cut<C>, Range<C>>)ImmutableSortedMap.of();
            }
            return new RangesByUpperBound<Object>((NavigableMap<Cut<?>, Range<?>>)this.rangesByLowerBound, (Range<Cut<?>>)range.intersection(this.upperBoundWindow));
        }
        
        @Override
        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.get(o) != null;
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            Collection<Object> collection;
            if (!this.upperBoundWindow.hasUpperBound()) {
                collection = this.rangesByLowerBound.descendingMap().values();
            }
            else {
                collection = this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
            }
            final PeekingIterator<Object> peekingIterator = Iterators.peekingIterator((Iterator<?>)collection.iterator());
            if (peekingIterator.hasNext() && this.upperBoundWindow.upperBound.isLessThan((Cut<C>)peekingIterator.peek().upperBound)) {
                peekingIterator.next();
            }
            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (peekingIterator.hasNext()) {
                        final Range range = peekingIterator.next();
                        Map.Entry<Cut<C>, Range> immutableEntry;
                        if (!RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan((C)range.upperBound)) {
                            immutableEntry = (Map.Entry<Cut<C>, Range>)(Entry)this.endOfData();
                        }
                        else {
                            immutableEntry = Maps.immutableEntry(range.upperBound, range);
                        }
                        return (Entry<Cut<C>, Range<C>>)immutableEntry;
                    }
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
            };
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            Iterator<Object> iterator;
            if (this.upperBoundWindow.hasLowerBound()) {
                final Map.Entry<Cut<C>, Range> lowerEntry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());
                if (lowerEntry != null) {
                    if (!this.upperBoundWindow.lowerBound.isLessThan((Cut<C>)lowerEntry.getValue().upperBound)) {
                        iterator = this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
                    }
                    else {
                        iterator = this.rangesByLowerBound.tailMap(lowerEntry.getKey(), true).values().iterator();
                    }
                }
                else {
                    iterator = this.rangesByLowerBound.values().iterator();
                }
            }
            else {
                iterator = this.rangesByLowerBound.values().iterator();
            }
            return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                @Override
                protected Entry<Cut<C>, Range<C>> computeNext() {
                    if (!iterator.hasNext()) {
                        return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                    }
                    final Range range = iterator.next();
                    if (!RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan((C)range.upperBound)) {
                        return (Entry<Cut<C>, Range<C>>)Maps.immutableEntry((Cut<C>)range.upperBound, range);
                    }
                    return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                }
            };
        }
        
        @Override
        public Range<C> get(@Nullable final Object o) {
            if (o instanceof Cut) {
                try {
                    final Cut cut = (Cut)o;
                    if (!this.upperBoundWindow.contains(cut)) {
                        return null;
                    }
                    final Map.Entry<Cut, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(cut);
                    if (lowerEntry != null && lowerEntry.getValue().upperBound.equals(cut)) {
                        return lowerEntry.getValue();
                    }
                }
                catch (final ClassCastException ex) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> headMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(b)));
        }
        
        @Override
        public boolean isEmpty() {
            boolean empty = false;
            if (!this.upperBoundWindow.equals(Range.all())) {
                if (!this.entryIterator().hasNext()) {
                    empty = true;
                }
            }
            else {
                empty = this.rangesByLowerBound.isEmpty();
            }
            return empty;
        }
        
        @Override
        public int size() {
            if (!this.upperBoundWindow.equals(Range.all())) {
                return Iterators.size(this.entryIterator());
            }
            return this.rangesByLowerBound.size();
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> subMap(final Cut<C> cut, final boolean b, final Cut<C> cut2, final boolean b2) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(b), cut2, BoundType.forBoolean(b2)));
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> tailMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(b)));
        }
    }
    
    private final class SubRangeSet extends TreeRangeSet<C>
    {
        private final Range<C> restriction;
        
        SubRangeSet(final Range<C> restriction) {
            super(new SubRangeSetRangesByLowerBound((Range)Range.all(), (Range)restriction, (NavigableMap)TreeRangeSet.this.rangesByLowerBound), null);
            this.restriction = restriction;
        }
        
        @Override
        public void add(final Range<C> range) {
            Preconditions.checkArgument(this.restriction.encloses(range), "Cannot add range %s to subRangeSet(%s)", range, this.restriction);
            super.add(range);
        }
        
        @Override
        public void clear() {
            TreeRangeSet.this.remove(this.restriction);
        }
        
        @Override
        public boolean contains(final C c) {
            boolean b = false;
            if (this.restriction.contains(c) && TreeRangeSet.this.contains(c)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean encloses(final Range<C> range) {
            boolean b = false;
            if (!this.restriction.isEmpty() && this.restriction.encloses(range)) {
                final Range access$600 = TreeRangeSet.this.rangeEnclosing(range);
                if (access$600 != null && !access$600.intersection(this.restriction).isEmpty()) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        @Nullable
        @Override
        public Range<C> rangeContaining(final C c) {
            final Range<C> range = null;
            if (this.restriction.contains(c)) {
                final Range<C> rangeContaining = TreeRangeSet.this.rangeContaining(c);
                Range<C> intersection = range;
                if (rangeContaining != null) {
                    intersection = rangeContaining.intersection(this.restriction);
                }
                return intersection;
            }
            return null;
        }
        
        @Override
        public void remove(final Range<C> range) {
            if (range.isConnected(this.restriction)) {
                TreeRangeSet.this.remove(range.intersection(this.restriction));
            }
        }
        
        @Override
        public RangeSet<C> subRangeSet(final Range<C> range) {
            if (range.encloses(this.restriction)) {
                return this;
            }
            if (!range.isConnected(this.restriction)) {
                return (RangeSet<C>)ImmutableRangeSet.of();
            }
            return new SubRangeSet(this.restriction.intersection(range));
        }
    }
    
    private static final class SubRangeSetRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>>
    {
        private final Range<Cut<C>> lowerBoundWindow;
        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> rangesByUpperBound;
        private final Range<C> restriction;
        
        private SubRangeSetRangesByLowerBound(final Range<Cut<C>> range, final Range<C> range2, final NavigableMap<Cut<C>, Range<C>> navigableMap) {
            this.lowerBoundWindow = Preconditions.checkNotNull(range);
            this.restriction = Preconditions.checkNotNull(range2);
            this.rangesByLowerBound = Preconditions.checkNotNull(navigableMap);
            this.rangesByUpperBound = (NavigableMap<Cut<C>, Range<C>>)new RangesByUpperBound((NavigableMap<Cut<Comparable>, Range<Comparable>>)navigableMap);
        }
        
        private NavigableMap<Cut<C>, Range<C>> subMap(final Range<Cut<C>> range) {
            if (range.isConnected(this.lowerBoundWindow)) {
                return new SubRangeSetRangesByLowerBound<Object>((Range<Cut<?>>)this.lowerBoundWindow.intersection(range), this.restriction, (NavigableMap<Cut<?>, Range<?>>)this.rangesByLowerBound);
            }
            return (NavigableMap<Cut<C>, Range<C>>)ImmutableSortedMap.of();
        }
        
        @Override
        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.get(o) != null;
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            if (!this.restriction.isEmpty()) {
                final Cut<Cut<C>> cut = Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
                return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                    final /* synthetic */ Iterator val$completeRangeItr = SubRangeSetRangesByLowerBound.this.rangesByLowerBound.headMap(cut.endpoint(), cut.typeAsUpperBound() == BoundType.CLOSED).descendingMap().values().iterator();
                    
                    @Override
                    protected Entry<Cut<C>, Range<C>> computeNext() {
                        // 
                        // This method could not be decompiled.
                        // 
                        // Original Bytecode:
                        // 
                        //     1: getfield        com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.val$completeRangeItr:Ljava/util/Iterator;
                        //     4: invokeinterface java/util/Iterator.hasNext:()Z
                        //     9: ifeq            82
                        //    12: aload_0        
                        //    13: getfield        com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.val$completeRangeItr:Ljava/util/Iterator;
                        //    16: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
                        //    21: checkcast       Lcom/google/common/collect/Range;
                        //    24: astore_1       
                        //    25: aload_0        
                        //    26: getfield        com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.this$0:Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;
                        //    29: invokestatic    com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound.access$300:(Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;)Lcom/google/common/collect/Range;
                        //    32: getfield        com/google/common/collect/Range.lowerBound:Lcom/google/common/collect/Cut;
                        //    35: aload_1        
                        //    36: getfield        com/google/common/collect/Range.upperBound:Lcom/google/common/collect/Cut;
                        //    39: invokevirtual   com/google/common/collect/Cut.compareTo:(Lcom/google/common/collect/Cut;)I
                        //    42: ifge            90
                        //    45: aload_1        
                        //    46: aload_0        
                        //    47: getfield        com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.this$0:Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;
                        //    50: invokestatic    com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound.access$300:(Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;)Lcom/google/common/collect/Range;
                        //    53: invokevirtual   com/google/common/collect/Range.intersection:(Lcom/google/common/collect/Range;)Lcom/google/common/collect/Range;
                        //    56: astore_1       
                        //    57: aload_0        
                        //    58: getfield        com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.this$0:Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;
                        //    61: invokestatic    com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound.access$400:(Lcom/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound;)Lcom/google/common/collect/Range;
                        //    64: aload_1        
                        //    65: getfield        com/google/common/collect/Range.lowerBound:Lcom/google/common/collect/Cut;
                        //    68: invokevirtual   com/google/common/collect/Range.contains:(Ljava/lang/Comparable;)Z
                        //    71: ifne            98
                        //    74: aload_0        
                        //    75: invokevirtual   com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.endOfData:()Ljava/lang/Object;
                        //    78: checkcast       Ljava/util/Map$Entry;
                        //    81: areturn        
                        //    82: aload_0        
                        //    83: invokevirtual   com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.endOfData:()Ljava/lang/Object;
                        //    86: checkcast       Ljava/util/Map$Entry;
                        //    89: areturn        
                        //    90: aload_0        
                        //    91: invokevirtual   com/google/common/collect/TreeRangeSet$SubRangeSetRangesByLowerBound$2.endOfData:()Ljava/lang/Object;
                        //    94: checkcast       Ljava/util/Map$Entry;
                        //    97: areturn        
                        //    98: aload_1        
                        //    99: getfield        com/google/common/collect/Range.lowerBound:Lcom/google/common/collect/Cut;
                        //   102: aload_1        
                        //   103: invokestatic    com/google/common/collect/Maps.immutableEntry:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map$Entry;
                        //   106: areturn        
                        //    Signature:
                        //  ()Ljava/util/Map$Entry<Lcom/google/common/collect/Cut<TC;>;Lcom/google/common/collect/Range<TC;>;>;
                        // 
                        // The error that occurred was:
                        // 
                        // java.lang.UnsupportedOperationException: The requested operation is not supported.
                        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
                        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
                        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
                        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
                        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
                        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
                        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameters(TypeSubstitutionVisitor.java:406)
                        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:317)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:667)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.inferBinaryExpression(TypeAnalysis.java:2124)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1545)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
                        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
                        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
                        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:425)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                        // 
                        throw new IllegalStateException("An error occurred while decompiling this method.");
                    }
                };
            }
            return (Iterator<Entry<Cut<C>, Range<C>>>)Iterators.emptyIterator();
        }
        
        @Override
        Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
            boolean b = false;
            if (this.restriction.isEmpty()) {
                return (Iterator<Entry<Cut<C>, Range<C>>>)Iterators.emptyIterator();
            }
            if (!this.lowerBoundWindow.upperBound.isLessThan(this.restriction.lowerBound)) {
                Iterator<Object> iterator;
                if (!this.lowerBoundWindow.lowerBound.isLessThan(this.restriction.lowerBound)) {
                    final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound = this.rangesByLowerBound;
                    final Cut<C> endpoint = this.lowerBoundWindow.lowerBound.endpoint();
                    if (this.lowerBoundWindow.lowerBoundType() == BoundType.CLOSED) {
                        b = true;
                    }
                    iterator = rangesByLowerBound.tailMap(endpoint, b).values().iterator();
                }
                else {
                    iterator = this.rangesByUpperBound.tailMap(this.restriction.lowerBound, false).values().iterator();
                }
                return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
                    final /* synthetic */ Cut val$upperBoundOnLowerBounds = Ordering.natural().min(SubRangeSetRangesByLowerBound.this.lowerBoundWindow.upperBound, Cut.belowValue(SubRangeSetRangesByLowerBound.this.restriction.upperBound));
                    
                    @Override
                    protected Entry<Cut<C>, Range<C>> computeNext() {
                        if (!iterator.hasNext()) {
                            return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                        }
                        final Range range = iterator.next();
                        if (!this.val$upperBoundOnLowerBounds.isLessThan(range.lowerBound)) {
                            final Range intersection = range.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                            return (Entry<Cut<C>, Range<C>>)Maps.immutableEntry((Cut<C>)intersection.lowerBound, intersection);
                        }
                        return (Entry<Cut<C>, Range<C>>)((AbstractIterator<Map.Entry>)this).endOfData();
                    }
                };
            }
            return (Iterator<Entry<Cut<C>, Range<C>>>)Iterators.emptyIterator();
        }
        
        @Nullable
        @Override
        public Range<C> get(@Nullable final Object o) {
            if (o instanceof Cut) {
                try {
                    final Cut cut = (Cut)o;
                    if (!this.lowerBoundWindow.contains(cut) || cut.compareTo(this.restriction.lowerBound) < 0 || cut.compareTo(this.restriction.upperBound) >= 0) {
                        return null;
                    }
                    if (!cut.equals(this.restriction.lowerBound)) {
                        final Range range = this.rangesByLowerBound.get(cut);
                        if (range != null) {
                            return range.intersection(this.restriction);
                        }
                    }
                    else {
                        final Range<C> range2 = Maps.valueOrNull((Entry<?, Range<C>>)this.rangesByLowerBound.floorEntry(cut));
                        if (range2 != null && range2.upperBound.compareTo(this.restriction.lowerBound) > 0) {
                            return range2.intersection(this.restriction);
                        }
                    }
                }
                catch (final ClassCastException ex) {
                    return null;
                }
            }
            return null;
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> headMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.upTo(cut, BoundType.forBoolean(b)));
        }
        
        @Override
        public int size() {
            return Iterators.size(this.entryIterator());
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> subMap(final Cut<C> cut, final boolean b, final Cut<C> cut2, final boolean b2) {
            return this.subMap(Range.range(cut, BoundType.forBoolean(b), cut2, BoundType.forBoolean(b2)));
        }
        
        @Override
        public NavigableMap<Cut<C>, Range<C>> tailMap(final Cut<C> cut, final boolean b) {
            return this.subMap(Range.downTo(cut, BoundType.forBoolean(b)));
        }
    }
}
