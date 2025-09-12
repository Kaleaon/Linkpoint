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
import java.util.Date;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            MoneyTransaction, DaoSession

public class MoneyTransactionDao extends AbstractDao
{
    public static class Properties
    {

        public static final Property AgentUUID = new Property(2, java/util/UUID, "agentUUID", false, "AGENT_UUID");
        public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
        public static final Property NewBalance;
        public static final Property Timestamp = new Property(1, java/util/Date, "timestamp", false, "TIMESTAMP");
        public static final Property TransactionAmount;

        static 
        {
            TransactionAmount = new Property(3, Integer.TYPE, "transactionAmount", false, "TRANSACTION_AMOUNT");
            NewBalance = new Property(4, Integer.TYPE, "newBalance", false, "NEW_BALANCE");
        }

        public Properties()
        {
        }
    }


    public static final String TABLENAME = "MONEY_TRANSACTION";

    public MoneyTransactionDao(DaoConfig daoconfig)
    {
        super(daoconfig);
    }

    public MoneyTransactionDao(DaoConfig daoconfig, DaoSession daosession)
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
        sqlitedatabase.execSQL((new StringBuilder()).append("CREATE TABLE ").append(s).append("'MONEY_TRANSACTION' (").append("'_id' INTEGER PRIMARY KEY ,").append("'TIMESTAMP' INTEGER NOT NULL ,").append("'AGENT_UUID' TEXT,").append("'TRANSACTION_AMOUNT' INTEGER NOT NULL ,").append("'NEW_BALANCE' INTEGER NOT NULL );").toString());
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
        sqlitedatabase.execSQL(stringbuilder.append(s).append("'MONEY_TRANSACTION'").toString());
    }

    protected void bindValues(SQLiteStatement sqlitestatement, MoneyTransaction moneytransaction)
    {
        sqlitestatement.clearBindings();
        Object obj = moneytransaction.getId();
        if (obj != null)
        {
            sqlitestatement.bindLong(1, ((Long) (obj)).longValue());
        }
        sqlitestatement.bindLong(2, moneytransaction.getTimestamp().getTime());
        obj = moneytransaction.getAgentUUID();
        if (obj != null)
        {
            sqlitestatement.bindString(3, ((UUID) (obj)).toString());
        }
        sqlitestatement.bindLong(4, moneytransaction.getTransactionAmount());
        sqlitestatement.bindLong(5, moneytransaction.getNewBalance());
    }

    protected volatile void bindValues(SQLiteStatement sqlitestatement, Object obj)
    {
        bindValues(sqlitestatement, (MoneyTransaction)obj);
    }

    public Long getKey(MoneyTransaction moneytransaction)
    {
        if (moneytransaction != null)
        {
            return moneytransaction.getId();
        } else
        {
            return null;
        }
    }

    public volatile Object getKey(Object obj)
    {
        return getKey((MoneyTransaction)obj);
    }

    protected boolean isEntityUpdateable()
    {
        return true;
    }

    public MoneyTransaction readEntity(Cursor cursor, int i)
    {
        UUID uuid = null;
        Long long1;
        Date date;
        if (cursor.isNull(i + 0))
        {
            long1 = null;
        } else
        {
            long1 = Long.valueOf(cursor.getLong(i + 0));
        }
        date = new Date(cursor.getLong(i + 1));
        if (!cursor.isNull(i + 2))
        {
            uuid = UUID.fromString(cursor.getString(i + 2));
        }
        return new MoneyTransaction(long1, date, uuid, cursor.getInt(i + 3), cursor.getInt(i + 4));
    }

    public volatile Object readEntity(Cursor cursor, int i)
    {
        return readEntity(cursor, i);
    }

    public void readEntity(Cursor cursor, MoneyTransaction moneytransaction, int i)
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
        moneytransaction.setId(((Long) (obj)));
        moneytransaction.setTimestamp(new Date(cursor.getLong(i + 1)));
        if (cursor.isNull(i + 2))
        {
            obj = obj1;
        } else
        {
            obj = UUID.fromString(cursor.getString(i + 2));
        }
        moneytransaction.setAgentUUID(((UUID) (obj)));
        moneytransaction.setTransactionAmount(cursor.getInt(i + 3));
        moneytransaction.setNewBalance(cursor.getInt(i + 4));
    }

    public volatile void readEntity(Cursor cursor, Object obj, int i)
    {
        readEntity(cursor, (MoneyTransaction)obj, i);
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

    protected Long updateKeyAfterInsert(MoneyTransaction moneytransaction, long l)
    {
        moneytransaction.setId(Long.valueOf(l));
        return Long.valueOf(l);
    }

    protected volatile Object updateKeyAfterInsert(Object obj, long l)
    {
        return updateKeyAfterInsert((MoneyTransaction)obj, l);
    }
}
