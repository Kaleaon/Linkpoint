package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.app.NotificationCompat;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class CachedAssetDao extends AbstractDao<CachedAsset, String> {
    public static final String TABLENAME = "CachedAssets";

    public static class Properties {
        public static final Property Data = new Property(2, byte[].class, "data", false, "DATA");
        public static final Property Key = new Property(0, String.class, "key", true, "KEY");
        public static final Property MustRevalidate = new Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        public static final Property Status = new Property(1, Integer.TYPE, NotificationCompat.CATEGORY_STATUS, false, "STATUS");
    }

    public CachedAssetDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public CachedAssetDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedAssets' (" + "'KEY' TEXT PRIMARY KEY NOT NULL ," + "'STATUS' INTEGER NOT NULL ," + "'DATA' BLOB," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedAssets'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, CachedAsset cachedAsset) {
        sQLiteStatement.clearBindings();
        String key = cachedAsset.getKey();
        if (key != null) {
            sQLiteStatement.bindString(1, key);
        }
        sQLiteStatement.bindLong(2, (long) cachedAsset.getStatus());
        byte[] data = cachedAsset.getData();
        if (data != null) {
            sQLiteStatement.bindBlob(3, data);
        }
        sQLiteStatement.bindLong(4, cachedAsset.getMustRevalidate() ? 1 : 0);
    }

    public String getKey(CachedAsset cachedAsset) {
        if (cachedAsset != null) {
            return cachedAsset.getKey();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public CachedAsset readEntity(Cursor cursor, int i) {
        byte[] bArr = null;
        boolean z = false;
        String string = cursor.isNull(i + 0) ? null : cursor.getString(i + 0);
        int i2 = cursor.getInt(i + 1);
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2);
        }
        if (cursor.getShort(i + 3) != 0) {
            z = true;
        }
        return new CachedAsset(string, i2, bArr, z);
    }

    public void readEntity(Cursor cursor, CachedAsset cachedAsset, int i) {
        byte[] bArr = null;
        cachedAsset.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0));
        cachedAsset.setStatus(cursor.getInt(i + 1));
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2);
        }
        cachedAsset.setData(bArr);
        cachedAsset.setMustRevalidate(cursor.getShort(i + 3) != 0);
    }

    public String readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return cursor.getString(i + 0);
    }

    /* access modifiers changed from: protected */
    public String updateKeyAfterInsert(CachedAsset cachedAsset, long j) {
        return cachedAsset.getKey();
    }
}
