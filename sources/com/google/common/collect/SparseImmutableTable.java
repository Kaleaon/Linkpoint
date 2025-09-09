package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@GwtCompatible
@Immutable
final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] iterationOrderColumn;
    private final int[] iterationOrderRow;
    private final ImmutableMap<R, Map<C, V>> rowMap;

    SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> immutableList, ImmutableSet<R> immutableSet, ImmutableSet<C> immutableSet2) {
        int i = 0;
        ImmutableMap<R, Integer> indexMap = Maps.indexMap(immutableSet);
        LinkedHashMap newLinkedHashMap = Maps.newLinkedHashMap();
        UnmodifiableIterator<R> it = immutableSet.iterator();
        while (it.hasNext()) {
            newLinkedHashMap.put(it.next(), new LinkedHashMap());
        }
        LinkedHashMap newLinkedHashMap2 = Maps.newLinkedHashMap();
        UnmodifiableIterator<C> it2 = immutableSet2.iterator();
        while (it2.hasNext()) {
            newLinkedHashMap2.put(it2.next(), new LinkedHashMap());
        }
        int[] iArr = new int[immutableList.size()];
        int[] iArr2 = new int[immutableList.size()];
        while (true) {
            int i2 = i;
            if (i2 >= immutableList.size()) {
                this.iterationOrderRow = iArr;
                this.iterationOrderColumn = iArr2;
                ImmutableMap.Builder builder = new ImmutableMap.Builder(newLinkedHashMap.size());
                for (Map.Entry entry : newLinkedHashMap.entrySet()) {
                    builder.put(entry.getKey(), ImmutableMap.copyOf((Map) entry.getValue()));
                }
                this.rowMap = builder.build();
                ImmutableMap.Builder builder2 = new ImmutableMap.Builder(newLinkedHashMap2.size());
                for (Map.Entry entry2 : newLinkedHashMap2.entrySet()) {
                    builder2.put(entry2.getKey(), ImmutableMap.copyOf((Map) entry2.getValue()));
                }
                this.columnMap = builder2.build();
                return;
            }
            Table.Cell cell = (Table.Cell) immutableList.get(i2);
            Object rowKey = cell.getRowKey();
            Object columnKey = cell.getColumnKey();
            Object value = cell.getValue();
            iArr[i2] = indexMap.get(rowKey).intValue();
            Map map = (Map) newLinkedHashMap.get(rowKey);
            iArr2[i2] = map.size();
            Object put = map.put(columnKey, value);
            if (put == null) {
                ((Map) newLinkedHashMap2.get(columnKey)).put(rowKey, value);
                i = i2 + 1;
            } else {
                throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + put);
            }
        }
    }

    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    /* access modifiers changed from: package-private */
    public Table.Cell<R, C, V> getCell(int i) {
        Map.Entry entry = (Map.Entry) this.rowMap.entrySet().asList().get(this.iterationOrderRow[i]);
        Map.Entry entry2 = (Map.Entry) ((ImmutableMap) entry.getValue()).entrySet().asList().get(this.iterationOrderColumn[i]);
        return cellOf(entry.getKey(), entry2.getKey(), entry2.getValue());
    }

    /* access modifiers changed from: package-private */
    public V getValue(int i) {
        int i2 = this.iterationOrderRow[i];
        return ((ImmutableMap) this.rowMap.values().asList().get(i2)).values().asList().get(this.iterationOrderColumn[i]);
    }

    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    public int size() {
        return this.iterationOrderRow.length;
    }
}
