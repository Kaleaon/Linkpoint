package com.linkpoint.cloud.common

import android.os.Bundle
import android.os.Parcelable
import com.google.common.collect.ImmutableList
import java.util.UUID

class LogMessageBatch : Bundleable {
    val agentName: String
    val agentUUID: UUID
    val lastMessageID: Long
    val messages: ImmutableList<LogChatMessage>

    constructor(bundle: Bundle) {
        this.agentUUID = UUID.fromString(bundle.getString("agentUUID"))
        this.agentName = bundle.getString("agentName")!!
        this.lastMessageID = bundle.getLong("lastMessageID")
        
        val parcelableArray = bundle.getParcelableArray("messages")
        val builder = ImmutableList.builder<LogChatMessage>()
        
        if (parcelableArray != null) {
            for (parcelable in parcelableArray) {
                if (parcelable is Bundle) {
                    builder.add(LogChatMessage(parcelable))
                }
            }
        }
        this.messages = builder.build()
    }

    constructor(uuid: UUID, name: String, messageList: List<LogChatMessage>, lastMessageID: Long) {
        this.agentUUID = uuid
        this.agentName = name
        this.messages = ImmutableList.copyOf(messageList)
        this.lastMessageID = lastMessageID
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putString("agentUUID", agentUUID.toString())
            putString("agentName", agentName)
            putLong("lastMessageID", lastMessageID)
            
            val parcelableArr = Array<Parcelable>(messages.size) { i ->
                messages[i].toBundle()
            }
            putParcelableArray("messages", parcelableArr)
        }
    }
}