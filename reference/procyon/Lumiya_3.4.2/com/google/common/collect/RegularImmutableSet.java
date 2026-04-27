// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class RegularImmutableSet<E> extends ImmutableSet<E>
{
    static final RegularImmutableSet<Object> EMPTY;
    private final transient Object[] elements;
    private final transient int hashCode;
    private final transient int mask;
    @VisibleForTesting
    final transient Object[] table;
    
    static {
        EMPTY = new RegularImmutableSet<Object>(ObjectArrays.EMPTY_ARRAY, 0, null, 0);
    }
    
    RegularImmutableSet(final Object[] elements, final int hashCode, final Object[] table, final int mask) {
        this.elements = elements;
        this.table = table;
        this.mask = mask;
        this.hashCode = hashCode;
    }
    
    @Override
    public boolean contains(@Nullable final Object obj) {
        final Object[] table = this.table;
        if (obj == null || table == null) {
            return false;
        }
        int smearedHash = Hashing.smearedHash(obj);
        while (true) {
            smearedHash &= this.mask;
            final Object o = table[smearedHash];
            if (o == null) {
                return false;
            }
            if (o.equals(obj)) {
                return true;
            }
            ++smearedHash;
        }
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        System.arraycopy(this.elements, 0, array, n, this.elements.length);
        return this.elements.length + n;
    }
    
    @Override
    ImmutableList<E> createAsList() {
        ImmutableList<Object> of;
        if (this.table != null) {
            of = new RegularImmutableAsList<Object>((ImmutableCollection<Object>)this, this.elements);
        }
        else {
            of = ImmutableList.of();
        }
        return (ImmutableList<E>)of;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    boolean isHashCodeFast() {
        return true;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.forArray((E[])this.elements);
    }
    
    @Override
    public int size() {
        return this.elements.length;
    }
}
