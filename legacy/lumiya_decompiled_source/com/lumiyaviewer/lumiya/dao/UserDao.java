package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;
/* loaded from: classes.dex */
public class UserDao extends AbstractDao<User, Long> {
    public static final String TABLENAME = "Users";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Uuid = new Property(1, UUID.class, "uuid", false, "UUID");
        public static final Property UserName = new Property(2, String.class, "userName", false, "USER_NAME");
        public static final Property DisplayName = new Property(3, String.class, "displayName", false, "DISPLAY_NAME");
        public static final Property BadUUID = new Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID");
        public static final Property IsFriend = new Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND");
        public static final Property RightsGiven = new Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        public static final Property RightsHas = new Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
    }

    public UserDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'Users' ('_id' INTEGER PRIMARY KEY ,'UUID' TEXT,'USER_NAME' TEXT,'DISPLAY_NAME' TEXT,'BAD_UUID' INTEGER NOT NULL ,'IS_FRIEND' INTEGER NOT NULL ,'RIGHTS_GIVEN' INTEGER NOT NULL ,'RIGHTS_HAS' INTEGER NOT NULL );");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_Users_UUID ON Users (UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Users'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, User user) {
        sQLiteStatement.clearBindings();
        Long id = user.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        UUID uuid = user.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(2, uuid.toString());
        }
        String userName = user.getUserName();
        if (userName != null) {
            sQLiteStatement.bindString(3, userName);
        }
        String displayName = user.getDisplayName();
        if (displayName != null) {
            sQLiteStatement.bindString(4, displayName);
        }
        sQLiteStatement.bindLong(5, user.getBadUUID() ? 1L : 0L);
        sQLiteStatement.bindLong(6, user.getIsFriend() ? 1L : 0L);
        sQLiteStatement.bindLong(7, user.getRightsGiven());
        sQLiteStatement.bindLong(8, user.getRightsHas());
    }

    @Override // de.greenrobot.dao.AbstractDao
    public Long getKey(User user) {
        if (user != null) {
            return user.getId();
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
    public User readEntity(Cursor cursor, int i) {
        return new User(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1)), cursor.isNull(i + 2) ? null : cursor.getString(i + 2), cursor.isNull(i + 3) ? null : cursor.getString(i + 3), cursor.getShort(i + 4) != 0, cursor.getShort(i + 5) != 0, cursor.getInt(i + 6), cursor.getInt(i + 7));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, User user, int i) {
        user.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        user.setUuid(cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1)));
        user.setUserName(cursor.isNull(i + 2) ? null : cursor.getString(i + 2));
        user.setDisplayName(cursor.isNull(i + 3) ? null : cursor.getString(i + 3));
        user.setBadUUID(cursor.getShort(i + 4) != 0);
        user.setIsFriend(cursor.getShort(i + 5) != 0);
        user.setRightsGiven(cursor.getInt(i + 6));
        user.setRightsHas(cursor.getInt(i + 7));
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
    public Long updateKeyAfterInsert(User user, long j) {
        user.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
