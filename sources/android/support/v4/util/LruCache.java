package android.support.v4.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
        if (i > 0) {
            this.maxSize = i;
            this.map = new LinkedHashMap<>(0, 0.75f, true);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    private int safeSizeOf(K k, V v) {
        int sizeOf = sizeOf(k, v);
        if (sizeOf >= 0) {
            return sizeOf;
        }
        throw new IllegalStateException("Negative size: " + k + "=" + v);
    }

    /* access modifiers changed from: protected */
    public V create(K k) {
        return null;
    }

    public final synchronized int createCount() {
        return this.createCount;
    }

    /* access modifiers changed from: protected */
    public void entryRemoved(boolean z, K k, V v, V v2) {
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0019, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r4.createCount++;
        r1 = r4.map.put(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (r1 != null) goto L_0x004f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        r4.size += safeSizeOf(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0032, code lost:
        if (r1 != null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        trimToSize(r4.maxSize);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r4.map.put(r5, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0058, code lost:
        entryRemoved(false, r5, r0, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x005c, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = create(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0017, code lost:
        if (r0 == null) goto L_0x004e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final V get(K r5) {
        /*
            r4 = this;
            r1 = 0
            if (r5 == 0) goto L_0x003a
            monitor-enter(r4)
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch:{ all -> 0x004b }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x004b }
            if (r0 != 0) goto L_0x0043
            int r0 = r4.missCount     // Catch:{ all -> 0x004b }
            int r0 = r0 + 1
            r4.missCount = r0     // Catch:{ all -> 0x004b }
            monitor-exit(r4)     // Catch:{ all -> 0x004b }
            java.lang.Object r0 = r4.create(r5)
            if (r0 == 0) goto L_0x004e
            monitor-enter(r4)
            int r1 = r4.createCount     // Catch:{ all -> 0x0055 }
            int r1 = r1 + 1
            r4.createCount = r1     // Catch:{ all -> 0x0055 }
            java.util.LinkedHashMap<K, V> r1 = r4.map     // Catch:{ all -> 0x0055 }
            java.lang.Object r1 = r1.put(r5, r0)     // Catch:{ all -> 0x0055 }
            if (r1 != 0) goto L_0x004f
            int r2 = r4.size     // Catch:{ all -> 0x0055 }
            int r3 = r4.safeSizeOf(r5, r0)     // Catch:{ all -> 0x0055 }
            int r2 = r2 + r3
            r4.size = r2     // Catch:{ all -> 0x0055 }
        L_0x0031:
            monitor-exit(r4)     // Catch:{ all -> 0x0055 }
            if (r1 != 0) goto L_0x0058
            int r1 = r4.maxSize
            r4.trimToSize(r1)
            return r0
        L_0x003a:
            java.lang.NullPointerException r0 = new java.lang.NullPointerException
            java.lang.String r1 = "key == null"
            r0.<init>(r1)
            throw r0
        L_0x0043:
            int r1 = r4.hitCount     // Catch:{ all -> 0x004b }
            int r1 = r1 + 1
            r4.hitCount = r1     // Catch:{ all -> 0x004b }
            monitor-exit(r4)     // Catch:{ all -> 0x004b }
            return r0
        L_0x004b:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x004b }
            throw r0
        L_0x004e:
            return r1
        L_0x004f:
            java.util.LinkedHashMap<K, V> r2 = r4.map     // Catch:{ all -> 0x0055 }
            r2.put(r5, r1)     // Catch:{ all -> 0x0055 }
            goto L_0x0031
        L_0x0055:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0055 }
            throw r0
        L_0x0058:
            r2 = 0
            r4.entryRemoved(r2, r5, r0, r1)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LruCache.get(java.lang.Object):java.lang.Object");
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
                entryRemoved(false, k, remove, (V) null);
            }
            return remove;
        }
        throw new NullPointerException("key == null");
    }

    public void resize(int i) {
        if (i > 0) {
            synchronized (this) {
                this.maxSize = i;
            }
            trimToSize(i);
            return;
        }
        throw new IllegalArgumentException("maxSize <= 0");
    }

    public final synchronized int size() {
        return this.size;
    }

    /* access modifiers changed from: protected */
    public int sizeOf(K k, V v) {
        return 1;
    }

    public final synchronized Map<K, V> snapshot() {
        return new LinkedHashMap(this.map);
    }

    public final synchronized String toString() {
        String format;
        int i = 0;
        synchronized (this) {
            int i2 = this.hitCount + this.missCount;
            if (i2 != 0) {
                i = (this.hitCount * 100) / i2;
            }
            format = String.format(Locale.US, "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]", new Object[]{Integer.valueOf(this.maxSize), Integer.valueOf(this.hitCount), Integer.valueOf(this.missCount), Integer.valueOf(i)});
        }
        return format;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trimToSize(int r5) {
        /*
            r4 = this;
        L_0x0000:
            monitor-enter(r4)
            int r0 = r4.size     // Catch:{ all -> 0x0027 }
            if (r0 >= 0) goto L_0x002a
        L_0x0005:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0027 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0027 }
            r1.<init>()     // Catch:{ all -> 0x0027 }
            java.lang.Class r2 = r4.getClass()     // Catch:{ all -> 0x0027 }
            java.lang.String r2 = r2.getName()     // Catch:{ all -> 0x0027 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0027 }
            java.lang.String r2 = ".sizeOf() is reporting inconsistent results!"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0027 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0027 }
            r0.<init>(r1)     // Catch:{ all -> 0x0027 }
            throw r0     // Catch:{ all -> 0x0027 }
        L_0x0027:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0027 }
            throw r0
        L_0x002a:
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch:{ all -> 0x0027 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0027 }
            if (r0 != 0) goto L_0x0038
        L_0x0032:
            int r0 = r4.size     // Catch:{ all -> 0x0027 }
            if (r0 > r5) goto L_0x003d
        L_0x0036:
            monitor-exit(r4)     // Catch:{ all -> 0x0027 }
            return
        L_0x0038:
            int r0 = r4.size     // Catch:{ all -> 0x0027 }
            if (r0 != 0) goto L_0x0005
            goto L_0x0032
        L_0x003d:
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch:{ all -> 0x0027 }
            boolean r0 = r0.isEmpty()     // Catch:{ all -> 0x0027 }
            if (r0 != 0) goto L_0x0036
            java.util.LinkedHashMap<K, V> r0 = r4.map     // Catch:{ all -> 0x0027 }
            java.util.Set r0 = r0.entrySet()     // Catch:{ all -> 0x0027 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x0027 }
            java.lang.Object r0 = r0.next()     // Catch:{ all -> 0x0027 }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ all -> 0x0027 }
            java.lang.Object r1 = r0.getKey()     // Catch:{ all -> 0x0027 }
            java.lang.Object r0 = r0.getValue()     // Catch:{ all -> 0x0027 }
            java.util.LinkedHashMap<K, V> r2 = r4.map     // Catch:{ all -> 0x0027 }
            r2.remove(r1)     // Catch:{ all -> 0x0027 }
            int r2 = r4.size     // Catch:{ all -> 0x0027 }
            int r3 = r4.safeSizeOf(r1, r0)     // Catch:{ all -> 0x0027 }
            int r2 = r2 - r3
            r4.size = r2     // Catch:{ all -> 0x0027 }
            int r2 = r4.evictionCount     // Catch:{ all -> 0x0027 }
            int r2 = r2 + 1
            r4.evictionCount = r2     // Catch:{ all -> 0x0027 }
            monitor-exit(r4)     // Catch:{ all -> 0x0027 }
            r2 = 1
            r3 = 0
            r4.entryRemoved(r2, r1, r0, r3)
            goto L_0x0000
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LruCache.trimToSize(int):void");
    }
}
