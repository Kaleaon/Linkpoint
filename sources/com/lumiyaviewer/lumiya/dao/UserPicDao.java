package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

public class UserPicDao extends AbstractDao<UserPic, Long> {
    public static final String TABLENAME = "USER_PIC";

    public static class Properties {
        public static final Property Bitmap = new Property(2, byte[].class, "bitmap", false, "BITMAP");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
    }

    public UserPicDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserPicDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'USER_PIC' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'BITMAP' BLOB);");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_USER_PIC_UUID ON USER_PIC" + " (UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'USER_PIC'");
    }

    /* access modifiers changed from: protected */
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

    public Long getKey(UserPic userPic) {
        if (userPic != null) {
            return userPic.getId();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public UserPic readEntity(Cursor cursor, int i) {
        byte[] bArr = null;
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        String string = cursor.isNull(i + 1) ? null : cursor.getString(i + 1);
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2);
        }
        return new UserPic(valueOf, string, bArr);
    }

    public void readEntity(Cursor cursor, UserPic userPic, int i) {
        byte[] bArr = null;
        userPic.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        userPic.setUuid(cursor.isNull(i + 1) ? null : cursor.getString(i + 1));
        if (!cursor.isNull(i + 2)) {
            bArr = cursor.getBlob(i + 2);
        }
        userPic.setBitmap(bArr);
    }

    public Long readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return Long.valueOf(cursor.getLong(i + 0));
    }

    /* access modifiers changed from: protected */
    public Long updateKeyAfterInsert(UserPic userPic, long j) {
        userPic.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
