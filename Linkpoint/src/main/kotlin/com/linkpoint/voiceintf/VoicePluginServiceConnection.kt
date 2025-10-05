package com.linkpoint.voiceintf

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.Parcelable
import android.support.v7.app.NotificationCompat
import com.google.common.base.Objects
import com.google.common.base.Strings
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.Maps
import com.linkpoint.Debug
import com.linkpoint.GridConnectionService
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.licensing.LicenseChecker
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.SLChatSystemMessageEvent
import com.linkpoint.slproto.chat.SLMissedVoiceCallEvent
import com.linkpoint.slproto.chat.SLVoiceUpgradeEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUser
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatFragment
import com.linkpoint.ui.chat.contacts.ChatFragmentActivityFactory
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.settings.NotificationType
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.VoicePluginMessageType
import com.linkpoint.voice.common.VoicePluginMessenger
import com.linkpoint.voice.common.messages.VoiceAcceptCall
import com.linkpoint.voice.common.messages.VoiceAudioProperties
import com.linkpoint.voice.common.messages.VoiceChannelStatus
import com.linkpoint.voice.common.messages.VoiceEnableMic
import com.linkpoint.voice.common.messages.VoiceInitialize
import com.linkpoint.voice.common.messages.VoiceInitializeReply
import com.linkpoint.voice.common.messages.VoiceLogin
import com.linkpoint.voice.common.messages.VoiceLoginStatus
import com.linkpoint.voice.common.messages.VoiceLogout
import com.linkpoint.voice.common.messages.VoiceRejectCall
import com.linkpoint.voice.common.messages.VoiceRinging
import com.linkpoint.voice.common.messages.VoiceSetAudioProperties
import com.linkpoint.voice.common.messages.VoiceTerminateCall
import com.linkpoint.voice.common.model.VoiceBluetoothState
import com.linkpoint.voice.common.model.VoiceChannelInfo
import com.linkpoint.voice.common.model.VoiceChatInfo
import com.linkpoint.voice.common.model.VoiceLoginInfo
import java.util.Collections
import java.util.HashSet
import java.util.List
import java.util.Set
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.annotation.Nullable

class VoicePluginServiceConnection : ServiceConnection {
    const val String ACTION_VOICE_ACCEPT = "accept"
    const val String ACTION_VOICE_REJECT = "reject"
    private const val Int INCOMING_CALL_NOTIFICATION_ID = 1001
    private const val String INTENT_EXTRA_CHATTER_ID = "chatterID"
    private const val String INTENT_EXTRA_OPEN_CHATTER = "openChatterIntent"
    private const val String INTENT_EXTRA_RINGING_MESSSAGE = "ringingMessage"
    private const val Int REQUIRED_PLUGIN_VERSION = 3
    private const val AtomicBoolean installOfferDisplayed = AtomicBoolean(false)
    private val Context context
    private val Handler fromPluginHandler = Handler() {

        /* renamed from: -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues  reason: not valid java name */
        private const val /* synthetic */ Int[] f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues = null
        final /* synthetic */ Int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$voice$common$VoicePluginMessageType

        /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues  reason: not valid java name */
        @JvmStatic
private /* synthetic */ Int[] m906getcomlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues() {
            if (f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues != null) {
                return f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues
            }
            Int[] iArr = Int[VoicePluginMessageType.values().length]
            try {
                iArr[VoicePluginMessageType.VoiceAcceptCall.ordinal()] = 6
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceAudioProperties.ordinal()] = 1
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceChannelClosed.ordinal()] = 7
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceChannelStatus.ordinal()] = 2
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceConnectChannel.ordinal()] = 8
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceEnableMic.ordinal()] = 9
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceInitialize.ordinal()] = 10
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceInitializeReply.ordinal()] = 3
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLogin.ordinal()] = 11
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLoginStatus.ordinal()] = 4
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLogout.ordinal()] = 12
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceRejectCall.ordinal()] = 13
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceRinging.ordinal()] = 5
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceSet3DPosition.ordinal()] = 14
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceSetAudioProperties.ordinal()] = 15
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceTerminateCall.ordinal()] = 16
            } catch (NoSuchFieldError e16) {
            }
            f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues = iArr
            return iArr
        }

        public Unit handleMessage(Message message) {
            if (message.what == 200 && (message.obj instanceof Bundle)) {
                Bundle bundle = (Bundle) message.obj
                if (bundle.containsKey("message") && bundle.containsKey("messageType")) {
                    try {
                        switch (m906getcomlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues()[VoicePluginMessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                            case 1:
                                VoicePluginServiceConnection.this.onVoiceAudioProperties(VoiceAudioProperties(bundle.getBundle("message")))
                                return
                            case 2:
                                VoicePluginServiceConnection.this.onVoiceChannelStatus(VoiceChannelStatus(bundle.getBundle("message")))
                                return
                            case 3:
                                VoicePluginServiceConnection.this.onVoiceInitializeReply(VoiceInitializeReply(bundle.getBundle("message")))
                                return
                            case 4:
                                VoicePluginServiceConnection.this.onVoiceLoginStatus(VoiceLoginStatus(bundle.getBundle("message")))
                                return
                            case 5:
                                VoicePluginServiceConnection.this.onVoiceRinging(VoiceRinging(bundle.getBundle("message")))
                                return
                            default:
                                return
                        }
                    } catch (Exception e) {
                        Debug.Warning(e)
                    }
                    Debug.Warning(e)
                }
            }
        }
    }
    private val Messenger fromPluginMessenger
    private val Set<String> incomingCallNotificationTags = Collections.synchronizedSet(HashSet())
    private val Handler mainThreadHandler = Handler()
    private ChatterNameRetriever ringingChatterNameRetriever = null
    private Messenger toPluginMessenger
    private val AtomicReference<UserManager> userManager = AtomicReference<>((Object) null)
    private val BiMap<ChatterID, VoiceChannelInfo> voiceChannels = Maps.synchronizedBiMap(HashBiMap.create())
    private val AtomicBoolean voiceInitialized = AtomicBoolean(false)
    private val AtomicReference<VoiceLoginInfo> voiceLoginInfo = AtomicReference<>((Object) null)

    public VoicePluginServiceConnection(Context context2) {
        this.context = context2
        this.fromPluginMessenger = Messenger(this.fromPluginHandler)
    }

    private Unit cancelNotifications(String str) {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification")
        if (str != null) {
            notificationManager.cancel(str, 1001)
            this.incomingCallNotificationTags.remove(str)
            return
        }
        for (String cancel : this.incomingCallNotificationTags) {
            notificationManager.cancel(cancel, 1001)
        }
        this.incomingCallNotificationTags.clear()
    }

    @JvmStatic
    Boolean checkPluginInstalled(Context context2) {
        Intent intent = Intent()
        intent.setComponent(ComponentName("com.linkpoint.voice", "com.linkpoint.voice.VoiceService"))
        List<ResolveInfo> queryIntentServices = context2.getPackageManager().queryIntentServices(intent, 0)
        return queryIntentServices != null && queryIntentServices.size() > 0
    }

    @JvmStatic
    Boolean isPluginSupported() {
        String[] strArr
        if (Build.VERSION.SDK_INT >= 21) {
            strArr = Build.SUPPORTED_ABIS
        } else {
            strArr = String[]{Build.CPU_ABI, Build.CPU_ABI2}
        }
        if (strArr == null) {
            return false
        }
        for (String str : strArr) {
            if (str != null && (str.toLowerCase().contains("armeabi") || str.toLowerCase().contains("arm64"))) {
                return true
            }
        }
        return false
    }

    /* access modifiers changed from: private */
    public Unit onVoiceAudioProperties(VoiceAudioProperties voiceAudioProperties) {
        VoiceBluetoothState voiceBluetoothState = null
        Object[] objArr = Object[1]
        if (voiceAudioProperties != null) {
            voiceBluetoothState = voiceAudioProperties.bluetoothState
        }
        objArr[0] = voiceBluetoothState
        Debug.Printf("Voice: voice audio properties received, bluetooth state %s", objArr)
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null) {
            userManager2.setVoiceAudioProperties(voiceAudioProperties)
        }
    }

    /* access modifiers changed from: private */
    public Unit onVoiceChannelStatus(VoiceChannelStatus voiceChannelStatus) {
        SLModules modules
        if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            cancelNotifications(voiceChannelStatus.channelInfo.voiceChannelURI)
        }
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null) {
            ChatterID chatterID = (ChatterID) this.voiceChannels.inverse().get(voiceChannelStatus.channelInfo)
            if (chatterID != null) {
                userManager2.setVoiceChatInfo(chatterID, voiceChannelStatus.chatInfo)
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None && voiceChannelStatus.chatInfo.previousState == VoiceChatInfo.VoiceChatState.Ringing && (chatterID instanceof ChatterID.ChatterIDUser)) {
                    userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, SLMissedVoiceCallEvent(ChatMessageSourceUser(((ChatterID.ChatterIDUser) chatterID).getChatterUUID()), userManager2.getUserID(), LinkpointApp.getContext().getString(R.string.missed_voice_call)), true)
                }
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                    userManager2.setVoiceActiveChatter(chatterID)
                }
            }
            SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit()
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.voice.onVoiceChannelStatus(voiceChannelStatus)
            }
        }
    }

    /* access modifiers changed from: private */
    public Unit onVoiceInitializeReply(VoiceInitializeReply voiceInitializeReply) {
        if (!voiceInitializeReply.appVersionOk) {
            UserManager userManager2 = this.userManager.get()
            if (userManager2 != null) {
                userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager2.getUserID()), SLVoiceUpgradeEvent(userManager2.getUserID(), LinkpointApp.getContext().getString(R.string.app_upgrade_for_voice_needed), false, LicenseChecker.APP_STORE_URL), false)
            }
        } else if (voiceInitializeReply.pluginVersionCode < 3) {
            UserManager userManager3 = this.userManager.get()
            if (userManager3 != null) {
                userManager3.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager3.getUserID()), SLVoiceUpgradeEvent(userManager3.getUserID(), LinkpointApp.getContext().getString(R.string.plugin_upgrade_for_voice_needed), false, LicenseChecker.VOICE_PLUGIN_URL), false)
            }
        } else if (voiceInitializeReply.errorMessage == null) {
            this.voiceInitialized.set(true)
            VoiceLoginInfo voiceLoginInfo2 = this.voiceLoginInfo.get()
            if (voiceLoginInfo2 != null) {
                sendMessage(VoicePluginMessageType.VoiceLogin, VoiceLogin(voiceLoginInfo2))
            }
        } else {
            UserManager userManager4 = this.userManager.get()
            if (userManager4 != null) {
                userManager4.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager4.getUserID()), SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), userManager4.getUserID(), LinkpointApp.getContext().getString(R.string.voice_plugin_error_format, Object[]{voiceInitializeReply.errorMessage})), false)
            }
        }
    }

    /* access modifiers changed from: private */
    public Unit onVoiceLoginStatus(VoiceLoginStatus voiceLoginStatus) {
        SLModules modules
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null) {
            SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit()
            if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
                modules.voice.onVoiceLoginStatus(this, voiceLoginStatus)
            }
            userManager2.setVoiceLoggedIn(voiceLoginStatus.loggedIn)
        }
    }

    /* access modifiers changed from: private */
    public Unit onVoiceRinging(VoiceRinging voiceRinging) {
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null && voiceRinging != null && voiceRinging.agentUUID != null) {
            ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(userManager2.getUserID(), voiceRinging.agentUUID)
            this.voiceChannels.forcePut(userChatterID, voiceRinging.voiceChannelInfo)
            this.ringingChatterNameRetriever = ChatterNameRetriever(userChatterID, ChatterNameRetriever.OnChatterNameUpdated(this, voiceRinging) {

                /* renamed from: -$f0 */
                private val /* synthetic */ Object f609$f0

                /* renamed from: -$f1 */
                private val /* synthetic */ Object f610$f1

                private val /* synthetic */ Unit $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):Unit, class status: UNLOADED
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
                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
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

            }, UIThreadExecutor.getSerialInstance(), false)
            this.ringingChatterNameRetriever.subscribe()
        }
    }

    @JvmStatic
    Unit setInstallOfferDisplayed(Boolean z) {
        installOfferDisplayed.set(z)
    }

    @JvmStatic
    Boolean shouldDisplayInstallOffer() {
        return !installOfferDisplayed.getAndSet(true)
    }

    private Unit showIncomingCallNotification(VoiceRinging voiceRinging, String str, ChatterID chatterID) {
        Intent intent
        Intent intent2 = Intent(this.context, GridConnectionService.class)
        intent2.setAction(ACTION_VOICE_REJECT)
        intent2.setData(voiceRinging.toUri())
        intent2.putExtra(INTENT_EXTRA_RINGING_MESSSAGE, voiceRinging.toBundle())
        Intent createIntent = ChatFragmentActivityFactory.getInstance().createIntent(this.context, ChatFragment.makeSelection(chatterID))
        createIntent.addFlags(536870912)
        ActivityUtils.setActiveAgentID(createIntent, chatterID.agentUUID)
        UserManager userManager2 = this.userManager.get()
        if (userManager2 == null || (intent = userManager2.getUnreadNotificationManager().captureNotify(UnreadNotificationInfo.create(userManager2.getUserID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 1, NotificationType.Private, UnreadNotificationInfo.UnreadMessageSource.create(chatterID, (String) null, (List<SLChatEvent>) null, 0), UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null)), createIntent)) == null) {
            intent = createIntent
        }
        Intent intent3 = Intent(this.context, GridConnectionService.class)
        intent3.setAction(ACTION_VOICE_ACCEPT)
        intent3.setData(voiceRinging.toUri())
        intent3.putExtra(INTENT_EXTRA_RINGING_MESSSAGE, voiceRinging.toBundle())
        intent3.putExtra("chatterID", chatterID.toBundle())
        intent3.putExtra(INTENT_EXTRA_OPEN_CHATTER, PendingIntent.getActivity(this.context, 0, intent, 0))
        Notification build = NotificationCompat.Builder(this.context).setSmallIcon(R.drawable.ic_incoming_voice_call).setContentTitle(str).setContentText(this.context.getString(R.string.incoming_voice_call_text)).setDefaults(-1).setPriority(1).setDeleteIntent(PendingIntent.getService(this.context, 0, intent2, 0)).setContentIntent(PendingIntent.getActivity(this.context, 0, intent, 0)).setAutoCancel(true).addAction(R.drawable.ic_voice_call_accept, this.context.getString(R.string.voice_call_accept), PendingIntent.getService(this.context, 0, intent3, 0)).addAction(R.drawable.ic_voice_call_reject, this.context.getString(R.string.voice_call_reject), PendingIntent.getService(this.context, 0, intent2, 0)).build()
        String str2 = voiceRinging.voiceChannelInfo.voiceChannelURI
        this.incomingCallNotificationTags.add(str2)
        ((NotificationManager) this.context.getSystemService("notification")).notify(str2, 1001, build)
    }

    public Unit acceptCall(Intent intent) {
        if (intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            VoiceRinging voiceRinging = VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE))
            Debug.Printf("Voice: accepting session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI)
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo))
        }
        Debug.Printf("Voice: cancelling notifications", Object[0])
        cancelNotifications((String) null)
        if (intent.hasExtra(INTENT_EXTRA_OPEN_CHATTER)) {
            Parcelable parcelableExtra = intent.getParcelableExtra(INTENT_EXTRA_OPEN_CHATTER)
            if (parcelableExtra instanceof PendingIntent) {
                try {
                    Debug.Printf("Voice: starting pending open chatter intent", Object[0])
                    ((PendingIntent) parcelableExtra).send()
                } catch (PendingIntent.CanceledException e) {
                    Debug.Warning(e)
                }
            }
        }
    }

    public Unit acceptVoiceCall(ChatterID chatterID) {
        VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo) this.voiceChannels.get(chatterID)
        if (voiceChannelInfo != null) {
            Debug.Printf("Voice: cancelling notification", Object[0])
            cancelNotifications((String) null)
            Debug.Printf("Voice: accepting voice call (chatterID %s)", chatterID)
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, VoiceAcceptCall((String) null, voiceChannelInfo))
        }
    }

    public Unit acceptVoiceCall(VoiceRinging voiceRinging) {
        Debug.Printf("Voice: cancelling notification", Object[0])
        cancelNotifications((String) null)
        Debug.Printf("Voice: accepting voice call (session handle %s)", voiceRinging.sessionHandle)
        sendMessage(VoicePluginMessageType.VoiceAcceptCall, VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo))
    }

    public Unit addChannel(ChatterID chatterID, VoiceChannelInfo voiceChannelInfo) {
        this.voiceChannels.forcePut(chatterID, voiceChannelInfo)
    }

    public Unit disconnect() {
        this.mainThreadHandler.post($Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA(this))
    }

    public Unit enableVoiceMic(Boolean z) {
        sendMessage(VoicePluginMessageType.VoiceEnableMic, VoiceEnableMic(z))
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701  reason: not valid java name */
    public /* synthetic */ Unit m904lambda$com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701(VoiceRinging voiceRinging, ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.ringingChatterNameRetriever) {
            String resolvedName = chatterNameRetriever.getResolvedName()
            if (!Strings.isNullOrEmpty(resolvedName)) {
                showIncomingCallNotification(voiceRinging, resolvedName, chatterNameRetriever.chatterID)
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898  reason: not valid java name */
    public /* synthetic */ Unit m905lambda$com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898() {
        Debug.Printf("LinkpointVoice: disconnecting from voice plugin", Object[0])
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null) {
            userManager2.setVoiceLoggedIn(false)
        }
        sendMessage(VoicePluginMessageType.VoiceLogout, VoiceLogout())
        this.context.unbindService(this)
    }

    public Unit onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Printf("LinkpointVoice: service connected", Object[0])
        this.toPluginMessenger = Messenger(iBinder)
        try {
            sendMessage(VoicePluginMessageType.VoiceInitialize, VoiceInitialize(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode))
        } catch (PackageManager.NameNotFoundException e) {
            Debug.Warning(e)
        }
    }

    public Unit onServiceDisconnected(ComponentName componentName) {
        Debug.Printf("LinkpointCloud: service disconnected", Object[0])
        UserManager userManager2 = this.userManager.get()
        if (userManager2 != null) {
            userManager2.setVoiceLoggedIn(false)
        }
    }

    public Unit rejectCall(Intent intent) {
        if (intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            VoiceRinging voiceRinging = VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE))
            Debug.Printf("Voice: requesting to reject session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI)
            sendMessage(VoicePluginMessageType.VoiceRejectCall, VoiceRejectCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo))
            cancelNotifications(voiceRinging.voiceChannelInfo.voiceChannelURI)
            return
        }
        cancelNotifications((String) null)
    }

    public Boolean sendMessage(VoicePluginMessageType voicePluginMessageType, VoicePluginMessage voicePluginMessage) {
        if (this.toPluginMessenger != null) {
            return VoicePluginMessenger.sendMessage(this.toPluginMessenger, voicePluginMessageType, voicePluginMessage, this.fromPluginMessenger)
        }
        return false
    }

    public Unit setVoiceAudioProperties(VoiceSetAudioProperties voiceSetAudioProperties) {
        sendMessage(VoicePluginMessageType.VoiceSetAudioProperties, voiceSetAudioProperties)
    }

    public Unit setVoiceLoginInfo(VoiceLoginInfo voiceLoginInfo2, UserManager userManager2) {
        this.userManager.set(userManager2)
        if (!Objects.equal(this.voiceLoginInfo.getAndSet(voiceLoginInfo2), voiceLoginInfo2) && this.voiceInitialized.get() && voiceLoginInfo2 != null) {
            sendMessage(VoicePluginMessageType.VoiceLogin, VoiceLogin(voiceLoginInfo2))
        }
    }

    public Unit terminateVoiceCall(ChatterID chatterID) {
        VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo) this.voiceChannels.get(chatterID)
        if (voiceChannelInfo != null) {
            sendMessage(VoicePluginMessageType.VoiceTerminateCall, VoiceTerminateCall(voiceChannelInfo))
        }
    }
}
