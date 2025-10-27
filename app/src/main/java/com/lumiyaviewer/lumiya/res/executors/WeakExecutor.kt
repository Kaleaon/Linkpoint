package com.lumiyaviewer.lumiya.res.executors

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.res.collections.WeakQueue
import com.lumiyaviewer.lumiya.utils.HasPriority
import java.lang.ref.WeakReference
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.RunnableFuture
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import androidx.annotation.NonNull

class WeakExecutor : ThreadPoolExecutor {
    private val usePriorities: Boolean = false

    private class ComparableFutureTask<T> : FutureTask<T> : Comparable<ComparableFutureTask<T>> {
        private Int priority

        private class WeakCallable<T> : Callable<T> {
            private WeakReference<Callable<T>> callableRef

            private WeakCallable(Callable<T> callable) {
                this.callableRef = WeakReference<>(callable)
            }

            /* synthetic */ WeakCallable(Callable callable, WeakCallable weakCallable) {
                this(callable)
            }

            T call() throws Exception {
                Callable callable = (Callable) this.callableRef.get()
                if (callable != null) {
                    return callable.call()
                }
                return null
            }
        }

        private class WeakRunnable : Runnable {
            private WeakReference<Runnable> runnableRef

            private WeakRunnable(Runnable runnable) {
                this.runnableRef = WeakReference<>(runnable)
            }

            /* synthetic */ WeakRunnable(Runnable runnable, WeakRunnable weakRunnable) {
                this(runnable)
            }

            fun run(): Unit {
                Runnable runnable = (Runnable) this.runnableRef.get()
                runnable?.run()
                }
            }
        }

        ComparableFutureTask(Runnable runnable, T t) {
            super(WeakRunnable(runnable, (WeakRunnable) null), t)
            if (runnable instanceof HasPriority) {
                this.priority = ((HasPriority) runnable).getPriority()
            } else {
                this.priority = 0
            }
        }

        ComparableFutureTask(Callable<T> callable) {
            super(WeakCallable(callable, (WeakCallable) null))
            if (callable instanceof HasPriority) {
                this.priority = ((HasPriority) callable).getPriority()
            } else {
                this.priority = 0
            }
        }

        fun compareTo(comparableFutureTask: ComparableFutureTask<T>): Int {
            if (comparableFutureTask == this) {
                return 0
            }
            return this.priority - comparableFutureTask.priority
        }
    }

    WeakExecutor(String str, Int i) {
        super(i, i, 60, TimeUnit.SECONDS, WeakQueue(), $Lambda$paN_qX4OegT79dFg6kmGbliJfA0(str))
        Debug.Printf("Executor for %s: maxThreads %d", str, Int.valueOf(i))
        allowCoreThreadTimeOut(true)
    }

    constructor(str: String, i: Int, blockingQueue: BlockingQueue<Runnable>) {
        super(i, i, 60, TimeUnit.SECONDS, blockingQueue, ThreadFactory(str) {

            /* renamed from: -$f0 */
            private /* synthetic */ Any f51$f0

            private /* synthetic */ java.lang.Thread $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0.1.$m$0(java.lang.Runnable):java.lang.Thread, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0.1.$m$0(java.lang.Runnable):java.lang.Thread, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/

            java.lang.Thread newThread(
/*
Method generation error in method: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0.1.newThread(java.lang.Runnable):java.lang.Thread, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.res.executors.-$Lambda$paN_qX4OegT79dFg6kmGbliJfA0.1.newThread(java.lang.Runnable):java.lang.Thread, class status: UNLOADED
            	at jadx.core.dex.nodes.MethodNode.getArgRegs(MethodNode.java:278)
            	at jadx.core.codegen.MethodGen.addDefinition(MethodGen.java:116)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:313)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
            	at java.util.ArrayList.forEach(ArrayList.java:1259)
            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            
*/
        Debug.Printf("Executor for %s: maxThreads %d", str, Int.valueOf(i))
        allowCoreThreadTimeOut(true)
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106  reason: not valid java name */
    /* synthetic */ Thread m107lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106(String str, Runnable runnable) {
        Thread thread = Thread(runnable, str)
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()))
        thread.setPriority(4)
        return thread
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531  reason: not valid java name */
    /* synthetic */ Thread m108lambda$com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531(String str, Runnable runnable) {
        Thread thread = Thread(runnable, str)
        Debug.Printf("Creating thread %s got %d", str, Long.valueOf(thread.getId()))
        thread.setPriority(4)
        return thread
    }

    /* access modifiers changed from: protected */
    <T> RunnableFuture<T> newTaskFor(Runnable runnable, T t) {
        return this.usePriorities ? ComparableFutureTask(runnable, t) : super.newTaskFor(runnable, t)
    }

    /* access modifiers changed from: protected */
    <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return this.usePriorities ? ComparableFutureTask(callable) : super.newTaskFor(callable)
    }
}
