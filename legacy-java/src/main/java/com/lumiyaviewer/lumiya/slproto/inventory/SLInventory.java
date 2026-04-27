package com.lumiyaviewer.lumiya.slproto.inventory;

import android.annotation.SuppressLint;
import android.database.Cursor;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryDBObject;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryNewContentsEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryUpdatedEvent;
import com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.messages.CopyInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.CreateInventoryFolder;
import com.lumiyaviewer.lumiya.slproto.messages.CreateInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents;
import com.lumiyaviewer.lumiya.slproto.messages.LinkInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.MoveTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryFolder;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryObjects;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateTaskInventory;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/* loaded from: classes.dex */
public class SLInventory extends SLModule {

    @SuppressLint({"UseSparseArrays"})
    private Map<Integer, OnInventoryCallbackListener> callbacks;
    private final SLCaps caps;
    private final InventoryDB db;
    private final Executor dbExecutor;
    private ExecutorService executor;

    @Nullable
    private final String fetchCap;
    private final AtomicBoolean fetchEntireInventoryRequested;
    private final Map<UUID, SLInventoryFetchRequest> fetchRequests;
    private final ResultHandler<UUID, SLInventoryEntry> folderEntryResultHandler;
    private final RequestHandler<UUID> folderLoadingRequestHandler;
    private final ResultHandler<UUID, Boolean> folderLoadingResultHandler;
    private final RequestHandler<UUID> folderRequestHandler;
    private AtomicInteger nextCallbackID;
    private final SubscriptionData<UUID, SLInventoryEntry> nextFolderSubscription;
    private SLMessageEventListener reloadEvent;
    public SLInventoryEntry rootFolder;
    private boolean rootFolderFetchNeeded;
    private final SubscriptionData<UUID, SLInventoryEntry> rootFolderSubscription;
    private final ResultHandler<SubscriptionSingleKey, Boolean> seachRunningResultHandler;
    private final ResultHandler<SubscriptionSingleKey, Boolean> searchProcessResultHandler;
    private final RequestHandler<SubscriptionSingleKey> searchRequestHandler;
    private final RequestHandler<SubscriptionSingleKey> searchRunningRequestHandler;
    private Map<UUID, SLInventoryUDPFetchRequest> udpFetchPendingRequests;
    private Map<UUID, SLInventoryUDPFetchRequest> udpFetchRequests;
    private final UserManager userManager;

    /* loaded from: classes.dex */
    public static class InventoryFetchException extends IOException {
        public InventoryFetchException(String str) {
            super(str);
        }
    }

    /* loaded from: classes.dex */
    public static class NoInventoryItemException extends Exception {
        private static final long serialVersionUID = 1;

        public NoInventoryItemException(long j) {
            super("Inventory item " + j + " not found");
        }

        public NoInventoryItemException(UUID uuid) {
            super("Inventory item " + uuid.toString() + " not found");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnInventoryCallbackListener {
        void onInventoryCallback(SLInventoryEntry sLInventoryEntry);
    }

    /* loaded from: classes.dex */
    public interface OnNotecardUpdatedListener {
        void onNotecardUpdated(SLInventoryEntry sLInventoryEntry, @Nullable String str);
    }

    public SLInventory(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit);
        this.rootFolder = null;
        this.executor = null;
        this.nextCallbackID = new AtomicInteger(1);
        this.rootFolderFetchNeeded = false;
        this.callbacks = Collections.synchronizedMap(new HashMap());
        this.udpFetchRequests = Collections.synchronizedMap(new HashMap());
        this.udpFetchPendingRequests = Collections.synchronizedMap(new HashMap());
        this.fetchRequests = new ConcurrentHashMap();
        this.fetchEntireInventoryRequested = new AtomicBoolean(false);
        this.folderLoadingRequestHandler = new SimpleRequestHandler<UUID>() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.1
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull UUID uuid) {
                SLInventory.this.updateFolderLoadingStatus(uuid);
            }
        };
        this.searchRunningRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.2
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.updateSearchRunningStatus();
            }
        };
        this.folderRequestHandler = new RequestHandler<UUID>() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.3
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull UUID uuid) {
                try {
                    Debug.Printf("Inventory: folderRequestHandler: folderId = '%s'", uuid);
                    SLInventoryFetchRequest sLInventoryHTTPFetchRequest = SLInventory.this.fetchCap != null ? new SLInventoryHTTPFetchRequest(SLInventory.this, uuid, SLInventory.this.fetchCap) : new SLInventoryUDPFetchRequest(SLInventory.this, uuid);
                    SLInventory.this.fetchRequests.put(uuid, sLInventoryHTTPFetchRequest);
                    SLInventory.this.updateFolderLoadingStatus(uuid);
                    sLInventoryHTTPFetchRequest.start();
                } catch (NoInventoryItemException e) {
                    Debug.Warning(e);
                }
            }

            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequestCancelled(@Nonnull UUID uuid) {
                SLInventoryFetchRequest sLInventoryFetchRequest = (SLInventoryFetchRequest) SLInventory.this.fetchRequests.remove(uuid);
                SLInventory.this.updateFolderLoadingStatus(uuid);
                if (sLInventoryFetchRequest != null) {
                    sLInventoryFetchRequest.cancel();
                }
            }
        };
        this.searchRequestHandler = new RequestHandler<SubscriptionSingleKey>() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.4
            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequest(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.fetchEntireInventoryRequested.set(true);
                SLInventory.this.fetchNextFolder();
            }

            @Override // com.lumiyaviewer.lumiya.react.RequestHandler
            public void onRequestCancelled(@Nonnull SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.fetchEntireInventoryRequested.set(false);
                SLInventory.this.updateSearchRunningStatus();
            }
        };
        this.reloadEvent = new SLMessageEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.5
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.eventBus.publish(new SLInventoryUpdatedEvent(null, null, true));
            }

            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageTimeout(SLMessage sLMessage) {
            }
        };
        this.userManager = UserManager.getUserManager(sLAgentCircuit.getAgentUUID());
        this.db = this.userManager != null ? this.userManager.getInventoryManager().getDatabase() : null;
        this.caps = sLCaps;
        this.fetchCap = sLCaps.getCapability(SLCaps.SLCapability.FetchInventoryDescendents2);
        this.dbExecutor = this.userManager != null ? this.userManager.getInventoryManager().getExecutor() : null;
        if (this.userManager != null) {
            this.userManager.getInventoryManager().setCurrentSessionID(sLAgentCircuit.getAuthReply().sessionID);
            this.userManager.getInventoryManager().setRootFolder(sLAgentCircuit.getAuthReply().inventoryRoot);
        }
        try {
            if (this.db != null) {
                Debug.Printf("Inventory: creating root folder with folderUUID %s", sLAgentCircuit.getAuthReply().inventoryRoot.toString());
                this.rootFolder = this.db.findEntryOrCreate(sLAgentCircuit.getAuthReply().inventoryRoot);
                if (this.rootFolder.getId() == 0) {
                    this.rootFolder.name = "Inventory";
                    this.rootFolder.isFolder = true;
                    this.rootFolder.parent_id = 0L;
                    this.rootFolder.agentUUID = sLAgentCircuit.circuitInfo.agentID;
                    this.db.saveEntry(this.rootFolder);
                    this.rootFolderFetchNeeded = true;
                }
            }
            Debug.Printf("Inventory: ready.", new Object[0]);
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e);
        }
        this.folderEntryResultHandler = this.userManager != null ? this.userManager.getInventoryManager().getFolderRequestSource().attachRequestHandler(this.folderRequestHandler) : null;
        if (this.userManager == null) {
            this.folderLoadingResultHandler = null;
            this.searchProcessResultHandler = null;
            this.nextFolderSubscription = null;
            this.seachRunningResultHandler = null;
            this.rootFolderSubscription = null;
            return;
        }
        this.folderLoadingResultHandler = this.userManager.getInventoryManager().getFolderLoadingRequestSource().attachRequestHandler(new AsyncRequestHandler(this.dbExecutor, this.folderLoadingRequestHandler));
        this.seachRunningResultHandler = this.userManager.getInventoryManager().getSearchRunning().attachRequestHandler(new AsyncRequestHandler(this.dbExecutor, this.searchRunningRequestHandler));
        this.searchProcessResultHandler = this.userManager.getInventoryManager().getSearchProcessRequestSource().attachRequestHandler(new AsyncRequestHandler(this.dbExecutor, this.searchRequestHandler));
        this.nextFolderSubscription = new SubscriptionData<>(this.dbExecutor, new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs
            private final /* synthetic */ void $m$0(Object obj) {
                ((SLInventory) this).m184com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref0((SLInventoryEntry) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        }, new Subscription.OnError() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.2
            private final /* synthetic */ void $m$0(Throwable th) {
                ((SLInventory) this).m185com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref1(th);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnError
            public final void onError(Throwable th) {
                $m$0(th);
            }
        });
        if (this.rootFolderFetchNeeded) {
            this.rootFolderSubscription = new SubscriptionData<>(this.dbExecutor, new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.1
                private final /* synthetic */ void $m$0(Object obj) {
                    ((SLInventory) this).m186com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref2((SLInventoryEntry) obj);
                }

                @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
                public final void onData(Object obj) {
                    $m$0(obj);
                }
            });
        } else {
            this.rootFolderSubscription = null;
        }
    }

    private void DoCreateInventoryItem(UUID uuid, int i, int i2, String str, String str2, OnInventoryCallbackListener onInventoryCallbackListener) {
        CreateInventoryItem createInventoryItem = new CreateInventoryItem();
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        createInventoryItem.InventoryBlock_Field.CallbackID = getNextCallbackID();
        createInventoryItem.InventoryBlock_Field.FolderID = uuid;
        createInventoryItem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE;
        createInventoryItem.InventoryBlock_Field.Type = i;
        createInventoryItem.InventoryBlock_Field.InvType = i2;
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str);
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2);
        this.callbacks.put(Integer.valueOf(createInventoryItem.InventoryBlock_Field.CallbackID), onInventoryCallbackListener);
        createInventoryItem.isReliable = true;
        SendMessage(createInventoryItem);
    }

    private void DoUpdateInventoryItem(final SLInventoryEntry sLInventoryEntry, final OnInventoryCallbackListener onInventoryCallbackListener) {
        UpdateInventoryItem updateInventoryItem = new UpdateInventoryItem();
        updateInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateInventoryItem.AgentData_Field.TransactionID = UUID.randomUUID();
        UpdateInventoryItem.InventoryData inventoryData = new UpdateInventoryItem.InventoryData();
        inventoryData.ItemID = sLInventoryEntry.uuid;
        inventoryData.FolderID = sLInventoryEntry.parentUUID;
        inventoryData.CallbackID = 0;
        inventoryData.CreatorID = sLInventoryEntry.creatorUUID;
        inventoryData.OwnerID = sLInventoryEntry.ownerUUID;
        inventoryData.GroupID = sLInventoryEntry.groupUUID;
        inventoryData.BaseMask = sLInventoryEntry.baseMask;
        inventoryData.OwnerMask = sLInventoryEntry.ownerMask;
        inventoryData.GroupMask = sLInventoryEntry.groupMask;
        inventoryData.EveryoneMask = sLInventoryEntry.everyoneMask;
        inventoryData.NextOwnerMask = sLInventoryEntry.nextOwnerMask;
        inventoryData.GroupOwned = sLInventoryEntry.isGroupOwned;
        inventoryData.TransactionID = new UUID(0L, 0L);
        inventoryData.Type = sLInventoryEntry.assetType;
        inventoryData.InvType = sLInventoryEntry.invType;
        inventoryData.Flags = sLInventoryEntry.flags;
        inventoryData.SaleType = sLInventoryEntry.saleType;
        inventoryData.SalePrice = sLInventoryEntry.salePrice;
        inventoryData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name);
        inventoryData.Description = SLMessage.stringToVariableUTF(sLInventoryEntry.description);
        inventoryData.CreationDate = sLInventoryEntry.creationDate;
        inventoryData.CRC = 0;
        updateInventoryItem.InventoryData_Fields.add(inventoryData);
        updateInventoryItem.isReliable = true;
        Debug.Printf("Update inventory callback %d", Integer.valueOf(inventoryData.CallbackID));
        updateInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.13
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                super.onMessageAcknowledged(sLMessage);
                onInventoryCallbackListener.onInventoryCallback(sLInventoryEntry);
            }
        });
        SendMessage(updateInventoryItem);
    }

    private void DoUpdateTaskInventoryItem(SLInventoryEntry sLInventoryEntry, int i, SLMessageEventListener sLMessageEventListener) {
        UpdateTaskInventory updateTaskInventory = new UpdateTaskInventory();
        updateTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateTaskInventory.UpdateData_Field.LocalID = i;
        updateTaskInventory.UpdateData_Field.Key = 0;
        UpdateTaskInventory.InventoryData inventoryData = updateTaskInventory.InventoryData_Field;
        inventoryData.ItemID = sLInventoryEntry.uuid;
        inventoryData.FolderID = sLInventoryEntry.parentUUID;
        inventoryData.CreatorID = sLInventoryEntry.creatorUUID;
        inventoryData.OwnerID = sLInventoryEntry.ownerUUID;
        inventoryData.GroupID = sLInventoryEntry.groupUUID;
        inventoryData.BaseMask = sLInventoryEntry.baseMask;
        inventoryData.OwnerMask = sLInventoryEntry.ownerMask;
        inventoryData.GroupMask = sLInventoryEntry.groupMask;
        inventoryData.EveryoneMask = sLInventoryEntry.everyoneMask;
        inventoryData.NextOwnerMask = sLInventoryEntry.nextOwnerMask;
        inventoryData.GroupOwned = sLInventoryEntry.isGroupOwned;
        inventoryData.TransactionID = new UUID(0L, 0L);
        inventoryData.Type = sLInventoryEntry.assetType;
        inventoryData.InvType = sLInventoryEntry.invType;
        inventoryData.Flags = sLInventoryEntry.flags;
        inventoryData.SaleType = sLInventoryEntry.saleType;
        inventoryData.SalePrice = sLInventoryEntry.salePrice;
        inventoryData.Name = SLMessage.stringToVariableOEM(sLInventoryEntry.name);
        inventoryData.Description = SLMessage.stringToVariableUTF(sLInventoryEntry.description);
        inventoryData.CreationDate = sLInventoryEntry.creationDate;
        inventoryData.CRC = 0;
        updateTaskInventory.isReliable = true;
        updateTaskInventory.setEventListener(sLMessageEventListener);
        SendMessage(updateTaskInventory);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void MoveTaskInventory(UUID uuid, int i, UUID uuid2) {
        MoveTaskInventory moveTaskInventory = new MoveTaskInventory();
        moveTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moveTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moveTaskInventory.AgentData_Field.FolderID = uuid;
        moveTaskInventory.InventoryData_Field.LocalID = i;
        moveTaskInventory.InventoryData_Field.ItemID = uuid2;
        moveTaskInventory.isReliable = true;
        SendMessage(moveTaskInventory);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void StartUploadingNotecardContents(final SLInventoryEntry sLInventoryEntry, @Nullable final UUID uuid, final boolean z, final byte[] bArr, final OnNotecardUpdatedListener onNotecardUpdatedListener) {
        GenericHTTPExecutor.getInstance().execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.9
            private final /* synthetic */ void $m$0() {
                ((SLInventory) this).m190x8292c7bb((SLInventoryEntry) sLInventoryEntry, (byte[]) bArr, (UUID) uuid, z, (SLInventory.OnNotecardUpdatedListener) onNotecardUpdatedListener);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    @Nullable
    private String UploadNotecardContents(SLInventoryEntry sLInventoryEntry, @Nullable UUID uuid, boolean z, byte[] bArr) {
        String capabilityOrThrow;
        boolean z2;
        int i = 0;
        String str = null;
        try {
            LLSDXMLRequest lLSDXMLRequest = new LLSDXMLRequest();
            if (z) {
                z2 = true;
                capabilityOrThrow = getCaps().getCapabilityOrThrow(uuid == null ? SLCaps.SLCapability.UpdateScriptAgent : SLCaps.SLCapability.UpdateScriptTask);
            } else {
                capabilityOrThrow = getCaps().getCapabilityOrThrow(uuid == null ? SLCaps.SLCapability.UpdateNotecardAgentInventory : SLCaps.SLCapability.UpdateNotecardTaskInventory);
                z2 = false;
            }
            ImmutableMap.Builder builder = ImmutableMap.builder();
            builder.put("item_id", new LLSDUUID(sLInventoryEntry.uuid));
            if (uuid != null) {
                builder.put("task_id", new LLSDUUID(uuid));
            }
            if (z2) {
                builder.put("target", new LLSDString("mono"));
                if (uuid != null) {
                    builder.put("is_script_running", new LLSDInt(1));
                }
            }
            LLSDMap lLSDMap = new LLSDMap(builder.build());
            Debug.Log("Notecard upload request: Initial uploader request: " + lLSDMap.serializeToXML());
            LLSDNode PerformRequest = lLSDXMLRequest.PerformRequest(capabilityOrThrow, lLSDMap);
            Debug.Log("Notecard upload request: Initial uploader reply: " + PerformRequest.serializeToXML());
            String asString = PerformRequest.byKey("uploader").asString();
            if (asString != null) {
                asString = SLCaps.repairURL(capabilityOrThrow, asString);
            }
            Response execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(Strings.nullToEmpty(asString)).post(RequestBody.create(MediaType.parse("application/vnd.ll.notecard"), bArr)).header(HttpHeaders.ACCEPT, "application/llsd+xml").build()).execute();
            if (execute == null) {
                throw new IOException("Null response");
            }
            try {
                if (!execute.isSuccessful()) {
                    throw new IOException("Response error code " + execute.code());
                }
                LLSDNode parseXML = LLSDNode.parseXML(execute.body().byteStream(), null);
                Debug.Log("upload reply: " + parseXML.serializeToXML());
                String asString2 = parseXML.byKey("state").asString();
                UUID asUUID = parseXML.byKey("new_asset").asUUID();
                if (asString2.equals("complete")) {
                    if (uuid == null) {
                        SLInventoryEntry findEntry = this.db.findEntry(sLInventoryEntry.uuid);
                        if (findEntry != null) {
                            findEntry.assetUUID = asUUID;
                            this.db.saveEntry(findEntry);
                        }
                        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
                    }
                    if (parseXML.keyExists("compiled") && !parseXML.byKey("compiled").asBoolean() && parseXML.keyExists("errors")) {
                        LLSDNode byKey = parseXML.byKey("errors");
                        str = "";
                        while (i < byKey.getCount()) {
                            if (i != 0) {
                                str = str + "; ";
                            }
                            String str2 = str + byKey.byIndex(i).asString();
                            i++;
                            str = str2;
                        }
                    }
                }
                return str;
            } finally {
                execute.close();
            }
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e);
            return "Failed to upload inventory asset";
        } catch (SLCaps.NoSuchCapabilityException e2) {
            Debug.Warning(e2);
            return "Failed to upload inventory asset";
        } catch (LLSDException e3) {
            Debug.Warning(e3);
            return "Failed to upload inventory asset";
        } catch (IOException e4) {
            Debug.Warning(e4);
            return "Failed to upload inventory asset";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchNextFolder() {
        if (!this.fetchEntireInventoryRequested.get()) {
            updateSearchRunningStatus();
            return;
        }
        UUID uuid = this.circuitInfo.sessionID;
        try {
            Cursor query = this.db.getDatabase().query(InventoryEntryDBObject.tableName, new String[]{"uuid_high", "uuid_low"}, "isFolder AND (sessionID_high != ? OR sessionID_low != ?)", new String[]{Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())}, null, null, null, "1");
            if (query.moveToFirst()) {
                UUID uuid2 = UUIDPool.getUUID(query.getLong(0), query.getLong(1));
                Debug.Printf("InventorySearch: fetching next folder: %s", uuid2);
                this.nextFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), uuid2);
                updateSearchRunningStatus();
            } else {
                Debug.Printf("InventorySearch: no more folders to fetch", new Object[0]);
                this.searchProcessResultHandler.onResultData(SubscriptionSingleKey.Value, true);
                this.nextFolderSubscription.unsubscribe();
                updateSearchRunningStatus();
            }
            query.close();
        } catch (Exception e) {
            Debug.Printf("InventorySearch: error while fetching folders", new Object[0]);
            Debug.Warning(e);
            this.fetchEntireInventoryRequested.set(false);
            this.searchProcessResultHandler.onResultError(SubscriptionSingleKey.Value, e);
            this.nextFolderSubscription.unsubscribe();
            updateSearchRunningStatus();
        }
    }

    private int getNextCallbackID() {
        return this.nextCallbackID.getAndIncrement();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onNextFolderError, reason: merged with bridge method [inline-methods] */
    public void m185com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref1(Throwable th) {
        fetchNextFolder();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onNextFolderFetched, reason: merged with bridge method [inline-methods] */
    public void m184com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref0(SLInventoryEntry sLInventoryEntry) {
        if (Objects.equal(sLInventoryEntry.sessionID, this.circuitInfo.sessionID)) {
            fetchNextFolder();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onRootFolderFetched, reason: merged with bridge method [inline-methods] */
    public void m186com_lumiyaviewer_lumiya_slproto_inventory_SLInventorymthref2(SLInventoryEntry sLInventoryEntry) {
        this.rootFolderFetchNeeded = false;
        if (this.rootFolderSubscription != null) {
            this.rootFolderSubscription.unsubscribe();
        }
        Debug.Printf("Inventory: Fetched root folder.", new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFolderLoadingStatus(@Nonnull UUID uuid) {
        if (this.folderLoadingResultHandler != null) {
            this.folderLoadingResultHandler.onResultData(uuid, Boolean.valueOf(this.fetchRequests.containsKey(uuid)));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSearchRunningStatus() {
        if (this.searchRunningRequestHandler != null) {
            this.seachRunningResultHandler.onResultData(SubscriptionSingleKey.Value, Boolean.valueOf(this.fetchEntireInventoryRequested.get() ? this.nextFolderSubscription.isSubscribed() : false));
        }
    }

    public Collection<SLInventoryEntry> CollectGiveableItems(SLInventoryEntry sLInventoryEntry) {
        ArrayList arrayList = new ArrayList();
        Cursor query = SLInventoryEntry.query(this.db.getDatabase(), "parent_id = ?", new String[]{Long.toString(sLInventoryEntry.getId())}, (String) null);
        if (query != null) {
            while (query.moveToNext()) {
                SLInventoryEntry sLInventoryEntry2 = new SLInventoryEntry(query);
                if (sLInventoryEntry2.isFolder) {
                    arrayList.add(sLInventoryEntry2);
                    arrayList.addAll(CollectGiveableItems(sLInventoryEntry2));
                } else if ((sLInventoryEntry2.baseMask & sLInventoryEntry2.ownerMask & 8192) != 0) {
                    arrayList.add(sLInventoryEntry2);
                }
            }
            query.close();
        }
        return arrayList;
    }

    public void CopyInventoryFromNotecard(final UUID uuid, final UUID uuid2, final UUID uuid3, @Nullable final Runnable runnable) {
        GenericHTTPExecutor.getInstance().execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.5
            private final /* synthetic */ void $m$0() {
                ((SLInventory) this).m193x829dc330((UUID) uuid, (UUID) uuid2, (UUID) uuid3, (Runnable) runnable);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void CopyInventoryItem(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        CopyInventoryItem copyInventoryItem = new CopyInventoryItem();
        copyInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        copyInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        CopyInventoryItem.InventoryData inventoryData = new CopyInventoryItem.InventoryData();
        inventoryData.CallbackID = getNextCallbackID();
        inventoryData.OldAgentID = sLInventoryEntry.agentUUID;
        inventoryData.OldItemID = sLInventoryEntry.uuid;
        inventoryData.NewFolderID = sLInventoryEntry2.uuid;
        inventoryData.NewName = SLMessage.stringToVariableOEM(sLInventoryEntry.name);
        copyInventoryItem.InventoryData_Fields.add(inventoryData);
        copyInventoryItem.isReliable = true;
        SendMessage(copyInventoryItem);
    }

    public void CopyObjectContents(final String str, final int i, Set<UUID> set, final Function<UUID, Void> function) {
        if (this.rootFolder == null) {
            function.apply(null);
        } else {
            final ImmutableSet copyOf = ImmutableSet.copyOf((Collection) set);
            this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.6
                private final /* synthetic */ void $m$0() {
                    ((SLInventory) this).m194x829e9985((String) str, (ImmutableSet) copyOf, i, (Function) function);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        }
    }

    public void DeleteInventoryItem(final SLInventoryEntry sLInventoryEntry) {
        if (sLInventoryEntry.isFolder) {
            RemoveInventoryFolder removeInventoryFolder = new RemoveInventoryFolder();
            removeInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
            removeInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            RemoveInventoryFolder.FolderData folderData = new RemoveInventoryFolder.FolderData();
            folderData.FolderID = sLInventoryEntry.uuid;
            removeInventoryFolder.FolderData_Fields.add(folderData);
            removeInventoryFolder.isReliable = true;
            removeInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.6
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
                }
            });
            SendMessage(removeInventoryFolder);
            return;
        }
        RemoveInventoryItem removeInventoryItem = new RemoveInventoryItem();
        removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        RemoveInventoryItem.InventoryData inventoryData = new RemoveInventoryItem.InventoryData();
        inventoryData.ItemID = sLInventoryEntry.uuid;
        removeInventoryItem.InventoryData_Fields.add(inventoryData);
        removeInventoryItem.isReliable = true;
        removeInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.7
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
            }
        });
        SendMessage(removeInventoryItem);
    }

    public void DeleteInventoryItemRaw(UUID uuid) {
        RemoveInventoryItem removeInventoryItem = new RemoveInventoryItem();
        removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        RemoveInventoryItem.InventoryData inventoryData = new RemoveInventoryItem.InventoryData();
        inventoryData.ItemID = uuid;
        removeInventoryItem.InventoryData_Fields.add(inventoryData);
        removeInventoryItem.isReliable = true;
        removeInventoryItem.setEventListener(this.reloadEvent);
        SendMessage(removeInventoryItem);
    }

    public void DeleteMultiInventoryItemRaw(final SLInventoryEntry sLInventoryEntry, List<UUID> list) {
        RemoveInventoryObjects removeInventoryObjects = new RemoveInventoryObjects();
        removeInventoryObjects.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeInventoryObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (UUID uuid : list) {
            RemoveInventoryObjects.ItemData itemData = new RemoveInventoryObjects.ItemData();
            itemData.ItemID = uuid;
            removeInventoryObjects.ItemData_Fields.add(itemData);
        }
        removeInventoryObjects.isReliable = true;
        removeInventoryObjects.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.8
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
            }
        });
        SendMessage(removeInventoryObjects);
    }

    public UUID DoCreateNewFolder(final SLInventoryEntry sLInventoryEntry, String str, boolean z, @Nullable final Function<UUID, Void> function) {
        final UUID randomUUID = UUID.randomUUID();
        if (z) {
            try {
                SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(randomUUID);
                findEntryOrCreate.resetId();
                findEntryOrCreate.uuid = randomUUID;
                findEntryOrCreate.parent_id = sLInventoryEntry.getId();
                findEntryOrCreate.parentUUID = sLInventoryEntry.uuid;
                findEntryOrCreate.name = str;
                findEntryOrCreate.description = "";
                findEntryOrCreate.agentUUID = this.circuitInfo.agentID;
                findEntryOrCreate.isFolder = true;
                findEntryOrCreate.typeDefault = -1;
                findEntryOrCreate.version = 0;
                this.db.saveEntry(findEntryOrCreate);
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e);
            }
        }
        Debug.Printf("Inventory: Creating new folder with uuid = %s, parent %s", randomUUID, sLInventoryEntry.uuid);
        CreateInventoryFolder createInventoryFolder = new CreateInventoryFolder();
        createInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
        createInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        createInventoryFolder.FolderData_Field.FolderID = randomUUID;
        createInventoryFolder.FolderData_Field.ParentID = sLInventoryEntry.uuid;
        createInventoryFolder.FolderData_Field.Type = -1;
        createInventoryFolder.FolderData_Field.Name = SLMessage.stringToVariableOEM(str);
        createInventoryFolder.isReliable = true;
        createInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.16
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                Debug.Printf("Inventory: new folder created with uuid = %s, parent %s", randomUUID, sLInventoryEntry.uuid);
                if (SLInventory.this.userManager != null) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
                }
                if (function != null) {
                    function.apply(randomUUID);
                }
            }
        });
        SendMessage(createInventoryFolder);
        return randomUUID;
    }

    public void DoCreateNewLandmark(SLInventoryEntry sLInventoryEntry, String str, String str2) {
        CreateInventoryItem createInventoryItem = new CreateInventoryItem();
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        createInventoryItem.InventoryBlock_Field.CallbackID = getNextCallbackID();
        createInventoryItem.InventoryBlock_Field.FolderID = sLInventoryEntry.uuid;
        createInventoryItem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE;
        createInventoryItem.InventoryBlock_Field.Type = SLAssetType.AT_LANDMARK.getTypeCode();
        createInventoryItem.InventoryBlock_Field.InvType = SLInventoryType.IT_LANDMARK.getTypeCode();
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str);
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2);
        createInventoryItem.isReliable = true;
        SendMessage(createInventoryItem);
    }

    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.BulkUpdateInventory)
    public void HandleBulkUpdateInventory(LLSDNode lLSDNode) {
        Debug.Printf("BulkUpdateInventory: EventQueue event", new Object[0]);
        SLInventoryNewContentsEvent sLInventoryNewContentsEvent = new SLInventoryNewContentsEvent();
        HashSet hashSet = new HashSet();
        try {
            if (lLSDNode.keyExists("FolderData")) {
                LLSDNode byKey = lLSDNode.byKey("FolderData");
                for (int i = 0; i < byKey.getCount(); i++) {
                    LLSDNode byIndex = byKey.byIndex(i);
                    UUID asUUID = byIndex.byKey("FolderID").asUUID();
                    if (asUUID.getLeastSignificantBits() != 0 || asUUID.getMostSignificantBits() != 0) {
                        Debug.Printf("Inventory: BulkUpdateInventory got folder %s", asUUID.toString());
                        OnInventoryCallbackListener onInventoryCallbackListener = null;
                        if (byIndex.keyExists("CallbackID")) {
                            Debug.Printf("Inventory: got callback id %d", Integer.valueOf(byIndex.byKey("CallbackID").asInt()));
                            onInventoryCallbackListener = this.callbacks.remove(Integer.valueOf(byIndex.byKey("CallbackID").asInt()));
                        }
                        SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(asUUID);
                        UUID asUUID2 = byIndex.byKey("ParentID").asUUID();
                        SLInventoryEntry findEntry = this.db.findEntry(asUUID2);
                        if (findEntry != null) {
                            findEntryOrCreate.parent_id = findEntry.getId();
                            findEntryOrCreate.parentUUID = asUUID2;
                            findEntryOrCreate.name = byIndex.byKey("Name").asString();
                            findEntryOrCreate.typeDefault = byIndex.byKey("Type").asInt();
                            findEntryOrCreate.isFolder = true;
                            if (findEntryOrCreate.getId() == 0 && onInventoryCallbackListener == null) {
                                sLInventoryNewContentsEvent.AddItem(true, asUUID, findEntryOrCreate.name);
                            }
                            this.db.saveEntry(findEntryOrCreate);
                            hashSet.add(asUUID);
                            hashSet.add(asUUID2);
                        } else {
                            hashSet.add(asUUID2);
                            if (findEntryOrCreate.getId() != 0) {
                                this.db.deleteEntry(findEntryOrCreate);
                            }
                        }
                        if (onInventoryCallbackListener != null) {
                            onInventoryCallbackListener.onInventoryCallback(findEntryOrCreate);
                        }
                    }
                }
            }
            if (lLSDNode.keyExists("ItemData")) {
                LLSDNode byKey2 = lLSDNode.byKey("ItemData");
                for (int i2 = 0; i2 < byKey2.getCount(); i2++) {
                    LLSDNode byIndex2 = byKey2.byIndex(i2);
                    UUID asUUID3 = byIndex2.byKey("ItemID").asUUID();
                    if (asUUID3.getLeastSignificantBits() != 0 || asUUID3.getMostSignificantBits() != 0) {
                        Debug.Printf("Inventory: BulkUpdateInventory got item %s", asUUID3.toString());
                        UUID asUUID4 = byIndex2.byKey("FolderID").asUUID();
                        OnInventoryCallbackListener onInventoryCallbackListener2 = null;
                        if (byIndex2.keyExists("CallbackID")) {
                            Debug.Printf("Inventory: got callback id %d", Integer.valueOf(byIndex2.byKey("CallbackID").asInt()));
                            onInventoryCallbackListener2 = this.callbacks.remove(Integer.valueOf(byIndex2.byKey("CallbackID").asInt()));
                        }
                        SLInventoryEntry findEntryOrCreate2 = this.db.findEntryOrCreate(asUUID3);
                        hashSet.add(asUUID4);
                        findEntryOrCreate2.groupMask = byIndex2.byKey("GroupMask").asInt();
                        findEntryOrCreate2.description = byIndex2.byKey("Description").asString();
                        findEntryOrCreate2.isGroupOwned = byIndex2.byKey("GroupOwned").asBoolean();
                        findEntryOrCreate2.everyoneMask = byIndex2.byKey("EveryoneMask").asInt();
                        findEntryOrCreate2.assetType = byIndex2.byKey("Type").asInt();
                        findEntryOrCreate2.invType = byIndex2.byKey("InvType").asInt();
                        findEntryOrCreate2.groupUUID = byIndex2.byKey("GroupID").asUUID();
                        findEntryOrCreate2.name = byIndex2.byKey("Name").asString();
                        findEntryOrCreate2.baseMask = byIndex2.byKey("BaseMask").asInt();
                        findEntryOrCreate2.saleType = byIndex2.byKey("SaleType").asInt();
                        findEntryOrCreate2.salePrice = byIndex2.byKey("SalePrice").asInt();
                        findEntryOrCreate2.ownerUUID = byIndex2.byKey("OwnerID").asUUID();
                        findEntryOrCreate2.flags = byIndex2.byKey("Flags").asInt();
                        findEntryOrCreate2.ownerMask = byIndex2.byKey("OwnerMask").asInt();
                        findEntryOrCreate2.nextOwnerMask = byIndex2.byKey("NextOwnerMask").asInt();
                        findEntryOrCreate2.assetUUID = byIndex2.byKey("AssetID").asUUID();
                        findEntryOrCreate2.creationDate = byIndex2.byKey("CreationDate").asInt();
                        findEntryOrCreate2.parentUUID = asUUID4;
                        SLInventoryEntry findEntry2 = this.db.findEntry(asUUID4);
                        if (findEntry2 != null) {
                            if (findEntryOrCreate2.getId() == 0 && onInventoryCallbackListener2 == null && findEntry2.typeDefault != 14 && findEntry2.typeDefault != 2) {
                                sLInventoryNewContentsEvent.AddItem(false, asUUID4, findEntryOrCreate2.name);
                            }
                            findEntryOrCreate2.parent_id = findEntry2.getId();
                            this.db.saveEntry(findEntryOrCreate2);
                        } else if (findEntryOrCreate2.getId() != 0) {
                            this.db.deleteEntry(findEntryOrCreate2);
                        }
                        if (onInventoryCallbackListener2 != null) {
                            onInventoryCallbackListener2.onInventoryCallback(findEntryOrCreate2);
                        }
                    }
                }
            }
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e);
        } catch (LLSDException e2) {
            Debug.Warning(e2);
        }
        if (this.userManager != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                this.userManager.getInventoryManager().requestFolderUpdate((UUID) it.next());
            }
        }
        if (sLInventoryNewContentsEvent.isEmpty()) {
            return;
        }
        this.eventBus.publish(sLInventoryNewContentsEvent);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        if (!this.rootFolderFetchNeeded || this.rootFolderSubscription == null || this.rootFolder == null || this.userManager == null) {
            return;
        }
        Debug.Printf("Inventory: Fetching root folder: %s", this.rootFolder.uuid);
        this.rootFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), this.rootFolder.uuid);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCloseCircuit() {
        if (this.rootFolderSubscription != null) {
            this.rootFolderSubscription.unsubscribe();
        }
        if (this.nextFolderSubscription != null) {
            this.nextFolderSubscription.unsubscribe();
        }
        if (this.userManager != null) {
            this.userManager.getInventoryManager().getFolderRequestSource().detachRequestHandler(this.folderRequestHandler);
        }
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }

    @SLMessageHandler
    public synchronized void HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        SLInventoryUDPFetchRequest sLInventoryUDPFetchRequest = this.udpFetchRequests.get(inventoryDescendents.AgentData_Field.FolderID);
        if (sLInventoryUDPFetchRequest != null && sLInventoryUDPFetchRequest.HandleInventoryDescendents(inventoryDescendents)) {
            this.udpFetchRequests.remove(inventoryDescendents.AgentData_Field.FolderID);
            SLInventoryUDPFetchRequest remove = this.udpFetchPendingRequests.remove(inventoryDescendents.AgentData_Field.FolderID);
            if (remove != null) {
                this.udpFetchRequests.put(inventoryDescendents.AgentData_Field.FolderID, remove);
                remove.start();
            }
        }
    }

    @SLMessageHandler
    public void HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updateCreateInventoryItem) {
        SLInventoryNewContentsEvent sLInventoryNewContentsEvent = new SLInventoryNewContentsEvent();
        HashSet hashSet = new HashSet();
        for (UpdateCreateInventoryItem.InventoryData inventoryData : updateCreateInventoryItem.InventoryData_Fields) {
            UUID uuid = inventoryData.ItemID;
            UUID uuid2 = inventoryData.FolderID;
            Debug.Printf("Inventory: UpdateCreateInventoryItem got folder %s item %s, callback %d", uuid2.toString(), uuid.toString(), Integer.valueOf(inventoryData.CallbackID));
            hashSet.add(uuid2);
            OnInventoryCallbackListener remove = this.callbacks.remove(Integer.valueOf(inventoryData.CallbackID));
            try {
                SLInventoryEntry findEntryOrCreate = this.db.findEntryOrCreate(uuid);
                findEntryOrCreate.groupMask = inventoryData.GroupMask;
                findEntryOrCreate.description = SLMessage.stringFromVariableUTF(inventoryData.Description);
                findEntryOrCreate.isGroupOwned = inventoryData.GroupOwned;
                findEntryOrCreate.everyoneMask = inventoryData.EveryoneMask;
                findEntryOrCreate.assetType = inventoryData.Type;
                findEntryOrCreate.invType = inventoryData.InvType;
                findEntryOrCreate.groupUUID = inventoryData.GroupID;
                findEntryOrCreate.name = SLMessage.stringFromVariableOEM(inventoryData.Name);
                findEntryOrCreate.baseMask = inventoryData.BaseMask;
                findEntryOrCreate.saleType = inventoryData.SaleType;
                findEntryOrCreate.salePrice = inventoryData.SalePrice;
                findEntryOrCreate.ownerUUID = inventoryData.OwnerID;
                findEntryOrCreate.flags = inventoryData.Flags;
                findEntryOrCreate.ownerMask = inventoryData.OwnerMask;
                findEntryOrCreate.nextOwnerMask = inventoryData.NextOwnerMask;
                findEntryOrCreate.assetUUID = inventoryData.AssetID;
                findEntryOrCreate.creationDate = inventoryData.CreationDate;
                findEntryOrCreate.creatorUUID = inventoryData.CreatorID;
                findEntryOrCreate.parentUUID = uuid2;
                SLInventoryEntry findEntry = this.db.findEntry(uuid2);
                if (findEntry != null) {
                    if (findEntryOrCreate.getId() == 0 && remove == null && findEntry.typeDefault != 14 && findEntry.typeDefault != 2) {
                        sLInventoryNewContentsEvent.AddItem(false, uuid2, findEntryOrCreate.name);
                    }
                    findEntryOrCreate.parent_id = findEntry.getId();
                    this.db.saveEntry(findEntryOrCreate);
                } else if (findEntryOrCreate.getId() != 0) {
                    this.db.deleteEntry(findEntryOrCreate);
                }
                if (remove != null) {
                    remove.onInventoryCallback(findEntryOrCreate);
                }
            } catch (DBObject.DatabaseBindingException e) {
                e.printStackTrace();
            }
        }
        if (this.userManager != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                this.userManager.getInventoryManager().requestFolderUpdate((UUID) it.next());
            }
        }
        if (sLInventoryNewContentsEvent.isEmpty()) {
            return;
        }
        this.eventBus.publish(sLInventoryNewContentsEvent);
    }

    public void LinkInventoryItem(final SLInventoryEntry sLInventoryEntry, UUID uuid, int i, int i2, String str, String str2) {
        LinkInventoryItem linkInventoryItem = new LinkInventoryItem();
        linkInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        linkInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        linkInventoryItem.InventoryBlock_Field.FolderID = sLInventoryEntry.uuid;
        linkInventoryItem.InventoryBlock_Field.TransactionID = UUID.randomUUID();
        linkInventoryItem.InventoryBlock_Field.OldItemID = uuid;
        linkInventoryItem.InventoryBlock_Field.Type = i2;
        linkInventoryItem.InventoryBlock_Field.InvType = i;
        linkInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(str);
        linkInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(str2);
        linkInventoryItem.isReliable = true;
        linkInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.9
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
            }
        });
        SendMessage(linkInventoryItem);
    }

    public void MoveInventoryItem(final SLInventoryEntry sLInventoryEntry, final SLInventoryEntry sLInventoryEntry2) {
        final UUID uuid = sLInventoryEntry.parentUUID;
        if (sLInventoryEntry.isFolder) {
            UpdateInventoryFolder updateInventoryFolder = new UpdateInventoryFolder();
            updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
            updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            UpdateInventoryFolder.FolderData folderData = new UpdateInventoryFolder.FolderData();
            folderData.FolderID = sLInventoryEntry.uuid;
            folderData.ParentID = sLInventoryEntry2.uuid;
            folderData.Type = sLInventoryEntry.typeDefault;
            folderData.Name = SLMessage.stringToVariableUTF(sLInventoryEntry.name);
            updateInventoryFolder.FolderData_Fields.add(folderData);
            updateInventoryFolder.isReliable = true;
            updateInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.10
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(uuid);
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry2.uuid);
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
                }
            });
            SendMessage(updateInventoryFolder);
            return;
        }
        MoveInventoryItem moveInventoryItem = new MoveInventoryItem();
        moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moveInventoryItem.AgentData_Field.Stamp = false;
        MoveInventoryItem.InventoryData inventoryData = new MoveInventoryItem.InventoryData();
        inventoryData.FolderID = sLInventoryEntry2.uuid;
        inventoryData.ItemID = sLInventoryEntry.uuid;
        inventoryData.NewName = SLMessage.stringToVariableUTF(sLInventoryEntry.name);
        moveInventoryItem.InventoryData_Fields.add(inventoryData);
        moveInventoryItem.isReliable = true;
        moveInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.11
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(uuid);
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry2.uuid);
            }
        });
        SendMessage(moveInventoryItem);
    }

    public void MoveInventoryItemRaw(UUID uuid, String str, UUID uuid2) {
        MoveInventoryItem moveInventoryItem = new MoveInventoryItem();
        moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moveInventoryItem.AgentData_Field.Stamp = false;
        MoveInventoryItem.InventoryData inventoryData = new MoveInventoryItem.InventoryData();
        inventoryData.FolderID = uuid2;
        inventoryData.ItemID = uuid;
        inventoryData.NewName = SLMessage.stringToVariableUTF(str);
        moveInventoryItem.InventoryData_Fields.add(inventoryData);
        moveInventoryItem.isReliable = true;
        moveInventoryItem.setEventListener(this.reloadEvent);
        SendMessage(moveInventoryItem);
    }

    public void RenameInventoryItem(final SLInventoryEntry sLInventoryEntry, String str) {
        sLInventoryEntry.name = str;
        try {
            this.db.saveEntry(sLInventoryEntry);
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e);
        }
        if (!sLInventoryEntry.isFolder) {
            DoUpdateInventoryItem(sLInventoryEntry, new OnInventoryCallbackListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.3
                private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry2) {
                    ((SLInventory) this).m188x827623db((SLInventoryEntry) sLInventoryEntry, sLInventoryEntry2);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.OnInventoryCallbackListener
                public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry2) {
                    $m$0(sLInventoryEntry2);
                }
            });
            return;
        }
        UpdateInventoryFolder updateInventoryFolder = new UpdateInventoryFolder();
        updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        UpdateInventoryFolder.FolderData folderData = new UpdateInventoryFolder.FolderData();
        folderData.FolderID = sLInventoryEntry.uuid;
        folderData.ParentID = sLInventoryEntry.parentUUID;
        folderData.Type = sLInventoryEntry.typeDefault;
        folderData.Name = SLMessage.stringToVariableUTF(str);
        updateInventoryFolder.FolderData_Fields.add(folderData);
        updateInventoryFolder.isReliable = true;
        updateInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.12
            @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
            public void onMessageAcknowledged(SLMessage sLMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
            }
        });
        SendMessage(updateInventoryFolder);
    }

    public void TrashInventoryItem(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14);
        if (findSpecialFolder != null) {
            MoveInventoryItem(sLInventoryEntry, findSpecialFolder);
        }
    }

    public void UpdateNotecard(@Nullable final SLInventoryEntry sLInventoryEntry, UUID uuid, final boolean z, String str, String str2, @Nullable final byte[] bArr, @Nullable final UUID uuid2, final int i, @Nullable final OnNotecardUpdatedListener onNotecardUpdatedListener) {
        boolean z2;
        if (sLInventoryEntry != null) {
            z2 = !(Objects.equal(sLInventoryEntry.name, str) ? Objects.equal(sLInventoryEntry.description, str2) : false);
        } else {
            z2 = true;
        }
        if (uuid2 != null) {
            if (!z2 || sLInventoryEntry == null) {
                StartUploadingNotecardContents(sLInventoryEntry, uuid2, z, bArr, onNotecardUpdatedListener);
                return;
            }
            sLInventoryEntry.name = str;
            sLInventoryEntry.description = str2;
            DoUpdateTaskInventoryItem(sLInventoryEntry, i, new SLMessageEventListener.SLMessageBaseEventListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.14
                @Override // com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener, com.lumiyaviewer.lumiya.slproto.SLMessageEventListener
                public void onMessageAcknowledged(SLMessage sLMessage) {
                    SLInventory.this.userManager.getObjectsManager().requestTaskInventoryUpdate(i);
                    SLInventory.this.StartUploadingNotecardContents(sLInventoryEntry, uuid2, z, bArr, onNotecardUpdatedListener);
                }
            });
            return;
        }
        if (sLInventoryEntry == null) {
            Debug.Printf("Notecard: Creating new inventory entry.", new Object[0]);
            DoCreateInventoryItem(uuid, z ? SLAssetType.AT_LSL_TEXT.getTypeCode() : SLAssetType.AT_NOTECARD.getTypeCode(), z ? SLInventoryType.IT_LSL.getTypeCode() : SLInventoryType.IT_NOTECARD.getTypeCode(), str, str2, new OnInventoryCallbackListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.7
                private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry2) {
                    ((SLInventory) this).m191x82934f61(z, (byte[]) bArr, (SLInventory.OnNotecardUpdatedListener) onNotecardUpdatedListener, sLInventoryEntry2);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.OnInventoryCallbackListener
                public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry2) {
                    $m$0(sLInventoryEntry2);
                }
            });
        } else {
            if (!z2) {
                StartUploadingNotecardContents(sLInventoryEntry, null, z, bArr, onNotecardUpdatedListener);
                return;
            }
            sLInventoryEntry.name = str;
            sLInventoryEntry.description = str2;
            try {
                this.db.saveEntry(sLInventoryEntry);
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e);
            }
            Debug.Printf("Notecard: Updating existing inventory entry %s", sLInventoryEntry.uuid);
            DoUpdateInventoryItem(sLInventoryEntry, new OnInventoryCallbackListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.8
                private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry2) {
                    ((SLInventory) this).m192x829d436f(z, (byte[]) bArr, (SLInventory.OnNotecardUpdatedListener) onNotecardUpdatedListener, sLInventoryEntry2);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.OnInventoryCallbackListener
                public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry2) {
                    $m$0(sLInventoryEntry2);
                }
            });
        }
    }

    public void UpdateStoreInventoryItem(final SLInventoryEntry sLInventoryEntry) {
        try {
            this.db.saveEntry(sLInventoryEntry);
            DoUpdateInventoryItem(sLInventoryEntry, new OnInventoryCallbackListener() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.4
                private final /* synthetic */ void $m$0(SLInventoryEntry sLInventoryEntry2) {
                    ((SLInventory) this).m189x82901fe6((SLInventoryEntry) sLInventoryEntry, sLInventoryEntry2);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.OnInventoryCallbackListener
                public final void onInventoryCallback(SLInventoryEntry sLInventoryEntry2) {
                    $m$0(sLInventoryEntry2);
                }
            });
        } catch (DBObject.DatabaseBindingException e) {
            Debug.Warning(e);
        }
    }

    public boolean canMoveToTrash(SLInventoryEntry sLInventoryEntry) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14);
        return (findSpecialFolder == null || sLInventoryEntry.parent_id == findSpecialFolder.getId()) ? false : true;
    }

    @Nullable
    public UUID findSpecialFolder(int i) {
        SLInventoryEntry findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), i);
        if (findSpecialFolder != null) {
            return findSpecialFolder.uuid;
        }
        return null;
    }

    public UUID getCallingCardsFolderUUID() {
        SLInventoryEntry findSpecialFolder;
        if (this.rootFolder == null || (findSpecialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 2)) == null) {
            return null;
        }
        return findSpecialFolder.uuid;
    }

    public SLCaps getCaps() {
        return this.caps;
    }

    public InventoryDB getDatabase() {
        return this.db;
    }

    public ExecutorService getExecutor() {
        if (this.executor == null) {
            this.executor = Executors.newSingleThreadExecutor();
        }
        return this.executor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_10310, reason: not valid java name */
    public /* synthetic */ void m187x8264e904(long j, UUID uuid, boolean z, boolean z2) {
        Debug.Printf("Inventory: onFetchComplete: folderId = '%s'", Long.valueOf(j));
        this.fetchRequests.remove(uuid);
        SLInventoryEntry findEntry = this.db.findEntry(uuid);
        if (findEntry != null) {
            findEntry.sessionID = this.circuitInfo.sessionID;
            findEntry.fetchFailed = !z;
            try {
                this.db.saveEntry(findEntry);
            } catch (DBObject.DatabaseBindingException e) {
                Debug.Warning(e);
            }
        }
        if (z) {
            this.folderEntryResultHandler.onResultData(uuid, findEntry);
        } else if (!z2) {
            this.folderEntryResultHandler.onResultError(uuid, new InventoryFetchException("Failed to retrieve folder contents"));
        }
        updateFolderLoadingStatus(uuid);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_27010, reason: not valid java name */
    public /* synthetic */ void m188x827623db(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.uuid);
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_42520, reason: not valid java name */
    public /* synthetic */ void m189x82901fe6(SLInventoryEntry sLInventoryEntry, SLInventoryEntry sLInventoryEntry2) {
        this.userManager.getInventoryManager().requestFolderUpdate(sLInventoryEntry.parentUUID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_48053, reason: not valid java name */
    public /* synthetic */ void m190x8292c7bb(SLInventoryEntry sLInventoryEntry, byte[] bArr, UUID uuid, boolean z, OnNotecardUpdatedListener onNotecardUpdatedListener) {
        Object[] objArr = new Object[1];
        objArr[0] = sLInventoryEntry != null ? sLInventoryEntry.uuid : null;
        Debug.Printf("Notecard: Starting to upload contents for entry %s", objArr);
        String UploadNotecardContents = bArr != null ? UploadNotecardContents(sLInventoryEntry, uuid, z, bArr) : null;
        Object[] objArr2 = new Object[1];
        objArr2[0] = sLInventoryEntry != null ? sLInventoryEntry.uuid : null;
        Debug.Printf("Notecard: Notecard entry %s updated", objArr2);
        if (onNotecardUpdatedListener != null) {
            onNotecardUpdatedListener.onNotecardUpdated(sLInventoryEntry, UploadNotecardContents);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_49599, reason: not valid java name */
    public /* synthetic */ void m191x82934f61(boolean z, byte[] bArr, OnNotecardUpdatedListener onNotecardUpdatedListener, SLInventoryEntry sLInventoryEntry) {
        StartUploadingNotecardContents(sLInventoryEntry, null, z, bArr, onNotecardUpdatedListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_50229, reason: not valid java name */
    public /* synthetic */ void m192x829d436f(boolean z, byte[] bArr, OnNotecardUpdatedListener onNotecardUpdatedListener, SLInventoryEntry sLInventoryEntry) {
        StartUploadingNotecardContents(sLInventoryEntry, null, z, bArr, onNotecardUpdatedListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_51539, reason: not valid java name */
    public /* synthetic */ void m193x829dc330(UUID uuid, UUID uuid2, UUID uuid3, Runnable runnable) {
        try {
            new LLSDXMLRequest().PerformRequest(getCaps().getCapabilityOrThrow(SLCaps.SLCapability.CopyInventoryFromNotecard), new LLSDMap(new LLSDMap.LLSDMapEntry("notecard-id", new LLSDUUID(uuid)), new LLSDMap.LLSDMapEntry("object-id", new LLSDUUID()), new LLSDMap.LLSDMapEntry("item-id", new LLSDUUID(uuid2)), new LLSDMap.LLSDMapEntry("folder-id", new LLSDUUID(uuid3)), new LLSDMap.LLSDMapEntry("callback-id", new LLSDInt(0))));
        } catch (SLCaps.NoSuchCapabilityException e) {
            Debug.Warning(e);
        } catch (LLSDException e2) {
            Debug.Warning(e2);
        } catch (IOException e3) {
            Debug.Warning(e3);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_53068, reason: not valid java name */
    public /* synthetic */ void m194x829e9985(String str, final ImmutableSet immutableSet, final int i, final Function function) {
        DoCreateNewFolder(this.rootFolder, str, true, new Function<UUID, Void>() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.SLInventory.15
            @Override // com.google.common.base.Function
            @Nullable
            public Void apply(@Nullable UUID uuid) {
                if (uuid != null) {
                    Iterator<E> it = immutableSet.iterator();
                    while (it.hasNext()) {
                        SLInventory.this.MoveTaskInventory(uuid, i, (UUID) it.next());
                    }
                }
                function.apply(uuid);
                return null;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onFetchComplete(SLInventoryFetchRequest sLInventoryFetchRequest, final UUID uuid, final long j, final boolean z, final boolean z2) {
        if (this.dbExecutor != null) {
            this.dbExecutor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.inventory.-$Lambda$eCHTl-_kh2tUCaOJ-O9NRHJvhjs.10
                private final /* synthetic */ void $m$0() {
                    ((SLInventory) this).m187x8264e904(j, (UUID) uuid, z, z2);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        }
    }
}
