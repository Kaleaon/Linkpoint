// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import java.util.NavigableSet;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public interface SortedMultiset<E> extends SortedMultisetBridge<E>, SortedIterable<E>
{
    Comparator<? super E> comparator();
    
    SortedMultiset<E> descendingMultiset();
    
    NavigableSet<E> elementSet();
    
    Set<Entry<E>> entrySet();
    
    Entry<E> firstEntry();
    
    SortedMultiset<E> headMultiset(final E p0, final BoundType p1);
    
    Iterator<E> iterator();
    
    Entry<E> lastEntry();
    
    Entry<E> pollFirstEntry();
    
    Entry<E> pollLastEntry();
    
    SortedMultiset<E> subMultiset(final E p0, final BoundType p1, final E p2, final BoundType p3);
    
    SortedMultiset<E> tailMultiset(final E p0, final BoundType p1);
}
