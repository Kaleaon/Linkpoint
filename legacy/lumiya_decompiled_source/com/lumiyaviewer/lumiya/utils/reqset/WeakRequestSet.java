package com.lumiyaviewer.lumiya.utils.reqset;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
@ThreadSafe
/* loaded from: classes.dex */
class WeakRequestSet<T> {
    private final Map<T, Set<WeakReference<Object>>> requests = new HashMap();
    private final Object lock = new Object();

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean addRequest(@Nonnull T t, @Nonnull Object obj) {
        boolean z;
        boolean z2 = true;
        synchronized (this.lock) {
            Set<WeakReference<Object>> set = this.requests.get(t);
            if (set == null) {
                HashSet hashSet = new HashSet();
                hashSet.add(new WeakReference(obj));
                this.requests.put(t, hashSet);
            } else {
                Iterator<WeakReference<Object>> it = set.iterator();
                boolean z3 = false;
                while (it.hasNext()) {
                    WeakReference<Object> next = it.next();
                    if (next.get() == null) {
                        it.remove();
                        z = z3;
                    } else {
                        z = next.get() == obj ? true : z3;
                    }
                    z3 = z;
                }
                if (z3) {
                    z2 = false;
                } else {
                    set.add(new WeakReference<>(obj));
                }
            }
        }
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void completeRequest(@Nonnull T t) {
        Set<WeakReference<Object>> remove;
        synchronized (this.lock) {
            remove = this.requests.remove(t);
        }
        Iterator<T> it = remove.iterator();
        while (it.hasNext()) {
            Object obj = ((WeakReference) it.next()).get();
            if (obj != null && (obj instanceof RequestCompleteListener)) {
                ((RequestCompleteListener) obj).onRequestComplete(t);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x004d, code lost:
        r0 = r0.getKey();
     */
    @javax.annotation.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public T getRequest() {
        /*
            r6 = this;
            r2 = 0
            java.lang.Object r3 = r6.lock
            monitor-enter(r3)
            java.util.Map<T, java.util.Set<java.lang.ref.WeakReference<java.lang.Object>>> r0 = r6.requests     // Catch: java.lang.Throwable -> L3a
            java.util.Set r0 = r0.entrySet()     // Catch: java.lang.Throwable -> L3a
            java.util.Iterator r4 = r0.iterator()     // Catch: java.lang.Throwable -> L3a
        Le:
            boolean r0 = r4.hasNext()     // Catch: java.lang.Throwable -> L3a
            if (r0 == 0) goto L53
            java.lang.Object r0 = r4.next()     // Catch: java.lang.Throwable -> L3a
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch: java.lang.Throwable -> L3a
            java.lang.Object r1 = r0.getValue()     // Catch: java.lang.Throwable -> L3a
            java.util.Set r1 = (java.util.Set) r1     // Catch: java.lang.Throwable -> L3a
            java.util.Iterator r5 = r1.iterator()     // Catch: java.lang.Throwable -> L3a
        L24:
            boolean r1 = r5.hasNext()     // Catch: java.lang.Throwable -> L3a
            if (r1 == 0) goto L3d
            java.lang.Object r1 = r5.next()     // Catch: java.lang.Throwable -> L3a
            java.lang.ref.WeakReference r1 = (java.lang.ref.WeakReference) r1     // Catch: java.lang.Throwable -> L3a
            java.lang.Object r1 = r1.get()     // Catch: java.lang.Throwable -> L3a
            if (r1 != 0) goto L24
            r5.remove()     // Catch: java.lang.Throwable -> L3a
            goto L24
        L3a:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L3d:
            java.lang.Object r1 = r0.getValue()     // Catch: java.lang.Throwable -> L3a
            java.util.Set r1 = (java.util.Set) r1     // Catch: java.lang.Throwable -> L3a
            boolean r1 = r1.isEmpty()     // Catch: java.lang.Throwable -> L3a
            if (r1 == 0) goto L4d
            r4.remove()     // Catch: java.lang.Throwable -> L3a
            goto Le
        L4d:
            java.lang.Object r0 = r0.getKey()     // Catch: java.lang.Throwable -> L3a
        L51:
            monitor-exit(r3)
            return r0
        L53:
            r0 = r2
            goto L51
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.reqset.WeakRequestSet.getRequest():java.lang.Object");
    }
}
