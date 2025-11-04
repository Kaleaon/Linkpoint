// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import android.database.Cursor;
import java.util.UUID;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class ChatterDao extends AbstractDao<Chatter, Long>
{
    public static final String TABLENAME = "CHATTER";
    
    public ChatterDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public ChatterDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String s;
        if (b) {
            s = "IF NOT EXISTS ";
        }
        else {
            s = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'CHATTER' (" + "'_id' INTEGER PRIMARY KEY ," + "'TYPE' INTEGER NOT NULL ," + "'UUID' TEXT," + "'ACTIVE' INTEGER NOT NULL ," + "'MUTED' INTEGER NOT NULL ," + "'UNREAD_COUNT' INTEGER NOT NULL ," + "'LAST_MESSAGE_ID' INTEGER," + "'LAST_SESSION_ID' TEXT);");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_CHATTER_TYPE_UUID ON CHATTER" + " (TYPE,UUID);");
    }
    
    public static void dropTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        final StringBuilder append = new StringBuilder().append("DROP TABLE ");
        String str;
        if (b) {
            str = "IF EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL(append.append(str).append("'CHATTER'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final Chatter chatter) {
        final long n = 1L;
        sqLiteStatement.clearBindings();
        final Long id = chatter.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        sqLiteStatement.bindLong(2, (long)chatter.getType());
        final UUID uuid = chatter.getUuid();
        if (uuid != null) {
            sqLiteStatement.bindString(3, uuid.toString());
        }
        long n2;
        if (chatter.getActive()) {
            n2 = 1L;
        }
        else {
            n2 = 0L;
        }
        sqLiteStatement.bindLong(4, n2);
        long n3;
        if (chatter.getMuted()) {
            n3 = n;
        }
        else {
            n3 = 0L;
        }
        sqLiteStatement.bindLong(5, n3);
        sqLiteStatement.bindLong(6, (long)chatter.getUnreadCount());
        final Long lastMessageID = chatter.getLastMessageID();
        if (lastMessageID != null) {
            sqLiteStatement.bindLong(7, (long)lastMessageID);
        }
        final UUID lastSessionID = chatter.getLastSessionID();
        if (lastSessionID != null) {
            sqLiteStatement.bindString(8, lastSessionID.toString());
        }
    }
    
    public Long getKey(final Chatter chatter) {
        if (chatter != null) {
            return chatter.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public Chatter readEntity(final Cursor cursor, final int n) {
        boolean b = true;
        final UUID uuid = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        final int int1 = cursor.getInt(n + 1);
        UUID fromString;
        if (cursor.isNull(n + 2)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 2));
        }
        final boolean b2 = cursor.getShort(n + 3) != 0;
        if (cursor.getShort(n + 4) == 0) {
            b = false;
        }
        final int int2 = cursor.getInt(n + 5);
        Long value2;
        if (cursor.isNull(n + 6)) {
            value2 = null;
        }
        else {
            value2 = cursor.getLong(n + 6);
        }
        UUID fromString2;
        if (cursor.isNull(n + 7)) {
            fromString2 = uuid;
        }
        else {
            fromString2 = UUID.fromString(cursor.getString(n + 7));
        }
        return new Chatter(value, int1, fromString, b2, b, int2, value2, fromString2);
    }
    
    public void readEntity(final Cursor cursor, final Chatter chatter, final int n) {
        final boolean b = true;
        final UUID uuid = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        chatter.setId(value);
        chatter.setType(cursor.getInt(n + 1));
        UUID fromString;
        if (cursor.isNull(n + 2)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 2));
        }
        chatter.setUuid(fromString);
        chatter.setActive(cursor.getShort(n + 3) != 0);
        chatter.setMuted(cursor.getShort(n + 4) != 0 && b);
        chatter.setUnreadCount(cursor.getInt(n + 5));
        Long value2;
        if (cursor.isNull(n + 6)) {
            value2 = null;
        }
        else {
            value2 = cursor.getLong(n + 6);
        }
        chatter.setLastMessageID(value2);
        UUID fromString2;
        if (cursor.isNull(n + 7)) {
            fromString2 = uuid;
        }
        else {
            fromString2 = UUID.fromString(cursor.getString(n + 7));
        }
        chatter.setLastSessionID(fromString2);
    }
    
    public Long readKey(final Cursor cursor, final int n) {
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        return value;
    }
    
    @Override
    protected Long updateKeyAfterInsert(final Chatter chatter, final long n) {
        chatter.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property Active;
        public static final Property Id;
        public static final Property LastMessageID;
        public static final Property LastSessionID;
        public static final Property Muted;
        public static final Property Type;
        public static final Property UnreadCount;
        public static final Property Uuid;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            Type = new Property(1, Integer.TYPE, "type", false, "TYPE");
            Uuid = new Property(2, UUID.class, "uuid", false, "UUID");
            Active = new Property(3, Boolean.TYPE, "active", false, "ACTIVE");
            Muted = new Property(4, Boolean.TYPE, "muted", false, "MUTED");
            UnreadCount = new Property(5, Integer.TYPE, "unreadCount", false, "UNREAD_COUNT");
            LastMessageID = new Property(6, Long.class, "lastMessageID", false, "LAST_MESSAGE_ID");
            LastSessionID = new Property(7, UUID.class, "lastSessionID", false, "LAST_SESSION_ID");
        }
    }
}
