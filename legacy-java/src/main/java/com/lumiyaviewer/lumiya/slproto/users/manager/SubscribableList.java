package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public class SubscribableList<T> extends AbstractList<T> {
    private final Object lock = new Object();
    private final List<T> backingList = new ArrayList();
    private final Map<List<T>, Optional<Executor>> targets = new WeakHashMap();

    @Override // java.util.AbstractList, java.util.List
    public void add(final int i, final T t) {
        ImmutableList<Map.Entry> copyOf;
        synchronized (this.lock) {
            this.backingList.add(i, t);
            copyOf = ImmutableList.copyOf((Collection) this.targets.entrySet());
        }
        for (Map.Entry entry : copyOf) {
            final List list = (List) entry.getKey();
            Executor executor = (Executor) ((Optional) entry.getValue()).orNull();
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$Gzuh54B3D66vdv-4A7qntNjZJmM.2
                    private final /* synthetic */ void $m$0() {
                        ((List) list).add(i, t);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                list.add(i, t);
            }
        }
    }

    public List<T> addSubscription(List<T> list, Optional<Executor> optional) {
        ImmutableList copyOf;
        synchronized (this.lock) {
            this.targets.put(list, optional);
            copyOf = ImmutableList.copyOf((Collection) this.backingList);
        }
        return copyOf;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        ImmutableList<Map.Entry> copyOf;
        synchronized (this.lock) {
            this.backingList.clear();
            copyOf = ImmutableList.copyOf((Collection) this.targets.entrySet());
        }
        for (Map.Entry entry : copyOf) {
            final List list = (List) entry.getKey();
            Executor executor = (Executor) ((Optional) entry.getValue()).orNull();
            if (executor != null) {
                list.getClass();
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$Gzuh54B3D66vdv-4A7qntNjZJmM
                    private final /* synthetic */ void $m$0() {
                        ((List) list).clear();
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                list.clear();
            }
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public T get(int i) {
        T t;
        synchronized (this.lock) {
            t = this.backingList.get(i);
        }
        return t;
    }

    @Override // java.util.AbstractList, java.util.List
    public T remove(final int i) {
        T remove;
        ImmutableList<Map.Entry> copyOf;
        synchronized (this.lock) {
            remove = this.backingList.remove(i);
            copyOf = ImmutableList.copyOf((Collection) this.targets.entrySet());
        }
        for (Map.Entry entry : copyOf) {
            final List list = (List) entry.getKey();
            Executor executor = (Executor) ((Optional) entry.getValue()).orNull();
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$Gzuh54B3D66vdv-4A7qntNjZJmM.1
                    private final /* synthetic */ void $m$0() {
                        ((List) list).remove(i);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                list.remove(i);
            }
        }
        return remove;
    }

    public void removeSubscription(List<T> list) {
        synchronized (this.lock) {
            this.targets.remove(list);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public T set(final int i, final T t) {
        T t2;
        ImmutableList<Map.Entry> copyOf;
        synchronized (this.lock) {
            t2 = this.backingList.set(i, t);
            copyOf = ImmutableList.copyOf((Collection) this.targets.entrySet());
        }
        for (Map.Entry entry : copyOf) {
            final List list = (List) entry.getKey();
            Executor executor = (Executor) ((Optional) entry.getValue()).orNull();
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$Gzuh54B3D66vdv-4A7qntNjZJmM.3
                    private final /* synthetic */ void $m$0() {
                        ((List) list).set(i, t);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                list.set(i, t);
            }
        }
        return t2;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        int size;
        synchronized (this.lock) {
            size = this.backingList.size();
        }
        return size;
    }
}
