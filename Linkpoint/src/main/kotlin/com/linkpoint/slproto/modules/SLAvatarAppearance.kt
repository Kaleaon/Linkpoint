package com.linkpoint.slproto.modules

import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import com.linkpoint.Debug
import com.linkpoint.orm.InventoryDB
import com.linkpoint.orm.InventoryEntryList
import com.linkpoint.orm.InventoryQuery
import com.linkpoint.react.AsyncRequestHandler
import com.linkpoint.react.RequestHandler
import com.linkpoint.react.ResultHandler
import com.linkpoint.react.SimpleRequestHandler
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.SLParcelInfo
import com.linkpoint.slproto.assets.SLWearable
import com.linkpoint.slproto.assets.SLWearableData
import com.linkpoint.slproto.assets.SLWearableType
import com.linkpoint.slproto.avatar.SLAvatarParams
import com.linkpoint.slproto.baker.BakeProcess
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.events.SLBakingProgressEvent
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.https.GenericHTTPExecutor
import com.linkpoint.slproto.https.LLSDXMLRequest
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.inventory.SLInventory
import com.linkpoint.slproto.inventory.SLInventoryEntry
import com.linkpoint.slproto.inventory.SLInventoryType
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.messages.AgentIsNowWearing
import com.linkpoint.slproto.messages.AgentSetAppearance
import com.linkpoint.slproto.messages.AgentWearablesRequest
import com.linkpoint.slproto.messages.AgentWearablesUpdate
import com.linkpoint.slproto.messages.AvatarAppearance
import com.linkpoint.slproto.messages.DetachAttachmentIntoInv
import com.linkpoint.slproto.messages.ObjectDetach
import com.linkpoint.slproto.messages.RezMultipleAttachmentsFromInv
import com.linkpoint.slproto.messages.RezSingleAttachmentFromInv
import com.linkpoint.slproto.modules.rlv.RLVController
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.objects.SLObjectInfo
import com.linkpoint.slproto.textures.SLTextureEntry
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.manager.UserManager
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.NoSuchElementException
import java.util.UUID
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nonnull

class SLAvatarAppearance : SLModule(), SLWearable.OnWearableStatusChangeListener {
    private const val Int Param_agentSizeVPHeadSize = 682
    private const val Int Param_agentSizeVPHeelHeight = 198
    private const val Int Param_agentSizeVPHeight = 33
    private const val Int Param_agentSizeVPHipLength = 842
    private const val Int Param_agentSizeVPLegLength = 692
    private const val Int Param_agentSizeVPNeckLength = 756
    private const val Int Param_agentSizeVPPlatformHeight = 503
    private SLTextureEntry agentBakedTextures
    private Boolean agentSizeKnown = false
    private Float agentSizeVPHeadSize
    private Float agentSizeVPHeelHeight
    private Float agentSizeVPHeight
    private Float agentSizeVPHipLength
    private Float agentSizeVPLegLength
    private Float agentSizeVPNeckLength
    private Float agentSizeVPPlatformHeight
    private IntArray agentVisualParams
    private volatile BakeProcess bakeProcess = null
    private Thread bakingThread = null
    private val SLCaps caps
    private val AtomicReference<UUID> cofFolderUUID = AtomicReference<>()
    private volatile Boolean cofReady = false
    private volatile Int currentCofAppearanceVersion = 0
    private volatile Int currentCofInventoryVersion = 0
    private val SubscriptionData<InventoryQuery, InventoryEntryList> currentOutfitFolder
    private val SubscriptionData<InventoryQuery, InventoryEntryList> findCofFolder
    private val SLInventory inventory
    private volatile Boolean lastCofUpdateError = false
    private volatile Int lastCofUpdatedVersion = 0
    private volatile Boolean legacyAppearanceReady = false
    private volatile Boolean multiLayerDone = false
    private volatile Boolean needUpdateAppearance = false
    private val AtomicBoolean needUpdateCOF = AtomicBoolean(false)
    private val SLParcelInfo parcelInfo
    private Future<?> serverSideAppearanceUpdateTask = null
    private Int setAppearanceSerialNum = 1
    private val UserManager userManager
    private val AtomicReference<Map<UUID, String>> wantedAttachments = AtomicReference<>(ImmutableMap.of())
    private SLInventoryEntry wantedOutfitFolder = null
    private volatile ImmutableMap<UUID, String> wornAttachments = ImmutableMap.of()
    private val RequestHandler<SubscriptionSingleKey> wornItemsRequestHandler = AsyncRequestHandler(this.agentCircuit, SimpleRequestHandler<SubscriptionSingleKey>() {
        fun onRequest(subscriptionSingleKey: SubscriptionSingleKey) {
            if (SLAvatarAppearance.this.wornItemsResultHandler != null) {
                SLAvatarAppearance.this.wornItemsResultHandler.onResultData(subscriptionSingleKey, SLAvatarAppearance.this.getWornItems())
            }
        }
    /* access modifiers changed from: private */
    val ResultHandler<SubscriptionSingleKey, ImmutableList<WornItem>> wornItemsResultHandler
    private volatile Table<SLWearableType, UUID, SLWearable> wornWearables = ImmutableTable.of()

    @JvmStatic
    class WornItem {
        private val Int attachedTo
        private val Boolean isTouchable
        private val UUID itemID
        private val String name
        /* access modifiers changed from: private */
        val Int objectLocalID
        private val SLWearableType wornOn

        WornItem(SLWearableType sLWearableType, Int i, UUID uuid, String str, Int i2, Boolean z) {
            this.wornOn = sLWearableType
            this.attachedTo = i
            this.itemID = uuid
            this.name = str
            this.objectLocalID = i2
            this.isTouchable = z
        }

        /* access modifiers changed from: package-private */
         public fun getAttachedTo(): Int {
            return this.attachedTo
        }

         public fun getIsTouchable(): Boolean {
            return this.isTouchable
        }

         public fun getName(): String {
            return this.name
        }

         public fun getObjectLocalID(): Int {
            return this.objectLocalID
        }

         public fun getWornOn(): SLWearableType {
            return this.wornOn
        }

         public fun itemID(): UUID {
            return this.itemID
        }
    }

    public SLAvatarAppearance(SLAgentCircuit sLAgentCircuit, SLInventory sLInventory, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.caps = sLCaps
        this.inventory = sLInventory
        this.parcelInfo = sLAgentCircuit.getGridConnection().parcelInfo
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID())
        this.currentOutfitFolder = SubscriptionData<>(sLAgentCircuit, $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(this))
        this.findCofFolder = SubscriptionData<>(sLAgentCircuit, Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f118$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.$m$0(java.lang.Object):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.$m$0(java.lang.Object):Unit, class status: UNLOADED
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

        // Fixed decompilation artifact - proper method structure
        if (this.userManager != null) {
            this.wornItemsResultHandler = this.userManager.wornItems().attachRequestHandler(this.wornItemsRequestHandler)
        } else {
            this.wornItemsResultHandler = null
        }
    }

    private fun DetachItem(i: Int) {
        SLObjectAvatarInfo agentAvatar
        Debug.Log("Outfits: detaching item " + i)
        val z2: Boolean = false
        val map: Map = this.wantedAttachments.get()
        if (map != null) {
            val hashMap: HashMap = HashMap(map)
            if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
                try {
                    val it: Iterator<SLObjectInfo> = agentAvatar.treeNode.iterator()
                    while (true) {
                        if (!it.hasNext()) {
                            z = false
                            break
                        }
                        val next: SLObjectInfo = it.next()
                        if (next.attachedToUUID != null && (!next.isDead) && next.localID == i) {
                            if (hashMap.remove(next.getId()) != null) {
                                z = true
                                break
                            } else if (hashMap.remove(next.attachedToUUID) != null) {
                                z = true
                                break
                            }
                        }
                    }
                    z2 = z
                } catch (NoSuchElementException e) {
                    Debug.Warning(e)
                }
            }
            if (z2) {
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap))
            }
        }
        val objectDetach: ObjectDetach = ObjectDetach()
        objectDetach.AgentData_Field.AgentID = this.circuitInfo.agentID
        objectDetach.AgentData_Field.SessionID = this.circuitInfo.sessionID
        ObjectDetach.ObjectData objectData = ObjectDetach.ObjectData()
        objectData.ObjectLocalID = i
        objectDetach.ObjectData_Fields.add(objectData)
        objectDetach.isReliable = true
        SendMessage(objectDetach)
        if (z2) {
            this.needUpdateCOF.set(true)
            UpdateCOFContents()
        }
    }

    private fun ForceUpdateAppearance(z: Boolean) {
        this.needUpdateAppearance = true
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            this.eventBus.publish(SLBakingProgressEvent(true, false, 0))
        } else if (z) {
            this.lastCofUpdatedVersion = 0
            this.currentCofAppearanceVersion = 0
            RequestServerRebake()
        }
        StartUpdatingAppearance()
    }

    private fun ProcessMultiLayer() {
        if (!this.multiLayerDone && this.cofReady && this.legacyAppearanceReady) {
            UpdateMultiLayer()
        }
    }

    private fun RequestServerRebake() {
        SLInventoryEntry folder
        val capability: String = this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance)
        val data: InventoryEntryList = this.currentOutfitFolder.getData()
        if (capability != null && data != null && (folder = data.getFolder()) != null) {
            this.currentCofInventoryVersion = folder.version
            if ((this.currentCofInventoryVersion != this.lastCofUpdatedVersion && this.currentCofInventoryVersion != this.currentCofAppearanceVersion) || this.lastCofUpdateError) {
                this.lastCofUpdatedVersion = this.currentCofInventoryVersion
                this.lastCofUpdateError = false
                UpdateServerSideAppearance(capability, folder.version)
            }
        }
    }

    private fun SendAgentIsNowWearing() {
        val agentIsNowWearing: AgentIsNowWearing = AgentIsNowWearing()
        agentIsNowWearing.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentIsNowWearing.AgentData_Field.SessionID = this.circuitInfo.sessionID
        for (SLWearableType sLWearableType : SLWearableType.values()) {
            val row: Map<UUID, SLWearable> = this.wornWearables.row(sLWearableType)
            if (row != null) {
                z = true
                for (SLWearable sLWearable : row.values()) {
                    AgentIsNowWearing.WearableData wearableData = AgentIsNowWearing.WearableData()
                    wearableData.ItemID = sLWearable.itemID
                    wearableData.WearableType = sLWearableType.getTypeCode()
                    agentIsNowWearing.WearableData_Fields.add(wearableData)
                    z = false
                }
            } else {
                z = true
            }
            if (z) {
                AgentIsNowWearing.WearableData wearableData2 = AgentIsNowWearing.WearableData()
                wearableData2.ItemID = UUID(0, 0)
                wearableData2.WearableType = sLWearableType.getTypeCode()
                agentIsNowWearing.WearableData_Fields.add(wearableData2)
            }
        }
        Debug.Log("AvatarAppearance: Sending AgentIsNowWearing, " + agentIsNowWearing.WearableData_Fields.size() + " wearables.")
        agentIsNowWearing.isReliable = true
        SendMessage(agentIsNowWearing)
        this.needUpdateCOF.set(true)
        UpdateCOFContents()
    }

    private fun SendAvatarSetAppearance() {
        val sLObjectAvatarInfo: SLObjectAvatarInfo = null
        UpdateCOFContents()
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            if (this.parcelInfo != null) {
                sLObjectAvatarInfo = this.parcelInfo.getAgentAvatar()
            }
            if (!(this.agentBakedTextures == null || sLObjectAvatarInfo == null)) {
                sLObjectAvatarInfo.ApplyAvatarTextures(this.agentBakedTextures, true)
            }
            val agentSetAppearance: AgentSetAppearance = AgentSetAppearance()
            agentSetAppearance.AgentData_Field.AgentID = this.circuitInfo.agentID
            agentSetAppearance.AgentData_Field.SessionID = this.circuitInfo.sessionID
            agentSetAppearance.AgentData_Field.SerialNum = this.setAppearanceSerialNum
            agentSetAppearance.AgentData_Field.Size = LLVector3()
            agentSetAppearance.AgentData_Field.Size.x = 1.0f
            agentSetAppearance.AgentData_Field.Size.y = 1.0f
            agentSetAppearance.AgentData_Field.Size.z = 1.0f
            if (this.agentBakedTextures != null) {
                agentSetAppearance.ObjectData_Field.TextureEntry = this.agentBakedTextures.packByteArray()
            } else {
                agentSetAppearance.ObjectData_Field.TextureEntry = Byte[0]
            }
            this.agentVisualParams = getAppearanceParams()
            if (sLObjectAvatarInfo != null) {
                sLObjectAvatarInfo.ApplyAvatarVisualParams(this.agentVisualParams)
            }
            for (Int i : this.agentVisualParams) {
                AgentSetAppearance.VisualParam visualParam = AgentSetAppearance.VisualParam()
                visualParam.ParamValue = i
                agentSetAppearance.VisualParam_Fields.add(visualParam)
            }
            if (this.agentSizeKnown && areWearablesReady()) {
                agentSetAppearance.AgentData_Field.Size.x = 0.45f
                agentSetAppearance.AgentData_Field.Size.y = 0.6f
                agentSetAppearance.AgentData_Field.Size.z = getAgentHeight()
                Debug.Log("set agent height to " + agentSetAppearance.AgentData_Field.Size.z)
            }
            agentSetAppearance.isReliable = true
            Debug.Log("AvatarAppearance: Sending agentSetAppearance: " + agentSetAppearance.VisualParam_Fields.size() + " params, hasTextures = " + (this.agentBakedTextures != null ? "yes" : "no"))
            SendMessage(agentSetAppearance)
            this.setAppearanceSerialNum++
        }
    }

    private fun StartUpdatingAppearance() {
        updateIfWearablesReady()
    }

    private fun UpdateCOFContents() {
        InventoryEntryList<SLInventoryEntry> data
        SLInventoryEntry folder
        val areWearablesReady: Boolean = areWearablesReady()
        Debug.Printf("Wearables ready %b, cofReady %b", Boolean.valueOf(areWearablesReady), Boolean.valueOf(this.cofReady))
        if ((areWearablesReady ? this.cofReady : false) && (data = this.currentOutfitFolder.getData()) != null && (folder = data.getFolder()) != null && this.needUpdateCOF.getAndSet(false)) {
            this.currentCofInventoryVersion = folder.version
            val linkedList: LinkedList = LinkedList()
            val hashMap: HashMap = HashMap()
            val hashMap2: HashMap = HashMap()
            val hashSet: HashSet = HashSet()
            for (SLWearable sLWearable : this.wornWearables.values()) {
                if (!sLWearable.getIsFailed()) {
                    hashSet.add(sLWearable.itemID)
                    hashMap.put(sLWearable.itemID, sLWearable)
                }
            }
            val map: Map = this.wantedAttachments.get()
            if (map != null) {
                hashMap2.putAll(map)
            }
            val z3: Boolean = true
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (sLInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
                    if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                        if (!hashSet.contains(sLInventoryEntry.assetUUID)) {
                            linkedList.add(sLInventoryEntry.uuid)
                        }
                    } else if (sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && map != null && !map.containsKey(sLInventoryEntry.assetUUID)) {
                        Debug.Printf("Attached entry %s (%s) not found in wanted attachments", sLInventoryEntry.assetUUID, sLInventoryEntry.name)
                        linkedList.add(sLInventoryEntry.uuid)
                    }
                    hashMap.remove(sLInventoryEntry.assetUUID)
                    hashMap2.remove(sLInventoryEntry.assetUUID)
                    z2 = z3
                } else if (sLInventoryEntry.assetType != SLAssetType.AT_LINK_FOLDER.getTypeCode() || this.wantedOutfitFolder == null) {
                    z2 = z3
                } else if (!this.wantedOutfitFolder.uuid.equals(sLInventoryEntry.assetUUID)) {
                    linkedList.add(sLInventoryEntry.uuid)
                    z2 = z3
                } else {
                    z2 = false
                }
                z3 = z2
            }
            Debug.Printf("Update COF: addWearablesList %d, killList %d", Integer.valueOf(hashMap.size()), Integer.valueOf(linkedList.size()))
            if (!linkedList.isEmpty()) {
                this.inventory.DeleteMultiInventoryItemRaw(folder, linkedList)
                z = true
            } else {
                z = false
            }
            for (SLWearable sLWearable2 : hashMap.values()) {
                Debug.Printf("Update COF: adding %s, name = '%s'", sLWearable2.itemID, sLWearable2.getName())
                this.inventory.LinkInventoryItem(folder, sLWearable2.itemID, SLInventoryType.IT_WEARABLE.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), sLWearable2.getName(), "")
                z = true
            }
            for (Map.Entry entry : hashMap2.entrySet()) {
                Debug.Printf("Update COF: adding attachment %s, name = '%s'", entry.getKey(), entry.getValue())
                this.inventory.LinkInventoryItem(folder, (UUID) entry.getKey(), SLInventoryType.IT_OBJECT.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), (String) entry.getValue(), "")
                z = true
            }
            if (z3 && this.wantedOutfitFolder != null) {
                Debug.Printf("Update COF: adding outfit link for outfit folder %s", this.wantedOutfitFolder.uuid)
                this.inventory.LinkInventoryItem(folder, this.wantedOutfitFolder.uuid, SLInventoryType.IT_CATEGORY.getTypeCode(), SLAssetType.AT_LINK_FOLDER.getTypeCode(), this.wantedOutfitFolder.name, "")
                z = true
            }
            Debug.Printf("Update COF: COF updated (had changes: %b).", Boolean.valueOf(z))
            if (z && this.userManager != null) {
                this.userManager.getInventoryManager().requestFolderUpdate(folder.uuid)
            }
            RequestServerRebake()
        }
    }

    private fun UpdateCurrentOutfitLink(inventoryEntryList: InventoryEntryList) {
        val it: Iterator = inventoryEntryList.iterator()
        while (it.hasNext()) {
            val sLInventoryEntry: SLInventoryEntry = (SLInventoryEntry) it.next()
            if (sLInventoryEntry.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
                this.userManager.wornOutfitLink().setData(SubscriptionSingleKey.Value, sLInventoryEntry.assetUUID)
                return
            }
        }
    }

    private synchronized Unit UpdateMultiLayer() {
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv
        Debug.Printf("AvatarAppearance: MultiLayer: Updating multi layer appearance.", Object[0])
        val data: InventoryEntryList<SLInventoryEntry> = this.currentOutfitFolder.getData()
        val database: InventoryDB = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null
        if (!(data == null || database == null)) {
            val linkedList: LinkedList = LinkedList()
            val linkedList2: LinkedList<SLInventoryEntry> = LinkedList<>()
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                    linkedList.add(sLInventoryEntry)
                } else if (sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (sLInventoryEntry.isLink() && sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode())) {
                    linkedList2.add(sLInventoryEntry)
                }
            }
            if (WearItemList(database, linkedList, false)) {
                Debug.Printf("AvatarAppearance: MultiLayer: had some extra layers.", Object[0])
                SendAgentIsNowWearing()
                StartUpdatingAppearance()
            } else {
                Debug.Printf("AvatarAppearance: MultiLayer: no extra layers.", Object[0])
            }
            if (linkedList2.size() != 0) {
                Debug.Printf("AvatarAppearance: Re-attaching %d attachments from COF.", Integer.valueOf(linkedList2.size()))
                val hashMap: HashMap = HashMap()
                val randomUUID: UUID = UUID.randomUUID()
                val rezMultipleAttachmentsFromInv2: RezMultipleAttachmentsFromInv = null
                for (SLInventoryEntry sLInventoryEntry2 : linkedList2) {
                    val resolveLink: SLInventoryEntry = database.resolveLink(sLInventoryEntry2)
                    if (resolveLink != null) {
                        if (rezMultipleAttachmentsFromInv2 == null) {
                            rezMultipleAttachmentsFromInv2 = RezMultipleAttachmentsFromInv()
                            rezMultipleAttachmentsFromInv2.AgentData_Field.AgentID = this.circuitInfo.agentID
                            rezMultipleAttachmentsFromInv2.AgentData_Field.SessionID = this.circuitInfo.sessionID
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.CompoundMsgID = randomUUID
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.TotalObjects = linkedList2.size()
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.FirstDetachAll = false
                        }
                        RezMultipleAttachmentsFromInv.ObjectData objectData = RezMultipleAttachmentsFromInv.ObjectData()
                        Debug.Printf("Re-attaching attachment: entry %s (%s)", resolveLink.uuid, sLInventoryEntry2.name)
                        hashMap.put(resolveLink.uuid, sLInventoryEntry2.name)
                        objectData.ItemID = resolveLink.uuid
                        objectData.OwnerID = resolveLink.ownerUUID
                        objectData.AttachmentPt = 128
                        objectData.ItemFlags = resolveLink.flags
                        objectData.GroupMask = resolveLink.groupMask
                        objectData.EveryoneMask = resolveLink.everyoneMask
                        objectData.NextOwnerMask = resolveLink.nextOwnerMask
                        objectData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry2.name)
                        objectData.Description = SLMessage.stringToVariableOEM(sLInventoryEntry2.description)
                        rezMultipleAttachmentsFromInv2.ObjectData_Fields.add(objectData)
                        if (rezMultipleAttachmentsFromInv2.ObjectData_Fields.size() >= 4) {
                            rezMultipleAttachmentsFromInv2.isReliable = true
                            SendMessage(rezMultipleAttachmentsFromInv2)
                            rezMultipleAttachmentsFromInv = null
                            rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv
                        }
                    }
                    rezMultipleAttachmentsFromInv = rezMultipleAttachmentsFromInv2
                    rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv
                }
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap))
                if (rezMultipleAttachmentsFromInv2 != null) {
                    rezMultipleAttachmentsFromInv2.isReliable = true
                    SendMessage(rezMultipleAttachmentsFromInv2)
                }
            } else {
                Debug.Printf("AvatarAppearance: No attachments in COF.", Object[0])
            }
        }
        this.multiLayerDone = true
    }

    private synchronized Unit UpdateServerSideAppearance(String str, Int i) {
        Debug.Printf("AvatarAppearance: capURL '%s', cofVersion %d", str, Integer.valueOf(i))
        if (this.serverSideAppearanceUpdateTask != null) {
            this.serverSideAppearanceUpdateTask.cancel(true)
        }
        this.serverSideAppearanceUpdateTask = GenericHTTPExecutor.getInstance().submit(Runnable(i, this, str) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Int f119$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f120$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f121$f2

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.$m$0():Unit, class status: UNLOADED
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

    private fun UpdateWearableNames() {
        SLInventoryEntry resolveLink
        SLWearableType byCode
        SLWearable sLWearable
        val inventoryDB: InventoryDB = null
        val data: InventoryEntryList<SLInventoryEntry> = this.currentOutfitFolder.getData()
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase()
        }
        if (data != null && inventoryDB != null) {
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (!(sLInventoryEntry.isFolderOrFolderLink() || (resolveLink = inventoryDB.resolveLink(sLInventoryEntry)) == null || resolveLink.invType != SLInventoryType.IT_WEARABLE.getTypeCode() || (byCode = SLWearableType.getByCode(resolveLink.flags & 255)) == null || (sLWearable = this.wornWearables.get(byCode, resolveLink.assetUUID)) == null)) {
                    sLWearable.setInventoryName(resolveLink.name)
                }
            }
        }
    }

    private fun WearItemList(inventoryDB: InventoryDB, list: List<SLInventoryEntry>, z: Boolean): Boolean {
        SLWearableType byCode
        val z5: Boolean = false
        val rLVController: RLVController = this.agentCircuit.getModules().rlvController
        val create: HashBasedTable<SLWearableType, UUID, SLWearable> = HashBasedTable.create(this.wornWearables)
        val it: Iterator<T> = list.iterator()
        while (true) {
            z2 = z5
            if (!it.hasNext()) {
                break
            }
            val resolveLink: SLInventoryEntry = inventoryDB.resolveLink((SLInventoryEntry) it.next())
            if (!(resolveLink == null || (byCode = SLWearableType.getByCode(resolveLink.flags & 255)) == null)) {
                val isBodyPart: Boolean = !z ? byCode.isBodyPart() : true
                if (!rLVController.canWearItem(byCode)) {
                    z3 = false
                } else if (isBodyPart) {
                    if (!rLVController.canTakeItemOff(byCode)) {
                        val z6: Boolean = false
                        val it2: Iterator<T> = create.row(byCode).keySet().iterator()
                        while (true) {
                            z4 = z6
                            if (!it2.hasNext()) {
                                break
                            }
                            z6 = !((UUID) it2.next()).equals(resolveLink.assetUUID) ? true : z4
                        }
                        if (z4) {
                            z3 = false
                        }
                    }
                    z3 = true
                } else {
                    z3 = true
                }
                if (z3 && !create.contains(byCode, resolveLink.assetUUID)) {
                    if (isBodyPart) {
                        val hashSet: HashSet<UUID> = HashSet<>(create.row(byCode).keySet())
                        hashSet.remove(resolveLink.assetUUID)
                        for (UUID remove : hashSet) {
                            val remove2: SLWearable = create.remove(byCode, remove)
                            if (remove2 != null) {
                                remove2.dispose()
                            }
                        }
                    }
                    addWearable(create, byCode, resolveLink.uuid, resolveLink.assetUUID, resolveLink.name)
                    z2 = true
                }
            }
            z5 = z2
        }
        if (z2) {
            this.wornWearables = ImmutableTable.copyOf(create)
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables)
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
        }
        return z2
    }

     private fun addWearable(table: Table<SLWearableType, UUID, SLWearable>, sLWearableType: SLWearableType, uuid: UUID, uuid2: UUID, str: String): SLWearable {
        val sLWearable: SLWearable = SLWearable(this.userManager, this.agentCircuit, uuid, uuid2, sLWearableType, this)
        if (str != null) {
            sLWearable.setInventoryName(str)
        }
        table.put(sLWearableType, uuid2, sLWearable)
        return sLWearable
    }

     private fun areWearablesReady(): Boolean {
        val values: Array<SLWearableType> = SLWearableType.values()
        val length: Int = values.length
        val i: Int = 0
        val z6: Boolean = false
        val z7: Boolean = false
        while (i < length) {
            val sLWearableType: SLWearableType = values[i]
            val isCritical: Boolean = sLWearableType.getIsCritical()
            val row: Map<UUID, SLWearable> = this.wornWearables.row(sLWearableType)
            if (row != null) {
                z2 = false
                z = z7
                for (SLWearable sLWearable : row.values()) {
                    if (sLWearable.getIsValid()) {
                        z4 = true
                        z5 = z
                    } else if (!sLWearable.getIsFailed()) {
                        z4 = z2
                        z5 = true
                    } else {
                        z4 = z2
                        z5 = z
                    }
                    z = z5
                    z2 = z4
                }
            } else {
                z2 = false
                z = z7
            }
            if (!isCritical) {
                z3 = z6
            } else if (!z2) {
                val objArr: Array<Any> = Object[2]
                objArr[0] = sLWearableType
                objArr[1] = Integer.valueOf(row != null ? row.size() : 0)
                Debug.Printf("missing wearables on critical layer %s (worn: %d entries)", objArr)
                z3 = true
            } else {
                z3 = z6
            }
            i++
            z6 = z3
            z7 = z
        }
        Debug.Printf("hasNotDownloaded %b, hasCriticalMissing %b", Boolean.valueOf(z7), Boolean.valueOf(z6))
        if (!z7) {
            return !z6
        }
        return false
    }

     private fun canDetachItem(uuid: UUID): Boolean {
        SLObjectAvatarInfo agentAvatar
        if (this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null) {
            return true
        }
        try {
            for (SLObjectInfo next : agentAvatar.treeNode) {
                if (next.attachedToUUID != null && (!next.isDead) && next.attachedToUUID.equals(uuid)) {
                    if (!this.agentCircuit.getModules().rlvController.canDetachItem(next.attachmentID, next.getId())) {
                        return false
                    }
                }
            }
            return true
        } catch (NoSuchElementException e) {
            Debug.Warning(e)
            return true
        }
    }

     private fun canWearItem(sLWearableType: SLWearableType): Boolean {
        return this.agentCircuit.getModules().rlvController.canWearItem(sLWearableType)
    }

     private fun getAgentHeight(): Float {
        return (this.agentSizeVPLegLength * 0.1918f) + 1.706f + (this.agentSizeVPHipLength * 0.0375f) + (this.agentSizeVPHeight * 0.12022f) + (this.agentSizeVPHeadSize * 0.01117f) + (this.agentSizeVPNeckLength * 0.038f) + (this.agentSizeVPHeelHeight * 0.08f) + (this.agentSizeVPPlatformHeight * 0.07f)
    }

     private fun getAppearanceParams(): IntArray {
        SLAvatarParams.AvatarParam avatarParam
        val iArr: IntArray = Int[218]
        for (Int i = 0; i < 218; i++) {
            iArr[i] = 0
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramDefs[i]
            if (!(paramSet == null || paramSet.params.size() <= 0 || (avatarParam = (SLAvatarParams.AvatarParam) paramSet.params.get(0)) == null)) {
                val round: Int = Math.round(((avatarParam.defValue - avatarParam.minValue) * 255.0f) / (avatarParam.maxValue - avatarParam.minValue))
                if (round < 0) {
                    round = 0
                } else if (round > 255) {
                    round = 255
                }
                iArr[i] = round
            }
        }
        for (SLWearable wearableData : this.wornWearables.values()) {
            val wearableData2: SLWearableData = wearableData.getWearableData()
            if (wearableData2 != null) {
                for (SLWearableData.WearableParam wearableParam : wearableData2.params) {
                    SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableParam.paramIndex))
                    if (paramSet2 != null && paramSet2.params.size() > 0 && paramSet2.appearanceIndex >= 0) {
                        SLAvatarParams.AvatarParam avatarParam2 = (SLAvatarParams.AvatarParam) paramSet2.params.get(0)
                        val round2: Int = Math.round(((wearableParam.paramValue - avatarParam2.minValue) * 255.0f) / (avatarParam2.maxValue - avatarParam2.minValue))
                        if (round2 < 0) {
                            round2 = 0
                        } else if (round2 > 255) {
                            round2 = 255
                        }
                        iArr[paramSet2.appearanceIndex] = round2
                        switch (paramSet2.id) {
                            case 33:
                                this.agentSizeVPHeight = wearableParam.paramValue
                                break
                            case Param_agentSizeVPHeelHeight /*198*/:
                                this.agentSizeVPHeelHeight = wearableParam.paramValue
                                break
                            case Param_agentSizeVPPlatformHeight /*503*/:
                                this.agentSizeVPPlatformHeight = wearableParam.paramValue
                                break
                            case Param_agentSizeVPHeadSize /*682*/:
                                this.agentSizeVPHeadSize = wearableParam.paramValue
                                break
                            case Param_agentSizeVPLegLength /*692*/:
                                this.agentSizeVPLegLength = wearableParam.paramValue
                                break
                            case Param_agentSizeVPNeckLength /*756*/:
                                this.agentSizeVPNeckLength = wearableParam.paramValue
                                break
                            case Param_agentSizeVPHipLength /*842*/:
                                this.agentSizeVPHipLength = wearableParam.paramValue
                                break
                        }
                    }
                }
            }
        }
        this.agentSizeKnown = true
        return iArr
    }

    /* access modifiers changed from: private */
    public ImmutableList<WornItem> getWornItems() {
        SLObjectAvatarInfo agentAvatar
        ImmutableList.Builder builder = ImmutableList.builder()
        for (Table.Cell cell : this.wornWearables.cellSet()) {
            val sLWearable: SLWearable = (SLWearable) cell.getValue()
            if (sLWearable != null) {
                builder.add((Object) WornItem((SLWearableType) cell.getRowKey(), 0, (UUID) cell.getColumnKey(), sLWearable.getName(), 0, false))
            }
        }
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    builder.add((Object) WornItem((SLWearableType) null, next.attachmentID, next.getId(), next.getName(), next.localID, next.isTouchable()))
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e)
            }
        }
        return builder.build()
    }

     private fun isItemWorn(sLInventoryEntry: SLInventoryEntry, z: Boolean): Boolean {
        return sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, z) != null
    }

    /* access modifiers changed from: private */
    /* renamed from: onCofFolderEntry */
    fun m201com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref1(inventoryEntryList: InventoryEntryList) {
        if (inventoryEntryList != null) {
            val it: Iterator = inventoryEntryList.iterator()
            while (it.hasNext()) {
                val sLInventoryEntry: SLInventoryEntry = (SLInventoryEntry) it.next()
                if (sLInventoryEntry != null && sLInventoryEntry.isFolder && sLInventoryEntry.typeDefault == 46) {
                    this.cofFolderUUID.set(sLInventoryEntry.uuid)
                    this.findCofFolder.unsubscribe()
                    this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(sLInventoryEntry.uuid, (String) null, true, true, false, (SLAssetType) null))
                    return
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentOutfitFolder */
    fun m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0(inventoryEntryList: InventoryEntryList) {
        SLInventoryEntry folder
        if (inventoryEntryList != null && (folder = inventoryEntryList.getFolder()) != null && Objects.equal(folder.sessionID, this.agentCircuit.circuitInfo.sessionID)) {
            Debug.Log("AvatarAppearance: COF has been fetched from inventory.")
            UpdateWearableNames()
            this.cofReady = true
            UpdateCurrentOutfitLink(inventoryEntryList)
            ProcessMultiLayer()
            UpdateCOFContents()
            RequestServerRebake()
        }
    }

     private fun startBaking() {
        val bakeProcess2: BakeProcess = this.bakeProcess
        if (bakeProcess2 != null) {
            bakeProcess2.cancel()
        }
        this.bakeProcess = BakeProcess(this.wornWearables, this, this.agentCircuit.getModules().textureUploader, this.eventBus)
    }

     private fun updateIfWearablesReady() {
        if (areWearablesReady()) {
            SendAvatarSetAppearance()
            if (!this.needUpdateAppearance) {
                UpdateCOFContents()
            } else if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
                startBaking()
            }
        }
    }

    fun AttachInventoryItem(sLInventoryEntry: SLInventoryEntry, i: Int, z: Boolean) {
        val inventoryDB: InventoryDB = null
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase()
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry)
        }
        if (sLInventoryEntry != null) {
            if (sLInventoryEntry.assetType == SLAssetType.AT_CLOTHING.getTypeCode() || sLInventoryEntry.assetType == SLAssetType.AT_BODYPART.getTypeCode()) {
                WearItem(sLInventoryEntry, z)
                return
            }
            Debug.Printf("Outfits: Attaching inventory item %s", sLInventoryEntry.uuid.toString())
            val map: Map = this.wantedAttachments.get()
            if (map == null || (!map.containsKey(sLInventoryEntry.uuid))) {
                val hashMap: HashMap = HashMap()
                if (map != null) {
                    hashMap.putAll(map)
                }
                hashMap.put(sLInventoryEntry.uuid, sLInventoryEntry.name)
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap))
                z2 = true
            } else {
                z2 = false
            }
            val rezSingleAttachmentFromInv: RezSingleAttachmentFromInv = RezSingleAttachmentFromInv()
            rezSingleAttachmentFromInv.AgentData_Field.AgentID = this.circuitInfo.agentID
            rezSingleAttachmentFromInv.AgentData_Field.SessionID = this.circuitInfo.sessionID
            if (!z) {
                i |= 128
            }
            rezSingleAttachmentFromInv.ObjectData_Field.ItemID = sLInventoryEntry.uuid
            rezSingleAttachmentFromInv.ObjectData_Field.OwnerID = sLInventoryEntry.ownerUUID
            rezSingleAttachmentFromInv.ObjectData_Field.AttachmentPt = i
            rezSingleAttachmentFromInv.ObjectData_Field.ItemFlags = sLInventoryEntry.flags
            rezSingleAttachmentFromInv.ObjectData_Field.GroupMask = sLInventoryEntry.groupMask
            rezSingleAttachmentFromInv.ObjectData_Field.EveryoneMask = sLInventoryEntry.everyoneMask
            rezSingleAttachmentFromInv.ObjectData_Field.NextOwnerMask = sLInventoryEntry.nextOwnerMask
            rezSingleAttachmentFromInv.ObjectData_Field.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name)
            rezSingleAttachmentFromInv.ObjectData_Field.Description = SLMessage.stringToVariableOEM(sLInventoryEntry.description)
            rezSingleAttachmentFromInv.isReliable = true
            SendMessage(rezSingleAttachmentFromInv)
            if (z2) {
                this.needUpdateCOF.set(true)
                UpdateCOFContents()
            }
        }
    }

    fun ChangeOutfit(list: List<SLInventoryEntry>, z: Boolean, sLInventoryEntry: SLInventoryEntry) {
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv
        SLWearableType byCode
        SLWearable remove
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv2
        val database: InventoryDB = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null
        val map: Map = this.wantedAttachments.get()
        val hashMap: HashMap = map != null ? HashMap(map) : HashMap()
        if (z) {
            z2 = true
            hashMap.clear()
        } else {
            z2 = false
        }
        if (sLInventoryEntry != null) {
            if (z) {
                if (this.wantedOutfitFolder == null) {
                    this.wantedOutfitFolder = sLInventoryEntry
                    z3 = true
                } else if (!this.wantedOutfitFolder.uuid.equals(sLInventoryEntry.uuid)) {
                    this.wantedOutfitFolder = sLInventoryEntry
                    z3 = true
                }
            }
            z3 = z2
        } else {
            z3 = z2
        }
        val randomUUID: UUID = UUID.randomUUID()
        val arrayList: ArrayList = ArrayList()
        for (SLInventoryEntry sLInventoryEntry2 : list) {
            val resolveLink: SLInventoryEntry = database != null ? database.resolveLink(sLInventoryEntry2) : sLInventoryEntry2
            if (resolveLink != null) {
                sLInventoryEntry2 = resolveLink
            }
            if (sLInventoryEntry2 != null && (sLInventoryEntry2.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (sLInventoryEntry2.isLink() && sLInventoryEntry2.invType == SLInventoryType.IT_OBJECT.getTypeCode()))) {
                arrayList.add(sLInventoryEntry2)
            }
        }
        val rezMultipleAttachmentsFromInv3: RezMultipleAttachmentsFromInv = RezMultipleAttachmentsFromInv()
        rezMultipleAttachmentsFromInv3.AgentData_Field.AgentID = this.circuitInfo.agentID
        rezMultipleAttachmentsFromInv3.AgentData_Field.SessionID = this.circuitInfo.sessionID
        rezMultipleAttachmentsFromInv3.HeaderData_Field.CompoundMsgID = randomUUID
        rezMultipleAttachmentsFromInv3.HeaderData_Field.TotalObjects = arrayList.size()
        rezMultipleAttachmentsFromInv3.HeaderData_Field.FirstDetachAll = z
        Debug.Printf("Wearing: totalAttachments %d", Integer.valueOf(arrayList.size()))
        val it: Iterator = arrayList.iterator()
        while (true) {
            z4 = z3
            rezMultipleAttachmentsFromInv = rezMultipleAttachmentsFromInv3
            if (!it.hasNext()) {
                break
            }
            val sLInventoryEntry3: SLInventoryEntry = (SLInventoryEntry) it.next()
            if (rezMultipleAttachmentsFromInv == null) {
                val rezMultipleAttachmentsFromInv4: RezMultipleAttachmentsFromInv = RezMultipleAttachmentsFromInv()
                rezMultipleAttachmentsFromInv4.AgentData_Field.AgentID = this.circuitInfo.agentID
                rezMultipleAttachmentsFromInv4.AgentData_Field.SessionID = this.circuitInfo.sessionID
                rezMultipleAttachmentsFromInv4.HeaderData_Field.CompoundMsgID = randomUUID
                rezMultipleAttachmentsFromInv4.HeaderData_Field.TotalObjects = arrayList.size()
                rezMultipleAttachmentsFromInv4.HeaderData_Field.FirstDetachAll = z
                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv4
            } else {
                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv
            }
            RezMultipleAttachmentsFromInv.ObjectData objectData = RezMultipleAttachmentsFromInv.ObjectData()
            val uuid: UUID = sLInventoryEntry3.uuid
            Debug.Printf("Wearing: entry '%s' actualUUID %s", sLInventoryEntry3.name, uuid)
            hashMap.put(uuid, sLInventoryEntry3.name)
            z3 = true
            objectData.ItemID = uuid
            objectData.OwnerID = sLInventoryEntry3.ownerUUID
            objectData.AttachmentPt = 128
            objectData.ItemFlags = sLInventoryEntry3.flags
            objectData.GroupMask = sLInventoryEntry3.groupMask
            objectData.EveryoneMask = sLInventoryEntry3.everyoneMask
            objectData.NextOwnerMask = sLInventoryEntry3.nextOwnerMask
            objectData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry3.name)
            objectData.Description = SLMessage.stringToVariableOEM(sLInventoryEntry3.description)
            rezMultipleAttachmentsFromInv2.ObjectData_Fields.add(objectData)
            if (rezMultipleAttachmentsFromInv2.ObjectData_Fields.size() >= 4) {
                rezMultipleAttachmentsFromInv2.isReliable = true
                SendMessage(rezMultipleAttachmentsFromInv2)
                rezMultipleAttachmentsFromInv3 = null
            } else {
                rezMultipleAttachmentsFromInv3 = rezMultipleAttachmentsFromInv2
            }
        }
        if (rezMultipleAttachmentsFromInv != null) {
            rezMultipleAttachmentsFromInv.isReliable = true
            SendMessage(rezMultipleAttachmentsFromInv)
        }
        val z9: Boolean = false
        val rLVController: RLVController = this.agentCircuit.getModules().rlvController
        val hashSet: HashSet = HashSet()
        val create: HashBasedTable<SLWearableType, UUID, SLWearable> = HashBasedTable.create(this.wornWearables)
        val it2: Iterator<T> = list.iterator()
        while (true) {
            z5 = z9
            if (!it2.hasNext()) {
                break
            }
            val sLInventoryEntry4: SLInventoryEntry = (SLInventoryEntry) it2.next()
            val resolveLink2: SLInventoryEntry = database != null ? database.resolveLink(sLInventoryEntry4) : sLInventoryEntry4
            if (resolveLink2 != null && ((resolveLink2.assetType == SLAssetType.AT_BODYPART.getTypeCode() || resolveLink2.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) && (byCode = SLWearableType.getByCode(resolveLink2.flags & 255)) != null)) {
                if (!rLVController.canWearItem(byCode)) {
                    z7 = false
                } else if (byCode.isBodyPart()) {
                    if (!rLVController.canTakeItemOff(byCode)) {
                        val z10: Boolean = false
                        val it3: Iterator<T> = create.row(byCode).keySet().iterator()
                        while (true) {
                            z8 = z10
                            if (!it3.hasNext()) {
                                break
                            }
                            z10 = !((UUID) it3.next()).equals(resolveLink2.assetUUID) ? true : z8
                        }
                        if (z8) {
                            z7 = false
                        }
                    }
                    z7 = true
                } else {
                    z7 = true
                }
                if (z7) {
                    hashSet.add(resolveLink2.assetUUID)
                    if (!create.contains(byCode, resolveLink2.assetUUID)) {
                        addWearable(create, byCode, resolveLink2.uuid, resolveLink2.assetUUID, resolveLink2.name)
                        z5 = true
                        if (byCode.isBodyPart()) {
                            val hashSet2: HashSet<UUID> = HashSet<>()
                            for (UUID uuid2 : create.row(byCode).keySet()) {
                                if (!uuid2.equals(resolveLink2.assetUUID)) {
                                    hashSet2.add(uuid2)
                                }
                            }
                            for (UUID uuid3 : hashSet2) {
                                if (create.row(byCode).size() > 1 && (remove = create.remove(byCode, uuid3)) != null) {
                                    remove.dispose()
                                }
                            }
                        }
                    }
                }
            }
            z9 = z5
        }
        if (z) {
            z6 = z5
            for (SLWearableType sLWearableType : SLWearableType.values()) {
                if (!sLWearableType.isBodyPart() && rLVController.canTakeItemOff(sLWearableType)) {
                    val row: Map<UUID, SLWearable> = create.row(sLWearableType)
                    val hashSet3: HashSet<UUID> = HashSet<>()
                    for (UUID uuid4 : row.keySet()) {
                        if (!hashSet.contains(uuid4)) {
                            hashSet3.add(uuid4)
                        }
                    }
                    val z11: Boolean = z6
                    for (UUID remove2 : hashSet3) {
                        val remove3: SLWearable = row.remove(remove2)
                        if (remove3 != null) {
                            remove3.dispose()
                        }
                        z11 = true
                    }
                    z6 = z11
                }
            }
        } else {
            z6 = z5
        }
        if (z6) {
            this.wornWearables = ImmutableTable.copyOf(create)
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables)
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
        }
        if (z4) {
            this.wantedAttachments.set(ImmutableMap.copyOf(hashMap))
        }
        if (z6) {
            SendAgentIsNowWearing()
            ForceUpdateAppearance(false)
            z4 = false
        }
        if (z4) {
            this.needUpdateCOF.set(true)
            UpdateCOFContents()
        }
    }

    fun DetachInventoryItem(sLInventoryEntry: SLInventoryEntry) {
        if (canDetachItem(sLInventoryEntry)) {
            val uuid: UUID = sLInventoryEntry.isLink() ? sLInventoryEntry.assetUUID : sLInventoryEntry.uuid
            Debug.Log("Outfits: Detaching inventory item " + uuid)
            val map: Map = this.wantedAttachments.get()
            if (map == null) {
                z = false
            } else if (map.containsKey(uuid)) {
                val hashMap: HashMap = HashMap(map)
                hashMap.remove(uuid)
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap))
                z = true
            } else {
                z = false
            }
            val detachAttachmentIntoInv: DetachAttachmentIntoInv = DetachAttachmentIntoInv()
            detachAttachmentIntoInv.ObjectData_Field.AgentID = this.circuitInfo.agentID
            detachAttachmentIntoInv.ObjectData_Field.ItemID = uuid
            detachAttachmentIntoInv.isReliable = true
            SendMessage(detachAttachmentIntoInv)
            if (z) {
                this.needUpdateCOF.set(true)
                UpdateCOFContents()
            }
        }
    }

    fun DetachItem(wornItem: WornItem) {
        if (canDetachItem(wornItem)) {
            DetachItem(wornItem.objectLocalID)
        }
    }

    fun DetachItemFromPoint(i: Int) {
        SLObjectAvatarInfo agentAvatar
        val hashSet: HashSet<Integer> = null
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead) && next.attachmentID == i && this.agentCircuit.getModules().rlvController.canDetachItem(i, next.getId())) {
                        if (hashSet == null) {
                            hashSet = HashSet<>()
                        }
                        hashSet.add(Integer.valueOf(next.localID))
                    }
                    hashSet = hashSet
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e)
            }
        }
        if (hashSet != null) {
            for (Integer intValue : hashSet) {
                DetachItem(intValue.intValue())
            }
        }
    }

    fun ForceTakeItemOff(sLWearableType: SLWearableType) {
        if (!this.wornWearables.row(sLWearableType).isEmpty()) {
            z = true
            val create: HashBasedTable<SLWearableType, UUID, SLWearable> = HashBasedTable.create(this.wornWearables)
            create.rowKeySet().remove(sLWearableType)
            this.wornWearables = ImmutableTable.copyOf(create)
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables)
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
        } else {
            z = false
        }
        if (z) {
            SendAgentIsNowWearing()
            ForceUpdateAppearance(false)
        }
    }

    @SLMessageHandler
    fun HandleAgentWearablesUpdate(agentWearablesUpdate: AgentWearablesUpdate) {
        Debug.Log("AvatarAppearance: Got AgentWearablesUpdate, " + agentWearablesUpdate.WearableData_Fields.size() + " wearables.")
        val hashSet: HashSet = HashSet()
        val create: HashBasedTable<SLWearableType, UUID, SLWearable> = HashBasedTable.create(this.wornWearables)
        for (AgentWearablesUpdate.WearableData wearableData : agentWearablesUpdate.WearableData_Fields) {
            Debug.Log("Wearable: type = " + wearableData.WearableType + ", itemID = " + wearableData.ItemID + ", assetID = " + wearableData.AssetID)
            if (wearableData.AssetID.getLeastSignificantBits() != 0 || wearableData.AssetID.getMostSignificantBits() != 0) {
                hashSet.add(wearableData.AssetID)
                val byCode: SLWearableType = SLWearableType.getByCode(wearableData.WearableType)
                if (byCode != null && create.get(byCode, wearableData.AssetID) == null) {
                    addWearable(create, byCode, wearableData.ItemID, wearableData.AssetID, (String) null)
                }
            }
        }
        Debug.Log("AvatarAppearance: AgentWearablesUpdate: wearing now: " + hashSet.size() + " ids")
        val hashSet2: HashSet<UUID> = HashSet<>()
        for (UUID uuid : create.columnKeySet()) {
            if (!hashSet.contains(uuid)) {
                hashSet2.add(uuid)
            }
        }
        for (UUID column : hashSet2) {
            val column2: Map<SLWearableType, SLWearable> = create.column(column)
            for (SLWearable dispose : column2.values()) {
                dispose.dispose()
            }
            column2.clear()
        }
        this.wornWearables = ImmutableTable.copyOf(create)
        this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables)
        this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
        UpdateWearableNames()
        this.legacyAppearanceReady = true
        ProcessMultiLayer()
        SendAgentIsNowWearing()
        StartUpdatingAppearance()
    }

    fun HandleAvatarAppearance(avatarAppearance: AvatarAppearance) {
        if (avatarAppearance.AppearanceData_Fields.size() > 0) {
            this.currentCofAppearanceVersion = avatarAppearance.AppearanceData_Fields.get(0).CofVersion
            Debug.Printf("AvatarAppearance: inventory COF %d, last updated COF %d, appearance COF %d", Integer.valueOf(this.currentCofInventoryVersion), Integer.valueOf(this.lastCofUpdatedVersion), Integer.valueOf(this.currentCofAppearanceVersion))
        }
    }

    fun HandleCircuitReady() {
        SLInventoryEntry findSpecialFolder
        val z: Boolean = true
        super.HandleCircuitReady()
        if (this.userManager != null) {
            val rootFolder: UUID = this.userManager.getInventoryManager().getRootFolder()
            if (rootFolder == null || (findSpecialFolder = this.userManager.getInventoryManager().getDatabase().findSpecialFolder(rootFolder, 46)) == null) {
                z = false
            } else {
                Debug.Printf("Found existing COF folder: %s", findSpecialFolder.uuid)
                this.cofFolderUUID.set(findSpecialFolder.uuid)
                this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(findSpecialFolder.uuid, (String) null, true, true, false, (SLAssetType) null))
            }
            if (!z) {
                Debug.Printf("Existing COF folder not found, requesting.", Object[0])
                this.findCofFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.findFolderWithType((UUID) null, 46))
            }
        }
    }

    fun HandleCloseCircuit() {
        this.findCofFolder.unsubscribe()
        this.currentOutfitFolder.unsubscribe()
        if (this.userManager != null) {
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, null)
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, null)
            this.userManager.wornItems().detachRequestHandler(this.wornItemsRequestHandler)
        }
        if (this.bakingThread != null) {
            this.bakingThread.interrupt()
            this.bakingThread = null
        }
        if (this.serverSideAppearanceUpdateTask != null) {
            this.serverSideAppearanceUpdateTask.cancel(true)
        }
        super.HandleCloseCircuit()
    }

    fun OnMyAvatarCreated(sLObjectAvatarInfo: SLObjectAvatarInfo) {
        if (this.agentVisualParams != null) {
            sLObjectAvatarInfo.ApplyAvatarVisualParams(this.agentVisualParams)
        }
    }

    fun SendAgentWearablesRequest() {
        val agentWearablesRequest: AgentWearablesRequest = AgentWearablesRequest()
        agentWearablesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID
        agentWearablesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID
        agentWearablesRequest.isReliable = true
        SendMessage(agentWearablesRequest)
    }

    fun TakeItemOff(sLInventoryEntry: SLInventoryEntry) {
        val inventoryDB: InventoryDB = null
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase()
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry)
        }
        if (sLInventoryEntry != null) {
            TakeItemOff(sLInventoryEntry.assetUUID)
        }
    }

    fun TakeItemOff(uuid: UUID) {
        val rLVController: RLVController = this.agentCircuit.getModules().rlvController
        val create: HashBasedTable<SLWearableType, UUID, SLWearable> = HashBasedTable.create(this.wornWearables)
        val values: Array<SLWearableType> = SLWearableType.values()
        val length: Int = values.length
        val i: Int = 0
        val z2: Boolean = false
        while (i < length) {
            val sLWearableType: SLWearableType = values[i]
            if (!rLVController.canTakeItemOff(sLWearableType)) {
                z = z2
            } else {
                val remove: SLWearable = create.remove(sLWearableType, uuid)
                if (remove != null) {
                    remove.dispose()
                    create.columnKeySet().remove(uuid)
                    z = true
                } else {
                    z = z2
                }
            }
            i++
            z2 = z
        }
        if (z2) {
            this.wornWearables = ImmutableTable.copyOf(create)
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables)
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
            SendAgentIsNowWearing()
            ForceUpdateAppearance(false)
        }
    }

    fun UpdateMyAttachments() {
        SLObjectAvatarInfo agentAvatar
        val hashMap: HashMap = HashMap()
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead)) {
                        hashMap.put(next.attachedToUUID, Strings.nullToEmpty(next.getName()))
                    }
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace()
            }
        }
        val copyOf: ImmutableMap<UUID, String> = ImmutableMap.copyOf(hashMap)
        if (!this.wornAttachments.equals(copyOf)) {
            Debug.Log("AvatarAppearance: attachments changed.")
            this.wornAttachments = copyOf
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, this.wornAttachments)
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value)
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value)
        }
    }

    fun WearItem(sLInventoryEntry: SLInventoryEntry, z: Boolean) {
        val inventoryDB: InventoryDB = null
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase()
        }
        if (inventoryDB != null) {
            WearItemList(inventoryDB, ImmutableList.of(sLInventoryEntry), z)
            SendAgentIsNowWearing()
            ForceUpdateAppearance(false)
        }
    }

     public fun canDetachItem(sLInventoryEntry: SLInventoryEntry): Boolean {
        if (sLInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                return true
            }
            if (sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && this.wornAttachments.containsKey(sLInventoryEntry.assetUUID)) {
                return canDetachItem(sLInventoryEntry.assetUUID)
            }
        } else if (sLInventoryEntry.assetType == SLAssetType.AT_BODYPART.getTypeCode() || sLInventoryEntry.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            return true
        } else {
            if (sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode()) {
                if (!this.wornAttachments.containsKey(sLInventoryEntry.uuid) || canDetachItem(sLInventoryEntry.uuid)) {
                    return !this.wornAttachments.containsKey(sLInventoryEntry.assetUUID) || canDetachItem(sLInventoryEntry.assetUUID)
                }
                return false
            }
        }
        return false
    }

     public fun canDetachItem(wornItem: WornItem): Boolean {
        return this.agentCircuit.getModules().rlvController.canDetachItem(wornItem.getAttachedTo(), wornItem.itemID())
    }

     public fun canTakeItemOff(sLWearableType: SLWearableType): Boolean {
        return this.agentCircuit.getModules().rlvController.canTakeItemOff(sLWearableType)
    }

     public fun canTakeItemOff(sLInventoryEntry: SLInventoryEntry): Boolean {
        val whatIsItemWornOn: Object = sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false)
        if (whatIsItemWornOn == null) {
            return true
        }
        val rLVController: RLVController = this.agentCircuit.getModules().rlvController
        if (whatIsItemWornOn instanceof SLWearableType) {
            return rLVController.canTakeItemOff((SLWearableType) whatIsItemWornOn)
        }
        return true
    }

     public fun canWearItem(sLInventoryEntry: SLInventoryEntry): Boolean {
        val inventoryDB: InventoryDB = null
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase()
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry)
        }
        if (sLInventoryEntry == null) {
            return false
        }
        val byCode: SLWearableType = SLWearableType.getByCode(sLInventoryEntry.flags & 255)
        return byCode == null || canWearItem(byCode)
    }

    fun finishBaking(bakeProcess2: BakeProcess, sLTextureEntry: SLTextureEntry) {
        if (sLTextureEntry != null) {
            this.agentBakedTextures = sLTextureEntry
            SendAvatarSetAppearance()
        }
        if (this.bakeProcess == bakeProcess2) {
            this.bakeProcess = null
        }
    }

     public fun getAttachmentUUID(i: Int): UUID {
        SLObjectAvatarInfo agentAvatar
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead) && next.attachmentID == i) {
                        return next.getId()
                    }
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e)
            }
        }
        return null
    }

     public fun hasWornWearable(sLWearableType: SLWearableType): Boolean {
        return this.wornWearables.containsRow(sLWearableType)
    }

     public fun isItemWorn(sLInventoryEntry: SLInventoryEntry): Boolean {
        return isItemWorn(sLInventoryEntry, false)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_17963  reason: not valid java name */
    public /* synthetic */ Unit m202lambda$com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_17963(Int i, String str) {
        val lLSDXMLRequest: LLSDXMLRequest = LLSDXMLRequest()
        val lLSDMap: LLSDMap = LLSDMap(LLSDMap.LLSDMapEntry("cof_version", LLSDInt(i)))
        val i2: Int = 3
        while (i2 > 0) {
            try {
                val PerformRequest: LLSDNode = lLSDXMLRequest.PerformRequest(str, lLSDMap)
                if (PerformRequest != null && PerformRequest.keyExists("error")) {
                    val byKey: LLSDNode = PerformRequest.byKey("error")
                    if (byKey.isString()) {
                        Debug.Printf("AvatarAppearance: server-side error: %s", byKey.asString())
                    } else {
                        Debug.Printf("AvatarAppearance: server-side update ok.", Object[0])
                    }
                }
                this.lastCofUpdateError = false
                return
            } catch (Exception e) {
                Debug.Printf("AvatarAppearance: server-side update error: [exception %s]", e.toString())
                this.lastCofUpdateError = true
                val i3: Int = i2 - 1
                try {
                    Thread.sleep(1000)
                    i2 = i3
                } catch (InterruptedException e2) {
                    return
                }
            }
        }
    }

    fun onWearableStatusChanged(sLWearable: SLWearable) {
        updateIfWearablesReady()
    }
}
