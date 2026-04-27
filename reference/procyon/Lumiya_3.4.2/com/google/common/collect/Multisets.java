// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.io.Serializable;
import com.google.common.base.Objects;
import java.util.Set;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Predicates;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.primitives.Ints;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class Multisets
{
    private static final Ordering<Multiset.Entry<?>> DECREASING_COUNT_ORDERING;
    
    static {
        DECREASING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
            @Override
            public int compare(final Multiset.Entry<?> entry, final Multiset.Entry<?> entry2) {
                return Ints.compare(entry2.getCount(), entry.getCount());
            }
        };
    }
    
    private Multisets() {
    }
    
    static <E> boolean addAllImpl(final Multiset<E> multiset, final Collection<? extends E> collection) {
        if (!collection.isEmpty()) {
            if (!(collection instanceof Multiset)) {
                Iterators.addAll(multiset, collection.iterator());
            }
            else {
                for (final Multiset.Entry<E> entry : cast((Iterable<Object>)collection).entrySet()) {
                    multiset.add(entry.getElement(), entry.getCount());
                }
            }
            return true;
        }
        return false;
    }
    
    static <T> Multiset<T> cast(final Iterable<T> iterable) {
        return (Multiset<T>)iterable;
    }
    
    public static boolean containsOccurrences(final Multiset<?> multiset, final Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        for (final Multiset.Entry<Object> entry : multiset2.entrySet()) {
            if (multiset.count(entry.getElement()) < entry.getCount()) {
                return false;
            }
        }
        return true;
    }
    
    @Beta
    public static <E> ImmutableMultiset<E> copyHighestCountFirst(final Multiset<E> multiset) {
        return ImmutableMultiset.copyFromEntries((Collection<? extends Multiset.Entry<? extends E>>)Multisets.DECREASING_COUNT_ORDERING.immutableSortedCopy(multiset.entrySet()));
    }
    
    @Beta
    public static <E> Multiset<E> difference(final Multiset<E> multiset, final Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>() {
            @Override
            public int count(@Nullable final Object o) {
                int max = 0;
                final int count = multiset.count(o);
                if (count != 0) {
                    max = Math.max(0, count - multiset2.count(o));
                }
                return max;
            }
            
            @Override
            int distinctElements() {
                return Iterators.size(this.entryIterator());
            }
            
            @Override
            Iterator<Entry<E>> entryIterator() {
                return new AbstractIterator<Entry<E>>() {
                    final /* synthetic */ Iterator val$iterator1 = multiset.entrySet().iterator();
                    
                    @Override
                    protected Entry<E> computeNext() {
                        while (this.val$iterator1.hasNext()) {
                            final Multiset.Entry<Object> entry = this.val$iterator1.next();
                            final E element = entry.getElement();
                            final int n = entry.getCount() - multiset2.count(element);
                            if (n > 0) {
                                return Multisets.immutableEntry(element, n);
                            }
                        }
                        return (Entry<E>)((AbstractIterator<Multiset.Entry>)this).endOfData();
                    }
                };
            }
        };
    }
    
    static boolean equalsImpl(final Multiset<?> multiset, @Nullable final Object o) {
        if (o == multiset) {
            return true;
        }
        if (!(o instanceof Multiset)) {
            return false;
        }
        final Multiset multiset2 = (Multiset)o;
        if (multiset.size() == multiset2.size() && multiset.entrySet().size() == multiset2.entrySet().size()) {
            for (final Multiset.Entry<Object> entry : multiset2.entrySet()) {
                if (multiset.count(entry.getElement()) != entry.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @CheckReturnValue
    @Beta
    public static <E> Multiset<E> filter(final Multiset<E> multiset, final Predicate<? super E> predicate) {
        if (!(multiset instanceof FilteredMultiset)) {
            return new FilteredMultiset<E>((Multiset<Object>)multiset, (Predicate<? super Object>)predicate);
        }
        final FilteredMultiset filteredMultiset = (FilteredMultiset)multiset;
        return new FilteredMultiset<E>((Multiset<Object>)filteredMultiset.unfiltered, Predicates.and((Predicate<? super Object>)filteredMultiset.predicate, (Predicate<? super Object>)predicate));
    }
    
    public static <E> Multiset.Entry<E> immutableEntry(@Nullable final E e, final int n) {
        return new ImmutableEntry<E>(e, n);
    }
    
    static int inferDistinctElements(final Iterable<?> iterable) {
        if (!(iterable instanceof Multiset)) {
            return 11;
        }
        return ((Multiset)iterable).elementSet().size();
    }
    
    public static <E> Multiset<E> intersection(final Multiset<E> multiset, final Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>() {
            @Override
            public int count(final Object o) {
                int min = 0;
                final int count = multiset.count(o);
                if (count != 0) {
                    min = Math.min(count, multiset2.count(o));
                }
                return min;
            }
            
            @Override
            Set<E> createElementSet() {
                return (Set<E>)Sets.intersection(multiset.elementSet(), multiset2.elementSet());
            }
            
            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
            
            @Override
            Iterator<Entry<E>> entryIterator() {
                return new AbstractIterator<Entry<E>>() {
                    final /* synthetic */ Iterator val$iterator1 = multiset.entrySet().iterator();
                    
                    @Override
                    protected Entry<E> computeNext() {
                        while (this.val$iterator1.hasNext()) {
                            final Multiset.Entry<Object> entry = this.val$iterator1.next();
                            final E element = entry.getElement();
                            final int min = Math.min(entry.getCount(), multiset2.count(element));
                            if (min > 0) {
                                return Multisets.immutableEntry(element, min);
                            }
                        }
                        return (Entry<E>)((AbstractIterator<Multiset.Entry>)this).endOfData();
                    }
                };
            }
        };
    }
    
    static <E> Iterator<E> iteratorImpl(final Multiset<E> multiset) {
        return new MultisetIteratorImpl<E>(multiset, multiset.entrySet().iterator());
    }
    
    static boolean removeAllImpl(final Multiset<?> multiset, Collection<?> elementSet) {
        if (elementSet instanceof Multiset) {
            elementSet = ((Multiset)elementSet).elementSet();
        }
        return multiset.elementSet().removeAll(elementSet);
    }
    
    public static boolean removeOccurrences(final Multiset<?> multiset, final Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        final Iterator<Multiset.Entry<Object>> iterator = multiset.entrySet().iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Multiset.Entry<Object> entry = iterator.next();
            final int count = multiset2.count(entry.getElement());
            if (count < entry.getCount()) {
                if (count <= 0) {
                    continue;
                }
                multiset.remove(entry.getElement(), count);
                b = true;
            }
            else {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    public static boolean removeOccurrences(final Multiset<?> multiset, final Iterable<?> iterable) {
        boolean b = false;
        if (!(iterable instanceof Multiset)) {
            Preconditions.checkNotNull(multiset);
            Preconditions.checkNotNull(iterable);
            final Iterator<Object> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                b |= multiset.remove(iterator.next());
            }
            return b;
        }
        return removeOccurrences(multiset, (Multiset<?>)iterable);
    }
    
    static boolean retainAllImpl(final Multiset<?> multiset, Collection<?> elementSet) {
        Preconditions.checkNotNull(elementSet);
        if (elementSet instanceof Multiset) {
            elementSet = ((Multiset)elementSet).elementSet();
        }
        return multiset.elementSet().retainAll(elementSet);
    }
    
    public static boolean retainOccurrences(final Multiset<?> multiset, final Multiset<?> multiset2) {
        return retainOccurrencesImpl(multiset, multiset2);
    }
    
    private static <E> boolean retainOccurrencesImpl(final Multiset<E> multiset, final Multiset<?> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        final Iterator<Multiset.Entry> iterator = multiset.entrySet().iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Multiset.Entry entry = iterator.next();
            final int count = multiset2.count(entry.getElement());
            if (count != 0) {
                if (count >= entry.getCount()) {
                    continue;
                }
                multiset.setCount((E)entry.getElement(), count);
                b = true;
            }
            else {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    static <E> int setCountImpl(final Multiset<E> multiset, final E e, int n) {
        CollectPreconditions.checkNonnegative(n, "count");
        final int count = multiset.count(e);
        n -= count;
        if (n <= 0) {
            if (n < 0) {
                multiset.remove(e, -n);
            }
        }
        else {
            multiset.add(e, n);
        }
        return count;
    }
    
    static <E> boolean setCountImpl(final Multiset<E> multiset, final E e, final int n, final int n2) {
        CollectPreconditions.checkNonnegative(n, "oldCount");
        CollectPreconditions.checkNonnegative(n2, "newCount");
        if (multiset.count(e) != n) {
            return false;
        }
        multiset.setCount(e, n2);
        return true;
    }
    
    static int sizeImpl(final Multiset<?> multiset) {
        final Iterator<Multiset.Entry<?>> iterator = multiset.entrySet().iterator();
        long n = 0L;
        while (iterator.hasNext()) {
            n += iterator.next().getCount();
        }
        return Ints.saturatedCast(n);
    }
    
    @Beta
    public static <E> Multiset<E> sum(final Multiset<? extends E> multiset, final Multiset<? extends E> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>() {
            @Override
            public boolean contains(@Nullable final Object o) {
                boolean b = false;
                if (multiset.contains(o) || multiset2.contains(o)) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public int count(final Object o) {
                return multiset.count(o) + multiset2.count(o);
            }
            
            @Override
            Set<E> createElementSet() {
                return (Set<E>)Sets.union(multiset.elementSet(), multiset2.elementSet());
            }
            
            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
            
            @Override
            Iterator<Entry<E>> entryIterator() {
                return new AbstractIterator<Entry<E>>() {
                    final /* synthetic */ Iterator val$iterator1 = multiset.entrySet().iterator();
                    final /* synthetic */ Iterator val$iterator2 = multiset2.entrySet().iterator();
                    
                    @Override
                    protected Entry<E> computeNext() {
                        if (!this.val$iterator1.hasNext()) {
                            while (this.val$iterator2.hasNext()) {
                                final Multiset.Entry<Object> entry = this.val$iterator2.next();
                                final E element = entry.getElement();
                                if (!multiset.contains(element)) {
                                    return Multisets.immutableEntry(element, entry.getCount());
                                }
                            }
                            return (Entry<E>)((AbstractIterator<Multiset.Entry>)this).endOfData();
                        }
                        final Multiset.Entry<Object> entry2 = this.val$iterator1.next();
                        final E element2 = entry2.getElement();
                        return Multisets.immutableEntry(element2, entry2.getCount() + multiset2.count(element2));
                    }
                };
            }
            
            @Override
            public boolean isEmpty() {
                boolean b = false;
                if (multiset.isEmpty() && multiset2.isEmpty()) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public int size() {
                return multiset.size() + multiset2.size();
            }
        };
    }
    
    @Beta
    public static <E> Multiset<E> union(final Multiset<? extends E> multiset, final Multiset<? extends E> multiset2) {
        Preconditions.checkNotNull(multiset);
        Preconditions.checkNotNull(multiset2);
        return new AbstractMultiset<E>() {
            @Override
            public boolean contains(@Nullable final Object o) {
                boolean b = false;
                if (multiset.contains(o) || multiset2.contains(o)) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public int count(final Object o) {
                return Math.max(multiset.count(o), multiset2.count(o));
            }
            
            @Override
            Set<E> createElementSet() {
                return (Set<E>)Sets.union(multiset.elementSet(), multiset2.elementSet());
            }
            
            @Override
            int distinctElements() {
                return this.elementSet().size();
            }
            
            @Override
            Iterator<Entry<E>> entryIterator() {
                return new AbstractIterator<Entry<E>>() {
                    final /* synthetic */ Iterator val$iterator1 = multiset.entrySet().iterator();
                    final /* synthetic */ Iterator val$iterator2 = multiset2.entrySet().iterator();
                    
                    @Override
                    protected Entry<E> computeNext() {
                        if (!this.val$iterator1.hasNext()) {
                            while (this.val$iterator2.hasNext()) {
                                final Multiset.Entry<Object> entry = this.val$iterator2.next();
                                final E element = entry.getElement();
                                if (!multiset.contains(element)) {
                                    return Multisets.immutableEntry(element, entry.getCount());
                                }
                            }
                            return (Entry<E>)((AbstractIterator<Multiset.Entry>)this).endOfData();
                        }
                        final Multiset.Entry<Object> entry2 = this.val$iterator1.next();
                        final E element2 = entry2.getElement();
                        return Multisets.immutableEntry(element2, Math.max(entry2.getCount(), multiset2.count(element2)));
                    }
                };
            }
            
            @Override
            public boolean isEmpty() {
                boolean b = false;
                if (multiset.isEmpty() && multiset2.isEmpty()) {
                    b = true;
                }
                return b;
            }
        };
    }
    
    @Deprecated
    public static <E> Multiset<E> unmodifiableMultiset(final ImmutableMultiset<E> immutableMultiset) {
        return Preconditions.checkNotNull(immutableMultiset);
    }
    
    public static <E> Multiset<E> unmodifiableMultiset(final Multiset<? extends E> multiset) {
        if (!(multiset instanceof UnmodifiableMultiset) && !(multiset instanceof ImmutableMultiset)) {
            return new UnmodifiableMultiset<E>((Multiset<? extends E>)Preconditions.checkNotNull((Multiset<E>)multiset));
        }
        return (Multiset<E>)multiset;
    }
    
    @Beta
    public static <E> SortedMultiset<E> unmodifiableSortedMultiset(final SortedMultiset<E> sortedMultiset) {
        return new UnmodifiableSortedMultiset<E>(Preconditions.checkNotNull(sortedMultiset));
    }
    
    abstract static class AbstractEntry<E> implements Entry<E>
    {
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            if (this.getCount() == entry.getCount() && Objects.equal(this.getElement(), entry.getElement())) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            final Object element = this.getElement();
            int hashCode;
            if (element != null) {
                hashCode = element.hashCode();
            }
            else {
                hashCode = 0;
            }
            return hashCode ^ this.getCount();
        }
        
        @Override
        public String toString() {
            final String value = String.valueOf(this.getElement());
            final int count = this.getCount();
            String string = value;
            if (count != 1) {
                string = value + " x " + count;
            }
            return string;
        }
    }
    
    abstract static class ElementSet<E> extends ImprovedAbstractSet<E>
    {
        @Override
        public void clear() {
            this.multiset().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.multiset().contains(o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return this.multiset().containsAll(collection);
        }
        
        @Override
        public boolean isEmpty() {
            return this.multiset().isEmpty();
        }
        
        @Override
        public Iterator<E> iterator() {
            return (Iterator<E>)new TransformedIterator<Multiset.Entry<E>, E>(this.multiset().entrySet().iterator()) {
                @Override
                E transform(final Multiset.Entry<E> entry) {
                    return entry.getElement();
                }
            };
        }
        
        abstract Multiset<E> multiset();
        
        @Override
        public boolean remove(final Object o) {
            boolean b = false;
            if (this.multiset().remove(o, Integer.MAX_VALUE) > 0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int size() {
            return this.multiset().entrySet().size();
        }
    }
    
    abstract static class EntrySet<E> extends ImprovedAbstractSet<Multiset.Entry<E>>
    {
        @Override
        public void clear() {
            this.multiset().clear();
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof Multiset.Entry)) {
                return false;
            }
            final Multiset.Entry entry = (Multiset.Entry)o;
            if (entry.getCount() > 0) {
                if (this.multiset().count(entry.getElement()) == entry.getCount()) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        abstract Multiset<E> multiset();
        
        @Override
        public boolean remove(Object element) {
            if (element instanceof Multiset.Entry) {
                final Multiset.Entry entry = (Multiset.Entry)element;
                element = entry.getElement();
                final int count = entry.getCount();
                if (count != 0) {
                    return this.multiset().setCount((E)element, count, 0);
                }
            }
            return false;
        }
    }
    
    private static final class FilteredMultiset<E> extends AbstractMultiset<E>
    {
        final Predicate<? super E> predicate;
        final Multiset<E> unfiltered;
        
        FilteredMultiset(final Multiset<E> multiset, final Predicate<? super E> predicate) {
            this.unfiltered = Preconditions.checkNotNull(multiset);
            this.predicate = Preconditions.checkNotNull(predicate);
        }
        
        @Override
        public int add(@Nullable final E e, final int n) {
            Preconditions.checkArgument(this.predicate.apply(e), "Element %s does not match predicate %s", e, this.predicate);
            return this.unfiltered.add(e, n);
        }
        
        @Override
        public void clear() {
            this.elementSet().clear();
        }
        
        @Override
        public int count(@Nullable final Object o) {
            int n = 0;
            final int count = this.unfiltered.count(o);
            if (count <= 0) {
                return 0;
            }
            if (this.predicate.apply((Object)o)) {
                n = count;
            }
            return n;
        }
        
        @Override
        Set<E> createElementSet() {
            return Sets.filter(this.unfiltered.elementSet(), this.predicate);
        }
        
        @Override
        Set<Entry<E>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), (Predicate<? super Entry<E>>)new Predicate<Entry<E>>() {
                @Override
                public boolean apply(final Entry<E> entry) {
                    return FilteredMultiset.this.predicate.apply(entry.getElement());
                }
            });
        }
        
        @Override
        int distinctElements() {
            return this.elementSet().size();
        }
        
        @Override
        Iterator<Entry<E>> entryIterator() {
            throw new AssertionError((Object)"should never be called");
        }
        
        @Override
        public UnmodifiableIterator<E> iterator() {
            return Iterators.filter(this.unfiltered.iterator(), this.predicate);
        }
        
        @Override
        public int remove(@Nullable final Object o, int remove) {
            final int n = 0;
            CollectPreconditions.checkNonnegative(remove, "occurrences");
            if (remove != 0) {
                if (!this.contains(o)) {
                    remove = n;
                }
                else {
                    remove = this.unfiltered.remove(o, remove);
                }
                return remove;
            }
            return this.count(o);
        }
    }
    
    static class ImmutableEntry<E> extends AbstractEntry<E> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final int count;
        @Nullable
        private final E element;
        
        ImmutableEntry(@Nullable final E element, final int count) {
            this.element = element;
            CollectPreconditions.checkNonnegative(this.count = count, "count");
        }
        
        @Override
        public final int getCount() {
            return this.count;
        }
        
        @Nullable
        @Override
        public final E getElement() {
            return this.element;
        }
        
        public ImmutableEntry<E> nextInBucket() {
            return null;
        }
    }
    
    static final class MultisetIteratorImpl<E> implements Iterator<E>
    {
        private boolean canRemove;
        private Multiset.Entry<E> currentEntry;
        private final Iterator<Multiset.Entry<E>> entryIterator;
        private int laterCount;
        private final Multiset<E> multiset;
        private int totalCount;
        
        MultisetIteratorImpl(final Multiset<E> multiset, final Iterator<Multiset.Entry<E>> entryIterator) {
            this.multiset = multiset;
            this.entryIterator = entryIterator;
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.laterCount > 0 || this.entryIterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public E next() {
            if (this.hasNext()) {
                if (this.laterCount == 0) {
                    this.currentEntry = this.entryIterator.next();
                    final int count = this.currentEntry.getCount();
                    this.laterCount = count;
                    this.totalCount = count;
                }
                --this.laterCount;
                this.canRemove = true;
                return this.currentEntry.getElement();
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            if (this.totalCount != 1) {
                this.multiset.remove(this.currentEntry.getElement());
            }
            else {
                this.entryIterator.remove();
            }
            --this.totalCount;
            this.canRemove = false;
        }
    }
    
    static class UnmodifiableMultiset<E> extends ForwardingMultiset<E> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Multiset<? extends E> delegate;
        transient Set<E> elementSet;
        transient Set<Entry<E>> entrySet;
        
        UnmodifiableMultiset(final Multiset<? extends E> delegate) {
            this.delegate = delegate;
        }
        
        @Override
        public int add(final E e, final int n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean add(final E e) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
        
        Set<E> createElementSet() {
            return Collections.unmodifiableSet(this.delegate.elementSet());
        }
        
        @Override
        protected Multiset<E> delegate() {
            return (Multiset<E>)this.delegate;
        }
        
        @Override
        public Set<E> elementSet() {
            Set<E> elementSet = this.elementSet;
            if (elementSet == null) {
                elementSet = this.createElementSet();
                this.elementSet = elementSet;
            }
            return elementSet;
        }
        
        @Override
        public Set<Entry<E>> entrySet() {
            Object entrySet = this.entrySet;
            if (entrySet == null) {
                entrySet = Collections.unmodifiableSet((Set<? extends Entry<E>>)this.delegate.entrySet());
                this.entrySet = (Set<Entry<E>>)entrySet;
            }
            return (Set<Entry<E>>)entrySet;
        }
        
        @Override
        public Iterator<E> iterator() {
            return (Iterator<E>)Iterators.unmodifiableIterator(this.delegate.iterator());
        }
        
        @Override
        public int remove(final Object o, final int n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int setCount(final E e, final int n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean setCount(final E e, final int n, final int n2) {
            throw new UnsupportedOperationException();
        }
    }
}
