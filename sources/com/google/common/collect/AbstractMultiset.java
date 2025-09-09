package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    private transient Set<E> elementSet;
    private transient Set<Multiset.Entry<E>> entrySet;

    class ElementSet extends Multisets.ElementSet<E> {
        ElementSet() {
        }

        /* access modifiers changed from: package-private */
        public Multiset<E> multiset() {
            return AbstractMultiset.this;
        }
    }

    class EntrySet extends Multisets.EntrySet<E> {
        EntrySet() {
        }

        public Iterator<Multiset.Entry<E>> iterator() {
            return AbstractMultiset.this.entryIterator();
        }

        /* access modifiers changed from: package-private */
        public Multiset<E> multiset() {
            return AbstractMultiset.this;
        }

        public int size() {
            return AbstractMultiset.this.distinctElements();
        }
    }

    AbstractMultiset() {
    }

    public int add(@Nullable E e, int i) {
        throw new UnsupportedOperationException();
    }

    public boolean add(@Nullable E e) {
        add(e, 1);
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return Multisets.addAllImpl(this, collection);
    }

    public void clear() {
        Iterators.clear(entryIterator());
    }

    public boolean contains(@Nullable Object obj) {
        return count(obj) > 0;
    }

    public int count(@Nullable Object obj) {
        for (Multiset.Entry entry : entrySet()) {
            if (Objects.equal(entry.getElement(), obj)) {
                return entry.getCount();
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public Set<E> createElementSet() {
        return new ElementSet();
    }

    /* access modifiers changed from: package-private */
    public Set<Multiset.Entry<E>> createEntrySet() {
        return new EntrySet();
    }

    /* access modifiers changed from: package-private */
    public abstract int distinctElements();

    public Set<E> elementSet() {
        Set<E> set = this.elementSet;
        if (set != null) {
            return set;
        }
        Set<E> createElementSet = createElementSet();
        this.elementSet = createElementSet;
        return createElementSet;
    }

    /* access modifiers changed from: package-private */
    public abstract Iterator<Multiset.Entry<E>> entryIterator();

    public Set<Multiset.Entry<E>> entrySet() {
        Set<Multiset.Entry<E>> set = this.entrySet;
        if (set != null) {
            return set;
        }
        Set<Multiset.Entry<E>> createEntrySet = createEntrySet();
        this.entrySet = createEntrySet;
        return createEntrySet;
    }

    public boolean equals(@Nullable Object obj) {
        return Multisets.equalsImpl(this, obj);
    }

    public int hashCode() {
        return entrySet().hashCode();
    }

    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    public int remove(@Nullable Object obj, int i) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(@Nullable Object obj) {
        return remove(obj, 1) > 0;
    }

    public boolean removeAll(Collection<?> collection) {
        return Multisets.removeAllImpl(this, collection);
    }

    public boolean retainAll(Collection<?> collection) {
        return Multisets.retainAllImpl(this, collection);
    }

    public int setCount(@Nullable E e, int i) {
        return Multisets.setCountImpl(this, e, i);
    }

    public boolean setCount(@Nullable E e, int i, int i2) {
        return Multisets.setCountImpl(this, e, i, i2);
    }

    public int size() {
        return Multisets.sizeImpl(this);
    }

    public String toString() {
        return entrySet().toString();
    }
}
