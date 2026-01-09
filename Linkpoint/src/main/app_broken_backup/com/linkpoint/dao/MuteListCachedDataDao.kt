package com.linkpoint.dao
import java.util.*

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class MuteListCachedDataDao : AbstractDao<MuteListCachedData, Long> {
    val TABLENAME: String = "MUTE_LIST_CACHED_DATA"

    class Properties {
        Property CRC = Property(1, Int.TYPE, "CRC", false, "CRC")
        Property Data = Property(2, ByteArray.class, "data", false, "DATA")
        Property Id = Property(0, Long.class, "id", true, "_id")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MUTE_LIST_CACHED_DATA' (" + "'_id' INTEGER PRIMARY KEY ," + "'CRC' INTEGER NOT NULL ," + "'DATA' BLOB NOT NULL );")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MUTE_LIST_CACHED_DATA'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, muteListCachedData: MuteListCachedData): Unit {
        sQLiteStatement.clearBindings()
        Long id = muteListCachedData.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, (Long) muteListCachedData.getCRC())
        sQLiteStatement.bindBlob(3, muteListCachedData.getData())
    }

    fun getKey(muteListCachedData: MuteListCachedData): Long {
        return muteListCachedData != null ? muteListCachedData.getId() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): MuteListCachedData {
        return MuteListCachedData(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), cursor.getInt(i + 1), cursor.getBlob(i + 2))
    }

    fun readEntity(cursor: Cursor, muteListCachedData: MuteListCachedData, i: Int): Unit {
        muteListCachedData.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        muteListCachedData.setCRC(cursor.getInt(i + 1))
        muteListCachedData.setData(cursor.getBlob(i + 2))
    }

    fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected fun updateKeyAfterInsert(muteListCachedData: MuteListCachedData, j: Long): Long {
        muteListCachedData.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
