// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SortedSetMultimap<K, V> extends SetMultimap<K, V>
{
    Map<K, Collection<V>> asMap();
    
    SortedSet<V> get(@Nullable final K p0);
    
    SortedSet<V> removeAll(@Nullable final Object p0);
    
    SortedSet<V> replaceValues(final K p0, final Iterable<? extends V> p1);
    
    Comparator<? super V> valueComparator();
}
