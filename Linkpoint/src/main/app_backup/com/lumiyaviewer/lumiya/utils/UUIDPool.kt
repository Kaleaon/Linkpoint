package com.lumiyaviewer.lumiya.utils

import java.util.UUID

object UUIDPool {
    val ZeroUUID = UUID(0, 0)

    fun getUUID(mostSig: Long, leastSig: Long): UUID {
        return UUID(mostSig, leastSig)
    }

    fun getUUID(str: String?): UUID? {
        if (str == null) return null
        try {
            return UUID.fromString(str)
        } catch (e: IllegalArgumentException) {
            return null
        }
    }
}
