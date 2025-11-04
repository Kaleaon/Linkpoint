// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.async;

import de.greenrobot.dao.query.Query;
import java.util.concurrent.Callable;
import de.greenrobot.dao.AbstractDao;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDaoSession;

public class AsyncSession
{
    private final AbstractDaoSession daoSession;
    private final AsyncOperationExecutor executor;
    private int sessionFlags;
    
    public AsyncSession(final AbstractDaoSession daoSession) {
        this.daoSession = daoSession;
        this.executor = new AsyncOperationExecutor();
    }
    
    private <E> AsyncOperation enqueEntityOperation(final AsyncOperation.OperationType operationType, final Class<E> clazz, final Object o, final int n) {
        final AsyncOperation asyncOperation = new AsyncOperation(operationType, this.daoSession.getDao(clazz), null, o, n | this.sessionFlags);
        this.executor.enqueue(asyncOperation);
        return asyncOperation;
    }
    
    private AsyncOperation enqueueDatabaseOperation(final AsyncOperation.OperationType operationType, final Object o, final int n) {
        final AsyncOperation asyncOperation = new AsyncOperation(operationType, null, this.daoSession.getDatabase(), o, n | this.sessionFlags);
        this.executor.enqueue(asyncOperation);
        return asyncOperation;
    }
    
    private AsyncOperation enqueueEntityOperation(final AsyncOperation.OperationType operationType, final Object o, final int n) {
        return this.enqueEntityOperation(operationType, o.getClass(), o, n);
    }
    
    public AsyncOperation callInTx(final Callable<?> callable) {
        return this.callInTx(callable, 0);
    }
    
    public AsyncOperation callInTx(final Callable<?> callable, final int n) {
        return this.enqueueDatabaseOperation(AsyncOperation.OperationType.TransactionCallable, callable, n);
    }
    
    public AsyncOperation count(final Class<?> clazz) {
        return this.count(clazz, 0);
    }
    
    public AsyncOperation count(final Class<?> clazz, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.Count, clazz, null, n);
    }
    
    public AsyncOperation delete(final Object o) {
        return this.delete(o, 0);
    }
    
    public AsyncOperation delete(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.Delete, o, n);
    }
    
    public <E> AsyncOperation deleteAll(final Class<E> clazz) {
        return this.deleteAll(clazz, 0);
    }
    
    public <E> AsyncOperation deleteAll(final Class<E> clazz, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.DeleteAll, clazz, null, n);
    }
    
    public AsyncOperation deleteByKey(final Object o) {
        return this.deleteByKey(o, 0);
    }
    
    public AsyncOperation deleteByKey(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.DeleteByKey, o, n);
    }
    
    public <E> AsyncOperation deleteInTx(final Class<E> clazz, final int n, final E... array) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.DeleteInTxArray, clazz, array, n);
    }
    
    public <E> AsyncOperation deleteInTx(final Class<E> clazz, final Iterable<E> iterable) {
        return this.deleteInTx(clazz, iterable, 0);
    }
    
    public <E> AsyncOperation deleteInTx(final Class<E> clazz, final Iterable<E> iterable, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.DeleteInTxIterable, clazz, iterable, n);
    }
    
    public <E> AsyncOperation deleteInTx(final Class<E> clazz, final E... array) {
        return this.deleteInTx(clazz, 0, array);
    }
    
    public AsyncOperationListener getListener() {
        return this.executor.getListener();
    }
    
    public AsyncOperationListener getListenerMainThread() {
        return this.executor.getListenerMainThread();
    }
    
    public int getMaxOperationCountToMerge() {
        return this.executor.getMaxOperationCountToMerge();
    }
    
    public int getSessionFlags() {
        return this.sessionFlags;
    }
    
    public int getWaitForMergeMillis() {
        return this.executor.getWaitForMergeMillis();
    }
    
    public AsyncOperation insert(final Object o) {
        return this.insert(o, 0);
    }
    
    public AsyncOperation insert(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.Insert, o, n);
    }
    
    public <E> AsyncOperation insertInTx(final Class<E> clazz, final int n, final E... array) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.InsertInTxArray, clazz, array, n);
    }
    
    public <E> AsyncOperation insertInTx(final Class<E> clazz, final Iterable<E> iterable) {
        return this.insertInTx(clazz, iterable, 0);
    }
    
    public <E> AsyncOperation insertInTx(final Class<E> clazz, final Iterable<E> iterable, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.InsertInTxIterable, clazz, iterable, n);
    }
    
    public <E> AsyncOperation insertInTx(final Class<E> clazz, final E... array) {
        return this.insertInTx(clazz, 0, array);
    }
    
    public AsyncOperation insertOrReplace(final Object o) {
        return this.insertOrReplace(o, 0);
    }
    
    public AsyncOperation insertOrReplace(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.InsertOrReplace, o, n);
    }
    
    public <E> AsyncOperation insertOrReplaceInTx(final Class<E> clazz, final int n, final E... array) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.InsertOrReplaceInTxArray, clazz, array, n);
    }
    
    public <E> AsyncOperation insertOrReplaceInTx(final Class<E> clazz, final Iterable<E> iterable) {
        return this.insertOrReplaceInTx(clazz, iterable, 0);
    }
    
    public <E> AsyncOperation insertOrReplaceInTx(final Class<E> clazz, final Iterable<E> iterable, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.InsertOrReplaceInTxIterable, clazz, iterable, n);
    }
    
    public <E> AsyncOperation insertOrReplaceInTx(final Class<E> clazz, final E... array) {
        return this.insertOrReplaceInTx(clazz, 0, array);
    }
    
    public boolean isCompleted() {
        return this.executor.isCompleted();
    }
    
    public AsyncOperation load(final Class<?> clazz, final Object o) {
        return this.load(clazz, o, 0);
    }
    
    public AsyncOperation load(final Class<?> clazz, final Object o, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.Load, clazz, o, n);
    }
    
    public AsyncOperation loadAll(final Class<?> clazz) {
        return this.loadAll(clazz, 0);
    }
    
    public AsyncOperation loadAll(final Class<?> clazz, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.LoadAll, clazz, null, n);
    }
    
    public AsyncOperation queryList(final Query<?> query) {
        return this.queryList(query, 0);
    }
    
    public AsyncOperation queryList(final Query<?> query, final int n) {
        return this.enqueueDatabaseOperation(AsyncOperation.OperationType.QueryList, query, n);
    }
    
    public AsyncOperation queryUnique(final Query<?> query) {
        return this.queryUnique(query, 0);
    }
    
    public AsyncOperation queryUnique(final Query<?> query, final int n) {
        return this.enqueueDatabaseOperation(AsyncOperation.OperationType.QueryUnique, query, n);
    }
    
    public AsyncOperation refresh(final Object o) {
        return this.refresh(o, 0);
    }
    
    public AsyncOperation refresh(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.Refresh, o, n);
    }
    
    public AsyncOperation runInTx(final Runnable runnable) {
        return this.runInTx(runnable, 0);
    }
    
    public AsyncOperation runInTx(final Runnable runnable, final int n) {
        return this.enqueueDatabaseOperation(AsyncOperation.OperationType.TransactionRunnable, runnable, n);
    }
    
    public void setListener(final AsyncOperationListener listener) {
        this.executor.setListener(listener);
    }
    
    public void setListenerMainThread(final AsyncOperationListener listenerMainThread) {
        this.executor.setListenerMainThread(listenerMainThread);
    }
    
    public void setMaxOperationCountToMerge(final int maxOperationCountToMerge) {
        this.executor.setMaxOperationCountToMerge(maxOperationCountToMerge);
    }
    
    public void setSessionFlags(final int sessionFlags) {
        this.sessionFlags = sessionFlags;
    }
    
    public void setWaitForMergeMillis(final int waitForMergeMillis) {
        this.executor.setWaitForMergeMillis(waitForMergeMillis);
    }
    
    public AsyncOperation update(final Object o) {
        return this.update(o, 0);
    }
    
    public AsyncOperation update(final Object o, final int n) {
        return this.enqueueEntityOperation(AsyncOperation.OperationType.Update, o, n);
    }
    
    public <E> AsyncOperation updateInTx(final Class<E> clazz, final int n, final E... array) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.UpdateInTxArray, clazz, array, n);
    }
    
    public <E> AsyncOperation updateInTx(final Class<E> clazz, final Iterable<E> iterable) {
        return this.updateInTx(clazz, iterable, 0);
    }
    
    public <E> AsyncOperation updateInTx(final Class<E> clazz, final Iterable<E> iterable, final int n) {
        return this.enqueEntityOperation(AsyncOperation.OperationType.UpdateInTxIterable, clazz, iterable, n);
    }
    
    public <E> AsyncOperation updateInTx(final Class<E> clazz, final E... array) {
        return this.updateInTx(clazz, 0, array);
    }
    
    public void waitForCompletion() {
        this.executor.waitForCompletion();
    }
    
    public boolean waitForCompletion(final int n) {
        return this.executor.waitForCompletion(n);
    }
}
