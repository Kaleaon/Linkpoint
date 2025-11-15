package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class UserDao : AbstractDao<User, Long> {
    val TABLENAME: String = "Users"

    class Properties {
        Property BadUUID = Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID")
        Property DisplayName = Property(3, String.class, "displayName", false, "DISPLAY_NAME")
        Property Id = Property(0, Long.class, "id", true, "_id")
        Property IsFriend = Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND")
        Property RightsGiven = Property(6, Int.TYPE, "rightsGiven", false, "RIGHTS_GIVEN")
        Property RightsHas = Property(7, Int.TYPE, "rightsHas", false, "RIGHTS_HAS")
        Property UserName = Property(2, String.class, "userName", false, "USER_NAME")
        Property Uuid = Property(1, UUID.class, "uuid", false, "UUID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'Users' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'BAD_UUID' INTEGER NOT NULL ," + "'IS_FRIEND' INTEGER NOT NULL ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL );")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_Users_UUID ON Users" + " (UUID);")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Users'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, user: User): Unit {
        Long j = 1
        sQLiteStatement.clearBindings()
        Long id = user.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        UUID uuid = user.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid.toString())
        }
        String userName = user.getUserName()
        if (userName != null) {
            sQLiteStatement.bindString(3, userName)
        }
        userName = user.getDisplayName()
        if (userName != null) {
            sQLiteStatement.bindString(4, userName)
        }
        sQLiteStatement.bindLong(5, user.getBadUUID() ? 1 : 0)
        if (!user.getIsFriend()) {
            j = 0
        }
        sQLiteStatement.bindLong(6, j)
        sQLiteStatement.bindLong(7, (Long) user.getRightsGiven())
        sQLiteStatement.bindLong(8, (Long) user.getRightsHas())
    }

    fun getKey(user: User): Long {
        return user != null ? user.getId() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): User {
        Boolean z = true
        String str = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        UUID fromString = cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1))
        String string = cursor.isNull(i + 2) ? null : cursor.getString(i + 2)
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3)
        }
        Boolean z2 = cursor.getShort(i + 4) != (Short) 0
        if (cursor.getShort(i + 5) == (Short) 0) {
            z = false
        }
        return User(valueOf, fromString, string, str, z2, z, cursor.getInt(i + 6), cursor.getInt(i + 7))
    }

    fun readEntity(cursor: Cursor, user: User, i: Int): Unit {
        Boolean z = true
        String str = null
        user.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        user.setUuid(cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1)))
        user.setUserName(cursor.isNull(i + 2) ? null : cursor.getString(i + 2))
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3)
        }
        user.setDisplayName(str)
        user.setBadUUID(cursor.getShort(i + 4) != (Short) 0)
        if (cursor.getShort(i + 5) == (Short) 0) {
            z = false
        }
        user.setIsFriend(z)
        user.setRightsGiven(cursor.getInt(i + 6))
        user.setRightsHas(cursor.getInt(i + 7))
    }

    fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected fun updateKeyAfterInsert(user: User, j: Long): Long {
        user.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
