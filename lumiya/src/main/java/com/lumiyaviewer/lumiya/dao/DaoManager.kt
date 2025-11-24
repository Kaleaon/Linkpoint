package com.lumiyaviewer.lumiya.dao

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.LumiyaApp
import java.io.File
import java.util.HashMap
import java.util.UUID

object DaoManager {
    private val lock = Any()
    private val userDaoSessions = HashMap<UUID, DaoSession>()

    fun getUserDaoSession(uuid: UUID?): DaoSession? {
        if (uuid == null) {
            return null
        }
        
        synchronized(lock) {
            var daoSession = userDaoSessions[uuid]
            if (daoSession == null) {
                try {
                    val cacheDir = GlobalOptions.getInstance().getCacheDir("database")
                        ?: throw IllegalStateException("Cache directory is null")
                    
                    val dbFile = File(cacheDir, "userdb-$uuid.db")
                    val dbHelper = DBOpenHelper(LumiyaApp.getContext(), dbFile.absolutePath, null)
                    
                    daoSession = DaoMaster(dbHelper.writableDatabase).newSession()
                    userDaoSessions[uuid] = daoSession
                } catch (e: Exception) {
                    android.util.Log.e("DaoManager", "Failed to create DaoSession for user $uuid", e)
                    return null
                }
            }
            return daoSession
        }
    }
    
    fun closeDaoSession(uuid: UUID?) {
        if (uuid == null) {
            return
        }
        
        synchronized(lock) {
            val daoSession = userDaoSessions.remove(uuid)
            if (daoSession != null) {
                try {
                    daoSession.database.close()
                } catch (e: Exception) {
                    android.util.Log.w("DaoManager", "Error closing database for user $uuid", e)
                }
            }
        }
    }
    
    fun closeAllSessions() {
        synchronized(lock) {
            for ((uuid, daoSession) in userDaoSessions) {
                try {
                    daoSession.database.close()
                } catch (e: Exception) {
                    android.util.Log.w("DaoManager", "Error closing database for user $uuid", e)
                }
            }
            userDaoSessions.clear()
        }
    }
}
