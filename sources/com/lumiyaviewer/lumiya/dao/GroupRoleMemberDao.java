// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            GroupRoleMember, DaoSession

public class GroupRoleMemberDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property GroupID = new Property(0, java/util/UUID, "groupID", false, "GROUP_ID");
        public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");
        public static final Property RoleID = new Property(2, java/util/UUID, "roleID", false, "ROLE_ID");
        public static final Property UserID = new Property(3, java/util/UUID, "userID", false, "USER_ID");


        public Properties()
        {
        }
    }


    public static final String TABLENAME = "GroupRoleMembers";

    public GroupRoleMemberDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public GroupRoleMemberDao(DaoConfig daoconfig, DaoSession daosession)
    {
        super(daoconfig, daosession);
    }

    public static void createTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        String s;
        if (flag)
        {
            s = "IF NOT EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'GroupRoleMembers' (").append("'GROUP_ID' TEXT NOT NULL ,").append("'REQUEST_ID' TEXT NOT NULL ,").append("'ROLE_ID' TEXT NOT NULL ,").append("'USER_ID' TEXT NOT NULL );").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_GroupRoleMembers_GROUP_ID_ROLE_ID_REQUEST_ID ON GroupRoleMembers").append(" (GROUP_ID,ROLE_ID,REQUEST_ID);").toString());
    }

    public static void dropTable(SQLiteDatabase sqlitedatabase, boolean flag)
    {
        StringBuilder stringbuilder = (new StringBuilder()).append("DROP TABLE ");
        String s;
        if (flag)
        {
            s = "IF EXISTS ";
        } else
        {
            s = "";
        }
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'GroupRoleMembers'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, GroupRoleMember grouprolemember)
    {
        sqlitestatement.clearBindings();
        sqlitestatement.bindString(1, grouprolemember.getGroupID().toString());
        sqlitestatement.bindString(2, grouprolemember.getRequestID().toString());
        sqlitestatement.bindString(3, grouprolemember.getRoleID().toString());
        sqlitestatement.bindString(4, grouprolemember.getUserID().toString());
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (GroupRoleMember)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((GroupRoleMember)obj);
    }

    public Void getKey(GroupRoleMember grouprolemember)
    {
        return null;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public GroupRoleMember readEntity(Cursor cursor, int i)
    {
        return new GroupRoleMember(UUID.fromString(cursor.getString(i + 0)), UUID.fromString(cursor.getString(i + 1)), UUID.fromString(cursor.getString(i + 2)), UUID.fromString(cursor.getString(i + 3)));
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, GroupRoleMember grouprolemember, int i)
    {
        grouprolemember.setGroupID(UUID.fromString(cursor.getString(i + 0)));
        grouprolemember.setRequestID(UUID.fromString(cursor.getString(i + 1)));
        grouprolemember.setRoleID(UUID.fromString(cursor.getString(i + 2)));
        grouprolemember.setUserID(UUID.fromString(cursor.getString(i + 3)));
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (GroupRoleMember)obj, i);
    }

    public volatile Object readKey(Cursor cursor, int i)
    {
        return readKey(cursor, i);
    }

    public Void readKey(Cursor cursor, int i)
    {
        return null;
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((GroupRoleMember)obj, l);
    }

    protected Void updateKeyAfterInsert(GroupRoleMember grouprolemember, long l)
    {
        return null;
    }
}
