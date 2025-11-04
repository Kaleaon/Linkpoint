// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.InternalQueryDaoAccess;
import de.greenrobot.dao.AbstractDao;

abstract class AbstractQuery<T>
{
    protected final AbstractDao<T, ?> dao;
    protected final InternalQueryDaoAccess<T> daoAccess;
    protected final Thread ownerThread;
    protected final String[] parameters;
    protected final String sql;
    
    protected AbstractQuery(final AbstractDao<T, ?> dao, final String sql, final String[] parameters) {
        this.dao = dao;
        this.daoAccess = new InternalQueryDaoAccess<T>(dao);
        this.sql = sql;
        this.parameters = parameters;
        this.ownerThread = Thread.currentThread();
    }
    
    protected static String[] toStringArray(final Object[] array) {
        final int length = array.length;
        final String[] array2 = new String[length];
        for (int i = 0; i < length; ++i) {
            final Object o = array[i];
            if (o == null) {
                array2[i] = null;
            }
            else {
                array2[i] = o.toString();
            }
        }
        return array2;
    }
    
    protected void checkThread() {
        if (Thread.currentThread() == this.ownerThread) {
            return;
        }
        throw new DaoException("Method may be called only in owner thread, use forCurrentThread to get an instance for this thread");
    }
    
    public void setParameter(final int n, final Object o) {
        this.checkThread();
        if (o == null) {
            this.parameters[n] = null;
        }
        else {
            this.parameters[n] = o.toString();
        }
    }
}
