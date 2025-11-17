package com.linkpoint.dao

import java.util.Date
import java.util.UUID

data class ChatMessage(
    var accepted: Boolean = false,
    var assetType: Int = 0,
    var chatChannel: Int = 0,
    var chatterID: Long = 0L,
    var dialogIgnored: Boolean = false,
    var dialogSelectedOption: String,
    var eventState: Int = 0,
    var id: Long = 0L,
    var isOffline: Boolean = false,
    var itemID: UUID,
    var itemName: String,
    var messageText: String,
    var messageType: Int = 0,
    var newBalance: Int = 0,
    var objectName: String,
    var origIMType: Int = 0,
    var origTimestamp: Date,
    var questionMask: Int = 0,
    var senderLegacyName: String,
    var senderName: String,
    var senderType: Int = 0,
    var senderUUID: UUID,
    var sessionID: UUID,
    var syncedToGoogleDrive: Boolean = false,
    var textBoxButtonIndex: Int = 0,
    var timestamp: Date,
    var transactionAmount: Int = 0,
    var userID: UUID,
    var viewType: Int = 0,
)
