// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import android.database.Cursor;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.identityscope.IdentityScope;
import android.database.sqlite.SQLiteDatabase;

public class InternalUnitTestDaoAccess<T, K>
{
    private final AbstractDao<T, K> dao;
    
    public InternalUnitTestDaoAccess(final SQLiteDatabase sqLiteDatabase, final Class<AbstractDao<T, K>> clazz, final IdentityScope<?, ?> identityScope) throws Exception {
        final DaoConfig daoConfig = new DaoConfig(sqLiteDatabase, clazz);
        daoConfig.setIdentityScope(identityScope);
        this.dao = clazz.getConstructor(DaoConfig.class).newInstance(daoConfig);
    }
    
    public AbstractDao<T, K> getDao() {
        return this.dao;
    }
    
    public K getKey(final T t) {
        return this.dao.getKey(t);
    }
    
    public Property[] getProperties() {
        return this.dao.getProperties();
    }
    
    public boolean isEntityUpdateable() {
        return this.dao.isEntityUpdateable();
    }
    
    public T readEntity(final Cursor cursor, final int n) {
        return this.dao.readEntity(cursor, n);
    }
    
    public K readKey(final Cursor cursor, final int n) {
        return this.dao.readKey(cursor, n);
    }
}
