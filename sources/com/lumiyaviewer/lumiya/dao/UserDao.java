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
//            User, DaoSession

public class UserDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property BadUUID;
        public static final Property DisplayName = new Property(3, java/lang/String, "displayName", false, "DISPLAY_NAME");
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property IsFriend;
        public static final Property RightsGiven;
        public static final Property RightsHas;
        public static final Property UserName = new Property(2, java/lang/String, "userName", false, "USER_NAME");
        public static final Property Uuid = new Property(1, java/util/UUID, "uuid", false, "UUID");

        static 
        {
            BadUUID = new Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID");
            IsFriend = new Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND");
            RightsGiven = new Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
            RightsHas = new Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "Users";

    public UserDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public UserDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'Users' (").append("'_id' INTEGER PRIMARY KEY ,").append("'UUID' TEXT,").append("'USER_NAME' TEXT,").append("'DISPLAY_NAME' TEXT,").append("'BAD_UUID' INTEGER NOT NULL ,").append("'IS_FRIEND' INTEGER NOT NULL ,").append("'RIGHTS_GIVEN' INTEGER NOT NULL ,").append("'RIGHTS_HAS' INTEGER NOT NULL );").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_Users_UUID ON Users").append(" (UUID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'Users'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, User user)
    {
        long l1 = 1L;
        sqlitestatement.clearBindings();
        Object obj = user.getId();
        if (obj != null)
        {
            sqlitestatement.bindLong(1, ((Long) (obj)).longValue());
        }
        obj = user.getUuid();
        if (obj != null)
        {
            sqlitestatement.bindString(2, ((UUID) (obj)).toString());
        }
        obj = user.getUserName();
        if (obj != null)
        {
            sqlitestatement.bindString(3, ((String) (obj)));
        }
        obj = user.getDisplayName();
        if (obj != null)
        {
            sqlitestatement.bindString(4, ((String) (obj)));
        }
        long l;
        if (user.getBadUUID())
        {
            l = 1L;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(5, l);
        if (user.getIsFriend())
        {
            l = l1;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(6, l);
        sqlitestatement.bindLong(7, user.getRightsGiven());
        sqlitestatement.bindLong(8, user.getRightsHas());
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (User)obj);
    }

    public Long getKey(User user)
    {
        if (user != null)
        {
            return user.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((User)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public User readEntity(Cursor cursor, int i)
    {
        boolean flag1 = true;
        String s1 = null;
        Long long1;
        UUID uuid;
        String s;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if (cursor.isNull(i + 1))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 1));
        }
        if (cursor.isNull(i + 2))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 2);
        }
        if (!cursor.isNull(i + 3))
        {
            s1 = cursor.getString(i + 3);
        }
        if (cursor.getShort(i + 4) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (cursor.getShort(i + 5) == 0)
        {
            flag1 = false;
        }
        return new User(long1, uuid, s, s1, flag, flag1, cursor.getInt(i + 6), cursor.getInt(i + 7));
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, User user, int i)
    {
        boolean flag1 = true;
        Object obj1 = null;
        Object obj;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            obj = null;
        } else
        {
            obj = Long.valueOf(cursor.getLong(i + 0));
        }
        user.setId(((Long) (obj)));
        if (cursor.isNull(i + 1))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 1));
        }
        user.setUuid(((UUID) (obj)));
        if (cursor.isNull(i + 2))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 2);
        }
        user.setUserName(((String) (obj)));
        if (cursor.isNull(i + 3))
        {
            obj = obj1;
        } else
        {
            obj = cursor.getString(i + 3);
        }
        user.setDisplayName(((String) (obj)));
        if (cursor.getShort(i + 4) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        user.setBadUUID(flag);
        if (cursor.getShort(i + 5) != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        user.setIsFriend(flag);
        user.setRightsGiven(cursor.getInt(i + 6));
        user.setRightsHas(cursor.getInt(i + 7));
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (User)obj, i);
    }

    public Long readKey(Cursor cursor, int i)
    {
        if (cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return Long.valueOf(cursor.getLong(i + 0));
        }
    }

    public volatile Object readKey(Cursor cursor, int i)
    {
        return readKey(cursor, i);
    }

    protected Long updateKeyAfterInsert(User user, long l)
    {
        user.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((User)obj, l);
    }
}
