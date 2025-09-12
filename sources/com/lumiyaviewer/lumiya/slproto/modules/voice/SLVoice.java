// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.voice;

import android.content.Context;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceConnectChannel;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLoginStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSet3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DVector;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class SLVoice extends SLModule
{

    private static final int INVALID_PARCEL_ID = -1;
    private static final int LOGIN_DELAY = 5;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private final String capURL;
    private final String chatSessionRequestURL;
    private volatile VoiceChannelInfo connectedVoiceChannel;
    private int currentParcelID;
    private VoiceChannelInfo currentParcelVoiceChannel;
    private int loginAttempts;
    private final String parcelVoiceCapURL;
    private final Object parcelVoiceChannelLock = new Object();
    private final Set requestedGroupChats = Collections.synchronizedSet(new HashSet());
    private int requestedParcelID;
    private volatile boolean shutdown;
    private final UserManager userManager;
    private boolean voiceCredentialsRequested;
    private volatile boolean voiceEnabled;
    private volatile boolean voiceLoggedIn;
    private final SubscriptionData voiceLoggedInSubscription = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY(this));
    private volatile VoiceLoginInfo voiceLoginInfo;
    private volatile VoicePluginServiceConnection voicePluginServiceConnection;

    static SLAgentCircuit _2D_get0(SLVoice slvoice)
    {
        return slvoice.agentCircuit;
    }

    static UserManager _2D_get1(SLVoice slvoice)
    {
        return slvoice.userManager;
    }

    static boolean _2D_get2(SLVoice slvoice)
    {
        return slvoice.voiceEnabled;
    }

    static boolean _2D_get3(SLVoice slvoice)
    {
        return slvoice.voiceLoggedIn;
    }

    static VoicePluginServiceConnection _2D_get4(SLVoice slvoice)
    {
        return slvoice.voicePluginServiceConnection;
    }

    public SLVoice(SLAgentCircuit slagentcircuit, SLCaps slcaps)
    {
        super(slagentcircuit);
        voiceLoggedIn = false;
        voiceEnabled = false;
        voiceCredentialsRequested = false;
        voicePluginServiceConnection = null;
        connectedVoiceChannel = null;
        shutdown = false;
        loginAttempts = 0;
        requestedParcelID = -1;
        currentParcelID = -1;
        currentParcelVoiceChannel = null;
        voiceLoginInfo = null;
        userManager = UserManager.getUserManager(agentCircuit.getAgentUUID());
        capURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.ProvisionVoiceAccountRequest);
        parcelVoiceCapURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.ParcelVoiceInfoRequest);
        chatSessionRequestURL = slcaps.getCapability(com.lumiyaviewer.lumiya.slproto.caps.SLCaps.SLCapability.ChatSessionRequest);
        if (userManager != null)
        {
            voiceLoggedInSubscription.subscribe(userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
        }
        if (capURL != null)
        {
            Debug.Printf("Voice cap: '%s'", new Object[] {
                capURL
            });
        } else
        {
            Debug.Printf("Voice cap not supported", new Object[0]);
        }
        EventBus.getInstance().subscribe(this);
        updateVoiceEnabledStatus();
    }

    private void onParcelVoiceInfoResult(LLSDNode llsdnode)
    {
        if (llsdnode == null)
        {
            break MISSING_BLOCK_LABEL_20;
        }
        Debug.Printf("SLVoice: parcel voice info '%s'", new Object[] {
            llsdnode.serializeToXML()
        });
        return;
        llsdnode;
        Debug.Warning(llsdnode);
        return;
    }

    private void onProvisionVoiceAccountResult(LLSDNode llsdnode)
    {
        if (llsdnode == null)
        {
            break MISSING_BLOCK_LABEL_85;
        }
        Debug.Printf("SLVoice: result '%s'", new Object[] {
            llsdnode.serializeToXML()
        });
        voiceLoginInfo = new VoiceLoginInfo(llsdnode.byKey("voice_sip_uri_hostname").asString(), llsdnode.byKey("voice_account_server_name").asString(), agentCircuit.getAgentUUID(), llsdnode.byKey("username").asString(), llsdnode.byKey("password").asString());
        updateVoiceEnabledStatus();
_L1:
        return;
        llsdnode;
        Debug.Warning(llsdnode);
        return;
        Debug.Printf("SLVoice: null result", new Object[0]);
        if (!shutdown && loginAttempts < 3 && voiceEnabled)
        {
            loginAttempts = loginAttempts + 1;
            try
            {
                Thread.sleep(5000L);
                if (!shutdown && voiceEnabled)
                {
                    new LLSDXMLAsyncRequest(capURL, new LLSDUndefined(), new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls2(this));
                    return;
                }
            }
            // Misplaced declaration of an exception variable
            catch (LLSDNode llsdnode)
            {
                Debug.Warning(llsdnode);
                return;
            }
        } else
        {
            Debug.Printf("SLVoice: giving up", new Object[0]);
            return;
        }
          goto _L1
    }

    private void onVoiceLoginStatusChanged(Boolean boolean1)
    {
        boolean flag;
        if (boolean1 != null)
        {
            flag = boolean1.booleanValue();
        } else
        {
            flag = false;
        }
        voiceLoggedIn = flag;
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_2D_mthref_2D_0(Boolean boolean1)
    {
        onVoiceLoginStatusChanged(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_2D_mthref_2D_1(LLSDNode llsdnode)
    {
        onProvisionVoiceAccountResult(llsdnode);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_2D_mthref_2D_2(LLSDNode llsdnode)
    {
        onProvisionVoiceAccountResult(llsdnode);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_2D_mthref_2D_3(LLSDNode llsdnode)
    {
        onParcelVoiceInfoResult(llsdnode);
    }

    public void HandleCloseCircuit()
    {
        shutdown = true;
        voiceLoggedInSubscription.unsubscribe();
        super.HandleCloseCircuit();
    }

    public VoiceChannelInfo getCurrentParcelVoiceChannel()
    {
        Object obj = parcelVoiceChannelLock;
        obj;
        JVM INSTR monitorenter ;
        VoiceChannelInfo voicechannelinfo = currentParcelVoiceChannel;
        obj;
        JVM INSTR monitorexit ;
        return voicechannelinfo;
        Exception exception;
        exception;
        throw exception;
    }

    public VoiceLoginInfo getVoiceLoginInfo()
    {
        return voiceLoginInfo;
    }

    public boolean groupVoiceChatRequest(UUID uuid)
    {
        if (voiceEnabled && voiceLoggedIn && chatSessionRequestURL != null)
        {
            requestedGroupChats.add(uuid);
            agentCircuit.StartGroupSessionForVoice(uuid);
            return true;
        } else
        {
            return false;
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_12525(int i, LLSDNode llsdnode)
    {
        currentParcelID = i;
        if (llsdnode == null) goto _L2; else goto _L1
_L1:
        Object obj = parcelVoiceChannelLock;
        obj;
        JVM INSTR monitorenter ;
        llsdnode = new VoiceChannelInfo(llsdnode.byKey("voice_credentials").byKey("channel_uri").asString(), true, true);
_L3:
        if (Objects.equal(currentParcelVoiceChannel, llsdnode))
        {
            break MISSING_BLOCK_LABEL_137;
        }
        currentParcelVoiceChannel = llsdnode;
        i = 1;
_L5:
        obj;
        JVM INSTR monitorexit ;
_L4:
        if (i != 0)
        {
            agentCircuit.getModules().minimap.requestUpdateAvatarParcelData();
        }
        return;
        llsdnode;
        Debug.Printf("Voice: error retrieving parcel voice info for %d (%s)", new Object[] {
            Integer.valueOf(i), llsdnode.getMessage()
        });
        llsdnode = null;
          goto _L3
        llsdnode;
        throw llsdnode;
_L2:
        Debug.Printf("Voice: error retrieving parcel voice info for %d", new Object[] {
            Integer.valueOf(i)
        });
        i = 0;
          goto _L4
        i = 0;
          goto _L5
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14030(VoiceLoginStatus voiceloginstatus, VoicePluginServiceConnection voicepluginserviceconnection)
    {
        if (voiceloginstatus.loggedIn)
        {
            voicePluginServiceConnection = voicepluginserviceconnection;
            return;
        } else
        {
            voicePluginServiceConnection = null;
            connectedVoiceChannel = null;
            return;
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14408(VoiceChannelStatus voicechannelstatus)
    {
        if (voicechannelstatus.errorMessage == null) goto _L2; else goto _L1
_L1:
        if (connectedVoiceChannel != null && Objects.equal(connectedVoiceChannel.voiceChannelURI, voicechannelstatus.channelInfo.voiceChannelURI))
        {
            connectedVoiceChannel = null;
        }
_L4:
        return;
_L2:
        if (voicechannelstatus.chatInfo.state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
        {
            continue; /* Loop/switch isn't completed */
        }
        if (connectedVoiceChannel != null && Objects.equal(connectedVoiceChannel.voiceChannelURI, voicechannelstatus.channelInfo.voiceChannelURI))
        {
            connectedVoiceChannel = null;
            return;
        }
        continue; /* Loop/switch isn't completed */
        if (voicechannelstatus.chatInfo.state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active) goto _L4; else goto _L3
_L3:
        connectedVoiceChannel = voicechannelstatus.channelInfo;
        if (voiceLoggedIn && voicechannelstatus.channelInfo.isSpatial)
        {
            updateSpatialVoicePosition();
            return;
        }
        if (true) goto _L4; else goto _L5
_L5:
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_4388()
    {
        voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
        if (!voiceEnabled) goto _L2; else goto _L1
_L1:
        if (voiceCredentialsRequested) goto _L4; else goto _L3
_L3:
        voiceCredentialsRequested = true;
        if (capURL != null)
        {
            new LLSDXMLAsyncRequest(capURL, new LLSDUndefined(), new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls1(this));
        }
_L6:
        return;
_L4:
        if (voiceLoginInfo != null && voiceLoggedIn ^ true)
        {
            GridConnectionService gridconnectionservice = GridConnectionService.getServiceInstance();
            if (gridconnectionservice != null)
            {
                gridconnectionservice.startVoice(voiceLoginInfo, UserManager.getUserManager(agentCircuit.getAgentUUID()));
                return;
            }
        }
        continue; /* Loop/switch isn't completed */
_L2:
        GridConnectionService gridconnectionservice1 = GridConnectionService.getServiceInstance();
        if (gridconnectionservice1 != null)
        {
            gridconnectionservice1.stopVoice();
            return;
        }
        if (true) goto _L6; else goto _L5
_L5:
    }

    public void nearbyVoiceChatRequest(VoiceChannelInfo voicechannelinfo)
    {
        VoicePluginServiceConnection voicepluginserviceconnection = voicePluginServiceConnection;
        if (voiceEnabled && voiceLoggedIn && voicepluginserviceconnection != null)
        {
            voicepluginserviceconnection.addChannel(ChatterID.getLocalChatterID(userManager.getUserID()), voicechannelinfo);
            voicepluginserviceconnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voicechannelinfo, null));
        }
    }

    public void onGlobalOptionsChanged(com.lumiyaviewer.lumiya.GlobalOptions.GlobalOptionsChangedEvent globaloptionschangedevent)
    {
        updateVoiceEnabledStatus();
    }

    public void onGroupSessionReady(final UUID groupID)
    {
        if (requestedGroupChats.remove(groupID) && chatSessionRequestURL != null)
        {
            LLSDMap llsdmap = new LLSDMap(new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry[] {
                new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("method", new LLSDString("call")), new com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap.LLSDMapEntry("session-id", new LLSDUUID(groupID))
            });
            new LLSDXMLAsyncRequest(chatSessionRequestURL, llsdmap, new com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener() {

                final SLVoice this$0;
                final UUID val$groupID;

                public void onLLSDXMLResult(LLSDNode llsdnode)
                {
                    ChatterID chatterid = ChatterID.getGroupChatterID(SLVoice._2D_get1(SLVoice.this).getUserID(), groupID);
                    if (llsdnode != null) goto _L2; else goto _L1
_L1:
                    try
                    {
                        throw new LLSDException("Null result");
                    }
                    // Misplaced declaration of an exception variable
                    catch (LLSDNode llsdnode)
                    {
                        SLVoice._2D_get0(SLVoice.this).HandleChatEvent(chatterid, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), SLVoice._2D_get1(SLVoice.this).getUserID(), LumiyaApp.getContext().getString(0x7f09011a)), false);
                    }
                    Debug.Warning(llsdnode);
_L4:
                    return;
_L2:
                    Object obj;
                    VoicePluginServiceConnection voicepluginserviceconnection;
                    obj = llsdnode.byKey("voice_credentials").byKey("channel_uri").asString();
                    llsdnode = llsdnode.byKey("voice_credentials").byKey("channel_credentials").asString();
                    voicepluginserviceconnection = SLVoice._2D_get4(SLVoice.this);
                    if (!SLVoice._2D_get2(SLVoice.this) || !SLVoice._2D_get3(SLVoice.this) || voicepluginserviceconnection == null) goto _L4; else goto _L3
_L3:
                    obj = new VoiceChannelInfo(((String) (obj)), false, true);
                    voicepluginserviceconnection.addChannel(chatterid, ((VoiceChannelInfo) (obj)));
                    voicepluginserviceconnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(((VoiceChannelInfo) (obj)), llsdnode));
                    return;
                }

            
            {
                this$0 = SLVoice.this;
                groupID = uuid;
                super();
            }
            });
        }
    }

    public void onVoiceChannelStatus(VoiceChannelStatus voicechannelstatus)
    {
        agentCircuit.execute(new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls5(this, voicechannelstatus));
    }

    public void onVoiceLoginStatus(VoicePluginServiceConnection voicepluginserviceconnection, VoiceLoginStatus voiceloginstatus)
    {
        agentCircuit.execute(new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls6(this, voiceloginstatus, voicepluginserviceconnection));
    }

    public boolean requestParcelVoiceInfo()
    {
        if (parcelVoiceCapURL != null)
        {
            new LLSDXMLAsyncRequest(parcelVoiceCapURL, new LLSDUndefined(), new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls3(this));
            return true;
        } else
        {
            return false;
        }
    }

    public void setCurrentParcel(int i)
    {
        boolean flag1 = false;
        Object obj = parcelVoiceChannelLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = flag1;
        if (parcelVoiceCapURL == null)
        {
            break MISSING_BLOCK_LABEL_52;
        }
        flag = flag1;
        if (capURL == null)
        {
            break MISSING_BLOCK_LABEL_52;
        }
        flag = flag1;
        if (requestedParcelID == i)
        {
            break MISSING_BLOCK_LABEL_52;
        }
        requestedParcelID = i;
        flag = true;
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            new LLSDXMLAsyncRequest(parcelVoiceCapURL, new LLSDUndefined(), new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls7(i, this));
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void updateSpatialVoicePosition()
    {
        VoicePluginServiceConnection voicepluginserviceconnection = voicePluginServiceConnection;
        VoiceChannelInfo voicechannelinfo = connectedVoiceChannel;
        if (voicepluginserviceconnection != null && voicechannelinfo != null && voicechannelinfo.isSpatial)
        {
            Object obj = agentCircuit.getAgentGlobalPosition();
            Object obj1 = agentCircuit.getModules();
            if (obj != null && obj1 != null)
            {
                float f1 = ((SLModules) (obj1)).avatarControl.getAgentHeading() * 0.01745329F;
                float f = (float)Math.cos(f1);
                f1 = (float)Math.sin(f1);
                obj1 = Voice3DVector.fromLLCoords(f, f1, 0.0F);
                Voice3DVector voice3dvector = Voice3DVector.fromLLCoords(-f1, f, 0.0F);
                Voice3DVector voice3dvector1 = Voice3DVector.fromLLCoords(0.0F, 0.0F, 1.0F);
                obj = new Voice3DPosition(Voice3DVector.fromLLCoords((float)((LLVector3d) (obj)).x, (float)((LLVector3d) (obj)).y, (float)((LLVector3d) (obj)).z), new Voice3DVector(0.0F, 0.0F, 0.0F), ((Voice3DVector) (obj1)), voice3dvector1, voice3dvector);
                voicepluginserviceconnection.sendMessage(VoicePluginMessageType.VoiceSet3DPosition, new VoiceSet3DPosition(voicechannelinfo, ((Voice3DPosition) (obj)), ((Voice3DPosition) (obj))));
            }
        }
    }

    public void updateVoiceEnabledStatus()
    {
        UIThreadExecutor.getInstance().execute(new _2D_.Lambda.RETWaU3Ta92aG3GeBeXvI3Y9viY._cls4(this));
    }

    public boolean userVoiceChatRequest(UUID uuid)
    {
        VoicePluginServiceConnection voicepluginserviceconnection = voicePluginServiceConnection;
        Object obj = voiceLoginInfo;
        if (voiceEnabled && voiceLoggedIn && uuid != null && voicepluginserviceconnection != null && obj != null && userManager != null)
        {
            obj = new VoiceChannelInfo(uuid, ((VoiceLoginInfo) (obj)).voiceSipUriHostname);
            voicepluginserviceconnection.addChannel(ChatterID.getUserChatterID(userManager.getUserID(), uuid), ((VoiceChannelInfo) (obj)));
            voicepluginserviceconnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(((VoiceChannelInfo) (obj)), null));
            return true;
        } else
        {
            return false;
        }
    }
}
