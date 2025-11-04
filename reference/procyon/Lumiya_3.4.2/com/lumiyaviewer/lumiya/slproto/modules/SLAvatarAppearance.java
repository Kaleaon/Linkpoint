// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesUpdate;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.messages.DetachAttachmentIntoInv;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.messages.RezSingleAttachmentFromInv;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import java.util.Collection;
import com.google.common.collect.HashBasedTable;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.messages.RezMultipleAttachmentsFromInv;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryType;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import java.util.HashSet;
import java.util.LinkedList;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.messages.AgentSetAppearance;
import com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.util.NoSuchElementException;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ObjectDetach;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.google.common.collect.ImmutableTable;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.google.common.collect.Table;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.concurrent.Future;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.baker.BakeProcess;
import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;

public class SLAvatarAppearance extends SLModule implements OnWearableStatusChangeListener
{
    private static final int Param_agentSizeVPHeadSize = 682;
    private static final int Param_agentSizeVPHeelHeight = 198;
    private static final int Param_agentSizeVPHeight = 33;
    private static final int Param_agentSizeVPHipLength = 842;
    private static final int Param_agentSizeVPLegLength = 692;
    private static final int Param_agentSizeVPNeckLength = 756;
    private static final int Param_agentSizeVPPlatformHeight = 503;
    private SLTextureEntry agentBakedTextures;
    private boolean agentSizeKnown;
    private float agentSizeVPHeadSize;
    private float agentSizeVPHeelHeight;
    private float agentSizeVPHeight;
    private float agentSizeVPHipLength;
    private float agentSizeVPLegLength;
    private float agentSizeVPNeckLength;
    private float agentSizeVPPlatformHeight;
    private int[] agentVisualParams;
    private volatile BakeProcess bakeProcess;
    private Thread bakingThread;
    private final SLCaps caps;
    private final AtomicReference<UUID> cofFolderUUID;
    private volatile boolean cofReady;
    private volatile int currentCofAppearanceVersion;
    private volatile int currentCofInventoryVersion;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> currentOutfitFolder;
    private final SubscriptionData<InventoryQuery, InventoryEntryList> findCofFolder;
    private final SLInventory inventory;
    private volatile boolean lastCofUpdateError;
    private volatile int lastCofUpdatedVersion;
    private volatile boolean legacyAppearanceReady;
    private volatile boolean multiLayerDone;
    private volatile boolean needUpdateAppearance;
    private final AtomicBoolean needUpdateCOF;
    private final SLParcelInfo parcelInfo;
    private Future<?> serverSideAppearanceUpdateTask;
    private int setAppearanceSerialNum;
    private final UserManager userManager;
    private final AtomicReference<Map<UUID, String>> wantedAttachments;
    private SLInventoryEntry wantedOutfitFolder;
    @Nonnull
    private volatile ImmutableMap<UUID, String> wornAttachments;
    private final RequestHandler<SubscriptionSingleKey> wornItemsRequestHandler;
    private final ResultHandler<SubscriptionSingleKey, ImmutableList<WornItem>> wornItemsResultHandler;
    @Nonnull
    private volatile Table<SLWearableType, UUID, SLWearable> wornWearables;
    
    public SLAvatarAppearance(final SLAgentCircuit slAgentCircuit, final SLInventory inventory, final SLCaps caps) {
        super(slAgentCircuit);
        this.setAppearanceSerialNum = 1;
        this.agentSizeKnown = false;
        this.needUpdateAppearance = false;
        this.needUpdateCOF = new AtomicBoolean(false);
        this.wantedAttachments = new AtomicReference<Map<UUID, String>>((Map<UUID, String>)ImmutableMap.of());
        this.wornAttachments = ImmutableMap.of();
        this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.of();
        this.wantedOutfitFolder = null;
        this.bakingThread = null;
        this.serverSideAppearanceUpdateTask = null;
        this.currentCofInventoryVersion = 0;
        this.currentCofAppearanceVersion = 0;
        this.lastCofUpdatedVersion = 0;
        this.lastCofUpdateError = false;
        this.legacyAppearanceReady = false;
        this.cofReady = false;
        this.multiLayerDone = false;
        this.cofFolderUUID = new AtomicReference<UUID>();
        this.wornItemsRequestHandler = new AsyncRequestHandler<SubscriptionSingleKey>(this.agentCircuit, new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                if (SLAvatarAppearance.this.wornItemsResultHandler != null) {
                    SLAvatarAppearance.this.wornItemsResultHandler.onResultData(subscriptionSingleKey, SLAvatarAppearance.this.getWornItems());
                }
            }
        });
        this.bakeProcess = null;
        this.caps = caps;
        this.inventory = inventory;
        this.parcelInfo = slAgentCircuit.getGridConnection().parcelInfo;
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
        this.currentOutfitFolder = new SubscriptionData<InventoryQuery, InventoryEntryList>(slAgentCircuit, new _$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo(this));
        this.findCofFolder = new SubscriptionData<InventoryQuery, InventoryEntryList>(slAgentCircuit, new _$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo$1(this));
        if (this.userManager != null) {
            this.wornItemsResultHandler = this.userManager.wornItems().attachRequestHandler(this.wornItemsRequestHandler);
        }
        else {
            this.wornItemsResultHandler = null;
        }
    }
    
    private void DetachItem(final int n) {
        Debug.Log("Outfits: detaching item " + n);
        int n2 = 0;
        final int n3 = 0;
        Object o = this.wantedAttachments.get();
        Label_0187: {
            if (o == null) {
                break Label_0187;
            }
            o = new HashMap((Map<?, ?>)o);
            int n4 = n3;
        Label_0164:
            while (true) {
                if (this.parcelInfo == null) {
                    break Label_0164;
                }
                final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
                n4 = n3;
                if (agentAvatar == null) {
                    break Label_0164;
                }
                while (true) {
                    Label_0319: {
                        try {
                            Block_12: {
                                Block_9: {
                                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                                        if (slObjectInfo.attachedToUUID != null && (slObjectInfo.isDead ^ true) && slObjectInfo.localID == n) {
                                            if (((Map<? extends K, ? extends V>)o).remove(slObjectInfo.getId()) != null) {
                                                break Block_9;
                                            }
                                            if (((Map<? extends K, ? extends V>)o).remove(slObjectInfo.attachedToUUID) != null) {
                                                break Block_12;
                                            }
                                            continue;
                                        }
                                    }
                                    break Label_0319;
                                }
                                n4 = 1;
                                if ((n2 = n4) != 0) {
                                    this.wantedAttachments.set((Map<UUID, String>)ImmutableMap.copyOf((Map<?, ?>)o));
                                    n2 = n4;
                                }
                                final ObjectDetach objectDetach = new ObjectDetach();
                                objectDetach.AgentData_Field.AgentID = this.circuitInfo.agentID;
                                objectDetach.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                                o = new ObjectDetach.ObjectData();
                                ((ObjectDetach.ObjectData)o).ObjectLocalID = n;
                                objectDetach.ObjectData_Fields.add((ObjectDetach.ObjectData)o);
                                objectDetach.isReliable = true;
                                this.SendMessage(objectDetach);
                                if (n2 != 0) {
                                    this.needUpdateCOF.set(true);
                                    this.UpdateCOFContents();
                                }
                                return;
                            }
                            n4 = 1;
                            continue Label_0164;
                        }
                        catch (final NoSuchElementException ex) {
                            Debug.Warning(ex);
                            n4 = n3;
                            continue Label_0164;
                        }
                        continue Label_0164;
                    }
                    n4 = 0;
                    continue Label_0164;
                }
                break;
            }
        }
    }
    
    private void ForceUpdateAppearance(final boolean b) {
        this.needUpdateAppearance = true;
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            this.eventBus.publish(new SLBakingProgressEvent(true, false, 0));
        }
        else if (b) {
            this.lastCofUpdatedVersion = 0;
            this.currentCofAppearanceVersion = 0;
            this.RequestServerRebake();
        }
        this.StartUpdatingAppearance();
    }
    
    private void ProcessMultiLayer() {
        if (!this.multiLayerDone && this.cofReady && this.legacyAppearanceReady) {
            this.UpdateMultiLayer();
        }
    }
    
    private void RequestServerRebake() {
        final String capability = this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance);
        final InventoryEntryList list = this.currentOutfitFolder.getData();
        if (capability != null && list != null) {
            final SLInventoryEntry folder = list.getFolder();
            if (folder != null) {
                this.currentCofInventoryVersion = folder.version;
                if ((this.currentCofInventoryVersion != this.lastCofUpdatedVersion && this.currentCofInventoryVersion != this.currentCofAppearanceVersion) || this.lastCofUpdateError) {
                    this.lastCofUpdatedVersion = this.currentCofInventoryVersion;
                    this.lastCofUpdateError = false;
                    this.UpdateServerSideAppearance(capability, folder.version);
                }
            }
        }
    }
    
    private void SendAgentIsNowWearing() {
        final AgentIsNowWearing agentIsNowWearing = new AgentIsNowWearing();
        agentIsNowWearing.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentIsNowWearing.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (final SLWearableType slWearableType : SLWearableType.values()) {
            final Map<UUID, SLWearable> row = this.wornWearables.row(slWearableType);
            boolean b;
            if (row != null) {
                final Iterator<Object> iterator = row.values().iterator();
                b = true;
                while (iterator.hasNext()) {
                    final SLWearable slWearable = iterator.next();
                    final AgentIsNowWearing.WearableData e = new AgentIsNowWearing.WearableData();
                    e.ItemID = slWearable.itemID;
                    e.WearableType = slWearableType.getTypeCode();
                    agentIsNowWearing.WearableData_Fields.add(e);
                    b = false;
                }
            }
            else {
                b = true;
            }
            if (b) {
                final AgentIsNowWearing.WearableData e2 = new AgentIsNowWearing.WearableData();
                e2.ItemID = new UUID(0L, 0L);
                e2.WearableType = slWearableType.getTypeCode();
                agentIsNowWearing.WearableData_Fields.add(e2);
            }
        }
        Debug.Log("AvatarAppearance: Sending AgentIsNowWearing, " + agentIsNowWearing.WearableData_Fields.size() + " wearables.");
        agentIsNowWearing.isReliable = true;
        this.SendMessage(agentIsNowWearing);
        this.needUpdateCOF.set(true);
        this.UpdateCOFContents();
    }
    
    private void SendAvatarSetAppearance() {
        SLObjectAvatarInfo agentAvatar = null;
        this.UpdateCOFContents();
        if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
            if (this.parcelInfo != null) {
                agentAvatar = this.parcelInfo.getAgentAvatar();
            }
            if (this.agentBakedTextures != null && agentAvatar != null) {
                agentAvatar.ApplyAvatarTextures(this.agentBakedTextures, true);
            }
            final AgentSetAppearance agentSetAppearance = new AgentSetAppearance();
            agentSetAppearance.AgentData_Field.AgentID = this.circuitInfo.agentID;
            agentSetAppearance.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            agentSetAppearance.AgentData_Field.SerialNum = this.setAppearanceSerialNum;
            agentSetAppearance.AgentData_Field.Size = new LLVector3();
            agentSetAppearance.AgentData_Field.Size.x = 1.0f;
            agentSetAppearance.AgentData_Field.Size.y = 1.0f;
            agentSetAppearance.AgentData_Field.Size.z = 1.0f;
            if (this.agentBakedTextures != null) {
                agentSetAppearance.ObjectData_Field.TextureEntry = this.agentBakedTextures.packByteArray();
            }
            else {
                agentSetAppearance.ObjectData_Field.TextureEntry = new byte[0];
            }
            this.agentVisualParams = this.getAppearanceParams();
            if (agentAvatar != null) {
                agentAvatar.ApplyAvatarVisualParams(this.agentVisualParams);
            }
            for (final int paramValue : this.agentVisualParams) {
                final AgentSetAppearance.VisualParam e = new AgentSetAppearance.VisualParam();
                e.ParamValue = paramValue;
                agentSetAppearance.VisualParam_Fields.add(e);
            }
            if (this.agentSizeKnown && this.areWearablesReady()) {
                agentSetAppearance.AgentData_Field.Size.x = 0.45f;
                agentSetAppearance.AgentData_Field.Size.y = 0.6f;
                agentSetAppearance.AgentData_Field.Size.z = this.getAgentHeight();
                Debug.Log("set agent height to " + agentSetAppearance.AgentData_Field.Size.z);
            }
            agentSetAppearance.isReliable = true;
            final StringBuilder append = new StringBuilder().append("AvatarAppearance: Sending agentSetAppearance: ").append(agentSetAppearance.VisualParam_Fields.size()).append(" params, hasTextures = ");
            String str;
            if (this.agentBakedTextures != null) {
                str = "yes";
            }
            else {
                str = "no";
            }
            Debug.Log(append.append(str).toString());
            this.SendMessage(agentSetAppearance);
            ++this.setAppearanceSerialNum;
        }
    }
    
    private void StartUpdatingAppearance() {
        this.updateIfWearablesReady();
    }
    
    private void UpdateCOFContents() {
        final boolean wearablesReady = this.areWearablesReady();
        Debug.Printf("Wearables ready %b, cofReady %b", wearablesReady, this.cofReady);
        if (!wearablesReady || !this.cofReady) {
            return;
        }
        final InventoryEntryList list = this.currentOutfitFolder.getData();
        if (list == null) {
            return;
        }
        final SLInventoryEntry folder = list.getFolder();
        if (folder == null) {
            return;
        }
        if (this.needUpdateCOF.getAndSet(false)) {
            this.currentCofInventoryVersion = folder.version;
            final LinkedList list2 = new LinkedList();
            final HashMap hashMap = new HashMap();
            final HashMap hashMap2 = new HashMap();
            final HashSet set = new HashSet();
            for (final SLWearable slWearable : this.wornWearables.values()) {
                if (!slWearable.getIsFailed()) {
                    set.add(slWearable.itemID);
                    hashMap.put(slWearable.itemID, slWearable);
                }
            }
            final Map map = this.wantedAttachments.get();
            if (map != null) {
                hashMap2.putAll(map);
            }
            final Iterator<SLInventoryEntry> iterator2 = list.iterator();
            boolean b = true;
            while (iterator2.hasNext()) {
                final SLInventoryEntry slInventoryEntry = iterator2.next();
                if (slInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
                    if (slInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                        if (!set.contains(slInventoryEntry.assetUUID)) {
                            list2.add(slInventoryEntry.uuid);
                        }
                    }
                    else if (slInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && map != null && !map.containsKey(slInventoryEntry.assetUUID)) {
                        Debug.Printf("Attached entry %s (%s) not found in wanted attachments", slInventoryEntry.assetUUID, slInventoryEntry.name);
                        list2.add(slInventoryEntry.uuid);
                    }
                    hashMap.remove(slInventoryEntry.assetUUID);
                    hashMap2.remove(slInventoryEntry.assetUUID);
                }
                else {
                    if (slInventoryEntry.assetType != SLAssetType.AT_LINK_FOLDER.getTypeCode() || this.wantedOutfitFolder == null) {
                        continue;
                    }
                    if (!this.wantedOutfitFolder.uuid.equals(slInventoryEntry.assetUUID)) {
                        list2.add(slInventoryEntry.uuid);
                    }
                    else {
                        b = false;
                    }
                }
            }
            Debug.Printf("Update COF: addWearablesList %d, killList %d", hashMap.size(), list2.size());
            boolean b2;
            if (!list2.isEmpty()) {
                this.inventory.DeleteMultiInventoryItemRaw(folder, list2);
                b2 = true;
            }
            else {
                b2 = false;
            }
            for (final SLWearable slWearable2 : hashMap.values()) {
                Debug.Printf("Update COF: adding %s, name = '%s'", slWearable2.itemID, slWearable2.getName());
                this.inventory.LinkInventoryItem(folder, slWearable2.itemID, SLInventoryType.IT_WEARABLE.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), slWearable2.getName(), "");
                b2 = true;
            }
            for (final Map.Entry<Object, V> entry : hashMap2.entrySet()) {
                Debug.Printf("Update COF: adding attachment %s, name = '%s'", entry.getKey(), entry.getValue());
                this.inventory.LinkInventoryItem(folder, entry.getKey(), SLInventoryType.IT_OBJECT.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), (String)entry.getValue(), "");
                b2 = true;
            }
            boolean b3 = b2;
            if (b) {
                b3 = b2;
                if (this.wantedOutfitFolder != null) {
                    Debug.Printf("Update COF: adding outfit link for outfit folder %s", this.wantedOutfitFolder.uuid);
                    this.inventory.LinkInventoryItem(folder, this.wantedOutfitFolder.uuid, SLInventoryType.IT_CATEGORY.getTypeCode(), SLAssetType.AT_LINK_FOLDER.getTypeCode(), this.wantedOutfitFolder.name, "");
                    b3 = true;
                }
            }
            Debug.Printf("Update COF: COF updated (had changes: %b).", b3);
            if (b3 && this.userManager != null) {
                this.userManager.getInventoryManager().requestFolderUpdate(folder.uuid);
            }
            this.RequestServerRebake();
        }
    }
    
    private void UpdateCurrentOutfitLink(@Nonnull final InventoryEntryList list) {
        for (final SLInventoryEntry slInventoryEntry : list) {
            if (slInventoryEntry.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode()) {
                this.userManager.wornOutfitLink().setData(SubscriptionSingleKey.Value, slInventoryEntry.assetUUID);
                break;
            }
        }
    }
    
    private void UpdateMultiLayer() {
    Label_0591_Outer:
        while (true) {
            RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv2 = null;
            Label_0612: {
                while (true) {
                    Label_0599: {
                    Label_0074_Outer:
                        while (true) {
                            InventoryDB database = null;
                            LinkedList list = null;
                            LinkedList list2 = null;
                        Label_0179:
                            while (true) {
                                SLInventoryEntry slInventoryEntry = null;
                            Label_0130:
                                while (true) {
                                    synchronized (this) {
                                        Debug.Printf("AvatarAppearance: MultiLayer: Updating multi layer appearance.", new Object[0]);
                                        Object iterator = this.currentOutfitFolder.getData();
                                        if (this.userManager != null) {
                                            database = this.userManager.getInventoryManager().getDatabase();
                                            if (iterator != null && database != null) {
                                                list = new LinkedList();
                                                list2 = new LinkedList();
                                                iterator = ((Iterable<SLInventoryEntry>)iterator).iterator();
                                                while (((Iterator)iterator).hasNext()) {
                                                    slInventoryEntry = (SLInventoryEntry)((Iterator)iterator).next();
                                                    if (slInventoryEntry.invType != SLInventoryType.IT_WEARABLE.getTypeCode()) {
                                                        break Label_0130;
                                                    }
                                                    list.add(slInventoryEntry);
                                                }
                                                break Label_0179;
                                            }
                                            break;
                                        }
                                    }
                                    database = null;
                                    continue Label_0074_Outer;
                                }
                                if (slInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (slInventoryEntry.isLink() && slInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode())) {
                                    list2.add(slInventoryEntry);
                                    continue Label_0591_Outer;
                                }
                                continue Label_0591_Outer;
                            }
                            if (this.WearItemList(database, list, false)) {
                                Debug.Printf("AvatarAppearance: MultiLayer: had some extra layers.", new Object[0]);
                                this.SendAgentIsNowWearing();
                                this.StartUpdatingAppearance();
                            }
                            else {
                                Debug.Printf("AvatarAppearance: MultiLayer: no extra layers.", new Object[0]);
                            }
                            if (list2.size() == 0) {
                                break Label_0599;
                            }
                            Debug.Printf("AvatarAppearance: Re-attaching %d attachments from COF.", list2.size());
                            final HashMap hashMap = new HashMap();
                            final UUID randomUUID = UUID.randomUUID();
                            final Iterator iterator2 = list2.iterator();
                            RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv = null;
                            while (iterator2.hasNext()) {
                                final SLInventoryEntry slInventoryEntry2 = (SLInventoryEntry)iterator2.next();
                                final SLInventoryEntry resolveLink = database.resolveLink(slInventoryEntry2);
                                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv;
                                if (resolveLink == null) {
                                    break Label_0612;
                                }
                                RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv3;
                                if ((rezMultipleAttachmentsFromInv3 = rezMultipleAttachmentsFromInv) == null) {
                                    rezMultipleAttachmentsFromInv3 = new RezMultipleAttachmentsFromInv();
                                    rezMultipleAttachmentsFromInv3.AgentData_Field.AgentID = this.circuitInfo.agentID;
                                    rezMultipleAttachmentsFromInv3.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                                    rezMultipleAttachmentsFromInv3.HeaderData_Field.CompoundMsgID = randomUUID;
                                    rezMultipleAttachmentsFromInv3.HeaderData_Field.TotalObjects = list2.size();
                                    rezMultipleAttachmentsFromInv3.HeaderData_Field.FirstDetachAll = false;
                                }
                                final RezMultipleAttachmentsFromInv.ObjectData e = new RezMultipleAttachmentsFromInv.ObjectData();
                                Debug.Printf("Re-attaching attachment: entry %s (%s)", resolveLink.uuid, slInventoryEntry2.name);
                                hashMap.put(resolveLink.uuid, slInventoryEntry2.name);
                                e.ItemID = resolveLink.uuid;
                                e.OwnerID = resolveLink.ownerUUID;
                                e.AttachmentPt = 128;
                                e.ItemFlags = resolveLink.flags;
                                e.GroupMask = resolveLink.groupMask;
                                e.EveryoneMask = resolveLink.everyoneMask;
                                e.NextOwnerMask = resolveLink.nextOwnerMask;
                                e.Name = SLMessage.stringToVariableOEM(slInventoryEntry2.name);
                                e.Description = SLMessage.stringToVariableOEM(slInventoryEntry2.description);
                                rezMultipleAttachmentsFromInv3.ObjectData_Fields.add(e);
                                rezMultipleAttachmentsFromInv2 = rezMultipleAttachmentsFromInv3;
                                if (rezMultipleAttachmentsFromInv3.ObjectData_Fields.size() < 4) {
                                    break Label_0612;
                                }
                                rezMultipleAttachmentsFromInv3.isReliable = true;
                                this.SendMessage(rezMultipleAttachmentsFromInv3);
                                rezMultipleAttachmentsFromInv = null;
                            }
                            this.wantedAttachments.set((Map<UUID, String>)ImmutableMap.copyOf((Map<?, ?>)hashMap));
                            if (rezMultipleAttachmentsFromInv != null) {
                                rezMultipleAttachmentsFromInv.isReliable = true;
                                this.SendMessage(rezMultipleAttachmentsFromInv);
                                break;
                            }
                            break;
                        }
                        this.multiLayerDone = true;
                        monitorexit(this);
                        return;
                    }
                    Debug.Printf("AvatarAppearance: No attachments in COF.", new Object[0]);
                    continue;
                }
            }
            RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv = rezMultipleAttachmentsFromInv2;
            continue;
        }
    }
    
    private void UpdateServerSideAppearance(final String s, final int i) {
        synchronized (this) {
            Debug.Printf("AvatarAppearance: capURL '%s', cofVersion %d", s, i);
            if (this.serverSideAppearanceUpdateTask != null) {
                this.serverSideAppearanceUpdateTask.cancel(true);
            }
            this.serverSideAppearanceUpdateTask = GenericHTTPExecutor.getInstance().submit(new _$Lambda$Jp5Too8LbDpaKzeYKjkvQvC1hZo$2(i, this, s));
        }
    }
    
    private void UpdateWearableNames() {
        InventoryDB database = null;
        final InventoryEntryList list = this.currentOutfitFolder.getData();
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        if (list != null && database != null) {
            for (final SLInventoryEntry slInventoryEntry : list) {
                if (!slInventoryEntry.isFolderOrFolderLink()) {
                    final SLInventoryEntry resolveLink = database.resolveLink(slInventoryEntry);
                    if (resolveLink == null || resolveLink.invType != SLInventoryType.IT_WEARABLE.getTypeCode()) {
                        continue;
                    }
                    final SLWearableType byCode = SLWearableType.getByCode(resolveLink.flags & 0xFF);
                    if (byCode == null) {
                        continue;
                    }
                    final SLWearable slWearable = this.wornWearables.get(byCode, resolveLink.assetUUID);
                    if (slWearable == null) {
                        continue;
                    }
                    slWearable.setInventoryName(resolveLink.name);
                }
            }
        }
    }
    
    private boolean WearItemList(final InventoryDB inventoryDB, final List<SLInventoryEntry> list, final boolean b) {
        final RLVController rlvController = this.agentCircuit.getModules().rlvController;
        final HashBasedTable<Object, Object, Object> create = HashBasedTable.create((Table<?, ?, ?>)this.wornWearables);
        final Iterator<Object> iterator = list.iterator();
        boolean b2 = false;
    Label_0350_Outer:
        while (iterator.hasNext()) {
            final SLInventoryEntry resolveLink = inventoryDB.resolveLink(iterator.next());
            if (resolveLink == null) {
                continue Label_0350_Outer;
            }
            final SLWearableType byCode = SLWearableType.getByCode(resolveLink.flags & 0xFF);
            if (byCode == null) {
                continue Label_0350_Outer;
            }
            final boolean b3 = b || byCode.isBodyPart();
            boolean b4 = false;
            Label_0103: {
                if (!rlvController.canWearItem(byCode)) {
                    b4 = false;
                }
                else if (b3) {
                    if (rlvController.canTakeItemOff(byCode) ^ true) {
                        final Iterator iterator2 = create.row(byCode).keySet().iterator();
                        boolean b5 = false;
                        while (iterator2.hasNext()) {
                            if (!((UUID)iterator2.next()).equals(resolveLink.assetUUID)) {
                                b5 = true;
                            }
                        }
                        if (b5) {
                            b4 = false;
                            break Label_0103;
                        }
                    }
                    b4 = true;
                }
                else {
                    b4 = true;
                }
            }
            if (!b4 || create.contains(byCode, resolveLink.assetUUID)) {
                continue Label_0350_Outer;
            }
            if (b3) {
                final HashSet set = new HashSet(create.row(byCode).keySet());
                set.remove(resolveLink.assetUUID);
                final Iterator iterator3 = set.iterator();
                while (iterator3.hasNext()) {
                    final SLWearable slWearable = (SLWearable)create.remove(byCode, iterator3.next());
                    if (slWearable != null) {
                        slWearable.dispose();
                    }
                }
            }
            this.addWearable((Table<SLWearableType, UUID, SLWearable>)create, byCode, resolveLink.uuid, resolveLink.assetUUID, resolveLink.name);
            b2 = true;
            while (true) {
                continue Label_0350_Outer;
                continue;
            }
        }
        if (b2) {
            this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.copyOf((Table<?, ?, ?>)create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        return b2;
    }
    
    private SLWearable addWearable(final Table<SLWearableType, UUID, SLWearable> table, final SLWearableType slWearableType, final UUID uuid, final UUID uuid2, final String inventoryName) {
        final SLWearable slWearable = new SLWearable(this.userManager, this.agentCircuit, uuid, uuid2, slWearableType, (SLWearable.OnWearableStatusChangeListener)this);
        if (inventoryName != null) {
            slWearable.setInventoryName(inventoryName);
        }
        table.put(slWearableType, uuid2, slWearable);
        return slWearable;
    }
    
    private boolean areWearablesReady() {
        final boolean b = false;
        final SLWearableType[] values = SLWearableType.values();
        final int length = values.length;
        int i = 0;
        boolean b2 = false;
        boolean b3 = false;
        while (i < length) {
            final SLWearableType slWearableType = values[i];
            final boolean isCritical = slWearableType.getIsCritical();
            final Map<UUID, SLWearable> row = this.wornWearables.row(slWearableType);
            int n2;
            boolean b4;
            if (row != null) {
                final Iterator<Object> iterator = row.values().iterator();
                int n = 0;
                while (true) {
                    n2 = n;
                    b4 = b3;
                    if (!iterator.hasNext()) {
                        break;
                    }
                    final SLWearable slWearable = iterator.next();
                    if (slWearable.getIsValid()) {
                        n = 1;
                    }
                    else {
                        if (slWearable.getIsFailed()) {
                            continue;
                        }
                        b3 = true;
                    }
                }
            }
            else {
                n2 = 0;
                b4 = b3;
            }
            boolean b5;
            if (isCritical) {
                if ((n2 ^ 0x1) != 0x0) {
                    int size;
                    if (row != null) {
                        size = row.size();
                    }
                    else {
                        size = 0;
                    }
                    Debug.Printf("missing wearables on critical layer %s (worn: %d entries)", slWearableType, size);
                    b5 = true;
                }
                else {
                    b5 = b2;
                }
            }
            else {
                b5 = b2;
            }
            ++i;
            b2 = b5;
            b3 = b4;
        }
        Debug.Printf("hasNotDownloaded %b, hasCriticalMissing %b", b3, b2);
        boolean b6 = b;
        if (!b3) {
            b6 = (b2 ^ true);
        }
        return b6;
    }
    
    private boolean canDetachItem(final UUID obj) {
        if (this.parcelInfo != null) {
            final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                        if (slObjectInfo.attachedToUUID != null && (slObjectInfo.isDead ^ true) && slObjectInfo.attachedToUUID.equals(obj) && !this.agentCircuit.getModules().rlvController.canDetachItem(slObjectInfo.attachmentID, slObjectInfo.getId())) {
                            return false;
                        }
                    }
                    return true;
                }
                catch (final NoSuchElementException ex) {
                    Debug.Warning(ex);
                }
            }
        }
        return true;
    }
    
    private boolean canWearItem(final SLWearableType slWearableType) {
        return this.agentCircuit.getModules().rlvController.canWearItem(slWearableType);
    }
    
    private float getAgentHeight() {
        return this.agentSizeVPLegLength * 0.1918f + 1.706f + this.agentSizeVPHipLength * 0.0375f + this.agentSizeVPHeight * 0.12022f + this.agentSizeVPHeadSize * 0.01117f + this.agentSizeVPNeckLength * 0.038f + this.agentSizeVPHeelHeight * 0.08f + this.agentSizeVPPlatformHeight * 0.07f;
    }
    
    private int[] getAppearanceParams() {
        final int[] array = new int[218];
        for (int i = 0; i < 218; ++i) {
            array[i] = 0;
            final SLAvatarParams.ParamSet set = SLAvatarParams.paramDefs[i];
            if (set != null && set.params.size() > 0) {
                final SLAvatarParams.AvatarParam avatarParam = set.params.get(0);
                if (avatarParam != null) {
                    final int round = Math.round((avatarParam.defValue - avatarParam.minValue) * 255.0f / (avatarParam.maxValue - avatarParam.minValue));
                    int n;
                    if (round < 0) {
                        n = 0;
                    }
                    else if ((n = round) > 255) {
                        n = 255;
                    }
                    array[i] = n;
                }
            }
        }
        final Iterator<Object> iterator = this.wornWearables.values().iterator();
        while (iterator.hasNext()) {
            final SLWearableData wearableData = iterator.next().getWearableData();
            if (wearableData != null) {
                for (final SLWearableData.WearableParam wearableParam : wearableData.params) {
                    final SLAvatarParams.ParamSet set2 = SLAvatarParams.paramByIDs.get(wearableParam.paramIndex);
                    if (set2 != null && set2.params.size() > 0 && set2.appearanceIndex >= 0) {
                        final SLAvatarParams.AvatarParam avatarParam2 = set2.params.get(0);
                        final int round2 = Math.round((wearableParam.paramValue - avatarParam2.minValue) * 255.0f / (avatarParam2.maxValue - avatarParam2.minValue));
                        int n2;
                        if (round2 < 0) {
                            n2 = 0;
                        }
                        else if ((n2 = round2) > 255) {
                            n2 = 255;
                        }
                        array[set2.appearanceIndex] = n2;
                        switch (set2.id) {
                            default: {
                                continue;
                            }
                            case 33: {
                                this.agentSizeVPHeight = wearableParam.paramValue;
                                continue;
                            }
                            case 198: {
                                this.agentSizeVPHeelHeight = wearableParam.paramValue;
                                continue;
                            }
                            case 503: {
                                this.agentSizeVPPlatformHeight = wearableParam.paramValue;
                                continue;
                            }
                            case 682: {
                                this.agentSizeVPHeadSize = wearableParam.paramValue;
                                continue;
                            }
                            case 692: {
                                this.agentSizeVPLegLength = wearableParam.paramValue;
                                continue;
                            }
                            case 756: {
                                this.agentSizeVPNeckLength = wearableParam.paramValue;
                                continue;
                            }
                            case 842: {
                                this.agentSizeVPHipLength = wearableParam.paramValue;
                                continue;
                            }
                        }
                    }
                }
            }
        }
        this.agentSizeKnown = true;
        return array;
    }
    
    private ImmutableList<WornItem> getWornItems() {
        final ImmutableList.Builder<WornItem> builder = ImmutableList.builder();
        for (final Table.Cell<R, C, SLWearable> cell : this.wornWearables.cellSet()) {
            final SLWearable slWearable = cell.getValue();
            if (slWearable != null) {
                builder.add(new WornItem((SLWearableType)cell.getRowKey(), 0, (UUID)cell.getColumnKey(), slWearable.getName(), 0, false));
            }
        }
        if (this.parcelInfo != null) {
            final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                        builder.add(new WornItem(null, slObjectInfo.attachmentID, slObjectInfo.getId(), slObjectInfo.getName(), slObjectInfo.localID, slObjectInfo.isTouchable()));
                    }
                }
                catch (final NoSuchElementException ex) {
                    Debug.Warning(ex);
                }
            }
        }
        return builder.build();
    }
    
    private boolean isItemWorn(final SLInventoryEntry slInventoryEntry, final boolean b) {
        return slInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, b) != null;
    }
    
    private void onCofFolderEntry(final InventoryEntryList list) {
        if (list != null) {
            for (final SLInventoryEntry slInventoryEntry : list) {
                if (slInventoryEntry != null && slInventoryEntry.isFolder && slInventoryEntry.typeDefault == 46) {
                    this.cofFolderUUID.set(slInventoryEntry.uuid);
                    this.findCofFolder.unsubscribe();
                    this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(slInventoryEntry.uuid, null, true, true, false, null));
                    break;
                }
            }
        }
    }
    
    private void onCurrentOutfitFolder(final InventoryEntryList list) {
        if (list != null) {
            final SLInventoryEntry folder = list.getFolder();
            if (folder != null && Objects.equal(folder.sessionID, this.agentCircuit.circuitInfo.sessionID)) {
                Debug.Log("AvatarAppearance: COF has been fetched from inventory.");
                this.UpdateWearableNames();
                this.cofReady = true;
                this.UpdateCurrentOutfitLink(list);
                this.ProcessMultiLayer();
                this.UpdateCOFContents();
                this.RequestServerRebake();
            }
        }
    }
    
    private void startBaking() {
        final BakeProcess bakeProcess = this.bakeProcess;
        if (bakeProcess != null) {
            bakeProcess.cancel();
        }
        this.bakeProcess = new BakeProcess(this.wornWearables, this, this.agentCircuit.getModules().textureUploader, this.eventBus);
    }
    
    private void updateIfWearablesReady() {
        if (this.areWearablesReady()) {
            this.SendAvatarSetAppearance();
            if (this.needUpdateAppearance) {
                if (this.caps.getCapability(SLCaps.SLCapability.UpdateAvatarAppearance) == null) {
                    this.startBaking();
                }
            }
            else {
                this.UpdateCOFContents();
            }
        }
    }
    
    public void AttachInventoryItem(final SLInventoryEntry slInventoryEntry, final int n, final boolean b) {
        InventoryDB database = null;
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry resolveLink = slInventoryEntry;
        if (database != null) {
            resolveLink = database.resolveLink(slInventoryEntry);
        }
        if (resolveLink == null) {
            return;
        }
        if (resolveLink.assetType == SLAssetType.AT_CLOTHING.getTypeCode() || resolveLink.assetType == SLAssetType.AT_BODYPART.getTypeCode()) {
            this.WearItem(resolveLink, b);
            return;
        }
        Debug.Printf("Outfits: Attaching inventory item %s", resolveLink.uuid.toString());
        final Map map = this.wantedAttachments.get();
        int n2;
        if (map == null || (map.containsKey(resolveLink.uuid) ^ true)) {
            final HashMap hashMap = new HashMap();
            if (map != null) {
                hashMap.putAll(map);
            }
            hashMap.put(resolveLink.uuid, resolveLink.name);
            this.wantedAttachments.set((Map<UUID, String>)ImmutableMap.copyOf((Map<?, ?>)hashMap));
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        final RezSingleAttachmentFromInv rezSingleAttachmentFromInv = new RezSingleAttachmentFromInv();
        rezSingleAttachmentFromInv.AgentData_Field.AgentID = this.circuitInfo.agentID;
        rezSingleAttachmentFromInv.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        int attachmentPt = n;
        if (!b) {
            attachmentPt = (n | 0x80);
        }
        rezSingleAttachmentFromInv.ObjectData_Field.ItemID = resolveLink.uuid;
        rezSingleAttachmentFromInv.ObjectData_Field.OwnerID = resolveLink.ownerUUID;
        rezSingleAttachmentFromInv.ObjectData_Field.AttachmentPt = attachmentPt;
        rezSingleAttachmentFromInv.ObjectData_Field.ItemFlags = resolveLink.flags;
        rezSingleAttachmentFromInv.ObjectData_Field.GroupMask = resolveLink.groupMask;
        rezSingleAttachmentFromInv.ObjectData_Field.EveryoneMask = resolveLink.everyoneMask;
        rezSingleAttachmentFromInv.ObjectData_Field.NextOwnerMask = resolveLink.nextOwnerMask;
        rezSingleAttachmentFromInv.ObjectData_Field.Name = SLMessage.stringToVariableOEM(resolveLink.name);
        rezSingleAttachmentFromInv.ObjectData_Field.Description = SLMessage.stringToVariableOEM(resolveLink.description);
        rezSingleAttachmentFromInv.isReliable = true;
        this.SendMessage(rezSingleAttachmentFromInv);
        if (n2 != 0) {
            this.needUpdateCOF.set(true);
            this.UpdateCOFContents();
        }
    }
    
    public void ChangeOutfit(final List<SLInventoryEntry> list, final boolean b, SLInventoryEntry resolveLink) {
        InventoryDB database;
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        else {
            database = null;
        }
        final Map m = this.wantedAttachments.get();
        HashMap hashMap;
        if (m != null) {
            hashMap = new HashMap(m);
        }
        else {
            hashMap = new HashMap();
        }
        int n;
        if (b) {
            n = 1;
            hashMap.clear();
        }
        else {
            n = 0;
        }
        if (resolveLink != null) {
            if (b) {
                if (this.wantedOutfitFolder == null) {
                    this.wantedOutfitFolder = resolveLink;
                    n = 1;
                }
                else if (!this.wantedOutfitFolder.uuid.equals(resolveLink.uuid)) {
                    this.wantedOutfitFolder = resolveLink;
                    n = 1;
                }
            }
        }
        final UUID randomUUID = UUID.randomUUID();
        final ArrayList list2 = new ArrayList();
        for (SLInventoryEntry slInventoryEntry : list) {
            if (database != null) {
                resolveLink = database.resolveLink(slInventoryEntry);
            }
            else {
                resolveLink = slInventoryEntry;
            }
            if (resolveLink != null) {
                slInventoryEntry = resolveLink;
            }
            if (slInventoryEntry != null && (slInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode() || (slInventoryEntry.isLink() && slInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode()))) {
                list2.add(slInventoryEntry);
            }
        }
        RezMultipleAttachmentsFromInv rezMultipleAttachmentsFromInv = new RezMultipleAttachmentsFromInv();
        rezMultipleAttachmentsFromInv.AgentData_Field.AgentID = this.circuitInfo.agentID;
        rezMultipleAttachmentsFromInv.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        rezMultipleAttachmentsFromInv.HeaderData_Field.CompoundMsgID = randomUUID;
        rezMultipleAttachmentsFromInv.HeaderData_Field.TotalObjects = list2.size();
        rezMultipleAttachmentsFromInv.HeaderData_Field.FirstDetachAll = b;
        Debug.Printf("Wearing: totalAttachments %d", list2.size());
        final Iterator iterator2 = list2.iterator();
        int n2 = n;
        while (iterator2.hasNext()) {
            final SLInventoryEntry slInventoryEntry2 = (SLInventoryEntry)iterator2.next();
            if (rezMultipleAttachmentsFromInv == null) {
                rezMultipleAttachmentsFromInv = new RezMultipleAttachmentsFromInv();
                rezMultipleAttachmentsFromInv.AgentData_Field.AgentID = this.circuitInfo.agentID;
                rezMultipleAttachmentsFromInv.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                rezMultipleAttachmentsFromInv.HeaderData_Field.CompoundMsgID = randomUUID;
                rezMultipleAttachmentsFromInv.HeaderData_Field.TotalObjects = list2.size();
                rezMultipleAttachmentsFromInv.HeaderData_Field.FirstDetachAll = b;
            }
            final RezMultipleAttachmentsFromInv.ObjectData e = new RezMultipleAttachmentsFromInv.ObjectData();
            final UUID uuid = slInventoryEntry2.uuid;
            Debug.Printf("Wearing: entry '%s' actualUUID %s", slInventoryEntry2.name, uuid);
            hashMap.put(uuid, slInventoryEntry2.name);
            e.ItemID = uuid;
            e.OwnerID = slInventoryEntry2.ownerUUID;
            e.AttachmentPt = 128;
            e.ItemFlags = slInventoryEntry2.flags;
            e.GroupMask = slInventoryEntry2.groupMask;
            e.EveryoneMask = slInventoryEntry2.everyoneMask;
            e.NextOwnerMask = slInventoryEntry2.nextOwnerMask;
            e.Name = SLMessage.stringToVariableOEM(slInventoryEntry2.name);
            e.Description = SLMessage.stringToVariableOEM(slInventoryEntry2.description);
            rezMultipleAttachmentsFromInv.ObjectData_Fields.add(e);
            if (rezMultipleAttachmentsFromInv.ObjectData_Fields.size() >= 4) {
                rezMultipleAttachmentsFromInv.isReliable = true;
                this.SendMessage(rezMultipleAttachmentsFromInv);
                rezMultipleAttachmentsFromInv = null;
            }
            n2 = 1;
        }
        if (rezMultipleAttachmentsFromInv != null) {
            rezMultipleAttachmentsFromInv.isReliable = true;
            this.SendMessage(rezMultipleAttachmentsFromInv);
        }
        final RLVController rlvController = this.agentCircuit.getModules().rlvController;
        final HashSet set = new HashSet();
        final HashBasedTable<Object, Object, Object> create = HashBasedTable.create((Table<?, ?, ?>)this.wornWearables);
        final Iterator<Object> iterator3 = list.iterator();
        int n3 = 0;
    Label_0949_Outer:
        while (iterator3.hasNext()) {
            SLInventoryEntry resolveLink2 = iterator3.next();
            if (database != null) {
                resolveLink2 = database.resolveLink(resolveLink2);
            }
            int n4 = n3;
            while (true) {
                Label_1439: {
                    if (resolveLink2 == null) {
                        break Label_1439;
                    }
                    if (resolveLink2.assetType != SLAssetType.AT_BODYPART.getTypeCode() && resolveLink2.assetType != SLAssetType.AT_CLOTHING.getTypeCode()) {
                        continue Label_0949_Outer;
                    }
                    final SLWearableType byCode = SLWearableType.getByCode(resolveLink2.flags & 0xFF);
                    n4 = n3;
                    if (byCode == null) {
                        break Label_1439;
                    }
                    int n5 = 0;
                    Label_0792: {
                        if (!rlvController.canWearItem(byCode)) {
                            n5 = 0;
                        }
                        else if (byCode.isBodyPart()) {
                            if (rlvController.canTakeItemOff(byCode) ^ true) {
                                final Iterator iterator4 = create.row(byCode).keySet().iterator();
                                boolean b2 = false;
                                while (iterator4.hasNext()) {
                                    if (!((UUID)iterator4.next()).equals(resolveLink2.assetUUID)) {
                                        b2 = true;
                                    }
                                }
                                if (b2) {
                                    n5 = 0;
                                    break Label_0792;
                                }
                            }
                            n5 = 1;
                        }
                        else {
                            n5 = 1;
                        }
                    }
                    n4 = n3;
                    if (n5 == 0) {
                        break Label_1439;
                    }
                    set.add(resolveLink2.assetUUID);
                    n4 = n3;
                    if (create.contains(byCode, resolveLink2.assetUUID)) {
                        break Label_1439;
                    }
                    this.addWearable((Table<SLWearableType, UUID, SLWearable>)create, byCode, resolveLink2.uuid, resolveLink2.assetUUID, resolveLink2.name);
                    n4 = 1;
                    if (!byCode.isBodyPart()) {
                        break Label_1439;
                    }
                    final HashSet set2 = new HashSet();
                    for (final UUID uuid2 : create.row(byCode).keySet()) {
                        if (!uuid2.equals(resolveLink2.assetUUID)) {
                            set2.add(uuid2);
                        }
                    }
                    final Iterator iterator6 = set2.iterator();
                    while (true) {
                        n4 = n4;
                        if (!iterator6.hasNext()) {
                            break Label_1439;
                        }
                        final UUID uuid3 = (UUID)iterator6.next();
                        if (create.row(byCode).size() <= 1) {
                            continue Label_0949_Outer;
                        }
                        final SLWearable slWearable = (SLWearable)create.remove(byCode, uuid3);
                        if (slWearable == null) {
                            continue Label_0949_Outer;
                        }
                        slWearable.dispose();
                    }
                    continue Label_0949_Outer;
                }
                n3 = n4;
                continue;
            }
        }
        int n7;
        if (b) {
            final SLWearableType[] values = SLWearableType.values();
            final int length = values.length;
            int n6 = 0;
            while (true) {
                n7 = n3;
                if (n6 >= length) {
                    break;
                }
                final SLWearableType slWearableType = values[n6];
                int n8;
                if (slWearableType.isBodyPart()) {
                    n8 = n3;
                }
                else {
                    n8 = n3;
                    if (rlvController.canTakeItemOff(slWearableType)) {
                        final Map row = create.row(slWearableType);
                        final HashSet set3 = new HashSet();
                        for (final UUID uuid4 : row.keySet()) {
                            if (!set.contains(uuid4)) {
                                set3.add(uuid4);
                            }
                        }
                        final Iterator iterator8 = set3.iterator();
                        while (iterator8.hasNext()) {
                            final SLWearable slWearable2 = (SLWearable)row.remove(iterator8.next());
                            if (slWearable2 != null) {
                                slWearable2.dispose();
                            }
                            n3 = 1;
                        }
                        n8 = n3;
                    }
                }
                ++n6;
                n3 = n8;
            }
        }
        else {
            n7 = n3;
        }
        if (n7 != 0) {
            this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.copyOf((Table<?, ?, ?>)create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (n2 != 0) {
            this.wantedAttachments.set((Map<UUID, String>)ImmutableMap.copyOf((Map<?, ?>)hashMap));
        }
        if (n7 != 0) {
            this.SendAgentIsNowWearing();
            this.ForceUpdateAppearance(false);
            n2 = 0;
        }
        if (n2 != 0) {
            this.needUpdateCOF.set(true);
            this.UpdateCOFContents();
        }
    }
    
    public void DetachInventoryItem(final SLInventoryEntry slInventoryEntry) {
        if (this.canDetachItem(slInventoryEntry)) {
            final UUID uuid = slInventoryEntry.uuid;
            UUID assetUUID;
            if (slInventoryEntry.isLink()) {
                assetUUID = slInventoryEntry.assetUUID;
            }
            else {
                assetUUID = uuid;
            }
            Debug.Log("Outfits: Detaching inventory item " + assetUUID);
            final Map m = this.wantedAttachments.get();
            int n;
            if (m != null) {
                if (m.containsKey(assetUUID)) {
                    final HashMap hashMap = new HashMap(m);
                    hashMap.remove(assetUUID);
                    this.wantedAttachments.set((Map<UUID, String>)ImmutableMap.copyOf((Map<?, ?>)hashMap));
                    n = 1;
                }
                else {
                    n = 0;
                }
            }
            else {
                n = 0;
            }
            final DetachAttachmentIntoInv detachAttachmentIntoInv = new DetachAttachmentIntoInv();
            detachAttachmentIntoInv.ObjectData_Field.AgentID = this.circuitInfo.agentID;
            detachAttachmentIntoInv.ObjectData_Field.ItemID = assetUUID;
            detachAttachmentIntoInv.isReliable = true;
            this.SendMessage(detachAttachmentIntoInv);
            if (n != 0) {
                this.needUpdateCOF.set(true);
                this.UpdateCOFContents();
            }
        }
    }
    
    public void DetachItem(final WornItem wornItem) {
        if (this.canDetachItem(wornItem)) {
            this.DetachItem(wornItem.objectLocalID);
        }
    }
    
    public void DetachItemFromPoint(final int n) {
        Iterable<Integer> iterable = null;
        final Iterable iterable2 = null;
        Set<Integer> set = null;
        Iterable<Integer> iterable3 = iterable2;
        if (this.parcelInfo != null) {
            final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
            iterable3 = iterable2;
            if (agentAvatar != null) {
            Label_0184_Outer:
                while (true) {
                Label_0184:
                    while (true) {
                        Label_0245: {
                            try {
                                final Iterator<Object> iterator = agentAvatar.treeNode.iterator();
                                while (true) {
                                    iterable = set;
                                    iterable3 = set;
                                    if (iterator.hasNext()) {
                                        iterable = set;
                                        final SLObjectInfo slObjectInfo = iterator.next();
                                        iterable = set;
                                        if (slObjectInfo.attachedToUUID == null) {
                                            continue Label_0184_Outer;
                                        }
                                        iterable = set;
                                        if (!(slObjectInfo.isDead ^ true)) {
                                            continue Label_0184_Outer;
                                        }
                                        iterable = set;
                                        if (slObjectInfo.attachmentID != n) {
                                            continue Label_0184_Outer;
                                        }
                                        iterable = set;
                                        if (!this.agentCircuit.getModules().rlvController.canDetachItem(n, slObjectInfo.getId())) {
                                            break Label_0245;
                                        }
                                        Set<Integer> set2;
                                        if ((set2 = set) == null) {
                                            iterable = set;
                                            set2 = new(java.util.HashSet.class)();
                                            iterable = set;
                                            new HashSet();
                                        }
                                        iterable = set2;
                                        set2.add(slObjectInfo.localID);
                                        set = set2;
                                        continue Label_0184_Outer;
                                        continue Label_0184;
                                    }
                                    break;
                                }
                            }
                            catch (final NoSuchElementException ex) {
                                Debug.Warning(ex);
                                iterable3 = iterable;
                            }
                            break;
                        }
                        continue Label_0184;
                    }
                }
            }
        }
        if (iterable3 != null) {
            final Iterator<Integer> iterator2 = iterable3.iterator();
            while (iterator2.hasNext()) {
                this.DetachItem(iterator2.next());
            }
        }
    }
    
    public void ForceTakeItemOff(final SLWearableType slWearableType) {
        int n;
        if (!this.wornWearables.row(slWearableType).isEmpty()) {
            n = 1;
            final HashBasedTable<Object, Object, Object> create = HashBasedTable.create((Table<?, ?, ?>)this.wornWearables);
            create.rowKeySet().remove(slWearableType);
            this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.copyOf((Table<?, ?, ?>)create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        else {
            n = 0;
        }
        if (n != 0) {
            this.SendAgentIsNowWearing();
            this.ForceUpdateAppearance(false);
        }
    }
    
    @SLMessageHandler
    public void HandleAgentWearablesUpdate(final AgentWearablesUpdate agentWearablesUpdate) {
        Debug.Log("AvatarAppearance: Got AgentWearablesUpdate, " + agentWearablesUpdate.WearableData_Fields.size() + " wearables.");
        final HashSet set = new HashSet();
        final HashBasedTable<Object, Object, Object> create = HashBasedTable.create((Table<?, ?, ?>)this.wornWearables);
        for (final AgentWearablesUpdate.WearableData wearableData : agentWearablesUpdate.WearableData_Fields) {
            Debug.Log("Wearable: type = " + wearableData.WearableType + ", itemID = " + wearableData.ItemID + ", assetID = " + wearableData.AssetID);
            if (wearableData.AssetID.getLeastSignificantBits() != 0L || wearableData.AssetID.getMostSignificantBits() != 0L) {
                set.add(wearableData.AssetID);
                final SLWearableType byCode = SLWearableType.getByCode(wearableData.WearableType);
                if (byCode == null || create.get(byCode, wearableData.AssetID) != null) {
                    continue;
                }
                this.addWearable((Table<SLWearableType, UUID, SLWearable>)create, byCode, wearableData.ItemID, wearableData.AssetID, null);
            }
        }
        Debug.Log("AvatarAppearance: AgentWearablesUpdate: wearing now: " + set.size() + " ids");
        final HashSet set2 = new HashSet();
        for (final UUID uuid : create.columnKeySet()) {
            if (!set.contains(uuid)) {
                set2.add(uuid);
            }
        }
        final Iterator iterator3 = set2.iterator();
        while (iterator3.hasNext()) {
            final Map<Object, Object> column = create.column(iterator3.next());
            final Iterator<Object> iterator4 = column.values().iterator();
            while (iterator4.hasNext()) {
                iterator4.next().dispose();
            }
            column.clear();
        }
        this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.copyOf((Table<?, ?, ?>)create);
        this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
        this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        this.UpdateWearableNames();
        this.legacyAppearanceReady = true;
        this.ProcessMultiLayer();
        this.SendAgentIsNowWearing();
        this.StartUpdatingAppearance();
    }
    
    public void HandleAvatarAppearance(final AvatarAppearance avatarAppearance) {
        if (avatarAppearance.AppearanceData_Fields.size() > 0) {
            this.currentCofAppearanceVersion = ((AvatarAppearance.AppearanceData)avatarAppearance.AppearanceData_Fields.get(0)).CofVersion;
            Debug.Printf("AvatarAppearance: inventory COF %d, last updated COF %d, appearance COF %d", this.currentCofInventoryVersion, this.lastCofUpdatedVersion, this.currentCofAppearanceVersion);
        }
    }
    
    @Override
    public void HandleCircuitReady() {
        int n = 1;
        super.HandleCircuitReady();
        if (this.userManager != null) {
            final UUID rootFolder = this.userManager.getInventoryManager().getRootFolder();
            while (true) {
                Label_0144: {
                    if (rootFolder == null) {
                        break Label_0144;
                    }
                    final SLInventoryEntry specialFolder = this.userManager.getInventoryManager().getDatabase().findSpecialFolder(rootFolder, 46);
                    if (specialFolder == null) {
                        break Label_0144;
                    }
                    Debug.Printf("Found existing COF folder: %s", specialFolder.uuid);
                    this.cofFolderUUID.set(specialFolder.uuid);
                    this.currentOutfitFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(specialFolder.uuid, null, true, true, false, null));
                    if (n == 0) {
                        Debug.Printf("Existing COF folder not found, requesting.", new Object[0]);
                        this.findCofFolder.subscribe(this.userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.findFolderWithType(null, 46));
                        return;
                    }
                    return;
                }
                n = 0;
                continue;
            }
        }
    }
    
    @Override
    public void HandleCloseCircuit() {
        this.findCofFolder.unsubscribe();
        this.currentOutfitFolder.unsubscribe();
        if (this.userManager != null) {
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, (ImmutableMap<UUID, String>)null);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, (Table<SLWearableType, UUID, SLWearable>)null);
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
    
    public void OnMyAvatarCreated(final SLObjectAvatarInfo slObjectAvatarInfo) {
        if (this.agentVisualParams != null) {
            slObjectAvatarInfo.ApplyAvatarVisualParams(this.agentVisualParams);
        }
    }
    
    public void SendAgentWearablesRequest() {
        final AgentWearablesRequest agentWearablesRequest = new AgentWearablesRequest();
        agentWearablesRequest.AgentData_Field.AgentID = this.circuitInfo.agentID;
        agentWearablesRequest.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        agentWearablesRequest.isReliable = true;
        this.SendMessage(agentWearablesRequest);
    }
    
    public void TakeItemOff(final SLInventoryEntry slInventoryEntry) {
        InventoryDB database = null;
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry resolveLink = slInventoryEntry;
        if (database != null) {
            resolveLink = database.resolveLink(slInventoryEntry);
        }
        if (resolveLink != null) {
            this.TakeItemOff(resolveLink.assetUUID);
        }
    }
    
    public void TakeItemOff(final UUID uuid) {
        final RLVController rlvController = this.agentCircuit.getModules().rlvController;
        final HashBasedTable<Object, Object, Object> create = HashBasedTable.create((Table<?, ?, ?>)this.wornWearables);
        final SLWearableType[] values = SLWearableType.values();
        final int length = values.length;
        int i = 0;
        boolean b = false;
        while (i < length) {
            final SLWearableType slWearableType = values[i];
            if (rlvController.canTakeItemOff(slWearableType)) {
                final SLWearable slWearable = create.remove(slWearableType, uuid);
                if (slWearable != null) {
                    slWearable.dispose();
                    create.columnKeySet().remove(uuid);
                    b = true;
                }
            }
            ++i;
        }
        if (b) {
            this.wornWearables = (Table<SLWearableType, UUID, SLWearable>)ImmutableTable.copyOf((Table<?, ?, ?>)create);
            this.userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, this.wornWearables);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
            this.SendAgentIsNowWearing();
            this.ForceUpdateAppearance(false);
        }
    }
    
    public void UpdateMyAttachments() {
        final HashMap hashMap = new HashMap();
        if (this.parcelInfo != null) {
            final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                        if (slObjectInfo.attachedToUUID != null && (slObjectInfo.isDead ^ true)) {
                            hashMap.put(slObjectInfo.attachedToUUID, Strings.nullToEmpty(slObjectInfo.getName()));
                        }
                    }
                }
                catch (final NoSuchElementException ex) {
                    ex.printStackTrace();
                }
            }
        }
        final ImmutableMap<Object, Object> copy = (ImmutableMap<Object, Object>)ImmutableMap.copyOf((Map<? extends UUID, ? extends String>)hashMap);
        if (!this.wornAttachments.equals(copy)) {
            Debug.Log("AvatarAppearance: attachments changed.");
            this.wornAttachments = (ImmutableMap<UUID, String>)copy;
            this.userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, this.wornAttachments);
            this.userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
            this.userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
    }
    
    public void WearItem(final SLInventoryEntry slInventoryEntry, final boolean b) {
        InventoryDB database = null;
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        if (database != null) {
            this.WearItemList(database, ImmutableList.of(slInventoryEntry), b);
            this.SendAgentIsNowWearing();
            this.ForceUpdateAppearance(false);
        }
    }
    
    public boolean canDetachItem(final SLInventoryEntry slInventoryEntry) {
        if (slInventoryEntry.assetType == SLAssetType.AT_LINK.getTypeCode()) {
            if (slInventoryEntry.invType == SLInventoryType.IT_WEARABLE.getTypeCode()) {
                return true;
            }
            if (slInventoryEntry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && this.wornAttachments.containsKey(slInventoryEntry.assetUUID)) {
                return this.canDetachItem(slInventoryEntry.assetUUID);
            }
        }
        else {
            if (slInventoryEntry.assetType == SLAssetType.AT_BODYPART.getTypeCode() || slInventoryEntry.assetType == SLAssetType.AT_CLOTHING.getTypeCode()) {
                return true;
            }
            if (slInventoryEntry.assetType == SLAssetType.AT_OBJECT.getTypeCode()) {
                return (!this.wornAttachments.containsKey(slInventoryEntry.uuid) || this.canDetachItem(slInventoryEntry.uuid)) && (!this.wornAttachments.containsKey(slInventoryEntry.assetUUID) || this.canDetachItem(slInventoryEntry.assetUUID));
            }
        }
        return false;
    }
    
    public boolean canDetachItem(final WornItem wornItem) {
        return this.agentCircuit.getModules().rlvController.canDetachItem(wornItem.getAttachedTo(), wornItem.itemID());
    }
    
    public boolean canTakeItemOff(final SLWearableType slWearableType) {
        return this.agentCircuit.getModules().rlvController.canTakeItemOff(slWearableType);
    }
    
    public boolean canTakeItemOff(final SLInventoryEntry slInventoryEntry) {
        final Object whatIsItemWornOn = slInventoryEntry.whatIsItemWornOn(this.wornAttachments, this.wornWearables, false);
        if (whatIsItemWornOn != null) {
            final RLVController rlvController = this.agentCircuit.getModules().rlvController;
            if (whatIsItemWornOn instanceof SLWearableType) {
                return rlvController.canTakeItemOff((SLWearableType)whatIsItemWornOn);
            }
        }
        return true;
    }
    
    public boolean canWearItem(final SLInventoryEntry slInventoryEntry) {
        InventoryDB database = null;
        if (this.userManager != null) {
            database = this.userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry resolveLink = slInventoryEntry;
        if (database != null) {
            resolveLink = database.resolveLink(slInventoryEntry);
        }
        if (resolveLink == null) {
            return false;
        }
        final SLWearableType byCode = SLWearableType.getByCode(resolveLink.flags & 0xFF);
        return byCode == null || this.canWearItem(byCode);
    }
    
    public void finishBaking(final BakeProcess bakeProcess, final SLTextureEntry agentBakedTextures) {
        if (agentBakedTextures != null) {
            this.agentBakedTextures = agentBakedTextures;
            this.SendAvatarSetAppearance();
        }
        if (this.bakeProcess == bakeProcess) {
            this.bakeProcess = null;
        }
    }
    
    public UUID getAttachmentUUID(final int n) {
        if (this.parcelInfo != null) {
            final SLObjectAvatarInfo agentAvatar = this.parcelInfo.getAgentAvatar();
            if (agentAvatar != null) {
                try {
                    for (final SLObjectInfo slObjectInfo : agentAvatar.treeNode) {
                        if (slObjectInfo.attachedToUUID != null && (slObjectInfo.isDead ^ true) && slObjectInfo.attachmentID == n) {
                            return slObjectInfo.getId();
                        }
                    }
                    return null;
                }
                catch (final NoSuchElementException ex) {
                    Debug.Warning(ex);
                }
            }
        }
        return null;
    }
    
    public boolean hasWornWearable(final SLWearableType slWearableType) {
        return this.wornWearables.containsRow(slWearableType);
    }
    
    public boolean isItemWorn(final SLInventoryEntry slInventoryEntry) {
        return this.isItemWorn(slInventoryEntry, false);
    }
    
    @Override
    public void onWearableStatusChanged(final SLWearable slWearable) {
        this.updateIfWearablesReady();
    }
    
    public static class WornItem
    {
        private final int attachedTo;
        private final boolean isTouchable;
        private final UUID itemID;
        private final String name;
        private final int objectLocalID;
        private final SLWearableType wornOn;
        
        WornItem(final SLWearableType wornOn, final int attachedTo, final UUID itemID, final String name, final int objectLocalID, final boolean isTouchable) {
            this.wornOn = wornOn;
            this.attachedTo = attachedTo;
            this.itemID = itemID;
            this.name = name;
            this.objectLocalID = objectLocalID;
            this.isTouchable = isTouchable;
        }
        
        int getAttachedTo() {
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
}
