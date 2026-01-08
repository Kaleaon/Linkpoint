package com.linkpoint.slproto.users

import com.linkpoint.slproto.users.ChatterNameRetriever
import java.lang.ref.WeakReference
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.Executor
import androidx.annotation.Nullable

class MultipleChatterNameRetriever : ChatterNameRetriever.OnChatterNameUpdated {
    private UUID agentUUID
    @Nullable
    private Executor executor
    private WeakReference<OnChatterNameUpdated> listener
    private Any lock = Any()
    private Map<UUID, ChatterNameRetriever> retrievers = HashMap()

    interface OnChatterNameUpdated {
        fun onChatterNameUpdated(MultipleChatterNameRetriever multipleChatterNameRetriever)
    }

    MultipleChatterNameRetriever(UUID uuid, OnChatterNameUpdated onChatterNameUpdated, Executor executor2) {
        this.agentUUID = uuid
        this.listener = WeakReference<>(onChatterNameUpdated)
        this.executor = executor2
    }

    fun addChatter(UUID uuid): String {
        ChatterNameRetriever chatterNameRetriever
        ChatterNameRetriever put
        synchronized (this.lock) {
            chatterNameRetriever = this.retrievers.get(uuid)
        }
        if (chatterNameRetriever != null) {
            return chatterNameRetriever.getResolvedName()
        }
        ChatterNameRetriever chatterNameRetriever2 = ChatterNameRetriever(ChatterID.getUserChatterID(this.agentUUID, uuid), this, this.executor)
        synchronized (this.lock) {
            put = this.retrievers.put(uuid, chatterNameRetriever2)
        }
        if (put != null) {
            put.dispose()
        }
        return chatterNameRetriever2.getResolvedName()
    }

    fun clearChatters(): Unit {
        HashSet<ChatterNameRetriever> hashSet = null
        synchronized (this.lock) {
            Iterator<Map.Entry<UUID, ChatterNameRetriever>> it = this.retrievers.entrySet().iterator()
            while (it.hasNext()) {
                Map.Entry next = it.next()
                if (hashSet == null) {
                    hashSet = HashSet<>()
                }
                hashSet.add((next as ChatterNameRetriever).getValue())
                it.remove()
            }
        }
        if (hashSet != null) {
            for (ChatterNameRetriever dispose : hashSet) {
                dispose.dispose()
            }
        }
    }

    fun onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever): Unit {
        OnChatterNameUpdated onChatterNameUpdated = (this as OnChatterNameUpdated).listener.get()
        if (onChatterNameUpdated != null) {
            onChatterNameUpdated.onChatterNameUpdated(this)
        }
    }

    fun retainChatters(Set<UUID> set): Unit {
        HashSet<ChatterNameRetriever> hashSet = null
        synchronized (this.lock) {
            Iterator<Map.Entry<UUID, ChatterNameRetriever>> it = this.retrievers.entrySet().iterator()
            while (it.hasNext()) {
                Map.Entry next = it.next()
                if (!set.contains(next.getKey())) {
                    if (hashSet == null) {
                        hashSet = HashSet<>()
                    }
                    hashSet.add((next as ChatterNameRetriever).getValue())
                    it.remove()
                }
                hashSet = hashSet
            }
        }
        if (hashSet != null) {
            for (ChatterNameRetriever dispose : hashSet) {
                dispose.dispose()
            }
        }
    }
}
