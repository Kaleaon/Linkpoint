// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface SetMultimap<K, V> extends Multimap<K, V>
{
    Map<K, Collection<V>> asMap();
    
    Set<Map.Entry<K, V>> entries();
    
    boolean equals(@Nullable final Object p0);
    
    Set<V> get(@Nullable final K p0);
    
    Set<V> removeAll(@Nullable final Object p0);
    
    Set<V> replaceValues(final K p0, final Iterable<? extends V> p1);
}
