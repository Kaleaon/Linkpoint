package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.linkpoint.ui.common.ChatterFragment
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.Date
import java.util.UUID

class ChatMessageDao : AbstractDao<ChatMessage, Long> {
    val TABLENAME: String = "CHAT_MESSAGE"

    class Properties {
        Property Accepted = Property(22, Boolean.class, "accepted", false, "ACCEPTED")
        Property AssetType = Property(17, Int.class, "assetType", false, "ASSET_TYPE")
        Property ChatChannel = Property(20, Int.class, "chatChannel", false, "CHAT_CHANNEL")
        Property ChatterID = Property(1, Long.TYPE, ChatterFragment.CHATTER_ID_KEY, false, "CHATTER_ID")
        Property DialogButtons = Property(26, ByteArray.class, "dialogButtons", false, "DIALOG_BUTTONS")
        Property DialogIgnored = Property(21, Boolean.class, "dialogIgnored", false, "DIALOG_IGNORED")
        Property DialogSelectedOption = Property(27, String.class, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION")
        Property EventState = Property(12, Int.class, "eventState", false, "EVENT_STATE")
        Property Id = Property(0, Long.class, "id", true, "_id")
        Property IsOffline = Property(5, Boolean.class, "isOffline", false, "IS_OFFLINE")
        Property ItemID = Property(15, UUID.class, "itemID", false, "ITEM_ID")
        Property ItemName = Property(16, String.class, "itemName", false, "ITEM_NAME")
        Property MessageText = Property(10, String.class, "messageText", false, "MESSAGE_TEXT")
        Property MessageType = Property(11, Int.TYPE, "messageType", false, "MESSAGE_TYPE")
        Property NewBalance = Property(19, Int.class, "newBalance", false, "NEW_BALANCE")
        Property ObjectName = Property(24, String.class, "objectName", false, "OBJECT_NAME")
        Property OrigIMType = Property(13, Int.class, "origIMType", false, "ORIG_IMTYPE")
        Property OrigTimestamp = Property(4, Date.class, "origTimestamp", false, "ORIG_TIMESTAMP")
        Property QuestionMask = Property(25, Int.class, "questionMask", false, "QUESTION_MASK")
        Property SenderLegacyName = Property(9, String.class, "senderLegacyName", false, "SENDER_LEGACY_NAME")
        Property SenderName = Property(8, String.class, "senderName", false, "SENDER_NAME")
        Property SenderType = Property(7, Int.class, "senderType", false, "SENDER_TYPE")
        Property SenderUUID = Property(6, UUID.class, "senderUUID", false, "SENDER_UUID")
        Property SessionID = Property(14, UUID.class, "sessionID", false, "SESSION_ID")
        Property SyncedToGoogleDrive = Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE")
        Property TextBoxButtonIndex = Property(28, Int.class, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX")
        Property Timestamp = Property(2, Date.class, "timestamp", false, "TIMESTAMP")
        Property TransactionAmount = Property(18, Int.class, "transactionAmount", false, "TRANSACTION_AMOUNT")
        Property UserID = Property(23, UUID.class, "userID", false, "USER_ID")
        Property ViewType = Property(3, Int.TYPE, "viewType", false, "VIEW_TYPE")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHAT_MESSAGE' (" + "'_id' INTEGER PRIMARY KEY ," + "'CHATTER_ID' INTEGER NOT NULL ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'VIEW_TYPE' INTEGER NOT NULL ," + "'ORIG_TIMESTAMP' INTEGER," + "'IS_OFFLINE' INTEGER," + "'SENDER_UUID' TEXT," + "'SENDER_TYPE' INTEGER," + "'SENDER_NAME' TEXT," + "'SENDER_LEGACY_NAME' TEXT," + "'MESSAGE_TEXT' TEXT," + "'MESSAGE_TYPE' INTEGER NOT NULL ," + "'EVENT_STATE' INTEGER," + "'ORIG_IMTYPE' INTEGER," + "'SESSION_ID' TEXT," + "'ITEM_ID' TEXT," + "'ITEM_NAME' TEXT," + "'ASSET_TYPE' INTEGER," + "'TRANSACTION_AMOUNT' INTEGER," + "'NEW_BALANCE' INTEGER," + "'CHAT_CHANNEL' INTEGER," + "'DIALOG_IGNORED' INTEGER," + "'ACCEPTED' INTEGER," + "'USER_ID' TEXT," + "'OBJECT_NAME' TEXT," + "'QUESTION_MASK' INTEGER," + "'DIALOG_BUTTONS' BLOB," + "'DIALOG_SELECTED_OPTION' TEXT," + "'TEXT_BOX_BUTTON_INDEX' INTEGER," + "'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE" + " (CHATTER_ID);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE" + " (_id,SYNCED_TO_GOOGLE_DRIVE);")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHAT_MESSAGE'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, chatMessage: ChatMessage): Unit {
        Long j = 1
        sQLiteStatement.clearBindings()
        Long id = chatMessage.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, chatMessage.getChatterID())
        sQLiteStatement.bindLong(3, chatMessage.getTimestamp().getTime())
        sQLiteStatement.bindLong(4, (Long) chatMessage.getViewType())
        Date origTimestamp = chatMessage.getOrigTimestamp()
        if (origTimestamp != null) {
            sQLiteStatement.bindLong(5, origTimestamp.getTime())
        }
        Boolean isOffline = chatMessage.getIsOffline()
        if (isOffline != null) {
            sQLiteStatement.bindLong(6, isOffline.booleanValue() ? 1 : 0)
        }
        UUID senderUUID = chatMessage.getSenderUUID()
        if (senderUUID != null) {
            sQLiteStatement.bindString(7, senderUUID.toString())
        }
        Int senderType = chatMessage.getSenderType()
        if (senderType != null) {
            sQLiteStatement.bindLong(8, (Long) senderType.intValue())
        }
        String senderName = chatMessage.getSenderName()
        if (senderName != null) {
            sQLiteStatement.bindString(9, senderName)
        }
        senderName = chatMessage.getSenderLegacyName()
        if (senderName != null) {
            sQLiteStatement.bindString(10, senderName)
        }
        senderName = chatMessage.getMessageText()
        if (senderName != null) {
            sQLiteStatement.bindString(11, senderName)
        }
        sQLiteStatement.bindLong(12, (Long) chatMessage.getMessageType())
        senderType = chatMessage.getEventState()
        if (senderType != null) {
            sQLiteStatement.bindLong(13, (Long) senderType.intValue())
        }
        senderType = chatMessage.getOrigIMType()
        if (senderType != null) {
            sQLiteStatement.bindLong(14, (Long) senderType.intValue())
        }
        senderUUID = chatMessage.getSessionID()
        if (senderUUID != null) {
            sQLiteStatement.bindString(15, senderUUID.toString())
        }
        senderUUID = chatMessage.getItemID()
        if (senderUUID != null) {
            sQLiteStatement.bindString(16, senderUUID.toString())
        }
        senderName = chatMessage.getItemName()
        if (senderName != null) {
            sQLiteStatement.bindString(17, senderName)
        }
        senderType = chatMessage.getAssetType()
        if (senderType != null) {
            sQLiteStatement.bindLong(18, (Long) senderType.intValue())
        }
        senderType = chatMessage.getTransactionAmount()
        if (senderType != null) {
            sQLiteStatement.bindLong(19, (Long) senderType.intValue())
        }
        senderType = chatMessage.getNewBalance()
        if (senderType != null) {
            sQLiteStatement.bindLong(20, (Long) senderType.intValue())
        }
        senderType = chatMessage.getChatChannel()
        if (senderType != null) {
            sQLiteStatement.bindLong(21, (Long) senderType.intValue())
        }
        isOffline = chatMessage.getDialogIgnored()
        if (isOffline != null) {
            sQLiteStatement.bindLong(22, isOffline.booleanValue() ? 1 : 0)
        }
        isOffline = chatMessage.getAccepted()
        if (isOffline != null) {
            sQLiteStatement.bindLong(23, isOffline.booleanValue() ? 1 : 0)
        }
        senderUUID = chatMessage.getUserID()
        if (senderUUID != null) {
            sQLiteStatement.bindString(24, senderUUID.toString())
        }
        senderName = chatMessage.getObjectName()
        if (senderName != null) {
            sQLiteStatement.bindString(25, senderName)
        }
        senderType = chatMessage.getQuestionMask()
        if (senderType != null) {
            sQLiteStatement.bindLong(26, (Long) senderType.intValue())
        }
        ByteArray dialogButtons = chatMessage.getDialogButtons()
        if (dialogButtons != null) {
            sQLiteStatement.bindBlob(27, dialogButtons)
        }
        senderName = chatMessage.getDialogSelectedOption()
        if (senderName != null) {
            sQLiteStatement.bindString(28, senderName)
        }
        senderType = chatMessage.getTextBoxButtonIndex()
        if (senderType != null) {
            sQLiteStatement.bindLong(29, (Long) senderType.intValue())
        }
        if (!chatMessage.getSyncedToGoogleDrive()) {
            j = 0
        }
        sQLiteStatement.bindLong(30, j)
    }

    fun getKey(chatMessage: ChatMessage): Long {
        return chatMessage != null ? chatMessage.getId() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): ChatMessage {
        Boolean bool
        Boolean bool2
        Boolean bool3
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        Long j = cursor.getLong(i + 1)
        Date date = Date(cursor.getLong(i + 2))
        Int i2 = cursor.getInt(i + 3)
        Date date2 = cursor.isNull(i + 4) ? null : Date(cursor.getLong(i + 4))
        if (cursor.isNull(i + 5)) {
            bool = null
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 5) != (Short) 0)
        }
        UUID fromString = cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6))
        Int valueOf2 = cursor.isNull(i + 7) ? null : Int.valueOf(cursor.getInt(i + 7))
        String string = cursor.isNull(i + 8) ? null : cursor.getString(i + 8)
        String string2 = cursor.isNull(i + 9) ? null : cursor.getString(i + 9)
        String string3 = cursor.isNull(i + 10) ? null : cursor.getString(i + 10)
        Int i3 = cursor.getInt(i + 11)
        Int valueOf3 = cursor.isNull(i + 12) ? null : Int.valueOf(cursor.getInt(i + 12))
        Int valueOf4 = cursor.isNull(i + 13) ? null : Int.valueOf(cursor.getInt(i + 13))
        UUID fromString2 = cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14))
        UUID fromString3 = cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15))
        String string4 = cursor.isNull(i + 16) ? null : cursor.getString(i + 16)
        Int valueOf5 = cursor.isNull(i + 17) ? null : Int.valueOf(cursor.getInt(i + 17))
        Int valueOf6 = cursor.isNull(i + 18) ? null : Int.valueOf(cursor.getInt(i + 18))
        Int valueOf7 = cursor.isNull(i + 19) ? null : Int.valueOf(cursor.getInt(i + 19))
        Int valueOf8 = cursor.isNull(i + 20) ? null : Int.valueOf(cursor.getInt(i + 20))
        if (cursor.isNull(i + 21)) {
            bool2 = null
        } else {
            bool2 = Boolean.valueOf(cursor.getShort(i + 21) != (Short) 0)
        }
        if (cursor.isNull(i + 22)) {
            bool3 = null
        } else {
            bool3 = Boolean.valueOf(cursor.getShort(i + 22) != (Short) 0)
        }
        return ChatMessage(valueOf, j, date, i2, date2, bool, fromString, valueOf2, string, string2, string3, i3, valueOf3, valueOf4, fromString2, fromString3, string4, valueOf5, valueOf6, valueOf7, valueOf8, bool2, bool3, cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)), cursor.isNull(i + 24) ? null : cursor.getString(i + 24), cursor.isNull(i + 25) ? null : Int.valueOf(cursor.getInt(i + 25)), cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26), cursor.isNull(i + 27) ? null : cursor.getString(i + 27), cursor.isNull(i + 28) ? null : Int.valueOf(cursor.getInt(i + 28)), cursor.getShort(i + 29) != (Short) 0)
    }

    fun readEntity(cursor: Cursor, chatMessage: ChatMessage, i: Int): Unit {
        Boolean bool
        Boolean z = true
        Int num = null
        chatMessage.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        chatMessage.setChatterID(cursor.getLong(i + 1))
        chatMessage.setTimestamp(Date(cursor.getLong(i + 2)))
        chatMessage.setViewType(cursor.getInt(i + 3))
        chatMessage.setOrigTimestamp(cursor.isNull(i + 4) ? null : Date(cursor.getLong(i + 4)))
        if (cursor.isNull(i + 5)) {
            bool = null
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 5) != (Short) 0)
        }
        chatMessage.setIsOffline(bool)
        chatMessage.setSenderUUID(cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6)))
        chatMessage.setSenderType(cursor.isNull(i + 7) ? null : Int.valueOf(cursor.getInt(i + 7)))
        chatMessage.setSenderName(cursor.isNull(i + 8) ? null : cursor.getString(i + 8))
        chatMessage.setSenderLegacyName(cursor.isNull(i + 9) ? null : cursor.getString(i + 9))
        chatMessage.setMessageText(cursor.isNull(i + 10) ? null : cursor.getString(i + 10))
        chatMessage.setMessageType(cursor.getInt(i + 11))
        chatMessage.setEventState(cursor.isNull(i + 12) ? null : Int.valueOf(cursor.getInt(i + 12)))
        chatMessage.setOrigIMType(cursor.isNull(i + 13) ? null : Int.valueOf(cursor.getInt(i + 13)))
        chatMessage.setSessionID(cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14)))
        chatMessage.setItemID(cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15)))
        chatMessage.setItemName(cursor.isNull(i + 16) ? null : cursor.getString(i + 16))
        chatMessage.setAssetType(cursor.isNull(i + 17) ? null : Int.valueOf(cursor.getInt(i + 17)))
        chatMessage.setTransactionAmount(cursor.isNull(i + 18) ? null : Int.valueOf(cursor.getInt(i + 18)))
        chatMessage.setNewBalance(cursor.isNull(i + 19) ? null : Int.valueOf(cursor.getInt(i + 19)))
        chatMessage.setChatChannel(cursor.isNull(i + 20) ? null : Int.valueOf(cursor.getInt(i + 20)))
        if (cursor.isNull(i + 21)) {
            bool = null
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 21) != (Short) 0)
        }
        chatMessage.setDialogIgnored(bool)
        if (cursor.isNull(i + 22)) {
            bool = null
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 22) != (Short) 0)
        }
        chatMessage.setAccepted(bool)
        chatMessage.setUserID(cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)))
        chatMessage.setObjectName(cursor.isNull(i + 24) ? null : cursor.getString(i + 24))
        chatMessage.setQuestionMask(cursor.isNull(i + 25) ? null : Int.valueOf(cursor.getInt(i + 25)))
        chatMessage.setDialogButtons(cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26))
        chatMessage.setDialogSelectedOption(cursor.isNull(i + 27) ? null : cursor.getString(i + 27))
        if (!cursor.isNull(i + 28)) {
            num = Int.valueOf(cursor.getInt(i + 28))
        }
        chatMessage.setTextBoxButtonIndex(num)
        if (cursor.getShort(i + 29) == (Short) 0) {
            z = false
        }
        chatMessage.setSyncedToGoogleDrive(z)
    }

    fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected fun updateKeyAfterInsert(chatMessage: ChatMessage, j: Long): Long {
        chatMessage.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
