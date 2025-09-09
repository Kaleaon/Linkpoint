package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

public class FriendDao extends AbstractDao<Friend, UUID> {
    public static final String TABLENAME = "Friends";

    public static class Properties {
        public static final Property IsOnline = new Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE");
        public static final Property RightsGiven = new Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        public static final Property RightsHas = new Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        public static final Property Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
    }

    public FriendDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public FriendDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'Friends' (" + "'UUID' TEXT PRIMARY KEY ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL ," + "'IS_ONLINE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Friends'");
    }

    /* access modifiers changed from: protected */
    public void bindValues(SQLiteStatement sQLiteStatement, Friend friend) {
        sQLiteStatement.clearBindings();
        UUID uuid = friend.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString());
        }
        sQLiteStatement.bindLong(2, (long) friend.getRightsGiven());
        sQLiteStatement.bindLong(3, (long) friend.getRightsHas());
        sQLiteStatement.bindLong(4, friend.getIsOnline() ? 1 : 0);
    }

    public UUID getKey(Friend friend) {
        if (friend != null) {
            return friend.getUuid();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isEntityUpdateable() {
        return true;
    }

    public Friend readEntity(Cursor cursor, int i) {
        boolean z = false;
        UUID fromString = cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0));
        int i2 = cursor.getInt(i + 1);
        int i3 = cursor.getInt(i + 2);
        if (cursor.getShort(i + 3) != 0) {
            z = true;
        }
        return new Friend(fromString, i2, i3, z);
    }

    public void readEntity(Cursor cursor, Friend friend, int i) {
        friend.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        friend.setRightsGiven(cursor.getInt(i + 1));
        friend.setRightsHas(cursor.getInt(i + 2));
        friend.setIsOnline(cursor.getShort(i + 3) != 0);
    }

    public UUID readKey(Cursor cursor, int i) {
        if (cursor.isNull(i + 0)) {
            return null;
        }
        return UUID.fromString(cursor.getString(i + 0));
    }

    /* access modifiers changed from: protected */
    public UUID updateKeyAfterInsert(Friend friend, long j) {
        return friend.getUuid();
    }
}
