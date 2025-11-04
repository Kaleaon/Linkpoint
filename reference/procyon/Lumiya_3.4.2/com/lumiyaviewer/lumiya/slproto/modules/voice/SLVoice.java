// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.voice;

import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3d;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSet3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DVector;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceConnectChannel;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLoginStatus;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDException;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest;
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import java.util.Collections;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.caps.SLCaps;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import java.util.Set;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

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
    private final Object parcelVoiceChannelLock;
    private final Set<UUID> requestedGroupChats;
    private int requestedParcelID;
    private volatile boolean shutdown;
    private final UserManager userManager;
    private boolean voiceCredentialsRequested;
    private volatile boolean voiceEnabled;
    private volatile boolean voiceLoggedIn;
    private final SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedInSubscription;
    @Nullable
    private volatile VoiceLoginInfo voiceLoginInfo;
    @Nullable
    private volatile VoicePluginServiceConnection voicePluginServiceConnection;
    
    public SLVoice(final SLAgentCircuit slAgentCircuit, final SLCaps slCaps) {
        super(slAgentCircuit);
        this.requestedGroupChats = Collections.synchronizedSet(new HashSet<UUID>());
        this.voiceLoggedInSubscription = new SubscriptionData<SubscriptionSingleKey, Boolean>(UIThreadExecutor.getInstance(), new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY(this));
        this.voiceLoggedIn = false;
        this.voiceEnabled = false;
        this.voiceCredentialsRequested = false;
        this.voicePluginServiceConnection = null;
        this.connectedVoiceChannel = null;
        this.shutdown = false;
        this.loginAttempts = 0;
        this.parcelVoiceChannelLock = new Object();
        this.requestedParcelID = -1;
        this.currentParcelID = -1;
        this.currentParcelVoiceChannel = null;
        this.voiceLoginInfo = null;
        this.userManager = UserManager.getUserManager(this.agentCircuit.getAgentUUID());
        this.capURL = slCaps.getCapability(SLCaps.SLCapability.ProvisionVoiceAccountRequest);
        this.parcelVoiceCapURL = slCaps.getCapability(SLCaps.SLCapability.ParcelVoiceInfoRequest);
        this.chatSessionRequestURL = slCaps.getCapability(SLCaps.SLCapability.ChatSessionRequest);
        if (this.userManager != null) {
            this.voiceLoggedInSubscription.subscribe(this.userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
        }
        if (this.capURL != null) {
            Debug.Printf("Voice cap: '%s'", this.capURL);
        }
        else {
            Debug.Printf("Voice cap not supported", new Object[0]);
        }
        EventBus.getInstance().subscribe(this);
        this.updateVoiceEnabledStatus();
    }
    
    private void onParcelVoiceInfoResult(final LLSDNode llsdNode) {
        if (llsdNode == null) {
            return;
        }
        try {
            Debug.Printf("SLVoice: parcel voice info '%s'", llsdNode.serializeToXML());
        }
        catch (final Exception ex) {
            Debug.Warning(ex);
        }
    }
    
    private void onProvisionVoiceAccountResult(final LLSDNode llsdNode) {
        Label_0089: {
            if (llsdNode == null) {
                break Label_0089;
            }
            try {
                Debug.Printf("SLVoice: result '%s'", llsdNode.serializeToXML());
                this.voiceLoginInfo = new VoiceLoginInfo(llsdNode.byKey("voice_sip_uri_hostname").asString(), llsdNode.byKey("voice_account_server_name").asString(), this.agentCircuit.getAgentUUID(), llsdNode.byKey("username").asString(), llsdNode.byKey("password").asString());
                this.updateVoiceEnabledStatus();
                return;
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
                return;
            }
        }
        Debug.Printf("SLVoice: null result", new Object[0]);
        if (!this.shutdown && this.loginAttempts < 3 && this.voiceEnabled) {
            ++this.loginAttempts;
            try {
                Thread.sleep(5000L);
                if (!this.shutdown && this.voiceEnabled) {
                    final LLSDXMLAsyncRequest llsdxmlAsyncRequest = new LLSDXMLAsyncRequest(this.capURL, new LLSDUndefined(), (LLSDXMLAsyncRequest.LLSDXMLResultListener)new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$2(this));
                }
            }
            catch (final InterruptedException ex2) {
                Debug.Warning(ex2);
            }
            return;
        }
        Debug.Printf("SLVoice: giving up", new Object[0]);
    }
    
    private void onVoiceLoginStatusChanged(final Boolean b) {
        this.voiceLoggedIn = (b != null && b);
    }
    
    @Override
    public void HandleCloseCircuit() {
        this.shutdown = true;
        this.voiceLoggedInSubscription.unsubscribe();
        super.HandleCloseCircuit();
    }
    
    public VoiceChannelInfo getCurrentParcelVoiceChannel() {
        synchronized (this.parcelVoiceChannelLock) {
            return this.currentParcelVoiceChannel;
        }
    }
    
    @Nullable
    public VoiceLoginInfo getVoiceLoginInfo() {
        return this.voiceLoginInfo;
    }
    
    public boolean groupVoiceChatRequest(final UUID uuid) {
        if (this.voiceEnabled && this.voiceLoggedIn && this.chatSessionRequestURL != null) {
            this.requestedGroupChats.add(uuid);
            this.agentCircuit.StartGroupSessionForVoice(uuid);
            return true;
        }
        return false;
    }
    
    public void nearbyVoiceChatRequest(final VoiceChannelInfo voiceChannelInfo) {
        final VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        if (this.voiceEnabled && this.voiceLoggedIn && voicePluginServiceConnection != null) {
            voicePluginServiceConnection.addChannel(ChatterID.getLocalChatterID(this.userManager.getUserID()), voiceChannelInfo);
            voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, null));
        }
    }
    
    @EventHandler
    public void onGlobalOptionsChanged(final GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        this.updateVoiceEnabledStatus();
    }
    
    public void onGroupSessionReady(final UUID uuid) {
        if (this.requestedGroupChats.remove(uuid) && this.chatSessionRequestURL != null) {
            new LLSDXMLAsyncRequest(this.chatSessionRequestURL, new LLSDMap(new LLSDMap.LLSDMapEntry[] { new LLSDMap.LLSDMapEntry("method", new LLSDString("call")), new LLSDMap.LLSDMapEntry("session-id", new LLSDUUID(uuid)) }), (LLSDXMLAsyncRequest.LLSDXMLResultListener)new LLSDXMLAsyncRequest.LLSDXMLResultListener() {
                @Override
                public void onLLSDXMLResult(final LLSDNode llsdNode) {
                    final ChatterID groupChatterID = ChatterID.getGroupChatterID(SLVoice.this.userManager.getUserID(), uuid);
                    if (llsdNode == null) {
                        try {
                            throw new LLSDException("Null result");
                        }
                        catch (final LLSDException ex) {
                            SLVoice.this.agentCircuit.HandleChatEvent(groupChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), SLVoice.this.userManager.getUserID(), LumiyaApp.getContext().getString(2131296538)), false);
                            Debug.Warning(ex);
                        }
                    }
                    else {
                        final String string = llsdNode.byKey("voice_credentials").byKey("channel_uri").asString();
                        final String string2 = llsdNode.byKey("voice_credentials").byKey("channel_credentials").asString();
                        final VoicePluginServiceConnection -get4 = SLVoice.this.voicePluginServiceConnection;
                        if (SLVoice.this.voiceEnabled && SLVoice.this.voiceLoggedIn && -get4 != null) {
                            final VoiceChannelInfo voiceChannelInfo = new VoiceChannelInfo(string, false, true);
                            -get4.addChannel(groupChatterID, voiceChannelInfo);
                            -get4.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, string2));
                        }
                    }
                }
            });
        }
    }
    
    public void onVoiceChannelStatus(final VoiceChannelStatus voiceChannelStatus) {
        this.agentCircuit.execute(new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$5(this, voiceChannelStatus));
    }
    
    public void onVoiceLoginStatus(final VoicePluginServiceConnection voicePluginServiceConnection, final VoiceLoginStatus voiceLoginStatus) {
        this.agentCircuit.execute(new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$6(this, voiceLoginStatus, voicePluginServiceConnection));
    }
    
    public boolean requestParcelVoiceInfo() {
        if (this.parcelVoiceCapURL != null) {
            new LLSDXMLAsyncRequest(this.parcelVoiceCapURL, new LLSDUndefined(), (LLSDXMLAsyncRequest.LLSDXMLResultListener)new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$3(this));
            return true;
        }
        return false;
    }
    
    public void setCurrentParcel(final int requestedParcelID) {
        final boolean b = false;
        final Object parcelVoiceChannelLock = this.parcelVoiceChannelLock;
        monitorenter(parcelVoiceChannelLock);
        int n = b ? 1 : 0;
        try {
            if (this.parcelVoiceCapURL != null) {
                n = (b ? 1 : 0);
                if (this.capURL != null) {
                    n = (b ? 1 : 0);
                    if (this.requestedParcelID != requestedParcelID) {
                        this.requestedParcelID = requestedParcelID;
                        n = 1;
                    }
                }
            }
            monitorexit(parcelVoiceChannelLock);
            if (n != 0) {
                final LLSDXMLAsyncRequest llsdxmlAsyncRequest = new LLSDXMLAsyncRequest(this.parcelVoiceCapURL, new LLSDUndefined(), (LLSDXMLAsyncRequest.LLSDXMLResultListener)new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$7(requestedParcelID, this));
            }
        }
        finally {
            monitorexit(parcelVoiceChannelLock);
        }
    }
    
    public void updateSpatialVoicePosition() {
        final VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        final VoiceChannelInfo connectedVoiceChannel = this.connectedVoiceChannel;
        if (voicePluginServiceConnection != null && connectedVoiceChannel != null && connectedVoiceChannel.isSpatial) {
            final LLVector3d agentGlobalPosition = this.agentCircuit.getAgentGlobalPosition();
            final SLModules modules = this.agentCircuit.getModules();
            if (agentGlobalPosition != null && modules != null) {
                final float n = modules.avatarControl.getAgentHeading() * 0.017453292f;
                final float n2 = (float)Math.cos(n);
                final float n3 = (float)Math.sin(n);
                final Voice3DPosition voice3DPosition = new Voice3DPosition(Voice3DVector.fromLLCoords((float)agentGlobalPosition.x, (float)agentGlobalPosition.y, (float)agentGlobalPosition.z), new Voice3DVector(0.0f, 0.0f, 0.0f), Voice3DVector.fromLLCoords(n2, n3, 0.0f), Voice3DVector.fromLLCoords(0.0f, 0.0f, 1.0f), Voice3DVector.fromLLCoords(-n3, n2, 0.0f));
                voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceSet3DPosition, new VoiceSet3DPosition(connectedVoiceChannel, voice3DPosition, voice3DPosition));
            }
        }
    }
    
    public void updateVoiceEnabledStatus() {
        UIThreadExecutor.getInstance().execute(new _$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY$4(this));
    }
    
    public boolean userVoiceChatRequest(final UUID uuid) {
        final VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        final VoiceLoginInfo voiceLoginInfo = this.voiceLoginInfo;
        if (this.voiceEnabled && this.voiceLoggedIn && uuid != null && voicePluginServiceConnection != null && voiceLoginInfo != null && this.userManager != null) {
            final VoiceChannelInfo voiceChannelInfo = new VoiceChannelInfo(uuid, voiceLoginInfo.voiceSipUriHostname);
            voicePluginServiceConnection.addChannel(ChatterID.getUserChatterID(this.userManager.getUserID(), uuid), voiceChannelInfo);
            voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, null));
            return true;
        }
        return false;
    }
}
