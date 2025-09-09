package de.greenrobot.dao.async;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

public class AsyncOperation {
    public static final int FLAG_MERGE_TX = 1;
    public static final int FLAG_STOP_QUEUE_ON_EXCEPTION = 2;
    public static final int FLAG_TRACK_CREATOR_STACKTRACE = 4;
    private volatile boolean completed;
    final Exception creatorStacktrace;
    final AbstractDao<Object, Object> dao;
    private final SQLiteDatabase database;
    final int flags;
    volatile int mergedOperationsCount;
    final Object parameter;
    volatile Object result;
    int sequenceNumber;
    volatile Throwable throwable;
    volatile long timeCompleted;
    volatile long timeStarted;
    final OperationType type;

    public enum OperationType {
        Insert,
        InsertInTxIterable,
        InsertInTxArray,
        InsertOrReplace,
        InsertOrReplaceInTxIterable,
        InsertOrReplaceInTxArray,
        Update,
        UpdateInTxIterable,
        UpdateInTxArray,
        Delete,
        DeleteInTxIterable,
        DeleteInTxArray,
        DeleteByKey,
        DeleteAll,
        TransactionRunnable,
        TransactionCallable,
        QueryList,
        QueryUnique,
        Load,
        LoadAll,
        Count,
        Refresh
    }

    AsyncOperation(OperationType operationType, AbstractDao<?, ?> abstractDao, SQLiteDatabase sQLiteDatabase, Object obj, int i) {
        this.type = operationType;
        this.flags = i;
        this.dao = abstractDao;
        this.database = sQLiteDatabase;
        this.parameter = obj;
        this.creatorStacktrace = (i & 4) == 0 ? null : new Exception("AsyncOperation was created here");
    }

    public Exception getCreatorStacktrace() {
        return this.creatorStacktrace;
    }

    /* access modifiers changed from: package-private */
    public SQLiteDatabase getDatabase() {
        return this.database == null ? this.dao.getDatabase() : this.database;
    }

    public long getDuration() {
        if (this.timeCompleted != 0) {
            return this.timeCompleted - this.timeStarted;
        }
        throw new DaoException("This operation did not yet complete");
    }

    public int getMergedOperationsCount() {
        return this.mergedOperationsCount;
    }

    public Object getParameter() {
        return this.parameter;
    }

    public synchronized Object getResult() {
        if (!this.completed) {
            waitForCompletion();
        }
        if (this.throwable == null) {
        } else {
            throw new AsyncDaoException(this, this.throwable);
        }
        return this.result;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public long getTimeCompleted() {
        return this.timeCompleted;
    }

    public long getTimeStarted() {
        return this.timeStarted;
    }

    public OperationType getType() {
        return this.type;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public boolean isCompletedSucessfully() {
        return this.completed && this.throwable == null;
    }

    public boolean isFailed() {
        return this.throwable != null;
    }

    public boolean isMergeTx() {
        return (this.flags & 1) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isMergeableWith(AsyncOperation asyncOperation) {
        return asyncOperation != null && isMergeTx() && asyncOperation.isMergeTx() && getDatabase() == asyncOperation.getDatabase();
    }

    /* access modifiers changed from: package-private */
    public void reset() {
        this.timeStarted = 0;
        this.timeCompleted = 0;
        this.completed = false;
        this.throwable = null;
        this.result = null;
        this.mergedOperationsCount = 0;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setCompleted() {
        this.completed = true;
        notifyAll();
    }

    public void setThrowable(Throwable th) {
        this.throwable = th;
    }

    public synchronized Object waitForCompletion() {
        while (!this.completed) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return this.result;
    }

    public synchronized boolean waitForCompletion(int i) {
        if (!this.completed) {
            try {
                wait((long) i);
            } catch (InterruptedException e) {
                throw new DaoException("Interrupted while waiting for operation to complete", e);
            }
        }
        return this.completed;
    }
}
