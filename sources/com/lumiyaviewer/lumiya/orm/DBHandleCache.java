// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            DBHandle

public class DBHandleCache
{
    public static interface DBOpenHelper
    {

        public abstract SQLiteDatabase openOrCreateDatabase(String s)
            throws SQLiteException;
    }

    private static class DBOpenRef
    {

        private final String fileName;
        private int handleCount;
        private final SQLiteDatabase sqliteDB;

        public final void acquireReference()
        {
            handleCount = handleCount + 1;
        }

        public final SQLiteDatabase getDB()
        {
            return sqliteDB;
        }

        public final String getFileName()
        {
            return fileName;
        }

        public final int releaseReference()
        {
            handleCount = handleCount - 1;
            return handleCount;
        }

        public DBOpenRef(String s, SQLiteDatabase sqlitedatabase)
        {
            fileName = s;
            sqliteDB = sqlitedatabase;
            handleCount = 0;
        }
    }

    private static class InstanceHolder
    {

        private static final DBHandleCache Instance = new DBHandleCache(null);

        static DBHandleCache _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private final Map fileMap;
    private final Map refMap;
    private final ReferenceQueue refQueue;

    private DBHandleCache()
    {
        refQueue = new ReferenceQueue();
        refMap = new IdentityHashMap();
        fileMap = new HashMap();
        Debug.Printf("DBHandleCache: Initialized.", new Object[0]);
    }

    DBHandleCache(DBHandleCache dbhandlecache)
    {
        this();
    }

    public static DBHandleCache getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public void Cleanup()
    {
        this;
        JVM INSTR monitorenter ;
_L2:
        Object obj = refQueue.poll();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_100;
        }
        Object obj1 = (DBOpenRef)refMap.remove(obj);
        if (obj1 == null) goto _L2; else goto _L1
_L1:
        if (((DBOpenRef) (obj1)).releaseReference() > 0) goto _L2; else goto _L3
_L3:
        obj = ((DBOpenRef) (obj1)).getFileName();
        Debug.Printf("DBHandle: Closing db '%s'", new Object[] {
            obj
        });
        obj1 = ((DBOpenRef) (obj1)).getDB();
        if (((SQLiteDatabase) (obj1)).isOpen())
        {
            ((SQLiteDatabase) (obj1)).close();
        }
_L4:
        fileMap.remove(obj);
          goto _L2
        Exception exception;
        exception;
        throw exception;
        SQLiteException sqliteexception;
        sqliteexception;
        Debug.Warning(sqliteexception);
          goto _L4
        this;
        JVM INSTR monitorexit ;
    }

    public DBHandle OpenDB(String s, DBOpenHelper dbopenhelper)
        throws SQLiteException
    {
        this;
        JVM INSTR monitorenter ;
        DBOpenRef dbopenref1 = (DBOpenRef)fileMap.get(s);
        DBOpenRef dbopenref;
        dbopenref = dbopenref1;
        if (dbopenref1 != null)
        {
            break MISSING_BLOCK_LABEL_66;
        }
        Debug.Printf("DBHandle: Opening db '%s'", new Object[] {
            s
        });
        dbopenref = new DBOpenRef(s, dbopenhelper.openOrCreateDatabase(s));
        fileMap.put(s, dbopenref);
        s = new DBHandle(dbopenref.getDB());
        dbopenref.acquireReference();
        dbopenhelper = new PhantomReference(s, refQueue);
        refMap.put(dbopenhelper, dbopenref);
        this;
        JVM INSTR monitorexit ;
        return s;
        s;
        throw s;
    }

    public boolean hasOpenHandles()
    {
        this;
        JVM INSTR monitorenter ;
        if (!fileMap.isEmpty()) goto _L2; else goto _L1
_L1:
        boolean flag = refMap.isEmpty();
_L4:
        this;
        JVM INSTR monitorexit ;
        return flag ^ true;
_L2:
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        throw exception;
    }
}
