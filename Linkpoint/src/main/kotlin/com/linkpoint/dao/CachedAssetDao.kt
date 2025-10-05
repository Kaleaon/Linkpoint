package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.support.v4.app.NotificationCompat
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class CachedAssetDao : AbstractDao()<CachedAsset, String> {
    const val String TABLENAME = "CachedAssets"

    @JvmStatic
    class Properties {
        const val Property Data = Property(2, Byte[].class, "data", false, "DATA")
        const val Property Key = Property(0, String.class, "key", true, "KEY")
        const val Property MustRevalidate = Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE")
        const val Property Status = Property(1, Integer.TYPE, NotificationCompat.CATEGORY_STATUS, false, "STATUS")
    }

    public CachedAssetDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public CachedAssetDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedAssets' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'STATUS' INTEGER NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedAssets'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, CachedAsset cachedAsset) {
        sQLiteStatement.clearBindings()
        String key = cachedAsset.getKey()
        if (key != null) {
            sQLiteStatement.bindString(1, key)
        }
        sQLiteStatement.bindLong(2, (Long) cachedAsset.getStatus())
        Byte[] data = cachedAsset.getData()
        if (data != null) {
            sQLiteStatement.bindBlob(3, data)
        }
        sQLiteStatement.bindLong(4, cachedAsset.getMustRevalidate() ? 1 : 0)
    }

    public String getKey(CachedAsset cachedAsset) {
        return cachedAsset != null ? cachedAsset.getKey() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public CachedAsset readEntity(Cursor cursor, Int i) {
        Byte[] bArr = null
        Boolean z = false
        String string = cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
        Int i2 = cursor.getInt(i + 1)
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        if (cursor.getShort(i + 3) != (Short) 0) {
            z = true
        }
        return CachedAsset(string, i2, bArr, z)
    }

    public Unit readEntity(Cursor cursor, CachedAsset cachedAsset, Int i) {
        Byte[] bArr = null
        cachedAsset.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0))
        cachedAsset.setStatus(cursor.getInt(i + 1))
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2)
        }
        cachedAsset.setData(bArr)
        cachedAsset.setMustRevalidate(cursor.getShort(i + 3) != (Short) 0)
    }

    public String readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : cursor.getString(i + 0)
    }

    protected String updateKeyAfterInsert(CachedAsset cachedAsset, Long j) {
        return cachedAsset.getKey()
    }
}
