// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;

@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V>
{
    protected ForwardingSortedMap() {
    }
    
    private int unsafeCompare(final Object o, final Object o2) {
        final Comparator<? super K> comparator = this.comparator();
        if (comparator != null) {
            return comparator.compare(o, o2);
        }
        return ((Comparable)o).compareTo(o2);
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.delegate().comparator();
    }
    
    @Override
    protected abstract SortedMap<K, V> delegate();
    
    @Override
    public K firstKey() {
        return this.delegate().firstKey();
    }
    
    @Override
    public SortedMap<K, V> headMap(final K k) {
        return this.delegate().headMap(k);
    }
    
    @Override
    public K lastKey() {
        return this.delegate().lastKey();
    }
    
    @Beta
    @Override
    protected boolean standardContainsKey(@Nullable final Object o) {
        boolean b = false;
        try {
            if (this.unsafeCompare(this.tailMap((K)o).firstKey(), o) == 0) {
                b = true;
            }
            return b;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NoSuchElementException ex2) {
            return false;
        }
        catch (final NullPointerException ex3) {
            return false;
        }
    }
    
    @Beta
    protected SortedMap<K, V> standardSubMap(final K k, final K i) {
        boolean b = false;
        if (this.unsafeCompare(k, i) <= 0) {
            b = true;
        }
        Preconditions.checkArgument(b, (Object)"fromKey must be <= toKey");
        return this.tailMap(k).headMap(i);
    }
    
    @Override
    public SortedMap<K, V> subMap(final K k, final K i) {
        return this.delegate().subMap(k, i);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K k) {
        return this.delegate().tailMap(k);
    }
    
    @Beta
    protected class StandardKeySet extends SortedKeySet<K, V>
    {
        public StandardKeySet() {
            super(ForwardingSortedMap.this);
        }
    }
}
