// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.AbstractDao;

public class GroupRoleMemberDao extends AbstractDao<GroupRoleMember, Void>
{
    public static final String TABLENAME = "GroupRoleMembers";
    
    public GroupRoleMemberDao(final DaoConfig daoConfig) {
        super(daoConfig);
    }
    
    public GroupRoleMemberDao(final DaoConfig daoConfig, final DaoSession daoSession) {
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
        sqLiteDatabase.execSQL("CREATE TABLE " + s + "'GroupRoleMembers' (" + "'GROUP_ID' TEXT NOT NULL ," + "'REQUEST_ID' TEXT NOT NULL ," + "'ROLE_ID' TEXT NOT NULL ," + "'USER_ID' TEXT NOT NULL );");
        sqLiteDatabase.execSQL("CREATE INDEX " + s + "IDX_GroupRoleMembers_GROUP_ID_ROLE_ID_REQUEST_ID ON GroupRoleMembers" + " (GROUP_ID,ROLE_ID,REQUEST_ID);");
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
        sqLiteDatabase.execSQL(append.append(str).append("'GroupRoleMembers'").toString());
    }
    
    @Override
    protected void bindValues(final SQLiteStatement sqLiteStatement, final GroupRoleMember groupRoleMember) {
        sqLiteStatement.clearBindings();
        sqLiteStatement.bindString(1, groupRoleMember.getGroupID().toString());
        sqLiteStatement.bindString(2, groupRoleMember.getRequestID().toString());
        sqLiteStatement.bindString(3, groupRoleMember.getRoleID().toString());
        sqLiteStatement.bindString(4, groupRoleMember.getUserID().toString());
    }
    
    public Void getKey(final GroupRoleMember groupRoleMember) {
        return null;
    }
    
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    public GroupRoleMember readEntity(final Cursor cursor, final int n) {
        return new GroupRoleMember(UUID.fromString(cursor.getString(n + 0)), UUID.fromString(cursor.getString(n + 1)), UUID.fromString(cursor.getString(n + 2)), UUID.fromString(cursor.getString(n + 3)));
    }
    
    public void readEntity(final Cursor cursor, final GroupRoleMember groupRoleMember, final int n) {
        groupRoleMember.setGroupID(UUID.fromString(cursor.getString(n + 0)));
        groupRoleMember.setRequestID(UUID.fromString(cursor.getString(n + 1)));
        groupRoleMember.setRoleID(UUID.fromString(cursor.getString(n + 2)));
        groupRoleMember.setUserID(UUID.fromString(cursor.getString(n + 3)));
    }
    
    public Void readKey(final Cursor cursor, final int n) {
        return null;
    }
    
    @Override
    protected Void updateKeyAfterInsert(final GroupRoleMember groupRoleMember, final long n) {
        return null;
    }
    
    public static class Properties
    {
        public static final Property GroupID;
        public static final Property RequestID;
        public static final Property RoleID;
        public static final Property UserID;
        
        static {
            GroupID = new Property(0, UUID.class, "groupID", false, "GROUP_ID");
            RequestID = new Property(1, UUID.class, "requestID", false, "REQUEST_ID");
            RoleID = new Property(2, UUID.class, "roleID", false, "ROLE_ID");
            UserID = new Property(3, UUID.class, "userID", false, "USER_ID");
        }
    }
}
