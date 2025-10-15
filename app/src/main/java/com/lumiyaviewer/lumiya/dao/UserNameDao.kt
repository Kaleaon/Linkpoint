package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class UserNameDao : AbstractDao<UserName, UUID> {
    String TABLENAME = "UserNames"

    class Properties {
        Property DisplayName = Property(2, String.class, "displayName", false, "DISPLAY_NAME")
        Property IsBadUUID = Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID")
        Property UserName = Property(1, String.class, "userName", false, "USER_NAME")
        Property Uuid = Property(0, UUID.class, "uuid", true, "UUID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'UserNames' (" + "'UUID' TEXT PRIMARY KEY ," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'IS_BAD_UUID' INTEGER NOT NULL );")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'UserNames'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, userName: UserName): Unit {
        sQLiteStatement.clearBindings()
        UUID uuid = userName.getUuid()
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString())
        }
        String userName2 = userName.getUserName()
        if (userName2 != null) {
            sQLiteStatement.bindString(2, userName2)
        }
        userName2 = userName.getDisplayName()
        if (userName2 != null) {
            sQLiteStatement.bindString(3, userName2)
        }
        sQLiteStatement.bindLong(4, userName.getIsBadUUID() ? 1 : 0)
    }

    fun getKey(userName: UserName): UUID {
        return userName != null ? userName.getUuid() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): UserName {
        Boolean z = false
        String str = null
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
        String string = cursor.isNull(i + 1) ? null : cursor.getString(i + 1)
        if (!cursor.isNull(i + 2)) {
            str = cursor.getString(i + 2)
        }
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return UserName(fromString, string, str, z)
    }

    fun readEntity(cursor: Cursor, userName: UserName, i: Int): Unit {
        String str = null
        userName.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)))
        userName.setUserName(cursor.isNull(i + 1) ? null : cursor.getString(i + 1))
        if (!cursor.isNull(i + 2)) {
            str = cursor.getString(i + 2)
        }
        userName.setDisplayName(str)
        userName.setIsBadUUID(cursor.getShort(i + 3) != (Short) 0)
    }

    fun readKey(cursor: Cursor, i: Int): UUID {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0))
    }

    protected fun updateKeyAfterInsert(userName: UserName, j: Long): UUID {
        return userName.getUuid()
    }
}
