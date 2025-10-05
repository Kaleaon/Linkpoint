package com.linkpoint.cloud.common

import android.os.Bundle
import com.google.common.base.Strings
import java.util.UUID

class LogFlushMessages : Bundleable {
    val agentUUID: UUID
    val agentName: String?
    val chatterName: String?

    constructor(bundle: Bundle) {
        agentUUID = UUID.fromString(bundle.getString("agentUUID"))
        agentName = Strings.nullToEmpty(bundle.getString("agentName"))
        chatterName = bundle.getString("chatterName")
    }

    constructor(agentUUID: UUID, agentName: String?, chatterName: String?) {
        this.agentUUID = agentUUID
        this.agentName = agentName
        this.chatterName = chatterName
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString("agentUUID", agentUUID.toString())
            putString("agentName", agentName)
            putString("chatterName", chatterName)
        }
    }
}