// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;
import de.greenrobot.dao.InternalUnitTestDaoAccess;
import de.greenrobot.dao.AbstractDao;

public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends DbTest
{
    protected D dao;
    protected InternalUnitTestDaoAccess<T, K> daoAccess;
    protected final Class<D> daoClass;
    protected IdentityScope<K, T> identityScopeForDao;
    protected Property pkColumn;
    
    public AbstractDaoTest(final Class<D> clazz) {
        this(clazz, true);
    }
    
    public AbstractDaoTest(final Class<D> daoClass, final boolean b) {
        super(b);
        this.daoClass = daoClass;
    }
    
    protected void clearIdentityScopeIfAny() {
        if (this.identityScopeForDao == null) {
            DaoLog.d("No identity scope to clear");
        }
        else {
            this.identityScopeForDao.clear();
            DaoLog.d("Identity scope cleared");
        }
    }
    
    protected void logTableDump() {
        this.logTableDump(this.dao.getTablename());
    }
    
    public void setIdentityScopeBeforeSetUp(final IdentityScope<K, T> identityScopeForDao) {
        this.identityScopeForDao = identityScopeForDao;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            this.setUpTableForDao();
            this.daoAccess = new InternalUnitTestDaoAccess<T, K>(this.db, (Class<AbstractDao<T, K>>)this.daoClass, this.identityScopeForDao);
            this.dao = (D)this.daoAccess.getDao();
        }
        catch (final Exception cause) {
            throw new RuntimeException("Could not prepare DAO Test", cause);
        }
    }
    
    protected void setUpTableForDao() throws Exception {
        try {
            this.daoClass.getMethod("createTable", SQLiteDatabase.class, Boolean.TYPE).invoke(null, this.db, false);
        }
        catch (final NoSuchMethodException ex) {
            DaoLog.i("No createTable method");
        }
    }
}
