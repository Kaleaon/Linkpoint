package com.lumiyaviewer.lumiya.voiceintf;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.support.v7.app.NotificationCompat;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessenger;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAcceptCall;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceEnableMic;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitialize;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitializeReply;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLogin;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLoginStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLogout;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRejectCall;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceTerminateCall;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceBluetoothState;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class VoicePluginServiceConnection implements ServiceConnection {
    public static final String ACTION_VOICE_ACCEPT = "accept";
    public static final String ACTION_VOICE_REJECT = "reject";
    private static final int INCOMING_CALL_NOTIFICATION_ID = 1001;
    private static final String INTENT_EXTRA_CHATTER_ID = "chatterID";
    private static final String INTENT_EXTRA_OPEN_CHATTER = "openChatterIntent";
    private static final String INTENT_EXTRA_RINGING_MESSSAGE = "ringingMessage";
    private static final int REQUIRED_PLUGIN_VERSION = 3;
    private static final AtomicBoolean installOfferDisplayed = new AtomicBoolean(false);
    private final Context context;
    private final Handler fromPluginHandler = new Handler() {

        /* renamed from: -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues  reason: not valid java name */
        private static final /* synthetic */ int[] f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$voice$common$VoicePluginMessageType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues  reason: not valid java name */
        private static /* synthetic */ int[] m906getcomlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues() {
            if (f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues != null) {
                return f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues;
            }
            int[] iArr = new int[VoicePluginMessageType.values().length];
            try {
                iArr[VoicePluginMessageType.VoiceAcceptCall.ordinal()] = 6;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceAudioProperties.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceChannelClosed.ordinal()] = 7;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceChannelStatus.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceConnectChannel.ordinal()] = 8;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceEnableMic.ordinal()] = 9;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceInitialize.ordinal()] = 10;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceInitializeReply.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLogin.ordinal()] = 11;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLoginStatus.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceLogout.ordinal()] = 12;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceRejectCall.ordinal()] = 13;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceRinging.ordinal()] = 5;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceSet3DPosition.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceSetAudioProperties.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[VoicePluginMessageType.VoiceTerminateCall.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            f611comlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues = iArr;
            return iArr;
        }

        public void handleMessage(Message message) {
            if (message.what == 200 && (message.obj instanceof Bundle)) {
                Bundle bundle = (Bundle) message.obj;
                if (bundle.containsKey("message") && bundle.containsKey("messageType")) {
                    try {
                        switch (m906getcomlumiyaviewerlumiyavoicecommonVoicePluginMessageTypeSwitchesValues()[VoicePluginMessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                            case 1:
                                VoicePluginServiceConnection.this.onVoiceAudioProperties(new VoiceAudioProperties(bundle.getBundle("message")));
                                return;
                            case 2:
                                VoicePluginServiceConnection.this.onVoiceChannelStatus(new VoiceChannelStatus(bundle.getBundle("message")));
                                return;
                            case 3:
                                VoicePluginServiceConnection.this.onVoiceInitializeReply(new VoiceInitializeReply(bundle.getBundle("message")));
                                return;
                            case 4:
                                VoicePluginServiceConnection.this.onVoiceLoginStatus(new VoiceLoginStatus(bundle.getBundle("message")));
                                return;
                            case 5:
                                VoicePluginServiceConnection.this.onVoiceRinging(new VoiceRinging(bundle.getBundle("message")));
                                return;
                            default:
                                return;
                        }
                    } catch (Exception e) {
                        Debug.Warning(e);
                    }
                    Debug.Warning(e);
                }
            }
        }
    };
    private final Messenger fromPluginMessenger;
    private final Set<String> incomingCallNotificationTags = Collections.synchronizedSet(new HashSet());
    private final Handler mainThreadHandler = new Handler();
    @Nullable
    private ChatterNameRetriever ringingChatterNameRetriever = null;
    @Nullable
    private Messenger toPluginMessenger;
    private final AtomicReference<UserManager> userManager = new AtomicReference<>((Object) null);
    private final BiMap<ChatterID, VoiceChannelInfo> voiceChannels = Maps.synchronizedBiMap(HashBiMap.create());
    private final AtomicBoolean voiceInitialized = new AtomicBoolean(false);
    private final AtomicReference<VoiceLoginInfo> voiceLoginInfo = new AtomicReference<>((Object) null);

    public VoicePluginServiceConnection(Context context2) {
        this.context = context2;
        this.fromPluginMessenger = new Messenger(this.fromPluginHandler);
    }

    private void cancelNotifications(@Nullable String str) {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification");
        if (str != null) {
            notificationManager.cancel(str, 1001);
            this.incomingCallNotificationTags.remove(str);
            return;
        }
        for (String cancel : this.incomingCallNotificationTags) {
            notificationManager.cancel(cancel, 1001);
        }
        this.incomingCallNotificationTags.clear();
    }

    public static boolean checkPluginInstalled(Context context2) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
        List<ResolveInfo> queryIntentServices = context2.getPackageManager().queryIntentServices(intent, 0);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    public static boolean isPluginSupported() {
        String[] strArr;
        if (Build.VERSION.SDK_INT >= 21) {
            strArr = Build.SUPPORTED_ABIS;
        } else {
            strArr = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
        if (strArr == null) {
            return false;
        }
        for (String str : strArr) {
            if (str != null && (str.toLowerCase().contains("armeabi") || str.toLowerCase().contains("arm64"))) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void onVoiceAudioProperties(VoiceAudioProperties voiceAudioProperties) {
        VoiceBluetoothState voiceBluetoothState = null;
        Object[] objArr = new Object[1];
        if (voiceAudioProperties != null) {
            voiceBluetoothState = voiceAudioProperties.bluetoothState;
        }
        objArr[0] = voiceBluetoothState;
        Debug.Printf("Voice: voice audio properties received, bluetooth state %s", objArr);
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null) {
            userManager2.setVoiceAudioProperties(voiceAudioProperties);
        }
    }

    /* access modifiers changed from: private */
    public void onVoiceChannelStatus(VoiceChannelStatus voiceChannelStatus) {
        SLModules modules;
        if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            cancelNotifications(voiceChannelStatus.channelInfo.voiceChannelURI);
        }
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null) {
            ChatterID chatterID = (ChatterID) this.voiceChannels.inverse().get(voiceChannelStatus.channelInfo);
            if (chatterID != null) {
                userManager2.setVoiceChatInfo(chatterID, voiceChannelStatus.chatInfo);
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None && voiceChannelStatus.chatInfo.previousState == VoiceChatInfo.VoiceChatState.Ringing && (chatterID instanceof ChatterID.ChatterIDUser)) {
                    userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, new SLMissedVoiceCallEvent(new ChatMessageSourceUser(((ChatterID.ChatterIDUser) chatterID).getChatterUUID()), userManager2.getUserID(), LumiyaApp.getContext().getString(R.string.missed_voice_call)), true);
                }
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                    userManager2.setVoiceActiveChatter(chatterID);
                }
            }
            SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit();
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.voice.onVoiceChannelStatus(voiceChannelStatus);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onVoiceInitializeReply(VoiceInitializeReply voiceInitializeReply) {
        if (!voiceInitializeReply.appVersionOk) {
            UserManager userManager2 = this.userManager.get();
            if (userManager2 != null) {
                userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager2.getUserID()), new SLVoiceUpgradeEvent(userManager2.getUserID(), LumiyaApp.getContext().getString(R.string.app_upgrade_for_voice_needed), false, LicenseChecker.APP_STORE_URL), false);
            }
        } else if (voiceInitializeReply.pluginVersionCode < 3) {
            UserManager userManager3 = this.userManager.get();
            if (userManager3 != null) {
                userManager3.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager3.getUserID()), new SLVoiceUpgradeEvent(userManager3.getUserID(), LumiyaApp.getContext().getString(R.string.plugin_upgrade_for_voice_needed), false, LicenseChecker.VOICE_PLUGIN_URL), false);
            }
        } else if (voiceInitializeReply.errorMessage == null) {
            this.voiceInitialized.set(true);
            VoiceLoginInfo voiceLoginInfo2 = this.voiceLoginInfo.get();
            if (voiceLoginInfo2 != null) {
                sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceLoginInfo2));
            }
        } else {
            UserManager userManager4 = this.userManager.get();
            if (userManager4 != null) {
                userManager4.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager4.getUserID()), new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), userManager4.getUserID(), LumiyaApp.getContext().getString(R.string.voice_plugin_error_format, new Object[]{voiceInitializeReply.errorMessage})), false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onVoiceLoginStatus(VoiceLoginStatus voiceLoginStatus) {
        SLModules modules;
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null) {
            SLAgentCircuit activeAgentCircuit = userManager2.getActiveAgentCircuit();
            if (!(activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null)) {
                modules.voice.onVoiceLoginStatus(this, voiceLoginStatus);
            }
            userManager2.setVoiceLoggedIn(voiceLoginStatus.loggedIn);
        }
    }

    /* access modifiers changed from: private */
    public void onVoiceRinging(VoiceRinging voiceRinging) {
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null && voiceRinging != null && voiceRinging.agentUUID != null) {
            ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(userManager2.getUserID(), voiceRinging.agentUUID);
            this.voiceChannels.forcePut(userChatterID, voiceRinging.voiceChannelInfo);
            this.ringingChatterNameRetriever = new ChatterNameRetriever(userChatterID, new ChatterNameRetriever.OnChatterNameUpdated(this, voiceRinging) {

                /* renamed from: -$f0 */
                private final /* synthetic */ Object f609$f0;

                /* renamed from: -$f1 */
                private final /* synthetic */ Object f610$f1;

                private final /* synthetic */ void $m$0(
/*
Method generation error in method: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, dex: classes.dex
                jadx.core.utils.exceptions.JadxRuntimeException: Method args not loaded: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA.1.$m$0(com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever):void, class status: UNLOADED
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

            }, UIThreadExecutor.getSerialInstance(), false);
            this.ringingChatterNameRetriever.subscribe();
        }
    }

    public static void setInstallOfferDisplayed(boolean z) {
        installOfferDisplayed.set(z);
    }

    public static boolean shouldDisplayInstallOffer() {
        return !installOfferDisplayed.getAndSet(true);
    }

    private void showIncomingCallNotification(VoiceRinging voiceRinging, String str, ChatterID chatterID) {
        Intent intent;
        Intent intent2 = new Intent(this.context, GridConnectionService.class);
        intent2.setAction(ACTION_VOICE_REJECT);
        intent2.setData(voiceRinging.toUri());
        intent2.putExtra(INTENT_EXTRA_RINGING_MESSSAGE, voiceRinging.toBundle());
        Intent createIntent = ChatFragmentActivityFactory.getInstance().createIntent(this.context, ChatFragment.makeSelection(chatterID));
        createIntent.addFlags(536870912);
        ActivityUtils.setActiveAgentID(createIntent, chatterID.agentUUID);
        UserManager userManager2 = this.userManager.get();
        if (userManager2 == null || (intent = userManager2.getUnreadNotificationManager().captureNotify(UnreadNotificationInfo.create(userManager2.getUserID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 1, NotificationType.Private, UnreadNotificationInfo.UnreadMessageSource.create(chatterID, (String) null, (List<SLChatEvent>) null, 0), UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null)), createIntent)) == null) {
            intent = createIntent;
        }
        Intent intent3 = new Intent(this.context, GridConnectionService.class);
        intent3.setAction(ACTION_VOICE_ACCEPT);
        intent3.setData(voiceRinging.toUri());
        intent3.putExtra(INTENT_EXTRA_RINGING_MESSSAGE, voiceRinging.toBundle());
        intent3.putExtra("chatterID", chatterID.toBundle());
        intent3.putExtra(INTENT_EXTRA_OPEN_CHATTER, PendingIntent.getActivity(this.context, 0, intent, 0));
        Notification build = new NotificationCompat.Builder(this.context).setSmallIcon(R.drawable.ic_incoming_voice_call).setContentTitle(str).setContentText(this.context.getString(R.string.incoming_voice_call_text)).setDefaults(-1).setPriority(1).setDeleteIntent(PendingIntent.getService(this.context, 0, intent2, 0)).setContentIntent(PendingIntent.getActivity(this.context, 0, intent, 0)).setAutoCancel(true).addAction(R.drawable.ic_voice_call_accept, this.context.getString(R.string.voice_call_accept), PendingIntent.getService(this.context, 0, intent3, 0)).addAction(R.drawable.ic_voice_call_reject, this.context.getString(R.string.voice_call_reject), PendingIntent.getService(this.context, 0, intent2, 0)).build();
        String str2 = voiceRinging.voiceChannelInfo.voiceChannelURI;
        this.incomingCallNotificationTags.add(str2);
        ((NotificationManager) this.context.getSystemService("notification")).notify(str2, 1001, build);
    }

    public void acceptCall(Intent intent) {
        if (intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE));
            Debug.Printf("Voice: accepting session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
        }
        Debug.Printf("Voice: cancelling notifications", new Object[0]);
        cancelNotifications((String) null);
        if (intent.hasExtra(INTENT_EXTRA_OPEN_CHATTER)) {
            Parcelable parcelableExtra = intent.getParcelableExtra(INTENT_EXTRA_OPEN_CHATTER);
            if (parcelableExtra instanceof PendingIntent) {
                try {
                    Debug.Printf("Voice: starting pending open chatter intent", new Object[0]);
                    ((PendingIntent) parcelableExtra).send();
                } catch (PendingIntent.CanceledException e) {
                    Debug.Warning(e);
                }
            }
        }
    }

    public void acceptVoiceCall(ChatterID chatterID) {
        VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo) this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            Debug.Printf("Voice: cancelling notification", new Object[0]);
            cancelNotifications((String) null);
            Debug.Printf("Voice: accepting voice call (chatterID %s)", chatterID);
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall((String) null, voiceChannelInfo));
        }
    }

    public void acceptVoiceCall(VoiceRinging voiceRinging) {
        Debug.Printf("Voice: cancelling notification", new Object[0]);
        cancelNotifications((String) null);
        Debug.Printf("Voice: accepting voice call (session handle %s)", voiceRinging.sessionHandle);
        sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
    }

    public void addChannel(ChatterID chatterID, VoiceChannelInfo voiceChannelInfo) {
        this.voiceChannels.forcePut(chatterID, voiceChannelInfo);
    }

    public void disconnect() {
        this.mainThreadHandler.post(new $Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA(this));
    }

    public void enableVoiceMic(boolean z) {
        sendMessage(VoicePluginMessageType.VoiceEnableMic, new VoiceEnableMic(z));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701  reason: not valid java name */
    public /* synthetic */ void m904lambda$com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701(VoiceRinging voiceRinging, ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.ringingChatterNameRetriever) {
            String resolvedName = chatterNameRetriever.getResolvedName();
            if (!Strings.isNullOrEmpty(resolvedName)) {
                showIncomingCallNotification(voiceRinging, resolvedName, chatterNameRetriever.chatterID);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898  reason: not valid java name */
    public /* synthetic */ void m905lambda$com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898() {
        Debug.Printf("LumiyaVoice: disconnecting from voice plugin", new Object[0]);
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null) {
            userManager2.setVoiceLoggedIn(false);
        }
        sendMessage(VoicePluginMessageType.VoiceLogout, new VoiceLogout());
        this.context.unbindService(this);
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Printf("LumiyaVoice: service connected", new Object[0]);
        this.toPluginMessenger = new Messenger(iBinder);
        try {
            sendMessage(VoicePluginMessageType.VoiceInitialize, new VoiceInitialize(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            Debug.Warning(e);
        }
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
        UserManager userManager2 = this.userManager.get();
        if (userManager2 != null) {
            userManager2.setVoiceLoggedIn(false);
        }
    }

    public void rejectCall(Intent intent) {
        if (intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE));
            Debug.Printf("Voice: requesting to reject session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
            sendMessage(VoicePluginMessageType.VoiceRejectCall, new VoiceRejectCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
            cancelNotifications(voiceRinging.voiceChannelInfo.voiceChannelURI);
            return;
        }
        cancelNotifications((String) null);
    }

    public boolean sendMessage(VoicePluginMessageType voicePluginMessageType, VoicePluginMessage voicePluginMessage) {
        if (this.toPluginMessenger != null) {
            return VoicePluginMessenger.sendMessage(this.toPluginMessenger, voicePluginMessageType, voicePluginMessage, this.fromPluginMessenger);
        }
        return false;
    }

    public void setVoiceAudioProperties(VoiceSetAudioProperties voiceSetAudioProperties) {
        sendMessage(VoicePluginMessageType.VoiceSetAudioProperties, voiceSetAudioProperties);
    }

    public void setVoiceLoginInfo(VoiceLoginInfo voiceLoginInfo2, UserManager userManager2) {
        this.userManager.set(userManager2);
        if (!Objects.equal(this.voiceLoginInfo.getAndSet(voiceLoginInfo2), voiceLoginInfo2) && this.voiceInitialized.get() && voiceLoginInfo2 != null) {
            sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceLoginInfo2));
        }
    }

    public void terminateVoiceCall(ChatterID chatterID) {
        VoiceChannelInfo voiceChannelInfo = (VoiceChannelInfo) this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            sendMessage(VoicePluginMessageType.VoiceTerminateCall, new VoiceTerminateCall(voiceChannelInfo));
        }
    }
}
