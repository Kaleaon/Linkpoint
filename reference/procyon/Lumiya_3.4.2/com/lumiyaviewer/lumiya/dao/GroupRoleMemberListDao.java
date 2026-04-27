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

public class GroupRoleMemberListDao extends AbstractDao<GroupRoleMemberList, UUID>
{
    public static final String TABLENAME = "GroupRoleMemberLists";
    
    public GroupRoleMemberListDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public GroupRoleMemberListDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupRoleMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL ," + "'MUST_REVALIDATE' INTEGER NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'GroupRoleMemberLists'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final GroupRoleMemberList list) {
        sqLiteStatement.clearBindings();
        final UUID groupID = list.getGroupID();
        if (groupID != null) {
            sqLiteStatement.bindString(1, groupID.toString());
        }
        sqLiteStatement.bindString(2, list.getRequestID().toString());
        long n;
        if (list.getMustRevalidate()) {
            n = 1L;
        }
        else {
            n = 0L;
        }
        sqLiteStatement.bindLong(3, n);
    }
    
    public UUID getKey(final GroupRoleMemberList list) {
        if (list != null) {
            return list.getGroupID();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public GroupRoleMemberList readEntity(final Cursor cursor, final int n) {
        boolean b = false;
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        final UUID fromString2 = UUID.fromString(cursor.getString(n + 1));
        if (cursor.getShort(n + 2) != 0) {
            b = true;
        }
        return new GroupRoleMemberList(fromString, fromString2, b);
    }
    
    public void readEntity(final Cursor cursor, final GroupRoleMemberList list, final int n) {
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        list.setGroupID(fromString);
        list.setRequestID(UUID.fromString(cursor.getString(n + 1)));
        list.setMustRevalidate(cursor.getShort(n + 2) != 0);
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
    protected UUID updateKeyAfterInsert(final GroupRoleMemberList list, final long n) {
        return list.getGroupID();
    }
    
    public static class Properties
    {
        public static final Property GroupID;
        public static final Property MustRevalidate;
        public static final Property RequestID;
        
        static {
            GroupID = new Property(0, UUID.class, "groupID", true, "GROUP_ID");
            RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
            MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }
    }
}
