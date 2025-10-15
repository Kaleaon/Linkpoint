package com.lumiyaviewer.lumiya.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import de.greenrobot.dao.AbstractDao
import de.greenrobot.dao.Property
import de.greenrobot.dao.internal.DaoConfig
import java.util.UUID

class SearchGridResultDao : AbstractDao<SearchGridResult, Long> {
    String TABLENAME = "SearchGridResults"

    class Properties {
        Property Id = Property(0, Long.class, "id", true, "_id")
        Property ItemName = Property(4, String.class, "itemName", false, "ITEM_NAME")
        Property ItemType = Property(2, Int.TYPE, "itemType", false, "ITEM_TYPE")
        Property ItemUUID = Property(3, UUID.class, "itemUUID", false, "ITEM_UUID")
        Property LevensteinDistance = Property(5, Int.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE")
        Property MemberCount = Property(6, Int.class, "memberCount", false, "MEMBER_COUNT")
        Property SearchUUID = Property(1, UUID.class, "searchUUID", false, "SEARCH_UUID")
    }

    constructor(daoConfig: DaoConfig) {
        super(daoConfig)
    }

    constructor(daoConfig: DaoConfig, daoSession: DaoSession) {
        super(daoConfig, daoSession)
    }

    fun createTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'SearchGridResults' (" + "'_id' INTEGER PRIMARY KEY ," + "'SEARCH_UUID' TEXT NOT NULL ," + "'ITEM_TYPE' INTEGER NOT NULL ," + "'ITEM_UUID' TEXT NOT NULL ," + "'ITEM_NAME' TEXT NOT NULL ," + "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," + "'MEMBER_COUNT' INTEGER);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults" + " (SEARCH_UUID);")
    }

    fun dropTable(sQLiteDatabase: SQLiteDatabase, z: Boolean): Unit {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'SearchGridResults'")
    }

    protected fun bindValues(sQLiteStatement: SQLiteStatement, searchGridResult: SearchGridResult): Unit {
        sQLiteStatement.clearBindings()
        Long id = searchGridResult.getId()
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue())
        }
        sQLiteStatement.bindString(2, searchGridResult.getSearchUUID().toString())
        sQLiteStatement.bindLong(3, (Long) searchGridResult.getItemType())
        sQLiteStatement.bindString(4, searchGridResult.getItemUUID().toString())
        sQLiteStatement.bindString(5, searchGridResult.getItemName())
        sQLiteStatement.bindLong(6, (Long) searchGridResult.getLevensteinDistance())
        Int memberCount = searchGridResult.getMemberCount()
        if (memberCount != null) {
            sQLiteStatement.bindLong(7, (Long) memberCount.intValue())
        }
    }

    fun getKey(searchGridResult: SearchGridResult): Long {
        return searchGridResult != null ? searchGridResult.getId() : null
    }

    protected fun isEntityUpdateable(): Boolean {
        return true
    }

    fun readEntity(cursor: Cursor, i: Int): SearchGridResult {
        Int num = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        UUID fromString = UUID.fromString(cursor.getString(i + 1))
        Int i2 = cursor.getInt(i + 2)
        UUID fromString2 = UUID.fromString(cursor.getString(i + 3))
        String string = cursor.getString(i + 4)
        Int i3 = cursor.getInt(i + 5)
        if (!cursor.isNull(i + 6)) {
            num = Int.valueOf(cursor.getInt(i + 6))
        }
        return SearchGridResult(valueOf, fromString, i2, fromString2, string, i3, num)
    }

    fun readEntity(cursor: Cursor, searchGridResult: SearchGridResult, i: Int): Unit {
        Int num = null
        searchGridResult.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)))
        searchGridResult.setSearchUUID(UUID.fromString(cursor.getString(i + 1)))
        searchGridResult.setItemType(cursor.getInt(i + 2))
        searchGridResult.setItemUUID(UUID.fromString(cursor.getString(i + 3)))
        searchGridResult.setItemName(cursor.getString(i + 4))
        searchGridResult.setLevensteinDistance(cursor.getInt(i + 5))
        if (!cursor.isNull(i + 6)) {
            num = Int.valueOf(cursor.getInt(i + 6))
        }
        searchGridResult.setMemberCount(num)
    }

    fun readKey(cursor: Cursor, i: Int): Long {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected fun updateKeyAfterInsert(searchGridResult: SearchGridResult, j: Long): Long {
        searchGridResult.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
