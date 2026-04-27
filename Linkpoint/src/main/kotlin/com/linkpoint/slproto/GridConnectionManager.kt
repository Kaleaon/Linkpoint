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

    @JvmStatic
     fun removeConnection(uuid: UUID, sLGridConnection: SLGridConnection) {
        synchronized (lock) {
            if (((SLGridConnection) connections.get(uuid)) == sLGridConnection) {
                connections.remove(uuid)
            }
        }
    }

    @JvmStatic
     fun setConnection(uuid: UUID, sLGridConnection: SLGridConnection) {
        synchronized (lock) {
            connections.put(uuid, sLGridConnection)
        }
    }
}
