// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.async;

import de.greenrobot.dao.DaoLog;
import java.util.concurrent.TimeUnit;
import android.os.Message;
import android.os.Looper;
import android.database.sqlite.SQLiteDatabase;
import java.util.concurrent.Callable;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.DaoException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.BlockingQueue;
import android.os.Handler;
import java.util.concurrent.ExecutorService;
import android.os.Handler$Callback;

class AsyncOperationExecutor implements Runnable, Handler$Callback
{
    private static ExecutorService executorService;
    private int countOperationsCompleted;
    private int countOperationsEnqueued;
    private volatile boolean executorRunning;
    private Handler handlerMainThread;
    private int lastSequenceNumber;
    private volatile AsyncOperationListener listener;
    private volatile AsyncOperationListener listenerMainThread;
    private volatile int maxOperationCountToMerge;
    private final BlockingQueue<AsyncOperation> queue;
    private volatile int waitForMergeMillis;
    
    static {
        AsyncOperationExecutor.executorService = Executors.newCachedThreadPool();
    }
    
    AsyncOperationExecutor() {
        this.queue = new LinkedBlockingQueue<AsyncOperation>();
        this.maxOperationCountToMerge = 50;
        this.waitForMergeMillis = 50;
    }
    
    private void executeOperation(final AsyncOperation asyncOperation) {
        asyncOperation.timeStarted = System.currentTimeMillis();
        while (true) {
            Label_0485: {
                Label_0468: {
                    Label_0454: {
                        Label_0436: {
                            Label_0426: {
                                Label_0412: {
                                    Label_0395: {
                                        Label_0378: {
                                            Label_0370: {
                                                Label_0362: {
                                                    Label_0345: {
                                                        Label_0328: {
                                                            Label_0314: {
                                                                Label_0297: {
                                                                    Label_0280: {
                                                                        Label_0265: {
                                                                            Label_0248: {
                                                                                Label_0231: {
                                                                                    Label_0216: {
                                                                                        Label_0199: {
                                                                                            Label_0182: {
                                                                                                Label_0168: {
                                                                                                    try {
                                                                                                        switch (asyncOperation.type) {
                                                                                                            default: {
                                                                                                                throw new DaoException("Unsupported operation: " + asyncOperation.type);
                                                                                                            }
                                                                                                            case Delete: {
                                                                                                                break Label_0168;
                                                                                                            }
                                                                                                            case DeleteInTxIterable: {
                                                                                                                break Label_0182;
                                                                                                            }
                                                                                                            case DeleteInTxArray: {
                                                                                                                break Label_0199;
                                                                                                            }
                                                                                                            case Insert: {
                                                                                                                break Label_0216;
                                                                                                            }
                                                                                                            case InsertInTxIterable: {
                                                                                                                break Label_0231;
                                                                                                            }
                                                                                                            case InsertInTxArray: {
                                                                                                                break Label_0248;
                                                                                                            }
                                                                                                            case InsertOrReplace: {
                                                                                                                break Label_0265;
                                                                                                            }
                                                                                                            case InsertOrReplaceInTxIterable: {
                                                                                                                break Label_0280;
                                                                                                            }
                                                                                                            case InsertOrReplaceInTxArray: {
                                                                                                                break Label_0297;
                                                                                                            }
                                                                                                            case Update: {
                                                                                                                break Label_0314;
                                                                                                            }
                                                                                                            case UpdateInTxIterable: {
                                                                                                                break Label_0328;
                                                                                                            }
                                                                                                            case UpdateInTxArray: {
                                                                                                                break Label_0345;
                                                                                                            }
                                                                                                            case TransactionRunnable: {
                                                                                                                break Label_0362;
                                                                                                            }
                                                                                                            case TransactionCallable: {
                                                                                                                break Label_0370;
                                                                                                            }
                                                                                                            case QueryList: {
                                                                                                                break Label_0378;
                                                                                                            }
                                                                                                            case QueryUnique: {
                                                                                                                break Label_0395;
                                                                                                            }
                                                                                                            case DeleteByKey: {
                                                                                                                break Label_0412;
                                                                                                            }
                                                                                                            case DeleteAll: {
                                                                                                                break Label_0426;
                                                                                                            }
                                                                                                            case Load: {
                                                                                                                break Label_0436;
                                                                                                            }
                                                                                                            case LoadAll: {
                                                                                                                break Label_0454;
                                                                                                            }
                                                                                                            case Count: {
                                                                                                                break Label_0468;
                                                                                                            }
                                                                                                            case Refresh: {
                                                                                                                break Label_0485;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                    catch (final Throwable throwable) {
                                                                                                        asyncOperation.throwable = throwable;
                                                                                                    }
                                                                                                    asyncOperation.timeCompleted = System.currentTimeMillis();
                                                                                                    return;
                                                                                                }
                                                                                                asyncOperation.dao.delete(asyncOperation.parameter);
                                                                                                continue;
                                                                                            }
                                                                                            asyncOperation.dao.deleteInTx((Iterable<Object>)asyncOperation.parameter);
                                                                                            continue;
                                                                                        }
                                                                                        asyncOperation.dao.deleteInTx((Object[])asyncOperation.parameter);
                                                                                        continue;
                                                                                    }
                                                                                    asyncOperation.dao.insert(asyncOperation.parameter);
                                                                                    continue;
                                                                                }
                                                                                asyncOperation.dao.insertInTx((Iterable<Object>)asyncOperation.parameter);
                                                                                continue;
                                                                            }
                                                                            asyncOperation.dao.insertInTx((Object[])asyncOperation.parameter);
                                                                            continue;
                                                                        }
                                                                        asyncOperation.dao.insertOrReplace(asyncOperation.parameter);
                                                                        continue;
                                                                    }
                                                                    asyncOperation.dao.insertOrReplaceInTx((Iterable<Object>)asyncOperation.parameter);
                                                                    continue;
                                                                }
                                                                asyncOperation.dao.insertOrReplaceInTx((Object[])asyncOperation.parameter);
                                                                continue;
                                                            }
                                                            asyncOperation.dao.update(asyncOperation.parameter);
                                                            continue;
                                                        }
                                                        asyncOperation.dao.updateInTx((Iterable<Object>)asyncOperation.parameter);
                                                        continue;
                                                    }
                                                    asyncOperation.dao.updateInTx((Object[])asyncOperation.parameter);
                                                    continue;
                                                }
                                                this.executeTransactionRunnable(asyncOperation);
                                                continue;
                                            }
                                            this.executeTransactionCallable(asyncOperation);
                                            continue;
                                        }
                                        asyncOperation.result = ((Query)asyncOperation.parameter).list();
                                        continue;
                                    }
                                    asyncOperation.result = ((Query)asyncOperation.parameter).unique();
                                    continue;
                                }
                                asyncOperation.dao.deleteByKey(asyncOperation.parameter);
                                continue;
                            }
                            asyncOperation.dao.deleteAll();
                            continue;
                        }
                        asyncOperation.result = asyncOperation.dao.load(asyncOperation.parameter);
                        continue;
                    }
                    asyncOperation.result = asyncOperation.dao.loadAll();
                    continue;
                }
                asyncOperation.result = asyncOperation.dao.count();
                continue;
            }
            asyncOperation.dao.refresh(asyncOperation.parameter);
            continue;
        }
    }
    
    private void executeOperationAndPostCompleted(final AsyncOperation asyncOperation) {
        this.executeOperation(asyncOperation);
        this.handleOperationCompleted(asyncOperation);
    }
    
    private void executeTransactionCallable(final AsyncOperation asyncOperation) throws Exception {
        final SQLiteDatabase database = asyncOperation.getDatabase();
        database.beginTransaction();
        try {
            asyncOperation.result = ((Callable)asyncOperation.parameter).call();
            database.setTransactionSuccessful();
        }
        finally {
            database.endTransaction();
        }
    }
    
    private void executeTransactionRunnable(final AsyncOperation asyncOperation) {
        final SQLiteDatabase database = asyncOperation.getDatabase();
        database.beginTransaction();
        try {
            ((Runnable)asyncOperation.parameter).run();
            database.setTransactionSuccessful();
        }
        finally {
            database.endTransaction();
        }
    }
    
    private void handleOperationCompleted(final AsyncOperation asyncOperation) {
        asyncOperation.setCompleted();
        final AsyncOperationListener listener = this.listener;
        while (true) {
            while (true) {
                Label_0013: {
                    if (listener == null) {
                        break Label_0013;
                    }
                    Label_0046: {
                        break Label_0046;
                        while (true) {
                            synchronized (this) {
                                ++this.countOperationsCompleted;
                                if (this.countOperationsCompleted == this.countOperationsEnqueued) {
                                    this.notifyAll();
                                }
                                return;
                                listener.onAsyncOperationCompleted(asyncOperation);
                                break;
                                iftrue(Label_0085:)(this.handlerMainThread == null);
                                Label_0063: {
                                    break Label_0063;
                                    Label_0085: {
                                        this.handlerMainThread = new Handler(Looper.getMainLooper(), (Handler$Callback)this);
                                    }
                                }
                                this.handlerMainThread.sendMessage(this.handlerMainThread.obtainMessage(1, (Object)asyncOperation));
                            }
                        }
                    }
                }
                if (this.listenerMainThread == null) {
                    continue;
                }
                break;
            }
            continue;
        }
    }
    
    private void mergeTxAndExecute(final AsyncOperation p0, final AsyncOperation p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore_3       
        //     2: new             Ljava/util/ArrayList;
        //     5: dup            
        //     6: invokespecial   java/util/ArrayList.<init>:()V
        //     9: astore          4
        //    11: aload           4
        //    13: aload_1        
        //    14: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    17: pop            
        //    18: aload           4
        //    20: aload_2        
        //    21: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //    24: pop            
        //    25: aload_1        
        //    26: invokevirtual   de/greenrobot/dao/async/AsyncOperation.getDatabase:()Landroid/database/sqlite/SQLiteDatabase;
        //    29: astore_1       
        //    30: aload_1        
        //    31: invokevirtual   android/database/sqlite/SQLiteDatabase.beginTransaction:()V
        //    34: iconst_0       
        //    35: istore          5
        //    37: aload           4
        //    39: invokevirtual   java/util/ArrayList.size:()I
        //    42: istore          6
        //    44: iload           5
        //    46: iload           6
        //    48: if_icmplt       86
        //    51: iconst_0       
        //    52: istore          7
        //    54: aload_1        
        //    55: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //    58: iload           7
        //    60: ifne            290
        //    63: ldc_w           "Reverted merged transaction because one of the operations failed. Executing operations one by one instead..."
        //    66: invokestatic    de/greenrobot/dao/DaoLog.i:(Ljava/lang/String;)I
        //    69: pop            
        //    70: aload           4
        //    72: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //    75: astore_1       
        //    76: aload_1        
        //    77: invokeinterface java/util/Iterator.hasNext:()Z
        //    82: ifne            336
        //    85: return         
        //    86: aload           4
        //    88: iload           5
        //    90: invokevirtual   java/util/ArrayList.get:(I)Ljava/lang/Object;
        //    93: checkcast       Lde/greenrobot/dao/async/AsyncOperation;
        //    96: astore          8
        //    98: aload_0        
        //    99: aload           8
        //   101: invokespecial   de/greenrobot/dao/async/AsyncOperationExecutor.executeOperation:(Lde/greenrobot/dao/async/AsyncOperation;)V
        //   104: aload           8
        //   106: invokevirtual   de/greenrobot/dao/async/AsyncOperation.isFailed:()Z
        //   109: ifne            130
        //   112: iload           5
        //   114: aload           4
        //   116: invokevirtual   java/util/ArrayList.size:()I
        //   119: iconst_1       
        //   120: isub           
        //   121: if_icmpeq       136
        //   124: iinc            5, 1
        //   127: goto            37
        //   130: iconst_0       
        //   131: istore          7
        //   133: goto            54
        //   136: aload_0        
        //   137: getfield        de/greenrobot/dao/async/AsyncOperationExecutor.queue:Ljava/util/concurrent/BlockingQueue;
        //   140: invokeinterface java/util/concurrent/BlockingQueue.peek:()Ljava/lang/Object;
        //   145: checkcast       Lde/greenrobot/dao/async/AsyncOperation;
        //   148: astore_2       
        //   149: iload           5
        //   151: aload_0        
        //   152: getfield        de/greenrobot/dao/async/AsyncOperationExecutor.maxOperationCountToMerge:I
        //   155: if_icmplt       168
        //   158: aload_1        
        //   159: invokevirtual   android/database/sqlite/SQLiteDatabase.setTransactionSuccessful:()V
        //   162: iconst_1       
        //   163: istore          7
        //   165: goto            54
        //   168: aload           8
        //   170: aload_2        
        //   171: invokevirtual   de/greenrobot/dao/async/AsyncOperation.isMergeableWith:(Lde/greenrobot/dao/async/AsyncOperation;)Z
        //   174: ifeq            158
        //   177: aload_0        
        //   178: getfield        de/greenrobot/dao/async/AsyncOperationExecutor.queue:Ljava/util/concurrent/BlockingQueue;
        //   181: invokeinterface java/util/concurrent/BlockingQueue.remove:()Ljava/lang/Object;
        //   186: checkcast       Lde/greenrobot/dao/async/AsyncOperation;
        //   189: astore          8
        //   191: aload           8
        //   193: aload_2        
        //   194: if_acmpne       215
        //   197: aload           4
        //   199: aload           8
        //   201: invokevirtual   java/util/ArrayList.add:(Ljava/lang/Object;)Z
        //   204: pop            
        //   205: goto            124
        //   208: astore_2       
        //   209: aload_1        
        //   210: invokevirtual   android/database/sqlite/SQLiteDatabase.endTransaction:()V
        //   213: aload_2        
        //   214: athrow         
        //   215: new             Lde/greenrobot/dao/DaoException;
        //   218: astore_2       
        //   219: aload_2        
        //   220: ldc_w           "Internal error: peeked op did not match removed op"
        //   223: invokespecial   de/greenrobot/dao/DaoException.<init>:(Ljava/lang/String;)V
        //   226: aload_2        
        //   227: athrow         
        //   228: astore_1       
        //   229: new             Ljava/lang/StringBuilder;
        //   232: dup            
        //   233: invokespecial   java/lang/StringBuilder.<init>:()V
        //   236: ldc_w           "Async transaction could not be ended, success so far was: "
        //   239: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   242: iload           7
        //   244: invokevirtual   java/lang/StringBuilder.append:(Z)Ljava/lang/StringBuilder;
        //   247: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   250: aload_1        
        //   251: invokestatic    de/greenrobot/dao/DaoLog.i:(Ljava/lang/String;Ljava/lang/Throwable;)I
        //   254: pop            
        //   255: iload_3        
        //   256: istore          7
        //   258: goto            58
        //   261: astore_1       
        //   262: new             Ljava/lang/StringBuilder;
        //   265: dup            
        //   266: invokespecial   java/lang/StringBuilder.<init>:()V
        //   269: ldc_w           "Async transaction could not be ended, success so far was: "
        //   272: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   275: iconst_0       
        //   276: invokevirtual   java/lang/StringBuilder.append:(Z)Ljava/lang/StringBuilder;
        //   279: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   282: aload_1        
        //   283: invokestatic    de/greenrobot/dao/DaoLog.i:(Ljava/lang/String;Ljava/lang/Throwable;)I
        //   286: pop            
        //   287: goto            213
        //   290: aload           4
        //   292: invokevirtual   java/util/ArrayList.size:()I
        //   295: istore          5
        //   297: aload           4
        //   299: invokevirtual   java/util/ArrayList.iterator:()Ljava/util/Iterator;
        //   302: astore_1       
        //   303: aload_1        
        //   304: invokeinterface java/util/Iterator.hasNext:()Z
        //   309: ifeq            85
        //   312: aload_1        
        //   313: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   318: checkcast       Lde/greenrobot/dao/async/AsyncOperation;
        //   321: astore_2       
        //   322: aload_2        
        //   323: iload           5
        //   325: putfield        de/greenrobot/dao/async/AsyncOperation.mergedOperationsCount:I
        //   328: aload_0        
        //   329: aload_2        
        //   330: invokespecial   de/greenrobot/dao/async/AsyncOperationExecutor.handleOperationCompleted:(Lde/greenrobot/dao/async/AsyncOperation;)V
        //   333: goto            303
        //   336: aload_1        
        //   337: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   342: checkcast       Lde/greenrobot/dao/async/AsyncOperation;
        //   345: astore_2       
        //   346: aload_2        
        //   347: invokevirtual   de/greenrobot/dao/async/AsyncOperation.reset:()V
        //   350: aload_0        
        //   351: aload_2        
        //   352: invokespecial   de/greenrobot/dao/async/AsyncOperationExecutor.executeOperationAndPostCompleted:(Lde/greenrobot/dao/async/AsyncOperation;)V
        //   355: goto            76
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                        
        //  -----  -----  -----  -----  ----------------------------
        //  37     44     208    215    Any
        //  54     58     228    261    Ljava/lang/RuntimeException;
        //  86     124    208    215    Any
        //  136    158    208    215    Any
        //  158    162    208    215    Any
        //  168    191    208    215    Any
        //  197    205    208    215    Any
        //  209    213    261    290    Ljava/lang/RuntimeException;
        //  215    228    208    215    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IndexOutOfBoundsException: Index 166 out of bounds for length 166
        //     at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
        //     at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
        //     at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
        //     at java.base/java.util.Objects.checkIndex(Objects.java:385)
        //     at java.base/java.util.ArrayList.get(ArrayList.java:427)
        //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3362)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:112)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void enqueue(final AsyncOperation asyncOperation) {
        synchronized (this) {
            final int n = this.lastSequenceNumber + 1;
            this.lastSequenceNumber = n;
            asyncOperation.sequenceNumber = n;
            this.queue.add(asyncOperation);
            ++this.countOperationsEnqueued;
            if (!this.executorRunning) {
                this.executorRunning = true;
                AsyncOperationExecutor.executorService.execute(this);
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
    
    public boolean handleMessage(final Message message) {
        final AsyncOperationListener listenerMainThread = this.listenerMainThread;
        if (listenerMainThread != null) {
            listenerMainThread.onAsyncOperationCompleted((AsyncOperation)message.obj);
        }
        return false;
    }
    
    public boolean isCompleted() {
        synchronized (this) {
            return this.countOperationsEnqueued == this.countOperationsCompleted;
        }
    }
    
    @Override
    public void run() {
        while (true) {
        Label_0028_Outer:
            while (true) {
                AsyncOperation asyncOperation = null;
                AsyncOperation asyncOperation2;
                while (true) {
                    Label_0075: {
                        while (true) {
                            try {
                                while (true) {
                                    asyncOperation = this.queue.poll(1L, TimeUnit.SECONDS);
                                    if (asyncOperation == null) {
                                        break Label_0075;
                                    }
                                    if (asyncOperation.isMergeTx()) {
                                        break;
                                    }
                                    this.executeOperationAndPostCompleted(asyncOperation);
                                }
                            }
                            catch (final InterruptedException ex) {
                                DaoLog.w(Thread.currentThread().getName() + " was interruppted", ex);
                                return;
                                synchronized (this) {
                                    if (this.queue.poll() != null) {
                                        continue Label_0028_Outer;
                                    }
                                    this.executorRunning = false;
                                    monitorexit(this);
                                    this.executorRunning = false;
                                    return;
                                }
                            }
                            finally {
                                this.executorRunning = false;
                            }
                            break;
                        }
                    }
                    asyncOperation2 = this.queue.poll(this.waitForMergeMillis, TimeUnit.MILLISECONDS);
                    if (asyncOperation2 == null) {
                        continue;
                    }
                    break;
                }
                if (!asyncOperation.isMergeableWith(asyncOperation2)) {
                    this.executeOperationAndPostCompleted(asyncOperation);
                    this.executeOperationAndPostCompleted(asyncOperation2);
                    continue Label_0028_Outer;
                }
                this.mergeTxAndExecute(asyncOperation, asyncOperation2);
                continue Label_0028_Outer;
            }
        }
    }
    
    public void setListener(final AsyncOperationListener listener) {
        this.listener = listener;
    }
    
    public void setListenerMainThread(final AsyncOperationListener listenerMainThread) {
        this.listenerMainThread = listenerMainThread;
    }
    
    public void setMaxOperationCountToMerge(final int maxOperationCountToMerge) {
        this.maxOperationCountToMerge = maxOperationCountToMerge;
    }
    
    public void setWaitForMergeMillis(final int waitForMergeMillis) {
        this.waitForMergeMillis = waitForMergeMillis;
    }
    
    public void waitForCompletion() {
        synchronized (this) {
            while (!this.isCompleted()) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {
                    throw new DaoException("Interrupted while waiting for all operations to complete", ex);
                }
            }
        }
    }
    
    public boolean waitForCompletion(final int n) {
        synchronized (this) {
            if (!this.isCompleted()) {
                final long timeoutMillis = n;
                try {
                    this.wait(timeoutMillis);
                }
                catch (final InterruptedException ex) {
                    throw new DaoException("Interrupted while waiting for all operations to complete", ex);
                }
            }
            return this.isCompleted();
        }
    }
}
