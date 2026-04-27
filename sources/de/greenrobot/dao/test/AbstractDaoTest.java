package de.greenrobot.dao.test;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.InternalUnitTestDaoAccess;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.identityscope.IdentityScope;

public abstract class AbstractDaoTest<D extends AbstractDao<T, K>, T, K> extends DbTest {
    protected D dao;
    protected InternalUnitTestDaoAccess<T, K> daoAccess;
    protected final Class<D> daoClass;
    protected IdentityScope<K, T> identityScopeForDao;
    protected Property pkColumn;

    public AbstractDaoTest(Class<D> cls) {
        this(cls, true);
    }

    public AbstractDaoTest(Class<D> cls, boolean z) {
        super(z);
        this.daoClass = cls;
    }

    /* access modifiers changed from: protected */
    public void clearIdentityScopeIfAny() {
        if (this.identityScopeForDao == null) {
            DaoLog.d("No identity scope to clear");
            return;
        }
        this.identityScopeForDao.clear();
        DaoLog.d("Identity scope cleared");
    }

    /* access modifiers changed from: protected */
    public void logTableDump() {
        logTableDump(this.dao.getTablename());
    }

    public void setIdentityScopeBeforeSetUp(IdentityScope<K, T> identityScope) {
        this.identityScopeForDao = identityScope;
    }

    /* access modifiers changed from: protected */
    public void setUp() throws Exception {
        super.setUp();
        try {
            setUpTableForDao();
            this.daoAccess = new InternalUnitTestDaoAccess<>(this.db, this.daoClass, this.identityScopeForDao);
            this.dao = this.daoAccess.getDao();
        } catch (Exception e) {
            throw new RuntimeException("Could not prepare DAO Test", e);
        }
    }

    /* access modifiers changed from: protected */
    public void setUpTableForDao() throws Exception {
        try {
            this.daoClass.getMethod("createTable", new Class[]{SQLiteDatabase.class, Boolean.TYPE}).invoke((Object) null, new Object[]{this.db, false});
        } catch (NoSuchMethodException e) {
            DaoLog.i("No createTable method");
        }
    }
}
