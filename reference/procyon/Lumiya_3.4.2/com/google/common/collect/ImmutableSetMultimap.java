// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.j2objc.annotations.Weak;
import java.util.Arrays;
import com.google.common.base.MoreObjects;
import java.util.Set;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V> implements SetMultimap<K, V>
{
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    private final transient ImmutableSet<V> emptySet;
    private transient ImmutableSet<Map.Entry<K, V>> entries;
    private transient ImmutableSetMultimap<V, K> inverse;
    
    ImmutableSetMultimap(final ImmutableMap<K, ImmutableSet<V>> immutableMap, final int n, @Nullable final Comparator<? super V> comparator) {
        super((ImmutableMap<K, ? extends ImmutableCollection<Object>>)immutableMap, n);
        this.emptySet = emptySet(comparator);
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> copyOf(final Multimap<? extends K, ? extends V> multimap) {
        return copyOf(multimap, (Comparator<? super V>)null);
    }
    
    private static <K, V> ImmutableSetMultimap<K, V> copyOf(final Multimap<? extends K, ? extends V> multimap, final Comparator<? super V> comparator) {
        int n = 0;
        Preconditions.checkNotNull(multimap);
        if (multimap.isEmpty() && comparator == null) {
            return of();
        }
        if (multimap instanceof ImmutableSetMultimap) {
            final ImmutableSetMultimap immutableSetMultimap = (ImmutableSetMultimap)multimap;
            if (!immutableSetMultimap.isPartialView()) {
                return immutableSetMultimap;
            }
        }
        final ImmutableMap.Builder<K, ImmutableSet<V>> builder = new ImmutableMap.Builder<K, ImmutableSet<V>>(multimap.asMap().size());
        for (final Map.Entry<Object, V> entry : multimap.asMap().entrySet()) {
            final Object key = entry.getKey();
            final ImmutableSet<Object> valueSet = valueSet((Comparator<? super Object>)comparator, (Collection<?>)entry.getValue());
            if (valueSet.isEmpty()) {
                continue;
            }
            builder.put((K)key, valueSet);
            n += valueSet.size();
        }
        return new ImmutableSetMultimap<K, V>((ImmutableMap<Object, ImmutableSet<Object>>)builder.build(), n, (Comparator<? super Object>)comparator);
    }
    
    @Beta
    public static <K, V> ImmutableSetMultimap<K, V> copyOf(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
        return new Builder<K, V>().putAll(iterable).build();
    }
    
    private static <V> ImmutableSet<V> emptySet(@Nullable final Comparator<? super V> comparator) {
        ImmutableSet<Object> set;
        if (comparator != null) {
            set = ImmutableSortedSet.emptySet((Comparator<? super Object>)comparator);
        }
        else {
            set = ImmutableSet.of();
        }
        return (ImmutableSet<V>)set;
    }
    
    private ImmutableSetMultimap<V, K> invert() {
        final Builder<Object, Object> builder = builder();
        for (final Map.Entry<K, Object> entry : this.entries()) {
            builder.put(entry.getValue(), entry.getKey());
        }
        final ImmutableSetMultimap<Object, Object> build = builder.build();
        build.inverse = (ImmutableSetMultimap<Object, Object>)this;
        return (ImmutableSetMultimap<V, K>)build;
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of() {
        return (ImmutableSetMultimap<K, V>)EmptyImmutableSetMultimap.INSTANCE;
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of(final K k, final V v) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        return builder.build();
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of(final K k, final V v, final K i, final V v2) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        return builder.build();
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        builder.put(j, v3);
        return builder.build();
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        final Builder<Object, Object> builder = builder();
        builder.put(k, v);
        builder.put(i, v2);
        builder.put(j, v3);
        builder.put(l, v4);
        return builder.build();
    }
    
    public static <K, V> ImmutableSetMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
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
        final Comparator comparator = (Comparator)objectInputStream.readObject();
        final int int1 = objectInputStream.readInt();
        Label_0073: {
            if (int1 < 0) {
                break Label_0073;
            }
            final ImmutableMap.Builder<Object, ImmutableSet<Object>> builder = ImmutableMap.builder();
            int n = 0;
            int n2 = 0;
        Label_0126_Outer:
            while (true) {
                Label_0100: {
                    if (n < int1) {
                        break Label_0100;
                    }
                    try {
                        FieldSettersHolder.MAP_FIELD_SETTER.set(this, builder.build());
                        FieldSettersHolder.SIZE_FIELD_SETTER.set(this, n2);
                        FieldSettersHolder.EMPTY_SET_FIELD_SETTER.set(this, emptySet(comparator));
                        return;
                        Label_0173: {
                            final int int2;
                            throw new InvalidObjectException("Invalid value count " + int2);
                        }
                        final Object object = objectInputStream.readObject();
                        final int int2 = objectInputStream.readInt();
                        iftrue(Label_0173:)(int2 <= 0);
                    Block_6_Outer:
                        while (true) {
                            Block_4: {
                                break Block_4;
                                int n3 = 0;
                                iftrue(Label_0202:)(n3 < int2);
                                while (true) {
                                    final ImmutableSet.Builder<Object> valuesBuilder;
                                    Block_5: {
                                        break Block_5;
                                        Label_0202:
                                        valuesBuilder.add(objectInputStream.readObject());
                                        ++n3;
                                        continue Block_6_Outer;
                                        Label_0218:
                                        throw new InvalidObjectException("Duplicate key-value pairs exist for key " + object);
                                        final ImmutableSet<Object> build;
                                        builder.put(object, build);
                                        n2 += int2;
                                        ++n;
                                        continue Label_0126_Outer;
                                        throw new InvalidObjectException("Invalid key count " + int1);
                                    }
                                    final ImmutableSet<Object> build = valuesBuilder.build();
                                    iftrue(Label_0218:)(build.size() != int2);
                                    continue;
                                }
                            }
                            final ImmutableSet.Builder<Object> valuesBuilder = valuesBuilder(comparator);
                            int n3 = 0;
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
    
    private static <V> ImmutableSet<V> valueSet(@Nullable final Comparator<? super V> comparator, final Collection<? extends V> collection) {
        ImmutableSet<Object> set;
        if (comparator != null) {
            set = ImmutableSortedSet.copyOf((Comparator<? super Object>)comparator, (Collection<?>)collection);
        }
        else {
            set = ImmutableSet.copyOf((Collection<?>)collection);
        }
        return (ImmutableSet<V>)set;
    }
    
    private static <V> ImmutableSet.Builder<V> valuesBuilder(@Nullable final Comparator<? super V> comparator) {
        Object o;
        if (comparator != null) {
            o = new ImmutableSortedSet.Builder((Comparator<? super Object>)comparator);
        }
        else {
            o = new ImmutableSet.Builder();
        }
        return (ImmutableSet.Builder<V>)o;
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.valueComparator());
        Serialization.writeMultimap((Multimap<Object, Object>)this, objectOutputStream);
    }
    
    @Override
    public ImmutableSet<Map.Entry<K, V>> entries() {
        ImmutableSet<Map.Entry<K, V>> entries = this.entries;
        if (entries == null) {
            entries = (ImmutableSet<Map.Entry<K, V>>)new EntrySet((ImmutableSetMultimap<Object, Object>)this);
            this.entries = entries;
        }
        return entries;
    }
    
    @Override
    public ImmutableSet<V> get(@Nullable final K k) {
        return MoreObjects.firstNonNull((ImmutableSet)this.map.get(k), this.emptySet);
    }
    
    @Override
    public ImmutableSetMultimap<V, K> inverse() {
        ImmutableSetMultimap<V, K> inverse = this.inverse;
        if (inverse == null) {
            inverse = this.invert();
            this.inverse = inverse;
        }
        return inverse;
    }
    
    @Deprecated
    @Override
    public ImmutableSet<V> removeAll(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public ImmutableSet<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }
    
    @Nullable
    Comparator<? super V> valueComparator() {
        Comparator<? super V> comparator;
        if (!(this.emptySet instanceof ImmutableSortedSet)) {
            comparator = null;
        }
        else {
            comparator = ((ImmutableSortedSet)this.emptySet).comparator();
        }
        return comparator;
    }
    
    public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V>
    {
        public Builder() {
            super(MultimapBuilder.linkedHashKeys().linkedHashSetValues().build());
        }
        
        public ImmutableSetMultimap<K, V> build() {
            if (this.keyComparator != null) {
                final SetMultimap<Object, Object> build = MultimapBuilder.linkedHashKeys().linkedHashSetValues().build();
                for (final Map.Entry entry : Ordering.from(this.keyComparator).onKeys().immutableSortedCopy(this.builderMultimap.asMap().entrySet())) {
                    build.putAll(entry.getKey(), (Iterable<? extends V>)entry.getValue());
                }
                this.builderMultimap = (Multimap<K, V>)build;
            }
            return (ImmutableSetMultimap<K, V>)copyOf((Multimap<?, ?>)this.builderMultimap, (Comparator<? super Object>)this.valueComparator);
        }
        
        public Builder<K, V> orderKeysBy(final Comparator<? super K> comparator) {
            this.keyComparator = (Comparator<? super K>)Preconditions.checkNotNull((Comparator<? super K>)comparator);
            return this;
        }
        
        public Builder<K, V> orderValuesBy(final Comparator<? super V> comparator) {
            super.orderValuesBy(comparator);
            return this;
        }
        
        public Builder<K, V> put(final K k, final V v) {
            this.builderMultimap.put(Preconditions.checkNotNull(k), Preconditions.checkNotNull(v));
            return this;
        }
        
        public Builder<K, V> put(final Map.Entry<? extends K, ? extends V> entry) {
            this.builderMultimap.put(Preconditions.checkNotNull((K)entry.getKey()), Preconditions.checkNotNull((V)entry.getValue()));
            return this;
        }
        
        public Builder<K, V> putAll(final Multimap<? extends K, ? extends V> multimap) {
            for (final Map.Entry entry : multimap.asMap().entrySet()) {
                this.putAll(entry.getKey(), (Iterable<? extends V>)entry.getValue());
            }
            return this;
        }
        
        @Beta
        public Builder<K, V> putAll(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
            super.putAll(iterable);
            return this;
        }
        
        public Builder<K, V> putAll(final K k, final Iterable<? extends V> iterable) {
            final Collection<V> value = this.builderMultimap.get(Preconditions.checkNotNull(k));
            final Iterator<? extends V> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                value.add(Preconditions.checkNotNull((V)iterator.next()));
            }
            return this;
        }
        
        public Builder<K, V> putAll(final K k, final V... a) {
            return this.putAll(k, (Iterable<? extends V>)Arrays.asList(a));
        }
    }
    
    private static final class EntrySet<K, V> extends ImmutableSet<Map.Entry<K, V>>
    {
        @Weak
        private final transient ImmutableSetMultimap<K, V> multimap;
        
        EntrySet(final ImmutableSetMultimap<K, V> multimap) {
            this.multimap = multimap;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            return this.multimap.containsEntry(entry.getKey(), entry.getValue());
        }
        
        @Override
        boolean isPartialView() {
            return false;
        }
        
        @Override
        public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
            return this.multimap.entryIterator();
        }
        
        @Override
        public int size() {
            return this.multimap.size();
        }
    }
}
