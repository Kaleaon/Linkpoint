// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.concurrent.Executors;
import com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder;
import com.lumiyaviewer.lumiya.slproto.messages.LinkInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.handler.SLEventQueueMessageHandler;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryNewContentsEvent;
import com.lumiyaviewer.lumiya.slproto.messages.CreateInventoryFolder;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryObjects;
import java.util.List;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryFolder;
import com.google.common.collect.ImmutableSet;
import com.google.common.base.Function;
import com.lumiyaviewer.lumiya.slproto.messages.CopyInventoryItem;
import java.util.ArrayList;
import java.util.Collection;
import com.google.common.base.Objects;
import android.database.Cursor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import okhttp3.Response;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import java.io.IOException;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import com.google.common.base.Strings;
import okhttp3.Request;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.messages.MoveTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateTaskInventory;
import com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryItem;
import com.lumiyaviewer.lumiya.slproto.messages.CreateInventoryItem;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryUpdatedEvent;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import java.util.concurrent.atomic.AtomicInteger;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import android.annotation.SuppressLint;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLInventory extends SLModule
{
    @SuppressLint({ "UseSparseArrays" })
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
    
    public SLInventory(final SLAgentCircuit slAgentCircuit, final SLCaps caps) {
        super(slAgentCircuit);
        this.rootFolder = null;
        this.executor = null;
        this.nextCallbackID = new AtomicInteger(1);
        this.rootFolderFetchNeeded = false;
        this.callbacks = Collections.synchronizedMap(new HashMap<Integer, OnInventoryCallbackListener>());
        this.udpFetchRequests = Collections.synchronizedMap(new HashMap<UUID, SLInventoryUDPFetchRequest>());
        this.udpFetchPendingRequests = Collections.synchronizedMap(new HashMap<UUID, SLInventoryUDPFetchRequest>());
        this.fetchRequests = new ConcurrentHashMap<UUID, SLInventoryFetchRequest>();
        this.fetchEntireInventoryRequested = new AtomicBoolean(false);
        this.folderLoadingRequestHandler = new SimpleRequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                SLInventory.this.updateFolderLoadingStatus(uuid);
            }
        };
        this.searchRunningRequestHandler = new SimpleRequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.updateSearchRunningStatus();
            }
        };
        this.folderRequestHandler = new RequestHandler<UUID>() {
            @Override
            public void onRequest(@Nonnull final UUID uuid) {
                try {
                    Debug.Printf("Inventory: folderRequestHandler: folderId = '%s'", uuid);
                    SLInventoryFetchRequest slInventoryFetchRequest;
                    if (SLInventory.this.fetchCap != null) {
                        slInventoryFetchRequest = new SLInventoryHTTPFetchRequest(SLInventory.this, uuid, SLInventory.this.fetchCap);
                    }
                    else {
                        slInventoryFetchRequest = new SLInventoryUDPFetchRequest(SLInventory.this, uuid);
                    }
                    SLInventory.this.fetchRequests.put(uuid, slInventoryFetchRequest);
                    SLInventory.this.updateFolderLoadingStatus(uuid);
                    slInventoryFetchRequest.start();
                }
                catch (final NoInventoryItemException ex) {
                    Debug.Warning(ex);
                }
            }
            
            @Override
            public void onRequestCancelled(@Nonnull final UUID uuid) {
                final SLInventoryFetchRequest slInventoryFetchRequest = SLInventory.this.fetchRequests.remove(uuid);
                SLInventory.this.updateFolderLoadingStatus(uuid);
                if (slInventoryFetchRequest != null) {
                    slInventoryFetchRequest.cancel();
                }
            }
        };
        this.searchRequestHandler = new RequestHandler<SubscriptionSingleKey>() {
            @Override
            public void onRequest(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.fetchEntireInventoryRequested.set(true);
                SLInventory.this.fetchNextFolder();
            }
            
            @Override
            public void onRequestCancelled(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
                SLInventory.this.fetchEntireInventoryRequested.set(false);
                SLInventory.this.updateSearchRunningStatus();
            }
        };
        this.reloadEvent = new SLMessageEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLInventory.this.eventBus.publish(new SLInventoryUpdatedEvent(null, null, true));
            }
            
            @Override
            public void onMessageTimeout(final SLMessage slMessage) {
            }
        };
        this.userManager = UserManager.getUserManager(slAgentCircuit.getAgentUUID());
    Label_0603_Outer:
        while (true) {
            Executor executor = null;
            Label_0225: {
                while (true) {
                    InventoryDB database = null;
                    Label_0186: {
                        if (this.userManager != null) {
                            database = this.userManager.getInventoryManager().getDatabase();
                            break Label_0186;
                        }
                        Label_0598: {
                            break Label_0598;
                        Label_0424_Outer:
                            while (true) {
                                Label_0629: {
                                Label_0621:
                                    while (true) {
                                    Label_0616:
                                        while (true) {
                                            try {
                                                if (this.db != null) {
                                                    Debug.Printf("Inventory: creating root folder with folderUUID %s", slAgentCircuit.getAuthReply().inventoryRoot.toString());
                                                    this.rootFolder = this.db.findEntryOrCreate(slAgentCircuit.getAuthReply().inventoryRoot);
                                                    if (this.rootFolder.getId() == 0L) {
                                                        this.rootFolder.name = "Inventory";
                                                        this.rootFolder.isFolder = true;
                                                        this.rootFolder.parent_id = 0L;
                                                        this.rootFolder.agentUUID = slAgentCircuit.circuitInfo.agentID;
                                                        this.db.saveEntry(this.rootFolder);
                                                        this.rootFolderFetchNeeded = true;
                                                    }
                                                }
                                                Debug.Printf("Inventory: ready.", new Object[0]);
                                                if (this.userManager == null) {
                                                    break Label_0616;
                                                }
                                                final ResultHandler<UUID, SLInventoryEntry> attachRequestHandler = this.userManager.getInventoryManager().getFolderRequestSource().attachRequestHandler(this.folderRequestHandler);
                                                this.folderEntryResultHandler = attachRequestHandler;
                                                if (this.userManager == null) {
                                                    break Label_0629;
                                                }
                                                this.folderLoadingResultHandler = this.userManager.getInventoryManager().getFolderLoadingRequestSource().attachRequestHandler(new AsyncRequestHandler<UUID>(this.dbExecutor, this.folderLoadingRequestHandler));
                                                this.seachRunningResultHandler = this.userManager.getInventoryManager().getSearchRunning().attachRequestHandler(new AsyncRequestHandler<SubscriptionSingleKey>(this.dbExecutor, this.searchRunningRequestHandler));
                                                this.searchProcessResultHandler = this.userManager.getInventoryManager().getSearchProcessRequestSource().attachRequestHandler(new AsyncRequestHandler<SubscriptionSingleKey>(this.dbExecutor, this.searchRequestHandler));
                                                this.nextFolderSubscription = new SubscriptionData<UUID, SLInventoryEntry>(this.dbExecutor, new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs(this), new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$2(this));
                                                if (this.rootFolderFetchNeeded) {
                                                    this.rootFolderSubscription = new SubscriptionData<UUID, SLInventoryEntry>(this.dbExecutor, new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$1(this));
                                                    return;
                                                }
                                                break Label_0621;
                                                database = null;
                                                break;
                                                executor = null;
                                                break Label_0225;
                                            }
                                            catch (final DBObject.DatabaseBindingException ex) {
                                                Debug.Warning(ex);
                                                continue Label_0424_Outer;
                                            }
                                            break;
                                        }
                                        final ResultHandler<UUID, SLInventoryEntry> attachRequestHandler = null;
                                        continue Label_0603_Outer;
                                    }
                                    this.rootFolderSubscription = null;
                                    return;
                                }
                                this.folderLoadingResultHandler = null;
                                this.searchProcessResultHandler = null;
                                this.nextFolderSubscription = null;
                                this.seachRunningResultHandler = null;
                                this.rootFolderSubscription = null;
                                return;
                            }
                        }
                    }
                    this.db = database;
                    this.caps = caps;
                    this.fetchCap = caps.getCapability(SLCaps.SLCapability.FetchInventoryDescendents2);
                    if (this.userManager == null) {
                        continue;
                    }
                    break;
                }
                executor = this.userManager.getInventoryManager().getExecutor();
            }
            this.dbExecutor = executor;
            if (this.userManager != null) {
                this.userManager.getInventoryManager().setCurrentSessionID(slAgentCircuit.getAuthReply().sessionID);
                this.userManager.getInventoryManager().setRootFolder(slAgentCircuit.getAuthReply().inventoryRoot);
            }
            continue Label_0603_Outer;
        }
    }
    
    private void DoCreateInventoryItem(final UUID folderID, final int type, final int invType, final String s, final String s2, final OnInventoryCallbackListener onInventoryCallbackListener) {
        final CreateInventoryItem createInventoryItem = new CreateInventoryItem();
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        createInventoryItem.InventoryBlock_Field.CallbackID = this.getNextCallbackID();
        createInventoryItem.InventoryBlock_Field.FolderID = folderID;
        createInventoryItem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE;
        createInventoryItem.InventoryBlock_Field.Type = type;
        createInventoryItem.InventoryBlock_Field.InvType = invType;
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s2);
        this.callbacks.put(createInventoryItem.InventoryBlock_Field.CallbackID, onInventoryCallbackListener);
        createInventoryItem.isReliable = true;
        this.SendMessage(createInventoryItem);
    }
    
    private void DoUpdateInventoryItem(final SLInventoryEntry slInventoryEntry, final OnInventoryCallbackListener onInventoryCallbackListener) {
        final UpdateInventoryItem updateInventoryItem = new UpdateInventoryItem();
        updateInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateInventoryItem.AgentData_Field.TransactionID = UUID.randomUUID();
        final UpdateInventoryItem.InventoryData e = new UpdateInventoryItem.InventoryData();
        e.ItemID = slInventoryEntry.uuid;
        e.FolderID = slInventoryEntry.parentUUID;
        e.CallbackID = 0;
        e.CreatorID = slInventoryEntry.creatorUUID;
        e.OwnerID = slInventoryEntry.ownerUUID;
        e.GroupID = slInventoryEntry.groupUUID;
        e.BaseMask = slInventoryEntry.baseMask;
        e.OwnerMask = slInventoryEntry.ownerMask;
        e.GroupMask = slInventoryEntry.groupMask;
        e.EveryoneMask = slInventoryEntry.everyoneMask;
        e.NextOwnerMask = slInventoryEntry.nextOwnerMask;
        e.GroupOwned = slInventoryEntry.isGroupOwned;
        e.TransactionID = new UUID(0L, 0L);
        e.Type = slInventoryEntry.assetType;
        e.InvType = slInventoryEntry.invType;
        e.Flags = slInventoryEntry.flags;
        e.SaleType = slInventoryEntry.saleType;
        e.SalePrice = slInventoryEntry.salePrice;
        e.Name = SLMessage.stringToVariableOEM(slInventoryEntry.name);
        e.Description = SLMessage.stringToVariableUTF(slInventoryEntry.description);
        e.CreationDate = slInventoryEntry.creationDate;
        e.CRC = 0;
        updateInventoryItem.InventoryData_Fields.add(e);
        updateInventoryItem.isReliable = true;
        Debug.Printf("Update inventory callback %d", e.CallbackID);
        updateInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                super.onMessageAcknowledged(slMessage);
                onInventoryCallbackListener.onInventoryCallback(slInventoryEntry);
            }
        });
        this.SendMessage(updateInventoryItem);
    }
    
    private void DoUpdateTaskInventoryItem(final SLInventoryEntry slInventoryEntry, final int localID, final SLMessageEventListener eventListener) {
        final UpdateTaskInventory updateTaskInventory = new UpdateTaskInventory();
        updateTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        updateTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        updateTaskInventory.UpdateData_Field.LocalID = localID;
        updateTaskInventory.UpdateData_Field.Key = 0;
        final UpdateTaskInventory.InventoryData inventoryData_Field = updateTaskInventory.InventoryData_Field;
        inventoryData_Field.ItemID = slInventoryEntry.uuid;
        inventoryData_Field.FolderID = slInventoryEntry.parentUUID;
        inventoryData_Field.CreatorID = slInventoryEntry.creatorUUID;
        inventoryData_Field.OwnerID = slInventoryEntry.ownerUUID;
        inventoryData_Field.GroupID = slInventoryEntry.groupUUID;
        inventoryData_Field.BaseMask = slInventoryEntry.baseMask;
        inventoryData_Field.OwnerMask = slInventoryEntry.ownerMask;
        inventoryData_Field.GroupMask = slInventoryEntry.groupMask;
        inventoryData_Field.EveryoneMask = slInventoryEntry.everyoneMask;
        inventoryData_Field.NextOwnerMask = slInventoryEntry.nextOwnerMask;
        inventoryData_Field.GroupOwned = slInventoryEntry.isGroupOwned;
        inventoryData_Field.TransactionID = new UUID(0L, 0L);
        inventoryData_Field.Type = slInventoryEntry.assetType;
        inventoryData_Field.InvType = slInventoryEntry.invType;
        inventoryData_Field.Flags = slInventoryEntry.flags;
        inventoryData_Field.SaleType = slInventoryEntry.saleType;
        inventoryData_Field.SalePrice = slInventoryEntry.salePrice;
        inventoryData_Field.Name = SLMessage.stringToVariableOEM(slInventoryEntry.name);
        inventoryData_Field.Description = SLMessage.stringToVariableUTF(slInventoryEntry.description);
        inventoryData_Field.CreationDate = slInventoryEntry.creationDate;
        inventoryData_Field.CRC = 0;
        updateTaskInventory.isReliable = true;
        updateTaskInventory.setEventListener(eventListener);
        this.SendMessage(updateTaskInventory);
    }
    
    private void MoveTaskInventory(final UUID folderID, final int localID, final UUID itemID) {
        final MoveTaskInventory moveTaskInventory = new MoveTaskInventory();
        moveTaskInventory.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moveTaskInventory.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moveTaskInventory.AgentData_Field.FolderID = folderID;
        moveTaskInventory.InventoryData_Field.LocalID = localID;
        moveTaskInventory.InventoryData_Field.ItemID = itemID;
        moveTaskInventory.isReliable = true;
        this.SendMessage(moveTaskInventory);
    }
    
    private void StartUploadingNotecardContents(final SLInventoryEntry slInventoryEntry, @Nullable final UUID uuid, final boolean b, final byte[] array, final OnNotecardUpdatedListener onNotecardUpdatedListener) {
        GenericHTTPExecutor.getInstance().execute(new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$9(b, this, slInventoryEntry, array, uuid, onNotecardUpdatedListener));
    }
    
    @Nullable
    private String UploadNotecardContents(final SLInventoryEntry slInventoryEntry, @Nullable UUID uuid, final boolean b, final byte[] array) {
        Response execute;
        try {
            final LLSDXMLRequest llsdxmlRequest = new LLSDXMLRequest();
            if (!b) {
                goto Label_0377;
            }
            final SLCaps caps = this.getCaps();
            if (uuid != null) {
                goto Label_0369;
            }
            final String capabilityOrThrow = caps.getCapabilityOrThrow(SLCaps.SLCapability.UpdateScriptAgent);
            final boolean b2 = true;
            final ImmutableMap.Builder<String, LLSDUUID> builder = ImmutableMap.builder();
            builder.put("item_id", new LLSDUUID(slInventoryEntry.uuid));
            if (uuid != null) {
                builder.put("task_id", new LLSDUUID(uuid));
            }
            if (b2) {
                builder.put("target", (LLSDUUID)new LLSDString("mono"));
                if (uuid != null) {
                    builder.put("is_script_running", (LLSDUUID)new LLSDInt(1));
                }
            }
            final LLSDMap llsdMap = new LLSDMap((Map<String, LLSDNode>)builder.build());
            Debug.Log("Notecard upload request: Initial uploader request: " + llsdMap.serializeToXML());
            final LLSDNode performRequest = llsdxmlRequest.PerformRequest(capabilityOrThrow, llsdMap);
            Debug.Log("Notecard upload request: Initial uploader reply: " + performRequest.serializeToXML());
            final String string = performRequest.byKey("uploader").asString();
            String repairURL;
            if ((repairURL = string) != null) {
                repairURL = SLCaps.repairURL(capabilityOrThrow, string);
            }
            execute = SLHTTPSConnection.getOkHttpClient().newCall(new Request.Builder().url(Strings.nullToEmpty(repairURL)).post(RequestBody.create(MediaType.parse("application/vnd.ll.notecard"), array)).header("Accept", "application/llsd+xml").build()).execute();
            if (execute == null) {
                throw new IOException("Null response");
            }
        }
        catch (final DBObject.DatabaseBindingException ex) {
            Debug.Warning(ex);
            return "Failed to upload inventory asset";
        }
        catch (final IOException ex2) {
            Debug.Warning(ex2);
            return "Failed to upload inventory asset";
        }
        catch (final SLCaps.NoSuchCapabilityException ex3) {
            Debug.Warning(ex3);
            return "Failed to upload inventory asset";
        }
        catch (final LLSDException ex4) {
            Debug.Warning(ex4);
            return "Failed to upload inventory asset";
        }
        try {
            if (!execute.isSuccessful()) {
                uuid = (UUID)new StringBuilder();
                throw new IOException(((StringBuilder)uuid).append("Response error code ").append(execute.code()).toString());
            }
            goto Label_0476;
        }
        finally {}
    }
    
    private void fetchNextFolder() {
        Label_0247: {
            if (!this.fetchEntireInventoryRequested.get()) {
                break Label_0247;
            }
            final UUID sessionID = this.circuitInfo.sessionID;
            try {
                final Cursor query = this.db.getDatabase().query("Entries", new String[] { "uuid_high", "uuid_low" }, "isFolder AND (sessionID_high != ? OR sessionID_low != ?)", new String[] { Long.toString(sessionID.getMostSignificantBits()), Long.toString(sessionID.getLeastSignificantBits()) }, (String)null, (String)null, (String)null, "1");
                if (query.moveToFirst()) {
                    final UUID uuid = UUIDPool.getUUID(query.getLong(0), query.getLong(1));
                    Debug.Printf("InventorySearch: fetching next folder: %s", uuid);
                    this.nextFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), uuid);
                    this.updateSearchRunningStatus();
                }
                else {
                    Debug.Printf("InventorySearch: no more folders to fetch", new Object[0]);
                    this.searchProcessResultHandler.onResultData(SubscriptionSingleKey.Value, true);
                    this.nextFolderSubscription.unsubscribe();
                    this.updateSearchRunningStatus();
                }
                query.close();
                return;
            }
            catch (final Exception ex) {
                Debug.Printf("InventorySearch: error while fetching folders", new Object[0]);
                Debug.Warning(ex);
                this.fetchEntireInventoryRequested.set(false);
                this.searchProcessResultHandler.onResultError(SubscriptionSingleKey.Value, ex);
                this.nextFolderSubscription.unsubscribe();
                this.updateSearchRunningStatus();
                return;
            }
        }
        this.updateSearchRunningStatus();
    }
    
    private int getNextCallbackID() {
        return this.nextCallbackID.getAndIncrement();
    }
    
    private void onNextFolderError(final Throwable t) {
        this.fetchNextFolder();
    }
    
    private void onNextFolderFetched(final SLInventoryEntry slInventoryEntry) {
        if (Objects.equal(slInventoryEntry.sessionID, this.circuitInfo.sessionID)) {
            this.fetchNextFolder();
        }
    }
    
    private void onRootFolderFetched(final SLInventoryEntry slInventoryEntry) {
        this.rootFolderFetchNeeded = false;
        if (this.rootFolderSubscription != null) {
            this.rootFolderSubscription.unsubscribe();
        }
        Debug.Printf("Inventory: Fetched root folder.", new Object[0]);
    }
    
    private void updateFolderLoadingStatus(@Nonnull final UUID uuid) {
        if (this.folderLoadingResultHandler != null) {
            this.folderLoadingResultHandler.onResultData(uuid, this.fetchRequests.containsKey(uuid));
        }
    }
    
    private void updateSearchRunningStatus() {
        if (this.searchRunningRequestHandler != null) {
            this.seachRunningResultHandler.onResultData(SubscriptionSingleKey.Value, this.fetchEntireInventoryRequested.get() && this.nextFolderSubscription.isSubscribed());
        }
    }
    
    public Collection<SLInventoryEntry> CollectGiveableItems(final SLInventoryEntry slInventoryEntry) {
        final ArrayList list = new ArrayList();
        final Cursor query = SLInventoryEntry.query(this.db.getDatabase(), "parent_id = ?", new String[] { Long.toString(slInventoryEntry.getId()) }, null);
        if (query != null) {
            while (query.moveToNext()) {
                final SLInventoryEntry slInventoryEntry2 = new SLInventoryEntry(query);
                if (slInventoryEntry2.isFolder) {
                    list.add(slInventoryEntry2);
                    list.addAll(this.CollectGiveableItems(slInventoryEntry2));
                }
                else {
                    if ((slInventoryEntry2.baseMask & slInventoryEntry2.ownerMask & 0x2000) == 0x0) {
                        continue;
                    }
                    list.add(slInventoryEntry2);
                }
            }
            query.close();
        }
        return list;
    }
    
    public void CopyInventoryFromNotecard(final UUID uuid, final UUID uuid2, final UUID uuid3, @Nullable final Runnable runnable) {
        GenericHTTPExecutor.getInstance().execute(new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$5(this, uuid, uuid2, uuid3, runnable));
    }
    
    public void CopyInventoryItem(final SLInventoryEntry slInventoryEntry, final SLInventoryEntry slInventoryEntry2) {
        final CopyInventoryItem copyInventoryItem = new CopyInventoryItem();
        copyInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        copyInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final CopyInventoryItem.InventoryData e = new CopyInventoryItem.InventoryData();
        e.CallbackID = this.getNextCallbackID();
        e.OldAgentID = slInventoryEntry.agentUUID;
        e.OldItemID = slInventoryEntry.uuid;
        e.NewFolderID = slInventoryEntry2.uuid;
        e.NewName = SLMessage.stringToVariableOEM(slInventoryEntry.name);
        copyInventoryItem.InventoryData_Fields.add(e);
        copyInventoryItem.isReliable = true;
        this.SendMessage(copyInventoryItem);
    }
    
    public void CopyObjectContents(final String s, final int n, final Set<UUID> set, final Function<UUID, Void> function) {
        if (this.rootFolder != null) {
            this.dbExecutor.execute(new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$6(n, this, s, ImmutableSet.copyOf((Collection<?>)set), function));
        }
        else {
            function.apply(null);
        }
    }
    
    public void DeleteInventoryItem(final SLInventoryEntry slInventoryEntry) {
        if (slInventoryEntry.isFolder) {
            final RemoveInventoryFolder removeInventoryFolder = new RemoveInventoryFolder();
            removeInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
            removeInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            final RemoveInventoryFolder.FolderData e = new RemoveInventoryFolder.FolderData();
            e.FolderID = slInventoryEntry.uuid;
            removeInventoryFolder.FolderData_Fields.add(e);
            removeInventoryFolder.isReliable = true;
            removeInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.parentUUID);
                }
            });
            this.SendMessage(removeInventoryFolder);
        }
        else {
            final RemoveInventoryItem removeInventoryItem = new RemoveInventoryItem();
            removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
            removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            final RemoveInventoryItem.InventoryData e2 = new RemoveInventoryItem.InventoryData();
            e2.ItemID = slInventoryEntry.uuid;
            removeInventoryItem.InventoryData_Fields.add(e2);
            removeInventoryItem.isReliable = true;
            removeInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.parentUUID);
                }
            });
            this.SendMessage(removeInventoryItem);
        }
    }
    
    public void DeleteInventoryItemRaw(final UUID itemID) {
        final RemoveInventoryItem removeInventoryItem = new RemoveInventoryItem();
        removeInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        final RemoveInventoryItem.InventoryData e = new RemoveInventoryItem.InventoryData();
        e.ItemID = itemID;
        removeInventoryItem.InventoryData_Fields.add(e);
        removeInventoryItem.isReliable = true;
        removeInventoryItem.setEventListener(this.reloadEvent);
        this.SendMessage(removeInventoryItem);
    }
    
    public void DeleteMultiInventoryItemRaw(final SLInventoryEntry slInventoryEntry, final List<UUID> list) {
        final RemoveInventoryObjects removeInventoryObjects = new RemoveInventoryObjects();
        removeInventoryObjects.AgentData_Field.AgentID = this.circuitInfo.agentID;
        removeInventoryObjects.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        for (final UUID itemID : list) {
            final RemoveInventoryObjects.ItemData e = new RemoveInventoryObjects.ItemData();
            e.ItemID = itemID;
            removeInventoryObjects.ItemData_Fields.add(e);
        }
        removeInventoryObjects.isReliable = true;
        removeInventoryObjects.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.uuid);
            }
        });
        this.SendMessage(removeInventoryObjects);
    }
    
    public UUID DoCreateNewFolder(final SLInventoryEntry slInventoryEntry, final String name, final boolean b, @Nullable final Function<UUID, Void> function) {
        final UUID randomUUID = UUID.randomUUID();
        while (true) {
            if (!b) {
                break Label_0103;
            }
            try {
                final SLInventoryEntry entryOrCreate = this.db.findEntryOrCreate(randomUUID);
                entryOrCreate.resetId();
                entryOrCreate.uuid = randomUUID;
                entryOrCreate.parent_id = slInventoryEntry.getId();
                entryOrCreate.parentUUID = slInventoryEntry.uuid;
                entryOrCreate.name = name;
                entryOrCreate.description = "";
                entryOrCreate.agentUUID = this.circuitInfo.agentID;
                entryOrCreate.isFolder = true;
                entryOrCreate.typeDefault = -1;
                entryOrCreate.version = 0;
                this.db.saveEntry(entryOrCreate);
                Debug.Printf("Inventory: Creating new folder with uuid = %s, parent %s", randomUUID, slInventoryEntry.uuid);
                final CreateInventoryFolder createInventoryFolder = new CreateInventoryFolder();
                createInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
                createInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                createInventoryFolder.FolderData_Field.FolderID = randomUUID;
                createInventoryFolder.FolderData_Field.ParentID = slInventoryEntry.uuid;
                createInventoryFolder.FolderData_Field.Type = -1;
                createInventoryFolder.FolderData_Field.Name = SLMessage.stringToVariableOEM(name);
                createInventoryFolder.isReliable = true;
                createInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                    @Override
                    public void onMessageAcknowledged(final SLMessage slMessage) {
                        Debug.Printf("Inventory: new folder created with uuid = %s, parent %s", randomUUID, slInventoryEntry.uuid);
                        if (SLInventory.this.userManager != null) {
                            SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.uuid);
                        }
                        if (function != null) {
                            function.apply(randomUUID);
                        }
                    }
                });
                this.SendMessage(createInventoryFolder);
                return randomUUID;
            }
            catch (final DBObject.DatabaseBindingException ex) {
                Debug.Warning(ex);
                continue;
            }
            break;
        }
    }
    
    public void DoCreateNewLandmark(final SLInventoryEntry slInventoryEntry, final String s, final String s2) {
        final CreateInventoryItem createInventoryItem = new CreateInventoryItem();
        createInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        createInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        createInventoryItem.InventoryBlock_Field.CallbackID = this.getNextCallbackID();
        createInventoryItem.InventoryBlock_Field.FolderID = slInventoryEntry.uuid;
        createInventoryItem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createInventoryItem.InventoryBlock_Field.NextOwnerMask = Integer.MAX_VALUE;
        createInventoryItem.InventoryBlock_Field.Type = SLAssetType.AT_LANDMARK.getTypeCode();
        createInventoryItem.InventoryBlock_Field.InvType = SLInventoryType.IT_LANDMARK.getTypeCode();
        createInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        createInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s2);
        createInventoryItem.isReliable = true;
        this.SendMessage(createInventoryItem);
    }
    
    @SLEventQueueMessageHandler(eventName = SLCapEventQueue.CapsEventType.BulkUpdateInventory)
    public void HandleBulkUpdateInventory(final LLSDNode llsdNode) {
        Debug.Printf("BulkUpdateInventory: EventQueue event", new Object[0]);
        final SLInventoryNewContentsEvent slInventoryNewContentsEvent = new SLInventoryNewContentsEvent();
        final HashSet set = new HashSet();
        try {
            if (llsdNode.keyExists("FolderData")) {
                final LLSDNode byKey = llsdNode.byKey("FolderData");
                for (int i = 0; i < byKey.getCount(); ++i) {
                    final LLSDNode byIndex = byKey.byIndex(i);
                    final UUID uuid = byIndex.byKey("FolderID").asUUID();
                    if (uuid.getLeastSignificantBits() != 0L || uuid.getMostSignificantBits() != 0L) {
                        Debug.Printf("Inventory: BulkUpdateInventory got folder %s", uuid.toString());
                        OnInventoryCallbackListener onInventoryCallbackListener = null;
                        if (byIndex.keyExists("CallbackID")) {
                            Debug.Printf("Inventory: got callback id %d", byIndex.byKey("CallbackID").asInt());
                            onInventoryCallbackListener = this.callbacks.remove(byIndex.byKey("CallbackID").asInt());
                        }
                        final SLInventoryEntry entryOrCreate = this.db.findEntryOrCreate(uuid);
                        final UUID uuid2 = byIndex.byKey("ParentID").asUUID();
                        final SLInventoryEntry entry = this.db.findEntry(uuid2);
                        if (entry != null) {
                            entryOrCreate.parent_id = entry.getId();
                            entryOrCreate.parentUUID = uuid2;
                            entryOrCreate.name = byIndex.byKey("Name").asString();
                            entryOrCreate.typeDefault = byIndex.byKey("Type").asInt();
                            entryOrCreate.isFolder = true;
                            if (entryOrCreate.getId() == 0L && onInventoryCallbackListener == null) {
                                slInventoryNewContentsEvent.AddItem(true, uuid, entryOrCreate.name);
                            }
                            this.db.saveEntry(entryOrCreate);
                            set.add(uuid);
                            set.add(uuid2);
                        }
                        else {
                            set.add(uuid2);
                            if (entryOrCreate.getId() != 0L) {
                                this.db.deleteEntry(entryOrCreate);
                            }
                        }
                        if (onInventoryCallbackListener != null) {
                            onInventoryCallbackListener.onInventoryCallback(entryOrCreate);
                        }
                    }
                }
                goto Label_0446;
            }
            goto Label_0446;
        }
        catch (final LLSDException ex) {
            Debug.Warning(ex);
        }
        catch (final DBObject.DatabaseBindingException ex2) {
            Debug.Warning(ex2);
            goto Label_0396;
        }
        if (!slInventoryNewContentsEvent.isEmpty()) {
            this.eventBus.publish(slInventoryNewContentsEvent);
        }
    }
    
    @Override
    public void HandleCircuitReady() {
        super.HandleCircuitReady();
        if (this.rootFolderFetchNeeded && this.rootFolderSubscription != null && this.rootFolder != null && this.userManager != null) {
            Debug.Printf("Inventory: Fetching root folder: %s", this.rootFolder.uuid);
            this.rootFolderSubscription.subscribe(this.userManager.getInventoryManager().getFolderEntryPool(), this.rootFolder.uuid);
        }
    }
    
    @Override
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
    public void HandleInventoryDescendents(final InventoryDescendents inventoryDescendents) {
        synchronized (this) {
            final SLInventoryUDPFetchRequest slInventoryUDPFetchRequest = this.udpFetchRequests.get(inventoryDescendents.AgentData_Field.FolderID);
            if (slInventoryUDPFetchRequest != null && slInventoryUDPFetchRequest.HandleInventoryDescendents(inventoryDescendents)) {
                this.udpFetchRequests.remove(inventoryDescendents.AgentData_Field.FolderID);
                final SLInventoryUDPFetchRequest slInventoryUDPFetchRequest2 = this.udpFetchPendingRequests.remove(inventoryDescendents.AgentData_Field.FolderID);
                if (slInventoryUDPFetchRequest2 != null) {
                    this.udpFetchRequests.put(inventoryDescendents.AgentData_Field.FolderID, slInventoryUDPFetchRequest2);
                    slInventoryUDPFetchRequest2.start();
                }
            }
        }
    }
    
    @SLMessageHandler
    public void HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem iterator) {
        final SLInventoryNewContentsEvent slInventoryNewContentsEvent = new SLInventoryNewContentsEvent();
        final HashSet set = new HashSet();
        iterator = (UpdateCreateInventoryItem)iterator.InventoryData_Fields.iterator();
        while (((Iterator)iterator).hasNext()) {
            while (true) {
                final UpdateCreateInventoryItem.InventoryData inventoryData = ((Iterator<UpdateCreateInventoryItem.InventoryData>)iterator).next();
                final UUID itemID = inventoryData.ItemID;
                final UUID folderID = inventoryData.FolderID;
                Debug.Printf("Inventory: UpdateCreateInventoryItem got folder %s item %s, callback %d", folderID.toString(), itemID.toString(), inventoryData.CallbackID);
                set.add(folderID);
                final OnInventoryCallbackListener onInventoryCallbackListener = this.callbacks.remove(inventoryData.CallbackID);
                while (true) {
                    SLInventoryEntry entryOrCreate = null;
                    Label_0440: {
                        try {
                            entryOrCreate = this.db.findEntryOrCreate(itemID);
                            entryOrCreate.groupMask = inventoryData.GroupMask;
                            entryOrCreate.description = SLMessage.stringFromVariableUTF(inventoryData.Description);
                            entryOrCreate.isGroupOwned = inventoryData.GroupOwned;
                            entryOrCreate.everyoneMask = inventoryData.EveryoneMask;
                            entryOrCreate.assetType = inventoryData.Type;
                            entryOrCreate.invType = inventoryData.InvType;
                            entryOrCreate.groupUUID = inventoryData.GroupID;
                            entryOrCreate.name = SLMessage.stringFromVariableOEM(inventoryData.Name);
                            entryOrCreate.baseMask = inventoryData.BaseMask;
                            entryOrCreate.saleType = inventoryData.SaleType;
                            entryOrCreate.salePrice = inventoryData.SalePrice;
                            entryOrCreate.ownerUUID = inventoryData.OwnerID;
                            entryOrCreate.flags = inventoryData.Flags;
                            entryOrCreate.ownerMask = inventoryData.OwnerMask;
                            entryOrCreate.nextOwnerMask = inventoryData.NextOwnerMask;
                            entryOrCreate.assetUUID = inventoryData.AssetID;
                            entryOrCreate.creationDate = inventoryData.CreationDate;
                            entryOrCreate.creatorUUID = inventoryData.CreatorID;
                            entryOrCreate.parentUUID = folderID;
                            final SLInventoryEntry entry = this.db.findEntry(folderID);
                            if (entry == null) {
                                break Label_0440;
                            }
                            if (entryOrCreate.getId() == 0L && onInventoryCallbackListener == null && entry.typeDefault != 14 && entry.typeDefault != 2) {
                                slInventoryNewContentsEvent.AddItem(false, folderID, entryOrCreate.name);
                            }
                            entryOrCreate.parent_id = entry.getId();
                            this.db.saveEntry(entryOrCreate);
                            if (onInventoryCallbackListener != null) {
                                onInventoryCallbackListener.onInventoryCallback(entryOrCreate);
                            }
                        }
                        catch (final DBObject.DatabaseBindingException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    }
                    if (entryOrCreate.getId() != 0L) {
                        this.db.deleteEntry(entryOrCreate);
                        continue;
                    }
                    continue;
                }
            }
        }
        if (this.userManager != null) {
            final Iterator iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                this.userManager.getInventoryManager().requestFolderUpdate((UUID)iterator2.next());
            }
        }
        if (!slInventoryNewContentsEvent.isEmpty()) {
            this.eventBus.publish(slInventoryNewContentsEvent);
        }
    }
    
    public void LinkInventoryItem(final SLInventoryEntry slInventoryEntry, final UUID oldItemID, final int invType, final int type, final String s, final String s2) {
        final LinkInventoryItem linkInventoryItem = new LinkInventoryItem();
        linkInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        linkInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        linkInventoryItem.InventoryBlock_Field.FolderID = slInventoryEntry.uuid;
        linkInventoryItem.InventoryBlock_Field.TransactionID = UUID.randomUUID();
        linkInventoryItem.InventoryBlock_Field.OldItemID = oldItemID;
        linkInventoryItem.InventoryBlock_Field.Type = type;
        linkInventoryItem.InventoryBlock_Field.InvType = invType;
        linkInventoryItem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        linkInventoryItem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s2);
        linkInventoryItem.isReliable = true;
        linkInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
            @Override
            public void onMessageAcknowledged(final SLMessage slMessage) {
                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.uuid);
            }
        });
        this.SendMessage(linkInventoryItem);
    }
    
    public void MoveInventoryItem(final SLInventoryEntry slInventoryEntry, final SLInventoryEntry slInventoryEntry2) {
        final UUID parentUUID = slInventoryEntry.parentUUID;
        if (slInventoryEntry.isFolder) {
            final UpdateInventoryFolder updateInventoryFolder = new UpdateInventoryFolder();
            updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
            updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            final UpdateInventoryFolder.FolderData e = new UpdateInventoryFolder.FolderData();
            e.FolderID = slInventoryEntry.uuid;
            e.ParentID = slInventoryEntry2.uuid;
            e.Type = slInventoryEntry.typeDefault;
            e.Name = SLMessage.stringToVariableUTF(slInventoryEntry.name);
            updateInventoryFolder.FolderData_Fields.add(e);
            updateInventoryFolder.isReliable = true;
            updateInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(parentUUID);
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry2.uuid);
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.uuid);
                }
            });
            this.SendMessage(updateInventoryFolder);
        }
        else {
            final MoveInventoryItem moveInventoryItem = new MoveInventoryItem();
            moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
            moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
            moveInventoryItem.AgentData_Field.Stamp = false;
            final MoveInventoryItem.InventoryData e2 = new MoveInventoryItem.InventoryData();
            e2.FolderID = slInventoryEntry2.uuid;
            e2.ItemID = slInventoryEntry.uuid;
            e2.NewName = SLMessage.stringToVariableUTF(slInventoryEntry.name);
            moveInventoryItem.InventoryData_Fields.add(e2);
            moveInventoryItem.isReliable = true;
            moveInventoryItem.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(parentUUID);
                    SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry2.uuid);
                }
            });
            this.SendMessage(moveInventoryItem);
        }
    }
    
    public void MoveInventoryItemRaw(final UUID itemID, final String s, final UUID folderID) {
        final MoveInventoryItem moveInventoryItem = new MoveInventoryItem();
        moveInventoryItem.AgentData_Field.AgentID = this.circuitInfo.agentID;
        moveInventoryItem.AgentData_Field.SessionID = this.circuitInfo.sessionID;
        moveInventoryItem.AgentData_Field.Stamp = false;
        final MoveInventoryItem.InventoryData e = new MoveInventoryItem.InventoryData();
        e.FolderID = folderID;
        e.ItemID = itemID;
        e.NewName = SLMessage.stringToVariableUTF(s);
        moveInventoryItem.InventoryData_Fields.add(e);
        moveInventoryItem.isReliable = true;
        moveInventoryItem.setEventListener(this.reloadEvent);
        this.SendMessage(moveInventoryItem);
    }
    
    public void RenameInventoryItem(final SLInventoryEntry slInventoryEntry, final String name) {
        while (true) {
            slInventoryEntry.name = name;
            while (true) {
                try {
                    this.db.saveEntry(slInventoryEntry);
                    if (slInventoryEntry.isFolder) {
                        final UpdateInventoryFolder updateInventoryFolder = new UpdateInventoryFolder();
                        updateInventoryFolder.AgentData_Field.AgentID = this.circuitInfo.agentID;
                        updateInventoryFolder.AgentData_Field.SessionID = this.circuitInfo.sessionID;
                        final UpdateInventoryFolder.FolderData e = new UpdateInventoryFolder.FolderData();
                        e.FolderID = slInventoryEntry.uuid;
                        e.ParentID = slInventoryEntry.parentUUID;
                        e.Type = slInventoryEntry.typeDefault;
                        e.Name = SLMessage.stringToVariableUTF(name);
                        updateInventoryFolder.FolderData_Fields.add(e);
                        updateInventoryFolder.isReliable = true;
                        updateInventoryFolder.setEventListener(new SLMessageEventListener.SLMessageBaseEventListener() {
                            @Override
                            public void onMessageAcknowledged(final SLMessage slMessage) {
                                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.uuid);
                                SLInventory.this.userManager.getInventoryManager().requestFolderUpdate(slInventoryEntry.parentUUID);
                            }
                        });
                        this.SendMessage(updateInventoryFolder);
                        return;
                    }
                }
                catch (final DBObject.DatabaseBindingException ex) {
                    Debug.Warning(ex);
                    continue;
                }
                break;
            }
            this.DoUpdateInventoryItem(slInventoryEntry, (OnInventoryCallbackListener)new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$3(this, slInventoryEntry));
        }
    }
    
    public void TrashInventoryItem(final SLInventoryEntry slInventoryEntry) {
        final SLInventoryEntry specialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14);
        if (specialFolder != null) {
            this.MoveInventoryItem(slInventoryEntry, specialFolder);
        }
    }
    
    public void UpdateNotecard(@Nullable final SLInventoryEntry slInventoryEntry, final UUID uuid, final boolean b, final String s, final String s2, @Nullable final byte[] array, @Nullable final UUID uuid2, int n, @Nullable final OnNotecardUpdatedListener onNotecardUpdatedListener) {
        boolean b2;
        if (slInventoryEntry != null) {
            b2 = ((Objects.equal(slInventoryEntry.name, s) && Objects.equal(slInventoryEntry.description, s2)) ^ true);
        }
        else {
            b2 = true;
        }
        if (uuid2 == null) {
            if (slInventoryEntry == null) {
                Debug.Printf("Notecard: Creating new inventory entry.", new Object[0]);
                if (b) {
                    n = SLAssetType.AT_LSL_TEXT.getTypeCode();
                }
                else {
                    n = SLAssetType.AT_NOTECARD.getTypeCode();
                }
                int n2;
                if (b) {
                    n2 = SLInventoryType.IT_LSL.getTypeCode();
                }
                else {
                    n2 = SLInventoryType.IT_NOTECARD.getTypeCode();
                }
                this.DoCreateInventoryItem(uuid, n, n2, s, s2, (OnInventoryCallbackListener)new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$7(b, this, array, onNotecardUpdatedListener));
            }
            else {
                if (b2) {
                    slInventoryEntry.name = s;
                    slInventoryEntry.description = s2;
                    while (true) {
                        try {
                            this.db.saveEntry(slInventoryEntry);
                            Debug.Printf("Notecard: Updating existing inventory entry %s", slInventoryEntry.uuid);
                            this.DoUpdateInventoryItem(slInventoryEntry, (OnInventoryCallbackListener)new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$8(b, this, array, onNotecardUpdatedListener));
                            return;
                        }
                        catch (final DBObject.DatabaseBindingException ex) {
                            Debug.Warning(ex);
                            continue;
                        }
                        break;
                    }
                }
                this.StartUploadingNotecardContents(slInventoryEntry, null, b, array, onNotecardUpdatedListener);
            }
        }
        else if (b2 && slInventoryEntry != null) {
            slInventoryEntry.name = s;
            slInventoryEntry.description = s2;
            this.DoUpdateTaskInventoryItem(slInventoryEntry, n, new SLMessageEventListener.SLMessageBaseEventListener() {
                @Override
                public void onMessageAcknowledged(final SLMessage slMessage) {
                    SLInventory.this.userManager.getObjectsManager().requestTaskInventoryUpdate(n);
                    SLInventory.this.StartUploadingNotecardContents(slInventoryEntry, uuid2, b, array, onNotecardUpdatedListener);
                }
            });
        }
        else {
            this.StartUploadingNotecardContents(slInventoryEntry, uuid2, b, array, onNotecardUpdatedListener);
        }
    }
    
    public void UpdateStoreInventoryItem(final SLInventoryEntry slInventoryEntry) {
        try {
            this.db.saveEntry(slInventoryEntry);
            this.DoUpdateInventoryItem(slInventoryEntry, (OnInventoryCallbackListener)new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$4(this, slInventoryEntry));
        }
        catch (final DBObject.DatabaseBindingException ex) {
            Debug.Warning(ex);
        }
    }
    
    public boolean canMoveToTrash(final SLInventoryEntry slInventoryEntry) {
        final SLInventoryEntry specialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 14);
        return specialFolder != null && slInventoryEntry.parent_id != specialFolder.getId();
    }
    
    @Nullable
    public UUID findSpecialFolder(final int n) {
        UUID uuid = null;
        final SLInventoryEntry specialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), n);
        if (specialFolder != null) {
            uuid = specialFolder.uuid;
        }
        return uuid;
    }
    
    public UUID getCallingCardsFolderUUID() {
        if (this.rootFolder != null) {
            final SLInventoryEntry specialFolder = this.db.findSpecialFolder(this.rootFolder.getId(), 2);
            if (specialFolder != null) {
                return specialFolder.uuid;
            }
        }
        return null;
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
    
    void onFetchComplete(final SLInventoryFetchRequest slInventoryFetchRequest, final UUID uuid, final long n, final boolean b, final boolean b2) {
        if (this.dbExecutor != null) {
            this.dbExecutor.execute(new _$Lambda$eCHTl__kh2tUCaOJ_O9NRHJvhjs$10(b, b2, n, this, uuid));
        }
    }
    
    public static class InventoryFetchException extends IOException
    {
        public InventoryFetchException(final String message) {
            super(message);
        }
    }
    
    public static class NoInventoryItemException extends Exception
    {
        private static final long serialVersionUID = 1L;
        
        public NoInventoryItemException(final long lng) {
            super("Inventory item " + lng + " not found");
        }
        
        public NoInventoryItemException(final UUID uuid) {
            super("Inventory item " + uuid.toString() + " not found");
        }
    }
    
    interface OnInventoryCallbackListener
    {
        void onInventoryCallback(final SLInventoryEntry p0);
    }
    
    public interface OnNotecardUpdatedListener
    {
        void onNotecardUpdated(final SLInventoryEntry p0, @Nullable final String p1);
    }
}
