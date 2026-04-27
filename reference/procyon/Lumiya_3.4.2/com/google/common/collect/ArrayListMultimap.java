// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public final class ArrayListMultimap<K, V> extends AbstractListMultimap<K, V>
{
    private static final int DEFAULT_VALUES_PER_KEY = 3;
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    @VisibleForTesting
    transient int expectedValuesPerKey;
    
    private ArrayListMultimap() {
        super(new HashMap());
        this.expectedValuesPerKey = 3;
    }
    
    private ArrayListMultimap(final int n, final int expectedValuesPerKey) {
        super((Map<Object, Collection<Object>>)Maps.newHashMapWithExpectedSize(n));
        CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
        this.expectedValuesPerKey = expectedValuesPerKey;
    }
    
    private ArrayListMultimap(final Multimap<? extends K, ? extends V> multimap) {
        final int size = multimap.keySet().size();
        int expectedValuesPerKey;
        if (!(multimap instanceof ArrayListMultimap)) {
            expectedValuesPerKey = 3;
        }
        else {
            expectedValuesPerKey = ((ArrayListMultimap)multimap).expectedValuesPerKey;
        }
        this(size, expectedValuesPerKey);
        this.putAll(multimap);
    }
    
    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<K, V>();
    }
    
    public static <K, V> ArrayListMultimap<K, V> create(final int n, final int n2) {
        return new ArrayListMultimap<K, V>(n, n2);
    }
    
    public static <K, V> ArrayListMultimap<K, V> create(final Multimap<? extends K, ? extends V> multimap) {
        return new ArrayListMultimap<K, V>(multimap);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.expectedValuesPerKey = 3;
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
    List<V> createCollection() {
        return new ArrayList<V>(this.expectedValuesPerKey);
    }
    
    public void trimToSize() {
        final Iterator<Collection<V>> iterator = (Iterator<Collection<V>>)this.backingMap().values().iterator();
        while (iterator.hasNext()) {
            ((ArrayList)iterator.next()).trimToSize();
        }
    }
}
