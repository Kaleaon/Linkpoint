// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.provider;

import java.util.concurrent.Callable;
import android.support.annotation.VisibleForTesting;
import android.os.Message;
import android.os.HandlerThread;
import android.os.Handler;
import android.support.annotation.GuardedBy;
import android.os.Handler$Callback;
import android.support.annotation.RestrictTo;

@RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
public class SelfDestructiveThread
{
    private static final int MSG_DESTRUCTION = 0;
    private static final int MSG_INVOKE_RUNNABLE = 1;
    private Handler$Callback mCallback;
    private final int mDestructAfterMillisec;
    @GuardedBy("mLock")
    private int mGeneration;
    @GuardedBy("mLock")
    private Handler mHandler;
    private final Object mLock;
    private final int mPriority;
    @GuardedBy("mLock")
    private HandlerThread mThread;
    private final String mThreadName;
    
    public SelfDestructiveThread(final String mThreadName, final int mPriority, final int mDestructAfterMillisec) {
        this.mLock = new Object();
        this.mCallback = (Handler$Callback)new Handler$Callback() {
            public boolean handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        return true;
                    }
                    case 1: {
                        SelfDestructiveThread.this.onInvokeRunnable((Runnable)message.obj);
                        return true;
                    }
                    case 0: {
                        SelfDestructiveThread.this.onDestruction();
                        return true;
                    }
                }
            }
        };
        this.mThreadName = mThreadName;
        this.mPriority = mPriority;
        this.mDestructAfterMillisec = mDestructAfterMillisec;
        this.mGeneration = 0;
    }
    
    private void onDestruction() {
        synchronized (this.mLock) {
            if (!this.mHandler.hasMessages(1)) {
                this.mThread.quit();
                this.mThread = null;
                this.mHandler = null;
            }
        }
    }
    
    private void onInvokeRunnable(final Runnable runnable) {
        runnable.run();
        synchronized (this.mLock) {
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), (long)this.mDestructAfterMillisec);
        }
    }
    
    private void post(final Runnable runnable) {
        synchronized (this.mLock) {
            if (this.mThread == null) {
                (this.mThread = new HandlerThread(this.mThreadName, this.mPriority)).start();
                this.mHandler = new Handler(this.mThread.getLooper(), this.mCallback);
                ++this.mGeneration;
            }
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, (Object)runnable));
        }
    }
    
    @VisibleForTesting
    public int getGeneration() {
        synchronized (this.mLock) {
            return this.mGeneration;
        }
    }
    
    @VisibleForTesting
    public boolean isRunning() {
        synchronized (this.mLock) {
            return this.mThread != null;
        }
    }
    
    public <T> void postAndReply(final Callable<T> callable, final ReplyCallback<T> replyCallback) {
        this.post(new Runnable() {
            final /* synthetic */ Handler val$callingHandler = new Handler();
            
            @Override
            public void run() {
                while (true) {
                    try {
                        final Object call = callable.call();
                        this.val$callingHandler.post((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                replyCallback.onReply(call);
                            }
                        });
                    }
                    catch (final Exception ex) {
                        final Object call = null;
                        continue;
                    }
                    break;
                }
            }
        });
    }
    
    public <T> T postAndWait(final Callable<T> p0, final int p1) throws InterruptedException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: invokespecial   java/util/concurrent/locks/ReentrantLock.<init>:()V
        //     7: astore_3       
        //     8: aload_3        
        //     9: invokevirtual   java/util/concurrent/locks/ReentrantLock.newCondition:()Ljava/util/concurrent/locks/Condition;
        //    12: astore          4
        //    14: new             Ljava/util/concurrent/atomic/AtomicReference;
        //    17: dup            
        //    18: invokespecial   java/util/concurrent/atomic/AtomicReference.<init>:()V
        //    21: astore          5
        //    23: new             Ljava/util/concurrent/atomic/AtomicBoolean;
        //    26: dup            
        //    27: iconst_1       
        //    28: invokespecial   java/util/concurrent/atomic/AtomicBoolean.<init>:(Z)V
        //    31: astore          6
        //    33: aload_0        
        //    34: new             Landroid/support/v4/provider/SelfDestructiveThread$3;
        //    37: dup            
        //    38: aload_0        
        //    39: aload           5
        //    41: aload_1        
        //    42: aload_3        
        //    43: aload           6
        //    45: aload           4
        //    47: invokespecial   android/support/v4/provider/SelfDestructiveThread$3.<init>:(Landroid/support/v4/provider/SelfDestructiveThread;Ljava/util/concurrent/atomic/AtomicReference;Ljava/util/concurrent/Callable;Ljava/util/concurrent/locks/ReentrantLock;Ljava/util/concurrent/atomic/AtomicBoolean;Ljava/util/concurrent/locks/Condition;)V
        //    50: invokespecial   android/support/v4/provider/SelfDestructiveThread.post:(Ljava/lang/Runnable;)V
        //    53: aload_3        
        //    54: invokevirtual   java/util/concurrent/locks/ReentrantLock.lock:()V
        //    57: aload           6
        //    59: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
        //    62: ifeq            130
        //    65: getstatic       java/util/concurrent/TimeUnit.MILLISECONDS:Ljava/util/concurrent/TimeUnit;
        //    68: iload_2        
        //    69: i2l            
        //    70: invokevirtual   java/util/concurrent/TimeUnit.toNanos:(J)J
        //    73: lstore          7
        //    75: aload           4
        //    77: lload           7
        //    79: invokeinterface java/util/concurrent/locks/Condition.awaitNanos:(J)J
        //    84: lstore          9
        //    86: lload           9
        //    88: lstore          7
        //    90: aload           6
        //    92: invokevirtual   java/util/concurrent/atomic/AtomicBoolean.get:()Z
        //    95: ifeq            146
        //    98: lload           7
        //   100: lconst_0       
        //   101: lcmp           
        //   102: ifle            158
        //   105: iconst_1       
        //   106: istore_2       
        //   107: iload_2        
        //   108: ifne            163
        //   111: new             Ljava/lang/InterruptedException;
        //   114: astore_1       
        //   115: aload_1        
        //   116: ldc             "timeout"
        //   118: invokespecial   java/lang/InterruptedException.<init>:(Ljava/lang/String;)V
        //   121: aload_1        
        //   122: athrow         
        //   123: astore_1       
        //   124: aload_3        
        //   125: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   128: aload_1        
        //   129: athrow         
        //   130: aload           5
        //   132: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //   135: astore_1       
        //   136: aload_3        
        //   137: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   140: aload_1        
        //   141: areturn        
        //   142: astore_1       
        //   143: goto            90
        //   146: aload           5
        //   148: invokevirtual   java/util/concurrent/atomic/AtomicReference.get:()Ljava/lang/Object;
        //   151: astore_1       
        //   152: aload_3        
        //   153: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   156: aload_1        
        //   157: areturn        
        //   158: iconst_0       
        //   159: istore_2       
        //   160: goto            107
        //   163: goto            75
        //    Exceptions:
        //  throws java.lang.InterruptedException
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/util/concurrent/Callable<TT;>;I)TT;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  57     75     123    130    Any
        //  75     86     142    146    Ljava/lang/InterruptedException;
        //  75     86     123    130    Any
        //  90     98     123    130    Any
        //  111    123    123    130    Any
        //  130    136    123    130    Any
        //  146    152    123    130    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0075:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
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
    
    public interface ReplyCallback<T>
    {
        void onReply(final T p0);
    }
}
