package com.lumiyaviewer.lumiya.slproto.types

import java.util.UUID

object UUIDPool {
    @JvmField
    val ZeroUUID: UUID = UUID(0, 0)
    
    @JvmStatic
    fun fromString(str: String): UUID {
        return try {
            UUID.fromString(str)
        } catch (e: Exception) {
            ZeroUUID
        }
    }
}
