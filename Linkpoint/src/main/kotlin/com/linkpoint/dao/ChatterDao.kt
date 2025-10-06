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
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHATTER' (" + "'_id' INTEGER PRIMARY KEY ," + "'TYPE' INTEGER NOT NULL ," + "'UUID' TEXT," + "'ACTIVE' INTEGER NOT NULL ," + "'MUTED' INTEGER NOT NULL ," + "'UNREAD_COUNT' INTEGER NOT NULL ," + "'LAST_MESSAGE_ID' INTEGER," + "'LAST_SESSION_ID' TEXT);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHATTER_TYPE_UUID ON CHATTER" + " (TYPE,UUID);")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHATTER'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, Chatter chatter) {
        Long j = 1
        sQLiteStatement.clearBindings()
        Long id = chatter.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, (Long) chatter.getType())
        UUID uuid = chatter.getUuid()
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

    public Long getKey(Chatter chatter) {
        return chatter != null ? chatter.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public Chatter readEntity(Cursor cursor, Int i) {
        Boolean z = true
        UUID uuid = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        Int i2 = cursor.getInt(i + 1)
        UUID fromString = cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2))
        Boolean z2 = cursor.getShort(i + 3) != (Short) 0
        if (cursor.getShort(i + 4) == (Short) 0) {
            z = false
        }
        Int i3 = cursor.getInt(i + 5)
        Long valueOf2 = cursor.isNull(i + 6) ? null : Long.valueOf(cursor.getLong(i + 6))
        if (!cursor.isNull(i + 7)) {
            uuid = UUID.fromString(cursor.getString(i + 7))
        }
        return Chatter(valueOf, i2, fromString, z2, z, i3, valueOf2, uuid)
    }

    fun readEntity(Cursor cursor, Chatter chatter, Int i) {
        Boolean z = true
        UUID uuid = null
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

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(Chatter chatter, Long j) {
        chatter.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
