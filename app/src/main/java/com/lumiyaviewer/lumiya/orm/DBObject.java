package com.lumiyaviewer.lumiya.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Parcelable;
import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class DBObject implements Parcelable {
    protected long _id;

    public static class DatabaseBindingException extends Exception {
        public DatabaseBindingException(Class<?> cls, String str) {
            super("Failed to bind " + cls.getSimpleName() + ": " + str);
        }

        public DatabaseBindingException(String str) {
            super(str);
        }
    }

    public DBObject() {
        this._id = 0;
    }

    public DBObject(Cursor cursor) {
        loadFromCursor(cursor);
    }

    public DBObject(SQLiteDatabase sQLiteDatabase, long j) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        Cursor query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[]{Long.toString(j)}, (String) null, (String) null, (String) null);
        if (!query.moveToFirst()) {
            query.close();
            throw new DatabaseBindingException(getClass(), "not found: _id = " + j);
        }
        loadFromCursor(query);
        query.close();
    }

    /* access modifiers changed from: protected */
    public UUID UUIDfromBlob(byte[] bArr) {
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        return new UUID(wrap.getLong(), wrap.getLong());
    }

    /* access modifiers changed from: protected */
    public byte[] UUIDtoBlob(UUID uuid) {
        ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
        wrap.putLong(uuid.getMostSignificantBits());
        wrap.putLong(uuid.getLeastSignificantBits());
        return wrap.array();
    }

    public abstract void bindInsertOrUpdate(SQLiteStatement sQLiteStatement);

    public void delete(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        } else if (this._id != 0) {
            sQLiteDatabase.delete(getTableName(), "_id = ?", new String[]{Long.toString(this._id)});
        }
    }

    public abstract ContentValues getContentValues();

    /* access modifiers changed from: protected */
    public abstract String[] getFieldNames();

    public long getId() {
        return this._id;
    }

    /* access modifiers changed from: protected */
    public abstract String getTableName();

    public abstract void loadFromCursor(Cursor cursor);

    public void reload(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        } else if (this._id != 0) {
            Cursor query = sQLiteDatabase.query(getTableName(), getFieldNames(), "_id = ?", new String[]{Long.toString(this._id)}, (String) null, (String) null, (String) null);
            if (query.moveToFirst()) {
                loadFromCursor(query);
            }
            query.close();
        }
    }

    public void resetId() {
        this._id = 0;
    }

    public void save(SQLiteDatabase sQLiteDatabase) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        String tableName = getTableName();
        ContentValues contentValues = getContentValues();
        try {
            if (this._id != 0) {
                sQLiteDatabase.update(tableName, contentValues, "_id = ?", new String[]{Long.toString(this._id)});
                return;
            }
            this._id = sQLiteDatabase.insert(tableName, (String) null, contentValues);
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = new DatabaseBindingException(getClass(), "insert or update failed");
            databaseBindingException.initCause(e);
            throw databaseBindingException;
        }
    }

    /* access modifiers changed from: protected */
    public void updateOrInsert(SQLiteDatabase sQLiteDatabase, String str, String[] strArr) throws DatabaseBindingException {
        if (sQLiteDatabase == null) {
            throw new DatabaseBindingException(getClass(), "database not opened.");
        }
        String tableName = getTableName();
        ContentValues contentValues = getContentValues();
        try {
            if (sQLiteDatabase.update(tableName, contentValues, str, strArr) == 0) {
                this._id = sQLiteDatabase.insert(tableName, (String) null, contentValues);
            }
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = new DatabaseBindingException(getClass(), "insert or update failed");
            databaseBindingException.initCause(e);
            throw databaseBindingException;
        }
    }

    /* access modifiers changed from: protected */
    public void updateOrInsert(SQLiteStatement sQLiteStatement, SQLiteStatement sQLiteStatement2) throws DatabaseBindingException {
        try {
            bindInsertOrUpdate(sQLiteStatement);
            if (sQLiteStatement.executeUpdateDelete() == 0) {
                bindInsertOrUpdate(sQLiteStatement2);
                this._id = sQLiteStatement2.executeInsert();
            }
        } catch (SQLiteException e) {
            DatabaseBindingException databaseBindingException = new DatabaseBindingException(getClass(), "insert or update failed");
            databaseBindingException.initCause(e);
            throw databaseBindingException;
        }
    }
}
