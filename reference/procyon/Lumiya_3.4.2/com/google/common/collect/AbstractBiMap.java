// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true)
abstract class AbstractBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable
{
    @GwtIncompatible("Not needed in emulated source.")
    private static final long serialVersionUID = 0L;
    private transient Map<K, V> delegate;
    private transient Set<Entry<K, V>> entrySet;
    transient AbstractBiMap<V, K> inverse;
    private transient Set<K> keySet;
    private transient Set<V> valueSet;
    
    private AbstractBiMap(final Map<K, V> delegate, final AbstractBiMap<V, K> inverse) {
        this.delegate = delegate;
        this.inverse = inverse;
    }
    
    AbstractBiMap(final Map<K, V> map, final Map<V, K> map2) {
        this.setDelegates(map, map2);
    }
    
    private V putInBothMaps(@Nullable final K k, @Nullable final V v, final boolean b) {
        this.checkKey(k);
        this.checkValue(v);
        final boolean containsKey = this.containsKey(k);
        if (containsKey && Objects.equal(v, this.get(k))) {
            return v;
        }
        if (!b) {
            Preconditions.checkArgument(!this.containsValue(v), "value already present: %s", v);
        }
        else {
            this.inverse().remove(v);
        }
        final V put = this.delegate.put(k, v);
        this.updateInverseMap(k, containsKey, put, v);
        return put;
    }
    
    private V removeFromBothMaps(Object remove) {
        remove = this.delegate.remove(remove);
        this.removeFromInverseMap(remove);
        return (V)remove;
    }
    
    private void removeFromInverseMap(final V v) {
        this.inverse.delegate.remove(v);
    }
    
    private void updateInverseMap(final K k, final boolean b, final V v, final V v2) {
        if (b) {
            this.removeFromInverseMap(v);
        }
        this.inverse.delegate.put((K)v2, (V)k);
    }
    
    K checkKey(@Nullable final K k) {
        return k;
    }
    
    V checkValue(@Nullable final V v) {
        return v;
    }
    
    @Override
    public void clear() {
        this.delegate.clear();
        this.inverse.delegate.clear();
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.inverse.containsKey(o);
    }
    
    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public V forcePut(@Nullable final K k, @Nullable final V v) {
        return this.putInBothMaps(k, v, true);
    }
    
    @Override
    public BiMap<V, K> inverse() {
        return (BiMap<V, K>)this.inverse;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keySet = this.keySet;
        if (keySet == null) {
            keySet = new KeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @Override
    public V put(@Nullable final K k, @Nullable final V v) {
        return this.putInBothMaps(k, v, false);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V remove(@Nullable Object removeFromBothMaps) {
        if (!this.containsKey(removeFromBothMaps)) {
            removeFromBothMaps = null;
        }
        else {
            removeFromBothMaps = this.removeFromBothMaps(removeFromBothMaps);
        }
        return (V)removeFromBothMaps;
    }
    
    void setDelegates(final Map<K, V> delegate, final Map<V, K> map) {
        final boolean b = false;
        Preconditions.checkState(this.delegate == null);
        Preconditions.checkState(this.inverse == null);
        Preconditions.checkArgument(delegate.isEmpty());
        Preconditions.checkArgument(map.isEmpty());
        Preconditions.checkArgument(delegate != map || b);
        this.delegate = delegate;
        this.inverse = new Inverse<V, K>((Map)map, this);
    }
    
    void setInverse(final AbstractBiMap<V, K> inverse) {
        this.inverse = inverse;
    }
    
    @Override
    public Set<V> values() {
        Set<V> valueSet = this.valueSet;
        if (valueSet == null) {
            valueSet = new ValueSet();
            this.valueSet = valueSet;
        }
        return valueSet;
    }
    
    private class EntrySet extends ForwardingSet<Entry<K, V>>
    {
        final Set<Entry<K, V>> esDelegate;
        
        private EntrySet() {
            this.esDelegate = AbstractBiMap.this.delegate.entrySet();
        }
        
        @Override
        public void clear() {
            AbstractBiMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return Maps.containsEntryImpl(this.delegate(), o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return this.standardContainsAll(collection);
        }
        
        @Override
        protected Set<Entry<K, V>> delegate() {
            return this.esDelegate;
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
                Entry<K, V> entry;
                final /* synthetic */ Iterator val$iterator = EntrySet.this.esDelegate.iterator();
                
                @Override
                public boolean hasNext() {
                    return this.val$iterator.hasNext();
                }
                
                @Override
                public Entry<K, V> next() {
                    this.entry = (Entry<K, V>)this.val$iterator.next();
                    return new ForwardingMapEntry<K, V>() {
                        final /* synthetic */ Entry val$finalEntry = AbstractBiMap$EntrySet$1.this.entry;
                        
                        @Override
                        protected Entry<K, V> delegate() {
                            return this.val$finalEntry;
                        }
                        
                        @Override
                        public V setValue(final V value) {
                            Preconditions.checkState(EntrySet.this.contains(this), (Object)"entry no longer in map");
                            if (!Objects.equal(value, ((ForwardingMapEntry<K, Object>)this).getValue())) {
                                Preconditions.checkArgument(!AbstractBiMap.this.containsValue(value), "value already present: %s", value);
                                final V setValue = this.val$finalEntry.setValue(value);
                                Preconditions.checkState(Objects.equal(value, AbstractBiMap.this.get(((ForwardingMapEntry<Object, V>)this).getKey())), (Object)"entry no longer in map");
                                AbstractBiMap.this.updateInverseMap(((ForwardingMapEntry<Object, V>)this).getKey(), true, setValue, value);
                                return setValue;
                            }
                            return value;
                        }
                    };
                }
                
                @Override
                public void remove() {
                    CollectPreconditions.checkRemove(this.entry != null);
                    final V value = this.entry.getValue();
                    this.val$iterator.remove();
                    AbstractBiMap.this.removeFromInverseMap(value);
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            if (this.esDelegate.contains(o)) {
                final Entry entry = (Entry)o;
                AbstractBiMap.this.inverse.delegate.remove(entry.getValue());
                this.esDelegate.remove(entry);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
        
        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.standardToArray(array);
        }
    }
    
    private static class Inverse<K, V> extends AbstractBiMap<K, V>
    {
        @GwtIncompatible("Not needed in emulated source.")
        private static final long serialVersionUID = 0L;
        
        private Inverse(final Map<K, V> map, final AbstractBiMap<V, K> abstractBiMap) {
            super(map, (AbstractBiMap<Object, Object>)abstractBiMap, null);
        }
        
        @GwtIncompatible("java.io.ObjectInputStream")
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.setInverse((AbstractBiMap<V, K>)objectInputStream.readObject());
        }
        
        @GwtIncompatible("java.io.ObjectOuputStream")
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeObject(this.inverse());
        }
        
        @Override
        K checkKey(final K k) {
            return this.inverse.checkValue(k);
        }
        
        @Override
        V checkValue(final V v) {
            return this.inverse.checkKey(v);
        }
        
        @GwtIncompatible("Not needed in the emulated source.")
        Object readResolve() {
            return this.inverse().inverse();
        }
    }
    
    private class KeySet extends ForwardingSet<K>
    {
        @Override
        public void clear() {
            AbstractBiMap.this.clear();
        }
        
        @Override
        protected Set<K> delegate() {
            return AbstractBiMap.this.delegate.keySet();
        }
        
        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(AbstractBiMap.this.entrySet().iterator());
        }
        
        @Override
        public boolean remove(final Object o) {
            if (this.contains(o)) {
                AbstractBiMap.this.removeFromBothMaps(o);
                return true;
            }
            return false;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
    }
    
    private class ValueSet extends ForwardingSet<V>
    {
        final Set<V> valuesDelegate;
        
        private ValueSet() {
            this.valuesDelegate = (Set<V>)AbstractBiMap.this.inverse.keySet();
        }
        
        @Override
        protected Set<V> delegate() {
            return this.valuesDelegate;
        }
        
        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(AbstractBiMap.this.entrySet().iterator());
        }
        
        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.standardToArray(array);
        }
        
        @Override
        public String toString() {
            return this.standardToString();
        }
    }
}
