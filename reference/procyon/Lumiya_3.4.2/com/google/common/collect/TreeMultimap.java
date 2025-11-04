// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.NavigableMap;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Collection;
import java.io.ObjectInputStream;
import com.google.common.base.Preconditions;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Comparator;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V>
{
    @GwtIncompatible("not needed in emulated source")
    private static final long serialVersionUID = 0L;
    private transient Comparator<? super K> keyComparator;
    private transient Comparator<? super V> valueComparator;
    
    TreeMultimap(final Comparator<? super K> comparator, final Comparator<? super V> valueComparator) {
        super(new TreeMap(comparator));
        this.keyComparator = comparator;
        this.valueComparator = valueComparator;
    }
    
    private TreeMultimap(final Comparator<? super K> comparator, final Comparator<? super V> comparator2, final Multimap<? extends K, ? extends V> multimap) {
        this(comparator, comparator2);
        this.putAll(multimap);
    }
    
    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
        return new TreeMultimap<K, V>(Ordering.natural(), Ordering.natural());
    }
    
    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(final Multimap<? extends K, ? extends V> multimap) {
        return new TreeMultimap<K, V>(Ordering.natural(), Ordering.natural(), multimap);
    }
    
    public static <K, V> TreeMultimap<K, V> create(final Comparator<? super K> comparator, final Comparator<? super V> comparator2) {
        return new TreeMultimap<K, V>(Preconditions.checkNotNull(comparator), Preconditions.checkNotNull(comparator2));
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.keyComparator = Preconditions.checkNotNull(objectInputStream.readObject());
        this.valueComparator = Preconditions.checkNotNull(objectInputStream.readObject());
        this.setMap(new TreeMap<K, Collection<V>>(this.keyComparator));
        Serialization.populateMultimap((Multimap<Object, Object>)this, objectInputStream);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.keyComparator());
        objectOutputStream.writeObject(this.valueComparator());
        Serialization.writeMultimap((Multimap<Object, Object>)this, objectOutputStream);
    }
    
    @GwtIncompatible("NavigableMap")
    @Override
    public NavigableMap<K, Collection<V>> asMap() {
        return (NavigableMap)super.asMap();
    }
    
    @GwtIncompatible("NavigableMap")
    @Override
    NavigableMap<K, Collection<V>> backingMap() {
        return (NavigableMap)super.backingMap();
    }
    
    @GwtIncompatible("NavigableMap")
    @Override
    NavigableMap<K, Collection<V>> createAsMap() {
        return (NavigableMap<K, Collection<V>>)new NavigableAsMap(this.backingMap());
    }
    
    @Override
    Collection<V> createCollection(@Nullable final K k) {
        if (k == null) {
            this.keyComparator().compare((Object)k, (Object)k);
        }
        return super.createCollection(k);
    }
    
    @Override
    SortedSet<V> createCollection() {
        return new TreeSet<V>(this.valueComparator);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    NavigableSet<K> createKeySet() {
        return (NavigableSet<K>)new NavigableKeySet(this.backingMap());
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public NavigableSet<V> get(@Nullable final K k) {
        return (NavigableSet)super.get(k);
    }
    
    public Comparator<? super K> keyComparator() {
        return this.keyComparator;
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public NavigableSet<K> keySet() {
        return (NavigableSet)super.keySet();
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    Collection<V> unmodifiableCollectionSubclass(final Collection<V> collection) {
        return (Collection<V>)Sets.unmodifiableNavigableSet((NavigableSet<Object>)collection);
    }
    
    @Override
    public Comparator<? super V> valueComparator() {
        return this.valueComparator;
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    Collection<V> wrapCollection(final K k, final Collection<V> collection) {
        return (Collection<V>)new WrappedNavigableSet((K)k, (NavigableSet<V>)collection, null);
    }
}
