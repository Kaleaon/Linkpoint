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

public class UserNameDao extends AbstractDao<UserName, UUID>
{
    public static final String TABLENAME = "UserNames";
    
    public UserNameDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public UserNameDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'UserNames' (" + "'UUID' TEXT PRIMARY KEY ," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'IS_BAD_UUID' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'UserNames'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final UserName userName) {
        sqLiteStatement.clearBindings();
        final UUID uuid = userName.getUuid();
        if (uuid != null) {
            sqLiteStatement.bindString(1, uuid.toString());
        }
        final String userName2 = userName.getUserName();
        if (userName2 != null) {
            sqLiteStatement.bindString(2, userName2);
        }
        final String displayName = userName.getDisplayName();
        if (displayName != null) {
            sqLiteStatement.bindString(3, displayName);
        }
        long n;
        if (userName.getIsBadUUID()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(4, n);
    }
    
    public UUID getKey(final UserName userName) {
        if (userName != null) {
            return userName.getUuid();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public UserName readEntity(final Cursor cursor, final int n) {
        boolean b = false;
        String string = null;
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        String string2;
        if (cursor.isNull(n + 1)) {
            string2 = null;
        }
        else {
            string2 = cursor.getString(n + 1);
        }
        if (!cursor.isNull(n + 2)) {
            string = cursor.getString(n + 2);
        }
        if (cursor.getShort(n + 3) != 0) {
            b = true;
        }
        return new UserName(fromString, string2, string, b);
    }
    
    public void readEntity(final Cursor cursor, final UserName userName, final int n) {
        final String s = null;
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        userName.setUuid(fromString);
        String string;
        if (cursor.isNull(n + 1)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 1);
        }
        userName.setUserName(string);
        String string2;
        if (cursor.isNull(n + 2)) {
            string2 = s;
        }
        else {
            string2 = cursor.getString(n + 2);
        }
        userName.setDisplayName(string2);
        userName.setIsBadUUID(cursor.getShort(n + 3) != 0);
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
    protected UUID updateKeyAfterInsert(final UserName userName, final long n) {
        return userName.getUuid();
    }
    
    public static class Properties
    {
        public static final Property DisplayName;
        public static final Property IsBadUUID;
        public static final Property UserName;
        public static final Property Uuid;
        
        static {
            Uuid = new Property(0, UUID.class, "uuid", true, "UUID");
            UserName = new Property(1, String.class, "userName", false, "USER_NAME");
            DisplayName = new Property(2, String.class, "displayName", false, "DISPLAY_NAME");
            IsBadUUID = new Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID");
        }
    }
}
