package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multiset;
import com.google.common.collect.Serialization;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public final class ConcurrentHashMultiset<E> extends AbstractMultiset<E> implements Serializable {
    private static final long serialVersionUID = 1;
    /* access modifiers changed from: private */
    public final transient ConcurrentMap<E, AtomicInteger> countMap;

    private class EntrySet extends AbstractMultiset<E>.EntrySet {
        private EntrySet() {
            super();
        }

        private List<Multiset.Entry<E>> snapshot() {
            ArrayList newArrayListWithExpectedSize = Lists.newArrayListWithExpectedSize(size());
            Iterators.addAll(newArrayListWithExpectedSize, iterator());
            return newArrayListWithExpectedSize;
        }

        /* access modifiers changed from: package-private */
        public ConcurrentHashMultiset<E> multiset() {
            return ConcurrentHashMultiset.this;
        }

        public Object[] toArray() {
            return snapshot().toArray();
        }

        public <T> T[] toArray(T[] tArr) {
            return snapshot().toArray(tArr);
        }
    }

    private static class FieldSettersHolder {
        static final Serialization.FieldSetter<ConcurrentHashMultiset> COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");

        private FieldSettersHolder() {
        }
    }

    @VisibleForTesting
    ConcurrentHashMultiset(ConcurrentMap<E, AtomicInteger> concurrentMap) {
        Preconditions.checkArgument(concurrentMap.isEmpty());
        this.countMap = concurrentMap;
    }

    public static <E> ConcurrentHashMultiset<E> create() {
        return new ConcurrentHashMultiset<>(new ConcurrentHashMap());
    }

    @Beta
    public static <E> ConcurrentHashMultiset<E> create(MapMaker mapMaker) {
        return new ConcurrentHashMultiset<>(mapMaker.makeMap());
    }

    public static <E> ConcurrentHashMultiset<E> create(Iterable<? extends E> iterable) {
        ConcurrentHashMultiset<E> create = create();
        Iterables.addAll(create, iterable);
        return create;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, (Object) (ConcurrentMap) objectInputStream.readObject());
    }

    private List<E> snapshot() {
        ArrayList newArrayListWithExpectedSize = Lists.newArrayListWithExpectedSize(size());
        for (Multiset.Entry entry : entrySet()) {
            Object element = entry.getElement();
            for (int count = entry.getCount(); count > 0; count--) {
                newArrayListWithExpectedSize.add(element);
            }
        }
        return newArrayListWithExpectedSize;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.countMap);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001c, code lost:
        r1 = new java.util.concurrent.atomic.AtomicInteger(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        if (r4.countMap.putIfAbsent(r5, r1) != null) goto L_0x0070;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int add(E r5, int r6) {
        /*
            r4 = this;
            r3 = 0
            com.google.common.base.Preconditions.checkNotNull(r5)
            if (r6 == 0) goto L_0x002a
            java.lang.String r0 = "occurences"
            com.google.common.collect.CollectPreconditions.checkPositive(r6, r0)
        L_0x000c:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r0 = r4.countMap
            java.lang.Object r0 = com.google.common.collect.Maps.safeGet(r0, r5)
            java.util.concurrent.atomic.AtomicInteger r0 = (java.util.concurrent.atomic.AtomicInteger) r0
            if (r0 == 0) goto L_0x002f
        L_0x0016:
            int r1 = r0.get()
            if (r1 != 0) goto L_0x003f
            java.util.concurrent.atomic.AtomicInteger r1 = new java.util.concurrent.atomic.AtomicInteger
            r1.<init>(r6)
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r2 = r4.countMap
            java.lang.Object r2 = r2.putIfAbsent(r5, r1)
            if (r2 != 0) goto L_0x0070
        L_0x0029:
            return r3
        L_0x002a:
            int r0 = r4.count(r5)
            return r0
        L_0x002f:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r0 = r4.countMap
            java.util.concurrent.atomic.AtomicInteger r1 = new java.util.concurrent.atomic.AtomicInteger
            r1.<init>(r6)
            java.lang.Object r0 = r0.putIfAbsent(r5, r1)
            java.util.concurrent.atomic.AtomicInteger r0 = (java.util.concurrent.atomic.AtomicInteger) r0
            if (r0 != 0) goto L_0x0016
            return r3
        L_0x003f:
            int r2 = com.google.common.math.IntMath.checkedAdd(r1, r6)     // Catch:{ ArithmeticException -> 0x004a }
            boolean r2 = r0.compareAndSet(r1, r2)     // Catch:{ ArithmeticException -> 0x004a }
            if (r2 == 0) goto L_0x0016
            return r1
        L_0x004a:
            r0 = move-exception
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Overflow adding "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r2 = r2.append(r6)
            java.lang.String r3 = " occurrences to a count of "
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.StringBuilder r1 = r2.append(r1)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0070:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r2 = r4.countMap
            boolean r0 = r2.replace(r5, r0, r1)
            if (r0 != 0) goto L_0x0029
            goto L_0x000c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ConcurrentHashMultiset.add(java.lang.Object, int):int");
    }

    public /* bridge */ /* synthetic */ boolean add(Object obj) {
        return super.add(obj);
    }

    public /* bridge */ /* synthetic */ boolean addAll(Collection collection) {
        return super.addAll(collection);
    }

    public void clear() {
        this.countMap.clear();
    }

    public /* bridge */ /* synthetic */ boolean contains(Object obj) {
        return super.contains(obj);
    }

    public int count(@Nullable Object obj) {
        AtomicInteger atomicInteger = (AtomicInteger) Maps.safeGet(this.countMap, obj);
        if (atomicInteger != null) {
            return atomicInteger.get();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public Set<E> createElementSet() {
        final Set keySet = this.countMap.keySet();
        return new ForwardingSet<E>() {
            public boolean contains(@Nullable Object obj) {
                return obj != null && Collections2.safeContains(keySet, obj);
            }

            public boolean containsAll(Collection<?> collection) {
                return standardContainsAll(collection);
            }

            /* access modifiers changed from: protected */
            public Set<E> delegate() {
                return keySet;
            }

            public boolean remove(Object obj) {
                return obj != null && Collections2.safeRemove(keySet, obj);
            }

            public boolean removeAll(Collection<?> collection) {
                return standardRemoveAll(collection);
            }
        };
    }

    public Set<Multiset.Entry<E>> createEntrySet() {
        return new EntrySet();
    }

    /* access modifiers changed from: package-private */
    public int distinctElements() {
        return this.countMap.size();
    }

    public /* bridge */ /* synthetic */ Set elementSet() {
        return super.elementSet();
    }

    /* access modifiers changed from: package-private */
    public Iterator<Multiset.Entry<E>> entryIterator() {
        final AnonymousClass2 r0 = new AbstractIterator<Multiset.Entry<E>>() {
            private Iterator<Map.Entry<E, AtomicInteger>> mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();

            /* access modifiers changed from: protected */
            public Multiset.Entry<E> computeNext() {
                while (this.mapEntries.hasNext()) {
                    Map.Entry next = this.mapEntries.next();
                    int i = ((AtomicInteger) next.getValue()).get();
                    if (i != 0) {
                        return Multisets.immutableEntry(next.getKey(), i);
                    }
                }
                return (Multiset.Entry) endOfData();
            }
        };
        return new ForwardingIterator<Multiset.Entry<E>>() {
            private Multiset.Entry<E> last;

            /* access modifiers changed from: protected */
            public Iterator<Multiset.Entry<E>> delegate() {
                return r0;
            }

            public Multiset.Entry<E> next() {
                this.last = (Multiset.Entry) super.next();
                return this.last;
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.last != null);
                ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
                this.last = null;
            }
        };
    }

    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    public boolean isEmpty() {
        return this.countMap.isEmpty();
    }

    public /* bridge */ /* synthetic */ Iterator iterator() {
        return super.iterator();
    }

    public int remove(@Nullable Object obj, int i) {
        int i2;
        int max;
        if (i == 0) {
            return count(obj);
        }
        CollectPreconditions.checkPositive(i, "occurences");
        AtomicInteger atomicInteger = (AtomicInteger) Maps.safeGet(this.countMap, obj);
        if (atomicInteger == null) {
            return 0;
        }
        do {
            i2 = atomicInteger.get();
            if (i2 == 0) {
                return 0;
            }
            max = Math.max(0, i2 - i);
        } while (!atomicInteger.compareAndSet(i2, max));
        if (max == 0) {
            this.countMap.remove(obj, atomicInteger);
        }
        return i2;
    }

    public /* bridge */ /* synthetic */ boolean remove(Object obj) {
        return super.remove(obj);
    }

    public /* bridge */ /* synthetic */ boolean removeAll(Collection collection) {
        return super.removeAll(collection);
    }

    public boolean removeExactly(@Nullable Object obj, int i) {
        int i2;
        int i3;
        if (i == 0) {
            return true;
        }
        CollectPreconditions.checkPositive(i, "occurences");
        AtomicInteger atomicInteger = (AtomicInteger) Maps.safeGet(this.countMap, obj);
        if (atomicInteger == null) {
            return false;
        }
        do {
            i2 = atomicInteger.get();
            if (i2 < i) {
                return false;
            }
            i3 = i2 - i;
        } while (!atomicInteger.compareAndSet(i2, i3));
        if (i3 == 0) {
            this.countMap.remove(obj, atomicInteger);
        }
        return true;
    }

    public /* bridge */ /* synthetic */ boolean retainAll(Collection collection) {
        return super.retainAll(collection);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
        if (r6 == 0) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0038, code lost:
        r1 = new java.util.concurrent.atomic.AtomicInteger(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0043, code lost:
        if (r4.countMap.putIfAbsent(r5, r1) != null) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0046, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setCount(E r5, int r6) {
        /*
            r4 = this;
            r3 = 0
            com.google.common.base.Preconditions.checkNotNull(r5)
            java.lang.String r0 = "count"
            com.google.common.collect.CollectPreconditions.checkNonnegative(r6, r0)
        L_0x000a:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r0 = r4.countMap
            java.lang.Object r0 = com.google.common.collect.Maps.safeGet(r0, r5)
            java.util.concurrent.atomic.AtomicInteger r0 = (java.util.concurrent.atomic.AtomicInteger) r0
            if (r0 == 0) goto L_0x0023
        L_0x0014:
            int r1 = r0.get()
            if (r1 == 0) goto L_0x0036
            boolean r2 = r0.compareAndSet(r1, r6)
            if (r2 == 0) goto L_0x0014
            if (r6 == 0) goto L_0x0050
        L_0x0022:
            return r1
        L_0x0023:
            if (r6 == 0) goto L_0x0035
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r0 = r4.countMap
            java.util.concurrent.atomic.AtomicInteger r1 = new java.util.concurrent.atomic.AtomicInteger
            r1.<init>(r6)
            java.lang.Object r0 = r0.putIfAbsent(r5, r1)
            java.util.concurrent.atomic.AtomicInteger r0 = (java.util.concurrent.atomic.AtomicInteger) r0
            if (r0 != 0) goto L_0x0014
            return r3
        L_0x0035:
            return r3
        L_0x0036:
            if (r6 == 0) goto L_0x0046
            java.util.concurrent.atomic.AtomicInteger r1 = new java.util.concurrent.atomic.AtomicInteger
            r1.<init>(r6)
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r2 = r4.countMap
            java.lang.Object r2 = r2.putIfAbsent(r5, r1)
            if (r2 != 0) goto L_0x0047
        L_0x0045:
            return r3
        L_0x0046:
            return r3
        L_0x0047:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r2 = r4.countMap
            boolean r0 = r2.replace(r5, r0, r1)
            if (r0 != 0) goto L_0x0045
            goto L_0x000a
        L_0x0050:
            java.util.concurrent.ConcurrentMap<E, java.util.concurrent.atomic.AtomicInteger> r2 = r4.countMap
            r2.remove(r5, r0)
            goto L_0x0022
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ConcurrentHashMultiset.setCount(java.lang.Object, int):int");
    }

    public boolean setCount(E e, int i, int i2) {
        Preconditions.checkNotNull(e);
        CollectPreconditions.checkNonnegative(i, "oldCount");
        CollectPreconditions.checkNonnegative(i2, "newCount");
        AtomicInteger atomicInteger = (AtomicInteger) Maps.safeGet(this.countMap, e);
        if (atomicInteger != null) {
            int i3 = atomicInteger.get();
            if (i3 == i) {
                if (i3 != 0) {
                    if (atomicInteger.compareAndSet(i3, i2)) {
                        if (i2 == 0) {
                            this.countMap.remove(e, atomicInteger);
                        }
                        return true;
                    }
                } else if (i2 != 0) {
                    AtomicInteger atomicInteger2 = new AtomicInteger(i2);
                    return this.countMap.putIfAbsent(e, atomicInteger2) == null || this.countMap.replace(e, atomicInteger, atomicInteger2);
                } else {
                    this.countMap.remove(e, atomicInteger);
                    return true;
                }
            }
            return false;
        } else if (i != 0) {
            return false;
        } else {
            if (i2 != 0) {
                return this.countMap.putIfAbsent(e, new AtomicInteger(i2)) == null;
            }
            return true;
        }
    }

    public int size() {
        long j = 0;
        Iterator it = this.countMap.values().iterator();
        while (true) {
            long j2 = j;
            if (!it.hasNext()) {
                return Ints.saturatedCast(j2);
            }
            j = ((long) ((AtomicInteger) it.next()).get()) + j2;
        }
    }

    public Object[] toArray() {
        return snapshot().toArray();
    }

    public <T> T[] toArray(T[] tArr) {
        return snapshot().toArray(tArr);
    }

    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }
}
