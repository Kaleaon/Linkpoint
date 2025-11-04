// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class MuteListCachedDataDao extends AbstractDao<MuteListCachedData, Long>
{
    public static final String TABLENAME = "MUTE_LIST_CACHED_DATA";
    
    public MuteListCachedDataDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public MuteListCachedDataDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String str;
        if (b) {
            str = "IF NOT EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'MUTE_LIST_CACHED_DATA' (" + "'_id' INTEGER PRIMARY KEY ," + "'CRC' INTEGER NOT NULL ," + "'DATA' BLOB NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'MUTE_LIST_CACHED_DATA'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final MuteListCachedData muteListCachedData) {
        sqLiteStatement.clearBindings();
        final Long id = muteListCachedData.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        sqLiteStatement.bindLong(2, (long)muteListCachedData.getCRC());
        sqLiteStatement.bindBlob(3, muteListCachedData.getData());
    }
    
    public Long getKey(final MuteListCachedData muteListCachedData) {
        if (muteListCachedData != null) {
            return muteListCachedData.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public MuteListCachedData readEntity(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return new MuteListCachedData(value, cursor.getInt(n + 1), cursor.getBlob(n + 2));
    }
    
    public void readEntity(final Cursor cursor, final MuteListCachedData muteListCachedData, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        muteListCachedData.setId(value);
        muteListCachedData.setCRC(cursor.getInt(n + 1));
        muteListCachedData.setData(cursor.getBlob(n + 2));
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
    protected Long updateKeyAfterInsert(final MuteListCachedData muteListCachedData, final long n) {
        muteListCachedData.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property CRC;
        public static final Property Data;
        public static final Property Id;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            CRC = new Property(1, Integer.TYPE, "CRC", false, "CRC");
            Data = new Property(2, byte[].class, "data", false, "DATA");
        }
    }
}
