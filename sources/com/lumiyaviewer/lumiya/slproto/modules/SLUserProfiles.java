// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.AsyncLimitsRequestHandler;
import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AgentDataUpdateRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarNotesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesReply;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesRequest;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPropertiesUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.PickDelete;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoReply;
import com.lumiyaviewer.lumiya.slproto.messages.PickInfoUpdate;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AgentGroupDataInfo;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.io.IOException;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLModule, SLModules, SLAvatarControl

public class SLUserProfiles extends SLModule
{

    public static final int AVATAR_AGEVERIFIED = 32;
    public static final int AVATAR_ALLOW_PUBLISH = 1;
    public static final int AVATAR_IDENTIFIED = 4;
    public static final int AVATAR_MATURE_PUBLISH = 2;
    public static final int AVATAR_ONLINE = 16;
    public static final int AVATAR_TRANSACTED = 8;
    private final RequestHandler agentDataUpdateRequestHandler;
    private ResultHandler agentDataUpdateResultHandler;
    private ResultHandler avatarGroupListsResultHandler;
    private final RequestHandler avatarNotesRequestHandler;
    private ResultHandler avatarNotesResultHandler;
    private final RequestHandler avatarPickInfosRequestHandler;
    private ResultHandler avatarPickInfosResultHandler;
    private final RequestHandler avatarPicksRequestHandler;
    private ResultHandler avatarPicksResultHandler;
    private final RequestHandler avatarPropertiesRequestHandler;
    private ResultHandler avatarPropertiesResultHandler;
    private boolean requestedNewGroupData;
    private final String setHomeLocationCap;
    private final UserManager userManager;

    static UserManager _2D_get0(SLUserProfiles sluserprofiles)
    {
        return sluserprofiles.userManager;
    }

    SLUserProfiles(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        super(slagentcircuit);
        requestedNewGroupData = false;
        avatarPropertiesRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLUserProfiles this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                Debug.Printf("AvatarGroupList: Requesting avatar properties for %s", new Object[] {
                    uuid.toString()
                });
                AvatarPropertiesRequest avatarpropertiesrequest = new AvatarPropertiesRequest();
                avatarpropertiesrequest.AgentData_Field.AgentID = circuitInfo.agentID;
                avatarpropertiesrequest.AgentData_Field.SessionID = circuitInfo.sessionID;
                avatarpropertiesrequest.AgentData_Field.AvatarID = uuid;
                avatarpropertiesrequest.isReliable = true;
                SendMessage(avatarpropertiesrequest);
                if (uuid.equals(circuitInfo.agentID))
                {
                    requestAgentDataUpdate();
                }
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        }, false, 3, 15000L);
        agentDataUpdateRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLUserProfiles this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                requestAgentDataUpdate();
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        }, false, 3, 15000L);
        avatarNotesRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLUserProfiles this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                agentCircuit.SendGenericMessage("avatarnotesrequest", new String[] {
                    uuid.toString()
                });
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        }, false, 3, 15000L);
        avatarPicksRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLUserProfiles this$0;

            public volatile void onRequest(Object obj)
            {
                onRequest((UUID)obj);
            }

            public void onRequest(UUID uuid)
            {
                agentCircuit.SendGenericMessage("avatarpicksrequest", new String[] {
                    uuid.toString()
                });
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        }, false, 3, 15000L);
        avatarPickInfosRequestHandler = new AsyncLimitsRequestHandler(agentCircuit, new SimpleRequestHandler() {

            final SLUserProfiles this$0;

            public void onRequest(AvatarPickKey avatarpickkey)
            {
                agentCircuit.SendGenericMessage("pickinforequest", new String[] {
                    avatarpickkey.avatarID.toString(), avatarpickkey.pickID.toString()
                });
            }

            public volatile void onRequest(Object obj)
            {
                onRequest((AvatarPickKey)obj);
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        }, false, 3, 15000L);
        userManager = UserManager.getUserManager(slagentcircuit.circuitInfo.agentID);
        setHomeLocationCap = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.HomeLocation);
    }

    public void DeletePick(UUID uuid)
    {
        PickDelete pickdelete = new PickDelete();
        pickdelete.AgentData_Field.AgentID = circuitInfo.agentID;
        pickdelete.AgentData_Field.SessionID = circuitInfo.sessionID;
        pickdelete.Data_Field.PickID = uuid;
        pickdelete.isReliable = true;
        pickdelete.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLUserProfiles this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                super.onMessageAcknowledged(slmessage);
                if (SLUserProfiles._2D_get0(SLUserProfiles.this) != null)
                {
                    SLUserProfiles._2D_get0(SLUserProfiles.this).getAvatarPicks().requestUpdate(SLUserProfiles._2D_get0(SLUserProfiles.this).getUserID());
                }
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        });
        SendMessage(pickdelete);
    }

    public void HandleAgentDataUpdate(AgentDataUpdate agentdataupdate)
    {
        if (agentDataUpdateResultHandler != null)
        {
            agentDataUpdateResultHandler.onResultData(agentdataupdate.AgentData_Field.AgentID, agentdataupdate);
        }
    }

    public void HandleAgentGroupDataUpdate(LLSDNode llsdnode)
    {
        try
        {
            llsdnode = (AgentGroupDataInfo)llsdnode.toObject(com/lumiyaviewer/lumiya/slproto/modules/groups/AgentGroupDataInfo);
            if (avatarGroupListsResultHandler != null)
            {
                llsdnode = new AvatarGroupList(llsdnode);
                avatarGroupListsResultHandler.onResultData(((AvatarGroupList) (llsdnode)).avatarID, llsdnode);
                if (!((AvatarGroupList) (llsdnode)).newGroupDataValid && requestedNewGroupData ^ true)
                {
                    requestedNewGroupData = true;
                    requestAgentDataUpdate();
                }
            }
            return;
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            Debug.Warning(llsdnode);
        }
    }

    public void HandleAgentGroupDataUpdate(AgentGroupDataUpdate agentgroupdataupdate)
    {
        if (avatarGroupListsResultHandler != null)
        {
            agentgroupdataupdate = new AvatarGroupList(agentgroupdataupdate);
            avatarGroupListsResultHandler.onResultData(((AvatarGroupList) (agentgroupdataupdate)).avatarID, agentgroupdataupdate);
        }
    }

    public void HandleAvatarGroupsReply(LLSDNode llsdnode)
    {
        try
        {
            llsdnode = (AgentGroupDataInfo)llsdnode.toObject(com/lumiyaviewer/lumiya/slproto/modules/groups/AgentGroupDataInfo);
            if (avatarGroupListsResultHandler != null)
            {
                llsdnode = new AvatarGroupList(llsdnode);
                if (!Objects.equal(((AvatarGroupList) (llsdnode)).avatarID, circuitInfo.agentID))
                {
                    avatarGroupListsResultHandler.onResultData(((AvatarGroupList) (llsdnode)).avatarID, llsdnode);
                }
            }
            return;
        }
        // Misplaced declaration of an exception variable
        catch (LLSDNode llsdnode)
        {
            llsdnode.printStackTrace();
        }
    }

    public void HandleAvatarGroupsReply(AvatarGroupsReply avatargroupsreply)
    {
        if (!Objects.equal(avatargroupsreply.AgentData_Field.AvatarID, circuitInfo.agentID) && avatarGroupListsResultHandler != null)
        {
            avatargroupsreply = new AvatarGroupList(avatargroupsreply);
            avatarGroupListsResultHandler.onResultData(((AvatarGroupList) (avatargroupsreply)).avatarID, avatargroupsreply);
        }
    }

    public void HandleAvatarNotesReply(AvatarNotesReply avatarnotesreply)
    {
        if (avatarNotesResultHandler != null)
        {
            avatarNotesResultHandler.onResultData(avatarnotesreply.Data_Field.TargetID, avatarnotesreply);
        }
    }

    public void HandleAvatarPicksReply(AvatarPicksReply avatarpicksreply)
    {
        if (avatarPicksResultHandler != null)
        {
            avatarPicksResultHandler.onResultData(avatarpicksreply.AgentData_Field.TargetID, avatarpicksreply);
        }
    }

    public void HandleAvatarPropertiesReply(AvatarPropertiesReply avatarpropertiesreply)
    {
        if (avatarPropertiesResultHandler != null)
        {
            avatarPropertiesResultHandler.onResultData(avatarpropertiesreply.AgentData_Field.AvatarID, avatarpropertiesreply);
        }
    }

    public void HandleCircuitReady()
    {
        if (userManager != null)
        {
            avatarPropertiesResultHandler = userManager.getAvatarProperties().getRequestSource().attachRequestHandler(avatarPropertiesRequestHandler);
            avatarNotesResultHandler = userManager.getAvatarNotes().getRequestSource().attachRequestHandler(avatarNotesRequestHandler);
            avatarPicksResultHandler = userManager.getAvatarPicks().getRequestSource().attachRequestHandler(avatarPicksRequestHandler);
            avatarPickInfosResultHandler = userManager.getAvatarPickInfos().getRequestSource().attachRequestHandler(avatarPickInfosRequestHandler);
            avatarGroupListsResultHandler = userManager.getAvatarGroupLists().getRequestSource().attachRequestHandler(avatarPropertiesRequestHandler);
            agentDataUpdateResultHandler = userManager.getAgentDataUpdates().getRequestSource().attachRequestHandler(agentDataUpdateRequestHandler);
        }
    }

    public void HandleCloseCircuit()
    {
        if (userManager != null)
        {
            userManager.getAvatarProperties().getRequestSource().detachRequestHandler(avatarPropertiesRequestHandler);
            userManager.getAvatarNotes().getRequestSource().detachRequestHandler(avatarNotesRequestHandler);
            userManager.getAvatarPicks().getRequestSource().detachRequestHandler(avatarPicksRequestHandler);
            userManager.getAvatarPickInfos().getRequestSource().detachRequestHandler(avatarPickInfosRequestHandler);
        }
    }

    public void HandlePickInfoReply(PickInfoReply pickinforeply)
    {
        if (avatarPickInfosResultHandler != null)
        {
            avatarPickInfosResultHandler.onResultData(new AvatarPickKey(pickinforeply.Data_Field.CreatorID, pickinforeply.Data_Field.PickID), pickinforeply);
        }
    }

    public void SaveUserNotes(UUID uuid, String s)
    {
        AvatarNotesUpdate avatarnotesupdate = new AvatarNotesUpdate();
        avatarnotesupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        avatarnotesupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        avatarnotesupdate.Data_Field.TargetID = uuid;
        avatarnotesupdate.Data_Field.Notes = SLMessage.stringToVariableUTF(s);
        avatarnotesupdate.isReliable = true;
        SendMessage(avatarnotesupdate);
        if (avatarNotesResultHandler != null)
        {
            AvatarNotesReply avatarnotesreply = new AvatarNotesReply();
            avatarnotesreply.AgentData_Field.AgentID = circuitInfo.agentID;
            avatarnotesreply.Data_Field.Notes = SLMessage.stringToVariableUTF(s);
            avatarnotesreply.Data_Field.TargetID = uuid;
            avatarNotesResultHandler.onResultData(uuid, avatarnotesreply);
        }
    }

    public boolean SetHomeLocation()
    {
        if (setHomeLocationCap == null)
        {
            return false;
        }
        Object obj = agentCircuit.getModules().avatarControl.getAgentPosition().getPosition();
        double d = ((double)agentCircuit.getModules().avatarControl.getAgentHeading() * 3.1415926535897931D) / 180D;
        Object obj1 = new LLVector3((float)Math.cos(d), (float)Math.sin(d), 0.0F);
        obj = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
            new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("HomeLocation", new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("LocationId", new LLSDInt(1)), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("LocationPos", ((LLVector3) (obj)).toLLSD()), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("LocationLookAt", ((LLVector3) (obj1)).toLLSD())
            }))
        });
        obj1 = new LLSDXMLRequest();
        boolean flag;
        try
        {
            obj = ((LLSDXMLRequest) (obj1)).PerformRequest(setHomeLocationCap, ((LLSDNode) (obj)));
        }
        catch (IOException ioexception)
        {
            Debug.Warning(ioexception);
            return false;
        }
        catch (LLSDException llsdexception)
        {
            Debug.Warning(llsdexception);
            return false;
        }
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_217;
        }
        Debug.Printf("SetHomeLocation: result %s", new Object[] {
            ((LLSDNode) (obj)).serializeToXML()
        });
        flag = ((LLSDNode) (obj)).byKey("success").asBoolean();
        return flag;
        return false;
    }

    public void UpdateAvatarProperties(UUID uuid, UUID uuid1, String s, String s1, boolean flag, boolean flag1, String s2)
    {
        AvatarPropertiesUpdate avatarpropertiesupdate = new AvatarPropertiesUpdate();
        avatarpropertiesupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        avatarpropertiesupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        avatarpropertiesupdate.PropertiesData_Field.ImageID = uuid;
        avatarpropertiesupdate.PropertiesData_Field.FLImageID = uuid1;
        avatarpropertiesupdate.PropertiesData_Field.AboutText = SLMessage.stringToVariableUTF(s);
        avatarpropertiesupdate.PropertiesData_Field.FLAboutText = SLMessage.stringToVariableOEM(s1);
        avatarpropertiesupdate.PropertiesData_Field.AllowPublish = flag;
        avatarpropertiesupdate.PropertiesData_Field.MaturePublish = flag1;
        avatarpropertiesupdate.PropertiesData_Field.ProfileURL = SLMessage.stringToVariableOEM(s2);
        avatarpropertiesupdate.isReliable = true;
        avatarpropertiesupdate.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLUserProfiles this$0;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                super.onMessageAcknowledged(slmessage);
                if (SLUserProfiles._2D_get0(SLUserProfiles.this) != null)
                {
                    SLUserProfiles._2D_get0(SLUserProfiles.this).getAvatarProperties().requestUpdate(SLUserProfiles._2D_get0(SLUserProfiles.this).getUserID());
                }
            }

            
            {
                this$0 = SLUserProfiles.this;
                super();
            }
        });
        SendMessage(avatarpropertiesupdate);
    }

    public void UpdatePickInfo(final UUID pickID, UUID uuid, UUID uuid1, String s, String s1, UUID uuid2, LLVector3d llvector3d, 
            int i, boolean flag)
    {
        PickInfoUpdate pickinfoupdate = new PickInfoUpdate();
        pickinfoupdate.AgentData_Field.AgentID = circuitInfo.agentID;
        pickinfoupdate.AgentData_Field.SessionID = circuitInfo.sessionID;
        pickinfoupdate.Data_Field.PickID = pickID;
        pickinfoupdate.Data_Field.CreatorID = uuid;
        pickinfoupdate.Data_Field.TopPick = false;
        pickinfoupdate.Data_Field.ParcelID = uuid1;
        pickinfoupdate.Data_Field.Name = SLMessage.stringToVariableOEM(s);
        pickinfoupdate.Data_Field.Desc = SLMessage.stringToVariableUTF(s1);
        pickinfoupdate.Data_Field.SnapshotID = uuid2;
        pickinfoupdate.Data_Field.PosGlobal = llvector3d;
        pickinfoupdate.Data_Field.SortOrder = i;
        pickinfoupdate.Data_Field.Enabled = flag;
        pickinfoupdate.isReliable = true;
        pickinfoupdate.setEventListener(new com.lumiyaviewer.lumiya.slproto.SLMessageEventListener.SLMessageBaseEventListener() {

            final SLUserProfiles this$0;
            final UUID val$pickID;

            public void onMessageAcknowledged(SLMessage slmessage)
            {
                super.onMessageAcknowledged(slmessage);
                if (SLUserProfiles._2D_get0(SLUserProfiles.this) != null)
                {
                    SLUserProfiles._2D_get0(SLUserProfiles.this).getAvatarPickInfos().requestUpdate(new AvatarPickKey(SLUserProfiles._2D_get0(SLUserProfiles.this).getUserID(), pickID));
                    SLUserProfiles._2D_get0(SLUserProfiles.this).getAvatarPicks().requestUpdate(SLUserProfiles._2D_get0(SLUserProfiles.this).getUserID());
                }
            }

            
            {
                this$0 = SLUserProfiles.this;
                pickID = uuid;
                super();
            }
        });
        SendMessage(pickinfoupdate);
    }

    public void requestAgentDataUpdate()
    {
        AgentDataUpdateRequest agentdataupdaterequest = new AgentDataUpdateRequest();
        agentdataupdaterequest.AgentData_Field.AgentID = circuitInfo.agentID;
        agentdataupdaterequest.AgentData_Field.SessionID = circuitInfo.sessionID;
        agentdataupdaterequest.isReliable = true;
        SendMessage(agentdataupdaterequest);
    }
}
