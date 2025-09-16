package com.lumiyaviewer.lumiya.slproto.inventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.DBHandle;
import com.lumiyaviewer.lumiya.orm.DBHandleCache;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SLInventoryOpenHelper implements DBHandleCache.DBOpenHelper {
    private static final int DB_VERSION = 21;

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final SLInventoryOpenHelper Instance = new SLInventoryOpenHelper();

        private InstanceHolder() {
        }
    }

    private void enableWriteAheadLogging(SQLiteDatabase sQLiteDatabase) {
        try {
            Method method = sQLiteDatabase.getClass().getMethod("enableWriteAheadLogging", new Class[0]);
            if (method != null) {
                method.invoke(sQLiteDatabase, new Object[0]);
                Debug.Printf("Write-ahead logging is supported.", new Object[0]);
            }
        } catch (NoSuchMethodException e) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
        } catch (IllegalArgumentException e2) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            e4.printStackTrace();
        }
    }

    public static SLInventoryOpenHelper getInstance() {
        return InstanceHolder.Instance;
    }

    private boolean initTables(SQLiteDatabase sQLiteDatabase) throws SQLiteException {
        sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);");
        Cursor query = sQLiteDatabase.query("DBVersion", new String[]{"Version"}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
        if (!query.moveToFirst()) {
            z = true;
            z2 = true;
        } else if (query.getInt(0) != 21) {
            z = false;
            z2 = true;
        } else {
            z = false;
            z2 = false;
        }
        query.close();
        if (z2) {
            Debug.Printf("Database needs upgrade.", new Object[0]);
            try {
                for (String str : SLInventoryEntry.getCreateTableStatements()) {
                    Debug.Printf("Inventory init: %s", str);
                    sQLiteDatabase.execSQL(str);
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("Version", 21);
                if (z) {
                    sQLiteDatabase.insert("DBVersion", (String) null, contentValues);
                } else {
                    sQLiteDatabase.update("DBVersion", contentValues, (String) null, (String[]) null);
                }
                Debug.Printf("Upgraded database to version %d", 21);
                return true;
            } catch (Exception e) {
                SQLiteException sQLiteException = new SQLiteException(e.getMessage());
                sQLiteException.initCause(e);
                throw sQLiteException;
            }
        } else {
            Debug.Printf("Database does not need upgrade.", new Object[0]);
            return false;
        }
    }

    public synchronized DBHandle openDB(String str) {
        try {
            Debug.Printf("Opening inventory DB '%s'", str);
        } catch (SQLiteException e) {
            Debug.Warning(e);
            return null;
        }
        return DBHandleCache.getInstance().OpenDB(str, this);
    }

    public SQLiteDatabase openOrCreateDatabase(String str) throws SQLiteException {
        SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, (SQLiteDatabase.CursorFactory) null);
        if (openOrCreateDatabase != null) {
            Debug.Printf("DB file '%s' opened", str);
            enableWriteAheadLogging(openOrCreateDatabase);
            if (initTables(openOrCreateDatabase)) {
                Debug.Printf("Reopening DB file '%s'", str);
                openOrCreateDatabase.close();
                openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(str, (SQLiteDatabase.CursorFactory) null);
                if (openOrCreateDatabase != null) {
                    enableWriteAheadLogging(openOrCreateDatabase);
                } else {
                    throw new SQLiteException("DB was null");
                }
            }
            return openOrCreateDatabase;
        }
        throw new SQLiteException("DB was null");
    }
}
