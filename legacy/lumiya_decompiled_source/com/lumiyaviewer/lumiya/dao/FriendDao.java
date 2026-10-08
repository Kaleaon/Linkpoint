package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;
/* loaded from: classes.dex */
public class FriendDao extends AbstractDao<Friend, UUID> {
    public static final String TABLENAME = "Friends";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
        public static final Property RightsGiven = new Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        public static final Property RightsHas = new Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        public static final Property IsOnline = new Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE");
    }

    public FriendDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public FriendDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("CREATE TABLE " + (z ? "IF NOT EXISTS " : "") + "'Friends' ('UUID' TEXT PRIMARY KEY ,'RIGHTS_GIVEN' INTEGER NOT NULL ,'RIGHTS_HAS' INTEGER NOT NULL ,'IS_ONLINE' INTEGER NOT NULL );");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'Friends'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, Friend friend) {
        sQLiteStatement.clearBindings();
        UUID uuid = friend.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(1, uuid.toString());
        }
        sQLiteStatement.bindLong(2, friend.getRightsGiven());
        sQLiteStatement.bindLong(3, friend.getRightsHas());
        sQLiteStatement.bindLong(4, friend.getIsOnline() ? 1L : 0L);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public UUID getKey(Friend friend) {
        if (friend != null) {
            return friend.getUuid();
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
    public Friend readEntity(Cursor cursor, int i) {
        return new Friend(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)), cursor.getInt(i + 1), cursor.getInt(i + 2), cursor.getShort(i + 3) != 0);
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, Friend friend, int i) {
        friend.setUuid(cursor.isNull(i + 0) ? null : UUID.fromString(cursor.getString(i + 0)));
        friend.setRightsGiven(cursor.getInt(i + 1));
        friend.setRightsHas(cursor.getInt(i + 2));
        friend.setIsOnline(cursor.getShort(i + 3) != 0);
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
    public UUID updateKeyAfterInsert(Friend friend, long j) {
        return friend.getUuid();
    }
}
