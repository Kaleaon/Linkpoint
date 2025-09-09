package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class CachedResponseDao extends AbstractDao<CachedResponse, String> {
    public static final String TABLENAME = "CachedResponses";

    public static class Properties {
        public static final Property Data = new Property(1, byte[].class, "data", false, "DATA");
        public static final Property Key = new Property(0, String.class, "key", true, "KEY");
        public static final Property MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
    }

    public CachedResponseDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public CachedResponseDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedResponses' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedResponses'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, CachedResponse cachedResponse) {
        sQLiteStatement.clearBindings();
        String key = cachedResponse.getKey();
        if (key != null) {
            sQLiteStatement.bindString(1, key);
        }
        byte[] data = cachedResponse.getData();
        if (data != null) {
            sQLiteStatement.bindBlob(2, data);
        }
        sQLiteStatement.bindLong(3, cachedResponse.getMustRevalidate() ? 1 : 0);
    }

    public String getKey(CachedResponse cachedResponse) {
        if (cachedResponse != null) {
            return cachedResponse.getKey();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public CachedResponse readEntity(Cursor cursor, int i) {
        byte[] bArr = null;
        boolean z = false;
        String string = cursor.isNull(i + 0) ? null : cursor.getString(i + 0);
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1);
        }
        if (cursor.getShort(i + 2) != 0) {
            z = true;
        }
        return new CachedResponse(string, bArr, z);
    }

    public void readEntity(Cursor cursor, CachedResponse cachedResponse, int i) {
        byte[] bArr = null;
        cachedResponse.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0));
        if (!cursor.isNull(i + 1)) {
            bArr = cursor.getBlob(i + 1);
        }
        cachedResponse.setData(bArr);
        cachedResponse.setMustRevalidate(cursor.getShort(i + 2) != 0);
    }

    public String readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return cursor.getString(i + 0);
    }

    /* access modifiers changed from: protected */
    public String updateKeyAfterInsert(CachedResponse cachedResponse, long j) {
        return cachedResponse.getKey();
    }
}
