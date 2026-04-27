// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V>
{
    @Deprecated
    public static <K, V> ImmutableSortedMap.Builder<K, V> builder() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static <K, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        throw new UnsupportedOperationException();
    }
}
