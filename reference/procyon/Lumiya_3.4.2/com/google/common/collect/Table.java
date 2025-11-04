// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Table<R, C, V>
{
    Set<Cell<R, C, V>> cellSet();
    
    void clear();
    
    Map<R, V> column(final C p0);
    
    Set<C> columnKeySet();
    
    Map<C, Map<R, V>> columnMap();
    
    boolean contains(@Nullable final Object p0, @Nullable final Object p1);
    
    boolean containsColumn(@Nullable final Object p0);
    
    boolean containsRow(@Nullable final Object p0);
    
    boolean containsValue(@Nullable final Object p0);
    
    boolean equals(@Nullable final Object p0);
    
    V get(@Nullable final Object p0, @Nullable final Object p1);
    
    int hashCode();
    
    boolean isEmpty();
    
    @Nullable
    V put(final R p0, final C p1, final V p2);
    
    void putAll(final Table<? extends R, ? extends C, ? extends V> p0);
    
    @Nullable
    V remove(@Nullable final Object p0, @Nullable final Object p1);
    
    Map<C, V> row(final R p0);
    
    Set<R> rowKeySet();
    
    Map<R, Map<C, V>> rowMap();
    
    int size();
    
    Collection<V> values();
    
    public interface Cell<R, C, V>
    {
        boolean equals(@Nullable final Object p0);
        
        @Nullable
        C getColumnKey();
        
        @Nullable
        R getRowKey();
        
        @Nullable
        V getValue();
        
        int hashCode();
    }
}
