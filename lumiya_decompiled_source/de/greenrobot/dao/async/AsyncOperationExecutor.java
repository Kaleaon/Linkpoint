package de.greenrobot.dao.async;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import de.greenrobot.dao.DaoException;
import de.greenrobot.dao.DaoLog;
import de.greenrobot.dao.query.Query;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AsyncOperationExecutor implements Runnable, Handler.Callback {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private final BlockingQueue<AsyncOperation> queue = new LinkedBlockingQueue();
    private volatile int maxOperationCountToMerge = 50;
    private volatile int waitForMergeMillis = 50;

    private void executeOperation(AsyncOperation asyncOperation) {
        asyncOperation.timeStarted = System.currentTimeMillis();
        try {
            switch (asyncOperation.type) {
                case Delete:
                    asyncOperation.dao.delete(asyncOperation.parameter);
                    break;
                case DeleteInTxIterable:
                    asyncOperation.dao.deleteInTx((Iterable) asyncOperation.parameter);
                    break;
                case DeleteInTxArray:
                    asyncOperation.dao.deleteInTx((Object[]) asyncOperation.parameter);
                    break;
                case Insert:
                    asyncOperation.dao.insert(asyncOperation.parameter);
                    break;
                case InsertInTxIterable:
                    asyncOperation.dao.insertInTx((Iterable) asyncOperation.parameter);
                    break;
                case InsertInTxArray:
                    asyncOperation.dao.insertInTx((Object[]) asyncOperation.parameter);
                    break;
                case InsertOrReplace:
                    asyncOperation.dao.insertOrReplace(asyncOperation.parameter);
                    break;
                case InsertOrReplaceInTxIterable:
                    asyncOperation.dao.insertOrReplaceInTx((Iterable) asyncOperation.parameter);
                    break;
                case InsertOrReplaceInTxArray:
                    asyncOperation.dao.insertOrReplaceInTx((Object[]) asyncOperation.parameter);
                    break;
                case Update:
                    asyncOperation.dao.update(asyncOperation.parameter);
                    break;
                case UpdateInTxIterable:
                    asyncOperation.dao.updateInTx((Iterable) asyncOperation.parameter);
                    break;
                case UpdateInTxArray:
                    asyncOperation.dao.updateInTx((Object[]) asyncOperation.parameter);
                    break;
                case TransactionRunnable:
                    executeTransactionRunnable(asyncOperation);
                    break;
                case TransactionCallable:
                    executeTransactionCallable(asyncOperation);
                    break;
                case QueryList:
                    asyncOperation.result = ((Query) asyncOperation.parameter).list();
                    break;
                case QueryUnique:
                    asyncOperation.result = ((Query) asyncOperation.parameter).unique();
                    break;
                case DeleteByKey:
                    asyncOperation.dao.deleteByKey(asyncOperation.parameter);
                    break;
                case DeleteAll:
                    asyncOperation.dao.deleteAll();
                    break;
                case Load:
                    asyncOperation.result = asyncOperation.dao.load(asyncOperation.parameter);
                    break;
                case LoadAll:
                    asyncOperation.result = asyncOperation.dao.loadAll();
                    break;
                case Count:
                    asyncOperation.result = Long.valueOf(asyncOperation.dao.count());
                    break;
                case Refresh:
                    asyncOperation.dao.refresh(asyncOperation.parameter);
                    break;
                default:
                    throw new DaoException("Unsupported operation: " + asyncOperation.type);
            }
        } catch (Throwable th) {
            asyncOperation.throwable = th;
        }
        asyncOperation.timeCompleted = System.currentTimeMillis();
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

    /* JADX WARN: Code restructure failed: missing block: B:21:0x005b, code lost:
        r5.setTransactionSuccessful();
        r0 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
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
        L14:
            int r0 = r4.size()     // Catch: java.lang.Throwable -> L74
            if (r2 < r0) goto L32
            r0 = r3
        L1b:
            r5.endTransaction()     // Catch: java.lang.RuntimeException -> L82
            r3 = r0
        L1f:
            if (r3 != 0) goto Lb4
            java.lang.String r0 = "Reverted merged transaction because one of the operations failed. Executing operations one by one instead..."
            de.greenrobot.dao.DaoLog.i(r0)
            java.util.Iterator r1 = r4.iterator()
        L2b:
            boolean r0 = r1.hasNext()
            if (r0 != 0) goto Lce
        L31:
            return
        L32:
            java.lang.Object r0 = r4.get(r2)     // Catch: java.lang.Throwable -> L74
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch: java.lang.Throwable -> L74
            r7.executeOperation(r0)     // Catch: java.lang.Throwable -> L74
            boolean r1 = r0.isFailed()     // Catch: java.lang.Throwable -> L74
            if (r1 != 0) goto L4d
            int r1 = r4.size()     // Catch: java.lang.Throwable -> L74
            int r1 = r1 + (-1)
            if (r2 == r1) goto L4f
        L49:
            int r0 = r2 + 1
            r2 = r0
            goto L14
        L4d:
            r0 = r3
            goto L1b
        L4f:
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r1 = r7.queue     // Catch: java.lang.Throwable -> L74
            java.lang.Object r1 = r1.peek()     // Catch: java.lang.Throwable -> L74
            de.greenrobot.dao.async.AsyncOperation r1 = (de.greenrobot.dao.async.AsyncOperation) r1     // Catch: java.lang.Throwable -> L74
            int r6 = r7.maxOperationCountToMerge     // Catch: java.lang.Throwable -> L74
            if (r2 < r6) goto L60
        L5b:
            r5.setTransactionSuccessful()     // Catch: java.lang.Throwable -> L74
            r0 = 1
            goto L1b
        L60:
            boolean r0 = r0.isMergeableWith(r1)     // Catch: java.lang.Throwable -> L74
            if (r0 == 0) goto L5b
            java.util.concurrent.BlockingQueue<de.greenrobot.dao.async.AsyncOperation> r0 = r7.queue     // Catch: java.lang.Throwable -> L74
            java.lang.Object r0 = r0.remove()     // Catch: java.lang.Throwable -> L74
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0     // Catch: java.lang.Throwable -> L74
            if (r0 != r1) goto L79
            r4.add(r0)     // Catch: java.lang.Throwable -> L74
            goto L49
        L74:
            r0 = move-exception
            r5.endTransaction()     // Catch: java.lang.RuntimeException -> L9b
        L78:
            throw r0
        L79:
            de.greenrobot.dao.DaoException r0 = new de.greenrobot.dao.DaoException     // Catch: java.lang.Throwable -> L74
            java.lang.String r1 = "Internal error: peeked op did not match removed op"
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L74
            throw r0     // Catch: java.lang.Throwable -> L74
        L82:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Async transaction could not be ended, success so far was: "
            java.lang.StringBuilder r2 = r2.append(r5)
            java.lang.StringBuilder r0 = r2.append(r0)
            java.lang.String r0 = r0.toString()
            de.greenrobot.dao.DaoLog.i(r0, r1)
            goto L1f
        L9b:
            r1 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Async transaction could not be ended, success so far was: "
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            de.greenrobot.dao.DaoLog.i(r2, r1)
            goto L78
        Lb4:
            int r1 = r4.size()
            java.util.Iterator r2 = r4.iterator()
        Lbc:
            boolean r0 = r2.hasNext()
            if (r0 == 0) goto L31
            java.lang.Object r0 = r2.next()
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0
            r0.mergedOperationsCount = r1
            r7.handleOperationCompleted(r0)
            goto Lbc
        Lce:
            java.lang.Object r0 = r1.next()
            de.greenrobot.dao.async.AsyncOperation r0 = (de.greenrobot.dao.async.AsyncOperation) r0
            r0.reset()
            r7.executeOperationAndPostCompleted(r0)
            goto L2b
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

    @Override // android.os.Handler.Callback
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

    @Override // java.lang.Runnable
    public void run() {
        AsyncOperation poll;
        AsyncOperation asyncOperation;
        AsyncOperation poll2;
        while (true) {
            try {
                AsyncOperation poll3 = this.queue.poll(1L, TimeUnit.SECONDS);
                if (poll3 != null) {
                    asyncOperation = poll3;
                } else {
                    synchronized (this) {
                        poll = this.queue.poll();
                        if (poll == null) {
                            this.executorRunning = false;
                            return;
                        }
                    }
                    asyncOperation = poll;
                }
                if (!asyncOperation.isMergeTx() || (poll2 = this.queue.poll(this.waitForMergeMillis, TimeUnit.MILLISECONDS)) == null) {
                    executeOperationAndPostCompleted(asyncOperation);
                } else if (asyncOperation.isMergeableWith(poll2)) {
                    mergeTxAndExecute(asyncOperation, poll2);
                } else {
                    executeOperationAndPostCompleted(asyncOperation);
                    executeOperationAndPostCompleted(poll2);
                }
            } catch (InterruptedException e) {
                DaoLog.w(Thread.currentThread().getName() + " was interruppted", e);
                return;
            } finally {
                this.executorRunning = false;
            }
        }
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
                wait(i);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for all operations to complete", e);
            }
        }
        return isCompleted();
    }
}
