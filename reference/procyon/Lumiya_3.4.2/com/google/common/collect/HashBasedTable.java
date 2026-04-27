// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.HashMap;
import com.google.common.base.Supplier;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
public class HashBasedTable<R, C, V> extends StandardTable<R, C, V>
{
    private static final long serialVersionUID = 0L;
    
    HashBasedTable(final Map<R, Map<C, V>> map, final Factory<C, V> factory) {
        super(map, factory);
    }
    
    public static <R, C, V> HashBasedTable<R, C, V> create() {
        return new HashBasedTable<R, C, V>(new HashMap<R, Map<C, V>>(), new Factory<C, V>(0));
    }
    
    public static <R, C, V> HashBasedTable<R, C, V> create(final int n, final int n2) {
        CollectPreconditions.checkNonnegative(n2, "expectedCellsPerRow");
        return new HashBasedTable<R, C, V>((Map<R, Map<C, V>>)Maps.newHashMapWithExpectedSize(n), new Factory<C, V>(n2));
    }
    
    public static <R, C, V> HashBasedTable<R, C, V> create(final Table<? extends R, ? extends C, ? extends V> table) {
        final HashBasedTable<Object, Object, Object> create = create();
        create.putAll(table);
        return (HashBasedTable<R, C, V>)create;
    }
    
    @Override
    public boolean contains(@Nullable final Object o, @Nullable final Object o2) {
        return super.contains(o, o2);
    }
    
    @Override
    public boolean containsColumn(@Nullable final Object o) {
        return super.containsColumn(o);
    }
    
    @Override
    public boolean containsRow(@Nullable final Object o) {
        return super.containsRow(o);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return super.containsValue(o);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return super.equals(o);
    }
    
    @Override
    public V get(@Nullable final Object o, @Nullable final Object o2) {
        return super.get(o, o2);
    }
    
    @Override
    public V remove(@Nullable final Object o, @Nullable final Object o2) {
        return super.remove(o, o2);
    }
    
    private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final int expectedSize;
        
        Factory(final int expectedSize) {
            this.expectedSize = expectedSize;
        }
        
        @Override
        public Map<C, V> get() {
            return (Map<C, V>)Maps.newHashMapWithExpectedSize(this.expectedSize);
        }
    }
}
