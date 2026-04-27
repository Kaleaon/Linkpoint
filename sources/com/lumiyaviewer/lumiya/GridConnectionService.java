// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.ActiveChattersManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotifications;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.notify.OnlineNotificationInfo;
import com.lumiyaviewer.lumiya.ui.settings.NotificationSettings;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.lumiyaviewer.lumiya.utils.LEDAction;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            Debug, LumiyaApp, GlobalOptions

public class GridConnectionService extends Service
    implements android.content.SharedPreferences.OnSharedPreferenceChangeListener
{
    public class GridServiceBinder extends Binder
    {

        final GridConnectionService this$0;

        public EventBus getEventBus()
        {
            return GridConnectionService._2D_get0(GridConnectionService.this);
        }

        public SLGridConnection getGridConn()
        {
            return GridConnectionService._2D_get1();
        }

        public GridServiceBinder()
        {
            this$0 = GridConnectionService.this;
            super();
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues[];
    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues[];
    public static final String LOGIN_ACTION = "com.lumiyaviewer.lumiya.ACTION_LOGIN";
    private static final int REQUEST_CODE_UNREAD_NOTIFY = 0x7f100040;
    private static SLGridConnection gridConnection = null;
    private static String gridName = "Second Life";
    private static NotificationSettings notifyGroup;
    private static NotificationSettings notifyLocalChat;
    private static NotificationSettings notifyPrivate;
    private static boolean onlineNotify = false;
    private static WeakReference serviceInstance = null;
    private static boolean soundOnNotify = true;
    private static Set visibleActivities = Collections.synchronizedSet(new HashSet());
    private final BroadcastReceiver cloudPluginInstalledReceiver = new BroadcastReceiver() {

        final GridConnectionService this$0;

        public void onReceive(Context context, Intent intent)
        {
            if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.lumiyaviewer.lumiya.cloud") && GridConnectionService._2D_get1() != null && GridConnectionService._2D_get1().getConnectionState() == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected)
            {
                context = GridConnectionService._2D_get1().getActiveAgentUUID();
                if (context != null)
                {
                    context = UserManager.getUserManager(context);
                    if (context != null)
                    {
                        GridConnectionService._2D_wrap1(GridConnectionService.this, context);
                    }
                }
            }
        }

            
            {
                this$0 = GridConnectionService.this;
                super();
            }
    };
    private boolean cloudPluginReceiverRegistered;
    private boolean cloudSyncEnabled;
    private CloudSyncServiceConnection cloudSyncServiceConnection;
    private UserManager cloudSyncUserManager;
    private ChatterNameRetriever connectedAgentNameRetriever;
    private final SubscriptionData currentLocationInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda._cls3DowF6pLKgVjVrTY9aZKQ2J3cf0(this));
    private final EventBus eventBus = EventBus.getInstance();
    private Handler licenseCheckHandler;
    private final IBinder mBinder = new GridServiceBinder();
    private final Handler mHandler = new Handler();
    private final com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated onActiveAgentNameUpdated = new _2D_.Lambda._cls3DowF6pLKgVjVrTY9aZKQ2J3cf0._cls2(this);
    private OnlineNotificationInfo onlineNotificationInfo;
    private SharedPreferences prefs;
    private final Set shownNotificationIds = Collections.newSetFromMap(Collections.synchronizedMap(new HashMap()));
    private Subscription unreadNotifySubscription;
    private final BroadcastReceiver voicePluginInstalledReceiver = new BroadcastReceiver() {

        final GridConnectionService this$0;

        public void onReceive(Context context, Intent intent)
        {
            if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.lumiyaviewer.lumiya.voice") && GridConnectionService._2D_get1() != null && GridConnectionService._2D_get1().getConnectionState() == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected)
            {
                context = GridConnectionService._2D_get1().getActiveAgentUUID();
                if (context != null)
                {
                    context = UserManager.getUserManager(context);
                    if (context != null)
                    {
                        context = context.getActiveAgentCircuit();
                        if (context != null)
                        {
                            context = context.getModules();
                            if (context != null)
                            {
                                ((SLModules) (context)).voice.updateVoiceEnabledStatus();
                            }
                        }
                    }
                }
            }
        }

            
            {
                this$0 = GridConnectionService.this;
                super();
            }
    };
    private boolean voicePluginReceiverRegistered;
    private VoicePluginServiceConnection voicePluginServiceConnection;
    private android.net.wifi.WifiManager.WifiLock wifiLock;

    static EventBus _2D_get0(GridConnectionService gridconnectionservice)
    {
        return gridconnectionservice.eventBus;
    }

    static SLGridConnection _2D_get1()
    {
        return gridConnection;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues;
        }
        int ai[] = new int[NotificationType.values().length];
        try
        {
            ai[NotificationType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[NotificationType.LocalChat.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[NotificationType.Private.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues = ai;
        return ai;
    }

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues;
        }
        int ai[] = new int[LEDAction.values().length];
        try
        {
            ai[LEDAction.Always.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[LEDAction.Fast.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[LEDAction.None.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[LEDAction.Slow.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues = ai;
        return ai;
    }

    static void _2D_wrap0(GridConnectionService gridconnectionservice, SLAuthParams slauthparams)
    {
        gridconnectionservice.performLogin(slauthparams);
    }

    static void _2D_wrap1(GridConnectionService gridconnectionservice, UserManager usermanager)
    {
        gridconnectionservice.startCloudSync(usermanager);
    }

    public GridConnectionService()
    {
        prefs = null;
        cloudSyncEnabled = false;
        cloudSyncUserManager = null;
        cloudPluginReceiverRegistered = false;
        voicePluginReceiverRegistered = false;
        connectedAgentNameRetriever = null;
        onlineNotificationInfo = new OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, connectedAgentNameRetriever, null);
        wifiLock = null;
        licenseCheckHandler = new Handler() {

            final GridConnectionService this$0;

            public void handleMessage(Message message)
            {
                message.what;
                JVM INSTR tableswitch 2131755033 2131755035: default 32
            //                           2131755033 33
            //                           2131755034 100
            //                           2131755035 69;
                   goto _L1 _L2 _L3 _L4
_L1:
                return;
_L2:
                Debug.Printf("License: License check ok.", new Object[0]);
                if (message.obj instanceof SLAuthParams)
                {
                    message = (SLAuthParams)message.obj;
                    GridConnectionService._2D_wrap0(GridConnectionService.this, message);
                    return;
                }
                  goto _L1
_L4:
                Debug.Printf("License: License check failed.", new Object[0]);
                GridConnectionService._2D_get0(GridConnectionService.this).publish(new SLLoginResultEvent(false, "You don't have valid license to use this application.", null));
                return;
_L3:
                if (message.obj instanceof String)
                {
                    message = (String)message.obj;
                } else
                {
                    message = "Internal application error";
                }
                Debug.Printf("License: License check app error: %s", new Object[] {
                    message
                });
                GridConnectionService._2D_get0(GridConnectionService.this).publish(new SLLoginResultEvent(false, (new StringBuilder()).append("License check failed: ").append(message).append(".").toString(), null));
                return;
            }

            
            {
                this$0 = GridConnectionService.this;
                super();
            }
        };
        cloudSyncServiceConnection = null;
        voicePluginServiceConnection = null;
        getGridConnection();
        eventBus.subscribe(this, null, mHandler);
    }

    private void connectToVoicePlugin(VoiceLoginInfo voicelogininfo, UserManager usermanager)
    {
        if (voicePluginServiceConnection == null)
        {
            voicePluginServiceConnection = new VoicePluginServiceConnection(this);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
            boolean flag;
            try
            {
                flag = bindService(intent, voicePluginServiceConnection, 1);
            }
            catch (SecurityException securityexception)
            {
                Debug.Warning(securityexception);
                flag = false;
            }
            Debug.Printf("LumiyaVoice: bindService = %b", new Object[] {
                Boolean.valueOf(flag)
            });
            if (!flag)
            {
                voicePluginServiceConnection = null;
                IntentFilter intentfilter = new IntentFilter();
                intentfilter.addAction("android.intent.action.PACKAGE_ADDED");
                intentfilter.addDataScheme("package");
                if (android.os.Build.VERSION.SDK_INT >= 19)
                {
                    intentfilter.addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.voice", 0);
                }
                registerReceiver(voicePluginInstalledReceiver, intentfilter);
                voicePluginReceiverRegistered = true;
                if (usermanager != null && VoicePluginServiceConnection.shouldDisplayInstallOffer())
                {
                    usermanager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(usermanager.getUserID()), new SLVoiceUpgradeEvent(usermanager.getUserID(), LumiyaApp.getContext().getString(0x7f090279), true, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"), false);
                }
            }
        }
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.setVoiceLoginInfo(voicelogininfo, usermanager);
        }
    }

    public static SLGridConnection getGridConnection()
    {
        if (gridConnection == null)
        {
            gridConnection = new SLGridConnection();
        }
        return gridConnection;
    }

    public static GridConnectionService getServiceInstance()
    {
        GridConnectionService gridconnectionservice = null;
        if (serviceInstance != null)
        {
            gridconnectionservice = (GridConnectionService)serviceInstance.get();
        }
        return gridconnectionservice;
    }

    private void handleStartService(Intent intent)
    {
        String s;
        if (intent != null)
        {
            s = "not null";
        } else
        {
            s = "null";
        }
        Debug.Printf("GridConnectionService: service is now started, intent is %s", new Object[] {
            s
        });
        updateOnlineNotification();
        if (intent == null) goto _L2; else goto _L1
_L1:
        s = Strings.nullToEmpty(intent.getAction());
        if (!s.equals("com.lumiyaviewer.lumiya.ACTION_LOGIN")) goto _L4; else goto _L3
_L3:
        intent = new SLAuthParams(intent);
        new LicenseChecker(getApplicationContext(), licenseCheckHandler, intent);
_L2:
        return;
_L4:
        if (!s.equals("reject"))
        {
            continue; /* Loop/switch isn't completed */
        }
        if (voicePluginServiceConnection == null) goto _L2; else goto _L5
_L5:
        voicePluginServiceConnection.rejectCall(intent);
        return;
        if (!s.equals("accept") || voicePluginServiceConnection == null) goto _L2; else goto _L6
_L6:
        voicePluginServiceConnection.acceptCall(intent);
        return;
    }

    public static boolean hasVisibleActivities()
    {
        return visibleActivities.isEmpty() ^ true;
    }

    private void hideUnreadNotificationSingle(int i)
    {
        if (shownNotificationIds.contains(Integer.valueOf(i)))
        {
            shownNotificationIds.remove(Integer.valueOf(i));
            ((NotificationManager)getSystemService("notification")).cancel(i);
        }
    }

    private static NotificationSettings notifySettingsByType(NotificationType notificationtype)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues()[notificationtype.ordinal()])
        {
        default:
            return notifyLocalChat;

        case 2: // '\002'
            return notifyLocalChat;

        case 1: // '\001'
            return notifyGroup;

        case 3: // '\003'
            return notifyPrivate;
        }
    }

    private void onCurrentLocationInfo(CurrentLocationInfo currentlocationinfo)
    {
        updateOnlineNotification();
    }

    private void onUnreadNotification(UnreadNotifications unreadnotifications)
    {
        showUnreadNotification(unreadnotifications);
    }

    private void performLogin(SLAuthParams slauthparams)
    {
        gridName = slauthparams.gridName;
        gridConnection.Connect(slauthparams);
    }

    private void readPreferences(SharedPreferences sharedpreferences)
    {
        onlineNotify = sharedpreferences.getBoolean("onlineNotify", true);
        soundOnNotify = sharedpreferences.getBoolean("soundOnNotify", true);
        cloudSyncEnabled = sharedpreferences.getBoolean("sync_to_gdrive", false);
        notifyLocalChat.Load(sharedpreferences);
        notifyPrivate.Load(sharedpreferences);
        notifyGroup.Load(sharedpreferences);
        SLGridConnection.setAutoresponseInfo(sharedpreferences.getBoolean("autoresponse", false), sharedpreferences.getString("autoresponseText", "(Autoresponse) I have auto-response feature enabled. I will respond shortly."));
        Debug.Log((new StringBuilder()).append("GridConnectionService: prefs: onlineNotify = ").append(onlineNotify).toString());
        Debug.Log((new StringBuilder()).append("GridConnectionService: prefs: soundOnNotify = ").append(soundOnNotify).toString());
        if (gridConnection != null)
        {
            try
            {
                gridConnection.getModules().HandleGlobalOptionsChange();
            }
            // Misplaced declaration of an exception variable
            catch (SharedPreferences sharedpreferences) { }
        }
        updateOnlineNotification();
        updateCloudSyncStatus();
    }

    public static void setActivityVisible(Activity activity, boolean flag)
    {
        if (flag)
        {
            visibleActivities.add(activity);
        } else
        {
            visibleActivities.remove(activity);
        }
        if (visibleActivities.isEmpty() || gridConnection == null)
        {
            break MISSING_BLOCK_LABEL_52;
        }
        if (gridConnection.getConnectionState() == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected)
        {
            gridConnection.getAgentCircuit().UnpauseAgent();
        }
        return;
        activity;
        activity.printStackTrace();
        return;
    }

    private void showUnreadNotification(UnreadNotifications unreadnotifications)
    {
        NotificationChannels notificationchannels = NotificationChannels.getInstance();
        if (notificationchannels.useNotificationGroups())
        {
            UnreadNotifications unreadnotifications1 = unreadnotifications;
            if (notificationchannels.areNotificationsSystemControlled())
            {
                unreadnotifications1 = unreadnotifications.filter(notificationchannels.getEnabledTypes(this));
            }
            if (!unreadnotifications1.notificationGroups().isEmpty())
            {
                unreadnotifications = unreadnotifications1.merge();
                showUnreadNotificationSingle(unreadnotifications, 0x7f10003d, notificationchannels.getChannelName(notificationchannels.getChannelByType((NotificationType)unreadnotifications.mostImportantFreshType().or((NotificationType)unreadnotifications.mostImportantType().or(NotificationType.LocalChat)))), "messageNotifications", true, null);
                unreadnotifications = NotificationType.VALUES.iterator();
                do
                {
                    if (!unreadnotifications.hasNext())
                    {
                        break;
                    }
                    Object obj = (NotificationType)unreadnotifications.next();
                    com.lumiyaviewer.lumiya.ui.notify.NotificationChannels.Channel channel = notificationchannels.getChannelByType(((NotificationType) (obj)));
                    if (channel != null)
                    {
                        UnreadNotificationInfo unreadnotificationinfo = (UnreadNotificationInfo)unreadnotifications1.notificationGroups().get(obj);
                        obj = Integer.toString(9 - ((NotificationType) (obj)).getPriority());
                        showUnreadNotificationSingle(unreadnotificationinfo, channel.notificationId, notificationchannels.getChannelName(channel), "messageNotifications", false, ((String) (obj)));
                    }
                } while (true);
            } else
            if (!shownNotificationIds.isEmpty())
            {
                unreadnotifications = (NotificationManager)getSystemService("notification");
                for (Iterator iterator = shownNotificationIds.iterator(); iterator.hasNext(); unreadnotifications.cancel(((Integer)iterator.next()).intValue())) { }
                shownNotificationIds.clear();
            }
            return;
        } else
        {
            showUnreadNotificationSingle(unreadnotifications.merge(), 0x7f10003d, "miscellaneous", null, true, null);
            return;
        }
    }

    private void showUnreadNotificationSingle(UnreadNotificationInfo unreadnotificationinfo, int i, String s, String s1, boolean flag, String s2)
    {
        Object obj;
        NotificationManager notificationmanager;
        android.support.v4.app.NotificationCompat.Builder builder;
        boolean flag1;
label0:
        {
label1:
            {
                notificationmanager = (NotificationManager)getSystemService("notification");
                boolean flag2 = false;
                flag1 = flag2;
                if (unreadnotificationinfo == null)
                {
                    break label1;
                }
                if (unreadnotificationinfo.totalUnreadCount() == 0)
                {
                    flag1 = flag2;
                    if (unreadnotificationinfo.objectPopupInfo().objectPopupsCount() == 0)
                    {
                        break label1;
                    }
                }
                flag1 = true;
            }
            if (!flag1)
            {
                hideUnreadNotificationSingle(i);
                return;
            }
            Debug.Log("GridConnectionService: updateUnreadNotification: notification state changed.");
            builder = new android.support.v4.app.NotificationCompat.Builder(this, s);
            builder.setSmallIcon(0x7f020087);
            if (s1 != null)
            {
                builder.setGroup(s1);
                builder.setGroupSummary(flag);
                builder.setGroupAlertBehavior(2);
                if (s2 != null)
                {
                    builder.setSortKey(s2);
                }
            }
            UserManager usermanager = UserManager.getUserManager(unreadnotificationinfo.agentUUID());
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource unreadmessagesource;
            if (unreadnotificationinfo.unreadSources().size() == 1)
            {
                unreadmessagesource = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)unreadnotificationinfo.unreadSources().get(0);
            } else
            {
                unreadmessagesource = null;
            }
            s2 = ChatFragmentActivityFactory.getInstance();
            if (unreadmessagesource != null)
            {
                s = ChatFragment.makeSelection(unreadmessagesource.chatterID());
            } else
            {
                s = null;
            }
            obj = s2.createIntent(this, s);
            ((Intent) (obj)).addFlags(0x20000000);
            ActivityUtils.setActiveAgentID(((Intent) (obj)), unreadnotificationinfo.agentUUID());
            if (unreadnotificationinfo.singleFreshSource().isPresent())
            {
                s = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)unreadnotificationinfo.singleFreshSource().get();
            } else
            {
                Iterator iterator3 = unreadnotificationinfo.unreadSources().iterator();
                s = null;
                while (iterator3.hasNext()) 
                {
                    com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource unreadmessagesource2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)iterator3.next();
                    s2 = unreadmessagesource2;
                    if (s != null)
                    {
                        if (unreadmessagesource2.unreadMessagesCount() > s.unreadMessagesCount())
                        {
                            s2 = unreadmessagesource2;
                        } else
                        {
                            s2 = s;
                        }
                    }
                    s = s2;
                }
            }
            if (s != null)
            {
                ((Intent) (obj)).putExtra("weakSelection", ChatFragment.makeSelection(s.chatterID()));
            }
            if (usermanager != null)
            {
                s = usermanager.getUnreadNotificationManager().captureNotify(unreadnotificationinfo, ((Intent) (obj)));
                if (s != null)
                {
                    obj = s;
                }
            }
            if (unreadmessagesource != null && unreadnotificationinfo.objectPopupInfo().objectPopupsCount() == 0)
            {
                s = (String)unreadmessagesource.chatterName().or(gridName);
                builder.setContentTitle(s);
                if (unreadmessagesource.unreadMessagesCount() == 1 && unreadmessagesource.unreadMessages().size() == 1)
                {
                    builder.setContentText(((SLChatEvent)unreadmessagesource.unreadMessages().get(0)).getPlainTextMessage(this, unreadmessagesource.chatterID().getUserManager(), true));
                } else
                {
                    builder.setContentText(String.format(getString(0x7f090363), new Object[] {
                        Integer.valueOf(unreadmessagesource.unreadMessagesCount())
                    }));
                }
                if (unreadmessagesource.unreadMessages().size() > 1)
                {
                    s2 = new android.support.v4.app.NotificationCompat.InboxStyle();
                    Iterator iterator1 = unreadmessagesource.unreadMessages().iterator();
                    while (iterator1.hasNext()) 
                    {
                        SLChatEvent slchatevent = (SLChatEvent)iterator1.next();
                        UserManager usermanager1 = unreadmessagesource.chatterID().getUserManager();
                        boolean flag3;
                        if (unreadmessagesource.chatterID().getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User)
                        {
                            flag3 = true;
                        } else
                        {
                            flag3 = false;
                        }
                        s2.addLine(slchatevent.getPlainTextMessage(this, usermanager1, flag3, "  ", " "));
                    }
                    s2.setBigContentTitle(s);
                    s2.setSummaryText(String.format(getString(0x7f090363), new Object[] {
                        Integer.valueOf(unreadnotificationinfo.totalUnreadCount())
                    }));
                    builder.setStyle(s2);
                }
                break label0;
            }
        }
        if (unreadnotificationinfo.totalUnreadCount() == 0) goto _L2; else goto _L1
_L1:
        s2 = getResources().getQuantityString(0x7f110003, unreadnotificationinfo.totalUnreadCount(), new Object[] {
            Integer.valueOf(unreadnotificationinfo.totalUnreadCount())
        });
        s = s2;
        if (unreadnotificationinfo.objectPopupInfo().objectPopupsCount() != 0)
        {
            s = (new StringBuilder()).append(s2).append(getResources().getQuantityString(0x7f110000, unreadnotificationinfo.objectPopupInfo().objectPopupsCount(), new Object[] {
                Integer.valueOf(unreadnotificationinfo.objectPopupInfo().objectPopupsCount())
            })).toString();
        }
        builder.setContentTitle(s);
        Iterator iterator;
        com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource unreadmessagesource3;
        Iterator iterator2;
        if (unreadnotificationinfo.totalUnreadCount() != 0 || unreadnotificationinfo.objectPopupInfo().objectPopupsCount() == 0)
        {
            builder.setContentTitle(getResources().getQuantityString(0x7f110003, unreadnotificationinfo.totalUnreadCount(), new Object[] {
                Integer.valueOf(unreadnotificationinfo.totalUnreadCount())
            }));
        } else
        {
            builder.setContentTitle(getResources().getQuantityString(0x7f110004, unreadnotificationinfo.objectPopupInfo().objectPopupsCount(), new Object[] {
                Integer.valueOf(unreadnotificationinfo.objectPopupInfo().objectPopupsCount())
            }));
        }
        s = unreadnotificationinfo.unreadSources().iterator();
        do
        {
            if (!s.hasNext())
            {
                break MISSING_BLOCK_LABEL_2465;
            }
            s2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)s.next();
        } while (!s2.chatterName().isPresent());
        s = (String)s2.chatterName().orNull();
_L13:
        k = unreadnotificationinfo.unreadSources().size();
        if (s != null)
        {
            if (k > 1)
            {
                s = (new StringBuilder()).append(s).append(" ").append(String.format(getString(0x7f090253), new Object[] {
                    Integer.valueOf(k - 1)
                })).toString();
            }
        } else
        {
            s = String.format(getString(0x7f0900b2), new Object[] {
                Integer.valueOf(k)
            });
        }
        builder.setContentText(s);
        s2 = new android.support.v4.app.NotificationCompat.InboxStyle();
        for (iterator = unreadnotificationinfo.unreadSources().iterator(); iterator.hasNext();)
        {
            unreadmessagesource3 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)iterator.next();
            iterator2 = unreadmessagesource3.unreadMessages().iterator();
            while (iterator2.hasNext()) 
            {
                s2.addLine(((SLChatEvent)iterator2.next()).getPlainTextMessage(this, unreadmessagesource3.chatterID().getUserManager(), false, "  ", " "));
            }
        }

        s2.setBigContentTitle(String.format(getString(0x7f090363), new Object[] {
            Integer.valueOf(unreadnotificationinfo.totalUnreadCount())
        }));
        s2.setSummaryText(s);
        builder.setStyle(s2);
          goto _L3
_L2:
        if (unreadnotificationinfo.objectPopupInfo().objectPopupsCount() != 0)
        {
            ((Intent) (obj)).putExtra("objectPopupNotification", true);
            if (((Intent) (obj)).getBundleExtra("selection") == null && ((Intent) (obj)).getBundleExtra("weakSelection") == null)
            {
                ((Intent) (obj)).putExtra("selection", ChatFragment.makeSelection(ChatterID.getLocalChatterID(unreadnotificationinfo.agentUUID())));
            }
            s = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupMessage)unreadnotificationinfo.objectPopupInfo().lastObjectPopup().orNull();
            if (unreadnotificationinfo.objectPopupInfo().freshObjectPopupsCount() == 1 && s != null)
            {
                builder.setContentTitle(s.objectName());
                builder.setContentText(s.message());
            } else
            {
                builder.setContentTitle(getResources().getQuantityString(0x7f110004, unreadnotificationinfo.objectPopupInfo().objectPopupsCount(), new Object[] {
                    Integer.valueOf(unreadnotificationinfo.objectPopupInfo().objectPopupsCount())
                }));
                if (s != null)
                {
                    builder.setContentText((new StringBuilder()).append(s.objectName()).append(": ").append(s.message()).toString());
                } else
                {
                    builder.setContentText(getResources().getQuantityString(0x7f110004, unreadnotificationinfo.objectPopupInfo().objectPopupsCount(), new Object[] {
                        Integer.valueOf(unreadnotificationinfo.objectPopupInfo().objectPopupsCount())
                    }));
                }
            }
        } else
        {
            flag1 = false;
        }
_L3:
        int k;
        if (!flag1)
        {
            hideUnreadNotificationSingle(i);
            return;
        }
        s = null;
        if (unreadnotificationinfo.freshMessagesCount() > 0)
        {
            com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource unreadmessagesource1 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource)unreadnotificationinfo.singleFreshSource().orNull();
            int j;
            if (unreadmessagesource1 != null)
            {
                if (unreadnotificationinfo.freshMessagesCount() == 1 && unreadmessagesource1.unreadMessages().size() > 0)
                {
                    s2 = (SLChatEvent)unreadmessagesource1.unreadMessages().get(unreadmessagesource1.unreadMessages().size() - 1);
                    if (unreadmessagesource1.chatterID().getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
                    {
                        s = getString(0x7f0900b8);
                    } else
                    if (unreadmessagesource1.chatterID().getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group)
                    {
                        s = getString(0x7f0900b6);
                    } else
                    {
                        s = getString(0x7f0900b7);
                    }
                    s = (new StringBuilder()).append("[").append(s).append("] ").append(s2.getPlainTextMessage(this, unreadmessagesource1.chatterID().getUserManager(), false).toString().trim()).toString();
                    j = s.indexOf("\n");
                    s2 = s;
                    if (j >= 0)
                    {
                        s2 = s.substring(0, j);
                    }
                    s = s2;
                    if (s2.length() > 30)
                    {
                        s = (new StringBuilder()).append(s2.substring(0, 30)).append("...").toString();
                    }
                } else
                {
                    s2 = String.format(getString(0x7f090363), new Object[] {
                        Integer.valueOf(unreadnotificationinfo.freshMessagesCount())
                    });
                    if (unreadmessagesource1.chatterID().getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local)
                    {
                        s = getString(0x7f0901b9, new Object[] {
                            unreadmessagesource1.chatterName().or(getString(0x7f090192))
                        });
                    } else
                    if (unreadmessagesource1.chatterID().getChatterType() == com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group)
                    {
                        s = getString(0x7f0901b9, new Object[] {
                            unreadmessagesource1.chatterName().or(getString(0x7f0900d9))
                        });
                    } else
                    if (unreadmessagesource1.chatterName().isPresent())
                    {
                        s = String.format(getString(0x7f09012d), new Object[] {
                            unreadmessagesource1.chatterName().orNull()
                        });
                    } else
                    {
                        s = null;
                    }
                    if (s != null)
                    {
                        s = (new StringBuilder()).append(s2).append(" ").append(s).toString();
                    } else
                    {
                        s = s2;
                    }
                }
            } else
            {
                s = String.format(getString(0x7f090363), new Object[] {
                    Integer.valueOf(unreadnotificationinfo.freshMessagesCount())
                });
            }
        } else
        if (unreadnotificationinfo.objectPopupInfo().freshObjectPopupsCount() != 0)
        {
            s = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupMessage)unreadnotificationinfo.objectPopupInfo().lastObjectPopup().orNull();
            if (unreadnotificationinfo.objectPopupInfo().freshObjectPopupsCount() == 1 && s != null)
            {
                s = (new StringBuilder()).append(s.objectName()).append(": ").append(s.message()).toString();
                j = s.indexOf("\n");
                s2 = s;
                if (j >= 0)
                {
                    s2 = s.substring(0, j);
                }
                s = s2;
                if (s2.length() > 30)
                {
                    s = (new StringBuilder()).append(s2.substring(0, 30)).append("...").toString();
                }
            } else
            {
                s = getResources().getQuantityString(0x7f110004, unreadnotificationinfo.objectPopupInfo().objectPopupsCount(), new Object[] {
                    Integer.valueOf(unreadnotificationinfo.objectPopupInfo().objectPopupsCount())
                });
            }
        }
        builder.setOngoing(false);
        builder.setNumber(unreadnotificationinfo.totalUnreadCount());
        builder.setContentIntent(PendingIntent.getActivity(this, 0x7f100040, ((Intent) (obj)), 0x8000000));
        builder.setDefaults(0);
        if (s != null)
        {
            builder.setTicker(s);
        }
        builder.setOnlyAlertOnce(true);
        if (s1 != null && !(flag ^ true)) goto _L5; else goto _L4
_L4:
        s1 = LEDAction.None;
        j = 0;
        s = null;
        if (unreadnotificationinfo.totalUnreadCount() > 0)
        {
            s = (NotificationType)unreadnotificationinfo.mostImportantType().orNull();
        }
        s2 = s;
        if (s == null)
        {
            s2 = s;
            if (unreadnotificationinfo.objectPopupInfo().objectPopupsCount() != 0)
            {
                s2 = NotificationType.LocalChat;
            }
        }
        if (s2 != null)
        {
            s = notifySettingsByType(s2);
            s1 = s.getLEDAction();
            j = s.getLEDColor();
        }
        s = null;
        if (unreadnotificationinfo.freshMessagesCount() != 0)
        {
            s = (NotificationType)unreadnotificationinfo.mostImportantFreshType().orNull();
        }
        s2 = s;
        if (s == null)
        {
            s2 = s;
            if (unreadnotificationinfo.objectPopupInfo().freshObjectPopupsCount() != 0)
            {
                s2 = NotificationType.LocalChat;
            }
        }
        if (s2 != null)
        {
            unreadnotificationinfo = notifySettingsByType(s2);
        } else
        {
            unreadnotificationinfo = null;
        }
        Debug.Printf("GridConnectionService: updateUnreadNotification: ledAction = %s, color = %08x", new Object[] {
            s1.toString(), Integer.valueOf(j)
        });
        if (s1 == LEDAction.None) goto _L7; else goto _L6
_L6:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_utils_2D_LEDActionSwitchesValues()[s1.ordinal()];
        JVM INSTR tableswitch 1 4: default 1880
    //                   1 2384
    //                   2 2397
    //                   3 2430
    //                   4 2413;
           goto _L8 _L9 _L10 _L11 _L12
_L8:
        break; /* Loop/switch isn't completed */
_L11:
        break MISSING_BLOCK_LABEL_2430;
_L7:
        if (soundOnNotify && unreadnotificationinfo != null)
        {
            unreadnotificationinfo = unreadnotificationinfo.getRingtone();
            if (unreadnotificationinfo != null)
            {
                builder.setSound(Uri.parse(unreadnotificationinfo));
                builder.setOnlyAlertOnce(false);
            }
        } else
        {
            Debug.Printf("GridConnectionService: will not emit sound.", new Object[0]);
        }
_L5:
        shownNotificationIds.add(Integer.valueOf(i));
        notificationmanager.notify(i, builder.build());
        return;
_L9:
        builder.setLights(j, 1, 0);
          goto _L7
_L10:
        builder.setLights(j, 300, 100);
          goto _L7
_L12:
        builder.setLights(j, 1000, 500);
          goto _L7
        builder.setLights(j, 0, 0);
          goto _L7
        s = null;
          goto _L13
    }

    private void startCloudSync(UserManager usermanager)
    {
        cloudSyncUserManager = usermanager;
        updateCloudSyncStatus();
    }

    private void stopCloudSync()
    {
        cloudSyncUserManager = null;
        updateCloudSyncStatus();
    }

    private void updateCloudSyncStatus()
    {
        if (cloudSyncEnabled && cloudSyncUserManager != null)
        {
            if (cloudSyncServiceConnection == null)
            {
                cloudSyncServiceConnection = new CloudSyncServiceConnection(this, cloudSyncUserManager);
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.cloud", "com.lumiyaviewer.lumiya.cloud.DriveSyncService"));
                boolean flag;
                try
                {
                    flag = bindService(intent, cloudSyncServiceConnection, 1);
                }
                catch (SecurityException securityexception)
                {
                    Debug.Warning(securityexception);
                    flag = false;
                }
                Debug.Printf("LumiyaCloud: bindService = %b", new Object[] {
                    Boolean.valueOf(flag)
                });
                if (!flag)
                {
                    cloudSyncServiceConnection.stopSyncing();
                    Object obj = new Intent("android.intent.action.VIEW");
                    ((Intent) (obj)).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"));
                    cloudSyncServiceConnection.showSyncingError(getString(0x7f0900c6), getString(0x7f0900c7), ((Intent) (obj)));
                    cloudSyncServiceConnection = null;
                    obj = new IntentFilter();
                    ((IntentFilter) (obj)).addAction("android.intent.action.PACKAGE_ADDED");
                    ((IntentFilter) (obj)).addDataScheme("package");
                    if (android.os.Build.VERSION.SDK_INT >= 19)
                    {
                        ((IntentFilter) (obj)).addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.cloud", 0);
                    }
                    registerReceiver(cloudPluginInstalledReceiver, ((IntentFilter) (obj)));
                    cloudPluginReceiverRegistered = true;
                }
            }
            return;
        }
        if (cloudSyncServiceConnection != null)
        {
            cloudSyncServiceConnection.stopSyncing();
            cloudSyncServiceConnection = null;
        }
        if (cloudPluginReceiverRegistered)
        {
            try
            {
                unregisterReceiver(cloudPluginInstalledReceiver);
            }
            catch (IllegalArgumentException illegalargumentexception)
            {
                Debug.Warning(illegalargumentexception);
            }
        }
        cloudPluginReceiverRegistered = false;
    }

    private void updateOnlineNotification()
    {
        boolean flag1;
        if (gridConnection != null)
        {
            Object obj = gridConnection.getConnectionState();
            java.util.UUID uuid;
            boolean flag;
            if (obj != com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle)
            {
                if (GlobalOptions.getInstance().getKeepWifiOn())
                {
                    flag = true;
                } else
                {
                    flag = false;
                }
            } else
            {
                flag = false;
            }
            uuid = gridConnection.getActiveAgentUUID();
            if (uuid != null && obj != com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle)
            {
                flag1 = flag;
                if (connectedAgentNameRetriever == null)
                {
                    connectedAgentNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, uuid), onActiveAgentNameUpdated, UIThreadExecutor.getSerialInstance());
                    flag1 = flag;
                }
            } else
            {
                if (connectedAgentNameRetriever != null)
                {
                    connectedAgentNameRetriever.dispose();
                }
                connectedAgentNameRetriever = null;
                flag1 = flag;
            }
        } else
        {
            flag1 = false;
        }
        if (!flag1 || wifiLock != null) goto _L2; else goto _L1
_L1:
        obj = getApplicationContext();
        if (obj != null)
        {
            wifiLock = ((WifiManager)((Context) (obj)).getSystemService("wifi")).createWifiLock(1, "Lumiya");
            wifiLock.acquire();
            Debug.Printf("WiFi lock acquired", new Object[0]);
        }
_L4:
        obj = new OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, connectedAgentNameRetriever, (CurrentLocationInfo)currentLocationInfo.getData());
        if (!((OnlineNotificationInfo) (obj)).equals(onlineNotificationInfo))
        {
            onlineNotificationInfo = ((OnlineNotificationInfo) (obj));
            obj = ((OnlineNotificationInfo) (obj)).getNotification(this);
            if (obj == null)
            {
                break; /* Loop/switch isn't completed */
            }
            startForeground(0x7f100022, ((android.app.Notification) (obj)));
        }
        return;
_L2:
        if (wifiLock != null && flag1 ^ true)
        {
            wifiLock.release();
            wifiLock = null;
            Debug.Printf("WiFi lock released", new Object[0]);
        }
        if (true) goto _L4; else goto _L3
_L3:
        stopForeground(true);
        return;
    }

    void _2D_com_lumiyaviewer_lumiya_GridConnectionService_2D_mthref_2D_0(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocationInfo(currentlocationinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_GridConnectionService_2D_mthref_2D_1(UnreadNotifications unreadnotifications)
    {
        onUnreadNotification(unreadnotifications);
    }

    public void acceptVoiceCall(ChatterID chatterid)
    {
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.acceptVoiceCall(chatterid);
        }
    }

    public void acceptVoiceCall(VoiceRinging voiceringing)
    {
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.acceptVoiceCall(voiceringing);
        }
    }

    public void enableVoiceMic(boolean flag)
    {
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.enableVoiceMic(flag);
        }
    }

    public VoicePluginServiceConnection getVoicePluginServiceConnection()
    {
        return voicePluginServiceConnection;
    }

    public void handleConnectEvent(SLLoginResultEvent slloginresultevent)
    {
        UserManager usermanager = UserManager.getUserManager(slloginresultevent.activeAgentUUID);
        if (usermanager != null)
        {
            unreadNotifySubscription = usermanager.getUnreadNotificationManager().getUnreadNotifications().subscribe(UnreadNotificationManager.unreadNotificationKey, UIThreadExecutor.getSerialInstance(), new _2D_.Lambda._cls3DowF6pLKgVjVrTY9aZKQ2J3cf0._cls1(this));
            currentLocationInfo.subscribe(usermanager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        updateOnlineNotification();
        VoicePluginServiceConnection.setInstallOfferDisplayed(false);
        if (!slloginresultevent.success)
        {
            Debug.Log("GridConnectionService: stopping self because of connect failed.");
            stopCloudSync();
            stopSelf();
            return;
        } else
        {
            startCloudSync(usermanager);
            return;
        }
    }

    public void handleConnectionStateChangedEvent(SLConnectionStateChangedEvent slconnectionstatechangedevent)
    {
        updateOnlineNotification();
    }

    public void handleDisconnectEvent(SLDisconnectEvent sldisconnectevent)
    {
        updateOnlineNotification();
        Debug.Log("GridConnectionService: stopping self because of disconnect.");
        stopCloudSync();
        stopVoice();
        stopSelf();
        currentLocationInfo.unsubscribe();
        VoicePluginServiceConnection.setInstallOfferDisplayed(false);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_GridConnectionService_20777(ChatterNameRetriever chatternameretriever)
    {
        updateOnlineNotification();
    }

    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public void onCreate()
    {
        super.onCreate();
        serviceInstance = new WeakReference(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        readPreferences(prefs);
        updateOnlineNotification();
    }

    public void onDestroy()
    {
        if (prefs != null)
        {
            prefs.unregisterOnSharedPreferenceChangeListener(this);
            prefs = null;
        }
        onlineNotificationInfo = new OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, connectedAgentNameRetriever, null);
        stopForeground(true);
        shownNotificationIds.clear();
        if (eventBus != null)
        {
            eventBus.unsubscribe(this);
        }
        serviceInstance = null;
        super.onDestroy();
    }

    public void onGlobalPreferencesChanged(GlobalOptions.GlobalOptionsChangedEvent globaloptionschangedevent)
    {
        readPreferences(globaloptionschangedevent.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedpreferences, String s)
    {
        readPreferences(sharedpreferences);
        updateOnlineNotification();
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        String s;
        if (intent != null)
        {
            s = "not null";
        } else
        {
            s = "null";
        }
        Debug.Printf("onStartCommand: intent is %s, flags %08x", new Object[] {
            s, Integer.valueOf(i)
        });
        if ((i & 1) != 0)
        {
            intent = null;
        }
        handleStartService(intent);
        return 2;
    }

    public boolean onUnbind(Intent intent)
    {
        Debug.Log((new StringBuilder()).append("GridConnectionService: onUnbind called, connection state = ").append(gridConnection.getConnectionState()).toString());
        if (gridConnection.getConnectionState() == com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Idle)
        {
            Debug.Log("GridConnectionService: Stopping self because unbind and no clients are bound");
            stopCloudSync();
            stopSelf();
        }
        return super.onUnbind(intent);
    }

    public void startVoice(VoiceLoginInfo voicelogininfo, UserManager usermanager)
    {
        connectToVoicePlugin(voicelogininfo, usermanager);
    }

    public void stopVoice()
    {
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.disconnect();
            voicePluginServiceConnection = null;
        }
    }

    public void terminateVoiceCall(ChatterID chatterid)
    {
        if (voicePluginServiceConnection != null)
        {
            voicePluginServiceConnection.terminateVoiceCall(chatterid);
        }
    }

    static 
    {
        notifyLocalChat = new NotificationSettings(NotificationType.LocalChat);
        notifyPrivate = new NotificationSettings(NotificationType.Private);
        notifyGroup = new NotificationSettings(NotificationType.Group);
    }
}
