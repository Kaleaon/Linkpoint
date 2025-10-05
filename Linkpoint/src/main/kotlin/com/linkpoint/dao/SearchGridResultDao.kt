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
    Unit createTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        String str = z ? "IF NOT EXISTS " : ""
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'SearchGridResults' (" + "'_id' INTEGER PRIMARY KEY ," + "'SEARCH_UUID' TEXT NOT NULL ," + "'ITEM_TYPE' INTEGER NOT NULL ," + "'ITEM_UUID' TEXT NOT NULL ," + "'ITEM_NAME' TEXT NOT NULL ," + "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," + "'MEMBER_COUNT' INTEGER);")
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults" + " (SEARCH_UUID);")
    }

    @JvmStatic
    Unit dropTable(SQLiteDatabase sQLiteDatabase, Boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'SearchGridResults'")
    }

    protected Unit bindValues(SQLiteStatement sQLiteStatement, SearchGridResult searchGridResult) {
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
        Integer memberCount = searchGridResult.getMemberCount()
        if (memberCount != null) {
            sQLiteStatement.bindLong(7, (Long) memberCount.intValue())
        }
    }

    public Long getKey(SearchGridResult searchGridResult) {
        return searchGridResult != null ? searchGridResult.getId() : null
    }

    protected Boolean isEntityUpdateable() {
        return true
    }

    public SearchGridResult readEntity(Cursor cursor, Int i) {
        Integer num = null
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
        UUID fromString = UUID.fromString(cursor.getString(i + 1))
        Int i2 = cursor.getInt(i + 2)
        UUID fromString2 = UUID.fromString(cursor.getString(i + 3))
        String string = cursor.getString(i + 4)
        Int i3 = cursor.getInt(i + 5)
        if (!cursor.isNull(i + 6)) {
            num = Integer.valueOf(cursor.getInt(i + 6))
        }
        return SearchGridResult(valueOf, fromString, i2, fromString2, string, i3, num)
    }

    fun readEntity(Cursor cursor, SearchGridResult searchGridResult, Int i) {
        Integer num = null
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

    public Long readKey(Cursor cursor, Int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0))
    }

    protected Long updateKeyAfterInsert(SearchGridResult searchGridResult, Long j) {
        searchGridResult.setId(Long.valueOf(j))
        return Long.valueOf(j)
    }
}
