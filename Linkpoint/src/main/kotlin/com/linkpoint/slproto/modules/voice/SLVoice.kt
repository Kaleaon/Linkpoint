package com.linkpoint.slproto.modules.voice

import android.support.v4.app.NotificationCompat
import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.GlobalOptions
import com.linkpoint.GridConnectionService
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.caps.SLCaps
import com.linkpoint.slproto.chat.SLChatSystemMessageEvent
import com.linkpoint.slproto.https.LLSDXMLAsyncRequest
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.types.LLVector3d
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.voice.common.VoicePluginMessageType
import com.linkpoint.voice.common.messages.VoiceChannelStatus
import com.linkpoint.voice.common.messages.VoiceConnectChannel
import com.linkpoint.voice.common.messages.VoiceLoginStatus
import com.linkpoint.voice.common.messages.VoiceSet3DPosition
import com.linkpoint.voice.common.model.Voice3DPosition
import com.linkpoint.voice.common.model.Voice3DVector
import com.linkpoint.voice.common.model.VoiceChannelInfo
import com.linkpoint.voice.common.model.VoiceChatInfo
import com.linkpoint.voice.common.model.VoiceLoginInfo
import com.linkpoint.voiceintf.VoicePluginServiceConnection
import java.util.Collections
import java.util.HashSet
import java.util.Set
import java.util.UUID
import javax.annotation.Nullable

class SLVoice : SLModule() {
    private const val INVALID_PARCEL_ID: Int = -1
    private const val LOGIN_DELAY: Int = 5
    private const val MAX_LOGIN_ATTEMPTS: Int = 3
    private val String capURL
    private val String chatSessionRequestURL
    private volatile VoiceChannelInfo connectedVoiceChannel = null
    private Int currentParcelID = -1
    private VoiceChannelInfo currentParcelVoiceChannel = null
    private Int loginAttempts = 0
    private val String parcelVoiceCapURL
    private val Object parcelVoiceChannelLock = Object()
    private val Set<UUID> requestedGroupChats = Collections.synchronizedSet(HashSet())
    private Int requestedParcelID = -1
    private volatile Boolean shutdown = false
    /* access modifiers changed from: private */
    val UserManager userManager = UserManager.getUserManager(this.agentCircuit.getAgentUUID())
    private Boolean voiceCredentialsRequested = false
    /* access modifiers changed from: private */
    public volatile Boolean voiceEnabled = false
    /* access modifiers changed from: private */
    public volatile Boolean voiceLoggedIn = false
    private val SubscriptionData<SubscriptionSingleKey, Boolean> voiceLoggedInSubscription = SubscriptionData<>(UIThreadExecutor.getInstance(), $Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY(this))
    private volatile VoiceLoginInfo voiceLoginInfo = null
    /* access modifiers changed from: private */
    public volatile VoicePluginServiceConnection voicePluginServiceConnection = null

    public SLVoice(SLAgentCircuit sLAgentCircuit, SLCaps sLCaps) {
        super(sLAgentCircuit)
        this.capURL = sLCaps.getCapability(SLCaps.SLCapability.ProvisionVoiceAccountRequest)
        this.parcelVoiceCapURL = sLCaps.getCapability(SLCaps.SLCapability.ParcelVoiceInfoRequest)
        this.chatSessionRequestURL = sLCaps.getCapability(SLCaps.SLCapability.ChatSessionRequest)
        if (this.userManager != null) {
            this.voiceLoggedInSubscription.subscribe(this.userManager.getVoiceLoggedIn(), SubscriptionSingleKey.Value)
        }
        if (this.capURL != null) {
            Debug.Printf("Voice cap: '%s'", this.capURL)
        } else {
            Debug.Printf("Voice cap not supported", Object[0])
        }
        EventBus.getInstance().subscribe((Object) this)
        updateVoiceEnabledStatus()
    }

    /* access modifiers changed from: private */
    /* renamed from: onParcelVoiceInfoResult */
    public Unit m253com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref3(LLSDNode lLSDNode) {
        if (lLSDNode != null) {
            try {
                Debug.Printf("SLVoice: parcel voice info '%s'", lLSDNode.serializeToXML())
            } catch (Exception e) {
                Debug.Warning(e)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onProvisionVoiceAccountResult */
    public Unit m252com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref2(LLSDNode lLSDNode) {
        if (lLSDNode != null) {
            try {
                Debug.Printf("SLVoice: result '%s'", lLSDNode.serializeToXML())
                this.voiceLoginInfo = VoiceLoginInfo(lLSDNode.byKey("voice_sip_uri_hostname").asString(), lLSDNode.byKey("voice_account_server_name").asString(), this.agentCircuit.getAgentUUID(), lLSDNode.byKey("username").asString(), lLSDNode.byKey("password").asString())
                updateVoiceEnabledStatus()
            } catch (Exception e) {
                Debug.Warning(e)
            }
        } else {
            Debug.Printf("SLVoice: null result", Object[0])
            if (this.shutdown || this.loginAttempts >= 3 || !this.voiceEnabled) {
                Debug.Printf("SLVoice: giving up", Object[0])
                return
            }
            this.loginAttempts++
            try {
                Thread.sleep(5000)
                if (!this.shutdown && this.voiceEnabled) {
                    LLSDXMLAsyncRequest(this.capURL, LLSDUndefined(), LLSDXMLAsyncRequest.LLSDXMLResultListener(this) {

                        /* renamed from: -$f0 */
                        private val /* synthetic */ Object f133$f0

                        private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.2.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, dex: classes.dex
                        jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.2.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, class status: UNLOADED
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
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:311)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:68)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:156)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

                }
            } catch (InterruptedException e2) {
                Debug.Warning(e2)
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: onVoiceLoginStatusChanged */
    public Unit m250com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoicemthref0(Boolean bool) {
        this.voiceLoggedIn = bool != null ? bool.booleanValue() : false
    }

    public Unit HandleCloseCircuit() {
        this.shutdown = true
        this.voiceLoggedInSubscription.unsubscribe()
        super.HandleCloseCircuit()
    }

    public VoiceChannelInfo getCurrentParcelVoiceChannel() {
        VoiceChannelInfo voiceChannelInfo
        synchronized (this.parcelVoiceChannelLock) {
            voiceChannelInfo = this.currentParcelVoiceChannel
        }
        return voiceChannelInfo
    }

    public VoiceLoginInfo getVoiceLoginInfo() {
        return this.voiceLoginInfo
    }

    public Boolean groupVoiceChatRequest(UUID uuid) {
        if (!this.voiceEnabled || !this.voiceLoggedIn || this.chatSessionRequestURL == null) {
            return false
        }
        this.requestedGroupChats.add(uuid)
        this.agentCircuit.StartGroupSessionForVoice(uuid)
        return true
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_12525  reason: not valid java name */
    public /* synthetic */ Unit m254lambda$com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_12525(Int i, LLSDNode lLSDNode) {
        VoiceChannelInfo voiceChannelInfo
        this.currentParcelID = i
        if (lLSDNode != null) {
            synchronized (this.parcelVoiceChannelLock) {
                try {
                    voiceChannelInfo = VoiceChannelInfo(lLSDNode.byKey("voice_credentials").byKey("channel_uri").asString(), true, true)
                } catch (LLSDException e) {
                    Debug.Printf("Voice: error retrieving parcel voice info for %d (%s)", Integer.valueOf(i), e.getMessage())
                    voiceChannelInfo = null
                }
                if (!Objects.equal(this.currentParcelVoiceChannel, voiceChannelInfo)) {
                    this.currentParcelVoiceChannel = voiceChannelInfo
                    z = true
                } else {
                    z = false
                }
            }
        } else {
            Debug.Printf("Voice: error retrieving parcel voice info for %d", Integer.valueOf(i))
            z = false
        }
        if (z) {
            this.agentCircuit.getModules().minimap.requestUpdateAvatarParcelData()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14030  reason: not valid java name */
    public /* synthetic */ Unit m255lambda$com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14030(VoiceLoginStatus voiceLoginStatus, VoicePluginServiceConnection voicePluginServiceConnection2) {
        if (voiceLoginStatus.loggedIn) {
            this.voicePluginServiceConnection = voicePluginServiceConnection2
            return
        }
        this.voicePluginServiceConnection = null
        this.connectedVoiceChannel = null
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14408  reason: not valid java name */
    public /* synthetic */ Unit m256lambda$com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_14408(VoiceChannelStatus voiceChannelStatus) {
        if (voiceChannelStatus.errorMessage != null) {
            if (this.connectedVoiceChannel != null && Objects.equal(this.connectedVoiceChannel.voiceChannelURI, voiceChannelStatus.channelInfo.voiceChannelURI)) {
                this.connectedVoiceChannel = null
            }
        } else if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            if (this.connectedVoiceChannel != null && Objects.equal(this.connectedVoiceChannel.voiceChannelURI, voiceChannelStatus.channelInfo.voiceChannelURI)) {
                this.connectedVoiceChannel = null
            }
        } else if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
            this.connectedVoiceChannel = voiceChannelStatus.channelInfo
            if (this.voiceLoggedIn && voiceChannelStatus.channelInfo.isSpatial) {
                updateSpatialVoicePosition()
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_4388  reason: not valid java name */
    public /* synthetic */ Unit m257lambda$com_lumiyaviewer_lumiya_slproto_modules_voice_SLVoice_4388() {
        GridConnectionService serviceInstance
        this.voiceEnabled = GlobalOptions.getInstance().getVoiceEnabled()
        if (!this.voiceEnabled) {
            GridConnectionService serviceInstance2 = GridConnectionService.getServiceInstance()
            if (serviceInstance2 != null) {
                serviceInstance2.stopVoice()
            }
        } else if (!this.voiceCredentialsRequested) {
            this.voiceCredentialsRequested = true
            if (this.capURL != null) {
                LLSDXMLAsyncRequest(this.capURL, LLSDUndefined(), LLSDXMLAsyncRequest.LLSDXMLResultListener(this) {

                    /* renamed from: -$f0 */
                    private val /* synthetic */ Object f132$f0

                    private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.1.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, dex: classes.dex
                    jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.1.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, class status: UNLOADED
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
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                    	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

            }
        } else if (this.voiceLoginInfo != null && (!this.voiceLoggedIn) && (serviceInstance = GridConnectionService.getServiceInstance()) != null) {
            serviceInstance.startVoice(this.voiceLoginInfo, UserManager.getUserManager(this.agentCircuit.getAgentUUID()))
        }
    }

    public Unit nearbyVoiceChatRequest(VoiceChannelInfo voiceChannelInfo) {
        VoicePluginServiceConnection voicePluginServiceConnection2 = this.voicePluginServiceConnection
        if (this.voiceEnabled && this.voiceLoggedIn && voicePluginServiceConnection2 != null) {
            voicePluginServiceConnection2.addChannel(ChatterID.getLocalChatterID(this.userManager.getUserID()), voiceChannelInfo)
            voicePluginServiceConnection2.sendMessage(VoicePluginMessageType.VoiceConnectChannel, VoiceConnectChannel(voiceChannelInfo, (String) null))
        }
    }

    @EventHandler
    public Unit onGlobalOptionsChanged(GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        updateVoiceEnabledStatus()
    }

    public Unit onGroupSessionReady(final UUID uuid) {
        if (this.requestedGroupChats.remove(uuid) && this.chatSessionRequestURL != null) {
            LLSDXMLAsyncRequest(this.chatSessionRequestURL, LLSDMap(LLSDMap.LLSDMapEntry("method", LLSDString(NotificationCompat.CATEGORY_CALL)), LLSDMap.LLSDMapEntry("session-id", LLSDUUID(uuid))), LLSDXMLAsyncRequest.LLSDXMLResultListener() {
                public Unit onLLSDXMLResult(LLSDNode lLSDNode) {
                    ChatterID groupChatterID = ChatterID.getGroupChatterID(SLVoice.this.userManager.getUserID(), uuid)
                    if (lLSDNode == null) {
                        try {
                            throw LLSDException("Null result")
                        } catch (LLSDException e) {
                            SLVoice.this.agentCircuit.HandleChatEvent(groupChatterID, SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), SLVoice.this.userManager.getUserID(), LinkpointApp.getContext().getString(R.string.failed_to_connect_group_voice)), false)
                            Debug.Warning(e)
                        }
                    } else {
                        String asString = lLSDNode.byKey("voice_credentials").byKey("channel_uri").asString()
                        String asString2 = lLSDNode.byKey("voice_credentials").byKey("channel_credentials").asString()
                        VoicePluginServiceConnection r3 = SLVoice.this.voicePluginServiceConnection
                        if (SLVoice.this.voiceEnabled && SLVoice.this.voiceLoggedIn && r3 != null) {
                            VoiceChannelInfo voiceChannelInfo = VoiceChannelInfo(asString, false, true)
                            r3.addChannel(groupChatterID, voiceChannelInfo)
                            r3.sendMessage(VoicePluginMessageType.VoiceConnectChannel, VoiceConnectChannel(voiceChannelInfo, asString2))
                        }
                    }
                }
        }
    }

    public Unit onVoiceChannelStatus(VoiceChannelStatus voiceChannelStatus) {
        this.agentCircuit.execute(Runnable(this, voiceChannelStatus) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f136$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f137$f1

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.5.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.5.$m$0():Unit, class status: UNLOADED
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

    }

    public Unit onVoiceLoginStatus(VoicePluginServiceConnection voicePluginServiceConnection2, VoiceLoginStatus voiceLoginStatus) {
        this.agentCircuit.execute(Runnable(this, voiceLoginStatus, voicePluginServiceConnection2) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f138$f0

            /* renamed from: -$f1 */
            private val /* synthetic */ Object f139$f1

            /* renamed from: -$f2 */
            private val /* synthetic */ Object f140$f2

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.6.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.6.$m$0():Unit, class status: UNLOADED
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

    }

    public Boolean requestParcelVoiceInfo() {
        if (this.parcelVoiceCapURL == null) {
            return false
        }
        LLSDXMLAsyncRequest(this.parcelVoiceCapURL, LLSDUndefined(), LLSDXMLAsyncRequest.LLSDXMLResultListener(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f134$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.3.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.3.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, class status: UNLOADED
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

        return true
    }

    public Unit setCurrentParcel(Int i) {
        Boolean z = false
        synchronized (this.parcelVoiceChannelLock) {
            if (!(this.parcelVoiceCapURL == null || this.capURL == null || this.requestedParcelID == i)) {
                this.requestedParcelID = i
                z = true
            }
        }
        if (z) {
            LLSDXMLAsyncRequest(this.parcelVoiceCapURL, LLSDUndefined(), LLSDXMLAsyncRequest.LLSDXMLResultListener(i, this) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Int f141$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f142$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.7.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.7.$m$0(com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode):Unit, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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

        }
    }

    public Unit updateSpatialVoicePosition() {
        VoicePluginServiceConnection voicePluginServiceConnection2 = this.voicePluginServiceConnection
        VoiceChannelInfo voiceChannelInfo = this.connectedVoiceChannel
        if (voicePluginServiceConnection2 != null && voiceChannelInfo != null && voiceChannelInfo.isSpatial) {
            LLVector3d agentGlobalPosition = this.agentCircuit.getAgentGlobalPosition()
            SLModules modules = this.agentCircuit.getModules()
            if (agentGlobalPosition != null && modules != null) {
                Float agentHeading = modules.avatarControl.getAgentHeading() * 0.017453292f
                Float cos = (Float) Math.cos((Double) agentHeading)
                Float sin = (Float) Math.sin((Double) agentHeading)
                Voice3DVector fromLLCoords = Voice3DVector.fromLLCoords(cos, sin, 0.0f)
                Voice3DVector fromLLCoords2 = Voice3DVector.fromLLCoords(-sin, cos, 0.0f)
                Voice3DPosition voice3DPosition = Voice3DPosition(Voice3DVector.fromLLCoords((Float) agentGlobalPosition.x, (Float) agentGlobalPosition.y, (Float) agentGlobalPosition.z), Voice3DVector(0.0f, 0.0f, 0.0f), fromLLCoords, Voice3DVector.fromLLCoords(0.0f, 0.0f, 1.0f), fromLLCoords2)
                voicePluginServiceConnection2.sendMessage(VoicePluginMessageType.VoiceSet3DPosition, VoiceSet3DPosition(voiceChannelInfo, voice3DPosition, voice3DPosition))
            }
        }
    }

    public Unit updateVoiceEnabledStatus() {
        UIThreadExecutor.getInstance().execute(Runnable(this) {

            /* renamed from: -$f0 */
            private val /* synthetic */ Object f135$f0

            private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.4.$m$0():Unit, dex: classes.dex
            jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.slproto.modules.voice.-$Lambda$RETWaU3Ta92aG3GeBeXvI3Y9viY.4.$m$0():Unit, class status: UNLOADED
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

    }

    public Boolean userVoiceChatRequest(UUID uuid) {
        VoicePluginServiceConnection voicePluginServiceConnection2 = this.voicePluginServiceConnection
        VoiceLoginInfo voiceLoginInfo2 = this.voiceLoginInfo
        if (!this.voiceEnabled || !this.voiceLoggedIn || uuid == null || voicePluginServiceConnection2 == null || voiceLoginInfo2 == null || this.userManager == null) {
            return false
        }
        VoiceChannelInfo voiceChannelInfo = VoiceChannelInfo(uuid, voiceLoginInfo2.voiceSipUriHostname)
        voicePluginServiceConnection2.addChannel(ChatterID.getUserChatterID(this.userManager.getUserID(), uuid), voiceChannelInfo)
        voicePluginServiceConnection2.sendMessage(VoicePluginMessageType.VoiceConnectChannel, VoiceConnectChannel(voiceChannelInfo, (String) null))
        return true
    }
}
