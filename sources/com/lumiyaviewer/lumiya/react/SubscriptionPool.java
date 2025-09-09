package com.lumiyaviewer.lumiya.react;

import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SubscriptionPool<K, T> implements Unsubscribable<K, T>, Refreshable<K>, ResultHandler<K, T>, Subscribable<K, T>, RequestSource<K, T> {
    @Nullable
    private Executor cacheInvalidateExecutor;
    @Nullable
    private Refreshable<K> cacheInvalidateHandler;
    @Nullable
    private Executor disposeExecutor;
    @Nullable
    private DisposeHandler<T> disposeHandler;
    private final Map<K, SubscriptionRequestedList<K, T>> entries;
    private final Object lock;
    private final ReferenceQueue<Subscription<K, T>> refQueue;
    @Nullable
    private RequestHandler<K> requestHandler;
    private boolean requestOnce;

    private static class SubscriptionRequestedList<K, T> extends SubscriptionList<K, T> {
        boolean requested;

        private SubscriptionRequestedList() {
            this.requested = false;
        }

        /* synthetic */ SubscriptionRequestedList(SubscriptionRequestedList subscriptionRequestedList) {
            this();
        }
    }

    public SubscriptionPool() {
        this.entries = new HashMap();
        this.lock = new Object();
        this.refQueue = new ReferenceQueue<>();
        this.requestHandler = null;
        this.disposeHandler = null;
        this.disposeExecutor = null;
        this.cacheInvalidateExecutor = null;
        this.cacheInvalidateHandler = null;
        this.requestOnce = false;
        this.requestHandler = null;
    }

    public SubscriptionPool(@Nullable RequestHandler<K> requestHandler2) {
        this.entries = new HashMap();
        this.lock = new Object();
        this.refQueue = new ReferenceQueue<>();
        this.requestHandler = null;
        this.disposeHandler = null;
        this.disposeExecutor = null;
        this.cacheInvalidateExecutor = null;
        this.cacheInvalidateHandler = null;
        this.requestOnce = false;
        this.requestHandler = requestHandler2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000e, code lost:
        r0 = (com.lumiyaviewer.lumiya.react.Subscription.SubscriptionReference) r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void collectReferences() {
        /*
            r7 = this;
            r6 = 1
            r5 = 0
        L_0x0002:
            java.lang.ref.ReferenceQueue<com.lumiyaviewer.lumiya.react.Subscription<K, T>> r0 = r7.refQueue
            java.lang.ref.Reference r0 = r0.poll()
            if (r0 == 0) goto L_0x0050
            boolean r1 = r0 instanceof com.lumiyaviewer.lumiya.react.Subscription.SubscriptionReference
            if (r1 == 0) goto L_0x0002
            com.lumiyaviewer.lumiya.react.Subscription$SubscriptionReference r0 = (com.lumiyaviewer.lumiya.react.Subscription.SubscriptionReference) r0
            java.lang.Object r2 = r0.getKey()
            if (r2 == 0) goto L_0x0002
            java.lang.String r1 = "UserPic: collecting reference for %s"
            java.lang.Object[] r3 = new java.lang.Object[r6]
            java.lang.String r4 = r2.toString()
            r3[r5] = r4
            com.lumiyaviewer.lumiya.Debug.Printf(r1, r3)
            java.lang.Object r3 = r7.lock
            monitor-enter(r3)
            java.util.Map<K, com.lumiyaviewer.lumiya.react.SubscriptionPool$SubscriptionRequestedList<K, T>> r1 = r7.entries     // Catch:{ all -> 0x004d }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x004d }
            com.lumiyaviewer.lumiya.react.SubscriptionList r1 = (com.lumiyaviewer.lumiya.react.SubscriptionList) r1     // Catch:{ all -> 0x004d }
            if (r1 == 0) goto L_0x004b
            r1.removeByReference(r0)     // Catch:{ all -> 0x004d }
            boolean r0 = r1.isEmpty()     // Catch:{ all -> 0x004d }
            if (r0 == 0) goto L_0x004b
            java.util.Map<K, com.lumiyaviewer.lumiya.react.SubscriptionPool$SubscriptionRequestedList<K, T>> r0 = r7.entries     // Catch:{ all -> 0x004d }
            r0.remove(r2)     // Catch:{ all -> 0x004d }
            com.lumiyaviewer.lumiya.react.RequestHandler<K> r0 = r7.requestHandler     // Catch:{ all -> 0x004d }
            if (r0 == 0) goto L_0x0048
            com.lumiyaviewer.lumiya.react.RequestHandler<K> r0 = r7.requestHandler     // Catch:{ all -> 0x004d }
            r0.onRequestCancelled(r2)     // Catch:{ all -> 0x004d }
        L_0x0048:
            r7.disposeOldData(r1)     // Catch:{ all -> 0x004d }
        L_0x004b:
            monitor-exit(r3)
            goto L_0x0002
        L_0x004d:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x0050:
            java.lang.Object r1 = r7.lock
            monitor-enter(r1)
            java.lang.String r0 = "UserPic: subscriptions = %d"
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x006b }
            java.util.Map<K, com.lumiyaviewer.lumiya.react.SubscriptionPool$SubscriptionRequestedList<K, T>> r3 = r7.entries     // Catch:{ all -> 0x006b }
            int r3 = r3.size()     // Catch:{ all -> 0x006b }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x006b }
            r4 = 0
            r2[r4] = r3     // Catch:{ all -> 0x006b }
            com.lumiyaviewer.lumiya.Debug.Printf(r0, r2)     // Catch:{ all -> 0x006b }
            monitor-exit(r1)
            return
        L_0x006b:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.react.SubscriptionPool.collectReferences():void");
    }

    private void disposeOldData(SubscriptionList<K, T> subscriptionList) {
        T data;
        if (this.disposeHandler != null && (data = subscriptionList.getData()) != null) {
            if (this.disposeExecutor != null) {
                this.disposeExecutor.execute(new $Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo(this, data));
            } else {
                this.disposeHandler.onDispose(data);
            }
        }
    }

    public ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler2) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler2;
        }
        return this;
    }

    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler2) {
        synchronized (this.lock) {
            if (this.requestHandler == requestHandler2) {
                this.requestHandler = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8106  reason: not valid java name */
    public /* synthetic */ void m40lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(Object obj) {
        this.disposeHandler.onDispose(obj);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8749  reason: not valid java name */
    public /* synthetic */ void m41lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8749(Object obj) {
        this.cacheInvalidateHandler.requestUpdate(obj);
    }

    public void onResultData(@Nonnull K k, T t) {
        List<Subscription> list;
        collectReferences();
        synchronized (this.lock) {
            SubscriptionRequestedList subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList != null) {
                subscriptionRequestedList.setData(t);
                subscriptionRequestedList.requested = false;
                list = subscriptionRequestedList.getSubscriptions(false);
            } else {
                list = null;
            }
        }
        if (list != null) {
            for (Subscription onData : list) {
                onData.onData(t);
            }
        }
    }

    public void onResultError(@Nonnull K k, Throwable th) {
        List<Subscription> list;
        collectReferences();
        synchronized (this.lock) {
            SubscriptionRequestedList subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList != null) {
                subscriptionRequestedList.setError(th);
                subscriptionRequestedList.requested = false;
                list = subscriptionRequestedList.getSubscriptions(false);
            } else {
                list = null;
            }
        }
        if (list != null) {
            for (Subscription onError : list) {
                onError.onError(th);
            }
        }
    }

    public void requestUpdate(K k) {
        synchronized (this.lock) {
            if (this.cacheInvalidateHandler != null) {
                if (this.cacheInvalidateExecutor != null) {
                    this.cacheInvalidateExecutor.execute(new Runnable(this, k) {

                        /* renamed from: -$f0 */
                        private final /* synthetic */ Object f17$f0;

                        /* renamed from: -$f1 */
                        private final /* synthetic */ Object f18$f1;

                        private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo.1.$m$0():void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo.1.$m$0():void, class status: UNLOADED
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
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:260)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:70)
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

                        public final void run(
/*
Method generation error in method: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo.1.run():void, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.react.-$Lambda$FQ4ueWG6sVQMwgP3YGPP2nbRyFo.1.run():void, class status: UNLOADED
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
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:260)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:70)
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
                    });
                } else {
                    this.cacheInvalidateHandler.requestUpdate(k);
                }
            }
            SubscriptionRequestedList subscriptionRequestedList = this.entries.get(k);
            if (!(subscriptionRequestedList == null || this.requestHandler == null)) {
                if (this.requestHandler instanceof Refreshable) {
                    ((Refreshable) this.requestHandler).requestUpdate(k);
                } else if (!this.requestOnce || (!subscriptionRequestedList.requested)) {
                    subscriptionRequestedList.requested = true;
                    this.requestHandler.onRequest(k);
                }
            }
        }
    }

    public void requestUpdateAll() {
        synchronized (this.lock) {
            for (Map.Entry entry : this.entries.entrySet()) {
                Object key = entry.getKey();
                SubscriptionRequestedList subscriptionRequestedList = (SubscriptionRequestedList) entry.getValue();
                if (!(subscriptionRequestedList == null || this.requestHandler == null)) {
                    if (!this.requestOnce || (!subscriptionRequestedList.requested)) {
                        subscriptionRequestedList.requested = true;
                        this.requestHandler.onRequest(key);
                    }
                }
            }
        }
    }

    public void requestUpdateSome(Predicate<K> predicate) {
        HashSet hashSet;
        HashSet<Object> hashSet2 = null;
        RequestHandler<K> requestHandler2 = this.requestHandler;
        if (requestHandler2 != null) {
            synchronized (this.lock) {
                for (Map.Entry entry : this.entries.entrySet()) {
                    Object key = entry.getKey();
                    SubscriptionRequestedList subscriptionRequestedList = (SubscriptionRequestedList) entry.getValue();
                    if (subscriptionRequestedList == null || !predicate.apply(key) || (this.requestOnce && !(!subscriptionRequestedList.requested))) {
                        hashSet = hashSet2;
                    } else {
                        subscriptionRequestedList.requested = true;
                        hashSet = hashSet2 == null ? new HashSet() : hashSet2;
                        hashSet.add(key);
                    }
                    hashSet2 = hashSet;
                }
            }
            if (hashSet2 != null) {
                for (Object onRequest : hashSet2) {
                    requestHandler2.onRequest(onRequest);
                }
            }
        }
    }

    public SubscriptionPool<K, T> setCacheInvalidateHandler(Refreshable<K> refreshable, @Nullable Executor executor) {
        this.cacheInvalidateHandler = refreshable;
        this.cacheInvalidateExecutor = executor;
        return this;
    }

    public SubscriptionPool<K, T> setDisposeHandler(DisposeHandler<T> disposeHandler2, @Nullable Executor executor) {
        this.disposeHandler = disposeHandler2;
        this.disposeExecutor = executor;
        return this;
    }

    public SubscriptionPool<K, T> setRequestOnce(boolean z) {
        this.requestOnce = z;
        return this;
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, (Executor) null, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return subscribe(k, (Executor) null, onData, onError);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, executor, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        Subscription<K, T> subscription = new Subscription<>(k, this, executor, onData, onError, this.refQueue);
        synchronized (this.lock) {
            boolean z = false;
            SubscriptionRequestedList subscriptionRequestedList = this.entries.get(k);
            if (subscriptionRequestedList == null) {
                subscriptionRequestedList = new SubscriptionRequestedList((SubscriptionRequestedList) null);
                this.entries.put(k, subscriptionRequestedList);
                z = true;
            }
            subscriptionRequestedList.addSubscription(subscription);
            Throwable error = subscriptionRequestedList.getError();
            if (error != null) {
                subscription.onError(error);
            } else {
                Object data = subscriptionRequestedList.getData();
                if (data != null) {
                    subscription.onData(data);
                }
            }
            if (z && this.requestHandler != null) {
                subscriptionRequestedList.requested = true;
                this.requestHandler.onRequest(k);
            }
        }
        collectReferences();
        return subscription;
    }

    public void unsubscribe(Subscription<K, T> subscription) {
        K key = subscription.getKey();
        synchronized (this.lock) {
            SubscriptionList subscriptionList = this.entries.get(key);
            if (subscriptionList != null) {
                subscriptionList.removeSubscription(subscription);
                if (subscriptionList.isEmpty()) {
                    this.entries.remove(key);
                    if (this.requestHandler != null) {
                        this.requestHandler.onRequestCancelled(key);
                    }
                    disposeOldData(subscriptionList);
                }
            }
        }
        collectReferences();
    }

    public SubscriptionPool<K, T> withRequestHandler(@Nonnull RequestHandler<K> requestHandler2) {
        synchronized (this.lock) {
            this.requestHandler = requestHandler2;
        }
        return this;
    }
}
