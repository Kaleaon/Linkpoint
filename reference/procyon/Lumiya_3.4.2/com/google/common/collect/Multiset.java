// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Collection;

@GwtCompatible
public interface Multiset<E> extends Collection<E>
{
    int add(@Nullable final E p0, final int p1);
    
    boolean add(final E p0);
    
    boolean contains(@Nullable final Object p0);
    
    boolean containsAll(final Collection<?> p0);
    
    int count(@Nullable final Object p0);
    
    Set<E> elementSet();
    
    Set<Entry<E>> entrySet();
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
    
    Iterator<E> iterator();
    
    int remove(@Nullable final Object p0, final int p1);
    
    boolean remove(@Nullable final Object p0);
    
    boolean removeAll(final Collection<?> p0);
    
    boolean retainAll(final Collection<?> p0);
    
    int setCount(final E p0, final int p1);
    
    boolean setCount(final E p0, final int p1, final int p2);
    
    String toString();
    
    public interface Entry<E>
    {
        boolean equals(final Object p0);
        
        int getCount();
        
        E getElement();
        
        int hashCode();
        
        String toString();
    }
}
