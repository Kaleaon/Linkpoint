// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.TreeSet;
import java.util.SortedSet;
import java.util.LinkedList;
import java.util.EnumSet;
import java.util.Set;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;
import com.google.common.base.Supplier;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Collection;
import java.util.Map;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@Beta
@GwtCompatible
public abstract class MultimapBuilder<K0, V0>
{
    private static final int DEFAULT_EXPECTED_KEYS = 8;
    
    private MultimapBuilder() {
    }
    
    public static <K0 extends Enum<K0>> MultimapBuilderWithKeys<K0> enumKeys(final Class<K0> clazz) {
        Preconditions.checkNotNull(clazz);
        return (MultimapBuilderWithKeys<K0>)new MultimapBuilderWithKeys<K0>() {
            @Override
             <K extends K0, V> Map<K, Collection<V>> createMap() {
                return new EnumMap<K, Collection<V>>(clazz);
            }
        };
    }
    
    public static MultimapBuilderWithKeys<Object> hashKeys() {
        return hashKeys(8);
    }
    
    public static MultimapBuilderWithKeys<Object> hashKeys(final int n) {
        CollectPreconditions.checkNonnegative(n, "expectedKeys");
        return (MultimapBuilderWithKeys<Object>)new MultimapBuilderWithKeys<Object>() {
            @Override
             <K, V> Map<K, Collection<V>> createMap() {
                return (Map<K, Collection<V>>)Maps.newHashMapWithExpectedSize(n);
            }
        };
    }
    
    public static MultimapBuilderWithKeys<Object> linkedHashKeys() {
        return linkedHashKeys(8);
    }
    
    public static MultimapBuilderWithKeys<Object> linkedHashKeys(final int n) {
        CollectPreconditions.checkNonnegative(n, "expectedKeys");
        return (MultimapBuilderWithKeys<Object>)new MultimapBuilderWithKeys<Object>() {
            @Override
             <K, V> Map<K, Collection<V>> createMap() {
                return (Map<K, Collection<V>>)Maps.newLinkedHashMapWithExpectedSize(n);
            }
        };
    }
    
    public static MultimapBuilderWithKeys<Comparable> treeKeys() {
        return (MultimapBuilderWithKeys<Comparable>)treeKeys(Ordering.natural());
    }
    
    public static <K0> MultimapBuilderWithKeys<K0> treeKeys(final Comparator<K0> comparator) {
        Preconditions.checkNotNull(comparator);
        return (MultimapBuilderWithKeys<K0>)new MultimapBuilderWithKeys<K0>() {
            @Override
             <K extends K0, V> Map<K, Collection<V>> createMap() {
                return new TreeMap<K, Collection<V>>(comparator);
            }
        };
    }
    
    public abstract <K extends K0, V extends V0> Multimap<K, V> build();
    
    public <K extends K0, V extends V0> Multimap<K, V> build(final Multimap<? extends K, ? extends V> multimap) {
        final Multimap<Object, Object> build = this.build();
        build.putAll(multimap);
        return (Multimap<K, V>)build;
    }
    
    private static final class ArrayListSupplier<V> implements Supplier<List<V>>, Serializable
    {
        private final int expectedValuesPerKey;
        
        ArrayListSupplier(final int n) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
        }
        
        @Override
        public List<V> get() {
            return new ArrayList<V>(this.expectedValuesPerKey);
        }
    }
    
    private static final class EnumSetSupplier<V extends Enum<V>> implements Supplier<Set<V>>, Serializable
    {
        private final Class<V> clazz;
        
        EnumSetSupplier(final Class<V> clazz) {
            this.clazz = Preconditions.checkNotNull(clazz);
        }
        
        @Override
        public Set<V> get() {
            return EnumSet.noneOf(this.clazz);
        }
    }
    
    private static final class HashSetSupplier<V> implements Supplier<Set<V>>, Serializable
    {
        private final int expectedValuesPerKey;
        
        HashSetSupplier(final int n) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
        }
        
        @Override
        public Set<V> get() {
            return (Set<V>)Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
        }
    }
    
    private static final class LinkedHashSetSupplier<V> implements Supplier<Set<V>>, Serializable
    {
        private final int expectedValuesPerKey;
        
        LinkedHashSetSupplier(final int n) {
            this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
        }
        
        @Override
        public Set<V> get() {
            return (Set<V>)Sets.newLinkedHashSetWithExpectedSize(this.expectedValuesPerKey);
        }
    }
    
    private enum LinkedListSupplier implements Supplier<List<Object>>
    {
        INSTANCE;
        
        public static <V> Supplier<List<V>> instance() {
            return (Supplier<List<V>>)LinkedListSupplier.INSTANCE;
        }
        
        @Override
        public List<Object> get() {
            return new LinkedList<Object>();
        }
    }
    
    public abstract static class ListMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0>
    {
        ListMultimapBuilder() {
            super(null);
        }
        
        @Override
        public abstract <K extends K0, V extends V0> ListMultimap<K, V> build();
        
        @Override
        public <K extends K0, V extends V0> ListMultimap<K, V> build(final Multimap<? extends K, ? extends V> multimap) {
            return (ListMultimap)super.build(multimap);
        }
    }
    
    public abstract static class MultimapBuilderWithKeys<K0>
    {
        private static final int DEFAULT_EXPECTED_VALUES_PER_KEY = 2;
        
        MultimapBuilderWithKeys() {
        }
        
        public ListMultimapBuilder<K0, Object> arrayListValues() {
            return this.arrayListValues(2);
        }
        
        public ListMultimapBuilder<K0, Object> arrayListValues(final int n) {
            CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
            return new ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends List<V>>)new ArrayListSupplier(n));
                }
            };
        }
        
        abstract <K extends K0, V> Map<K, Collection<V>> createMap();
        
        public <V0 extends Enum<V0>> SetMultimapBuilder<K0, V0> enumSetValues(final Class<V0> clazz) {
            Preconditions.checkNotNull(clazz, (Object)"valueClass");
            return new SetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends Set<V>>)new EnumSetSupplier(clazz));
                }
            };
        }
        
        public SetMultimapBuilder<K0, Object> hashSetValues() {
            return this.hashSetValues(2);
        }
        
        public SetMultimapBuilder<K0, Object> hashSetValues(final int n) {
            CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
            return new SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends Set<V>>)new HashSetSupplier(n));
                }
            };
        }
        
        public SetMultimapBuilder<K0, Object> linkedHashSetValues() {
            return this.linkedHashSetValues(2);
        }
        
        public SetMultimapBuilder<K0, Object> linkedHashSetValues(final int n) {
            CollectPreconditions.checkNonnegative(n, "expectedValuesPerKey");
            return new SetMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> SetMultimap<K, V> build() {
                    return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends Set<V>>)new LinkedHashSetSupplier(n));
                }
            };
        }
        
        public ListMultimapBuilder<K0, Object> linkedListValues() {
            return new ListMultimapBuilder<K0, Object>() {
                @Override
                public <K extends K0, V> ListMultimap<K, V> build() {
                    return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends List<V>>)LinkedListSupplier.instance());
                }
            };
        }
        
        public SortedSetMultimapBuilder<K0, Comparable> treeSetValues() {
            return (SortedSetMultimapBuilder<K0, Comparable>)this.treeSetValues(Ordering.natural());
        }
        
        public <V0> SortedSetMultimapBuilder<K0, V0> treeSetValues(final Comparator<V0> comparator) {
            Preconditions.checkNotNull(comparator, (Object)"comparator");
            return new SortedSetMultimapBuilder<K0, V0>() {
                @Override
                public <K extends K0, V extends V0> SortedSetMultimap<K, V> build() {
                    return Multimaps.newSortedSetMultimap(MultimapBuilderWithKeys.this.createMap(), (Supplier<? extends SortedSet<V>>)new TreeSetSupplier(comparator));
                }
            };
        }
    }
    
    public abstract static class SetMultimapBuilder<K0, V0> extends MultimapBuilder<K0, V0>
    {
        SetMultimapBuilder() {
            super(null);
        }
        
        @Override
        public abstract <K extends K0, V extends V0> SetMultimap<K, V> build();
        
        @Override
        public <K extends K0, V extends V0> SetMultimap<K, V> build(final Multimap<? extends K, ? extends V> multimap) {
            return (SetMultimap)super.build(multimap);
        }
    }
    
    public abstract static class SortedSetMultimapBuilder<K0, V0> extends SetMultimapBuilder<K0, V0>
    {
        SortedSetMultimapBuilder() {
        }
        
        @Override
        public abstract <K extends K0, V extends V0> SortedSetMultimap<K, V> build();
        
        @Override
        public <K extends K0, V extends V0> SortedSetMultimap<K, V> build(final Multimap<? extends K, ? extends V> multimap) {
            return (SortedSetMultimap)super.build(multimap);
        }
    }
    
    private static final class TreeSetSupplier<V> implements Supplier<SortedSet<V>>, Serializable
    {
        private final Comparator<? super V> comparator;
        
        TreeSetSupplier(final Comparator<? super V> comparator) {
            this.comparator = Preconditions.checkNotNull(comparator);
        }
        
        @Override
        public SortedSet<V> get() {
            return new TreeSet<V>(this.comparator);
        }
    }
}
