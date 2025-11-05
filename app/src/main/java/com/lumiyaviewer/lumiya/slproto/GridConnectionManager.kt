package com.lumiyaviewer.lumiya.slproto

import java.util.UUID
import java.util.WeakHashMap

/**
 * Tracks active grid connections by agent UUID using weak references.
 */
object GridConnectionManager {

    private val connections = WeakHashMap<UUID, SLGridConnection>()
    private val lock = Any()

    @JvmStatic
    fun getConnection(uuid: UUID?): SLGridConnection? {
        if (uuid == null) return null
        synchronized(lock) {
            return connections[uuid]
        }
    }

    @JvmStatic
    fun setConnection(uuid: UUID, connection: SLGridConnection) {
        synchronized(lock) {
            connections[uuid] = connection
        }
    }

    @JvmStatic
    fun removeConnection(uuid: UUID?, connection: SLGridConnection?) {
        if (uuid == null || connection == null) return
        synchronized(lock) {
            val current = connections[uuid]
            if (current == connection) {
                connections.remove(uuid)
            }
        }
    }
}
