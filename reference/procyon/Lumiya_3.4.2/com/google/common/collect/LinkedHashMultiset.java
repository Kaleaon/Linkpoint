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
import java.util.LinkedHashMap;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public final class LinkedHashMultiset<E> extends AbstractMapBasedMultiset<E>
{
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    
    private LinkedHashMultiset() {
        super(new LinkedHashMap());
    }
    
    private LinkedHashMultiset(final int n) {
        super((Map<Object, Count>)Maps.newLinkedHashMapWithExpectedSize(n));
    }
    
    public static <E> LinkedHashMultiset<E> create() {
        return new LinkedHashMultiset<E>();
    }
    
    public static <E> LinkedHashMultiset<E> create(final int n) {
        return new LinkedHashMultiset<E>(n);
    }
    
    public static <E> LinkedHashMultiset<E> create(final Iterable<? extends E> iterable) {
        final LinkedHashMultiset<Object> create = create(Multisets.inferDistinctElements(iterable));
        Iterables.addAll(create, iterable);
        return (LinkedHashMultiset<E>)create;
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int count = Serialization.readCount(objectInputStream);
        this.setBackingMap(new LinkedHashMap<E, Count>());
        Serialization.populateMultiset((Multiset<Object>)this, objectInputStream, count);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMultiset((Multiset<Object>)this, objectOutputStream);
    }
}
