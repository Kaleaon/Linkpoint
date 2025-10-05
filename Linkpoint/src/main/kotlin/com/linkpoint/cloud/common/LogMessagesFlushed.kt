package com.linkpoint.cloud.common

import android.os.Bundle
import com.google.common.collect.ImmutableList
import com.google.common.primitives.Longs
import java.util.UUID

class LogMessagesFlushed : Bundleable {
    val agentUUID: UUID
    val messageIDs: ImmutableList<Long>

    constructor(bundle: Bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"))
        val longArray = bundle.getLongArray("messageIDs") ?: longArrayOf()
        this.messageIDs = ImmutableList.copyOf(Longs.asList(*longArray))
    }

    constructor(uuid: UUID, messageIDs: Collection<Long>) {
        this.agentUUID = uuid
        this.messageIDs = ImmutableList.copyOf(messageIDs)
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString("agentUUID", agentUUID.toString())
            putLongArray("messageIDs", Longs.toArray(messageIDs))
        }
    }
}