package com.linkpoint.cloud

import android.os.Messenger
import com.linkpoint.cloud.common.Bundleable
import com.linkpoint.cloud.common.CloudSyncMessenger
import com.linkpoint.cloud.common.MessageType
import java.util.HashMap
import java.util.UUID

class AgentSyncConnections {
    private val messengers: MutableMap<UUID, Messenger> = HashMap()

    fun addSyncConnection(uuid: UUID, messenger: Messenger) {
        messengers[uuid] = messenger
    }

    fun sendMessage(uuid: UUID, messageType: MessageType, bundleable: Bundleable, messenger: Messenger): Boolean {
        val targetMessenger = messengers[uuid] ?: return false
        
        if (CloudSyncMessenger.sendMessage(targetMessenger, messageType, bundleable, messenger)) {
            return true
        }
        
        messengers.remove(uuid)
        return false
    }
}