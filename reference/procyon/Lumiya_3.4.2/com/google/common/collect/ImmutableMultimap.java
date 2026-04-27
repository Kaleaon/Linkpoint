// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.j2objc.annotations.Weak;
import java.util.Arrays;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.Set;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.annotations.Beta;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true)
public abstract class ImmutableMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
    final transient int size;
    
    ImmutableMultimap(final ImmutableMap<K, ? extends ImmutableCollection<V>> map, final int size) {
        this.map = map;
        this.size = size;
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    public static <K, V> ImmutableMultimap<K, V> copyOf(final Multimap<? extends K, ? extends V> multimap) {
        if (multimap instanceof ImmutableMultimap) {
            final ImmutableMultimap immutableMultimap = (ImmutableMultimap)multimap;
            if (!immutableMultimap.isPartialView()) {
                return immutableMultimap;
            }
        }
        return (ImmutableMultimap<K, V>)ImmutableListMultimap.copyOf((Multimap<?, ?>)multimap);
    }
    
    @Beta
    public static <K, V> ImmutableMultimap<K, V> copyOf(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
        return (ImmutableMultimap<K, V>)ImmutableListMultimap.copyOf((Iterable<? extends Map.Entry<?, ?>>)iterable);
    }
    
    public static <K, V> ImmutableMultimap<K, V> of() {
        return (ImmutableMultimap<K, V>)ImmutableListMultimap.of();
    }
    
    public static <K, V> ImmutableMultimap<K, V> of(final K k, final V v) {
        return ImmutableListMultimap.of(k, v);
    }
    
    public static <K, V> ImmutableMultimap<K, V> of(final K k, final V v, final K i, final V v2) {
        return ImmutableListMultimap.of(k, v, i, v2);
    }
    
    public static <K, V> ImmutableMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        return ImmutableListMultimap.of(k, v, i, v2, j, v3);
    }
    
    public static <K, V> ImmutableMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        return ImmutableListMultimap.of(k, v, i, v2, j, v3, l, v4);
    }
    
    public static <K, V> ImmutableMultimap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        return ImmutableListMultimap.of(k, v, i, v2, j, v3, l, v4, m, v5);
    }
    
    @Override
    public ImmutableMap<K, Collection<V>> asMap() {
        return (ImmutableMap<K, Collection<V>>)this.map;
    }
    
    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        boolean b = false;
        if (o != null && super.containsValue(o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    Map<K, Collection<V>> createAsMap() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    ImmutableCollection<Map.Entry<K, V>> createEntries() {
        return (ImmutableCollection<Map.Entry<K, V>>)new EntryCollection((ImmutableMultimap<Object, Object>)this);
    }
    
    @Override
    ImmutableMultiset<K> createKeys() {
        return new Keys();
    }
    
    @Override
    ImmutableCollection<V> createValues() {
        return (ImmutableCollection<V>)new Values((ImmutableMultimap<Object, Object>)this);
    }
    
    @Override
    public ImmutableCollection<Map.Entry<K, V>> entries() {
        return (ImmutableCollection)super.entries();
    }
    
    @Override
    UnmodifiableIterator<Map.Entry<K, V>> entryIterator() {
        return new Itr<Map.Entry<K, V>>() {
            Map.Entry<K, V> output(final K k, final V v) {
                return Maps.immutableEntry(k, v);
            }
        };
    }
    
    @Override
    public abstract ImmutableCollection<V> get(final K p0);
    
    public abstract ImmutableMultimap<V, K> inverse();
    
    boolean isPartialView() {
        return this.map.isPartialView();
    }
    
    @Override
    public ImmutableSet<K> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public ImmutableMultiset<K> keys() {
        return (ImmutableMultiset)super.keys();
    }
    
    @Deprecated
    @Override
    public boolean put(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public boolean putAll(final Multimap<? extends K, ? extends V> multimap) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public boolean putAll(final K k, final Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public boolean remove(final Object o, final Object o2) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public ImmutableCollection<V> removeAll(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public ImmutableCollection<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    UnmodifiableIterator<V> valueIterator() {
        return new Itr<V>() {
            @Override
            V output(final K k, final V v) {
                return v;
            }
        };
    }
    
    @Override
    public ImmutableCollection<V> values() {
        return (ImmutableCollection)super.values();
    }
    
    public static class Builder<K, V>
    {
        Multimap<K, V> builderMultimap;
        Comparator<? super K> keyComparator;
        Comparator<? super V> valueComparator;
        
        public Builder() {
            this(MultimapBuilder.linkedHashKeys().arrayListValues().build());
        }
        
        Builder(final Multimap<K, V> builderMultimap) {
            this.builderMultimap = builderMultimap;
        }
        
        public ImmutableMultimap<K, V> build() {
            if (this.valueComparator != null) {
                final Iterator<Collection<V>> iterator = this.builderMultimap.asMap().values().iterator();
                while (iterator.hasNext()) {
                    Collections.sort((List<Object>)iterator.next(), (Comparator<? super Object>)this.valueComparator);
                }
            }
            if (this.keyComparator != null) {
                final ListMultimap<Object, Object> build = MultimapBuilder.linkedHashKeys().arrayListValues().build();
                for (final Map.Entry entry : Ordering.from(this.keyComparator).onKeys().immutableSortedCopy(this.builderMultimap.asMap().entrySet())) {
                    build.putAll(entry.getKey(), (Iterable<? extends V>)entry.getValue());
                }
                this.builderMultimap = (Multimap<K, V>)build;
            }
            return ImmutableMultimap.copyOf((Multimap<? extends K, ? extends V>)this.builderMultimap);
        }
        
        public Builder<K, V> orderKeysBy(final Comparator<? super K> comparator) {
            this.keyComparator = Preconditions.checkNotNull(comparator);
            return this;
        }
        
        public Builder<K, V> orderValuesBy(final Comparator<? super V> comparator) {
            this.valueComparator = Preconditions.checkNotNull(comparator);
            return this;
        }
        
        public Builder<K, V> put(final K k, final V v) {
            CollectPreconditions.checkEntryNotNull(k, v);
            this.builderMultimap.put(k, v);
            return this;
        }
        
        public Builder<K, V> put(final Map.Entry<? extends K, ? extends V> entry) {
            return (Builder<K, V>)this.put(entry.getKey(), entry.getValue());
        }
        
        public Builder<K, V> putAll(final Multimap<? extends K, ? extends V> multimap) {
            for (final Map.Entry entry : multimap.asMap().entrySet()) {
                this.putAll(entry.getKey(), (Iterable<? extends V>)entry.getValue());
            }
            return this;
        }
        
        @Beta
        public Builder<K, V> putAll(final Iterable<? extends Map.Entry<? extends K, ? extends V>> iterable) {
            final Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.put((Map.Entry<? extends K, ? extends V>)iterator.next());
            }
            return this;
        }
        
        public Builder<K, V> putAll(final K k, final Iterable<? extends V> iterable) {
            if (k != null) {
                final Collection<V> value = this.builderMultimap.get(k);
                for (final V next : iterable) {
                    CollectPreconditions.checkEntryNotNull(k, next);
                    value.add(next);
                }
                return this;
            }
            throw new NullPointerException("null key in entry: null=" + Iterables.toString(iterable));
        }
        
        public Builder<K, V> putAll(final K k, final V... a) {
            return this.putAll(k, (Iterable<? extends V>)Arrays.asList(a));
        }
    }
    
    private static class EntryCollection<K, V> extends ImmutableCollection<Map.Entry<K, V>>
    {
        private static final long serialVersionUID = 0L;
        @Weak
        final ImmutableMultimap<K, V> multimap;
        
        EntryCollection(final ImmutableMultimap<K, V> multimap) {
            this.multimap = multimap;
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            return this.multimap.containsEntry(entry.getKey(), entry.getValue());
        }
        
        @Override
        boolean isPartialView() {
            return this.multimap.isPartialView();
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
    
    @GwtIncompatible("java serialization is not supported")
    static class FieldSettersHolder
    {
        static final Serialization.FieldSetter<ImmutableSetMultimap> EMPTY_SET_FIELD_SETTER;
        static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER;
        static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER;
        
        static {
            MAP_FIELD_SETTER = (Serialization.FieldSetter)Serialization.getFieldSetter(ImmutableMultimap.class, "map");
            SIZE_FIELD_SETTER = (Serialization.FieldSetter)Serialization.getFieldSetter(ImmutableMultimap.class, "size");
            EMPTY_SET_FIELD_SETTER = (Serialization.FieldSetter)Serialization.getFieldSetter(ImmutableSetMultimap.class, "emptySet");
        }
    }
    
    private abstract class Itr<T> extends UnmodifiableIterator<T>
    {
        K key;
        final Iterator<Map.Entry<K, Collection<V>>> mapIterator;
        Iterator<V> valueIterator;
        
        private Itr() {
            this.mapIterator = ImmutableMultimap.this.asMap().entrySet().iterator();
            this.key = null;
            this.valueIterator = (Iterator<V>)Iterators.emptyIterator();
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.mapIterator.hasNext() || this.valueIterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T next() {
            if (!this.valueIterator.hasNext()) {
                final Map.Entry entry = this.mapIterator.next();
                this.key = entry.getKey();
                this.valueIterator = ((Collection)entry.getValue()).iterator();
            }
            return this.output(this.key, this.valueIterator.next());
        }
        
        abstract T output(final K p0, final V p1);
    }
    
    class Keys extends ImmutableMultiset<K>
    {
        @Override
        public boolean contains(@Nullable final Object o) {
            return ImmutableMultimap.this.containsKey(o);
        }
        
        @Override
        public int count(@Nullable final Object o) {
            final Collection collection = (Collection)ImmutableMultimap.this.map.get(o);
            int size;
            if (collection != null) {
                size = collection.size();
            }
            else {
                size = 0;
            }
            return size;
        }
        
        @Override
        public Set<K> elementSet() {
            return ImmutableMultimap.this.keySet();
        }
        
        @Override
        Entry<K> getEntry(final int n) {
            final Map.Entry<E, V> entry = ImmutableMultimap.this.map.entrySet().asList().get(n);
            return Multisets.immutableEntry((K)entry.getKey(), ((Collection)entry.getValue()).size());
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public int size() {
            return ImmutableMultimap.this.size();
        }
    }
    
    private static final class Values<K, V> extends ImmutableCollection<V>
    {
        private static final long serialVersionUID = 0L;
        @Weak
        private final transient ImmutableMultimap<K, V> multimap;
        
        Values(final ImmutableMultimap<K, V> multimap) {
            this.multimap = multimap;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return this.multimap.containsValue(o);
        }
        
        @GwtIncompatible("not present in emulated superclass")
        @Override
        int copyIntoArray(final Object[] array, int copyIntoArray) {
            final Iterator iterator = this.multimap.map.values().iterator();
            while (iterator.hasNext()) {
                copyIntoArray = ((ImmutableCollection)iterator.next()).copyIntoArray(array, copyIntoArray);
            }
            return copyIntoArray;
        }
        
        @Override
        boolean isPartialView() {
            return true;
        }
        
        @Override
        public UnmodifiableIterator<V> iterator() {
            return this.multimap.valueIterator();
        }
        
        @Override
        public int size() {
            return this.multimap.size();
        }
    }
}
