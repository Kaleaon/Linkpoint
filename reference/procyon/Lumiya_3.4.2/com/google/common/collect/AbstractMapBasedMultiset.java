// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.ConcurrentModificationException;
import com.google.common.primitives.Ints;
import java.util.Set;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;
import com.google.common.base.Preconditions;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true)
abstract class AbstractMapBasedMultiset<E> extends AbstractMultiset<E> implements Serializable
{
    @GwtIncompatible("not needed in emulated source.")
    private static final long serialVersionUID = -2250766705698539974L;
    private transient Map<E, Count> backingMap;
    private transient long size;
    
    protected AbstractMapBasedMultiset(final Map<E, Count> map) {
        this.backingMap = Preconditions.checkNotNull(map);
        this.size = super.size();
    }
    
    static /* synthetic */ long access$122(final AbstractMapBasedMultiset abstractMapBasedMultiset, long size) {
        size = abstractMapBasedMultiset.size - size;
        return abstractMapBasedMultiset.size = size;
    }
    
    private static int getAndSet(final Count count, final int n) {
        if (count != null) {
            return count.getAndSet(n);
        }
        return 0;
    }
    
    @GwtIncompatible("java.io.ObjectStreamException")
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("Stream data required");
    }
    
    @Override
    public int add(@Nullable final E e, final int i) {
        int n = 0;
        if (i != 0) {
            Preconditions.checkArgument(i > 0, "occurrences cannot be negative: %s", i);
            final Count count = this.backingMap.get(e);
            if (count != null) {
                final int value = count.get();
                final long l = value + (long)i;
                boolean b;
                if (l > 2147483647L) {
                    b = true;
                }
                else {
                    b = false;
                }
                Preconditions.checkArgument(!b, "too many occurrences: %s", l);
                count.getAndAdd(i);
                n = value;
            }
            else {
                this.backingMap.put(e, new Count(i));
            }
            this.size += i;
            return n;
        }
        return this.count(e);
    }
    
    @Override
    public void clear() {
        final Iterator<Count> iterator = this.backingMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().set(0);
        }
        this.backingMap.clear();
        this.size = 0L;
    }
    
    @Override
    public int count(@Nullable final Object o) {
        final Count count = Maps.safeGet(this.backingMap, o);
        int value;
        if (count != null) {
            value = count.get();
        }
        else {
            value = 0;
        }
        return value;
    }
    
    @Override
    int distinctElements() {
        return this.backingMap.size();
    }
    
    @Override
    Iterator<Entry<E>> entryIterator() {
        return new Iterator<Entry<E>>() {
            Map.Entry<E, Count> toRemove;
            final /* synthetic */ Iterator val$backingEntries = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
            
            @Override
            public boolean hasNext() {
                return this.val$backingEntries.hasNext();
            }
            
            @Override
            public Entry<E> next() {
                final Map.Entry toRemove = (Map.Entry)this.val$backingEntries.next();
                this.toRemove = toRemove;
                return new Multisets.AbstractEntry<E>() {
                    @Override
                    public int getCount() {
                        final Count count = toRemove.getValue();
                        if (count == null || count.get() == 0) {
                            final Count count2 = AbstractMapBasedMultiset.this.backingMap.get(this.getElement());
                            if (count2 != null) {
                                return count2.get();
                            }
                        }
                        int value;
                        if (count != null) {
                            value = count.get();
                        }
                        else {
                            value = 0;
                        }
                        return value;
                    }
                    
                    @Override
                    public E getElement() {
                        return toRemove.getKey();
                    }
                };
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.toRemove != null);
                AbstractMapBasedMultiset.access$122(AbstractMapBasedMultiset.this, this.toRemove.getValue().getAndSet(0));
                this.val$backingEntries.remove();
                this.toRemove = null;
            }
        };
    }
    
    @Override
    public Set<Entry<E>> entrySet() {
        return super.entrySet();
    }
    
    @Override
    public Iterator<E> iterator() {
        return new MapBasedMultisetIterator();
    }
    
    @Override
    public int remove(@Nullable final Object o, final int i) {
        if (i == 0) {
            return this.count(o);
        }
        Preconditions.checkArgument(i > 0, "occurrences cannot be negative: %s", i);
        final Count count = this.backingMap.get(o);
        if (count != null) {
            final int value = count.get();
            int n;
            if (value <= (n = i)) {
                this.backingMap.remove(o);
                n = value;
            }
            count.addAndGet(-n);
            this.size -= n;
            return value;
        }
        return 0;
    }
    
    void setBackingMap(final Map<E, Count> backingMap) {
        this.backingMap = backingMap;
    }
    
    @Override
    public int setCount(@Nullable final E e, final int n) {
        CollectPreconditions.checkNonnegative(n, "count");
        int n2;
        if (n != 0) {
            final Count count = this.backingMap.get(e);
            n2 = getAndSet(count, n);
            if (count == null) {
                this.backingMap.put(e, new Count(n));
            }
        }
        else {
            n2 = getAndSet(this.backingMap.remove(e), n);
        }
        this.size += n - n2;
        return n2;
    }
    
    @Override
    public int size() {
        return Ints.saturatedCast(this.size);
    }
    
    private class MapBasedMultisetIterator implements Iterator<E>
    {
        boolean canRemove;
        Map.Entry<E, Count> currentEntry;
        final Iterator<Map.Entry<E, Count>> entryIterator;
        int occurrencesLeft;
        
        MapBasedMultisetIterator() {
            this.entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.occurrencesLeft > 0 || this.entryIterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public E next() {
            if (this.occurrencesLeft == 0) {
                this.currentEntry = this.entryIterator.next();
                this.occurrencesLeft = this.currentEntry.getValue().get();
            }
            --this.occurrencesLeft;
            this.canRemove = true;
            return this.currentEntry.getKey();
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.canRemove);
            if (this.currentEntry.getValue().get() > 0) {
                if (this.currentEntry.getValue().addAndGet(-1) == 0) {
                    this.entryIterator.remove();
                }
                AbstractMapBasedMultiset.this.size--;
                this.canRemove = false;
                return;
            }
            throw new ConcurrentModificationException();
        }
    }
}
