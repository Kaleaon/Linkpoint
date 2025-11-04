// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.util;

import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

public class LruCache<K, V>
{
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private final LinkedHashMap<K, V> map;
    private int maxSize;
    private int missCount;
    private int putCount;
    private int size;
    
    public LruCache(final int maxSize) {
        if (maxSize > 0) {
            this.maxSize = maxSize;
            this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }
    
    private int safeSizeOf(final K obj, final V obj2) {
        final int size = this.sizeOf(obj, obj2);
        if (size >= 0) {
            return size;
        }
        throw new IllegalStateException("Negative size: " + obj + "=" + obj2);
    }
    
    protected V create(final K k) {
        return null;
    }
    
    public final int createCount() {
        synchronized (this) {
            return this.createCount;
        }
    }
    
    protected void entryRemoved(final boolean b, final K k, final V v, final V v2) {
    }
    
    public final void evictAll() {
        this.trimToSize(-1);
    }
    
    public final int evictionCount() {
        synchronized (this) {
            return this.evictionCount;
        }
    }
    
    public final V get(final K p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: ifnull          98
        //     4: aload_0        
        //     5: monitorenter   
        //     6: aload_0        
        //     7: getfield        android/support/v4/util/LruCache.map:Ljava/util/LinkedHashMap;
        //    10: aload_1        
        //    11: invokevirtual   java/util/LinkedHashMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //    14: astore_2       
        //    15: aload_2        
        //    16: ifnonnull       108
        //    19: aload_0        
        //    20: aload_0        
        //    21: getfield        android/support/v4/util/LruCache.missCount:I
        //    24: iconst_1       
        //    25: iadd           
        //    26: putfield        android/support/v4/util/LruCache.missCount:I
        //    29: aload_0        
        //    30: monitorexit    
        //    31: aload_0        
        //    32: aload_1        
        //    33: invokevirtual   android/support/v4/util/LruCache.create:(Ljava/lang/Object;)Ljava/lang/Object;
        //    36: astore_3       
        //    37: aload_3        
        //    38: ifnull          127
        //    41: aload_0        
        //    42: monitorenter   
        //    43: aload_0        
        //    44: aload_0        
        //    45: getfield        android/support/v4/util/LruCache.createCount:I
        //    48: iconst_1       
        //    49: iadd           
        //    50: putfield        android/support/v4/util/LruCache.createCount:I
        //    53: aload_0        
        //    54: getfield        android/support/v4/util/LruCache.map:Ljava/util/LinkedHashMap;
        //    57: aload_1        
        //    58: aload_3        
        //    59: invokevirtual   java/util/LinkedHashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //    62: astore_2       
        //    63: aload_2        
        //    64: ifnonnull       129
        //    67: aload_0        
        //    68: aload_0        
        //    69: getfield        android/support/v4/util/LruCache.size:I
        //    72: aload_0        
        //    73: aload_1        
        //    74: aload_3        
        //    75: invokespecial   android/support/v4/util/LruCache.safeSizeOf:(Ljava/lang/Object;Ljava/lang/Object;)I
        //    78: iadd           
        //    79: putfield        android/support/v4/util/LruCache.size:I
        //    82: aload_0        
        //    83: monitorexit    
        //    84: aload_2        
        //    85: ifnonnull       147
        //    88: aload_0        
        //    89: aload_0        
        //    90: getfield        android/support/v4/util/LruCache.maxSize:I
        //    93: invokevirtual   android/support/v4/util/LruCache.trimToSize:(I)V
        //    96: aload_3        
        //    97: areturn        
        //    98: new             Ljava/lang/NullPointerException;
        //   101: dup            
        //   102: ldc             "key == null"
        //   104: invokespecial   java/lang/NullPointerException.<init>:(Ljava/lang/String;)V
        //   107: athrow         
        //   108: aload_0        
        //   109: aload_0        
        //   110: getfield        android/support/v4/util/LruCache.hitCount:I
        //   113: iconst_1       
        //   114: iadd           
        //   115: putfield        android/support/v4/util/LruCache.hitCount:I
        //   118: aload_0        
        //   119: monitorexit    
        //   120: aload_2        
        //   121: areturn        
        //   122: astore_1       
        //   123: aload_0        
        //   124: monitorexit    
        //   125: aload_1        
        //   126: athrow         
        //   127: aconst_null    
        //   128: areturn        
        //   129: aload_0        
        //   130: getfield        android/support/v4/util/LruCache.map:Ljava/util/LinkedHashMap;
        //   133: aload_1        
        //   134: aload_2        
        //   135: invokevirtual   java/util/LinkedHashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   138: pop            
        //   139: goto            82
        //   142: astore_1       
        //   143: aload_0        
        //   144: monitorexit    
        //   145: aload_1        
        //   146: athrow         
        //   147: aload_0        
        //   148: iconst_0       
        //   149: aload_1        
        //   150: aload_3        
        //   151: aload_2        
        //   152: invokevirtual   android/support/v4/util/LruCache.entryRemoved:(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
        //   155: aload_2        
        //   156: areturn        
        //    Signature:
        //  (TK;)TV;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  6      15     122    127    Any
        //  19     31     122    127    Any
        //  43     63     142    147    Any
        //  67     82     142    147    Any
        //  82     84     142    147    Any
        //  108    120    122    127    Any
        //  123    125    122    127    Any
        //  129    139    142    147    Any
        //  143    145    142    147    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0082:
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
    
    public final int hitCount() {
        synchronized (this) {
            return this.hitCount;
        }
    }
    
    public final int maxSize() {
        synchronized (this) {
            return this.maxSize;
        }
    }
    
    public final int missCount() {
        synchronized (this) {
            return this.missCount;
        }
    }
    
    public final V put(final K key, final V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        while (true) {
            while (true) {
                final V put;
                synchronized (this) {
                    ++this.putCount;
                    this.size += this.safeSizeOf(key, value);
                    put = this.map.put(key, value);
                    if (put != null) {
                        this.size -= this.safeSizeOf(key, put);
                    }
                    monitorexit(this);
                    if (put == null) {
                        this.trimToSize(this.maxSize);
                        return put;
                    }
                }
                final K k;
                this.entryRemoved(false, k, put, value);
                continue;
            }
        }
    }
    
    public final int putCount() {
        synchronized (this) {
            return this.putCount;
        }
    }
    
    public final V remove(final K key) {
        Label_0027: {
            if (key == null) {
                break Label_0027;
            }
            while (true) {
                final V remove;
                Label_0060: {
                    synchronized (this) {
                        remove = this.map.remove(key);
                        if (remove != null) {
                            this.size -= this.safeSizeOf(key, remove);
                        }
                        monitorexit(this);
                        if (remove == null) {
                            return remove;
                        }
                        break Label_0060;
                        throw new NullPointerException("key == null");
                    }
                }
                final K k;
                this.entryRemoved(false, k, remove, null);
                return remove;
            }
        }
    }
    
    public void resize(final int maxSize) {
        Label_0019: {
            if (maxSize <= 0) {
                break Label_0019;
            }
            synchronized (this) {
                this.maxSize = maxSize;
                monitorexit(this);
                this.trimToSize(maxSize);
                return;
                throw new IllegalArgumentException("maxSize <= 0");
            }
        }
    }
    
    public final int size() {
        synchronized (this) {
            return this.size;
        }
    }
    
    protected int sizeOf(final K k, final V v) {
        return 1;
    }
    
    public final Map<K, V> snapshot() {
        synchronized (this) {
            return new LinkedHashMap<K, V>((Map<? extends K, ? extends V>)this.map);
        }
    }
    
    @Override
    public final String toString() {
        int i = 0;
        synchronized (this) {
            final int n = this.hitCount + this.missCount;
            if (n != 0) {
                i = this.hitCount * 100 / n;
            }
            return String.format(Locale.US, "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", this.maxSize, this.hitCount, this.missCount, i);
        }
    }
    
    public void trimToSize(final int n) {
        while (true) {
            while (true) {
                synchronized (this) {
                    if (this.size < 0) {
                        throw new IllegalStateException(this.getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                    }
                }
                if (this.map.isEmpty() && this.size != 0) {
                    continue;
                }
                break;
            }
            if (this.size <= n || this.map.isEmpty()) {
                break;
            }
            final Map.Entry entry = (Map.Entry)this.map.entrySet().iterator().next();
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            this.map.remove(key);
            this.size -= this.safeSizeOf((K)key, (V)value);
            ++this.evictionCount;
            monitorexit(this);
            this.entryRemoved(true, (K)key, (V)value, null);
        }
        monitorexit(this);
    }
}
