package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class ChatterDao : AbstractDao()<Chatter, Long> {
    const val TABLENAME: String = "CHATTER"

    @JvmStatic
    class Properties {
        const val Property Active = Property(3, Boolean.TYPE, "active", false, "ACTIVE")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property LastMessageID = Property(6, Long.class, "lastMessageID", false, "LAST_MESSAGE_ID")
        const val Property LastSessionID = Property(7, UUID.class, "lastSessionID", false, "LAST_SESSION_ID")
        const val Property Muted = Property(4, Boolean.TYPE, "muted", false, "MUTED")
        const val Property Type = Property(1, Integer.TYPE, "type", false, "TYPE")
        const val Property UnreadCount = Property(5, Integer.TYPE, "unreadCount", false, "UNREAD_COUNT")
        const val Property Uuid = Property(2, UUID.class, "uuid", false, "UUID")
    }

    public ChatterDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public ChatterDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHATTER' (" + "'_id' INTEGER PRIMARY KEY ," + "'TYPE' INTEGER NOT NULL ," + "'UUID' TEXT," + "'ACTIVE' INTEGER NOT NULL ," + "'MUTED' INTEGER NOT NULL ," + "'UNREAD_COUNT' INTEGER NOT NULL ," + "'LAST_MESSAGE_ID' INTEGER," + "'LAST_SESSION_ID' TEXT);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHATTER_TYPE_UUID ON CHATTER" + " (TYPE,UUID);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHATTER'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, chatter: Chatter) {
        val j: Long = 1
        sQLiteStatement.clearBindings()
        val id: Long = chatter.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, (Long) chatter.getType())
        val uuid: UUID = chatter.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(3, uuid.toString())
        }
        sQLiteStatement.bindLong(4, chatter.getActive() ? 1 : 0)
        if (!chatter.getMuted()) {
            j = 0
        }
        sQLiteStatement.bindLong(5, j)
        sQLiteStatement.bindLong(6, (Long) chatter.getUnreadCount())
        id = chatter.getLastMessageID()
        if (id != null) {
            sQLiteStatement.bindLong(7, id.longValue())
        }
        uuid = chatter.getLastSessionID()
        if (uuid != null) {
            sQLiteStatement.bindString(8, uuid.toString())
        }
    }

     public fun getKey(chatter: Chatter): Long {
        return chatter != null ? chatter.getId() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): Chatter {
        val z: Boolean = true
        val uuid: UUID = null
        val valueOf: Long = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        val i2: Int = cursor.getInt(i + 1)
        val fromString: UUID = cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2))
        val z2: Boolean = cursor.getShort(i + 3) != (Short) 0
        if (cursor.getShort(i + 4) == (Short) 0) {
            z = false
        }
        val i3: Int = cursor.getInt(i + 5)
        val valueOf2: Long = cursor.isNull(i + 6) ? null : Long.valueOf(cursor.getLong(i + 6))
        if (!cursor.isNull(i + 7)) {
            uuid = UUID.fromString(cursor.getString(i + 7))
        }
        return Chatter(valueOf, i2, fromString, z2, z, i3, valueOf2, uuid)
    }

    fun readEntity(cursor: Cursor, chatter: Chatter, i: Int) {
        val z: Boolean = true
        val uuid: UUID = null
        chatter.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        chatter.setType(cursor.getInt(i + 1))
        chatter.setUuid(cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2)))
        chatter.setActive(cursor.getShort(i + 3) != (Short) 0)
        if (cursor.getShort(i + 4) == (Short) 0) {
            z = false
        }
        chatter.setMuted(z)
        chatter.setUnreadCount(cursor.getInt(i + 5))
        chatter.setLastMessageID(cursor.isNull(i + 6) ? null : Long.valueOf(cursor.getLong(i + 6)))
        if (!cursor.isNull(i + 7)) {
            uuid = UUID.fromString(cursor.getString(i + 7))
        }
        chatter.setLastSessionID(uuid)
    }

     public fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

     protected fun updateKeyAfterInsert(chatter: Chatter, j: Long): Long {
        chatter.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
