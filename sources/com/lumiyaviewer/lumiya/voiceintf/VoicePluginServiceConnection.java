// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voiceintf;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatSystemMessageEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLMissedVoiceCallEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUser;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
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

public class VoicePluginServiceConnection
    implements ServiceConnection
{

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

        private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues[];
        final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$voice$common$VoicePluginMessageType[];
        final VoicePluginServiceConnection this$0;

        private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues()
        {
            if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues != null)
            {
                return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues;
            }
            int ai[] = new int[VoicePluginMessageType.values().length];
            try
            {
                ai[VoicePluginMessageType.VoiceAcceptCall.ordinal()] = 6;
            }
            catch (NoSuchFieldError nosuchfielderror15) { }
            try
            {
                ai[VoicePluginMessageType.VoiceAudioProperties.ordinal()] = 1;
            }
            catch (NoSuchFieldError nosuchfielderror14) { }
            try
            {
                ai[VoicePluginMessageType.VoiceChannelClosed.ordinal()] = 7;
            }
            catch (NoSuchFieldError nosuchfielderror13) { }
            try
            {
                ai[VoicePluginMessageType.VoiceChannelStatus.ordinal()] = 2;
            }
            catch (NoSuchFieldError nosuchfielderror12) { }
            try
            {
                ai[VoicePluginMessageType.VoiceConnectChannel.ordinal()] = 8;
            }
            catch (NoSuchFieldError nosuchfielderror11) { }
            try
            {
                ai[VoicePluginMessageType.VoiceEnableMic.ordinal()] = 9;
            }
            catch (NoSuchFieldError nosuchfielderror10) { }
            try
            {
                ai[VoicePluginMessageType.VoiceInitialize.ordinal()] = 10;
            }
            catch (NoSuchFieldError nosuchfielderror9) { }
            try
            {
                ai[VoicePluginMessageType.VoiceInitializeReply.ordinal()] = 3;
            }
            catch (NoSuchFieldError nosuchfielderror8) { }
            try
            {
                ai[VoicePluginMessageType.VoiceLogin.ordinal()] = 11;
            }
            catch (NoSuchFieldError nosuchfielderror7) { }
            try
            {
                ai[VoicePluginMessageType.VoiceLoginStatus.ordinal()] = 4;
            }
            catch (NoSuchFieldError nosuchfielderror6) { }
            try
            {
                ai[VoicePluginMessageType.VoiceLogout.ordinal()] = 12;
            }
            catch (NoSuchFieldError nosuchfielderror5) { }
            try
            {
                ai[VoicePluginMessageType.VoiceRejectCall.ordinal()] = 13;
            }
            catch (NoSuchFieldError nosuchfielderror4) { }
            try
            {
                ai[VoicePluginMessageType.VoiceRinging.ordinal()] = 5;
            }
            catch (NoSuchFieldError nosuchfielderror3) { }
            try
            {
                ai[VoicePluginMessageType.VoiceSet3DPosition.ordinal()] = 14;
            }
            catch (NoSuchFieldError nosuchfielderror2) { }
            try
            {
                ai[VoicePluginMessageType.VoiceSetAudioProperties.ordinal()] = 15;
            }
            catch (NoSuchFieldError nosuchfielderror1) { }
            try
            {
                ai[VoicePluginMessageType.VoiceTerminateCall.ordinal()] = 16;
            }
            catch (NoSuchFieldError nosuchfielderror) { }
            _2D_com_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues = ai;
            return ai;
        }

        public void handleMessage(Message message)
        {
            if (message.what != 200 || !(message.obj instanceof Bundle))
            {
                break MISSING_BLOCK_LABEL_211;
            }
            message = (Bundle)message.obj;
            if (!message.containsKey("message") || !message.containsKey("messageType"))
            {
                break MISSING_BLOCK_LABEL_211;
            }
            VoicePluginMessageType voicepluginmessagetype = VoicePluginMessageType.valueOf(message.getString("messageType"));
            _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_voice_2D_common_2D_VoicePluginMessageTypeSwitchesValues()[voicepluginmessagetype.ordinal()];
            JVM INSTR tableswitch 1 5: default 211
        //                       1 190
        //                       2 148
        //                       3 100
        //                       4 127
        //                       5 169;
               goto _L1 _L2 _L3 _L4 _L5 _L6
_L1:
            break MISSING_BLOCK_LABEL_211;
_L4:
            VoicePluginServiceConnection._2D_wrap2(VoicePluginServiceConnection.this, new VoiceInitializeReply(message.getBundle("message")));
            return;
_L5:
            try
            {
                VoicePluginServiceConnection._2D_wrap3(VoicePluginServiceConnection.this, new VoiceLoginStatus(message.getBundle("message")));
                return;
            }
            // Misplaced declaration of an exception variable
            catch (Message message)
            {
                Debug.Warning(message);
                return;
            }
_L3:
            VoicePluginServiceConnection._2D_wrap1(VoicePluginServiceConnection.this, new VoiceChannelStatus(message.getBundle("message")));
            return;
_L6:
            VoicePluginServiceConnection._2D_wrap4(VoicePluginServiceConnection.this, new VoiceRinging(message.getBundle("message")));
            return;
_L2:
            VoicePluginServiceConnection._2D_wrap0(VoicePluginServiceConnection.this, new VoiceAudioProperties(message.getBundle("message")));
            return;
        }

            
            {
                this$0 = VoicePluginServiceConnection.this;
                super();
            }
    };
    private final Messenger fromPluginMessenger;
    private final Set incomingCallNotificationTags = Collections.synchronizedSet(new HashSet());
    private final Handler mainThreadHandler = new Handler();
    private ChatterNameRetriever ringingChatterNameRetriever;
    private Messenger toPluginMessenger;
    private final AtomicReference userManager = new AtomicReference(null);
    private final BiMap voiceChannels = Maps.synchronizedBiMap(HashBiMap.create());
    private final AtomicBoolean voiceInitialized = new AtomicBoolean(false);
    private final AtomicReference voiceLoginInfo = new AtomicReference(null);

    static void _2D_wrap0(VoicePluginServiceConnection voicepluginserviceconnection, VoiceAudioProperties voiceaudioproperties)
    {
        voicepluginserviceconnection.onVoiceAudioProperties(voiceaudioproperties);
    }

    static void _2D_wrap1(VoicePluginServiceConnection voicepluginserviceconnection, VoiceChannelStatus voicechannelstatus)
    {
        voicepluginserviceconnection.onVoiceChannelStatus(voicechannelstatus);
    }

    static void _2D_wrap2(VoicePluginServiceConnection voicepluginserviceconnection, VoiceInitializeReply voiceinitializereply)
    {
        voicepluginserviceconnection.onVoiceInitializeReply(voiceinitializereply);
    }

    static void _2D_wrap3(VoicePluginServiceConnection voicepluginserviceconnection, VoiceLoginStatus voiceloginstatus)
    {
        voicepluginserviceconnection.onVoiceLoginStatus(voiceloginstatus);
    }

    static void _2D_wrap4(VoicePluginServiceConnection voicepluginserviceconnection, VoiceRinging voiceringing)
    {
        voicepluginserviceconnection.onVoiceRinging(voiceringing);
    }

    public VoicePluginServiceConnection(Context context1)
    {
        ringingChatterNameRetriever = null;
        context = context1;
        fromPluginMessenger = new Messenger(fromPluginHandler);
    }

    private void cancelNotifications(String s)
    {
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService("notification");
        if (s != null)
        {
            notificationmanager.cancel(s, 1001);
            incomingCallNotificationTags.remove(s);
            return;
        }
        for (s = incomingCallNotificationTags.iterator(); s.hasNext(); notificationmanager.cancel((String)s.next(), 1001)) { }
        incomingCallNotificationTags.clear();
    }

    public static boolean checkPluginInstalled(Context context1)
    {
        boolean flag1 = false;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
        context1 = context1.getPackageManager().queryIntentServices(intent, 0);
        boolean flag = flag1;
        if (context1 != null)
        {
            flag = flag1;
            if (context1.size() > 0)
            {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isPluginSupported()
    {
        int i;
        boolean flag1 = false;
        String as[];
        String s;
        boolean flag;
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            as = Build.SUPPORTED_ABIS;
        } else
        {
            as = (new String[] {
                Build.CPU_ABI, Build.CPU_ABI2
            });
        }
        flag = flag1;
        if (as == null) goto _L2; else goto _L1
_L1:
        i = 0;
_L7:
        flag = flag1;
        if (i >= as.length) goto _L2; else goto _L3
_L3:
        s = as[i];
        if (s == null || !s.toLowerCase().contains("armeabi") && !s.toLowerCase().contains("arm64")) goto _L5; else goto _L4
_L4:
        flag = true;
_L2:
        return flag;
_L5:
        i++;
        if (true) goto _L7; else goto _L6
_L6:
    }

    private void onVoiceAudioProperties(VoiceAudioProperties voiceaudioproperties)
    {
        Object obj = null;
        if (voiceaudioproperties != null)
        {
            obj = voiceaudioproperties.bluetoothState;
        }
        Debug.Printf("Voice: voice audio properties received, bluetooth state %s", new Object[] {
            obj
        });
        obj = (UserManager)userManager.get();
        if (obj != null)
        {
            ((UserManager) (obj)).setVoiceAudioProperties(voiceaudioproperties);
        }
    }

    private void onVoiceChannelStatus(VoiceChannelStatus voicechannelstatus)
    {
        if (voicechannelstatus.chatInfo.state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
        {
            cancelNotifications(voicechannelstatus.channelInfo.voiceChannelURI);
        }
        Object obj = (UserManager)userManager.get();
        if (obj != null)
        {
            ChatterID chatterid = (ChatterID)voiceChannels.inverse().get(voicechannelstatus.channelInfo);
            if (chatterid != null)
            {
                ((UserManager) (obj)).setVoiceChatInfo(chatterid, voicechannelstatus.chatInfo);
                if (voicechannelstatus.chatInfo.state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None && voicechannelstatus.chatInfo.previousState == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Ringing && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
                {
                    ((UserManager) (obj)).getChatterList().getActiveChattersManager().HandleChatEvent(chatterid, new SLMissedVoiceCallEvent(new ChatMessageSourceUser(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID()), ((UserManager) (obj)).getUserID(), LumiyaApp.getContext().getString(0x7f0901ba)), true);
                }
                if (voicechannelstatus.chatInfo.state == com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.Active)
                {
                    ((UserManager) (obj)).setVoiceActiveChatter(chatterid);
                }
            }
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules();
                if (obj != null)
                {
                    ((SLModules) (obj)).voice.onVoiceChannelStatus(voicechannelstatus);
                }
            }
        }
    }

    private void onVoiceInitializeReply(VoiceInitializeReply voiceinitializereply)
    {
        if (voiceinitializereply.appVersionOk) goto _L2; else goto _L1
_L1:
        voiceinitializereply = (UserManager)userManager.get();
        if (voiceinitializereply != null)
        {
            voiceinitializereply.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(voiceinitializereply.getUserID()), new SLVoiceUpgradeEvent(voiceinitializereply.getUserID(), LumiyaApp.getContext().getString(0x7f090045), false, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"), false);
        }
_L4:
        return;
_L2:
        if (voiceinitializereply.pluginVersionCode >= 3)
        {
            break; /* Loop/switch isn't completed */
        }
        voiceinitializereply = (UserManager)userManager.get();
        if (voiceinitializereply != null)
        {
            voiceinitializereply.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(voiceinitializereply.getUserID()), new SLVoiceUpgradeEvent(voiceinitializereply.getUserID(), LumiyaApp.getContext().getString(0x7f09027a), false, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"), false);
            return;
        }
        if (true) goto _L4; else goto _L3
_L3:
        if (voiceinitializereply.errorMessage != null)
        {
            break; /* Loop/switch isn't completed */
        }
        voiceInitialized.set(true);
        voiceinitializereply = (VoiceLoginInfo)voiceLoginInfo.get();
        if (voiceinitializereply != null)
        {
            sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voiceinitializereply));
            return;
        }
        if (true) goto _L4; else goto _L5
_L5:
        UserManager usermanager = (UserManager)userManager.get();
        if (usermanager != null)
        {
            usermanager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(usermanager.getUserID()), new SLChatSystemMessageEvent(ChatMessageSourceUnknown.getInstance(), usermanager.getUserID(), LumiyaApp.getContext().getString(0x7f09037f, new Object[] {
                voiceinitializereply.errorMessage
            })), false);
            return;
        }
        if (true) goto _L4; else goto _L6
_L6:
    }

    private void onVoiceLoginStatus(VoiceLoginStatus voiceloginstatus)
    {
        UserManager usermanager = (UserManager)userManager.get();
        if (usermanager != null)
        {
            Object obj = usermanager.getActiveAgentCircuit();
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules();
                if (obj != null)
                {
                    ((SLModules) (obj)).voice.onVoiceLoginStatus(this, voiceloginstatus);
                }
            }
            usermanager.setVoiceLoggedIn(voiceloginstatus.loggedIn);
        }
    }

    private void onVoiceRinging(VoiceRinging voiceringing)
    {
        Object obj = (UserManager)userManager.get();
        if (obj != null && voiceringing != null && voiceringing.agentUUID != null)
        {
            obj = ChatterID.getUserChatterID(((UserManager) (obj)).getUserID(), voiceringing.agentUUID);
            voiceChannels.forcePut(obj, voiceringing.voiceChannelInfo);
            ringingChatterNameRetriever = new ChatterNameRetriever(((ChatterID) (obj)), new _2D_.Lambda.KEiwggiQxhrsJugAMeHgzXJrgrA._cls1(this, voiceringing), UIThreadExecutor.getSerialInstance(), false);
            ringingChatterNameRetriever.subscribe();
        }
    }

    public static void setInstallOfferDisplayed(boolean flag)
    {
        installOfferDisplayed.set(flag);
    }

    public static boolean shouldDisplayInstallOffer()
    {
        return installOfferDisplayed.getAndSet(true) ^ true;
    }

    private void showIncomingCallNotification(VoiceRinging voiceringing, String s, ChatterID chatterid)
    {
        Intent intent1 = new Intent(context, com/lumiyaviewer/lumiya/GridConnectionService);
        intent1.setAction("reject");
        intent1.setData(voiceringing.toUri());
        intent1.putExtra("ringingMessage", voiceringing.toBundle());
        Intent intent = ChatFragmentActivityFactory.getInstance().createIntent(context, ChatFragment.makeSelection(chatterid));
        intent.addFlags(0x20000000);
        ActivityUtils.setActiveAgentID(intent, chatterid.agentUUID);
        Object obj = (UserManager)userManager.get();
        if (obj != null)
        {
            UnreadNotificationInfo unreadnotificationinfo = UnreadNotificationInfo.create(((UserManager) (obj)).getUserID(), 0, null, null, 1, NotificationType.Private, com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource.create(chatterid, null, null, 0), com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
            obj = ((UserManager) (obj)).getUnreadNotificationManager().captureNotify(unreadnotificationinfo, intent);
            if (obj != null)
            {
                intent = ((Intent) (obj));
            }
        }
        obj = new Intent(context, com/lumiyaviewer/lumiya/GridConnectionService);
        ((Intent) (obj)).setAction("accept");
        ((Intent) (obj)).setData(voiceringing.toUri());
        ((Intent) (obj)).putExtra("ringingMessage", voiceringing.toBundle());
        ((Intent) (obj)).putExtra("chatterID", chatterid.toBundle());
        ((Intent) (obj)).putExtra("openChatterIntent", PendingIntent.getActivity(context, 0, intent, 0));
        s = (new android.support.v7.app.NotificationCompat.Builder(context)).setSmallIcon(0x7f020076).setContentTitle(s).setContentText(context.getString(0x7f09015c)).setDefaults(-1).setPriority(1).setDeleteIntent(PendingIntent.getService(context, 0, intent1, 0)).setContentIntent(PendingIntent.getActivity(context, 0, intent, 0)).setAutoCancel(true).addAction(0x7f020088, context.getString(0x7f090379), PendingIntent.getService(context, 0, ((Intent) (obj)), 0)).addAction(0x7f020089, context.getString(0x7f09037a), PendingIntent.getService(context, 0, intent1, 0)).build();
        voiceringing = voiceringing.voiceChannelInfo.voiceChannelURI;
        incomingCallNotificationTags.add(voiceringing);
        ((NotificationManager)context.getSystemService("notification")).notify(voiceringing, 1001, s);
    }

    public void acceptCall(Intent intent)
    {
        if (intent.hasExtra("ringingMessage"))
        {
            VoiceRinging voiceringing = new VoiceRinging(intent.getBundleExtra("ringingMessage"));
            Debug.Printf("Voice: accepting session '%s', url '%s'", new Object[] {
                voiceringing.sessionHandle, voiceringing.voiceChannelInfo.voiceChannelURI
            });
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceringing.sessionHandle, voiceringing.voiceChannelInfo));
        }
        Debug.Printf("Voice: cancelling notifications", new Object[0]);
        cancelNotifications(null);
        if (!intent.hasExtra("openChatterIntent"))
        {
            break MISSING_BLOCK_LABEL_128;
        }
        intent = intent.getParcelableExtra("openChatterIntent");
        if (!(intent instanceof PendingIntent))
        {
            break MISSING_BLOCK_LABEL_128;
        }
        Debug.Printf("Voice: starting pending open chatter intent", new Object[0]);
        ((PendingIntent)intent).send();
        return;
        intent;
        Debug.Warning(intent);
        return;
    }

    public void acceptVoiceCall(ChatterID chatterid)
    {
        VoiceChannelInfo voicechannelinfo = (VoiceChannelInfo)voiceChannels.get(chatterid);
        if (voicechannelinfo != null)
        {
            Debug.Printf("Voice: cancelling notification", new Object[0]);
            cancelNotifications(null);
            Debug.Printf("Voice: accepting voice call (chatterID %s)", new Object[] {
                chatterid
            });
            sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(null, voicechannelinfo));
        }
    }

    public void acceptVoiceCall(VoiceRinging voiceringing)
    {
        Debug.Printf("Voice: cancelling notification", new Object[0]);
        cancelNotifications(null);
        Debug.Printf("Voice: accepting voice call (session handle %s)", new Object[] {
            voiceringing.sessionHandle
        });
        sendMessage(VoicePluginMessageType.VoiceAcceptCall, new VoiceAcceptCall(voiceringing.sessionHandle, voiceringing.voiceChannelInfo));
    }

    public void addChannel(ChatterID chatterid, VoiceChannelInfo voicechannelinfo)
    {
        voiceChannels.forcePut(chatterid, voicechannelinfo);
    }

    public void disconnect()
    {
        mainThreadHandler.post(new _2D_.Lambda.KEiwggiQxhrsJugAMeHgzXJrgrA(this));
    }

    public void enableVoiceMic(boolean flag)
    {
        sendMessage(VoicePluginMessageType.VoiceEnableMic, new VoiceEnableMic(flag));
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_13701(VoiceRinging voiceringing, ChatterNameRetriever chatternameretriever)
    {
        if (chatternameretriever == ringingChatterNameRetriever)
        {
            String s = chatternameretriever.getResolvedName();
            if (!Strings.isNullOrEmpty(s))
            {
                showIncomingCallNotification(voiceringing, s, chatternameretriever.chatterID);
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_voiceintf_VoicePluginServiceConnection_17898()
    {
        Debug.Printf("LumiyaVoice: disconnecting from voice plugin", new Object[0]);
        UserManager usermanager = (UserManager)userManager.get();
        if (usermanager != null)
        {
            usermanager.setVoiceLoggedIn(false);
        }
        sendMessage(VoicePluginMessageType.VoiceLogout, new VoiceLogout());
        context.unbindService(this);
    }

    public void onServiceConnected(ComponentName componentname, IBinder ibinder)
    {
        Debug.Printf("LumiyaVoice: service connected", new Object[0]);
        toPluginMessenger = new Messenger(ibinder);
        try
        {
            componentname = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            sendMessage(VoicePluginMessageType.VoiceInitialize, new VoiceInitialize(((PackageInfo) (componentname)).versionCode));
            return;
        }
        // Misplaced declaration of an exception variable
        catch (ComponentName componentname)
        {
            Debug.Warning(componentname);
        }
    }

    public void onServiceDisconnected(ComponentName componentname)
    {
        Debug.Printf("LumiyaCloud: service disconnected", new Object[0]);
        componentname = (UserManager)userManager.get();
        if (componentname != null)
        {
            componentname.setVoiceLoggedIn(false);
        }
    }

    public void rejectCall(Intent intent)
    {
        if (intent.hasExtra("ringingMessage"))
        {
            intent = new VoiceRinging(intent.getBundleExtra("ringingMessage"));
            Debug.Printf("Voice: requesting to reject session '%s', url '%s'", new Object[] {
                ((VoiceRinging) (intent)).sessionHandle, ((VoiceRinging) (intent)).voiceChannelInfo.voiceChannelURI
            });
            sendMessage(VoicePluginMessageType.VoiceRejectCall, new VoiceRejectCall(((VoiceRinging) (intent)).sessionHandle, ((VoiceRinging) (intent)).voiceChannelInfo));
            cancelNotifications(((VoiceRinging) (intent)).voiceChannelInfo.voiceChannelURI);
            return;
        } else
        {
            cancelNotifications(null);
            return;
        }
    }

    public boolean sendMessage(VoicePluginMessageType voicepluginmessagetype, VoicePluginMessage voicepluginmessage)
    {
        if (toPluginMessenger != null)
        {
            return VoicePluginMessenger.sendMessage(toPluginMessenger, voicepluginmessagetype, voicepluginmessage, fromPluginMessenger);
        } else
        {
            return false;
        }
    }

    public void setVoiceAudioProperties(VoiceSetAudioProperties voicesetaudioproperties)
    {
        sendMessage(VoicePluginMessageType.VoiceSetAudioProperties, voicesetaudioproperties);
    }

    public void setVoiceLoginInfo(VoiceLoginInfo voicelogininfo, UserManager usermanager)
    {
        userManager.set(usermanager);
        if (!Objects.equal(voiceLoginInfo.getAndSet(voicelogininfo), voicelogininfo) && voiceInitialized.get() && voicelogininfo != null)
        {
            sendMessage(VoicePluginMessageType.VoiceLogin, new VoiceLogin(voicelogininfo));
        }
    }

    public void terminateVoiceCall(ChatterID chatterid)
    {
        chatterid = (VoiceChannelInfo)voiceChannels.get(chatterid);
        if (chatterid != null)
        {
            sendMessage(VoicePluginMessageType.VoiceTerminateCall, new VoiceTerminateCall(chatterid));
        }
    }

}
