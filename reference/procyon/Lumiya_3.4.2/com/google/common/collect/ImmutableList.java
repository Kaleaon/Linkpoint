// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.ListIterator;
import javax.annotation.Nullable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import java.util.RandomAccess;
import java.util.List;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableList<E> extends ImmutableCollection<E> implements List<E>, RandomAccess
{
    ImmutableList() {
    }
    
    static <E> ImmutableList<E> asImmutableList(final Object[] array) {
        return asImmutableList(array, array.length);
    }
    
    static <E> ImmutableList<E> asImmutableList(Object[] arraysCopy, final int n) {
        switch (n) {
            default: {
                if (n < arraysCopy.length) {
                    arraysCopy = ObjectArrays.arraysCopyOf(arraysCopy, n);
                }
                return new RegularImmutableList<E>(arraysCopy);
            }
            case 0: {
                return of();
            }
            case 1: {
                return new SingletonImmutableList<E>((E)arraysCopy[0]);
            }
        }
    }
    
    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }
    
    private static <E> ImmutableList<E> construct(final Object... array) {
        return (ImmutableList<E>)asImmutableList(ObjectArrays.checkElementsNotNull(array));
    }
    
    public static <E> ImmutableList<E> copyOf(final Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(iterable);
        ImmutableList<Object> list;
        if (!(iterable instanceof Collection)) {
            list = copyOf((Iterator<?>)iterable.iterator());
        }
        else {
            list = copyOf((Collection<?>)iterable);
        }
        return (ImmutableList<E>)list;
    }
    
    public static <E> ImmutableList<E> copyOf(final Collection<? extends E> collection) {
        if (!(collection instanceof ImmutableCollection)) {
            return (ImmutableList<E>)construct(collection.toArray());
        }
        ImmutableList<Object> list = ((ImmutableCollection<Object>)collection).asList();
        if (list.isPartialView()) {
            list = asImmutableList(list.toArray());
        }
        return (ImmutableList<E>)list;
    }
    
    public static <E> ImmutableList<E> copyOf(final Iterator<? extends E> iterator) {
        if (!iterator.hasNext()) {
            return of();
        }
        final E next = iterator.next();
        if (iterator.hasNext()) {
            return new Builder<E>().add(next).addAll(iterator).build();
        }
        return of(next);
    }
    
    public static <E> ImmutableList<E> copyOf(final E[] array) {
        switch (array.length) {
            default: {
                return new RegularImmutableList<E>(ObjectArrays.checkElementsNotNull((Object[])array.clone()));
            }
            case 0: {
                return of();
            }
            case 1: {
                return new SingletonImmutableList<E>(array[0]);
            }
        }
    }
    
    public static <E> ImmutableList<E> of() {
        return (ImmutableList<E>)RegularImmutableList.EMPTY;
    }
    
    public static <E> ImmutableList<E> of(final E e) {
        return new SingletonImmutableList<E>(e);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2) {
        return construct(e, e2);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3) {
        return construct(e, e2, e3);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4) {
        return construct(e, e2, e3, e4);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5) {
        return construct(e, e2, e3, e4, e5);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6) {
        return construct(e, e2, e3, e4, e5, e6);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7) {
        return construct(e, e2, e3, e4, e5, e6, e7);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8) {
        return construct(e, e2, e3, e4, e5, e6, e7, e8);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9) {
        return construct(e, e2, e3, e4, e5, e6, e7, e8, e9);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9, final E e10) {
        return construct(e, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9, final E e10, final E e11) {
        return construct(e, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11);
    }
    
    public static <E> ImmutableList<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E e7, final E e8, final E e9, final E e10, final E e11, final E e12, final E... array) {
        final Object[] array2 = new Object[array.length + 12];
        array2[0] = e;
        array2[1] = e2;
        array2[2] = e3;
        array2[3] = e4;
        array2[4] = e5;
        array2[5] = e6;
        array2[6] = e7;
        array2[7] = e8;
        array2[8] = e9;
        array2[9] = e10;
        array2[10] = e11;
        array2[11] = e12;
        System.arraycopy(array, 0, array2, 12, array.length);
        return (ImmutableList<E>)construct(array2);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }
    
    @Deprecated
    @Override
    public final void add(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final boolean addAll(final int n, final Collection<? extends E> collection) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final ImmutableList<E> asList() {
        return this;
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        boolean b = false;
        if (this.indexOf(o) >= 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        final int size = this.size();
        for (int i = 0; i < size; ++i) {
            array[n + i] = this.get(i);
        }
        return n + size;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return Lists.equalsImpl(this, o);
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        for (int size = this.size(), i = 0; i < size; ++i) {
            n = ~(~(n * 31 + this.get(i).hashCode()));
        }
        return n;
    }
    
    @Override
    public int indexOf(@Nullable final Object o) {
        int indexOfImpl;
        if (o != null) {
            indexOfImpl = Lists.indexOfImpl(this, o);
        }
        else {
            indexOfImpl = -1;
        }
        return indexOfImpl;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.listIterator();
    }
    
    @Override
    public int lastIndexOf(@Nullable final Object o) {
        int lastIndexOfImpl;
        if (o != null) {
            lastIndexOfImpl = Lists.lastIndexOfImpl(this, o);
        }
        else {
            lastIndexOfImpl = -1;
        }
        return lastIndexOfImpl;
    }
    
    @Override
    public UnmodifiableListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public UnmodifiableListIterator<E> listIterator(final int n) {
        return new AbstractIndexedListIterator<E>(this.size(), n) {
            @Override
            protected E get(final int n) {
                return ImmutableList.this.get(n);
            }
        };
    }
    
    @Deprecated
    @Override
    public final E remove(final int n) {
        throw new UnsupportedOperationException();
    }
    
    public ImmutableList<E> reverse() {
        ImmutableList list = this;
        if (this.size() > 1) {
            list = new ReverseImmutableList<E>(this);
        }
        return list;
    }
    
    @Deprecated
    @Override
    public final E set(final int n, final E e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableList<E> subList(final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n2, this.size());
        final int n3 = n2 - n;
        if (n3 == this.size()) {
            return this;
        }
        switch (n3) {
            default: {
                return this.subListUnchecked(n, n2);
            }
            case 0: {
                return of();
            }
            case 1: {
                return of(this.get(n));
            }
        }
    }
    
    ImmutableList<E> subListUnchecked(final int n, final int n2) {
        return new SubList(n, n2 - n);
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    public static final class Builder<E> extends ArrayBasedBuilder<E>
    {
        public Builder() {
            this(4);
        }
        
        Builder(final int n) {
            super(n);
        }
        
        public Builder<E> add(final E e) {
            super.add(e);
            return this;
        }
        
        public Builder<E> add(final E... array) {
            super.add(array);
            return this;
        }
        
        public Builder<E> addAll(final Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }
        
        public Builder<E> addAll(final Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }
        
        public ImmutableList<E> build() {
            return ImmutableList.asImmutableList(this.contents, this.size);
        }
    }
    
    private static class ReverseImmutableList<E> extends ImmutableList<E>
    {
        private final transient ImmutableList<E> forwardList;
        
        ReverseImmutableList(final ImmutableList<E> forwardList) {
            this.forwardList = forwardList;
        }
        
        private int reverseIndex(final int n) {
            return this.size() - 1 - n;
        }
        
        private int reversePosition(final int n) {
            return this.size() - n;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return this.forwardList.contains(o);
        }
        
        @Override
        public E get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.forwardList.get(this.reverseIndex(n));
        }
        
        @Override
        public int indexOf(@Nullable final Object o) {
            final int lastIndex = this.forwardList.lastIndexOf(o);
            int reverseIndex;
            if (lastIndex < 0) {
                reverseIndex = -1;
            }
            else {
                reverseIndex = this.reverseIndex(lastIndex);
            }
            return reverseIndex;
        }
        
        @Override
        boolean isPartialView() {
            return this.forwardList.isPartialView();
        }
        
        @Override
        public int lastIndexOf(@Nullable final Object o) {
            final int index = this.forwardList.indexOf(o);
            int reverseIndex;
            if (index < 0) {
                reverseIndex = -1;
            }
            else {
                reverseIndex = this.reverseIndex(index);
            }
            return reverseIndex;
        }
        
        @Override
        public ImmutableList<E> reverse() {
            return this.forwardList;
        }
        
        @Override
        public int size() {
            return this.forwardList.size();
        }
        
        @Override
        public ImmutableList<E> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            return this.forwardList.subList(this.reversePosition(n2), this.reversePosition(n)).reverse();
        }
    }
    
    static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Object[] elements;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return ImmutableList.copyOf(this.elements);
        }
    }
    
    class SubList extends ImmutableList<E>
    {
        final transient int length;
        final transient int offset;
        
        SubList(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public E get(final int n) {
            Preconditions.checkElementIndex(n, this.length);
            return ImmutableList.this.get(this.offset + n);
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return this.length;
        }
        
        @Override
        public ImmutableList<E> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.length);
            return ImmutableList.this.subList(this.offset + n, this.offset + n2);
        }
    }
}
