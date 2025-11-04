package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

/* loaded from: classes.dex */
public class ChatterDao extends AbstractDao<Chatter, Long> {
    public static final String TABLENAME = "CHATTER";

    /* loaded from: classes.dex */
    public static class Properties {
        public static final Property Id = new Property(0, Long.class, "id", true, "_id");
        public static final Property Type = new Property(1, Integer.TYPE, "type", false, "TYPE");
        public static final Property Uuid = new Property(2, UUID.class, "uuid", false, "UUID");
        public static final Property Active = new Property(3, Boolean.TYPE, "active", false, "ACTIVE");
        public static final Property Muted = new Property(4, Boolean.TYPE, "muted", false, "MUTED");
        public static final Property UnreadCount = new Property(5, Integer.TYPE, "unreadCount", false, "UNREAD_COUNT");
        public static final Property LastMessageID = new Property(6, Long.class, "lastMessageID", false, "LAST_MESSAGE_ID");
        public static final Property LastSessionID = new Property(7, UUID.class, "lastSessionID", false, "LAST_SESSION_ID");
    }

    public ChatterDao(DaoConfig daoConfig) {
        super(daoConfig);
    }

    public ChatterDao(DaoConfig daoConfig, DaoSession daoSession) {
        super(daoConfig, daoSession);
    }

    public static void createTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        String str = z ? "IF NOT EXISTS " : "";
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "'CHATTER' ('_id' INTEGER PRIMARY KEY ,'TYPE' INTEGER NOT NULL ,'UUID' TEXT,'ACTIVE' INTEGER NOT NULL ,'MUTED' INTEGER NOT NULL ,'UNREAD_COUNT' INTEGER NOT NULL ,'LAST_MESSAGE_ID' INTEGER,'LAST_SESSION_ID' TEXT);");
        sQLiteDatabase.execSQL("CREATE INDEX " + str + "IDX_CHATTER_TYPE_UUID ON CHATTER (TYPE,UUID);");
    }

    public static void dropTable(SQLiteDatabase sQLiteDatabase, boolean z) {
        sQLiteDatabase.execSQL("DROP TABLE " + (z ? "IF EXISTS " : "") + "'CHATTER'");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // de.greenrobot.dao.AbstractDao
    public void bindValues(SQLiteStatement sQLiteStatement, Chatter chatter) {
        sQLiteStatement.clearBindings();
        Long id = chatter.getId();
        if (id != null) {
            sQLiteStatement.bindLong(1, id.longValue());
        }
        sQLiteStatement.bindLong(2, chatter.getType());
        UUID uuid = chatter.getUuid();
        if (uuid != null) {
            sQLiteStatement.bindString(3, uuid.toString());
        }
        sQLiteStatement.bindLong(4, chatter.getActive() ? 1L : 0L);
        sQLiteStatement.bindLong(5, chatter.getMuted() ? 1L : 0L);
        sQLiteStatement.bindLong(6, chatter.getUnreadCount());
        Long lastMessageID = chatter.getLastMessageID();
        if (lastMessageID != null) {
            sQLiteStatement.bindLong(7, lastMessageID.longValue());
        }
        UUID lastSessionID = chatter.getLastSessionID();
        if (lastSessionID != null) {
            sQLiteStatement.bindString(8, lastSessionID.toString());
        }
    }

    @Override // de.greenrobot.dao.AbstractDao
    public Long getKey(Chatter chatter) {
        if (chatter != null) {
            return chatter.getId();
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
    public Chatter readEntity(Cursor cursor, int i) {
        return new Chatter(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)), cursor.getInt(i + 1), cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2)), cursor.getShort(i + 3) != 0, cursor.getShort(i + 4) != 0, cursor.getInt(i + 5), cursor.isNull(i + 6) ? null : Long.valueOf(cursor.getLong(i + 6)), cursor.isNull(i + 7) ? null : UUID.fromString(cursor.getString(i + 7)));
    }

    @Override // de.greenrobot.dao.AbstractDao
    public void readEntity(Cursor cursor, Chatter chatter, int i) {
        chatter.setId(cursor.isNull(i + 0) ? null : Long.valueOf(cursor.getLong(i + 0)));
        chatter.setType(cursor.getInt(i + 1));
        chatter.setUuid(cursor.isNull(i + 2) ? null : UUID.fromString(cursor.getString(i + 2)));
        chatter.setActive(cursor.getShort(i + 3) != 0);
        chatter.setMuted(cursor.getShort(i + 4) != 0);
        chatter.setUnreadCount(cursor.getInt(i + 5));
        chatter.setLastMessageID(cursor.isNull(i + 6) ? null : Long.valueOf(cursor.getLong(i + 6)));
        chatter.setLastSessionID(cursor.isNull(i + 7) ? null : UUID.fromString(cursor.getString(i + 7)));
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
    public Long updateKeyAfterInsert(Chatter chatter, long j) {
        chatter.setId(Long.valueOf(j));
        return Long.valueOf(j);
    }
}
