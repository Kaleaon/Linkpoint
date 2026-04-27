// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteException;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import java.nio.ByteBuffer;
import java.util.UUID;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.os.Parcelable;

public abstract class DBObject implements Parcelable
{
    protected long _id;
    
    public DBObject() {
        this._id = 0L;
    }
    
    public DBObject(final Cursor cursor) {
        this.loadFromCursor(cursor);
    }
    
    public DBObject(final SQLiteDatabase sqLiteDatabase, final long n) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(this.getClass(), "database not opened.");
        }
        final Cursor query = sqLiteDatabase.query(this.getTableName(), this.getFieldNames(), "_id = ?", new String[] { Long.toString(n) }, (String)null, (String)null, (String)null);
        if (!query.moveToFirst()) {
            query.close();
            throw new DatabaseBindingException(this.getClass(), "not found: _id = " + n);
        }
        this.loadFromCursor(query);
        query.close();
    }
    
    protected UUID UUIDfromBlob(final byte[] array) {
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        return new UUID(wrap.getLong(), wrap.getLong());
    }
    
    protected byte[] UUIDtoBlob(final UUID uuid) {
        final ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
        wrap.putLong(uuid.getMostSignificantBits());
        wrap.putLong(uuid.getLeastSignificantBits());
        return wrap.array();
    }
    
    public abstract void bindInsertOrUpdate(final SQLiteStatement p0);
    
    public void delete(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(this.getClass(), "database not opened.");
        }
        if (this._id != 0L) {
            sqLiteDatabase.delete(this.getTableName(), "_id = ?", new String[] { Long.toString(this._id) });
        }
    }
    
    public abstract ContentValues getContentValues();
    
    protected abstract String[] getFieldNames();
    
    public long getId() {
        return this._id;
    }
    
    protected abstract String getTableName();
    
    public abstract void loadFromCursor(final Cursor p0);
    
    public void reload(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(this.getClass(), "database not opened.");
        }
        if (this._id != 0L) {
            final Cursor query = sqLiteDatabase.query(this.getTableName(), this.getFieldNames(), "_id = ?", new String[] { Long.toString(this._id) }, (String)null, (String)null, (String)null);
            if (query.moveToFirst()) {
                this.loadFromCursor(query);
            }
            query.close();
        }
    }
    
    public void resetId() {
        this._id = 0L;
    }
    
    public void save(final SQLiteDatabase sqLiteDatabase) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(this.getClass(), "database not opened.");
        }
        final String tableName = this.getTableName();
        final ContentValues contentValues = this.getContentValues();
        try {
            if (this._id != 0L) {
                sqLiteDatabase.update(tableName, contentValues, "_id = ?", new String[] { Long.toString(this._id) });
            }
            else {
                this._id = sqLiteDatabase.insert(tableName, (String)null, contentValues);
            }
        }
        catch (final SQLiteException cause) {
            final DatabaseBindingException ex = new DatabaseBindingException(this.getClass(), "insert or update failed");
            ex.initCause((Throwable)cause);
            throw ex;
        }
    }
    
    protected void updateOrInsert(final SQLiteDatabase sqLiteDatabase, final String s, final String[] array) throws DatabaseBindingException {
        if (sqLiteDatabase == null) {
            throw new DatabaseBindingException(this.getClass(), "database not opened.");
        }
        final String tableName = this.getTableName();
        final ContentValues contentValues = this.getContentValues();
        try {
            if (sqLiteDatabase.update(tableName, contentValues, s, array) == 0) {
                this._id = sqLiteDatabase.insert(tableName, (String)null, contentValues);
            }
        }
        catch (final SQLiteException cause) {
            final DatabaseBindingException ex = new DatabaseBindingException(this.getClass(), "insert or update failed");
            ex.initCause((Throwable)cause);
            throw ex;
        }
    }
    
    protected void updateOrInsert(final SQLiteStatement sqLiteStatement, final SQLiteStatement sqLiteStatement2) throws DatabaseBindingException {
        try {
            this.bindInsertOrUpdate(sqLiteStatement);
            if (sqLiteStatement.executeUpdateDelete() == 0) {
                this.bindInsertOrUpdate(sqLiteStatement2);
                this._id = sqLiteStatement2.executeInsert();
            }
        }
        catch (final SQLiteException cause) {
            final DatabaseBindingException ex = new DatabaseBindingException(this.getClass(), "insert or update failed");
            ex.initCause((Throwable)cause);
            throw ex;
        }
    }
    
    public static class DatabaseBindingException extends Exception
    {
        public DatabaseBindingException(final Class<?> clazz, final String str) {
            super("Failed to bind " + clazz.getSimpleName() + ": " + str);
        }
        
        public DatabaseBindingException(final String message) {
            super(message);
        }
    }
}
