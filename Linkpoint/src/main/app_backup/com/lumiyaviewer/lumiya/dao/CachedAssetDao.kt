package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import androidx.core.app.NotificationCompat
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class CachedAssetDao : AbstractDao<CachedAsset, String> {
    
    companion object {
        const val TABLENAME = "CachedAssets"
        
        object Properties {
            @JvmField val Data = Property(2, ByteArray::class.java, "data", false, "DATA")
            @JvmField val Key = Property(0, String::class.java, "key", true, "KEY")
            @JvmField val MustRevalidate = Property(3, Boolean::class.java, "mustRevalidate", false, "MUST_REVALIDATE")
            @JvmField val Status = Property(1, Int::class.java, "status", false, "STATUS") // Assuming 'status' is the column/property name
        }
        
        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'CachedAssets' ('KEY' TEXT PRIMARY KEY NOT NULL ,'STATUS' INTEGER NOT NULL ,'DATA' BLOB,'MUST_REVALIDATE' INTEGER NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'CachedAssets'")
        }
    }

    constructor(config: DaoConfig) : super(config)
    
    constructor(config: DaoConfig, daoSession: DaoSession) : super(config, daoSession)

    override fun bindValues(stmt: SQLiteStatement, entity: CachedAsset) {
        stmt.clearBindings()
        val key = entity.getKey()
        if (key != null) {
            stmt.bindString(1, key)
        }
        stmt.bindLong(2, entity.getStatus().toLong())
        val data = entity.getData()
        if (data != null) {
            stmt.bindBlob(3, data)
        }
        stmt.bindLong(4, if (entity.getMustRevalidate()) 1L else 0L)
    }

    override fun getKey(entity: CachedAsset?): String? {
        return entity?.getKey()
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): CachedAsset {
        val key = if (cursor.isNull(offset + 0)) null else cursor.getString(offset + 0)
        val status = cursor.getInt(offset + 1)
        val data = if (cursor.isNull(offset + 2)) null else cursor.getBlob(offset + 2)
        val mustRevalidate = cursor.getShort(offset + 3) != 0.toShort()
        
        return CachedAsset(key, status, data, mustRevalidate)
    }

    override fun readEntity(cursor: Cursor, entity: CachedAsset, offset: Int) {
        entity.setKey(if (cursor.isNull(offset + 0)) null else cursor.getString(offset + 0))
        entity.setStatus(cursor.getInt(offset + 1))
        entity.setData(if (cursor.isNull(offset + 2)) null else cursor.getBlob(offset + 2))
        entity.setMustRevalidate(cursor.getShort(offset + 3) != 0.toShort())
    }

    override fun readKey(cursor: Cursor, offset: Int): String? {
        return if (cursor.isNull(offset + 0)) null else cursor.getString(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: CachedAsset, rowId: Long): String? {
        return entity.getKey()
    }
}
