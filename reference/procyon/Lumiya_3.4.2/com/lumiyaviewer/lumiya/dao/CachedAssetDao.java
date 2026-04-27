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

public class CachedAssetDao extends AbstractDao<CachedAsset, String>
{
    public static final String TABLENAME = "CachedAssets";
    
    public CachedAssetDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public CachedAssetDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'CachedAssets' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'STATUS' INTEGER NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'CachedAssets'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final CachedAsset cachedAsset) {
        sqLiteStatement.clearBindings();
        final String key = cachedAsset.getKey();
        if (key != null) {
            sqLiteStatement.bindString(1, key);
        }
        sqLiteStatement.bindLong(2, (long)cachedAsset.getStatus());
        final byte[] data = cachedAsset.getData();
        if (data != null) {
            sqLiteStatement.bindBlob(3, data);
        }
        long n;
        if (cachedAsset.getMustRevalidate()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(4, n);
    }
    
    public String getKey(final CachedAsset cachedAsset) {
        if (cachedAsset != null) {
            return cachedAsset.getKey();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public CachedAsset readEntity(final Cursor cursor, final int n) {
        byte[] blob = null;
        boolean b = false;
        String string;
        if (cursor.isNull(n + 0)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 0);
        }
        final int int1 = cursor.getInt(n + 1);
        if (!cursor.isNull(n + 2)) {
            blob = cursor.getBlob(n + 2);
        }
        if (cursor.getShort(n + 3) != 0) {
            b = true;
        }
        return new CachedAsset(string, int1, blob, b);
    }
    
    public void readEntity(final Cursor cursor, final CachedAsset cachedAsset, final int n) {
        final byte[] array = null;
        String string;
        if (cursor.isNull(n + 0)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 0);
        }
        cachedAsset.setKey(string);
        cachedAsset.setStatus(cursor.getInt(n + 1));
        byte[] blob;
        if (cursor.isNull(n + 2)) {
            blob = array;
        }
        else {
            blob = cursor.getBlob(n + 2);
        }
        cachedAsset.setData(blob);
        cachedAsset.setMustRevalidate(cursor.getShort(n + 3) != 0);
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
    protected String updateKeyAfterInsert(final CachedAsset cachedAsset, final long n) {
        return cachedAsset.getKey();
    }
    
    public static class Properties
    {
        public static final Property Data;
        public static final Property Key;
        public static final Property MustRevalidate;
        public static final Property Status;
        
        static {
            Key = new Property(0, String.class, "key", true, "KEY");
            Status = new Property(1, Integer.TYPE, "status", false, "STATUS");
            Data = new Property(2, byte[].class, "data", false, "DATA");
            MustRevalidate = new Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }
    }
}
