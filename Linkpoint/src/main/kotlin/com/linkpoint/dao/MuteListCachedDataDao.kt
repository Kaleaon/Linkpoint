package com.linkpoint.dao
import java.util.*

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class MuteListCachedDataDao : AbstractDao()<MuteListCachedData, Long> {
    const val TABLENAME: String = "MUTE_LIST_CACHED_DATA"

    @JvmStatic
    class Properties {
        const val Property CRC = Property(1, Integer.TYPE, "CRC", false, "CRC")
        const val Property Data = Property(2, Byte[].class, "data", false, "DATA")
        const val Property Id = Property(0, Long.class, "id", true, "_id")
    }

    public MuteListCachedDataDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public MuteListCachedDataDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'MUTE_LIST_CACHED_DATA' (" + "'_id' INTEGER PRIMARY KEY ," + "'CRC' INTEGER NOT NULL ," + "'DATA' BLOB NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'MUTE_LIST_CACHED_DATA'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, MuteListCachedData muteListCachedData) {
        sQLiteStatement.clearBindings()
        Long id = muteListCachedData.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindLong(2, (Long) muteListCachedData.getCRC())
        sQLiteStatement.bindBlob(3, muteListCachedData.getData())
    }

    public Long getKey(MuteListCachedData muteListCachedData) {
        return muteListCachedData != null ? muteListCachedData.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public MuteListCachedData readEntity(Cursor cursor, Int i) {
        return MuteListCachedData(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), cursor.getInt(i + 1), cursor.getBlob(i + 2))
    }

    fun readEntity(Cursor cursor, MuteListCachedData muteListCachedData, Int i) {
        muteListCachedData.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        muteListCachedData.setCRC(cursor.getInt(i + 1))
        muteListCachedData.setData(cursor.getBlob(i + 2))
    }

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(MuteListCachedData muteListCachedData, Long j) {
        muteListCachedData.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
