// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public final class HashMultimap<K, V> extends AbstractSetMultimap<K, V>
{
    private static final int DEFAULT_VALUES_PER_KEY = 2;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;
    @VisibleForTesting
    transient int expectedValuesPerKey;
    
    private HashMultimap() {
        super(new HashMap());
        this.expectedValuesPerKey = 2;
    }
    
    private HashMultimap(final int n, final int expectedValuesPerKey) {
        boolean b = false;
        super((Map<Object, Collection<Object>>)Maps.newHashMapWithExpectedSize(n));
        this.expectedValuesPerKey = 2;
        if (expectedValuesPerKey >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        this.expectedValuesPerKey = expectedValuesPerKey;
    }
    
    private HashMultimap(final Multimap<? extends K, ? extends V> multimap) {
        super((Map<Object, Collection<Object>>)Maps.newHashMapWithExpectedSize(multimap.keySet().size()));
        this.expectedValuesPerKey = 2;
        this.putAll(multimap);
    }
    
    public static <K, V> HashMultimap<K, V> create() {
        return new HashMultimap<K, V>();
    }
    
    public static <K, V> HashMultimap<K, V> create(final int n, final int n2) {
        return new HashMultimap<K, V>(n, n2);
    }
    
    public static <K, V> HashMultimap<K, V> create(final Multimap<? extends K, ? extends V> multimap) {
        return new HashMultimap<K, V>(multimap);
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.expectedValuesPerKey = 2;
        final int count = Serialization.readCount(objectInputStream);
        this.setMap((Map<K, Collection<V>>)Maps.newHashMap());
        Serialization.populateMultimap((Multimap<Object, Object>)this, objectInputStream, count);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMultimap((Multimap<Object, Object>)this, objectOutputStream);
    }
    
    @Override
    Set<V> createCollection() {
        return (Set<V>)Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
    }
}
