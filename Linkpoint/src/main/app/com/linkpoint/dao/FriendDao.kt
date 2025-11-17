package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class FriendDao : AbstractDao<Friend, UUID> {
    val TABLENAME: String = "Friends"

    class Properties {
        Property IsOnline = Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE")
        Property RightsGiven = Property(1, Int.TYPE, "rightsGiven", false, "RIGHTS_GIVEN")
        Property RightsHas = Property(2, Int.TYPE, "rightsHas", false, "RIGHTS_HAS")
        Property Uuid = Property(0, UUID.class, "uuid", true, "UUID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'Friends' (" + "'UUID' TEXT PRIMARY KEY ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL ," + "'IS_ONLINE' INTEGER NOT NULL );")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Friends'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, friend: Friend): Unit {
        sQLiteStatement.clearBindings()
        UUID uuid = friend.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString())
        }
        sQLiteStatement.bindLong(2, (Long) friend.getRightsGiven())
        sQLiteStatement.bindLong(3, (Long) friend.getRightsHas())
        sQLiteStatement.bindLong(4, friend.getIsOnline() ? 1 : 0)
    }

    fun getKey(friend: Friend): UUID {
        return friend != null ? friend.getUuid() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): Friend {
        Boolean z = false
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        Int i2 = cursor.getInt(i + 1)
        Int i3 = cursor.getInt(i + 2)
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return Friend(fromString, i2, i3, z)
    }

    fun readEntity(cursor: Cursor, friend: Friend, i: Int): Unit {
        friend.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        friend.setRightsGiven(cursor.getInt(i + 1))
        friend.setRightsHas(cursor.getInt(i + 2))
        friend.setIsOnline(cursor.getShort(i + 3) != (Short) 0)
    }

    fun readKey(cursor: Cursor, i: Int): UUID {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected fun updateKeyAfterInsert(friend: Friend, j: Long): UUID {
        return friend.getUuid()
    }
}
