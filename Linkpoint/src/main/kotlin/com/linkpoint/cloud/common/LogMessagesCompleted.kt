package com.linkpoint.cloud.common

import android.os.Bundle
import java.util.UUID

class LogMessagesCompleted : Bundleable {
    val agentUUID: UUID
    val lastWrittenMessageID: Long

    constructor(bundle: Bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"))
        this.lastWrittenMessageID = bundle.getLong("lastWrittenMessageID")
    }

    constructor(uuid: UUID, lastWrittenMessageID: Long) {
        this.agentUUID = uuid
        this.lastWrittenMessageID = lastWrittenMessageID
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString("agentUUID", agentUUID.toString())
            putLong("lastWrittenMessageID", lastWrittenMessageID)
        }
    }
}