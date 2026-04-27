// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class ReverseNaturalOrdering extends Ordering<Comparable> implements Serializable
{
    static final ReverseNaturalOrdering INSTANCE;
    private static final long serialVersionUID = 0L;
    
    static {
        INSTANCE = new ReverseNaturalOrdering();
    }
    
    private ReverseNaturalOrdering() {
    }
    
    private Object readResolve() {
        return ReverseNaturalOrdering.INSTANCE;
    }
    
    @Override
    public int compare(final Comparable comparable, final Comparable comparable2) {
        Preconditions.checkNotNull(comparable);
        if (comparable != comparable2) {
            return comparable2.compareTo(comparable);
        }
        return 0;
    }
    
    public <E extends Comparable> E max(final E e, final E e2) {
        return NaturalOrdering.INSTANCE.min(e, e2);
    }
    
    public <E extends Comparable> E max(final E e, final E e2, final E e3, final E... array) {
        return NaturalOrdering.INSTANCE.min(e, e2, e3, array);
    }
    
    @Override
    public <E extends Comparable> E max(final Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.min(iterable);
    }
    
    @Override
    public <E extends Comparable> E max(final Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.min(iterator);
    }
    
    public <E extends Comparable> E min(final E e, final E e2) {
        return NaturalOrdering.INSTANCE.max(e, e2);
    }
    
    public <E extends Comparable> E min(final E e, final E e2, final E e3, final E... array) {
        return NaturalOrdering.INSTANCE.max(e, e2, e3, array);
    }
    
    @Override
    public <E extends Comparable> E min(final Iterable<E> iterable) {
        return NaturalOrdering.INSTANCE.max(iterable);
    }
    
    @Override
    public <E extends Comparable> E min(final Iterator<E> iterator) {
        return NaturalOrdering.INSTANCE.max(iterator);
    }
    
    @Override
    public <S extends Comparable> Ordering<S> reverse() {
        return Ordering.natural();
    }
    
    @Override
    public String toString() {
        return "Ordering.natural().reverse()";
    }
}
