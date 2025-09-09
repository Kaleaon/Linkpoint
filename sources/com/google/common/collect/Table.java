package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public interface Table<R, C, V> {

    public interface Cell<R, C, V> {
        boolean equals(@Nullable Object obj);

        @Nullable
        C getColumnKey();

        @Nullable
        R getRowKey();

        @Nullable
        V getValue();

        int hashCode();
    }

    Set<Cell<R, C, V>> cellSet();

    void clear();

    Map<R, V> column(C c);

    Set<C> columnKeySet();

    Map<C, Map<R, V>> columnMap();

    boolean contains(@Nullable Object obj, @Nullable Object obj2);

    boolean containsColumn(@Nullable Object obj);

    boolean containsRow(@Nullable Object obj);

    boolean containsValue(@Nullable Object obj);

    boolean equals(@Nullable Object obj);

    V get(@Nullable Object obj, @Nullable Object obj2);

    int hashCode();

    boolean isEmpty();

    @Nullable
    V put(R r, C c, V v);

    void putAll(Table<? extends R, ? extends C, ? extends V> table);

    @Nullable
    V remove(@Nullable Object obj, @Nullable Object obj2);

    Map<C, V> row(R r);

    Set<R> rowKeySet();

    Map<R, Map<C, V>> rowMap();

    int size();

    Collection<V> values();
}
