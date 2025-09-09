package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Multiset<E> extends Collection<E> {

    public interface Entry<E> {
        boolean equals(Object obj);

        int getCount();

        E getElement();

        int hashCode();

        String toString();
    }

    int add(@Nullable E e, int i);

    boolean add(E e);

    boolean contains(@Nullable Object obj);

    boolean containsAll(Collection<?> collection);

    int count(@Nullable Object obj);

    Set<E> elementSet();

    Set<Entry<E>> entrySet();

    boolean equals(@Nullable Object obj);

    int hashCode();

    Iterator<E> iterator();

    int remove(@Nullable Object obj, int i);

    boolean remove(@Nullable Object obj);

    boolean removeAll(Collection<?> collection);

    boolean retainAll(Collection<?> collection);

    int setCount(E e, int i);

    boolean setCount(E e, int i, int i2);

    String toString();
}
