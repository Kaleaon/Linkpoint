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
        const val Property Data = Property(1, ByteArray.class, "data", false, "DATA")
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
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedResponses' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedResponses'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, cachedResponse: CachedResponse) {
        sQLiteStatement.clearBindings()
        val key: String = cachedResponse.getKey()
        if (key != null) {
            sQLiteStatement.bindString(1, key)
        }
        val data: ByteArray = cachedResponse.getData()
        if (data != null) {
            sQLiteStatement.bindBlob(2, data)
        }
        sQLiteStatement.bindLong(3, cachedResponse.getMustRevalidate() ? 1 : 0)
    }

     public fun getKey(cachedResponse: CachedResponse): String {
        return cachedResponse != null ? cachedResponse.getKey() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): CachedResponse {
        val bArr: ByteArray = null
        val z: Boolean = false
        val string: String = cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1)
        }
        if (cursor.getShort(i + 2) != (Short) 0) {
            z = true
        }
        return CachedResponse(string, bArr, z)
    }

    fun readEntity(cursor: Cursor, cachedResponse: CachedResponse, i: Int) {
        val bArr: ByteArray = null
        cachedResponse.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0))
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1)
        }
        cachedResponse.setData(bArr)
        cachedResponse.setMustRevalidate(cursor.getShort(i + 2) != (Short) 0)
    }

     public fun readKey(cursor: Cursor, i: Int): String {
        return cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
    }

     protected fun updateKeyAfterInsert(cachedResponse: CachedResponse, j: Long): String {
        return cachedResponse.getKey()
    }
}
