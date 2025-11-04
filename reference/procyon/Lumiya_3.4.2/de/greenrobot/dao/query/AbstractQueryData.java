// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.query;

import android.os.Process;
import java.lang.ref.WeakReference;
import android.util.SparseArray;
import de.greenrobot.dao.AbstractDao;

abstract class AbstractQueryData<T, Q extends AbstractQuery<T>>
{
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Q>> queriesForThreads;
    final String sql;
    
    AbstractQueryData(final AbstractDao<T, ?> dao, final String sql, final String[] initialValues) {
        this.dao = dao;
        this.sql = sql;
        this.initialValues = initialValues;
        this.queriesForThreads = (SparseArray<WeakReference<Q>>)new SparseArray();
    }
    
    protected abstract Q createQuery();
    
    Q forCurrentThread() {
        final int myTid = Process.myTid();
        synchronized (this.queriesForThreads) {
            final WeakReference weakReference = (WeakReference)this.queriesForThreads.get(myTid);
            AbstractQuery<T> query;
            if (weakReference == null) {
                query = null;
            }
            else {
                query = (AbstractQuery)weakReference.get();
            }
            if (query != null) {
                System.arraycopy(this.initialValues, 0, query.parameters, 0, this.initialValues.length);
            }
            else {
                this.gc();
                query = this.createQuery();
                this.queriesForThreads.put(myTid, (Object)new WeakReference(query));
            }
            return (Q)query;
        }
    }
    
    Q forCurrentThread(final Q q) {
        if (Thread.currentThread() != q.ownerThread) {
            return this.forCurrentThread();
        }
        System.arraycopy(this.initialValues, 0, q.parameters, 0, this.initialValues.length);
        return q;
    }
    
    void gc() {
        synchronized (this.queriesForThreads) {
            for (int i = this.queriesForThreads.size() - 1; i >= 0; --i) {
                if (((WeakReference)this.queriesForThreads.valueAt(i)).get() == null) {
                    this.queriesForThreads.remove(this.queriesForThreads.keyAt(i));
                }
            }
        }
    }
}
