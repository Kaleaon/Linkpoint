package com.lumiyaviewer.lumiya.slproto

import java.util.Map
import java.util.UUID
import java.util.WeakHashMap
import javax.annotation.Nonnull
import javax.annotation.Nullable

object GridConnectionManager {
    private Map<UUID, SLGridConnection> connections = WeakHashMap()
    private Object lock = Object()

    @Nullable
    fun getConnection(uuid: UUID): SLGridConnection {
        if (uuid == null) {
            return null
        }
        SLGridConnection sLGridConnection
        synchronized (lock) {
            sLGridConnection = (SLGridConnection) connections.get(uuid)
        }
        return sLGridConnection
    }

    fun removeConnection(uuid: UUID, sLGridConnection: SLGridConnection): Unit {
        synchronized (lock) {
            if (((SLGridConnection) connections.get(uuid)) == sLGridConnection) {
                connections.remove(uuid)
            }
        }
    }

    fun setConnection(uuid: UUID, sLGridConnection: SLGridConnection): Unit {
        synchronized (lock) {
            connections.put(uuid, sLGridConnection)
        }
    }
}
