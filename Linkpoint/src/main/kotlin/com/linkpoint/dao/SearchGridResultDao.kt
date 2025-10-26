package com.linkpoint.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class SearchGridResultDao : AbstractDao()<SearchGridResult, Long> {
    const val TABLENAME: String = "SearchGridResults"

    @JvmStatic
    class Properties {
        const val Property Id = Property(0, Long.class, "id", true, "_id")
        const val Property ItemName = Property(4, String.class, "itemName", false, "ITEM_NAME")
        const val Property ItemType = Property(2, Integer.TYPE, "itemType", false, "ITEM_TYPE")
        const val Property ItemUUID = Property(3, UUID.class, "itemUUID", false, "ITEM_UUID")
        const val Property LevensteinDistance = Property(5, Integer.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE")
        const val Property MemberCount = Property(6, Integer.class, "memberCount", false, "MEMBER_COUNT")
        const val Property SearchUUID = Property(1, UUID.class, "searchUUID", false, "SEARCH_UUID")
    }

    public SearchGridResultDao(DaoConfig daoConfig) {
        super(daoConfig)
    }

    public SearchGridResultDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession)
    }

    @JvmStatic
     fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        val str: String = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'SearchGridResults' (" + "'_id' INTEGER PRIMARY KEY ," + "'SEARCH_UUID' TEXT NOT NULL ," + "'ITEM_TYPE' INTEGER NOT NULL ," + "'ITEM_UUID' TEXT NOT NULL ," + "'ITEM_NAME' TEXT NOT NULL ," + "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," + "'MEMBER_COUNT' INTEGER);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults" + " (SEARCH_UUID);")
    }

    @JvmStatic
     fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'SearchGridResults'")
    }

     protected fun bindValues(sQLiteStatement: SQLiteStatement, searchGridResult: SearchGridResult) {
        sQLiteStatement.clearBindings()
        val id: Long = searchGridResult.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindString(2, searchGridResult.getSearchUUID().toString())
        sQLiteStatement.bindLong(3, (Long) searchGridResult.getItemType())
        sQLiteStatement.bindString(4, searchGridResult.getItemUUID().toString())
        sQLiteStatement.bindString(5, searchGridResult.getItemName())
        sQLiteStatement.bindLong(6, (Long) searchGridResult.getLevensteinDistance())
        val memberCount: Integer = searchGridResult.getMemberCount()
        if (memberCount != null) {
            sQLiteStatement.bindLong(7, (Long) memberCount.intValue())
        }
    }

     public fun getKey(searchGridResult: SearchGridResult): Long {
        return searchGridResult != null ? searchGridResult.getId() : null
    }

     protected fun isEntityUpdateable(): Boolean {
        return true
    }

     public fun readEntity(cursor: Cursor, i: Int): SearchGridResult {
        val num: Integer = null
        val valueOf: Long = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        val fromString: UUID = UUID.fromString(cursor.getString(i + 1))
        val i2: Int = cursor.getInt(i + 2)
        val fromString2: UUID = UUID.fromString(cursor.getString(i + 3))
        val string: String = cursor.getString(i + 4)
        val i3: Int = cursor.getInt(i + 5)
        if (!cursor.isNull(i + 6)) {
            num = Integer.valueOf(cursor.getInt(i + 6))
        }
        return SearchGridResult(valueOf, fromString, i2, fromString2, string, i3, num)
    }

    fun readEntity(cursor: Cursor, searchGridResult: SearchGridResult, i: Int) {
        val num: Integer = null
        searchGridResult.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        searchGridResult.setSearchUUID(UUID.fromString(cursor.getString(i + 1)))
        searchGridResult.setItemType(cursor.getInt(i + 2))
        searchGridResult.setItemUUID(UUID.fromString(cursor.getString(i + 3)))
        searchGridResult.setItemName(cursor.getString(i + 4))
        searchGridResult.setLevensteinDistance(cursor.getInt(i + 5))
        if (!cursor.isNull(i + 6)) {
            num = Integer.valueOf(cursor.getInt(i + 6))
        }
        searchGridResult.setMemberCount(num)
    }

     public fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

     protected fun updateKeyAfterInsert(searchGridResult: SearchGridResult, j: Long): Long {
        searchGridResult.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
