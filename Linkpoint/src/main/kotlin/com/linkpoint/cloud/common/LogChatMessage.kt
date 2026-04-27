package com.linkpoint.cloud.common

import android.os.Bundle
import java.util.UUID

class LogChatMessage : Bundleable {
    val chatterName: String
    val chatterType: Int
    val chatterUUID: UUID?
    val messageID: Long
    val messageText: String

    constructor(chatterType: Int, chatterUUID: UUID?, messageID: Long, chatterName: String, messageText: String) {
        this.chatterType = chatterType
        this.chatterUUID = chatterUUID
        this.messageID = messageID
        this.chatterName = chatterName
        this.messageText = messageText
    }

    constructor(bundle: Bundle) {
        this.chatterType = bundle.getInt("chatterType")
        this.chatterUUID = if (!bundle.containsKey("chatterUUID")) null else UUID.fromString(bundle.getString("chatterUUID"))
        this.messageID = bundle.getLong("messageID")
        this.chatterName = bundle.getString("chatterName")!!
        this.messageText = bundle.getString("messageText")!!
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putInt("chatterType", chatterType)
            chatterUUID?.let {
                putString("chatterUUID", it.toString())
            }
            putLong("messageID", messageID)
            putString("chatterName", chatterName)
            putString("messageText", messageText)
        }
    }
}