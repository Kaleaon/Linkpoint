// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Throwables;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;
import com.google.common.annotations.Beta;

@Beta
public final class Monitor
{
    @GuardedBy("lock")
    private Guard activeGuards;
    private final boolean fair;
    private final ReentrantLock lock;
    
    public Monitor() {
        this(false);
    }
    
    public Monitor(final boolean b) {
        this.activeGuards = null;
        this.fair = b;
        this.lock = new ReentrantLock(b);
    }
    
    @GuardedBy("lock")
    private void await(final Guard guard, final boolean b) throws InterruptedException {
        Label_0033: {
            if (b) {
                break Label_0033;
            }
            while (true) {
                this.beginWaitingFor(guard);
                try {
                    do {
                        guard.condition.await();
                    } while (!guard.isSatisfied());
                    return;
                    this.signalNextWaiter();
                    continue;
                }
                finally {
                    this.endWaitingFor(guard);
                }
                break;
            }
        }
    }
    
    @GuardedBy("lock")
    private boolean awaitNanos(final Guard guard, long awaitNanos, final boolean b) throws InterruptedException {
        int n = 1;
        while (true) {
            int n2;
            if (awaitNanos > 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                break;
            }
            Label_0084: {
                if (n != 0) {
                    break Label_0084;
                }
                n2 = n;
                int n3;
                Label_0089_Outer:Label_0122_Outer:
                while (true) {
                    n3 = n2;
                    while (true) {
                        Label_0133: {
                            try {
                                awaitNanos = guard.condition.awaitNanos(awaitNanos);
                                n = n2;
                                n3 = n2;
                                if (guard.isSatisfied()) {
                                    return true;
                                }
                                break;
                                Label_0104: {
                                    n3 = n;
                                }
                                this.signalNextWaiter();
                                while (true) {
                                    n3 = n;
                                    this.beginWaitingFor(guard);
                                    n2 = 0;
                                    continue Label_0089_Outer;
                                    iftrue(Label_0104:)(b);
                                    continue Label_0122_Outer;
                                }
                            }
                            finally {
                                if (n3 == 0) {
                                    break Label_0133;
                                }
                            }
                            this.endWaitingFor(guard);
                            return true;
                        }
                        this.endWaitingFor(guard);
                        continue;
                    }
                }
            }
        }
        if (n == 0) {
            this.endWaitingFor(guard);
        }
        return false;
    }
    
    @GuardedBy("lock")
    private void awaitUninterruptibly(final Guard guard, final boolean b) {
        Label_0033: {
            if (b) {
                break Label_0033;
            }
            while (true) {
                this.beginWaitingFor(guard);
                try {
                    do {
                        guard.condition.awaitUninterruptibly();
                    } while (!guard.isSatisfied());
                    return;
                    this.signalNextWaiter();
                    continue;
                }
                finally {
                    this.endWaitingFor(guard);
                }
                break;
            }
        }
    }
    
    @GuardedBy("lock")
    private void beginWaitingFor(final Guard activeGuards) {
        if (activeGuards.waiterCount++ == 0) {
            activeGuards.next = this.activeGuards;
            this.activeGuards = activeGuards;
        }
    }
    
    @GuardedBy("lock")
    private void endWaitingFor(final Guard guard) {
        final int waiterCount = guard.waiterCount - 1;
        guard.waiterCount = waiterCount;
        if (waiterCount == 0) {
            Guard activeGuards = this.activeGuards;
            Guard guard2 = null;
            while (activeGuards != guard) {
                final Guard next = activeGuards.next;
                guard2 = activeGuards;
                activeGuards = next;
            }
            if (guard2 != null) {
                guard2.next = activeGuards.next;
            }
            else {
                this.activeGuards = activeGuards.next;
            }
            activeGuards.next = null;
        }
    }
    
    private static long initNanoTime(long nanoTime) {
        int n;
        if (nanoTime > 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            return 0L;
        }
        if ((nanoTime = System.nanoTime()) == 0L) {
            nanoTime = 1L;
        }
        return nanoTime;
    }
    
    @GuardedBy("lock")
    private boolean isSatisfied(final Guard guard) {
        try {
            return guard.isSatisfied();
        }
        catch (final Throwable t) {
            this.signalAllWaiters();
            throw Throwables.propagate(t);
        }
    }
    
    private static long remainingNanos(long n, final long n2) {
        final long n3 = 0L;
        int n4;
        if (n2 > 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            n = n3;
        }
        else {
            n = n2 - (System.nanoTime() - n);
        }
        return n;
    }
    
    @GuardedBy("lock")
    private void signalAllWaiters() {
        for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
            guard.condition.signalAll();
        }
    }
    
    @GuardedBy("lock")
    private void signalNextWaiter() {
        for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
            if (this.isSatisfied(guard)) {
                guard.condition.signal();
                break;
            }
        }
    }
    
    private static long toSafeNanos(long nanos, final TimeUnit timeUnit) {
        final long n = 0L;
        nanos = timeUnit.toNanos(nanos);
        int n2;
        if (nanos > 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            nanos = n;
        }
        else {
            int n3;
            if (nanos <= 6917529027641081853L) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                nanos = 6917529027641081853L;
            }
        }
        return nanos;
    }
    
    public void enter() {
        this.lock.lock();
    }
    
    public boolean enter(final long p0, final TimeUnit p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_3        
        //     2: invokestatic    com/google/common/util/concurrent/Monitor.toSafeNanos:(JLjava/util/concurrent/TimeUnit;)J
        //     5: lstore          4
        //     7: aload_0        
        //     8: getfield        com/google/common/util/concurrent/Monitor.lock:Ljava/util/concurrent/locks/ReentrantLock;
        //    11: astore          6
        //    13: aload_0        
        //    14: getfield        com/google/common/util/concurrent/Monitor.fair:Z
        //    17: ifeq            52
        //    20: invokestatic    java/lang/Thread.interrupted:()Z
        //    23: istore          7
        //    25: invokestatic    java/lang/System.nanoTime:()J
        //    28: lstore          8
        //    30: lload           4
        //    32: lstore_1       
        //    33: aload           6
        //    35: lload_1        
        //    36: getstatic       java/util/concurrent/TimeUnit.NANOSECONDS:Ljava/util/concurrent/TimeUnit;
        //    39: invokevirtual   java/util/concurrent/locks/ReentrantLock.tryLock:(JLjava/util/concurrent/TimeUnit;)Z
        //    42: istore          10
        //    44: iload           7
        //    46: ifne            62
        //    49: iload           10
        //    51: ireturn        
        //    52: aload           6
        //    54: invokevirtual   java/util/concurrent/locks/ReentrantLock.tryLock:()Z
        //    57: ifeq            20
        //    60: iconst_1       
        //    61: ireturn        
        //    62: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //    65: invokevirtual   java/lang/Thread.interrupt:()V
        //    68: goto            49
        //    71: astore_3       
        //    72: lload           8
        //    74: lload           4
        //    76: invokestatic    com/google/common/util/concurrent/Monitor.remainingNanos:(JJ)J
        //    79: lstore_1       
        //    80: iconst_1       
        //    81: istore          7
        //    83: goto            33
        //    86: astore_3       
        //    87: iload           7
        //    89: ifne            94
        //    92: aload_3        
        //    93: athrow         
        //    94: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //    97: invokevirtual   java/lang/Thread.interrupt:()V
        //   100: goto            92
        //   103: astore_3       
        //   104: goto            87
        //   107: astore_3       
        //   108: iconst_1       
        //   109: istore          7
        //   111: goto            87
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  25     30     86     87     Any
        //  33     44     71     86     Ljava/lang/InterruptedException;
        //  33     44     103    107    Any
        //  72     80     107    114    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: logicalnot:boolean(invokevirtual:boolean(ReentrantLock::tryLock, var_6_0B:ReentrantLock))
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
    
    public boolean enterIf(final Guard guard) {
        Label_0028: {
            if (guard.monitor != this) {
                break Label_0028;
            }
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                final boolean satisfied = guard.isSatisfied();
                if (!satisfied) {
                    lock.unlock();
                }
                return satisfied;
                throw new IllegalMonitorStateException();
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public boolean enterIf(final Guard guard, final long n, final TimeUnit timeUnit) {
        Label_0032: {
            if (guard.monitor != this) {
                break Label_0032;
            }
            if (!this.enter(n, timeUnit)) {
                return false;
            }
            try {
                final boolean satisfied = guard.isSatisfied();
                if (!satisfied) {
                    this.lock.unlock();
                }
                return satisfied;
                throw new IllegalMonitorStateException();
            }
            finally {
                this.lock.unlock();
            }
        }
    }
    
    public boolean enterIfInterruptibly(final Guard guard) throws InterruptedException {
        Label_0028: {
            if (guard.monitor != this) {
                break Label_0028;
            }
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                final boolean satisfied = guard.isSatisfied();
                if (!satisfied) {
                    lock.unlock();
                }
                return satisfied;
                throw new IllegalMonitorStateException();
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public boolean enterIfInterruptibly(final Guard guard, final long timeout, final TimeUnit unit) throws InterruptedException {
        Label_0039: {
            if (guard.monitor != this) {
                break Label_0039;
            }
            final ReentrantLock lock = this.lock;
            if (!lock.tryLock(timeout, unit)) {
                return false;
            }
            try {
                final boolean satisfied = guard.isSatisfied();
                if (!satisfied) {
                    lock.unlock();
                }
                return satisfied;
                throw new IllegalMonitorStateException();
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public void enterInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }
    
    public boolean enterInterruptibly(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(timeout, unit);
    }
    
    public void enterWhen(final Guard guard) throws InterruptedException {
        Label_0034: {
            if (guard.monitor != this) {
                break Label_0034;
            }
            final ReentrantLock lock = this.lock;
            final boolean heldByCurrentThread = lock.isHeldByCurrentThread();
            lock.lockInterruptibly();
            try {
                if (!guard.isSatisfied()) {
                    this.await(guard, heldByCurrentThread);
                }
                return;
                throw new IllegalMonitorStateException();
            }
            finally {
                this.leave();
            }
        }
    }
    
    public boolean enterWhen(final Guard guard, long remainingNanos, final TimeUnit unit) throws InterruptedException {
        final long safeNanos = toSafeNanos(remainingNanos, unit);
        Label_0079: {
            if (guard.monitor != this) {
                break Label_0079;
            }
            final ReentrantLock lock = this.lock;
            boolean heldByCurrentThread = lock.isHeldByCurrentThread();
            Label_0087: {
                if (!this.fair) {
                    break Label_0087;
                }
                final long initNanoTime;
                Label_0036: {
                    initNanoTime = initNanoTime(safeNanos);
                }
                if (!lock.tryLock(remainingNanos, unit)) {
                    return false;
                }
                remainingNanos = initNanoTime;
                try {
                    while (true) {
                        while (true) {
                            Label_0071: {
                                if (!guard.isSatisfied()) {
                                    if (remainingNanos == 0L) {
                                        remainingNanos = safeNanos;
                                    }
                                    else {
                                        remainingNanos = remainingNanos(remainingNanos, safeNanos);
                                    }
                                    if (!this.awaitNanos(guard, remainingNanos, heldByCurrentThread)) {
                                        heldByCurrentThread = false;
                                        break Label_0071;
                                    }
                                }
                                heldByCurrentThread = true;
                            }
                            if (!heldByCurrentThread) {
                                lock.unlock();
                            }
                            return heldByCurrentThread;
                            remainingNanos = 0L;
                            continue;
                        }
                        throw new IllegalMonitorStateException();
                        iftrue(Label_0106:)(Thread.interrupted());
                        iftrue(Label_0036:)(!lock.tryLock());
                        continue;
                    }
                    Label_0106:
                    throw new InterruptedException();
                }
                finally {
                    EndFinally_1: {
                        if (!heldByCurrentThread) {
                            try {
                                this.signalNextWaiter();
                            }
                            finally {
                                lock.unlock();
                            }
                            break EndFinally_1;
                        }
                        lock.unlock();
                    }
                }
            }
        }
    }
    
    public void enterWhenUninterruptibly(final Guard guard) {
        Label_0034: {
            if (guard.monitor != this) {
                break Label_0034;
            }
            final ReentrantLock lock = this.lock;
            final boolean heldByCurrentThread = lock.isHeldByCurrentThread();
            lock.lock();
            try {
                if (!guard.isSatisfied()) {
                    this.awaitUninterruptibly(guard, heldByCurrentThread);
                }
                return;
                throw new IllegalMonitorStateException();
            }
            finally {
                this.leave();
            }
        }
    }
    
    public boolean enterWhenUninterruptibly(final Guard p0, final long p1, final TimeUnit p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload           4
        //     3: invokestatic    com/google/common/util/concurrent/Monitor.toSafeNanos:(JLjava/util/concurrent/TimeUnit;)J
        //     6: lstore          5
        //     8: aload_1        
        //     9: getfield        com/google/common/util/concurrent/Monitor$Guard.monitor:Lcom/google/common/util/concurrent/Monitor;
        //    12: aload_0        
        //    13: if_acmpne       84
        //    16: aload_0        
        //    17: getfield        com/google/common/util/concurrent/Monitor.lock:Ljava/util/concurrent/locks/ReentrantLock;
        //    20: astore          4
        //    22: lconst_0       
        //    23: lstore_2       
        //    24: aload           4
        //    26: invokevirtual   java/util/concurrent/locks/ReentrantLock.isHeldByCurrentThread:()Z
        //    29: istore          7
        //    31: invokestatic    java/lang/Thread.interrupted:()Z
        //    34: istore          8
        //    36: aload_0        
        //    37: getfield        com/google/common/util/concurrent/Monitor.fair:Z
        //    40: ifeq            92
        //    43: lload           5
        //    45: invokestatic    com/google/common/util/concurrent/Monitor.initNanoTime:(J)J
        //    48: lstore          9
        //    50: lload           5
        //    52: lstore_2       
        //    53: iload           8
        //    55: istore          11
        //    57: iload           11
        //    59: istore          8
        //    61: aload           4
        //    63: lload_2        
        //    64: getstatic       java/util/concurrent/TimeUnit.NANOSECONDS:Ljava/util/concurrent/TimeUnit;
        //    67: invokevirtual   java/util/concurrent/locks/ReentrantLock.tryLock:(JLjava/util/concurrent/TimeUnit;)Z
        //    70: istore          12
        //    72: iload           12
        //    74: ifne            172
        //    77: iload           11
        //    79: ifne            182
        //    82: iconst_0       
        //    83: ireturn        
        //    84: new             Ljava/lang/IllegalMonitorStateException;
        //    87: dup            
        //    88: invokespecial   java/lang/IllegalMonitorStateException.<init>:()V
        //    91: athrow         
        //    92: aload           4
        //    94: invokevirtual   java/util/concurrent/locks/ReentrantLock.tryLock:()Z
        //    97: istore          11
        //    99: iload           11
        //   101: ifeq            43
        //   104: iload           7
        //   106: istore          11
        //   108: iload           11
        //   110: istore          7
        //   112: iload           8
        //   114: istore          11
        //   116: lload_2        
        //   117: lstore          9
        //   119: aload_1        
        //   120: invokevirtual   com/google/common/util/concurrent/Monitor$Guard.isSatisfied:()Z
        //   123: ifne            210
        //   126: lload_2        
        //   127: lconst_0       
        //   128: lcmp           
        //   129: ifne            216
        //   132: lload_2        
        //   133: lstore          9
        //   135: lload           5
        //   137: invokestatic    com/google/common/util/concurrent/Monitor.initNanoTime:(J)J
        //   140: lstore_2       
        //   141: lload           5
        //   143: lstore          13
        //   145: lload_2        
        //   146: lstore          9
        //   148: aload_0        
        //   149: aload_1        
        //   150: lload           13
        //   152: iload           7
        //   154: invokespecial   com/google/common/util/concurrent/Monitor.awaitNanos:(Lcom/google/common/util/concurrent/Monitor$Guard;JZ)Z
        //   157: istore          7
        //   159: iload           7
        //   161: ifeq            230
        //   164: iload           11
        //   166: ifne            250
        //   169: iload           7
        //   171: ireturn        
        //   172: iload           11
        //   174: istore          8
        //   176: lload           9
        //   178: lstore_2       
        //   179: goto            104
        //   182: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //   185: invokevirtual   java/lang/Thread.interrupt:()V
        //   188: goto            82
        //   191: astore          15
        //   193: iconst_1       
        //   194: istore          8
        //   196: iconst_1       
        //   197: istore          11
        //   199: lload           9
        //   201: lload           5
        //   203: invokestatic    com/google/common/util/concurrent/Monitor.remainingNanos:(JJ)J
        //   206: lstore_2       
        //   207: goto            57
        //   210: iconst_1       
        //   211: istore          7
        //   213: goto            159
        //   216: lload_2        
        //   217: lstore          9
        //   219: lload_2        
        //   220: lload           5
        //   222: invokestatic    com/google/common/util/concurrent/Monitor.remainingNanos:(JJ)J
        //   225: lstore          13
        //   227: goto            145
        //   230: iload           11
        //   232: istore          8
        //   234: aload           4
        //   236: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   239: goto            164
        //   242: astore_1       
        //   243: iload           8
        //   245: ifne            289
        //   248: aload_1        
        //   249: athrow         
        //   250: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //   253: invokevirtual   java/lang/Thread.interrupt:()V
        //   256: goto            169
        //   259: astore          15
        //   261: iconst_1       
        //   262: istore          11
        //   264: iconst_0       
        //   265: istore          7
        //   267: lload           9
        //   269: lstore_2       
        //   270: goto            116
        //   273: astore_1       
        //   274: iload           11
        //   276: istore          8
        //   278: aload           4
        //   280: invokevirtual   java/util/concurrent/locks/ReentrantLock.unlock:()V
        //   283: iload           11
        //   285: istore          8
        //   287: aload_1        
        //   288: athrow         
        //   289: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //   292: invokevirtual   java/lang/Thread.interrupt:()V
        //   295: goto            248
        //   298: astore_1       
        //   299: goto            243
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  36     43     298    302    Any
        //  43     50     298    302    Any
        //  61     72     191    210    Ljava/lang/InterruptedException;
        //  61     72     242    243    Any
        //  92     99     298    302    Any
        //  119    126    259    273    Ljava/lang/InterruptedException;
        //  119    126    273    289    Any
        //  135    141    259    273    Ljava/lang/InterruptedException;
        //  135    141    273    289    Any
        //  148    159    259    273    Ljava/lang/InterruptedException;
        //  148    159    273    289    Any
        //  199    207    242    243    Any
        //  219    227    259    273    Ljava/lang/InterruptedException;
        //  219    227    273    289    Any
        //  234    239    242    243    Any
        //  278    283    242    243    Any
        //  287    289    242    243    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0145:
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
    
    public int getOccupiedDepth() {
        return this.lock.getHoldCount();
    }
    
    public int getQueueLength() {
        return this.lock.getQueueLength();
    }
    
    public int getWaitQueueLength(final Guard guard) {
        Label_0029: {
            if (guard.monitor != this) {
                break Label_0029;
            }
            this.lock.lock();
            try {
                return guard.waiterCount;
                throw new IllegalMonitorStateException();
            }
            finally {
                this.lock.unlock();
            }
        }
    }
    
    public boolean hasQueuedThread(final Thread thread) {
        return this.lock.hasQueuedThread(thread);
    }
    
    public boolean hasQueuedThreads() {
        return this.lock.hasQueuedThreads();
    }
    
    public boolean hasWaiters(final Guard guard) {
        boolean b = false;
        if (this.getWaitQueueLength(guard) > 0) {
            b = true;
        }
        return b;
    }
    
    public boolean isFair() {
        return this.fair;
    }
    
    public boolean isOccupied() {
        return this.lock.isLocked();
    }
    
    public boolean isOccupiedByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }
    
    public void leave() {
        final ReentrantLock lock = this.lock;
        try {
            if (lock.getHoldCount() == 1) {
                this.signalNextWaiter();
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    public boolean tryEnter() {
        return this.lock.tryLock();
    }
    
    public boolean tryEnterIf(final Guard guard) {
        Label_0031: {
            if (guard.monitor != this) {
                break Label_0031;
            }
            final ReentrantLock lock = this.lock;
            if (!lock.tryLock()) {
                return false;
            }
            try {
                final boolean satisfied = guard.isSatisfied();
                if (!satisfied) {
                    lock.unlock();
                }
                return satisfied;
                throw new IllegalMonitorStateException();
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    public void waitFor(final Guard guard) throws InterruptedException {
        boolean b = false;
        if (guard.monitor == this) {
            b = true;
        }
        if (b & this.lock.isHeldByCurrentThread()) {
            if (!guard.isSatisfied()) {
                this.await(guard, true);
            }
            return;
        }
        throw new IllegalMonitorStateException();
    }
    
    public boolean waitFor(final Guard guard, long safeNanos, final TimeUnit timeUnit) throws InterruptedException {
        boolean b = false;
        safeNanos = toSafeNanos(safeNanos, timeUnit);
        if (guard.monitor == this) {
            b = true;
        }
        if (!(b & this.lock.isHeldByCurrentThread())) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) {
            return true;
        }
        if (!Thread.interrupted()) {
            return this.awaitNanos(guard, safeNanos, true);
        }
        throw new InterruptedException();
    }
    
    public void waitForUninterruptibly(final Guard guard) {
        boolean b = false;
        if (guard.monitor == this) {
            b = true;
        }
        if (b & this.lock.isHeldByCurrentThread()) {
            if (!guard.isSatisfied()) {
                this.awaitUninterruptibly(guard, true);
            }
            return;
        }
        throw new IllegalMonitorStateException();
    }
    
    public boolean waitForUninterruptibly(final Guard p0, final long p1, final TimeUnit p2) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: istore          5
        //     3: lload_2        
        //     4: aload           4
        //     6: invokestatic    com/google/common/util/concurrent/Monitor.toSafeNanos:(JLjava/util/concurrent/TimeUnit;)J
        //     9: lstore          6
        //    11: aload_1        
        //    12: getfield        com/google/common/util/concurrent/Monitor$Guard.monitor:Lcom/google/common/util/concurrent/Monitor;
        //    15: aload_0        
        //    16: if_acmpeq       78
        //    19: iconst_0       
        //    20: istore          8
        //    22: iload           8
        //    24: aload_0        
        //    25: getfield        com/google/common/util/concurrent/Monitor.lock:Ljava/util/concurrent/locks/ReentrantLock;
        //    28: invokevirtual   java/util/concurrent/locks/ReentrantLock.isHeldByCurrentThread:()Z
        //    31: iand           
        //    32: ifeq            84
        //    35: aload_1        
        //    36: invokevirtual   com/google/common/util/concurrent/Monitor$Guard.isSatisfied:()Z
        //    39: ifne            92
        //    42: lload           6
        //    44: invokestatic    com/google/common/util/concurrent/Monitor.initNanoTime:(J)J
        //    47: lstore          9
        //    49: invokestatic    java/lang/Thread.interrupted:()Z
        //    52: istore          11
        //    54: lload           6
        //    56: lstore_2       
        //    57: iconst_1       
        //    58: istore          12
        //    60: aload_0        
        //    61: aload_1        
        //    62: lload_2        
        //    63: iload           12
        //    65: invokespecial   com/google/common/util/concurrent/Monitor.awaitNanos:(Lcom/google/common/util/concurrent/Monitor$Guard;JZ)Z
        //    68: istore          12
        //    70: iload           11
        //    72: ifne            94
        //    75: iload           12
        //    77: ireturn        
        //    78: iconst_1       
        //    79: istore          8
        //    81: goto            22
        //    84: new             Ljava/lang/IllegalMonitorStateException;
        //    87: dup            
        //    88: invokespecial   java/lang/IllegalMonitorStateException.<init>:()V
        //    91: athrow         
        //    92: iconst_1       
        //    93: ireturn        
        //    94: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //    97: invokevirtual   java/lang/Thread.interrupt:()V
        //   100: goto            75
        //   103: astore          4
        //   105: aload_1        
        //   106: invokevirtual   com/google/common/util/concurrent/Monitor$Guard.isSatisfied:()Z
        //   109: ifne            129
        //   112: lload           9
        //   114: lload           6
        //   116: invokestatic    com/google/common/util/concurrent/Monitor.remainingNanos:(JJ)J
        //   119: lstore_2       
        //   120: iconst_1       
        //   121: istore          11
        //   123: iconst_0       
        //   124: istore          12
        //   126: goto            60
        //   129: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //   132: invokevirtual   java/lang/Thread.interrupt:()V
        //   135: iconst_1       
        //   136: ireturn        
        //   137: astore_1       
        //   138: iload           11
        //   140: ifne            145
        //   143: aload_1        
        //   144: athrow         
        //   145: invokestatic    java/lang/Thread.currentThread:()Ljava/lang/Thread;
        //   148: invokevirtual   java/lang/Thread.interrupt:()V
        //   151: goto            143
        //   154: astore_1       
        //   155: iload           5
        //   157: istore          11
        //   159: goto            138
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                            
        //  -----  -----  -----  -----  --------------------------------
        //  60     70     103    137    Ljava/lang/InterruptedException;
        //  60     70     137    138    Any
        //  105    120    154    162    Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0129:
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
    
    @Beta
    public abstract static class Guard
    {
        final Condition condition;
        @Weak
        final Monitor monitor;
        @GuardedBy("monitor.lock")
        Guard next;
        @GuardedBy("monitor.lock")
        int waiterCount;
        
        protected Guard(final Monitor monitor) {
            this.waiterCount = 0;
            this.monitor = Preconditions.checkNotNull(monitor, (Object)"monitor");
            this.condition = monitor.lock.newCondition();
        }
        
        public abstract boolean isSatisfied();
    }
}
