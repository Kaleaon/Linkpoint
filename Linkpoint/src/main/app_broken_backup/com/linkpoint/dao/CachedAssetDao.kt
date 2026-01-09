package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.fragment.app.NotificationCompat
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class CachedAssetDao : AbstractDao<CachedAsset, String> {
    val TABLENAME: String = "CachedAssets"

    class Properties {
        Property Data = Property(2, ByteArray.class, "data", false, "DATA")
        Property Key = Property(0, String.class, "key", true, "KEY")
        Property MustRevalidate = Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE")
        Property Status = Property(1, Int.TYPE, NotificationCompat.CATEGORY_STATUS, false, "STATUS")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean)  {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedAssets' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'STATUS' INTEGER NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean)  {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedAssets'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, cachedAsset: CachedAsset)  {
        sQLiteStatement.clearBindings()
        var key: String = cachedAsset.getKey()
        if (key != null) {
            sQLiteStatement.bindString(1, key)
        }
        sQLiteStatement.bindLong(2, (Long) cachedAsset.getStatus())
        ByteArray data = cachedAsset.getData()
        if (data != null) {
            sQLiteStatement.bindBlob(3, data)
        }
        sQLiteStatement.bindLong(4, cachedAsset.getMustRevalidate() ? 1 : 0)
    }

    fun getKey(cachedAsset: CachedAsset): String {
        return cachedAsset != null ? cachedAsset.getKey() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): CachedAsset {
        ByteArray bArr = null
        var z: Boolean = false
        var string: String = cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
        var i2: Int = cursor.getInt(i + 1)
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return CachedAsset(string, i2, bArr, z)
    }

    fun readEntity(cursor: Cursor, cachedAsset: CachedAsset, i: Int)  {
        ByteArray bArr = null
        cachedAsset.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0))
        cachedAsset.setStatus(cursor.getInt(i + 1))
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        cachedAsset.setData(bArr)
        cachedAsset.setMustRevalidate(cursor.getShort(i + 3) != (Short) 0)
    }

    fun readKey(cursor: Cursor, i: Int): String {
        return cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
    }

    protected fun updateKeyAfterInsert(cachedAsset: CachedAsset, j: Long): String {
        return cachedAsset.getKey()
    }
}
