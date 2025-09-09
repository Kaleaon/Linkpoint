package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class UserDao extends AbstractDao<User, Long> {
    public static final String TABLENAME = "Users";

    public static class Properties {
        public static final Property BadUUID = new Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID");
        public static final Property DisplayName = new Property(3, String.class, "displayName", false, "DISPLAY_NAME");
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property IsFriend = new Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND");
        public static final Property RightsGiven = new Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        public static final Property RightsHas = new Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        public static final Property UserName = new Property(2, String.class, "userName", false, "USER_NAME");
        public static final Property Uuid = new Property(1, UUID.class, "uuid", false, "UUID");
    }

    public UserDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public UserDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'Users' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'BAD_UUID' INTEGER NOT NULL ," + "'IS_FRIEND' INTEGER NOT NULL ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL );");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_Users_UUID ON Users" + " (UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Users'");
    }

    protected void bindValues(SQLiteStatement sQLiteStatement, User user) {
        long j = 1;
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
        userName = user.getDisplayName();
        if (userName != null) {
            sQLiteStatement.bindString(4, userName);
        }
        sQLiteStatement.bindLong(5, user.getBadUUID() ? 1 : 0);
        if (!user.getIsFriend()) {
            j = 0;
        }
        sQLiteStatement.bindLong(6, j);
        sQLiteStatement.bindLong(7, (long) user.getRightsGiven());
        sQLiteStatement.bindLong(8, (long) user.getRightsHas());
    }

    public Long getKey(User user) {
        return user != null ? user.getId() : null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }

    public User readEntity(Cursor cursor, int i) {
        boolean z = true;
        String str = null;
        Long valueOf = cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
        UUID fromString = cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1));
        String string = cursor.isNull(i + 2) ? null : cursor.getString(i + 2);
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3);
        }
        boolean z2 = cursor.getShort(i + 4) != (short) 0;
        if (cursor.getShort(i + 5) == (short) 0) {
            z = false;
        }
        return new User(valueOf, fromString, string, str, z2, z, cursor.getInt(i + 6), cursor.getInt(i + 7));
    }

    public void readEntity(Cursor cursor, User user, int i) {
        boolean z = true;
        String str = null;
        user.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        user.setUuid(cursor.isNull(i + 1) ? null : UUID.fromString(cursor.getString(i + 1)));
        user.setUserName(cursor.isNull(i + 2) ? null : cursor.getString(i + 2));
        if (!cursor.isNull(i + 3)) {
            str = cursor.getString(i + 3);
        }
        user.setDisplayName(str);
        user.setBadUUID(cursor.getShort(i + 4) != (short) 0);
        if (cursor.getShort(i + 5) == (short) 0) {
            z = false;
        }
        user.setIsFriend(z);
        user.setRightsGiven(cursor.getInt(i + 6));
        user.setRightsHas(cursor.getInt(i + 7));
    }

    public Long readKey(Cursor cursor, int i) {
        return cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0));
    }

    protected Long updateKeyAfterInsert(User user, long j) {
        user.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
