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
//            CachedResponse, DaoSession

public class CachedResponseDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Data = new Property(1, [B, "data", false, "DATA");
        public static final Property Key = new Property(0, java/lang/String, "key", true, "KEY");
        public static final Property MustRevalidate;

        static 
        {
            MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "CachedResponses";

    public CachedResponseDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public CachedResponseDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CachedResponses' (").append("'KEY' TEXT PRIMARY KEY NOT NULL ,").append("'DATA' BLOB,").append("'MUST_REVALIDATE' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CachedResponses'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, CachedResponse cachedresponse)
    {
        sqlitestatement.clearBindings();
        String s = cachedresponse.getKey();
        if (s != null)
        {
            sqlitestatement.bindString(1, s);
        }
        byte abyte0[] = cachedresponse.getData();
        if (abyte0 != null)
        {
            sqlitestatement.bindBlob(2, abyte0);
        }
        long l;
        if (cachedresponse.getMustRevalidate())
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
        bindValues(sqlitestatement, (CachedResponse)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((CachedResponse)obj);
    }

    public String getKey(CachedResponse cachedresponse)
    {
        if (cachedresponse != null)
        {
            return cachedresponse.getKey();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public CachedResponse readEntity(Cursor cursor, int i)
    {
        byte abyte0[] = null;
        boolean flag = false;
        String s;
        if (cursor.isNull(i + 0))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 0);
        }
        if (!cursor.isNull(i + 1))
        {
            abyte0 = cursor.getBlob(i + 1);
        }
        if (cursor.getShort(i + 2) != 0)
        {
            flag = true;
        }
        return new CachedResponse(s, abyte0, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, CachedResponse cachedresponse, int i)
    {
        Object obj1 = null;
        Object obj;
        boolean flag;
        if (cursor.isNull(i + 0))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 0);
        }
        cachedresponse.setKey(((String) (obj)));
        if (cursor.isNull(i + 1))
        {
            obj = obj1;
        } else
        {
            obj = cursor.getBlob(i + 1);
        }
        cachedresponse.setData(((byte []) (obj)));
        if (cursor.getShort(i + 2) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        cachedresponse.setMustRevalidate(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (CachedResponse)obj, i);
    }

    public volatile Object readKey(Cursor cursor, int i)
    {
        return readKey(cursor, i);
    }

    public String readKey(Cursor cursor, int i)
    {
        if (cursor.isNull(i + 0))
        {
            return null;
        } else
        {
            return cursor.getString(i + 0);
        }
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((CachedResponse)obj, l);
    }

    protected String updateKeyAfterInsert(CachedResponse cachedresponse, long l)
    {
        return cachedresponse.getKey();
    }
}
