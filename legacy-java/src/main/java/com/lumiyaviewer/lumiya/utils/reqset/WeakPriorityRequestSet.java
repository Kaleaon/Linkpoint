package com.lumiyaviewer.lumiya.utils.reqset;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
/* loaded from: classes.dex */
public class WeakPriorityRequestSet<T> {
    private final Map<Integer, WeakRequestSet<T>> priorityBins = new TreeMap();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();
    private final Set<WeakReference<RequestListener>> listeners = new HashSet();

    private void invokeListeners() {
        LinkedList linkedList = new LinkedList();
        this.lock.lock();
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator();
            while (it.hasNext()) {
                RequestListener requestListener = it.next().get();
                if (requestListener == null) {
                    it.remove();
                } else {
                    linkedList.add(requestListener);
                }
            }
            this.lock.unlock();
            Iterator<T> it2 = linkedList.iterator();
            while (it2.hasNext()) {
                ((RequestListener) it2.next()).onNewRequest();
            }
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    public void addListener(RequestListener requestListener) {
        boolean z;
        this.lock.lock();
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator();
            boolean z2 = false;
            while (it.hasNext()) {
                RequestListener requestListener2 = it.next().get();
                if (requestListener2 == null) {
                    it.remove();
                    z = z2;
                } else {
                    z = requestListener2 == requestListener ? true : z2;
                }
                z2 = z;
            }
            if (!z2) {
                this.listeners.add(new WeakReference<>(requestListener));
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void addRequest(int i, T t, Object obj) {
        this.lock.lock();
        try {
            WeakRequestSet<T> weakRequestSet = this.priorityBins.get(Integer.valueOf(i));
            if (weakRequestSet == null) {
                weakRequestSet = new WeakRequestSet<>();
                this.priorityBins.put(Integer.valueOf(i), weakRequestSet);
            }
            boolean addRequest = weakRequestSet.addRequest(t, obj);
            if (addRequest) {
                this.notEmpty.signalAll();
            }
            if (addRequest) {
                invokeListeners();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void completeRequest(T t) {
        this.lock.lock();
        try {
            Iterator<T> it = this.priorityBins.values().iterator();
            while (it.hasNext()) {
                ((WeakRequestSet) it.next()).completeRequest(t);
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Nullable
    public T getRequest() {
        T t = null;
        this.lock.lock();
        try {
            Iterator<T> it = this.priorityBins.entrySet().iterator();
            while (it.hasNext() && (t = (T) ((WeakRequestSet) ((Map.Entry) it.next()).getValue()).getRequest()) == null) {
            }
            return t;
        } finally {
            this.lock.unlock();
        }
    }

    public void removeListener(RequestListener requestListener) {
        this.lock.lock();
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator();
            while (it.hasNext()) {
                RequestListener requestListener2 = it.next().get();
                if (requestListener2 == null || requestListener2 == requestListener) {
                    it.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void waitRequest() throws InterruptedException {
        this.lock.lock();
        try {
            if (getRequest() == null) {
                this.notEmpty.await();
            }
        } finally {
            this.lock.unlock();
        }
    }
}
