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

class ChatMessageDao : AbstractDao()<ChatMessage, Long> {
    const val TABLENAME: String = "CHAT_MESSAGE"

    @JvmStatic
    class Properties {
        const val Property Accepted = Property(22, Boolean.class, "accepted", false, "ACCEPTED")
        const val Property AssetType = Property(17, Integer.class, "assetType", false, "ASSET_TYPE")
        const val Property ChatChannel = Property(20, Integer.class, "chatChannel", false, "CHAT_CHANNEL")
        const val Property ChatterID = Property(1, Long.TYPE, ChatterFragment.CHATTER_ID_KEY, false, "CHATTER_ID")
        const val Property DialogButtons = Property(26, ByteArray.class, "dialogButtons", false, "DIALOG_BUTTONS")
        const val Property DialogIgnored = Property(21, Boolean.class, "dialogIgnored", false, "DIALOG_IGNORED")
        const val Property DialogSelectedOption = Property(27, String.class, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION")
        const val Property EventState = Property(12, Integer.class, "eventState", false, "EVENT_STATE")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property IsOffline = Property(5, Boolean.class, "isOffline", false, "IS_OFFLINE")
        const val Property ItemID = Property(15, UUID.class, "itemID", false, "ITEM_ID")
        const val Property ItemName = Property(16, String.class, "itemName", false, "ITEM_NAME")
        const val Property MessageText = Property(10, String.class, "messageText", false, "MESSAGE_TEXT")
        const val Property MessageType = Property(11, Integer.TYPE, "messageType", false, "MESSAGE_TYPE")
        const val Property NewBalance = Property(19, Integer.class, "newBalance", false, "NEW_BALANCE")
        const val Property ObjectName = Property(24, String.class, "objectName", false, "OBJECT_NAME")
        const val Property OrigIMType = Property(13, Integer.class, "origIMType", false, "ORIG_IMTYPE")
        const val Property OrigTimestamp = Property(4, Date.class, "origTimestamp", false, "ORIG_TIMESTAMP")
        const val Property QuestionMask = Property(25, Integer.class, "questionMask", false, "QUESTION_MASK")
        const val Property SenderLegacyName = Property(9, String.class, "senderLegacyName", false, "SENDER_LEGACY_NAME")
        const val Property SenderName = Property(8, String.class, "senderName", false, "SENDER_NAME")
        const val Property SenderType = Property(7, Integer.class, "senderType", false, "SENDER_TYPE")
        const val Property SenderUUID = Property(6, UUID.class, "senderUUID", false, "SENDER_UUID")
        const val Property SessionID = Property(14, UUID.class, "sessionID", false, "SESSION_ID")
        const val Property SyncedToGoogleDrive = Property(29, Boolean.TYPE, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE")
        const val Property TextBoxButtonIndex = Property(28, Integer.class, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX")
        const val Property Timestamp = Property(2, Date.class, "timestamp", false, "TIMESTAMP")
        const val Property TransactionAmount = Property(18, Integer.class, "transactionAmount", false, "TRANSACTION_AMOUNT")
        const val Property UserID = Property(23, UUID.class, "userID", false, "USER_ID")
        const val Property ViewType = Property(3, Integer.TYPE, "viewType", false, "VIEW_TYPE")
    }

    public ChatMessageDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public ChatMessageDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHAT_MESSAGE' (" + "'_id' INTEGER PRIMARY KEY ," + "'CHATTER_ID' INTEGER NOT NULL ," + "'TIMESTAMP' INTEGER NOT NULL ," + "'VIEW_TYPE' INTEGER NOT NULL ," + "'ORIG_TIMESTAMP' INTEGER," + "'IS_OFFLINE' INTEGER," + "'SENDER_UUID' TEXT," + "'SENDER_TYPE' INTEGER," + "'SENDER_NAME' TEXT," + "'SENDER_LEGACY_NAME' TEXT," + "'MESSAGE_TEXT' TEXT," + "'MESSAGE_TYPE' INTEGER NOT NULL ," + "'EVENT_STATE' INTEGER," + "'ORIG_IMTYPE' INTEGER," + "'SESSION_ID' TEXT," + "'ITEM_ID' TEXT," + "'ITEM_NAME' TEXT," + "'ASSET_TYPE' INTEGER," + "'TRANSACTION_AMOUNT' INTEGER," + "'NEW_BALANCE' INTEGER," + "'CHAT_CHANNEL' INTEGER," + "'DIALOG_IGNORED' INTEGER," + "'ACCEPTED' INTEGER," + "'USER_ID' TEXT," + "'OBJECT_NAME' TEXT," + "'QUESTION_MASK' INTEGER," + "'DIALOG_BUTTONS' BLOB," + "'DIALOG_SELECTED_OPTION' TEXT," + "'TEXT_BOX_BUTTON_INDEX' INTEGER," + "'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE" + " (CHATTER_ID);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE" + " (_id,SYNCED_TO_GOOGLE_DRIVE);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHAT_MESSAGE'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, chatMessage: ChatMessage) {
        val j: Long = 1
        sQLiteStatement.clearBindings()
        val id: Long = chatMessage.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, chatMessage.getChatterID())
        sQLiteStatement.bindLong(3, chatMessage.getTimestamp().getTime())
        sQLiteStatement.bindLong(4, (Long) chatMessage.getViewType())
        val origTimestamp: Date = chatMessage.getOrigTimestamp()
        if (origTimestamp != null) {
            sQLiteStatement.bindLong(5, origTimestamp.getTime())
        }
        val isOffline: Boolean = chatMessage.getIsOffline()
        if (isOffline != null) {
            sQLiteStatement.bindLong(6, isOffline.booleanValue() ? 1 : 0)
        }
        val senderUUID: UUID = chatMessage.getSenderUUID()
        if (senderUUID != null) {
            sQLiteStatement.bindString(7, senderUUID.toString())
        }
        val senderType: Integer = chatMessage.getSenderType()
        if (senderType != null) {
            sQLiteStatement.bindLong(8, (Long) senderType.intValue())
        }
        val senderName: String = chatMessage.getSenderName()
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
        val dialogButtons: ByteArray = chatMessage.getDialogButtons()
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

     public fun getKey(chatMessage: ChatMessage): Long {
        return chatMessage != null ? chatMessage.getId() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): ChatMessage {
        Boolean bool
        Boolean bool2
        Boolean bool3
        val valueOf: Long = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        val j: Long = cursor.getLong(i + 1)
        val date: Date = Date(cursor.getLong(i + 2))
        val i2: Int = cursor.getInt(i + 3)
        val date2: Date = cursor.isNull(i + 4) ? null : Date(cursor.getLong(i + 4))
        if (cursor.isNull(i + 5)) {
            bool = null
        } else {
            bool = Boolean.valueOf(cursor.getShort(i + 5) != (Short) 0)
        }
        val fromString: UUID = cursor.isNull(i + 6) ? null : UUID.fromString(cursor.getString(i + 6))
        val valueOf2: Integer = cursor.isNull(i + 7) ? null : Integer.valueOf(cursor.getInt(i + 7))
        val string: String = cursor.isNull(i + 8) ? null : cursor.getString(i + 8)
        val string2: String = cursor.isNull(i + 9) ? null : cursor.getString(i + 9)
        val string3: String = cursor.isNull(i + 10) ? null : cursor.getString(i + 10)
        val i3: Int = cursor.getInt(i + 11)
        val valueOf3: Integer = cursor.isNull(i + 12) ? null : Integer.valueOf(cursor.getInt(i + 12))
        val valueOf4: Integer = cursor.isNull(i + 13) ? null : Integer.valueOf(cursor.getInt(i + 13))
        val fromString2: UUID = cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14))
        val fromString3: UUID = cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15))
        val string4: String = cursor.isNull(i + 16) ? null : cursor.getString(i + 16)
        val valueOf5: Integer = cursor.isNull(i + 17) ? null : Integer.valueOf(cursor.getInt(i + 17))
        val valueOf6: Integer = cursor.isNull(i + 18) ? null : Integer.valueOf(cursor.getInt(i + 18))
        val valueOf7: Integer = cursor.isNull(i + 19) ? null : Integer.valueOf(cursor.getInt(i + 19))
        val valueOf8: Integer = cursor.isNull(i + 20) ? null : Integer.valueOf(cursor.getInt(i + 20))
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
        return ChatMessage(valueOf, j, date, i2, date2, bool, fromString, valueOf2, string, string2, string3, i3, valueOf3, valueOf4, fromString2, fromString3, string4, valueOf5, valueOf6, valueOf7, valueOf8, bool2, bool3, cursor.isNull(i + 23) ? null : UUID.fromString(cursor.getString(i + 23)), cursor.isNull(i + 24) ? null : cursor.getString(i + 24), cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)), cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26), cursor.isNull(i + 27) ? null : cursor.getString(i + 27), cursor.isNull(i + 28) ? null : Integer.valueOf(cursor.getInt(i + 28)), cursor.getShort(i + 29) != (Short) 0)
    }

    fun readEntity(cursor: Cursor, chatMessage: ChatMessage, i: Int) {
        Boolean bool
        val z: Boolean = true
        val num: Integer = null
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
        chatMessage.setSenderType(cursor.isNull(i + 7) ? null : Integer.valueOf(cursor.getInt(i + 7)))
        chatMessage.setSenderName(cursor.isNull(i + 8) ? null : cursor.getString(i + 8))
        chatMessage.setSenderLegacyName(cursor.isNull(i + 9) ? null : cursor.getString(i + 9))
        chatMessage.setMessageText(cursor.isNull(i + 10) ? null : cursor.getString(i + 10))
        chatMessage.setMessageType(cursor.getInt(i + 11))
        chatMessage.setEventState(cursor.isNull(i + 12) ? null : Integer.valueOf(cursor.getInt(i + 12)))
        chatMessage.setOrigIMType(cursor.isNull(i + 13) ? null : Integer.valueOf(cursor.getInt(i + 13)))
        chatMessage.setSessionID(cursor.isNull(i + 14) ? null : UUID.fromString(cursor.getString(i + 14)))
        chatMessage.setItemID(cursor.isNull(i + 15) ? null : UUID.fromString(cursor.getString(i + 15)))
        chatMessage.setItemName(cursor.isNull(i + 16) ? null : cursor.getString(i + 16))
        chatMessage.setAssetType(cursor.isNull(i + 17) ? null : Integer.valueOf(cursor.getInt(i + 17)))
        chatMessage.setTransactionAmount(cursor.isNull(i + 18) ? null : Integer.valueOf(cursor.getInt(i + 18)))
        chatMessage.setNewBalance(cursor.isNull(i + 19) ? null : Integer.valueOf(cursor.getInt(i + 19)))
        chatMessage.setChatChannel(cursor.isNull(i + 20) ? null : Integer.valueOf(cursor.getInt(i + 20)))
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
        chatMessage.setQuestionMask(cursor.isNull(i + 25) ? null : Integer.valueOf(cursor.getInt(i + 25)))
        chatMessage.setDialogButtons(cursor.isNull(i + 26) ? null : cursor.getBlob(i + 26))
        chatMessage.setDialogSelectedOption(cursor.isNull(i + 27) ? null : cursor.getString(i + 27))
        if (!cursor.isNull(i + 28)) {
            num = Integer.valueOf(cursor.getInt(i + 28))
        }
        chatMessage.setTextBoxButtonIndex(num)
        if (cursor.getShort(i + 29) == (Short) 0) {
            z = false
        }
        chatMessage.setSyncedToGoogleDrive(z)
    }

     public fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

     protected fun updateKeyAfterInsert(chatMessage: ChatMessage, j: Long): Long {
        chatMessage.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
