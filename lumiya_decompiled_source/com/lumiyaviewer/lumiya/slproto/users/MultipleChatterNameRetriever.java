package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class MultipleChatterNameRetriever implements ChatterNameRetriever.OnChatterNameUpdated {
    private final UUID agentUUID;
    @Nullable
    private final Executor executor;
    private final WeakReference<OnChatterNameUpdated> listener;
    private final Object lock = new Object();
    private final Map<UUID, ChatterNameRetriever> retrievers = new HashMap();

    /* loaded from: classes.dex */
    public interface OnChatterNameUpdated {
        void onChatterNameUpdated(MultipleChatterNameRetriever multipleChatterNameRetriever);
    }

    public MultipleChatterNameRetriever(UUID uuid, OnChatterNameUpdated onChatterNameUpdated, Executor executor) {
        this.agentUUID = uuid;
        this.listener = new WeakReference<>(onChatterNameUpdated);
        this.executor = executor;
    }

    public String addChatter(UUID uuid) {
        ChatterNameRetriever chatterNameRetriever;
        ChatterNameRetriever put;
        synchronized (this.lock) {
            chatterNameRetriever = this.retrievers.get(uuid);
        }
        if (chatterNameRetriever != null) {
            return chatterNameRetriever.getResolvedName();
        }
        ChatterNameRetriever chatterNameRetriever2 = new ChatterNameRetriever(ChatterID.getUserChatterID(this.agentUUID, uuid), this, this.executor);
        synchronized (this.lock) {
            put = this.retrievers.put(uuid, chatterNameRetriever2);
        }
        if (put != null) {
            put.dispose();
        }
        return chatterNameRetriever2.getResolvedName();
    }

    public void clearChatters() {
        HashSet<ChatterNameRetriever> hashSet = null;
        synchronized (this.lock) {
            Iterator<Map.Entry<UUID, ChatterNameRetriever>> it = this.retrievers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ChatterNameRetriever> next = it.next();
                if (hashSet == null) {
                    hashSet = new HashSet();
                }
                hashSet.add(next.getValue());
                it.remove();
            }
        }
        if (hashSet != null) {
            for (ChatterNameRetriever chatterNameRetriever : hashSet) {
                chatterNameRetriever.dispose();
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        OnChatterNameUpdated onChatterNameUpdated = this.listener.get();
        if (onChatterNameUpdated != null) {
            onChatterNameUpdated.onChatterNameUpdated(this);
        }
    }

    public void retainChatters(Set<UUID> set) {
        HashSet<ChatterNameRetriever> hashSet = null;
        synchronized (this.lock) {
            Iterator<Map.Entry<UUID, ChatterNameRetriever>> it = this.retrievers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, ChatterNameRetriever> next = it.next();
                if (!set.contains(next.getKey())) {
                    if (hashSet == null) {
                        hashSet = new HashSet();
                    }
                    hashSet.add(next.getValue());
                    it.remove();
                }
                hashSet = hashSet;
            }
        }
        if (hashSet != null) {
            for (ChatterNameRetriever chatterNameRetriever : hashSet) {
                chatterNameRetriever.dispose();
            }
        }
    }
}
