package de.greenrobot.dao.query;

import android.os.Process;
import android.util.SparseArray;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.AbstractQuery;
import java.lang.ref.WeakReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AbstractQueryData<T, Q extends AbstractQuery<T>> {
    final AbstractDao<T, ?> dao;
    final String[] initialValues;
    final SparseArray<WeakReference<Q>> queriesForThreads = new SparseArray<>();
    final String sql;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractQueryData(AbstractDao<T, ?> abstractDao, String str, String[] strArr) {
        this.dao = abstractDao;
        this.sql = str;
        this.initialValues = strArr;
    }

    protected abstract Q createQuery();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Q forCurrentThread() {
        Q q;
        int myTid = Process.myTid();
        synchronized (this.queriesForThreads) {
            WeakReference<Q> weakReference = this.queriesForThreads.get(myTid);
            q = weakReference == null ? null : weakReference.get();
            if (q != null) {
                System.arraycopy(this.initialValues, 0, q.parameters, 0, this.initialValues.length);
            } else {
                gc();
                q = createQuery();
                this.queriesForThreads.put(myTid, new WeakReference<>(q));
            }
        }
        return q;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Q forCurrentThread(Q q) {
        if (Thread.currentThread() != q.ownerThread) {
            return forCurrentThread();
        }
        System.arraycopy(this.initialValues, 0, q.parameters, 0, this.initialValues.length);
        return q;
    }

    void gc() {
        synchronized (this.queriesForThreads) {
            for (int size = this.queriesForThreads.size() - 1; size >= 0; size--) {
                if (this.queriesForThreads.valueAt(size).get() == null) {
                    this.queriesForThreads.remove(this.queriesForThreads.keyAt(size));
                }
            }
        }
    }
}
