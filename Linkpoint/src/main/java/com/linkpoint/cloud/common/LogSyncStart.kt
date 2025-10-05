package com.linkpoint.cloud.common

import android.os.Bundle
import java.util.UUID

class LogSyncStart : Bundleable {
    val agentUUID: UUID
    val appVersionCode: Int

    constructor(appVersionCode: Int, agentUUID: UUID) {
        this.appVersionCode = appVersionCode
        this.agentUUID = agentUUID
    }

    constructor(bundle: Bundle) {
        appVersionCode = bundle.getInt("appVersionCode")
        agentUUID = UUID.fromString(bundle.getString("agentUUID"))
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putInt("appVersionCode", appVersionCode)
            putString("agentUUID", agentUUID.toString())
        }
    }
}