package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
/* loaded from: classes.dex */
public class UserPicDao extends AbstractDao<UserPic, Long> {
    public static final String TABLENAME = "USER_PIC";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public static final Property Bitmap = new Property(2, byte[].class, "bitmap", false, "BITMAP");
    }

    public UserPicDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserPicDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'USER_PIC' ('_id' INTEGER PRIMARY KEY ,'UUID' TEXT,'BITMAP' BLOB);");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_USER_PIC_UUID ON USER_PIC (UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'USER_PIC'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, UserPic userPic) {
        sQLiteStatement.clearBindings();
        Long id = userPic.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        String uuid = userPic.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid);
        }
        byte[] bitmap = userPic.getBitmap();
        if (bitmap != null) {
            sQLiteStatement.bindBlob(3, bitmap);
        }
    }

    @Override // de.greenrobot.dao.AbstractDao
    public Long getKey(UserPic userPic) {
        if (userPic != null) {
            return userPic.getId();
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
    public UserPic readEntity(Cursor cursor, int i) {
        return new UserPic(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), cursor.isNull(i + 1) ? null : cursor.getString(i + 1), cursor.isNull(i + 2) ? null : cursor.getBlob(i + 2));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, UserPic userPic, int i) {
        userPic.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        userPic.setUuid(cursor.isNull(i + 1) ? null : cursor.getString(i + 1));
        userPic.setBitmap(cursor.isNull(i + 2) ? null : cursor.getBlob(i + 2));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // de.greenrobot.dao.AbstractDao
    public Long readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i + 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public Long updateKeyAfterInsert(UserPic userPic, long j) {
        userPic.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
