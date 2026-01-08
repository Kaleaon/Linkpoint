package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class SearchGridResultDao(config: DaoConfig, daoSession: DaoSession?) : AbstractDao<SearchGridResult, Long>(config, daoSession) {

    companion object {
        const val TABLENAME = "SearchGridResults"

        object Properties {
            @JvmField val Id = Property(0, Long::class.java, "id", true, "_id")
            @JvmField val ItemName = Property(4, String::class.java, "itemName", false, "ITEM_NAME")
            @JvmField val ItemType = Property(2, Int::class.java, "itemType", false, "ITEM_TYPE")
            @JvmField val ItemUUID = Property(3, String::class.java, "itemUUID", false, "ITEM_UUID")
            @JvmField val LevensteinDistance = Property(5, Int::class.java, "levensteinDistance", false, "LEVENSTEIN_DISTANCE")
            @JvmField val MemberCount = Property(6, Int::class.java, "memberCount", false, "MEMBER_COUNT")
            @JvmField val SearchUUID = Property(1, String::class.java, "searchUUID", false, "SEARCH_UUID")
        }

        @JvmStatic
        fun createTable(db: SQLiteDatabase, ifNotExists: Boolean) {
            val constraint = if (ifNotExists) "IF NOT EXISTS " else ""
            db.execSQL("CREATE TABLE $constraint'SearchGridResults' (" +
                    "'_id' INTEGER PRIMARY KEY ," +
                    "'SEARCH_UUID' TEXT NOT NULL ," +
                    "'ITEM_TYPE' INTEGER NOT NULL ," +
                    "'ITEM_UUID' TEXT NOT NULL ," +
                    "'ITEM_NAME' TEXT NOT NULL ," +
                    "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," +
                    "'MEMBER_COUNT' INTEGER);")
            db.execSQL("CREATE INDEX ${constraint}IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults (SEARCH_UUID);")
        }

        @JvmStatic
        fun dropTable(db: SQLiteDatabase, ifExists: Boolean) {
            val constraint = if (ifExists) "IF EXISTS " else ""
            db.execSQL("DROP TABLE $constraint'SearchGridResults'")
        }
    }

    override fun bindValues(stmt: SQLiteStatement, entity: SearchGridResult) {
        stmt.clearBindings()
        val id = entity.id
        if (id != null) {
            stmt.bindLong(1, id)
        }
        stmt.bindString(2, entity.searchUUID.toString())
        stmt.bindLong(3, entity.itemType.toLong())
        stmt.bindString(4, entity.itemUUID.toString())
        stmt.bindString(5, entity.itemName)
        stmt.bindLong(6, entity.levensteinDistance.toLong())
        
        val memberCount = entity.memberCount
        if (memberCount != null) {
            stmt.bindLong(7, memberCount.toLong())
        }
    }

    override fun getKey(entity: SearchGridResult?): Long? {
        return entity?.id
    }

    override fun isEntityUpdateable(): Boolean {
        return true
    }

    override fun readEntity(cursor: Cursor, offset: Int): SearchGridResult {
        val id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        val searchUUID = UUID.fromString(cursor.getString(offset + 1))
        val itemType = cursor.getInt(offset + 2)
        val itemUUID = UUID.fromString(cursor.getString(offset + 3))
        val itemName = cursor.getString(offset + 4)
        val levensteinDistance = cursor.getInt(offset + 5)
        val memberCount = if (cursor.isNull(offset + 6)) null else cursor.getInt(offset + 6)
        
        return SearchGridResult(id, searchUUID, itemType, itemUUID, itemName, levensteinDistance, memberCount)
    }

    override fun readEntity(cursor: Cursor, entity: SearchGridResult, offset: Int) {
        entity.id = if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
        entity.searchUUID = UUID.fromString(cursor.getString(offset + 1))
        entity.itemType = cursor.getInt(offset + 2)
        entity.itemUUID = UUID.fromString(cursor.getString(offset + 3))
        entity.itemName = cursor.getString(offset + 4)
        entity.levensteinDistance = cursor.getInt(offset + 5)
        entity.memberCount = if (cursor.isNull(offset + 6)) null else cursor.getInt(offset + 6)
    }

    override fun readKey(cursor: Cursor, offset: Int): Long? {
        return if (cursor.isNull(offset + 0)) null else cursor.getLong(offset + 0)
    }

    override fun updateKeyAfterInsert(entity: SearchGridResult, rowId: Long): Long? {
        entity.id = rowId
        return rowId
    }
}
