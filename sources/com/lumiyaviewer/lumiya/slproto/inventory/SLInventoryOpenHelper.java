// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryEntry

public class SLInventoryOpenHelper
    implements com.lumiyaviewer.lumiya.orm.DBHandleCache.DBOpenHelper
{
    private static class InstanceHolder
    {

        private static final SLInventoryOpenHelper Instance = new SLInventoryOpenHelper();

        static SLInventoryOpenHelper _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private static final int DB_VERSION = 21;

    public SLInventoryOpenHelper()
    {
    }

    private void enableWriteAheadLogging(SQLiteDatabase sqlitedatabase)
    {
        Method method;
        try
        {
            method = sqlitedatabase.getClass().getMethod("enableWriteAheadLogging", new Class[0]);
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            sqlitedatabase.printStackTrace();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            sqlitedatabase.printStackTrace();
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            Debug.Printf("Write-ahead logging not supported.", new Object[0]);
            sqlitedatabase.printStackTrace();
            return;
        }
        if (method == null)
        {
            break MISSING_BLOCK_LABEL_37;
        }
        method.invoke(sqlitedatabase, new Object[0]);
        Debug.Printf("Write-ahead logging is supported.", new Object[0]);
    }

    public static SLInventoryOpenHelper getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    private boolean initTables(SQLiteDatabase sqlitedatabase)
        throws SQLiteException
    {
        boolean flag1 = false;
        sqlitedatabase.execSQL("CREATE TABLE IF NOT EXISTS DBVersion (Version INTEGER);");
        Object obj = sqlitedatabase.query("DBVersion", new String[] {
            "Version"
        }, null, null, null, null, null);
        String as[];
        String s;
        boolean flag;
        int i;
        int j;
        if (((Cursor) (obj)).moveToFirst())
        {
            if (((Cursor) (obj)).getInt(0) != 21)
            {
                flag = false;
                i = 1;
            } else
            {
                flag = false;
                i = 0;
            }
        } else
        {
            flag = true;
            i = 1;
        }
        ((Cursor) (obj)).close();
        if (i == 0)
        {
            break MISSING_BLOCK_LABEL_214;
        }
        Debug.Printf("Database needs upgrade.", new Object[0]);
        try
        {
            as = SLInventoryEntry.getCreateTableStatements();
            j = as.length;
        }
        // Misplaced declaration of an exception variable
        catch (SQLiteDatabase sqlitedatabase)
        {
            SQLiteException sqliteexception = new SQLiteException(sqlitedatabase.getMessage());
            sqliteexception.initCause(sqlitedatabase);
            throw sqliteexception;
        }
        i = ((flag1) ? 1 : 0);
        if (i >= j)
        {
            break; /* Loop/switch isn't completed */
        }
        s = as[i];
        Debug.Printf("Inventory init: %s", new Object[] {
            s
        });
        sqlitedatabase.execSQL(s);
        i++;
        if (true) goto _L2; else goto _L1
_L2:
        break MISSING_BLOCK_LABEL_89;
_L1:
        as = new ContentValues();
        as.put("Version", Integer.valueOf(21));
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_180;
        }
        sqlitedatabase.insert("DBVersion", null, as);
_L3:
        Debug.Printf("Upgraded database to version %d", new Object[] {
            Integer.valueOf(21)
        });
        return true;
        sqlitedatabase.update("DBVersion", as, null, null);
          goto _L3
        Debug.Printf("Database does not need upgrade.", new Object[0]);
        return false;
    }

    public DBHandle openDB(String s)
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Printf("Opening inventory DB '%s'", new Object[] {
            s
        });
        s = DBHandleCache.getInstance().OpenDB(s, this);
        this;
        JVM INSTR monitorexit ;
        return s;
        s;
        Debug.Warning(s);
        this;
        JVM INSTR monitorexit ;
        return null;
        s;
        throw s;
    }

    public SQLiteDatabase openOrCreateDatabase(String s)
        throws SQLiteException
    {
label0:
        {
label1:
            {
                SQLiteDatabase sqlitedatabase1 = SQLiteDatabase.openOrCreateDatabase(s, null);
                if (sqlitedatabase1 == null)
                {
                    break label0;
                }
                Debug.Printf("DB file '%s' opened", new Object[] {
                    s
                });
                enableWriteAheadLogging(sqlitedatabase1);
                SQLiteDatabase sqlitedatabase = sqlitedatabase1;
                if (initTables(sqlitedatabase1))
                {
                    Debug.Printf("Reopening DB file '%s'", new Object[] {
                        s
                    });
                    sqlitedatabase1.close();
                    sqlitedatabase = SQLiteDatabase.openOrCreateDatabase(s, null);
                    if (sqlitedatabase == null)
                    {
                        break label1;
                    }
                    enableWriteAheadLogging(sqlitedatabase);
                }
                return sqlitedatabase;
            }
            throw new SQLiteException("DB was null");
        }
        throw new SQLiteException("DB was null");
    }
}
