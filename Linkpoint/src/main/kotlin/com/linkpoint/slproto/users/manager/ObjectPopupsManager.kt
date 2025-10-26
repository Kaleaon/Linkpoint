package com.linkpoint.slproto.users.manager

import com.linkpoint.Debug
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.Set
import java.util.WeakHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ObjectPopupsManager {
    private const val MAX_POPUPS: Int = 99
    private SLChatEvent displayedPopupEvent = null
    private val AtomicInteger freshPopupsCount = AtomicInteger(0)
    private SLChatEvent lastEvent = null
    private val Object listenerLock = Object()
    private WeakReference<ObjectPopupListener> objectPopupListener = null
    private Executor objectPopupListenerExecutor = null
    private val SubscribableList<SLChatEvent> objectPopups = SubscribableList<>()
    private Boolean popupAnimated = false
    private val Set<Object> popupWatchers = Collections.newSetFromMap(WeakHashMap())
    private val AtomicInteger unreadPopupCount = AtomicInteger(0)
    private val UserManager userManager

    interface ObjectPopupListener {
         fun onNewObjectPopup(sLChatEvent: SLChatEvent)

         fun onObjectPopupCountChanged(i: Int)
    }

    ObjectPopupsManager(UserManager userManager2) {
        this.userManager = userManager2
    }

     private fun addObjectPopupInternal(sLChatEvent: SLChatEvent) {
        this.objectPopups.add(0, sLChatEvent)
        while (this.objectPopups.size() > 99) {
            try {
                this.objectPopups.remove(this.objectPopups.size() - 1)
            } catch (Exception e) {
                Debug.Warning(e)
            }
        }
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_8558  reason: not valid java name */
    static /* synthetic */ Unit m337lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ObjectPopupsManager_8558(ObjectPopupListener objectPopupListener2) {
        objectPopupListener2.onObjectPopupCountChanged(0)
        objectPopupListener2.onNewObjectPopup((SLChatEvent) null)
    }

     private fun notifyCountUpdated() {
        Executor executor
        val objectPopupListener2: ObjectPopupListener = null
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null) {
                objectPopupListener2 = (ObjectPopupListener) this.objectPopupListener.get()
                executor = this.objectPopupListenerExecutor
            } else {
                executor = null
            }
        }
        if (objectPopupListener2 != null) {
            val size: Int = this.objectPopups.size()
            if (executor != null) {
                executor.execute(Runnable(size, objectPopupListener2) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Int f205$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f206$f1

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.4.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.4.$m$0():Unit, class status: UNLOADED
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

            } else {
                objectPopupListener2.onObjectPopupCountChanged(size)
            }
        }
    }

    /* access modifiers changed from: package-private */
    fun addObjectPopup(sLChatEvent: SLChatEvent) {
        Executor executor
        val z: Boolean = false
        val objectPopupListener2: ObjectPopupListener = null
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null) {
                objectPopupListener2 = (ObjectPopupListener) this.objectPopupListener.get()
                executor = this.objectPopupListenerExecutor
                if (objectPopupListener2 != null && this.displayedPopupEvent == null) {
                    this.displayedPopupEvent = sLChatEvent
                    this.popupAnimated = false
                    z = true
                }
            } else {
                executor = null
            }
            if (!(!this.popupWatchers.isEmpty())) {
                this.freshPopupsCount.incrementAndGet()
                this.unreadPopupCount.incrementAndGet()
                this.lastEvent = sLChatEvent
            }
        }
        if (!z) {
            addObjectPopupInternal(sLChatEvent)
            val size: Int = this.objectPopups.size()
            if (objectPopupListener2 != null) {
                if (executor != null) {
                    executor.execute(Runnable(size, objectPopupListener2) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Int f203$f0

                        /* renamed from: -$f1 */
                        private val /* synthetic */ Object f204$f1

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.3.$m$0():Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.3.$m$0():Unit, class status: UNLOADED
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
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                } else {
                    objectPopupListener2.onObjectPopupCountChanged(size)
                }
            }
        } else if (executor != null) {
            executor.execute(Runnable(objectPopupListener2, sLChatEvent) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f201$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f202$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.2.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.2.$m$0():Unit, class status: UNLOADED
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
                	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

        } else {
            objectPopupListener2.onNewObjectPopup(sLChatEvent)
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun addPopupWatcher(obj: Object) {
        synchronized (this.listenerLock) {
            this.popupWatchers.add(obj)
            this.freshPopupsCount.set(0)
            this.unreadPopupCount.set(0)
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x001f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    fun cancelObjectPopup(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r6) {
        /*
            r5 = this
            r1 = 0
            java.lang.Object r3 = r5.listenerLock
            monitor-enter(r3)
            if (r6 == 0) goto L_0x0046
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r0 = r5.displayedPopupEvent     // Catch:{ all -> 0x0049 }
            if (r6 != r0) goto L_0x0046
            r0 = 0
            r5.displayedPopupEvent = r0     // Catch:{ all -> 0x0049 }
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener> r0 = r5.objectPopupListener     // Catch:{ all -> 0x0049 }
            if (r0 == 0) goto L_0x0044
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener> r0 = r5.objectPopupListener     // Catch:{ all -> 0x0049 }
            java.lang.Object r0 = r0.get()     // Catch:{ all -> 0x0049 }
            com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager$ObjectPopupListener r0 = (com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.ObjectPopupListener) r0     // Catch:{ all -> 0x0049 }
        L_0x0019:
            java.util.concurrent.Executor r2 = r5.objectPopupListenerExecutor     // Catch:{ all -> 0x0049 }
        L_0x001b:
            com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent r4 = r5.lastEvent     // Catch:{ all -> 0x0049 }
            if (r6 != r4) goto L_0x0022
            r4 = 0
            r5.lastEvent = r4     // Catch:{ all -> 0x0049 }
        L_0x0022:
            monitor-exit(r3)
            if (r0 == 0) goto L_0x002f
            if (r2 == 0) goto L_0x004c
            com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE r1 = com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE
            r1.<init>(r0)
            r2.execute(r1)
        L_0x002f:
            com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList<com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent> r0 = r5.objectPopups
            val r0: Boolean = r0.remove(r6)
            if (r0 == 0) goto L_0x003a
            r5.notifyCountUpdated()
        L_0x003a:
            com.lumiyaviewer.lumiya.slproto.users.manager.UserManager r0 = r5.userManager
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager r0 = r0.getUnreadNotificationManager()
            r0.updateUnreadNotifications()
            return
        L_0x0044:
            r0 = r1
            goto L_0x0019
        L_0x0046:
            r0 = r1
            r2 = r1
            goto L_0x001b
        L_0x0049:
            r0 = move-exception
            monitor-exit(r3)
            throw r0
        L_0x004c:
            r0.onNewObjectPopup(r1)
            goto L_0x002f
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager.cancelObjectPopup(com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent):Unit")
    }

    /* access modifiers changed from: package-private */
    fun clearObjectPopups() {
        ObjectPopupListener objectPopupListener2
        Executor executor
        synchronized (this.listenerLock) {
            this.displayedPopupEvent = null
            objectPopupListener2 = this.objectPopupListener != null ? (ObjectPopupListener) this.objectPopupListener.get() : null
            executor = this.objectPopupListenerExecutor
            this.unreadPopupCount.set(0)
            this.freshPopupsCount.set(0)
            this.lastEvent = null
        }
        this.objectPopups.clear()
        if (objectPopupListener2 != null) {
            if (executor != null) {
                executor.execute(Runnable(objectPopupListener2) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f200$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.1.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$gJtxV6TiuzFNXMR7-6og75a4tFE.1.$m$0():Unit, class status: UNLOADED
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

            } else {
                objectPopupListener2.onObjectPopupCountChanged(0)
                objectPopupListener2.onNewObjectPopup((SLChatEvent) null)
            }
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

    fun dismissDisplayedObjectPopup(sLChatEvent: SLChatEvent) {
        synchronized (this.listenerLock) {
            if (sLChatEvent == this.displayedPopupEvent) {
                this.displayedPopupEvent = null
            } else if (sLChatEvent == null) {
                sLChatEvent = this.displayedPopupEvent
                this.displayedPopupEvent = null
            } else {
                sLChatEvent = null
            }
            this.freshPopupsCount.set(0)
            this.unreadPopupCount.set(0)
        }
        if (sLChatEvent != null) {
            addObjectPopupInternal(sLChatEvent)
            notifyCountUpdated()
        }
        this.userManager.getUnreadNotificationManager().updateUnreadNotifications()
    }

     public fun getDisplayedObjectPopup(): SLChatEvent {
        SLChatEvent sLChatEvent
        synchronized (this.listenerLock) {
            sLChatEvent = this.displayedPopupEvent
        }
        return sLChatEvent
    }

    /* access modifiers changed from: package-private */
    public UnreadNotificationInfo.ObjectPopupNotification getNotification(Boolean z) {
        UnreadNotificationInfo.ObjectPopupNotification create
        synchronized (this.listenerLock) {
            val i: Int = this.unreadPopupCount.get()
            val andSet: Int = z ? this.freshPopupsCount.getAndSet(0) : 0
            UnreadNotificationInfo.ObjectPopupMessage objectPopupMessage = null
            if (this.lastEvent instanceof SLChatTextEvent) {
                objectPopupMessage = UnreadNotificationInfo.ObjectPopupMessage.create(this.lastEvent.getSource().getSourceName(this.userManager), ((SLChatTextEvent) this.lastEvent).getRawText())
            }
            create = UnreadNotificationInfo.ObjectPopupNotification.create(andSet, i, objectPopupMessage)
        }
        return create
    }

     public fun getObjectPopupCount(): Int {
        return this.objectPopups.size()
    }

    public SubscribableList<SLChatEvent> getObjectPopups() {
        return this.objectPopups
    }

     public fun mustAnimatePopup(sLChatEvent: SLChatEvent): Boolean {
        val z: Boolean = false
        synchronized (this.listenerLock) {
            if (sLChatEvent == this.displayedPopupEvent) {
                z = !this.popupAnimated
                this.popupAnimated = true
            }
        }
        return z
    }

    fun removeObjectPopupListener(objectPopupListener2: ObjectPopupListener) {
        synchronized (this.listenerLock) {
            if (this.objectPopupListener != null && this.objectPopupListener.get() == objectPopupListener2) {
                this.objectPopupListener = null
                this.objectPopupListenerExecutor = null
            }
        }
    }

    fun removePopupWatcher(obj: Object) {
        synchronized (this.listenerLock) {
            this.popupWatchers.remove(obj)
        }
    }

    fun setObjectPopupListener(objectPopupListener2: ObjectPopupListener, executor: Executor) {
        synchronized (this.listenerLock) {
            this.objectPopupListener = WeakReference<>(objectPopupListener2)
            this.objectPopupListenerExecutor = executor
        }
    }
}
