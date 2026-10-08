package android.support.v4.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes.dex */
public class LruCache<K, V> {
    private int createCount;
    private int evictionCount;
    private int hitCount;
    private final LinkedHashMap<K, V> map;
    private int maxSize;
    private int missCount;
    private int putCount;
    private int size;

    public LruCache(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = i;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    private int safeSizeOf(K k, V v) {
        int sizeOf = sizeOf(k, v);
        if (sizeOf >= 0) {
            return sizeOf;
        }
        throw new IllegalStateException("Negative size: " + k + "=" + v);
    }

    protected V create(K k) {
        return null;
    }

    public final synchronized int createCount() {
        return this.createCount;
    }

    protected void entryRemoved(boolean z, K k, V v, V v2) {
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }

    public final V get(K k) {
        V put;
        if (k != null) {
            synchronized (this) {
                V v = this.map.get(k);
                if (v != null) {
                    this.hitCount++;
                    return v;
                }
                this.missCount++;
                V create = create(k);
                if (create != null) {
                    synchronized (this) {
                        this.createCount++;
                        put = this.map.put(k, create);
                        if (put == null) {
                            this.size += safeSizeOf(k, create);
                        } else {
                            this.map.put(k, put);
                        }
                    }
                    if (put == null) {
                        trimToSize(this.maxSize);
                        return create;
                    }
                    entryRemoved(false, k, create, put);
                    return put;
                }
                return null;
            }
        }
        throw new NullPointerException("key == null");
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int maxSize() {
        return this.maxSize;
    }

    public final synchronized int missCount() {
        return this.missCount;
    }

    public final V put(K k, V v) {
        V put;
        if (k == null || v == null) {
            throw new NullPointerException("key == null || value == null");
        }
        synchronized (this) {
            this.putCount++;
            this.size += safeSizeOf(k, v);
            put = this.map.put(k, v);
            if (put != null) {
                this.size -= safeSizeOf(k, put);
            }
        }
        if (put != null) {
            entryRemoved(false, k, put, v);
        }
        trimToSize(this.maxSize);
        return put;
    }

    public final synchronized int putCount() {
        return this.putCount;
    }

    public final V remove(K k) {
        V remove;
        if (k != null) {
            synchronized (this) {
                remove = this.map.remove(k);
                if (remove != null) {
                    this.size -= safeSizeOf(k, remove);
                }
            }
            if (remove != null) {
                entryRemoved(false, k, remove, null);
            }
            return remove;
        }
        throw new NullPointerException("key == null");
    }

    public void resize(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        synchronized (this) {
            this.maxSize = i;
        }
        trimToSize(i);
    }

    public final synchronized int size() {
        return this.size;
    }

    protected int sizeOf(K k, V v) {
        return 1;
    }

    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap(this.map);
    }

    public final synchronized String toString() {
        String format;
        synchronized (this) {
            int i = this.hitCount + this.missCount;
            format = String.format(Locale.US, "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(i != 0 ? (this.hitCount * 100) / i : 0));
        }
        return format;
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x0037, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void trimToSize(int r5) {
        /*
            r4 = this;
        L0:
            monitor-enter(r4)
            int r0 = r4.size     // Catch: java.lang.Throwable -> L27
            if (r0 >= 0) goto L2a
        L5:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch: java.lang.Throwable -> L27
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L27
            r1.<init>()     // Catch: java.lang.Throwable -> L27
            java.lang.Class r2 = r4.getClass()     // Catch: java.lang.Throwable -> L27
            java.lang.String r2 = r2.getName()     // Catch: java.lang.Throwable -> L27
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L27
            java.lang.String r2 = ".sizeOf() is reporting inconsistent results!"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch: java.lang.Throwable -> L27
            java.lang.String r1 = r1.toString()     // Catch: java.lang.Throwable -> L27
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L27
            throw r0     // Catch: java.lang.Throwable -> L27
        L27:
            r0 = move-exception
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L27
            throw r0
        L2a:
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch: java.lang.Throwable -> L27
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L27
            if (r0 != 0) goto L38
        L32:
            int r0 = r4.size     // Catch: java.lang.Throwable -> L27
            if (r0 > r5) goto L3d
        L36:
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L27
            return
        L38:
            int r0 = r4.size     // Catch: java.lang.Throwable -> L27
            if (r0 != 0) goto L5
            goto L32
        L3d:
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch: java.lang.Throwable -> L27
            boolean r0 = r0.isEmpty()     // Catch: java.lang.Throwable -> L27
            if (r0 != 0) goto L36
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch: java.lang.Throwable -> L27
            java.util.Set r0 = r0.entrySet()     // Catch: java.lang.Throwable -> L27
            java.util.Iterator r0 = r0.iterator()     // Catch: java.lang.Throwable -> L27
            java.lang.Object r0 = r0.next()     // Catch: java.lang.Throwable -> L27
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch: java.lang.Throwable -> L27
            java.lang.Object r1 = r0.getKey()     // Catch: java.lang.Throwable -> L27
            java.lang.Object r0 = r0.getValue()     // Catch: java.lang.Throwable -> L27
            java.util.LinkedHashMap<K, V> r2 = r4.map     // Catch: java.lang.Throwable -> L27
            r2.remove(r1)     // Catch: java.lang.Throwable -> L27
            int r2 = r4.size     // Catch: java.lang.Throwable -> L27
            int r3 = r4.safeSizeOf(r1, r0)     // Catch: java.lang.Throwable -> L27
            int r2 = r2 - r3
            r4.size = r2     // Catch: java.lang.Throwable -> L27
            int r2 = r4.evictionCount     // Catch: java.lang.Throwable -> L27
            int r2 = r2 + 1
            r4.evictionCount = r2     // Catch: java.lang.Throwable -> L27
            monitor-exit(r4)     // Catch: java.lang.Throwable -> L27
            r2 = 1
            r3 = 0
            r4.entryRemoved(r2, r1, r0, r3)
            goto L0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LruCache.trimToSize(int):void");
    }
}
