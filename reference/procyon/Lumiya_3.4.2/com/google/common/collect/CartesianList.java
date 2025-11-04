// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.ListIterator;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.math.IntMath;
import com.google.common.annotations.GwtCompatible;
import java.util.RandomAccess;
import java.util.List;
import java.util.AbstractList;

@GwtCompatible
final class CartesianList<E> extends AbstractList<List<E>> implements RandomAccess
{
    private final transient ImmutableList<List<E>> axes;
    private final transient int[] axesSizeProduct;
    
    CartesianList(final ImmutableList<List<E>> axes) {
        this.axes = axes;
        final int[] axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        try {
            int i = axes.size();
            --i;
            while (i >= 0) {
                axesSizeProduct[i] = IntMath.checkedMultiply(axesSizeProduct[i + 1], ((List)axes.get(i)).size());
                --i;
            }
            this.axesSizeProduct = axesSizeProduct;
        }
        catch (final ArithmeticException ex) {
            throw new IllegalArgumentException("Cartesian product too large; must have size at most Integer.MAX_VALUE");
        }
    }
    
    static <E> List<List<E>> create(final List<? extends List<? extends E>> list) {
        final ImmutableList.Builder<ImmutableList> builder = (ImmutableList.Builder<ImmutableList>)new ImmutableList.Builder<ImmutableList<Object>>(list.size());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)iterator.next());
            if (copy.isEmpty()) {
                return (List<List<E>>)ImmutableList.of();
            }
            builder.add(copy);
        }
        return (List<List<E>>)new CartesianList((ImmutableList<List<Object>>)builder.build());
    }
    
    private int getAxisIndexForProductIndex(final int n, final int n2) {
        return n / this.axesSizeProduct[n2 + 1] % this.axes.get(n2).size();
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        if (!(o instanceof List)) {
            return false;
        }
        final List list = (List)o;
        if (list.size() == this.axes.size()) {
            final ListIterator listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                if (!((List)this.axes.get(listIterator.nextIndex())).contains(listIterator.next())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public ImmutableList<E> get(final int n) {
        Preconditions.checkElementIndex(n, this.size());
        return new ImmutableList<E>() {
            @Override
            public E get(final int n) {
                Preconditions.checkElementIndex(n, this.size());
                return ((List)CartesianList.this.axes.get(n)).get(CartesianList.this.getAxisIndexForProductIndex(n, n));
            }
            
            @Override
            boolean isPartialView() {
                return true;
            }
            
            @Override
            public int size() {
                return CartesianList.this.axes.size();
            }
        };
    }
    
    @Override
    public int size() {
        return this.axesSizeProduct[0];
    }
}
