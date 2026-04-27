// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.concurrent.GuardedBy;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.base.Equivalence;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import com.google.common.base.Preconditions;
import com.google.common.base.Function;

class ComputingConcurrentHashMap<K, V> extends MapMakerInternalMap<K, V>
{
    private static final long serialVersionUID = 4L;
    final Function<? super K, ? extends V> computingFunction;
    
    ComputingConcurrentHashMap(final MapMaker mapMaker, final Function<? super K, ? extends V> function) {
        super(mapMaker);
        this.computingFunction = Preconditions.checkNotNull(function);
    }
    
    @Override
    Segment<K, V> createSegment(final int n, final int n2) {
        return new ComputingSegment<K, V>(this, n, n2);
    }
    
    V getOrCompute(final K k) throws ExecutionException {
        final int hash = this.hash(Preconditions.checkNotNull(k));
        return this.segmentFor(hash).getOrCompute(k, hash, this.computingFunction);
    }
    
    ComputingSegment<K, V> segmentFor(final int n) {
        return (ComputingSegment)super.segmentFor(n);
    }
    
    @Override
    Object writeReplace() {
        return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, (MapMaker.RemovalListener<? super Object, ? super Object>)this.removalListener, (ConcurrentMap<Object, Object>)this, (Function<? super Object, ?>)this.computingFunction);
    }
    
    private static final class ComputationExceptionReference<K, V> implements ValueReference<K, V>
    {
        final Throwable t;
        
        ComputationExceptionReference(final Throwable t) {
            this.t = t;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return this;
        }
        
        @Override
        public V get() {
            return null;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public boolean isComputingReference() {
            return false;
        }
        
        @Override
        public V waitForValue() throws ExecutionException {
            throw new ExecutionException(this.t);
        }
    }
    
    private static final class ComputedReference<K, V> implements ValueReference<K, V>
    {
        final V value;
        
        ComputedReference(@Nullable final V value) {
            this.value = value;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return this;
        }
        
        @Override
        public V get() {
            return this.value;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public boolean isComputingReference() {
            return false;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    static final class ComputingSegment<K, V> extends Segment<K, V>
    {
        ComputingSegment(final MapMakerInternalMap<K, V> mapMakerInternalMap, final int n, final int n2) {
            super(mapMakerInternalMap, n, n2);
        }
        
        V compute(final K p0, final int p1, final ReferenceEntry<K, V> p2, final ComputingValueReference<K, V> p3) throws ExecutionException {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: aconst_null    
            //     4: astore          6
            //     6: invokestatic    java/lang/System.nanoTime:()J
            //     9: pop2           
            //    10: aload_3        
            //    11: monitorenter   
            //    12: aload           6
            //    14: astore          5
            //    16: aload           4
            //    18: aload_1        
            //    19: iload_2        
            //    20: invokevirtual   com/google/common/collect/ComputingConcurrentHashMap$ComputingValueReference.compute:(Ljava/lang/Object;I)Ljava/lang/Object;
            //    23: astore          6
            //    25: aload           6
            //    27: astore          5
            //    29: invokestatic    java/lang/System.nanoTime:()J
            //    32: lstore          7
            //    34: aload           6
            //    36: astore          5
            //    38: lload           7
            //    40: lstore          9
            //    42: aload_3        
            //    43: monitorexit    
            //    44: aload           6
            //    46: ifnonnull       117
            //    49: lload           7
            //    51: lconst_0       
            //    52: lcmp           
            //    53: ifne            60
            //    56: invokestatic    java/lang/System.nanoTime:()J
            //    59: pop2           
            //    60: aload           6
            //    62: ifnull          157
            //    65: aload           6
            //    67: areturn        
            //    68: astore          11
            //    70: lconst_0       
            //    71: lstore          7
            //    73: aload           5
            //    75: astore          6
            //    77: aload           6
            //    79: astore          5
            //    81: lload           7
            //    83: lstore          9
            //    85: aload_3        
            //    86: monitorexit    
            //    87: aload           6
            //    89: astore_3       
            //    90: lload           7
            //    92: lstore          9
            //    94: aload           11
            //    96: athrow         
            //    97: astore          6
            //    99: lload           9
            //   101: lconst_0       
            //   102: lcmp           
            //   103: ifne            110
            //   106: invokestatic    java/lang/System.nanoTime:()J
            //   109: pop2           
            //   110: aload_3        
            //   111: ifnull          169
            //   114: aload           6
            //   116: athrow         
            //   117: aload           6
            //   119: astore_3       
            //   120: lload           7
            //   122: lstore          9
            //   124: aload_0        
            //   125: aload_1        
            //   126: iload_2        
            //   127: aload           6
            //   129: iconst_1       
            //   130: invokevirtual   com/google/common/collect/ComputingConcurrentHashMap$ComputingSegment.put:(Ljava/lang/Object;ILjava/lang/Object;Z)Ljava/lang/Object;
            //   133: ifnull          49
            //   136: aload           6
            //   138: astore_3       
            //   139: lload           7
            //   141: lstore          9
            //   143: aload_0        
            //   144: aload_1        
            //   145: iload_2        
            //   146: aload           6
            //   148: getstatic       com/google/common/collect/MapMaker$RemovalCause.REPLACED:Lcom/google/common/collect/MapMaker$RemovalCause;
            //   151: invokevirtual   com/google/common/collect/ComputingConcurrentHashMap$ComputingSegment.enqueueNotification:(Ljava/lang/Object;ILjava/lang/Object;Lcom/google/common/collect/MapMaker$RemovalCause;)V
            //   154: goto            49
            //   157: aload_0        
            //   158: aload_1        
            //   159: iload_2        
            //   160: aload           4
            //   162: invokevirtual   com/google/common/collect/ComputingConcurrentHashMap$ComputingSegment.clearValue:(Ljava/lang/Object;ILcom/google/common/collect/MapMakerInternalMap$ValueReference;)Z
            //   165: pop            
            //   166: goto            65
            //   169: aload_0        
            //   170: aload_1        
            //   171: iload_2        
            //   172: aload           4
            //   174: invokevirtual   com/google/common/collect/ComputingConcurrentHashMap$ComputingSegment.clearValue:(Ljava/lang/Object;ILcom/google/common/collect/MapMakerInternalMap$ValueReference;)Z
            //   177: pop            
            //   178: goto            114
            //   181: astore          6
            //   183: lconst_0       
            //   184: lstore          9
            //   186: aload           5
            //   188: astore_3       
            //   189: goto            99
            //   192: astore          11
            //   194: aload           5
            //   196: astore          6
            //   198: lload           9
            //   200: lstore          7
            //   202: goto            77
            //    Exceptions:
            //  throws java.util.concurrent.ExecutionException
            //    Signature:
            //  (TK;ILcom/google/common/collect/MapMakerInternalMap$ReferenceEntry<TK;TV;>;Lcom/google/common/collect/ComputingConcurrentHashMap$ComputingValueReference<TK;TV;>;)TV;
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type
            //  -----  -----  -----  -----  ----
            //  10     12     181    192    Any
            //  16     25     68     77     Any
            //  29     34     68     77     Any
            //  42     44     192    205    Any
            //  85     87     192    205    Any
            //  94     97     97     99     Any
            //  124    136    97     99     Any
            //  143    154    97     99     Any
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0049:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        V getOrCompute(final K k, final int n, final Function<? super K, ? extends V> function) throws ExecutionException {
            Object newValue = null;
            int count;
            AtomicReferenceArray<ReferenceEntry<K, V>> table = null;
            int n2 = 0;
            Object o = null;
            K key;
            V value;
            boolean b;
            V v;
            final K i;
            Label_0081_Outer:Label_0105_Outer:
            while (true) {
                while (true) {
                Label_0495:
                    while (true) {
                        Label_0411: {
                            try {
                            Label_0170_Outer:
                                while (true) {
                                    newValue = this.getEntry(k, n);
                                    while (true) {
                                        while (true) {
                                            Label_0013: {
                                                if (newValue == null) {
                                                    break Label_0013;
                                                }
                                                Label_0144: {
                                                    break Label_0144;
                                                    while (true) {
                                                        this.lock();
                                                        try {
                                                            this.preWriteCleanup();
                                                            count = this.count;
                                                            table = this.table;
                                                            n2 = (n & table.length() - 1);
                                                            o = (newValue = table.get(n2));
                                                            while (true) {
                                                                while (true) {
                                                                    while (true) {
                                                                        while (newValue != null) {
                                                                            key = ((ReferenceEntry<K, ReferenceEntry>)newValue).getKey();
                                                                            if (((ReferenceEntry)newValue).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                                                                                if (!((ReferenceEntry<K, V>)newValue).getValueReference().isComputingReference()) {
                                                                                    value = ((ReferenceEntry<K, V>)newValue).getValueReference().get();
                                                                                    if (value != null) {
                                                                                        if (!this.map.expires() || !this.map.isExpired((ReferenceEntry<K, V>)newValue)) {
                                                                                            this.recordLockedRead((ReferenceEntry<K, V>)newValue);
                                                                                            return value;
                                                                                        }
                                                                                        this.enqueueNotification(key, n, value, MapMaker.RemovalCause.EXPIRED);
                                                                                    }
                                                                                    else {
                                                                                        this.enqueueNotification(key, n, value, MapMaker.RemovalCause.COLLECTED);
                                                                                    }
                                                                                    this.evictionQueue.remove(newValue);
                                                                                    this.expirationQueue.remove(newValue);
                                                                                    this.count = count - 1;
                                                                                    count = 1;
                                                                                }
                                                                                else {
                                                                                    count = 0;
                                                                                }
                                                                                if (count != 0) {
                                                                                    break Label_0411;
                                                                                }
                                                                                o = null;
                                                                                this.unlock();
                                                                                this.postWriteCleanup();
                                                                                if (count != 0) {
                                                                                    break Label_0081_Outer;
                                                                                }
                                                                                if (!Thread.holdsLock(newValue)) {
                                                                                    break Label_0495;
                                                                                }
                                                                                b = false;
                                                                                Preconditions.checkState(b, (Object)"Recursive computation");
                                                                                o = ((ReferenceEntry<K, ReferenceEntry>)newValue).getValueReference().waitForValue();
                                                                                if (o != null) {
                                                                                    this.recordRead((ReferenceEntry<K, V>)newValue);
                                                                                    return (V)o;
                                                                                }
                                                                                continue Label_0170_Outer;
                                                                            }
                                                                            else {
                                                                                newValue = ((ReferenceEntry<K, ReferenceEntry>)newValue).getNext();
                                                                            }
                                                                        }
                                                                        count = 1;
                                                                        continue Label_0081_Outer;
                                                                    }
                                                                    continue Label_0105_Outer;
                                                                }
                                                                iftrue(Label_0018:)(!((ReferenceEntry<K, ReferenceEntry>)newValue).getValueReference().isComputingReference());
                                                                continue Label_0170_Outer;
                                                            }
                                                            o = this.getLiveValue((ReferenceEntry<K, ReferenceEntry>)newValue);
                                                            iftrue(Label_0013:)(o == null);
                                                            this.recordRead((ReferenceEntry<K, V>)newValue);
                                                            return (V)o;
                                                        }
                                                        finally {
                                                            this.unlock();
                                                            this.postWriteCleanup();
                                                        }
                                                    }
                                                }
                                            }
                                            if (newValue == null) {
                                                continue Label_0081_Outer;
                                            }
                                            break;
                                        }
                                        continue;
                                    }
                                }
                            }
                            finally {
                                this.postReadCleanup();
                            }
                        }
                        v = (V)new ComputingValueReference<K, V>((Function<? super K, ? extends V>)function);
                        if (newValue != null) {
                            ((ReferenceEntry<K, ReferenceEntry>)newValue).setValueReference((ValueReference<K, ReferenceEntry>)v);
                            o = v;
                            continue Label_0105_Outer;
                        }
                        newValue = this.newEntry(i, n, (ReferenceEntry<K, V>)o);
                        ((ReferenceEntry<K, ReferenceEntry>)newValue).setValueReference((ValueReference<K, ReferenceEntry>)v);
                        table.set(n2, (ReferenceEntry<K, Object>)newValue);
                        o = v;
                        continue Label_0105_Outer;
                    }
                    b = true;
                    continue;
                }
            }
            final Object compute = this.compute(i, n, (ReferenceEntry<K, Object>)newValue, (ComputingValueReference<K, Object>)o);
            this.postReadCleanup();
            return (V)compute;
        }
    }
    
    static final class ComputingSerializationProxy<K, V> extends AbstractSerializationProxy<K, V>
    {
        private static final long serialVersionUID = 4L;
        final Function<? super K, ? extends V> computingFunction;
        
        ComputingSerializationProxy(final Strength strength, final Strength strength2, final Equivalence<Object> equivalence, final Equivalence<Object> equivalence2, final long n, final long n2, final int n3, final int n4, final MapMaker.RemovalListener<? super K, ? super V> removalListener, final ConcurrentMap<K, V> concurrentMap, final Function<? super K, ? extends V> computingFunction) {
            super(strength, strength2, equivalence, equivalence2, n, n2, n3, n4, removalListener, concurrentMap);
            this.computingFunction = computingFunction;
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = (ConcurrentMap<K, V>)this.readMapMaker(objectInputStream).makeComputingMap((Function<? super K, ? extends V>)this.computingFunction);
            this.readEntries(objectInputStream);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            this.writeMapTo(objectOutputStream);
        }
        
        Object readResolve() {
            return this.delegate;
        }
    }
    
    private static final class ComputingValueReference<K, V> implements ValueReference<K, V>
    {
        @GuardedBy("this")
        volatile ValueReference<K, V> computedReference;
        final Function<? super K, ? extends V> computingFunction;
        
        public ComputingValueReference(final Function<? super K, ? extends V> computingFunction) {
            this.computedReference = MapMakerInternalMap.unset();
            this.computingFunction = computingFunction;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
            this.setValueReference(valueReference);
        }
        
        V compute(final K k, final int n) throws ExecutionException {
            try {
                final V apply = (V)this.computingFunction.apply(k);
                this.setValueReference(new ComputedReference<K, V>(apply));
                return apply;
            }
            catch (final Throwable cause) {
                this.setValueReference(new ComputationExceptionReference<K, V>(cause));
                throw new ExecutionException(cause);
            }
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, @Nullable final V v, final ReferenceEntry<K, V> referenceEntry) {
            return this;
        }
        
        @Override
        public V get() {
            return null;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public boolean isComputingReference() {
            return true;
        }
        
        void setValueReference(final ValueReference<K, V> computedReference) {
            synchronized (this) {
                if (this.computedReference == MapMakerInternalMap.UNSET) {
                    this.computedReference = computedReference;
                    this.notifyAll();
                }
            }
        }
        
        @Override
        public V waitForValue() throws ExecutionException {
            int n = 0;
            final int n2 = 0;
            if (this.computedReference == MapMakerInternalMap.UNSET) {
                try {
                    monitorenter(this);
                    n = n2;
                    try {
                        while (this.computedReference == MapMakerInternalMap.UNSET) {
                            try {
                                this.wait();
                            }
                            catch (final InterruptedException ex) {
                                n = 1;
                            }
                        }
                        monitorexit(this);
                        if (n != 0) {
                            Thread.currentThread().interrupt();
                            return this.computedReference.waitForValue();
                        }
                        return this.computedReference.waitForValue();
                    }
                    finally {
                        monitorexit(this);
                    }
                }
                finally {
                    while (true) {
                        if (n == 0) {
                            break Label_0076;
                        }
                        Thread.currentThread().interrupt();
                        break Label_0076;
                        continue;
                    }
                }
            }
            return this.computedReference.waitForValue();
        }
    }
}
