package de.greenrobot.dao;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class AbstractDaoMaster {
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap = new HashMap();
    protected final SQLiteDatabase db;
    protected final int schemaVersion;

    public AbstractDaoMaster(SQLiteDatabase sQLiteDatabase, int i) {
        this.db = sQLiteDatabase;
        this.schemaVersion = i;
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public abstract AbstractDaoSession newSession();

    public abstract AbstractDaoSession newSession(IdentityScopeType identityScopeType);

    /* JADX INFO: Access modifiers changed from: protected */
    public void registerDaoClass(Class<? extends AbstractDao<?, ?>> cls) {
        this.daoConfigMap.put(cls, new DaoConfig(this.db, cls));
    }
}
