// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.reflect;

import java.util.Collection;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterators;
import com.google.common.base.Function;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMapEntry;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import java.util.Map;
import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;

@Beta
public final class MutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B>
{
    private final Map<TypeToken<? extends B>, B> backingMap;
    
    public MutableTypeToInstanceMap() {
        this.backingMap = (Map<TypeToken<? extends B>, B>)Maps.newHashMap();
    }
    
    @Nullable
    private <T extends B> T trustedGet(final TypeToken<T> typeToken) {
        return (T)this.backingMap.get(typeToken);
    }
    
    @Nullable
    private <T extends B> T trustedPut(final TypeToken<T> typeToken, @Nullable final T t) {
        return (T)this.backingMap.put((TypeToken<? extends B>)typeToken, t);
    }
    
    @Override
    protected Map<TypeToken<? extends B>, B> delegate() {
        return this.backingMap;
    }
    
    @Override
    public Set<Entry<TypeToken<? extends B>, B>> entrySet() {
        return UnmodifiableEntry.transformEntries(super.entrySet());
    }
    
    @Nullable
    @Override
    public <T extends B> T getInstance(final TypeToken<T> typeToken) {
        return (T)this.trustedGet((TypeToken<Object>)typeToken.rejectTypeVariables());
    }
    
    @Nullable
    @Override
    public <T extends B> T getInstance(final Class<T> clazz) {
        return this.trustedGet((TypeToken<T>)TypeToken.of((Class<T>)clazz));
    }
    
    @Override
    public B put(final TypeToken<? extends B> typeToken, final B b) {
        throw new UnsupportedOperationException("Please use putInstance() instead.");
    }
    
    @Override
    public void putAll(final Map<? extends TypeToken<? extends B>, ? extends B> map) {
        throw new UnsupportedOperationException("Please use putInstance() instead.");
    }
    
    @Nullable
    @Override
    public <T extends B> T putInstance(final TypeToken<T> typeToken, @Nullable final T t) {
        return (T)this.trustedPut((TypeToken<Object>)typeToken.rejectTypeVariables(), t);
    }
    
    @Nullable
    @Override
    public <T extends B> T putInstance(final Class<T> clazz, @Nullable final T t) {
        return this.trustedPut((TypeToken<T>)TypeToken.of((Class<T>)clazz), t);
    }
    
    private static final class UnmodifiableEntry<K, V> extends ForwardingMapEntry<K, V>
    {
        private final Entry<K, V> delegate;
        
        private UnmodifiableEntry(final Entry<K, V> entry) {
            this.delegate = (Entry<K, V>)Preconditions.checkNotNull((Map.Entry)entry);
        }
        
        private static <K, V> Iterator<Entry<K, V>> transformEntries(final Iterator<Entry<K, V>> iterator) {
            return Iterators.transform((Iterator<Object>)iterator, (Function<? super Object, ? extends Entry<K, V>>)new Function<Entry<K, V>, Entry<K, V>>() {
                @Override
                public Entry<K, V> apply(final Entry<K, V> entry) {
                    return new UnmodifiableEntry<K, V>((Entry)entry);
                }
            });
        }
        
        static <K, V> Set<Entry<K, V>> transformEntries(final Set<Entry<K, V>> set) {
            return new ForwardingSet<Entry<K, V>>() {
                @Override
                protected Set<Entry<K, V>> delegate() {
                    return set;
                }
                
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return (Iterator<Entry<K, V>>)transformEntries((Iterator<Entry<Object, Object>>)super.iterator());
                }
                
                @Override
                public Object[] toArray() {
                    return this.standardToArray();
                }
                
                @Override
                public <T> T[] toArray(final T[] array) {
                    return this.standardToArray(array);
                }
            };
        }
        
        @Override
        protected Entry<K, V> delegate() {
            return this.delegate;
        }
        
        @Override
        public V setValue(final V v) {
            throw new UnsupportedOperationException();
        }
    }
}
