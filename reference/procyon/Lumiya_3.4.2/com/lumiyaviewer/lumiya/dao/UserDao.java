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

public class UserDao extends AbstractDao<User, Long>
{
    public static final String TABLENAME = "Users";
    
    public UserDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public UserDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'Users' (" + "'_id' INTEGER PRIMARY KEY ," + "'UUID' TEXT," + "'USER_NAME' TEXT," + "'DISPLAY_NAME' TEXT," + "'BAD_UUID' INTEGER NOT NULL ," + "'IS_FRIEND' INTEGER NOT NULL ," + "'RIGHTS_GIVEN' INTEGER NOT NULL ," + "'RIGHTS_HAS' INTEGER NOT NULL );");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_Users_UUID ON Users" + " (UUID);");
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
        sqLiteDatabase.execSQL(append.append(str).append("'Users'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final User user) {
        final long n = 1L;
        sqLiteStatement.clearBindings();
        final Long id = user.getId();
        if (id != null) {
            sqLiteStatement.bindLong(1, (long)id);
        }
        final UUID uuid = user.getUuid();
        if (uuid != null) {
            sqLiteStatement.bindString(2, uuid.toString());
        }
        final String userName = user.getUserName();
        if (userName != null) {
            sqLiteStatement.bindString(3, userName);
        }
        final String displayName = user.getDisplayName();
        if (displayName != null) {
            sqLiteStatement.bindString(4, displayName);
        }
        long n2;
        if (user.getBadUUID()) {
            n2 = 1L;
        }
        else {
            n2 = 0L;
        }
        sqLiteStatement.bindLong(5, n2);
        long n3;
        if (user.getIsFriend()) {
            n3 = n;
        }
        else {
            n3 = 0L;
        }
        sqLiteStatement.bindLong(6, n3);
        sqLiteStatement.bindLong(7, (long)user.getRightsGiven());
        sqLiteStatement.bindLong(8, (long)user.getRightsHas());
    }
    
    public Long getKey(final User user) {
        if (user != null) {
            return user.getId();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public User readEntity(final Cursor cursor, final int n) {
        boolean b = true;
        String string = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        UUID fromString;
        if (cursor.isNull(n + 1)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 1));
        }
        String string2;
        if (cursor.isNull(n + 2)) {
            string2 = null;
        }
        else {
            string2 = cursor.getString(n + 2);
        }
        if (!cursor.isNull(n + 3)) {
            string = cursor.getString(n + 3);
        }
        final boolean b2 = cursor.getShort(n + 4) != 0;
        if (cursor.getShort(n + 5) == 0) {
            b = false;
        }
        return new User(value, fromString, string2, string, b2, b, cursor.getInt(n + 6), cursor.getInt(n + 7));
    }
    
    public void readEntity(final Cursor cursor, final User user, final int n) {
        final boolean b = true;
        final String s = null;
        Long value;
        if (cursor.isNull(n + 0)) {
            value = null;
        }
        else {
            value = cursor.getLong(n + 0);
        }
        user.setId(value);
        UUID fromString;
        if (cursor.isNull(n + 1)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 1));
        }
        user.setUuid(fromString);
        String string;
        if (cursor.isNull(n + 2)) {
            string = null;
        }
        else {
            string = cursor.getString(n + 2);
        }
        user.setUserName(string);
        String string2;
        if (cursor.isNull(n + 3)) {
            string2 = s;
        }
        else {
            string2 = cursor.getString(n + 3);
        }
        user.setDisplayName(string2);
        user.setBadUUID(cursor.getShort(n + 4) != 0);
        user.setIsFriend(cursor.getShort(n + 5) != 0 && b);
        user.setRightsGiven(cursor.getInt(n + 6));
        user.setRightsHas(cursor.getInt(n + 7));
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
    protected Long updateKeyAfterInsert(final User user, final long n) {
        user.setId(n);
        return n;
    }
    
    public static class Properties
    {
        public static final Property BadUUID;
        public static final Property DisplayName;
        public static final Property Id;
        public static final Property IsFriend;
        public static final Property RightsGiven;
        public static final Property RightsHas;
        public static final Property UserName;
        public static final Property Uuid;
        
        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            Uuid = new Property(1, UUID.class, "uuid", false, "UUID");
            UserName = new Property(2, String.class, "userName", false, "USER_NAME");
            DisplayName = new Property(3, String.class, "displayName", false, "DISPLAY_NAME");
            BadUUID = new Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID");
            IsFriend = new Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND");
            RightsGiven = new Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
            RightsHas = new Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        }
    }
}
