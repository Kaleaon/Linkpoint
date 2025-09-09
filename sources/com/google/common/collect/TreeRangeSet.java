package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.lang.Comparable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

@GwtIncompatible("uses NavigableMap")
@Beta
public class TreeRangeSet<C extends Comparable<?>> extends AbstractRangeSet<C> {
    private transient Set<Range<C>> asDescendingSetOfRanges;
    private transient Set<Range<C>> asRanges;
    private transient RangeSet<C> complement;
    @VisibleForTesting
    final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;

    final class AsRanges extends ForwardingCollection<Range<C>> implements Set<Range<C>> {
        final Collection<Range<C>> delegate;

        AsRanges(Collection<Range<C>> collection) {
            this.delegate = collection;
        }

        /* access modifiers changed from: protected */
        public Collection<Range<C>> delegate() {
            return this.delegate;
        }

        public boolean equals(@Nullable Object obj) {
            return Sets.equalsImpl(this, obj);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    private final class Complement extends TreeRangeSet<C> {
        Complement() {
            super(new ComplementRangesByLowerBound(TreeRangeSet.this.rangesByLowerBound));
        }

        public void add(Range<C> range) {
            TreeRangeSet.this.remove(range);
        }

        public RangeSet<C> complement() {
            return TreeRangeSet.this;
        }

        public boolean contains(C c) {
            return !TreeRangeSet.this.contains(c);
        }

        public void remove(Range<C> range) {
            TreeRangeSet.this.add(range);
        }
    }

    private static final class ComplementRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {
        /* access modifiers changed from: private */
        public final Range<Cut<C>> complementLowerBoundWindow;
        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> positiveRangesByUpperBound;

        ComplementRangesByLowerBound(NavigableMap<Cut<C>, Range<C>> navigableMap) {
            this(navigableMap, Range.all());
        }

        private ComplementRangesByLowerBound(NavigableMap<Cut<C>, Range<C>> navigableMap, Range<Cut<C>> range) {
            this.positiveRangesByLowerBound = navigableMap;
            this.positiveRangesByUpperBound = new RangesByUpperBound(navigableMap);
            this.complementLowerBoundWindow = range;
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            if (!this.complementLowerBoundWindow.isConnected(range)) {
                return ImmutableSortedMap.of();
            }
            return new ComplementRangesByLowerBound(this.positiveRangesByLowerBound, range.intersection(this.complementLowerBoundWindow));
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        public boolean containsKey(Object obj) {
            return get(obj) != null;
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            Cut<C> cut;
            boolean z = false;
            Cut aboveAll = !this.complementLowerBoundWindow.hasUpperBound() ? Cut.aboveAll() : this.complementLowerBoundWindow.upperEndpoint();
            if (this.complementLowerBoundWindow.hasUpperBound() && this.complementLowerBoundWindow.upperBoundType() == BoundType.CLOSED) {
                z = true;
            }
            final PeekingIterator peekingIterator = Iterators.peekingIterator(this.positiveRangesByUpperBound.headMap(aboveAll, z).descendingMap().values().iterator());
            if (peekingIterator.hasNext()) {
                cut = ((Range) peekingIterator.peek()).upperBound != Cut.aboveAll() ? this.positiveRangesByLowerBound.higherKey(((Range) peekingIterator.peek()).upperBound) : ((Range) peekingIterator.next()).lowerBound;
            } else if (!this.complementLowerBoundWindow.contains(Cut.belowAll()) || this.positiveRangesByLowerBound.containsKey(Cut.belowAll())) {
                return Iterators.emptyIterator();
            } else {
                cut = this.positiveRangesByLowerBound.higherKey(Cut.belowAll());
            }
            final Cut cut2 = (Cut) MoreObjects.firstNonNull(cut, Cut.aboveAll());
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeUpperBound = cut2;

                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    if (this.nextComplementRangeUpperBound == Cut.belowAll()) {
                        return (Map.Entry) endOfData();
                    }
                    if (peekingIterator.hasNext()) {
                        Range range = (Range) peekingIterator.next();
                        Range<C> create = Range.create(range.upperBound, this.nextComplementRangeUpperBound);
                        this.nextComplementRangeUpperBound = range.lowerBound;
                        if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(create.lowerBound)) {
                            return Maps.immutableEntry(create.lowerBound, create);
                        }
                    } else if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.lowerBound.isLessThan(Cut.belowAll())) {
                        Range<C> create2 = Range.create(Cut.belowAll(), this.nextComplementRangeUpperBound);
                        this.nextComplementRangeUpperBound = Cut.belowAll();
                        return Maps.immutableEntry(Cut.belowAll(), create2);
                    }
                    return (Map.Entry) endOfData();
                }
            };
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> entryIterator() {
            Collection values;
            final Cut<C> cut;
            boolean z = false;
            if (!this.complementLowerBoundWindow.hasLowerBound()) {
                values = this.positiveRangesByUpperBound.values();
            } else {
                NavigableMap<Cut<C>, Range<C>> navigableMap = this.positiveRangesByUpperBound;
                Comparable lowerEndpoint = this.complementLowerBoundWindow.lowerEndpoint();
                if (this.complementLowerBoundWindow.lowerBoundType() == BoundType.CLOSED) {
                    z = true;
                }
                values = navigableMap.tailMap(lowerEndpoint, z).values();
            }
            final PeekingIterator peekingIterator = Iterators.peekingIterator(values.iterator());
            if (this.complementLowerBoundWindow.contains(Cut.belowAll()) && (!peekingIterator.hasNext() || ((Range) peekingIterator.peek()).lowerBound != Cut.belowAll())) {
                cut = Cut.belowAll();
            } else if (!peekingIterator.hasNext()) {
                return Iterators.emptyIterator();
            } else {
                cut = ((Range) peekingIterator.next()).upperBound;
            }
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                Cut<C> nextComplementRangeLowerBound = cut;

                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    Range<C> range;
                    if (ComplementRangesByLowerBound.this.complementLowerBoundWindow.upperBound.isLessThan(this.nextComplementRangeLowerBound) || this.nextComplementRangeLowerBound == Cut.aboveAll()) {
                        return (Map.Entry) endOfData();
                    }
                    if (!peekingIterator.hasNext()) {
                        range = Range.create(this.nextComplementRangeLowerBound, Cut.aboveAll());
                        this.nextComplementRangeLowerBound = Cut.aboveAll();
                    } else {
                        Range range2 = (Range) peekingIterator.next();
                        Range<C> create = Range.create(this.nextComplementRangeLowerBound, range2.lowerBound);
                        this.nextComplementRangeLowerBound = range2.upperBound;
                        range = create;
                    }
                    return Maps.immutableEntry(range.lowerBound, range);
                }
            };
        }

        @Nullable
        public Range<C> get(Object obj) {
            if (obj instanceof Cut) {
                try {
                    Cut cut = (Cut) obj;
                    Map.Entry firstEntry = tailMap(cut, true).firstEntry();
                    if (firstEntry != null && ((Cut) firstEntry.getKey()).equals(cut)) {
                        return (Range) firstEntry.getValue();
                    }
                } catch (ClassCastException e) {
                    return null;
                }
            }
            return null;
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean z) {
            return subMap(Range.upTo(cut, BoundType.forBoolean(z)));
        }

        public int size() {
            return Iterators.size(entryIterator());
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean z, Cut<C> cut2, boolean z2) {
            return subMap(Range.range(cut, BoundType.forBoolean(z), cut2, BoundType.forBoolean(z2)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean z) {
            return subMap(Range.downTo(cut, BoundType.forBoolean(z)));
        }
    }

    @VisibleForTesting
    static final class RangesByUpperBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {
        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        /* access modifiers changed from: private */
        public final Range<Cut<C>> upperBoundWindow;

        RangesByUpperBound(NavigableMap<Cut<C>, Range<C>> navigableMap) {
            this.rangesByLowerBound = navigableMap;
            this.upperBoundWindow = Range.all();
        }

        private RangesByUpperBound(NavigableMap<Cut<C>, Range<C>> navigableMap, Range<Cut<C>> range) {
            this.rangesByLowerBound = navigableMap;
            this.upperBoundWindow = range;
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            return !range.isConnected(this.upperBoundWindow) ? ImmutableSortedMap.of() : new RangesByUpperBound(this.rangesByLowerBound, range.intersection(this.upperBoundWindow));
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        public boolean containsKey(@Nullable Object obj) {
            return get(obj) != null;
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            final PeekingIterator peekingIterator = Iterators.peekingIterator((!this.upperBoundWindow.hasUpperBound() ? this.rangesByLowerBound.descendingMap().values() : this.rangesByLowerBound.headMap(this.upperBoundWindow.upperEndpoint(), false).descendingMap().values()).iterator());
            if (peekingIterator.hasNext() && this.upperBoundWindow.upperBound.isLessThan(((Range) peekingIterator.peek()).upperBound)) {
                peekingIterator.next();
            }
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    if (!peekingIterator.hasNext()) {
                        return (Map.Entry) endOfData();
                    }
                    Range range = (Range) peekingIterator.next();
                    return !RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan(range.upperBound) ? (Map.Entry) endOfData() : Maps.immutableEntry(range.upperBound, range);
                }
            };
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> entryIterator() {
            final Iterator it;
            if (this.upperBoundWindow.hasLowerBound()) {
                Map.Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(this.upperBoundWindow.lowerEndpoint());
                it = lowerEntry != null ? !this.upperBoundWindow.lowerBound.isLessThan(lowerEntry.getValue().upperBound) ? this.rangesByLowerBound.tailMap(this.upperBoundWindow.lowerEndpoint(), true).values().iterator() : this.rangesByLowerBound.tailMap(lowerEntry.getKey(), true).values().iterator() : this.rangesByLowerBound.values().iterator();
            } else {
                it = this.rangesByLowerBound.values().iterator();
            }
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    if (!it.hasNext()) {
                        return (Map.Entry) endOfData();
                    }
                    Range range = (Range) it.next();
                    return !RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan(range.upperBound) ? Maps.immutableEntry(range.upperBound, range) : (Map.Entry) endOfData();
                }
            };
        }

        public Range<C> get(@Nullable Object obj) {
            Map.Entry<Cut<C>, Range<C>> lowerEntry;
            if (obj instanceof Cut) {
                try {
                    Cut cut = (Cut) obj;
                    if (this.upperBoundWindow.contains(cut) && (lowerEntry = this.rangesByLowerBound.lowerEntry(cut)) != null && lowerEntry.getValue().upperBound.equals(cut)) {
                        return lowerEntry.getValue();
                    }
                } catch (ClassCastException e) {
                    return null;
                }
            }
            return null;
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean z) {
            return subMap(Range.upTo(cut, BoundType.forBoolean(z)));
        }

        public boolean isEmpty() {
            return !this.upperBoundWindow.equals(Range.all()) ? !entryIterator().hasNext() : this.rangesByLowerBound.isEmpty();
        }

        public int size() {
            return !this.upperBoundWindow.equals(Range.all()) ? Iterators.size(entryIterator()) : this.rangesByLowerBound.size();
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean z, Cut<C> cut2, boolean z2) {
            return subMap(Range.range(cut, BoundType.forBoolean(z), cut2, BoundType.forBoolean(z2)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean z) {
            return subMap(Range.downTo(cut, BoundType.forBoolean(z)));
        }
    }

    private final class SubRangeSet extends TreeRangeSet<C> {
        private final Range<C> restriction;

        SubRangeSet(Range<C> range) {
            super(new SubRangeSetRangesByLowerBound(Range.all(), range, TreeRangeSet.this.rangesByLowerBound));
            this.restriction = range;
        }

        public void add(Range<C> range) {
            Preconditions.checkArgument(this.restriction.encloses(range), "Cannot add range %s to subRangeSet(%s)", range, this.restriction);
            TreeRangeSet.super.add(range);
        }

        public void clear() {
            TreeRangeSet.this.remove(this.restriction);
        }

        public boolean contains(C c) {
            return this.restriction.contains(c) && TreeRangeSet.this.contains(c);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0012, code lost:
            r1 = com.google.common.collect.TreeRangeSet.access$600(r3.this$0, r4);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean encloses(com.google.common.collect.Range<C> r4) {
            /*
                r3 = this;
                r0 = 0
                com.google.common.collect.Range<C> r1 = r3.restriction
                boolean r1 = r1.isEmpty()
                if (r1 == 0) goto L_0x000a
            L_0x0009:
                return r0
            L_0x000a:
                com.google.common.collect.Range<C> r1 = r3.restriction
                boolean r1 = r1.encloses(r4)
                if (r1 == 0) goto L_0x0009
                com.google.common.collect.TreeRangeSet r1 = com.google.common.collect.TreeRangeSet.this
                com.google.common.collect.Range r1 = r1.rangeEnclosing(r4)
                if (r1 != 0) goto L_0x001b
            L_0x001a:
                return r0
            L_0x001b:
                com.google.common.collect.Range<C> r2 = r3.restriction
                com.google.common.collect.Range r1 = r1.intersection(r2)
                boolean r1 = r1.isEmpty()
                if (r1 != 0) goto L_0x001a
                r0 = 1
                goto L_0x001a
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.TreeRangeSet.SubRangeSet.encloses(com.google.common.collect.Range):boolean");
        }

        @Nullable
        public Range<C> rangeContaining(C c) {
            Range rangeContaining;
            if (!this.restriction.contains(c) || (rangeContaining = TreeRangeSet.this.rangeContaining(c)) == null) {
                return null;
            }
            return rangeContaining.intersection(this.restriction);
        }

        public void remove(Range<C> range) {
            if (range.isConnected(this.restriction)) {
                TreeRangeSet.this.remove(range.intersection(this.restriction));
            }
        }

        public RangeSet<C> subRangeSet(Range<C> range) {
            return !range.encloses(this.restriction) ? !range.isConnected(this.restriction) ? ImmutableRangeSet.of() : new SubRangeSet(this.restriction.intersection(range)) : this;
        }
    }

    private static final class SubRangeSetRangesByLowerBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {
        /* access modifiers changed from: private */
        public final Range<Cut<C>> lowerBoundWindow;
        private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
        private final NavigableMap<Cut<C>, Range<C>> rangesByUpperBound;
        /* access modifiers changed from: private */
        public final Range<C> restriction;

        private SubRangeSetRangesByLowerBound(Range<Cut<C>> range, Range<C> range2, NavigableMap<Cut<C>, Range<C>> navigableMap) {
            this.lowerBoundWindow = (Range) Preconditions.checkNotNull(range);
            this.restriction = (Range) Preconditions.checkNotNull(range2);
            this.rangesByLowerBound = (NavigableMap) Preconditions.checkNotNull(navigableMap);
            this.rangesByUpperBound = new RangesByUpperBound(navigableMap);
        }

        private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
            return range.isConnected(this.lowerBoundWindow) ? new SubRangeSetRangesByLowerBound(this.lowerBoundWindow.intersection(range), this.restriction, this.rangesByLowerBound) : ImmutableSortedMap.of();
        }

        public Comparator<? super Cut<C>> comparator() {
            return Ordering.natural();
        }

        public boolean containsKey(@Nullable Object obj) {
            return get(obj) != null;
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
            if (this.restriction.isEmpty()) {
                return Iterators.emptyIterator();
            }
            Cut cut = (Cut) Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
            final Iterator it = this.rangesByLowerBound.headMap(cut.endpoint(), cut.typeAsUpperBound() == BoundType.CLOSED).descendingMap().values().iterator();
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    if (!it.hasNext()) {
                        return (Map.Entry) endOfData();
                    }
                    Range range = (Range) it.next();
                    if (SubRangeSetRangesByLowerBound.this.restriction.lowerBound.compareTo(range.upperBound) >= 0) {
                        return (Map.Entry) endOfData();
                    }
                    Range intersection = range.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                    return !SubRangeSetRangesByLowerBound.this.lowerBoundWindow.contains(intersection.lowerBound) ? (Map.Entry) endOfData() : Maps.immutableEntry(intersection.lowerBound, intersection);
                }
            };
        }

        /* access modifiers changed from: package-private */
        public Iterator<Map.Entry<Cut<C>, Range<C>>> entryIterator() {
            final Iterator it;
            boolean z = false;
            if (this.restriction.isEmpty()) {
                return Iterators.emptyIterator();
            }
            if (this.lowerBoundWindow.upperBound.isLessThan(this.restriction.lowerBound)) {
                return Iterators.emptyIterator();
            }
            if (!this.lowerBoundWindow.lowerBound.isLessThan(this.restriction.lowerBound)) {
                NavigableMap<Cut<C>, Range<C>> navigableMap = this.rangesByLowerBound;
                C endpoint = this.lowerBoundWindow.lowerBound.endpoint();
                if (this.lowerBoundWindow.lowerBoundType() == BoundType.CLOSED) {
                    z = true;
                }
                it = navigableMap.tailMap(endpoint, z).values().iterator();
            } else {
                it = this.rangesByUpperBound.tailMap(this.restriction.lowerBound, false).values().iterator();
            }
            final Cut cut = (Cut) Ordering.natural().min(this.lowerBoundWindow.upperBound, Cut.belowValue(this.restriction.upperBound));
            return new AbstractIterator<Map.Entry<Cut<C>, Range<C>>>() {
                /* access modifiers changed from: protected */
                public Map.Entry<Cut<C>, Range<C>> computeNext() {
                    if (!it.hasNext()) {
                        return (Map.Entry) endOfData();
                    }
                    Range range = (Range) it.next();
                    if (cut.isLessThan(range.lowerBound)) {
                        return (Map.Entry) endOfData();
                    }
                    Range intersection = range.intersection(SubRangeSetRangesByLowerBound.this.restriction);
                    return Maps.immutableEntry(intersection.lowerBound, intersection);
                }
            };
        }

        @Nullable
        public Range<C> get(@Nullable Object obj) {
            if (obj instanceof Cut) {
                try {
                    Cut cut = (Cut) obj;
                    if (!this.lowerBoundWindow.contains(cut) || cut.compareTo(this.restriction.lowerBound) < 0 || cut.compareTo(this.restriction.upperBound) >= 0) {
                        return null;
                    }
                    if (!cut.equals(this.restriction.lowerBound)) {
                        Range range = (Range) this.rangesByLowerBound.get(cut);
                        if (range != null) {
                            return range.intersection(this.restriction);
                        }
                    } else {
                        Range range2 = (Range) Maps.valueOrNull(this.rangesByLowerBound.floorEntry(cut));
                        if (range2 != null && range2.upperBound.compareTo(this.restriction.lowerBound) > 0) {
                            return range2.intersection(this.restriction);
                        }
                    }
                } catch (ClassCastException e) {
                    return null;
                }
            }
            return null;
        }

        public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean z) {
            return subMap(Range.upTo(cut, BoundType.forBoolean(z)));
        }

        public int size() {
            return Iterators.size(entryIterator());
        }

        public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean z, Cut<C> cut2, boolean z2) {
            return subMap(Range.range(cut, BoundType.forBoolean(z), cut2, BoundType.forBoolean(z2)));
        }

        public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean z) {
            return subMap(Range.downTo(cut, BoundType.forBoolean(z)));
        }
    }

    private TreeRangeSet(NavigableMap<Cut<C>, Range<C>> navigableMap) {
        this.rangesByLowerBound = navigableMap;
    }

    public static <C extends Comparable<?>> TreeRangeSet<C> create() {
        return new TreeRangeSet<>(new TreeMap());
    }

    public static <C extends Comparable<?>> TreeRangeSet<C> create(RangeSet<C> rangeSet) {
        TreeRangeSet<C> create = create();
        create.addAll(rangeSet);
        return create;
    }

    /* access modifiers changed from: private */
    @Nullable
    public Range<C> rangeEnclosing(Range<C> range) {
        Preconditions.checkNotNull(range);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        if (floorEntry != null && floorEntry.getValue().encloses(range)) {
            return floorEntry.getValue();
        }
        return null;
    }

    private void replaceRangeWithSameLowerBound(Range<C> range) {
        if (!range.isEmpty()) {
            this.rangesByLowerBound.put(range.lowerBound, range);
        } else {
            this.rangesByLowerBound.remove(range.lowerBound);
        }
    }

    public void add(Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            Cut<C> cut = range.lowerBound;
            Cut<C> cut2 = range.upperBound;
            Map.Entry<K, V> lowerEntry = this.rangesByLowerBound.lowerEntry(cut);
            if (lowerEntry != null) {
                Range range2 = (Range) lowerEntry.getValue();
                if (range2.upperBound.compareTo(cut) >= 0) {
                    if (range2.upperBound.compareTo(cut2) >= 0) {
                        cut2 = range2.upperBound;
                    }
                    cut = range2.lowerBound;
                }
            }
            Map.Entry<K, V> floorEntry = this.rangesByLowerBound.floorEntry(cut2);
            if (floorEntry != null) {
                Range range3 = (Range) floorEntry.getValue();
                if (range3.upperBound.compareTo(cut2) >= 0) {
                    cut2 = range3.upperBound;
                }
            }
            this.rangesByLowerBound.subMap(cut, cut2).clear();
            replaceRangeWithSameLowerBound(Range.create(cut, cut2));
        }
    }

    public /* bridge */ /* synthetic */ void addAll(RangeSet rangeSet) {
        super.addAll(rangeSet);
    }

    public Set<Range<C>> asDescendingSetOfRanges() {
        Set<Range<C>> set = this.asDescendingSetOfRanges;
        if (set != null) {
            return set;
        }
        AsRanges asRanges2 = new AsRanges(this.rangesByLowerBound.descendingMap().values());
        this.asDescendingSetOfRanges = asRanges2;
        return asRanges2;
    }

    public Set<Range<C>> asRanges() {
        Set<Range<C>> set = this.asRanges;
        if (set != null) {
            return set;
        }
        AsRanges asRanges2 = new AsRanges(this.rangesByLowerBound.values());
        this.asRanges = asRanges2;
        return asRanges2;
    }

    public /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    public RangeSet<C> complement() {
        RangeSet<C> rangeSet = this.complement;
        if (rangeSet != null) {
            return rangeSet;
        }
        Complement complement2 = new Complement();
        this.complement = complement2;
        return complement2;
    }

    public /* bridge */ /* synthetic */ boolean contains(Comparable comparable) {
        return super.contains(comparable);
    }

    public boolean encloses(Range<C> range) {
        Preconditions.checkNotNull(range);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.lowerBound);
        return floorEntry != null && floorEntry.getValue().encloses(range);
    }

    public /* bridge */ /* synthetic */ boolean enclosesAll(RangeSet rangeSet) {
        return super.enclosesAll(rangeSet);
    }

    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    @Nullable
    public Range<C> rangeContaining(C c) {
        Preconditions.checkNotNull(c);
        Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(Cut.belowValue(c));
        if (floorEntry != null && floorEntry.getValue().contains(c)) {
            return floorEntry.getValue();
        }
        return null;
    }

    public void remove(Range<C> range) {
        Preconditions.checkNotNull(range);
        if (!range.isEmpty()) {
            Map.Entry<Cut<C>, Range<C>> lowerEntry = this.rangesByLowerBound.lowerEntry(range.lowerBound);
            if (lowerEntry != null) {
                Range value = lowerEntry.getValue();
                if (value.upperBound.compareTo(range.lowerBound) >= 0) {
                    if (range.hasUpperBound() && value.upperBound.compareTo(range.upperBound) >= 0) {
                        replaceRangeWithSameLowerBound(Range.create(range.upperBound, value.upperBound));
                    }
                    replaceRangeWithSameLowerBound(Range.create(value.lowerBound, range.lowerBound));
                }
            }
            Map.Entry<Cut<C>, Range<C>> floorEntry = this.rangesByLowerBound.floorEntry(range.upperBound);
            if (floorEntry != null) {
                Range value2 = floorEntry.getValue();
                if (range.hasUpperBound() && value2.upperBound.compareTo(range.upperBound) >= 0) {
                    replaceRangeWithSameLowerBound(Range.create(range.upperBound, value2.upperBound));
                }
            }
            this.rangesByLowerBound.subMap(range.lowerBound, range.upperBound).clear();
        }
    }

    public /* bridge */ /* synthetic */ void removeAll(RangeSet rangeSet) {
        super.removeAll(rangeSet);
    }

    public Range<C> span() {
        Map.Entry<Cut<C>, Range<C>> firstEntry = this.rangesByLowerBound.firstEntry();
        Map.Entry<Cut<C>, Range<C>> lastEntry = this.rangesByLowerBound.lastEntry();
        if (firstEntry != null) {
            return Range.create(firstEntry.getValue().lowerBound, lastEntry.getValue().upperBound);
        }
        throw new NoSuchElementException();
    }

    public RangeSet<C> subRangeSet(Range<C> range) {
        return !range.equals(Range.all()) ? new SubRangeSet(range) : this;
    }
}
