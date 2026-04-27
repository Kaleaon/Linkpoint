// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import java.util.HashMap;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.Map;

public abstract class AbstractDaoMaster
{
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;
    protected final SQLiteDatabase db;
    protected final int schemaVersion;
    
    public AbstractDaoMaster(final SQLiteDatabase db, final int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;
        this.daoConfigMap = new HashMap<Class<? extends AbstractDao<?, ?>>, DaoConfig>();
    }
    
    public SQLiteDatabase getDatabase() {
        return this.db;
    }
    
    public int getSchemaVersion() {
        return this.schemaVersion;
    }
    
    public abstract AbstractDaoSession newSession();
    
    public abstract AbstractDaoSession newSession(final IdentityScopeType p0);
    
    protected void registerDaoClass(final Class<? extends AbstractDao<?, ?>> clazz) {
        this.daoConfigMap.put(clazz, new DaoConfig(this.db, clazz));
    }
}
