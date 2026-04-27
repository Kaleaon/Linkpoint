package com.linkpoint.slproto

import java.util.Map
import java.util.UUID
import java.util.WeakHashMap
import javax.annotation.Nonnull
import javax.annotation.Nullable

class GridConnectionManager {
    private const val Map<UUID, SLGridConnection> connections = WeakHashMap()
    private const val Object lock = Object()

    @JvmStatic
    SLGridConnection getConnection(UUID uuid) {
        if (uuid == null) {
            return null
        }
        SLGridConnection sLGridConnection
        synchronized (lock) {
            sLGridConnection = (SLGridConnection) connections.get(uuid)
        }
        return sLGridConnection
    }

    @JvmStatic
    Unit removeConnection(UUID uuid, SLGridConnection sLGridConnection) {
        synchronized (lock) {
            if (((SLGridConnection) connections.get(uuid)) == sLGridConnection) {
                connections.remove(uuid)
            }
        }
    }

    @JvmStatic
    Unit setConnection(UUID uuid, SLGridConnection sLGridConnection) {
        synchronized (lock) {
            connections.put(uuid, sLGridConnection)
        }
    }
}
