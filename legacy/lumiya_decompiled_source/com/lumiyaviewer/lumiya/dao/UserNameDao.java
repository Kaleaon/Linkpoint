package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;
/* loaded from: classes.dex */
public class UserNameDao extends AbstractDao<UserName, UUID> {
    public static final String TABLENAME = "UserNames";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
        public static final Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public static final Property DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
        public static final Property IsBadUUID = new Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID");
    }

    public UserNameDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserNameDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'UserNames' ('UUID' TEXT PRIMARY KEY ,'USER_NAME' TEXT,'DISPLAY_NAME' TEXT,'IS_BAD_UUID' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'UserNames'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, UserName userName) {
        sQLiteStatement.clearBindings();
        UUID uuid = userName.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString());
        }
        String userName2 = userName.getUserName();
        if (userName2 != null) {
            sQLiteStatement.bindString(2, userName2);
        }
        String displayName = userName.getDisplayName();
        if (displayName != null) {
            sQLiteStatement.bindString(3, displayName);
        }
        sQLiteStatement.bindLong(4, userName.getIsBadUUID() ? 1L : 0L);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public UUID getKey(UserName userName) {
        if (userName != null) {
            return userName.getUuid();
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
    public UserName readEntity(Cursor cursor, int i) {
        return new UserName(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), cursor.isNull(i + 1) ? null : cursor.getString(i + 1), cursor.isNull(i + 2) ? null : cursor.getString(i + 2), cursor.getShort(i + 3) != 0);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, UserName userName, int i) {
        userName.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        userName.setUserName(cursor.isNull(i + 1) ? null : cursor.getString(i + 1));
        userName.setDisplayName(cursor.isNull(i + 2) ? null : cursor.getString(i + 2));
        userName.setIsBadUUID(cursor.getShort(i + 3) != 0);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public UUID readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return UUID.fromString(cursor.getString(i + 0));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public UUID updateKeyAfterInsert(UserName userName, long j) {
        return userName.getUuid();
    }
}
