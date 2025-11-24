package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class ChatterDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<Chatter, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "CHATTER"

        object Properties {
            @JvmField val Active = Property(3, Boolean::class.java, "active", false, "ACTIVE")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val LastMessageID = Property(6, Long::class.java, "lastMessageID", false, "LAST_MESSAGE_ID")
            @JvmField val LastSessionID = Property(7, String::class.java, "lastSessionID", false, "LAST_SESSION_ID")
            @JvmField val Muted = Property(4, Boolean::class.java, "muted", false, "MUTED")
            @JvmField val Type = Property(1, Int::class.java, "type", false, "TYPE")
            @JvmField val UnreadCount = Property(5, Int::class.java, "unreadCount", false, "UNREAD_COUNT")
            @JvmField val Uuid = Property(2, String::class.java, "uuid", false, "UUID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'CHATTER' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'TYPE' INTEGER NOT NULL ," +
                    "'UUID' TEXT," +
                    "'ACTIVE' INTEGER NOT NULL ," +
                    "'MUTED' INTEGER NOT NULL ," +
                    "'UNREAD_COUNT' INTEGER NOT NULL ," +
                    "'LAST_MESSAGE_ID' INTEGER," +
                    "'LAST_SESSION_ID' TEXT);")
            db.execSQL("CREATE INDEX " + constraint + "IDX_CHATTER_TYPE_UUID ON CHATTER (TYPE,UUID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'CHATTER'")
        }
    }

    constructor(config: DaoConfig) : this(config, null)

    override fun bindValues(stmt: SQLiteStatement, entity: Chatter) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        stmt.bindLong(2, entity.type.toLong())
        
        val uuid = entity.uuid
        if (uuid != null) {
            stmt.bindString(3, uuid.toString())
        }
        
        stmt.bindLong(4, if (entity.active) 1L else 0L)
        stmt.bindLong(5, if (entity.muted) 1L else 0L)
        stmt.bindLong(6, entity.unreadCount.toLong())
        
        val lastMessageID = entity.lastMessageID
        if (lastMessageID != null) {
            stmt.bindLong(7, lastMessageID)
        }
        
        val lastSessionID = entity.lastSessionID
        if (lastSessionID != null) {
            stmt.bindString(8, lastSessionID.toString())
        }
    }

    override fun getKey(entity: Chatter?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): Chatter {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val type = cursor.getInt(offset + 1)
        val uuid = if (cursor.isNull(offset + 2)) null else UUID.fromString(cursor.getString(offset + 2))
        val active = cursor.getShort(offset + 3).toInt() != 0
        val muted = cursor.getShort(offset + 4).toInt() != 0
        val unreadCount = cursor.getInt(offset + 5)
        val lastMessageID = if (cursor.isNull(offset + 6)) null else cursor.getLong(offset + 6)
        val lastSessionID = if (cursor.isNull(offset + 7)) null else UUID.fromString(cursor.getString(offset + 7))

        return Chatter(id, type, uuid, active, muted, unreadCount, lastMessageID, lastSessionID)
    }

    override fun readEntity(cursor: Cursor, entity: Chatter, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.type = cursor.getInt(offset + 1)
        entity.uuid = if (cursor.isNull(offset + 2)) null else UUID.fromString(cursor.getString(offset + 2))
        entity.active = cursor.getShort(offset + 3).toInt() != 0
        entity.muted = cursor.getShort(offset + 4).toInt() != 0
        entity.unreadCount = cursor.getInt(offset + 5)
        entity.lastMessageID = if (cursor.isNull(offset + 6)) null else cursor.getLong(offset + 6)
        entity.lastSessionID = if (cursor.isNull(offset + 7)) null else UUID.fromString(cursor.getString(offset + 7))
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: Chatter, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
