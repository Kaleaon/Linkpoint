// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.orm.InventoryEntryList;
import com.lumiyaviewer.lumiya.orm.InventoryQuery;
import com.lumiyaviewer.lumiya.react.AsyncRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearable;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableData;
import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams;
import com.lumiyaviewer.lumiya.slproto.baker.BakeProcess;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.events.SLBakingProgressEvent;
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
import com.lumiyaviewer.lumiya.slproto.users.manager.InventoryManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules

public class SLAvatarAppearance extends SLModule
    implements com.lumiyaviewer.lumiya.slproto.assets.SLWearable.OnWearableStatusChangeListener
{
    public static class WornItem
    {

        private final int attachedTo;
        private final boolean isTouchable;
        private final UUID itemID;
        private final String name;
        private final int objectLocalID;
        private final SLWearableType wornOn;

        static int _2D_get0(WornItem wornitem)
        {
            return wornitem.objectLocalID;
        }

        int getAttachedTo()
        {
            return attachedTo;
        }

        public boolean getIsTouchable()
        {
            return isTouchable;
        }

        public String getName()
        {
            return name;
        }

        public int getObjectLocalID()
        {
            return objectLocalID;
        }

        public SLWearableType getWornOn()
        {
            return wornOn;
        }

        public UUID itemID()
        {
            return itemID;
        }

        WornItem(SLWearableType slwearabletype, int i, UUID uuid, String s, int j, boolean flag)
        {
            wornOn = slwearabletype;
            attachedTo = i;
            itemID = uuid;
            name = s;
            objectLocalID = j;
            isTouchable = flag;
        }
    }


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
    private int agentVisualParams[];
    private volatile BakeProcess bakeProcess;
    private Thread bakingThread;
    private final SLCaps caps;
    private final AtomicReference cofFolderUUID = new AtomicReference();
    private volatile boolean cofReady;
    private volatile int currentCofAppearanceVersion;
    private volatile int currentCofInventoryVersion;
    private final SubscriptionData currentOutfitFolder;
    private final SubscriptionData findCofFolder;
    private final SLInventory inventory;
    private volatile boolean lastCofUpdateError;
    private volatile int lastCofUpdatedVersion;
    private volatile boolean legacyAppearanceReady;
    private volatile boolean multiLayerDone;
    private volatile boolean needUpdateAppearance;
    private final AtomicBoolean needUpdateCOF = new AtomicBoolean(false);
    private final SLParcelInfo parcelInfo;
    private Future serverSideAppearanceUpdateTask;
    private int setAppearanceSerialNum;
    private final UserManager userManager;
    private final AtomicReference wantedAttachments = new AtomicReference(ImmutableMap.of());
    private SLInventoryEntry wantedOutfitFolder;
    private volatile ImmutableMap wornAttachments;
    private final RequestHandler wornItemsRequestHandler;
    private final ResultHandler wornItemsResultHandler;
    private volatile Table wornWearables;

    static ResultHandler _2D_get0(SLAvatarAppearance slavatarappearance)
    {
        return slavatarappearance.wornItemsResultHandler;
    }

    static ImmutableList _2D_wrap0(SLAvatarAppearance slavatarappearance)
    {
        return slavatarappearance.getWornItems();
    }

    public SLAvatarAppearance(SLAgentCircuit slagentcircuit, SLInventory slinventory, SLCaps slcaps)
    {
        super(slagentcircuit);
        setAppearanceSerialNum = 1;
        agentSizeKnown = false;
        needUpdateAppearance = false;
        wornAttachments = ImmutableMap.of();
        wornWearables = ImmutableTable.of();
        wantedOutfitFolder = null;
        bakingThread = null;
        serverSideAppearanceUpdateTask = null;
        currentCofInventoryVersion = 0;
        currentCofAppearanceVersion = 0;
        lastCofUpdatedVersion = 0;
        lastCofUpdateError = false;
        legacyAppearanceReady = false;
        cofReady = false;
        multiLayerDone = false;
        wornItemsRequestHandler = new AsyncRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLAvatarAppearance this$0;

            public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
            {
                if (SLAvatarAppearance._2D_get0(SLAvatarAppearance.this) != null)
                {
                    SLAvatarAppearance._2D_get0(SLAvatarAppearance.this).onResultData(subscriptionsinglekey, SLAvatarAppearance._2D_wrap0(SLAvatarAppearance.this));
                }
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((SubscriptionSingleKey)obj);
            }

            
            {
                this$0 = SLAvatarAppearance.this;
                super();
            }
        });
        bakeProcess = null;
        caps = slcaps;
        inventory = slinventory;
        parcelInfo = slagentcircuit.getGridConnection().parcelInfo;
        userManager = UserManager.getUserManager(slagentcircuit.getAgentUUID());
        currentOutfitFolder = new SubscriptionData(slagentcircuit, obj -> m200com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearancemthref0((InventoryEntryList) obj));
        findCofFolder = new SubscriptionData(slagentcircuit, obj -> onCofFolderEntry((InventoryEntryList) obj));
        if (userManager != null)
        {
            wornItemsResultHandler = userManager.wornItems().attachRequestHandler(wornItemsRequestHandler);
            return;
        } else
        {
            wornItemsResultHandler = null;
            return;
        }
    }

    private void DetachItem(int i)
    {
        Object obj;
        boolean flag1;
        boolean flag2;
        Debug.Log((new StringBuilder()).append("Outfits: detaching item ").append(i).toString());
        flag1 = false;
        flag2 = false;
        obj = (Map)wantedAttachments.get();
        if (obj == null) goto _L2; else goto _L1
_L1:
        boolean flag;
        obj = new HashMap(((Map) (obj)));
        flag = flag2;
        if (parcelInfo == null) goto _L4; else goto _L3
_L3:
        Object obj1;
        obj1 = parcelInfo.getAgentAvatar();
        flag = flag2;
        if (obj1 == null) goto _L4; else goto _L5
_L5:
        obj1 = ((SLObjectInfo) (obj1)).treeNode.iterator();
_L9:
        Object obj2;
        Object obj3;
        do
        {
            if (!((Iterator) (obj1)).hasNext())
            {
                break MISSING_BLOCK_LABEL_303;
            }
            obj2 = (SLObjectInfo)((Iterator) (obj1)).next();
        } while (((SLObjectInfo) (obj2)).attachedToUUID == null || !(((SLObjectInfo) (obj2)).isDead ^ true) || ((SLObjectInfo) (obj2)).localID != i);
        obj3 = ((Map) (obj)).remove(((SLObjectInfo) (obj2)).getId());
        if (obj3 == null) goto _L7; else goto _L6
_L6:
        flag = true;
_L4:
        flag1 = flag;
        if (flag)
        {
            wantedAttachments.set(ImmutableMap.copyOf(((Map) (obj))));
            flag1 = flag;
        }
_L2:
        obj = new ObjectDetach();
        ((ObjectDetach) (obj)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((ObjectDetach) (obj)).AgentData_Field.SessionID = circuitInfo.sessionID;
        com.lumiyaviewer.lumiya.slproto.messages.ObjectDetach.ObjectData objectdata = new com.lumiyaviewer.lumiya.slproto.messages.ObjectDetach.ObjectData();
        objectdata.ObjectLocalID = i;
        ((ObjectDetach) (obj)).ObjectData_Fields.add(objectdata);
        obj.isReliable = true;
        SendMessage(((SLMessage) (obj)));
        if (flag1)
        {
            needUpdateCOF.set(true);
            UpdateCOFContents();
        }
        return;
_L7:
        obj2 = ((Map) (obj)).remove(((SLObjectInfo) (obj2)).attachedToUUID);
        if (obj2 == null) goto _L9; else goto _L8
_L8:
        flag = true;
          goto _L4
        NoSuchElementException nosuchelementexception;
        nosuchelementexception;
        Debug.Warning(nosuchelementexception);
        flag = flag2;
          goto _L4
        flag = false;
          goto _L4
    }

    private void ForceUpdateAppearance(boolean flag)
    {
        needUpdateAppearance = true;
        if (caps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateAvatarAppearance) != null) goto _L2; else goto _L1
_L1:
        eventBus.publish(new SLBakingProgressEvent(true, false, 0));
_L4:
        StartUpdatingAppearance();
        return;
_L2:
        if (flag)
        {
            lastCofUpdatedVersion = 0;
            currentCofAppearanceVersion = 0;
            RequestServerRebake();
        }
        if (true) goto _L4; else goto _L3
_L3:
    }

    private void ProcessMultiLayer()
    {
        if (!multiLayerDone && cofReady && legacyAppearanceReady)
        {
            UpdateMultiLayer();
        }
    }

    private void RequestServerRebake()
    {
        String s;
        Object obj;
        s = caps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateAvatarAppearance);
        obj = (InventoryEntryList)currentOutfitFolder.getData();
        if (s != null && obj != null)
        {
            obj = ((InventoryEntryList) (obj)).getFolder();
            if (obj != null)
            {
                currentCofInventoryVersion = ((SLInventoryEntry) (obj)).version;
                break MISSING_BLOCK_LABEL_47;
            }
        }
_L1:
        return;
        if (currentCofInventoryVersion != lastCofUpdatedVersion && currentCofInventoryVersion != currentCofAppearanceVersion || lastCofUpdateError)
        {
            lastCofUpdatedVersion = currentCofInventoryVersion;
            lastCofUpdateError = false;
            UpdateServerSideAppearance(s, ((SLInventoryEntry) (obj)).version);
        }
          goto _L1
    }

    private void SendAgentIsNowWearing()
    {
        AgentIsNowWearing agentisnowwearing = new AgentIsNowWearing();
        agentisnowwearing.AgentData_Field.AgentID = circuitInfo.agentID;
        agentisnowwearing.AgentData_Field.SessionID = circuitInfo.sessionID;
        SLWearableType aslwearabletype[] = SLWearableType.values();
        int j = aslwearabletype.length;
        for (int i = 0; i < j; i++)
        {
            SLWearableType slwearabletype = aslwearabletype[i];
            Object obj = wornWearables.row(slwearabletype);
            boolean flag;
            if (obj != null)
            {
                obj = ((Map) (obj)).values().iterator();
                for (flag = true; ((Iterator) (obj)).hasNext(); flag = false)
                {
                    SLWearable slwearable = (SLWearable)((Iterator) (obj)).next();
                    com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing.WearableData wearabledata1 = new com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing.WearableData();
                    wearabledata1.ItemID = slwearable.itemID;
                    wearabledata1.WearableType = slwearabletype.getTypeCode();
                    agentisnowwearing.WearableData_Fields.add(wearabledata1);
                }

            } else
            {
                flag = true;
            }
            if (flag)
            {
                com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing.WearableData wearabledata = new com.lumiyaviewer.lumiya.slproto.messages.AgentIsNowWearing.WearableData();
                wearabledata.ItemID = new UUID(0L, 0L);
                wearabledata.WearableType = slwearabletype.getTypeCode();
                agentisnowwearing.WearableData_Fields.add(wearabledata);
            }
        }

        Debug.Log((new StringBuilder()).append("AvatarAppearance: Sending AgentIsNowWearing, ").append(agentisnowwearing.WearableData_Fields.size()).append(" wearables.").toString());
        agentisnowwearing.isReliable = true;
        SendMessage(agentisnowwearing);
        needUpdateCOF.set(true);
        UpdateCOFContents();
    }

    private void SendAvatarSetAppearance()
    {
        Object obj = null;
        UpdateCOFContents();
        if (caps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateAvatarAppearance) == null)
        {
            if (parcelInfo != null)
            {
                obj = parcelInfo.getAgentAvatar();
            }
            if (agentBakedTextures != null && obj != null)
            {
                ((SLObjectAvatarInfo) (obj)).ApplyAvatarTextures(agentBakedTextures, true);
            }
            AgentSetAppearance agentsetappearance = new AgentSetAppearance();
            agentsetappearance.AgentData_Field.AgentID = circuitInfo.agentID;
            agentsetappearance.AgentData_Field.SessionID = circuitInfo.sessionID;
            agentsetappearance.AgentData_Field.SerialNum = setAppearanceSerialNum;
            agentsetappearance.AgentData_Field.Size = new LLVector3();
            agentsetappearance.AgentData_Field.Size.x = 1.0F;
            agentsetappearance.AgentData_Field.Size.y = 1.0F;
            agentsetappearance.AgentData_Field.Size.z = 1.0F;
            int ai[];
            int j;
            if (agentBakedTextures != null)
            {
                agentsetappearance.ObjectData_Field.TextureEntry = agentBakedTextures.packByteArray();
            } else
            {
                agentsetappearance.ObjectData_Field.TextureEntry = new byte[0];
            }
            agentVisualParams = getAppearanceParams();
            if (obj != null)
            {
                ((SLObjectAvatarInfo) (obj)).ApplyAvatarVisualParams(agentVisualParams);
            }
            ai = agentVisualParams;
            j = ai.length;
            for (int i = 0; i < j; i++)
            {
                int k = ai[i];
                com.lumiyaviewer.lumiya.slproto.messages.AgentSetAppearance.VisualParam visualparam = new com.lumiyaviewer.lumiya.slproto.messages.AgentSetAppearance.VisualParam();
                visualparam.ParamValue = k;
                agentsetappearance.VisualParam_Fields.add(visualparam);
            }

            if (agentSizeKnown && areWearablesReady())
            {
                agentsetappearance.AgentData_Field.Size.x = 0.45F;
                agentsetappearance.AgentData_Field.Size.y = 0.6F;
                agentsetappearance.AgentData_Field.Size.z = getAgentHeight();
                Debug.Log((new StringBuilder()).append("set agent height to ").append(agentsetappearance.AgentData_Field.Size.z).toString());
            }
            agentsetappearance.isReliable = true;
            StringBuilder stringbuilder = (new StringBuilder()).append("AvatarAppearance: Sending agentSetAppearance: ").append(agentsetappearance.VisualParam_Fields.size()).append(" params, hasTextures = ");
            if (agentBakedTextures != null)
            {
                ai = "yes";
            } else
            {
                ai = "no";
            }
            Debug.Log(stringbuilder.append(ai).toString());
            SendMessage(agentsetappearance);
            setAppearanceSerialNum = setAppearanceSerialNum + 1;
        }
    }

    private void StartUpdatingAppearance()
    {
        updateIfWearablesReady();
    }

    private void UpdateCOFContents()
    {
        SLInventoryEntry slinventoryentry;
        Object obj;
        Object obj1;
        HashMap hashmap;
        HashSet hashset;
        Object obj2;
        Object obj3;
        boolean flag;
        boolean flag1 = areWearablesReady();
        Debug.Printf("Wearables ready %b, cofReady %b", new Object[] {
            Boolean.valueOf(flag1), Boolean.valueOf(cofReady)
        });
        if (flag1)
        {
            flag1 = cofReady;
        } else
        {
            flag1 = false;
        }
        if (!flag1)
        {
            return;
        }
        obj2 = (InventoryEntryList)currentOutfitFolder.getData();
        if (obj2 == null)
        {
            return;
        }
        slinventoryentry = ((InventoryEntryList) (obj2)).getFolder();
        if (slinventoryentry == null)
        {
            return;
        }
        if (!needUpdateCOF.getAndSet(false))
        {
            break MISSING_BLOCK_LABEL_881;
        }
        currentCofInventoryVersion = slinventoryentry.version;
        obj1 = new LinkedList();
        hashmap = new HashMap();
        obj = new HashMap();
        hashset = new HashSet();
        obj3 = wornWearables.values().iterator();
        do
        {
            if (!((Iterator) (obj3)).hasNext())
            {
                break;
            }
            SLWearable slwearable1 = (SLWearable)((Iterator) (obj3)).next();
            if (!slwearable1.getIsFailed())
            {
                hashset.add(slwearable1.itemID);
                hashmap.put(slwearable1.itemID, slwearable1);
            }
        } while (true);
        obj3 = (Map)wantedAttachments.get();
        if (obj3 != null)
        {
            ((Map) (obj)).putAll(((Map) (obj3)));
        }
        obj2 = ((Iterable) (obj2)).iterator();
        flag = true;
_L7:
        SLInventoryEntry slinventoryentry1;
        if (!((Iterator) (obj2)).hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        slinventoryentry1 = (SLInventoryEntry)((Iterator) (obj2)).next();
        if (slinventoryentry1.assetType != SLAssetType.AT_LINK.getTypeCode()) goto _L2; else goto _L1
_L1:
        if (slinventoryentry1.invType != SLInventoryType.IT_WEARABLE.getTypeCode()) goto _L4; else goto _L3
_L3:
        if (!hashset.contains(slinventoryentry1.assetUUID))
        {
            ((List) (obj1)).add(slinventoryentry1.uuid);
        }
_L5:
        hashmap.remove(slinventoryentry1.assetUUID);
        ((Map) (obj)).remove(slinventoryentry1.assetUUID);
        continue; /* Loop/switch isn't completed */
_L4:
        if (slinventoryentry1.invType == SLInventoryType.IT_OBJECT.getTypeCode() && obj3 != null && !((Map) (obj3)).containsKey(slinventoryentry1.assetUUID))
        {
            Debug.Printf("Attached entry %s (%s) not found in wanted attachments", new Object[] {
                slinventoryentry1.assetUUID, slinventoryentry1.name
            });
            ((List) (obj1)).add(slinventoryentry1.uuid);
        }
        if (true) goto _L5; else goto _L2
_L2:
        if (slinventoryentry1.assetType == SLAssetType.AT_LINK_FOLDER.getTypeCode() && wantedOutfitFolder != null)
        {
            if (!wantedOutfitFolder.uuid.equals(slinventoryentry1.assetUUID))
            {
                ((List) (obj1)).add(slinventoryentry1.uuid);
            } else
            {
                flag = false;
            }
        }
        if (true) goto _L7; else goto _L6
_L6:
        Debug.Printf("Update COF: addWearablesList %d, killList %d", new Object[] {
            Integer.valueOf(hashmap.size()), Integer.valueOf(((List) (obj1)).size())
        });
        boolean flag2;
        boolean flag3;
        if (!((List) (obj1)).isEmpty())
        {
            inventory.DeleteMultiInventoryItemRaw(slinventoryentry, ((List) (obj1)));
            flag2 = true;
        } else
        {
            flag2 = false;
        }
        for (obj1 = hashmap.values().iterator(); ((Iterator) (obj1)).hasNext();)
        {
            SLWearable slwearable = (SLWearable)((Iterator) (obj1)).next();
            Debug.Printf("Update COF: adding %s, name = '%s'", new Object[] {
                slwearable.itemID, slwearable.getName()
            });
            inventory.LinkInventoryItem(slinventoryentry, slwearable.itemID, SLInventoryType.IT_WEARABLE.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), slwearable.getName(), "");
            flag2 = true;
        }

        for (obj = ((Map) (obj)).entrySet().iterator(); ((Iterator) (obj)).hasNext();)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)((Iterator) (obj)).next();
            Debug.Printf("Update COF: adding attachment %s, name = '%s'", new Object[] {
                entry.getKey(), entry.getValue()
            });
            inventory.LinkInventoryItem(slinventoryentry, (UUID)entry.getKey(), SLInventoryType.IT_OBJECT.getTypeCode(), SLAssetType.AT_LINK.getTypeCode(), (String)entry.getValue(), "");
            flag2 = true;
        }

        flag3 = flag2;
        if (flag)
        {
            flag3 = flag2;
            if (wantedOutfitFolder != null)
            {
                Debug.Printf("Update COF: adding outfit link for outfit folder %s", new Object[] {
                    wantedOutfitFolder.uuid
                });
                inventory.LinkInventoryItem(slinventoryentry, wantedOutfitFolder.uuid, SLInventoryType.IT_CATEGORY.getTypeCode(), SLAssetType.AT_LINK_FOLDER.getTypeCode(), wantedOutfitFolder.name, "");
                flag3 = true;
            }
        }
        Debug.Printf("Update COF: COF updated (had changes: %b).", new Object[] {
            Boolean.valueOf(flag3)
        });
        if (flag3 && userManager != null)
        {
            userManager.getInventoryManager().requestFolderUpdate(slinventoryentry.uuid);
        }
        RequestServerRebake();
    }

    private void UpdateCurrentOutfitLink(InventoryEntryList inventoryentrylist)
    {
        inventoryentrylist = inventoryentrylist.iterator();
        do
        {
            if (!inventoryentrylist.hasNext())
            {
                break;
            }
            SLInventoryEntry slinventoryentry = (SLInventoryEntry)inventoryentrylist.next();
            if (slinventoryentry.assetType != SLAssetType.AT_LINK_FOLDER.getTypeCode())
            {
                continue;
            }
            userManager.wornOutfitLink().setData(SubscriptionSingleKey.Value, slinventoryentry.assetUUID);
            break;
        } while (true);
    }

    private void UpdateMultiLayer()
    {
        this;
        JVM INSTR monitorenter ;
        Object obj;
        Debug.Printf("AvatarAppearance: MultiLayer: Updating multi layer appearance.", new Object[0]);
        obj = (InventoryEntryList)currentOutfitFolder.getData();
        if (userManager == null) goto _L2; else goto _L1
_L1:
        InventoryDB inventorydb = userManager.getInventoryManager().getDatabase();
_L10:
        if (obj == null || inventorydb == null) goto _L4; else goto _L3
_L3:
        Object obj1;
        LinkedList linkedlist;
        obj1 = new LinkedList();
        linkedlist = new LinkedList();
        obj = ((Iterable) (obj)).iterator();
_L9:
        if (!((Iterator) (obj)).hasNext()) goto _L6; else goto _L5
_L5:
        Object obj2 = (SLInventoryEntry)((Iterator) (obj)).next();
        if (((SLInventoryEntry) (obj2)).invType != SLInventoryType.IT_WEARABLE.getTypeCode()) goto _L8; else goto _L7
_L7:
        ((List) (obj1)).add(obj2);
          goto _L9
        Exception exception;
        exception;
        throw exception;
_L2:
        inventorydb = null;
          goto _L10
_L8:
        if (((SLInventoryEntry) (obj2)).assetType != SLAssetType.AT_OBJECT.getTypeCode() && (!((SLInventoryEntry) (obj2)).isLink() || ((SLInventoryEntry) (obj2)).invType != SLInventoryType.IT_OBJECT.getTypeCode())) goto _L9; else goto _L11
_L11:
        linkedlist.add(obj2);
          goto _L9
_L6:
        if (!WearItemList(inventorydb, ((List) (obj1)), false)) goto _L13; else goto _L12
_L12:
        Debug.Printf("AvatarAppearance: MultiLayer: had some extra layers.", new Object[0]);
        SendAgentIsNowWearing();
        StartUpdatingAppearance();
_L19:
        if (linkedlist.size() == 0) goto _L15; else goto _L14
_L14:
        HashMap hashmap;
        UUID uuid;
        Iterator iterator;
        Debug.Printf("AvatarAppearance: Re-attaching %d attachments from COF.", new Object[] {
            Integer.valueOf(linkedlist.size())
        });
        hashmap = new HashMap();
        uuid = UUID.randomUUID();
        iterator = linkedlist.iterator();
        exception = null;
_L18:
        if (!iterator.hasNext()) goto _L17; else goto _L16
_L16:
        SLInventoryEntry slinventoryentry;
        SLInventoryEntry slinventoryentry1;
        slinventoryentry = (SLInventoryEntry)iterator.next();
        slinventoryentry1 = inventorydb.resolveLink(slinventoryentry);
        obj2 = exception;
        if (slinventoryentry1 == null)
        {
            break MISSING_BLOCK_LABEL_606;
        }
        obj1 = exception;
        if (exception != null)
        {
            break MISSING_BLOCK_LABEL_372;
        }
        obj1 = new RezMultipleAttachmentsFromInv();
        ((RezMultipleAttachmentsFromInv) (obj1)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((RezMultipleAttachmentsFromInv) (obj1)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((RezMultipleAttachmentsFromInv) (obj1)).HeaderData_Field.CompoundMsgID = uuid;
        ((RezMultipleAttachmentsFromInv) (obj1)).HeaderData_Field.TotalObjects = linkedlist.size();
        ((RezMultipleAttachmentsFromInv) (obj1)).HeaderData_Field.FirstDetachAll = false;
        exception = new com.lumiyaviewer.lumiya.slproto.messages.RezMultipleAttachmentsFromInv.ObjectData();
        Debug.Printf("Re-attaching attachment: entry %s (%s)", new Object[] {
            slinventoryentry1.uuid, slinventoryentry.name
        });
        hashmap.put(slinventoryentry1.uuid, slinventoryentry.name);
        exception.ItemID = slinventoryentry1.uuid;
        exception.OwnerID = slinventoryentry1.ownerUUID;
        exception.AttachmentPt = 128;
        exception.ItemFlags = slinventoryentry1.flags;
        exception.GroupMask = slinventoryentry1.groupMask;
        exception.EveryoneMask = slinventoryentry1.everyoneMask;
        exception.NextOwnerMask = slinventoryentry1.nextOwnerMask;
        exception.Name = SLMessage.stringToVariableOEM(slinventoryentry.name);
        exception.Description = SLMessage.stringToVariableOEM(slinventoryentry.description);
        ((RezMultipleAttachmentsFromInv) (obj1)).ObjectData_Fields.add(exception);
        obj2 = obj1;
        if (((RezMultipleAttachmentsFromInv) (obj1)).ObjectData_Fields.size() < 4)
        {
            break MISSING_BLOCK_LABEL_606;
        }
        obj1.isReliable = true;
        SendMessage(((SLMessage) (obj1)));
        exception = null;
          goto _L18
_L13:
        Debug.Printf("AvatarAppearance: MultiLayer: no extra layers.", new Object[0]);
          goto _L19
_L17:
        wantedAttachments.set(ImmutableMap.copyOf(hashmap));
        if (exception == null) goto _L4; else goto _L20
_L20:
        exception.isReliable = true;
        SendMessage(exception);
_L4:
        multiLayerDone = true;
        this;
        JVM INSTR monitorexit ;
        return;
_L15:
        Debug.Printf("AvatarAppearance: No attachments in COF.", new Object[0]);
          goto _L4
        exception = ((Exception) (obj2));
          goto _L18
    }

    private void UpdateServerSideAppearance(String s, int i)
    {
        this;
        JVM INSTR monitorenter ;
        Debug.Printf("AvatarAppearance: capURL '%s', cofVersion %d", new Object[] {
            s, Integer.valueOf(i)
        });
        if (serverSideAppearanceUpdateTask != null)
        {
            serverSideAppearanceUpdateTask.cancel(true);
        }
        serverSideAppearanceUpdateTask = GenericHTTPExecutor.getInstance().submit(new _2D_.Lambda.Jp5Too8LbDpaKzeYKjkvQvC1hZo._cls2(i, this, s));
        this;
        JVM INSTR monitorexit ;
        return;
        s;
        throw s;
    }

    private void UpdateWearableNames()
    {
        InventoryDB inventorydb = null;
        Object obj = (InventoryEntryList)currentOutfitFolder.getData();
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        }
        if (obj != null && inventorydb != null)
        {
            obj = ((Iterable) (obj)).iterator();
            do
            {
                if (!((Iterator) (obj)).hasNext())
                {
                    break;
                }
                SLInventoryEntry slinventoryentry = (SLInventoryEntry)((Iterator) (obj)).next();
                if (!slinventoryentry.isFolderOrFolderLink())
                {
                    slinventoryentry = inventorydb.resolveLink(slinventoryentry);
                    if (slinventoryentry != null && slinventoryentry.invType == SLInventoryType.IT_WEARABLE.getTypeCode())
                    {
                        Object obj1 = SLWearableType.getByCode(slinventoryentry.flags & 0xff);
                        if (obj1 != null)
                        {
                            obj1 = (SLWearable)wornWearables.get(obj1, slinventoryentry.assetUUID);
                            if (obj1 != null)
                            {
                                ((SLWearable) (obj1)).setInventoryName(slinventoryentry.name);
                            }
                        }
                    }
                }
            } while (true);
        }
    }

    private boolean WearItemList(InventoryDB inventorydb, List list, boolean flag)
    {
        RLVController rlvcontroller;
        HashBasedTable hashbasedtable;
        boolean flag2;
        rlvcontroller = agentCircuit.getModules().rlvController;
        hashbasedtable = HashBasedTable.create(wornWearables);
        list = list.iterator();
        flag2 = false;
_L4:
        if (!list.hasNext()) goto _L2; else goto _L1
_L1:
        SLInventoryEntry slinventoryentry = inventorydb.resolveLink((SLInventoryEntry)list.next());
        if (slinventoryentry == null) goto _L4; else goto _L3
_L3:
        SLWearableType slwearabletype = SLWearableType.getByCode(slinventoryentry.flags & 0xff);
        if (slwearabletype == null) goto _L4; else goto _L5
_L5:
        boolean flag1;
        Object obj;
        SLWearable slwearable;
        boolean flag3;
        if (!flag)
        {
            flag3 = slwearabletype.isBodyPart();
        } else
        {
            flag3 = true;
        }
        if (!rlvcontroller.canWearItem(slwearabletype))
        {
            flag1 = false;
        } else
        {
label0:
            {
                if (flag3)
                {
                    if (!(rlvcontroller.canTakeItemOff(slwearabletype) ^ true))
                    {
                        break MISSING_BLOCK_LABEL_403;
                    }
                    Iterator iterator = hashbasedtable.row(slwearabletype).keySet().iterator();
                    flag1 = false;
                    do
                    {
                        if (!iterator.hasNext())
                        {
                            break;
                        }
                        if (!((UUID)iterator.next()).equals(slinventoryentry.assetUUID))
                        {
                            flag1 = true;
                        }
                    } while (true);
                    break label0;
                }
                flag1 = true;
            }
        }
_L7:
        if (!flag1 || hashbasedtable.contains(slwearabletype, slinventoryentry.assetUUID)) goto _L4; else goto _L6
_L6:
        if (flag3)
        {
            obj = new HashSet(hashbasedtable.row(slwearabletype).keySet());
            ((Set) (obj)).remove(slinventoryentry.assetUUID);
            obj = ((Iterable) (obj)).iterator();
            do
            {
                if (!((Iterator) (obj)).hasNext())
                {
                    break;
                }
                slwearable = (SLWearable)hashbasedtable.remove(slwearabletype, (UUID)((Iterator) (obj)).next());
                if (slwearable != null)
                {
                    slwearable.dispose();
                }
            } while (true);
        }
        break MISSING_BLOCK_LABEL_323;
        if (!flag1)
        {
            break MISSING_BLOCK_LABEL_403;
        }
        flag1 = false;
          goto _L7
        addWearable(hashbasedtable, slwearabletype, slinventoryentry.uuid, slinventoryentry.assetUUID, slinventoryentry.name);
        flag2 = true;
          goto _L4
_L2:
        if (flag2)
        {
            wornWearables = ImmutableTable.copyOf(hashbasedtable);
            userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, wornWearables);
            userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        return flag2;
        flag1 = true;
          goto _L7
    }

    private SLWearable addWearable(Table table, SLWearableType slwearabletype, UUID uuid, UUID uuid1, String s)
    {
        uuid = new SLWearable(userManager, agentCircuit, uuid, uuid1, slwearabletype, this);
        if (s != null)
        {
            uuid.setInventoryName(s);
        }
        table.put(slwearabletype, uuid1, uuid);
        return uuid;
    }

    private boolean areWearablesReady()
    {
        boolean flag5 = false;
        SLWearableType aslwearabletype[] = SLWearableType.values();
        int k = aslwearabletype.length;
        int j = 0;
        boolean flag3 = false;
        boolean flag2 = false;
        while (j < k) 
        {
            SLWearableType slwearabletype = aslwearabletype[j];
            boolean flag6 = slwearabletype.getIsCritical();
            Map map = wornWearables.row(slwearabletype);
            boolean flag1;
            boolean flag4;
            if (map != null)
            {
                Iterator iterator = map.values().iterator();
                boolean flag = false;
                do
                {
                    flag1 = flag;
                    flag4 = flag2;
                    if (!iterator.hasNext())
                    {
                        break;
                    }
                    SLWearable slwearable = (SLWearable)iterator.next();
                    if (slwearable.getIsValid())
                    {
                        flag = true;
                    } else
                    if (!slwearable.getIsFailed())
                    {
                        flag2 = true;
                    }
                } while (true);
            } else
            {
                flag1 = false;
                flag4 = flag2;
            }
            if (flag6)
            {
                if (flag1 ^ true)
                {
                    int i;
                    if (map != null)
                    {
                        i = map.size();
                    } else
                    {
                        i = 0;
                    }
                    Debug.Printf("missing wearables on critical layer %s (worn: %d entries)", new Object[] {
                        slwearabletype, Integer.valueOf(i)
                    });
                    flag2 = true;
                } else
                {
                    flag2 = flag3;
                }
            } else
            {
                flag2 = flag3;
            }
            j++;
            flag3 = flag2;
            flag2 = flag4;
        }
        Debug.Printf("hasNotDownloaded %b, hasCriticalMissing %b", new Object[] {
            Boolean.valueOf(flag2), Boolean.valueOf(flag3)
        });
        flag4 = flag5;
        if (!flag2)
        {
            flag4 = flag3 ^ true;
        }
        return flag4;
    }

    private boolean canDetachItem(UUID uuid)
    {
        Object obj;
        if (parcelInfo == null)
        {
            break MISSING_BLOCK_LABEL_114;
        }
        obj = parcelInfo.getAgentAvatar();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_114;
        }
        obj = ((SLObjectInfo) (obj)).treeNode.iterator();
        boolean flag;
        do
        {
            SLObjectInfo slobjectinfo;
            do
            {
                if (!((Iterator) (obj)).hasNext())
                {
                    break MISSING_BLOCK_LABEL_114;
                }
                slobjectinfo = (SLObjectInfo)((Iterator) (obj)).next();
            } while (slobjectinfo.attachedToUUID == null || !(slobjectinfo.isDead ^ true) || !slobjectinfo.attachedToUUID.equals(uuid));
            int i = slobjectinfo.attachmentID;
            flag = agentCircuit.getModules().rlvController.canDetachItem(i, slobjectinfo.getId());
        } while (flag);
        return false;
        uuid;
        Debug.Warning(uuid);
        return true;
    }

    private boolean canWearItem(SLWearableType slwearabletype)
    {
        return agentCircuit.getModules().rlvController.canWearItem(slwearabletype);
    }

    private float getAgentHeight()
    {
        return agentSizeVPLegLength * 0.1918F + 1.706F + agentSizeVPHipLength * 0.0375F + agentSizeVPHeight * 0.12022F + agentSizeVPHeadSize * 0.01117F + agentSizeVPNeckLength * 0.038F + agentSizeVPHeelHeight * 0.08F + agentSizeVPPlatformHeight * 0.07F;
    }

    private int[] getAppearanceParams()
    {
        int ai[];
        int k;
        ai = new int[218];
        k = 0;
_L14:
        if (k >= 218) goto _L2; else goto _L1
_L1:
        Object obj;
        ai[k] = 0;
        obj = SLAvatarParams.paramDefs[k];
        if (obj == null || ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj)).params.size() <= 0) goto _L4; else goto _L3
_L3:
        obj = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet) (obj)).params.get(0);
        if (obj == null) goto _L4; else goto _L5
_L5:
        int i1 = Math.round(((((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).defValue - ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).minValue) * 255F) / (((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).maxValue - ((com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam) (obj)).minValue));
        if (i1 >= 0) goto _L7; else goto _L6
_L6:
        int i = 0;
_L8:
        ai[k] = i;
_L4:
        k++;
        continue; /* Loop/switch isn't completed */
_L7:
        i = i1;
        if (i1 > 255)
        {
            i = 255;
        }
        if (true) goto _L8; else goto _L2
_L2:
        obj = wornWearables.values().iterator();
_L10:
        Object obj1;
        if (!((Iterator) (obj)).hasNext())
        {
            break MISSING_BLOCK_LABEL_487;
        }
        obj1 = ((SLWearable)((Iterator) (obj)).next()).getWearableData();
        if (obj1 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        obj1 = ((SLWearableData) (obj1)).params.iterator();
_L12:
        com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableParam wearableparam;
        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet paramset;
        do
        {
            if (!((Iterator) (obj1)).hasNext())
            {
                continue; /* Loop/switch isn't completed */
            }
            wearableparam = (com.lumiyaviewer.lumiya.slproto.assets.SLWearableData.WearableParam)((Iterator) (obj1)).next();
            paramset = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.ParamSet)SLAvatarParams.paramByIDs.get(Integer.valueOf(wearableparam.paramIndex));
        } while (paramset == null || paramset.params.size() <= 0 || paramset.appearanceIndex < 0);
        break; /* Loop/switch isn't completed */
        if (true) goto _L10; else goto _L9
_L9:
        com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam avatarparam = (com.lumiyaviewer.lumiya.slproto.avatar.SLAvatarParams.AvatarParam)paramset.params.get(0);
        int l = Math.round(((wearableparam.paramValue - avatarparam.minValue) * 255F) / (avatarparam.maxValue - avatarparam.minValue));
        int j;
        if (l < 0)
        {
            j = 0;
        } else
        {
            j = l;
            if (l > 255)
            {
                j = 255;
            }
        }
        ai[paramset.appearanceIndex] = j;
        switch (paramset.id)
        {
        case 33: // '!'
            agentSizeVPHeight = wearableparam.paramValue;
            break;

        case 198: 
            agentSizeVPHeelHeight = wearableparam.paramValue;
            break;

        case 503: 
            agentSizeVPPlatformHeight = wearableparam.paramValue;
            break;

        case 682: 
            agentSizeVPHeadSize = wearableparam.paramValue;
            break;

        case 692: 
            agentSizeVPLegLength = wearableparam.paramValue;
            break;

        case 756: 
            agentSizeVPNeckLength = wearableparam.paramValue;
            break;

        case 842: 
            agentSizeVPHipLength = wearableparam.paramValue;
            break;
        }
        continue; /* Loop/switch isn't completed */
        if (true) goto _L12; else goto _L11
_L11:
        agentSizeKnown = true;
        return ai;
        if (true) goto _L14; else goto _L13
_L13:
    }

    private ImmutableList getWornItems()
    {
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        Iterator iterator = wornWearables.cellSet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            com.google.common.collect.Table.Cell cell = (com.google.common.collect.Table.Cell)iterator.next();
            SLWearable slwearable = (SLWearable)cell.getValue();
            if (slwearable != null)
            {
                builder.add(new WornItem((SLWearableType)cell.getRowKey(), 0, (UUID)cell.getColumnKey(), slwearable.getName(), 0, false));
            }
        } while (true);
        if (parcelInfo != null)
        {
            Object obj = parcelInfo.getAgentAvatar();
            if (obj != null)
            {
                try
                {
                    SLObjectInfo slobjectinfo;
                    for (obj = ((SLObjectInfo) (obj)).treeNode.iterator(); ((Iterator) (obj)).hasNext(); builder.add(new WornItem(null, slobjectinfo.attachmentID, slobjectinfo.getId(), slobjectinfo.getName(), slobjectinfo.localID, slobjectinfo.isTouchable())))
                    {
                        slobjectinfo = (SLObjectInfo)((Iterator) (obj)).next();
                    }

                }
                catch (NoSuchElementException nosuchelementexception)
                {
                    Debug.Warning(nosuchelementexception);
                }
            }
        }
        return builder.build();
    }

    private boolean isItemWorn(SLInventoryEntry slinventoryentry, boolean flag)
    {
        return slinventoryentry.whatIsItemWornOn(wornAttachments, wornWearables, flag) != null;
    }

    private void onCofFolderEntry(InventoryEntryList inventoryentrylist)
    {
label0:
        {
            if (inventoryentrylist == null)
            {
                break label0;
            }
            inventoryentrylist = inventoryentrylist.iterator();
            SLInventoryEntry slinventoryentry;
            do
            {
                if (!inventoryentrylist.hasNext())
                {
                    break label0;
                }
                slinventoryentry = (SLInventoryEntry)inventoryentrylist.next();
            } while (slinventoryentry == null || !slinventoryentry.isFolder || slinventoryentry.typeDefault != 46);
            cofFolderUUID.set(slinventoryentry.uuid);
            findCofFolder.unsubscribe();
            currentOutfitFolder.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(slinventoryentry.uuid, null, true, true, false, null));
        }
    }

    private void onCurrentOutfitFolder(InventoryEntryList inventoryentrylist)
    {
        if (inventoryentrylist != null)
        {
            SLInventoryEntry slinventoryentry = inventoryentrylist.getFolder();
            if (slinventoryentry != null && Objects.equal(slinventoryentry.sessionID, agentCircuit.circuitInfo.sessionID))
            {
                Debug.Log("AvatarAppearance: COF has been fetched from inventory.");
                UpdateWearableNames();
                cofReady = true;
                UpdateCurrentOutfitLink(inventoryentrylist);
                ProcessMultiLayer();
                UpdateCOFContents();
                RequestServerRebake();
            }
        }
    }

    private void startBaking()
    {
        BakeProcess bakeprocess = bakeProcess;
        if (bakeprocess != null)
        {
            bakeprocess.cancel();
        }
        bakeProcess = new BakeProcess(wornWearables, this, agentCircuit.getModules().textureUploader, eventBus);
    }

    private void updateIfWearablesReady()
    {
label0:
        {
            if (areWearablesReady())
            {
                SendAvatarSetAppearance();
                if (!needUpdateAppearance)
                {
                    break label0;
                }
                if (caps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.UpdateAvatarAppearance) == null)
                {
                    startBaking();
                }
            }
            return;
        }
        UpdateCOFContents();
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_2D_mthref_2D_0(InventoryEntryList inventoryentrylist)
    {
        onCurrentOutfitFolder(inventoryentrylist);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_2D_mthref_2D_1(InventoryEntryList inventoryentrylist)
    {
        onCofFolderEntry(inventoryentrylist);
    }

    public void AttachInventoryItem(SLInventoryEntry slinventoryentry, int i, boolean flag)
    {
        InventoryDB inventorydb = null;
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry slinventoryentry1 = slinventoryentry;
        if (inventorydb != null)
        {
            slinventoryentry1 = inventorydb.resolveLink(slinventoryentry);
        }
        if (slinventoryentry1 == null)
        {
            return;
        }
        if (slinventoryentry1.assetType == SLAssetType.AT_CLOTHING.getTypeCode() || slinventoryentry1.assetType == SLAssetType.AT_BODYPART.getTypeCode())
        {
            WearItem(slinventoryentry1, flag);
            return;
        }
        Debug.Printf("Outfits: Attaching inventory item %s", new Object[] {
            slinventoryentry1.uuid.toString()
        });
        slinventoryentry = (Map)wantedAttachments.get();
        boolean flag1;
        int j;
        if (slinventoryentry == null || slinventoryentry.containsKey(slinventoryentry1.uuid) ^ true)
        {
            HashMap hashmap = new HashMap();
            if (slinventoryentry != null)
            {
                hashmap.putAll(slinventoryentry);
            }
            hashmap.put(slinventoryentry1.uuid, slinventoryentry1.name);
            wantedAttachments.set(ImmutableMap.copyOf(hashmap));
            flag1 = true;
        } else
        {
            flag1 = false;
        }
        slinventoryentry = new RezSingleAttachmentFromInv();
        ((RezSingleAttachmentFromInv) (slinventoryentry)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).AgentData_Field.SessionID = circuitInfo.sessionID;
        j = i;
        if (!flag)
        {
            j = i | 0x80;
        }
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.ItemID = slinventoryentry1.uuid;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.OwnerID = slinventoryentry1.ownerUUID;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.AttachmentPt = j;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.ItemFlags = slinventoryentry1.flags;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.GroupMask = slinventoryentry1.groupMask;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.EveryoneMask = slinventoryentry1.everyoneMask;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.NextOwnerMask = slinventoryentry1.nextOwnerMask;
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.Name = SLMessage.stringToVariableOEM(slinventoryentry1.name);
        ((RezSingleAttachmentFromInv) (slinventoryentry)).ObjectData_Field.Description = SLMessage.stringToVariableOEM(slinventoryentry1.description);
        slinventoryentry.isReliable = true;
        SendMessage(slinventoryentry);
        if (flag1)
        {
            needUpdateCOF.set(true);
            UpdateCOFContents();
        }
    }

    public void ChangeOutfit(List list, boolean flag, SLInventoryEntry slinventoryentry)
    {
        Object obj;
        Object obj1;
        Object obj2;
        Object obj3;
        Object obj4;
        Object obj5;
        boolean flag1;
        if (userManager != null)
        {
            obj1 = userManager.getInventoryManager().getDatabase();
        } else
        {
            obj1 = null;
        }
        obj = (Map)wantedAttachments.get();
        if (obj != null)
        {
            obj = new HashMap(((Map) (obj)));
        } else
        {
            obj = new HashMap();
        }
        Object obj6;
        Object obj7;
        UUID uuid;
        int i;
        boolean flag2;
        boolean flag3;
        int j;
        if (flag)
        {
            flag1 = true;
            ((Map) (obj)).clear();
        } else
        {
            flag1 = false;
        }
        if (slinventoryentry == null || !flag) goto _L2; else goto _L1
_L1:
        if (wantedOutfitFolder != null) goto _L4; else goto _L3
_L3:
        wantedOutfitFolder = slinventoryentry;
        flag1 = true;
_L2:
        obj3 = UUID.randomUUID();
        obj4 = new ArrayList();
        obj5 = list.iterator();
        do
        {
            if (!((Iterator) (obj5)).hasNext())
            {
                break;
            }
            obj2 = (SLInventoryEntry)((Iterator) (obj5)).next();
            if (obj1 != null)
            {
                slinventoryentry = ((InventoryDB) (obj1)).resolveLink(((SLInventoryEntry) (obj2)));
            } else
            {
                slinventoryentry = ((SLInventoryEntry) (obj2));
            }
            if (slinventoryentry != null)
            {
                obj2 = slinventoryentry;
            }
            if (obj2 != null && (((SLInventoryEntry) (obj2)).assetType == SLAssetType.AT_OBJECT.getTypeCode() || ((SLInventoryEntry) (obj2)).isLink() && ((SLInventoryEntry) (obj2)).invType == SLInventoryType.IT_OBJECT.getTypeCode()))
            {
                ((List) (obj4)).add(obj2);
            }
        } while (true);
        break; /* Loop/switch isn't completed */
_L4:
        if (!wantedOutfitFolder.uuid.equals(slinventoryentry.uuid))
        {
            wantedOutfitFolder = slinventoryentry;
            flag1 = true;
        }
        if (true) goto _L2; else goto _L5
_L5:
        slinventoryentry = new RezMultipleAttachmentsFromInv();
        ((RezMultipleAttachmentsFromInv) (slinventoryentry)).AgentData_Field.AgentID = circuitInfo.agentID;
        ((RezMultipleAttachmentsFromInv) (slinventoryentry)).AgentData_Field.SessionID = circuitInfo.sessionID;
        ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.CompoundMsgID = ((UUID) (obj3));
        ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.TotalObjects = ((List) (obj4)).size();
        ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.FirstDetachAll = flag;
        Debug.Printf("Wearing: totalAttachments %d", new Object[] {
            Integer.valueOf(((List) (obj4)).size())
        });
        obj2 = ((Iterable) (obj4)).iterator();
        for (flag2 = flag1; ((Iterator) (obj2)).hasNext(); flag2 = true)
        {
            obj5 = (SLInventoryEntry)((Iterator) (obj2)).next();
            if (slinventoryentry == null)
            {
                slinventoryentry = new RezMultipleAttachmentsFromInv();
                ((RezMultipleAttachmentsFromInv) (slinventoryentry)).AgentData_Field.AgentID = circuitInfo.agentID;
                ((RezMultipleAttachmentsFromInv) (slinventoryentry)).AgentData_Field.SessionID = circuitInfo.sessionID;
                ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.CompoundMsgID = ((UUID) (obj3));
                ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.TotalObjects = ((List) (obj4)).size();
                ((RezMultipleAttachmentsFromInv) (slinventoryentry)).HeaderData_Field.FirstDetachAll = flag;
            }
            obj6 = new com.lumiyaviewer.lumiya.slproto.messages.RezMultipleAttachmentsFromInv.ObjectData();
            obj7 = ((SLInventoryEntry) (obj5)).uuid;
            Debug.Printf("Wearing: entry '%s' actualUUID %s", new Object[] {
                ((SLInventoryEntry) (obj5)).name, obj7
            });
            ((Map) (obj)).put(obj7, ((SLInventoryEntry) (obj5)).name);
            obj6.ItemID = ((UUID) (obj7));
            obj6.OwnerID = ((SLInventoryEntry) (obj5)).ownerUUID;
            obj6.AttachmentPt = 128;
            obj6.ItemFlags = ((SLInventoryEntry) (obj5)).flags;
            obj6.GroupMask = ((SLInventoryEntry) (obj5)).groupMask;
            obj6.EveryoneMask = ((SLInventoryEntry) (obj5)).everyoneMask;
            obj6.NextOwnerMask = ((SLInventoryEntry) (obj5)).nextOwnerMask;
            obj6.Name = SLMessage.stringToVariableOEM(((SLInventoryEntry) (obj5)).name);
            obj6.Description = SLMessage.stringToVariableOEM(((SLInventoryEntry) (obj5)).description);
            ((RezMultipleAttachmentsFromInv) (slinventoryentry)).ObjectData_Fields.add(obj6);
            if (((RezMultipleAttachmentsFromInv) (slinventoryentry)).ObjectData_Fields.size() >= 4)
            {
                slinventoryentry.isReliable = true;
                SendMessage(slinventoryentry);
                slinventoryentry = null;
            }
        }

        if (slinventoryentry != null)
        {
            slinventoryentry.isReliable = true;
            SendMessage(slinventoryentry);
        }
        slinventoryentry = agentCircuit.getModules().rlvController;
        obj2 = new HashSet();
        obj3 = HashBasedTable.create(wornWearables);
        obj4 = list.iterator();
        flag1 = false;
_L11:
        if (!((Iterator) (obj4)).hasNext()) goto _L7; else goto _L6
_L6:
        list = (SLInventoryEntry)((Iterator) (obj4)).next();
        if (obj1 != null)
        {
            list = ((InventoryDB) (obj1)).resolveLink(list);
        }
        flag3 = flag1;
        if (list == null) goto _L9; else goto _L8
_L8:
        if (((SLInventoryEntry) (list)).assetType != SLAssetType.AT_BODYPART.getTypeCode() && ((SLInventoryEntry) (list)).assetType != SLAssetType.AT_CLOTHING.getTypeCode()) goto _L11; else goto _L10
_L10:
        obj5 = SLWearableType.getByCode(((SLInventoryEntry) (list)).flags & 0xff);
        flag3 = flag1;
        if (obj5 == null) goto _L9; else goto _L12
_L12:
        if (!slinventoryentry.canWearItem(((SLWearableType) (obj5))))
        {
            i = 0;
        } else
        {
label0:
            {
                if (((SLWearableType) (obj5)).isBodyPart())
                {
                    if (!(slinventoryentry.canTakeItemOff(((SLWearableType) (obj5))) ^ true))
                    {
                        break MISSING_BLOCK_LABEL_1449;
                    }
                    obj6 = ((Table) (obj3)).row(obj5).keySet().iterator();
                    i = 0;
                    do
                    {
                        if (!((Iterator) (obj6)).hasNext())
                        {
                            break;
                        }
                        if (!((UUID)((Iterator) (obj6)).next()).equals(((SLInventoryEntry) (list)).assetUUID))
                        {
                            i = 1;
                        }
                    } while (true);
                    break label0;
                }
                i = 1;
            }
        }
_L13:
        flag3 = flag1;
        if (i != 0)
        {
            ((Set) (obj2)).add(((SLInventoryEntry) (list)).assetUUID);
            flag3 = flag1;
            if (!((Table) (obj3)).contains(obj5, ((SLInventoryEntry) (list)).assetUUID))
            {
                addWearable(((Table) (obj3)), ((SLWearableType) (obj5)), ((SLInventoryEntry) (list)).uuid, ((SLInventoryEntry) (list)).assetUUID, ((SLInventoryEntry) (list)).name);
                flag1 = true;
                flag3 = flag1;
                if (((SLWearableType) (obj5)).isBodyPart())
                {
                    obj6 = new HashSet();
                    obj7 = ((Table) (obj3)).row(obj5).keySet().iterator();
                    do
                    {
                        if (!((Iterator) (obj7)).hasNext())
                        {
                            break;
                        }
                        uuid = (UUID)((Iterator) (obj7)).next();
                        if (!uuid.equals(((SLInventoryEntry) (list)).assetUUID))
                        {
                            ((Set) (obj6)).add(uuid);
                        }
                    } while (true);
                    break MISSING_BLOCK_LABEL_1048;
                }
            }
        }
          goto _L9
        if (i == 0)
        {
            break MISSING_BLOCK_LABEL_1449;
        }
        i = 0;
          goto _L13
        list = ((Iterable) (obj6)).iterator();
        do
        {
            flag3 = flag1;
            if (!list.hasNext())
            {
                break;
            }
            obj6 = (UUID)list.next();
            if (((Table) (obj3)).row(obj5).size() > 1)
            {
                obj6 = (SLWearable)((Table) (obj3)).remove(obj5, obj6);
                if (obj6 != null)
                {
                    ((SLWearable) (obj6)).dispose();
                }
            }
        } while (true);
          goto _L9
_L7:
        if (!flag) goto _L15; else goto _L14
_L14:
        list = SLWearableType.values();
        j = list.length;
        i = 0;
_L20:
        flag3 = flag1;
        if (i >= j) goto _L17; else goto _L16
_L16:
        obj1 = list[i];
        if (!((SLWearableType) (obj1)).isBodyPart()) goto _L19; else goto _L18
_L18:
        flag3 = flag1;
_L22:
        i++;
        flag1 = flag3;
          goto _L20
_L19:
        flag3 = flag1;
        if (!slinventoryentry.canTakeItemOff(((SLWearableType) (obj1)))) goto _L22; else goto _L21
_L21:
        obj1 = ((Table) (obj3)).row(obj1);
        obj4 = new HashSet();
        obj5 = ((Map) (obj1)).keySet().iterator();
        do
        {
            if (!((Iterator) (obj5)).hasNext())
            {
                break;
            }
            obj6 = (UUID)((Iterator) (obj5)).next();
            if (!((Set) (obj2)).contains(obj6))
            {
                ((Set) (obj4)).add(obj6);
            }
        } while (true);
        for (obj4 = ((Iterable) (obj4)).iterator(); ((Iterator) (obj4)).hasNext();)
        {
            obj5 = (SLWearable)((Map) (obj1)).remove((UUID)((Iterator) (obj4)).next());
            if (obj5 != null)
            {
                ((SLWearable) (obj5)).dispose();
            }
            flag1 = true;
        }

        break MISSING_BLOCK_LABEL_1435;
_L15:
        flag3 = flag1;
_L17:
        if (flag3)
        {
            wornWearables = ImmutableTable.copyOf(((Table) (obj3)));
            userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, wornWearables);
            userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
        if (flag2)
        {
            wantedAttachments.set(ImmutableMap.copyOf(((Map) (obj))));
        }
        if (flag3)
        {
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
            flag2 = false;
        }
        if (flag2)
        {
            needUpdateCOF.set(true);
            UpdateCOFContents();
        }
        return;
        flag3 = flag1;
          goto _L22
_L9:
        flag1 = flag3;
          goto _L11
        i = 1;
          goto _L13
    }

    public void DetachInventoryItem(SLInventoryEntry slinventoryentry)
    {
        if (canDetachItem(slinventoryentry))
        {
            Object obj = slinventoryentry.uuid;
            boolean flag;
            if (slinventoryentry.isLink())
            {
                slinventoryentry = slinventoryentry.assetUUID;
            } else
            {
                slinventoryentry = ((SLInventoryEntry) (obj));
            }
            Debug.Log((new StringBuilder()).append("Outfits: Detaching inventory item ").append(slinventoryentry).toString());
            obj = (Map)wantedAttachments.get();
            if (obj != null)
            {
                if (((Map) (obj)).containsKey(slinventoryentry))
                {
                    obj = new HashMap(((Map) (obj)));
                    ((Map) (obj)).remove(slinventoryentry);
                    wantedAttachments.set(ImmutableMap.copyOf(((Map) (obj))));
                    flag = true;
                } else
                {
                    flag = false;
                }
            } else
            {
                flag = false;
            }
            obj = new DetachAttachmentIntoInv();
            ((DetachAttachmentIntoInv) (obj)).ObjectData_Field.AgentID = circuitInfo.agentID;
            ((DetachAttachmentIntoInv) (obj)).ObjectData_Field.ItemID = slinventoryentry;
            obj.isReliable = true;
            SendMessage(((SLMessage) (obj)));
            if (flag)
            {
                needUpdateCOF.set(true);
                UpdateCOFContents();
            }
        }
    }

    public void DetachItem(WornItem wornitem)
    {
        if (canDetachItem(wornitem))
        {
            DetachItem(WornItem._2D_get0(wornitem));
        }
    }

    public void DetachItemFromPoint(int i)
    {
        HashSet hashset;
        HashSet hashset1;
        HashSet hashset2;
        Object obj1;
        hashset1 = null;
        Object obj = null;
        hashset = null;
        hashset2 = obj;
        if (parcelInfo == null)
        {
            break MISSING_BLOCK_LABEL_188;
        }
        obj1 = parcelInfo.getAgentAvatar();
        hashset2 = obj;
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_188;
        }
        Iterator iterator1 = ((SLObjectInfo) (obj1)).treeNode.iterator();
_L2:
        hashset1 = hashset;
        hashset2 = hashset;
        if (!iterator1.hasNext())
        {
            break MISSING_BLOCK_LABEL_188;
        }
        hashset1 = hashset;
        obj1 = (SLObjectInfo)iterator1.next();
        hashset1 = hashset;
        if (((SLObjectInfo) (obj1)).attachedToUUID == null) goto _L2; else goto _L1
_L1:
        hashset1 = hashset;
        if (!(((SLObjectInfo) (obj1)).isDead ^ true)) goto _L2; else goto _L3
_L3:
        hashset1 = hashset;
        if (((SLObjectInfo) (obj1)).attachmentID != i) goto _L2; else goto _L4
_L4:
        hashset1 = hashset;
        if (!agentCircuit.getModules().rlvController.canDetachItem(i, ((SLObjectInfo) (obj1)).getId())) goto _L2; else goto _L5
_L5:
        hashset2 = hashset;
        if (hashset != null)
        {
            break MISSING_BLOCK_LABEL_152;
        }
        hashset1 = hashset;
        hashset2 = new HashSet();
        hashset1 = hashset2;
        hashset2.add(Integer.valueOf(((SLObjectInfo) (obj1)).localID));
        hashset = hashset2;
          goto _L2
        NoSuchElementException nosuchelementexception;
        nosuchelementexception;
        Debug.Warning(nosuchelementexception);
        hashset2 = hashset1;
        if (hashset2 != null)
        {
            for (Iterator iterator = hashset2.iterator(); iterator.hasNext(); DetachItem(((Integer)iterator.next()).intValue())) { }
        }
        return;
    }

    public void ForceTakeItemOff(SLWearableType slwearabletype)
    {
        boolean flag;
        if (!wornWearables.row(slwearabletype).isEmpty())
        {
            flag = true;
            HashBasedTable hashbasedtable = HashBasedTable.create(wornWearables);
            hashbasedtable.rowKeySet().remove(slwearabletype);
            wornWearables = ImmutableTable.copyOf(hashbasedtable);
            userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, wornWearables);
            userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        } else
        {
            flag = false;
        }
        if (flag)
        {
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    public void HandleAgentWearablesUpdate(AgentWearablesUpdate agentwearablesupdate)
    {
        Debug.Log((new StringBuilder()).append("AvatarAppearance: Got AgentWearablesUpdate, ").append(agentwearablesupdate.WearableData_Fields.size()).append(" wearables.").toString());
        HashSet hashset = new HashSet();
        HashBasedTable hashbasedtable = HashBasedTable.create(wornWearables);
        agentwearablesupdate = agentwearablesupdate.WearableData_Fields.iterator();
        do
        {
            if (!agentwearablesupdate.hasNext())
            {
                break;
            }
            com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesUpdate.WearableData wearabledata = (com.lumiyaviewer.lumiya.slproto.messages.AgentWearablesUpdate.WearableData)agentwearablesupdate.next();
            Debug.Log((new StringBuilder()).append("Wearable: type = ").append(wearabledata.WearableType).append(", itemID = ").append(wearabledata.ItemID).append(", assetID = ").append(wearabledata.AssetID).toString());
            if (wearabledata.AssetID.getLeastSignificantBits() != 0L || wearabledata.AssetID.getMostSignificantBits() != 0L)
            {
                hashset.add(wearabledata.AssetID);
                SLWearableType slwearabletype = SLWearableType.getByCode(wearabledata.WearableType);
                if (slwearabletype != null && (SLWearable)hashbasedtable.get(slwearabletype, wearabledata.AssetID) == null)
                {
                    addWearable(hashbasedtable, slwearabletype, wearabledata.ItemID, wearabledata.AssetID, null);
                }
            }
        } while (true);
        Debug.Log((new StringBuilder()).append("AvatarAppearance: AgentWearablesUpdate: wearing now: ").append(hashset.size()).append(" ids").toString());
        agentwearablesupdate = new HashSet();
        Iterator iterator = hashbasedtable.columnKeySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            UUID uuid = (UUID)iterator.next();
            if (!hashset.contains(uuid))
            {
                agentwearablesupdate.add(uuid);
            }
        } while (true);
        Map map;
        for (agentwearablesupdate = agentwearablesupdate.iterator(); agentwearablesupdate.hasNext(); map.clear())
        {
            map = hashbasedtable.column((UUID)agentwearablesupdate.next());
            for (Iterator iterator1 = map.values().iterator(); iterator1.hasNext(); ((SLWearable)iterator1.next()).dispose()) { }
        }

        wornWearables = ImmutableTable.copyOf(hashbasedtable);
        userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, wornWearables);
        userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        UpdateWearableNames();
        legacyAppearanceReady = true;
        ProcessMultiLayer();
        SendAgentIsNowWearing();
        StartUpdatingAppearance();
    }

    public void HandleAvatarAppearance(AvatarAppearance avatarappearance)
    {
        if (avatarappearance.AppearanceData_Fields.size() > 0)
        {
            currentCofAppearanceVersion = ((com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance.AppearanceData)avatarappearance.AppearanceData_Fields.get(0)).CofVersion;
            Debug.Printf("AvatarAppearance: inventory COF %d, last updated COF %d, appearance COF %d", new Object[] {
                Integer.valueOf(currentCofInventoryVersion), Integer.valueOf(lastCofUpdatedVersion), Integer.valueOf(currentCofAppearanceVersion)
            });
        }
    }

    public void HandleCircuitReady()
    {
        boolean flag;
        flag = true;
        super.HandleCircuitReady();
        if (userManager == null) goto _L2; else goto _L1
_L1:
        Object obj = userManager.getInventoryManager().getRootFolder();
        if (obj == null) goto _L4; else goto _L3
_L3:
        obj = userManager.getInventoryManager().getDatabase().findSpecialFolder(((UUID) (obj)), 46);
        if (obj == null) goto _L4; else goto _L5
_L5:
        Debug.Printf("Found existing COF folder: %s", new Object[] {
            ((SLInventoryEntry) (obj)).uuid
        });
        cofFolderUUID.set(((SLInventoryEntry) (obj)).uuid);
        currentOutfitFolder.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.create(((SLInventoryEntry) (obj)).uuid, null, true, true, false, null));
_L7:
        if (!flag)
        {
            Debug.Printf("Existing COF folder not found, requesting.", new Object[0]);
            findCofFolder.subscribe(userManager.getInventoryManager().getInventoryEntries(), InventoryQuery.findFolderWithType(null, 46));
        }
_L2:
        return;
_L4:
        flag = false;
        if (true) goto _L7; else goto _L6
_L6:
    }

    public void HandleCloseCircuit()
    {
        findCofFolder.unsubscribe();
        currentOutfitFolder.unsubscribe();
        if (userManager != null)
        {
            userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, null);
            userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, null);
            userManager.wornItems().detachRequestHandler(wornItemsRequestHandler);
        }
        if (bakingThread != null)
        {
            bakingThread.interrupt();
            bakingThread = null;
        }
        if (serverSideAppearanceUpdateTask != null)
        {
            serverSideAppearanceUpdateTask.cancel(true);
        }
        super.HandleCloseCircuit();
    }

    public void OnMyAvatarCreated(SLObjectAvatarInfo slobjectavatarinfo)
    {
        if (agentVisualParams != null)
        {
            slobjectavatarinfo.ApplyAvatarVisualParams(agentVisualParams);
        }
    }

    public void SendAgentWearablesRequest()
    {
        AgentWearablesRequest agentwearablesrequest = new AgentWearablesRequest();
        agentwearablesrequest.AgentData_Field.AgentID = circuitInfo.agentID;
        agentwearablesrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentwearablesrequest.isReliable = true;
        SendMessage(agentwearablesrequest);
    }

    public void TakeItemOff(SLInventoryEntry slinventoryentry)
    {
        InventoryDB inventorydb = null;
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry slinventoryentry1 = slinventoryentry;
        if (inventorydb != null)
        {
            slinventoryentry1 = inventorydb.resolveLink(slinventoryentry);
        }
        if (slinventoryentry1 != null)
        {
            TakeItemOff(slinventoryentry1.assetUUID);
        }
    }

    public void TakeItemOff(UUID uuid)
    {
        RLVController rlvcontroller = agentCircuit.getModules().rlvController;
        HashBasedTable hashbasedtable = HashBasedTable.create(wornWearables);
        SLWearableType aslwearabletype[] = SLWearableType.values();
        int j = aslwearabletype.length;
        int i = 0;
        boolean flag = false;
        while (i < j) 
        {
            Object obj = aslwearabletype[i];
            if (rlvcontroller.canTakeItemOff(((SLWearableType) (obj))))
            {
                obj = (SLWearable)hashbasedtable.remove(obj, uuid);
                if (obj != null)
                {
                    ((SLWearable) (obj)).dispose();
                    hashbasedtable.columnKeySet().remove(uuid);
                    flag = true;
                }
            }
            i++;
        }
        if (flag)
        {
            wornWearables = ImmutableTable.copyOf(hashbasedtable);
            userManager.getWornWearablesPool().setData(SubscriptionSingleKey.Value, wornWearables);
            userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    public void UpdateMyAttachments()
    {
        Object obj = new HashMap();
        if (parcelInfo != null)
        {
            Object obj1 = parcelInfo.getAgentAvatar();
            if (obj1 != null)
            {
                try
                {
                    obj1 = ((SLObjectInfo) (obj1)).treeNode.iterator();
                    do
                    {
                        if (!((Iterator) (obj1)).hasNext())
                        {
                            break;
                        }
                        SLObjectInfo slobjectinfo = (SLObjectInfo)((Iterator) (obj1)).next();
                        if (slobjectinfo.attachedToUUID != null && slobjectinfo.isDead ^ true)
                        {
                            ((Map) (obj)).put(slobjectinfo.attachedToUUID, Strings.nullToEmpty(slobjectinfo.getName()));
                        }
                    } while (true);
                }
                catch (NoSuchElementException nosuchelementexception)
                {
                    nosuchelementexception.printStackTrace();
                }
            }
        }
        obj = ImmutableMap.copyOf(((Map) (obj)));
        if (!wornAttachments.equals(obj))
        {
            Debug.Log("AvatarAppearance: attachments changed.");
            wornAttachments = ((ImmutableMap) (obj));
            userManager.getWornAttachmentsPool().setData(SubscriptionSingleKey.Value, wornAttachments);
            userManager.getObjectsManager().myAvatarState().requestUpdate(SubscriptionSingleKey.Value);
            userManager.wornItems().requestUpdate(SubscriptionSingleKey.Value);
        }
    }

    public void WearItem(SLInventoryEntry slinventoryentry, boolean flag)
    {
        InventoryDB inventorydb = null;
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        }
        if (inventorydb != null)
        {
            WearItemList(inventorydb, ImmutableList.of(slinventoryentry), flag);
            SendAgentIsNowWearing();
            ForceUpdateAppearance(false);
        }
    }

    public boolean canDetachItem(SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry.assetType == SLAssetType.AT_LINK.getTypeCode())
        {
            if (slinventoryentry.invType == SLInventoryType.IT_WEARABLE.getTypeCode())
            {
                return true;
            }
            if (slinventoryentry.invType == SLInventoryType.IT_OBJECT.getTypeCode() && wornAttachments.containsKey(slinventoryentry.assetUUID))
            {
                return canDetachItem(slinventoryentry.assetUUID);
            }
        } else
        {
            if (slinventoryentry.assetType == SLAssetType.AT_BODYPART.getTypeCode() || slinventoryentry.assetType == SLAssetType.AT_CLOTHING.getTypeCode())
            {
                return true;
            }
            if (slinventoryentry.assetType == SLAssetType.AT_OBJECT.getTypeCode())
            {
                if (wornAttachments.containsKey(slinventoryentry.uuid) && !canDetachItem(slinventoryentry.uuid))
                {
                    return false;
                }
                return !wornAttachments.containsKey(slinventoryentry.assetUUID) || canDetachItem(slinventoryentry.assetUUID);
            }
        }
        return false;
    }

    public boolean canDetachItem(WornItem wornitem)
    {
        return agentCircuit.getModules().rlvController.canDetachItem(wornitem.getAttachedTo(), wornitem.itemID());
    }

    public boolean canTakeItemOff(SLWearableType slwearabletype)
    {
        return agentCircuit.getModules().rlvController.canTakeItemOff(slwearabletype);
    }

    public boolean canTakeItemOff(SLInventoryEntry slinventoryentry)
    {
        slinventoryentry = ((SLInventoryEntry) (slinventoryentry.whatIsItemWornOn(wornAttachments, wornWearables, false)));
        if (slinventoryentry != null)
        {
            RLVController rlvcontroller = agentCircuit.getModules().rlvController;
            if (slinventoryentry instanceof SLWearableType)
            {
                return rlvcontroller.canTakeItemOff((SLWearableType)slinventoryentry);
            }
        }
        return true;
    }

    public boolean canWearItem(SLInventoryEntry slinventoryentry)
    {
        InventoryDB inventorydb = null;
        if (userManager != null)
        {
            inventorydb = userManager.getInventoryManager().getDatabase();
        }
        SLInventoryEntry slinventoryentry1 = slinventoryentry;
        if (inventorydb != null)
        {
            slinventoryentry1 = inventorydb.resolveLink(slinventoryentry);
        }
        if (slinventoryentry1 == null)
        {
            return false;
        }
        slinventoryentry = SLWearableType.getByCode(slinventoryentry1.flags & 0xff);
        return slinventoryentry == null || canWearItem(((SLWearableType) (slinventoryentry)));
    }

    public void finishBaking(BakeProcess bakeprocess, SLTextureEntry sltextureentry)
    {
        if (sltextureentry != null)
        {
            agentBakedTextures = sltextureentry;
            SendAvatarSetAppearance();
        }
        if (bakeProcess == bakeprocess)
        {
            bakeProcess = null;
        }
    }

    public UUID getAttachmentUUID(int i)
    {
        Object obj;
        if (parcelInfo == null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        obj = parcelInfo.getAgentAvatar();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        obj = ((SLObjectInfo) (obj)).treeNode.iterator();
        SLObjectInfo slobjectinfo;
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                break MISSING_BLOCK_LABEL_84;
            }
            slobjectinfo = (SLObjectInfo)((Iterator) (obj)).next();
        } while (slobjectinfo.attachedToUUID == null || !(slobjectinfo.isDead ^ true) || slobjectinfo.attachmentID != i);
        obj = slobjectinfo.getId();
        return ((UUID) (obj));
        NoSuchElementException nosuchelementexception;
        nosuchelementexception;
        Debug.Warning(nosuchelementexception);
        return null;
    }

    public boolean hasWornWearable(SLWearableType slwearabletype)
    {
        return wornWearables.containsRow(slwearabletype);
    }

    public boolean isItemWorn(SLInventoryEntry slinventoryentry)
    {
        return isItemWorn(slinventoryentry, false);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_SLAvatarAppearance_17963(int i, String s)
    {
        LLSDXMLRequest llsdxmlrequest;
        LLSDMap llsdmap;
        llsdxmlrequest = new LLSDXMLRequest();
        llsdmap = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("cof_version", new LLSDInt(i))
        });
        i = 3;
_L4:
        if (i <= 0)
        {
            break; /* Loop/switch isn't completed */
        }
        LLSDNode llsdnode;
        try
        {
            llsdnode = llsdxmlrequest.PerformRequest(s, llsdmap);
        }
        catch (Exception exception)
        {
            Debug.Printf("AvatarAppearance: server-side update error: [exception %s]", new Object[] {
                exception.toString()
            });
            lastCofUpdateError = true;
            try
            {
                Thread.sleep(1000L);
            }
            // Misplaced declaration of an exception variable
            catch (String s)
            {
                break; /* Loop/switch isn't completed */
            }
            i--;
            continue; /* Loop/switch isn't completed */
        }
        if (llsdnode == null)
        {
            break MISSING_BLOCK_LABEL_109;
        }
        if (llsdnode.keyExists("error"))
        {
            llsdnode = llsdnode.byKey("error");
            if (!llsdnode.isString())
            {
                break MISSING_BLOCK_LABEL_115;
            }
            Debug.Printf("AvatarAppearance: server-side error: %s", new Object[] {
                llsdnode.asString()
            });
        }
_L2:
        lastCofUpdateError = false;
        return;
        Debug.Printf("AvatarAppearance: server-side update ok.", new Object[0]);
        if (true) goto _L2; else goto _L1
_L1:
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void onWearableStatusChanged(SLWearable slwearable)
    {
        updateIfWearablesReady();
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
}
