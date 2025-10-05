package com.linkpoint.orm

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.linkpoint.Debug
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.util.HashMap
import java.util.IdentityHashMap
import java.util.Map

class DBHandleCache {
    private val Map<String, DBOpenRef> fileMap
    private val Map<PhantomReference<DBHandle>, DBOpenRef> refMap
    private val ReferenceQueue<DBHandle> refQueue

    interface DBOpenHelper {
        SQLiteDatabase openOrCreateDatabase(String str) throws SQLiteException
    }

    @JvmStatic
private class DBOpenRef {
        private val String fileName
        private Int handleCount = 0
        private val SQLiteDatabase sqliteDB

        public DBOpenRef(String str, SQLiteDatabase sQLiteDatabase) {
            this.fileName = str
            this.sqliteDB = sQLiteDatabase
        }

        val Unit acquireReference() {
            this.handleCount++
        }

        val SQLiteDatabase getDB() {
            return this.sqliteDB
        }

        val String getFileName() {
            return this.fileName
        }

        val Int releaseReference() {
            this.handleCount--
            return this.handleCount
        }
    }

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val DBHandleCache Instance = DBHandleCache((DBHandleCache) null)

        private InstanceHolder() {
        }
    }

    private DBHandleCache() {
        this.refQueue = ReferenceQueue<>()
        this.refMap = IdentityHashMap()
        this.fileMap = HashMap()
        Debug.Printf("DBHandleCache: Initialized.", Object[0])
    }

    /* synthetic */ DBHandleCache(DBHandleCache dBHandleCache) {
        this()
    }

    @JvmStatic
    DBHandleCache getInstance() {
        return InstanceHolder.Instance
    }

    public synchronized Unit Cleanup() {
        while (true) {
            Reference<? : DBHandle> poll = this.refQueue.poll()
            if (poll == null) {
                break; // Exit when no more references to process
            }
            DBOpenRef remove = this.refMap.remove(poll)
            if (remove != null && remove.releaseReference() <= 0) {
                String fileName = remove.getFileName()
                Debug.Printf("DBHandle: Closing db '%s'", fileName)
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

    public synchronized DBHandle OpenDB(String str, DBOpenHelper dBOpenHelper) throws SQLiteException {
        if (str == null || str.trim().isEmpty()) {
            throw IllegalArgumentException("Database filename cannot be null or empty")
        }
        if (dBOpenHelper == null) {
            throw IllegalArgumentException("DBOpenHelper cannot be null")
        }
        
        DBHandle dBHandle
        DBOpenRef dBOpenRef = this.fileMap.get(str)
        if (dBOpenRef == null) {
            Debug.Printf("DBHandle: Opening db '%s'", str)
            try {
                SQLiteDatabase database = dBOpenHelper.openOrCreateDatabase(str)
                if (database == null) {
                    throw SQLiteException("Failed to open or create database: " + str)
                }
                dBOpenRef = DBOpenRef(str, database)
                this.fileMap.put(str, dBOpenRef)
            } catch (SQLiteException e) {
                Debug.Warning("Failed to open database: " + str, e)
                throw e
            }
        }
        dBHandle = DBHandle(dBOpenRef.getDB())
        dBOpenRef.acquireReference()
        this.refMap.put(PhantomReference<>(dBHandle, this.refQueue), dBOpenRef)
        return dBHandle
    }

    public synchronized Boolean hasOpenHandles() {
        return !this.fileMap.isEmpty() || !this.refMap.isEmpty()
    }
}
