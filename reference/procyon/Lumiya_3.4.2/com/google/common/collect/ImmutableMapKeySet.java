// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.j2objc.annotations.Weak;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class ImmutableMapKeySet<K, V> extends Indexed<K>
{
    @Weak
    private final ImmutableMap<K, V> map;
    
    ImmutableMapKeySet(final ImmutableMap<K, V> map) {
        this.map = map;
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    K get(final int n) {
        return this.map.entrySet().asList().get(n).getKey();
    }
    
    @Override
    boolean isPartialView() {
        return true;
    }
    
    @Override
    public UnmodifiableIterator<K> iterator() {
        return this.map.keyIterator();
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new KeySetSerializedForm((ImmutableMap<Object, ?>)this.map);
    }
    
    @GwtIncompatible("serialization")
    private static class KeySetSerializedForm<K> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final ImmutableMap<K, ?> map;
        
        KeySetSerializedForm(final ImmutableMap<K, ?> map) {
            this.map = map;
        }
        
        Object readResolve() {
            return this.map.keySet();
        }
    }
}
