// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcelable;
import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class DBObject
    implements Parcelable
{
    public static class DatabaseBindingException extends Exception
    {

        public DatabaseBindingException(Class class1, String s)
        {
            super((new StringBuilder()).append("Failed to bind ").append(class1.getSimpleName()).append(": ").append(s).toString());
        }

        public DatabaseBindingException(String s)
        {
            super(s);
        }
    }


    protected long _id;

    public DBObject()
    {
        _id = 0L;
    }

    public DBObject(Cursor cursor)
    {
        loadFromCursor(cursor);
    }

    public DBObject(SQLiteDatabase sqlitedatabase, long l)
        throws DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        sqlitedatabase = sqlitedatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[] {
            Long.toString(l)
        }, null, null, null);
        if (!sqlitedatabase.moveToFirst())
        {
            sqlitedatabase.close();
            throw new DatabaseBindingException(getClass(), (new StringBuilder()).append("not found: _id = ").append(l).toString());
        } else
        {
            loadFromCursor(sqlitedatabase);
            sqlitedatabase.close();
            return;
        }
    }

    protected UUID UUIDfromBlob(byte abyte0[])
    {
        abyte0 = ByteBuffer.wrap(abyte0);
        return new UUID(abyte0.getLong(), abyte0.getLong());
    }

    protected byte[] UUIDtoBlob(UUID uuid)
    {
        ByteBuffer bytebuffer = ByteBuffer.wrap(new byte[16]);
        bytebuffer.putLong(uuid.getMostSignificantBits());
        bytebuffer.putLong(uuid.getLeastSignificantBits());
        return bytebuffer.array();
    }

    public abstract void bindInsertOrUpdate(SQLiteStatement sqlitestatement);

    public void delete(SQLiteDatabase sqlitedatabase)
        throws DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        if (_id != 0L)
        {
            sqlitedatabase.delete(getTableName(), "_id = ?", new String[] {
                Long.toString(_id)
            });
        }
    }

    public abstract ContentValues getContentValues();

    protected abstract String[] getFieldNames();

    public long getId()
    {
        return _id;
    }

    protected abstract String getTableName();

    public abstract void loadFromCursor(Cursor cursor);

    public void reload(SQLiteDatabase sqlitedatabase)
        throws DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        if (_id != 0L)
        {
            sqlitedatabase = sqlitedatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[] {
                Long.toString(_id)
            }, null, null, null);
            if (sqlitedatabase.moveToFirst())
            {
                loadFromCursor(sqlitedatabase);
            }
            sqlitedatabase.close();
        }
    }

    public void resetId()
    {
        _id = 0L;
    }

    public void save(SQLiteDatabase sqlitedatabase)
        throws DatabaseBindingException
    {
        Object obj;
        ContentValues contentvalues;
        if (sqlitedatabase == null)
        {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        obj = getTableName();
        contentvalues = getContentValues();
        if (_id != 0L)
        {
            sqlitedatabase.update(((String) (obj)), contentvalues, "_id = ?", new String[] {
                Long.toString(_id)
            });
            return;
        }
        try
        {
            _id = sqlitedatabase.insert(((String) (obj)), null, contentvalues);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            obj = new DatabaseBindingException(getClass(), "insert or update failed");
        }
        ((DatabaseBindingException) (obj)).initCause(sqlitedatabase);
        throw obj;
    }

    protected void updateOrInsert(SQLiteDatabase sqlitedatabase, String s, String as[])
        throws DatabaseBindingException
    {
        if (sqlitedatabase == null)
        {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        String s1 = getTableName();
        ContentValues contentvalues = getContentValues();
        try
        {
            if (sqlitedatabase.update(s1, contentvalues, s, as) == 0)
            {
                _id = sqlitedatabase.insert(s1, null, contentvalues);
            }
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            s = new DatabaseBindingException(getClass(), "insert or update failed");
        }
        s.initCause(sqlitedatabase);
        throw s;
    }

    protected void updateOrInsert(SQLiteStatement sqlitestatement, SQLiteStatement sqlitestatement1)
        throws DatabaseBindingException
    {
        try
        {
            bindInsertOrUpdate(sqlitestatement);
            if (sqlitestatement.executeUpdateDelete() == 0)
            {
                bindInsertOrUpdate(sqlitestatement1);
                _id = sqlitestatement1.executeInsert();
            }
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteStatement sqlitestatement)
        {
            sqlitestatement1 = new DatabaseBindingException(getClass(), "insert or update failed");
        }
        sqlitestatement1.initCause(sqlitestatement);
        throw sqlitestatement1;
    }
}
