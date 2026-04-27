// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public final class HashMultiset<E> extends AbstractMapBasedMultiset<E>
{
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    
    private HashMultiset() {
        super(new HashMap());
    }
    
    private HashMultiset(final int n) {
        super((Map<Object, Count>)Maps.newHashMapWithExpectedSize(n));
    }
    
    public static <E> HashMultiset<E> create() {
        return new HashMultiset<E>();
    }
    
    public static <E> HashMultiset<E> create(final int n) {
        return new HashMultiset<E>(n);
    }
    
    public static <E> HashMultiset<E> create(final Iterable<? extends E> iterable) {
        final HashMultiset<Object> create = create(Multisets.inferDistinctElements(iterable));
        Iterables.addAll(create, iterable);
        return (HashMultiset<E>)create;
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int count = Serialization.readCount(objectInputStream);
        this.setBackingMap((Map<E, Count>)Maps.newHashMap());
        Serialization.populateMultiset((Multiset<Object>)this, objectInputStream, count);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMultiset((Multiset<Object>)this, objectOutputStream);
    }
}
