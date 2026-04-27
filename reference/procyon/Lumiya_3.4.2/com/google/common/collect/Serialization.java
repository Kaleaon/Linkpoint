// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

final class Serialization
{
    private Serialization() {
    }
    
    static <T> FieldSetter<T> getFieldSetter(final Class<T> clazz, final String name) {
        try {
            return new FieldSetter<T>(clazz.getDeclaredField(name));
        }
        catch (final NoSuchFieldException detailMessage) {
            throw new AssertionError((Object)detailMessage);
        }
    }
    
    static <K, V> void populateMap(final Map<K, V> map, final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        populateMap(map, objectInputStream, objectInputStream.readInt());
    }
    
    static <K, V> void populateMap(final Map<K, V> map, final ObjectInputStream objectInputStream, final int n) throws IOException, ClassNotFoundException {
        for (int i = 0; i < n; ++i) {
            map.put((K)objectInputStream.readObject(), (V)objectInputStream.readObject());
        }
    }
    
    static <K, V> void populateMultimap(final Multimap<K, V> multimap, final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        populateMultimap(multimap, objectInputStream, objectInputStream.readInt());
    }
    
    static <K, V> void populateMultimap(final Multimap<K, V> multimap, final ObjectInputStream objectInputStream, final int n) throws IOException, ClassNotFoundException {
        for (int i = 0; i < n; ++i) {
            final Collection<V> value = multimap.get((K)objectInputStream.readObject());
            for (int int1 = objectInputStream.readInt(), j = 0; j < int1; ++j) {
                value.add((V)objectInputStream.readObject());
            }
        }
    }
    
    static <E> void populateMultiset(final Multiset<E> multiset, final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        populateMultiset(multiset, objectInputStream, objectInputStream.readInt());
    }
    
    static <E> void populateMultiset(final Multiset<E> multiset, final ObjectInputStream objectInputStream, final int n) throws IOException, ClassNotFoundException {
        for (int i = 0; i < n; ++i) {
            multiset.add((E)objectInputStream.readObject(), objectInputStream.readInt());
        }
    }
    
    static int readCount(final ObjectInputStream objectInputStream) throws IOException {
        return objectInputStream.readInt();
    }
    
    static <K, V> void writeMap(final Map<K, V> map, final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(map.size());
        for (final Map.Entry<Object, V> entry : map.entrySet()) {
            objectOutputStream.writeObject(entry.getKey());
            objectOutputStream.writeObject(entry.getValue());
        }
    }
    
    static <K, V> void writeMultimap(final Multimap<K, V> multimap, final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(multimap.asMap().size());
        for (final Map.Entry<Object, V> entry : multimap.asMap().entrySet()) {
            objectOutputStream.writeObject(entry.getKey());
            objectOutputStream.writeInt(((Collection)entry.getValue()).size());
            final Iterator iterator2 = ((Collection)entry.getValue()).iterator();
            while (iterator2.hasNext()) {
                objectOutputStream.writeObject(iterator2.next());
            }
        }
    }
    
    static <E> void writeMultiset(final Multiset<E> multiset, final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeInt(multiset.entrySet().size());
        for (final Multiset.Entry<Object> entry : multiset.entrySet()) {
            objectOutputStream.writeObject(entry.getElement());
            objectOutputStream.writeInt(entry.getCount());
        }
    }
    
    static final class FieldSetter<T>
    {
        private final Field field;
        
        private FieldSetter(final Field field) {
            (this.field = field).setAccessible(true);
        }
        
        void set(final T obj, final int i) {
            try {
                this.field.set(obj, i);
            }
            catch (final IllegalAccessException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
        
        void set(final T obj, final Object value) {
            try {
                this.field.set(obj, value);
            }
            catch (final IllegalAccessException detailMessage) {
                throw new AssertionError((Object)detailMessage);
            }
        }
    }
}
