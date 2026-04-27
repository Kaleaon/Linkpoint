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

public class GroupMemberListDao extends AbstractDao<GroupMemberList, UUID>
{
    public static final String TABLENAME = "GroupMemberLists";
    
    public GroupMemberListDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public GroupMemberListDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + str + "'GroupMemberLists' (" + "'GROUP_ID' TEXT PRIMARY KEY ," + "'REQUEST_ID' TEXT NOT NULL );");
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
        sqLiteDatabase.execSQL(append.append(str).append("'GroupMemberLists'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final GroupMemberList list) {
        sqLiteStatement.clearBindings();
        final UUID groupID = list.getGroupID();
        if (groupID != null) {
            sqLiteStatement.bindString(1, groupID.toString());
        }
        sqLiteStatement.bindString(2, list.getRequestID().toString());
    }
    
    public UUID getKey(final GroupMemberList list) {
        if (list != null) {
            return list.getGroupID();
        }
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public GroupMemberList readEntity(final Cursor cursor, final int n) {
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        return new GroupMemberList(fromString, UUID.fromString(cursor.getString(n + 1)));
    }
    
    public void readEntity(final Cursor cursor, final GroupMemberList list, final int n) {
        UUID fromString;
        if (cursor.isNull(n + 0)) {
            fromString = null;
        }
        else {
            fromString = UUID.fromString(cursor.getString(n + 0));
        }
        list.setGroupID(fromString);
        list.setRequestID(UUID.fromString(cursor.getString(n + 1)));
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
    protected UUID updateKeyAfterInsert(final GroupMemberList list, final long n) {
        return list.getGroupID();
    }
    
    public static class Properties
    {
        public static final Property GroupID;
        public static final Property RequestID;
        
        static {
            GroupID = new Property(0, UUID.class, "groupID", true, "GROUP_ID");
            RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
        }
    }
}
