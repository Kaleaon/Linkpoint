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
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible(serializable = true)
/* loaded from: classes.dex */
public class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
    static final RegularImmutableMultiset<Object> EMPTY = new RegularImmutableMultiset<>(ImmutableList.of());
    private transient ImmutableSet<E> elementSet;
    private final transient Multisets.ImmutableEntry<E>[] entries;
    private final transient int hashCode;
    private final transient Multisets.ImmutableEntry<E>[] hashTable;
    private final transient int size;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ElementSet extends ImmutableSet.Indexed<E> {
        private ElementSet() {
        }

        @Override // com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(@Nullable Object obj) {
            return RegularImmutableMultiset.this.contains(obj);
        }

        @Override // com.google.common.collect.ImmutableSet.Indexed
        E get(int i) {
            return (E) RegularImmutableMultiset.this.entries[i].getElement();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.collect.ImmutableCollection
        public boolean isPartialView() {
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return RegularImmutableMultiset.this.entries.length;
        }
    }

    /* loaded from: classes.dex */
    private static final class NonTerminalEntry<E> extends Multisets.ImmutableEntry<E> {
        private final Multisets.ImmutableEntry<E> nextInBucket;

        NonTerminalEntry(E e, int i, Multisets.ImmutableEntry<E> immutableEntry) {
            super(e, i);
            this.nextInBucket = immutableEntry;
        }

        @Override // com.google.common.collect.Multisets.ImmutableEntry
        public Multisets.ImmutableEntry<E> nextInBucket() {
            return this.nextInBucket;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegularImmutableMultiset(Collection<? extends Multiset.Entry<? extends E>> collection) {
        int size = collection.size();
        Multisets.ImmutableEntry<E>[] immutableEntryArr = new Multisets.ImmutableEntry[size];
        if (size == 0) {
            this.entries = immutableEntryArr;
            this.hashTable = null;
            this.size = 0;
            this.hashCode = 0;
            this.elementSet = ImmutableSet.of();
            return;
        }
        int closedTableSize = Hashing.closedTableSize(size, 1.0d);
        int i = closedTableSize - 1;
        Multisets.ImmutableEntry<E>[] immutableEntryArr2 = new Multisets.ImmutableEntry[closedTableSize];
        int i2 = 0;
        long j = 0;
        int i3 = 0;
        for (Multiset.Entry<? extends E> entry : collection) {
            Object checkNotNull = Preconditions.checkNotNull(entry.getElement());
            int count = entry.getCount();
            int hashCode = checkNotNull.hashCode();
            int smear = Hashing.smear(hashCode) & i;
            Multisets.ImmutableEntry<E> immutableEntry = immutableEntryArr2[smear];
            Multisets.ImmutableEntry<E> nonTerminalEntry = immutableEntry != null ? new NonTerminalEntry<>(checkNotNull, count, immutableEntry) : !((entry instanceof Multisets.ImmutableEntry) && !(entry instanceof NonTerminalEntry)) ? new Multisets.ImmutableEntry<>(checkNotNull, count) : (Multisets.ImmutableEntry) entry;
            immutableEntryArr[i2] = nonTerminalEntry;
            immutableEntryArr2[smear] = nonTerminalEntry;
            j = count + j;
            i2++;
            i3 = (hashCode ^ count) + i3;
        }
        this.entries = immutableEntryArr;
        this.hashTable = immutableEntryArr2;
        this.size = Ints.saturatedCast(j);
        this.hashCode = i3;
    }

    @Override // com.google.common.collect.Multiset
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

    @Override // com.google.common.collect.Multiset
    public ImmutableSet<E> elementSet() {
        ImmutableSet<E> immutableSet = this.elementSet;
        if (immutableSet != null) {
            return immutableSet;
        }
        ElementSet elementSet = new ElementSet();
        this.elementSet = elementSet;
        return elementSet;
    }

    @Override // com.google.common.collect.ImmutableMultiset
    Multiset.Entry<E> getEntry(int i) {
        return this.entries[i];
    }

    @Override // com.google.common.collect.ImmutableMultiset, java.util.Collection, com.google.common.collect.Multiset
    public int hashCode() {
        return this.hashCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableCollection
    public boolean isPartialView() {
        return false;
    }

    @Override // java.util.AbstractCollection, java.util.Collection
    public int size() {
        return this.size;
    }
}
