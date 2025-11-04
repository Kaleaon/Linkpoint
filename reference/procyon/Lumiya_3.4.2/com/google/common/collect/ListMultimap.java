// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.List;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface ListMultimap<K, V> extends Multimap<K, V>
{
    Map<K, Collection<V>> asMap();
    
    boolean equals(@Nullable final Object p0);
    
    List<V> get(@Nullable final K p0);
    
    List<V> removeAll(@Nullable final Object p0);
    
    List<V> replaceValues(final K p0, final Iterable<? extends V> p1);
}
