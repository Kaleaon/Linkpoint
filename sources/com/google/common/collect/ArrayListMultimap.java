package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@GwtCompatible(emulated = true, serializable = true)
public final class ArrayListMultimap<K, V> extends AbstractListMultimap<K, V> {
    private static final int DEFAULT_VALUES_PER_KEY = 3;
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0;
    @VisibleForTesting
    transient int expectedValuesPerKey;

    private ArrayListMultimap() {
        super(new HashMap());
        this.expectedValuesPerKey = 3;
    }

    private ArrayListMultimap(int i, int i2) {
        super(Maps.newHashMapWithExpectedSize(i));
        CollectPreconditions.checkNonnegative(i2, "expectedValuesPerKey");
        this.expectedValuesPerKey = i2;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this(multimap.keySet().size(), !(multimap instanceof ArrayListMultimap) ? 3 : ((ArrayListMultimap) multimap).expectedValuesPerKey);
        putAll(multimap);
    }

    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<>();
    }

    public static <K, V> ArrayListMultimap<K, V> create(int i, int i2) {
        return new ArrayListMultimap<>(i, i2);
    }

    public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new ArrayListMultimap<>(multimap);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.expectedValuesPerKey = 3;
        int readCount = Serialization.readCount(objectInputStream);
        setMap(Maps.newHashMap());
        Serialization.populateMultimap(this, objectInputStream, readCount);
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMultimap(this, objectOutputStream);
    }

    public /* bridge */ /* synthetic */ Map asMap() {
        return super.asMap();
    }

    public /* bridge */ /* synthetic */ void clear() {
        super.clear();
    }

    public /* bridge */ /* synthetic */ boolean containsEntry(Object obj, Object obj2) {
        return super.containsEntry(obj, obj2);
    }

    public /* bridge */ /* synthetic */ boolean containsKey(Object obj) {
        return super.containsKey(obj);
    }

    public /* bridge */ /* synthetic */ boolean containsValue(Object obj) {
        return super.containsValue(obj);
    }

    /* access modifiers changed from: package-private */
    public List<V> createCollection() {
        return new ArrayList(this.expectedValuesPerKey);
    }

    public /* bridge */ /* synthetic */ Collection entries() {
        return super.entries();
    }

    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    public /* bridge */ /* synthetic */ List get(Object obj) {
        return super.get(obj);
    }

    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    public /* bridge */ /* synthetic */ boolean isEmpty() {
        return super.isEmpty();
    }

    public /* bridge */ /* synthetic */ Set keySet() {
        return super.keySet();
    }

    public /* bridge */ /* synthetic */ Multiset keys() {
        return super.keys();
    }

    public /* bridge */ /* synthetic */ boolean put(Object obj, Object obj2) {
        return super.put(obj, obj2);
    }

    public /* bridge */ /* synthetic */ boolean putAll(Multimap multimap) {
        return super.putAll(multimap);
    }

    public /* bridge */ /* synthetic */ boolean putAll(Object obj, Iterable iterable) {
        return super.putAll(obj, iterable);
    }

    public /* bridge */ /* synthetic */ boolean remove(Object obj, Object obj2) {
        return super.remove(obj, obj2);
    }

    public /* bridge */ /* synthetic */ List removeAll(Object obj) {
        return super.removeAll(obj);
    }

    public /* bridge */ /* synthetic */ List replaceValues(Object obj, Iterable iterable) {
        return super.replaceValues(obj, iterable);
    }

    public /* bridge */ /* synthetic */ int size() {
        return super.size();
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    public void trimToSize() {
        for (Collection collection : backingMap().values()) {
            ((ArrayList) collection).trimToSize();
        }
    }

    public /* bridge */ /* synthetic */ Collection values() {
        return super.values();
    }
}
