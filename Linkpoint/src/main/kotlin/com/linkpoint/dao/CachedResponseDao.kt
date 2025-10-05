package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class CachedResponseDao : AbstractDao()<CachedResponse, String> {
    const val TABLENAME: String = "CachedResponses"

    @JvmStatic
    class Properties {
        const val Property Data = Property(1, Byte[].class, "data", false, "DATA")
        const val Property Key = Property(0, String.class, "key", true, "KEY")
        const val Property MustRevalidate = Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE")
    }

    public CachedResponseDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public CachedResponseDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedResponses' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedResponses'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, CachedResponse cachedResponse) {
        sQLiteStatement.clearBindings()
        String key = cachedResponse.getKey()
        if (key != null) {
            sQLiteStatement.bindString(1, key)
        }
        Byte[] data = cachedResponse.getData()
        if (data != null) {
            sQLiteStatement.bindBlob(2, data)
        }
        sQLiteStatement.bindLong(3, cachedResponse.getMustRevalidate() ? 1 : 0)
    }

    public String getKey(CachedResponse cachedResponse) {
        return cachedResponse != null ? cachedResponse.getKey() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public CachedResponse readEntity(Cursor cursor, Int i) {
        Byte[] bArr = null
        Boolean z = false
        String string = cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1)
        }
        if (cursor.getShort(i + 2) != (Short) 0) {
            z = true
        }
        return CachedResponse(string, bArr, z)
    }

    fun readEntity(Cursor cursor, CachedResponse cachedResponse, Int i) {
        Byte[] bArr = null
        cachedResponse.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0))
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1)
        }
        cachedResponse.setData(bArr)
        cachedResponse.setMustRevalidate(cursor.getShort(i + 2) != (Short) 0)
    }

    public String readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
    }

    protected String updateKeyAfterInsert(CachedResponse cachedResponse, Long j) {
        return cachedResponse.getKey()
    }
}
