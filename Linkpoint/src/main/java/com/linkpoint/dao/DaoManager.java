package com.linkpoint.dao;

import com.linkpoint.GlobalOptions;
import com.linkpoint.LinkpointApp;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class DaoManager {
    private static final Object lock = new Object();
    private static final Map<UUID, DaoSession> userDaoSessions = new HashMap();

    @Nullable
    public static DaoSession getUserDaoSession(@Nullable UUID uuid) {
        if (uuid == null) {
            return null;
        }
        
        synchronized (lock) {
            DaoSession daoSession = userDaoSessions.get(uuid);
            if (daoSession == null) {
                try {
                    File cacheDir = GlobalOptions.getInstance().getCacheDir("database");
                    if (cacheDir == null) {
                        throw new IllegalStateException("Cache directory is null");
                    }
                    
                    File dbFile = new File(cacheDir, "userdb-" + uuid.toString() + ".db");
                    DBOpenHelper dbHelper = new DBOpenHelper(LinkpointApp.getContext(), dbFile.getAbsolutePath(), null);
                    
                    daoSession = new DaoMaster(dbHelper.getWritableDatabase()).newSession();
                    userDaoSessions.put(uuid, daoSession);
                } catch (Exception e) {
                    // Log error but don't throw, return null to indicate failure
                    android.util.Log.e("DaoManager", "Failed to create DaoSession for user " + uuid, e);
                    return null;
                }
            }
            return daoSession;
        }
    }
    
    /**
     * Closes and removes the DaoSession for the given user UUID
     */
    public static void closeDaoSession(@Nullable UUID uuid) {
        if (uuid == null) {
            return;
        }
        
        synchronized (lock) {
            DaoSession daoSession = userDaoSessions.remove(uuid);
            if (daoSession != null) {
                try {
                    daoSession.getDatabase().close();
                } catch (Exception e) {
                    android.util.Log.w("DaoManager", "Error closing database for user " + uuid, e);
                }
            }
        }
    }
    
    /**
     * Closes all open DaoSessions - should be called on app shutdown
     */
    public static void closeAllSessions() {
        synchronized (lock) {
            for (Map.Entry<UUID, DaoSession> entry : userDaoSessions.entrySet()) {
                try {
                    entry.getValue().getDatabase().close();
                } catch (Exception e) {
                    android.util.Log.w("DaoManager", "Error closing database for user " + entry.getKey(), e);
                }
            }
            userDaoSessions.clear();
        }
    }
}
