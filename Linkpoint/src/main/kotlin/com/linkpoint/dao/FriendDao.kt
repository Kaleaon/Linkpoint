package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class FriendDao : AbstractDao()<Friend, UUID> {
    const val TABLENAME: String = "Friends"

    @JvmStatic
    class Properties {
        const val Property IsOnline = Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE")
        const val Property RightsGiven = Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN")
        const val Property RightsHas = Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS")
        const val Property Uuid = Property(0, UUID.class, "uuid", true, "UUID")
    }

    public FriendDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public FriendDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'Friends' (" + "'UUID' TEXT PRIMARY KEY ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL ," + "'IS_ONLINE' INTEGER NOT NULL );")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Friends'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, friend: Friend) {
        sQLiteStatement.clearBindings()
        val uuid: UUID = friend.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString())
        }
        sQLiteStatement.bindLong(2, (Long) friend.getRightsGiven())
        sQLiteStatement.bindLong(3, (Long) friend.getRightsHas())
        sQLiteStatement.bindLong(4, friend.getIsOnline() ? 1 : 0)
    }

     public fun getKey(friend: Friend): UUID {
        return friend != null ? friend.getUuid() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): Friend {
        val z: Boolean = false
        val fromString: UUID = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        val i2: Int = cursor.getInt(i + 1)
        val i3: Int = cursor.getInt(i + 2)
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return Friend(fromString, i2, i3, z)
    }

    fun readEntity(cursor: Cursor, friend: Friend, i: Int) {
        friend.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        friend.setRightsGiven(cursor.getInt(i + 1))
        friend.setRightsHas(cursor.getInt(i + 2))
        friend.setIsOnline(cursor.getShort(i + 3) != (Short) 0)
    }

     public fun readKey(cursor: Cursor, i: Int): UUID {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

     protected fun updateKeyAfterInsert(friend: Friend, j: Long): UUID {
        return friend.getUuid()
    }
}
