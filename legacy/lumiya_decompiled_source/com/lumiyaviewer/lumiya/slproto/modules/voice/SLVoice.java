package com.lumiyaviewer.lumiya.slproto.modules.voice;

import android.support.v4.app.NotificationCompat;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.react.Subscription;
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
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class SLVoice extends SLModule {
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

    public SLVoice(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit);
        this.requestedGroupChats = Collections.synchronizedSet(new HashSet());
        this.voiceLoggedInSubscription = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY
            private final /* synthetic */ void $m$0(Object obj) {
                ((SLVoice) this).m261com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref0((Boolean) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
            public final void onData(Object obj) {
                $m$0(obj);
            }
        });
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
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.ProvisionVoiceAccountRequest);
        this.parcelVoiceCapURL = sLCaps.getCapability(SLCaps.SLCapability.ParcelVoiceInfoRequest);
        this.chatSessionRequestURL = sLCaps.getCapability(SLCaps.SLCapability.ChatSessionRequest);
        if (this.userManager != null) {
            this.voiceLoggedInSubscription.subscribe(this.userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value);
        }
        if (this.capURL != null) {
            Debug.Printf("Voice cap: '%s'", this.capURL);
        } else {
            Debug.Printf("Voice cap not supported", new Object[0]);
        }
        EventBus.getInstance().subscribe(this);
        updateVoiceEnabledStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onParcelVoiceInfoResult */
    public void m264com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref3(LLSDNode lLSDNode) {
        if (lLSDNode != null) {
            try {
                Debug.Printf("SLVoice: parcel voice info '%s'", lLSDNode.serializeToXML());
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onProvisionVoiceAccountResult */
    public void m263com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref2(LLSDNode lLSDNode) {
        if (lLSDNode != null) {
            try {
                Debug.Printf("SLVoice: result '%s'", lLSDNode.serializeToXML());
                this.voiceLoginInfo = new VoiceLoginInfo(lLSDNode.byKey("voice_sip_uri_hostname").asString(), lLSDNode.byKey("voice_account_server_name").asString(), this.agentCircuit.getAgentUUID(), lLSDNode.byKey("username").asString(), lLSDNode.byKey("password").asString());
                updateVoiceEnabledStatus();
                return;
            } catch (Exception e) {
                Debug.Warning(e);
                return;
            }
        }
        Debug.Printf("SLVoice: null result", new Object[0]);
        if (this.shutdown || this.loginAttempts >= 3 || !this.voiceEnabled) {
            Debug.Printf("SLVoice: giving up", new Object[0]);
            return;
        }
        this.loginAttempts++;
        try {
            Thread.sleep(5000L);
            if (this.shutdown || !this.voiceEnabled) {
                return;
            }
            new LLSDXMLAsyncRequest(this.capURL, new LLSDUndefined(), new LLSDXMLAsyncRequest.LLSDXMLResultListener() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.2
                private final /* synthetic */ void $m$0(LLSDNode lLSDNode2) {
                    ((SLVoice) this).m263com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref2(lLSDNode2);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener
                public final void onLLSDXMLResult(LLSDNode lLSDNode2) {
                    $m$0(lLSDNode2);
                }
            });
        } catch (InterruptedException e2) {
            Debug.Warning(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onVoiceLoginStatusChanged */
    public void m261com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref0(Boolean bool) {
        this.voiceLoggedIn = bool != null ? bool.booleanValue() : false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.modules.SLModule
    public void HandleCloseCircuit() {
        this.shutdown = true;
        this.voiceLoggedInSubscription.unsubscribe();
        super.HandleCloseCircuit();
    }

    public VoiceChannelInfo getCurrentParcelVoiceChannel() {
        VoiceChannelInfo voiceChannelInfo;
        synchronized (this.parcelVoiceChannelLock) {
            voiceChannelInfo = this.currentParcelVoiceChannel;
        }
        return voiceChannelInfo;
    }

    @Nullable
    public VoiceLoginInfo getVoiceLoginInfo() {
        return this.voiceLoginInfo;
    }

    public boolean groupVoiceChatRequest(UUID uuid) {
        if (this.voiceEnabled && this.voiceLoggedIn && this.chatSessionRequestURL != null) {
            this.requestedGroupChats.add(uuid);
            this.agentCircuit.StartGroupSessionForVoice(uuid);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_12525  reason: not valid java name */
    public /* synthetic */ void m265x4b46af5c(int i, LLSDNode lLSDNode) {
        boolean z;
        VoiceChannelInfo voiceChannelInfo;
        this.currentParcelID = i;
        if (lLSDNode != null) {
            synchronized (this.parcelVoiceChannelLock) {
                try {
                    voiceChannelInfo = new VoiceChannelInfo(lLSDNode.byKey("voice_credentials").byKey("channel_uri").asString(), true, true);
                } catch (LLSDException e) {
                    Debug.Printf("Voice: error retrieving parcel voice info for %d (%s)", Integer.valueOf(i), e.getMessage());
                    voiceChannelInfo = null;
                }
                if (Objects.equal(this.currentParcelVoiceChannel, voiceChannelInfo)) {
                    z = false;
                } else {
                    this.currentParcelVoiceChannel = voiceChannelInfo;
                    z = true;
                }
            }
        } else {
            Debug.Printf("Voice: error retrieving parcel voice info for %d", Integer.valueOf(i));
            z = false;
        }
        if (z) {
            this.agentCircuit.getModules().minimap.requestUpdateAvatarParcelData();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14030  reason: not valid java name */
    public /* synthetic */ void m266x4b47856f(VoiceLoginStatus voiceLoginStatus, VoicePluginServiceConnection voicePluginServiceConnection) {
        if (voiceLoginStatus.loggedIn) {
            this.voicePluginServiceConnection = voicePluginServiceConnection;
            return;
        }
        this.voicePluginServiceConnection = null;
        this.connectedVoiceChannel = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14408  reason: not valid java name */
    public /* synthetic */ void m267x4b47941e(VoiceChannelStatus voiceChannelStatus) {
        if (voiceChannelStatus.errorMessage != null) {
            if (this.connectedVoiceChannel == null || !Objects.equal(this.connectedVoiceChannel.voiceChannelURI, voiceChannelStatus.channelInfo.voiceChannelURI)) {
                return;
            }
            this.connectedVoiceChannel = null;
        } else if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            if (this.connectedVoiceChannel == null || !Objects.equal(this.connectedVoiceChannel.voiceChannelURI, voiceChannelStatus.channelInfo.voiceChannelURI)) {
                return;
            }
            this.connectedVoiceChannel = null;
        } else if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
            this.connectedVoiceChannel = voiceChannelStatus.channelInfo;
            if (this.voiceLoggedIn && voiceChannelStatus.channelInfo.isSpatial) {
                updateSpatialVoicePosition();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_4388  reason: not valid java name */
    public /* synthetic */ void m268xd924b13a() {
        GridConnectionService serviceInstance;
        this.voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled();
        if (!this.voiceEnabled) {
            GridConnectionService serviceInstance2 = GridConnectionService.getServiceInstance();
            if (serviceInstance2 != null) {
                serviceInstance2.stopVoice();
            }
        } else if (!this.voiceCredentialsRequested) {
            this.voiceCredentialsRequested = true;
            if (this.capURL != null) {
                new LLSDXMLAsyncRequest(this.capURL, new LLSDUndefined(), new LLSDXMLAsyncRequest.LLSDXMLResultListener() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.1
                    private final /* synthetic */ void $m$0(LLSDNode lLSDNode) {
                        ((SLVoice) this).m262com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref1(lLSDNode);
                    }

                    @Override // com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener
                    public final void onLLSDXMLResult(LLSDNode lLSDNode) {
                        $m$0(lLSDNode);
                    }
                });
            }
        } else if (this.voiceLoginInfo == null || !(!this.voiceLoggedIn) || (serviceInstance = GridConnectionService.getServiceInstance()) == null) {
        } else {
            serviceInstance.startVoice(this.voiceLoginInfo, UserManager.getUserManager(this.agentCircuit.getAgentUUID()));
        }
    }

    public void nearbyVoiceChatRequest(VoiceChannelInfo voiceChannelInfo) {
        VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        if (this.voiceEnabled && this.voiceLoggedIn && voicePluginServiceConnection != null) {
            voicePluginServiceConnection.addChannel(ChatterID.getLocalChatterID(this.userManager.getUserID()), voiceChannelInfo);
            voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, null));
        }
    }

    @EventHandler
    public void onGlobalOptionsChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        updateVoiceEnabledStatus();
    }

    public void onGroupSessionReady(final UUID uuid) {
        if (!this.requestedGroupChats.remove(uuid) || this.chatSessionRequestURL == null) {
            return;
        }
        new LLSDXMLAsyncRequest(this.chatSessionRequestURL, new LLSDMap(new LLSDMap.LLSDMapEntry("method", new LLSDString(NotificationCompat.CATEGORY_CALL)), new LLSDMap.LLSDMapEntry("session-id", new LLSDUUID(uuid))), new LLSDXMLAsyncRequest.LLSDXMLResultListener() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice.1
            @Override // com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener
            public void onLLSDXMLResult(LLSDNode lLSDNode) {
                ChatterID groupChatterID = ChatterID.getGroupChatterID(SLVoice.this.userManager.getUserID(), uuid);
                try {
                    if (lLSDNode == null) {
                        throw new LLSDException("Null result");
                    }
                    String asString = lLSDNode.byKey("voice_credentials").byKey("channel_uri").asString();
                    String asString2 = lLSDNode.byKey("voice_credentials").byKey("channel_credentials").asString();
                    VoicePluginServiceConnection voicePluginServiceConnection = SLVoice.this.voicePluginServiceConnection;
                    if (SLVoice.this.voiceEnabled && SLVoice.this.voiceLoggedIn && voicePluginServiceConnection != null) {
                        VoiceChannelInfo voiceChannelInfo = new VoiceChannelInfo(asString, false, true);
                        voicePluginServiceConnection.addChannel(groupChatterID, voiceChannelInfo);
                        voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, asString2));
                    }
                } catch (LLSDException e) {
                    SLVoice.this.agentCircuit.HandleChatEvent(groupChatterID, new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), SLVoice.this.userManager.getUserID(), LumiyaApp.getContext().getString(R.string.failed_to_connect_group_voice)), false);
                    Debug.Warning(e);
                }
            }
        });
    }

    public void onVoiceChannelStatus(final VoiceChannelStatus voiceChannelStatus) {
        this.agentCircuit.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.5
            private final /* synthetic */ void $m$0() {
                ((SLVoice) this).m267x4b47941e((VoiceChannelStatus) voiceChannelStatus);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void onVoiceLoginStatus(final VoicePluginServiceConnection voicePluginServiceConnection, final VoiceLoginStatus voiceLoginStatus) {
        this.agentCircuit.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.6
            private final /* synthetic */ void $m$0() {
                ((SLVoice) this).m266x4b47856f((VoiceLoginStatus) voiceLoginStatus, (VoicePluginServiceConnection) voicePluginServiceConnection);
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public boolean requestParcelVoiceInfo() {
        if (this.parcelVoiceCapURL != null) {
            new LLSDXMLAsyncRequest(this.parcelVoiceCapURL, new LLSDUndefined(), new LLSDXMLAsyncRequest.LLSDXMLResultListener() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.3
                private final /* synthetic */ void $m$0(LLSDNode lLSDNode) {
                    ((SLVoice) this).m264com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref3(lLSDNode);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener
                public final void onLLSDXMLResult(LLSDNode lLSDNode) {
                    $m$0(lLSDNode);
                }
            });
            return true;
        }
        return false;
    }

    public void setCurrentParcel(final int i) {
        boolean z = false;
        synchronized (this.parcelVoiceChannelLock) {
            if (this.parcelVoiceCapURL != null && this.capURL != null && this.requestedParcelID != i) {
                this.requestedParcelID = i;
                z = true;
            }
        }
        if (z) {
            new LLSDXMLAsyncRequest(this.parcelVoiceCapURL, new LLSDUndefined(), new LLSDXMLAsyncRequest.LLSDXMLResultListener() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.7
                private final /* synthetic */ void $m$0(LLSDNode lLSDNode) {
                    ((SLVoice) this).m265x4b46af5c(i, lLSDNode);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.https.LLSDXMLAsyncRequest.LLSDXMLResultListener
                public final void onLLSDXMLResult(LLSDNode lLSDNode) {
                    $m$0(lLSDNode);
                }
            });
        }
    }

    public void updateSpatialVoicePosition() {
        VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        VoiceChannelInfo voiceChannelInfo = this.connectedVoiceChannel;
        if (voicePluginServiceConnection == null || voiceChannelInfo == null || !voiceChannelInfo.isSpatial) {
            return;
        }
        LLVector3d agentGlobalPosition = this.agentCircuit.getAgentGlobalPosition();
        SLModules modules = this.agentCircuit.getModules();
        if (agentGlobalPosition == null || modules == null) {
            return;
        }
        float agentHeading = modules.avatarControl.getAgentHeading() * 0.017453292f;
        float cos = (float) Math.cos(agentHeading);
        float sin = (float) Math.sin(agentHeading);
        Voice3DVector fromLLCoords = Voice3DVector.fromLLCoords(cos, sin, 0.0f);
        Voice3DVector fromLLCoords2 = Voice3DVector.fromLLCoords(-sin, cos, 0.0f);
        Voice3DPosition voice3DPosition = new Voice3DPosition(Voice3DVector.fromLLCoords((float) agentGlobalPosition.x, (float) agentGlobalPosition.y, (float) agentGlobalPosition.z), new Voice3DVector(0.0f, 0.0f, 0.0f), fromLLCoords, Voice3DVector.fromLLCoords(0.0f, 0.0f, 1.0f), fromLLCoords2);
        voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceSet3DPosition, new VoiceSet3DPosition(voiceChannelInfo, voice3DPosition, voice3DPosition));
    }

    public void updateVoiceEnabledStatus() {
        UIThreadExecutor.getInstance().execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.4
            private final /* synthetic */ void $m$0() {
                ((SLVoice) this).m268xd924b13a();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public boolean userVoiceChatRequest(UUID uuid) {
        VoicePluginServiceConnection voicePluginServiceConnection = this.voicePluginServiceConnection;
        VoiceLoginInfo voiceLoginInfo = this.voiceLoginInfo;
        if (!this.voiceEnabled || !this.voiceLoggedIn || uuid == null || voicePluginServiceConnection == null || voiceLoginInfo == null || this.userManager == null) {
            return false;
        }
        VoiceChannelInfo voiceChannelInfo = new VoiceChannelInfo(uuid, voiceLoginInfo.voiceSipUriHostname);
        voicePluginServiceConnection.addChannel(ChatterID.getUserChatterID(this.userManager.getUserID(), uuid), voiceChannelInfo);
        voicePluginServiceConnection.sendMessage(VoicePluginMessageType.VoiceConnectChannel, new VoiceConnectChannel(voiceChannelInfo, null));
        return true;
    }
}
