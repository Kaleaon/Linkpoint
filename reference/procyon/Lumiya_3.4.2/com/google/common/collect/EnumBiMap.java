// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.base.Preconditions;
import java.util.Map;
import java.util.EnumMap;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class EnumBiMap<K extends Enum<K>, V extends Enum<V>> extends AbstractBiMap<K, V>
{
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    private transient Class<K> keyType;
    private transient Class<V> valueType;
    
    private EnumBiMap(final Class<K> clazz, final Class<V> clazz2) {
        super(WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)clazz)), WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)clazz2)));
        this.keyType = clazz;
        this.valueType = clazz2;
    }
    
    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(final Class<K> clazz, final Class<V> clazz2) {
        return new EnumBiMap<K, V>(clazz, clazz2);
    }
    
    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(final Map<K, V> map) {
        final EnumBiMap<Enum, Enum> create = create((Class<Enum>)inferKeyType((Map<K, ?>)map), (Class<Enum>)inferValueType((Map<?, V>)map));
        create.putAll(map);
        return (EnumBiMap<K, V>)create;
    }
    
    static <K extends Enum<K>> Class<K> inferKeyType(final Map<K, ?> map) {
        boolean b = false;
        if (map instanceof EnumBiMap) {
            return ((EnumBiMap)map).keyType();
        }
        if (!(map instanceof EnumHashBiMap)) {
            if (!map.isEmpty()) {
                b = true;
            }
            Preconditions.checkArgument(b);
            return map.keySet().iterator().next().getDeclaringClass();
        }
        return ((EnumHashBiMap)map).keyType();
    }
    
    private static <V extends Enum<V>> Class<V> inferValueType(final Map<?, V> map) {
        boolean b = false;
        if (!(map instanceof EnumBiMap)) {
            if (!map.isEmpty()) {
                b = true;
            }
            Preconditions.checkArgument(b);
            return ((V)map.values().iterator().next()).getDeclaringClass();
        }
        return (Class<V>)((EnumBiMap)map).valueType;
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.keyType = (Class)objectInputStream.readObject();
        this.valueType = (Class)objectInputStream.readObject();
        this.setDelegates((Map<K, V>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)this.keyType)), (Map<V, K>)WellBehavedMap.wrap(new EnumMap<Object, Object>((Class<Object>)this.valueType)));
        Serialization.populateMap((Map<Object, Object>)this, objectInputStream);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.keyType);
        objectOutputStream.writeObject(this.valueType);
        Serialization.writeMap((Map<Object, Object>)this, objectOutputStream);
    }
    
    @Override
    K checkKey(final K k) {
        return Preconditions.checkNotNull(k);
    }
    
    @Override
    V checkValue(final V v) {
        return Preconditions.checkNotNull(v);
    }
    
    public Class<K> keyType() {
        return this.keyType;
    }
    
    public Class<V> valueType() {
        return this.valueType;
    }
}
