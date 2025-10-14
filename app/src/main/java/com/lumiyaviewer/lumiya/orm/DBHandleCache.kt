package com.lumiyaviewer.lumiya.orm

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.lumiyaviewer.lumiya.Debug
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.Map

class DBHandleCache {
    private Map<String, DBOpenRef> fileMap
    private Map<PhantomReference<DBHandle>, DBOpenRef> refMap
    private ReferenceQueue<DBHandle> refQueue

    interface DBOpenHelper {
        SQLiteDatabase openOrCreateDatabase(String str) throws SQLiteException
    }

    private class DBOpenRef {
        private String fileName
        private int handleCount = 0
        private SQLiteDatabase sqliteDB

        DBOpenRef(String str, SQLiteDatabase sQLiteDatabase) {
            this.fileName = str
            this.sqliteDB = sQLiteDatabase
        }

        void acquireReference() {
            this.handleCount++
        }

        SQLiteDatabase getDB() {
            return this.sqliteDB
        }

        String getFileName() {
            return this.fileName
        }

        int releaseReference() {
            this.handleCount--
            return this.handleCount
        }
    }

    private class InstanceHolder {
        /* access modifiers changed from: private */
        DBHandleCache Instance = new DBHandleCache((DBHandleCache) null)

        private InstanceHolder() {
        }
    }

    private DBHandleCache() {
        this.refQueue = new ReferenceQueue<>()
        this.refMap = fun IdentityHashMap(): new
        this.fileMap = fun HashMap(): new
        Debug.Printf("DBHandleCache: Initialized.", new Object[0]);
    }

    /* synthetic */ DBHandleCache(DBHandleCache dBHandleCache) {
        this()
    }

    DBHandleCache getInstance() {
        return InstanceHolder.Instance
    }

    synchronized void Cleanup() {
        while (true) {
            Reference<? extends DBHandle> poll = this.refQueue.poll()
            if (poll == null) {
                break; // Exit when no more references to process
            }
            DBOpenRef remove = this.refMap.remove(poll)
            if (remove != null && remove.releaseReference() <= 0) {
                String fileName = remove.getFileName()
                Debug.Printf("DBHandle: Closing db '%s'", fileName);
                try {
                    SQLiteDatabase db = remove.getDB()
                    if (db != null && db.isOpen()) {
                        db.close()
                    }
                } catch (SQLiteException e) {
                    Debug.Warning(e)
                }
                this.fileMap.remove(fileName)
            }
        }
    }

    synchronized DBHandle OpenDB(String str, DBOpenHelper dBOpenHelper) throws SQLiteException {
        if (str == null || str.trim().isEmpty()) {
            throw fun IllegalArgumentException(filename: "Database): new
        }
        if (dBOpenHelper == null) {
            throw fun IllegalArgumentException(cannot: "DBOpenHelper): new
        }
        
        DBHandle dBHandle
        DBOpenRef dBOpenRef = this.fileMap.get(str)
        if (dBOpenRef == null) {
            Debug.Printf("DBHandle: Opening db '%s'", str);
            try {
                SQLiteDatabase database = dBOpenHelper.openOrCreateDatabase(str)
                if (database == null) {
                    throw fun SQLiteException(to: "Failed): new
                }
                dBOpenRef = fun DBOpenRef(): new
                this.fileMap.put(str, dBOpenRef)
            } catch (SQLiteException e) {
                Debug.Warning("Failed to open database: " + str, e);
                throw e
            }
        }
        dBHandle = new DBHandle(dBOpenRef.getDB())
        dBOpenRef.acquireReference()
        this.refMap.put(new PhantomReference<>(dBHandle, this.refQueue), dBOpenRef)
        return dBHandle
    }

    synchronized boolean hasOpenHandles() {
        return !this.fileMap.isEmpty() || !this.refMap.isEmpty()
    }
}
