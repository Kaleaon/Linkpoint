package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MapMakerInternalMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

class ComputingConcurrentHashMap<K, V> extends MapMakerInternalMap<K, V> {
    private static final long serialVersionUID = 4;
    final Function<? super K, ? extends V> computingFunction;

    private static final class ComputationExceptionReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {
        final Throwable t;

        ComputationExceptionReference(Throwable th) {
            this.t = th;
        }

        public void clear(MapMakerInternalMap.ValueReference<K, V> valueReference) {
        }

        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, MapMakerInternalMap.ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return null;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return false;
        }

        public V waitForValue() throws ExecutionException {
            throw new ExecutionException(this.t);
        }
    }

    private static final class ComputedReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {
        final V value;

        ComputedReference(@Nullable V v) {
            this.value = v;
        }

        public void clear(MapMakerInternalMap.ValueReference<K, V> valueReference) {
        }

        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, MapMakerInternalMap.ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return this.value;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return false;
        }

        public V waitForValue() {
            return get();
        }
    }

    static final class ComputingSegment<K, V> extends MapMakerInternalMap.Segment<K, V> {
        ComputingSegment(MapMakerInternalMap<K, V> mapMakerInternalMap, int i, int i2) {
            super(mapMakerInternalMap, i, i2);
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
            java.lang.System.nanoTime();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
            if (r1 == null) goto L_0x0038;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
            return r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0030, code lost:
            if (put(r7, r8, r1, true) == null) goto L_0x0012;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0032, code lost:
            enqueueNotification(r7, r8, r1, com.google.common.collect.MapMaker.RemovalCause.REPLACED);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x0038, code lost:
            clearValue(r7, r8, r10);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0010, code lost:
            if (r1 != null) goto L_0x002b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
            if (r2 != 0) goto L_0x0019;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public V compute(K r7, int r8, com.google.common.collect.MapMakerInternalMap.ReferenceEntry<K, V> r9, com.google.common.collect.ComputingConcurrentHashMap.ComputingValueReference<K, V> r10) throws java.util.concurrent.ExecutionException {
            /*
                r6 = this;
                r4 = 0
                r1 = 0
                java.lang.System.nanoTime()
                monitor-enter(r9)     // Catch:{ all -> 0x0040 }
                java.lang.Object r1 = r10.compute(r7, r8)     // Catch:{ all -> 0x001c }
                long r2 = java.lang.System.nanoTime()     // Catch:{ all -> 0x001c }
                monitor-exit(r9)     // Catch:{ all -> 0x0043 }
                if (r1 != 0) goto L_0x002b
            L_0x0012:
                int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r0 != 0) goto L_0x0019
                java.lang.System.nanoTime()
            L_0x0019:
                if (r1 == 0) goto L_0x0038
            L_0x001b:
                return r1
            L_0x001c:
                r0 = move-exception
                r2 = r4
            L_0x001e:
                monitor-exit(r9)     // Catch:{ all -> 0x0043 }
                throw r0     // Catch:{ all -> 0x0020 }
            L_0x0020:
                r0 = move-exception
            L_0x0021:
                int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                if (r2 != 0) goto L_0x0028
                java.lang.System.nanoTime()
            L_0x0028:
                if (r1 == 0) goto L_0x003c
            L_0x002a:
                throw r0
            L_0x002b:
                r0 = 1
                java.lang.Object r0 = r6.put(r7, r8, r1, r0)     // Catch:{ all -> 0x0020 }
                if (r0 == 0) goto L_0x0012
                com.google.common.collect.MapMaker$RemovalCause r0 = com.google.common.collect.MapMaker.RemovalCause.REPLACED     // Catch:{ all -> 0x0020 }
                r6.enqueueNotification(r7, r8, r1, r0)     // Catch:{ all -> 0x0020 }
                goto L_0x0012
            L_0x0038:
                r6.clearValue(r7, r8, r10)
                goto L_0x001b
            L_0x003c:
                r6.clearValue(r7, r8, r10)
                goto L_0x002a
            L_0x0040:
                r0 = move-exception
                r2 = r4
                goto L_0x0021
            L_0x0043:
                r0 = move-exception
                goto L_0x001e
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ComputingConcurrentHashMap.ComputingSegment.compute(java.lang.Object, int, com.google.common.collect.MapMakerInternalMap$ReferenceEntry, com.google.common.collect.ComputingConcurrentHashMap$ComputingValueReference):java.lang.Object");
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x0090, code lost:
            if (r5.getValueReference().isComputingReference() != false) goto L_0x00b1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x0092, code lost:
            r9 = r5.getValueReference().get();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x009a, code lost:
            if (r9 == null) goto L_0x00b4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00a2, code lost:
            if (r11.map.expires() != false) goto L_0x00c8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00a4, code lost:
            recordLockedRead(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
            unlock();
            postWriteCleanup();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ad, code lost:
            postReadCleanup();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b0, code lost:
            return r9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b1, code lost:
            r6 = false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
            enqueueNotification(r6, r13, r9, com.google.common.collect.MapMaker.RemovalCause.COLLECTED);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00b9, code lost:
            r11.evictionQueue.remove(r5);
            r11.expirationQueue.remove(r5);
            r11.count = r3;
            r6 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00ce, code lost:
            if (r11.map.isExpired(r5) == false) goto L_0x00a4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00d0, code lost:
            enqueueNotification(r6, r13, r9, com.google.common.collect.MapMaker.RemovalCause.EXPIRED);
         */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x003c A[Catch:{ all -> 0x00d6, all -> 0x00de }] */
        /* JADX WARNING: Removed duplicated region for block: B:76:0x0104  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public V getOrCompute(K r12, int r13, com.google.common.base.Function<? super K, ? extends V> r14) throws java.util.concurrent.ExecutionException {
            /*
                r11 = this;
                r2 = 1
                r4 = 0
                r1 = 0
            L_0x0003:
                com.google.common.collect.MapMakerInternalMap$ReferenceEntry r0 = r11.getEntry(r12, r13)     // Catch:{ all -> 0x00de }
                if (r0 != 0) goto L_0x0054
            L_0x0009:
                if (r0 != 0) goto L_0x0061
            L_0x000b:
                r11.lock()     // Catch:{ all -> 0x00de }
                r11.preWriteCleanup()     // Catch:{ all -> 0x00d6 }
                int r0 = r11.count     // Catch:{ all -> 0x00d6 }
                int r3 = r0 + -1
                java.util.concurrent.atomic.AtomicReferenceArray r7 = r11.table     // Catch:{ all -> 0x00d6 }
                int r0 = r7.length()     // Catch:{ all -> 0x00d6 }
                int r0 = r0 + -1
                r8 = r13 & r0
                java.lang.Object r0 = r7.get(r8)     // Catch:{ all -> 0x00d6 }
                com.google.common.collect.MapMakerInternalMap$ReferenceEntry r0 = (com.google.common.collect.MapMakerInternalMap.ReferenceEntry) r0     // Catch:{ all -> 0x00d6 }
                r5 = r0
            L_0x0026:
                if (r5 != 0) goto L_0x006d
                r6 = r2
            L_0x0029:
                if (r6 != 0) goto L_0x00e3
                r3 = r4
                r0 = r5
            L_0x002d:
                r11.unlock()     // Catch:{ all -> 0x00de }
                r11.postWriteCleanup()     // Catch:{ all -> 0x00de }
                if (r6 != 0) goto L_0x00fc
                r3 = r0
            L_0x0036:
                boolean r0 = java.lang.Thread.holdsLock(r3)     // Catch:{ all -> 0x00de }
                if (r0 == 0) goto L_0x0104
                r0 = r1
            L_0x003d:
                java.lang.String r5 = "Recursive computation"
                com.google.common.base.Preconditions.checkState(r0, r5)     // Catch:{ all -> 0x00de }
                com.google.common.collect.MapMakerInternalMap$ValueReference r0 = r3.getValueReference()     // Catch:{ all -> 0x00de }
                java.lang.Object r0 = r0.waitForValue()     // Catch:{ all -> 0x00de }
                if (r0 == 0) goto L_0x0003
                r11.recordRead(r3)     // Catch:{ all -> 0x00de }
                r11.postReadCleanup()
                return r0
            L_0x0054:
                java.lang.Object r3 = r11.getLiveValue(r0)     // Catch:{ all -> 0x00de }
                if (r3 == 0) goto L_0x0009
                r11.recordRead(r0)     // Catch:{ all -> 0x00de }
                r11.postReadCleanup()
                return r3
            L_0x0061:
                com.google.common.collect.MapMakerInternalMap$ValueReference r3 = r0.getValueReference()     // Catch:{ all -> 0x00de }
                boolean r3 = r3.isComputingReference()     // Catch:{ all -> 0x00de }
                if (r3 == 0) goto L_0x000b
                r3 = r0
                goto L_0x0036
            L_0x006d:
                java.lang.Object r6 = r5.getKey()     // Catch:{ all -> 0x00d6 }
                int r9 = r5.getHash()     // Catch:{ all -> 0x00d6 }
                if (r9 == r13) goto L_0x007c
            L_0x0077:
                com.google.common.collect.MapMakerInternalMap$ReferenceEntry r5 = r5.getNext()     // Catch:{ all -> 0x00d6 }
                goto L_0x0026
            L_0x007c:
                if (r6 == 0) goto L_0x0077
                com.google.common.collect.MapMakerInternalMap r9 = r11.map     // Catch:{ all -> 0x00d6 }
                com.google.common.base.Equivalence<java.lang.Object> r9 = r9.keyEquivalence     // Catch:{ all -> 0x00d6 }
                boolean r9 = r9.equivalent(r12, r6)     // Catch:{ all -> 0x00d6 }
                if (r9 == 0) goto L_0x0077
                com.google.common.collect.MapMakerInternalMap$ValueReference r9 = r5.getValueReference()     // Catch:{ all -> 0x00d6 }
                boolean r9 = r9.isComputingReference()     // Catch:{ all -> 0x00d6 }
                if (r9 != 0) goto L_0x00b1
                com.google.common.collect.MapMakerInternalMap$ValueReference r9 = r5.getValueReference()     // Catch:{ all -> 0x00d6 }
                java.lang.Object r9 = r9.get()     // Catch:{ all -> 0x00d6 }
                if (r9 == 0) goto L_0x00b4
                com.google.common.collect.MapMakerInternalMap r10 = r11.map     // Catch:{ all -> 0x00d6 }
                boolean r10 = r10.expires()     // Catch:{ all -> 0x00d6 }
                if (r10 != 0) goto L_0x00c8
            L_0x00a4:
                r11.recordLockedRead(r5)     // Catch:{ all -> 0x00d6 }
                r11.unlock()     // Catch:{ all -> 0x00de }
                r11.postWriteCleanup()     // Catch:{ all -> 0x00de }
                r11.postReadCleanup()
                return r9
            L_0x00b1:
                r6 = r1
                goto L_0x0029
            L_0x00b4:
                com.google.common.collect.MapMaker$RemovalCause r10 = com.google.common.collect.MapMaker.RemovalCause.COLLECTED     // Catch:{ all -> 0x00d6 }
                r11.enqueueNotification(r6, r13, r9, r10)     // Catch:{ all -> 0x00d6 }
            L_0x00b9:
                java.util.Queue r6 = r11.evictionQueue     // Catch:{ all -> 0x00d6 }
                r6.remove(r5)     // Catch:{ all -> 0x00d6 }
                java.util.Queue r6 = r11.expirationQueue     // Catch:{ all -> 0x00d6 }
                r6.remove(r5)     // Catch:{ all -> 0x00d6 }
                r11.count = r3     // Catch:{ all -> 0x00d6 }
                r6 = r2
                goto L_0x0029
            L_0x00c8:
                com.google.common.collect.MapMakerInternalMap r10 = r11.map     // Catch:{ all -> 0x00d6 }
                boolean r10 = r10.isExpired(r5)     // Catch:{ all -> 0x00d6 }
                if (r10 == 0) goto L_0x00a4
                com.google.common.collect.MapMaker$RemovalCause r10 = com.google.common.collect.MapMaker.RemovalCause.EXPIRED     // Catch:{ all -> 0x00d6 }
                r11.enqueueNotification(r6, r13, r9, r10)     // Catch:{ all -> 0x00d6 }
                goto L_0x00b9
            L_0x00d6:
                r0 = move-exception
                r11.unlock()     // Catch:{ all -> 0x00de }
                r11.postWriteCleanup()     // Catch:{ all -> 0x00de }
                throw r0     // Catch:{ all -> 0x00de }
            L_0x00de:
                r0 = move-exception
                r11.postReadCleanup()
                throw r0
            L_0x00e3:
                com.google.common.collect.ComputingConcurrentHashMap$ComputingValueReference r3 = new com.google.common.collect.ComputingConcurrentHashMap$ComputingValueReference     // Catch:{ all -> 0x00d6 }
                r3.<init>(r14)     // Catch:{ all -> 0x00d6 }
                if (r5 == 0) goto L_0x00f0
                r5.setValueReference(r3)     // Catch:{ all -> 0x00d6 }
                r0 = r5
                goto L_0x002d
            L_0x00f0:
                com.google.common.collect.MapMakerInternalMap$ReferenceEntry r0 = r11.newEntry(r12, r13, r0)     // Catch:{ all -> 0x00d6 }
                r0.setValueReference(r3)     // Catch:{ all -> 0x00d6 }
                r7.set(r8, r0)     // Catch:{ all -> 0x00d6 }
                goto L_0x002d
            L_0x00fc:
                java.lang.Object r0 = r11.compute(r12, r13, r0, r3)     // Catch:{ all -> 0x00de }
                r11.postReadCleanup()
                return r0
            L_0x0104:
                r0 = r2
                goto L_0x003d
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ComputingConcurrentHashMap.ComputingSegment.getOrCompute(java.lang.Object, int, com.google.common.base.Function):java.lang.Object");
        }
    }

    static final class ComputingSerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V> {
        private static final long serialVersionUID = 4;
        final Function<? super K, ? extends V> computingFunction;

        ComputingSerializationProxy(MapMakerInternalMap.Strength strength, MapMakerInternalMap.Strength strength2, Equivalence<Object> equivalence, Equivalence<Object> equivalence2, long j, long j2, int i, int i2, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> concurrentMap, Function<? super K, ? extends V> function) {
            super(strength, strength2, equivalence, equivalence2, j, j2, i, i2, removalListener, concurrentMap);
            this.computingFunction = function;
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = readMapMaker(objectInputStream).makeComputingMap(this.computingFunction);
            readEntries(objectInputStream);
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            writeMapTo(objectOutputStream);
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
            return this.delegate;
        }
    }

    private static final class ComputingValueReference<K, V> implements MapMakerInternalMap.ValueReference<K, V> {
        @GuardedBy("this")
        volatile MapMakerInternalMap.ValueReference<K, V> computedReference = MapMakerInternalMap.unset();
        final Function<? super K, ? extends V> computingFunction;

        public ComputingValueReference(Function<? super K, ? extends V> function) {
            this.computingFunction = function;
        }

        public void clear(MapMakerInternalMap.ValueReference<K, V> valueReference) {
            setValueReference(valueReference);
        }

        /* access modifiers changed from: package-private */
        public V compute(K k, int i) throws ExecutionException {
            try {
                V apply = this.computingFunction.apply(k);
                setValueReference(new ComputedReference(apply));
                return apply;
            } catch (Throwable th) {
                setValueReference(new ComputationExceptionReference(th));
                throw new ExecutionException(th);
            }
        }

        public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, @Nullable V v, MapMakerInternalMap.ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return null;
        }

        public MapMakerInternalMap.ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return true;
        }

        /* access modifiers changed from: package-private */
        public void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference) {
            synchronized (this) {
                if (this.computedReference == MapMakerInternalMap.UNSET) {
                    this.computedReference = valueReference;
                    notifyAll();
                }
            }
        }

        public V waitForValue() throws ExecutionException {
            boolean z = false;
            if (this.computedReference == MapMakerInternalMap.UNSET) {
                try {
                    synchronized (this) {
                        while (this.computedReference == MapMakerInternalMap.UNSET) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                z = true;
                            }
                        }
                    }
                    if (z) {
                        Thread.currentThread().interrupt();
                    }
                } catch (Throwable th) {
                    if (z) {
                        Thread.currentThread().interrupt();
                    }
                    throw th;
                }
            }
            return this.computedReference.waitForValue();
        }
    }

    ComputingConcurrentHashMap(MapMaker mapMaker, Function<? super K, ? extends V> function) {
        super(mapMaker);
        this.computingFunction = (Function) Preconditions.checkNotNull(function);
    }

    /* access modifiers changed from: package-private */
    public MapMakerInternalMap.Segment<K, V> createSegment(int i, int i2) {
        return new ComputingSegment(this, i, i2);
    }

    /* access modifiers changed from: package-private */
    public V getOrCompute(K k) throws ExecutionException {
        int hash = hash(Preconditions.checkNotNull(k));
        return segmentFor(hash).getOrCompute(k, hash, this.computingFunction);
    }

    /* access modifiers changed from: package-private */
    public ComputingSegment<K, V> segmentFor(int i) {
        return (ComputingSegment) super.segmentFor(i);
    }

    /* access modifiers changed from: package-private */
    public Object writeReplace() {
        return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
    }
}
