package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig

class MuteListCachedDataDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<MuteListCachedData, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "MUTE_LIST_CACHED_DATA"

        object Properties {
            @JvmField val CRC = Property(1, Int::class.java, "CRC", false, "CRC")
            @JvmField val Data = Property(2, ByteArray::class.java, "data", false, "DATA")
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'MUTE_LIST_CACHED_DATA' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'CRC' INTEGER NOT NULL ," +
                    "'DATA' BLOB NOT NULL );")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'MUTE_LIST_CACHED_DATA'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: MuteListCachedData) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        stmt.bindLong(2, entity.CRC.toLong())
        stmt.bindBlob(3, entity.data)
    }

    override fun getKey(entity: MuteListCachedData?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): MuteListCachedData {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val crc = cursor.getInt(offset + 1)
        val data = cursor.getBlob(offset + 2)
        return MuteListCachedData(id, crc, data)
    }

    override fun readEntity(cursor: Cursor, entity: MuteListCachedData, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.CRC = cursor.getInt(offset + 1)
        entity.data = cursor.getBlob(offset + 2)
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: MuteListCachedData, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
