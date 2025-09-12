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
//            GroupRoleMemberList, DaoSession

public class GroupRoleMemberListDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property GroupID = new Property(0, java/util/UUID, "groupID", true, "GROUP_ID");
        public static final Property MustRevalidate;
        public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");

        static 
        {
            MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "GroupRoleMemberLists";

    public GroupRoleMemberListDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public GroupRoleMemberListDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'GroupRoleMemberLists' (").append("'GROUP_ID' TEXT PRIMARY KEY ,").append("'REQUEST_ID' TEXT NOT NULL ,").append("'MUST_REVALIDATE' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'GroupRoleMemberLists'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, GroupRoleMemberList grouprolememberlist)
    {
        sqlitestatement.clearBindings();
        UUID uuid = grouprolememberlist.getGroupID();
        if (uuid != null)
        {
            sqlitestatement.bindString(1, uuid.toString());
        }
        sqlitestatement.bindString(2, grouprolememberlist.getRequestID().toString());
        long l;
        if (grouprolememberlist.getMustRevalidate())
        {
            l = 1L;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(3, l);
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (GroupRoleMemberList)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((GroupRoleMemberList)obj);
    }

    public UUID getKey(GroupRoleMemberList grouprolememberlist)
    {
        if (grouprolememberlist != null)
        {
            return grouprolememberlist.getGroupID();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public GroupRoleMemberList readEntity(Cursor cursor, int i)
    {
        boolean flag = false;
        UUID uuid;
        UUID uuid1;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        uuid1 = UUID.fromString(cursor.getString(i + 1));
        if (cursor.getShort(i + 2) != 0)
        {
            flag = true;
        }
        return new GroupRoleMemberList(uuid, uuid1, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, GroupRoleMemberList grouprolememberlist, int i)
    {
        UUID uuid;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        grouprolememberlist.setGroupID(uuid);
        grouprolememberlist.setRequestID(UUID.fromString(cursor.getString(i + 1)));
        if (cursor.getShort(i + 2) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        grouprolememberlist.setMustRevalidate(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (GroupRoleMemberList)obj, i);
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
        return updateKeyAfterInsert((GroupRoleMemberList)obj, l);
    }

    protected UUID updateKeyAfterInsert(GroupRoleMemberList grouprolememberlist, long l)
    {
        return grouprolememberlist.getGroupID();
    }
}
