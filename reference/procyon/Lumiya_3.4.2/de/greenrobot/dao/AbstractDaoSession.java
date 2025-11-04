// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao;

import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Map;
import android.database.sqlite.SQLiteDatabase;

public class AbstractDaoSession
{
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;
    
    public AbstractDaoSession(final SQLiteDatabase db) {
        this.db = db;
        this.entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();
    }
    
    public <V> V callInTx(final Callable<V> callable) throws Exception {
        this.db.beginTransaction();
        try {
            final V call = callable.call();
            this.db.setTransactionSuccessful();
            return call;
        }
        finally {
            this.db.endTransaction();
        }
    }
    
    public <V> V callInTxNoException(final Callable<V> callable) {
        this.db.beginTransaction();
        try {
            final V call = callable.call();
            this.db.setTransactionSuccessful();
            return call;
        }
        catch (final Exception ex) {
            throw new DaoException("Callable failed", ex);
        }
        finally {
            this.db.endTransaction();
        }
    }
    
    public <T> void delete(final T t) {
        this.getDao(t.getClass()).delete(t);
    }
    
    public <T> void deleteAll(final Class<T> clazz) {
        this.getDao(clazz).deleteAll();
    }
    
    public AbstractDao<?, ?> getDao(final Class<?> obj) {
        final AbstractDao abstractDao = this.entityToDao.get(obj);
        if (abstractDao != null) {
            return abstractDao;
        }
        throw new DaoException("No DAO registered for " + obj);
    }
    
    public SQLiteDatabase getDatabase() {
        return this.db;
    }
    
    public <T> long insert(final T t) {
        return this.getDao(t.getClass()).insert(t);
    }
    
    public <T> long insertOrReplace(final T t) {
        return this.getDao(t.getClass()).insertOrReplace(t);
    }
    
    public <T, K> T load(final Class<T> clazz, final K k) {
        return (T)this.getDao(clazz).load(k);
    }
    
    public <T, K> List<T> loadAll(final Class<T> clazz) {
        return (List<T>)this.getDao(clazz).loadAll();
    }
    
    public <T> QueryBuilder<T> queryBuilder(final Class<T> clazz) {
        return (QueryBuilder<T>)this.getDao(clazz).queryBuilder();
    }
    
    public <T, K> List<T> queryRaw(final Class<T> clazz, final String s, final String... array) {
        return (List<T>)this.getDao(clazz).queryRaw(s, array);
    }
    
    public <T> void refresh(final T t) {
        this.getDao(t.getClass()).refresh(t);
    }
    
    protected <T> void registerDao(final Class<T> clazz, final AbstractDao<T, ?> abstractDao) {
        this.entityToDao.put(clazz, abstractDao);
    }
    
    public void runInTx(final Runnable runnable) {
        this.db.beginTransaction();
        try {
            runnable.run();
            this.db.setTransactionSuccessful();
        }
        finally {
            this.db.endTransaction();
        }
    }
    
    public AsyncSession startAsyncSession() {
        return new AsyncSession(this);
    }
    
    public <T> void update(final T t) {
        this.getDao(t.getClass()).update(t);
    }
}
