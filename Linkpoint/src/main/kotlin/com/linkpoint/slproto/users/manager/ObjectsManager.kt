package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.react.RequestSource
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLParcelInfo
import com.linkpoint.slproto.inventory.SLTaskInventory
import com.linkpoint.slproto.objects.SLObjectDisplayInfo
import com.linkpoint.slproto.objects.SLObjectFilterInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.objects.SLObjectProfileData
import com.linkpoint.slproto.types.AgentPosition
import com.linkpoint.slproto.types.ImmutableVector
import com.linkpoint.slproto.users.MultipleChatterNameRetriever
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ObjectsManager {
    /* access modifiers changed from: private */
    public SLObjectFilterInfo filterInfo = SLObjectFilterInfo.create()
    /* access modifiers changed from: private */
    val Object filterLock = Object()
    private val SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarStatePool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val MultipleChatterNameRetriever nameRetriever
    /* access modifiers changed from: private */
    val SubscriptionPool<SubscriptionSingleKey, ObjectDisplayList> objectDisplayListPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val SubscriptionPool<Integer, SLObjectProfileData> objectProfilePool = SubscriptionPool<>()
    private val SimpleRequestHandler<Integer> objectProfileRequestHandler = SimpleRequestHandler<Integer>() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$2_3438  reason: not valid java name */
        public /* synthetic */ Unit m348lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$2_3438(SLAgentCircuit sLAgentCircuit, Integer num) {
            val objectProfile: SLObjectProfileData = sLAgentCircuit.getObjectProfile(num.intValue())
            if (objectProfile != null) {
                ObjectsManager.this.objectProfilePool.onResultData(num, objectProfile)
            } else {
                ObjectsManager.this.objectProfilePool.onResultError(num, ObjectDoesNotExistException(num.intValue(), (ObjectDoesNotExistException) null))
            }
        }

        fun onRequest(num: Integer) {
            val activeAgentCircuit: SLAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(Runnable(this, activeAgentCircuit, num) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f209$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f210$f1

                    /* renamed from: -$f2 */
                    private val /* synthetic */ Object f211$f2

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.1.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.1.$m$0():Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                    	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                    	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    
*/

            } else {
                ObjectsManager.this.objectProfilePool.onResultError(num, SLGridConnection.NotConnectedException())
            }
        }
    }
    private val MultipleChatterNameRetriever.OnChatterNameUpdated onChatterNameUpdated = $Lambda$n3FxEEuksYOCADj00lseQFiZ3z4(this)
    /* access modifiers changed from: private */
    val AtomicReference<SLParcelInfo> parcelInfo = AtomicReference<>((Object) null)
    private val SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimationsPool = SubscriptionSingleDataPool<>()
    private val SubscriptionPool<Integer, SLTaskInventory> taskInventoryPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val SubscriptionPool<UUID, ImmutableList<SLObjectInfo>> touchableObjectsPool = SubscriptionPool<>()
    private val SimpleRequestHandler<UUID> touchableObjectsRequestHandler = SimpleRequestHandler<UUID>() {
        /* access modifiers changed from: package-private */
        /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$3_4319  reason: not valid java name */
        public /* synthetic */ Unit m349lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$3_4319(SLAgentCircuit sLAgentCircuit, UUID uuid) {
            val userTouchableObjects: ImmutableList<SLObjectInfo> = sLAgentCircuit.getGridConnection().parcelInfo.getUserTouchableObjects(sLAgentCircuit, uuid)
            if (userTouchableObjects != null) {
                ObjectsManager.this.touchableObjectsPool.onResultData(uuid, userTouchableObjects)
            } else {
                ObjectsManager.this.touchableObjectsPool.onResultError(uuid, ObjectDoesNotExistException(uuid, (ObjectDoesNotExistException) null))
            }
        }

        fun onRequest(uuid: UUID) {
            val activeAgentCircuit: SLAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(Runnable(this, activeAgentCircuit, uuid) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f212$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f213$f1

                    /* renamed from: -$f2 */
                    private val /* synthetic */ Object f214$f2

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.2.$m$0():Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$n3FxEEuksYOCADj00lseQFiZ3z4.2.$m$0():Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:98)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:480)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.ClassGen.addInsnBody(ClassGen.java:437)
                    	at jadx.core.codegen.ClassGen.addField(ClassGen.java:378)
                    	at jadx.core.codegen.ClassGen.addFields(ClassGen.java:348)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:226)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    
*/

            } else {
                ObjectsManager.this.touchableObjectsPool.onResultError(uuid, SLGridConnection.NotConnectedException())
            }
        }
    }
    /* access modifiers changed from: private */
    val Runnable updateObjectListRunnable = Runnable() {
        fun run() {
            SLObjectFilterInfo r2
            synchronized (ObjectsManager.this.filterLock) {
                r2 = ObjectsManager.this.filterInfo
            }
            val activeAgentCircuit: SLAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit()
            val agentPosition: AgentPosition = activeAgentCircuit != null ? activeAgentCircuit.getModules().avatarControl.getAgentPosition() : null
            val immutablePosition: ImmutableVector = agentPosition != null ? agentPosition.getImmutablePosition() : null
            val sLParcelInfo: SLParcelInfo = (SLParcelInfo) ObjectsManager.this.parcelInfo.get()
            Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", sLParcelInfo, immutablePosition)
            ObjectsManager.this.objectDisplayListPool.onResultData(SubscriptionSingleKey.Value, (sLParcelInfo == null || immutablePosition == null) ? ObjectDisplayList(ImmutableList.of(), false) : sLParcelInfo.getDisplayObjects(immutablePosition, r2, ObjectsManager.this.nameRetriever))
        }
    }
    private val SimpleRequestHandler<SubscriptionSingleKey> updateRequestHandler = SimpleRequestHandler<SubscriptionSingleKey>() {
        fun onRequest(subscriptionSingleKey: SubscriptionSingleKey) {
            val activeAgentCircuit: SLAgentCircuit = ObjectsManager.this.userManager.getActiveAgentCircuit()
            if (activeAgentCircuit != null) {
                activeAgentCircuit.execute(ObjectsManager.this.updateObjectListRunnable)
            } else {
                ObjectsManager.this.objectDisplayListPool.onResultError(SubscriptionSingleKey.Value, SLGridConnection.NotConnectedException())
            }
        }

        fun onRequestCancelled(subscriptionSingleKey: SubscriptionSingleKey) {
            ObjectsManager.this.nameRetriever.clearChatters()
        }
    }
    /* access modifiers changed from: private */
    val UserManager userManager

    @JvmStatic
    class ObjectDisplayList {
        val Boolean isLoading
        val ImmutableList<SLObjectDisplayInfo> objects

        public ObjectDisplayList(ImmutableList<SLObjectDisplayInfo> immutableList, Boolean z) {
            this.objects = immutableList
            this.isLoading = z
        }
    }

    @JvmStatic
    class ObjectDoesNotExistException : Exception() {
        private val Int localID
        private val UUID uuid

        private ObjectDoesNotExistException(Int i) {
            this.localID = i
            this.uuid = null
        }

        /* synthetic */ ObjectDoesNotExistException(Int i, ObjectDoesNotExistException objectDoesNotExistException) {
            this(i)
        }

        private ObjectDoesNotExistException(UUID uuid2) {
            this.localID = 0
            this.uuid = uuid2
        }

        /* synthetic */ ObjectDoesNotExistException(UUID uuid2, ObjectDoesNotExistException objectDoesNotExistException) {
            this(uuid2)
        }

         public fun getLocalID(): Int {
            return this.localID
        }
    }

    ObjectsManager(UserManager userManager2) {
        this.userManager = userManager2
        this.nameRetriever = MultipleChatterNameRetriever(userManager2.getUserID(), this.onChatterNameUpdated, (Executor) null)
        this.objectDisplayListPool.attachRequestHandler(this.updateRequestHandler)
        this.objectProfilePool.attachRequestHandler(this.objectProfileRequestHandler)
        this.touchableObjectsPool.attachRequestHandler(this.touchableObjectsRequestHandler)
    }

    fun clearParcelInfo(sLParcelInfo: SLParcelInfo) {
        this.parcelInfo.compareAndSet(sLParcelInfo, (Object) null)
    }

    public Subscribable<SubscriptionSingleKey, ObjectDisplayList> getObjectDisplayList() {
        return this.objectDisplayListPool
    }

    public Subscribable<Integer, SLObjectProfileData> getObjectProfile() {
        return this.objectProfilePool
    }

    public Subscribable<Integer, SLTaskInventory> getObjectTaskInventory() {
        return this.taskInventoryPool
    }

    public RequestSource<Integer, SLTaskInventory> getTaskInventoryRequestSource() {
        return this.taskInventoryPool
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager_2320  reason: not valid java name */
    public /* synthetic */ Unit m347lambda$com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager_2320(MultipleChatterNameRetriever multipleChatterNameRetriever) {
        requestObjectListUpdate()
    }

    public SubscriptionPool<SubscriptionSingleKey, MyAvatarState> myAvatarState() {
        return this.myAvatarStatePool
    }

    fun requestObjectListUpdate() {
        this.objectDisplayListPool.requestUpdate(SubscriptionSingleKey.Value)
    }

    fun requestObjectProfileUpdate(i: Int) {
        this.objectProfilePool.requestUpdate(Integer.valueOf(i))
    }

    fun requestTaskInventoryUpdate(i: Int) {
        this.taskInventoryPool.requestUpdate(Integer.valueOf(i))
    }

    fun requestTouchableChildrenUpdate(uuid: UUID) {
        this.touchableObjectsPool.requestUpdate(uuid)
    }

    public SubscriptionSingleDataPool<ImmutableSet<UUID>> runningAnimations() {
        return this.runningAnimationsPool
    }

    fun setFilter(sLObjectFilterInfo: SLObjectFilterInfo) {
        val z: Boolean = false
        synchronized (this.filterLock) {
            if (!this.filterInfo.equals(sLObjectFilterInfo)) {
                this.filterInfo = sLObjectFilterInfo
                z = true
            }
        }
        if (z) {
            requestObjectListUpdate()
        }
    }

    fun setParcelInfo(sLParcelInfo: SLParcelInfo) {
        this.parcelInfo.set(sLParcelInfo)
        requestObjectListUpdate()
    }

    public Subscribable<UUID, ImmutableList<SLObjectInfo>> touchableObjects() {
        return this.touchableObjectsPool
    }
}
