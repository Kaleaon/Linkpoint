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
//            CachedAsset, DaoSession

public class CachedAssetDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Data = new Property(2, [B, "data", false, "DATA");
        public static final Property Key = new Property(0, java/lang/String, "key", true, "KEY");
        public static final Property MustRevalidate;
        public static final Property Status;

        static 
        {
            Status = new Property(1, Integer.TYPE, "status", false, "STATUS");
            MustRevalidate = new Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "CachedAssets";

    public CachedAssetDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public CachedAssetDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'CachedAssets' (").append("'KEY' TEXT PRIMARY KEY NOT NULL ,").append("'STATUS' INTEGER NOT NULL ,").append("'DATA' BLOB,").append("'MUST_REVALIDATE' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'CachedAssets'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, CachedAsset cachedasset)
    {
        sqlitestatement.clearBindings();
        String s = cachedasset.getKey();
        if (s != null)
        {
            sqlitestatement.bindString(1, s);
        }
        sqlitestatement.bindLong(2, cachedasset.getStatus());
        byte abyte0[] = cachedasset.getData();
        if (abyte0 != null)
        {
            sqlitestatement.bindBlob(3, abyte0);
        }
        long l;
        if (cachedasset.getMustRevalidate())
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
        bindValues(sqlitestatement, (CachedAsset)obj);
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((CachedAsset)obj);
    }

    public String getKey(CachedAsset cachedasset)
    {
        if (cachedasset != null)
        {
            return cachedasset.getKey();
        } else
        {
            return null;
        }
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public CachedAsset readEntity(Cursor cursor, int i)
    {
        byte abyte0[] = null;
        boolean flag = false;
        String s;
        int j;
        if (cursor.isNull(i + 0))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 0);
        }
        j = cursor.getInt(i + 1);
        if (!cursor.isNull(i + 2))
        {
            abyte0 = cursor.getBlob(i + 2);
        }
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        }
        return new CachedAsset(s, j, abyte0, flag);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, CachedAsset cachedasset, int i)
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
        cachedasset.setKey(((String) (obj)));
        cachedasset.setStatus(cursor.getInt(i + 1));
        if (cursor.isNull(i + 2))
        {
            obj = obj1;
        } else
        {
            obj = cursor.getBlob(i + 2);
        }
        cachedasset.setData(((byte []) (obj)));
        if (cursor.getShort(i + 3) != 0)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        cachedasset.setMustRevalidate(flag);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (CachedAsset)obj, i);
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
        return updateKeyAfterInsert((CachedAsset)obj, l);
    }

    protected String updateKeyAfterInsert(CachedAsset cachedasset, long l)
    {
        return cachedasset.getKey();
    }
}
