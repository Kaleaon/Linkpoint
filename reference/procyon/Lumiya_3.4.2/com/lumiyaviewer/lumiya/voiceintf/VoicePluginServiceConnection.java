// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voiceintf;

import com.lumiyaviewer.lumiya.voice.common.messages.VoiceTerminateCall;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceSetAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessenger;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRejectCall;
import android.content.pm.PackageManager$NameNotFoundException;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitialize;
import android.os.IBinder;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLogout;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceEnableMic;
import android.app.PendingIntent$CanceledException;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAcceptCall;
import android.app.Notification;
import android.support.v7.app.NotificationCompat;
import android.os.Parcelable;
import android.app.PendingIntent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLogin;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import android.os.Build;
import android.os.Build$VERSION;
import java.util.List;
import android.content.ComponentName;
import android.content.Intent;
import java.util.Iterator;
import android.app.NotificationManager;
import com.lumiyaviewer.lumiya.Debug;
import android.os.Bundle;
import android.os.Message;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessageType;
import java.util.Collections;
import java.util.HashSet;
import com.google.common.collect.Maps;
import com.google.common.collect.HashBiMap;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceLoginStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceInitializeReply;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceChannelStatus;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceAudioProperties;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.google.common.collect.BiMap;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.util.Set;
import android.os.Messenger;
import android.os.Handler;
import android.content.Context;
import java.util.concurrent.atomic.AtomicBoolean;
import android.content.ServiceConnection;

public class VoicePluginServiceConnection implements ServiceConnection
{
    public static final String ACTION_VOICE_ACCEPT = "accept";
    public static final String ACTION_VOICE_REJECT = "reject";
    private static final int INCOMING_CALL_NOTIFICATION_ID = 1001;
    private static final String INTENT_EXTRA_CHATTER_ID = "chatterID";
    private static final String INTENT_EXTRA_OPEN_CHATTER = "openChatterIntent";
    private static final String INTENT_EXTRA_RINGING_MESSSAGE = "ringingMessage";
    private static final int REQUIRED_PLUGIN_VERSION = 3;
    private static final AtomicBoolean installOfferDisplayed;
    private final Context context;
    private final Handler fromPluginHandler;
    private final Messenger fromPluginMessenger;
    private final Set<String> incomingCallNotificationTags;
    private final Handler mainThreadHandler;
    @Nullable
    private ChatterNameRetriever ringingChatterNameRetriever;
    @Nullable
    private Messenger toPluginMessenger;
    private final AtomicReference<UserManager> userManager;
    private final BiMap<ChatterID, VoiceChannelInfo> voiceChannels;
    private final AtomicBoolean voiceInitialized;
    private final AtomicReference<VoiceLoginInfo> voiceLoginInfo;
    
    static {
        installOfferDisplayed = new AtomicBoolean(false);
    }
    
    public VoicePluginServiceConnection(final Context context) {
        this.userManager = new AtomicReference<UserManager>(null);
        this.mainThreadHandler = new Handler();
        this.voiceInitialized = new AtomicBoolean(false);
        this.voiceLoginInfo = new AtomicReference<VoiceLoginInfo>(null);
        this.voiceChannels = Maps.synchronizedBiMap((BiMap<ChatterID, VoiceChannelInfo>)HashBiMap.create());
        this.incomingCallNotificationTags = Collections.synchronizedSet(new HashSet<String>());
        this.ringingChatterNameRetriever = null;
        this.fromPluginHandler = new Handler() {
            private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues() {
                if (VoicePluginServiceConnection$1.-com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues != null) {
                    return VoicePluginServiceConnection$1.-com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues;
                }
                int[] -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues = new int[VoicePluginMessageType.values().length];
                while (true) {
                    try {
                        -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceAcceptCall.ordinal()] = 6;
                        try {
                            -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceAudioProperties.ordinal()] = 1;
                            try {
                                -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceChannelClosed.ordinal()] = 7;
                                try {
                                    -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceChannelStatus.ordinal()] = 2;
                                    try {
                                        -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceConnectChannel.ordinal()] = 8;
                                        try {
                                            -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceEnableMic.ordinal()] = 9;
                                            try {
                                                -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceInitialize.ordinal()] = 10;
                                                try {
                                                    -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceInitializeReply.ordinal()] = 3;
                                                    try {
                                                        -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceLogin.ordinal()] = 11;
                                                        try {
                                                            -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceLoginStatus.ordinal()] = 4;
                                                            try {
                                                                -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceLogout.ordinal()] = 12;
                                                                try {
                                                                    -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceRejectCall.ordinal()] = 13;
                                                                    try {
                                                                        -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceRinging.ordinal()] = 5;
                                                                        try {
                                                                            -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceSet3DPosition.ordinal()] = 14;
                                                                            try {
                                                                                -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceSetAudioProperties.ordinal()] = 15;
                                                                                try {
                                                                                    -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues[VoicePluginMessageType.VoiceTerminateCall.ordinal()] = 16;
                                                                                    return -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues = -com-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues;
                                                                                }
                                                                                catch (final NoSuchFieldError noSuchFieldError) {}
                                                                            }
                                                                            catch (final NoSuchFieldError noSuchFieldError2) {}
                                                                        }
                                                                        catch (final NoSuchFieldError noSuchFieldError3) {}
                                                                    }
                                                                    catch (final NoSuchFieldError noSuchFieldError4) {}
                                                                }
                                                                catch (final NoSuchFieldError noSuchFieldError5) {}
                                                            }
                                                            catch (final NoSuchFieldError noSuchFieldError6) {}
                                                        }
                                                        catch (final NoSuchFieldError noSuchFieldError7) {}
                                                    }
                                                    catch (final NoSuchFieldError noSuchFieldError8) {}
                                                }
                                                catch (final NoSuchFieldError noSuchFieldError9) {}
                                            }
                                            catch (final NoSuchFieldError noSuchFieldError10) {}
                                        }
                                        catch (final NoSuchFieldError noSuchFieldError11) {}
                                    }
                                    catch (final NoSuchFieldError noSuchFieldError12) {}
                                }
                                catch (final NoSuchFieldError noSuchFieldError13) {}
                            }
                            catch (final NoSuchFieldError noSuchFieldError14) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError15) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError16) {
                        continue;
                    }
                    break;
                }
            }
            
            public void handleMessage(final Message message) {
                if (message.what != 200 || !(message.obj instanceof Bundle)) {
                    return;
                }
                final Bundle bundle = (Bundle)message.obj;
                if (!bundle.containsKey("message") || !bundle.containsKey("messageType")) {
                    return;
                }
                while (true) {
                    Label_0217: {
                        Label_0190: {
                            Label_0163: {
                                Label_0136: {
                                    try {
                                        switch (-getcom-lumiyaviewer-lumiya-voice-common-VoicePluginMessageTypeSwitchesValues()[VoicePluginMessageType.valueOf(bundle.getString("messageType")).ordinal()]) {
                                            case 3: {
                                                VoicePluginServiceConnection.this.onVoiceInitializeReply(new VoiceInitializeReply(bundle.getBundle("message")));
                                                break;
                                            }
                                            case 4: {
                                                break Label_0136;
                                            }
                                            case 2: {
                                                break Label_0163;
                                            }
                                            case 5: {
                                                break Label_0190;
                                            }
                                            case 1: {
                                                break Label_0217;
                                            }
                                        }
                                        return;
                                    }
                                    catch (final Exception ex) {
                                        Debug.Warning(ex);
                                        return;
                                    }
                                    return;
                                }
                                VoicePluginServiceConnection.this.onVoiceLoginStatus(new VoiceLoginStatus(bundle.getBundle("message")));
                                return;
                            }
                            VoicePluginServiceConnection.this.onVoiceChannelStatus(new VoiceChannelStatus(bundle.getBundle("message")));
                            return;
                        }
                        VoicePluginServiceConnection.this.onVoiceRinging(new VoiceRinging(bundle.getBundle("message")));
                        return;
                    }
                    VoicePluginServiceConnection.this.onVoiceAudioProperties(new VoiceAudioProperties(bundle.getBundle("message")));
                }
            }
        };
        this.context = context;
        this.fromPluginMessenger = new Messenger(this.fromPluginHandler);
    }
    
    private void cancelNotifications(@Nullable final String s) {
        final NotificationManager notificationManager = (NotificationManager)this.context.getSystemService("notification");
        if (s != null) {
            notificationManager.cancel(s, 1001);
            this.incomingCallNotificationTags.remove(s);
        }
        else {
            final Iterator<Object> iterator = this.incomingCallNotificationTags.iterator();
            while (iterator.hasNext()) {
                notificationManager.cancel((String)iterator.next(), 1001);
            }
            this.incomingCallNotificationTags.clear();
        }
    }
    
    public static boolean checkPluginInstalled(final Context context) {
        final boolean b = false;
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
        final List queryIntentServices = context.getPackageManager().queryIntentServices(intent, 0);
        boolean b2 = b;
        if (queryIntentServices != null) {
            b2 = b;
            if (queryIntentServices.size() > 0) {
                b2 = true;
            }
        }
        return b2;
    }
    
    public static boolean isPluginSupported() {
        final boolean b = false;
        String[] supported_ABIS;
        if (Build$VERSION.SDK_INT >= 21) {
            supported_ABIS = Build.SUPPORTED_ABIS;
        }
        else {
            supported_ABIS = new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
        }
        boolean b2 = b;
        if (supported_ABIS != null) {
            int n = 0;
            while (true) {
                b2 = b;
                if (n >= supported_ABIS.length) {
                    break;
                }
                final String s = supported_ABIS[n];
                if (s != null && (s.toLowerCase().contains("armeabi") || s.toLowerCase().contains("arm64"))) {
                    b2 = true;
                    break;
                }
                ++n;
            }
        }
        return b2;
    }
    
    private void onVoiceAudioProperties(final VoiceAudioProperties voiceAudioProperties) {
        Object bluetoothState = null;
        if (voiceAudioProperties != null) {
            bluetoothState = voiceAudioProperties.bluetoothState;
        }
        Debug.Printf("Voice: voice audio properties received, bluetooth state %s", bluetoothState);
        final UserManager userManager = this.userManager.get();
        if (userManager != null) {
            userManager.setVoiceAudioProperties(voiceAudioProperties);
        }
    }
    
    private void onVoiceChannelStatus(final VoiceChannelStatus voiceChannelStatus) {
        if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None) {
            this.cancelNotifications(voiceChannelStatus.channelInfo.voiceChannelURI);
        }
        final UserManager userManager = this.userManager.get();
        if (userManager != null) {
            final ChatterID voiceActiveChatter = this.voiceChannels.inverse().get(voiceChannelStatus.channelInfo);
            if (voiceActiveChatter != null) {
                userManager.setVoiceChatInfo(voiceActiveChatter, voiceChannelStatus.chatInfo);
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.None && voiceChannelStatus.chatInfo.previousState == VoiceChatInfo.VoiceChatState.Ringing && voiceActiveChatter instanceof ChatterID.ChatterIDUser) {
                    userManager.getChatterList().getActiveChattersManager().HandleChatEvent(voiceActiveChatter, new SLMissedVoiceCallEvent(new ChatMessageSourceUser(((ChatterID.ChatterIDUser)voiceActiveChatter).getChatterUUID()), userManager.getUserID(), LumiyaApp.getContext().getString(2131296698)), true);
                }
                if (voiceChannelStatus.chatInfo.state == VoiceChatInfo.VoiceChatState.Active) {
                    userManager.setVoiceActiveChatter(voiceActiveChatter);
                }
            }
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.voice.onVoiceChannelStatus(voiceChannelStatus);
                }
            }
        }
    }
    
    private void onVoiceInitializeReply(final VoiceInitializeReply voiceInitializeReply) {
        if (!voiceInitializeReply.appVersionOk) {
            final UserManager userManager = this.userManager.get();
            if (userManager != null) {
                userManager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager.getUserID()), new SLVoiceUpgradeEvent(userManager.getUserID(), LumiyaApp.getContext().getString(2131296325), false, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"), false);
            }
        }
        else if (voiceInitializeReply.pluginVersionCode < 3) {
            final UserManager userManager2 = this.userManager.get();
            if (userManager2 != null) {
                userManager2.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager2.getUserID()), new SLVoiceUpgradeEvent(userManager2.getUserID(), LumiyaApp.getContext().getString(2131296890), false, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"), false);
            }
        }
        else if (voiceInitializeReply.errorMessage == null) {
            this.voiceInitialized.set(true);
            final VoiceLoginInfo voiceLoginInfo = this.voiceLoginInfo.get();
            if (voiceLoginInfo != null) {
                this.sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceLoginInfo));
            }
        }
        else {
            final UserManager userManager3 = this.userManager.get();
            if (userManager3 != null) {
                userManager3.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager3.getUserID()), new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), userManager3.getUserID(), LumiyaApp.getContext().getString(2131297151, new Object[] { voiceInitializeReply.errorMessage })), false);
            }
        }
    }
    
    private void onVoiceLoginStatus(final VoiceLoginStatus voiceLoginStatus) {
        final UserManager userManager = this.userManager.get();
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                final SLModules modules = activeAgentCircuit.getModules();
                if (modules != null) {
                    modules.voice.onVoiceLoginStatus(this, voiceLoginStatus);
                }
            }
            userManager.setVoiceLoggedIn(voiceLoginStatus.loggedIn);
        }
    }
    
    private void onVoiceRinging(final VoiceRinging voiceRinging) {
        final UserManager userManager = this.userManager.get();
        if (userManager != null && voiceRinging != null && voiceRinging.agentUUID != null) {
            final ChatterID.ChatterIDUser userChatterID = ChatterID.getUserChatterID(userManager.getUserID(), voiceRinging.agentUUID);
            this.voiceChannels.forcePut(userChatterID, voiceRinging.voiceChannelInfo);
            (this.ringingChatterNameRetriever = new ChatterNameRetriever(userChatterID, (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA$1(this, voiceRinging), UIThreadExecutor.getSerialInstance(), false)).subscribe();
        }
    }
    
    public static void setInstallOfferDisplayed(final boolean newValue) {
        VoicePluginServiceConnection.installOfferDisplayed.set(newValue);
    }
    
    public static boolean shouldDisplayInstallOffer() {
        return VoicePluginServiceConnection.installOfferDisplayed.getAndSet(true) ^ true;
    }
    
    private void showIncomingCallNotification(final VoiceRinging voiceRinging, final String contentTitle, final ChatterID chatterID) {
        final Intent intent = new Intent(this.context, (Class)GridConnectionService.class);
        intent.setAction("reject");
        intent.setData(voiceRinging.toUri());
        intent.putExtra("ringingMessage", voiceRinging.toBundle());
        Intent intent2 = ChatFragmentActivityFactory.getInstance().createIntent(this.context, ChatFragment.makeSelection(chatterID));
        intent2.addFlags(536870912);
        ActivityUtils.setActiveAgentID(intent2, chatterID.agentUUID);
        final UserManager userManager = this.userManager.get();
        if (userManager != null) {
            final Intent captureNotify = userManager.getUnreadNotificationManager().captureNotify(UnreadNotificationInfo.create(userManager.getUserID(), 0, null, null, 1, NotificationType.Private, UnreadNotificationInfo.UnreadMessageSource.create(chatterID, null, null, 0), UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null)), intent2);
            if (captureNotify != null) {
                intent2 = captureNotify;
            }
        }
        while (true) {
            final Intent intent3 = new Intent(this.context, (Class)GridConnectionService.class);
            intent3.setAction("accept");
            intent3.setData(voiceRinging.toUri());
            intent3.putExtra("ringingMessage", voiceRinging.toBundle());
            intent3.putExtra("chatterID", chatterID.toBundle());
            intent3.putExtra("openChatterIntent", (Parcelable)PendingIntent.getActivity(this.context, 0, intent2, 0));
            final Notification build = ((android.support.v4.app.NotificationCompat.Builder)new NotificationCompat.Builder(this.context)).setSmallIcon(2130837622).setContentTitle(contentTitle).setContentText(this.context.getString(2131296604)).setDefaults(-1).setPriority(1).setDeleteIntent(PendingIntent.getService(this.context, 0, intent, 0)).setContentIntent(PendingIntent.getActivity(this.context, 0, intent2, 0)).setAutoCancel(true).addAction(2130837640, this.context.getString(2131297145), PendingIntent.getService(this.context, 0, intent3, 0)).addAction(2130837641, this.context.getString(2131297146), PendingIntent.getService(this.context, 0, intent, 0)).build();
            final String voiceChannelURI = voiceRinging.voiceChannelInfo.voiceChannelURI;
            this.incomingCallNotificationTags.add(voiceChannelURI);
            ((NotificationManager)this.context.getSystemService("notification")).notify(voiceChannelURI, 1001, build);
            return;
            continue;
        }
    }
    
    public void acceptCall(final Intent intent) {
        if (intent.hasExtra("ringingMessage")) {
            final VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra("ringingMessage"));
            Debug.Printf("Voice: accepting session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
            this.sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
        }
        Debug.Printf("Voice: cancelling notifications", new Object[0]);
        this.cancelNotifications(null);
        if (!intent.hasExtra("openChatterIntent")) {
            return;
        }
        final Parcelable parcelableExtra = intent.getParcelableExtra("openChatterIntent");
        if (!(parcelableExtra instanceof PendingIntent)) {
            return;
        }
        try {
            Debug.Printf("Voice: starting pending open chatter intent", new Object[0]);
            ((PendingIntent)parcelableExtra).send();
        }
        catch (final PendingIntent$CanceledException ex) {
            Debug.Warning((Throwable)ex);
        }
    }
    
    public void acceptVoiceCall(final ChatterID chatterID) {
        final VoiceChannelInfo voiceChannelInfo = this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            Debug.Printf("Voice: cancelling notification", new Object[0]);
            this.cancelNotifications(null);
            Debug.Printf("Voice: accepting voice call (chatterID %s)", chatterID);
            this.sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(null, voiceChannelInfo));
        }
    }
    
    public void acceptVoiceCall(final VoiceRinging voiceRinging) {
        Debug.Printf("Voice: cancelling notification", new Object[0]);
        this.cancelNotifications(null);
        Debug.Printf("Voice: accepting voice call (session handle %s)", voiceRinging.sessionHandle);
        this.sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
    }
    
    public void addChannel(final ChatterID chatterID, final VoiceChannelInfo voiceChannelInfo) {
        this.voiceChannels.forcePut(chatterID, voiceChannelInfo);
    }
    
    public void disconnect() {
        this.mainThreadHandler.post((Runnable)new _$Lambda$KEiwggiQxhrsJugAMeHgzXJrgrA(this));
    }
    
    public void enableVoiceMic(final boolean b) {
        this.sendMessage(VoicePluginMessageType.VoiceEnableMic, new VoiceEnableMic(b));
    }
    
    public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
        Debug.Printf("LumiyaVoice: service connected", new Object[0]);
        this.toPluginMessenger = new Messenger(binder);
        try {
            this.sendMessage(VoicePluginMessageType.VoiceInitialize, new VoiceInitialize(this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0).versionCode));
        }
        catch (final PackageManager$NameNotFoundException ex) {
            Debug.Warning((Throwable)ex);
        }
    }
    
    public void onServiceDisconnected(final ComponentName componentName) {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
        final UserManager userManager = this.userManager.get();
        if (userManager != null) {
            userManager.setVoiceLoggedIn(false);
        }
    }
    
    public void rejectCall(final Intent intent) {
        if (intent.hasExtra("ringingMessage")) {
            final VoiceRinging voiceRinging = new VoiceRinging(intent.getBundleExtra("ringingMessage"));
            Debug.Printf("Voice: requesting to reject session '%s', url '%s'", voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo.voiceChannelURI);
            this.sendMessage(VoicePluginMessageType.VoiceRejectCall, new VoiceRejectCall(voiceRinging.sessionHandle, voiceRinging.voiceChannelInfo));
            this.cancelNotifications(voiceRinging.voiceChannelInfo.voiceChannelURI);
        }
        else {
            this.cancelNotifications(null);
        }
    }
    
    public boolean sendMessage(final VoicePluginMessageType voicePluginMessageType, final VoicePluginMessage voicePluginMessage) {
        return this.toPluginMessenger != null && VoicePluginMessenger.sendMessage(this.toPluginMessenger, voicePluginMessageType, voicePluginMessage, this.fromPluginMessenger);
    }
    
    public void setVoiceAudioProperties(final VoiceSetAudioProperties voiceSetAudioProperties) {
        this.sendMessage(VoicePluginMessageType.VoiceSetAudioProperties, voiceSetAudioProperties);
    }
    
    public void setVoiceLoginInfo(final VoiceLoginInfo newValue, final UserManager newValue2) {
        this.userManager.set(newValue2);
        if (!Objects.equal(this.voiceLoginInfo.getAndSet(newValue), newValue) && this.voiceInitialized.get() && newValue != null) {
            this.sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(newValue));
        }
    }
    
    public void terminateVoiceCall(final ChatterID chatterID) {
        final VoiceChannelInfo voiceChannelInfo = this.voiceChannels.get(chatterID);
        if (voiceChannelInfo != null) {
            this.sendMessage(VoicePluginMessageType.VoiceTerminateCall, new VoiceTerminateCall(voiceChannelInfo));
        }
    }
}
