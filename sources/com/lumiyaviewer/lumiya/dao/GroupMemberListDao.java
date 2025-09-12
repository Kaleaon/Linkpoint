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
//            GroupMemberList, DaoSession

public class GroupMemberListDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property GroupID = new Property(0, java/util/UUID, "groupID", true, "GROUP_ID");
        public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");


        public Properties()
        {
        }
    }


    public static final String TABLENAME = "GroupMemberLists";

    public GroupMemberListDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public GroupMemberListDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'GroupMemberLists' (").append("'GROUP_ID' TEXT PRIMARY KEY ,").append("'REQUEST_ID' TEXT NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'GroupMemberLists'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, GroupMemberList groupmemberlist)
    {
        sqlitestatement.clearBindings();
        UUID uuid = groupmemberlist.getGroupID();
        if (uuid != null)
        {
            sqlitestatement.bindString(1, uuid.toString());
        }
        sqlitestatement.bindString(2, groupmemberlist.getRequestID().toString());
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (GroupMemberList)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((GroupMemberList)obj);
    }

    public UUID getKey(GroupMemberList groupmemberlist)
    {
        if (groupmemberlist != null)
        {
            return groupmemberlist.getGroupID();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public GroupMemberList readEntity(Cursor cursor, int i)
    {
        UUID uuid;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        return new GroupMemberList(uuid, UUID.fromString(cursor.getString(i + 1)));
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, GroupMemberList groupmemberlist, int i)
    {
        UUID uuid;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        groupmemberlist.setGroupID(uuid);
        groupmemberlist.setRequestID(UUID.fromString(cursor.getString(i + 1)));
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (GroupMemberList)obj, i);
    }

    public volatile Object readKey(Cursor cursor, int i)
    {
        return readKey(cursor, i);
    }

    public UUID readKey(Cursor cursor, int i)
    {
        if (cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return UUID.fromString(cursor.getString(i + 0));
        }
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((GroupMemberList)obj, l);
    }

    protected UUID updateKeyAfterInsert(GroupMemberList groupmemberlist, long l)
    {
        return groupmemberlist.getGroupID();
    }
}
