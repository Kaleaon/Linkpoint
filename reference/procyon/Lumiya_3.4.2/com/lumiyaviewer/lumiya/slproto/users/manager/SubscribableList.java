// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.Iterator;
import java.util.Collection;
import com.google.common.collect.ImmutableList;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import com.google.common.base.Optional;
import java.util.Map;
import java.util.List;
import java.util.AbstractList;

public class SubscribableList<T> extends AbstractList<T>
{
    private final List<T> backingList;
    private final Object lock;
    private final Map<List<T>, Optional<Executor>> targets;
    
    public SubscribableList() {
        this.lock = new Object();
        this.backingList = new ArrayList<T>();
        this.targets = new WeakHashMap<List<T>, Optional<Executor>>();
    }
    
    @Override
    public void add(final int n, final T t) {
        while (true) {
            Object lock = this.lock;
            while (true) {
                Label_0124: {
                    synchronized (lock) {
                        this.backingList.add(n, t);
                        Object o = ImmutableList.copyOf((Collection<?>)this.targets.entrySet());
                        monitorexit(lock);
                        o = ((Iterable)o).iterator();
                        while (((Iterator)o).hasNext()) {
                            final Map.Entry entry = (Map.Entry)((Iterator)o).next();
                            lock = entry.getKey();
                            final Executor executor = ((Optional)entry.getValue()).orNull();
                            if (executor == null) {
                                break Label_0124;
                            }
                            executor.execute(new _$Lambda$Gzuh54B3D66vdv_4A7qntNjZJmM$2(n, lock, t));
                        }
                        break;
                    }
                }
                ((List<T>)lock).add(n, t);
                continue;
            }
        }
    }
    
    public List<T> addSubscription(final List<T> list, final Optional<Executor> optional) {
        synchronized (this.lock) {
            this.targets.put(list, optional);
            return (List<T>)ImmutableList.copyOf((Collection<?>)this.backingList);
        }
    }
    
    @Override
    public void clear() {
        while (true) {
            Object lock = this.lock;
            while (true) {
                Label_0114: {
                    synchronized (lock) {
                        this.backingList.clear();
                        final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)this.targets.entrySet());
                        monitorexit(lock);
                        for (final Map.Entry<List<?>, V> entry : copy) {
                            lock = entry.getKey();
                            final Executor executor = ((Optional)entry.getValue()).orNull();
                            if (executor == null) {
                                break Label_0114;
                            }
                            lock.getClass();
                            executor.execute(new _$Lambda$Gzuh54B3D66vdv_4A7qntNjZJmM(lock));
                        }
                        break;
                    }
                }
                ((List)lock).clear();
                continue;
            }
        }
    }
    
    @Override
    public T get(final int n) {
        synchronized (this.lock) {
            return this.backingList.get(n);
        }
    }
    
    @Override
    public T remove(final int n) {
        while (true) {
            Object lock = this.lock;
            while (true) {
                Label_0123: {
                    synchronized (lock) {
                        this.backingList.remove(n);
                        Object o = ImmutableList.copyOf((Collection<?>)this.targets.entrySet());
                        monitorexit(lock);
                        o = ((Iterable)o).iterator();
                        while (((Iterator)o).hasNext()) {
                            final Map.Entry entry = (Map.Entry)((Iterator)o).next();
                            lock = entry.getKey();
                            final Executor executor = ((Optional)entry.getValue()).orNull();
                            if (executor == null) {
                                break Label_0123;
                            }
                            executor.execute(new _$Lambda$Gzuh54B3D66vdv_4A7qntNjZJmM$1(n, lock));
                        }
                        break;
                    }
                }
                ((List)lock).remove(n);
                continue;
            }
        }
        return;
    }
    
    public void removeSubscription(final List<T> list) {
        synchronized (this.lock) {
            this.targets.remove(list);
        }
    }
    
    @Override
    public T set(final int n, final T t) {
        T set;
        while (true) {
            Object o = this.lock;
            while (true) {
                List<Object> copy = null;
                Label_0125: {
                    synchronized (o) {
                        set = this.backingList.set(n, t);
                        copy = ImmutableList.copyOf((Collection<?>)this.targets.entrySet());
                        monitorexit(o);
                        o = copy.iterator();
                        while (((Iterator)o).hasNext()) {
                            final Map.Entry<List, V> entry = (Map.Entry<List, V>)((Iterator)o).next();
                            copy = entry.getKey();
                            final Executor executor = ((Optional)entry.getValue()).orNull();
                            if (executor == null) {
                                break Label_0125;
                            }
                            executor.execute(new _$Lambda$Gzuh54B3D66vdv_4A7qntNjZJmM$3(n, copy, t));
                        }
                        break;
                    }
                }
                copy.set(n, t);
                continue;
            }
        }
        return set;
    }
    
    @Override
    public int size() {
        synchronized (this.lock) {
            return this.backingList.size();
        }
    }
}
