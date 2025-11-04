// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Multimap<K, V>
{
    Map<K, Collection<V>> asMap();
    
    void clear();
    
    boolean containsEntry(@Nullable final Object p0, @Nullable final Object p1);
    
    boolean containsKey(@Nullable final Object p0);
    
    boolean containsValue(@Nullable final Object p0);
    
    Collection<Map.Entry<K, V>> entries();
    
    boolean equals(@Nullable final Object p0);
    
    Collection<V> get(@Nullable final K p0);
    
    int hashCode();
    
    boolean isEmpty();
    
    Set<K> keySet();
    
    Multiset<K> keys();
    
    boolean put(@Nullable final K p0, @Nullable final V p1);
    
    boolean putAll(final Multimap<? extends K, ? extends V> p0);
    
    boolean putAll(@Nullable final K p0, final Iterable<? extends V> p1);
    
    boolean remove(@Nullable final Object p0, @Nullable final Object p1);
    
    Collection<V> removeAll(@Nullable final Object p0);
    
    Collection<V> replaceValues(@Nullable final K p0, final Iterable<? extends V> p1);
    
    int size();
    
    Collection<V> values();
}
