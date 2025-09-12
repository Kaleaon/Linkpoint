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
//            Chatter, DaoSession

public class ChatterDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Active;
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property LastMessageID = new Property(6, java/lang/Long, "lastMessageID", false, "LAST_MESSAGE_ID");
        public static final Property LastSessionID = new Property(7, java/util/UUID, "lastSessionID", false, "LAST_SESSION_ID");
        public static final Property Muted;
        public static final Property Type;
        public static final Property UnreadCount;
        public static final Property Uuid = new Property(2, java/util/UUID, "uuid", false, "UUID");

        static 
        {
            Type = new Property(1, Integer.TYPE, "type", false, "TYPE");
            Active = new Property(3, Boolean.TYPE, "active", false, "ACTIVE");
            Muted = new Property(4, Boolean.TYPE, "muted", false, "MUTED");
            UnreadCount = new Property(5, Integer.TYPE, "unreadCount", false, "UNREAD_COUNT");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "CHATTER";

    public ChatterDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public ChatterDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CHATTER' (").append("'_id' INTEGER PRIMARY KEY ,").append("'TYPE' INTEGER NOT NULL ,").append("'UUID' TEXT,").append("'ACTIVE' INTEGER NOT NULL ,").append("'MUTED' INTEGER NOT NULL ,").append("'UNREAD_COUNT' INTEGER NOT NULL ,").append("'LAST_MESSAGE_ID' INTEGER,").append("'LAST_SESSION_ID' TEXT);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_CHATTER_TYPE_UUID ON CHATTER").append(" (TYPE,UUID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CHATTER'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, Chatter chatter)
    {
        long l1 = 1L;
        sqlitestatement.clearBindings();
        Object obj = chatter.getId();
        if (obj != null)
        {
            sqlitestatement.bindLong(1, ((Long) (obj)).longValue());
        }
        sqlitestatement.bindLong(2, chatter.getType());
        obj = chatter.getUuid();
        if (obj != null)
        {
            sqlitestatement.bindString(3, ((UUID) (obj)).toString());
        }
        long l;
        if (chatter.getActive())
        {
            l = 1L;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(4, l);
        if (chatter.getMuted())
        {
            l = l1;
        } else
        {
            l = 0L;
        }
        sqlitestatement.bindLong(5, l);
        sqlitestatement.bindLong(6, chatter.getUnreadCount());
        obj = chatter.getLastMessageID();
        if (obj != null)
        {
            sqlitestatement.bindLong(7, ((Long) (obj)).longValue());
        }
        chatter = chatter.getLastSessionID();
        if (chatter != null)
        {
            sqlitestatement.bindString(8, chatter.toString());
        }
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (Chatter)obj);
    }

    public Long getKey(Chatter chatter)
    {
        if (chatter != null)
        {
            return chatter.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((Chatter)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public Chatter readEntity(Cursor cursor, int i)
    {
        boolean flag1 = true;
        Object obj = null;
        Long long1;
        UUID uuid;
        Long long2;
        int j;
        int k;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        j = cursor.getInt(i + 1);
        if (cursor.isNull(i + 2))
        {
            uuid = null;
        } else
        {
            uuid = UUID.fromString(cursor.getString(i + 2));
        }
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (cursor.getShort(i + 4) == 0)
        {
            flag1 = false;
        }
        k = cursor.getInt(i + 5);
        if (cursor.isNull(i + 6))
        {
            long2 = null;
        } else
        {
            long2 = Long.valueOf(cursor.getLong(i + 6));
        }
        if (cursor.isNull(i + 7))
        {
            cursor = obj;
        } else
        {
            cursor = UUID.fromString(cursor.getString(i + 7));
        }
        return new Chatter(long1, j, uuid, flag, flag1, k, long2, cursor);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, Chatter chatter, int i)
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
        chatter.setId(((Long) (obj)));
        chatter.setType(cursor.getInt(i + 1));
        if (cursor.isNull(i + 2))
        {
            obj = null;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 2));
        }
        chatter.setUuid(((UUID) (obj)));
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        chatter.setActive(flag);
        if (cursor.getShort(i + 4) != 0)
        {
            flag = flag1;
        } else
        {
            flag = false;
        }
        chatter.setMuted(flag);
        chatter.setUnreadCount(cursor.getInt(i + 5));
        if (cursor.isNull(i + 6))
        {
            obj = null;
        } else
        {
            obj = Long.valueOf(cursor.getLong(i + 6));
        }
        chatter.setLastMessageID(((Long) (obj)));
        if (cursor.isNull(i + 7))
        {
            cursor = obj1;
        } else
        {
            cursor = UUID.fromString(cursor.getString(i + 7));
        }
        chatter.setLastSessionID(cursor);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (Chatter)obj, i);
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

    protected Long updateKeyAfterInsert(Chatter chatter, long l)
    {
        chatter.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((Chatter)obj, l);
    }
}
