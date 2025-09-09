package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableMultiset<E> extends ImmutableCollection<E> implements Multiset<E> {
    private transient ImmutableSet<Multiset.Entry<E>> entrySet;

    public static class Builder<E> extends ImmutableCollection.Builder<E> {
        final Multiset<E> contents;

        public Builder() {
            this(LinkedHashMultiset.create());
        }

        Builder(Multiset<E> multiset) {
            this.contents = multiset;
        }

        public Builder<E> add(E e) {
            this.contents.add(Preconditions.checkNotNull(e));
            return this;
        }

        public Builder<E> add(E... eArr) {
            super.add(eArr);
            return this;
        }

        public Builder<E> addAll(Iterable<? extends E> iterable) {
            if (!(iterable instanceof Multiset)) {
                super.addAll(iterable);
            } else {
                for (Multiset.Entry next : Multisets.cast(iterable).entrySet()) {
                    addCopies(next.getElement(), next.getCount());
                }
            }
            return this;
        }

        public Builder<E> addAll(Iterator<? extends E> it) {
            super.addAll(it);
            return this;
        }

        public Builder<E> addCopies(E e, int i) {
            this.contents.add(Preconditions.checkNotNull(e), i);
            return this;
        }

        public ImmutableMultiset<E> build() {
            return ImmutableMultiset.copyOf(this.contents);
        }

        public Builder<E> setCount(E e, int i) {
            this.contents.setCount(Preconditions.checkNotNull(e), i);
            return this;
        }
    }

    private final class EntrySet extends ImmutableSet.Indexed<Multiset.Entry<E>> {
        private static final long serialVersionUID = 0;

        private EntrySet() {
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Multiset.Entry)) {
                return false;
            }
            Multiset.Entry entry = (Multiset.Entry) obj;
            return entry.getCount() > 0 && ImmutableMultiset.this.count(entry.getElement()) == entry.getCount();
        }

        /* access modifiers changed from: package-private */
        public Multiset.Entry<E> get(int i) {
            return ImmutableMultiset.this.getEntry(i);
        }

        public int hashCode() {
            return ImmutableMultiset.this.hashCode();
        }

        /* access modifiers changed from: package-private */
        public boolean isPartialView() {
            return ImmutableMultiset.this.isPartialView();
        }

        public int size() {
            return ImmutableMultiset.this.elementSet().size();
        }

        /* access modifiers changed from: package-private */
        public Object writeReplace() {
            return new EntrySetSerializedForm(ImmutableMultiset.this);
        }
    }

    static class EntrySetSerializedForm<E> implements Serializable {
        final ImmutableMultiset<E> multiset;

        EntrySetSerializedForm(ImmutableMultiset<E> immutableMultiset) {
            this.multiset = immutableMultiset;
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
            return this.multiset.entrySet();
        }
    }

    private static class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        final int[] counts;
        final Object[] elements;

        SerializedForm(Multiset<?> multiset) {
            int i = 0;
            int size = multiset.entrySet().size();
            this.elements = new Object[size];
            this.counts = new int[size];
            Iterator<Multiset.Entry<?>> it = multiset.entrySet().iterator();
            while (true) {
                int i2 = i;
                if (it.hasNext()) {
                    Multiset.Entry next = it.next();
                    this.elements[i2] = next.getElement();
                    this.counts[i2] = next.getCount();
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
            LinkedHashMultiset create = LinkedHashMultiset.create(this.elements.length);
            for (int i = 0; i < this.elements.length; i++) {
                create.add(this.elements[i], this.counts[i]);
            }
            return ImmutableMultiset.copyOf(create);
        }
    }

    ImmutableMultiset() {
    }

    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    private static <E> ImmutableMultiset<E> copyFromElements(E... eArr) {
        LinkedHashMultiset create = LinkedHashMultiset.create();
        Collections.addAll(create, eArr);
        return copyFromEntries(create.entrySet());
    }

    static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> collection) {
        return !collection.isEmpty() ? new RegularImmutableMultiset(collection) : of();
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [java.lang.Iterable<? extends E>, java.lang.Iterable] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <E> com.google.common.collect.ImmutableMultiset<E> copyOf(java.lang.Iterable<? extends E> r2) {
        /*
            boolean r0 = r2 instanceof com.google.common.collect.ImmutableMultiset
            if (r0 != 0) goto L_0x0015
        L_0x0004:
            boolean r0 = r2 instanceof com.google.common.collect.Multiset
            if (r0 != 0) goto L_0x001f
            com.google.common.collect.LinkedHashMultiset r0 = com.google.common.collect.LinkedHashMultiset.create(r2)
        L_0x000c:
            java.util.Set r0 = r0.entrySet()
            com.google.common.collect.ImmutableMultiset r0 = copyFromEntries(r0)
            return r0
        L_0x0015:
            r0 = r2
            com.google.common.collect.ImmutableMultiset r0 = (com.google.common.collect.ImmutableMultiset) r0
            boolean r1 = r0.isPartialView()
            if (r1 != 0) goto L_0x0004
            return r0
        L_0x001f:
            com.google.common.collect.Multiset r0 = com.google.common.collect.Multisets.cast(r2)
            goto L_0x000c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ImmutableMultiset.copyOf(java.lang.Iterable):com.google.common.collect.ImmutableMultiset");
    }

    public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> it) {
        LinkedHashMultiset create = LinkedHashMultiset.create();
        Iterators.addAll(create, it);
        return copyFromEntries(create.entrySet());
    }

    public static <E> ImmutableMultiset<E> copyOf(E[] eArr) {
        return copyFromElements(eArr);
    }

    private final ImmutableSet<Multiset.Entry<E>> createEntrySet() {
        return !isEmpty() ? new EntrySet() : ImmutableSet.of();
    }

    public static <E> ImmutableMultiset<E> of() {
        return RegularImmutableMultiset.EMPTY;
    }

    public static <E> ImmutableMultiset<E> of(E e) {
        return copyFromElements(e);
    }

    public static <E> ImmutableMultiset<E> of(E e, E e2) {
        return copyFromElements(e, e2);
    }

    public static <E> ImmutableMultiset<E> of(E e, E e2, E e3) {
        return copyFromElements(e, e2, e3);
    }

    public static <E> ImmutableMultiset<E> of(E e, E e2, E e3, E e4) {
        return copyFromElements(e, e2, e3, e4);
    }

    public static <E> ImmutableMultiset<E> of(E e, E e2, E e3, E e4, E e5) {
        return copyFromElements(e, e2, e3, e4, e5);
    }

    public static <E> ImmutableMultiset<E> of(E e, E e2, E e3, E e4, E e5, E e6, E... eArr) {
        return new Builder().add((Object) e).add((Object) e2).add((Object) e3).add((Object) e4).add((Object) e5).add((Object) e6).add((Object[]) eArr).build();
    }

    @Deprecated
    public final int add(E e, int i) {
        throw new UnsupportedOperationException();
    }

    public boolean contains(@Nullable Object obj) {
        return count(obj) > 0;
    }

    /* access modifiers changed from: package-private */
    @GwtIncompatible("not present in emulated superclass")
    public int copyIntoArray(Object[] objArr, int i) {
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Multiset.Entry entry = (Multiset.Entry) it.next();
            Arrays.fill(objArr, i, entry.getCount() + i, entry.getElement());
            i += entry.getCount();
        }
        return i;
    }

    public ImmutableSet<Multiset.Entry<E>> entrySet() {
        ImmutableSet<Multiset.Entry<E>> immutableSet = this.entrySet;
        if (immutableSet != null) {
            return immutableSet;
        }
        ImmutableSet<Multiset.Entry<E>> createEntrySet = createEntrySet();
        this.entrySet = createEntrySet;
        return createEntrySet;
    }

    public boolean equals(@Nullable Object obj) {
        return Multisets.equalsImpl(this, obj);
    }

    /* access modifiers changed from: package-private */
    public abstract Multiset.Entry<E> getEntry(int i);

    public int hashCode() {
        return Sets.hashCodeImpl(entrySet());
    }

    public UnmodifiableIterator<E> iterator() {
        final UnmodifiableIterator it = entrySet().iterator();
        return new UnmodifiableIterator<E>() {
            E element;
            int remaining;

            public boolean hasNext() {
                return this.remaining > 0 || it.hasNext();
            }

            public E next() {
                if (this.remaining <= 0) {
                    Multiset.Entry entry = (Multiset.Entry) it.next();
                    this.element = entry.getElement();
                    this.remaining = entry.getCount();
                }
                this.remaining--;
                return this.element;
            }
        };
    }

    @Deprecated
    public final int remove(Object obj, int i) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public final int setCount(E e, int i) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public final boolean setCount(E e, int i, int i2) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return entrySet().toString();
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return new SerializedForm(this);
    }
}
