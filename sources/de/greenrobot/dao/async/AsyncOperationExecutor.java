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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void executeOperation(de.greenrobot.dao.async.AsyncOperation r4) {
        /*
            r3 = this;
            long r0 = java.lang.System.currentTimeMillis()
            r4.timeStarted = r0
            int[] r0 = de.greenrobot.dao.async.AsyncOperationExecutor.AnonymousClass1.$SwitchMap$de$greenrobot$dao$async$AsyncOperation$OperationType     // Catch:{ Throwable -> 0x002f }
            de.greenrobot.dao.async.AsyncOperation$OperationType r1 = r4.type     // Catch:{ Throwable -> 0x002f }
            int r1 = r1.ordinal()     // Catch:{ Throwable -> 0x002f }
            r0 = r0[r1]     // Catch:{ Throwable -> 0x002f }
            switch(r0) {
                case 1: goto L_0x0039;
                case 2: goto L_0x0041;
                case 3: goto L_0x004b;
                case 4: goto L_0x0057;
                case 5: goto L_0x005f;
                case 6: goto L_0x0069;
                case 7: goto L_0x0075;
                case 8: goto L_0x007d;
                case 9: goto L_0x0087;
                case 10: goto L_0x0093;
                case 11: goto L_0x009b;
                case 12: goto L_0x00a5;
                case 13: goto L_0x00b1;
                case 14: goto L_0x00b6;
                case 15: goto L_0x00bb;
                case 16: goto L_0x00c7;
                case 17: goto L_0x00d3;
                case 18: goto L_0x00dc;
                case 19: goto L_0x00e3;
                case 20: goto L_0x00ef;
                case 21: goto L_0x00f9;
                case 22: goto L_0x0107;
                default: goto L_0x0013;
            }     // Catch:{ Throwable -> 0x002f }
        L_0x0013:
            de.greenrobot.dao.DaoException r0 = new de.greenrobot.dao.DaoException     // Catch:{ Throwable -> 0x002f }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x002f }
            r1.<init>()     // Catch:{ Throwable -> 0x002f }
            java.lang.String r2 = "Unsupported operation: "
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ Throwable -> 0x002f }
            de.greenrobot.dao.async.AsyncOperation$OperationType r2 = r4.type     // Catch:{ Throwable -> 0x002f }
            java.lang.StringBuilder r1 = r1.append(r2)     // Catch:{ Throwable -> 0x002f }
            java.lang.String r1 = r1.toString()     // Catch:{ Throwable -> 0x002f }
            r0.<init>((java.lang.String) r1)     // Catch:{ Throwable -> 0x002f }
            throw r0     // Catch:{ Throwable -> 0x002f }
        L_0x002f:
            r0 = move-exception
            r4.throwable = r0
        L_0x0032:
            long r0 = java.lang.System.currentTimeMillis()
            r4.timeCompleted = r0
            return
        L_0x0039:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.delete(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0041:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Iterable r0 = (java.lang.Iterable) r0     // Catch:{ Throwable -> 0x002f }
            r1.deleteInTx(r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x004b:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            r1.deleteInTx((T[]) r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0057:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.insert(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x005f:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Iterable r0 = (java.lang.Iterable) r0     // Catch:{ Throwable -> 0x002f }
            r1.insertInTx(r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0069:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            r1.insertInTx((T[]) r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0075:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.insertOrReplace(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x007d:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Iterable r0 = (java.lang.Iterable) r0     // Catch:{ Throwable -> 0x002f }
            r1.insertOrReplaceInTx(r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0087:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            r1.insertOrReplaceInTx((T[]) r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0093:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.update(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x009b:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Iterable r0 = (java.lang.Iterable) r0     // Catch:{ Throwable -> 0x002f }
            r1.updateInTx(r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00a5:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r1 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch:{ Throwable -> 0x002f }
            r1.updateInTx((T[]) r0)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00b1:
            r3.executeTransactionRunnable(r4)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00b6:
            r3.executeTransactionCallable(r4)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00bb:
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            de.greenrobot.dao.query.Query r0 = (de.greenrobot.dao.query.Query) r0     // Catch:{ Throwable -> 0x002f }
            java.util.List r0 = r0.list()     // Catch:{ Throwable -> 0x002f }
            r4.result = r0     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00c7:
            java.lang.Object r0 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            de.greenrobot.dao.query.Query r0 = (de.greenrobot.dao.query.Query) r0     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r0.unique()     // Catch:{ Throwable -> 0x002f }
            r4.result = r0     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00d3:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.deleteByKey(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00dc:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            r0.deleteAll()     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00e3:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r0 = r0.load(r1)     // Catch:{ Throwable -> 0x002f }
            r4.result = r0     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00ef:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.util.List r0 = r0.loadAll()     // Catch:{ Throwable -> 0x002f }
            r4.result = r0     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x00f9:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            long r0 = r0.count()     // Catch:{ Throwable -> 0x002f }
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch:{ Throwable -> 0x002f }
            r4.result = r0     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        L_0x0107:
            de.greenrobot.dao.AbstractDao<java.lang.Object, java.lang.Object> r0 = r4.dao     // Catch:{ Throwable -> 0x002f }
            java.lang.Object r1 = r4.parameter     // Catch:{ Throwable -> 0x002f }
            r0.refresh(r1)     // Catch:{ Throwable -> 0x002f }
            goto L_0x0032
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.dao.async.AsyncOperationExecutor.executeOperation(de.greenrobot.dao.async.AsyncOperation):void");
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
