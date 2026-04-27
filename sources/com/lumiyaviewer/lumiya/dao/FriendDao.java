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
//            Friend, DaoSession

public class FriendDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property IsOnline;
        public static final Property RightsGiven;
        public static final Property RightsHas;
        public static final Property Uuid = new Property(0, java/util/UUID, "uuid", true, "UUID");

        static 
        {
            RightsGiven = new Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
            RightsHas = new Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
            IsOnline = new Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "Friends";

    public FriendDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public FriendDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'Friends' (").append("'UUID' TEXT PRIMARY KEY ,").append("'RIGHTS_GIVEN' INTEGER NOT NULL ,").append("'RIGHTS_HAS' INTEGER NOT NULL ,").append("'IS_ONLINE' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'Friends'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, Friend friend)
    {
        sqlitestatement.clearBindings();
        UUID uuid = friend.getUuid();
        if (uuid != null)
        {
            sqlitestatement.bindString(1, uuid.toString());
        }
        sqlitestatement.bindLong(2, friend.getRightsGiven());
        sqlitestatement.bindLong(3, friend.getRightsHas());
        long l;
        if (friend.getIsOnline())
        {
            l = 1L;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(4, l);
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (Friend)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((Friend)obj);
    }

    public UUID getKey(Friend friend)
    {
        if (friend != null)
        {
            return friend.getUuid();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public Friend readEntity(Cursor cursor, int i)
    {
        boolean flag = false;
        UUID uuid;
        int j;
        int k;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        j = cursor.getInt(i + 1);
        k = cursor.getInt(i + 2);
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        }
        return new Friend(uuid, j, k, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, Friend friend, int i)
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
        friend.setUuid(uuid);
        friend.setRightsGiven(cursor.getInt(i + 1));
        friend.setRightsHas(cursor.getInt(i + 2));
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        friend.setIsOnline(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (Friend)obj, i);
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
        return updateKeyAfterInsert((Friend)obj, l);
    }

    protected UUID updateKeyAfterInsert(Friend friend, long l)
    {
        return friend.getUuid();
    }
}
