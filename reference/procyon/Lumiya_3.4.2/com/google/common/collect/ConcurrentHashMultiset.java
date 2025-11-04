// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.primitives.Ints;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.math.IntMath;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import com.google.common.annotations.Beta;
import java.util.concurrent.ConcurrentHashMap;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentMap;
import java.io.Serializable;

public final class ConcurrentHashMultiset<E> extends AbstractMultiset<E> implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final transient ConcurrentMap<E, AtomicInteger> countMap;
    
    @VisibleForTesting
    ConcurrentHashMultiset(final ConcurrentMap<E, AtomicInteger> countMap) {
        Preconditions.checkArgument(countMap.isEmpty());
        this.countMap = countMap;
    }
    
    public static <E> ConcurrentHashMultiset<E> create() {
        return new ConcurrentHashMultiset<E>(new ConcurrentHashMap<E, AtomicInteger>());
    }
    
    @Beta
    public static <E> ConcurrentHashMultiset<E> create(final MapMaker mapMaker) {
        return new ConcurrentHashMultiset<E>(mapMaker.makeMap());
    }
    
    public static <E> ConcurrentHashMultiset<E> create(final Iterable<? extends E> iterable) {
        final ConcurrentHashMultiset<Object> create = create();
        Iterables.addAll(create, iterable);
        return (ConcurrentHashMultiset<E>)create;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, objectInputStream.readObject());
    }
    
    private List<E> snapshot() {
        final ArrayList<Object> arrayListWithExpectedSize = Lists.newArrayListWithExpectedSize(this.size());
        for (final Multiset.Entry<Object> entry : this.entrySet()) {
            final E element = entry.getElement();
            for (int i = entry.getCount(); i > 0; --i) {
                arrayListWithExpectedSize.add(element);
            }
        }
        return (List<E>)arrayListWithExpectedSize;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.countMap);
    }
    
    @Override
    public int add(final E e, final int initialValue) {
        Preconditions.checkNotNull(e);
        if (initialValue != 0) {
            CollectPreconditions.checkPositive(initialValue, "occurences");
        Label_0067:
            while (true) {
                AtomicInteger atomicInteger = Maps.safeGet(this.countMap, e);
                if (atomicInteger == null && (atomicInteger = this.countMap.putIfAbsent(e, new AtomicInteger(initialValue))) == null) {
                    return 0;
                }
                AtomicInteger atomicInteger2;
                while (true) {
                    final int value = atomicInteger.get();
                    if (value != 0) {
                        try {
                            if (atomicInteger.compareAndSet(value, IntMath.checkedAdd(value, initialValue))) {
                                return value;
                            }
                            continue;
                        }
                        catch (final ArithmeticException ex) {
                            throw new IllegalArgumentException("Overflow adding " + initialValue + " occurrences to a count of " + value);
                        }
                        break;
                    }
                    atomicInteger2 = new AtomicInteger(initialValue);
                    if (this.countMap.putIfAbsent(e, atomicInteger2) == null) {
                        break Label_0067;
                    }
                    break;
                }
                if (!this.countMap.replace(e, atomicInteger, atomicInteger2)) {
                    continue;
                }
                break;
            }
            return 0;
        }
        return this.count(e);
    }
    
    @Override
    public void clear() {
        this.countMap.clear();
    }
    
    @Override
    public int count(@Nullable final Object o) {
        final AtomicInteger atomicInteger = Maps.safeGet(this.countMap, o);
        int value;
        if (atomicInteger != null) {
            value = atomicInteger.get();
        }
        else {
            value = 0;
        }
        return value;
    }
    
    @Override
    Set<E> createElementSet() {
        return new ForwardingSet<E>() {
            final /* synthetic */ Set val$delegate = ConcurrentHashMultiset.this.countMap.keySet();
            
            @Override
            public boolean contains(@Nullable final Object o) {
                boolean b = false;
                if (o != null && Collections2.safeContains(this.val$delegate, o)) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public boolean containsAll(final Collection<?> collection) {
                return this.standardContainsAll(collection);
            }
            
            @Override
            protected Set<E> delegate() {
                return this.val$delegate;
            }
            
            @Override
            public boolean remove(final Object o) {
                boolean b = false;
                if (o != null && Collections2.safeRemove(this.val$delegate, o)) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return this.standardRemoveAll(collection);
            }
        };
    }
    
    public Set<Entry<E>> createEntrySet() {
        return (Set<Entry<E>>)new EntrySet();
    }
    
    @Override
    int distinctElements() {
        return this.countMap.size();
    }
    
    @Override
    Iterator<Entry<E>> entryIterator() {
        return new ForwardingIterator<Entry<E>>() {
            private Entry<E> last;
            final /* synthetic */ Iterator val$readOnlyIterator = new AbstractIterator<Entry<E>>(this) {
                private Iterator<Map.Entry<E, AtomicInteger>> mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
                
                @Override
                protected Entry<E> computeNext() {
                    while (this.mapEntries.hasNext()) {
                        final Map.Entry entry = this.mapEntries.next();
                        final int value = ((AtomicInteger)entry.getValue()).get();
                        if (value != 0) {
                            return Multisets.immutableEntry(entry.getKey(), value);
                        }
                    }
                    return (Entry<E>)((AbstractIterator<Multiset.Entry>)this).endOfData();
                }
            };
            
            @Override
            protected Iterator<Entry<E>> delegate() {
                return this.val$readOnlyIterator;
            }
            
            @Override
            public Entry<E> next() {
                return this.last = (Entry<E>)super.next();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.last != null);
                ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
                this.last = null;
            }
        };
    }
    
    @Override
    public boolean isEmpty() {
        return this.countMap.isEmpty();
    }
    
    @Override
    public int remove(@Nullable final Object o, final int n) {
        if (n == 0) {
            return this.count(o);
        }
        CollectPreconditions.checkPositive(n, "occurences");
        final AtomicInteger atomicInteger = Maps.safeGet(this.countMap, o);
        if (atomicInteger != null) {
            int value;
            int max;
            do {
                value = atomicInteger.get();
                if (value == 0) {
                    return 0;
                }
                max = Math.max(0, value - n);
            } while (!atomicInteger.compareAndSet(value, max));
            if (max == 0) {
                this.countMap.remove(o, atomicInteger);
            }
            return value;
        }
        return 0;
    }
    
    public boolean removeExactly(@Nullable final Object o, final int n) {
        if (n == 0) {
            return true;
        }
        CollectPreconditions.checkPositive(n, "occurences");
        final AtomicInteger atomicInteger = Maps.safeGet(this.countMap, o);
        if (atomicInteger != null) {
            int value;
            int newValue;
            do {
                value = atomicInteger.get();
                if (value < n) {
                    return false;
                }
                newValue = value - n;
            } while (!atomicInteger.compareAndSet(value, newValue));
            if (newValue == 0) {
                this.countMap.remove(o, atomicInteger);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int setCount(final E e, final int initialValue) {
        Preconditions.checkNotNull(e);
        CollectPreconditions.checkNonnegative(initialValue, "count");
    Label_0013:
        while (true) {
            AtomicInteger atomicInteger = Maps.safeGet(this.countMap, e);
            if (atomicInteger == null) {
                if (initialValue == 0) {
                    return 0;
                }
                if ((atomicInteger = this.countMap.putIfAbsent(e, new AtomicInteger(initialValue))) == null) {
                    return 0;
                }
            }
            int value;
            do {
                value = atomicInteger.get();
                if (value != 0) {
                    continue;
                }
                if (initialValue == 0) {
                    return 0;
                }
                final AtomicInteger atomicInteger2 = new AtomicInteger(initialValue);
                if (this.countMap.putIfAbsent(e, atomicInteger2) != null && !this.countMap.replace(e, atomicInteger, atomicInteger2)) {
                    continue Label_0013;
                }
                return 0;
            } while (!atomicInteger.compareAndSet(value, initialValue));
            if (initialValue == 0) {
                this.countMap.remove(e, atomicInteger);
            }
            return value;
        }
    }
    
    @Override
    public boolean setCount(final E e, final int n, final int initialValue) {
        boolean b = false;
        Preconditions.checkNotNull(e);
        CollectPreconditions.checkNonnegative(n, "oldCount");
        CollectPreconditions.checkNonnegative(initialValue, "newCount");
        final AtomicInteger atomicInteger = Maps.safeGet(this.countMap, e);
        if (atomicInteger != null) {
            final int value = atomicInteger.get();
            if (value == n) {
                if (value != 0) {
                    if (atomicInteger.compareAndSet(value, initialValue)) {
                        if (initialValue == 0) {
                            this.countMap.remove(e, atomicInteger);
                        }
                        return true;
                    }
                }
                else {
                    if (initialValue != 0) {
                        final AtomicInteger atomicInteger2 = new AtomicInteger(initialValue);
                        if (this.countMap.putIfAbsent(e, atomicInteger2) == null || this.countMap.replace(e, atomicInteger, atomicInteger2)) {
                            b = true;
                        }
                        return b;
                    }
                    this.countMap.remove(e, atomicInteger);
                    return true;
                }
            }
            return false;
        }
        return n == 0 && (initialValue == 0 || this.countMap.putIfAbsent(e, new AtomicInteger(initialValue)) == null);
    }
    
    @Override
    public int size() {
        final Iterator<Object> iterator = (Iterator<Object>)this.countMap.values().iterator();
        long n = 0L;
        while (iterator.hasNext()) {
            n += iterator.next().get();
        }
        return Ints.saturatedCast(n);
    }
    
    @Override
    public Object[] toArray() {
        return this.snapshot().toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.snapshot().toArray(array);
    }
    
    private class EntrySet extends AbstractMultiset.EntrySet
    {
        private List<Entry<E>> snapshot() {
            final ArrayList<Object> arrayListWithExpectedSize = Lists.newArrayListWithExpectedSize(((AbstractMultiset.EntrySet)this).size());
            Iterators.addAll(arrayListWithExpectedSize, ((AbstractMultiset.EntrySet)this).iterator());
            return (List<Entry<E>>)arrayListWithExpectedSize;
        }
        
        ConcurrentHashMultiset<E> multiset() {
            return ConcurrentHashMultiset.this;
        }
        
        @Override
        public Object[] toArray() {
            return this.snapshot().toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.snapshot().toArray(array);
        }
    }
    
    private static class FieldSettersHolder
    {
        static final Serialization.FieldSetter<ConcurrentHashMultiset> COUNT_MAP_FIELD_SETTER;
        
        static {
            COUNT_MAP_FIELD_SETTER = (Serialization.FieldSetter)Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");
        }
    }
}
