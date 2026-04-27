// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Set;
import com.google.common.annotations.GwtIncompatible;
import java.util.Arrays;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableMultiset<E> extends ImmutableCollection<E> implements Multiset<E>
{
    private transient ImmutableSet<Entry<E>> entrySet;
    
    ImmutableMultiset() {
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    private static <E> ImmutableMultiset<E> copyFromElements(final E... elements) {
        final LinkedHashMultiset<Object> create = LinkedHashMultiset.create();
        Collections.addAll(create, elements);
        return (ImmutableMultiset<E>)copyFromEntries((Collection<? extends Entry<?>>)create.entrySet());
    }
    
    static <E> ImmutableMultiset<E> copyFromEntries(final Collection<? extends Entry<? extends E>> collection) {
        if (!collection.isEmpty()) {
            return new RegularImmutableMultiset<E>(collection);
        }
        return of();
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final Iterable<? extends E> iterable) {
        if (iterable instanceof ImmutableMultiset) {
            final ImmutableMultiset immutableMultiset = (ImmutableMultiset)iterable;
            if (!immutableMultiset.isPartialView()) {
                return immutableMultiset;
            }
        }
        Multiset<Object> multiset;
        if (!(iterable instanceof Multiset)) {
            multiset = LinkedHashMultiset.create((Iterable<?>)iterable);
        }
        else {
            multiset = Multisets.cast((Iterable<Object>)iterable);
        }
        return (ImmutableMultiset<E>)copyFromEntries((Collection<? extends Entry<?>>)multiset.entrySet());
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final Iterator<? extends E> iterator) {
        final LinkedHashMultiset<Object> create = LinkedHashMultiset.create();
        Iterators.addAll(create, iterator);
        return (ImmutableMultiset<E>)copyFromEntries((Collection<? extends Entry<?>>)create.entrySet());
    }
    
    public static <E> ImmutableMultiset<E> copyOf(final E[] array) {
        return (ImmutableMultiset<E>)copyFromElements((Object[])array);
    }
    
    private final ImmutableSet<Entry<E>> createEntrySet() {
        Serializable of;
        if (!this.isEmpty()) {
            of = new EntrySet();
        }
        else {
            of = ImmutableSet.of();
        }
        return (ImmutableSet<Entry<E>>)of;
    }
    
    public static <E> ImmutableMultiset<E> of() {
        return (ImmutableMultiset<E>)RegularImmutableMultiset.EMPTY;
    }
    
    public static <E> ImmutableMultiset<E> of(final E e) {
        return copyFromElements(e);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e, final E e2) {
        return copyFromElements(e, e2);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e, final E e2, final E e3) {
        return copyFromElements(e, e2, e3);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e, final E e2, final E e3, final E e4) {
        return copyFromElements(e, e2, e3, e4);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e, final E e2, final E e3, final E e4, final E e5) {
        return copyFromElements(e, e2, e3, e4, e5);
    }
    
    public static <E> ImmutableMultiset<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E... array) {
        return new Builder<E>().add(e).add(e2).add(e3).add(e4).add(e5).add(e6).add(array).build();
    }
    
    @Deprecated
    @Override
    public final int add(final E e, final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        boolean b = false;
        if (this.count(o) > 0) {
            b = true;
        }
        return b;
    }
    
    @GwtIncompatible("not present in emulated superclass")
    @Override
    int copyIntoArray(final Object[] a, int fromIndex) {
        for (final Entry<Object> entry : this.entrySet()) {
            Arrays.fill(a, fromIndex, entry.getCount() + fromIndex, entry.getElement());
            fromIndex += entry.getCount();
        }
        return fromIndex;
    }
    
    @Override
    public ImmutableSet<Entry<E>> entrySet() {
        ImmutableSet<Entry<E>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = this.createEntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return Multisets.equalsImpl(this, o);
    }
    
    abstract Entry<E> getEntry(final int p0);
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return new UnmodifiableIterator<E>() {
            E element;
            int remaining;
            final /* synthetic */ Iterator val$entryIterator = ImmutableMultiset.this.entrySet().iterator();
            
            @Override
            public boolean hasNext() {
                boolean b = false;
                if (this.remaining > 0 || this.val$entryIterator.hasNext()) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public E next() {
                if (this.remaining <= 0) {
                    final Entry<E> entry = this.val$entryIterator.next();
                    this.element = entry.getElement();
                    this.remaining = entry.getCount();
                }
                --this.remaining;
                return this.element;
            }
        };
    }
    
    @Deprecated
    @Override
    public final int remove(final Object o, final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final int setCount(final E e, final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final boolean setCount(final E e, final int n, final int n2) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return this.entrySet().toString();
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    public static class Builder<E> extends ImmutableCollection.Builder<E>
    {
        final Multiset<E> contents;
        
        public Builder() {
            this(LinkedHashMultiset.create());
        }
        
        Builder(final Multiset<E> contents) {
            this.contents = contents;
        }
        
        public Builder<E> add(final E e) {
            this.contents.add(Preconditions.checkNotNull(e));
            return this;
        }
        
        public Builder<E> add(final E... array) {
            super.add(array);
            return this;
        }
        
        public Builder<E> addAll(final Iterable<? extends E> iterable) {
            if (!(iterable instanceof Multiset)) {
                super.addAll(iterable);
            }
            else {
                for (final Entry<E> entry : Multisets.cast((Iterable<Object>)iterable).entrySet()) {
                    this.addCopies(entry.getElement(), entry.getCount());
                }
            }
            return this;
        }
        
        public Builder<E> addAll(final Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }
        
        public Builder<E> addCopies(final E e, final int n) {
            this.contents.add(Preconditions.checkNotNull(e), n);
            return this;
        }
        
        public ImmutableMultiset<E> build() {
            return ImmutableMultiset.copyOf((Iterable<? extends E>)this.contents);
        }
        
        public Builder<E> setCount(final E e, final int n) {
            this.contents.setCount(Preconditions.checkNotNull(e), n);
            return this;
        }
    }
    
    private final class EntrySet extends Indexed<Entry<E>>
    {
        private static final long serialVersionUID = 0L;
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            if (entry.getCount() > 0) {
                if (ImmutableMultiset.this.count(entry.getElement()) == entry.getCount()) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        Entry<E> get(final int n) {
            return ImmutableMultiset.this.getEntry(n);
        }
        
        @Override
        public int hashCode() {
            return ImmutableMultiset.this.hashCode();
        }
        
        @Override
        boolean isPartialView() {
            return ImmutableMultiset.this.isPartialView();
        }
        
        @Override
        public int size() {
            return ImmutableMultiset.this.elementSet().size();
        }
        
        @Override
        Object writeReplace() {
            return new EntrySetSerializedForm(ImmutableMultiset.this);
        }
    }
    
    static class EntrySetSerializedForm<E> implements Serializable
    {
        final ImmutableMultiset<E> multiset;
        
        EntrySetSerializedForm(final ImmutableMultiset<E> multiset) {
            this.multiset = multiset;
        }
        
        Object readResolve() {
            return this.multiset.entrySet();
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final int[] counts;
        final Object[] elements;
        
        SerializedForm(final Multiset<?> multiset) {
            final int size = multiset.entrySet().size();
            this.elements = new Object[size];
            this.counts = new int[size];
            final Iterator<Entry<?>> iterator = multiset.entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Entry<Object> entry = iterator.next();
                this.elements[n] = entry.getElement();
                this.counts[n] = entry.getCount();
                ++n;
            }
        }
        
        Object readResolve() {
            final LinkedHashMultiset<Object> create = LinkedHashMultiset.create(this.elements.length);
            for (int i = 0; i < this.elements.length; ++i) {
                create.add(this.elements[i], this.counts[i]);
            }
            return ImmutableMultiset.copyOf((Iterable<?>)create);
        }
    }
}
