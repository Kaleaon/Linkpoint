package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
@Immutable
/* loaded from: classes.dex */
public final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V> {
    private final ImmutableMap<C, Map<R, V>> columnMap;
    private final int[] iterationOrderColumn;
    private final int[] iterationOrderRow;
    private final ImmutableMap<R, Map<C, V>> rowMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SparseImmutableTable(ImmutableList<Table.Cell<R, C, V>> immutableList, ImmutableSet<R> immutableSet, ImmutableSet<C> immutableSet2) {
        int i = 0;
        ImmutableMap indexMap = Maps.indexMap(immutableSet);
        LinkedHashMap newLinkedHashMap = Maps.newLinkedHashMap();
        Iterator it = immutableSet.iterator();
        while (it.hasNext()) {
            newLinkedHashMap.put(it.next(), new LinkedHashMap());
        }
        LinkedHashMap newLinkedHashMap2 = Maps.newLinkedHashMap();
        Iterator it2 = immutableSet2.iterator();
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
            Table.Cell<R, C, V> cell = immutableList.get(i2);
            R rowKey = cell.getRowKey();
            C columnKey = cell.getColumnKey();
            V value = cell.getValue();
            iArr[i2] = ((Integer) indexMap.get(rowKey)).intValue();
            Map map = (Map) newLinkedHashMap.get(rowKey);
            iArr2[i2] = map.size();
            Object put = map.put(columnKey, value);
            if (put != null) {
                throw new IllegalArgumentException("Duplicate value for row=" + rowKey + ", column=" + columnKey + ": " + value + ", " + put);
            }
            ((Map) newLinkedHashMap2.get(columnKey)).put(rowKey, value);
            i = i2 + 1;
        }
    }

    @Override // com.google.common.collect.ImmutableTable, com.google.common.collect.Table
    public ImmutableMap<C, Map<R, V>> columnMap() {
        return this.columnMap;
    }

    @Override // com.google.common.collect.RegularImmutableTable
    Table.Cell<R, C, V> getCell(int i) {
        Map.Entry<R, Map<C, V>> entry = this.rowMap.entrySet().asList().get(this.iterationOrderRow[i]);
        Map.Entry entry2 = (Map.Entry) ((ImmutableMap) entry.getValue()).entrySet().asList().get(this.iterationOrderColumn[i]);
        return cellOf(entry.getKey(), entry2.getKey(), entry2.getValue());
    }

    @Override // com.google.common.collect.RegularImmutableTable
    V getValue(int i) {
        int i2 = this.iterationOrderRow[i];
        return ((ImmutableMap) this.rowMap.values().asList().get(i2)).values().asList().get(this.iterationOrderColumn[i]);
    }

    @Override // com.google.common.collect.ImmutableTable, com.google.common.collect.Table
    public ImmutableMap<R, Map<C, V>> rowMap() {
        return this.rowMap;
    }

    @Override // com.google.common.collect.Table
    public int size() {
        return this.iterationOrderRow.length;
    }
}
