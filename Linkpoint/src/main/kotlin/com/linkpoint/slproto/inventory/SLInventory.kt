package com.linkpoint.slproto.inventory

import android.annotation.SuppressLint
import android.database.Cursor
import com.google.common.base.Function
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.net.HttpHeaders
import com.linkpoint.Debug
import com.linkpoint.orm.DBObject
import com.linkpoint.orm.InventoryDB
import com.linkpoint.orm.InventoryEntryDBObject
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.SLMessageEventListener
import com.linkpoint.slproto.caps.SLCapEventQueue
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.events.SLInventoryNewContentsEvent
import com.linkpoint.slproto.events.SLInventoryUpdatedEvent
import com.linkpoint.slproto.handler.SLEventQueueMessageHandler
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.GenericHTTPExecutor
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.https.SLHTTPSConnection
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.messages.CopyInventoryItem
import com.linkpoint.slproto.messages.CreateInventoryFolder
import com.linkpoint.slproto.messages.CreateInventoryItem
import com.linkpoint.slproto.messages.InventoryDescendents
import com.linkpoint.slproto.messages.LinkInventoryItem
import com.linkpoint.slproto.messages.MoveInventoryItem
import com.linkpoint.slproto.messages.MoveTaskInventory
import com.linkpoint.slproto.messages.RemoveInventoryFolder
import com.linkpoint.slproto.messages.RemoveInventoryItem
import com.linkpoint.slproto.messages.RemoveInventoryObjects
import com.linkpoint.slproto.messages.UpdateCreateInventoryItem
import com.linkpoint.slproto.messages.UpdateInventoryFolder
import com.linkpoint.slproto.messages.UpdateInventoryItem
import com.linkpoint.slproto.messages.UpdateTaskInventory
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.utils.UUIDPool
import java.io.IOException
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Set
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.Nonnull
import javax.annotation.Nullable
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

class SLInventory : SLModule() {
    @SuppressLint({"UseSparseArrays"})
    private Map<Integer, OnInventoryCallbackListener> callbacks = Collections.synchronizedMap(HashMap())
    private val SLCaps caps
    private val InventoryDB db
    private val Executor dbExecutor
    private ExecutorService executor = null
    /* access modifiers changed from: private */
    val String fetchCap
    /* access modifiers changed from: private */
    val AtomicBoolean fetchEntireInventoryRequested = AtomicBoolean(false)
    /* access modifiers changed from: private */
    val Map<UUID, SLInventoryFetchRequest> fetchRequests = ConcurrentHashMap()
    private val ResultHandler<UUID, SLInventoryEntry> folderEntryResultHandler
    private val RequestHandler<UUID> folderLoadingRequestHandler = SimpleRequestHandler<UUID>() {
        public Unit onRequest(UUID uuid) {
            SLInventory.this.updateFolderLoadingStatus(uuid)
        }
    }
    private val ResultHandler<UUID, Boolean> folderLoadingResultHandler
    private val RequestHandler<UUID> folderRequestHandler = RequestHandler<UUID>() {
        public Unit onRequest(UUID uuid) {
            try {
                Debug.Printf("Inventory: folderRequestHandler: folderId = '%s'", uuid)
                SLInventoryFetchRequest sLInventoryHTTPFetchRequest = SLInventory.this.fetchCap != null ? SLInventoryHTTPFetchRequest(SLInventory.this, uuid, SLInventory.this.fetchCap) : SLInventoryUDPFetchRequest(SLInventory.this, uuid)
                SLInventory.this.fetchRequests.put(uuid, sLInventoryHTTPFetchRequest)
                SLInventory.this.updateFolderLoadingStatus(uuid)
                sLInventoryHTTPFetchRequest.start()
            } catch (NoInventoryItemException e) {
                Debug.Warning(e)
            }
        }

        public Unit onRequestCancelled(UUID uuid) {
            SLInventoryFetchRequest sLInventoryFetchRequest = (SLInventoryFetchRequest) SLInventory.this.fetchRequests.remove(uuid)
            SLInventory.this.updateFolderLoadingStatus(uuid)
            if (sLInventoryFetchRequest != null) {
                sLInventoryFetchRequest.cancel()
            }
        }
    }
    private AtomicInteger nextCallbackID = AtomicInteger(1)
    private val SubscriptionData<UUID, SLInventoryEntry> nextFolderSubscription
    private SLMessageEventListener reloadEvent = SLMessageEventListener() {
        public Unit onMessageAcknowledged(SLMessage sLMessage) {
            SLInventory.this.eventBus.publish(SLInventoryUpdatedEvent((Set<Long>) null, (Set<Long>) null, true))
        }

        public Unit onMessageTimeout(SLMessage sLMessage) {
        }
    }
    public SLInventoryEntry rootFolder = null
    private Boolean rootFolderFetchNeeded = false
    private val SubscriptionData<UUID, SLInventoryEntry> rootFolderSubscription
    private val ResultHandler<SubscriptionSingleKey, Boolean> seachRunningResultHandler
    private val ResultHandler<SubscriptionSingleKey, Boolean> searchProcessResultHandler
    private val RequestHandler<SubscriptionSingleKey> searchRequestHandler = RequestHandler<SubscriptionSingleKey>() {
        public Unit onRequest(SubscriptionSingleKey subscriptionSingleKey) {
            SLInventory.this.fetchEntireInventoryRequested.set(true)
            SLInventory.this.fetchNextFolder()
        }

        public Unit onRequestCancelled(SubscriptionSingleKey subscriptionSingleKey) {
            SLInventory.this.fetchEntireInventoryRequested.set(false)
            SLInventory.this.updateSearchRunningStatus()
        }
    }
    private val RequestHandler<SubscriptionSingleKey> searchRunningRequestHandler = SimpleRequestHandler<SubscriptionSingleKey>() {
        public Unit onRequest(SubscriptionSingleKey subscriptionSingleKey) {
            SLInventory.this.updateSearchRunningStatus()
        }
    }
    private Map<UUID, SLInventoryUDPFetchRequest> udpFetchPendingRequests = Collections.synchronizedMap(HashMap())
    private Map<UUID, SLInventoryUDPFetchRequest> udpFetchRequests = Collections.synchronizedMap(HashMap())
    /* access modifiers changed from: private */
    val UserManager userManager

    @JvmStatic
    class InventoryFetchException : IOException() {
        public InventoryFetchException(String str) {
            super(str)
        }
    }

    @JvmStatic
    class NoInventoryItemException : Exception() {
        private const val Long serialVersionUID = 1

        public NoInventoryItemException(Long j) {
            super("Inventory item " + j + " not found")
        }

        public NoInventoryItemException(UUID uuid) {
            super("Inventory item " + uuid.toString() + " not found")
        }
    }

    interface OnInventoryCallbackListener {
        Unit onInventoryCallback(SLInventoryEntry sLInventoryEntry)
    }

    interface OnNotecardUpdatedListener {
        Unit onNotecardUpdated(SLInventoryEntry sLInventoryEntry, String str)
    }

    public SLInventory(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        this.db = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null
        this.caps = sLCaps
        this.fetchCap = sLCaps.getCapability(SLCaps.SLCapability.FetchInventoryDescendents2)
        this.dbExecutor = this.userManager != null ? this.userManager.getInventoryManager().getExecutor() : null
        if (this.userManager != null) {
            this.userManager.getInventoryManager().setCurrentSessionID(sLAgentCircuit.getAuthReply().sessionID)
            this.userManager.getInventoryManager().setRootFolder(sLAgentCircuit.getAuthReply().inventoryRoot)
        }
        try {
            if (this.db != null) {
                Debug.Printf("Inventory: creating root folder with folderUUID %s", sLAgentCircuit.getAuthReply().inventoryRoot.toString())
                this.rootFolder = this.db.findEntryOrCreate(sLAgentCircuit.getAuthReply().inventoryRoot)
                if (this.rootFolder.getId() == 0) {
                    this.rootFolder.name = "Inventory"
                    this.rootFolder.isFolder = true
                    this.rootFolder.parent_id = 0
                    this.rootFolder.agentUUID = sLAgentCircuit.circuitInfo.agentID
                    this.db.saveEntry(this.rootFolder)
                    this.rootFolderFetchNeeded = true
                }
            }
            Debug.Printf("Inventory: ready.", Object[0])
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e)
        }
        this.folderEntryResultHandler = this.userManager != null ? this.userManager.getInventoryManager().getFolderRequestSource().attachRequestHandler(this.folderRequestHandler) : null
        if (this.userManager != null) {
            this.folderLoadingResultHandler = this.userManager.getInventoryManager().getFolderLoadingRequestSource().attachRequestHandler(AsyncRequestHandler(this.dbExecutor, this.folderLoadingRequestHandler))
            this.seachRunningResultHandler = this.userManager.getInventoryManager().getSearchRunning().attachRequestHandler(AsyncRequestHandler(this.dbExecutor, this.searchRunningRequestHandler))
            this.searchProcessResultHandler = this.userManager.getInventoryManager().getSearchProcessRequestSource().attachRequestHandler(AsyncRequestHandler(this.dbExecutor, this.searchRequestHandler))
            this.nextFolderSubscription = SubscriptionData<>(this.dbExecutor, $Lambda$eCHTl_kh2tUCaOJO9NRHJvhjs(this), Subscription.OnError(this) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f82$f0

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.2.$m$0(java.lang.Throwable):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.2.$m$0(java.lang.Throwable):Unit, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
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
                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                
*/

            if (this.rootFolderFetchNeeded) {
                this.rootFolderSubscription = SubscriptionData<>(this.dbExecutor, Subscription.OnData(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f76$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.1.$m$0(java.lang.Object):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
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
                this.rootFolderSubscription = null
            }
        } else {
            this.folderLoadingResultHandler = null
            this.searchProcessResultHandler = null
            this.nextFolderSubscription = null
            this.seachRunningResultHandler = null
            this.rootFolderSubscription = null
        }
    }

    private Unit DoCreateInventoryItem(UUID uuid, Int i, Int i2, String str, String str2, OnInventoryCallbackListener onInventoryCallbackListener) {
        CreateInventoryItem createInventoryItem = CreateInventoryItem()
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        createInventoryItem.InventoryBlock_Field.CallbackID = getNextCallbackID()
        createInventoryItem.InventoryBlock_Field.FolderID = uuid
        createInventoryItem.InventoryBlock_Field.TransactionID = UUID(0, 0)
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE
        createInventoryItem.InventoryBlock_Field.Type = i
        createInventoryItem.InventoryBlock_Field.InvType = i2
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str)
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2)
        this.callbacks.put(Integer.valueOf(createInventoryItem.InventoryBlock_Field.CallbackID), onInventoryCallbackListener)
        createInventoryItem.isReliable = true
        SendMessage(createInventoryItem)
    }

    private Unit DoUpdateInventoryItem(final SLInventoryEntry sLInventoryEntry, final OnInventoryCallbackListener onInventoryCallbackListener) {
        UpdateInventoryItem updateInventoryItem = UpdateInventoryItem()
        updateInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        updateInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        updateInventoryItem.AgentData_Field.TransactionID = UUID.randomUUID()
        UpdateInventoryItem.InventoryData inventoryData = UpdateInventoryItem.InventoryData()
        inventoryData.ItemID = sLInventoryEntry.uuid
        inventoryData.FolderID = sLInventoryEntry.parentUUID
        inventoryData.CallbackID = 0
        inventoryData.CreatorID = sLInventoryEntry.creatorUUID
        inventoryData.OwnerID = sLInventoryEntry.ownerUUID
        inventoryData.GroupID = sLInventoryEntry.groupUUID
        inventoryData.BaseMask = sLInventoryEntry.baseMask
        inventoryData.OwnerMask = sLInventoryEntry.ownerMask
        inventoryData.GroupMask = sLInventoryEntry.groupMask
        inventoryData.EveryoneMask = sLInventoryEntry.everyoneMask
        inventoryData.NextOwnerMask = sLInventoryEntry.nextOwnerMask
        inventoryData.GroupOwned = sLInventoryEntry.isGroupOwned
        inventoryData.TransactionID = UUID(0, 0)
        inventoryData.Type = sLInventoryEntry.assetType
        inventoryData.InvType = sLInventoryEntry.invType
        inventoryData.Flags = sLInventoryEntry.flags
        inventoryData.SaleType = sLInventoryEntry.saleType
        inventoryData.SalePrice = sLInventoryEntry.salePrice
        inventoryData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name)
        inventoryData.Description = SLMessage.stringToVariableUTF(sLInventoryEntry.description)
        inventoryData.CreationDate = sLInventoryEntry.creationDate
        inventoryData.CRC = 0
        updateInventoryItem.InventoryData_Fields.add(inventoryData)
        updateInventoryItem.isReliable = true
        Debug.Printf("Update inventory callback %d", Integer.valueOf(inventoryData.CallbackID))
        updateInventoryItem.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage)
                onInventoryCallbackListener.onInventoryCallback(sLInventoryEntry)
            }
        SendMessage(updateInventoryItem)
    }

    private Unit DoUpdateTaskInventoryItem(SLInventoryEntry sLInventoryEntry, Int i, SLMessageEventListener sLMessageEventListener) {
        UpdateTaskInventory updateTaskInventory = UpdateTaskInventory()
        updateTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID
        updateTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID
        updateTaskInventory.UpdateData_Field.LocalID = i
        updateTaskInventory.UpdateData_Field.Key = 0
        UpdateTaskInventory.InventoryData inventoryData = updateTaskInventory.InventoryData_Field
        inventoryData.ItemID = sLInventoryEntry.uuid
        inventoryData.FolderID = sLInventoryEntry.parentUUID
        inventoryData.CreatorID = sLInventoryEntry.creatorUUID
        inventoryData.OwnerID = sLInventoryEntry.ownerUUID
        inventoryData.GroupID = sLInventoryEntry.groupUUID
        inventoryData.BaseMask = sLInventoryEntry.baseMask
        inventoryData.OwnerMask = sLInventoryEntry.ownerMask
        inventoryData.GroupMask = sLInventoryEntry.groupMask
        inventoryData.EveryoneMask = sLInventoryEntry.everyoneMask
        inventoryData.NextOwnerMask = sLInventoryEntry.nextOwnerMask
        inventoryData.GroupOwned = sLInventoryEntry.isGroupOwned
        inventoryData.TransactionID = UUID(0, 0)
        inventoryData.Type = sLInventoryEntry.assetType
        inventoryData.InvType = sLInventoryEntry.invType
        inventoryData.Flags = sLInventoryEntry.flags
        inventoryData.SaleType = sLInventoryEntry.saleType
        inventoryData.SalePrice = sLInventoryEntry.salePrice
        inventoryData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name)
        inventoryData.Description = SLMessage.stringToVariableUTF(sLInventoryEntry.description)
        inventoryData.CreationDate = sLInventoryEntry.creationDate
        inventoryData.CRC = 0
        updateTaskInventory.isReliable = true
        updateTaskInventory.setEventListener(sLMessageEventListener)
        SendMessage(updateTaskInventory)
    }

    /* access modifiers changed from: private */
    public Unit MoveTaskInventory(UUID uuid, Int i, UUID uuid2) {
        MoveTaskInventory moveTaskInventory = MoveTaskInventory()
        moveTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID
        moveTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moveTaskInventory.AgentData_Field.FolderID = uuid
        moveTaskInventory.InventoryData_Field.LocalID = i
        moveTaskInventory.InventoryData_Field.ItemID = uuid2
        moveTaskInventory.isReliable = true
        SendMessage(moveTaskInventory)
    }

    /* access modifiers changed from: private */
    public Unit StartUploadingNotecardContents(SLInventoryEntry sLInventoryEntry, UUID uuid, Boolean z, Byte[] bArr, OnNotecardUpdatedListener onNotecardUpdatedListener) {
        GenericHTTPExecutor.getInstance().execute(Runnable(z, this, sLInventoryEntry, bArr, uuid, onNotecardUpdatedListener) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Boolean f105$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f106$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f107$f2

            /* renamed from: -$f3 */
            private val /* synthetic */ Object f108$f3

            /* renamed from: -$f4 */
            private val /* synthetic */ Object f109$f4

            /* renamed from: -$f5 */
            private val /* synthetic */ Object f110$f5

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.9.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.9.$m$0():Unit, class status: UNLOADED
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

    }

    private String UploadNotecardContents(SLInventoryEntry sLInventoryEntry, UUID uuid, Boolean z, Byte[] bArr) {
        String capabilityOrThrow
        Response execute
        Int i = 0
        String str = null
        try {
            LLSDXMLRequest lLSDXMLRequest = LLSDXMLRequest()
            if (z) {
                z2 = true
                capabilityOrThrow = getCaps().getCapabilityOrThrow(uuid == null ? SLCaps.SLCapability.UpdateScriptAgent : SLCaps.SLCapability.UpdateScriptTask)
            } else {
                capabilityOrThrow = getCaps().getCapabilityOrThrow(uuid == null ? SLCaps.SLCapability.UpdateNotecardAgentInventory : SLCaps.SLCapability.UpdateNotecardTaskInventory)
                z2 = false
            }
            ImmutableMap.Builder builder = ImmutableMap.builder()
            builder.put("item_id", LLSDUUID(sLInventoryEntry.uuid))
            if (uuid != null) {
                builder.put("task_id", LLSDUUID(uuid))
            }
            if (z2) {
                builder.put("target", LLSDString("mono"))
                if (uuid != null) {
                    builder.put("is_script_running", LLSDInt(1))
                }
            }
            LLSDMap lLSDMap = LLSDMap((Map<String, LLSDNode>) builder.build())
            Debug.Log("Notecard upload request: Initial uploader request: " + lLSDMap.serializeToXML())
            LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(capabilityOrThrow, lLSDMap)
            Debug.Log("Notecard upload request: Initial uploader reply: " + PerformRequest.serializeToXML())
            String asString = PerformRequest.byKey("uploader").asString()
            if (asString != null) {
                asString = SLCaps.repairURL(capabilityOrThrow, asString)
            }
            execute = SLHTTPSConnection.getOkHttpClient().newCall(Request.Builder().url(Strings.nullToEmpty(asString)).post(RequestBody.create(MediaType.parse("application/vnd.ll.notecard"), bArr)).header(HttpHeaders.ACCEPT, "application/llsd+xml").build()).execute()
            if (execute == null) {
                throw IOException("Null response")
            } else if (!execute.isSuccessful()) {
                throw IOException("Response error code " + execute.code())
            } else {
                LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), (String) null)
                Debug.Log("upload reply: " + parseXML.serializeToXML())
                String asString2 = parseXML.byKey("state").asString()
                UUID asUUID = parseXML.byKey("new_asset").asUUID()
                if (asString2.equals("complete")) {
                    if (uuid == null) {
                        SLInventoryEntry findEntry = this.db.findEntry(sLInventoryEntry.uuid)
                        if (findEntry != null) {
                            findEntry.assetUUID = asUUID
                            this.db.saveEntry(findEntry)
                        }
                        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
                    }
                    if (parseXML.keyExists("compiled") && !parseXML.byKey("compiled").asBoolean() && parseXML.keyExists("errors")) {
                        LLSDNode byKey = parseXML.byKey("errors")
                        str = ""
                        while (i < byKey.getCount()) {
                            if (i != 0) {
                                str = str + "; "
                            }
                            String str2 = str + byKey.byIndex(i).asString()
                            i++
                            str = str2
                        }
                    }
                }
                execute.close()
                return str
            }
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e)
            return "Failed to upload inventory asset"
        } catch (IOException e2) {
            Debug.Warning(e2)
            return "Failed to upload inventory asset"
        } catch (LLSDException e3) {
            Debug.Warning(e3)
            return "Failed to upload inventory asset"
        } catch (SLCaps.NoSuchCapabilityException e4) {
            Debug.Warning(e4)
            return "Failed to upload inventory asset"
        } catch (Throwable th) {
            execute.close()
            throw th
        }
    }

    /* access modifiers changed from: private */
    public Unit fetchNextFolder() {
        if (this.fetchEntireInventoryRequested.get()) {
            UUID uuid = this.circuitInfo.sessionID
            try {
                Cursor query = this.db.getDatabase().query(InventoryEntryDBObject.tableName, String[]{"uuid_high", "uuid_low"}, "isFolder AND (sessionID_high != ? OR sessionID_low != ?)", String[]{Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())}, (String) null, (String) null, (String) null, "1")
                if (query.moveToFirst()) {
                    UUID uuid2 = UUIDPool.getUUID(query.getLong(0), query.getLong(1))
                    Debug.Printf("InventorySearch: fetching next folder: %s", uuid2)
                    this.nextFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), uuid2)
                    updateSearchRunningStatus()
                } else {
                    Debug.Printf("InventorySearch: no more folders to fetch", Object[0])
                    this.searchProcessResultHandler.onResultData(SubscriptionSingleKey.Value, true)
                    this.nextFolderSubscription.unsubscribe()
                    updateSearchRunningStatus()
                }
                query.close()
            } catch (Exception e) {
                Debug.Printf("InventorySearch: error while fetching folders", Object[0])
                Debug.Warning(e)
                this.fetchEntireInventoryRequested.set(false)
                this.searchProcessResultHandler.onResultError(SubscriptionSingleKey.Value, e)
                this.nextFolderSubscription.unsubscribe()
                updateSearchRunningStatus()
            }
        } else {
            updateSearchRunningStatus()
        }
    }

    private Int getNextCallbackID() {
        return this.nextCallbackID.getAndIncrement()
    }

    /* access modifiers changed from: private */
    /* renamed from: onNextFolderError */
    public Unit m175com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref1(Throwable th) {
        fetchNextFolder()
    }

    /* access modifiers changed from: private */
    /* renamed from: onNextFolderFetched */
    public Unit m174com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref0(SLInventoryEntry sLInventoryEntry) {
        if (Objects.equal(sLInventoryEntry.sessionID, this.circuitInfo.sessionID)) {
            fetchNextFolder()
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onRootFolderFetched */
    public Unit m176com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref2(SLInventoryEntry sLInventoryEntry) {
        this.rootFolderFetchNeeded = false
        if (this.rootFolderSubscription != null) {
            this.rootFolderSubscription.unsubscribe()
        }
        Debug.Printf("Inventory: Fetched root folder.", Object[0])
    }

    /* access modifiers changed from: private */
    public Unit updateFolderLoadingStatus(UUID uuid) {
        if (this.folderLoadingResultHandler != null) {
            this.folderLoadingResultHandler.onResultData(uuid, Boolean.valueOf(this.fetchRequests.containsKey(uuid)))
        }
    }

    /* access modifiers changed from: private */
    public Unit updateSearchRunningStatus() {
        if (this.searchRunningRequestHandler != null) {
            this.seachRunningResultHandler.onResultData(SubscriptionSingleKey.Value, Boolean.valueOf(this.fetchEntireInventoryRequested.get() ? this.nextFolderSubscription.isSubscribed() : false))
        }
    }

    public Collection<SLInventoryEntry> CollectGiveableItems(SLInventoryEntry sLInventoryEntry) {
        ArrayList arrayList = ArrayList()
        Cursor query = SLInventoryEntry.query(this.db.getDatabase(), "parent_id = ?", String[]{Long.toString(sLInventoryEntry.getId())}, (String) null)
        if (query != null) {
            while (query.moveToNext()) {
                SLInventoryEntry sLInventoryEntry2 = SLInventoryEntry(query)
                if (sLInventoryEntry2.isFolder) {
                    arrayList.add(sLInventoryEntry2)
                    arrayList.addAll(CollectGiveableItems(sLInventoryEntry2))
                } else if ((sLInventoryEntry2.baseMask & sLInventoryEntry2.ownerMask & 8192) != 0) {
                    arrayList.add(sLInventoryEntry2)
                }
            }
            query.close()
        }
        return arrayList
    }

    public Unit CopyInventoryFromNotecard(UUID uuid, UUID uuid2, UUID uuid3, Runnable runnable) {
        GenericHTTPExecutor.getInstance().execute(Runnable(this, uuid, uuid2, uuid3, runnable) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f87$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f88$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f89$f2

            /* renamed from: -$f3 */
            private val /* synthetic */ Object f90$f3

            /* renamed from: -$f4 */
            private val /* synthetic */ Object f91$f4

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.5.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.5.$m$0():Unit, class status: UNLOADED
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

    }

    public Unit CopyInventoryItem(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        CopyInventoryItem copyInventoryItem = CopyInventoryItem()
        copyInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        copyInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        CopyInventoryItem.InventoryData inventoryData = CopyInventoryItem.InventoryData()
        inventoryData.CallbackID = getNextCallbackID()
        inventoryData.OldAgentID = sLInventoryEntry.agentUUID
        inventoryData.OldItemID = sLInventoryEntry.uuid
        inventoryData.NewFolderID = sLInventoryEntry2.uuid
        inventoryData.NewName = SLMessage.stringToVariableOEM(sLInventoryEntry.name)
        copyInventoryItem.InventoryData_Fields.add(inventoryData)
        copyInventoryItem.isReliable = true
        SendMessage(copyInventoryItem)
    }

    public Unit CopyObjectContents(String str, Int i, Set<UUID> set, Function<UUID, Void> function) {
        if (this.rootFolder != null) {
            this.dbExecutor.execute(Runnable(i, this, str, ImmutableSet.copyOf(set), function) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Int f92$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f93$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Object f94$f2

                /* renamed from: -$f3 */
                private val /* synthetic */ Object f95$f3

                /* renamed from: -$f4 */
                private val /* synthetic */ Object f96$f4

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.6.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.6.$m$0():Unit, class status: UNLOADED
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

            return
        }
        function.apply(null)
    }

    public Unit DeleteInventoryItem(final SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.isFolder) {
            RemoveInventoryFolder removeInventoryFolder = RemoveInventoryFolder()
            removeInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID
            removeInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID
            RemoveInventoryFolder.FolderData folderData = RemoveInventoryFolder.FolderData()
            folderData.FolderID = sLInventoryEntry.uuid
            removeInventoryFolder.FolderData_Fields.add(folderData)
            removeInventoryFolder.isReliable = true
            removeInventoryFolder.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
                public Unit onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
                }
            SendMessage(removeInventoryFolder)
            return
        }
        RemoveInventoryItem removeInventoryItem = RemoveInventoryItem()
        removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        RemoveInventoryItem.InventoryData inventoryData = RemoveInventoryItem.InventoryData()
        inventoryData.ItemID = sLInventoryEntry.uuid
        removeInventoryItem.InventoryData_Fields.add(inventoryData)
        removeInventoryItem.isReliable = true
        removeInventoryItem.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
            }
        SendMessage(removeInventoryItem)
    }

    public Unit DeleteInventoryItemRaw(UUID uuid) {
        RemoveInventoryItem removeInventoryItem = RemoveInventoryItem()
        removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        RemoveInventoryItem.InventoryData inventoryData = RemoveInventoryItem.InventoryData()
        inventoryData.ItemID = uuid
        removeInventoryItem.InventoryData_Fields.add(inventoryData)
        removeInventoryItem.isReliable = true
        removeInventoryItem.setEventListener(this.reloadEvent)
        SendMessage(removeInventoryItem)
    }

    public Unit DeleteMultiInventoryItemRaw(final SLInventoryEntry sLInventoryEntry, List<UUID> list) {
        RemoveInventoryObjects removeInventoryObjects = RemoveInventoryObjects()
        removeInventoryObjects.AgentData_Field.AgentID = this.circuitInfo.agentID
        removeInventoryObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID
        for (UUID uuid : list) {
            RemoveInventoryObjects.ItemData itemData = RemoveInventoryObjects.ItemData()
            itemData.ItemID = uuid
            removeInventoryObjects.ItemData_Fields.add(itemData)
        }
        removeInventoryObjects.isReliable = true
        removeInventoryObjects.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
            }
        SendMessage(removeInventoryObjects)
    }

    public UUID DoCreateNewFolder(final SLInventoryEntry sLInventoryEntry, String str, Boolean z, final Function<UUID, Void> function) {
        final UUID randomUUID = UUID.randomUUID()
        if (z) {
            try {
                SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(randomUUID)
                findEntryOrCreate.resetId()
                findEntryOrCreate.uuid = randomUUID
                findEntryOrCreate.parent_id = sLInventoryEntry.getId()
                findEntryOrCreate.parentUUID = sLInventoryEntry.uuid
                findEntryOrCreate.name = str
                findEntryOrCreate.description = ""
                findEntryOrCreate.agentUUID = this.circuitInfo.agentID
                findEntryOrCreate.isFolder = true
                findEntryOrCreate.typeDefault = -1
                findEntryOrCreate.version = 0
                this.db.saveEntry(findEntryOrCreate)
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e)
            }
        }
        Debug.Printf("Inventory: Creating folder with uuid = %s, parent %s", randomUUID, sLInventoryEntry.uuid)
        CreateInventoryFolder createInventoryFolder = CreateInventoryFolder()
        createInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID
        createInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID
        createInventoryFolder.FolderData_Field.FolderID = randomUUID
        createInventoryFolder.FolderData_Field.ParentID = sLInventoryEntry.uuid
        createInventoryFolder.FolderData_Field.Type = -1
        createInventoryFolder.FolderData_Field.Name = SLMessage.stringToVariableOEM(str)
        createInventoryFolder.isReliable = true
        createInventoryFolder.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Printf("Inventory: folder created with uuid = %s, parent %s", randomUUID, sLInventoryEntry.uuid)
                if (SLInventory.this.userManager != null) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
                }
                if (function != null) {
                    function.apply(randomUUID)
                }
            }
        SendMessage(createInventoryFolder)
        return randomUUID
    }

    public Unit DoCreateNewLandmark(SLInventoryEntry sLInventoryEntry, String str, String str2) {
        CreateInventoryItem createInventoryItem = CreateInventoryItem()
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        createInventoryItem.InventoryBlock_Field.CallbackID = getNextCallbackID()
        createInventoryItem.InventoryBlock_Field.FolderID = sLInventoryEntry.uuid
        createInventoryItem.InventoryBlock_Field.TransactionID = UUID(0, 0)
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE
        createInventoryItem.InventoryBlock_Field.Type = SLAssetType.AT_LANDMARK.getTypeCode()
        createInventoryItem.InventoryBlock_Field.InvType = SLInventoryType.IT_LANDMARK.getTypeCode()
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str)
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2)
        createInventoryItem.isReliable = true
        SendMessage(createInventoryItem)
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.BulkUpdateInventory)
    public Unit HandleBulkUpdateInventory(LLSDNode lLSDNode) {
        Debug.Printf("BulkUpdateInventory: EventQueue event", Object[0])
        SLInventoryNewContentsEvent sLInventoryNewContentsEvent = SLInventoryNewContentsEvent()
        HashSet<UUID> hashSet = HashSet<>()
        try {
            if (lLSDNode.keyExists("FolderData")) {
                LLSDNode byKey = lLSDNode.byKey("FolderData")
                for (Int i = 0; i < byKey.getCount(); i++) {
                    LLSDNode byIndex = byKey.byIndex(i)
                    UUID asUUID = byIndex.byKey("FolderID").asUUID()
                    if (asUUID.getLeastSignificantBits() != 0 || asUUID.getMostSignificantBits() != 0) {
                        Debug.Printf("Inventory: BulkUpdateInventory got folder %s", asUUID.toString())
                        OnInventoryCallbackListener onInventoryCallbackListener = null
                        if (byIndex.keyExists("CallbackID")) {
                            Debug.Printf("Inventory: got callback id %d", Integer.valueOf(byIndex.byKey("CallbackID").asInt()))
                            onInventoryCallbackListener = this.callbacks.remove(Integer.valueOf(byIndex.byKey("CallbackID").asInt()))
                        }
                        SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(asUUID)
                        UUID asUUID2 = byIndex.byKey("ParentID").asUUID()
                        SLInventoryEntry findEntry = this.db.findEntry(asUUID2)
                        if (findEntry != null) {
                            findEntryOrCreate.parent_id = findEntry.getId()
                            findEntryOrCreate.parentUUID = asUUID2
                            findEntryOrCreate.name = byIndex.byKey("Name").asString()
                            findEntryOrCreate.typeDefault = byIndex.byKey("Type").asInt()
                            findEntryOrCreate.isFolder = true
                            if (findEntryOrCreate.getId() == 0 && onInventoryCallbackListener == null) {
                                sLInventoryNewContentsEvent.AddItem(true, asUUID, findEntryOrCreate.name)
                            }
                            this.db.saveEntry(findEntryOrCreate)
                            hashSet.add(asUUID)
                            hashSet.add(asUUID2)
                        } else {
                            hashSet.add(asUUID2)
                            if (findEntryOrCreate.getId() != 0) {
                                this.db.deleteEntry(findEntryOrCreate)
                            }
                        }
                        if (onInventoryCallbackListener != null) {
                            onInventoryCallbackListener.onInventoryCallback(findEntryOrCreate)
                        }
                    }
                }
            }
            if (lLSDNode.keyExists("ItemData")) {
                LLSDNode byKey2 = lLSDNode.byKey("ItemData")
                for (Int i2 = 0; i2 < byKey2.getCount(); i2++) {
                    LLSDNode byIndex2 = byKey2.byIndex(i2)
                    UUID asUUID3 = byIndex2.byKey("ItemID").asUUID()
                    if (asUUID3.getLeastSignificantBits() != 0 || asUUID3.getMostSignificantBits() != 0) {
                        Debug.Printf("Inventory: BulkUpdateInventory got item %s", asUUID3.toString())
                        UUID asUUID4 = byIndex2.byKey("FolderID").asUUID()
                        OnInventoryCallbackListener onInventoryCallbackListener2 = null
                        if (byIndex2.keyExists("CallbackID")) {
                            Debug.Printf("Inventory: got callback id %d", Integer.valueOf(byIndex2.byKey("CallbackID").asInt()))
                            onInventoryCallbackListener2 = this.callbacks.remove(Integer.valueOf(byIndex2.byKey("CallbackID").asInt()))
                        }
                        SLInventoryEntry findEntryOrCreate2 = this.db.findEntryOrCreate(asUUID3)
                        hashSet.add(asUUID4)
                        findEntryOrCreate2.groupMask = byIndex2.byKey("GroupMask").asInt()
                        findEntryOrCreate2.description = byIndex2.byKey("Description").asString()
                        findEntryOrCreate2.isGroupOwned = byIndex2.byKey("GroupOwned").asBoolean()
                        findEntryOrCreate2.everyoneMask = byIndex2.byKey("EveryoneMask").asInt()
                        findEntryOrCreate2.assetType = byIndex2.byKey("Type").asInt()
                        findEntryOrCreate2.invType = byIndex2.byKey("InvType").asInt()
                        findEntryOrCreate2.groupUUID = byIndex2.byKey("GroupID").asUUID()
                        findEntryOrCreate2.name = byIndex2.byKey("Name").asString()
                        findEntryOrCreate2.baseMask = byIndex2.byKey("BaseMask").asInt()
                        findEntryOrCreate2.saleType = byIndex2.byKey("SaleType").asInt()
                        findEntryOrCreate2.salePrice = byIndex2.byKey("SalePrice").asInt()
                        findEntryOrCreate2.ownerUUID = byIndex2.byKey("OwnerID").asUUID()
                        findEntryOrCreate2.flags = byIndex2.byKey("Flags").asInt()
                        findEntryOrCreate2.ownerMask = byIndex2.byKey("OwnerMask").asInt()
                        findEntryOrCreate2.nextOwnerMask = byIndex2.byKey("NextOwnerMask").asInt()
                        findEntryOrCreate2.assetUUID = byIndex2.byKey("AssetID").asUUID()
                        findEntryOrCreate2.creationDate = byIndex2.byKey("CreationDate").asInt()
                        findEntryOrCreate2.parentUUID = asUUID4
                        SLInventoryEntry findEntry2 = this.db.findEntry(asUUID4)
                        if (findEntry2 != null) {
                            if (findEntryOrCreate2.getId() == 0 && onInventoryCallbackListener2 == null && findEntry2.typeDefault != 14 && findEntry2.typeDefault != 2) {
                                sLInventoryNewContentsEvent.AddItem(false, asUUID4, findEntryOrCreate2.name)
                            }
                            findEntryOrCreate2.parent_id = findEntry2.getId()
                            this.db.saveEntry(findEntryOrCreate2)
                        } else if (findEntryOrCreate2.getId() != 0) {
                            this.db.deleteEntry(findEntryOrCreate2)
                        }
                        if (onInventoryCallbackListener2 != null) {
                            onInventoryCallbackListener2.onInventoryCallback(findEntryOrCreate2)
                        }
                    }
                }
            }
        } catch (LLSDException e) {
            Debug.Warning(e)
        } catch (DBObject.DatabaseBindingException e2) {
            Debug.Warning(e2)
        }
        if (this.userManager != null) {
            for (UUID requestFolderUpdate : hashSet) {
                this.userManager.getInventoryManager().requestFolderUpdate(requestFolderUpdate)
            }
        }
        if (!sLInventoryNewContentsEvent.isEmpty()) {
            this.eventBus.publish(sLInventoryNewContentsEvent)
        }
    }

    public Unit HandleCircuitReady() {
        super.HandleCircuitReady()
        if (this.rootFolderFetchNeeded && this.rootFolderSubscription != null && this.rootFolder != null && this.userManager != null) {
            Debug.Printf("Inventory: Fetching root folder: %s", this.rootFolder.uuid)
            this.rootFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), this.rootFolder.uuid)
        }
    }

    public Unit HandleCloseCircuit() {
        if (this.rootFolderSubscription != null) {
            this.rootFolderSubscription.unsubscribe()
        }
        if (this.nextFolderSubscription != null) {
            this.nextFolderSubscription.unsubscribe()
        }
        if (this.userManager != null) {
            this.userManager.getInventoryManager().getFolderRequestSource().detachRequestHandler(this.folderRequestHandler)
        }
        if (this.executor != null) {
            this.executor.shutdownNow()
            this.executor = null
        }
    }

    @SLMessageHandler
    public synchronized Unit HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        SLInventoryUDPFetchRequest sLInventoryUDPFetchRequest = this.udpFetchRequests.get(inventoryDescendents.AgentData_Field.FolderID)
        if (sLInventoryUDPFetchRequest != null && sLInventoryUDPFetchRequest.HandleInventoryDescendents(inventoryDescendents)) {
            this.udpFetchRequests.remove(inventoryDescendents.AgentData_Field.FolderID)
            SLInventoryUDPFetchRequest remove = this.udpFetchPendingRequests.remove(inventoryDescendents.AgentData_Field.FolderID)
            if (remove != null) {
                this.udpFetchRequests.put(inventoryDescendents.AgentData_Field.FolderID, remove)
                remove.start()
            }
        }
    }

    @SLMessageHandler
    public Unit HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updateCreateInventoryItem) {
        SLInventoryNewContentsEvent sLInventoryNewContentsEvent = SLInventoryNewContentsEvent()
        HashSet<UUID> hashSet = HashSet<>()
        for (UpdateCreateInventoryItem.InventoryData inventoryData : updateCreateInventoryItem.InventoryData_Fields) {
            UUID uuid = inventoryData.ItemID
            UUID uuid2 = inventoryData.FolderID
            Debug.Printf("Inventory: UpdateCreateInventoryItem got folder %s item %s, callback %d", uuid2.toString(), uuid.toString(), Integer.valueOf(inventoryData.CallbackID))
            hashSet.add(uuid2)
            OnInventoryCallbackListener remove = this.callbacks.remove(Integer.valueOf(inventoryData.CallbackID))
            try {
                SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(uuid)
                findEntryOrCreate.groupMask = inventoryData.GroupMask
                findEntryOrCreate.description = SLMessage.stringFromVariableUTF(inventoryData.Description)
                findEntryOrCreate.isGroupOwned = inventoryData.GroupOwned
                findEntryOrCreate.everyoneMask = inventoryData.EveryoneMask
                findEntryOrCreate.assetType = inventoryData.Type
                findEntryOrCreate.invType = inventoryData.InvType
                findEntryOrCreate.groupUUID = inventoryData.GroupID
                findEntryOrCreate.name = SLMessage.stringFromVariableOEM(inventoryData.Name)
                findEntryOrCreate.baseMask = inventoryData.BaseMask
                findEntryOrCreate.saleType = inventoryData.SaleType
                findEntryOrCreate.salePrice = inventoryData.SalePrice
                findEntryOrCreate.ownerUUID = inventoryData.OwnerID
                findEntryOrCreate.flags = inventoryData.Flags
                findEntryOrCreate.ownerMask = inventoryData.OwnerMask
                findEntryOrCreate.nextOwnerMask = inventoryData.NextOwnerMask
                findEntryOrCreate.assetUUID = inventoryData.AssetID
                findEntryOrCreate.creationDate = inventoryData.CreationDate
                findEntryOrCreate.creatorUUID = inventoryData.CreatorID
                findEntryOrCreate.parentUUID = uuid2
                SLInventoryEntry findEntry = this.db.findEntry(uuid2)
                if (findEntry != null) {
                    if (findEntryOrCreate.getId() == 0 && remove == null && findEntry.typeDefault != 14 && findEntry.typeDefault != 2) {
                        sLInventoryNewContentsEvent.AddItem(false, uuid2, findEntryOrCreate.name)
                    }
                    findEntryOrCreate.parent_id = findEntry.getId()
                    this.db.saveEntry(findEntryOrCreate)
                } else if (findEntryOrCreate.getId() != 0) {
                    this.db.deleteEntry(findEntryOrCreate)
                }
                if (remove != null) {
                    remove.onInventoryCallback(findEntryOrCreate)
                }
            } catch (DBObject.DatabaseBindingException e) {
                e.printStackTrace()
            }
        }
        if (this.userManager != null) {
            for (UUID requestFolderUpdate : hashSet) {
                this.userManager.getInventoryManager().requestFolderUpdate(requestFolderUpdate)
            }
        }
        if (!sLInventoryNewContentsEvent.isEmpty()) {
            this.eventBus.publish(sLInventoryNewContentsEvent)
        }
    }

    public Unit LinkInventoryItem(final SLInventoryEntry sLInventoryEntry, UUID uuid, Int i, Int i2, String str, String str2) {
        LinkInventoryItem linkInventoryItem = LinkInventoryItem()
        linkInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        linkInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        linkInventoryItem.InventoryBlock_Field.FolderID = sLInventoryEntry.uuid
        linkInventoryItem.InventoryBlock_Field.TransactionID = UUID.randomUUID()
        linkInventoryItem.InventoryBlock_Field.OldItemID = uuid
        linkInventoryItem.InventoryBlock_Field.Type = i2
        linkInventoryItem.InventoryBlock_Field.InvType = i
        linkInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str)
        linkInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2)
        linkInventoryItem.isReliable = true
        linkInventoryItem.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
            }
        SendMessage(linkInventoryItem)
    }

    public Unit MoveInventoryItem(final SLInventoryEntry sLInventoryEntry, final SLInventoryEntry sLInventoryEntry2) {
        final UUID uuid = sLInventoryEntry.parentUUID
        if (sLInventoryEntry.isFolder) {
            UpdateInventoryFolder updateInventoryFolder = UpdateInventoryFolder()
            updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID
            updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID
            UpdateInventoryFolder.FolderData folderData = UpdateInventoryFolder.FolderData()
            folderData.FolderID = sLInventoryEntry.uuid
            folderData.ParentID = sLInventoryEntry2.uuid
            folderData.Type = sLInventoryEntry.typeDefault
            folderData.Name = SLMessage.stringToVariableUTF(sLInventoryEntry.name)
            updateInventoryFolder.FolderData_Fields.add(folderData)
            updateInventoryFolder.isReliable = true
            updateInventoryFolder.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
                public Unit onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(uuid)
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry2.uuid)
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
                }
            SendMessage(updateInventoryFolder)
            return
        }
        MoveInventoryItem moveInventoryItem = MoveInventoryItem()
        moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moveInventoryItem.AgentData_Field.Stamp = false
        MoveInventoryItem.InventoryData inventoryData = MoveInventoryItem.InventoryData()
        inventoryData.FolderID = sLInventoryEntry2.uuid
        inventoryData.ItemID = sLInventoryEntry.uuid
        inventoryData.NewName = SLMessage.stringToVariableUTF(sLInventoryEntry.name)
        moveInventoryItem.InventoryData_Fields.add(inventoryData)
        moveInventoryItem.isReliable = true
        moveInventoryItem.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
            public Unit onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(uuid)
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry2.uuid)
            }
        SendMessage(moveInventoryItem)
    }

    public Unit MoveInventoryItemRaw(UUID uuid, String str, UUID uuid2) {
        MoveInventoryItem moveInventoryItem = MoveInventoryItem()
        moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID
        moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID
        moveInventoryItem.AgentData_Field.Stamp = false
        MoveInventoryItem.InventoryData inventoryData = MoveInventoryItem.InventoryData()
        inventoryData.FolderID = uuid2
        inventoryData.ItemID = uuid
        inventoryData.NewName = SLMessage.stringToVariableUTF(str)
        moveInventoryItem.InventoryData_Fields.add(inventoryData)
        moveInventoryItem.isReliable = true
        moveInventoryItem.setEventListener(this.reloadEvent)
        SendMessage(moveInventoryItem)
    }

    public Unit RenameInventoryItem(final SLInventoryEntry sLInventoryEntry, String str) {
        sLInventoryEntry.name = str
        try {
            this.db.saveEntry(sLInventoryEntry)
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e)
        }
        if (sLInventoryEntry.isFolder) {
            UpdateInventoryFolder updateInventoryFolder = UpdateInventoryFolder()
            updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID
            updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID
            UpdateInventoryFolder.FolderData folderData = UpdateInventoryFolder.FolderData()
            folderData.FolderID = sLInventoryEntry.uuid
            folderData.ParentID = sLInventoryEntry.parentUUID
            folderData.Type = sLInventoryEntry.typeDefault
            folderData.Name = SLMessage.stringToVariableUTF(str)
            updateInventoryFolder.FolderData_Fields.add(folderData)
            updateInventoryFolder.isReliable = true
            updateInventoryFolder.setEventListener(SLMessageEventListener.SLMessageBaseEventListener() {
                public Unit onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
                }
            SendMessage(updateInventoryFolder)
            return
        }
        DoUpdateInventoryItem(sLInventoryEntry, OnInventoryCallbackListener(this, sLInventoryEntry) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f83$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f84$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.3.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.3.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, class status: UNLOADED
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
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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

    }

    public Unit TrashInventoryItem(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14)
        if (findSpecialFolder != null) {
            MoveInventoryItem(sLInventoryEntry, findSpecialFolder)
        }
    }

    public Unit UpdateNotecard(SLInventoryEntry sLInventoryEntry, UUID uuid, Boolean z, String str, String str2, Byte[] bArr, UUID uuid2, Int i, OnNotecardUpdatedListener onNotecardUpdatedListener) {
        if (sLInventoryEntry != null) {
            z2 = !(Objects.equal(sLInventoryEntry.name, str) ? Objects.equal(sLInventoryEntry.description, str2) : false)
        } else {
            z2 = true
        }
        if (uuid2 == null) {
            if (sLInventoryEntry == null) {
                Debug.Printf("Notecard: Creating inventory entry.", Object[0])
                DoCreateInventoryItem(uuid, z ? SLAssetType.AT_LSL_TEXT.getTypeCode() : SLAssetType.AT_NOTECARD.getTypeCode(), z ? SLInventoryType.IT_LSL.getTypeCode() : SLInventoryType.IT_NOTECARD.getTypeCode(), str, str2, OnInventoryCallbackListener(z, this, bArr, onNotecardUpdatedListener) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Boolean f97$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f98$f1

                    /* renamed from: -$f2 */
                    private val /* synthetic */ Object f99$f2

                    /* renamed from: -$f3 */
                    private val /* synthetic */ Object f100$f3

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.7.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.7.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, class status: UNLOADED
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

            } else if (z2) {
                sLInventoryEntry.name = str
                sLInventoryEntry.description = str2
                try {
                    this.db.saveEntry(sLInventoryEntry)
                } catch (DBObject.DatabaseBindingException e) {
                    Debug.Warning(e)
                }
                Debug.Printf("Notecard: Updating existing inventory entry %s", sLInventoryEntry.uuid)
                DoUpdateInventoryItem(sLInventoryEntry, OnInventoryCallbackListener(z, this, bArr, onNotecardUpdatedListener) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Boolean f101$f0

                    /* renamed from: -$f1 */
                    private val /* synthetic */ Object f102$f1

                    /* renamed from: -$f2 */
                    private val /* synthetic */ Object f103$f2

                    /* renamed from: -$f3 */
                    private val /* synthetic */ Object f104$f3

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.8.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.8.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, class status: UNLOADED
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
                StartUploadingNotecardContents(sLInventoryEntry, (UUID) null, z, bArr, onNotecardUpdatedListener)
            }
        } else if (!z2 || sLInventoryEntry == null) {
            StartUploadingNotecardContents(sLInventoryEntry, uuid2, z, bArr, onNotecardUpdatedListener)
        } else {
            sLInventoryEntry.name = str
            sLInventoryEntry.description = str2
            final Int i2 = i
            final SLInventoryEntry sLInventoryEntry2 = sLInventoryEntry
            final UUID uuid3 = uuid2
            final Boolean z3 = z
            final Byte[] bArr2 = bArr
            final OnNotecardUpdatedListener onNotecardUpdatedListener2 = onNotecardUpdatedListener
            DoUpdateTaskInventoryItem(sLInventoryEntry, i, SLMessageEventListener.SLMessageBaseEventListener() {
                public Unit onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getObjectsManager().requestTaskInventoryUpdate(i2)
                    SLInventory.this.StartUploadingNotecardContents(sLInventoryEntry2, uuid3, z3, bArr2, onNotecardUpdatedListener2)
                }
        }
    }

    public Unit UpdateStoreInventoryItem(SLInventoryEntry sLInventoryEntry) {
        try {
            this.db.saveEntry(sLInventoryEntry)
            DoUpdateInventoryItem(sLInventoryEntry, OnInventoryCallbackListener(this, sLInventoryEntry) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f85$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f86$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.4.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.4.$m$0(com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry):Unit, class status: UNLOADED
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
                	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
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

        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e)
        }
    }

    public Boolean canMoveToTrash(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14)
        return (findSpecialFolder == null || sLInventoryEntry.parent_id == findSpecialFolder.getId()) ? false : true
    }

    public UUID findSpecialFolder(Int i) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), i)
        if (findSpecialFolder != null) {
            return findSpecialFolder.uuid
        }
        return null
    }

    public UUID getCallingCardsFolderUUID() {
        SLInventoryEntry findSpecialFolder
        if (this.rootFolder == null || (findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 2)) == null) {
            return null
        }
        return findSpecialFolder.uuid
    }

    public SLCaps getCaps() {
        return this.caps
    }

    public InventoryDB getDatabase() {
        return this.db
    }

    public ExecutorService getExecutor() {
        if (this.executor == null) {
            this.executor = Executors.newSingleThreadExecutor()
        }
        return this.executor
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_10310  reason: not valid java name */
    public /* synthetic */ Unit m177lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_10310(Long j, UUID uuid, Boolean z, Boolean z2) {
        Debug.Printf("Inventory: onFetchComplete: folderId = '%s'", Long.valueOf(j))
        this.fetchRequests.remove(uuid)
        SLInventoryEntry findEntry = this.db.findEntry(uuid)
        if (findEntry != null) {
            findEntry.sessionID = this.circuitInfo.sessionID
            findEntry.fetchFailed = !z
            try {
                this.db.saveEntry(findEntry)
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e)
            }
        }
        if (z) {
            this.folderEntryResultHandler.onResultData(uuid, findEntry)
        } else if (!z2) {
            this.folderEntryResultHandler.onResultError(uuid, InventoryFetchException("Failed to retrieve folder contents"))
        }
        updateFolderLoadingStatus(uuid)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_27010  reason: not valid java name */
    public /* synthetic */ Unit m178lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_27010(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid)
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_42520  reason: not valid java name */
    public /* synthetic */ Unit m179lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_42520(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_48053  reason: not valid java name */
    public /* synthetic */ Unit m180lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_48053(SLInventoryEntry sLInventoryEntry, Byte[] bArr, UUID uuid, Boolean z, OnNotecardUpdatedListener onNotecardUpdatedListener) {
        UUID uuid2 = null
        Object[] objArr = Object[1]
        objArr[0] = sLInventoryEntry != null ? sLInventoryEntry.uuid : null
        Debug.Printf("Notecard: Starting to upload contents for entry %s", objArr)
        String UploadNotecardContents = bArr != null ? UploadNotecardContents(sLInventoryEntry, uuid, z, bArr) : null
        Object[] objArr2 = Object[1]
        if (sLInventoryEntry != null) {
            uuid2 = sLInventoryEntry.uuid
        }
        objArr2[0] = uuid2
        Debug.Printf("Notecard: Notecard entry %s updated", objArr2)
        if (onNotecardUpdatedListener != null) {
            onNotecardUpdatedListener.onNotecardUpdated(sLInventoryEntry, UploadNotecardContents)
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_49599  reason: not valid java name */
    public /* synthetic */ Unit m181lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_49599(Boolean z, Byte[] bArr, OnNotecardUpdatedListener onNotecardUpdatedListener, SLInventoryEntry sLInventoryEntry) {
        StartUploadingNotecardContents(sLInventoryEntry, (UUID) null, z, bArr, onNotecardUpdatedListener)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_50229  reason: not valid java name */
    public /* synthetic */ Unit m182lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_50229(Boolean z, Byte[] bArr, OnNotecardUpdatedListener onNotecardUpdatedListener, SLInventoryEntry sLInventoryEntry) {
        StartUploadingNotecardContents(sLInventoryEntry, (UUID) null, z, bArr, onNotecardUpdatedListener)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_51539  reason: not valid java name */
    public /* synthetic */ Unit m183lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_51539(UUID uuid, UUID uuid2, UUID uuid3, Runnable runnable) {
        try {
            String capabilityOrThrow = getCaps().getCapabilityOrThrow(SLCaps.SLCapability.CopyInventoryFromNotecard)
            LLSDXMLRequest().PerformRequest(capabilityOrThrow, LLSDMap(LLSDMap.LLSDMapEntry("notecard-id", LLSDUUID(uuid)), LLSDMap.LLSDMapEntry("object-id", LLSDUUID()), LLSDMap.LLSDMapEntry("item-id", LLSDUUID(uuid2)), LLSDMap.LLSDMapEntry("folder-id", LLSDUUID(uuid3)), LLSDMap.LLSDMapEntry("callback-id", LLSDInt(0))))
        } catch (IOException e) {
            Debug.Warning(e)
        } catch (LLSDException e2) {
            Debug.Warning(e2)
        } catch (SLCaps.NoSuchCapabilityException e3) {
            Debug.Warning(e3)
        }
        if (runnable != null) {
            runnable.run()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_53068  reason: not valid java name */
    public /* synthetic */ Unit m184lambda$com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_53068(String str, final ImmutableSet immutableSet, final Int i, final Function function) {
        DoCreateNewFolder(this.rootFolder, str, true, Function<UUID, Void>() {
            public Void apply(UUID uuid) {
                if (uuid != null) {
                    for (UUID r0 : immutableSet) {
                        SLInventory.this.MoveTaskInventory(uuid, i, r0)
                    }
                }
                function.apply(uuid)
                return null
            }
    }

    /* access modifiers changed from: package-private */
    public Unit onFetchComplete(SLInventoryFetchRequest sLInventoryFetchRequest, UUID uuid, Long j, Boolean z, Boolean z2) {
        if (this.dbExecutor != null) {
            this.dbExecutor.execute(Runnable(z, z2, j, this, uuid) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Boolean f77$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Boolean f78$f1

                /* renamed from: -$f2 */
                private val /* synthetic */ Long f79$f2

                /* renamed from: -$f3 */
                private val /* synthetic */ Object f80$f3

                /* renamed from: -$f4 */
                private val /* synthetic */ Object f81$f4

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.10.$m$0():Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.10.$m$0():Unit, class status: UNLOADED
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

        }
    }
}
