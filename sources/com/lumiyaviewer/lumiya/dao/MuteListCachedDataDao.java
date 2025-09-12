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

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            MuteListCachedData, DaoSession

public class MuteListCachedDataDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property CRC;
        public static final Property Data = new Property(2, [B, "data", false, "DATA");
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");

        static 
        {
            CRC = new Property(1, Integer.TYPE, "CRC", false, "CRC");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "MUTE_LIST_CACHED_DATA";

    public MuteListCachedDataDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public MuteListCachedDataDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'MUTE_LIST_CACHED_DATA' (").append("'_id' INTEGER PRIMARY KEY ,").append("'CRC' INTEGER NOT NULL ,").append("'DATA' BLOB NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'MUTE_LIST_CACHED_DATA'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, MuteListCachedData mutelistcacheddata)
    {
        sqlitestatement.clearBindings();
        Long long1 = mutelistcacheddata.getId();
        if (long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        sqlitestatement.bindLong(2, mutelistcacheddata.getCRC());
        sqlitestatement.bindBlob(3, mutelistcacheddata.getData());
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (MuteListCachedData)obj);
    }

    public Long getKey(MuteListCachedData mutelistcacheddata)
    {
        if (mutelistcacheddata != null)
        {
            return mutelistcacheddata.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((MuteListCachedData)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public MuteListCachedData readEntity(Cursor cursor, int i)
    {
        Long long1;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        return new MuteListCachedData(long1, cursor.getInt(i + 1), cursor.getBlob(i + 2));
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, MuteListCachedData mutelistcacheddata, int i)
    {
        Long long1;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        mutelistcacheddata.setId(long1);
        mutelistcacheddata.setCRC(cursor.getInt(i + 1));
        mutelistcacheddata.setData(cursor.getBlob(i + 2));
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (MuteListCachedData)obj, i);
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

    protected Long updateKeyAfterInsert(MuteListCachedData mutelistcacheddata, long l)
    {
        mutelistcacheddata.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((MuteListCachedData)obj, l);
    }
}
