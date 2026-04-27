// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.async;

import de.greenrobot.dao.DaoException;
import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;

public class AsyncOperation
{
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
    
    AsyncOperation(final OperationType type, final AbstractDao<?, ?> dao, final SQLiteDatabase database, final Object parameter, final int flags) {
        this.type = type;
        this.flags = flags;
        this.dao = (AbstractDao<Object, Object>)dao;
        this.database = database;
        this.parameter = parameter;
        Exception creatorStacktrace;
        if ((flags & 0x4) == 0x0) {
            creatorStacktrace = null;
        }
        else {
            creatorStacktrace = new Exception("AsyncOperation was created here");
        }
        this.creatorStacktrace = creatorStacktrace;
    }
    
    public Exception getCreatorStacktrace() {
        return this.creatorStacktrace;
    }
    
    SQLiteDatabase getDatabase() {
        SQLiteDatabase sqLiteDatabase;
        if (this.database == null) {
            sqLiteDatabase = this.dao.getDatabase();
        }
        else {
            sqLiteDatabase = this.database;
        }
        return sqLiteDatabase;
    }
    
    public long getDuration() {
        if (this.timeCompleted == 0L) {
            throw new DaoException("This operation did not yet complete");
        }
        return this.timeCompleted - this.timeStarted;
    }
    
    public int getMergedOperationsCount() {
        return this.mergedOperationsCount;
    }
    
    public Object getParameter() {
        return this.parameter;
    }
    
    public Object getResult() {
        synchronized (this) {
            if (!this.completed) {
                this.waitForCompletion();
            }
            if (this.throwable == null) {
                final Object result = this.result;
                monitorexit(this);
                return result;
            }
        }
        final AsyncDaoException ex = new AsyncDaoException(this, this.throwable);
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
        boolean b = false;
        if (this.completed && this.throwable == null) {
            b = true;
        }
        return b;
    }
    
    public boolean isFailed() {
        return this.throwable != null;
    }
    
    public boolean isMergeTx() {
        boolean b = false;
        if ((this.flags & 0x1) != 0x0) {
            b = true;
        }
        return b;
    }
    
    boolean isMergeableWith(final AsyncOperation asyncOperation) {
        final boolean b = false;
        boolean b2;
        if (asyncOperation == null) {
            b2 = b;
        }
        else {
            b2 = b;
            if (this.isMergeTx()) {
                b2 = b;
                if (asyncOperation.isMergeTx()) {
                    b2 = b;
                    if (this.getDatabase() == asyncOperation.getDatabase()) {
                        b2 = true;
                    }
                }
            }
        }
        return b2;
    }
    
    void reset() {
        this.timeStarted = 0L;
        this.timeCompleted = 0L;
        this.completed = false;
        this.throwable = null;
        this.result = null;
        this.mergedOperationsCount = 0;
    }
    
    void setCompleted() {
        synchronized (this) {
            this.completed = true;
            this.notifyAll();
        }
    }
    
    public void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }
    
    public Object waitForCompletion() {
        synchronized (this) {
            while (!this.completed) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {
                    throw new DaoException("Interrupted while waiting for operation to complete", ex);
                }
            }
            return this.result;
        }
    }
    
    public boolean waitForCompletion(final int n) {
        synchronized (this) {
            if (!this.completed) {
                final long timeoutMillis = n;
                try {
                    this.wait(timeoutMillis);
                }
                catch (final InterruptedException ex) {
                    throw new DaoException("Interrupted while waiting for operation to complete", ex);
                }
            }
            return this.completed;
        }
    }
    
    public enum OperationType
    {
        Count, 
        Delete, 
        DeleteAll, 
        DeleteByKey, 
        DeleteInTxArray, 
        DeleteInTxIterable, 
        Insert, 
        InsertInTxArray, 
        InsertInTxIterable, 
        InsertOrReplace, 
        InsertOrReplaceInTxArray, 
        InsertOrReplaceInTxIterable, 
        Load, 
        LoadAll, 
        QueryList, 
        QueryUnique, 
        Refresh, 
        TransactionCallable, 
        TransactionRunnable, 
        Update, 
        UpdateInTxArray, 
        UpdateInTxIterable;
    }
}
