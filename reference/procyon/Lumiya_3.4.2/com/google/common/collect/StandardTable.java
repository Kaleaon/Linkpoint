// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Predicate;
import java.util.Collection;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import com.google.common.base.Supplier;
import java.util.Set;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
class StandardTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable
{
    private static final long serialVersionUID = 0L;
    @GwtTransient
    final Map<R, Map<C, V>> backingMap;
    private transient Set<C> columnKeySet;
    private transient ColumnMap columnMap;
    @GwtTransient
    final Supplier<? extends Map<C, V>> factory;
    private transient Map<R, Map<C, V>> rowMap;
    
    StandardTable(final Map<R, Map<C, V>> backingMap, final Supplier<? extends Map<C, V>> factory) {
        this.backingMap = backingMap;
        this.factory = factory;
    }
    
    private boolean containsMapping(final Object o, final Object o2, final Object o3) {
        boolean b = false;
        if (o3 != null && o3.equals(this.get(o, o2))) {
            b = true;
        }
        return b;
    }
    
    private Map<C, V> getOrCreate(final R r) {
        final Map map = this.backingMap.get(r);
        Map map2;
        if (map != null) {
            map2 = map;
        }
        else {
            final Map map3 = (Map)this.factory.get();
            this.backingMap.put(r, map3);
            map2 = map3;
        }
        return map2;
    }
    
    private Map<R, V> removeColumn(final Object o) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        final Iterator<Map.Entry<R, Map<C, V>>> iterator = this.backingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<K, Map> entry = (Map.Entry<K, Map>)iterator.next();
            final Object remove = entry.getValue().remove(o);
            if (remove != null) {
                linkedHashMap.put(entry.getKey(), remove);
                if (!entry.getValue().isEmpty()) {
                    continue;
                }
                iterator.remove();
            }
        }
        return linkedHashMap;
    }
    
    private boolean removeMapping(final Object o, final Object o2, final Object o3) {
        if (!this.containsMapping(o, o2, o3)) {
            return false;
        }
        this.remove(o, o2);
        return true;
    }
    
    @Override
    Iterator<Cell<R, C, V>> cellIterator() {
        return new CellIterator();
    }
    
    @Override
    public Set<Cell<R, C, V>> cellSet() {
        return super.cellSet();
    }
    
    @Override
    public void clear() {
        this.backingMap.clear();
    }
    
    @Override
    public Map<R, V> column(final C c) {
        return new Column(c);
    }
    
    @Override
    public Set<C> columnKeySet() {
        Set<C> columnKeySet = this.columnKeySet;
        if (columnKeySet == null) {
            columnKeySet = new ColumnKeySet();
            this.columnKeySet = columnKeySet;
        }
        return columnKeySet;
    }
    
    @Override
    public Map<C, Map<R, V>> columnMap() {
        ColumnMap columnMap = this.columnMap;
        if (columnMap == null) {
            columnMap = new ColumnMap();
            this.columnMap = columnMap;
        }
        return columnMap;
    }
    
    @Override
    public boolean contains(@Nullable final Object o, @Nullable final Object o2) {
        final boolean b = false;
        boolean b2;
        if (o == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (o2 != null) {
                b2 = b;
                if (super.contains(o, o2)) {
                    b2 = true;
                }
            }
        }
        return b2;
    }
    
    @Override
    public boolean containsColumn(@Nullable final Object o) {
        if (o != null) {
            final Iterator<Map<C, V>> iterator = this.backingMap.values().iterator();
            while (iterator.hasNext()) {
                if (Maps.safeContainsKey(iterator.next(), o)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public boolean containsRow(@Nullable final Object o) {
        boolean b = false;
        if (o != null && Maps.safeContainsKey(this.backingMap, o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        boolean b = false;
        if (o != null && super.containsValue(o)) {
            b = true;
        }
        return b;
    }
    
    Iterator<C> createColumnKeyIterator() {
        return new ColumnKeyIterator();
    }
    
    Map<R, Map<C, V>> createRowMap() {
        return new RowMap();
    }
    
    @Override
    public V get(@Nullable final Object o, @Nullable final Object o2) {
        V value = null;
        if (o != null && o2 != null) {
            value = super.get(o, o2);
        }
        return value;
    }
    
    @Override
    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }
    
    @Override
    public V put(final R r, final C c, final V v) {
        Preconditions.checkNotNull(r);
        Preconditions.checkNotNull(c);
        Preconditions.checkNotNull(v);
        return this.getOrCreate(r).put(c, v);
    }
    
    @Override
    public V remove(@Nullable final Object o, @Nullable Object remove) {
        if (o == null || remove == null) {
            return null;
        }
        final Map<C, V> map = Maps.safeGet(this.backingMap, o);
        if (map != null) {
            remove = map.remove(remove);
            if (map.isEmpty()) {
                this.backingMap.remove(o);
            }
            return (V)remove;
        }
        return null;
    }
    
    @Override
    public Map<C, V> row(final R r) {
        return new Row(r);
    }
    
    @Override
    public Set<R> rowKeySet() {
        return this.rowMap().keySet();
    }
    
    @Override
    public Map<R, Map<C, V>> rowMap() {
        Map<R, Map<C, V>> rowMap = this.rowMap;
        if (rowMap == null) {
            rowMap = this.createRowMap();
            this.rowMap = rowMap;
        }
        return rowMap;
    }
    
    @Override
    public int size() {
        final Iterator<Map<C, V>> iterator = this.backingMap.values().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            n += iterator.next().size();
        }
        return n;
    }
    
    @Override
    public Collection<V> values() {
        return super.values();
    }
    
    private class CellIterator implements Iterator<Cell<R, C, V>>
    {
        Iterator<Map.Entry<C, V>> columnIterator;
        Map.Entry<R, Map<C, V>> rowEntry;
        final Iterator<Map.Entry<R, Map<C, V>>> rowIterator;
        
        private CellIterator() {
            this.rowIterator = StandardTable.this.backingMap.entrySet().iterator();
            this.columnIterator = Iterators.emptyModifiableIterator();
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.rowIterator.hasNext() || this.columnIterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Cell<R, C, V> next() {
            if (!this.columnIterator.hasNext()) {
                this.rowEntry = this.rowIterator.next();
                this.columnIterator = this.rowEntry.getValue().entrySet().iterator();
            }
            final Map.Entry entry = this.columnIterator.next();
            return Tables.immutableCell(this.rowEntry.getKey(), (C)entry.getKey(), entry.getValue());
        }
        
        @Override
        public void remove() {
            this.columnIterator.remove();
            if (this.rowEntry.getValue().isEmpty()) {
                this.rowIterator.remove();
            }
        }
    }
    
    private class Column extends ViewCachingAbstractMap<R, V>
    {
        final C columnKey;
        
        Column(final C c) {
            this.columnKey = Preconditions.checkNotNull(c);
        }
        
        @Override
        public boolean containsKey(final Object o) {
            return StandardTable.this.contains(o, this.columnKey);
        }
        
        @Override
        Set<Entry<R, V>> createEntrySet() {
            return new EntrySet();
        }
        
        @Override
        Set<R> createKeySet() {
            return (Set<R>)new KeySet();
        }
        
        @Override
        Collection<V> createValues() {
            return (Collection<V>)new Values();
        }
        
        @Override
        public V get(final Object o) {
            return StandardTable.this.get(o, this.columnKey);
        }
        
        @Override
        public V put(final R r, final V v) {
            return StandardTable.this.put(r, this.columnKey, v);
        }
        
        @Override
        public V remove(final Object o) {
            return StandardTable.this.remove(o, this.columnKey);
        }
        
        boolean removeFromColumnIf(final Predicate<? super Entry<R, V>> predicate) {
            final Iterator<Map.Entry<R, Map<C, V>>> iterator = StandardTable.this.backingMap.entrySet().iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                final Map.Entry<K, Map> entry = (Map.Entry<K, Map>)iterator.next();
                final Map map = entry.getValue();
                final Object value = map.get(this.columnKey);
                if (value != null && predicate.apply((Object)Maps.immutableEntry(entry.getKey(), value))) {
                    map.remove(this.columnKey);
                    if (!map.isEmpty()) {
                        b = true;
                    }
                    else {
                        iterator.remove();
                        b = true;
                    }
                }
            }
            return b;
        }
        
        private class EntrySet extends ImprovedAbstractSet<Entry<R, V>>
        {
            @Override
            public void clear() {
                Column.this.removeFromColumnIf(Predicates.alwaysTrue());
            }
            
            @Override
            public boolean contains(final Object o) {
                if (!(o instanceof Entry)) {
                    return false;
                }
                final Entry entry = (Entry)o;
                return StandardTable.this.containsMapping(entry.getKey(), Column.this.columnKey, entry.getValue());
            }
            
            @Override
            public boolean isEmpty() {
                boolean b = false;
                if (!StandardTable.this.containsColumn(Column.this.columnKey)) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public Iterator<Entry<R, V>> iterator() {
                return new EntrySetIterator();
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!(o instanceof Entry)) {
                    return false;
                }
                final Entry entry = (Entry)o;
                return StandardTable.this.removeMapping(entry.getKey(), Column.this.columnKey, entry.getValue());
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return Column.this.removeFromColumnIf(Predicates.not(Predicates.in((Collection<? extends Entry<R, V>>)collection)));
            }
            
            @Override
            public int size() {
                final Iterator<Map<C, V>> iterator = StandardTable.this.backingMap.values().iterator();
                int n = 0;
                while (iterator.hasNext()) {
                    if (!iterator.next().containsKey(Column.this.columnKey)) {
                        continue;
                    }
                    ++n;
                }
                return n;
            }
        }
        
        private class EntrySetIterator extends AbstractIterator<Entry<R, V>>
        {
            final Iterator<Entry<R, Map<C, V>>> iterator;
            
            private EntrySetIterator() {
                this.iterator = StandardTable.this.backingMap.entrySet().iterator();
            }
            
            @Override
            protected Entry<R, V> computeNext() {
                while (this.iterator.hasNext()) {
                    final Entry entry = this.iterator.next();
                    if (entry.getValue().containsKey(Column.this.columnKey)) {
                        return new EntryImpl();
                    }
                }
                return (Entry<R, V>)((AbstractIterator<Map.Entry>)this).endOfData();
            }
            
            class EntryImpl extends AbstractMapEntry<R, V>
            {
                final /* synthetic */ Entry val$entry;
                
                EntryImpl(final Entry val$entry) {
                    this.val$entry = val$entry;
                }
                
                @Override
                public R getKey() {
                    return this.val$entry.getKey();
                }
                
                @Override
                public V getValue() {
                    return this.val$entry.getValue().get(Column.this.columnKey);
                }
                
                @Override
                public V setValue(final V v) {
                    return this.val$entry.getValue().put(Column.this.columnKey, Preconditions.checkNotNull(v));
                }
            }
        }
        
        private class KeySet extends Maps.KeySet<R, V>
        {
            KeySet() {
                super(Column.this);
            }
            
            @Override
            public boolean contains(final Object o) {
                return StandardTable.this.contains(o, Column.this.columnKey);
            }
            
            @Override
            public boolean remove(final Object o) {
                return StandardTable.this.remove(o, Column.this.columnKey) != null;
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return Column.this.removeFromColumnIf((Predicate<? super Entry<R, V>>)Maps.keyPredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends K>)collection))));
            }
        }
        
        private class Values extends Maps.Values<R, V>
        {
            Values() {
                super(Column.this);
            }
            
            @Override
            public boolean remove(final Object o) {
                boolean b = false;
                if (o != null && Column.this.removeFromColumnIf((Predicate<? super Entry<R, V>>)Maps.valuePredicateOnEntries(Predicates.equalTo(o)))) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return Column.this.removeFromColumnIf((Predicate<? super Entry<R, V>>)Maps.valuePredicateOnEntries(Predicates.in(collection)));
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return Column.this.removeFromColumnIf((Predicate<? super Entry<R, V>>)Maps.valuePredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends V>)collection))));
            }
        }
    }
    
    private class ColumnKeyIterator extends AbstractIterator<C>
    {
        Iterator<Map.Entry<C, V>> entryIterator;
        final Iterator<Map<C, V>> mapIterator;
        final Map<C, V> seen;
        
        private ColumnKeyIterator() {
            this.seen = (Map)StandardTable.this.factory.get();
            this.mapIterator = StandardTable.this.backingMap.values().iterator();
            this.entryIterator = (Iterator<Map.Entry<C, V>>)Iterators.emptyIterator();
        }
        
        @Override
        protected C computeNext() {
            while (true) {
                if (!this.entryIterator.hasNext()) {
                    if (!this.mapIterator.hasNext()) {
                        return this.endOfData();
                    }
                    this.entryIterator = this.mapIterator.next().entrySet().iterator();
                }
                else {
                    final Map.Entry entry = this.entryIterator.next();
                    if (!this.seen.containsKey(entry.getKey())) {
                        this.seen.put((C)entry.getKey(), (V)entry.getValue());
                        return (C)entry.getKey();
                    }
                    continue;
                }
            }
        }
    }
    
    private class ColumnKeySet extends TableSet<C>
    {
        @Override
        public boolean contains(final Object o) {
            return StandardTable.this.containsColumn(o);
        }
        
        @Override
        public Iterator<C> iterator() {
            return StandardTable.this.createColumnKeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o != null) {
                final Iterator<Map<C, V>> iterator = StandardTable.this.backingMap.values().iterator();
                boolean b = false;
                while (iterator.hasNext()) {
                    final Map map = iterator.next();
                    if (!map.keySet().remove(o)) {
                        continue;
                    }
                    if (!map.isEmpty()) {
                        b = true;
                    }
                    else {
                        iterator.remove();
                        b = true;
                    }
                }
                return b;
            }
            return false;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            final Iterator<Map<C, V>> iterator = StandardTable.this.backingMap.values().iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                final Map map = iterator.next();
                if (!Iterators.removeAll(map.keySet().iterator(), collection)) {
                    continue;
                }
                if (!map.isEmpty()) {
                    b = true;
                }
                else {
                    iterator.remove();
                    b = true;
                }
            }
            return b;
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            final Iterator<Map<C, V>> iterator = StandardTable.this.backingMap.values().iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                final Map map = iterator.next();
                if (!map.keySet().retainAll(collection)) {
                    continue;
                }
                if (!map.isEmpty()) {
                    b = true;
                }
                else {
                    iterator.remove();
                    b = true;
                }
            }
            return b;
        }
        
        @Override
        public int size() {
            return Iterators.size(this.iterator());
        }
    }
    
    private abstract class TableSet<T> extends ImprovedAbstractSet<T>
    {
        @Override
        public void clear() {
            StandardTable.this.backingMap.clear();
        }
        
        @Override
        public boolean isEmpty() {
            return StandardTable.this.backingMap.isEmpty();
        }
    }
    
    private class ColumnMap extends ViewCachingAbstractMap<C, Map<R, V>>
    {
        @Override
        public boolean containsKey(final Object o) {
            return StandardTable.this.containsColumn(o);
        }
        
        public Set<Entry<C, Map<R, V>>> createEntrySet() {
            return new ColumnMapEntrySet();
        }
        
        @Override
        Collection<Map<R, V>> createValues() {
            return (Collection<Map<R, V>>)new ColumnMapValues();
        }
        
        @Override
        public Map<R, V> get(final Object o) {
            Map<R, V> column;
            if (!StandardTable.this.containsColumn(o)) {
                column = null;
            }
            else {
                column = StandardTable.this.column(o);
            }
            return column;
        }
        
        @Override
        public Set<C> keySet() {
            return StandardTable.this.columnKeySet();
        }
        
        @Override
        public Map<R, V> remove(final Object o) {
            Map<R, V> access$900;
            if (!StandardTable.this.containsColumn(o)) {
                access$900 = null;
            }
            else {
                access$900 = StandardTable.this.removeColumn(o);
            }
            return access$900;
        }
        
        class ColumnMapEntrySet extends TableSet<Entry<C, Map<R, V>>>
        {
            @Override
            public boolean contains(Object key) {
                if (key instanceof Entry) {
                    final Entry entry = (Entry)key;
                    if (StandardTable.this.containsColumn(entry.getKey())) {
                        key = entry.getKey();
                        return ColumnMap.this.get(key).equals(entry.getValue());
                    }
                }
                return false;
            }
            
            @Override
            public Iterator<Entry<C, Map<R, V>>> iterator() {
                return Maps.asMapEntryIterator(StandardTable.this.columnKeySet(), (Function<? super C, Map<R, V>>)new Function<C, Map<R, V>>() {
                    @Override
                    public Map<R, V> apply(final C c) {
                        return StandardTable.this.column(c);
                    }
                });
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!this.contains(o)) {
                    return false;
                }
                StandardTable.this.removeColumn(((Entry)o).getKey());
                return true;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                Preconditions.checkNotNull(collection);
                return Sets.removeAllImpl(this, collection.iterator());
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                boolean b = false;
                Preconditions.checkNotNull(collection);
                for (final C next : Lists.newArrayList((Iterator<? extends C>)StandardTable.this.columnKeySet().iterator())) {
                    if (!collection.contains(Maps.immutableEntry(next, StandardTable.this.column(next)))) {
                        StandardTable.this.removeColumn(next);
                        b = true;
                    }
                }
                return b;
            }
            
            @Override
            public int size() {
                return StandardTable.this.columnKeySet().size();
            }
        }
        
        private class ColumnMapValues extends Maps.Values<C, Map<R, V>>
        {
            ColumnMapValues() {
                super(ColumnMap.this);
            }
            
            @Override
            public boolean remove(final Object o) {
                for (final Map.Entry<K, Map> entry : ((Maps.ViewCachingAbstractMap<C, Map<R, V>>)ColumnMap.this).entrySet()) {
                    if (entry.getValue().equals(o)) {
                        StandardTable.this.removeColumn(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                boolean b = false;
                Preconditions.checkNotNull(collection);
                for (final C next : Lists.newArrayList((Iterator<? extends C>)StandardTable.this.columnKeySet().iterator())) {
                    if (collection.contains(StandardTable.this.column(next))) {
                        StandardTable.this.removeColumn(next);
                        b = true;
                    }
                }
                return b;
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                boolean b = false;
                Preconditions.checkNotNull(collection);
                for (final C next : Lists.newArrayList((Iterator<? extends C>)StandardTable.this.columnKeySet().iterator())) {
                    if (!collection.contains(StandardTable.this.column(next))) {
                        StandardTable.this.removeColumn(next);
                        b = true;
                    }
                }
                return b;
            }
        }
    }
    
    class Row extends IteratorBasedAbstractMap<C, V>
    {
        Map<C, V> backingRowMap;
        final R rowKey;
        
        Row(final R r) {
            this.rowKey = Preconditions.checkNotNull(r);
        }
        
        Map<C, V> backingRowMap() {
            Map<C, V> backingRowMap;
            if (this.backingRowMap != null && (this.backingRowMap.isEmpty() && StandardTable.this.backingMap.containsKey(this.rowKey))) {
                backingRowMap = this.backingRowMap;
            }
            else {
                backingRowMap = this.computeBackingRowMap();
                this.backingRowMap = backingRowMap;
            }
            return backingRowMap;
        }
        
        @Override
        public void clear() {
            final Map<C, V> backingRowMap = this.backingRowMap();
            if (backingRowMap != null) {
                backingRowMap.clear();
            }
            this.maintainEmptyInvariant();
        }
        
        Map<C, V> computeBackingRowMap() {
            return StandardTable.this.backingMap.get(this.rowKey);
        }
        
        @Override
        public boolean containsKey(final Object o) {
            final boolean b = false;
            final Map<C, V> backingRowMap = this.backingRowMap();
            boolean b2;
            if (o == null) {
                b2 = b;
            }
            else {
                b2 = b;
                if (backingRowMap != null) {
                    b2 = b;
                    if (Maps.safeContainsKey(backingRowMap, o)) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        
        @Override
        Iterator<Entry<C, V>> entryIterator() {
            final Map<C, V> backingRowMap = this.backingRowMap();
            if (backingRowMap != null) {
                return new Iterator<Entry<C, V>>() {
                    final /* synthetic */ Iterator val$iterator = backingRowMap.entrySet().iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.val$iterator.hasNext();
                    }
                    
                    @Override
                    public Entry<C, V> next() {
                        return new ForwardingMapEntry<C, V>() {
                            final /* synthetic */ Entry val$entry = (Entry)StandardTable$Row$1.this.val$iterator.next();
                            
                            @Override
                            protected Entry<C, V> delegate() {
                                return this.val$entry;
                            }
                            
                            @Override
                            public boolean equals(final Object o) {
                                return this.standardEquals(o);
                            }
                            
                            @Override
                            public V setValue(final V v) {
                                return super.setValue(Preconditions.checkNotNull(v));
                            }
                        };
                    }
                    
                    @Override
                    public void remove() {
                        this.val$iterator.remove();
                        Row.this.maintainEmptyInvariant();
                    }
                };
            }
            return Iterators.emptyModifiableIterator();
        }
        
        @Override
        public V get(final Object o) {
            V safeGet = null;
            final Map<?, V> backingRowMap = this.backingRowMap();
            if (o != null && backingRowMap != null) {
                safeGet = Maps.safeGet(backingRowMap, o);
            }
            return safeGet;
        }
        
        void maintainEmptyInvariant() {
            if (this.backingRowMap() != null && this.backingRowMap.isEmpty()) {
                StandardTable.this.backingMap.remove(this.rowKey);
                this.backingRowMap = null;
            }
        }
        
        @Override
        public V put(final C c, final V v) {
            Preconditions.checkNotNull(c);
            Preconditions.checkNotNull(v);
            if (this.backingRowMap != null && !this.backingRowMap.isEmpty()) {
                return this.backingRowMap.put(c, v);
            }
            return StandardTable.this.put(this.rowKey, c, v);
        }
        
        @Override
        public V remove(Object safeRemove) {
            final Map<C, V> backingRowMap = (Map<C, V>)this.backingRowMap();
            if (backingRowMap != null) {
                safeRemove = Maps.safeRemove(backingRowMap, safeRemove);
                this.maintainEmptyInvariant();
                return (V)safeRemove;
            }
            return null;
        }
        
        @Override
        public int size() {
            final Map<C, V> backingRowMap = this.backingRowMap();
            int size;
            if (backingRowMap != null) {
                size = backingRowMap.size();
            }
            else {
                size = 0;
            }
            return size;
        }
    }
    
    class RowMap extends ViewCachingAbstractMap<R, Map<C, V>>
    {
        @Override
        public boolean containsKey(final Object o) {
            return StandardTable.this.containsRow(o);
        }
        
        protected Set<Entry<R, Map<C, V>>> createEntrySet() {
            return new EntrySet();
        }
        
        @Override
        public Map<C, V> get(final Object o) {
            Map<C, V> row;
            if (!StandardTable.this.containsRow(o)) {
                row = null;
            }
            else {
                row = StandardTable.this.row(o);
            }
            return row;
        }
        
        @Override
        public Map<C, V> remove(final Object o) {
            Map<C, V> map = null;
            if (o != null) {
                map = StandardTable.this.backingMap.remove(o);
            }
            return map;
        }
        
        class EntrySet extends TableSet<Entry<R, Map<C, V>>>
        {
            @Override
            public boolean contains(final Object o) {
                final boolean b = false;
                if (!(o instanceof Entry)) {
                    return false;
                }
                final Entry entry = (Entry)o;
                boolean b2;
                if (entry.getKey() == null) {
                    b2 = b;
                }
                else {
                    b2 = b;
                    if (entry.getValue() instanceof Map) {
                        b2 = b;
                        if (Collections2.safeContains(StandardTable.this.backingMap.entrySet(), entry)) {
                            b2 = true;
                        }
                    }
                }
                return b2;
            }
            
            @Override
            public Iterator<Entry<R, Map<C, V>>> iterator() {
                return Maps.asMapEntryIterator(StandardTable.this.backingMap.keySet(), (Function<? super R, Map<C, V>>)new Function<R, Map<C, V>>() {
                    @Override
                    public Map<C, V> apply(final R r) {
                        return StandardTable.this.row(r);
                    }
                });
            }
            
            @Override
            public boolean remove(final Object o) {
                final boolean b = false;
                if (!(o instanceof Entry)) {
                    return false;
                }
                final Entry entry = (Entry)o;
                boolean b2;
                if (entry.getKey() == null) {
                    b2 = b;
                }
                else {
                    b2 = b;
                    if (entry.getValue() instanceof Map) {
                        b2 = b;
                        if (StandardTable.this.backingMap.entrySet().remove(entry)) {
                            b2 = true;
                        }
                    }
                }
                return b2;
            }
            
            @Override
            public int size() {
                return StandardTable.this.backingMap.size();
            }
        }
    }
}
