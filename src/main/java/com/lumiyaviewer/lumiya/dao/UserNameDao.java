package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class UserNameDao extends AbstractDao<UserName, UUID> {
    public static final String TABLENAME = "UserNames";

    public static class Properties {
        public static final Property DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
        public static final Property IsBadUUID = new Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID");
        public static final Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public static final Property Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
    }

    public UserNameDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserNameDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'UserNames' (" + "'UUID' TEXT PRIMARY KEY ," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'IS_BAD_UUID' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'UserNames'");
    }

    protected void bindValues(SQLiteStatement sQLiteStatement, UserName userName) {
        sQLiteStatement.clearBindings();
        UUID uuid = userName.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString());
        }
        String userName2 = userName.getUserName();
        if (userName2 != null) {
            sQLiteStatement.bindString(2, userName2);
        }
        userName2 = userName.getDisplayName();
        if (userName2 != null) {
            sQLiteStatement.bindString(3, userName2);
        }
        sQLiteStatement.bindLong(4, userName.getIsBadUUID() ? 1 : 0);
    }

    public UUID getKey(UserName userName) {
        return userName != null ? userName.getUuid() : null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public UserName readEntity(Cursor cursor, int i) {
        boolean z = false;
        String str = null;
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0));
        String string = cursor.isNull(i + 1) ? null : cursor.getString(i + 1);
        if (!cursor.isNull(i + 2)) {
            str = cursor.getString(i + 2);
        }
        if (cursor.getShort(i + 3) != (short) 0) {
            z = true;
        }
        return new UserName(fromString, string, str, z);
    }

    public void readEntity(Cursor cursor, UserName userName, int i) {
        String str = null;
        userName.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        userName.setUserName(cursor.isNull(i + 1) ? null : cursor.getString(i + 1));
        if (!cursor.isNull(i + 2)) {
            str = cursor.getString(i + 2);
        }
        userName.setDisplayName(str);
        userName.setIsBadUUID(cursor.getShort(i + 3) != (short) 0);
    }

    public UUID readKey(Cursor cursor, int i) {
        return cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0));
    }

    protected UUID updateKeyAfterInsert(UserName userName, long j) {
        return userName.getUuid();
    }
}
