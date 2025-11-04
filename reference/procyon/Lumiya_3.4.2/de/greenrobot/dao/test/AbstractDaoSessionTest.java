// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.test;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.AbstractDaoMaster;

public abstract class AbstractDaoSessionTest<T extends AbstractDaoMaster, S extends AbstractDaoSession> extends DbTest
{
    protected T daoMaster;
    private final Class<T> daoMasterClass;
    protected S daoSession;
    
    public AbstractDaoSessionTest(final Class<T> clazz) {
        this(clazz, true);
    }
    
    public AbstractDaoSessionTest(final Class<T> daoMasterClass, final boolean b) {
        super(b);
        this.daoMasterClass = daoMasterClass;
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            this.daoMaster = this.daoMasterClass.getConstructor(SQLiteDatabase.class).newInstance(this.db);
            this.daoMasterClass.getMethod("createAllTables", SQLiteDatabase.class, Boolean.TYPE).invoke(null, this.db, false);
            this.daoSession = (S)this.daoMaster.newSession();
        }
        catch (final Exception cause) {
            throw new RuntimeException("Could not prepare DAO session test", cause);
        }
    }
}
