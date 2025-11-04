// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public class ImmutableListMultimap<K, V> extends ImmutableMultimap<K, V> implements ListMultimap<K, V>
{
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;
    private transient ImmutableListMultimap<V, K> inverse;
    
    ImmutableListMultimap(final ImmutableMap<K, ImmutableList<V>> immutableMap, final int n) {
        super((ImmutableMap<K, ? extends ImmutableCollection<Object>>)immutableMap, n);
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    public static <K, V> ImmutableListMultimap<K, V> copyOf(final Multimap<? extends K, ? extends V> multimap) {
        if (!multimap.isEmpty()) {
            if (multimap instanceof ImmutableListMultimap) {
                final ImmutableListMultimap immutableListMultimap = (ImmutableListMultimap)multimap;
                if (!immutableListMultimap.isPartialView()) {
                    return immutableListMultimap;
                }
            }
            final ImmutableMap.Builder<Object, ImmutableList<Object>> builder = (ImmutableMap.Builder<Object, ImmutableList<Object>>)new ImmutableMap.Builder<K, ImmutableList>(multimap.asMap().size());
            final Iterator iterator = multimap.asMap().entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator.next();
                final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)entry.getValue());
                if (copy.isEmpty()) {
                    continue;
                }
                builder.put(entry.getKey(), copy);
                n += copy.size();
            }
            return new ImmutableListMultimap<K, V>((ImmutableMap<Object, ImmutableList<Object>>)builder.build(), n);
        }
        return of();
    }
    
    @Beta
    public static <K, V> ImmutableListMultimap<K, V> copyOf(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
        return new Builder<K, V>().putAll(iterable).build();
    }
    
    private ImmutableListMultimap<V, K> invert() {
        final Builder<Object, Object> builder = builder();
        for (final Map.Entry<K, Object> entry : this.entries()) {
            builder.put(entry.getValue(), entry.getKey());
        }
        final ImmutableListMultimap<Object, Object> build = builder.build();
        build.inverse = (ImmutableListMultimap<Object, Object>)this;
        return (ImmutableListMultimap<V, K>)build;
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of() {
        return (ImmutableListMultimap<K, V>)EmptyImmutableListMultimap.INSTANCE;
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of(final K k, final V v) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        return builder.build();
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of(final K k, final V v, final K i, final V v2) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        return builder.build();
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        builder.put(j, v3);
        return builder.build();
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        builder.put(j, v3);
        builder.put(l, v4);
        return builder.build();
    }
    
    public static <K, V> ImmutableListMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        builder.put(j, v3);
        builder.put(l, v4);
        builder.put(m, v5);
        return builder.build();
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int int1 = objectInputStream.readInt();
        Label_0052: {
            if (int1 < 0) {
                break Label_0052;
            }
            final ImmutableMap.Builder<Object, ImmutableList<Object>> builder = ImmutableMap.builder();
            int n = 0;
            int n2 = 0;
            Object object;
            ImmutableList.Builder<Object> builder2;
            int int2;
            int n3;
            Block_5_Outer:Block_4_Outer:Label_0104_Outer:
            while (true) {
                Label_0079: {
                    if (n < int1) {
                        break Label_0079;
                    }
                    try {
                        FieldSettersHolder.MAP_FIELD_SETTER.set(this, builder.build());
                        FieldSettersHolder.SIZE_FIELD_SETTER.set(this, n2);
                        return;
                        throw new InvalidObjectException("Invalid key count " + int1);
                        while (true) {
                            while (true) {
                                while (true) {
                                    builder.put(object, builder2.build());
                                    n2 += int2;
                                    ++n;
                                    continue Block_5_Outer;
                                    builder2 = ImmutableList.builder();
                                    n3 = 0;
                                    iftrue(Label_0164:)(n3 < int2);
                                    continue Block_4_Outer;
                                }
                                object = objectInputStream.readObject();
                                int2 = objectInputStream.readInt();
                                iftrue(Label_0136:)(int2 <= 0);
                                continue Label_0104_Outer;
                            }
                            Label_0136: {
                                throw new InvalidObjectException("Invalid value count " + int2);
                            }
                            Label_0164:
                            builder2.add(objectInputStream.readObject());
                            ++n3;
                            continue;
                        }
                    }
                    catch (final IllegalArgumentException cause) {
                        throw (InvalidObjectException)new InvalidObjectException(cause.getMessage()).initCause(cause);
                    }
                }
                break;
            }
        }
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMultimap((Multimap<Object, Object>)this, objectOutputStream);
    }
    
    @Override
    public ImmutableList<V> get(@Nullable final K k) {
        ImmutableList<Object> of = (ImmutableList<Object>)this.map.get(k);
        if (of == null) {
            of = ImmutableList.of();
        }
        return (ImmutableList<V>)of;
    }
    
    @Override
    public ImmutableListMultimap<V, K> inverse() {
        ImmutableListMultimap<V, K> inverse = this.inverse;
        if (inverse == null) {
            inverse = this.invert();
            this.inverse = inverse;
        }
        return inverse;
    }
    
    @Deprecated
    @Override
    public ImmutableList<V> removeAll(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public ImmutableList<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }
    
    public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V>
    {
        public ImmutableListMultimap<K, V> build() {
            return (ImmutableListMultimap)super.build();
        }
        
        public Builder<K, V> orderKeysBy(final Comparator<? super K> comparator) {
            super.orderKeysBy(comparator);
            return this;
        }
        
        public Builder<K, V> orderValuesBy(final Comparator<? super V> comparator) {
            super.orderValuesBy(comparator);
            return this;
        }
        
        public Builder<K, V> put(final K k, final V v) {
            super.put(k, v);
            return this;
        }
        
        public Builder<K, V> put(final Map.Entry<? extends K, ? extends V> entry) {
            super.put(entry);
            return this;
        }
        
        public Builder<K, V> putAll(final Multimap<? extends K, ? extends V> multimap) {
            super.putAll(multimap);
            return this;
        }
        
        @Beta
        public Builder<K, V> putAll(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
            super.putAll(iterable);
            return this;
        }
        
        public Builder<K, V> putAll(final K k, final Iterable<? extends V> iterable) {
            super.putAll(k, iterable);
            return this;
        }
        
        public Builder<K, V> putAll(final K k, final V... array) {
            super.putAll(k, array);
            return this;
        }
    }
}
