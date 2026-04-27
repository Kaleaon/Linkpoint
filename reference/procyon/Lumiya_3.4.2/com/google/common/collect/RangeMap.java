// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.annotations.Beta;

@Beta
public interface RangeMap<K extends Comparable, V>
{
    Map<Range<K>, V> asDescendingMapOfRanges();
    
    Map<Range<K>, V> asMapOfRanges();
    
    void clear();
    
    boolean equals(@Nullable final Object p0);
    
    @Nullable
    V get(final K p0);
    
    @Nullable
    Map.Entry<Range<K>, V> getEntry(final K p0);
    
    int hashCode();
    
    void put(final Range<K> p0, final V p1);
    
    void putAll(final RangeMap<K, V> p0);
    
    void remove(final Range<K> p0);
    
    Range<K> span();
    
    RangeMap<K, V> subRangeMap(final Range<K> p0);
    
    String toString();
}
