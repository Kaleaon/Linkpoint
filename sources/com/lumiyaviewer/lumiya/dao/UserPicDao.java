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
//            UserPic, DaoSession

public class UserPicDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Bitmap = new Property(2, [B, "bitmap", false, "BITMAP");
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property Uuid = new Property(1, java/lang/String, "uuid", false, "UUID");


        public Properties()
        {
        }
    }


    public static final String TABLENAME = "USER_PIC";

    public UserPicDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public UserPicDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'USER_PIC' (").append("'_id' INTEGER PRIMARY KEY ,").append("'UUID' TEXT,").append("'BITMAP' BLOB);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_USER_PIC_UUID ON USER_PIC").append(" (UUID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'USER_PIC'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, UserPic userpic)
    {
        sqlitestatement.clearBindings();
        Object obj = userpic.getId();
        if (obj != null)
        {
            sqlitestatement.bindLong(1, ((Long) (obj)).longValue());
        }
        obj = userpic.getUuid();
        if (obj != null)
        {
            sqlitestatement.bindString(2, ((String) (obj)));
        }
        userpic = userpic.getBitmap();
        if (userpic != null)
        {
            sqlitestatement.bindBlob(3, userpic);
        }
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (UserPic)obj);
    }

    public Long getKey(UserPic userpic)
    {
        if (userpic != null)
        {
            return userpic.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((UserPic)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public UserPic readEntity(Cursor cursor, int i)
    {
        Object obj = null;
        Long long1;
        String s;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        if (cursor.isNull(i + 1))
        {
            s = null;
        } else
        {
            s = cursor.getString(i + 1);
        }
        if (cursor.isNull(i + 2))
        {
            cursor = obj;
        } else
        {
            cursor = cursor.getBlob(i + 2);
        }
        return new UserPic(long1, s, cursor);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, UserPic userpic, int i)
    {
        Object obj1 = null;
        Object obj;
        if (cursor.isNull(i + 0))
        {
            obj = null;
        } else
        {
            obj = Long.valueOf(cursor.getLong(i + 0));
        }
        userpic.setId(((Long) (obj)));
        if (cursor.isNull(i + 1))
        {
            obj = null;
        } else
        {
            obj = cursor.getString(i + 1);
        }
        userpic.setUuid(((String) (obj)));
        if (cursor.isNull(i + 2))
        {
            cursor = obj1;
        } else
        {
            cursor = cursor.getBlob(i + 2);
        }
        userpic.setBitmap(cursor);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (UserPic)obj, i);
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

    protected Long updateKeyAfterInsert(UserPic userpic, long l)
    {
        userpic.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((UserPic)obj, l);
    }
}
