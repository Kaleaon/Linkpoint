// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryNewContentsEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLInventoryUpdatedEvent;
import com.lumiyaviewer.lumiya.slproto.https.GenericHTTPExecutor;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.https.SLHTTPSConnection;
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
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
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
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryEntry, SLAssetType, SLInventoryType, SLInventoryUDPFetchRequest, 
//            SLInventoryFetchRequest, SLInventoryHTTPFetchRequest

public class SLInventory extends SLModule
{
    public static class InventoryFetchException extends IOException
    {

        public InventoryFetchException(String s)
        {
            super(s);
        }
    }

    public static class NoInventoryItemException extends Exception
    {

        private static final long serialVersionUID = 1L;

        public NoInventoryItemException(long l)
        {
            super((new StringBuilder()).append("Inventory item ").append(l).append(" not found").toString());
        }

        public NoInventoryItemException(UUID uuid)
        {
            super((new StringBuilder()).append("Inventory item ").append(uuid.toString()).append(" not found").toString());
        }
    }

    static interface OnInventoryCallbackListener
    {

        public abstract void onInventoryCallback(SLInventoryEntry slinventoryentry);
    }

    public static interface OnNotecardUpdatedListener
    {

        public abstract void onNotecardUpdated(SLInventoryEntry slinventoryentry, String s);
    }


    private Map callbacks;
    private final SLCaps caps;
    private final InventoryDB db;
    private final Executor dbExecutor;
    private ExecutorService executor;
    private final String fetchCap;
    private final AtomicBoolean fetchEntireInventoryRequested = new AtomicBoolean(false);
    private final Map fetchRequests = new ConcurrentHashMap();
    private final ResultHandler folderEntryResultHandler;
    private final RequestHandler folderLoadingRequestHandler = new SimpleRequestHandler() {

        final SLInventory this$0;

        public volatile void onRequest(Object obj)
        {
            onRequest((UUID)obj);
        }

        public void onRequest(UUID uuid)
        {
            SLInventory._2D_wrap3(SLInventory.this, uuid);
        }

            
            {
                this$0 = SLInventory.this;
                super();
            }
    };
    private final ResultHandler folderLoadingResultHandler;
    private final RequestHandler folderRequestHandler = new RequestHandler() {

        final SLInventory this$0;

        public volatile void onRequest(Object obj)
        {
            onRequest((UUID)obj);
        }

        public void onRequest(UUID uuid)
        {
            Object obj;
            Debug.Printf("Inventory: folderRequestHandler: folderId = '%s'", new Object[] {
                uuid
            });
            if (SLInventory._2D_get1(SLInventory.this) == null)
            {
                break MISSING_BLOCK_LABEL_71;
            }
            obj = new SLInventoryHTTPFetchRequest(SLInventory.this, uuid, SLInventory._2D_get1(SLInventory.this));
_L1:
            SLInventory._2D_get3(SLInventory.this).put(uuid, obj);
            SLInventory._2D_wrap3(SLInventory.this, uuid);
            ((SLInventoryFetchRequest) (obj)).start();
            return;
            try
            {
                obj = new SLInventoryUDPFetchRequest(SLInventory.this, uuid);
            }
            // Misplaced declaration of an exception variable
            catch (UUID uuid)
            {
                Debug.Warning(uuid);
                return;
            }
              goto _L1
        }

        public volatile void onRequestCancelled(Object obj)
        {
            onRequestCancelled((UUID)obj);
        }

        public void onRequestCancelled(UUID uuid)
        {
            SLInventoryFetchRequest slinventoryfetchrequest = (SLInventoryFetchRequest)SLInventory._2D_get3(SLInventory.this).remove(uuid);
            SLInventory._2D_wrap3(SLInventory.this, uuid);
            if (slinventoryfetchrequest != null)
            {
                slinventoryfetchrequest.cancel();
            }
        }

            
            {
                this$0 = SLInventory.this;
                super();
            }
    };
    private AtomicInteger nextCallbackID;
    private final SubscriptionData nextFolderSubscription;
    private SLMessageEventListener reloadEvent;
    public SLInventoryEntry rootFolder;
    private boolean rootFolderFetchNeeded;
    private final SubscriptionData rootFolderSubscription;
    private final ResultHandler seachRunningResultHandler;
    private final ResultHandler searchProcessResultHandler;
    private final RequestHandler searchRequestHandler = new RequestHandler() {

        final SLInventory this$0;

        public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
        {
            SLInventory._2D_get2(SLInventory.this).set(true);
            SLInventory._2D_wrap2(SLInventory.this);
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((SubscriptionSingleKey)obj);
        }

        public void onRequestCancelled(SubscriptionSingleKey subscriptionsinglekey)
        {
            SLInventory._2D_get2(SLInventory.this).set(false);
            SLInventory._2D_wrap4(SLInventory.this);
        }

        public volatile void onRequestCancelled(Object obj)
        {
            onRequestCancelled((SubscriptionSingleKey)obj);
        }

            
            {
                this$0 = SLInventory.this;
                super();
            }
    };
    private final RequestHandler searchRunningRequestHandler = new SimpleRequestHandler() {

        final SLInventory this$0;

        public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
        {
            SLInventory._2D_wrap4(SLInventory.this);
        }

        public volatile void onRequest(Object obj)
        {
            onRequest((SubscriptionSingleKey)obj);
        }

            
            {
                this$0 = SLInventory.this;
                super();
            }
    };
    private Map udpFetchPendingRequests;
    private Map udpFetchRequests;
    private final UserManager userManager;

    static EventBus _2D_get0(SLInventory slinventory)
    {
        return slinventory.eventBus;
    }

    static String _2D_get1(SLInventory slinventory)
    {
        return slinventory.fetchCap;
    }

    static AtomicBoolean _2D_get2(SLInventory slinventory)
    {
        return slinventory.fetchEntireInventoryRequested;
    }

    static Map _2D_get3(SLInventory slinventory)
    {
        return slinventory.fetchRequests;
    }

    static UserManager _2D_get4(SLInventory slinventory)
    {
        return slinventory.userManager;
    }

    static void _2D_wrap0(SLInventory slinventory, UUID uuid, int i, UUID uuid1)
    {
        slinventory.MoveTaskInventory(uuid, i, uuid1);
    }

    static void _2D_wrap1(SLInventory slinventory, SLInventoryEntry slinventoryentry, UUID uuid, boolean flag, byte abyte0[], OnNotecardUpdatedListener onnotecardupdatedlistener)
    {
        slinventory.StartUploadingNotecardContents(slinventoryentry, uuid, flag, abyte0, onnotecardupdatedlistener);
    }

    static void _2D_wrap2(SLInventory slinventory)
    {
        slinventory.fetchNextFolder();
    }

    static void _2D_wrap3(SLInventory slinventory, UUID uuid)
    {
        slinventory.updateFolderLoadingStatus(uuid);
    }

    static void _2D_wrap4(SLInventory slinventory)
    {
        slinventory.updateSearchRunningStatus();
    }

    public SLInventory(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        super(slagentcircuit);
        rootFolder = null;
        executor = null;
        nextCallbackID = new AtomicInteger(1);
        rootFolderFetchNeeded = false;
        callbacks = Collections.synchronizedMap(new HashMap());
        udpFetchRequests = Collections.synchronizedMap(new HashMap());
        udpFetchPendingRequests = Collections.synchronizedMap(new HashMap());
        reloadEvent = new SLMessageEventListener() {

            final SLInventory this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                slmessage = new SLInventoryUpdatedEvent(null, null, true);
                SLInventory._2D_get0(SLInventory.this).publish(slmessage);
            }

            public void onMessageTimeout(SLMessage slmessage)
            {
            }

            
            {
                this$0 = SLInventory.this;
                super();
            }
        };
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        InventoryDB inventorydb;
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        } else
        {
            inventorydb = null;
        }
        db = inventorydb;
        caps = slcaps;
        fetchCap = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.FetchInventoryDescendents2);
        if (userManager != null)
        {
            slcaps = userManager.getInventoryManager().getExecutor();
        } else
        {
            slcaps = null;
        }
        dbExecutor = slcaps;
        if (userManager != null)
        {
            userManager.getInventoryManager().setCurrentSessionID(slagentcircuit.getAuthReply().sessionID);
            userManager.getInventoryManager().setRootFolder(slagentcircuit.getAuthReply().inventoryRoot);
        }
        try
        {
            if (db != null)
            {
                Debug.Printf("Inventory: creating root folder with folderUUID %s", new Object[] {
                    slagentcircuit.getAuthReply().inventoryRoot.toString()
                });
                rootFolder = db.findEntryOrCreate(slagentcircuit.getAuthReply().inventoryRoot);
                if (rootFolder.getId() == 0L)
                {
                    rootFolder.name = "Inventory";
                    rootFolder.isFolder = true;
                    rootFolder.parent_id = 0L;
                    rootFolder.agentUUID = slagentcircuit.circuitInfo.agentID;
                    db.saveEntry(rootFolder);
                    rootFolderFetchNeeded = true;
                }
            }
            Debug.Printf("Inventory: ready.", new Object[0]);
        }
        // Misplaced declaration of an exception variable
        catch (SLAgentCircuit slagentcircuit)
        {
            Debug.Warning(slagentcircuit);
        }
        if (userManager != null)
        {
            slagentcircuit = userManager.getInventoryManager().getFolderRequestSource().attachRequestHandler(folderRequestHandler);
        } else
        {
            slagentcircuit = null;
        }
        folderEntryResultHandler = slagentcircuit;
        if (userManager != null)
        {
            folderLoadingResultHandler = userManager.getInventoryManager().getFolderLoadingRequestSource().attachRequestHandler(new AsyncRequestHandler(dbExecutor, folderLoadingRequestHandler));
            seachRunningResultHandler = userManager.getInventoryManager().getSearchRunning().attachRequestHandler(new AsyncRequestHandler(dbExecutor, searchRunningRequestHandler));
            searchProcessResultHandler = userManager.getInventoryManager().getSearchProcessRequestSource().attachRequestHandler(new AsyncRequestHandler(dbExecutor, searchRequestHandler));
            nextFolderSubscription = new SubscriptionData(dbExecutor, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs(this), new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls2(this));
            if (rootFolderFetchNeeded)
            {
                rootFolderSubscription = new SubscriptionData(dbExecutor, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls1(this));
                return;
            } else
            {
                rootFolderSubscription = null;
                return;
            }
        } else
        {
            folderLoadingResultHandler = null;
            searchProcessResultHandler = null;
            nextFolderSubscription = null;
            seachRunningResultHandler = null;
            rootFolderSubscription = null;
            return;
        }
    }

    private void DoCreateInventoryItem(UUID uuid, int i, int j, String s, String s1, OnInventoryCallbackListener oninventorycallbacklistener)
    {
        CreateInventoryItem createinventoryitem = new CreateInventoryItem();
        createinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        createinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        createinventoryitem.InventoryBlock_Field.CallbackID = getNextCallbackID();
        createinventoryitem.InventoryBlock_Field.FolderID = uuid;
        createinventoryitem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createinventoryitem.InventoryBlock_Field.NextOwnerMask = 0x7fffffff;
        createinventoryitem.InventoryBlock_Field.Type = i;
        createinventoryitem.InventoryBlock_Field.InvType = j;
        createinventoryitem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        createinventoryitem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s1);
        callbacks.put(Integer.valueOf(createinventoryitem.InventoryBlock_Field.CallbackID), oninventorycallbacklistener);
        createinventoryitem.isReliable = true;
        SendMessage(createinventoryitem);
    }

    private void DoUpdateInventoryItem(final SLInventoryEntry entry, final OnInventoryCallbackListener callbackListener)
    {
        UpdateInventoryItem updateinventoryitem = new UpdateInventoryItem();
        updateinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        updateinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        updateinventoryitem.AgentData_Field.TransactionID = UUID.randomUUID();
        com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryItem.InventoryData();
        inventorydata.ItemID = entry.uuid;
        inventorydata.FolderID = entry.parentUUID;
        inventorydata.CallbackID = 0;
        inventorydata.CreatorID = entry.creatorUUID;
        inventorydata.OwnerID = entry.ownerUUID;
        inventorydata.GroupID = entry.groupUUID;
        inventorydata.BaseMask = entry.baseMask;
        inventorydata.OwnerMask = entry.ownerMask;
        inventorydata.GroupMask = entry.groupMask;
        inventorydata.EveryoneMask = entry.everyoneMask;
        inventorydata.NextOwnerMask = entry.nextOwnerMask;
        inventorydata.GroupOwned = entry.isGroupOwned;
        inventorydata.TransactionID = new UUID(0L, 0L);
        inventorydata.Type = entry.assetType;
        inventorydata.InvType = entry.invType;
        inventorydata.Flags = entry.flags;
        inventorydata.SaleType = entry.saleType;
        inventorydata.SalePrice = entry.salePrice;
        inventorydata.Name = SLMessage.stringToVariableOEM(entry.name);
        inventorydata.Description = SLMessage.stringToVariableUTF(entry.description);
        inventorydata.CreationDate = entry.creationDate;
        inventorydata.CRC = 0;
        updateinventoryitem.InventoryData_Fields.add(inventorydata);
        updateinventoryitem.isReliable = true;
        Debug.Printf("Update inventory callback %d", new Object[] {
            Integer.valueOf(inventorydata.CallbackID)
        });
        updateinventoryitem.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLInventory this$0;
            final OnInventoryCallbackListener val$callbackListener;
            final SLInventoryEntry val$entry;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                super.onMessageAcknowledged(slmessage);
                callbackListener.onInventoryCallback(entry);
            }

            
            {
                this$0 = SLInventory.this;
                callbackListener = oninventorycallbacklistener;
                entry = slinventoryentry;
                super();
            }
        });
        SendMessage(updateinventoryitem);
    }

    private void DoUpdateTaskInventoryItem(SLInventoryEntry slinventoryentry, int i, SLMessageEventListener slmessageeventlistener)
    {
        UpdateTaskInventory updatetaskinventory = new UpdateTaskInventory();
        updatetaskinventory.AgentData_Field.AgentID = circuitInfo.agentID;
        updatetaskinventory.AgentData_Field.SessionID = circuitInfo.sessionID;
        updatetaskinventory.UpdateData_Field.LocalID = i;
        updatetaskinventory.UpdateData_Field.Key = 0;
        com.lumiyaviewer.lumiya.slproto.messages.UpdateTaskInventory.InventoryData inventorydata = updatetaskinventory.InventoryData_Field;
        inventorydata.ItemID = slinventoryentry.uuid;
        inventorydata.FolderID = slinventoryentry.parentUUID;
        inventorydata.CreatorID = slinventoryentry.creatorUUID;
        inventorydata.OwnerID = slinventoryentry.ownerUUID;
        inventorydata.GroupID = slinventoryentry.groupUUID;
        inventorydata.BaseMask = slinventoryentry.baseMask;
        inventorydata.OwnerMask = slinventoryentry.ownerMask;
        inventorydata.GroupMask = slinventoryentry.groupMask;
        inventorydata.EveryoneMask = slinventoryentry.everyoneMask;
        inventorydata.NextOwnerMask = slinventoryentry.nextOwnerMask;
        inventorydata.GroupOwned = slinventoryentry.isGroupOwned;
        inventorydata.TransactionID = new UUID(0L, 0L);
        inventorydata.Type = slinventoryentry.assetType;
        inventorydata.InvType = slinventoryentry.invType;
        inventorydata.Flags = slinventoryentry.flags;
        inventorydata.SaleType = slinventoryentry.saleType;
        inventorydata.SalePrice = slinventoryentry.salePrice;
        inventorydata.Name = SLMessage.stringToVariableOEM(slinventoryentry.name);
        inventorydata.Description = SLMessage.stringToVariableUTF(slinventoryentry.description);
        inventorydata.CreationDate = slinventoryentry.creationDate;
        inventorydata.CRC = 0;
        updatetaskinventory.isReliable = true;
        updatetaskinventory.setEventListener(slmessageeventlistener);
        SendMessage(updatetaskinventory);
    }

    private void MoveTaskInventory(UUID uuid, int i, UUID uuid1)
    {
        MoveTaskInventory movetaskinventory = new MoveTaskInventory();
        movetaskinventory.AgentData_Field.AgentID = circuitInfo.agentID;
        movetaskinventory.AgentData_Field.SessionID = circuitInfo.sessionID;
        movetaskinventory.AgentData_Field.FolderID = uuid;
        movetaskinventory.InventoryData_Field.LocalID = i;
        movetaskinventory.InventoryData_Field.ItemID = uuid1;
        movetaskinventory.isReliable = true;
        SendMessage(movetaskinventory);
    }

    private void StartUploadingNotecardContents(SLInventoryEntry slinventoryentry, UUID uuid, boolean flag, byte abyte0[], OnNotecardUpdatedListener onnotecardupdatedlistener)
    {
        GenericHTTPExecutor.getInstance().execute(new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls9(flag, this, slinventoryentry, abyte0, uuid, onnotecardupdatedlistener));
    }

    private String UploadNotecardContents(SLInventoryEntry slinventoryentry, UUID uuid, boolean flag, byte abyte0[])
    {
        Object obj2;
        boolean flag1;
        flag1 = false;
        obj2 = null;
        Object obj1 = new LLSDXMLRequest();
        if (!flag) goto _L2; else goto _L1
_L1:
        Object obj3 = getCaps();
        if (uuid != null) goto _L4; else goto _L3
_L3:
        Object obj = com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateScriptAgent;
_L16:
        obj = ((SLCaps) (obj3)).getCapabilityOrThrow(((com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability) (obj)));
        int i = 1;
_L7:
        obj3 = ImmutableMap.builder();
        ((com.google.common.collect.ImmutableMap.Builder) (obj3)).put("item_id", new LLSDUUID(slinventoryentry.uuid));
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_92;
        }
        ((com.google.common.collect.ImmutableMap.Builder) (obj3)).put("task_id", new LLSDUUID(uuid));
        if (!i)
        {
            break MISSING_BLOCK_LABEL_137;
        }
        ((com.google.common.collect.ImmutableMap.Builder) (obj3)).put("target", new LLSDString("mono"));
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_137;
        }
        ((com.google.common.collect.ImmutableMap.Builder) (obj3)).put("is_script_running", new LLSDInt(1));
        obj3 = new LLSDMap(((com.google.common.collect.ImmutableMap.Builder) (obj3)).build());
        Debug.Log((new StringBuilder()).append("Notecard upload request: Initial uploader request: ").append(((LLSDMap) (obj3)).serializeToXML()).toString());
        obj1 = ((LLSDXMLRequest) (obj1)).PerformRequest(((String) (obj)), ((LLSDNode) (obj3)));
        Debug.Log((new StringBuilder()).append("Notecard upload request: Initial uploader reply: ").append(((LLSDNode) (obj1)).serializeToXML()).toString());
        obj3 = ((LLSDNode) (obj1)).byKey("uploader").asString();
        obj1 = obj3;
        if (obj3 == null)
        {
            break MISSING_BLOCK_LABEL_247;
        }
        obj1 = SLCaps.repairURL(((String) (obj)), ((String) (obj3)));
        abyte0 = (new okhttp3.Request.Builder()).url(Strings.nullToEmpty(((String) (obj1)))).post(RequestBody.create(MediaType.parse("application/vnd.ll.notecard"), abyte0)).header("Accept", "application/llsd+xml").build();
        obj = SLHTTPSConnection.getOkHttpClient().newCall(abyte0).execute();
        if (obj != null) goto _L6; else goto _L5
_L4:
        obj = com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateScriptTask;
        continue; /* Loop/switch isn't completed */
_L2:
        obj4 = getCaps();
        if (uuid != null)
        {
            break MISSING_BLOCK_LABEL_368;
        }
        obj = com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateNotecardAgentInventory;
_L8:
        obj = ((SLCaps) (obj4)).getCapabilityOrThrow(((com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability) (obj)));
        i = 0;
          goto _L7
        obj = com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateNotecardTaskInventory;
          goto _L8
_L6:
        if (!((Response) (obj)).isSuccessful())
        {
            throw new IOException((new StringBuilder()).append("Response error code ").append(((Response) (obj)).code()).toString());
        }
        break MISSING_BLOCK_LABEL_433;
          goto _L7
        slinventoryentry;
        ((Response) (obj)).close();
        throw slinventoryentry;
        llsdnode = LLSDNode.parseXML(((Response) (obj)).body().byteStream(), null);
        Debug.Log((new StringBuilder()).append("upload reply: ").append(llsdnode.serializeToXML()).toString());
        s = llsdnode.byKey("state").asString();
        obj4 = llsdnode.byKey("new_asset").asUUID();
        abyte0 = obj2;
        if (!s.equals("complete")) goto _L10; else goto _L9
_L9:
        if (uuid != null)
        {
            break MISSING_BLOCK_LABEL_563;
        }
        uuid = db.findEntry(slinventoryentry.uuid);
        if (uuid == null)
        {
            break MISSING_BLOCK_LABEL_549;
        }
        uuid.assetUUID = ((UUID) (obj4));
        db.saveEntry(uuid);
        userManager.getInventoryManager().requestFolderUpdate(slinventoryentry.parentUUID);
        abyte0 = obj2;
        if (!llsdnode.keyExists("compiled")) goto _L10; else goto _L11
_L11:
        abyte0 = obj2;
        if (llsdnode.byKey("compiled").asBoolean()) goto _L10; else goto _L12
_L12:
        abyte0 = obj2;
        if (!llsdnode.keyExists("errors")) goto _L10; else goto _L13
_L13:
        llsdnode = llsdnode.byKey("errors");
        slinventoryentry = "";
        i = ((flag1) ? 1 : 0);
_L14:
        abyte0 = slinventoryentry;
        if (i >= llsdnode.getCount())
        {
            break; /* Loop/switch isn't completed */
        }
        uuid = slinventoryentry;
        if (i == 0)
        {
            break MISSING_BLOCK_LABEL_670;
        }
        uuid = (new StringBuilder()).append(slinventoryentry).append("; ").toString();
        slinventoryentry = (new StringBuilder()).append(uuid).append(llsdnode.byIndex(i).asString()).toString();
        i++;
        if (true) goto _L14; else goto _L10
_L10:
        ((Response) (obj)).close();
        return abyte0;
_L5:
        LLSDNode llsdnode;
        Object obj4;
        String s;
        try
        {
            throw new IOException("Null response");
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
            return "Failed to upload inventory asset";
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
            return "Failed to upload inventory asset";
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
            return "Failed to upload inventory asset";
        }
        return "Failed to upload inventory asset";
        if (true) goto _L16; else goto _L15
_L15:
    }

    private void fetchNextFolder()
    {
        Object obj2;
        if (!fetchEntireInventoryRequested.get())
        {
            break MISSING_BLOCK_LABEL_245;
        }
        obj2 = circuitInfo.sessionID;
        Object obj;
        obj = db.getDatabase();
        Object obj1 = Long.toString(((UUID) (obj2)).getMostSignificantBits());
        obj2 = Long.toString(((UUID) (obj2)).getLeastSignificantBits());
        obj = ((SQLiteDatabase) (obj)).query("Entries", new String[] {
            "uuid_high", "uuid_low"
        }, "isFolder AND (sessionID_high != ? OR sessionID_low != ?)", new String[] {
            obj1, obj2
        }, null, null, null, "1");
        if (!((Cursor) (obj)).moveToFirst())
        {
            break MISSING_BLOCK_LABEL_157;
        }
        obj1 = UUIDPool.getUUID(((Cursor) (obj)).getLong(0), ((Cursor) (obj)).getLong(1));
        Debug.Printf("InventorySearch: fetching next folder: %s", new Object[] {
            obj1
        });
        nextFolderSubscription.subscribe(userManager.getInventoryManager().getFolderEntryPool(), obj1);
        updateSearchRunningStatus();
_L1:
        ((Cursor) (obj)).close();
        return;
        try
        {
            Debug.Printf("InventorySearch: no more folders to fetch", new Object[0]);
            searchProcessResultHandler.onResultData(SubscriptionSingleKey.Value, Boolean.valueOf(true));
            nextFolderSubscription.unsubscribe();
            updateSearchRunningStatus();
        }
        catch (Exception exception)
        {
            Debug.Printf("InventorySearch: error while fetching folders", new Object[0]);
            Debug.Warning(exception);
            fetchEntireInventoryRequested.set(false);
            searchProcessResultHandler.onResultError(SubscriptionSingleKey.Value, exception);
            nextFolderSubscription.unsubscribe();
            updateSearchRunningStatus();
            return;
        }
          goto _L1
        updateSearchRunningStatus();
        return;
    }

    private int getNextCallbackID()
    {
        return nextCallbackID.getAndIncrement();
    }

    private void onNextFolderError(Throwable throwable)
    {
        fetchNextFolder();
    }

    private void onNextFolderFetched(SLInventoryEntry slinventoryentry)
    {
        if (Objects.equal(slinventoryentry.sessionID, circuitInfo.sessionID))
        {
            fetchNextFolder();
        }
    }

    private void onRootFolderFetched(SLInventoryEntry slinventoryentry)
    {
        rootFolderFetchNeeded = false;
        if (rootFolderSubscription != null)
        {
            rootFolderSubscription.unsubscribe();
        }
        Debug.Printf("Inventory: Fetched root folder.", new Object[0]);
    }

    private void updateFolderLoadingStatus(UUID uuid)
    {
        if (folderLoadingResultHandler != null)
        {
            folderLoadingResultHandler.onResultData(uuid, Boolean.valueOf(fetchRequests.containsKey(uuid)));
        }
    }

    private void updateSearchRunningStatus()
    {
        if (searchRunningRequestHandler != null)
        {
            ResultHandler resulthandler = seachRunningResultHandler;
            SubscriptionSingleKey subscriptionsinglekey = SubscriptionSingleKey.Value;
            boolean flag;
            if (fetchEntireInventoryRequested.get())
            {
                flag = nextFolderSubscription.isSubscribed();
            } else
            {
                flag = false;
            }
            resulthandler.onResultData(subscriptionsinglekey, Boolean.valueOf(flag));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_2D_mthref_2D_0(SLInventoryEntry slinventoryentry)
    {
        onNextFolderFetched(slinventoryentry);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_2D_mthref_2D_1(Throwable throwable)
    {
        onNextFolderError(throwable);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_2D_mthref_2D_2(SLInventoryEntry slinventoryentry)
    {
        onRootFolderFetched(slinventoryentry);
    }

    public Collection CollectGiveableItems(SLInventoryEntry slinventoryentry)
    {
        ArrayList arraylist = new ArrayList();
        slinventoryentry = SLInventoryEntry.query(db.getDatabase(), "parent_id = ?", new String[] {
            Long.toString(slinventoryentry.getId())
        }, null);
        if (slinventoryentry != null)
        {
            do
            {
                if (!slinventoryentry.moveToNext())
                {
                    break;
                }
                SLInventoryEntry slinventoryentry1 = new SLInventoryEntry(slinventoryentry);
                if (slinventoryentry1.isFolder)
                {
                    arraylist.add(slinventoryentry1);
                    arraylist.addAll(CollectGiveableItems(slinventoryentry1));
                } else
                if ((slinventoryentry1.baseMask & slinventoryentry1.ownerMask & 0x2000) != 0)
                {
                    arraylist.add(slinventoryentry1);
                }
            } while (true);
            slinventoryentry.close();
        }
        return arraylist;
    }

    public void CopyInventoryFromNotecard(UUID uuid, UUID uuid1, UUID uuid2, Runnable runnable)
    {
        GenericHTTPExecutor.getInstance().execute(new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls5(this, uuid, uuid1, uuid2, runnable));
    }

    public void CopyInventoryItem(SLInventoryEntry slinventoryentry, SLInventoryEntry slinventoryentry1)
    {
        CopyInventoryItem copyinventoryitem = new CopyInventoryItem();
        copyinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        copyinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.CopyInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.CopyInventoryItem.InventoryData();
        inventorydata.CallbackID = getNextCallbackID();
        inventorydata.OldAgentID = slinventoryentry.agentUUID;
        inventorydata.OldItemID = slinventoryentry.uuid;
        inventorydata.NewFolderID = slinventoryentry1.uuid;
        inventorydata.NewName = SLMessage.stringToVariableOEM(slinventoryentry.name);
        copyinventoryitem.InventoryData_Fields.add(inventorydata);
        copyinventoryitem.isReliable = true;
        SendMessage(copyinventoryitem);
    }

    public void CopyObjectContents(String s, int i, Set set, Function function)
    {
        if (rootFolder != null)
        {
            set = ImmutableSet.copyOf(set);
            dbExecutor.execute(new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls6(i, this, s, set, function));
            return;
        } else
        {
            function.apply(null);
            return;
        }
    }

    public void DeleteInventoryItem(final SLInventoryEntry item)
    {
        if (item.isFolder)
        {
            RemoveInventoryFolder removeinventoryfolder = new RemoveInventoryFolder();
            removeinventoryfolder.AgentData_Field.AgentID = circuitInfo.agentID;
            removeinventoryfolder.AgentData_Field.SessionID = circuitInfo.sessionID;
            com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryFolder.FolderData folderdata = new com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryFolder.FolderData();
            folderdata.FolderID = item.uuid;
            removeinventoryfolder.FolderData_Fields.add(folderdata);
            removeinventoryfolder.isReliable = true;
            removeinventoryfolder.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$item;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(item.parentUUID);
                }

            
            {
                this$0 = SLInventory.this;
                item = slinventoryentry;
                super();
            }
            });
            SendMessage(removeinventoryfolder);
            return;
        } else
        {
            RemoveInventoryItem removeinventoryitem = new RemoveInventoryItem();
            removeinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
            removeinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
            com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem.InventoryData();
            inventorydata.ItemID = item.uuid;
            removeinventoryitem.InventoryData_Fields.add(inventorydata);
            removeinventoryitem.isReliable = true;
            removeinventoryitem.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$item;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(item.parentUUID);
                }

            
            {
                this$0 = SLInventory.this;
                item = slinventoryentry;
                super();
            }
            });
            SendMessage(removeinventoryitem);
            return;
        }
    }

    public void DeleteInventoryItemRaw(UUID uuid)
    {
        RemoveInventoryItem removeinventoryitem = new RemoveInventoryItem();
        removeinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        removeinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryItem.InventoryData();
        inventorydata.ItemID = uuid;
        removeinventoryitem.InventoryData_Fields.add(inventorydata);
        removeinventoryitem.isReliable = true;
        removeinventoryitem.setEventListener(reloadEvent);
        SendMessage(removeinventoryitem);
    }

    public void DeleteMultiInventoryItemRaw(final SLInventoryEntry parentFolder, List list)
    {
        RemoveInventoryObjects removeinventoryobjects = new RemoveInventoryObjects();
        removeinventoryobjects.AgentData_Field.AgentID = circuitInfo.agentID;
        removeinventoryobjects.AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryObjects.ItemData itemdata;
        for (list = list.iterator(); list.hasNext(); removeinventoryobjects.ItemData_Fields.add(itemdata))
        {
            UUID uuid = (UUID)list.next();
            itemdata = new com.lumiyaviewer.lumiya.slproto.messages.RemoveInventoryObjects.ItemData();
            itemdata.ItemID = uuid;
        }

        removeinventoryobjects.isReliable = true;
        removeinventoryobjects.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLInventory this$0;
            final SLInventoryEntry val$parentFolder;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(parentFolder.uuid);
            }

            
            {
                this$0 = SLInventory.this;
                parentFolder = slinventoryentry;
                super();
            }
        });
        SendMessage(removeinventoryobjects);
    }

    public UUID DoCreateNewFolder(final SLInventoryEntry parentEntry, String s, boolean flag, final Function onFolderCreated)
    {
        final UUID newFolderUUID = UUID.randomUUID();
        CreateInventoryFolder createinventoryfolder;
        if (flag)
        {
            try
            {
                SLInventoryEntry slinventoryentry = db.findEntryOrCreate(newFolderUUID);
                slinventoryentry.resetId();
                slinventoryentry.uuid = newFolderUUID;
                slinventoryentry.parent_id = parentEntry.getId();
                slinventoryentry.parentUUID = parentEntry.uuid;
                slinventoryentry.name = s;
                slinventoryentry.description = "";
                slinventoryentry.agentUUID = circuitInfo.agentID;
                slinventoryentry.isFolder = true;
                slinventoryentry.typeDefault = -1;
                slinventoryentry.version = 0;
                db.saveEntry(slinventoryentry);
            }
            catch (com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception)
            {
                Debug.Warning(databasebindingexception);
            }
        }
        Debug.Printf("Inventory: Creating new folder with uuid = %s, parent %s", new Object[] {
            newFolderUUID, parentEntry.uuid
        });
        createinventoryfolder = new CreateInventoryFolder();
        createinventoryfolder.AgentData_Field.AgentID = circuitInfo.agentID;
        createinventoryfolder.AgentData_Field.SessionID = circuitInfo.sessionID;
        createinventoryfolder.FolderData_Field.FolderID = newFolderUUID;
        createinventoryfolder.FolderData_Field.ParentID = parentEntry.uuid;
        createinventoryfolder.FolderData_Field.Type = -1;
        createinventoryfolder.FolderData_Field.Name = SLMessage.stringToVariableOEM(s);
        createinventoryfolder.isReliable = true;
        createinventoryfolder.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLInventory this$0;
            final UUID val$newFolderUUID;
            final Function val$onFolderCreated;
            final SLInventoryEntry val$parentEntry;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                Debug.Printf("Inventory: new folder created with uuid = %s, parent %s", new Object[] {
                    newFolderUUID, parentEntry.uuid
                });
                if (SLInventory._2D_get4(SLInventory.this) != null)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(parentEntry.uuid);
                }
                if (onFolderCreated != null)
                {
                    onFolderCreated.apply(newFolderUUID);
                }
            }

            
            {
                this$0 = SLInventory.this;
                newFolderUUID = uuid;
                parentEntry = slinventoryentry;
                onFolderCreated = function;
                super();
            }
        });
        SendMessage(createinventoryfolder);
        return newFolderUUID;
    }

    public void DoCreateNewLandmark(SLInventoryEntry slinventoryentry, String s, String s1)
    {
        CreateInventoryItem createinventoryitem = new CreateInventoryItem();
        createinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        createinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        createinventoryitem.InventoryBlock_Field.CallbackID = getNextCallbackID();
        createinventoryitem.InventoryBlock_Field.FolderID = slinventoryentry.uuid;
        createinventoryitem.InventoryBlock_Field.TransactionID = new UUID(0L, 0L);
        createinventoryitem.InventoryBlock_Field.NextOwnerMask = 0x7fffffff;
        createinventoryitem.InventoryBlock_Field.Type = SLAssetType.AT_LANDMARK.getTypeCode();
        createinventoryitem.InventoryBlock_Field.InvType = SLInventoryType.IT_LANDMARK.getTypeCode();
        createinventoryitem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        createinventoryitem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s1);
        createinventoryitem.isReliable = true;
        SendMessage(createinventoryitem);
    }

    public void HandleBulkUpdateInventory(LLSDNode llsdnode)
    {
        SLInventoryNewContentsEvent slinventorynewcontentsevent;
        HashSet hashset;
        Debug.Printf("BulkUpdateInventory: EventQueue event", new Object[0]);
        slinventorynewcontentsevent = new SLInventoryNewContentsEvent();
        hashset = new HashSet();
        if (!llsdnode.keyExists("FolderData")) goto _L2; else goto _L1
_L1:
        LLSDNode llsdnode1 = llsdnode.byKey("FolderData");
        int i = 0;
_L17:
        if (i >= llsdnode1.getCount()) goto _L2; else goto _L3
_L3:
        LLSDNode llsdnode2;
        UUID uuid1;
        llsdnode2 = llsdnode1.byIndex(i);
        uuid1 = llsdnode2.byKey("FolderID").asUUID();
        if (uuid1.getLeastSignificantBits() == 0L && uuid1.getMostSignificantBits() == 0L) goto _L5; else goto _L4
_L4:
        Debug.Printf("Inventory: BulkUpdateInventory got folder %s", new Object[] {
            uuid1.toString()
        });
        Object obj = null;
        SLInventoryEntry slinventoryentry;
        UUID uuid2;
        SLInventoryEntry slinventoryentry1;
        if (llsdnode2.keyExists("CallbackID"))
        {
            Debug.Printf("Inventory: got callback id %d", new Object[] {
                Integer.valueOf(llsdnode2.byKey("CallbackID").asInt())
            });
            int j = llsdnode2.byKey("CallbackID").asInt();
            obj = (OnInventoryCallbackListener)callbacks.remove(Integer.valueOf(j));
        }
        slinventoryentry = db.findEntryOrCreate(uuid1);
        uuid2 = llsdnode2.byKey("ParentID").asUUID();
        slinventoryentry1 = db.findEntry(uuid2);
        if (slinventoryentry1 == null) goto _L7; else goto _L6
_L6:
        slinventoryentry.parent_id = slinventoryentry1.getId();
        slinventoryentry.parentUUID = uuid2;
        slinventoryentry.name = llsdnode2.byKey("Name").asString();
        slinventoryentry.typeDefault = llsdnode2.byKey("Type").asInt();
        slinventoryentry.isFolder = true;
        if (slinventoryentry.getId() != 0L || obj != null)
        {
            break MISSING_BLOCK_LABEL_311;
        }
        slinventorynewcontentsevent.AddItem(true, uuid1, slinventoryentry.name);
        db.saveEntry(slinventoryentry);
        hashset.add(uuid1);
        hashset.add(uuid2);
_L9:
        if (obj == null)
        {
            break; /* Loop/switch isn't completed */
        }
        UUID uuid;
        Object obj1;
        Object obj2;
        int k;
        try
        {
            ((OnInventoryCallbackListener) (obj)).onInventoryCallback(slinventoryentry);
            break; /* Loop/switch isn't completed */
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            Debug.Warning(llsdnode);
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            Debug.Warning(llsdnode);
        }
          goto _L8
_L7:
        hashset.add(uuid2);
        if (slinventoryentry.getId() != 0L)
        {
            db.deleteEntry(slinventoryentry);
        }
        if (true) goto _L9; else goto _L5
_L2:
        if (!llsdnode.keyExists("ItemData")) goto _L8; else goto _L10
_L10:
        obj = llsdnode.byKey("ItemData");
        i = 0;
_L18:
        if (i >= ((LLSDNode) (obj)).getCount()) goto _L8; else goto _L11
_L11:
        obj1 = ((LLSDNode) (obj)).byIndex(i);
        obj2 = ((LLSDNode) (obj1)).byKey("ItemID").asUUID();
        if (((UUID) (obj2)).getLeastSignificantBits() == 0L && ((UUID) (obj2)).getMostSignificantBits() == 0L) goto _L13; else goto _L12
_L12:
        Debug.Printf("Inventory: BulkUpdateInventory got item %s", new Object[] {
            ((UUID) (obj2)).toString()
        });
        uuid = ((LLSDNode) (obj1)).byKey("FolderID").asUUID();
        llsdnode = null;
        if (((LLSDNode) (obj1)).keyExists("CallbackID"))
        {
            Debug.Printf("Inventory: got callback id %d", new Object[] {
                Integer.valueOf(((LLSDNode) (obj1)).byKey("CallbackID").asInt())
            });
            k = ((LLSDNode) (obj1)).byKey("CallbackID").asInt();
            llsdnode = (OnInventoryCallbackListener)callbacks.remove(Integer.valueOf(k));
        }
        obj2 = db.findEntryOrCreate(((UUID) (obj2)));
        hashset.add(uuid);
        obj2.groupMask = ((LLSDNode) (obj1)).byKey("GroupMask").asInt();
        obj2.description = ((LLSDNode) (obj1)).byKey("Description").asString();
        obj2.isGroupOwned = ((LLSDNode) (obj1)).byKey("GroupOwned").asBoolean();
        obj2.everyoneMask = ((LLSDNode) (obj1)).byKey("EveryoneMask").asInt();
        obj2.assetType = ((LLSDNode) (obj1)).byKey("Type").asInt();
        obj2.invType = ((LLSDNode) (obj1)).byKey("InvType").asInt();
        obj2.groupUUID = ((LLSDNode) (obj1)).byKey("GroupID").asUUID();
        obj2.name = ((LLSDNode) (obj1)).byKey("Name").asString();
        obj2.baseMask = ((LLSDNode) (obj1)).byKey("BaseMask").asInt();
        obj2.saleType = ((LLSDNode) (obj1)).byKey("SaleType").asInt();
        obj2.salePrice = ((LLSDNode) (obj1)).byKey("SalePrice").asInt();
        obj2.ownerUUID = ((LLSDNode) (obj1)).byKey("OwnerID").asUUID();
        obj2.flags = ((LLSDNode) (obj1)).byKey("Flags").asInt();
        obj2.ownerMask = ((LLSDNode) (obj1)).byKey("OwnerMask").asInt();
        obj2.nextOwnerMask = ((LLSDNode) (obj1)).byKey("NextOwnerMask").asInt();
        obj2.assetUUID = ((LLSDNode) (obj1)).byKey("AssetID").asUUID();
        obj2.creationDate = ((LLSDNode) (obj1)).byKey("CreationDate").asInt();
        obj2.parentUUID = uuid;
        obj1 = db.findEntry(uuid);
        if (obj1 == null) goto _L15; else goto _L14
_L14:
        if (((SLInventoryEntry) (obj2)).getId() != 0L || llsdnode != null)
        {
            break MISSING_BLOCK_LABEL_974;
        }
        if (((SLInventoryEntry) (obj1)).typeDefault != 14 && ((SLInventoryEntry) (obj1)).typeDefault != 2)
        {
            slinventorynewcontentsevent.AddItem(false, uuid, ((SLInventoryEntry) (obj2)).name);
        }
        obj2.parent_id = ((SLInventoryEntry) (obj1)).getId();
        db.saveEntry(((SLInventoryEntry) (obj2)));
_L16:
        if (llsdnode == null)
        {
            break; /* Loop/switch isn't completed */
        }
        llsdnode.onInventoryCallback(((SLInventoryEntry) (obj2)));
        break; /* Loop/switch isn't completed */
_L15:
        if (((SLInventoryEntry) (obj2)).getId() != 0L)
        {
            db.deleteEntry(((SLInventoryEntry) (obj2)));
        }
        if (true) goto _L16; else goto _L13
_L8:
        if (userManager != null)
        {
            for (llsdnode = hashset.iterator(); llsdnode.hasNext(); userManager.getInventoryManager().requestFolderUpdate(((UUID) (obj))))
            {
                obj = (UUID)llsdnode.next();
            }

        }
        if (!slinventorynewcontentsevent.isEmpty())
        {
            eventBus.publish(slinventorynewcontentsevent);
        }
        return;
_L5:
        i++;
          goto _L17
_L13:
        i++;
          goto _L18
    }

    public void HandleCircuitReady()
    {
        super.HandleCircuitReady();
        if (rootFolderFetchNeeded && rootFolderSubscription != null && rootFolder != null && userManager != null)
        {
            Debug.Printf("Inventory: Fetching root folder: %s", new Object[] {
                rootFolder.uuid
            });
            rootFolderSubscription.subscribe(userManager.getInventoryManager().getFolderEntryPool(), rootFolder.uuid);
        }
    }

    public void HandleCloseCircuit()
    {
        if (rootFolderSubscription != null)
        {
            rootFolderSubscription.unsubscribe();
        }
        if (nextFolderSubscription != null)
        {
            nextFolderSubscription.unsubscribe();
        }
        if (userManager != null)
        {
            userManager.getInventoryManager().getFolderRequestSource().detachRequestHandler(folderRequestHandler);
        }
        if (executor != null)
        {
            executor.shutdownNow();
            executor = null;
        }
    }

    public void HandleInventoryDescendents(InventoryDescendents inventorydescendents)
    {
        this;
        JVM INSTR monitorenter ;
        SLInventoryUDPFetchRequest slinventoryudpfetchrequest = (SLInventoryUDPFetchRequest)udpFetchRequests.get(inventorydescendents.AgentData_Field.FolderID);
        if (slinventoryudpfetchrequest == null)
        {
            break MISSING_BLOCK_LABEL_97;
        }
        if (!slinventoryudpfetchrequest.HandleInventoryDescendents(inventorydescendents))
        {
            break MISSING_BLOCK_LABEL_97;
        }
        udpFetchRequests.remove(inventorydescendents.AgentData_Field.FolderID);
        slinventoryudpfetchrequest = (SLInventoryUDPFetchRequest)udpFetchPendingRequests.remove(inventorydescendents.AgentData_Field.FolderID);
        if (slinventoryudpfetchrequest == null)
        {
            break MISSING_BLOCK_LABEL_97;
        }
        udpFetchRequests.put(inventorydescendents.AgentData_Field.FolderID, slinventoryudpfetchrequest);
        slinventoryudpfetchrequest.start();
        this;
        JVM INSTR monitorexit ;
        return;
        inventorydescendents;
        throw inventorydescendents;
    }

    public void HandleUpdateCreateInventoryItem(UpdateCreateInventoryItem updatecreateinventoryitem)
    {
        SLInventoryNewContentsEvent slinventorynewcontentsevent;
        Object obj;
        slinventorynewcontentsevent = new SLInventoryNewContentsEvent();
        obj = new HashSet();
        updatecreateinventoryitem = updatecreateinventoryitem.InventoryData_Fields.iterator();
_L7:
        if (!updatecreateinventoryitem.hasNext()) goto _L2; else goto _L1
_L1:
        UUID uuid;
        OnInventoryCallbackListener oninventorycallbacklistener;
        Object obj1;
        Object obj2;
        obj1 = (com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData)updatecreateinventoryitem.next();
        obj2 = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).ItemID;
        uuid = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).FolderID;
        Debug.Printf("Inventory: UpdateCreateInventoryItem got folder %s item %s, callback %d", new Object[] {
            uuid.toString(), ((UUID) (obj2)).toString(), Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).CallbackID)
        });
        ((Set) (obj)).add(uuid);
        oninventorycallbacklistener = (OnInventoryCallbackListener)callbacks.remove(Integer.valueOf(((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).CallbackID));
        obj2 = db.findEntryOrCreate(((UUID) (obj2)));
        obj2.groupMask = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).GroupMask;
        obj2.description = SLMessage.stringFromVariableUTF(((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).Description);
        obj2.isGroupOwned = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).GroupOwned;
        obj2.everyoneMask = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).EveryoneMask;
        obj2.assetType = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).Type;
        obj2.invType = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).InvType;
        obj2.groupUUID = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).GroupID;
        obj2.name = SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).Name);
        obj2.baseMask = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).BaseMask;
        obj2.saleType = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).SaleType;
        obj2.salePrice = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).SalePrice;
        obj2.ownerUUID = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).OwnerID;
        obj2.flags = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).Flags;
        obj2.ownerMask = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).OwnerMask;
        obj2.nextOwnerMask = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).NextOwnerMask;
        obj2.assetUUID = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).AssetID;
        obj2.creationDate = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).CreationDate;
        obj2.creatorUUID = ((com.lumiyaviewer.lumiya.slproto.messages.UpdateCreateInventoryItem.InventoryData) (obj1)).CreatorID;
        obj2.parentUUID = uuid;
        obj1 = db.findEntry(uuid);
        if (obj1 == null) goto _L4; else goto _L3
_L3:
        if (((SLInventoryEntry) (obj2)).getId() != 0L || oninventorycallbacklistener != null)
        {
            break MISSING_BLOCK_LABEL_394;
        }
        if (((SLInventoryEntry) (obj1)).typeDefault != 14 && ((SLInventoryEntry) (obj1)).typeDefault != 2)
        {
            slinventorynewcontentsevent.AddItem(false, uuid, ((SLInventoryEntry) (obj2)).name);
        }
        obj2.parent_id = ((SLInventoryEntry) (obj1)).getId();
        db.saveEntry(((SLInventoryEntry) (obj2)));
_L5:
        if (oninventorycallbacklistener != null)
        {
            try
            {
                oninventorycallbacklistener.onInventoryCallback(((SLInventoryEntry) (obj2)));
            }
            catch (com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception)
            {
                databasebindingexception.printStackTrace();
            }
        }
        continue; /* Loop/switch isn't completed */
_L4:
        if (((SLInventoryEntry) (obj2)).getId() != 0L)
        {
            db.deleteEntry(((SLInventoryEntry) (obj2)));
        }
        if (true) goto _L5; else goto _L2
_L2:
        if (userManager != null)
        {
            for (updatecreateinventoryitem = ((Iterable) (obj)).iterator(); updatecreateinventoryitem.hasNext(); userManager.getInventoryManager().requestFolderUpdate(((UUID) (obj))))
            {
                obj = (UUID)updatecreateinventoryitem.next();
            }

        }
        if (!slinventorynewcontentsevent.isEmpty())
        {
            eventBus.publish(slinventorynewcontentsevent);
        }
        return;
        if (true) goto _L7; else goto _L6
_L6:
    }

    public void LinkInventoryItem(final SLInventoryEntry targetFolder, UUID uuid, int i, int j, String s, String s1)
    {
        LinkInventoryItem linkinventoryitem = new LinkInventoryItem();
        linkinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        linkinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        linkinventoryitem.InventoryBlock_Field.FolderID = targetFolder.uuid;
        linkinventoryitem.InventoryBlock_Field.TransactionID = UUID.randomUUID();
        linkinventoryitem.InventoryBlock_Field.OldItemID = uuid;
        linkinventoryitem.InventoryBlock_Field.Type = j;
        linkinventoryitem.InventoryBlock_Field.InvType = i;
        linkinventoryitem.InventoryBlock_Field.Name = SLMessage.stringToVariableOEM(s);
        linkinventoryitem.InventoryBlock_Field.Description = SLMessage.stringToVariableOEM(s1);
        linkinventoryitem.isReliable = true;
        linkinventoryitem.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLInventory this$0;
            final SLInventoryEntry val$targetFolder;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(targetFolder.uuid);
            }

            
            {
                this$0 = SLInventory.this;
                targetFolder = slinventoryentry;
                super();
            }
        });
        SendMessage(linkinventoryitem);
    }

    public void MoveInventoryItem(final SLInventoryEntry item, final SLInventoryEntry newParent)
    {
        final UUID oldParentUUID = item.parentUUID;
        if (item.isFolder)
        {
            UpdateInventoryFolder updateinventoryfolder = new UpdateInventoryFolder();
            updateinventoryfolder.AgentData_Field.AgentID = circuitInfo.agentID;
            updateinventoryfolder.AgentData_Field.SessionID = circuitInfo.sessionID;
            com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder.FolderData folderdata = new com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder.FolderData();
            folderdata.FolderID = item.uuid;
            folderdata.ParentID = newParent.uuid;
            folderdata.Type = item.typeDefault;
            folderdata.Name = SLMessage.stringToVariableUTF(item.name);
            updateinventoryfolder.FolderData_Fields.add(folderdata);
            updateinventoryfolder.isReliable = true;
            updateinventoryfolder.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$item;
                final SLInventoryEntry val$newParent;
                final UUID val$oldParentUUID;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(oldParentUUID);
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(newParent.uuid);
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(item.uuid);
                }

            
            {
                this$0 = SLInventory.this;
                oldParentUUID = uuid;
                newParent = slinventoryentry;
                item = slinventoryentry1;
                super();
            }
            });
            SendMessage(updateinventoryfolder);
            return;
        } else
        {
            MoveInventoryItem moveinventoryitem = new MoveInventoryItem();
            moveinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
            moveinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
            moveinventoryitem.AgentData_Field.Stamp = false;
            com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem.InventoryData();
            inventorydata.FolderID = newParent.uuid;
            inventorydata.ItemID = item.uuid;
            inventorydata.NewName = SLMessage.stringToVariableUTF(item.name);
            moveinventoryitem.InventoryData_Fields.add(inventorydata);
            moveinventoryitem.isReliable = true;
            moveinventoryitem.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$newParent;
                final UUID val$oldParentUUID;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(oldParentUUID);
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(newParent.uuid);
                }

            
            {
                this$0 = SLInventory.this;
                oldParentUUID = uuid;
                newParent = slinventoryentry;
                super();
            }
            });
            SendMessage(moveinventoryitem);
            return;
        }
    }

    public void MoveInventoryItemRaw(UUID uuid, String s, UUID uuid1)
    {
        MoveInventoryItem moveinventoryitem = new MoveInventoryItem();
        moveinventoryitem.AgentData_Field.AgentID = circuitInfo.agentID;
        moveinventoryitem.AgentData_Field.SessionID = circuitInfo.sessionID;
        moveinventoryitem.AgentData_Field.Stamp = false;
        com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem.InventoryData inventorydata = new com.lumiyaviewer.lumiya.slproto.messages.MoveInventoryItem.InventoryData();
        inventorydata.FolderID = uuid1;
        inventorydata.ItemID = uuid;
        inventorydata.NewName = SLMessage.stringToVariableUTF(s);
        moveinventoryitem.InventoryData_Fields.add(inventorydata);
        moveinventoryitem.isReliable = true;
        moveinventoryitem.setEventListener(reloadEvent);
        SendMessage(moveinventoryitem);
    }

    public void RenameInventoryItem(final SLInventoryEntry item, String s)
    {
        item.name = s;
        try
        {
            db.saveEntry(item);
        }
        catch (com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception)
        {
            Debug.Warning(databasebindingexception);
        }
        if (item.isFolder)
        {
            UpdateInventoryFolder updateinventoryfolder = new UpdateInventoryFolder();
            updateinventoryfolder.AgentData_Field.AgentID = circuitInfo.agentID;
            updateinventoryfolder.AgentData_Field.SessionID = circuitInfo.sessionID;
            com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder.FolderData folderdata = new com.lumiyaviewer.lumiya.slproto.messages.UpdateInventoryFolder.FolderData();
            folderdata.FolderID = item.uuid;
            folderdata.ParentID = item.parentUUID;
            folderdata.Type = item.typeDefault;
            folderdata.Name = SLMessage.stringToVariableUTF(s);
            updateinventoryfolder.FolderData_Fields.add(folderdata);
            updateinventoryfolder.isReliable = true;
            updateinventoryfolder.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$item;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(item.uuid);
                    SLInventory._2D_get4(SLInventory.this).getInventoryManager().requestFolderUpdate(item.parentUUID);
                }

            
            {
                this$0 = SLInventory.this;
                item = slinventoryentry;
                super();
            }
            });
            SendMessage(updateinventoryfolder);
            return;
        } else
        {
            DoUpdateInventoryItem(item, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls3(this, item));
            return;
        }
    }

    public void TrashInventoryItem(SLInventoryEntry slinventoryentry)
    {
        SLInventoryEntry slinventoryentry1 = db.findSpecialFolder(rootFolder.getId(), 14);
        if (slinventoryentry1 != null)
        {
            MoveInventoryItem(slinventoryentry, slinventoryentry1);
        }
    }

    public void UpdateNotecard(final SLInventoryEntry entry, UUID uuid, final boolean isScript, String s, String s1, final byte notecardData[], final UUID taskUUID, 
            final int taskLocalID, final OnNotecardUpdatedListener listener)
    {
        int i;
        if (entry != null)
        {
            boolean flag;
            if (Objects.equal(entry.name, s))
            {
                flag = Objects.equal(entry.description, s1);
            } else
            {
                flag = false;
            }
            i = flag ^ true;
        } else
        {
            i = 1;
        }
        if (taskUUID == null)
        {
            if (entry == null)
            {
                Debug.Printf("Notecard: Creating new inventory entry.", new Object[0]);
                if (isScript)
                {
                    taskLocalID = SLAssetType.AT_LSL_TEXT.getTypeCode();
                } else
                {
                    taskLocalID = SLAssetType.AT_NOTECARD.getTypeCode();
                }
                if (isScript)
                {
                    i = SLInventoryType.IT_LSL.getTypeCode();
                } else
                {
                    i = SLInventoryType.IT_NOTECARD.getTypeCode();
                }
                DoCreateInventoryItem(uuid, taskLocalID, i, s, s1, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls7(isScript, this, notecardData, listener));
                return;
            }
            if (i != 0)
            {
                entry.name = s;
                entry.description = s1;
                try
                {
                    db.saveEntry(entry);
                }
                // Misplaced declaration of an exception variable
                catch (UUID uuid)
                {
                    Debug.Warning(uuid);
                }
                Debug.Printf("Notecard: Updating existing inventory entry %s", new Object[] {
                    entry.uuid
                });
                DoUpdateInventoryItem(entry, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls8(isScript, this, notecardData, listener));
                return;
            } else
            {
                StartUploadingNotecardContents(entry, null, isScript, notecardData, listener);
                return;
            }
        }
        if (i != 0 && entry != null)
        {
            entry.name = s;
            entry.description = s1;
            DoUpdateTaskInventoryItem(entry, taskLocalID, new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

                final SLInventory this$0;
                final SLInventoryEntry val$entry;
                final boolean val$isScript;
                final OnNotecardUpdatedListener val$listener;
                final byte val$notecardData[];
                final int val$taskLocalID;
                final UUID val$taskUUID;

                public void onMessageAcknowledged(SLMessage slmessage)
                {
                    SLInventory._2D_get4(SLInventory.this).getObjectsManager().requestTaskInventoryUpdate(taskLocalID);
                    SLInventory._2D_wrap1(SLInventory.this, entry, taskUUID, isScript, notecardData, listener);
                }

            
            {
                this$0 = SLInventory.this;
                taskLocalID = i;
                entry = slinventoryentry;
                taskUUID = uuid;
                isScript = flag;
                notecardData = abyte0;
                listener = onnotecardupdatedlistener;
                super();
            }
            });
            return;
        } else
        {
            StartUploadingNotecardContents(entry, taskUUID, isScript, notecardData, listener);
            return;
        }
    }

    public void UpdateStoreInventoryItem(SLInventoryEntry slinventoryentry)
    {
        try
        {
            db.saveEntry(slinventoryentry);
            DoUpdateInventoryItem(slinventoryentry, new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls4(this, slinventoryentry));
            return;
        }
        // Misplaced declaration of an exception variable
        catch (SLInventoryEntry slinventoryentry)
        {
            Debug.Warning(slinventoryentry);
        }
    }

    public boolean canMoveToTrash(SLInventoryEntry slinventoryentry)
    {
        SLInventoryEntry slinventoryentry1 = db.findSpecialFolder(rootFolder.getId(), 14);
        if (slinventoryentry1 == null)
        {
            return false;
        }
        return slinventoryentry.parent_id != slinventoryentry1.getId();
    }

    public UUID findSpecialFolder(int i)
    {
        UUID uuid = null;
        SLInventoryEntry slinventoryentry = db.findSpecialFolder(rootFolder.getId(), i);
        if (slinventoryentry != null)
        {
            uuid = slinventoryentry.uuid;
        }
        return uuid;
    }

    public UUID getCallingCardsFolderUUID()
    {
        if (rootFolder != null)
        {
            SLInventoryEntry slinventoryentry = db.findSpecialFolder(rootFolder.getId(), 2);
            if (slinventoryentry != null)
            {
                return slinventoryentry.uuid;
            }
        }
        return null;
    }

    public SLCaps getCaps()
    {
        return caps;
    }

    public InventoryDB getDatabase()
    {
        return db;
    }

    public ExecutorService getExecutor()
    {
        if (executor == null)
        {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_10310(long l, UUID uuid, boolean flag, boolean flag1)
    {
        Debug.Printf("Inventory: onFetchComplete: folderId = '%s'", new Object[] {
            Long.valueOf(l)
        });
        fetchRequests.remove(uuid);
        SLInventoryEntry slinventoryentry = db.findEntry(uuid);
        if (slinventoryentry != null)
        {
            slinventoryentry.sessionID = circuitInfo.sessionID;
            slinventoryentry.fetchFailed = flag ^ true;
            try
            {
                db.saveEntry(slinventoryentry);
            }
            catch (com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception)
            {
                Debug.Warning(databasebindingexception);
            }
        }
        if (!flag) goto _L2; else goto _L1
_L1:
        folderEntryResultHandler.onResultData(uuid, slinventoryentry);
_L4:
        updateFolderLoadingStatus(uuid);
        return;
_L2:
        if (!flag1)
        {
            folderEntryResultHandler.onResultError(uuid, new InventoryFetchException("Failed to retrieve folder contents"));
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_27010(SLInventoryEntry slinventoryentry, SLInventoryEntry slinventoryentry1)
    {
        userManager.getInventoryManager().requestFolderUpdate(slinventoryentry.uuid);
        userManager.getInventoryManager().requestFolderUpdate(slinventoryentry.parentUUID);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_42520(SLInventoryEntry slinventoryentry, SLInventoryEntry slinventoryentry1)
    {
        userManager.getInventoryManager().requestFolderUpdate(slinventoryentry.parentUUID);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_48053(SLInventoryEntry slinventoryentry, byte abyte0[], UUID uuid, boolean flag, OnNotecardUpdatedListener onnotecardupdatedlistener)
    {
        Object obj = null;
        UUID uuid1;
        if (slinventoryentry != null)
        {
            uuid1 = slinventoryentry.uuid;
        } else
        {
            uuid1 = null;
        }
        Debug.Printf("Notecard: Starting to upload contents for entry %s", new Object[] {
            uuid1
        });
        if (abyte0 != null)
        {
            abyte0 = UploadNotecardContents(slinventoryentry, uuid, flag, abyte0);
        } else
        {
            abyte0 = null;
        }
        uuid = obj;
        if (slinventoryentry != null)
        {
            uuid = slinventoryentry.uuid;
        }
        Debug.Printf("Notecard: Notecard entry %s updated", new Object[] {
            uuid
        });
        if (onnotecardupdatedlistener != null)
        {
            onnotecardupdatedlistener.onNotecardUpdated(slinventoryentry, abyte0);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_49599(boolean flag, byte abyte0[], OnNotecardUpdatedListener onnotecardupdatedlistener, SLInventoryEntry slinventoryentry)
    {
        StartUploadingNotecardContents(slinventoryentry, null, flag, abyte0, onnotecardupdatedlistener);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_50229(boolean flag, byte abyte0[], OnNotecardUpdatedListener onnotecardupdatedlistener, SLInventoryEntry slinventoryentry)
    {
        StartUploadingNotecardContents(slinventoryentry, null, flag, abyte0, onnotecardupdatedlistener);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_51539(UUID uuid, UUID uuid1, UUID uuid2, Runnable runnable)
    {
        try
        {
            String s = getCaps().getCapabilityOrThrow(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.CopyInventoryFromNotecard);
            (new LLSDXMLRequest()).PerformRequest(s, new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("notecard-id", new LLSDUUID(uuid)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("object-id", new LLSDUUID()), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("item-id", new LLSDUUID(uuid1)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("folder-id", new LLSDUUID(uuid2)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("callback-id", new LLSDInt(0))
            }));
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        if (runnable != null)
        {
            runnable.run();
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_inventory_SLInventory_53068(String s, final ImmutableSet immutableEntries, final int taskLocalID, final Function onResult)
    {
        DoCreateNewFolder(rootFolder, s, true, new Function() {

            final SLInventory this$0;
            final ImmutableSet val$immutableEntries;
            final Function val$onResult;
            final int val$taskLocalID;

            public volatile Object apply(Object obj)
            {
                return apply((UUID)obj);
            }

            public Void apply(UUID uuid)
            {
                if (uuid != null)
                {
                    UUID uuid1;
                    for (Iterator iterator = immutableEntries.iterator(); iterator.hasNext(); SLInventory._2D_wrap0(SLInventory.this, uuid, taskLocalID, uuid1))
                    {
                        uuid1 = (UUID)iterator.next();
                    }

                }
                onResult.apply(uuid);
                return null;
            }

            
            {
                this$0 = SLInventory.this;
                immutableEntries = immutableset;
                taskLocalID = i;
                onResult = function;
                super();
            }
        });
    }

    void onFetchComplete(SLInventoryFetchRequest slinventoryfetchrequest, UUID uuid, long l, boolean flag, boolean flag1)
    {
        if (dbExecutor != null)
        {
            dbExecutor.execute(new _2D_.Lambda.eCHTl_2D__kh2tUCaOJ_2D_O9NRHJvhjs._cls10(flag, flag1, l, this, uuid));
        }
    }
}
