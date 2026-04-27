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
//            SearchGridResult, DaoSession

public class SearchGridResultDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property ItemName = new Property(4, java/lang/String, "itemName", false, "ITEM_NAME");
        public static final Property ItemType;
        public static final Property ItemUUID = new Property(3, java/util/UUID, "itemUUID", false, "ITEM_UUID");
        public static final Property LevensteinDistance;
        public static final Property MemberCount = new Property(6, java/lang/Integer, "memberCount", false, "MEMBER_COUNT");
        public static final Property SearchUUID = new Property(1, java/util/UUID, "searchUUID", false, "SEARCH_UUID");

        static 
        {
            ItemType = new Property(2, Integer.TYPE, "itemType", false, "ITEM_TYPE");
            LevensteinDistance = new Property(5, Integer.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "SearchGridResults";

    public SearchGridResultDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public SearchGridResultDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'SearchGridResults' (").append("'_id' INTEGER PRIMARY KEY ,").append("'SEARCH_UUID' TEXT NOT NULL ,").append("'ITEM_TYPE' INTEGER NOT NULL ,").append("'ITEM_UUID' TEXT NOT NULL ,").append("'ITEM_NAME' TEXT NOT NULL ,").append("'LEVENSTEIN_DISTANCE' INTEGER NOT NULL ,").append("'MEMBER_COUNT' INTEGER);").toString());
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE INDEX ").append(s).append("IDX_SearchGridResults_SEARCH_UUID ON SearchGridResults").append(" (SEARCH_UUID);").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'SearchGridResults'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, SearchGridResult searchgridresult)
    {
        sqlitestatement.clearBindings();
        Long long1 = searchgridresult.getId();
        if (long1 != null)
        {
            sqlitestatement.bindLong(1, long1.longValue());
        }
        sqlitestatement.bindString(2, searchgridresult.getSearchUUID().toString());
        sqlitestatement.bindLong(3, searchgridresult.getItemType());
        sqlitestatement.bindString(4, searchgridresult.getItemUUID().toString());
        sqlitestatement.bindString(5, searchgridresult.getItemName());
        sqlitestatement.bindLong(6, searchgridresult.getLevensteinDistance());
        searchgridresult = searchgridresult.getMemberCount();
        if (searchgridresult != null)
        {
            sqlitestatement.bindLong(7, searchgridresult.intValue());
        }
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (SearchGridResult)obj);
    }

    public Long getKey(SearchGridResult searchgridresult)
    {
        if (searchgridresult != null)
        {
            return searchgridresult.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((SearchGridResult)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public SearchGridResult readEntity(Cursor cursor, int i)
    {
        Object obj = null;
        Long long1;
        UUID uuid;
        UUID uuid1;
        String s;
        int j;
        int k;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        uuid = UUID.fromString(cursor.getString(i + 1));
        j = cursor.getInt(i + 2);
        uuid1 = UUID.fromString(cursor.getString(i + 3));
        s = cursor.getString(i + 4);
        k = cursor.getInt(i + 5);
        if (cursor.isNull(i + 6))
        {
            cursor = obj;
        } else
        {
            cursor = Integer.valueOf(cursor.getInt(i + 6));
        }
        return new SearchGridResult(long1, uuid, j, uuid1, s, k, cursor);
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, SearchGridResult searchgridresult, int i)
    {
        Object obj = null;
        Long long1;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        searchgridresult.setId(long1);
        searchgridresult.setSearchUUID(UUID.fromString(cursor.getString(i + 1)));
        searchgridresult.setItemType(cursor.getInt(i + 2));
        searchgridresult.setItemUUID(UUID.fromString(cursor.getString(i + 3)));
        searchgridresult.setItemName(cursor.getString(i + 4));
        searchgridresult.setLevensteinDistance(cursor.getInt(i + 5));
        if (cursor.isNull(i + 6))
        {
            cursor = obj;
        } else
        {
            cursor = Integer.valueOf(cursor.getInt(i + 6));
        }
        searchgridresult.setMemberCount(cursor);
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (SearchGridResult)obj, i);
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

    protected Long updateKeyAfterInsert(SearchGridResult searchgridresult, long l)
    {
        searchgridresult.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((SearchGridResult)obj, l);
    }
}
