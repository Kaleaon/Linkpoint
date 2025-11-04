// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils.reqset;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.lang.ref.WeakReference;
import java.util.Set;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class WeakPriorityRequestSet<T>
{
    private final Set<WeakReference<RequestListener>> listeners;
    private final Lock lock;
    private final Condition notEmpty;
    private final Map<Integer, WeakRequestSet<T>> priorityBins;
    
    public WeakPriorityRequestSet() {
        this.priorityBins = new TreeMap<Integer, WeakRequestSet<T>>();
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.listeners = new HashSet<WeakReference<RequestListener>>();
    }
    
    private void invokeListeners() {
        LinkedList list;
        while (true) {
            list = new LinkedList();
            this.lock.lock();
            while (true) {
                Label_0077: {
                    try {
                        final Iterator<WeakReference<RequestListener>> iterator = this.listeners.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().get() != null) {
                                break Label_0077;
                            }
                            iterator.remove();
                        }
                        break;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                final Throwable t;
                list.add(t);
                continue;
            }
        }
        this.lock.unlock();
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            ((RequestListener)iterator2.next()).onNewRequest();
        }
    }
    
    public void addListener(final RequestListener referent) {
        while (true) {
            this.lock.lock();
            while (true) {
                Label_0122: {
                    try {
                        Object iterator = this.listeners.iterator();
                        boolean b = false;
                        while (((Iterator)iterator).hasNext()) {
                            final RequestListener requestListener = ((WeakReference<RequestListener>)((Iterator)iterator).next()).get();
                            if (requestListener == null) {
                                ((Iterator)iterator).remove();
                            }
                            else {
                                if (requestListener != referent) {
                                    break Label_0122;
                                }
                                b = true;
                            }
                        }
                        if (!b) {
                            final Set<WeakReference<RequestListener>> listeners = this.listeners;
                            iterator = new WeakReference<RequestListener>(referent);
                            listeners.add((WeakReference<RequestListener>)iterator);
                        }
                        return;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                continue;
            }
        }
    }
    
    public void addRequest(final int n, final T t, final Object o) {
        this.lock.lock();
        try {
            WeakRequestSet set;
            if ((set = this.priorityBins.get(n)) == null) {
                set = new WeakRequestSet();
                this.priorityBins.put(n, set);
            }
            final boolean addRequest = set.addRequest(t, o);
            if (addRequest) {
                this.notEmpty.signalAll();
            }
            this.lock.unlock();
            if (addRequest) {
                this.invokeListeners();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void completeRequest(final T t) {
        this.lock.lock();
        try {
            final Iterator<Object> iterator = this.priorityBins.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().completeRequest(t);
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Nullable
    public T getRequest() {
        T t = null;
        this.lock.lock();
        try {
            final Iterator<Object> iterator = this.priorityBins.entrySet().iterator();
            while (iterator.hasNext()) {
                final Object request = iterator.next().getValue().getRequest();
                if ((t = (T)request) != null) {
                    t = (T)request;
                    break;
                }
            }
            return t;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void removeListener(final RequestListener requestListener) {
        this.lock.lock();
        try {
            final Iterator<WeakReference<RequestListener>> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                final RequestListener requestListener2 = iterator.next().get();
                if (requestListener2 == null || requestListener2 == requestListener) {
                    iterator.remove();
                }
            }
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void waitRequest() throws InterruptedException {
        this.lock.lock();
        try {
            if (this.getRequest() == null) {
                this.notEmpty.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
}
