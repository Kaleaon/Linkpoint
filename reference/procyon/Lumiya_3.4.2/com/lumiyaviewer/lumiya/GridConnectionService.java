// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya;

import android.os.Binder;
import android.preference.PreferenceManager;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent;
import com.lumiyaviewer.lumiya.eventbus.EventHandler;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging;
import android.app.Notification;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.net.Uri;
import android.app.PendingIntent;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import android.support.v4.app.NotificationCompat;
import javax.annotation.Nonnull;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import android.app.NotificationManager;
import com.lumiyaviewer.lumiya.licensing.LicenseChecker;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.os.Build$VERSION;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ComponentName;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.google.common.base.Strings;
import android.content.Intent;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent;
import android.os.Message;
import android.content.Context;
import java.util.Map;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import java.util.Collections;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams;
import com.lumiyaviewer.lumiya.utils.LEDAction;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import android.net.wifi.WifiManager$WifiLock;
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotifications;
import com.lumiyaviewer.lumiya.react.Subscription;
import android.content.SharedPreferences;
import com.lumiyaviewer.lumiya.ui.notify.OnlineNotificationInfo;
import android.os.IBinder;
import android.os.Handler;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.support.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection;
import android.content.BroadcastReceiver;
import android.app.Activity;
import java.util.Set;
import java.lang.ref.WeakReference;
import com.lumiyaviewer.lumiya.ui.settings.NotificationSettings;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import android.content.SharedPreferences$OnSharedPreferenceChangeListener;
import android.app.Service;

public class GridConnectionService extends Service implements SharedPreferences$OnSharedPreferenceChangeListener
{
    public static final String LOGIN_ACTION = "com.lumiyaviewer.lumiya.ACTION_LOGIN";
    private static final int REQUEST_CODE_UNREAD_NOTIFY = 2131755072;
    private static SLGridConnection gridConnection;
    private static String gridName;
    private static NotificationSettings notifyGroup;
    private static NotificationSettings notifyLocalChat;
    private static NotificationSettings notifyPrivate;
    private static boolean onlineNotify;
    private static WeakReference<GridConnectionService> serviceInstance;
    private static boolean soundOnNotify;
    private static Set<Activity> visibleActivities;
    private final BroadcastReceiver cloudPluginInstalledReceiver;
    private boolean cloudPluginReceiverRegistered;
    private boolean cloudSyncEnabled;
    private CloudSyncServiceConnection cloudSyncServiceConnection;
    @Nullable
    private UserManager cloudSyncUserManager;
    private ChatterNameRetriever connectedAgentNameRetriever;
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo;
    private final EventBus eventBus;
    private Handler licenseCheckHandler;
    private final IBinder mBinder;
    private final Handler mHandler;
    private final ChatterNameRetriever.OnChatterNameUpdated onActiveAgentNameUpdated;
    private OnlineNotificationInfo onlineNotificationInfo;
    private SharedPreferences prefs;
    private final Set<Integer> shownNotificationIds;
    private Subscription<Boolean, UnreadNotifications> unreadNotifySubscription;
    private final BroadcastReceiver voicePluginInstalledReceiver;
    private boolean voicePluginReceiverRegistered;
    private VoicePluginServiceConnection voicePluginServiceConnection;
    private WifiManager$WifiLock wifiLock;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues() {
        if (GridConnectionService.-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues != null) {
            return GridConnectionService.-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues = new int[NotificationType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.LocalChat.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.Private.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues = -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues() {
        if (GridConnectionService.-com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues != null) {
            return GridConnectionService.-com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues = new int[LEDAction.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues[LEDAction.Always.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues[LEDAction.Fast.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues[LEDAction.None.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues[LEDAction.Slow.ordinal()] = 4;
                            return -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues = -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues;
                        }
                        catch (final NoSuchFieldError noSuchFieldError) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError2) {}
                }
                catch (final NoSuchFieldError noSuchFieldError3) {}
            }
            catch (final NoSuchFieldError noSuchFieldError4) {
                continue;
            }
            break;
        }
    }
    
    static {
        GridConnectionService.serviceInstance = null;
        GridConnectionService.onlineNotify = false;
        GridConnectionService.soundOnNotify = true;
        GridConnectionService.notifyLocalChat = new NotificationSettings(NotificationType.LocalChat);
        GridConnectionService.notifyPrivate = new NotificationSettings(NotificationType.Private);
        GridConnectionService.notifyGroup = new NotificationSettings(NotificationType.Group);
        GridConnectionService.gridName = "Second Life";
        GridConnectionService.gridConnection = null;
        GridConnectionService.visibleActivities = Collections.synchronizedSet(new HashSet<Activity>());
    }
    
    public GridConnectionService() {
        this.eventBus = EventBus.getInstance();
        this.mHandler = new Handler();
        this.prefs = null;
        this.cloudSyncEnabled = false;
        this.cloudSyncUserManager = null;
        this.cloudPluginReceiverRegistered = false;
        this.voicePluginReceiverRegistered = false;
        this.currentLocationInfo = new SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(UIThreadExecutor.getInstance(), new _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0(this));
        this.connectedAgentNameRetriever = null;
        this.shownNotificationIds = Collections.newSetFromMap((Map<Integer, Boolean>)Collections.synchronizedMap((Map<E, Boolean>)new HashMap<Object, Boolean>()));
        this.onlineNotificationInfo = new OnlineNotificationInfo(GridConnectionService.onlineNotify, (Context)this, GridConnectionService.gridName, GridConnectionService.gridConnection, this.connectedAgentNameRetriever, null);
        this.wifiLock = null;
        this.mBinder = (IBinder)new GridServiceBinder();
        this.licenseCheckHandler = new Handler() {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    case 2131755033: {
                        Debug.Printf("License: License check ok.", new Object[0]);
                        if (message.obj instanceof SLAuthParams) {
                            GridConnectionService.this.performLogin((SLAuthParams)message.obj);
                            break;
                        }
                        break;
                    }
                    case 2131755035: {
                        Debug.Printf("License: License check failed.", new Object[0]);
                        GridConnectionService.this.eventBus.publish(new SLLoginResultEvent(false, "You don't have valid license to use this application.", null));
                        break;
                    }
                    case 2131755034: {
                        String str;
                        if (message.obj instanceof String) {
                            str = (String)message.obj;
                        }
                        else {
                            str = "Internal application error";
                        }
                        Debug.Printf("License: License check app error: %s", str);
                        GridConnectionService.this.eventBus.publish(new SLLoginResultEvent(false, "License check failed: " + str + ".", null));
                        break;
                    }
                }
            }
        };
        this.cloudPluginInstalledReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.lumiyaviewer.lumiya.cloud") && GridConnectionService.gridConnection != null && GridConnectionService.gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Connected) {
                    final UUID activeAgentUUID = GridConnectionService.gridConnection.getActiveAgentUUID();
                    if (activeAgentUUID != null) {
                        final UserManager userManager = UserManager.getUserManager(activeAgentUUID);
                        if (userManager != null) {
                            GridConnectionService.this.startCloudSync(userManager);
                        }
                    }
                }
            }
        };
        this.voicePluginInstalledReceiver = new BroadcastReceiver() {
            public void onReceive(final Context context, final Intent intent) {
                if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.lumiyaviewer.lumiya.voice") && GridConnectionService.gridConnection != null && GridConnectionService.gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Connected) {
                    final UUID activeAgentUUID = GridConnectionService.gridConnection.getActiveAgentUUID();
                    if (activeAgentUUID != null) {
                        final UserManager userManager = UserManager.getUserManager(activeAgentUUID);
                        if (userManager != null) {
                            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
                            if (activeAgentCircuit != null) {
                                final SLModules modules = activeAgentCircuit.getModules();
                                if (modules != null) {
                                    modules.voice.updateVoiceEnabledStatus();
                                }
                            }
                        }
                    }
                }
            }
        };
        this.cloudSyncServiceConnection = null;
        this.voicePluginServiceConnection = null;
        this.onActiveAgentNameUpdated = new _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$2(this);
        getGridConnection();
        this.eventBus.subscribe(this, null, this.mHandler);
    }
    
    private void connectToVoicePlugin(final VoiceLoginInfo voiceLoginInfo, final UserManager userManager) {
        Label_0190: {
            if (this.voicePluginServiceConnection != null) {
                break Label_0190;
            }
            this.voicePluginServiceConnection = new VoicePluginServiceConnection((Context)this);
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.voice", "com.lumiyaviewer.lumiya.voice.VoiceService"));
            while (true) {
                try {
                    final boolean bindService = this.bindService(intent, (ServiceConnection)this.voicePluginServiceConnection, 1);
                    Debug.Printf("LumiyaVoice: bindService = %b", bindService);
                    if (!bindService) {
                        this.voicePluginServiceConnection = null;
                        final IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
                        intentFilter.addDataScheme("package");
                        if (Build$VERSION.SDK_INT >= 19) {
                            intentFilter.addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.voice", 0);
                        }
                        this.registerReceiver(this.voicePluginInstalledReceiver, intentFilter);
                        this.voicePluginReceiverRegistered = true;
                        if (userManager != null && VoicePluginServiceConnection.shouldDisplayInstallOffer()) {
                            userManager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager.getUserID()), new SLVoiceUpgradeEvent(userManager.getUserID(), LumiyaApp.getContext().getString(2131296889), true, "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"), false);
                        }
                    }
                    if (this.voicePluginServiceConnection != null) {
                        this.voicePluginServiceConnection.setVoiceLoginInfo(voiceLoginInfo, userManager);
                    }
                }
                catch (final SecurityException ex) {
                    Debug.Warning(ex);
                    final boolean bindService = false;
                    continue;
                }
                break;
            }
        }
    }
    
    public static SLGridConnection getGridConnection() {
        if (GridConnectionService.gridConnection == null) {
            GridConnectionService.gridConnection = new SLGridConnection();
        }
        return GridConnectionService.gridConnection;
    }
    
    @Nullable
    public static GridConnectionService getServiceInstance() {
        GridConnectionService gridConnectionService = null;
        if (GridConnectionService.serviceInstance != null) {
            gridConnectionService = GridConnectionService.serviceInstance.get();
        }
        return gridConnectionService;
    }
    
    private void handleStartService(final Intent intent) {
        String s;
        if (intent != null) {
            s = "not null";
        }
        else {
            s = "null";
        }
        Debug.Printf("GridConnectionService: service is now started, intent is %s", s);
        this.updateOnlineNotification();
        if (intent != null) {
            final String nullToEmpty = Strings.nullToEmpty(intent.getAction());
            if (nullToEmpty.equals("com.lumiyaviewer.lumiya.ACTION_LOGIN")) {
                new LicenseChecker(this.getApplicationContext(), this.licenseCheckHandler, new SLAuthParams(intent));
            }
            else if (nullToEmpty.equals("reject")) {
                if (this.voicePluginServiceConnection != null) {
                    this.voicePluginServiceConnection.rejectCall(intent);
                }
            }
            else if (nullToEmpty.equals("accept") && this.voicePluginServiceConnection != null) {
                this.voicePluginServiceConnection.acceptCall(intent);
            }
        }
    }
    
    public static boolean hasVisibleActivities() {
        return GridConnectionService.visibleActivities.isEmpty() ^ true;
    }
    
    private void hideUnreadNotificationSingle(final int n) {
        if (this.shownNotificationIds.contains(n)) {
            this.shownNotificationIds.remove(n);
            ((NotificationManager)this.getSystemService("notification")).cancel(n);
        }
    }
    
    private static NotificationSettings notifySettingsByType(final NotificationType notificationType) {
        switch (-getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues()[notificationType.ordinal()]) {
            default: {
                return GridConnectionService.notifyLocalChat;
            }
            case 2: {
                return GridConnectionService.notifyLocalChat;
            }
            case 1: {
                return GridConnectionService.notifyGroup;
            }
            case 3: {
                return GridConnectionService.notifyPrivate;
            }
        }
    }
    
    private void onCurrentLocationInfo(final CurrentLocationInfo currentLocationInfo) {
        this.updateOnlineNotification();
    }
    
    private void onUnreadNotification(final UnreadNotifications unreadNotifications) {
        this.showUnreadNotification(unreadNotifications);
    }
    
    private void performLogin(final SLAuthParams slAuthParams) {
        GridConnectionService.gridName = slAuthParams.gridName;
        GridConnectionService.gridConnection.Connect(slAuthParams);
    }
    
    private void readPreferences(final SharedPreferences sharedPreferences) {
        GridConnectionService.onlineNotify = sharedPreferences.getBoolean("onlineNotify", true);
        GridConnectionService.soundOnNotify = sharedPreferences.getBoolean("soundOnNotify", true);
        this.cloudSyncEnabled = sharedPreferences.getBoolean("sync_to_gdrive", false);
        GridConnectionService.notifyLocalChat.Load(sharedPreferences);
        GridConnectionService.notifyPrivate.Load(sharedPreferences);
        GridConnectionService.notifyGroup.Load(sharedPreferences);
        SLGridConnection.setAutoresponseInfo(sharedPreferences.getBoolean("autoresponse", false), sharedPreferences.getString("autoresponseText", "(Autoresponse) I have auto-response feature enabled. I will respond shortly."));
        Debug.Log("GridConnectionService: prefs: onlineNotify = " + GridConnectionService.onlineNotify);
        Debug.Log("GridConnectionService: prefs: soundOnNotify = " + GridConnectionService.soundOnNotify);
        while (true) {
            if (GridConnectionService.gridConnection == null) {
                break Label_0151;
            }
            try {
                GridConnectionService.gridConnection.getModules().HandleGlobalOptionsChange();
                this.updateOnlineNotification();
                this.updateCloudSyncStatus();
            }
            catch (final SLGridConnection.NotConnectedException ex) {
                continue;
            }
            break;
        }
    }
    
    public static void setActivityVisible(final Activity activity, final boolean b) {
        Label_0053: {
            if (!b) {
                break Label_0053;
            }
            GridConnectionService.visibleActivities.add(activity);
            while (true) {
                if (GridConnectionService.visibleActivities.isEmpty() || GridConnectionService.gridConnection == null) {
                    return;
                }
                try {
                    if (GridConnectionService.gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Connected) {
                        GridConnectionService.gridConnection.getAgentCircuit().UnpauseAgent();
                    }
                    return;
                    GridConnectionService.visibleActivities.remove(activity);
                    continue;
                }
                catch (final SLGridConnection.NotConnectedException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }
    
    private void showUnreadNotification(final UnreadNotifications unreadNotifications) {
        final NotificationChannels instance = NotificationChannels.getInstance();
        if (instance.useNotificationGroups()) {
            UnreadNotifications filter = unreadNotifications;
            if (instance.areNotificationsSystemControlled()) {
                filter = unreadNotifications.filter(instance.getEnabledTypes((Context)this));
            }
            if (!filter.notificationGroups().isEmpty()) {
                final UnreadNotificationInfo merge = filter.merge();
                this.showUnreadNotificationSingle(merge, 2131755069, instance.getChannelName(instance.getChannelByType(merge.mostImportantFreshType().or(merge.mostImportantType().or(NotificationType.LocalChat)))), "messageNotifications", true, null);
                for (final NotificationType notificationType : NotificationType.VALUES) {
                    final NotificationChannels.Channel channelByType = instance.getChannelByType(notificationType);
                    if (channelByType != null) {
                        this.showUnreadNotificationSingle(filter.notificationGroups().get(notificationType), channelByType.notificationId, instance.getChannelName(channelByType), "messageNotifications", false, Integer.toString(9 - notificationType.getPriority()));
                    }
                }
            }
            else if (!this.shownNotificationIds.isEmpty()) {
                final NotificationManager notificationManager = (NotificationManager)this.getSystemService("notification");
                final Iterator<Object> iterator2 = this.shownNotificationIds.iterator();
                while (iterator2.hasNext()) {
                    notificationManager.cancel((int)iterator2.next());
                }
                this.shownNotificationIds.clear();
            }
        }
        else {
            this.showUnreadNotificationSingle(unreadNotifications.merge(), 2131755069, "miscellaneous", null, true, null);
        }
    }
    
    private void showUnreadNotificationSingle(final UnreadNotificationInfo unreadNotificationInfo, final int i, @Nonnull String ticker, @Nullable final String group, final boolean groupSummary, @Nullable String str) {
        final NotificationManager notificationManager = (NotificationManager)this.getSystemService("notification");
        int n2;
        final int n = n2 = 0;
        Label_0047: {
            if (unreadNotificationInfo != null) {
                if (unreadNotificationInfo.totalUnreadCount() == 0) {
                    n2 = n;
                    if (unreadNotificationInfo.objectPopupInfo().objectPopupsCount() == 0) {
                        break Label_0047;
                    }
                }
                n2 = 1;
            }
        }
        if (n2 != 0) {
            Debug.Log("GridConnectionService: updateUnreadNotification: notification state changed.");
            final NotificationCompat.Builder builder = new NotificationCompat.Builder((Context)this, ticker);
            builder.setSmallIcon(2130837639);
            if (group != null) {
                builder.setGroup(group);
                builder.setGroupSummary(groupSummary);
                builder.setGroupAlertBehavior(2);
                if (str != null) {
                    builder.setSortKey(str);
                }
            }
            final UserManager userManager = UserManager.getUserManager(unreadNotificationInfo.agentUUID());
            Object o;
            if (unreadNotificationInfo.unreadSources().size() == 1) {
                o = unreadNotificationInfo.unreadSources().get(0);
            }
            else {
                o = null;
            }
            final ChatFragmentActivityFactory instance = ChatFragmentActivityFactory.getInstance();
            Bundle selection;
            if (o != null) {
                selection = ChatFragment.makeSelection(((UnreadNotificationInfo.UnreadMessageSource)o).chatterID());
            }
            else {
                selection = null;
            }
            Intent intent = instance.createIntent((Context)this, selection);
            intent.addFlags(536870912);
            ActivityUtils.setActiveAgentID(intent, unreadNotificationInfo.agentUUID());
            UnreadNotificationInfo.UnreadMessageSource unreadMessageSource;
            if (unreadNotificationInfo.singleFreshSource().isPresent()) {
                unreadMessageSource = (UnreadNotificationInfo.UnreadMessageSource)unreadNotificationInfo.singleFreshSource().get();
            }
            else {
                final Iterator<Object> iterator = unreadNotificationInfo.unreadSources().iterator();
                unreadMessageSource = null;
                while (iterator.hasNext()) {
                    UnreadNotificationInfo.UnreadMessageSource unreadMessageSource3;
                    final UnreadNotificationInfo.UnreadMessageSource unreadMessageSource2 = unreadMessageSource3 = iterator.next();
                    if (unreadMessageSource != null) {
                        if (unreadMessageSource2.unreadMessagesCount() > unreadMessageSource.unreadMessagesCount()) {
                            unreadMessageSource3 = unreadMessageSource2;
                        }
                        else {
                            unreadMessageSource3 = unreadMessageSource;
                        }
                    }
                    unreadMessageSource = unreadMessageSource3;
                }
            }
            if (unreadMessageSource != null) {
                intent.putExtra("weakSelection", ChatFragment.makeSelection(unreadMessageSource.chatterID()));
            }
            if (userManager != null) {
                final Intent captureNotify = userManager.getUnreadNotificationManager().captureNotify(unreadNotificationInfo, intent);
                if (captureNotify != null) {
                    intent = captureNotify;
                }
            }
            while (true) {
                Label_0630: {
                    if (o != null && unreadNotificationInfo.objectPopupInfo().objectPopupsCount() == 0) {
                        final String s = ((UnreadNotificationInfo.UnreadMessageSource)o).chatterName().or(GridConnectionService.gridName);
                        builder.setContentTitle(s);
                        if (((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessagesCount() == 1 && ((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessages().size() == 1) {
                            builder.setContentText(((SLChatEvent)((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessages().get(0)).getPlainTextMessage((Context)this, ((UnreadNotificationInfo.UnreadMessageSource)o).chatterID().getUserManager(), true));
                        }
                        else {
                            builder.setContentText(String.format(this.getString(2131297123), ((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessagesCount()));
                        }
                        if (((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessages().size() > 1) {
                            final NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                            final Iterator<Object> iterator2 = ((UnreadNotificationInfo.UnreadMessageSource)o).unreadMessages().iterator();
                            while (iterator2.hasNext()) {
                                style.addLine(iterator2.next().getPlainTextMessage((Context)this, ((UnreadNotificationInfo.UnreadMessageSource)o).chatterID().getUserManager(), ((UnreadNotificationInfo.UnreadMessageSource)o).chatterID().getChatterType() == ChatterID.ChatterType.User, "  ", " "));
                            }
                            style.setBigContentTitle(s);
                            style.setSummaryText(String.format(this.getString(2131297123), unreadNotificationInfo.totalUnreadCount()));
                            builder.setStyle(style);
                        }
                    }
                    else {
                        if (unreadNotificationInfo.totalUnreadCount() != 0) {
                            str = (ticker = this.getResources().getQuantityString(2131820547, unreadNotificationInfo.totalUnreadCount(), new Object[] { unreadNotificationInfo.totalUnreadCount() }));
                            if (unreadNotificationInfo.objectPopupInfo().objectPopupsCount() != 0) {
                                ticker = str + this.getResources().getQuantityString(2131820544, unreadNotificationInfo.objectPopupInfo().objectPopupsCount(), new Object[] { unreadNotificationInfo.objectPopupInfo().objectPopupsCount() });
                            }
                            builder.setContentTitle(ticker);
                            if (unreadNotificationInfo.totalUnreadCount() != 0 || unreadNotificationInfo.objectPopupInfo().objectPopupsCount() == 0) {
                                builder.setContentTitle(this.getResources().getQuantityString(2131820547, unreadNotificationInfo.totalUnreadCount(), new Object[] { unreadNotificationInfo.totalUnreadCount() }));
                            }
                            else {
                                builder.setContentTitle(this.getResources().getQuantityString(2131820548, unreadNotificationInfo.objectPopupInfo().objectPopupsCount(), new Object[] { unreadNotificationInfo.objectPopupInfo().objectPopupsCount() }));
                            }
                            while (true) {
                                for (final UnreadNotificationInfo.UnreadMessageSource unreadMessageSource4 : unreadNotificationInfo.unreadSources()) {
                                    if (unreadMessageSource4.chatterName().isPresent()) {
                                        ticker = unreadMessageSource4.chatterName().orNull();
                                        final int size = unreadNotificationInfo.unreadSources().size();
                                        if (ticker != null) {
                                            if (size > 1) {
                                                ticker = ticker + " " + String.format(this.getString(2131296851), size - 1);
                                            }
                                        }
                                        else {
                                            ticker = String.format(this.getString(2131296434), size);
                                        }
                                        builder.setContentText(ticker);
                                        final NotificationCompat.InboxStyle style2 = new NotificationCompat.InboxStyle();
                                        for (final UnreadNotificationInfo.UnreadMessageSource unreadMessageSource5 : unreadNotificationInfo.unreadSources()) {
                                            final Iterator<Object> iterator5 = unreadMessageSource5.unreadMessages().iterator();
                                            while (iterator5.hasNext()) {
                                                style2.addLine(iterator5.next().getPlainTextMessage((Context)this, unreadMessageSource5.chatterID().getUserManager(), false, "  ", " "));
                                            }
                                        }
                                        style2.setBigContentTitle(String.format(this.getString(2131297123), unreadNotificationInfo.totalUnreadCount()));
                                        style2.setSummaryText(ticker);
                                        builder.setStyle(style2);
                                        break Label_0630;
                                    }
                                }
                                ticker = null;
                                continue;
                            }
                        }
                        if (unreadNotificationInfo.objectPopupInfo().objectPopupsCount() != 0) {
                            intent.putExtra("objectPopupNotification", true);
                            if (intent.getBundleExtra("selection") == null && intent.getBundleExtra("weakSelection") == null) {
                                intent.putExtra("selection", ChatFragment.makeSelection(ChatterID.getLocalChatterID(unreadNotificationInfo.agentUUID())));
                            }
                            final UnreadNotificationInfo.ObjectPopupMessage objectPopupMessage = unreadNotificationInfo.objectPopupInfo().lastObjectPopup().orNull();
                            if (unreadNotificationInfo.objectPopupInfo().freshObjectPopupsCount() == 1 && objectPopupMessage != null) {
                                builder.setContentTitle(objectPopupMessage.objectName());
                                builder.setContentText(objectPopupMessage.message());
                            }
                            else {
                                builder.setContentTitle(this.getResources().getQuantityString(2131820548, unreadNotificationInfo.objectPopupInfo().objectPopupsCount(), new Object[] { unreadNotificationInfo.objectPopupInfo().objectPopupsCount() }));
                                if (objectPopupMessage != null) {
                                    builder.setContentText(objectPopupMessage.objectName() + ": " + objectPopupMessage.message());
                                }
                                else {
                                    builder.setContentText(this.getResources().getQuantityString(2131820548, unreadNotificationInfo.objectPopupInfo().objectPopupsCount(), new Object[] { unreadNotificationInfo.objectPopupInfo().objectPopupsCount() }));
                                }
                            }
                        }
                        else {
                            n2 = 0;
                        }
                    }
                }
                if (n2 == 0) {
                    this.hideUnreadNotificationSingle(i);
                    return;
                }
                ticker = null;
                if (unreadNotificationInfo.freshMessagesCount() > 0) {
                    final UnreadNotificationInfo.UnreadMessageSource unreadMessageSource6 = unreadNotificationInfo.singleFreshSource().orNull();
                    if (unreadMessageSource6 != null) {
                        if (unreadNotificationInfo.freshMessagesCount() == 1 && unreadMessageSource6.unreadMessages().size() > 0) {
                            final SLChatEvent slChatEvent = unreadMessageSource6.unreadMessages().get(unreadMessageSource6.unreadMessages().size() - 1);
                            if (unreadMessageSource6.chatterID().getChatterType() == ChatterID.ChatterType.Local) {
                                ticker = this.getString(2131296440);
                            }
                            else if (unreadMessageSource6.chatterID().getChatterType() == ChatterID.ChatterType.Group) {
                                ticker = this.getString(2131296438);
                            }
                            else {
                                ticker = this.getString(2131296439);
                            }
                            ticker = "[" + ticker + "] " + slChatEvent.getPlainTextMessage((Context)this, unreadMessageSource6.chatterID().getUserManager(), false).toString().trim();
                            final int index = ticker.indexOf("\n");
                            str = ticker;
                            if (index >= 0) {
                                str = ticker.substring(0, index);
                            }
                            ticker = str;
                            if (str.length() > 30) {
                                ticker = str.substring(0, 30) + "...";
                            }
                        }
                        else {
                            str = String.format(this.getString(2131297123), unreadNotificationInfo.freshMessagesCount());
                            if (unreadMessageSource6.chatterID().getChatterType() == ChatterID.ChatterType.Local) {
                                ticker = this.getString(2131296697, new Object[] { unreadMessageSource6.chatterName().or(this.getString(2131296658)) });
                            }
                            else if (unreadMessageSource6.chatterID().getChatterType() == ChatterID.ChatterType.Group) {
                                ticker = this.getString(2131296697, new Object[] { unreadMessageSource6.chatterName().or(this.getString(2131296473)) });
                            }
                            else if (unreadMessageSource6.chatterName().isPresent()) {
                                ticker = String.format(this.getString(2131296557), unreadMessageSource6.chatterName().orNull());
                            }
                            else {
                                ticker = null;
                            }
                            if (ticker != null) {
                                ticker = str + " " + ticker;
                            }
                            else {
                                ticker = str;
                            }
                        }
                    }
                    else {
                        ticker = String.format(this.getString(2131297123), unreadNotificationInfo.freshMessagesCount());
                    }
                }
                else if (unreadNotificationInfo.objectPopupInfo().freshObjectPopupsCount() != 0) {
                    final UnreadNotificationInfo.ObjectPopupMessage objectPopupMessage2 = unreadNotificationInfo.objectPopupInfo().lastObjectPopup().orNull();
                    if (unreadNotificationInfo.objectPopupInfo().freshObjectPopupsCount() == 1 && objectPopupMessage2 != null) {
                        ticker = objectPopupMessage2.objectName() + ": " + objectPopupMessage2.message();
                        final int index2 = ticker.indexOf("\n");
                        str = ticker;
                        if (index2 >= 0) {
                            str = ticker.substring(0, index2);
                        }
                        ticker = str;
                        if (str.length() > 30) {
                            ticker = str.substring(0, 30) + "...";
                        }
                    }
                    else {
                        ticker = this.getResources().getQuantityString(2131820548, unreadNotificationInfo.objectPopupInfo().objectPopupsCount(), new Object[] { unreadNotificationInfo.objectPopupInfo().objectPopupsCount() });
                    }
                }
                builder.setOngoing(false);
                builder.setNumber(unreadNotificationInfo.totalUnreadCount());
                builder.setContentIntent(PendingIntent.getActivity((Context)this, 2131755072, intent, 134217728));
                builder.setDefaults(0);
                if (ticker != null) {
                    builder.setTicker(ticker);
                }
                builder.setOnlyAlertOnce(true);
                if (group == null || (groupSummary ^ true)) {
                    LEDAction ledAction = LEDAction.None;
                    int ledColor = 0;
                    NotificationType notificationType = null;
                    if (unreadNotificationInfo.totalUnreadCount() > 0) {
                        notificationType = unreadNotificationInfo.mostImportantType().orNull();
                    }
                    NotificationType localChat;
                    if ((localChat = notificationType) == null) {
                        localChat = notificationType;
                        if (unreadNotificationInfo.objectPopupInfo().objectPopupsCount() != 0) {
                            localChat = NotificationType.LocalChat;
                        }
                    }
                    if (localChat != null) {
                        final NotificationSettings notifySettingsByType = notifySettingsByType(localChat);
                        ledAction = notifySettingsByType.getLEDAction();
                        ledColor = notifySettingsByType.getLEDColor();
                    }
                    NotificationType notificationType2 = null;
                    if (unreadNotificationInfo.freshMessagesCount() != 0) {
                        notificationType2 = unreadNotificationInfo.mostImportantFreshType().orNull();
                    }
                    NotificationType localChat2;
                    if ((localChat2 = notificationType2) == null) {
                        localChat2 = notificationType2;
                        if (unreadNotificationInfo.objectPopupInfo().freshObjectPopupsCount() != 0) {
                            localChat2 = NotificationType.LocalChat;
                        }
                    }
                    NotificationSettings notifySettingsByType2;
                    if (localChat2 != null) {
                        notifySettingsByType2 = notifySettingsByType(localChat2);
                    }
                    else {
                        notifySettingsByType2 = null;
                    }
                    Debug.Printf("GridConnectionService: updateUnreadNotification: ledAction = %s, color = %08x", ledAction.toString(), ledColor);
                    if (ledAction != LEDAction.None) {
                        switch (-getcom-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues()[ledAction.ordinal()]) {
                            case 1: {
                                builder.setLights(ledColor, 1, 0);
                                break;
                            }
                            case 2: {
                                builder.setLights(ledColor, 300, 100);
                                break;
                            }
                            case 4: {
                                builder.setLights(ledColor, 1000, 500);
                                break;
                            }
                            case 3: {
                                builder.setLights(ledColor, 0, 0);
                                break;
                            }
                        }
                    }
                    if (GridConnectionService.soundOnNotify && notifySettingsByType2 != null) {
                        final String ringtone = notifySettingsByType2.getRingtone();
                        if (ringtone != null) {
                            builder.setSound(Uri.parse(ringtone));
                            builder.setOnlyAlertOnce(false);
                        }
                    }
                    else {
                        Debug.Printf("GridConnectionService: will not emit sound.", new Object[0]);
                    }
                }
                this.shownNotificationIds.add(i);
                notificationManager.notify(i, builder.build());
                return;
                continue;
            }
        }
        this.hideUnreadNotificationSingle(i);
    }
    
    private void startCloudSync(final UserManager cloudSyncUserManager) {
        this.cloudSyncUserManager = cloudSyncUserManager;
        this.updateCloudSyncStatus();
    }
    
    private void stopCloudSync() {
        this.cloudSyncUserManager = null;
        this.updateCloudSyncStatus();
    }
    
    private void updateCloudSyncStatus() {
        Label_0215: {
            if (!this.cloudSyncEnabled || this.cloudSyncUserManager == null) {
                break Label_0215;
            }
            if (this.cloudSyncServiceConnection != null) {
                return;
            }
            this.cloudSyncServiceConnection = new CloudSyncServiceConnection((Context)this, this.cloudSyncUserManager);
            final Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.lumiyaviewer.lumiya.cloud", "com.lumiyaviewer.lumiya.cloud.DriveSyncService"));
            while (true) {
                try {
                    final boolean bindService = this.bindService(intent, (ServiceConnection)this.cloudSyncServiceConnection, 1);
                    Debug.Printf("LumiyaCloud: bindService = %b", bindService);
                    if (!bindService) {
                        this.cloudSyncServiceConnection.stopSyncing();
                        final Intent intent2 = new Intent("android.intent.action.VIEW");
                        intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"));
                        this.cloudSyncServiceConnection.showSyncingError(this.getString(2131296454), this.getString(2131296455), intent2);
                        this.cloudSyncServiceConnection = null;
                        final IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
                        intentFilter.addDataScheme("package");
                        if (Build$VERSION.SDK_INT >= 19) {
                            intentFilter.addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.cloud", 0);
                        }
                        this.registerReceiver(this.cloudPluginInstalledReceiver, intentFilter);
                        this.cloudPluginReceiverRegistered = true;
                    }
                    return;
                }
                catch (final SecurityException ex) {
                    Debug.Warning(ex);
                    final boolean bindService = false;
                    continue;
                }
                break;
            }
        }
        if (this.cloudSyncServiceConnection != null) {
            this.cloudSyncServiceConnection.stopSyncing();
            this.cloudSyncServiceConnection = null;
        }
        while (true) {
            if (!this.cloudPluginReceiverRegistered) {
                break Label_0249;
            }
            try {
                this.unregisterReceiver(this.cloudPluginInstalledReceiver);
                this.cloudPluginReceiverRegistered = false;
            }
            catch (final IllegalArgumentException ex2) {
                Debug.Warning(ex2);
                continue;
            }
            break;
        }
    }
    
    private void updateOnlineNotification() {
        boolean b2;
        if (GridConnectionService.gridConnection != null) {
            final SLGridConnection.ConnectionState connectionState = GridConnectionService.gridConnection.getConnectionState();
            boolean b;
            if (connectionState != SLGridConnection.ConnectionState.Idle) {
                if (GlobalOptions.getInstance().getKeepWifiOn()) {
                    b = true;
                }
                else {
                    b = false;
                }
            }
            else {
                b = false;
            }
            final UUID activeAgentUUID = GridConnectionService.gridConnection.getActiveAgentUUID();
            if (activeAgentUUID != null && connectionState != SLGridConnection.ConnectionState.Idle) {
                b2 = b;
                if (this.connectedAgentNameRetriever == null) {
                    this.connectedAgentNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentUUID, activeAgentUUID), this.onActiveAgentNameUpdated, UIThreadExecutor.getSerialInstance());
                    b2 = b;
                }
            }
            else {
                if (this.connectedAgentNameRetriever != null) {
                    this.connectedAgentNameRetriever.dispose();
                }
                this.connectedAgentNameRetriever = null;
                b2 = b;
            }
        }
        else {
            b2 = false;
        }
        if (b2 && this.wifiLock == null) {
            final Context applicationContext = this.getApplicationContext();
            if (applicationContext != null) {
                (this.wifiLock = ((WifiManager)applicationContext.getSystemService("wifi")).createWifiLock(1, "Lumiya")).acquire();
                Debug.Printf("WiFi lock acquired", new Object[0]);
            }
        }
        else if (this.wifiLock != null && (b2 ^ true)) {
            this.wifiLock.release();
            this.wifiLock = null;
            Debug.Printf("WiFi lock released", new Object[0]);
        }
        final OnlineNotificationInfo onlineNotificationInfo = new OnlineNotificationInfo(GridConnectionService.onlineNotify, (Context)this, GridConnectionService.gridName, GridConnectionService.gridConnection, this.connectedAgentNameRetriever, this.currentLocationInfo.getData());
        if (!onlineNotificationInfo.equals(this.onlineNotificationInfo)) {
            this.onlineNotificationInfo = onlineNotificationInfo;
            final Notification notification = onlineNotificationInfo.getNotification((Context)this);
            if (notification != null) {
                this.startForeground(2131755042, notification);
            }
            else {
                this.stopForeground(true);
            }
        }
    }
    
    public void acceptVoiceCall(final ChatterID chatterID) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.acceptVoiceCall(chatterID);
        }
    }
    
    public void acceptVoiceCall(final VoiceRinging voiceRinging) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.acceptVoiceCall(voiceRinging);
        }
    }
    
    public void enableVoiceMic(final boolean b) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.enableVoiceMic(b);
        }
    }
    
    @javax.annotation.Nullable
    public VoicePluginServiceConnection getVoicePluginServiceConnection() {
        return this.voicePluginServiceConnection;
    }
    
    @EventHandler
    public void handleConnectEvent(final SLLoginResultEvent slLoginResultEvent) {
        final UserManager userManager = UserManager.getUserManager(slLoginResultEvent.activeAgentUUID);
        if (userManager != null) {
            this.unreadNotifySubscription = userManager.getUnreadNotificationManager().getUnreadNotifications().subscribe(UnreadNotificationManager.unreadNotificationKey, UIThreadExecutor.getSerialInstance(), new _$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0$1(this));
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        this.updateOnlineNotification();
        VoicePluginServiceConnection.setInstallOfferDisplayed(false);
        if (!slLoginResultEvent.success) {
            Debug.Log("GridConnectionService: stopping self because of connect failed.");
            this.stopCloudSync();
            this.stopSelf();
        }
        else {
            this.startCloudSync(userManager);
        }
    }
    
    @EventHandler
    public void handleConnectionStateChangedEvent(final SLConnectionStateChangedEvent slConnectionStateChangedEvent) {
        this.updateOnlineNotification();
    }
    
    @EventHandler
    public void handleDisconnectEvent(final SLDisconnectEvent slDisconnectEvent) {
        this.updateOnlineNotification();
        Debug.Log("GridConnectionService: stopping self because of disconnect.");
        this.stopCloudSync();
        this.stopVoice();
        this.stopSelf();
        this.currentLocationInfo.unsubscribe();
        VoicePluginServiceConnection.setInstallOfferDisplayed(false);
    }
    
    public IBinder onBind(final Intent intent) {
        return this.mBinder;
    }
    
    public void onCreate() {
        super.onCreate();
        GridConnectionService.serviceInstance = new WeakReference<GridConnectionService>(this);
        (this.prefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext())).registerOnSharedPreferenceChangeListener((SharedPreferences$OnSharedPreferenceChangeListener)this);
        this.readPreferences(this.prefs);
        this.updateOnlineNotification();
    }
    
    public void onDestroy() {
        if (this.prefs != null) {
            this.prefs.unregisterOnSharedPreferenceChangeListener((SharedPreferences$OnSharedPreferenceChangeListener)this);
            this.prefs = null;
        }
        this.onlineNotificationInfo = new OnlineNotificationInfo(GridConnectionService.onlineNotify, (Context)this, GridConnectionService.gridName, GridConnectionService.gridConnection, this.connectedAgentNameRetriever, null);
        this.stopForeground(true);
        this.shownNotificationIds.clear();
        if (this.eventBus != null) {
            this.eventBus.unsubscribe(this);
        }
        GridConnectionService.serviceInstance = null;
        super.onDestroy();
    }
    
    @EventHandler
    public void onGlobalPreferencesChanged(final GlobalOptions.GlobalOptionsChangedEvent globalOptionsChangedEvent) {
        this.readPreferences(globalOptionsChangedEvent.preferences);
    }
    
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String s) {
        this.readPreferences(sharedPreferences);
        this.updateOnlineNotification();
    }
    
    public int onStartCommand(Intent intent, final int i, final int n) {
        String s;
        if (intent != null) {
            s = "not null";
        }
        else {
            s = "null";
        }
        Debug.Printf("onStartCommand: intent is %s, flags %08x", s, i);
        if ((i & 0x1) != 0x0) {
            intent = null;
        }
        this.handleStartService(intent);
        return 2;
    }
    
    public boolean onUnbind(final Intent intent) {
        Debug.Log("GridConnectionService: onUnbind called, connection state = " + GridConnectionService.gridConnection.getConnectionState());
        if (GridConnectionService.gridConnection.getConnectionState() == SLGridConnection.ConnectionState.Idle) {
            Debug.Log("GridConnectionService: Stopping self because unbind and no clients are bound");
            this.stopCloudSync();
            this.stopSelf();
        }
        return super.onUnbind(intent);
    }
    
    public void startVoice(final VoiceLoginInfo voiceLoginInfo, final UserManager userManager) {
        this.connectToVoicePlugin(voiceLoginInfo, userManager);
    }
    
    public void stopVoice() {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.disconnect();
            this.voicePluginServiceConnection = null;
        }
    }
    
    public void terminateVoiceCall(final ChatterID chatterID) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.terminateVoiceCall(chatterID);
        }
    }
    
    public class GridServiceBinder extends Binder
    {
        public EventBus getEventBus() {
            return GridConnectionService.this.eventBus;
        }
        
        public SLGridConnection getGridConn() {
            return GridConnectionService.gridConnection;
        }
    }
}
