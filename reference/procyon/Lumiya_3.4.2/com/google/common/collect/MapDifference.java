// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface MapDifference<K, V>
{
    boolean areEqual();
    
    Map<K, ValueDifference<V>> entriesDiffering();
    
    Map<K, V> entriesInCommon();
    
    Map<K, V> entriesOnlyOnLeft();
    
    Map<K, V> entriesOnlyOnRight();
    
    boolean equals(@Nullable final Object p0);
    
    int hashCode();
    
    public interface ValueDifference<V>
    {
        boolean equals(@Nullable final Object p0);
        
        int hashCode();
        
        V leftValue();
        
        V rightValue();
    }
}
