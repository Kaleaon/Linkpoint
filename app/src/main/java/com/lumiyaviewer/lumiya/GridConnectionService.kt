package com.lumiyaviewer.lumiya

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
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import com.google.common.base.Strings
import com.lumiyaviewer.lumiya.GlobalOptions.GlobalOptionsChangedEvent
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.eventbus.EventHandler
import com.lumiyaviewer.lumiya.licensing.LicenseChecker
import com.lumiyaviewer.lumiya.react.Subscription
import com.lumiyaviewer.lumiya.react.SubscriptionData
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey
import com.lumiyaviewer.lumiya.react.UIThreadExecutor
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState
import com.lumiyaviewer.lumiya.slproto.SLGridConnection.NotConnectedException
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams
import com.lumiyaviewer.lumiya.slproto.chat.SLVoiceUpgradeEvent
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotifications
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels
import com.lumiyaviewer.lumiya.ui.notify.OnlineNotificationInfo
import com.lumiyaviewer.lumiya.ui.settings.NotificationSettings
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import com.lumiyaviewer.lumiya.utils.LEDAction
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.UUID

class GridConnectionService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val LOGIN_ACTION = "com.lumiyaviewer.lumiya.ACTION_LOGIN"
        private const val REQUEST_CODE_UNREAD_NOTIFY = 2131755072

        @Volatile
        private var gridConnection: SLGridConnection? = null
        
        private var gridName = "Second Life"
        private val notifyGroup = NotificationSettings(NotificationType.Group)
        private val notifyLocalChat = NotificationSettings(NotificationType.LocalChat)
        private val notifyPrivate = NotificationSettings(NotificationType.Private)
        private var onlineNotify = false
        private var soundOnNotify = true
        
        @Volatile
        private var serviceInstance: WeakReference<GridConnectionService>? = null
        
        private val visibleActivities = Collections.synchronizedSet(HashSet<Activity>())

        @JvmStatic
        fun getGridConnection(): SLGridConnection {
            if (gridConnection == null) {
                synchronized(this) {
                    if (gridConnection == null) {
                        gridConnection = SLGridConnection()
                    }
                }
            }
            return gridConnection!!
        }

        @JvmStatic
        fun getServiceInstance(): GridConnectionService? {
            return serviceInstance?.get()
        }

        @JvmStatic
        fun hasVisibleActivities(): Boolean {
            return visibleActivities.isNotEmpty()
        }

        @JvmStatic
        fun setActivityVisible(activity: Activity, visible: Boolean) {
            if (visible) {
                visibleActivities.add(activity)
            } else {
                visibleActivities.remove(activity)
            }
        }

        private fun notifySettingsByType(type: NotificationType): NotificationSettings {
            return when (type) {
                NotificationType.Group -> notifyGroup
                NotificationType.LocalChat -> notifyLocalChat
                NotificationType.Private -> notifyPrivate
            }
        }
    }

    // Instance fields
    private val eventBus = EventBus.getInstance()
    private val mBinder: IBinder = GridServiceBinder()
    private val mHandler = Handler(Looper.getMainLooper())
    private val shownNotificationIds = Collections.newSetFromMap(
        Collections.synchronizedMap(HashMap<Int, Boolean>())
    )

    private var prefs: SharedPreferences? = null
    private var connectedAgentNameRetriever: ChatterNameRetriever? = null
    private var onlineNotificationInfo = OnlineNotificationInfo(
        onlineNotify, this, gridName, gridConnection, connectedAgentNameRetriever, null
    )
    
    private var wifiLock: WifiManager.WifiLock? = null
    private var cloudSyncEnabled = false
    private var cloudSyncServiceConnection: CloudSyncServiceConnection? = null
    private var cloudSyncUserManager: UserManager? = null
    private var cloudPluginReceiverRegistered = false
    private var voicePluginReceiverRegistered = false
    private var voicePluginServiceConnection: VoicePluginServiceConnection? = null
    private var unreadNotifySubscription: Subscription<Boolean, UnreadNotifications>? = null

    private val currentLocationInfo = SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(
        UIThreadExecutor.getInstance()
    ) { /* Lambda implementation */ }

    // Inner class: GridServiceBinder
    inner class GridServiceBinder : Binder() {
        fun getEventBus(): EventBus = eventBus
        fun getGridConn(): SLGridConnection? = gridConnection
    }

    // BroadcastReceiver: Cloud plugin installed
    private val cloudPluginInstalledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val packageName = Strings.nullToEmpty(intent.data?.schemeSpecificPart)
            if (packageName == "com.lumiyaviewer.lumiya.cloud") {
                val connection = gridConnection
                if (connection?.connectionState == ConnectionState.Connected) {
                    connection.activeAgentUUID?.let { uuid ->
                        UserManager.getUserManager(uuid)?.let { userManager ->
                            startCloudSync(userManager)
                        }
                    }
                }
            }
        }
    }

    // BroadcastReceiver: Voice plugin installed
    private val voicePluginInstalledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val packageName = Strings.nullToEmpty(intent.data?.schemeSpecificPart)
            if (packageName == "com.lumiyaviewer.lumiya.voice") {
                val connection = gridConnection
                if (connection?.connectionState == ConnectionState.Connected) {
                    connection.activeAgentUUID?.let { uuid ->
                        UserManager.getUserManager(uuid)?.let { userManager ->
                            userManager.activeAgentCircuit?.modules?.voice?.updateVoiceEnabledStatus()
                        }
                    }
                }
            }
        }
    }

    // Handler: License check
    private val licenseCheckHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                R.id.msg_licensing_allow -> {
                    Debug.Printf("License: License check ok.")
                    if (msg.obj is SLAuthParams) {
                        performLogin(msg.obj as SLAuthParams)
                    }
                }
                R.id.msg_licensing_app_error -> {
                    val error = if (msg.obj is String) msg.obj as String else "Internal application error"
                    Debug.Printf("License: License check app error: %s", error)
                    eventBus.publish(SLLoginResultEvent(false, "License check failed: $error.", null))
                }
                R.id.msg_licensing_dont_allow -> {
                    Debug.Printf("License: License check failed.")
                    eventBus.publish(SLLoginResultEvent(
                        false,
                        "You don't have valid license to use this application.",
                        null
                    ))
                }
            }
        }
    }

    // Callback: Active agent name updated
    private val onActiveAgentNameUpdated = ChatterNameRetriever.OnChatterNameUpdated { _, _ ->
        updateOnlineNotification()
    }

    init {
        getGridConnection()
        eventBus.subscribe(this, null, mHandler)
    }

    // Service lifecycle methods
    override fun onCreate() {
        super.onCreate()
        serviceInstance = WeakReference(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        prefs?.registerOnSharedPreferenceChangeListener(this)
        readPreferences(prefs)
        updateOnlineNotification()
    }

    override fun onDestroy() {
        prefs?.unregisterOnSharedPreferenceChangeListener(this)
        prefs = null
        
        onlineNotificationInfo = OnlineNotificationInfo(
            onlineNotify, this, gridName, gridConnection, connectedAgentNameRetriever, null
        )
        
        stopForeground(true)
        shownNotificationIds.clear()
        eventBus.unsubscribe(this)
        serviceInstance = null
        
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder = mBinder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Debug.Printf(
            "onStartCommand: intent is %s, flags %08x",
            if (intent != null) "not null" else "null",
            flags
        )
        
        val actualIntent = if ((flags and START_FLAG_REDELIVERY) != 0) null else intent
        handleStartService(actualIntent)
        return START_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        Debug.Log("GridConnectionService: onUnbind called, connection state = ${gridConnection?.connectionState}")
        
        if (gridConnection?.connectionState == ConnectionState.Idle) {
            Debug.Log("GridConnectionService: Stopping self because unbind and no clients are bound")
            stopCloudSync()
            stopSelf()
        }
        
        return super.onUnbind(intent)
    }

    // SharedPreferences listener
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        readPreferences(sharedPreferences)
        updateOnlineNotification()
    }

    @EventHandler
    fun onGlobalPreferencesChanged(event: GlobalOptionsChangedEvent) {
        readPreferences(event.preferences)
    }

    // TO BE CONTINUED IN NEXT SECTION...
    // This file is too large to convert in one message.
    // Remaining methods will be added in subsequent sections.

    private fun handleStartService(intent: Intent?) {
        Debug.Printf(
            "GridConnectionService: service is now started, intent is %s",
            if (intent != null) "not null" else "null"
        )
        updateOnlineNotification()
        
        intent?.let {
            when (Strings.nullToEmpty(it.action)) {
                LOGIN_ACTION -> {
                    LicenseChecker(applicationContext, licenseCheckHandler, SLAuthParams(it))
                }
                VoicePluginServiceConnection.ACTION_VOICE_REJECT -> {
                    voicePluginServiceConnection?.rejectCall(it)
                }
                VoicePluginServiceConnection.ACTION_VOICE_ACCEPT -> {
                    voicePluginServiceConnection?.acceptCall(it)
                }
            }
        }
    }

    private fun readPreferences(sharedPreferences: SharedPreferences?) {
        sharedPreferences?.let { prefs ->
            onlineNotify = prefs.getBoolean("onlineNotify", true)
            soundOnNotify = prefs.getBoolean("soundOnNotify", true)
            cloudSyncEnabled = prefs.getBoolean("sync_to_gdrive", false)
            
            notifyLocalChat.Load(prefs)
            notifyPrivate.Load(prefs)
            notifyGroup.Load(prefs)
            
            val autoresponse = prefs.getBoolean("autoresponse", false)
            val autoresponseText = prefs.getString(
                "autoresponseText",
                "(Autoresponse) I have auto-response feature enabled. I will respond shortly."
            ) ?: ""
            SLGridConnection.setAutoresponseInfo(autoresponse, autoresponseText)
            
            Debug.Log("GridConnectionService: prefs: onlineNotify = $onlineNotify")
            Debug.Log("GridConnectionService: prefs: soundOnNotify = $soundOnNotify")
            
            gridConnection?.let { connection ->
                try {
                    connection.modules.HandleGlobalOptionsChange()
                } catch (e: NotConnectedException) {
                    // Ignore
                }
            }
            
            updateOnlineNotification()
            updateCloudSyncStatus()
        }
    }

    private fun performLogin(authParams: SLAuthParams) {
        gridName = authParams.gridName
        gridConnection?.Connect(authParams)
    }

    private fun onCurrentLocationInfo(info: CurrentLocationInfo) {
        updateOnlineNotification()
    }

    private fun onUnreadNotification(notifications: UnreadNotifications) {
        showUnreadNotification(notifications)
    }

    // More methods to be added...
    // This is a partial conversion - the file is too large for one response
    
    private fun hideUnreadNotificationSingle(notificationId: Int) {
        if (shownNotificationIds.contains(notificationId)) {
            shownNotificationIds.remove(notificationId)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }

    // Voice methods
    fun acceptVoiceCall(chatterID: ChatterID) {
        voicePluginServiceConnection?.acceptVoiceCall(chatterID)
    }

    fun acceptVoiceCall(voiceRinging: VoiceRinging) {
        voicePluginServiceConnection?.acceptVoiceCall(voiceRinging)
    }

    fun enableVoiceMic(enabled: Boolean) {
        voicePluginServiceConnection?.enableVoiceMic(enabled)
    }

    fun getVoicePluginServiceConnection(): VoicePluginServiceConnection? {
        return voicePluginServiceConnection
    }

    fun startVoice(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager) {
        connectToVoicePlugin(voiceLoginInfo, userManager)
    }

    fun stopVoice() {
        voicePluginServiceConnection?.disconnect()
        voicePluginServiceConnection = null
    }

    fun terminateVoiceCall(chatterID: ChatterID) {
        voicePluginServiceConnection?.terminateVoiceCall(chatterID)
    }

    // Event handlers  
    @EventHandler
    fun handleConnectEvent(event: SLLoginResultEvent) {
        // TODO: Implement full method body from original
    }

    @EventHandler
    fun handleConnectionStateChangedEvent(event: SLConnectionStateChangedEvent) {
        // TODO: Implement full method body from original
    }

    @EventHandler
    fun handleDisconnectEvent(event: SLDisconnectEvent) {
        // TODO: Implement full method body from original
    }

    // Private helper methods
    private fun connectToVoicePlugin(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager) {
        // TODO: Implement full method body from original - this is complex
    }

    private fun startCloudSync(userManager: UserManager) {
        // TODO: Implement
    }

    private fun stopCloudSync() {
        // TODO: Implement
    }

    private fun updateCloudSyncStatus() {
        // TODO: Implement
    }

    private fun updateOnlineNotification() {
        // TODO: Implement
    }

    private fun showUnreadNotification(notifications: UnreadNotifications) {
        // TODO: Implement - this is a large method
    }
}

// NOTE: This is a PARTIAL conversion due to file size (1,627 lines)
// The remaining complex methods need to be added in follow-up work:
// - connectToVoicePlugin (complex voice setup)
// - showUnreadNotification (large notification logic)
// - updateOnlineNotification (notification updates)
// - Event handler implementations
// - Cloud sync methods
