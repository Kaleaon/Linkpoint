package com.lumiyaviewer.lumiya.voiceintf;

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
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
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
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
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

    @Nullable
    private Messenger toPluginMessenger;
    private final AtomicReference<UserManager> userManager = new AtomicReference<>(null);
    private final Handler mainThreadHandler = new Handler();
    private final AtomicBoolean voiceInitialized = new AtomicBoolean(false);
    private final AtomicReference<VoiceLoginInfo> voiceLoginInfo = new AtomicReference<>(null);
    private final BiMap<ChatterID, VoiceChannelInfo> voiceChannels = Maps.synchronizedBiMap(HashBiMap.create());
    private final Set<String> incomingCallNotificationTags = Collections.synchronizedSet(new HashSet());

    @Nullable
    private ChatterNameRetriever ringingChatterNameRetriever = null;
    private final Handler fromPluginHandler = new Handler() { // from class: com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection.1

        /* renamed from: -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues, reason: not valid java name */
        private static final /* synthetic */ int[] f611x5636931c = null;
        final /* synthetic */ int[] $SWITCH_TABLE$com$lumiyaviewer$lumiya$voice$common$VoicePluginMessageType;

        /* renamed from: -getcom-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues, reason: not valid java name */
        private static /* synthetic */ int[] m916xcae206c0() {
            if (f611x5636931c != null) {
                return f611x5636931c;
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
            f611x5636931c = iArr;
            return iArr;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 200 && (message.obj instanceof Bundle)) {
                Bundle bundle = (Bundle) message.obj;
                if (bundle.containsKey("message") && bundle.containsKey("messageType")) {
                    try {
                        switch (m916xcae206c0()[VoicePluginMessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                            case 1:
                                VoicePluginServiceConnection.this.onVoiceAudioProperties(new VoiceAudioProperties(bundle.getBundle("message")));
                                break;
                            case 2:
                                VoicePluginServiceConnection.this.onVoiceChannelStatus(new VoiceChannelStatus(bundle.getBundle("message")));
                                break;
                            case 3:
                                VoicePluginServiceConnection.this.onVoiceInitializeReply(new VoiceInitializeReply(bundle.getBundle("message")));
                                break;
                            case 4:
                                VoicePluginServiceConnection.this.onVoiceLoginStatus(new VoiceLoginStatus(bundle.getBundle("message")));
                                break;
                            case 5:
                                VoicePluginServiceConnection.this.onVoiceRinging(new VoiceRinging(bundle.getBundle("message")));
                                break;
                        }
                    } catch (Exception e) {
                        Debug.Warning(e);
                    }
                }
            }
        }
    };
    private final Messenger fromPluginMessenger = new Messenger(this.fromPluginHandler);

    public VoicePluginServiceConnection(Context context) {
        this.context = context;
    }

    private void cancelNotifications(@Nullable String str) {
        NotificationManager notificationManager = (NotificationManager) this.context.getSystemService("notification");
        if (str != null) {
            notificationManager.cancel(str, 1001);
            this.incomingCallNotificationTags.remove(str);
        } else {
            Iterator<T> it = this.incomingCallNotificationTags.iterator();
            while (it.hasNext()) {
                notificationManager.cancel((String) it.next(), 1001);
            }
            this.incomingCallNotificationTags.clear();
        }
    }

    public static boolean checkPluginInstalled(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
        List<ResolveInfo> queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    public static boolean isPluginSupported() {
        String[] strArr = Build.VERSION.SDK_INT >= 21 ? Build.SUPPORTED_ABIS : new String[]{Build.CPU_ABI, Build.CPU_ABI2};
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

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceAudioProperties(VoiceAudioProperties voiceAudioProperties) {
        Object[] objArr = new Object[1];
        objArr[0] = voiceAudioProperties != null ? voiceAudioProperties.bluetoothState : null;
        Debug.Printf("Voice: voice audio properties received, bluetooth state %s", objArr);
        UserManager userManager = this.userManager.get();
        if (userManager != null) {
            userManager.setVoiceAudioProperties(voiceAudioProperties);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceChannelStatus(VoiceChannelStatus voiceChannelStatus) {
        SLModules modules;
        if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            cancelNotifications(voiceChannelStatus.channelInfo.voiceChannelURI);
        }
        UserManager userManager = this.userManager.get();
        if (userManager != null) {
            ChatterID chatterID = this.voiceChannels.inverse().get(voiceChannelStatus.channelInfo);
            if (chatterID != null) {
                userManager.setVoiceChatInfo(chatterID, voiceChannelStatus.chatInfo);
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None && voiceChannelStatus.chatInfo.previousState == VoiceChatInfo.VoiceChatState.Ringing && (chatterID instanceof ChatterID.ChatterIDUser)) {
                    userManager.getChatterList().getActiveChattersManager().HandleChatEvent(chatterID, new SLMissedVoiceCallEvent(new ChatMessageSourceUser(((ChatterID.ChatterIDUser) chatterID).getChatterUUID()), userManager.getUserID(), LumiyaApp.getContext().getString(R.string.missed_voice_call)), true);
                }
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                    userManager.setVoiceActiveChatter(chatterID);
                }
            }
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit == null || (modules = activeAgentCircuit.getModules()) == null) {
                return;
            }
            modules.voice.onVoiceChannelStatus(voiceChannelStatus);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceInitializeReply(VoiceInitializeReply voiceInitializeReply) {
        if (!voiceInitializeReply.appVersionOk) {
            UserManager userManager = this.userManager.get();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager.getUserID()), new SLVoiceUpgradeEvent(userManager.getUserID(), LumiyaApp.getContext().getString(R.string.app_upgrade_for_voice_needed), false, LicenseChecker.APP_STORE_URL), false);
                return;
            }
            return;
        }
        if (voiceInitializeReply.pluginVersionCode < 3) {
            UserManager userManager2 = this.userManager.get();
            if (userManager2 != null) {
                userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager2.getUserID()), new SLVoiceUpgradeEvent(userManager2.getUserID(), LumiyaApp.getContext().getString(R.string.plugin_upgrade_for_voice_needed), false, LicenseChecker.VOICE_PLUGIN_URL), false);
                return;
            }
            return;
        }
        if (voiceInitializeReply.errorMessage != null) {
            UserManager userManager3 = this.userManager.get();
            if (userManager3 != null) {
                userManager3.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager3.getUserID()), new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), userManager3.getUserID(), LumiyaApp.getContext().getString(R.string.voice_plugin_error_format, voiceInitializeReply.errorMessage)), false);
                return;
            }
            return;
        }
        this.voiceInitialized.set(true);
        VoiceLoginInfo voiceLoginInfo = this.voiceLoginInfo.get();
        if (voiceLoginInfo != null) {
            sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceLoginInfo));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceLoginStatus(VoiceLoginStatus voiceLoginStatus) {
        SLModules modules;
        UserManager userManager = this.userManager.get();
        if (userManager != null) {
            SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
                modules.voice.onVoiceLoginStatus(this, voiceLoginStatus);
            }
            userManager.setVoiceLoggedIn(voiceLoginStatus.loggedIn);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceRinging(final VoiceRinging voiceRinging) {
        UserManager userManager = this.userManager.get();
        if (userManager == null || voiceRinging == null || voiceRinging.agentUUID == null) {
            return;
        }
        ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(userManager.getUserID(), voiceRinging.agentUUID);
        this.voiceChannels.forcePut(userChatterID, voiceRinging.voiceChannelInfo);
        this.ringingChatterNameRetriever = new ChatterNameRetriever(userChatterID, new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA.1
            private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                ((VoicePluginServiceConnection) this).m914x2afa1cbb((VoiceRinging) voiceRinging, chatterNameRetriever);
            }

            @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
            public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                $m$0(chatterNameRetriever);
            }
        }, UIThreadExecutor.getSerialInstance(), false);
        this.ringingChatterNameRetriever.subscribe();
    }

    public static void setInstallOfferDisplayed(boolean z) {
        installOfferDisplayed.set(z);
    }

    public static boolean shouldDisplayInstallOffer() {
        return !installOfferDisplayed.getAndSet(true);
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x0061, code lost:
    
        if (r0 != null) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showIncomingCallNotification(com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging r12, java.lang.String r13, com.lumiyaviewer.lumiya.slproto.users.ChatterID r14) {
        /*
            Method dump skipped, instructions count: 294
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection.showIncomingCallNotification(com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging, java.lang.String, com.lumiyaviewer.lumiya.slproto.users.ChatterID):void");
    }

    public void acceptCall(Intent intent) {
        if (intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE));
            Debug.Printf("Voice: accepting session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
        }
        Debug.Printf("Voice: cancelling notifications", new Object[0]);
        cancelNotifications(null);
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
        VoiceChannelInfo voiceChannelInfo = this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            Debug.Printf("Voice: cancelling notification", new Object[0]);
            cancelNotifications(null);
            Debug.Printf("Voice: accepting voice call (chatterID %s)", chatterID);
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(null, voiceChannelInfo));
        }
    }

    public void acceptVoiceCall(VoiceRinging voiceRinging) {
        Debug.Printf("Voice: cancelling notification", new Object[0]);
        cancelNotifications(null);
        Debug.Printf("Voice: accepting voice call (session handle %s)", voiceRinging.sessionHandle);
        sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
    }

    public void addChannel(ChatterID chatterID, VoiceChannelInfo voiceChannelInfo) {
        this.voiceChannels.forcePut(chatterID, voiceChannelInfo);
    }

    public void disconnect() {
        this.mainThreadHandler.post(new Runnable() { // from class: com.lumiyaviewer.lumiya.voiceintf.-$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA
            private final /* synthetic */ void $m$0() {
                ((VoicePluginServiceConnection) this).m915x2afbf316();
            }

            @Override // java.lang.Runnable
            public final void run() {
                $m$0();
            }
        });
    }

    public void enableVoiceMic(boolean z) {
        sendMessage(VoicePluginMessageType.VoiceEnableMic, new VoiceEnableMic(z));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701, reason: not valid java name */
    public /* synthetic */ void m914x2afa1cbb(VoiceRinging voiceRinging, ChatterNameRetriever chatterNameRetriever) {
        if (chatterNameRetriever == this.ringingChatterNameRetriever) {
            String resolvedName = chatterNameRetriever.getResolvedName();
            if (Strings.isNullOrEmpty(resolvedName)) {
                return;
            }
            showIncomingCallNotification(voiceRinging, resolvedName, chatterNameRetriever.chatterID);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898, reason: not valid java name */
    public /* synthetic */ void m915x2afbf316() {
        Debug.Printf("LumiyaVoice: disconnecting from voice plugin", new Object[0]);
        UserManager userManager = this.userManager.get();
        if (userManager != null) {
            userManager.setVoiceLoggedIn(false);
        }
        sendMessage(VoicePluginMessageType.VoiceLogout, new VoiceLogout());
        this.context.unbindService(this);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Printf("LumiyaVoice: service connected", new Object[0]);
        this.toPluginMessenger = new Messenger(iBinder);
        try {
            sendMessage(VoicePluginMessageType.VoiceInitialize, new VoiceInitialize(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            Debug.Warning(e);
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
        UserManager userManager = this.userManager.get();
        if (userManager != null) {
            userManager.setVoiceLoggedIn(false);
        }
    }

    public void rejectCall(Intent intent) {
        if (!intent.hasExtra(INTENT_EXTRA_RINGING_MESSSAGE)) {
            cancelNotifications(null);
            return;
        }
        VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra(INTENT_EXTRA_RINGING_MESSSAGE));
        Debug.Printf("Voice: requesting to reject session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
        sendMessage(VoicePluginMessageType.VoiceRejectCall, new VoiceRejectCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
        cancelNotifications(voiceRinging.voiceChannelInfo.voiceChannelURI);
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

    public void setVoiceLoginInfo(VoiceLoginInfo voiceLoginInfo, UserManager userManager) {
        this.userManager.set(userManager);
        if (Objects.equal(this.voiceLoginInfo.getAndSet(voiceLoginInfo), voiceLoginInfo) || !this.voiceInitialized.get() || voiceLoginInfo == null) {
            return;
        }
        sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceLoginInfo));
    }

    public void terminateVoiceCall(ChatterID chatterID) {
        VoiceChannelInfo voiceChannelInfo = this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            sendMessage(VoicePluginMessageType.VoiceTerminateCall, new VoiceTerminateCall(voiceChannelInfo));
        }
    }
}
