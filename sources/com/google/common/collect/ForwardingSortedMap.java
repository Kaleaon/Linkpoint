package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class ForwardingSortedMap<K, V> extends ForwardingMap<K, V> implements SortedMap<K, V> {

    @Beta
    protected class StandardKeySet extends Maps.SortedKeySet<K, V> {
        public StandardKeySet() {
            super(ForwardingSortedMap.this);
        }
    }

    protected ForwardingSortedMap() {
    }

    private int unsafeCompare(Object obj, Object obj2) {
        Comparator comparator = comparator();
        return comparator != null ? comparator.compare(obj, obj2) : ((Comparable) obj).compareTo(obj2);
    }

    public Comparator<? super K> comparator() {
        return delegate().comparator();
    }

    /* access modifiers changed from: protected */
    public abstract SortedMap<K, V> delegate();

    public K firstKey() {
        return delegate().firstKey();
    }

    public SortedMap<K, V> headMap(K k) {
        return delegate().headMap(k);
    }

    public K lastKey() {
        return delegate().lastKey();
    }

    /* access modifiers changed from: protected */
    @Beta
    public boolean standardContainsKey(@Nullable Object obj) {
        try {
            return unsafeCompare(tailMap(obj).firstKey(), obj) == 0;
        } catch (ClassCastException e) {
            return false;
        } catch (NoSuchElementException e2) {
            return false;
        } catch (NullPointerException e3) {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    @Beta
    public SortedMap<K, V> standardSubMap(K k, K k2) {
        boolean z = false;
        if (unsafeCompare(k, k2) <= 0) {
            z = true;
        }
        Preconditions.checkArgument(z, "fromKey must be <= toKey");
        return tailMap(k).headMap(k2);
    }

    public SortedMap<K, V> subMap(K k, K k2) {
        return delegate().subMap(k, k2);
    }

    public SortedMap<K, V> tailMap(K k) {
        return delegate().tailMap(k);
    }
}
