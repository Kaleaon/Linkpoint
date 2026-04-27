package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.baker.BakeProcess;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing;
import com.lumiyaviewer.lumiya.slproto.messages.AgentSetAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.DetachAttachmentIntoInv;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDetach;
import com.lumiyaviewer.lumiya.slproto.messages.RezMultipleAttachmentsFromInv;
import com.lumiyaviewer.lumiya.slproto.messages.RezSingleAttachmentFromInv;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class SLAvatarAppearance extends SLModule implements SLWearable.OnWearableStatusChangeListener {
    private static final int Param_agentSizeVPHeadSize = 682;
    private static final int Param_agentSizeVPHeelHeight = 198;
    private static final int Param_agentSizeVPHeight = 33;
    private static final int Param_agentSizeVPHipLength = 842;
    private static final int Param_agentSizeVPLegLength = 692;
    private static final int Param_agentSizeVPNeckLength = 756;
    private static final int Param_agentSizeVPPlatformHeight = 503;
    private SLTextureEntry agentBakedTextures;
    private boolean agentSizeKnown = false;
    private float agentSizeVPHeadSize;
    private float agentSizeVPHeelHeight;
    private float agentSizeVPHeight;
    private float agentSizeVPHipLength;
    private float agentSizeVPLegLength;
    private float agentSizeVPNeckLength;
    private float agentSizeVPPlatformHeight;
    private int[] agentVisualParams;
    private volatile BakeProcess bakeProcess = null;
    private Thread bakingThread = null;
    private final SLCaps caps;
    private final AtomicReference<UUID> cofFolderUUID = new AtomicReference<>();
    private volatile boolean cofReady = false;
    private volatile int currentCofAppearanceVersion = 0;
    private volatile int currentCofInventoryVersion = 0;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> currentOutfitFolder;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> findCofFolder;
    private final SLInventory inventory;
    private volatile boolean lastCofUpdateError = false;
    private volatile int lastCofUpdatedVersion = 0;
    private volatile boolean legacyAppearanceReady = false;
    private volatile boolean multiLayerDone = false;
    private volatile boolean needUpdateAppearance = false;
    private final AtomicBoolean needUpdateCOF = new AtomicBoolean(false);
    private final SLParcelInfo parcelInfo;
    private Future<?> serverSideAppearanceUpdateTask = null;
    private int setAppearanceSerialNum = 1;
    private final UserManager userManager;
    private final AtomicReference<Map<UUID, String>> wantedAttachments = new AtomicReference<>(ImmutableMap.of());
    private SLInventoryEntry wantedOutfitFolder = null;
    @Nonnull
    private volatile ImmutableMap<UUID, String> wornAttachments = ImmutableMap.of();
    private final RequestHandler<SubscriptionSingleKey> wornItemsRequestHandler = new AsyncRequestHandler(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
        public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
            if (SLAvatarAppearance.this.wornItemsResultHandler != null) {
                SLAvatarAppearance.this.wornItemsResultHandler.onResultData(subscriptionSingleKey, SLAvatarAppearance.this.getWornItems());
            }
        }
    });
    /* access modifiers changed from: private */
    public final ResultHandler<SubscriptionSingleKey, ImmutableList<WornItem>> wornItemsResultHandler;
    @Nonnull
    private volatile Table<SLWearableType, UUID, SLWearable> wornWearables = ImmutableTable.of();

    public static class WornItem {
        private final int attachedTo;
        private final boolean isTouchable;
        private final UUID itemID;
        private final String name;
        /* access modifiers changed from: private */
        public final int objectLocalID;
        private final SLWearableType wornOn;

        WornItem(SLWearableType sLWearableType, int i, UUID uuid, String str, int i2, boolean z) {
            this.wornOn = sLWearableType;
            this.attachedTo = i;
            this.itemID = uuid;
            this.name = str;
            this.objectLocalID = i2;
            this.isTouchable = z;
        }

        /* access modifiers changed from: package-private */
        public int getAttachedTo() {
            return this.attachedTo;
        }

        public boolean getIsTouchable() {
            return this.isTouchable;
        }

        public String getName() {
            return this.name;
        }

        public int getObjectLocalID() {
            return this.objectLocalID;
        }

        public SLWearableType getWornOn() {
            return this.wornOn;
        }

        public UUID itemID() {
            return this.itemID;
        }
    }

    public SLAvatarAppearance(SLAgentCircuit sLAgentCircuit, SLInventory sLInventory, SLCaps sLCaps) {
        super(sLAgentCircuit);
        this.caps = sLCaps;
        this.inventory = sLInventory;
        this.parcelInfo = sLAgentCircuit.getGridConnection().parcelInfo;
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        this.currentOutfitFolder = new SubscriptionData<>(sLAgentCircuit, new $Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(this));
        this.findCofFolder = new SubscriptionData<>(sLAgentCircuit, new Subscription.OnData(this) {

            /* renamed from: -$f0 */
            private final /* synthetic */ Object f118$f0;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.$m$0(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.$m$0(java.lang.Object):void, class status: UNLOADED
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

            public final void onData(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.onData(java.lang.Object):void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.1.onData(java.lang.Object):void, class status: UNLOADED
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
        });
        if (this.userManager != null) {
            this.wornItemsResultHandler = this.userManager.wornItems().attachRequestHandler(this.wornItemsRequestHandler);
        } else {
            this.wornItemsResultHandler = null;
        }
    }

    private void DetachItem(int i) {
        SLObjectAvatarInfo agentAvatar;
        boolean z;
        Debug.Log("Outfits: detaching item " + i);
        boolean z2 = false;
        Map map = this.wantedAttachments.get();
        if (map != null) {
            HashMap hashMap = new HashMap(map);
            if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
                try {
                    Iterator<SLObjectInfo> it = agentAvatar.treeNode.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            z = false;
                            break;
                        }
                        SLObjectInfo next = it.next();
                        if (next.attachedToUUID != null && (!next.isDead) && next.localID == i) {
                            if (hashMap.remove(next.getId()) != null) {
                                z = true;
                                break;
                            } else if (hashMap.remove(next.attachedToUUID) != null) {
                                z = true;
                                break;
                            }
                        }
                    }
                    z2 = z;
                } catch (NoSuchElementException e) {
                    Debug.Warning(e);
                }
            }
            if (z2) {
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap));
            }
        }
        ObjectDetach objectDetach = new ObjectDetach();
        objectDetach.AgentData_Field.AgentID = this.circuitInfo.agentID;
        objectDetach.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        ObjectDetach.ObjectData objectData = new ObjectDetach.ObjectData();
        objectData.ObjectLocalID = i;
        objectDetach.ObjectData_Fields.add(objectData);
        objectDetach.isReliable = true;
        SendMessage(objectDetach);
        if (z2) {
            this.needUpdateCOF.set(true);
            UpdateCOFContents();
        }
    }

    private void ForceUpdateAppearance(boolean z) {
        this.needUpdateAppearance = true;
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            this.eventBus.publish(new SLBakingProgressEvent(true, false, 0));
        } else if (z) {
            this.lastCofUpdatedVersion = 0;
            this.currentCofAppearanceVersion = 0;
            RequestServerRebake();
        }
        StartUpdatingAppearance();
    }

    private void ProcessMultiLayer() {
        if (!this.multiLayerDone && this.cofReady && this.legacyAppearanceReady) {
            UpdateMultiLayer();
        }
    }

    private void RequestServerRebake() {
        SLInventoryEntry folder;
        String capability = this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance);
        InventoryEntryList data = this.currentOutfitFolder.getData();
        if (capability != null && data != null && (folder = data.getFolder()) != null) {
            this.currentCofInventoryVersion = folder.version;
            if ((this.currentCofInventoryVersion != this.lastCofUpdatedVersion && this.currentCofInventoryVersion != this.currentCofAppearanceVersion) || this.lastCofUpdateError) {
                this.lastCofUpdatedVersion = this.currentCofInventoryVersion;
                this.lastCofUpdateError = false;
                UpdateServerSideAppearance(capability, folder.version);
            }
        }
    }

    private void SendAgentIsNowWearing() {
        boolean z;
        AgentIsNowWearing agentIsNowWearing = new AgentIsNowWearing();
        agentIsNowWearing.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentIsNowWearing.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (SLWearableType sLWearableType : SLWearableType.values()) {
            Map<UUID, SLWearable> row = this.wornWearables.row(sLWearableType);
            if (row != null) {
                z = true;
                for (SLWearable sLWearable : row.values()) {
                    AgentIsNowWearing.WearableData wearableData = new AgentIsNowWearing.WearableData();
                    wearableData.ItemID = sLWearable.itemID;
                    wearableData.WearableType = sLWearableType.getTypeCode();
                    agentIsNowWearing.WearableData_Fields.add(wearableData);
                    z = false;
                }
            } else {
                z = true;
            }
            if (z) {
                AgentIsNowWearing.WearableData wearableData2 = new AgentIsNowWearing.WearableData();
                wearableData2.ItemID = new UUID(0, 0);
                wearableData2.WearableType = sLWearableType.getTypeCode();
                agentIsNowWearing.WearableData_Fields.add(wearableData2);
            }
        }
        Debug.Log("AvatarAppearance: Sending AgentIsNowWearing, " + agentIsNowWearing.WearableData_Fields.size() + " wearables.");
        agentIsNowWearing.isReliable = true;
        SendMessage(agentIsNowWearing);
        this.needUpdateCOF.set(true);
        UpdateCOFContents();
    }

    private void SendAvatarSetAppearance() {
        SLObjectAvatarInfo sLObjectAvatarInfo = null;
        UpdateCOFContents();
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            if (this.parcelInfo != null) {
                sLObjectAvatarInfo = this.parcelInfo.getAgentAvatar();
            }
            if (!(this.agentBakedTextures == null || sLObjectAvatarInfo == null)) {
                sLObjectAvatarInfo.ApplyAvatarTextures(this.agentBakedTextures, true);
            }
            AgentSetAppearance agentSetAppearance = new AgentSetAppearance();
            agentSetAppearance.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentSetAppearance.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            agentSetAppearance.AgentData_Field.SerialNum = this.setAppearanceSerialNum;
            agentSetAppearance.AgentData_Field.Size = new LLVector3();
            agentSetAppearance.AgentData_Field.Size.x = 1.0f;
            agentSetAppearance.AgentData_Field.Size.y = 1.0f;
            agentSetAppearance.AgentData_Field.Size.z = 1.0f;
            if (this.agentBakedTextures != null) {
                agentSetAppearance.ObjectData_Field.TextureEntry = this.agentBakedTextures.packByteArray();
            } else {
                agentSetAppearance.ObjectData_Field.TextureEntry = new byte[0];
            }
            this.agentVisualParams = getAppearanceParams();
            if (sLObjectAvatarInfo != null) {
                sLObjectAvatarInfo.ApplyAvatarVisualParams(this.agentVisualParams);
            }
            for (int i : this.agentVisualParams) {
                AgentSetAppearance.VisualParam visualParam = new AgentSetAppearance.VisualParam();
                visualParam.ParamValue = i;
                agentSetAppearance.VisualParam_Fields.add(visualParam);
            }
            if (this.agentSizeKnown && areWearablesReady()) {
                agentSetAppearance.AgentData_Field.Size.x = 0.45f;
                agentSetAppearance.AgentData_Field.Size.y = 0.6f;
                agentSetAppearance.AgentData_Field.Size.z = getAgentHeight();
                Debug.Log("set agent height to " + agentSetAppearance.AgentData_Field.Size.z);
            }
            agentSetAppearance.isReliable = true;
            Debug.Log("AvatarAppearance: Sending agentSetAppearance: " + agentSetAppearance.VisualParam_Fields.size() + " params, hasTextures = " + (this.agentBakedTextures != null ? "yes" : "no"));
            SendMessage(agentSetAppearance);
            this.setAppearanceSerialNum++;
        }
    }

    private void StartUpdatingAppearance() {
        updateIfWearablesReady();
    }

    private void UpdateCOFContents() {
        InventoryEntryList<SLInventoryEntry> data;
        SLInventoryEntry folder;
        boolean z;
        boolean z2;
        boolean areWearablesReady = areWearablesReady();
        Debug.Printf("Wearables ready %b, cofReady %b", Boolean.valueOf(areWearablesReady), Boolean.valueOf(this.cofReady));
        if ((areWearablesReady ? this.cofReady : false) && (data = this.currentOutfitFolder.getData()) != null && (folder = data.getFolder()) != null && this.needUpdateCOF.getAndSet(false)) {
            this.currentCofInventoryVersion = folder.version;
            LinkedList linkedList = new LinkedList();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            HashSet hashSet = new HashSet();
            for (SLWearable sLWearable : this.wornWearables.values()) {
                if (!sLWearable.getIsFailed()) {
                    hashSet.add(sLWearable.itemID);
                    hashMap.put(sLWearable.itemID, sLWearable);
                }
            }
            Map map = this.wantedAttachments.get();
            if (map != null) {
                hashMap2.putAll(map);
            }
            boolean z3 = true;
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (sLInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
                    if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                        if (!hashSet.contains(sLInventoryEntry.assetUUID)) {
                            linkedList.add(sLInventoryEntry.uuid);
                        }
                    } else if (sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && map != null && !map.containsKey(sLInventoryEntry.assetUUID)) {
                        Debug.Printf("Attached entry %s (%s) not found in wanted attachments", sLInventoryEntry.assetUUID, sLInventoryEntry.name);
                        linkedList.add(sLInventoryEntry.uuid);
                    }
                    hashMap.remove(sLInventoryEntry.assetUUID);
                    hashMap2.remove(sLInventoryEntry.assetUUID);
                    z2 = z3;
                } else if (sLInventoryEntry.assetType != SLAssetType.AT_LINK_FOLDER.getTypeCode() || this.wantedOutfitFolder == null) {
                    z2 = z3;
                } else if (!this.wantedOutfitFolder.uuid.equals(sLInventoryEntry.assetUUID)) {
                    linkedList.add(sLInventoryEntry.uuid);
                    z2 = z3;
                } else {
                    z2 = false;
                }
                z3 = z2;
            }
            Debug.Printf("Update COF: addWearablesList %d, killList %d", Integer.valueOf(hashMap.size()), Integer.valueOf(linkedList.size()));
            if (!linkedList.isEmpty()) {
                this.inventory.DeleteMultiInventoryItemRaw(folder, linkedList);
                z = true;
            } else {
                z = false;
            }
            for (SLWearable sLWearable2 : hashMap.values()) {
                Debug.Printf("Update COF: adding %s, name = '%s'", sLWearable2.itemID, sLWearable2.getName());
                this.inventory.LinkInventoryItem(folder, sLWearable2.itemID, SLInventoryType.IT_WEARABLE.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), sLWearable2.getName(), "");
                z = true;
            }
            for (Map.Entry entry : hashMap2.entrySet()) {
                Debug.Printf("Update COF: adding attachment %s, name = '%s'", entry.getKey(), entry.getValue());
                this.inventory.LinkInventoryItem(folder, (UUID) entry.getKey(), SLInventoryType.IT_OBJECT.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), (String) entry.getValue(), "");
                z = true;
            }
            if (z3 && this.wantedOutfitFolder != null) {
                Debug.Printf("Update COF: adding outfit link for outfit folder %s", this.wantedOutfitFolder.uuid);
                this.inventory.LinkInventoryItem(folder, this.wantedOutfitFolder.uuid, SLInventoryType.IT_CATEGORY.getTypeCode(), SLAssetType.AT_LINK_FOLDER.getTypeCode(), this.wantedOutfitFolder.name, "");
                z = true;
            }
            Debug.Printf("Update COF: COF updated (had changes: %b).", Boolean.valueOf(z));
            if (z && this.userManager != null) {
                this.userManager.getInventoryManager().requestFolderUpdate(folder.uuid);
            }
            RequestServerRebake();
        }
    }

    private void UpdateCurrentOutfitLink(@Nonnull InventoryEntryList inventoryEntryList) {
        Iterator it = inventoryEntryList.iterator();
        while (it.hasNext()) {
            SLInventoryEntry sLInventoryEntry = (SLInventoryEntry) it.next();
            if (sLInventoryEntry.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
                this.userManager.wornOutfitLink().setData(SubscriptionSingleKey.Value, sLInventoryEntry.assetUUID);
                return;
            }
        }
    }

    private synchronized void UpdateMultiLayer() {
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv;
        Debug.Printf("AvatarAppearance: MultiLayer: Updating multi layer appearance.", new Object[0]);
        InventoryEntryList<SLInventoryEntry> data = this.currentOutfitFolder.getData();
        InventoryDB database = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null;
        if (!(data == null || database == null)) {
            LinkedList linkedList = new LinkedList();
            LinkedList<SLInventoryEntry> linkedList2 = new LinkedList<>();
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                    linkedList.add(sLInventoryEntry);
                } else if (sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (sLInventoryEntry.isLink() && sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode())) {
                    linkedList2.add(sLInventoryEntry);
                }
            }
            if (WearItemList(database, linkedList, false)) {
                Debug.Printf("AvatarAppearance: MultiLayer: had some extra layers.", new Object[0]);
                SendAgentIsNowWearing();
                StartUpdatingAppearance();
            } else {
                Debug.Printf("AvatarAppearance: MultiLayer: no extra layers.", new Object[0]);
            }
            if (linkedList2.size() != 0) {
                Debug.Printf("AvatarAppearance: Re-attaching %d attachments from COF.", Integer.valueOf(linkedList2.size()));
                HashMap hashMap = new HashMap();
                UUID randomUUID = UUID.randomUUID();
                RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv2 = null;
                for (SLInventoryEntry sLInventoryEntry2 : linkedList2) {
                    SLInventoryEntry resolveLink = database.resolveLink(sLInventoryEntry2);
                    if (resolveLink != null) {
                        if (rezMultipleAttachmentsFromInv2 == null) {
                            rezMultipleAttachmentsFromInv2 = new RezMultipleAttachmentsFromInv();
                            rezMultipleAttachmentsFromInv2.AgentData_Field.AgentID = this.circuitInfo.agentID;
                            rezMultipleAttachmentsFromInv2.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.CompoundMsgID = randomUUID;
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.TotalObjects = linkedList2.size();
                            rezMultipleAttachmentsFromInv2.HeaderData_Field.FirstDetachAll = false;
                        }
                        RezMultipleAttachmentsFromInv.ObjectData objectData = new RezMultipleAttachmentsFromInv.ObjectData();
                        Debug.Printf("Re-attaching attachment: entry %s (%s)", resolveLink.uuid, sLInventoryEntry2.name);
                        hashMap.put(resolveLink.uuid, sLInventoryEntry2.name);
                        objectData.ItemID = resolveLink.uuid;
                        objectData.OwnerID = resolveLink.ownerUUID;
                        objectData.AttachmentPt = 128;
                        objectData.ItemFlags = resolveLink.flags;
                        objectData.GroupMask = resolveLink.groupMask;
                        objectData.EveryoneMask = resolveLink.everyoneMask;
                        objectData.NextOwnerMask = resolveLink.nextOwnerMask;
                        objectData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry2.name);
                        objectData.Description = SLMessage.stringToVariableOEM(sLInventoryEntry2.description);
                        rezMultipleAttachmentsFromInv2.ObjectData_Fields.add(objectData);
                        if (rezMultipleAttachmentsFromInv2.ObjectData_Fields.size() >= 4) {
                            rezMultipleAttachmentsFromInv2.isReliable = true;
                            SendMessage(rezMultipleAttachmentsFromInv2);
                            rezMultipleAttachmentsFromInv = null;
                            rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv;
                        }
                    }
                    rezMultipleAttachmentsFromInv = rezMultipleAttachmentsFromInv2;
                    rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv;
                }
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap));
                if (rezMultipleAttachmentsFromInv2 != null) {
                    rezMultipleAttachmentsFromInv2.isReliable = true;
                    SendMessage(rezMultipleAttachmentsFromInv2);
                }
            } else {
                Debug.Printf("AvatarAppearance: No attachments in COF.", new Object[0]);
            }
        }
        this.multiLayerDone = true;
    }

    private synchronized void UpdateServerSideAppearance(String str, int i) {
        Debug.Printf("AvatarAppearance: capURL '%s', cofVersion %d", str, Integer.valueOf(i));
        if (this.serverSideAppearanceUpdateTask != null) {
            this.serverSideAppearanceUpdateTask.cancel(true);
        }
        this.serverSideAppearanceUpdateTask = GenericHTTPExecutor.getInstance().submit(new Runnable(i, this, str) {

            /* renamed from: -$f0 */
            private final /* synthetic */ int f119$f0;

            /* renamed from: -$f1 */
            private final /* synthetic */ Object f120$f1;

            /* renamed from: -$f2 */
            private final /* synthetic */ Object f121$f2;

            private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.$m$0():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.$m$0():void, class status: UNLOADED
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

            public final void run(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.run():void, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.-$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo.2.run():void, class status: UNLOADED
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
        });
    }

    private void UpdateWearableNames() {
        SLInventoryEntry resolveLink;
        SLWearableType byCode;
        SLWearable sLWearable;
        InventoryDB inventoryDB = null;
        InventoryEntryList<SLInventoryEntry> data = this.currentOutfitFolder.getData();
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase();
        }
        if (data != null && inventoryDB != null) {
            for (SLInventoryEntry sLInventoryEntry : data) {
                if (!(sLInventoryEntry.isFolderOrFolderLink() || (resolveLink = inventoryDB.resolveLink(sLInventoryEntry)) == null || resolveLink.invType != SLInventoryType.IT_WEARABLE.getTypeCode() || (byCode = SLWearableType.getByCode(resolveLink.flags & 255)) == null || (sLWearable = this.wornWearables.get(byCode, resolveLink.assetUUID)) == null)) {
                    sLWearable.setInventoryName(resolveLink.name);
                }
            }
        }
    }

    private boolean WearItemList(InventoryDB inventoryDB, List<SLInventoryEntry> list, boolean z) {
        boolean z2;
        SLWearableType byCode;
        boolean z3;
        boolean z4;
        boolean z5 = false;
        RLVController rLVController = this.agentCircuit.getModules().rlvController;
        HashBasedTable<SLWearableType, UUID, SLWearable> create = HashBasedTable.create(this.wornWearables);
        Iterator<T> it = list.iterator();
        while (true) {
            z2 = z5;
            if (!it.hasNext()) {
                break;
            }
            SLInventoryEntry resolveLink = inventoryDB.resolveLink((SLInventoryEntry) it.next());
            if (!(resolveLink == null || (byCode = SLWearableType.getByCode(resolveLink.flags & 255)) == null)) {
                boolean isBodyPart = !z ? byCode.isBodyPart() : true;
                if (!rLVController.canWearItem(byCode)) {
                    z3 = false;
                } else if (isBodyPart) {
                    if (!rLVController.canTakeItemOff(byCode)) {
                        boolean z6 = false;
                        Iterator<T> it2 = create.row(byCode).keySet().iterator();
                        while (true) {
                            z4 = z6;
                            if (!it2.hasNext()) {
                                break;
                            }
                            z6 = !((UUID) it2.next()).equals(resolveLink.assetUUID) ? true : z4;
                        }
                        if (z4) {
                            z3 = false;
                        }
                    }
                    z3 = true;
                } else {
                    z3 = true;
                }
                if (z3 && !create.contains(byCode, resolveLink.assetUUID)) {
                    if (isBodyPart) {
                        HashSet<UUID> hashSet = new HashSet<>(create.row(byCode).keySet());
                        hashSet.remove(resolveLink.assetUUID);
                        for (UUID remove : hashSet) {
                            SLWearable remove2 = create.remove(byCode, remove);
                            if (remove2 != null) {
                                remove2.dispose();
                            }
                        }
                    }
                    addWearable(create, byCode, resolveLink.uuid, resolveLink.assetUUID, resolveLink.name);
                    z2 = true;
                }
            }
            z5 = z2;
        }
        if (z2) {
            this.wornWearables = ImmutableTable.copyOf(create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        return z2;
    }

    private SLWearable addWearable(Table<SLWearableType, UUID, SLWearable> table, SLWearableType sLWearableType, UUID uuid, UUID uuid2, String str) {
        SLWearable sLWearable = new SLWearable(this.userManager, this.agentCircuit, uuid, uuid2, sLWearableType, this);
        if (str != null) {
            sLWearable.setInventoryName(str);
        }
        table.put(sLWearableType, uuid2, sLWearable);
        return sLWearable;
    }

    private boolean areWearablesReady() {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        SLWearableType[] values = SLWearableType.values();
        int length = values.length;
        int i = 0;
        boolean z6 = false;
        boolean z7 = false;
        while (i < length) {
            SLWearableType sLWearableType = values[i];
            boolean isCritical = sLWearableType.getIsCritical();
            Map<UUID, SLWearable> row = this.wornWearables.row(sLWearableType);
            if (row != null) {
                z2 = false;
                z = z7;
                for (SLWearable sLWearable : row.values()) {
                    if (sLWearable.getIsValid()) {
                        z4 = true;
                        z5 = z;
                    } else if (!sLWearable.getIsFailed()) {
                        z4 = z2;
                        z5 = true;
                    } else {
                        z4 = z2;
                        z5 = z;
                    }
                    z = z5;
                    z2 = z4;
                }
            } else {
                z2 = false;
                z = z7;
            }
            if (!isCritical) {
                z3 = z6;
            } else if (!z2) {
                Object[] objArr = new Object[2];
                objArr[0] = sLWearableType;
                objArr[1] = Integer.valueOf(row != null ? row.size() : 0);
                Debug.Printf("missing wearables on critical layer %s (worn: %d entries)", objArr);
                z3 = true;
            } else {
                z3 = z6;
            }
            i++;
            z6 = z3;
            z7 = z;
        }
        Debug.Printf("hasNotDownloaded %b, hasCriticalMissing %b", Boolean.valueOf(z7), Boolean.valueOf(z6));
        if (!z7) {
            return !z6;
        }
        return false;
    }

    private boolean canDetachItem(UUID uuid) {
        SLObjectAvatarInfo agentAvatar;
        if (this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null) {
            return true;
        }
        try {
            for (SLObjectInfo next : agentAvatar.treeNode) {
                if (next.attachedToUUID != null && (!next.isDead) && next.attachedToUUID.equals(uuid)) {
                    if (!this.agentCircuit.getModules().rlvController.canDetachItem(next.attachmentID, next.getId())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (NoSuchElementException e) {
            Debug.Warning(e);
            return true;
        }
    }

    private boolean canWearItem(SLWearableType sLWearableType) {
        return this.agentCircuit.getModules().rlvController.canWearItem(sLWearableType);
    }

    private float getAgentHeight() {
        return (this.agentSizeVPLegLength * 0.1918f) + 1.706f + (this.agentSizeVPHipLength * 0.0375f) + (this.agentSizeVPHeight * 0.12022f) + (this.agentSizeVPHeadSize * 0.01117f) + (this.agentSizeVPNeckLength * 0.038f) + (this.agentSizeVPHeelHeight * 0.08f) + (this.agentSizeVPPlatformHeight * 0.07f);
    }

    private int[] getAppearanceParams() {
        SLAvatarParams.AvatarParam avatarParam;
        int[] iArr = new int[218];
        for (int i = 0; i < 218; i++) {
            iArr[i] = 0;
            SLAvatarParams.ParamSet paramSet = SLAvatarParams.paramDefs[i];
            if (!(paramSet == null || paramSet.params.size() <= 0 || (avatarParam = (SLAvatarParams.AvatarParam) paramSet.params.get(0)) == null)) {
                int round = Math.round(((avatarParam.defValue - avatarParam.minValue) * 255.0f) / (avatarParam.maxValue - avatarParam.minValue));
                if (round < 0) {
                    round = 0;
                } else if (round > 255) {
                    round = 255;
                }
                iArr[i] = round;
            }
        }
        for (SLWearable wearableData : this.wornWearables.values()) {
            SLWearableData wearableData2 = wearableData.getWearableData();
            if (wearableData2 != null) {
                for (SLWearableData.WearableParam wearableParam : wearableData2.params) {
                    SLAvatarParams.ParamSet paramSet2 = SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableParam.paramIndex));
                    if (paramSet2 != null && paramSet2.params.size() > 0 && paramSet2.appearanceIndex >= 0) {
                        SLAvatarParams.AvatarParam avatarParam2 = (SLAvatarParams.AvatarParam) paramSet2.params.get(0);
                        int round2 = Math.round(((wearableParam.paramValue - avatarParam2.minValue) * 255.0f) / (avatarParam2.maxValue - avatarParam2.minValue));
                        if (round2 < 0) {
                            round2 = 0;
                        } else if (round2 > 255) {
                            round2 = 255;
                        }
                        iArr[paramSet2.appearanceIndex] = round2;
                        switch (paramSet2.id) {
                            case 33:
                                this.agentSizeVPHeight = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPHeelHeight /*198*/:
                                this.agentSizeVPHeelHeight = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPPlatformHeight /*503*/:
                                this.agentSizeVPPlatformHeight = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPHeadSize /*682*/:
                                this.agentSizeVPHeadSize = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPLegLength /*692*/:
                                this.agentSizeVPLegLength = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPNeckLength /*756*/:
                                this.agentSizeVPNeckLength = wearableParam.paramValue;
                                break;
                            case Param_agentSizeVPHipLength /*842*/:
                                this.agentSizeVPHipLength = wearableParam.paramValue;
                                break;
                        }
                    }
                }
            }
        }
        this.agentSizeKnown = true;
        return iArr;
    }

    /* access modifiers changed from: private */
    public ImmutableList<WornItem> getWornItems() {
        SLObjectAvatarInfo agentAvatar;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Table.Cell cell : this.wornWearables.cellSet()) {
            SLWearable sLWearable = (SLWearable) cell.getValue();
            if (sLWearable != null) {
                builder.add((Object) new WornItem((SLWearableType) cell.getRowKey(), 0, (UUID) cell.getColumnKey(), sLWearable.getName(), 0, false));
            }
        }
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    builder.add((Object) new WornItem((SLWearableType) null, next.attachmentID, next.getId(), next.getName(), next.localID, next.isTouchable()));
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e);
            }
        }
        return builder.build();
    }

    private boolean isItemWorn(SLInventoryEntry sLInventoryEntry, boolean z) {
        return sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, z) != null;
    }

    /* access modifiers changed from: private */
    /* renamed from: onCofFolderEntry */
    public void m201com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref1(InventoryEntryList inventoryEntryList) {
        if (inventoryEntryList != null) {
            Iterator it = inventoryEntryList.iterator();
            while (it.hasNext()) {
                SLInventoryEntry sLInventoryEntry = (SLInventoryEntry) it.next();
                if (sLInventoryEntry != null && sLInventoryEntry.isFolder && sLInventoryEntry.typeDefault == 46) {
                    this.cofFolderUUID.set(sLInventoryEntry.uuid);
                    this.findCofFolder.unsubscribe();
                    this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(sLInventoryEntry.uuid, (String) null, true, true, false, (SLAssetType) null));
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onCurrentOutfitFolder */
    public void m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0(InventoryEntryList inventoryEntryList) {
        SLInventoryEntry folder;
        if (inventoryEntryList != null && (folder = inventoryEntryList.getFolder()) != null && Objects.equal(folder.sessionID, this.agentCircuit.circuitInfo.sessionID)) {
            Debug.Log("AvatarAppearance: COF has been fetched from inventory.");
            UpdateWearableNames();
            this.cofReady = true;
            UpdateCurrentOutfitLink(inventoryEntryList);
            ProcessMultiLayer();
            UpdateCOFContents();
            RequestServerRebake();
        }
    }

    private void startBaking() {
        BakeProcess bakeProcess2 = this.bakeProcess;
        if (bakeProcess2 != null) {
            bakeProcess2.cancel();
        }
        this.bakeProcess = new BakeProcess(this.wornWearables, this, this.agentCircuit.getModules().textureUploader, this.eventBus);
    }

    private void updateIfWearablesReady() {
        if (areWearablesReady()) {
            SendAvatarSetAppearance();
            if (!this.needUpdateAppearance) {
                UpdateCOFContents();
            } else if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
                startBaking();
            }
        }
    }

    public void AttachInventoryItem(SLInventoryEntry sLInventoryEntry, int i, boolean z) {
        boolean z2;
        InventoryDB inventoryDB = null;
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase();
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry);
        }
        if (sLInventoryEntry != null) {
            if (sLInventoryEntry.assetType == SLAssetType.AT_CLOTHING.getTypeCode() || sLInventoryEntry.assetType == SLAssetType.AT_BODYPART.getTypeCode()) {
                WearItem(sLInventoryEntry, z);
                return;
            }
            Debug.Printf("Outfits: Attaching inventory item %s", sLInventoryEntry.uuid.toString());
            Map map = this.wantedAttachments.get();
            if (map == null || (!map.containsKey(sLInventoryEntry.uuid))) {
                HashMap hashMap = new HashMap();
                if (map != null) {
                    hashMap.putAll(map);
                }
                hashMap.put(sLInventoryEntry.uuid, sLInventoryEntry.name);
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap));
                z2 = true;
            } else {
                z2 = false;
            }
            RezSingleAttachmentFromInv rezSingleAttachmentFromInv = new RezSingleAttachmentFromInv();
            rezSingleAttachmentFromInv.AgentData_Field.AgentID = this.circuitInfo.agentID;
            rezSingleAttachmentFromInv.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            if (!z) {
                i |= 128;
            }
            rezSingleAttachmentFromInv.ObjectData_Field.ItemID = sLInventoryEntry.uuid;
            rezSingleAttachmentFromInv.ObjectData_Field.OwnerID = sLInventoryEntry.ownerUUID;
            rezSingleAttachmentFromInv.ObjectData_Field.AttachmentPt = i;
            rezSingleAttachmentFromInv.ObjectData_Field.ItemFlags = sLInventoryEntry.flags;
            rezSingleAttachmentFromInv.ObjectData_Field.GroupMask = sLInventoryEntry.groupMask;
            rezSingleAttachmentFromInv.ObjectData_Field.EveryoneMask = sLInventoryEntry.everyoneMask;
            rezSingleAttachmentFromInv.ObjectData_Field.NextOwnerMask = sLInventoryEntry.nextOwnerMask;
            rezSingleAttachmentFromInv.ObjectData_Field.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name);
            rezSingleAttachmentFromInv.ObjectData_Field.Description = SLMessage.stringToVariableOEM(sLInventoryEntry.description);
            rezSingleAttachmentFromInv.isReliable = true;
            SendMessage(rezSingleAttachmentFromInv);
            if (z2) {
                this.needUpdateCOF.set(true);
                UpdateCOFContents();
            }
        }
    }

    public void ChangeOutfit(List<SLInventoryEntry> list, boolean z, SLInventoryEntry sLInventoryEntry) {
        boolean z2;
        boolean z3;
        boolean z4;
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv;
        boolean z5;
        boolean z6;
        SLWearableType byCode;
        boolean z7;
        boolean z8;
        SLWearable remove;
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv2;
        InventoryDB database = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null;
        Map map = this.wantedAttachments.get();
        HashMap hashMap = map != null ? new HashMap(map) : new HashMap();
        if (z) {
            z2 = true;
            hashMap.clear();
        } else {
            z2 = false;
        }
        if (sLInventoryEntry != null) {
            if (z) {
                if (this.wantedOutfitFolder == null) {
                    this.wantedOutfitFolder = sLInventoryEntry;
                    z3 = true;
                } else if (!this.wantedOutfitFolder.uuid.equals(sLInventoryEntry.uuid)) {
                    this.wantedOutfitFolder = sLInventoryEntry;
                    z3 = true;
                }
            }
            z3 = z2;
        } else {
            z3 = z2;
        }
        UUID randomUUID = UUID.randomUUID();
        ArrayList arrayList = new ArrayList();
        for (SLInventoryEntry sLInventoryEntry2 : list) {
            SLInventoryEntry resolveLink = database != null ? database.resolveLink(sLInventoryEntry2) : sLInventoryEntry2;
            if (resolveLink != null) {
                sLInventoryEntry2 = resolveLink;
            }
            if (sLInventoryEntry2 != null && (sLInventoryEntry2.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (sLInventoryEntry2.isLink() && sLInventoryEntry2.invType == SLInventoryType.IT_OBJECT.getTypeCode()))) {
                arrayList.add(sLInventoryEntry2);
            }
        }
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv3 = new RezMultipleAttachmentsFromInv();
        rezMultipleAttachmentsFromInv3.AgentData_Field.AgentID = this.circuitInfo.agentID;
        rezMultipleAttachmentsFromInv3.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        rezMultipleAttachmentsFromInv3.HeaderData_Field.CompoundMsgID = randomUUID;
        rezMultipleAttachmentsFromInv3.HeaderData_Field.TotalObjects = arrayList.size();
        rezMultipleAttachmentsFromInv3.HeaderData_Field.FirstDetachAll = z;
        Debug.Printf("Wearing: totalAttachments %d", Integer.valueOf(arrayList.size()));
        Iterator it = arrayList.iterator();
        while (true) {
            z4 = z3;
            rezMultipleAttachmentsFromInv = rezMultipleAttachmentsFromInv3;
            if (!it.hasNext()) {
                break;
            }
            SLInventoryEntry sLInventoryEntry3 = (SLInventoryEntry) it.next();
            if (rezMultipleAttachmentsFromInv == null) {
                RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv4 = new RezMultipleAttachmentsFromInv();
                rezMultipleAttachmentsFromInv4.AgentData_Field.AgentID = this.circuitInfo.agentID;
                rezMultipleAttachmentsFromInv4.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                rezMultipleAttachmentsFromInv4.HeaderData_Field.CompoundMsgID = randomUUID;
                rezMultipleAttachmentsFromInv4.HeaderData_Field.TotalObjects = arrayList.size();
                rezMultipleAttachmentsFromInv4.HeaderData_Field.FirstDetachAll = z;
                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv4;
            } else {
                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv;
            }
            RezMultipleAttachmentsFromInv.ObjectData objectData = new RezMultipleAttachmentsFromInv.ObjectData();
            UUID uuid = sLInventoryEntry3.uuid;
            Debug.Printf("Wearing: entry '%s' actualUUID %s", sLInventoryEntry3.name, uuid);
            hashMap.put(uuid, sLInventoryEntry3.name);
            z3 = true;
            objectData.ItemID = uuid;
            objectData.OwnerID = sLInventoryEntry3.ownerUUID;
            objectData.AttachmentPt = 128;
            objectData.ItemFlags = sLInventoryEntry3.flags;
            objectData.GroupMask = sLInventoryEntry3.groupMask;
            objectData.EveryoneMask = sLInventoryEntry3.everyoneMask;
            objectData.NextOwnerMask = sLInventoryEntry3.nextOwnerMask;
            objectData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry3.name);
            objectData.Description = SLMessage.stringToVariableOEM(sLInventoryEntry3.description);
            rezMultipleAttachmentsFromInv2.ObjectData_Fields.add(objectData);
            if (rezMultipleAttachmentsFromInv2.ObjectData_Fields.size() >= 4) {
                rezMultipleAttachmentsFromInv2.isReliable = true;
                SendMessage(rezMultipleAttachmentsFromInv2);
                rezMultipleAttachmentsFromInv3 = null;
            } else {
                rezMultipleAttachmentsFromInv3 = rezMultipleAttachmentsFromInv2;
            }
        }
        if (rezMultipleAttachmentsFromInv != null) {
            rezMultipleAttachmentsFromInv.isReliable = true;
            SendMessage(rezMultipleAttachmentsFromInv);
        }
        boolean z9 = false;
        RLVController rLVController = this.agentCircuit.getModules().rlvController;
        HashSet hashSet = new HashSet();
        HashBasedTable<SLWearableType, UUID, SLWearable> create = HashBasedTable.create(this.wornWearables);
        Iterator<T> it2 = list.iterator();
        while (true) {
            z5 = z9;
            if (!it2.hasNext()) {
                break;
            }
            SLInventoryEntry sLInventoryEntry4 = (SLInventoryEntry) it2.next();
            SLInventoryEntry resolveLink2 = database != null ? database.resolveLink(sLInventoryEntry4) : sLInventoryEntry4;
            if (resolveLink2 != null && ((resolveLink2.assetType == SLAssetType.AT_BODYPART.getTypeCode() || resolveLink2.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) && (byCode = SLWearableType.getByCode(resolveLink2.flags & 255)) != null)) {
                if (!rLVController.canWearItem(byCode)) {
                    z7 = false;
                } else if (byCode.isBodyPart()) {
                    if (!rLVController.canTakeItemOff(byCode)) {
                        boolean z10 = false;
                        Iterator<T> it3 = create.row(byCode).keySet().iterator();
                        while (true) {
                            z8 = z10;
                            if (!it3.hasNext()) {
                                break;
                            }
                            z10 = !((UUID) it3.next()).equals(resolveLink2.assetUUID) ? true : z8;
                        }
                        if (z8) {
                            z7 = false;
                        }
                    }
                    z7 = true;
                } else {
                    z7 = true;
                }
                if (z7) {
                    hashSet.add(resolveLink2.assetUUID);
                    if (!create.contains(byCode, resolveLink2.assetUUID)) {
                        addWearable(create, byCode, resolveLink2.uuid, resolveLink2.assetUUID, resolveLink2.name);
                        z5 = true;
                        if (byCode.isBodyPart()) {
                            HashSet<UUID> hashSet2 = new HashSet<>();
                            for (UUID uuid2 : create.row(byCode).keySet()) {
                                if (!uuid2.equals(resolveLink2.assetUUID)) {
                                    hashSet2.add(uuid2);
                                }
                            }
                            for (UUID uuid3 : hashSet2) {
                                if (create.row(byCode).size() > 1 && (remove = create.remove(byCode, uuid3)) != null) {
                                    remove.dispose();
                                }
                            }
                        }
                    }
                }
            }
            z9 = z5;
        }
        if (z) {
            z6 = z5;
            for (SLWearableType sLWearableType : SLWearableType.values()) {
                if (!sLWearableType.isBodyPart() && rLVController.canTakeItemOff(sLWearableType)) {
                    Map<UUID, SLWearable> row = create.row(sLWearableType);
                    HashSet<UUID> hashSet3 = new HashSet<>();
                    for (UUID uuid4 : row.keySet()) {
                        if (!hashSet.contains(uuid4)) {
                            hashSet3.add(uuid4);
                        }
                    }
                    boolean z11 = z6;
                    for (UUID remove2 : hashSet3) {
                        SLWearable remove3 = row.remove(remove2);
                        if (remove3 != null) {
                            remove3.dispose();
                        }
                        z11 = true;
                    }
                    z6 = z11;
                }
            }
        } else {
            z6 = z5;
        }
        if (z6) {
            this.wornWearables = ImmutableTable.copyOf(create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (z4) {
            this.wantedAttachments.set(ImmutableMap.copyOf(hashMap));
        }
        if (z6) {
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
            z4 = false;
        }
        if (z4) {
            this.needUpdateCOF.set(true);
            UpdateCOFContents();
        }
    }

    public void DetachInventoryItem(SLInventoryEntry sLInventoryEntry) {
        boolean z;
        if (canDetachItem(sLInventoryEntry)) {
            UUID uuid = sLInventoryEntry.isLink() ? sLInventoryEntry.assetUUID : sLInventoryEntry.uuid;
            Debug.Log("Outfits: Detaching inventory item " + uuid);
            Map map = this.wantedAttachments.get();
            if (map == null) {
                z = false;
            } else if (map.containsKey(uuid)) {
                HashMap hashMap = new HashMap(map);
                hashMap.remove(uuid);
                this.wantedAttachments.set(ImmutableMap.copyOf(hashMap));
                z = true;
            } else {
                z = false;
            }
            DetachAttachmentIntoInv detachAttachmentIntoInv = new DetachAttachmentIntoInv();
            detachAttachmentIntoInv.ObjectData_Field.AgentID = this.circuitInfo.agentID;
            detachAttachmentIntoInv.ObjectData_Field.ItemID = uuid;
            detachAttachmentIntoInv.isReliable = true;
            SendMessage(detachAttachmentIntoInv);
            if (z) {
                this.needUpdateCOF.set(true);
                UpdateCOFContents();
            }
        }
    }

    public void DetachItem(WornItem wornItem) {
        if (canDetachItem(wornItem)) {
            DetachItem(wornItem.objectLocalID);
        }
    }

    public void DetachItemFromPoint(int i) {
        SLObjectAvatarInfo agentAvatar;
        HashSet<Integer> hashSet = null;
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead) && next.attachmentID == i && this.agentCircuit.getModules().rlvController.canDetachItem(i, next.getId())) {
                        if (hashSet == null) {
                            hashSet = new HashSet<>();
                        }
                        hashSet.add(Integer.valueOf(next.localID));
                    }
                    hashSet = hashSet;
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e);
            }
        }
        if (hashSet != null) {
            for (Integer intValue : hashSet) {
                DetachItem(intValue.intValue());
            }
        }
    }

    public void ForceTakeItemOff(SLWearableType sLWearableType) {
        boolean z;
        if (!this.wornWearables.row(sLWearableType).isEmpty()) {
            z = true;
            HashBasedTable<SLWearableType, UUID, SLWearable> create = HashBasedTable.create(this.wornWearables);
            create.rowKeySet().remove(sLWearableType);
            this.wornWearables = ImmutableTable.copyOf(create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        } else {
            z = false;
        }
        if (z) {
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    @SLMessageHandler
    public void HandleAgentWearablesUpdate(AgentWearablesUpdate agentWearablesUpdate) {
        Debug.Log("AvatarAppearance: Got AgentWearablesUpdate, " + agentWearablesUpdate.WearableData_Fields.size() + " wearables.");
        HashSet hashSet = new HashSet();
        HashBasedTable<SLWearableType, UUID, SLWearable> create = HashBasedTable.create(this.wornWearables);
        for (AgentWearablesUpdate.WearableData wearableData : agentWearablesUpdate.WearableData_Fields) {
            Debug.Log("Wearable: type = " + wearableData.WearableType + ", itemID = " + wearableData.ItemID + ", assetID = " + wearableData.AssetID);
            if (wearableData.AssetID.getLeastSignificantBits() != 0 || wearableData.AssetID.getMostSignificantBits() != 0) {
                hashSet.add(wearableData.AssetID);
                SLWearableType byCode = SLWearableType.getByCode(wearableData.WearableType);
                if (byCode != null && create.get(byCode, wearableData.AssetID) == null) {
                    addWearable(create, byCode, wearableData.ItemID, wearableData.AssetID, (String) null);
                }
            }
        }
        Debug.Log("AvatarAppearance: AgentWearablesUpdate: wearing now: " + hashSet.size() + " ids");
        HashSet<UUID> hashSet2 = new HashSet<>();
        for (UUID uuid : create.columnKeySet()) {
            if (!hashSet.contains(uuid)) {
                hashSet2.add(uuid);
            }
        }
        for (UUID column : hashSet2) {
            Map<SLWearableType, SLWearable> column2 = create.column(column);
            for (SLWearable dispose : column2.values()) {
                dispose.dispose();
            }
            column2.clear();
        }
        this.wornWearables = ImmutableTable.copyOf(create);
        this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
        this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        UpdateWearableNames();
        this.legacyAppearanceReady = true;
        ProcessMultiLayer();
        SendAgentIsNowWearing();
        StartUpdatingAppearance();
    }

    public void HandleAvatarAppearance(AvatarAppearance avatarAppearance) {
        if (avatarAppearance.AppearanceData_Fields.size() > 0) {
            this.currentCofAppearanceVersion = avatarAppearance.AppearanceData_Fields.get(0).CofVersion;
            Debug.Printf("AvatarAppearance: inventory COF %d, last updated COF %d, appearance COF %d", Integer.valueOf(this.currentCofInventoryVersion), Integer.valueOf(this.lastCofUpdatedVersion), Integer.valueOf(this.currentCofAppearanceVersion));
        }
    }

    public void HandleCircuitReady() {
        SLInventoryEntry findSpecialFolder;
        boolean z = true;
        super.HandleCircuitReady();
        if (this.userManager != null) {
            UUID rootFolder = this.userManager.getInventoryManager().getRootFolder();
            if (rootFolder == null || (findSpecialFolder = this.userManager.getInventoryManager().getDatabase().findSpecialFolder(rootFolder, 46)) == null) {
                z = false;
            } else {
                Debug.Printf("Found existing COF folder: %s", findSpecialFolder.uuid);
                this.cofFolderUUID.set(findSpecialFolder.uuid);
                this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(findSpecialFolder.uuid, (String) null, true, true, false, (SLAssetType) null));
            }
            if (!z) {
                Debug.Printf("Existing COF folder not found, requesting.", new Object[0]);
                this.findCofFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.findFolderWithType((UUID) null, 46));
            }
        }
    }

    public void HandleCloseCircuit() {
        this.findCofFolder.unsubscribe();
        this.currentOutfitFolder.unsubscribe();
        if (this.userManager != null) {
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, null);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, null);
            this.userManager.wornItems().detachRequestHandler(this.wornItemsRequestHandler);
        }
        if (this.bakingThread != null) {
            this.bakingThread.interrupt();
            this.bakingThread = null;
        }
        if (this.serverSideAppearanceUpdateTask != null) {
            this.serverSideAppearanceUpdateTask.cancel(true);
        }
        super.HandleCloseCircuit();
    }

    public void OnMyAvatarCreated(SLObjectAvatarInfo sLObjectAvatarInfo) {
        if (this.agentVisualParams != null) {
            sLObjectAvatarInfo.ApplyAvatarVisualParams(this.agentVisualParams);
        }
    }

    public void SendAgentWearablesRequest() {
        AgentWearablesRequest agentWearablesRequest = new AgentWearablesRequest();
        agentWearablesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentWearablesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentWearablesRequest.isReliable = true;
        SendMessage(agentWearablesRequest);
    }

    public void TakeItemOff(SLInventoryEntry sLInventoryEntry) {
        InventoryDB inventoryDB = null;
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase();
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry);
        }
        if (sLInventoryEntry != null) {
            TakeItemOff(sLInventoryEntry.assetUUID);
        }
    }

    public void TakeItemOff(UUID uuid) {
        boolean z;
        RLVController rLVController = this.agentCircuit.getModules().rlvController;
        HashBasedTable<SLWearableType, UUID, SLWearable> create = HashBasedTable.create(this.wornWearables);
        SLWearableType[] values = SLWearableType.values();
        int length = values.length;
        int i = 0;
        boolean z2 = false;
        while (i < length) {
            SLWearableType sLWearableType = values[i];
            if (!rLVController.canTakeItemOff(sLWearableType)) {
                z = z2;
            } else {
                SLWearable remove = create.remove(sLWearableType, uuid);
                if (remove != null) {
                    remove.dispose();
                    create.columnKeySet().remove(uuid);
                    z = true;
                } else {
                    z = z2;
                }
            }
            i++;
            z2 = z;
        }
        if (z2) {
            this.wornWearables = ImmutableTable.copyOf(create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    public void UpdateMyAttachments() {
        SLObjectAvatarInfo agentAvatar;
        HashMap hashMap = new HashMap();
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead)) {
                        hashMap.put(next.attachedToUUID, Strings.nullToEmpty(next.getName()));
                    }
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
        ImmutableMap<UUID, String> copyOf = ImmutableMap.copyOf(hashMap);
        if (!this.wornAttachments.equals(copyOf)) {
            Debug.Log("AvatarAppearance: attachments changed.");
            this.wornAttachments = copyOf;
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, this.wornAttachments);
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    public void WearItem(SLInventoryEntry sLInventoryEntry, boolean z) {
        InventoryDB inventoryDB = null;
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase();
        }
        if (inventoryDB != null) {
            WearItemList(inventoryDB, ImmutableList.of(sLInventoryEntry), z);
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    public boolean canDetachItem(SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (sLInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                return true;
            }
            if (sLInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && this.wornAttachments.containsKey(sLInventoryEntry.assetUUID)) {
                return canDetachItem(sLInventoryEntry.assetUUID);
            }
        } else if (sLInventoryEntry.assetType == SLAssetType.AT_BODYPART.getTypeCode() || sLInventoryEntry.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
            return true;
        } else {
            if (sLInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode()) {
                if (!this.wornAttachments.containsKey(sLInventoryEntry.uuid) || canDetachItem(sLInventoryEntry.uuid)) {
                    return !this.wornAttachments.containsKey(sLInventoryEntry.assetUUID) || canDetachItem(sLInventoryEntry.assetUUID);
                }
                return false;
            }
        }
        return false;
    }

    public boolean canDetachItem(WornItem wornItem) {
        return this.agentCircuit.getModules().rlvController.canDetachItem(wornItem.getAttachedTo(), wornItem.itemID());
    }

    public boolean canTakeItemOff(SLWearableType sLWearableType) {
        return this.agentCircuit.getModules().rlvController.canTakeItemOff(sLWearableType);
    }

    public boolean canTakeItemOff(SLInventoryEntry sLInventoryEntry) {
        Object whatIsItemWornOn = sLInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false);
        if (whatIsItemWornOn == null) {
            return true;
        }
        RLVController rLVController = this.agentCircuit.getModules().rlvController;
        if (whatIsItemWornOn instanceof SLWearableType) {
            return rLVController.canTakeItemOff((SLWearableType) whatIsItemWornOn);
        }
        return true;
    }

    public boolean canWearItem(SLInventoryEntry sLInventoryEntry) {
        InventoryDB inventoryDB = null;
        if (this.userManager != null) {
            inventoryDB = this.userManager.getInventoryManager().getDatabase();
        }
        if (inventoryDB != null) {
            sLInventoryEntry = inventoryDB.resolveLink(sLInventoryEntry);
        }
        if (sLInventoryEntry == null) {
            return false;
        }
        SLWearableType byCode = SLWearableType.getByCode(sLInventoryEntry.flags & 255);
        return byCode == null || canWearItem(byCode);
    }

    public void finishBaking(BakeProcess bakeProcess2, SLTextureEntry sLTextureEntry) {
        if (sLTextureEntry != null) {
            this.agentBakedTextures = sLTextureEntry;
            SendAvatarSetAppearance();
        }
        if (this.bakeProcess == bakeProcess2) {
            this.bakeProcess = null;
        }
    }

    public UUID getAttachmentUUID(int i) {
        SLObjectAvatarInfo agentAvatar;
        if (!(this.parcelInfo == null || (agentAvatar = this.parcelInfo.getAgentAvatar()) == null)) {
            try {
                for (SLObjectInfo next : agentAvatar.treeNode) {
                    if (next.attachedToUUID != null && (!next.isDead) && next.attachmentID == i) {
                        return next.getId();
                    }
                }
            } catch (NoSuchElementException e) {
                Debug.Warning(e);
            }
        }
        return null;
    }

    public boolean hasWornWearable(SLWearableType sLWearableType) {
        return this.wornWearables.containsRow(sLWearableType);
    }

    public boolean isItemWorn(SLInventoryEntry sLInventoryEntry) {
        return isItemWorn(sLInventoryEntry, false);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_17963  reason: not valid java name */
    public /* synthetic */ void m202lambda$com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_17963(int i, String str) {
        LLSDXMLRequest lLSDXMLRequest = new LLSDXMLRequest();
        LLSDMap lLSDMap = new LLSDMap(new LLSDMap.LLSDMapEntry("cof_version", new LLSDInt(i)));
        int i2 = 3;
        while (i2 > 0) {
            try {
                LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(str, lLSDMap);
                if (PerformRequest != null && PerformRequest.keyExists("error")) {
                    LLSDNode byKey = PerformRequest.byKey("error");
                    if (byKey.isString()) {
                        Debug.Printf("AvatarAppearance: server-side error: %s", byKey.asString());
                    } else {
                        Debug.Printf("AvatarAppearance: server-side update ok.", new Object[0]);
                    }
                }
                this.lastCofUpdateError = false;
                return;
            } catch (Exception e) {
                Debug.Printf("AvatarAppearance: server-side update error: [exception %s]", e.toString());
                this.lastCofUpdateError = true;
                int i3 = i2 - 1;
                try {
                    Thread.sleep(1000);
                    i2 = i3;
                } catch (InterruptedException e2) {
                    return;
                }
            }
        }
    }

    public void onWearableStatusChanged(SLWearable sLWearable) {
        updateIfWearablesReady();
    }
}
