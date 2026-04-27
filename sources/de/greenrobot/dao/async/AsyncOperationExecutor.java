package de.greenrobot.dao.async;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import de.greenrobot.dao.DaoException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

class AsyncOperationExecutor implements Runnable, Handler.Callback {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private volatile int maxOperationCountToMerge = 50;
    private final BlockingQueue<AsyncOperation> queue = new LinkedBlockingQueue();
    private volatile int waitForMergeMillis = 50;

    AsyncOperationExecutor() {
    }

    private void executeOperation(AsyncOperation operation) {
        operation.timeStarted = System.currentTimeMillis();
        try {
            switch (operation.type) {
                case Delete:
                    operation.dao.delete(operation.parameter);
                    break;
                case DeleteInTxIterable:
                    operation.dao.deleteInTx((Iterable<?>) operation.parameter);
                    break;
                case DeleteInTxArray:
                    operation.dao.deleteInTx((Object[]) operation.parameter);
                    break;
                case Insert:
                    operation.dao.insert(operation.parameter);
                    break;
                case InsertInTxIterable:
                    operation.dao.insertInTx((Iterable<?>) operation.parameter);
                    break;
                case InsertInTxArray:
                    operation.dao.insertInTx((Object[]) operation.parameter);
                    break;
                case InsertOrReplace:
                    operation.dao.insertOrReplace(operation.parameter);
                    break;
                case InsertOrReplaceInTxIterable:
                    operation.dao.insertOrReplaceInTx((Iterable<?>) operation.parameter);
                    break;
                case InsertOrReplaceInTxArray:
                    operation.dao.insertOrReplaceInTx((Object[]) operation.parameter);
                    break;
                case Update:
                    operation.dao.update(operation.parameter);
                    break;
                case UpdateInTxIterable:
                    operation.dao.updateInTx((Iterable<?>) operation.parameter);
                    break;
                case UpdateInTxArray:
                    operation.dao.updateInTx((Object[]) operation.parameter);
                    break;
                case TransactionRunnable:
                    executeTransactionRunnable(operation);
                    break;
                case TransactionCallable:
                    executeTransactionCallable(operation);
                    break;
                case QueryList:
                    operation.result = ((de.greenrobot.dao.query.Query<?>) operation.parameter).list();
                    break;
                case QueryUnique:
                    operation.result = ((de.greenrobot.dao.query.Query<?>) operation.parameter).unique();
                    break;
                case DeleteByKey:
                    operation.dao.deleteByKey(operation.parameter);
                    break;
                case DeleteAll:
                    operation.dao.deleteAll();
                    break;
                case Load:
                    operation.result = operation.dao.load(operation.parameter);
                    break;
                case LoadAll:
                    operation.result = operation.dao.loadAll();
                    break;
                case Count:
                    operation.result = Long.valueOf(operation.dao.count());
                    break;
                case Refresh:
                    operation.dao.refresh(operation.parameter);
                    break;
                default:
                    throw new DaoException("Unsupported operation: " + operation.type);
            }
        } catch (Throwable th) {
            operation.throwable = th;
        } finally {
            operation.timeCompleted = System.currentTimeMillis();
        }
    }

    private void executeOperationAndPostCompleted(AsyncOperation asyncOperation) {
        executeOperation(asyncOperation);
        handleOperationCompleted(asyncOperation);
    }

    private void executeTransactionCallable(AsyncOperation asyncOperation) throws Exception {
        SQLiteDatabase database = asyncOperation.getDatabase();
        database.beginTransaction();
        try {
            asyncOperation.result = ((Callable) asyncOperation.parameter).call();
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void executeTransactionRunnable(AsyncOperation asyncOperation) {
        SQLiteDatabase database = asyncOperation.getDatabase();
        database.beginTransaction();
        try {
            ((Runnable) asyncOperation.parameter).run();
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    private void handleOperationCompleted(AsyncOperation asyncOperation) {
        asyncOperation.setCompleted();
        AsyncOperationListener asyncOperationListener = this.listener;
        if (asyncOperationListener != null) {
            asyncOperationListener.onAsyncOperationCompleted(asyncOperation);
        }
        if (this.listenerMainThread != null) {
            if (this.handlerMainThread == null) {
                this.handlerMainThread = new Handler(Looper.getMainLooper(), this);
            }
            this.handlerMainThread.sendMessage(this.handlerMainThread.obtainMessage(1, asyncOperation));
        }
        synchronized (this) {
            this.countOperationsCompleted++;
            if (this.countOperationsCompleted == this.countOperationsEnqueued) {
                notifyAll();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005b, code lost:
        r5.setTransactionSuccessful();
        r0 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeTxAndExecute(de.greenrobot.dao.async.AsyncOperation r8, de.greenrobot.dao.async.AsyncOperation r9) {
        /*
            r7 = this;
            r3 = 0
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            r4.add(r8)
            r4.add(r9)
            android.database.sqlite.SQLiteDatabase r5 = r8.getDatabase()
            r5.beginTransaction()
            r2 = r3
        L_0x0014:
            int r0 = r4.size()     // Catch:{ all -> 0x0074 }
            if (r2 < r0) goto L_0x0032
            r0 = r3
        L_0x001b:
            r5.endTransaction()     // Catch:{ RuntimeException -> 0x0082 }
            r3 = r0
        L_0x001f:
            if (r3 != 0) goto L_0x00b4
            java.lang.String r0 = "Reverted merged transaction because one of the operations failed. Executing operations one by one instead..."
            de.greenrobot.dao.DaoLog.i(r0)
            java.util.Iterator r1 = r4.iterator()
        L_0x002b:
            boolean r0 = r1.hasNext()
            if (r0 != 0) goto L_0x00ce
        L_0x0031:
            return
        L_0x0032:
            java.lang.Object r0 = r4.get(r2)     // Catch:{ all -> 0x0074 }
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch:{ all -> 0x0074 }
            r7.executeOperation(r0)     // Catch:{ all -> 0x0074 }
            boolean r1 = r0.isFailed()     // Catch:{ all -> 0x0074 }
            if (r1 != 0) goto L_0x004d
            int r1 = r4.size()     // Catch:{ all -> 0x0074 }
            int r1 = r1 + -1
            if (r2 == r1) goto L_0x004f
        L_0x0049:
            int r0 = r2 + 1
            r2 = r0
            goto L_0x0014
        L_0x004d:
            r0 = r3
            goto L_0x001b
        L_0x004f:
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r1 = r7.queue     // Catch:{ all -> 0x0074 }
            java.lang.Object r1 = r1.peek()     // Catch:{ all -> 0x0074 }
            de.greenrobot.dao.async.AsyncOperation r1 = (de.greenrobot.dao.async.AsyncOperation) r1     // Catch:{ all -> 0x0074 }
            int r6 = r7.maxOperationCountToMerge     // Catch:{ all -> 0x0074 }
            if (r2 < r6) goto L_0x0060
        L_0x005b:
            r5.setTransactionSuccessful()     // Catch:{ all -> 0x0074 }
            r0 = 1
            goto L_0x001b
        L_0x0060:
            boolean r0 = r0.isMergeableWith(r1)     // Catch:{ all -> 0x0074 }
            if (r0 == 0) goto L_0x005b
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r0 = r7.queue     // Catch:{ all -> 0x0074 }
            java.lang.Object r0 = r0.remove()     // Catch:{ all -> 0x0074 }
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch:{ all -> 0x0074 }
            if (r0 != r1) goto L_0x0079
            r4.add(r0)     // Catch:{ all -> 0x0074 }
            goto L_0x0049
        L_0x0074:
            r0 = move-exception
            r5.endTransaction()     // Catch:{ RuntimeException -> 0x009b }
        L_0x0078:
            throw r0
        L_0x0079:
            de.greenrobot.dao.DaoException r0 = new de.greenrobot.dao.DaoException     // Catch:{ all -> 0x0074 }
            java.lang.String r1 = "Internal error: peeked op did not match removed op"
            r0.<init>((java.lang.String) r1)     // Catch:{ all -> 0x0074 }
            throw r0     // Catch:{ all -> 0x0074 }
        L_0x0082:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Async transaction could not be ended, success so far was: "
            java.lang.StringBuilder r2 = r2.append(r5)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
            de.greenrobot.dao.DaoLog.i(r0, r1)
            goto L_0x001f
        L_0x009b:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Async transaction could not be ended, success so far was: "
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            de.greenrobot.dao.DaoLog.i(r2, r1)
            goto L_0x0078
        L_0x00b4:
            int r1 = r4.size()
            java.util.Iterator r2 = r4.iterator()
        L_0x00bc:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L_0x0031
            java.lang.Object r0 = r2.next()
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0
            r0.mergedOperationsCount = r1
            r7.handleOperationCompleted(r0)
            goto L_0x00bc
        L_0x00ce:
            java.lang.Object r0 = r1.next()
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0
            r0.reset()
            r7.executeOperationAndPostCompleted(r0)
            goto L_0x002b
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.dao.async.AsyncOperationExecutor.mergeTxAndExecute(de.greenrobot.dao.async.AsyncOperation, de.greenrobot.dao.async.AsyncOperation):void");
    }

    public void enqueue(AsyncOperation asyncOperation) {
        synchronized (this) {
            int i = this.lastSequenceNumber + 1;
            this.lastSequenceNumber = i;
            asyncOperation.sequenceNumber = i;
            this.queue.add(asyncOperation);
            this.countOperationsEnqueued++;
            if (!this.executorRunning) {
                this.executorRunning = true;
                executorService.execute(this);
            }
        }
    }

    public AsyncOperationListener getListener() {
        return this.listener;
    }

    public AsyncOperationListener getListenerMainThread() {
        return this.listenerMainThread;
    }

    public int getMaxOperationCountToMerge() {
        return this.maxOperationCountToMerge;
    }

    public int getWaitForMergeMillis() {
        return this.waitForMergeMillis;
    }

    public boolean handleMessage(Message message) {
        AsyncOperationListener asyncOperationListener = this.listenerMainThread;
        if (asyncOperationListener == null) {
            return false;
        }
        asyncOperationListener.onAsyncOperationCompleted((AsyncOperation) message.obj);
        return false;
    }

    public synchronized boolean isCompleted() {
        return this.countOperationsEnqueued == this.countOperationsCompleted;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        r1 = r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r6 = this;
            r5 = 0
        L_0x0001:
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r0 = r6.queue     // Catch:{ InterruptedException -> 0x001a }
            java.util.concurrent.TimeUnit r1 = java.util.concurrent.TimeUnit.SECONDS     // Catch:{ InterruptedException -> 0x001a }
            r2 = 1
            java.lang.Object r0 = r0.poll(r2, r1)     // Catch:{ InterruptedException -> 0x001a }
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch:{ InterruptedException -> 0x001a }
            if (r0 == 0) goto L_0x003d
            r1 = r0
        L_0x0010:
            boolean r0 = r1.isMergeTx()     // Catch:{ InterruptedException -> 0x001a }
            if (r0 != 0) goto L_0x0059
        L_0x0016:
            r6.executeOperationAndPostCompleted(r1)     // Catch:{ InterruptedException -> 0x001a }
            goto L_0x0001
        L_0x001a:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0055 }
            r1.<init>()     // Catch:{ all -> 0x0055 }
            java.lang.Thread r2 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0055 }
            java.lang.String r2 = r2.getName()     // Catch:{ all -> 0x0055 }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0055 }
            java.lang.String r2 = " was interruppted"
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ all -> 0x0055 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0055 }
            de.greenrobot.dao.DaoLog.w(r1, r0)     // Catch:{ all -> 0x0055 }
            r6.executorRunning = r5
            return
        L_0x003d:
            monitor-enter(r6)     // Catch:{ InterruptedException -> 0x001a }
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r0 = r6.queue     // Catch:{ all -> 0x0052 }
            java.lang.Object r0 = r0.poll()     // Catch:{ all -> 0x0052 }
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch:{ all -> 0x0052 }
            if (r0 == 0) goto L_0x004b
            monitor-exit(r6)     // Catch:{ all -> 0x0052 }
            r1 = r0
            goto L_0x0010
        L_0x004b:
            r0 = 0
            r6.executorRunning = r0     // Catch:{ all -> 0x0052 }
            monitor-exit(r6)     // Catch:{ all -> 0x0052 }
            r6.executorRunning = r5
            return
        L_0x0052:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0052 }
            throw r0     // Catch:{ InterruptedException -> 0x001a }
        L_0x0055:
            r0 = move-exception
            r6.executorRunning = r5
            throw r0
        L_0x0059:
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r0 = r6.queue     // Catch:{ InterruptedException -> 0x001a }
            int r2 = r6.waitForMergeMillis     // Catch:{ InterruptedException -> 0x001a }
            long r2 = (long) r2     // Catch:{ InterruptedException -> 0x001a }
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.MILLISECONDS     // Catch:{ InterruptedException -> 0x001a }
            java.lang.Object r0 = r0.poll(r2, r4)     // Catch:{ InterruptedException -> 0x001a }
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch:{ InterruptedException -> 0x001a }
            if (r0 == 0) goto L_0x0016
            boolean r2 = r1.isMergeableWith(r0)     // Catch:{ InterruptedException -> 0x001a }
            if (r2 != 0) goto L_0x0075
            r6.executeOperationAndPostCompleted(r1)     // Catch:{ InterruptedException -> 0x001a }
            r6.executeOperationAndPostCompleted(r0)     // Catch:{ InterruptedException -> 0x001a }
            goto L_0x0001
        L_0x0075:
            r6.mergeTxAndExecute(r1, r0)     // Catch:{ InterruptedException -> 0x001a }
            goto L_0x0001
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.dao.async.AsyncOperationExecutor.run():void");
    }

    public void setListener(AsyncOperationListener asyncOperationListener) {
        this.listener = asyncOperationListener;
    }

    public void setListenerMainThread(AsyncOperationListener asyncOperationListener) {
        this.listenerMainThread = asyncOperationListener;
    }

    public void setMaxOperationCountToMerge(int i) {
        this.maxOperationCountToMerge = i;
    }

    public void setWaitForMergeMillis(int i) {
        this.waitForMergeMillis = i;
    }

    public synchronized void waitForCompletion() {
        while (!isCompleted()) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
    }

    public synchronized boolean waitForCompletion(int i) {
        if (!isCompleted()) {
            try {
                wait((long) i);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
        return isCompleted();
    }
}
