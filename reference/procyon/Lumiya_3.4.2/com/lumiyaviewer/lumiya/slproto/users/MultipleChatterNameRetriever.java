// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.lang.ref.WeakReference;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import java.util.UUID;

public class MultipleChatterNameRetriever implements ChatterNameRetriever.OnChatterNameUpdated
{
    private final UUID agentUUID;
    @Nullable
    private final Executor executor;
    private final WeakReference<OnChatterNameUpdated> listener;
    private final Object lock;
    private final Map<UUID, ChatterNameRetriever> retrievers;
    
    public MultipleChatterNameRetriever(final UUID agentUUID, final OnChatterNameUpdated referent, final Executor executor) {
        this.lock = new Object();
        this.retrievers = new HashMap<UUID, ChatterNameRetriever>();
        this.agentUUID = agentUUID;
        this.listener = new WeakReference<OnChatterNameUpdated>(referent);
        this.executor = executor;
    }
    
    public String addChatter(final UUID uuid) {
        synchronized (this.lock) {
            final ChatterNameRetriever chatterNameRetriever = this.retrievers.get(uuid);
            monitorexit(this.lock);
            if (chatterNameRetriever != null) {
                return chatterNameRetriever.getResolvedName();
            }
        }
        final UUID uuid2;
        final ChatterNameRetriever chatterNameRetriever2 = new ChatterNameRetriever(ChatterID.getUserChatterID(this.agentUUID, uuid2), (ChatterNameRetriever.OnChatterNameUpdated)this, this.executor);
        synchronized (this.lock) {
            final ChatterNameRetriever chatterNameRetriever3 = this.retrievers.put(uuid2, chatterNameRetriever2);
            monitorexit(this.lock);
            if (chatterNameRetriever3 != null) {
                chatterNameRetriever3.dispose();
            }
            return chatterNameRetriever2.getResolvedName();
        }
    }
    
    public void clearChatters() {
        Iterable iterable = null;
        synchronized (this.lock) {
            final Iterator<Map.Entry<UUID, ChatterNameRetriever>> iterator = this.retrievers.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<K, ChatterNameRetriever> entry = (Map.Entry<K, ChatterNameRetriever>)iterator.next();
                Object o;
                if ((o = iterable) == null) {
                    o = new HashSet<Object>();
                }
                ((Set<ChatterNameRetriever>)o).add(entry.getValue());
                iterator.remove();
                iterable = (Iterable)o;
            }
        }
        final Object o2;
        monitorexit(o2);
        if (iterable != null) {
            final Iterator<Object> iterator2 = iterable.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().dispose();
            }
        }
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        final OnChatterNameUpdated onChatterNameUpdated = this.listener.get();
        if (onChatterNameUpdated != null) {
            onChatterNameUpdated.onChatterNameUpdated(this);
        }
    }
    
    public void retainChatters(final Set<UUID> set) {
        while (true) {
            Set<Object> set2 = null;
            while (true) {
                Label_0154: {
                    synchronized (this.lock) {
                        final Iterator<Map.Entry<UUID, ChatterNameRetriever>> iterator = this.retrievers.entrySet().iterator();
                        while (iterator.hasNext()) {
                            final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator.next();
                            if (set.contains(entry.getKey())) {
                                break Label_0154;
                            }
                            Set<Object> set3;
                            if ((set3 = set2) == null) {
                                set3 = new HashSet<Object>();
                            }
                            set3.add(entry.getValue());
                            iterator.remove();
                            set2 = set3;
                        }
                        monitorexit(this.lock);
                        if (set2 != null) {
                            final Iterator<Object> iterator2 = (Iterator<Object>)set2.iterator();
                            while (iterator2.hasNext()) {
                                iterator2.next().dispose();
                            }
                        }
                    }
                    break;
                }
                continue;
            }
        }
    }
    
    public interface OnChatterNameUpdated
    {
        void onChatterNameUpdated(final MultipleChatterNameRetriever p0);
    }
}
