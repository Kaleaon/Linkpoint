package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.app.NotificationCompat;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
/* loaded from: classes.dex */
public class CachedAssetDao extends AbstractDao<CachedAsset, String> {
    public static final String TABLENAME = "CachedAssets";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Key = new Property(0, String.class, "key", true, "KEY");
        public static final Property Status = new Property(1, Integer.TYPE, NotificationCompat.CATEGORY_STATUS, false, "STATUS");
        public static final Property Data = new Property(2, byte[].class, "data", false, "DATA");
        public static final Property MustRevalidate = new Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
    }

    public CachedAssetDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public CachedAssetDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'CachedAssets' ('KEY' TEXT PRIMARY KEY NOT NULL ,'STATUS' INTEGER NOT NULL ,'DATA' BLOB,'MUST_REVALIDATE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CachedAssets'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, CachedAsset cachedAsset) {
        sQLiteStatement.clearBindings();
        String key = cachedAsset.getKey();
        if (key != null) {
            sQLiteStatement.bindString(1, key);
        }
        sQLiteStatement.bindLong(2, cachedAsset.getStatus());
        byte[] data = cachedAsset.getData();
        if (data != null) {
            sQLiteStatement.bindBlob(3, data);
        }
        sQLiteStatement.bindLong(4, cachedAsset.getMustRevalidate() ? 1L : 0L);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public String getKey(CachedAsset cachedAsset) {
        if (cachedAsset != null) {
            return cachedAsset.getKey();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public boolean isEntityUpdateable() {
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.AbstractDao
    public CachedAsset readEntity(Cursor cursor, int i) {
        return new CachedAsset(cursor.isNull(i + 0) ? null : cursor.getString(i + 0), cursor.getInt(i + 1), cursor.isNull(i + 2) ? null : cursor.getBlob(i + 2), cursor.getShort(i + 3) != 0);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, CachedAsset cachedAsset, int i) {
        cachedAsset.setKey(cursor.isNull(i + 0) ? null : cursor.getString(i + 0));
        cachedAsset.setStatus(cursor.getInt(i + 1));
        cachedAsset.setData(cursor.isNull(i + 2) ? null : cursor.getBlob(i + 2));
        cachedAsset.setMustRevalidate(cursor.getShort(i + 3) != 0);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public String readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return cursor.getString(i + 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public String updateKeyAfterInsert(CachedAsset cachedAsset, long j) {
        return cachedAsset.getKey();
    }
}
