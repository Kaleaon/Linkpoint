// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;
import de.greenrobot.dao.AbstractDao;

public class FriendDao extends AbstractDao<Friend, UUID>
{
    public static final String TABLENAME = "Friends";
    
    public FriendDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public FriendDao(final DaoConfig daoConfig, final DaoSession daoSession) {
        super(daoConfig, daoSession);
    }
    
    public static void createTable(final SQLiteDatabase sqLiteDatabase, final boolean b) {
        String str;
        if (b) {
            str = "IF NOT EXISTS ";
        }
        else {
            str = "";
        }
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'Friends' (" + "'UUID' TEXT PRIMARY KEY ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL ," + "'IS_ONLINE' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'Friends'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final Friend friend) {
        sqLiteStatement.clearBindings();
        final UUID uuid = friend.getUuid();
        if (uuid != null) {
            sqLiteStatement.bindString(1, uuid.toString());
        }
        sqLiteStatement.bindLong(2, (long)friend.getRightsGiven());
        sqLiteStatement.bindLong(3, (long)friend.getRightsHas());
        long n;
        if (friend.getIsOnline()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(4, n);
    }
    
    public UUID getKey(final Friend friend) {
        if (friend != null) {
            return friend.getUuid();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public Friend readEntity(final Cursor cursor, final int n) {
        boolean b = false;
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        final int int1 = cursor.getInt(n + 1);
        final int int2 = cursor.getInt(n + 2);
        if (cursor.getShort(n + 3) != 0) {
            b = true;
        }
        return new Friend(fromString, int1, int2, b);
    }
    
    public void readEntity(final Cursor cursor, final Friend friend, final int n) {
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        friend.setUuid(fromString);
        friend.setRightsGiven(cursor.getInt(n + 1));
        friend.setRightsHas(cursor.getInt(n + 2));
        friend.setIsOnline(cursor.getShort(n + 3) != 0);
    }
    
    public UUID readKey(final Cursor cursor, final int n) {
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        return fromString;
    }
    
    @Override
    protected UUID updateKeyAfterInsert(final Friend friend, final long n) {
        return friend.getUuid();
    }
    
    public static class Properties
    {
        public static final Property IsOnline;
        public static final Property RightsGiven;
        public static final Property RightsHas;
        public static final Property Uuid;
        
        static {
            Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
            RightsGiven = new Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
            RightsHas = new Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
            IsOnline = new Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE");
        }
    }
}
