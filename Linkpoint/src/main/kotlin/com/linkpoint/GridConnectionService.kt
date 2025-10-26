package com.linkpoint

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.WifiLock
import android.os.Binder
import android.os.Build.VERSION
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.preference.PreferenceManager
import android.support.annotation.Nullable
import com.google.common.base.Strings
import com.linkpoint.GlobalOptions.GlobalOptionsChangedEvent
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.licensing.LicenseChecker
import com.linkpoint.react.Subscription
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.SLGridConnection.ConnectionState
import com.linkpoint.slproto.SLGridConnection.NotConnectedException
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.chat.SLVoiceUpgradeEvent
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.events.SLLoginResultEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.slproto.users.manager.UnreadNotificationManager
import com.linkpoint.slproto.users.manager.UnreadNotifications
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.sync.CloudSyncServiceConnection
import com.linkpoint.ui.notify.DummyNotificationChannelManager
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.notify.NotificationChannels.Channel
import com.linkpoint.ui.notify.OnlineNotificationInfo
import com.linkpoint.ui.settings.NotificationSettings
import com.linkpoint.ui.settings.NotificationType
import com.linkpoint.utils.LEDAction
import com.linkpoint.voice.common.messages.VoiceRinging
import com.linkpoint.voice.common.model.VoiceLoginInfo
import com.linkpoint.voiceintf.VoicePluginServiceConnection
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.Set
import java.util.UUID

class GridConnectionService : Service(), OnSharedPreferenceChangeListener {
    /* renamed from: -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues */
    private /* synthetic */ val f442Values: IntArray? = null
    /* renamed from: -com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues */
    private /* synthetic */ val f443Values: IntArray? = null
    
    companion object {
        const val LOGIN_ACTION: String = "com.linkpoint.ACTION_LOGIN"
    private const val REQUEST_CODE_UNREAD_NOTIFY: Int = 2131755072
    @JvmStatic
private SLGridConnection gridConnection = null
    @JvmStatic
private String gridName = "Second Life"
    @JvmStatic
private NotificationSettings notifyGroup = NotificationSettings(NotificationType.Group)
    @JvmStatic
private NotificationSettings notifyLocalChat = NotificationSettings(NotificationType.LocalChat)
    @JvmStatic
private NotificationSettings notifyPrivate = NotificationSettings(NotificationType.Private)
    @JvmStatic
private Boolean onlineNotify = false
    @JvmStatic
private WeakReference<GridConnectionService> serviceInstance = null
    @JvmStatic
private Boolean soundOnNotify = true
    @JvmStatic
private Set<Activity> visibleActivities = Collections.synchronizedSet(HashSet())
    private val BroadcastReceiver cloudPluginInstalledReceiver = BroadcastReceiver() {
        fun onReceive(context: Context, intent: Intent) {
            if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.linkpoint.cloud") && GridConnectionService.gridConnection != null && GridConnectionService.gridConnection.getConnectionState() == ConnectionState.Connected) {
                val activeAgentUUID: UUID = GridConnectionService.gridConnection.getActiveAgentUUID()
                if (activeAgentUUID != null) {
                    val userManager: UserManager = UserManager.getUserManager(activeAgentUUID)
                    if (userManager != null) {
                        GridConnectionService.this.startCloudSync(userManager)
                    }
                }
            }
        }
    }
    private Boolean cloudPluginReceiverRegistered = false
    private Boolean cloudSyncEnabled = false
    private CloudSyncServiceConnection cloudSyncServiceConnection = null
    private UserManager cloudSyncUserManager = null
    private ChatterNameRetriever connectedAgentNameRetriever = null
    private val SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = SubscriptionData(UIThreadExecutor.getInstance(), () -> { // Lambda implementation })
    private val EventBus eventBus = EventBus.getInstance()
    private Handler licenseCheckHandler = Handler() {
        fun handleMessage(message: Message) {
            switch (message.what) {
                case R.id.msg_licensing_allow /*2131755033*/:
                    Debug.Printf("License: License check ok.", Object[0])
                    if (message.obj instanceof SLAuthParams) {
                        GridConnectionService.this.performLogin((SLAuthParams) message.obj)
                        return
                    }
                    return
                case R.id.msg_licensing_app_error /*2131755034*/:
                    Debug.Printf("License: License check app error: %s", message.obj instanceof String ? (String) message.obj : "Internal application error")
                    GridConnectionService.this.eventBus.publish(SLLoginResultEvent(false, "License check failed: " + r0 + ".", null))
                    return
                case R.id.msg_licensing_dont_allow /*2131755035*/:
                    Debug.Printf("License: License check failed.", Object[0])
                    GridConnectionService.this.eventBus.publish(SLLoginResultEvent(false, "You don't have valid license to use this application.", null))
                    return
                default:
                    return
            }
        }
    }
    private val IBinder mBinder = GridServiceBinder()
    private val Handler mHandler = Handler()
    private val OnChatterNameUpdated onActiveAgentNameUpdated = com.lumiyaviewer.lumiya.-$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0.AnonymousClass2(this)
    private OnlineNotificationInfo onlineNotificationInfo = OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, this.connectedAgentNameRetriever, null)
    private SharedPreferences prefs = null
    private val Set<Integer> shownNotificationIds = Collections.newSetFromMap(Collections.synchronizedMap(HashMap()))
    private Subscription<Boolean, UnreadNotifications> unreadNotifySubscription
    private val BroadcastReceiver voicePluginInstalledReceiver = BroadcastReceiver() {
        fun onReceive(context: Context, intent: Intent) {
            if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.linkpoint.voice") && GridConnectionService.gridConnection != null && GridConnectionService.gridConnection.getConnectionState() == ConnectionState.Connected) {
                val activeAgentUUID: UUID = GridConnectionService.gridConnection.getActiveAgentUUID()
                if (activeAgentUUID != null) {
                    val userManager: UserManager = UserManager.getUserManager(activeAgentUUID)
                    if (userManager != null) {
                        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
                        if (activeAgentCircuit != null) {
                            val modules: SLModules = activeAgentCircuit.getModules()
                            if (modules != null) {
                                modules.voice.updateVoiceEnabledStatus()
                            }
                        }
                    }
                }
            }
        }
    }
    private Boolean voicePluginReceiverRegistered = false
    private VoicePluginServiceConnection voicePluginServiceConnection = null
    private WifiLock wifiLock = null

    class GridServiceBinder : Binder() {
         public fun getEventBus(): EventBus {
            return GridConnectionService.this.eventBus
        }

         public fun getGridConn(): SLGridConnection {
            return GridConnectionService.gridConnection
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues */
    @JvmStatic
private /* synthetic */ IntArray m8-getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues() {
        if (f442-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues != null) {
            return f442-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues
        }
        val iArr: IntArray = Int[NotificationType.values().length]
        try {
            iArr[NotificationType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[NotificationType.LocalChat.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[NotificationType.Private.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f442-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues = iArr
        return iArr
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues */
    @JvmStatic
private /* synthetic */ IntArray m9-getcom-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues() {
        if (f443-com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues != null) {
            return f443-com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues
        }
        val iArr: IntArray = Int[LEDAction.values().length]
        try {
            iArr[LEDAction.Always.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[LEDAction.Fast.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[LEDAction.None.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[LEDAction.Slow.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        f443-com-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues = iArr
        return iArr
    }

    public GridConnectionService() {
        getGridConnection()
        this.eventBus.subscribe(this, null, this.mHandler)
    }

     private fun connectToVoicePlugin(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager) {
        if (this.voicePluginServiceConnection == null) {
            Boolean bindService
            this.voicePluginServiceConnection = VoicePluginServiceConnection(this)
            val intent: Intent = Intent()
            intent.setComponent(ComponentName("com.linkpoint.voice", "com.linkpoint.voice.VoiceService"))
            try {
                bindService = bindService(intent, this.voicePluginServiceConnection, 1)
            } catch (Throwable e) {
                Debug.Warning(e)
                bindService = false
            }
            Debug.Printf("LinkpointVoice: bindService = %b", Boolean.valueOf(bindService))
            if (!bindService) {
                this.voicePluginServiceConnection = null
                val intentFilter: IntentFilter = IntentFilter()
                intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
                intentFilter.addDataScheme("package")
                if (VERSION.SDK_INT >= 19) {
                    intentFilter.addDataSchemeSpecificPart("com.linkpoint.voice", 0)
                }
                registerReceiver(this.voicePluginInstalledReceiver, intentFilter)
                this.voicePluginReceiverRegistered = true
                if (userManager != null && VoicePluginServiceConnection.shouldDisplayInstallOffer()) {
                    userManager.getChatterList().getActiveChattersManager().HandleChatEvent(ChatterID.getLocalChatterID(userManager.getUserID()), SLVoiceUpgradeEvent(userManager.getUserID(), LinkpointApp.getContext().getString(R.string.plugin_install_for_voice_needed), true, LicenseChecker.VOICE_PLUGIN_URL), false)
                }
            }
        }
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.setVoiceLoginInfo(voiceLoginInfo, userManager)
        }
    }

    @JvmStatic
     fun getGridConnection(): SLGridConnection {
        if (gridConnection == null) {
            gridConnection = SLGridConnection()
        }
        return gridConnection
    }

    @JvmStatic
     fun getServiceInstance(): GridConnectionService {
        return serviceInstance != null ? (GridConnectionService) serviceInstance.get() : null
    }

     private fun handleStartService(intent: Intent) {
        val str: String = "GridConnectionService: service is now started, intent is %s"
        val objArr: Array<Any> = Object[1]
        objArr[0] = intent != null ? "not null" : "null"
        Debug.Printf(str, objArr)
        updateOnlineNotification()
        if (intent != null) {
            val nullToEmpty: String = Strings.nullToEmpty(intent.getAction())
            if (nullToEmpty.equals(LOGIN_ACTION)) {
                val licenseChecker: LicenseChecker = LicenseChecker(getApplicationContext(), this.licenseCheckHandler, SLAuthParams(intent))
            } else if (nullToEmpty.equals(VoicePluginServiceConnection.ACTION_VOICE_REJECT)) {
                if (this.voicePluginServiceConnection != null) {
                    this.voicePluginServiceConnection.rejectCall(intent)
                }
            } else if (nullToEmpty.equals(VoicePluginServiceConnection.ACTION_VOICE_ACCEPT) && this.voicePluginServiceConnection != null) {
                this.voicePluginServiceConnection.acceptCall(intent)
            }
        }
    }

    @JvmStatic
     fun hasVisibleActivities(): Boolean {
        return visibleActivities.isEmpty() ^ 1
    }

     private fun hideUnreadNotificationSingle(i: Int) {
        if (this.shownNotificationIds.contains(Integer.valueOf(i))) {
            this.shownNotificationIds.remove(Integer.valueOf(i))
            ((NotificationManager) getSystemService("notification")).cancel(i)
        }
    }

    @JvmStatic
 private fun notifySettingsByType(notificationType: NotificationType): NotificationSettings {
        switch (m8-getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues()[notificationType.ordinal()]) {
            case 1:
                return notifyGroup
            case 2:
                return notifyLocalChat
            case 3:
                return notifyPrivate
            default:
                return notifyLocalChat
        }
    }

     private fun onCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo) {
        updateOnlineNotification()
    }

     private fun onUnreadNotification(unreadNotifications: UnreadNotifications) {
        showUnreadNotification(unreadNotifications)
    }

     private fun performLogin(sLAuthParams: SLAuthParams) {
        gridName = sLAuthParams.gridName
        gridConnection.Connect(sLAuthParams)
    }

     private fun readPreferences(sharedPreferences: SharedPreferences) {
        onlineNotify = sharedPreferences.getBoolean("onlineNotify", true)
        soundOnNotify = sharedPreferences.getBoolean("soundOnNotify", true)
        this.cloudSyncEnabled = sharedPreferences.getBoolean("sync_to_gdrive", false)
        notifyLocalChat.Load(sharedPreferences)
        notifyPrivate.Load(sharedPreferences)
        notifyGroup.Load(sharedPreferences)
        SLGridConnection.setAutoresponseInfo(sharedPreferences.getBoolean("autoresponse", false), sharedPreferences.getString("autoresponseText", "(Autoresponse) I have auto-response feature enabled. I will respond shortly."))
        Debug.Log("GridConnectionService: prefs: onlineNotify = " + onlineNotify)
        Debug.Log("GridConnectionService: prefs: soundOnNotify = " + soundOnNotify)
        if (gridConnection != null) {
            try {
                gridConnection.getModules().HandleGlobalOptionsChange()
            } catch (NotConnectedException e) {
            }
        }
        updateOnlineNotification()
        updateCloudSyncStatus()
    }

    @JvmStatic
     fun setActivityVisible(activity: Activity, z: Boolean) {
        if (z) {
            visibleActivities.add(activity)
        } else {
            visibleActivities.remove(activity)
        }
        if (!visibleActivities.isEmpty() && gridConnection != null) {
            try {
                if (gridConnection.getConnectionState() == ConnectionState.Connected) {
                    gridConnection.getAgentCircuit().UnpauseAgent()
                }
            } catch (NotConnectedException e) {
                e.printStackTrace()
            }
        }
    }

     private fun showUnreadNotification(unreadNotifications: UnreadNotifications) {
        val instance: NotificationChannels = NotificationChannels.getInstance()
        if (instance.useNotificationGroups()) {
            if (instance.areNotificationsSystemControlled()) {
                unreadNotifications = unreadNotifications.filter(instance.getEnabledTypes(this))
            }
            if (!unreadNotifications.notificationGroups().isEmpty()) {
                val merge: UnreadNotificationInfo = unreadNotifications.merge()
                showUnreadNotificationSingle(merge, R.id.unread_notify_id, instance.getChannelName(instance.getChannelByType((NotificationType) merge.mostImportantFreshType().or((NotificationType) merge.mostImportantType().or(NotificationType.LocalChat)))), NotificationChannels.MESSAGE_NOTIFICATION_GROUP, true, null)
                for (NotificationType notificationType : NotificationType.VALUES) {
                    val channelByType: Channel = instance.getChannelByType(notificationType)
                    if (channelByType != null) {
                        merge = (UnreadNotificationInfo) unreadNotifications.notificationGroups().get(notificationType)
                        val num: String = Integer.toString(9 - notificationType.getPriority())
                        showUnreadNotificationSingle(merge, channelByType.notificationId, instance.getChannelName(channelByType), NotificationChannels.MESSAGE_NOTIFICATION_GROUP, false, num)
                    }
                }
                return
            } else if (!this.shownNotificationIds.isEmpty()) {
                val notificationManager: NotificationManager = (NotificationManager) getSystemService("notification")
                for (Integer intValue : this.shownNotificationIds) {
                    notificationManager.cancel(intValue.intValue())
                }
                this.shownNotificationIds.clear()
                return
            } else {
                return
            }
        }
        showUnreadNotificationSingle(unreadNotifications.merge(), R.id.unread_notify_id, DummyNotificationChannelManager.DEFAULT_NOTIFICATION_CHANNEL, null, true, null)
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:94:0x038d  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:65:0x01e1  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:110:0x046c  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:62:0x01d2  */
     private fun showUnreadNotificationSingle(com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo r18, r19: Int, @javax.annotation.Nonnull java.lang.String r20, @android.support.annotation.Nullable java.lang.String r21, r22: Boolean, @android.support.annotation.Nullable java.lang.String r23) {
        /*
        r17 = this
        r2 = "notification"
        r0 = r17
        r2 = r0.getSystemService(r2)
        r8 = r2
        r8 = (android.app.NotificationManager) r8
        r11 = 0
        if (r18 == 0) goto L_0x0020
    L_0x000f:
        r2 = r18.totalUnreadCount()
        if (r2 != 0) goto L_0x001f
    L_0x0015:
        r2 = r18.objectPopupInfo()
        r2 = r2.objectPopupsCount()
        if (r2 == 0) goto L_0x0020
    L_0x001f:
        r11 = 1
    L_0x0020:
        if (r11 != 0) goto L_0x002a
    L_0x0022:
        r0 = r17
        r1 = r19
        r0.hideUnreadNotificationSingle(r1)
    L_0x0029:
        return
    L_0x002a:
        r2 = "GridConnectionService: updateUnreadNotification: notification state changed."
        com.lumiyaviewer.lumiya.Debug.Log(r2)
        r13 = android.support.v4.app.NotificationCompat$Builder
        r0 = r17
        r1 = r20
        r13.<init>(r0, r1)
        r2 = 2130837639; // 0x7f020087 Float:1.7280238E38 Double:1.0527736743E-314
        r13.setSmallIcon(r2)
        if (r21 == 0) goto L_0x0056
    L_0x0041:
        r0 = r21
        r13.setGroup(r0)
        r0 = r22
        r13.setGroupSummary(r0)
        r2 = 2
        r13.setGroupAlertBehavior(r2)
        if (r23 == 0) goto L_0x0056
    L_0x0051:
        r0 = r23
        r13.setSortKey(r0)
    L_0x0056:
        r2 = r18.agentUUID()
        r5 = com.lumiyaviewer.lumiya.slproto.users.manager.UserManager.getUserManager(r2)
        r2 = r18.unreadSources()
        r2 = r2.size()
        r3 = 1
        if (r2 != r3) goto L_0x015d
    L_0x0069:
        r2 = r18.unreadSources()
        r3 = 0
        r2 = r2.get(r3)
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r2
        r10 = r2
    L_0x0075:
        r3 = com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory.getInstance()
        if (r10 == 0) goto L_0x0161
    L_0x007b:
        r2 = r10.chatterID()
        r2 = com.lumiyaviewer.lumiya.ui.chat.ChatFragment.makeSelection(r2)
    L_0x0083:
        r0 = r17
        r4 = r3.createIntent(r0, r2)
        r2 = 536870912; // 0x20000000 Float:1.0842022E-19 Double:2.652494739E-315
        r4.addFlags(r2)
        r2 = r18.agentUUID()
        com.lumiyaviewer.lumiya.ui.common.ActivityUtils.setActiveAgentID(r4, r2)
        r2 = 0
        r3 = r18.singleFreshSource()
        r3 = r3.isPresent()
        if (r3 == 0) goto L_0x0164
    L_0x00a0:
        r2 = r18.singleFreshSource()
        r2 = r2.get()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r2
    L_0x00aa:
        if (r2 == 0) goto L_0x00ba
    L_0x00ac:
        r3 = "weakSelection"
        r2 = r2.chatterID()
        r2 = com.lumiyaviewer.lumiya.ui.chat.ChatFragment.makeSelection(r2)
        r4.putExtra(r3, r2)
    L_0x00ba:
        if (r5 == 0) goto L_0x07c5
    L_0x00bc:
        r2 = r5.getUnreadNotificationManager()
        r0 = r18
        r2 = r2.captureNotify(r0, r4)
        if (r2 == 0) goto L_0x07c5
    L_0x00c8:
        r12 = r2
    L_0x00c9:
        if (r10 == 0) goto L_0x01db
    L_0x00cb:
        r2 = r18.objectPopupInfo()
        r2 = r2.objectPopupsCount()
        if (r2 != 0) goto L_0x01db
    L_0x00d5:
        r2 = r10.chatterName()
        r3 = gridName
        r2 = r2.or(r3)
        r9 = r2
        r9 = (java.lang.String) r9
        r13.setContentTitle(r9)
        r2 = r10.unreadMessagesCount()
        r3 = 1
        if (r2 != r3) goto L_0x0189
    L_0x00ec:
        r2 = r10.unreadMessages()
        r2 = r2.size()
        r3 = 1
        if (r2 != r3) goto L_0x0189
    L_0x00f7:
        r2 = r10.unreadMessages()
        r3 = 0
        r2 = r2.get(r3)
        r2 = (com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent) r2
        r3 = r10.chatterID()
        r3 = r3.getUserManager()
        r4 = 1
        r0 = r17
        r2 = r2.getPlainTextMessage(r0, r3, r4)
        r13.setContentText(r2)
    L_0x0114:
        r2 = r10.unreadMessages()
        r2 = r2.size()
        r3 = 1
        if (r2 <= r3) goto L_0x01cf
    L_0x011f:
        r14 = android.support.v4.app.NotificationCompat$InboxStyle
        r14.<init>()
        r2 = r10.unreadMessages()
        r15 = r2.iterator()
    L_0x012c:
        r2 = r15.hasNext()
        if (r2 == 0) goto L_0x01ab
    L_0x0132:
        r2 = r15.next()
        r2 = (com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent) r2
        r3 = r10.chatterID()
        r4 = r3.getUserManager()
        r3 = r10.chatterID()
        r3 = r3.getChatterType()
        r5 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.User
        if (r3 != r5) goto L_0x01a9
    L_0x014c:
        r5 = 1
    L_0x014d:
        r6 = "  "
        r7 = " "
        r3 = r17
        r2 = r2.getPlainTextMessage(r3, r4, r5, r6, r7)
        r14.addLine(r2)
        goto L_0x012c
    L_0x015d:
        r2 = 0
        r10 = r2
        goto L_0x0075
    L_0x0161:
        r2 = 0
        goto L_0x0083
    L_0x0164:
        r3 = r18.unreadSources()
        r6 = r3.iterator()
        r3 = r2
    L_0x016d:
        r2 = r6.hasNext()
        if (r2 == 0) goto L_0x07c8
    L_0x0173:
        r2 = r6.next()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r2
        if (r3 == 0) goto L_0x0185
    L_0x017b:
        r7 = r2.unreadMessagesCount()
        r9 = r3.unreadMessagesCount()
        if (r7 <= r9) goto L_0x0187
    L_0x0185:
        r3 = r2
        goto L_0x016d
    L_0x0187:
        r2 = r3
        goto L_0x0185
    L_0x0189:
        r2 = 2131297123; // 0x7f090363 Float:1.8212182E38 Double:1.0530006896E-314
        r0 = r17
        r2 = r0.getString(r2)
        r3 = 1
        r3 = java.lang.Object[r3]
        r4 = r10.unreadMessagesCount()
        r4 = java.lang.Integer.valueOf(r4)
        r5 = 0
        r3[r5] = r4
        r2 = java.lang.String.format(r2, r3)
        r13.setContentText(r2)
        goto L_0x0114
    L_0x01a9:
        r5 = 0
        goto L_0x014d
    L_0x01ab:
        r14.setBigContentTitle(r9)
        r2 = 2131297123; // 0x7f090363 Float:1.8212182E38 Double:1.0530006896E-314
        r0 = r17
        r2 = r0.getString(r2)
        r3 = 1
        r3 = java.lang.Object[r3]
        r4 = r18.totalUnreadCount()
        r4 = java.lang.Integer.valueOf(r4)
        r5 = 0
        r3[r5] = r4
        r2 = java.lang.String.format(r2, r3)
        r14.setSummaryText(r2)
        r13.setStyle(r14)
    L_0x01cf:
        r2 = r11
    L_0x01d0:
        if (r2 != 0) goto L_0x046c
    L_0x01d2:
        r0 = r17
        r1 = r19
        r0.hideUnreadNotificationSingle(r1)
        goto L_0x0029
    L_0x01db:
        r2 = r18.totalUnreadCount()
        if (r2 == 0) goto L_0x038d
    L_0x01e1:
        r2 = r17.getResources()
        r3 = r18.totalUnreadCount()
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.totalUnreadCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r5 = 2131820547; // 0x7f110003 Float:1.9273812E38 Double:1.0532592954E-314
        r2 = r2.getQuantityString(r5, r3, r4)
        r3 = r18.objectPopupInfo()
        r3 = r3.objectPopupsCount()
        if (r3 == 0) goto L_0x023d
    L_0x0208:
        r3 = java.lang.StringBuilder
        r3.<init>()
        r2 = r3.append(r2)
        r3 = r17.getResources()
        r4 = r18.objectPopupInfo()
        r4 = r4.objectPopupsCount()
        r5 = 1
        r5 = java.lang.Object[r5]
        r6 = r18.objectPopupInfo()
        r6 = r6.objectPopupsCount()
        r6 = java.lang.Integer.valueOf(r6)
        r7 = 0
        r5[r7] = r6
        r6 = 2131820544; // 0x7f110000 Float:1.9273806E38 Double:1.053259294E-314
        r3 = r3.getQuantityString(r6, r4, r5)
        r2 = r2.append(r3)
        r2 = r2.toString()
    L_0x023d:
        r13.setContentTitle(r2)
        r2 = r18.totalUnreadCount()
        if (r2 != 0) goto L_0x0250
    L_0x0246:
        r2 = r18.objectPopupInfo()
        r2 = r2.objectPopupsCount()
        if (r2 != 0) goto L_0x0322
    L_0x0250:
        r2 = r17.getResources()
        r3 = r18.totalUnreadCount()
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.totalUnreadCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r5 = 2131820547; // 0x7f110003 Float:1.9273812E38 Double:1.0532592954E-314
        r2 = r2.getQuantityString(r5, r3, r4)
        r13.setContentTitle(r2)
    L_0x0270:
        r3 = 0
        r2 = r18.unreadSources()
        r4 = r2.iterator()
    L_0x0279:
        r2 = r4.hasNext()
        if (r2 == 0) goto L_0x07c2
    L_0x027f:
        r2 = r4.next()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r2
        r5 = r2.chatterName()
        r5 = r5.isPresent()
        if (r5 == 0) goto L_0x0279
    L_0x028f:
        r2 = r2.chatterName()
        r2 = r2.orNull()
        r2 = (java.lang.String) r2
    L_0x0299:
        r3 = r18.unreadSources()
        r3 = r3.size()
        if (r2 == 0) goto L_0x034c
    L_0x02a3:
        r4 = 1
        if (r3 <= r4) goto L_0x07bf
    L_0x02a6:
        r4 = java.lang.StringBuilder
        r4.<init>()
        r2 = r4.append(r2)
        r4 = " "
        r2 = r2.append(r4)
        r4 = 2131296851; // 0x7f090253 Float:1.821163E38 Double:1.053000555E-314
        r0 = r17
        r4 = r0.getString(r4)
        r5 = 1
        r5 = java.lang.Object[r5]
        r3 = r3 + -1
        r3 = java.lang.Integer.valueOf(r3)
        r6 = 0
        r5[r6] = r3
        r3 = java.lang.String.format(r4, r5)
        r2 = r2.append(r3)
        r2 = r2.toString()
        r10 = r2
    L_0x02d8:
        r13.setContentText(r10)
        r14 = android.support.v4.app.NotificationCompat$InboxStyle
        r14.<init>()
        r2 = r18.unreadSources()
        r15 = r2.iterator()
    L_0x02e8:
        r2 = r15.hasNext()
        if (r2 == 0) goto L_0x0366
    L_0x02ee:
        r2 = r15.next()
        r9 = r2
        r9 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r9
        r2 = r9.unreadMessages()
        r16 = r2.iterator()
    L_0x02fd:
        r2 = r16.hasNext()
        if (r2 == 0) goto L_0x02e8
    L_0x0303:
        r2 = r16.next()
        r2 = (com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent) r2
        r3 = r9.chatterID()
        r4 = r3.getUserManager()
        r6 = "  "
        r7 = " "
        r5 = 0
        r3 = r17
        r2 = r2.getPlainTextMessage(r3, r4, r5, r6, r7)
        r14.addLine(r2)
        goto L_0x02fd
    L_0x0322:
        r2 = r17.getResources()
        r3 = r18.objectPopupInfo()
        r3 = r3.objectPopupsCount()
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.objectPopupInfo()
        r5 = r5.objectPopupsCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r5 = 2131820548; // 0x7f110004 Float:1.9273814E38 Double:1.053259296E-314
        r2 = r2.getQuantityString(r5, r3, r4)
        r13.setContentTitle(r2)
        goto L_0x0270
    L_0x034c:
        r2 = 2131296434; // 0x7f0900b2 Float:1.8210785E38 Double:1.053000349E-314
        r0 = r17
        r2 = r0.getString(r2)
        r4 = 1
        r4 = java.lang.Object[r4]
        r3 = java.lang.Integer.valueOf(r3)
        r5 = 0
        r4[r5] = r3
        r2 = java.lang.String.format(r2, r4)
        r10 = r2
        goto L_0x02d8
    L_0x0366:
        r2 = 2131297123; // 0x7f090363 Float:1.8212182E38 Double:1.0530006896E-314
        r0 = r17
        r2 = r0.getString(r2)
        r3 = 1
        r3 = java.lang.Object[r3]
        r4 = r18.totalUnreadCount()
        r4 = java.lang.Integer.valueOf(r4)
        r5 = 0
        r3[r5] = r4
        r2 = java.lang.String.format(r2, r3)
        r14.setBigContentTitle(r2)
        r14.setSummaryText(r10)
        r13.setStyle(r14)
        r2 = r11
        goto L_0x01d0
    L_0x038d:
        r2 = r18.objectPopupInfo()
        r2 = r2.objectPopupsCount()
        if (r2 == 0) goto L_0x0469
    L_0x0397:
        r2 = "objectPopupNotification"
        r3 = 1
        r12.putExtra(r2, r3)
        r2 = "selection"
        r2 = r12.getBundleExtra(r2)
        if (r2 != 0) goto L_0x03c2
    L_0x03a7:
        r2 = "weakSelection"
        r2 = r12.getBundleExtra(r2)
        if (r2 != 0) goto L_0x03c2
    L_0x03b0:
        r2 = "selection"
        r3 = r18.agentUUID()
        r3 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.getLocalChatterID(r3)
        r3 = com.lumiyaviewer.lumiya.ui.chat.ChatFragment.makeSelection(r3)
        r12.putExtra(r2, r3)
    L_0x03c2:
        r2 = r18.objectPopupInfo()
        r2 = r2.lastObjectPopup()
        r2 = r2.orNull()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupMessage) r2
        r3 = r18.objectPopupInfo()
        r3 = r3.freshObjectPopupsCount()
        r4 = 1
        if (r3 != r4) goto L_0x03ee
    L_0x03db:
        if (r2 == 0) goto L_0x03ee
    L_0x03dd:
        r3 = r2.objectName()
        r13.setContentTitle(r3)
        r2 = r2.message()
        r13.setContentText(r2)
        r2 = r11
        goto L_0x01d0
    L_0x03ee:
        r3 = r17.getResources()
        r4 = r18.objectPopupInfo()
        r4 = r4.objectPopupsCount()
        r5 = 1
        r5 = java.lang.Object[r5]
        r6 = r18.objectPopupInfo()
        r6 = r6.objectPopupsCount()
        r6 = java.lang.Integer.valueOf(r6)
        r7 = 0
        r5[r7] = r6
        r6 = 2131820548; // 0x7f110004 Float:1.9273814E38 Double:1.053259296E-314
        r3 = r3.getQuantityString(r6, r4, r5)
        r13.setContentTitle(r3)
        if (r2 == 0) goto L_0x043e
    L_0x0418:
        r3 = java.lang.StringBuilder
        r3.<init>()
        r4 = r2.objectName()
        r3 = r3.append(r4)
        r4 = ": "
        r3 = r3.append(r4)
        r2 = r2.message()
        r2 = r3.append(r2)
        r2 = r2.toString()
        r13.setContentText(r2)
        r2 = r11
        goto L_0x01d0
    L_0x043e:
        r2 = r17.getResources()
        r3 = r18.objectPopupInfo()
        r3 = r3.objectPopupsCount()
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.objectPopupInfo()
        r5 = r5.objectPopupsCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r5 = 2131820548; // 0x7f110004 Float:1.9273814E38 Double:1.053259296E-314
        r2 = r2.getQuantityString(r5, r3, r4)
        r13.setContentText(r2)
        r2 = r11
        goto L_0x01d0
    L_0x0469:
        r2 = 0
        goto L_0x01d0
    L_0x046c:
        r2 = 0
        r3 = r18.freshMessagesCount()
        if (r3 <= 0) goto L_0x06ef
    L_0x0473:
        r2 = r18.singleFreshSource()
        r2 = r2.orNull()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource) r2
        if (r2 == 0) goto L_0x06d2
    L_0x047f:
        r3 = r18.freshMessagesCount()
        r4 = 1
        if (r3 != r4) goto L_0x061a
    L_0x0486:
        r3 = r2.unreadMessages()
        r3 = r3.size()
        if (r3 <= 0) goto L_0x061a
    L_0x0490:
        r3 = r2.unreadMessages()
        r4 = r2.unreadMessages()
        r4 = r4.size()
        r4 = r4 + -1
        r3 = r3.get(r4)
        r3 = (com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent) r3
        r4 = r2.chatterID()
        r4 = r4.getChatterType()
        r5 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local
        if (r4 != r5) goto L_0x05f8
    L_0x04b0:
        r4 = 2131296440; // 0x7f0900b8 Float:1.8210797E38 Double:1.053000352E-314
        r0 = r17
        r4 = r0.getString(r4)
    L_0x04b9:
        r5 = java.lang.StringBuilder
        r5.<init>()
        r6 = "["
        r5 = r5.append(r6)
        r4 = r5.append(r4)
        r5 = "] "
        r4 = r4.append(r5)
        r2 = r2.chatterID()
        r2 = r2.getUserManager()
        r5 = 0
        r0 = r17
        r2 = r3.getPlainTextMessage(r0, r2, r5)
        r2 = r2.toString()
        r2 = r2.trim()
        r2 = r4.append(r2)
        r2 = r2.toString()
        r3 = "\n"
        r3 = r2.indexOf(r3)
        if (r3 < 0) goto L_0x04fd
    L_0x04f8:
        r4 = 0
        r2 = r2.substring(r4, r3)
    L_0x04fd:
        r3 = r2.length()
        r4 = 30
        if (r3 <= r4) goto L_0x0520
    L_0x0505:
        r3 = java.lang.StringBuilder
        r3.<init>()
        r4 = 0
        r5 = 30
        r2 = r2.substring(r4, r5)
        r2 = r3.append(r2)
        r3 = "..."
        r2 = r2.append(r3)
        r2 = r2.toString()
    L_0x0520:
        r3 = 0
        r13.setOngoing(r3)
        r3 = r18.totalUnreadCount()
        r13.setNumber(r3)
        r3 = 2131755072; // 0x7f100040 Float:1.9141013E38 Double:1.0532269464E-314
        r4 = 134217728; // 0x8000000 Float:3.85186E-34 Double:6.63123685E-316
        r0 = r17
        r3 = android.app.PendingIntent.getActivity(r0, r3, r12, r4)
        r13.setContentIntent(r3)
        r3 = 0
        r13.setDefaults(r3)
        if (r2 == 0) goto L_0x0542
    L_0x053f:
        r13.setTicker(r2)
    L_0x0542:
        r2 = 1
        r13.setOnlyAlertOnce(r2)
        if (r21 == 0) goto L_0x054c
    L_0x0548:
        r2 = r22 ^ 1
        if (r2 == 0) goto L_0x05e2
    L_0x054c:
        r4 = com.lumiyaviewer.lumiya.utils.LEDAction.None
        r3 = 0
        r2 = 0
        r5 = r18.totalUnreadCount()
        if (r5 <= 0) goto L_0x0560
    L_0x0556:
        r2 = r18.mostImportantType()
        r2 = r2.orNull()
        r2 = (com.lumiyaviewer.lumiya.ui.settings.NotificationType) r2
    L_0x0560:
        if (r2 != 0) goto L_0x056e
    L_0x0562:
        r5 = r18.objectPopupInfo()
        r5 = r5.objectPopupsCount()
        if (r5 == 0) goto L_0x056e
    L_0x056c:
        r2 = com.lumiyaviewer.lumiya.ui.settings.NotificationType.LocalChat
    L_0x056e:
        if (r2 == 0) goto L_0x057e
    L_0x0570:
        r2 = notifySettingsByType(r2)
        r3 = r2.getLEDAction()
        r2 = r2.getLEDColor()
        r4 = r3
        r3 = r2
    L_0x057e:
        r2 = 0
        r5 = r18.freshMessagesCount()
        if (r5 == 0) goto L_0x058f
    L_0x0585:
        r2 = r18.mostImportantFreshType()
        r2 = r2.orNull()
        r2 = (com.lumiyaviewer.lumiya.ui.settings.NotificationType) r2
    L_0x058f:
        if (r2 != 0) goto L_0x059d
    L_0x0591:
        r5 = r18.objectPopupInfo()
        r5 = r5.freshObjectPopupsCount()
        if (r5 == 0) goto L_0x059d
    L_0x059b:
        r2 = com.lumiyaviewer.lumiya.ui.settings.NotificationType.LocalChat
    L_0x059d:
        if (r2 == 0) goto L_0x078e
    L_0x059f:
        r2 = notifySettingsByType(r2)
    L_0x05a3:
        r5 = "GridConnectionService: updateUnreadNotification: ledAction = %s, color = %08x"
        r6 = 2
        r6 = java.lang.Object[r6]
        r7 = r4.toString()
        r9 = 0
        r6[r9] = r7
        r7 = java.lang.Integer.valueOf(r3)
        r9 = 1
        r6[r9] = r7
        com.lumiyaviewer.lumiya.Debug.Printf(r5, r6)
        r5 = com.lumiyaviewer.lumiya.utils.LEDAction.None
        if (r4 == r5) goto L_0x05cb
    L_0x05be:
        r5 = m9-getcom-lumiyaviewer-lumiya-utils-LEDActionSwitchesValues()
        r4 = r4.ordinal()
        r4 = r5[r4]
        switch(r4) {
            case 1: goto L_0x0791
            case 2: goto L_0x0798
            case 3: goto L_0x07aa
            case 4: goto L_0x07a1
            default: goto L_0x05cb
        }
    L_0x05cb:
        r3 = soundOnNotify
        if (r3 == 0) goto L_0x07b1
    L_0x05cf:
        if (r2 == 0) goto L_0x07b1
    L_0x05d1:
        r2 = r2.getRingtone()
        if (r2 == 0) goto L_0x05e2
    L_0x05d7:
        r2 = android.net.Uri.parse(r2)
        r13.setSound(r2)
        r2 = 0
        r13.setOnlyAlertOnce(r2)
    L_0x05e2:
        r0 = r17
        r2 = r0.shownNotificationIds
        r3 = java.lang.Integer.valueOf(r19)
        r2.add(r3)
        r2 = r13.build()
        r0 = r19
        r8.notify(r0, r2)
        goto L_0x0029
    L_0x05f8:
        r4 = r2.chatterID()
        r4 = r4.getChatterType()
        r5 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group
        if (r4 != r5) goto L_0x060f
    L_0x0604:
        r4 = 2131296438; // 0x7f0900b6 Float:1.8210793E38 Double:1.053000351E-314
        r0 = r17
        r4 = r0.getString(r4)
        goto L_0x04b9
    L_0x060f:
        r4 = 2131296439; // 0x7f0900b7 Float:1.8210795E38 Double:1.0530003516E-314
        r0 = r17
        r4 = r0.getString(r4)
        goto L_0x04b9
    L_0x061a:
        r3 = 2131297123; // 0x7f090363 Float:1.8212182E38 Double:1.0530006896E-314
        r0 = r17
        r3 = r0.getString(r3)
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.freshMessagesCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r3 = java.lang.String.format(r3, r4)
        r4 = r2.chatterID()
        r4 = r4.getChatterType()
        r5 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Local
        if (r4 != r5) goto L_0x067d
    L_0x0641:
        r4 = 1
        r4 = java.lang.Object[r4]
        r2 = r2.chatterName()
        r5 = 2131296658; // 0x7f090192 Float:1.8211239E38 Double:1.05300046E-314
        r0 = r17
        r5 = r0.getString(r5)
        r2 = r2.or(r5)
        r5 = 0
        r4[r5] = r2
        r2 = 2131296697; // 0x7f0901b9 Float:1.8211318E38 Double:1.053000479E-314
        r0 = r17
        r2 = r0.getString(r2, r4)
    L_0x0661:
        if (r2 == 0) goto L_0x07bc
    L_0x0663:
        r4 = java.lang.StringBuilder
        r4.<init>()
        r3 = r4.append(r3)
        r4 = " "
        r3 = r3.append(r4)
        r2 = r3.append(r2)
        r2 = r2.toString()
        goto L_0x0520
    L_0x067d:
        r4 = r2.chatterID()
        r4 = r4.getChatterType()
        r5 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterType.Group
        if (r4 != r5) goto L_0x06aa
    L_0x0689:
        r4 = 1
        r4 = java.lang.Object[r4]
        r2 = r2.chatterName()
        r5 = 2131296473; // 0x7f0900d9 Float:1.8210864E38 Double:1.0530003684E-314
        r0 = r17
        r5 = r0.getString(r5)
        r2 = r2.or(r5)
        r5 = 0
        r4[r5] = r2
        r2 = 2131296697; // 0x7f0901b9 Float:1.8211318E38 Double:1.053000479E-314
        r0 = r17
        r2 = r0.getString(r2, r4)
        goto L_0x0661
    L_0x06aa:
        r4 = r2.chatterName()
        r4 = r4.isPresent()
        if (r4 == 0) goto L_0x06d0
    L_0x06b4:
        r4 = 2131296557; // 0x7f09012d Float:1.8211034E38 Double:1.05300041E-314
        r0 = r17
        r4 = r0.getString(r4)
        r5 = 1
        r5 = java.lang.Object[r5]
        r2 = r2.chatterName()
        r2 = r2.orNull()
        r6 = 0
        r5[r6] = r2
        r2 = java.lang.String.format(r4, r5)
        goto L_0x0661
    L_0x06d0:
        r2 = 0
        goto L_0x0661
    L_0x06d2:
        r2 = 2131297123; // 0x7f090363 Float:1.8212182E38 Double:1.0530006896E-314
        r0 = r17
        r2 = r0.getString(r2)
        r3 = 1
        r3 = java.lang.Object[r3]
        r4 = r18.freshMessagesCount()
        r4 = java.lang.Integer.valueOf(r4)
        r5 = 0
        r3[r5] = r4
        r2 = java.lang.String.format(r2, r3)
        goto L_0x0520
    L_0x06ef:
        r3 = r18.objectPopupInfo()
        r3 = r3.freshObjectPopupsCount()
        if (r3 == 0) goto L_0x0520
    L_0x06f9:
        r2 = r18.objectPopupInfo()
        r2 = r2.lastObjectPopup()
        r2 = r2.orNull()
        r2 = (com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.ObjectPopupMessage) r2
        r3 = r18.objectPopupInfo()
        r3 = r3.freshObjectPopupsCount()
        r4 = 1
        if (r3 != r4) goto L_0x0767
    L_0x0712:
        if (r2 == 0) goto L_0x0767
    L_0x0714:
        r3 = java.lang.StringBuilder
        r3.<init>()
        r4 = r2.objectName()
        r3 = r3.append(r4)
        r4 = ": "
        r3 = r3.append(r4)
        r2 = r2.message()
        r2 = r3.append(r2)
        r2 = r2.toString()
        r3 = "\n"
        r3 = r2.indexOf(r3)
        if (r3 < 0) goto L_0x0742
    L_0x073d:
        r4 = 0
        r2 = r2.substring(r4, r3)
    L_0x0742:
        r3 = r2.length()
        r4 = 30
        if (r3 <= r4) goto L_0x0520
    L_0x074a:
        r3 = java.lang.StringBuilder
        r3.<init>()
        r4 = 0
        r5 = 30
        r2 = r2.substring(r4, r5)
        r2 = r3.append(r2)
        r3 = "..."
        r2 = r2.append(r3)
        r2 = r2.toString()
        goto L_0x0520
    L_0x0767:
        r2 = r17.getResources()
        r3 = r18.objectPopupInfo()
        r3 = r3.objectPopupsCount()
        r4 = 1
        r4 = java.lang.Object[r4]
        r5 = r18.objectPopupInfo()
        r5 = r5.objectPopupsCount()
        r5 = java.lang.Integer.valueOf(r5)
        r6 = 0
        r4[r6] = r5
        r5 = 2131820548; // 0x7f110004 Float:1.9273814E38 Double:1.053259296E-314
        r2 = r2.getQuantityString(r5, r3, r4)
        goto L_0x0520
    L_0x078e:
        r2 = 0
        goto L_0x05a3
    L_0x0791:
        r4 = 1
        r5 = 0
        r13.setLights(r3, r4, r5)
        goto L_0x05cb
    L_0x0798:
        r4 = 300; // 0x12c Float:4.2E-43 Double:1.48E-321
        r5 = 100
        r13.setLights(r3, r4, r5)
        goto L_0x05cb
    L_0x07a1:
        r4 = 1000; // 0x3e8 Float:1.401E-42 Double:4.94E-321
        r5 = 500; // 0x1f4 Float:7.0E-43 Double:2.47E-321
        r13.setLights(r3, r4, r5)
        goto L_0x05cb
    L_0x07aa:
        r4 = 0
        r5 = 0
        r13.setLights(r3, r4, r5)
        goto L_0x05cb
    L_0x07b1:
        r2 = "GridConnectionService: will not emit sound."
        r3 = 0
        r3 = java.lang.Object[r3]
        com.lumiyaviewer.lumiya.Debug.Printf(r2, r3)
        goto L_0x05e2
    L_0x07bc:
        r2 = r3
        goto L_0x0520
    L_0x07bf:
        r10 = r2
        goto L_0x02d8
    L_0x07c2:
        r2 = r3
        goto L_0x0299
    L_0x07c5:
        r12 = r4
        goto L_0x00c9
    L_0x07c8:
        r2 = r3
        goto L_0x00aa
        */
        // Early return if notification info is null
        if (notificationInfo == null) {
            return
        }
        
        try {
            val notificationManager: NotificationManager = (NotificationManager) getSystemService("notification")
            
            // Determine notification content based on type and count
            String notificationTitle
            String notificationText
            
            if (notificationInfo.privateChatInfo().unreadCount() > 0) {
                if (notificationInfo.privateChatInfo().unreadCount() == 1) {
                    notificationTitle = getString(R.string.notification_private_message)
                    notificationText = notificationInfo.privateChatInfo().lastMessage()
                } else {
                    notificationTitle = getString(R.string.notification_multiple_private_messages)
                    notificationText = getString(R.string.notification_private_count, 
                                               notificationInfo.privateChatInfo().unreadCount())
                }
            } else if (notificationInfo.groupChatInfo().unreadCount() > 0) {
                if (notificationInfo.groupChatInfo().unreadCount() == 1) {
                    notificationTitle = getString(R.string.notification_group_message)
                    notificationText = notificationInfo.groupChatInfo().lastMessage()
                } else {
                    notificationTitle = getString(R.string.notification_multiple_group_messages)
                    notificationText = getString(R.string.notification_group_count, 
                                               notificationInfo.groupChatInfo().unreadCount())
                }
            } else if (notificationInfo.localChatInfo().unreadCount() > 0) {
                if (notificationInfo.localChatInfo().unreadCount() == 1) {
                    notificationTitle = getString(R.string.notification_local_message)
                    notificationText = notificationInfo.localChatInfo().lastMessage()
                } else {
                    notificationTitle = getString(R.string.notification_multiple_local_messages)
                    notificationText = getString(R.string.notification_local_count, 
                                               notificationInfo.localChatInfo().unreadCount())
                }
            } else if (notificationInfo.objectPopupInfo().objectPopupsCount() > 0) {
                notificationTitle = getString(R.string.notification_object_popup)
                notificationText = getResources().getQuantityString(R.plurals.notification_object_popups, 
                                                                   notificationInfo.objectPopupInfo().objectPopupsCount(),
                                                                   notificationInfo.objectPopupInfo().objectPopupsCount())
            } else {
                // No unread content, hide notification
                hideUnreadNotificationSingle(notificationId)
                return
            }
            
            // Truncate Long notification text
            if (notificationText != null && notificationText.length() > 30) {
                notificationText = notificationText.substring(0, 30) + "..."
            }
            
            // Create intent for opening the app
            val openIntent: Intent = Intent(this, com.lumiyaviewer.lumiya.ui.render.WorldViewActivity.class)
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0, openIntent, 
                                                                                            android.app.PendingIntent.FLAG_UPDATE_CURRENT)
            
            // Build notification
            android.support.v4.app.NotificationCompat.Builder builder = 
                android.support.v4.app.NotificationCompat.Builder(this, channelName)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT)
            
            // Set group if specified
            if (groupKey != null) {
                builder.setGroup(groupKey)
                if (isSummary) {
                    builder.setGroupSummary(true)
                }
            }
            
            // Set sort key if specified
            if (sortKey != null) {
                builder.setSortKey(sortKey)
            }
            
            // Configure notification lights and sound based on user preferences
            val settings: NotificationSettings = notifySettingsByType(NotificationType.LocalChat)
            if (settings != null) {
                if (settings.getNotifySound()) {
                    builder.setDefaults(Notification.DEFAULT_SOUND)
                } else {
                    Debug.Printf("GridConnectionService: will not emit sound.", Object[0])
                }
                
                if (settings.getNotifyLight()) {
                    builder.setLights(0xff00ff00, 1000, 500); // Green light, 1s on, 0.5s off
                } else {
                    builder.setLights(0xff00ff00, 0, 0); // No light
                }
            }
            
            // Show the notification
            val notification: Notification = builder.build()
            notificationManager.notify(notificationId, notification)
            this.shownNotificationIds.add(Integer.valueOf(notificationId))
            
        } catch (Exception e) {
            Debug.Warning(e)
        }
    }

     private fun startCloudSync(userManager: UserManager) {
        this.cloudSyncUserManager = userManager
        updateCloudSyncStatus()
    }

     private fun stopCloudSync() {
        this.cloudSyncUserManager = null
        updateCloudSyncStatus()
    }

     private fun updateCloudSyncStatus() {
        if (!this.cloudSyncEnabled || this.cloudSyncUserManager == null) {
            if (this.cloudSyncServiceConnection != null) {
                this.cloudSyncServiceConnection.stopSyncing()
                this.cloudSyncServiceConnection = null
            }
            if (this.cloudPluginReceiverRegistered) {
                try {
                    unregisterReceiver(this.cloudPluginInstalledReceiver)
                } catch (Throwable e) {
                    Debug.Warning(e)
                }
            }
            this.cloudPluginReceiverRegistered = false
        } else if (this.cloudSyncServiceConnection == null) {
            Boolean bindService
            this.cloudSyncServiceConnection = CloudSyncServiceConnection(this, this.cloudSyncUserManager)
            val intent: Intent = Intent()
            intent.setComponent(ComponentName("com.linkpoint.cloud", "com.linkpoint.cloud.DriveSyncService"))
            try {
                bindService = bindService(intent, this.cloudSyncServiceConnection, 1)
            } catch (Throwable e2) {
                Debug.Warning(e2)
                bindService = false
            }
            Debug.Printf("LinkpointCloud: bindService = %b", Boolean.valueOf(bindService))
            if (!bindService) {
                this.cloudSyncServiceConnection.stopSyncing()
                intent = Intent("android.intent.action.VIEW")
                intent.setData(Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL))
                this.cloudSyncServiceConnection.showSyncingError(getString(R.string.cloud_sync_not_installed), getString(R.string.cloud_sync_not_installed_long), intent)
                this.cloudSyncServiceConnection = null
                val intentFilter: IntentFilter = IntentFilter()
                intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
                intentFilter.addDataScheme("package")
                if (VERSION.SDK_INT >= 19) {
                    intentFilter.addDataSchemeSpecificPart("com.linkpoint.cloud", 0)
                }
                registerReceiver(this.cloudPluginInstalledReceiver, intentFilter)
                this.cloudPluginReceiverRegistered = true
            }
        }
    }

     private fun updateOnlineNotification() {
        if (gridConnection != null) {
            val connectionState: ConnectionState = gridConnection.getConnectionState()
            i = connectionState != ConnectionState.Idle ? GlobalOptions.getInstance().getKeepWifiOn() ? 1 : 0 : 0
            val activeAgentUUID: UUID = gridConnection.getActiveAgentUUID()
            if (activeAgentUUID == null || connectionState == ConnectionState.Idle) {
                if (this.connectedAgentNameRetriever != null) {
                    this.connectedAgentNameRetriever.dispose()
                }
                this.connectedAgentNameRetriever = null
            } else if (this.connectedAgentNameRetriever == null) {
                this.connectedAgentNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(activeAgentUUID, activeAgentUUID), this.onActiveAgentNameUpdated, UIThreadExecutor.getSerialInstance())
            }
        } else {
            i = 0
        }
        if (i != 0 && this.wifiLock == null) {
            val applicationContext: Context = getApplicationContext()
            if (applicationContext != null) {
                this.wifiLock = ((WifiManager) applicationContext.getSystemService("wifi")).createWifiLock(1, "Linkpoint")
                this.wifiLock.acquire()
                Debug.Printf("WiFi lock acquired", Object[0])
            }
        } else if (!(this.wifiLock == null || (i ^ 1) == 0)) {
            this.wifiLock.release()
            this.wifiLock = null
            Debug.Printf("WiFi lock released", Object[0])
        }
        val onlineNotificationInfo: OnlineNotificationInfo = OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, this.connectedAgentNameRetriever, (CurrentLocationInfo) this.currentLocationInfo.getData())
        if (!onlineNotificationInfo.equals(this.onlineNotificationInfo)) {
            this.onlineNotificationInfo = onlineNotificationInfo
            val notification: Notification = onlineNotificationInfo.getNotification(this)
            if (notification != null) {
                startForeground(R.id.online_notify_id, notification)
            } else {
                stopForeground(true)
            }
        }
    }

    /* renamed from: -com_lumiyaviewer_lumiya_GridConnectionService-mthref-0 */
    /* synthetic */ Unit m10-com_lumiyaviewer_lumiya_GridConnectionService-mthref-0(CurrentLocationInfo currentLocationInfo) {
        onCurrentLocationInfo(currentLocationInfo)
    }

    /* renamed from: -com_lumiyaviewer_lumiya_GridConnectionService-mthref-1 */
    /* synthetic */ Unit m11-com_lumiyaviewer_lumiya_GridConnectionService-mthref-1(UnreadNotifications unreadNotifications) {
        onUnreadNotification(unreadNotifications)
    }

    fun acceptVoiceCall(chatterID: ChatterID) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.acceptVoiceCall(chatterID)
        }
    }

    fun acceptVoiceCall(voiceRinging: VoiceRinging) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.acceptVoiceCall(voiceRinging)
        }
    }

    fun enableVoiceMic(z: Boolean) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.enableVoiceMic(z)
        }
    }

    @javax.annotation.Nullable
     public fun getVoicePluginServiceConnection(): VoicePluginServiceConnection {
        return this.voicePluginServiceConnection
    }

    @EventHandler
    fun handleConnectEvent(sLLoginResultEvent: SLLoginResultEvent) {
        val userManager: UserManager = UserManager.getUserManager(sLLoginResultEvent.activeAgentUUID)
        if (userManager != null) {
            this.unreadNotifySubscription = userManager.getUnreadNotificationManager().getUnreadNotifications().subscribe(UnreadNotificationManager.unreadNotificationKey, UIThreadExecutor.getSerialInstance(), com.lumiyaviewer.lumiya.-$Lambda$3DowF6pLKgVjVrTY9aZKQ2J3cf0.AnonymousClass1(this))
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value)
        }
        updateOnlineNotification()
        VoicePluginServiceConnection.setInstallOfferDisplayed(false)
        if (sLLoginResultEvent.success) {
            startCloudSync(userManager)
            return
        }
        Debug.Log("GridConnectionService: stopping self because of connect failed.")
        stopCloudSync()
        stopSelf()
    }

    @EventHandler
    fun handleConnectionStateChangedEvent(sLConnectionStateChangedEvent: SLConnectionStateChangedEvent) {
        updateOnlineNotification()
    }

    @EventHandler
    fun handleDisconnectEvent(sLDisconnectEvent: SLDisconnectEvent) {
        updateOnlineNotification()
        Debug.Log("GridConnectionService: stopping self because of disconnect.")
        stopCloudSync()
        stopVoice()
        stopSelf()
        this.currentLocationInfo.unsubscribe()
        VoicePluginServiceConnection.setInstallOfferDisplayed(false)
    }

    /* renamed from: updateNotificationOnNameRetrieve - updates online notification when chatter name is retrieved */
    /* synthetic */ Unit updateNotificationOnNameRetrieve(ChatterNameRetriever chatterNameRetriever) {
        updateOnlineNotification()
    }

     public override fun onBind(intent: Intent): IBinder {
        return this.mBinder
    }

    override fun onCreate() {
        super.onCreate()
        serviceInstance = WeakReference(this)
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext())
        this.prefs.registerOnSharedPreferenceChangeListener(this)
        readPreferences(this.prefs)
        updateOnlineNotification()
    }

    override fun onDestroy() {
        if (this.prefs != null) {
            this.prefs.unregisterOnSharedPreferenceChangeListener(this)
            this.prefs = null
        }
        this.onlineNotificationInfo = OnlineNotificationInfo(onlineNotify, this, gridName, gridConnection, this.connectedAgentNameRetriever, null)
        stopForeground(true)
        this.shownNotificationIds.clear()
        if (this.eventBus != null) {
            this.eventBus.unsubscribe(this)
        }
        serviceInstance = null
        super.onDestroy()
    }

    @EventHandler
    fun onGlobalPreferencesChanged(globalOptionsChangedEvent: GlobalOptionsChangedEvent) {
        readPreferences(globalOptionsChangedEvent.preferences)
    }

    fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, str: String) {
        readPreferences(sharedPreferences)
        updateOnlineNotification()
    }

     public override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        val str: String = "onStartCommand: intent is %s, flags %08x"
        val objArr: Array<Any> = Object[2]
        objArr[0] = intent != null ? "not null" : "null"
        objArr[1] = Integer.valueOf(i)
        Debug.Printf(str, objArr)
        if ((i & 1) != 0) {
            intent = null
        }
        handleStartService(intent)
        return 2
    }

     public override fun onUnbind(intent: Intent): Boolean {
        Debug.Log("GridConnectionService: onUnbind called, connection state = " + gridConnection.getConnectionState())
        if (gridConnection.getConnectionState() == ConnectionState.Idle) {
            Debug.Log("GridConnectionService: Stopping self because unbind and no clients are bound")
            stopCloudSync()
            stopSelf()
        }
        return super.onUnbind(intent)
    }

    fun startVoice(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager) {
        connectToVoicePlugin(voiceLoginInfo, userManager)
    }

    fun stopVoice() {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.disconnect()
            this.voicePluginServiceConnection = null
        }
    }

    fun terminateVoiceCall(chatterID: ChatterID) {
        if (this.voicePluginServiceConnection != null) {
            this.voicePluginServiceConnection.terminateVoiceCall(chatterID)
        }
    }
}
