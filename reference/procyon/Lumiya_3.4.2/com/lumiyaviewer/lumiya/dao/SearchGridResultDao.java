// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class SearchGridResultDao extends AbstractDao<SearchGridResult, Long>
{
    public static final String TABLENAME = "SearchGridResults";
    
    public SearchGridResultDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public SearchGridResultDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String s;
        if (b) {
            s = "IF NOT EXISTS ";
        }
        else {
            s = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'SearchGridResults' (" + "'_id' INTEGER PRIMARY KEY ," + "'SEARCH_UUID' TEXT NOT NULL ," + "'ITEM_TYPE' INTEGER NOT NULL ," + "'ITEM_UUID' TEXT NOT NULL ," + "'ITEM_NAME' TEXT NOT NULL ," + "'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ," + "'MEMBER_COUNT' INTEGER);");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults" + " (SEARCH_UUID);");
    }
    
    public static void dropTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        final StringBuilder append = new StringBuilder().append("DROP TABLE ");
        String str;
        if (b) {
            str = "IF EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL(append.append(str).append("'SearchGridResults'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final SearchGridResult searchGridResult) {
        sqLiteStatement.clearBindings();
        final Long id = searchGridResult.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        sqLiteStatement.bindString(2, searchGridResult.getSearchUUID().toString());
        sqLiteStatement.bindLong(3, (long)searchGridResult.getItemType());
        sqLiteStatement.bindString(4, searchGridResult.getItemUUID().toString());
        sqLiteStatement.bindString(5, searchGridResult.getItemName());
        sqLiteStatement.bindLong(6, (long)searchGridResult.getLevensteinDistance());
        final Integer memberCount = searchGridResult.getMemberCount();
        if (memberCount != null) {
            sqLiteStatement.bindLong(7, (long)memberCount);
        }
    }
    
    public Long getKey(final SearchGridResult searchGridResult) {
        if (searchGridResult != null) {
            return searchGridResult.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public SearchGridResult readEntity(final Cursor cursor, final int n) {
        final Integer n2 = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        final UUID fromString = UUID.fromString(cursor.getString(n + 1));
        final int int1 = cursor.getInt(n + 2);
        final UUID fromString2 = UUID.fromString(cursor.getString(n + 3));
        final String string = cursor.getString(n + 4);
        final int int2 = cursor.getInt(n + 5);
        Integer value2;
        if (cursor.isNull(n + 6)) {
            value2 = n2;
        }
        else {
            value2 = cursor.getInt(n + 6);
        }
        return new SearchGridResult(value, fromString, int1, fromString2, string, int2, value2);
    }
    
    public void readEntity(final Cursor cursor, final SearchGridResult searchGridResult, final int n) {
        final Integer n2 = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        searchGridResult.setId(value);
        searchGridResult.setSearchUUID(UUID.fromString(cursor.getString(n + 1)));
        searchGridResult.setItemType(cursor.getInt(n + 2));
        searchGridResult.setItemUUID(UUID.fromString(cursor.getString(n + 3)));
        searchGridResult.setItemName(cursor.getString(n + 4));
        searchGridResult.setLevensteinDistance(cursor.getInt(n + 5));
        Integer value2;
        if (cursor.isNull(n + 6)) {
            value2 = n2;
        }
        else {
            value2 = cursor.getInt(n + 6);
        }
        searchGridResult.setMemberCount(value2);
    }
    
    public Long readKey(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return value;
    }
    
    @Override
    protected Long updateKeyAfterInsert(final SearchGridResult searchGridResult, final long n) {
        searchGridResult.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property Id;
        public static final Property ItemName;
        public static final Property ItemType;
        public static final Property ItemUUID;
        public static final Property LevensteinDistance;
        public static final Property MemberCount;
        public static final Property SearchUUID;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            SearchUUID = new Property(1, UUID.class, "searchUUID", false, "SEARCH_UUID");
            ItemType = new Property(2, Integer.TYPE, "itemType", false, "ITEM_TYPE");
            ItemUUID = new Property(3, UUID.class, "itemUUID", false, "ITEM_UUID");
            ItemName = new Property(4, String.class, "itemName", false, "ITEM_NAME");
            LevensteinDistance = new Property(5, Integer.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE");
            MemberCount = new Property(6, Integer.class, "memberCount", false, "MEMBER_COUNT");
        }
    }
}
