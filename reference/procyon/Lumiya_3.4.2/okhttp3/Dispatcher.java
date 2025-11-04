// 
// Decompiled by Procyon v0.6.0
// 

package okhttp3;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import okhttp3.internal.Util;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;

public final class Dispatcher
{
    private ExecutorService executorService;
    private Runnable idleCallback;
    private int maxRequests;
    private int maxRequestsPerHost;
    private final Deque<RealCall.AsyncCall> readyAsyncCalls;
    private final Deque<RealCall.AsyncCall> runningAsyncCalls;
    private final Deque<RealCall> runningSyncCalls;
    
    public Dispatcher() {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningSyncCalls = new ArrayDeque<RealCall>();
    }
    
    public Dispatcher(final ExecutorService executorService) {
        this.maxRequests = 64;
        this.maxRequestsPerHost = 5;
        this.readyAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningAsyncCalls = new ArrayDeque<RealCall.AsyncCall>();
        this.runningSyncCalls = new ArrayDeque<RealCall>();
        this.executorService = executorService;
    }
    
    private <T> void finished(final Deque<T> p0, final T p1, final boolean p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: monitorenter   
        //     2: aload_1        
        //     3: aload_2        
        //     4: invokeinterface java/util/Deque.remove:(Ljava/lang/Object;)Z
        //     9: ifeq            35
        //    12: iload_3        
        //    13: ifne            52
        //    16: aload_0        
        //    17: invokevirtual   okhttp3/Dispatcher.runningCallsCount:()I
        //    20: istore          4
        //    22: aload_0        
        //    23: getfield        okhttp3/Dispatcher.idleCallback:Ljava/lang/Runnable;
        //    26: astore_1       
        //    27: aload_0        
        //    28: monitorexit    
        //    29: iload           4
        //    31: ifeq            59
        //    34: return         
        //    35: new             Ljava/lang/AssertionError;
        //    38: astore_1       
        //    39: aload_1        
        //    40: ldc             "Call wasn't in-flight!"
        //    42: invokespecial   java/lang/AssertionError.<init>:(Ljava/lang/Object;)V
        //    45: aload_1        
        //    46: athrow         
        //    47: astore_1       
        //    48: aload_0        
        //    49: monitorexit    
        //    50: aload_1        
        //    51: athrow         
        //    52: aload_0        
        //    53: invokespecial   okhttp3/Dispatcher.promoteCalls:()V
        //    56: goto            16
        //    59: aload_1        
        //    60: ifnull          34
        //    63: aload_1        
        //    64: invokeinterface java/lang/Runnable.run:()V
        //    69: goto            34
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/util/Deque<TT;>;TT;Z)V
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  2      12     47     52     Any
        //  16     29     47     52     Any
        //  35     47     47     52     Any
        //  48     50     47     52     Any
        //  52     56     47     52     Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(var_1_2F:Runnable, aconstnull:Runnable())
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
        //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
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
    
    private void promoteCalls() {
        if (this.runningAsyncCalls.size() >= this.maxRequests) {
            return;
        }
        if (!this.readyAsyncCalls.isEmpty()) {
            final Iterator<RealCall.AsyncCall> iterator = this.readyAsyncCalls.iterator();
            while (iterator.hasNext()) {
                final RealCall.AsyncCall asyncCall = iterator.next();
                if (this.runningCallsForHost(asyncCall) < this.maxRequestsPerHost) {
                    iterator.remove();
                    this.runningAsyncCalls.add(asyncCall);
                    this.executorService().execute(asyncCall);
                }
                if (this.runningAsyncCalls.size() >= this.maxRequests) {
                    return;
                }
            }
        }
    }
    
    private int runningCallsForHost(final RealCall.AsyncCall asyncCall) {
        final Iterator<RealCall.AsyncCall> iterator = this.runningAsyncCalls.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            if (!iterator.next().host().equals(asyncCall.host())) {
                continue;
            }
            ++n;
        }
        return n;
    }
    
    public void cancelAll() {
    Label_0050_Outer:
        while (true) {
            while (true) {
                Object o = null;
            Label_0105:
                while (true) {
                    synchronized (this) {
                        final Iterator<RealCall.AsyncCall> iterator = this.readyAsyncCalls.iterator();
                        while (iterator.hasNext()) {
                            iterator.next().get().cancel();
                        }
                        o = this.runningAsyncCalls.iterator();
                        if (!((Iterator)o).hasNext()) {
                            o = this.runningSyncCalls.iterator();
                            if (!((Iterator)o).hasNext()) {
                                return;
                            }
                            break Label_0105;
                        }
                    }
                    ((RealCall.AsyncCall)((Iterator)o).next()).get().cancel();
                    continue Label_0050_Outer;
                }
                ((RealCall)((Iterator)o).next()).cancel();
                continue;
            }
        }
    }
    
    void enqueue(final RealCall.AsyncCall asyncCall) {
        synchronized (this) {
            if (this.runningAsyncCalls.size() < this.maxRequests && this.runningCallsForHost(asyncCall) < this.maxRequestsPerHost) {
                this.runningAsyncCalls.add(asyncCall);
                this.executorService().execute(asyncCall);
            }
            else {
                this.readyAsyncCalls.add(asyncCall);
            }
        }
    }
    
    void executed(final RealCall realCall) {
        synchronized (this) {
            this.runningSyncCalls.add(realCall);
        }
    }
    
    public ExecutorService executorService() {
        synchronized (this) {
            if (this.executorService == null) {
                this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
            }
            return this.executorService;
        }
    }
    
    void finished(final RealCall.AsyncCall asyncCall) {
        this.finished(this.runningAsyncCalls, asyncCall, true);
    }
    
    void finished(final RealCall realCall) {
        this.finished(this.runningSyncCalls, realCall, false);
    }
    
    public int getMaxRequests() {
        synchronized (this) {
            return this.maxRequests;
        }
    }
    
    public int getMaxRequestsPerHost() {
        synchronized (this) {
            return this.maxRequestsPerHost;
        }
    }
    
    public List<Call> queuedCalls() {
        synchronized (this) {
            final ArrayList list = new ArrayList();
            final Iterator<RealCall.AsyncCall> iterator = this.readyAsyncCalls.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next().get());
            }
            return (List<Call>)Collections.unmodifiableList((List<?>)list);
        }
    }
    
    public int queuedCallsCount() {
        synchronized (this) {
            return this.readyAsyncCalls.size();
        }
    }
    
    public List<Call> runningCalls() {
        synchronized (this) {
            final ArrayList list = new ArrayList();
            list.addAll(this.runningSyncCalls);
            final Iterator<RealCall.AsyncCall> iterator = this.runningAsyncCalls.iterator();
            while (iterator.hasNext()) {
                list.add(iterator.next().get());
            }
            return (List<Call>)Collections.unmodifiableList((List<?>)list);
        }
    }
    
    public int runningCallsCount() {
        synchronized (this) {
            return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
        }
    }
    
    public void setIdleCallback(final Runnable idleCallback) {
        synchronized (this) {
            this.idleCallback = idleCallback;
        }
    }
    
    public void setMaxRequests(final int n) {
        monitorenter(this);
        Label_0019: {
            if (n < 1) {
                break Label_0019;
            }
            try {
                this.maxRequests = n;
                this.promoteCalls();
                return;
                throw new IllegalArgumentException("max < 1: " + n);
            }
            finally {
                monitorexit(this);
            }
        }
    }
    
    public void setMaxRequestsPerHost(final int n) {
        monitorenter(this);
        Label_0019: {
            if (n < 1) {
                break Label_0019;
            }
            try {
                this.maxRequestsPerHost = n;
                this.promoteCalls();
                return;
                throw new IllegalArgumentException("max < 1: " + n);
            }
            finally {
                monitorexit(this);
            }
        }
    }
}
