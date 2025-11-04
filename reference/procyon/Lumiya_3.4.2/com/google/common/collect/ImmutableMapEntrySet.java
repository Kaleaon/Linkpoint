// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.j2objc.annotations.Weak;
import java.io.Serializable;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible(emulated = true)
abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>>
{
    @Override
    public boolean contains(@Nullable Object value) {
        boolean b = false;
        if (!(value instanceof Map.Entry)) {
            return false;
        }
        final Map.Entry entry = (Map.Entry)value;
        value = this.map().get(entry.getKey());
        if (value != null && value.equals(entry.getValue())) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.map().hashCode();
    }
    
    @GwtIncompatible("not used in GWT")
    @Override
    boolean isHashCodeFast() {
        return this.map().isHashCodeFast();
    }
    
    @Override
    boolean isPartialView() {
        return this.map().isPartialView();
    }
    
    abstract ImmutableMap<K, V> map();
    
    @Override
    public int size() {
        return this.map().size();
    }
    
    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new EntrySetSerializedForm(this.map());
    }
    
    @GwtIncompatible("serialization")
    private static class EntrySetSerializedForm<K, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final ImmutableMap<K, V> map;
        
        EntrySetSerializedForm(final ImmutableMap<K, V> map) {
            this.map = map;
        }
        
        Object readResolve() {
            return this.map.entrySet();
        }
    }
    
    static final class RegularEntrySet<K, V> extends ImmutableMapEntrySet<K, V>
    {
        private final transient Map.Entry<K, V>[] entries;
        @Weak
        private final transient ImmutableMap<K, V> map;
        
        RegularEntrySet(final ImmutableMap<K, V> map, final Map.Entry<K, V>[] entries) {
            this.map = map;
            this.entries = entries;
        }
        
        @Override
        ImmutableList<Map.Entry<K, V>> createAsList() {
            return new RegularImmutableAsList<Map.Entry<K, V>>((ImmutableCollection<Map.Entry<K, V>>)this, this.entries);
        }
        
        @Override
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
            return (UnmodifiableIterator<Map.Entry<K, V>>)this.asList().iterator();
        }
        
        @Override
        ImmutableMap<K, V> map() {
            return this.map;
        }
    }
}
