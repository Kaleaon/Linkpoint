package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class CachedResponseDao : AbstractDao<CachedResponse, String> {
    
    companion object {
        const val TABLENAME = "CachedResponses"
        
        object Properties {
            @JvmField val Data = Property(1, ByteArray::class.java, "data", false, "DATA")
            @JvmField val Key = Property(0, String::class.java, "key", true, "KEY")
            @JvmField val MustRevalidate = Property(2, Boolean::class.java, "mustRevalidate", false, "MUST_REVALIDATE")
        }
        
        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'CachedResponses' ('KEY' TEXT PRIMARY KEY NOT NULL ,'DATA' BLOB,'MUST_REVALIDATE' INTEGER NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'CachedResponses'")
        }
    }

    constructor(config: DaoConfig) : super(config)
    
    constructor(config: DaoConfig, daoSession: DaoSession) : super(config, daoSession)

    override fun bindValues(stmt: SQLiteStatement, entity: CachedResponse) {
        stmt.clearBindings()
        val key = entity.getKey()
        if (key != null) {
            stmt.bindString(1, key)
        }
        val data = entity.getData()
        if (data != null) {
            stmt.bindBlob(2, data)
        }
        stmt.bindLong(3, if (entity.getMustRevalidate()) 1L else 0L)
    }

    override fun getKey(entity: CachedResponse?): String? {
        return entity?.getKey()
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): CachedResponse {
        val key = if (cursor.isNull(offset + 0)) null else cursor.getString(offset + 0)
        val data = if (cursor.isNull(offset + 1)) null else cursor.getBlob(offset + 1)
        val mustRevalidate = cursor.getShort(offset + 2) != 0.toShort()
        
        return CachedResponse(key ?: "", data, mustRevalidate)
    }

    override fun readEntity(cursor: Cursor, entity: CachedResponse, offset: Int) {
        entity.setKey(if (cursor.isNull(offset + 0)) "" else cursor.getString(offset + 0))
        entity.setData(if (cursor.isNull(offset + 1)) null else cursor.getBlob(offset + 1))
        entity.setMustRevalidate(cursor.getShort(offset + 2) != 0.toShort())
    }

    override fun readKey(cursor: Cursor, offset: Int): String? {
        return if (cursor.isNull(offset + 0)) null else cursor.getString(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: CachedResponse, rowId: Long): String? {
        return entity.getKey()
    }
}
