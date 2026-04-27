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
//            GroupMember, DaoSession

public class GroupMemberDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property AgentPowers;
        public static final Property Contribution;
        public static final Property GroupID = new Property(0, java/util/UUID, "groupID", false, "GROUP_ID");
        public static final Property IsOwner;
        public static final Property OnlineStatus = new Property(4, java/lang/String, "onlineStatus", false, "ONLINE_STATUS");
        public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");
        public static final Property Title = new Property(6, java/lang/String, "title", false, "TITLE");
        public static final Property UserID = new Property(2, java/util/UUID, "userID", false, "USER_ID");

        static 
        {
            Contribution = new Property(3, Integer.TYPE, "contribution", false, "CONTRIBUTION");
            AgentPowers = new Property(5, Long.TYPE, "agentPowers", false, "AGENT_POWERS");
            IsOwner = new Property(7, Boolean.TYPE, "isOwner", false, "IS_OWNER");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "GroupMembers";

    public GroupMemberDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public GroupMemberDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'GroupMembers' (").append("'GROUP_ID' TEXT NOT NULL ,").append("'REQUEST_ID' TEXT NOT NULL ,").append("'USER_ID' TEXT NOT NULL ,").append("'CONTRIBUTION' INTEGER NOT NULL ,").append("'ONLINE_STATUS' TEXT NOT NULL ,").append("'AGENT_POWERS' INTEGER NOT NULL ,").append("'TITLE' TEXT NOT NULL ,").append("'IS_OWNER' INTEGER NOT NULL );").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_GroupMembers_GROUP_ID_REQUEST_ID ON GroupMembers").append(" (GROUP_ID,REQUEST_ID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'GroupMembers'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, GroupMember groupmember)
    {
        sqlitestatement.clearBindings();
        sqlitestatement.bindString(1, groupmember.getGroupID().toString());
        sqlitestatement.bindString(2, groupmember.getRequestID().toString());
        sqlitestatement.bindString(3, groupmember.getUserID().toString());
        sqlitestatement.bindLong(4, groupmember.getContribution());
        sqlitestatement.bindString(5, groupmember.getOnlineStatus());
        sqlitestatement.bindLong(6, groupmember.getAgentPowers());
        sqlitestatement.bindString(7, groupmember.getTitle());
        long l;
        if (groupmember.getIsOwner())
        {
            l = 1L;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(8, l);
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (GroupMember)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((GroupMember)obj);
    }

    public Void getKey(GroupMember groupmember)
    {
        return null;
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public GroupMember readEntity(Cursor cursor, int i)
    {
        boolean flag = false;
        UUID uuid = UUID.fromString(cursor.getString(i + 0));
        UUID uuid1 = UUID.fromString(cursor.getString(i + 1));
        UUID uuid2 = UUID.fromString(cursor.getString(i + 2));
        int j = cursor.getInt(i + 3);
        String s = cursor.getString(i + 4);
        long l = cursor.getLong(i + 5);
        String s1 = cursor.getString(i + 6);
        if (cursor.getShort(i + 7) != 0)
        {
            flag = true;
        }
        return new GroupMember(uuid, uuid1, uuid2, j, s, l, s1, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, GroupMember groupmember, int i)
    {
        boolean flag = false;
        groupmember.setGroupID(UUID.fromString(cursor.getString(i + 0)));
        groupmember.setRequestID(UUID.fromString(cursor.getString(i + 1)));
        groupmember.setUserID(UUID.fromString(cursor.getString(i + 2)));
        groupmember.setContribution(cursor.getInt(i + 3));
        groupmember.setOnlineStatus(cursor.getString(i + 4));
        groupmember.setAgentPowers(cursor.getLong(i + 5));
        groupmember.setTitle(cursor.getString(i + 6));
        if (cursor.getShort(i + 7) != 0)
        {
            flag = true;
        }
        groupmember.setIsOwner(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (GroupMember)obj, i);
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
        return updateKeyAfterInsert((GroupMember)obj, l);
    }

    protected Void updateKeyAfterInsert(GroupMember groupmember, long l)
    {
        return null;
    }
}
