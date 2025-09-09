package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.primitives.Ints;
import java.util.Collection;
import javax.annotation.Nullable;

@GwtCompatible(serializable = true)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
    static final RegularImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset<>(ImmutableList.of());
    private transient ImmutableSet<E> elementSet;
    /* access modifiers changed from: private */
    public final transient Multisets.ImmutableEntry<E>[] entries;
    private final transient int hashCode;
    private final transient Multisets.ImmutableEntry<E>[] hashTable;
    private final transient int size;

    private final class ElementSet extends ImmutableSet.Indexed<E> {
        private ElementSet() {
        }

        public boolean contains(@Nullable Object obj) {
            return RegularImmutableMultiset.this.contains(obj);
        }

        /* access modifiers changed from: package-private */
        public E get(int i) {
            return RegularImmutableMultiset.this.entries[i].getElement();
        }

        /* access modifiers changed from: package-private */
        public boolean isPartialView() {
            return true;
        }

        public int size() {
            return RegularImmutableMultiset.this.entries.length;
        }
    }

    private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
        private final Multisets.ImmutableEntry<E> nextInBucket;

        NonTerminalEntry(E e, int i, Multisets.ImmutableEntry<E> immutableEntry) {
            super(e, i);
            this.nextInBucket = immutableEntry;
        }

        public Multisets.ImmutableEntry<E> nextInBucket() {
            return this.nextInBucket;
        }
    }

    RegularImmutableMultiset(Collection<? extends Multiset.Entry<? extends E>> collection) {
        Multisets.ImmutableEntry<E> immutableEntry;
        int size2 = collection.size();
        Multisets.ImmutableEntry<E>[] immutableEntryArr = new Multisets.ImmutableEntry[size2];
        if (size2 != 0) {
            int closedTableSize = Hashing.closedTableSize(size2, 1.0d);
            int i = closedTableSize - 1;
            Multisets.ImmutableEntry<E>[] immutableEntryArr2 = new Multisets.ImmutableEntry[closedTableSize];
            int i2 = 0;
            long j = 0;
            int i3 = 0;
            for (Multiset.Entry entry : collection) {
                Object checkNotNull = Preconditions.checkNotNull(entry.getElement());
                int count = entry.getCount();
                int hashCode2 = checkNotNull.hashCode();
                int smear = Hashing.smear(hashCode2) & i;
                Multisets.ImmutableEntry<E> immutableEntry2 = immutableEntryArr2[smear];
                if (immutableEntry2 != null) {
                    immutableEntry = new NonTerminalEntry<>(checkNotNull, count, immutableEntry2);
                } else {
                    immutableEntry = !((entry instanceof Multisets.ImmutableEntry) && !(entry instanceof NonTerminalEntry)) ? new Multisets.ImmutableEntry<>(checkNotNull, count) : (Multisets.ImmutableEntry) entry;
                }
                immutableEntryArr[i2] = immutableEntry;
                immutableEntryArr2[smear] = immutableEntry;
                j = ((long) count) + j;
                i2++;
                i3 = (hashCode2 ^ count) + i3;
            }
            this.entries = immutableEntryArr;
            this.hashTable = immutableEntryArr2;
            this.size = Ints.saturatedCast(j);
            this.hashCode = i3;
            return;
        }
        this.entries = immutableEntryArr;
        this.hashTable = null;
        this.size = 0;
        this.hashCode = 0;
        this.elementSet = ImmutableSet.of();
    }

    public int count(@Nullable Object obj) {
        Multisets.ImmutableEntry<E>[] immutableEntryArr = this.hashTable;
        if (obj == null || immutableEntryArr == null) {
            return 0;
        }
        for (Multisets.ImmutableEntry<E> immutableEntry = immutableEntryArr[Hashing.smearedHash(obj) & (immutableEntryArr.length - 1)]; immutableEntry != null; immutableEntry = immutableEntry.nextInBucket()) {
            if (Objects.equal(obj, immutableEntry.getElement())) {
                return immutableEntry.getCount();
            }
        }
        return 0;
    }

    public ImmutableSet<E> elementSet() {
        ImmutableSet<E> immutableSet = this.elementSet;
        if (immutableSet != null) {
            return immutableSet;
        }
        ElementSet elementSet2 = new ElementSet();
        this.elementSet = elementSet2;
        return elementSet2;
    }

    /* access modifiers changed from: package-private */
    public Multiset.Entry<E> getEntry(int i) {
        return this.entries[i];
    }

    public int hashCode() {
        return this.hashCode;
    }

    /* access modifiers changed from: package-private */
    public boolean isPartialView() {
        return false;
    }

    public int size() {
        return this.size;
    }
}
