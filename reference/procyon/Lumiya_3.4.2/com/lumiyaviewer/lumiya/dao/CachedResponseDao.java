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

public class CachedResponseDao extends AbstractDao<CachedResponse, String>
{
    public static final String TABLENAME = "CachedResponses";
    
    public CachedResponseDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public CachedResponseDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'CachedResponses' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'CachedResponses'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final CachedResponse cachedResponse) {
        sqLiteStatement.clearBindings();
        final String key = cachedResponse.getKey();
        if (key != null) {
            sqLiteStatement.bindString(1, key);
        }
        final byte[] data = cachedResponse.getData();
        if (data != null) {
            sqLiteStatement.bindBlob(2, data);
        }
        long n;
        if (cachedResponse.getMustRevalidate()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(3, n);
    }
    
    public String getKey(final CachedResponse cachedResponse) {
        if (cachedResponse != null) {
            return cachedResponse.getKey();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public CachedResponse readEntity(final Cursor cursor, final int n) {
        byte[] blob = null;
        boolean b = false;
        String string;
        if (cursor.isNull(n + 0)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 0);
        }
        if (!cursor.isNull(n + 1)) {
            blob = cursor.getBlob(n + 1);
        }
        if (cursor.getShort(n + 2) != 0) {
            b = true;
        }
        return new CachedResponse(string, blob, b);
    }
    
    public void readEntity(final Cursor cursor, final CachedResponse cachedResponse, final int n) {
        final byte[] array = null;
        String string;
        if (cursor.isNull(n + 0)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 0);
        }
        cachedResponse.setKey(string);
        byte[] blob;
        if (cursor.isNull(n + 1)) {
            blob = array;
        }
        else {
            blob = cursor.getBlob(n + 1);
        }
        cachedResponse.setData(blob);
        cachedResponse.setMustRevalidate(cursor.getShort(n + 2) != 0);
    }
    
    public String readKey(final Cursor cursor, final int n) {
        String string;
        if (cursor.isNull(n + 0)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 0);
        }
        return string;
    }
    
    @Override
    protected String updateKeyAfterInsert(final CachedResponse cachedResponse, final long n) {
        return cachedResponse.getKey();
    }
    
    public static class Properties
    {
        public static final Property Data;
        public static final Property Key;
        public static final Property MustRevalidate;
        
        static {
            Key = new Property(0, String.class, "key", true, "KEY");
            Data = new Property(1, byte[].class, "data", false, "DATA");
            MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }
    }
}
