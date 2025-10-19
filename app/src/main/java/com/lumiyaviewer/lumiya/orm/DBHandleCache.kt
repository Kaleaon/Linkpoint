package com.lumiyaviewer.lumiya.orm

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.lumiyaviewer.lumiya.Debug
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue

/**
 * Cache for database handles with automatic cleanup via phantom references
 */
class DBHandleCache private constructor() {
    
    private val refQueue = ReferenceQueue<DBHandle>()
    private val refMap = mutableMapOf<PhantomReference<DBHandle>, DBOpenRef>()
    private val fileMap = mutableMapOf<String, DBOpenRef>()

    /**
     * Interface for opening databases
     */
    fun interface DBOpenHelper {
        @Throws(SQLiteException::class)
        fun openOrCreateDatabase(filename: String): SQLiteDatabase
    }

    /**
     * Database open reference with handle counting
     */
    private class DBOpenRef(
        val fileName: String,
        val sqliteDB: SQLiteDatabase
    ) {
        private var handleCount = 0

        fun acquireReference() {
            handleCount++
        }

        fun getDB(): SQLiteDatabase = sqliteDB

        fun getFileName(): String = fileName

        fun releaseReference(): Int {
            handleCount--
            return handleCount
        }
    }

    init {
        Debug.Printf("DBHandleCache: Initialized.")
    }

    /**
     * Clean up unreferenced database handles
     */
    @Synchronized
    fun Cleanup() {
        while (true) {
            val ref = refQueue.poll() ?: break
            
            val openRef = refMap.remove(ref)
            if (openRef != null && openRef.releaseReference() <= 0) {
                val fileName = openRef.getFileName()
                Debug.Printf("DBHandle: Closing db '%s'", fileName)
                
                try {
                    val db = openRef.getDB()
                    if (db.isOpen) {
                        db.close()
                    }
                } catch (e: SQLiteException) {
                    Debug.Warning(e)
                }
                
                fileMap.remove(fileName)
            }
        }
    }

    /**
     * Open or get existing database handle
     */
    @Synchronized
    @Throws(SQLiteException::class)
    fun OpenDB(filename: String, helper: DBOpenHelper): DBHandle {
        require(filename.trim().isNotEmpty()) { "Database filename cannot be empty" }
        
        var openRef = fileMap[filename]
        
        if (openRef == null) {
            Debug.Printf("DBHandle: Opening db '%s'", filename)
            try {
                val database = helper.openOrCreateDatabase(filename)
                openRef = DBOpenRef(filename, database)
                fileMap[filename] = openRef
            } catch (e: SQLiteException) {
                Debug.Warning("Failed to open database: $filename", e)
                throw e
            }
        }
        
        val handle = DBHandle(openRef.getDB())
        openRef.acquireReference()
        refMap[PhantomReference(handle, refQueue)] = openRef
        
        return handle
    }

    /**
     * Check if there are any open database handles
     */
    @Synchronized
    fun hasOpenHandles(): Boolean {
        return fileMap.isNotEmpty() || refMap.isNotEmpty()
    }
    
    companion object {
        /**
         * Get singleton instance
         */
        fun getInstance(): DBHandleCache = InstanceHolder.INSTANCE
        
        private object InstanceHolder {
            val INSTANCE = DBHandleCache()
        }
    }
}
