package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class SearchGridResultDao extends AbstractDao<SearchGridResult, Long> {
    public static final String TABLENAME = "SearchGridResults";

    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property ItemName = new Property(4, String.class, "itemName", false, "ITEM_NAME");
        public static final Property ItemType = new Property(2, Integer.TYPE, "itemType", false, "ITEM_TYPE");
        public static final Property ItemUUID = new Property(3, UUID.class, "itemUUID", false, "ITEM_UUID");
        public static final Property LevensteinDistance = new Property(5, Integer.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE");
        public static final Property MemberCount = new Property(6, Integer.class, "memberCount", false, "MEMBER_COUNT");
        public static final Property SearchUUID = new Property(1, UUID.class, "searchUUID", false, "SEARCH_UUID");
    }

    public SearchGridResultDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public SearchGridResultDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'SearchGridResults' (" + "'_id' INTEGER PRIMARY KEY ," + "'SEARCH_UUID' TEXT NOT NULL ," + "'ITEM_TYPE' INTEGER NOT NULL ," + "'ITEM_UUID' TEXT NOT NULL ," + "'ITEM_NAME' TEXT NOT NULL ," + "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," + "'MEMBER_COUNT' INTEGER);");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults" + " (SEARCH_UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'SearchGridResults'");
    }

    protected void bindValues(SQLiteStatement sQLiteStatement, SearchGridResult searchGridResult) {
        sQLiteStatement.clearBindings();
        Long id = searchGridResult.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        sQLiteStatement.bindString(2, searchGridResult.getSearchUUID().toString());
        sQLiteStatement.bindLong(3, (long) searchGridResult.getItemType());
        sQLiteStatement.bindString(4, searchGridResult.getItemUUID().toString());
        sQLiteStatement.bindString(5, searchGridResult.getItemName());
        sQLiteStatement.bindLong(6, (long) searchGridResult.getLevensteinDistance());
        Integer memberCount = searchGridResult.getMemberCount();
        if (memberCount != null) {
            sQLiteStatement.bindLong(7, (long) memberCount.intValue());
        }
    }

    public Long getKey(SearchGridResult searchGridResult) {
        return searchGridResult != null ? searchGridResult.getId() : null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public SearchGridResult readEntity(Cursor cursor, int i) {
        Integer num = null;
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        UUID fromString = UUID.fromString(cursor.getString(i + 1));
        int i2 = cursor.getInt(i + 2);
        UUID fromString2 = UUID.fromString(cursor.getString(i + 3));
        String string = cursor.getString(i + 4);
        int i3 = cursor.getInt(i + 5);
        if (!cursor.isNull(i + 6)) {
            num = Integer.valueOf(cursor.getInt(i + 6));
        }
        return new SearchGridResult(valueOf, fromString, i2, fromString2, string, i3, num);
    }

    public void readEntity(Cursor cursor, SearchGridResult searchGridResult, int i) {
        Integer num = null;
        searchGridResult.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        searchGridResult.setSearchUUID(UUID.fromString(cursor.getString(i + 1)));
        searchGridResult.setItemType(cursor.getInt(i + 2));
        searchGridResult.setItemUUID(UUID.fromString(cursor.getString(i + 3)));
        searchGridResult.setItemName(cursor.getString(i + 4));
        searchGridResult.setLevensteinDistance(cursor.getInt(i + 5));
        if (!cursor.isNull(i + 6)) {
            num = Integer.valueOf(cursor.getInt(i + 6));
        }
        searchGridResult.setMemberCount(num);
    }

    public Long readKey(Cursor cursor, int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
    }

    protected Long updateKeyAfterInsert(SearchGridResult searchGridResult, long j) {
        searchGridResult.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
