package com.linkpoint.slproto.users.manager

import com.google.common.base.Objects
import com.google.common.base.Strings
import com.linkpoint.Debug
import com.linkpoint.orm.DBObject
import com.linkpoint.orm.InventoryDB
import com.linkpoint.orm.InventoryDBManager
import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.orm.InventoryQuery
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.DisposeHandler
import com.linkpoint.react.OpportunisticExecutor
import com.linkpoint.react.Refreshable
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.RequestProcessor
import com.linkpoint.react.RequestSource
import com.linkpoint.react.Subscribable
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UnsubscribableOne
import com.linkpoint.slproto.inventory.SLInventoryEntry
import java.util.Map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventoryManager {
    private val SubscriptionSingleDataPool<InventoryClipboardEntry> clipboardPool = SubscriptionSingleDataPool<>()
    /* access modifiers changed from: private */
    val AtomicReference<UUID> currentSessionID = AtomicReference<>((Object) null)
    /* access modifiers changed from: private */
    val SubscriptionPool<InventoryQuery, InventoryEntryList> entryListPool = SubscriptionPool<>()
    /* access modifiers changed from: private */
    val SubscriptionPool<UUID, SLInventoryEntry> folderEntryPool = SubscriptionPool<>()
    private val SubscriptionPool<UUID, Boolean> folderLoadingPool = SubscriptionPool<>()
    private val RequestProcessor<UUID, SLInventoryEntry, SLInventoryEntry> folderRequestProcessor
    /* access modifiers changed from: private */
    val InventoryDB inventoryDB
    /* access modifiers changed from: private */
    val OpportunisticExecutor inventoryDbExecutor = OpportunisticExecutor("InventoryDB")
    private val RequestHandler<InventoryQuery> queryRequestHandler = RequestHandler<InventoryQuery>() {
        private val Map<InventoryQuery, FolderSubscription> folderQueries = ConcurrentHashMap()

        fun onRequest(inventoryQuery: InventoryQuery) {
            FolderSubscription put
            if (inventoryQuery.containsString() != null) {
                InventoryManager.this.entryListPool.onResultData(inventoryQuery, inventoryQuery.query((SLInventoryEntry) null, InventoryManager.this.inventoryDB))
                return
            }
            val folderId: UUID = inventoryQuery.folderId()
            if (folderId == null) {
                folderId = (UUID) InventoryManager.this.rootFolderID.get()
            }
            Debug.Printf("Inventory: queryRequestHandler: folderId = '%s'", folderId)
            if (folderId != null && (put = this.folderQueries.put(inventoryQuery, FolderSubscription(InventoryManager.this, inventoryQuery, folderId, (FolderSubscription) null))) != null) {
                put.unsubscribe()
            }
        }

        fun onRequestCancelled(inventoryQuery: InventoryQuery) {
            val folderSubscription: FolderSubscription = this.folderQueries.get(inventoryQuery)
            if (folderSubscription != null) {
                folderSubscription.unsubscribe()
            }
        }
    }
    /* access modifiers changed from: private */
    val AtomicReference<UUID> rootFolderID = AtomicReference<>((Object) null)
    private val SubscriptionPool<SubscriptionSingleKey, Boolean> searchProcessPool = SubscriptionPool<>()
    private val SubscriptionPool<SubscriptionSingleKey, Boolean> searchRunningPool = SubscriptionPool<>()

    private class FolderSubscription : Subscription.OnData<SLInventoryEntry>, Subscription.OnError, UnsubscribableOne {
        private val InventoryQuery query
        private val Subscription<UUID, SLInventoryEntry> subscription

        private FolderSubscription(InventoryQuery inventoryQuery, UUID uuid) {
            this.query = inventoryQuery
            Debug.Printf("Inventory: folder subscription: folderId = '%s'", uuid)
            this.subscription = InventoryManager.this.folderEntryPool.subscribe(uuid, InventoryManager.this.inventoryDbExecutor, this, this)
        }

        /* synthetic */ FolderSubscription(InventoryManager inventoryManager, InventoryQuery inventoryQuery, UUID uuid, FolderSubscription folderSubscription) {
            this(inventoryQuery, uuid)
        }

        fun onData(sLInventoryEntry: SLInventoryEntry) {
            if (sLInventoryEntry != null) {
                Debug.Printf("Inventory: folder subscription got name: %s with folderId = '%s'", sLInventoryEntry.name, sLInventoryEntry.uuid)
            }
            InventoryManager.this.entryListPool.onResultData(this.query, this.query.query(sLInventoryEntry, InventoryManager.this.inventoryDB))
        }

        fun onError(th: Throwable) {
            Debug.Printf("Inventory: subscription error: %s", th)
            Debug.Warning(th)
            InventoryManager.this.entryListPool.onResultError(this.query, th)
        }

        fun unsubscribe() {
            this.subscription.unsubscribe()
        }
    }

    @JvmStatic
    class InventoryClipboardEntry {
        val SLInventoryEntry inventoryEntry
        val Boolean isCut

        public InventoryClipboardEntry(Boolean z, SLInventoryEntry sLInventoryEntry) {
            this.isCut = z
            this.inventoryEntry = sLInventoryEntry
        }
    }

    public InventoryManager(UUID uuid) {
        final InventoryDB userInventoryDB = InventoryDBManager.getUserInventoryDB(uuid)
        if (userInventoryDB == null) {
            throw IllegalArgumentException("Null inventory database")
        }
        this.inventoryDB = userInventoryDB
        this.folderRequestProcessor = RequestProcessor<UUID, SLInventoryEntry, SLInventoryEntry>(this.folderEntryPool, this.inventoryDbExecutor) {
            /* access modifiers changed from: protected */
             public fun isRequestComplete(uuid: UUID, sLInventoryEntry: SLInventoryEntry): Boolean {
                return sLInventoryEntry != null && Objects.equal(sLInventoryEntry.sessionID, InventoryManager.this.currentSessionID.get())
            }

            /* access modifiers changed from: protected */
             public fun processRequest(uuid: UUID): SLInventoryEntry {
                return userInventoryDB.findEntry(uuid)
            }

            /* access modifiers changed from: protected */
             public fun processResult(uuid: UUID, sLInventoryEntry: SLInventoryEntry): SLInventoryEntry {
                if (sLInventoryEntry != null) {
                    Debug.Printf("Inventory: entry subscription got name: %s with folderId = '%s'", sLInventoryEntry.name, sLInventoryEntry.uuid)
                }
                InventoryManager.this.updateSearchResults()
                return sLInventoryEntry
            }
        }
        this.folderEntryPool.setCacheInvalidateHandler(Refreshable(userInventoryDB) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f175$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY.2.$m$0(java.lang.Object):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY.2.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

        }, this.inventoryDbExecutor)
        this.entryListPool.attachRequestHandler(AsyncRequestHandler(this.inventoryDbExecutor, this.queryRequestHandler))
        this.entryListPool.setDisposeHandler(DisposeHandler() {
            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY.1.$m$0(java.lang.Object):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

        }, this.inventoryDbExecutor)
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_3450  reason: not valid java name */
    // TODO: Review synthetic accessor - static /* synthetic */ Unit m324lambda$com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_3450(InventoryDB inventoryDB2, UUID uuid) {
        val findEntry: SLInventoryEntry = inventoryDB2.findEntry(uuid)
        if (findEntry != null) {
            findEntry.sessionID = null
            try {
                inventoryDB2.saveEntry(findEntry)
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e)
            }
        }
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_6838  reason: not valid java name */
    // TODO: Review synthetic accessor - static /* synthetic */ Boolean m325lambda$com_lumiyaviewer_lumiya_slproto_users_manager_InventoryManager_6838(InventoryQuery inventoryQuery) {
        if (inventoryQuery != null) {
            return !Strings.isNullOrEmpty(inventoryQuery.containsString())
        }
        return false
    }

    /* access modifiers changed from: private */
    fun updateSearchResults() {
        this.entryListPool.requestUpdateSome($Lambda$JIBenvPHaOomPgMJhTFPuiVXBzY())
    }

    fun copyToClipboard(inventoryClipboardEntry: InventoryClipboardEntry) {
        this.clipboardPool.setData(SubscriptionSingleKey.Value, inventoryClipboardEntry)
    }

    public Subscribable<SubscriptionSingleKey, InventoryClipboardEntry> getClipboard() {
        return this.clipboardPool
    }

     public fun getDatabase(): InventoryDB {
        return this.inventoryDB
    }

     public fun getExecutor(): Executor {
        return this.inventoryDbExecutor
    }

    public Subscribable<UUID, SLInventoryEntry> getFolderEntryPool() {
        return this.folderEntryPool
    }

    public Subscribable<UUID, Boolean> getFolderLoading() {
        return this.folderLoadingPool
    }

    public RequestSource<UUID, Boolean> getFolderLoadingRequestSource() {
        return this.folderLoadingPool
    }

    public RequestSource<UUID, SLInventoryEntry> getFolderRequestSource() {
        return this.folderRequestProcessor
    }

    public Subscribable<InventoryQuery, InventoryEntryList> getInventoryEntries() {
        return this.entryListPool
    }

     public fun getRootFolder(): UUID {
        return this.rootFolderID.get()
    }

    public Subscribable<SubscriptionSingleKey, Boolean> getSearchProcess() {
        return this.searchProcessPool
    }

    public RequestSource<SubscriptionSingleKey, Boolean> getSearchProcessRequestSource() {
        return this.searchProcessPool
    }

    public SubscriptionPool<SubscriptionSingleKey, Boolean> getSearchRunning() {
        return this.searchRunningPool
    }

    fun requestFolderUpdate(uuid: UUID) {
        this.folderEntryPool.requestUpdate(uuid)
    }

    fun setCurrentSessionID(uuid: UUID) {
        this.currentSessionID.set(uuid)
    }

    fun setRootFolder(uuid: UUID) {
        this.rootFolderID.set(uuid)
    }
}
