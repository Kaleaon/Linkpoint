// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.base.Preconditions;
import java.util.EnumMap;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class ImmutableEnumMap<K extends Enum<K>, V> extends IteratorBasedImmutableMap<K, V>
{
    private final transient EnumMap<K, V> delegate;
    
    private ImmutableEnumMap(final EnumMap<K, V> delegate) {
        boolean b = false;
        this.delegate = delegate;
        if (!delegate.isEmpty()) {
            b = true;
        }
        Preconditions.checkArgument(b);
    }
    
    static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(final EnumMap<K, V> enumMap) {
        switch (enumMap.size()) {
            default: {
                return new ImmutableEnumMap<K, V>(enumMap);
            }
            case 0: {
                return ImmutableMap.of();
            }
            case 1: {
                final Entry entry = (Entry)Iterables.getOnlyElement(enumMap.entrySet());
                return ImmutableMap.of((K)entry.getKey(), (V)entry.getValue());
            }
        }
    }
    
    @Override
    public boolean containsKey(@Nullable final Object key) {
        return this.delegate.containsKey(key);
    }
    
    @Override
    UnmodifiableIterator<Entry<K, V>> entryIterator() {
        return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
    }
    
    @Override
    public boolean equals(Object delegate) {
        if (delegate != this) {
            if (delegate instanceof ImmutableEnumMap) {
                delegate = ((ImmutableEnumMap)delegate).delegate;
            }
            return this.delegate.equals(delegate);
        }
        return true;
    }
    
    @Override
    public V get(final Object key) {
        return this.delegate.get(key);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    UnmodifiableIterator<K> keyIterator() {
        return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    Object writeReplace() {
        return new EnumSerializedForm((EnumMap<Enum, Object>)this.delegate);
    }
    
    private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final EnumMap<K, V> delegate;
        
        EnumSerializedForm(final EnumMap<K, V> delegate) {
            this.delegate = delegate;
        }
        
        Object readResolve() {
            return new ImmutableEnumMap(this.delegate, null);
        }
    }
}
