package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import java.util.UUID

class ChatMessageDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<ChatMessage, Long>(config, daoSession) {

    // Store session explicitly to avoid visibility issues or implicit super access problems
    private val myDaoSession: DaoSession? = daoSession

    companion object {
        const val TABLENAME = "CHAT_MESSAGE"

        object Properties {
            @JvmField val Accepted = Property(22, Boolean::class.java, "accepted", false, "ACCEPTED")
            @JvmField val AssetType = Property(17, Int::class.java, "assetType", false, "ASSET_TYPE")
            @JvmField val ChatChannel = Property(20, Int::class.java, "chatChannel", false, "CHAT_CHANNEL")
            @JvmField val ChatterID = Property(1, Long::class.java, "chatterID", false, "CHATTER_ID")
            @JvmField val DialogButtons = Property(26, ByteArray::class.java, "dialogButtons", false, "DIALOG_BUTTONS")
            @JvmField val DialogIgnored = Property(21, Boolean::class.java, "dialogIgnored", false, "DIALOG_IGNORED")
            @JvmField val DialogSelectedOption = Property(27, String::class.java, "dialogSelectedOption", false, "DIALOG_SELECTED_OPTION")
            @JvmField val EventState = Property(12, Int::class.java, "eventState", false, "EVENT_STATE")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val IsOffline = Property(5, Boolean::class.java, "isOffline", false, "IS_OFFLINE")
            @JvmField val ItemID = Property(15, String::class.java, "itemID", false, "ITEM_ID")
            @JvmField val ItemName = Property(16, String::class.java, "itemName", false, "ITEM_NAME")
            @JvmField val MessageText = Property(10, String::class.java, "messageText", false, "MESSAGE_TEXT")
            @JvmField val MessageType = Property(11, Int::class.java, "messageType", false, "MESSAGE_TYPE")
            @JvmField val NewBalance = Property(19, Int::class.java, "newBalance", false, "NEW_BALANCE")
            @JvmField val ObjectName = Property(24, String::class.java, "objectName", false, "OBJECT_NAME")
            @JvmField val OrigIMType = Property(13, Int::class.java, "origIMType", false, "ORIG_IMTYPE")
            @JvmField val OrigTimestamp = Property(4, Long::class.java, "origTimestamp", false, "ORIG_TIMESTAMP")
            @JvmField val QuestionMask = Property(25, Int::class.java, "questionMask", false, "QUESTION_MASK")
            @JvmField val SenderLegacyName = Property(9, String::class.java, "senderLegacyName", false, "SENDER_LEGACY_NAME")
            @JvmField val SenderName = Property(8, String::class.java, "senderName", false, "SENDER_NAME")
            @JvmField val SenderType = Property(7, Int::class.java, "senderType", false, "SENDER_TYPE")
            @JvmField val SenderUUID = Property(6, String::class.java, "senderUUID", false, "SENDER_UUID")
            @JvmField val SessionID = Property(14, String::class.java, "sessionID", false, "SESSION_ID")
            @JvmField val SyncedToGoogleDrive = Property(29, Boolean::class.java, "syncedToGoogleDrive", false, "SYNCED_TO_GOOGLE_DRIVE")
            @JvmField val TextBoxButtonIndex = Property(28, Int::class.java, "textBoxButtonIndex", false, "TEXT_BOX_BUTTON_INDEX")
            @JvmField val Timestamp = Property(2, Long::class.java, "timestamp", false, "TIMESTAMP")
            @JvmField val TransactionAmount = Property(18, Int::class.java, "transactionAmount", false, "TRANSACTION_AMOUNT")
            @JvmField val UserID = Property(23, String::class.java, "userID", false, "USER_ID")
            @JvmField val ViewType = Property(3, Int::class.java, "viewType", false, "VIEW_TYPE")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'CHAT_MESSAGE' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'CHATTER_ID' INTEGER NOT NULL ," +
                    "'TIMESTAMP' INTEGER NOT NULL ," +
                    "'VIEW_TYPE' INTEGER NOT NULL ," +
                    "'ORIG_TIMESTAMP' INTEGER," +
                    "'IS_OFFLINE' INTEGER," +
                    "'SENDER_UUID' TEXT," +
                    "'SENDER_TYPE' INTEGER," +
                    "'SENDER_NAME' TEXT," +
                    "'SENDER_LEGACY_NAME' TEXT," +
                    "'MESSAGE_TEXT' TEXT," +
                    "'MESSAGE_TYPE' INTEGER NOT NULL ," +
                    "'EVENT_STATE' INTEGER," +
                    "'ORIG_IMTYPE' INTEGER," +
                    "'SESSION_ID' TEXT," +
                    "'ITEM_ID' TEXT," +
                    "'ITEM_NAME' TEXT," +
                    "'ASSET_TYPE' INTEGER," +
                    "'TRANSACTION_AMOUNT' INTEGER," +
                    "'NEW_BALANCE' INTEGER," +
                    "'CHAT_CHANNEL' INTEGER," +
                    "'DIALOG_IGNORED' INTEGER," +
                    "'ACCEPTED' INTEGER," +
                    "'USER_ID' TEXT," +
                    "'OBJECT_NAME' TEXT," +
                    "'QUESTION_MASK' INTEGER," +
                    "'DIALOG_BUTTONS' BLOB," +
                    "'DIALOG_SELECTED_OPTION' TEXT," +
                    "'TEXT_BOX_BUTTON_INDEX' INTEGER," +
                    "'SYNCED_TO_GOOGLE_DRIVE' INTEGER NOT NULL );")
            db.execSQL("CREATE INDEX " + constraint + "IDX_CHAT_MESSAGE_CHATTER_ID ON CHAT_MESSAGE (CHATTER_ID);")
            db.execSQL("CREATE INDEX " + constraint + "IDX_CHAT_MESSAGE__id_SYNCED_TO_GOOGLE_DRIVE ON CHAT_MESSAGE (_id,SYNCED_TO_GOOGLE_DRIVE);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'CHAT_MESSAGE'")
        }
    }

    constructor(config: DaoConfig) : this(config, null)

    override fun bindValues(stmt: SQLiteStatement, entity: ChatMessage) {
        stmt.clearBindings()
        val id = entity.id
        stmt.bindLong(1, id)
        stmt.bindLong(2, entity.chatterID)
        stmt.bindLong(3, entity.timestamp.time)
        stmt.bindLong(4, entity.viewType.toLong())
        
        val origTimestamp = entity.origTimestamp
        if (origTimestamp != null) {
            stmt.bindLong(5, origTimestamp.time)
        }
        
        stmt.bindLong(6, if (entity.isOffline) 1L else 0L)
        
        val senderUUID = entity.senderUUID
        if (senderUUID != null) {
            stmt.bindString(7, senderUUID.toString())
        }
        
        stmt.bindLong(8, entity.senderType.toLong())
        
        val senderName = entity.senderName
        if (senderName != null) {
            stmt.bindString(9, senderName)
        }
        
        val senderLegacyName = entity.senderLegacyName
        if (senderLegacyName != null) {
            stmt.bindString(10, senderLegacyName)
        }
        
        val messageText = entity.messageText
        if (messageText != null) {
            stmt.bindString(11, messageText)
        }
        
        stmt.bindLong(12, entity.messageType.toLong())
        
        stmt.bindLong(13, entity.eventState.toLong())
        
        stmt.bindLong(14, entity.origIMType.toLong())
        
        val sessionID = entity.sessionID
        if (sessionID != null) {
            stmt.bindString(15, sessionID.toString())
        }
        
        val itemID = entity.itemID
        if (itemID != null) {
            stmt.bindString(16, itemID.toString())
        }
        
        val itemName = entity.itemName
        if (itemName != null) {
            stmt.bindString(17, itemName)
        }
        
        stmt.bindLong(18, entity.assetType.toLong())
        stmt.bindLong(19, entity.transactionAmount.toLong())
        stmt.bindLong(20, entity.newBalance.toLong())
        stmt.bindLong(21, entity.chatChannel.toLong())
        
        stmt.bindLong(22, if (entity.dialogIgnored) 1L else 0L)
        stmt.bindLong(23, if (entity.accepted) 1L else 0L)
        
        val userID = entity.userID
        if (userID != null) {
            stmt.bindString(24, userID.toString())
        }
        
        val objectName = entity.objectName
        if (objectName != null) {
            stmt.bindString(25, objectName)
        }
        
        stmt.bindLong(26, entity.questionMask.toLong())
        
        val dialogSelectedOption = entity.dialogSelectedOption
        if (dialogSelectedOption != null) {
            stmt.bindString(27, dialogSelectedOption)
        }
        stmt.bindLong(28, entity.textBoxButtonIndex.toLong())
        stmt.bindLong(29, if (entity.syncedToGoogleDrive) 1L else 0L)
    }

    override fun getKey(entity: ChatMessage?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): ChatMessage {
        val id = if (cursor.isNull(offset + 0)) 0L else cursor.getLong(offset + 0)
        val chatterID = cursor.getLong(offset + 1)
        val timestamp = Date(cursor.getLong(offset + 2))
        val viewType = cursor.getInt(offset + 3)
        val origTimestamp = if (cursor.isNull(offset + 4)) Date() else Date(cursor.getLong(offset + 4))
        val isOffline = if (cursor.isNull(offset + 5)) false else cursor.getShort(offset + 5).toInt() != 0
        val senderUUID = if (cursor.isNull(offset + 6)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 6))
        val senderType = if (cursor.isNull(offset + 7)) 0 else cursor.getInt(offset + 7)
        val senderName = if (cursor.isNull(offset + 8)) "" else cursor.getString(offset + 8)
        val senderLegacyName = if (cursor.isNull(offset + 9)) "" else cursor.getString(offset + 9)
        val messageText = if (cursor.isNull(offset + 10)) "" else cursor.getString(offset + 10)
        val messageType = cursor.getInt(offset + 11)
        val eventState = if (cursor.isNull(offset + 12)) 0 else cursor.getInt(offset + 12)
        val origIMType = if (cursor.isNull(offset + 13)) 0 else cursor.getInt(offset + 13)
        val sessionID = if (cursor.isNull(offset + 14)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 14))
        val itemID = if (cursor.isNull(offset + 15)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 15))
        val itemName = if (cursor.isNull(offset + 16)) "" else cursor.getString(offset + 16)
        val assetType = if (cursor.isNull(offset + 17)) 0 else cursor.getInt(offset + 17)
        val transactionAmount = if (cursor.isNull(offset + 18)) 0 else cursor.getInt(offset + 18)
        val newBalance = if (cursor.isNull(offset + 19)) 0 else cursor.getInt(offset + 19)
        val chatChannel = if (cursor.isNull(offset + 20)) 0 else cursor.getInt(offset + 20)
        val dialogIgnored = if (cursor.isNull(offset + 21)) false else cursor.getShort(offset + 21).toInt() != 0
        val accepted = if (cursor.isNull(offset + 22)) false else cursor.getShort(offset + 22).toInt() != 0
        val userID = if (cursor.isNull(offset + 23)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 23))
        val objectName = if (cursor.isNull(offset + 24)) "" else cursor.getString(offset + 24)
        val questionMask = if (cursor.isNull(offset + 25)) 0 else cursor.getInt(offset + 25)
        val dialogSelectedOption = if (cursor.isNull(offset + 27)) "" else cursor.getString(offset + 27)
        val textBoxButtonIndex = if (cursor.isNull(offset + 28)) 0 else cursor.getInt(offset + 28)
        val syncedToGoogleDrive = cursor.getShort(offset + 29).toInt() != 0

        return ChatMessage(
            accepted, assetType, chatChannel, chatterID, dialogIgnored, dialogSelectedOption,
            eventState, id, isOffline, itemID, itemName, messageText, messageType, newBalance,
            objectName, origIMType, origTimestamp, questionMask, senderLegacyName, senderName,
            senderType, senderUUID, sessionID, syncedToGoogleDrive, textBoxButtonIndex, timestamp,
            transactionAmount, userID, viewType
        )
    }

    override fun readEntity(cursor: Cursor, entity: ChatMessage, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) 0L else cursor.getLong(offset + 0)
        entity.chatterID = cursor.getLong(offset + 1)
        entity.timestamp = Date(cursor.getLong(offset + 2))
        entity.viewType = cursor.getInt(offset + 3)
        entity.origTimestamp = if (cursor.isNull(offset + 4)) Date() else Date(cursor.getLong(offset + 4))
        entity.isOffline = if (cursor.isNull(offset + 5)) false else cursor.getShort(offset + 5).toInt() != 0
        entity.senderUUID = if (cursor.isNull(offset + 6)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 6))
        entity.senderType = if (cursor.isNull(offset + 7)) 0 else cursor.getInt(offset + 7)
        entity.senderName = if (cursor.isNull(offset + 8)) "" else cursor.getString(offset + 8)
        entity.senderLegacyName = if (cursor.isNull(offset + 9)) "" else cursor.getString(offset + 9)
        entity.messageText = if (cursor.isNull(offset + 10)) "" else cursor.getString(offset + 10)
        entity.messageType = cursor.getInt(offset + 11)
        entity.eventState = if (cursor.isNull(offset + 12)) 0 else cursor.getInt(offset + 12)
        entity.origIMType = if (cursor.isNull(offset + 13)) 0 else cursor.getInt(offset + 13)
        entity.sessionID = if (cursor.isNull(offset + 14)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 14))
        entity.itemID = if (cursor.isNull(offset + 15)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 15))
        entity.itemName = if (cursor.isNull(offset + 16)) "" else cursor.getString(offset + 16)
        entity.assetType = if (cursor.isNull(offset + 17)) 0 else cursor.getInt(offset + 17)
        entity.transactionAmount = if (cursor.isNull(offset + 18)) 0 else cursor.getInt(offset + 18)
        entity.newBalance = if (cursor.isNull(offset + 19)) 0 else cursor.getInt(offset + 19)
        entity.chatChannel = if (cursor.isNull(offset + 20)) 0 else cursor.getInt(offset + 20)
        entity.dialogIgnored = if (cursor.isNull(offset + 21)) false else cursor.getShort(offset + 21).toInt() != 0
        entity.accepted = if (cursor.isNull(offset + 22)) false else cursor.getShort(offset + 22).toInt() != 0
        entity.userID = if (cursor.isNull(offset + 23)) UUID.randomUUID() else UUID.fromString(cursor.getString(offset + 23))
        entity.objectName = if (cursor.isNull(offset + 24)) "" else cursor.getString(offset + 24)
        entity.questionMask = if (cursor.isNull(offset + 25)) 0 else cursor.getInt(offset + 25)
        entity.dialogSelectedOption = if (cursor.isNull(offset + 27)) "" else cursor.getString(offset + 27)
        entity.textBoxButtonIndex = if (cursor.isNull(offset + 28)) 0 else cursor.getInt(offset + 28)
        entity.syncedToGoogleDrive = cursor.getShort(offset + 29).toInt() != 0
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: ChatMessage, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
    
    fun getByChatterIdFlow(chatterId: Long): Flow<List<ChatMessage>> = flow {
        val list = queryBuilder()
            .where(Properties.ChatterID.eq(chatterId))
            .orderAsc(Properties.Timestamp)
            .list()
        emit(list)
    }

    fun getRecentByChatterId(chatterId: Long, limit: Int): List<ChatMessage> {
        return queryBuilder()
            .where(Properties.ChatterID.eq(chatterId))
            .orderDesc(Properties.Timestamp)
            .limit(limit)
            .list()
            .reversed()
    }

    fun searchByText(query: String): List<ChatMessage> {
        return queryBuilder()
            .where(Properties.MessageText.like("%$query%"))
            .list()
    }

    fun getOfflineMessagesFlow(): Flow<List<ChatMessage>> = flow {
        val list = queryBuilder()
            .where(Properties.IsOffline.eq(true))
            .list()
        emit(list)
    }

    fun getUnsyncedMessages(): List<ChatMessage> {
        return queryBuilder()
            .where(Properties.SyncedToGoogleDrive.eq(false))
            .list()
    }

    fun markAsSynced(messageIds: List<Long>) {
        if (messageIds.isEmpty()) return
        
        myDaoSession?.runInTx {
            for (id in messageIds) {
                val msg = load(id)
                if (msg != null) {
                    msg.syncedToGoogleDrive = true
                    update(msg)
                }
            }
        }
    }

    fun deleteOlderThan(date: Date): Int {
        val builder = queryBuilder()
        builder.where(Properties.Timestamp.lt(date.time))
        val list = builder.list()
        val count = list.size
        deleteInTx(list)
        return count
    }
}
