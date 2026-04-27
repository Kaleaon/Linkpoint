// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.sqlite.SQLiteDatabase$CursorFactory;
import com.lumiyaviewer.lumiya.orm.DBHandle;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.content.ContentValues;
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import com.lumiyaviewer.lumiya.Debug;
import android.database.sqlite.SQLiteDatabase;
import com.lumiyaviewer.lumiya.orm.DBHandleCache;

public class SLInventoryOpenHelper implements DBOpenHelper
{
    private static final int DB_VERSION = 21;
    
    private void enableWriteAheadLogging(final SQLiteDatabase obj) {
        try {
            final Method method = obj.getClass().getMethod("enableWriteAheadLogging", (Class[])new Class[0]);
            if (method != null) {
                method.invoke(obj, new Object[0]);
                Debug.Printf("Write-ahead logging is supported.", new Object[0]);
            }
        }
        catch (final InvocationTargetException ex) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            ex.printStackTrace();
        }
        catch (final IllegalAccessException ex2) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            ex2.printStackTrace();
        }
        catch (final IllegalArgumentException ex3) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            ex3.printStackTrace();
        }
        catch (final NoSuchMethodException ex4) {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
        }
    }
    
    public static SLInventoryOpenHelper getInstance() {
        return InstanceHolder.Instance;
    }
    
    private boolean initTables(final SQLiteDatabase sqLiteDatabase) throws SQLiteException {
        boolean b = true;
        final int n = 0;
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);");
        final Cursor query = sqLiteDatabase.query("DBVersion", new String[] { "Version" }, (String)null, (String[])null, (String)null, (String)null, (String)null);
        Label_0135: {
            if (!query.moveToFirst()) {
                break Label_0135;
            }
            int n2;
            int n3;
            if (query.getInt(0) != 21) {
                n2 = 0;
                n3 = 1;
            }
            else {
                n2 = 0;
                n3 = 0;
            }
            while (true) {
                query.close();
                if (n3 == 0) {
                    break Label_0135;
                }
                Debug.Printf("Database needs upgrade.", new Object[0]);
                try {
                    final String[] createTableStatements = InventoryEntryDBObject.getCreateTableStatements();
                    for (int length = createTableStatements.length, i = n; i < length; ++i) {
                        final String s = createTableStatements[i];
                        Debug.Printf("Inventory init: %s", s);
                        sqLiteDatabase.execSQL(s);
                    }
                    final ContentValues contentValues = new ContentValues();
                    contentValues.put("Version", Integer.valueOf(21));
                    if (n2 != 0) {
                        sqLiteDatabase.insert("DBVersion", (String)null, contentValues);
                    }
                    else {
                        sqLiteDatabase.update("DBVersion", contentValues, (String)null, (String[])null);
                    }
                    Debug.Printf("Upgraded database to version %d", 21);
                    return b;
                    n2 = 1;
                    n3 = 1;
                    continue;
                }
                catch (final Exception ex) {
                    final SQLiteException ex2 = new SQLiteException(ex.getMessage());
                    ex2.initCause((Throwable)ex);
                    throw ex2;
                }
                break;
            }
        }
        Debug.Printf("Database does not need upgrade.", new Object[0]);
        b = false;
        return b;
    }
    
    public DBHandle openDB(final String s) {
        synchronized (this) {
            try {
                Debug.Printf("Opening inventory DB '%s'", s);
                return DBHandleCache.getInstance().OpenDB(s, (DBHandleCache.DBOpenHelper)this);
            }
            catch (final SQLiteException ex) {
                Debug.Warning((Throwable)ex);
                return null;
            }
        }
    }
    
    @Override
    public SQLiteDatabase openOrCreateDatabase(final String s) throws SQLiteException {
        final SQLiteDatabase openOrCreateDatabase = SQLiteDatabase.openOrCreateDatabase(s, (SQLiteDatabase$CursorFactory)null);
        if (openOrCreateDatabase != null) {
            Debug.Printf("DB file '%s' opened", s);
            this.enableWriteAheadLogging(openOrCreateDatabase);
            SQLiteDatabase openOrCreateDatabase2 = openOrCreateDatabase;
            if (this.initTables(openOrCreateDatabase)) {
                Debug.Printf("Reopening DB file '%s'", s);
                openOrCreateDatabase.close();
                openOrCreateDatabase2 = SQLiteDatabase.openOrCreateDatabase(s, (SQLiteDatabase$CursorFactory)null);
                if (openOrCreateDatabase2 == null) {
                    throw new SQLiteException("DB was null");
                }
                this.enableWriteAheadLogging(openOrCreateDatabase2);
            }
            return openOrCreateDatabase2;
        }
        throw new SQLiteException("DB was null");
    }
    
    private static class InstanceHolder
    {
        private static final SLInventoryOpenHelper Instance;
        
        static {
            Instance = new SLInventoryOpenHelper();
        }
    }
}
