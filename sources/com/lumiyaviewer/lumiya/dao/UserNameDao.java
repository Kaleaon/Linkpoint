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
//            UserName, DaoSession

public class UserNameDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property DisplayName = new Property(2, java/lang/String, "displayName", false, "DISPLAY_NAME");
        public static final Property IsBadUUID;
        public static final Property UserName = new Property(1, java/lang/String, "userName", false, "USER_NAME");
        public static final Property Uuid = new Property(0, java/util/UUID, "uuid", true, "UUID");

        static 
        {
            IsBadUUID = new Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "UserNames";

    public UserNameDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public UserNameDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'UserNames' (").append("'UUID' TEXT PRIMARY KEY ,").append("'USER_NAME' TEXT,").append("'DISPLAY_NAME' TEXT,").append("'IS_BAD_UUID' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'UserNames'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, UserName username)
    {
        sqlitestatement.clearBindings();
        Object obj = username.getUuid();
        if (obj != null)
        {
            sqlitestatement.bindString(1, ((UUID) (obj)).toString());
        }
        obj = username.getUserName();
        if (obj != null)
        {
            sqlitestatement.bindString(2, ((String) (obj)));
        }
        obj = username.getDisplayName();
        if (obj != null)
        {
            sqlitestatement.bindString(3, ((String) (obj)));
        }
        long l;
        if (username.getIsBadUUID())
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
        bindValues(sqlitestatement, (UserName)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((UserName)obj);
    }

    public UUID getKey(UserName username)
    {
        if (username != null)
        {
            return username.getUuid();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public UserName readEntity(Cursor cursor, int i)
    {
        boolean flag = false;
        String s1 = null;
        UUID uuid;
        String s;
        if (cursor.isNull(i + 0))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 0));
        }
        if (cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        if (!cursor.isNull(i + 2))
        {
            s1 = cursor.getString(i + 2);
        }
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        }
        return new UserName(uuid, s, s1, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, UserName username, int i)
    {
        Object obj1 = null;
        Object obj;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 0));
        }
        username.setUuid(((UUID) (obj)));
        if (cursor.isNull(i + 1))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 1);
        }
        username.setUserName(((String) (obj)));
        if (cursor.isNull(i + 2))
        {
            obj = obj1;
        } else
        {
            obj = cursor.getString(i + 2);
        }
        username.setDisplayName(((String) (obj)));
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        username.setIsBadUUID(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (UserName)obj, i);
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
        return updateKeyAfterInsert((UserName)obj, l);
    }

    protected UUID updateKeyAfterInsert(UserName username, long l)
    {
        return username.getUuid();
    }
}
