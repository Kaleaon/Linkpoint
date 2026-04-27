// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.List;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V> implements ListMultimap<K, V>
{
    protected ForwardingListMultimap() {
    }
    
    @Override
    protected abstract ListMultimap<K, V> delegate();
    
    @Override
    public List<V> get(@Nullable final K k) {
        return this.delegate().get(k);
    }
    
    @Override
    public List<V> removeAll(@Nullable final Object o) {
        return this.delegate().removeAll(o);
    }
    
    @Override
    public List<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k, iterable);
    }
}
