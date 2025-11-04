// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import com.google.common.primitives.Ints;
import com.google.common.base.Preconditions;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
class RegularImmutableMultiset<E> extends ImmutableMultiset<E>
{
    static final RegularImmutableMultiset<Object> EMPTY;
    private transient ImmutableSet<E> elementSet;
    private final transient Multisets.ImmutableEntry<E>[] entries;
    private final transient int hashCode;
    private final transient Multisets.ImmutableEntry<E>[] hashTable;
    private final transient int size;
    
    static {
        EMPTY = new RegularImmutableMultiset<Object>((Collection<? extends Entry<?>>)ImmutableList.of());
    }
    
    RegularImmutableMultiset(final Collection<? extends Entry<? extends E>> collection) {
        final int size = collection.size();
        final Multisets.ImmutableEntry[] array = new Multisets.ImmutableEntry[size];
        if (size != 0) {
            final int closedTableSize = Hashing.closedTableSize(size, 1.0);
            final Multisets.ImmutableEntry[] hashTable = new Multisets.ImmutableEntry[closedTableSize];
            final Iterator iterator = collection.iterator();
            int n = 0;
            long n2 = 0L;
            int hashCode = 0;
            while (iterator.hasNext()) {
                final Multiset.Entry<?> entry = (Multiset.Entry<?>)iterator.next();
                final Object checkNotNull = Preconditions.checkNotNull(entry.getElement());
                final int count = entry.getCount();
                final int hashCode2 = checkNotNull.hashCode();
                final int n3 = Hashing.smear(hashCode2) & closedTableSize - 1;
                final Multisets.ImmutableEntry immutableEntry = hashTable[n3];
                Serializable s;
                if (immutableEntry != null) {
                    s = new NonTerminalEntry<Object>(checkNotNull, count, immutableEntry);
                }
                else {
                    int n4;
                    if (entry instanceof Multisets.ImmutableEntry && !(entry instanceof NonTerminalEntry)) {
                        n4 = 1;
                    }
                    else {
                        n4 = 0;
                    }
                    if (n4 == 0) {
                        s = new Multisets.ImmutableEntry<Object>(checkNotNull, count);
                    }
                    else {
                        s = (Multisets.ImmutableEntry<?>)entry;
                    }
                }
                hashTable[n3] = (array[n] = (Multisets.ImmutableEntry)s);
                n2 += count;
                ++n;
                hashCode += (hashCode2 ^ count);
            }
            this.entries = array;
            this.hashTable = hashTable;
            this.size = Ints.saturatedCast(n2);
            this.hashCode = hashCode;
        }
        else {
            this.entries = array;
            this.hashTable = null;
            this.size = 0;
            this.hashCode = 0;
            this.elementSet = ImmutableSet.of();
        }
    }
    
    @Override
    public int count(@Nullable final Object o) {
        final Multisets.ImmutableEntry<E>[] hashTable = this.hashTable;
        if (o != null && hashTable != null) {
            for (Serializable nextInBucket = hashTable[Hashing.smearedHash(o) & hashTable.length - 1]; nextInBucket != null; nextInBucket = ((Multisets.ImmutableEntry<Object>)nextInBucket).nextInBucket()) {
                if (Objects.equal(o, ((Multisets.ImmutableEntry<Object>)nextInBucket).getElement())) {
                    return ((Multisets.ImmutableEntry)nextInBucket).getCount();
                }
            }
            return 0;
        }
        return 0;
    }
    
    @Override
    public ImmutableSet<E> elementSet() {
        ImmutableSet<E> elementSet = this.elementSet;
        if (elementSet == null) {
            elementSet = new ElementSet();
            this.elementSet = elementSet;
        }
        return elementSet;
    }
    
    @Override
    Entry<E> getEntry(final int n) {
        return this.entries[n];
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    private final class ElementSet extends Indexed<E>
    {
        @Override
        public boolean contains(@Nullable final Object o) {
            return RegularImmutableMultiset.this.contains(o);
        }
        
        @Override
        E get(final int n) {
            return RegularImmutableMultiset.this.entries[n].getElement();
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return RegularImmutableMultiset.this.entries.length;
        }
    }
    
    private static final class NonTerminalEntry<E> extends ImmutableEntry<E>
    {
        private final ImmutableEntry<E> nextInBucket;
        
        NonTerminalEntry(final E e, final int n, final ImmutableEntry<E> nextInBucket) {
            super(e, n);
            this.nextInBucket = nextInBucket;
        }
        
        @Override
        public ImmutableEntry<E> nextInBucket() {
            return this.nextInBucket;
        }
    }
}
