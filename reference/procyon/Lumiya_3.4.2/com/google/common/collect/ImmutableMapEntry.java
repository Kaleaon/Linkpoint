// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import com.google.common.annotations.GwtIncompatible;

@GwtIncompatible("unnecessary")
class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V>
{
    ImmutableMapEntry(final ImmutableMapEntry<K, V> immutableMapEntry) {
        super(immutableMapEntry.getKey(), immutableMapEntry.getValue());
    }
    
    ImmutableMapEntry(final K k, final V v) {
        super(k, v);
        CollectPreconditions.checkEntryNotNull(k, v);
    }
    
    static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(final int n) {
        return new ImmutableMapEntry[n];
    }
    
    @Nullable
    ImmutableMapEntry<K, V> getNextInKeyBucket() {
        return null;
    }
    
    @Nullable
    ImmutableMapEntry<K, V> getNextInValueBucket() {
        return null;
    }
    
    boolean isReusable() {
        return true;
    }
    
    static final class NonTerminalImmutableBiMapEntry<K, V> extends NonTerminalImmutableMapEntry<K, V>
    {
        private final transient ImmutableMapEntry<K, V> nextInValueBucket;
        
        NonTerminalImmutableBiMapEntry(final K k, final V v, final ImmutableMapEntry<K, V> immutableMapEntry, final ImmutableMapEntry<K, V> nextInValueBucket) {
            super(k, v, immutableMapEntry);
            this.nextInValueBucket = nextInValueBucket;
        }
        
        @Nullable
        @Override
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return this.nextInValueBucket;
        }
    }
    
    static class NonTerminalImmutableMapEntry<K, V> extends ImmutableMapEntry<K, V>
    {
        private final transient ImmutableMapEntry<K, V> nextInKeyBucket;
        
        NonTerminalImmutableMapEntry(final K k, final V v, final ImmutableMapEntry<K, V> nextInKeyBucket) {
            super(k, v);
            this.nextInKeyBucket = nextInKeyBucket;
        }
        
        @Nullable
        @Override
        final ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return this.nextInKeyBucket;
        }
        
        @Override
        final boolean isReusable() {
            return false;
        }
    }
}
