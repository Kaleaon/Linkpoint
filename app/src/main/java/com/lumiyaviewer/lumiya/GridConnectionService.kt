package com.lumiyaviewer.lumiya

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.preference.PreferenceManager
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
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
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotifications
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.sync.CloudSyncServiceConnection
import com.lumiyaviewer.lumiya.ui.notify.DummyNotificationChannelManager
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels.Channel
import com.lumiyaviewer.lumiya.ui.notify.OnlineNotificationInfo
import com.lumiyaviewer.lumiya.ui.settings.NotificationSettings
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import com.lumiyaviewer.lumiya.utils.LEDAction
import com.lumiyaviewer.lumiya.voice.common.messages.VoiceRinging
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo
import com.lumiyaviewer.lumiya.voiceintf.VoicePluginServiceConnection
import java.lang.ref.WeakReference
import java.util.*

class GridConnectionService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    inner class GridServiceBinder : Binder() {
        val eventBus: EventBus
            get() = this@GridConnectionService.eventBus
        
        val gridConn: SLGridConnection?
            get() = gridConnection
    }

    private val cloudPluginInstalledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Strings.nullToEmpty(intent.data?.schemeSpecificPart) == "com.lumiyaviewer.lumiya.cloud" &&
                gridConnection?.connectionState == ConnectionState.Connected
            ) {
                val activeAgentUUID = gridConnection?.activeAgentUUID
                if (activeAgentUUID != null) {
                    val userManager = UserManager.getUserManager(activeAgentUUID)
                    if (userManager != null) {
                        startCloudSync(userManager)
                    }
                }
            }
        }
    }

    private var cloudPluginReceiverRegistered = false
    private var cloudSyncEnabled = false
    private var cloudSyncServiceConnection: CloudSyncServiceConnection? = null
    @Nullable
    private var cloudSyncUserManager: UserManager? = null
    private var connectedAgentNameRetriever: ChatterNameRetriever? = null
    
    private val currentLocationInfo = SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(
        UIThreadExecutor.getInstance()
    ) { onCurrentLocationInfo(it) }
    
    private val eventBus: EventBus = EventBus.getInstance()
    
    private val licenseCheckHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                R.id.msg_licensing_allow -> {
                    Debug.Printf("License: License check ok.")
                    if (msg.obj is SLAuthParams) {
                        performLogin(msg.obj as SLAuthParams)
                    }
                }
                R.id.msg_licensing_app_error -> {
                    val error = if (msg.obj is String) {
                        msg.obj as String
                    } else {
                        "Internal application error"
                    }
                    Debug.Printf("License: License check app error: %s", error)
                    eventBus.publish(SLLoginResultEvent(false, "License check failed: $error.", null))
                }
                R.id.msg_licensing_dont_allow -> {
                    Debug.Printf("License: License check failed.")
                    eventBus.publish(
                        SLLoginResultEvent(
                            false,
                            "You don't have valid license to use this application.",
                            null
                        )
                    )
                }
            }
        }
    }
    
    private val mBinder: IBinder = GridServiceBinder()
    private val mHandler = Handler()
    
    private val onActiveAgentNameUpdated = OnChatterNameUpdated { retriever ->
        updateNotificationOnNameRetrieve(retriever)
    }
    
    private var onlineNotificationInfo = OnlineNotificationInfo(
        onlineNotify,
        this,
        gridName,
        gridConnection,
        connectedAgentNameRetriever,
        null
    )
    
    private var prefs: SharedPreferences? = null
    private val shownNotificationIds = Collections.newSetFromMap(
        Collections.synchronizedMap(HashMap<Int, Boolean>())
    )
    
    private var unreadNotifySubscription: Subscription<Boolean, UnreadNotifications>? = null
    
    private val voicePluginInstalledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Strings.nullToEmpty(intent.data?.schemeSpecificPart) == "com.lumiyaviewer.lumiya.voice" &&
                gridConnection?.connectionState == ConnectionState.Connected
            ) {
                val activeAgentUUID = gridConnection?.activeAgentUUID
                if (activeAgentUUID != null) {
                    val userManager = UserManager.getUserManager(activeAgentUUID)
                    userManager?.activeAgentCircuit?.modules?.voice?.updateVoiceEnabledStatus()
                }
            }
        }
    }
    
    private var voicePluginReceiverRegistered = false
    private var voicePluginServiceConnection: VoicePluginServiceConnection? = null
    private var wifiLock: WifiManager.WifiLock? = null

    init {
        getGridConnection()
        eventBus.subscribe(this, null, mHandler)
    }

    private fun connectToVoicePlugin(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager?) {
        if (voicePluginServiceConnection == null) {
            voicePluginServiceConnection = VoicePluginServiceConnection(this)
            val intent = Intent().apply {
                component = ComponentName(
                    "com.lumiyaviewer.lumiya.voice",
                    "com.lumiyaviewer.lumiya.voice.VoiceService"
                )
            }
            
            val bindService = try {
                bindService(intent, voicePluginServiceConnection!!, Context.BIND_AUTO_CREATE)
            } catch (e: Throwable) {
                Debug.Warning(e)
                false
            }
            
            Debug.Printf("LumiyaVoice: bindService = %b", bindService)
            
            if (!bindService) {
                voicePluginServiceConnection = null
                
                val intentFilter = IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addDataScheme("package")
                    if (Build.VERSION.SDK_INT >= 19) {
                        addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.voice", 0)
                    }
                }
                
                registerReceiver(voicePluginInstalledReceiver, intentFilter)
                voicePluginReceiverRegistered = true
                
                if (userManager != null && VoicePluginServiceConnection.shouldDisplayInstallOffer()) {
                    userManager.chatterList.activeChattersManager.handleChatEvent(
                        ChatterID.getLocalChatterID(userManager.userID),
                        SLVoiceUpgradeEvent(
                            userManager.userID,
                            LumiyaApp.getContext().getString(R.string.plugin_install_for_voice_needed),
                            true,
                            LicenseChecker.VOICE_PLUGIN_URL
                        ),
                        false
                    )
                }
            }
        }
        
        voicePluginServiceConnection?.setVoiceLoginInfo(voiceLoginInfo, userManager)
    }

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

    private fun hideUnreadNotificationSingle(notificationId: Int) {
        if (shownNotificationIds.contains(notificationId)) {
            shownNotificationIds.remove(notificationId)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(notificationId)
        }
    }

    private fun onCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo?) {
        updateOnlineNotification()
    }

    private fun onUnreadNotification(unreadNotifications: UnreadNotifications) {
        showUnreadNotification(unreadNotifications)
    }

    private fun performLogin(authParams: SLAuthParams) {
        gridName = authParams.gridName
        gridConnection?.connect(authParams)
    }

    private fun readPreferences(sharedPreferences: SharedPreferences) {
        onlineNotify = sharedPreferences.getBoolean("onlineNotify", true)
        soundOnNotify = sharedPreferences.getBoolean("soundOnNotify", true)
        cloudSyncEnabled = sharedPreferences.getBoolean("sync_to_gdrive", false)
        
        notifyLocalChat.load(sharedPreferences)
        notifyPrivate.load(sharedPreferences)
        notifyGroup.load(sharedPreferences)
        
        SLGridConnection.setAutoresponseInfo(
            sharedPreferences.getBoolean("autoresponse", false),
            sharedPreferences.getString(
                "autoresponseText",
                "(Autoresponse) I have auto-response feature enabled. I will respond shortly."
            )
        )
        
        Debug.Log("GridConnectionService: prefs: onlineNotify = $onlineNotify")
        Debug.Log("GridConnectionService: prefs: soundOnNotify = $soundOnNotify")
        
        gridConnection?.let {
            try {
                it.modules.handleGlobalOptionsChange()
            } catch (e: NotConnectedException) {
                // Ignore
            }
        }
        
        updateOnlineNotification()
        updateCloudSyncStatus()
    }

    private fun showUnreadNotification(unreadNotifications: UnreadNotifications) {
        val notificationChannels = NotificationChannels.getInstance()
        
        if (notificationChannels.useNotificationGroups()) {
            val filteredNotifications = if (notificationChannels.areNotificationsSystemControlled()) {
                unreadNotifications.filter(notificationChannels.getEnabledTypes(this))
            } else {
                unreadNotifications
            }
            
            if (filteredNotifications.notificationGroups().isNotEmpty()) {
                val merged = filteredNotifications.merge()
                
                val mostImportantType = merged.mostImportantFreshType().or(
                    merged.mostImportantType().or(NotificationType.LocalChat)
                )
                
                showUnreadNotificationSingle(
                    merged,
                    R.id.unread_notify_id,
                    notificationChannels.getChannelName(
                        notificationChannels.getChannelByType(mostImportantType)
                    ),
                    NotificationChannels.MESSAGE_NOTIFICATION_GROUP,
                    true,
                    null
                )
                
                for (notificationType in NotificationType.VALUES) {
                    val channel = notificationChannels.getChannelByType(notificationType)
                    if (channel != null) {
                        val groupInfo = filteredNotifications.notificationGroups()[notificationType]
                        val sortKey = (9 - notificationType.priority).toString()
                        
                        showUnreadNotificationSingle(
                            groupInfo,
                            channel.notificationId,
                            notificationChannels.getChannelName(channel),
                            NotificationChannels.MESSAGE_NOTIFICATION_GROUP,
                            false,
                            sortKey
                        )
                    }
                }
                return
            } else if (shownNotificationIds.isNotEmpty()) {
                val notificationManager = 
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
                for (notificationId in shownNotificationIds) {
                    notificationManager.cancel(notificationId)
                }
                shownNotificationIds.clear()
                return
            } else {
                return
            }
        }
        
        showUnreadNotificationSingle(
            unreadNotifications.merge(),
            R.id.unread_notify_id,
            DummyNotificationChannelManager.DEFAULT_NOTIFICATION_CHANNEL,
            null,
            true,
            null
        )
    }

    private fun showUnreadNotificationSingle(
        notificationInfo: UnreadNotificationInfo?,
        notificationId: Int,
        channelName: String,
        groupKey: String?,
        isSummary: Boolean,
        sortKey: String?
    ) {
        val notificationManager = 
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Check if we should show notification
        val shouldShow = notificationInfo?.let {
            it.totalUnreadCount() != 0 || it.objectPopupInfo().objectPopupsCount() != 0
        } ?: false
        
        if (!shouldShow) {
            hideUnreadNotificationSingle(notificationId)
            return
        }
        
        Debug.Log("GridConnectionService: updateUnreadNotification: notification state changed.")
        
        val builder = NotificationCompat.Builder(this, channelName).apply {
            setSmallIcon(R.drawable.ic_notification)
            
            groupKey?.let { key ->
                setGroup(key)
                setGroupSummary(isSummary)
                setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                sortKey?.let { setContentSortKey(it) }
            }
        }
        
        val agentUUID = notificationInfo!!.agentUUID()
        val userManager = UserManager.getUserManager(agentUUID)
        
        // Determine single source if applicable
        val singleSource = if (notificationInfo.unreadSources().size == 1) {
            notificationInfo.unreadSources()[0]
        } else null
        
        // Create intent for notification
        val chatFragmentFactory = com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory.getInstance()
        val selection = singleSource?.let {
            com.lumiyaviewer.lumiya.ui.chat.ChatFragment.makeSelection(it.chatterID())
        }
        
        val intent = chatFragmentFactory.createIntent(this, selection).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            com.lumiyaviewer.lumiya.ui.common.ActivityUtils.setActiveAgentID(this, agentUUID)
        }
        
        // Add weak selection if there's a single fresh source
        notificationInfo.singleFreshSource().orNull()?.let { freshSource ->
            intent.putExtra(
                "weakSelection",
                com.lumiyaviewer.lumiya.ui.chat.ChatFragment.makeSelection(freshSource.chatterID())
            )
        }
        
        // Capture notification with user manager
        val finalIntent = userManager?.unreadNotificationManager?.captureNotify(
            notificationInfo,
            intent
        ) ?: intent
        
        // Build notification content
        buildNotificationContent(builder, notificationInfo, singleSource, userManager)
        
        // Configure notification
        builder.apply {
            setOngoing(false)
            setNumber(notificationInfo.totalUnreadCount())
            
            val pendingIntent = PendingIntent.getActivity(
                this@GridConnectionService,
                REQUEST_CODE_UNREAD_NOTIFY,
                finalIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            setContentIntent(pendingIntent)
            
            setDefaults(0)
            setOnlyAlertOnce(true)
        }
        
        // Configure LED and sound only for non-grouped or summary notifications
        if (groupKey == null || isSummary) {
            configureLEDAndSound(builder, notificationInfo)
        }
        
        shownNotificationIds.add(notificationId)
        notificationManager.notify(notificationId, builder.build())
    }

    private fun buildNotificationContent(
        builder: NotificationCompat.Builder,
        notificationInfo: UnreadNotificationInfo,
        singleSource: UnreadNotificationInfo.UnreadMessageSource?,
        userManager: UserManager?
    ) {
        if (singleSource != null && notificationInfo.objectPopupInfo().objectPopupsCount() == 0) {
            // Single source notification
            val chatterName = singleSource.chatterName().or(gridName) as String
            builder.setContentTitle(chatterName)
            
            if (singleSource.unreadMessagesCount() == 1 && 
                singleSource.unreadMessages().size == 1
            ) {
                val message = singleSource.unreadMessages()[0]
                val plainText = message.getPlainTextMessage(
                    this,
                    singleSource.chatterID().userManager,
                    true
                )
                builder.setContentText(plainText)
            } else {
                val text = getString(
                    R.string.notification_message_count,
                    singleSource.unreadMessagesCount()
                )
                builder.setContentText(text)
            }
            
            // Use inbox style for multiple messages
            if (singleSource.unreadMessages().size > 1) {
                val inboxStyle = NotificationCompat.InboxStyle()
                
                for (message in singleSource.unreadMessages()) {
                    val isUser = singleSource.chatterID().chatterType == ChatterID.ChatterType.User
                    val plainText = message.getPlainTextMessage(
                        this,
                        singleSource.chatterID().userManager,
                        isUser,
                        "  ",
                        " "
                    )
                    inboxStyle.addLine(plainText)
                }
                
                inboxStyle.setBigContentTitle(chatterName)
                inboxStyle.setSummaryText(
                    getString(R.string.notification_message_count, notificationInfo.totalUnreadCount())
                )
                
                builder.setStyle(inboxStyle)
            }
        } else if (notificationInfo.totalUnreadCount() != 0) {
            // Multiple sources notification
            buildMultiSourceNotification(builder, notificationInfo)
        } else if (notificationInfo.objectPopupInfo().objectPopupsCount() != 0) {
            // Object popup notification
            buildObjectPopupNotification(builder, notificationInfo)
        }
        
        // Set ticker for fresh messages
        if (notificationInfo.freshMessagesCount() > 0) {
            val ticker = buildTickerText(notificationInfo)
            ticker?.let { builder.setTicker(it) }
        }
    }

    private fun buildMultiSourceNotification(
        builder: NotificationCompat.Builder,
        notificationInfo: UnreadNotificationInfo
    ) {
        val totalCount = notificationInfo.totalUnreadCount()
        var title = resources.getQuantityString(
            R.plurals.notification_unread_messages,
            totalCount,
            totalCount
        )
        
        if (notificationInfo.objectPopupInfo().objectPopupsCount() > 0) {
            title += resources.getQuantityString(
                R.plurals.notification_object_popups,
                notificationInfo.objectPopupInfo().objectPopupsCount(),
                notificationInfo.objectPopupInfo().objectPopupsCount()
            )
        }
        
        builder.setContentTitle(title)
        
        // Find name for content text
        val sourceName = notificationInfo.unreadSources()
            .firstOrNull { it.chatterName().isPresent }
            ?.chatterName()?.orNull() as? String
        
        val sourceCount = notificationInfo.unreadSources().size
        val contentText = when {
            sourceName != null && sourceCount > 1 -> {
                "$sourceName ${getString(R.string.notification_and_others, sourceCount - 1)}"
            }
            sourceName != null -> sourceName
            else -> getString(R.string.notification_multiple_sources, sourceCount)
        }
        
        builder.setContentText(contentText)
        
        // Use inbox style
        val inboxStyle = NotificationCompat.InboxStyle()
        
        for (source in notificationInfo.unreadSources()) {
            for (message in source.unreadMessages()) {
                val plainText = message.getPlainTextMessage(
                    this,
                    source.chatterID().userManager,
                    false,
                    "  ",
                    " "
                )
                inboxStyle.addLine(plainText)
            }
        }
        
        inboxStyle.setBigContentTitle(
            getString(R.string.notification_message_count, totalCount)
        )
        inboxStyle.setSummaryText(contentText)
        
        builder.setStyle(inboxStyle)
    }

    private fun buildObjectPopupNotification(
        builder: NotificationCompat.Builder,
        notificationInfo: UnreadNotificationInfo
    ) {
        val lastPopup = notificationInfo.objectPopupInfo().lastObjectPopup().orNull() 
            as? UnreadNotificationInfo.ObjectPopupMessage
        val freshCount = notificationInfo.objectPopupInfo().freshObjectPopupsCount()
        
        if (freshCount == 1 && lastPopup != null) {
            builder.setContentTitle(lastPopup.objectName())
            builder.setContentText(lastPopup.message())
        } else {
            val title = resources.getQuantityString(
                R.plurals.notification_object_popups,
                notificationInfo.objectPopupInfo().objectPopupsCount(),
                notificationInfo.objectPopupInfo().objectPopupsCount()
            )
            builder.setContentTitle(title)
            
            lastPopup?.let {
                builder.setContentText("${it.objectName()}: ${it.message()}")
            } ?: run {
                val text = resources.getQuantityString(
                    R.plurals.notification_object_popups,
                    notificationInfo.objectPopupInfo().objectPopupsCount(),
                    notificationInfo.objectPopupInfo().objectPopupsCount()
                )
                builder.setContentText(text)
            }
        }
    }

    private fun buildTickerText(notificationInfo: UnreadNotificationInfo): String? {
        if (notificationInfo.freshMessagesCount() == 0) {
            return buildObjectPopupTicker(notificationInfo)
        }
        
        val singleFresh = notificationInfo.singleFreshSource().orNull() 
            as? UnreadNotificationInfo.UnreadMessageSource
        
        return if (singleFresh != null && 
                   notificationInfo.freshMessagesCount() == 1 &&
                   singleFresh.unreadMessages().isNotEmpty()
        ) {
            val lastMessage = singleFresh.unreadMessages().last()
            val chatterType = singleFresh.chatterID().chatterType
            
            val prefix = when (chatterType) {
                ChatterID.ChatterType.Local -> getString(R.string.notification_local_chat)
                ChatterID.ChatterType.Group -> getString(R.string.notification_group_chat)
                ChatterID.ChatterType.User -> getString(R.string.notification_private_message)
            }
            
            val messageText = lastMessage.getPlainTextMessage(
                this,
                singleFresh.chatterID().userManager,
                false
            ).toString().trim()
            
            val truncated = truncateText(messageText, 30)
            "[$prefix] $truncated"
        } else {
            getString(
                R.string.notification_message_count,
                notificationInfo.freshMessagesCount()
            )
        }
    }

    private fun buildObjectPopupTicker(notificationInfo: UnreadNotificationInfo): String? {
        val freshCount = notificationInfo.objectPopupInfo().freshObjectPopupsCount()
        if (freshCount == 0) return null
        
        val lastPopup = notificationInfo.objectPopupInfo().lastObjectPopup().orNull()
            as? UnreadNotificationInfo.ObjectPopupMessage
        
        return if (freshCount == 1 && lastPopup != null) {
            val text = "${lastPopup.objectName()}: ${lastPopup.message()}"
            truncateText(text, 30)
        } else {
            resources.getQuantityString(
                R.plurals.notification_object_popups,
                notificationInfo.objectPopupInfo().objectPopupsCount(),
                notificationInfo.objectPopupInfo().objectPopupsCount()
            )
        }
    }

    private fun truncateText(text: String, maxLength: Int): String {
        val newlineIndex = text.indexOf('\n')
        val truncated = if (newlineIndex >= 0) {
            text.substring(0, newlineIndex)
        } else {
            text
        }
        
        return if (truncated.length > maxLength) {
            "${truncated.substring(0, maxLength)}..."
        } else {
            truncated
        }
    }

    private fun configureLEDAndSound(
        builder: NotificationCompat.Builder,
        notificationInfo: UnreadNotificationInfo
    ) {
        var ledAction = LEDAction.None
        var ledColor = 0
        var notifySettings: NotificationSettings? = null
        
        // Determine LED action and color from total unread
        if (notificationInfo.totalUnreadCount() > 0) {
            val mostImportant = notificationInfo.mostImportantType().orNull() as? NotificationType
            mostImportant?.let {
                val settings = notifySettingsByType(it)
                ledAction = settings.ledAction
                ledColor = settings.ledColor
            }
        } else if (notificationInfo.objectPopupInfo().objectPopupsCount() > 0) {
            val settings = notifySettingsByType(NotificationType.LocalChat)
            ledAction = settings.ledAction
            ledColor = settings.ledColor
        }
        
        // Determine notification settings from fresh messages
        if (notificationInfo.freshMessagesCount() > 0) {
            val mostImportantFresh = notificationInfo.mostImportantFreshType().orNull() 
                as? NotificationType
            mostImportantFresh?.let {
                notifySettings = notifySettingsByType(it)
            }
        } else if (notificationInfo.objectPopupInfo().freshObjectPopupsCount() > 0) {
            notifySettings = notifySettingsByType(NotificationType.LocalChat)
        }
        
        Debug.Printf(
            "GridConnectionService: updateUnreadNotification: ledAction = %s, color = %08x",
            ledAction.toString(),
            ledColor
        )
        
        // Configure LED
        when (ledAction) {
            LEDAction.Always -> builder.setLights(ledColor, 1, 0)
            LEDAction.Fast -> builder.setLights(ledColor, 300, 100)
            LEDAction.Slow -> builder.setLights(ledColor, 1000, 500)
            LEDAction.None -> builder.setLights(ledColor, 0, 0)
        }
        
        // Configure sound
        if (soundOnNotify && notifySettings != null) {
            notifySettings.ringtone?.let { ringtone ->
                builder.setSound(Uri.parse(ringtone))
                builder.setOnlyAlertOnce(false)
            }
        } else {
            Debug.Printf("GridConnectionService: will not emit sound.")
        }
    }

    private fun startCloudSync(userManager: UserManager) {
        cloudSyncUserManager = userManager
        updateCloudSyncStatus()
    }

    private fun stopCloudSync() {
        cloudSyncUserManager = null
        updateCloudSyncStatus()
    }

    private fun updateCloudSyncStatus() {
        if (!cloudSyncEnabled || cloudSyncUserManager == null) {
            cloudSyncServiceConnection?.stopSyncing()
            cloudSyncServiceConnection = null
            
            if (cloudPluginReceiverRegistered) {
                try {
                    unregisterReceiver(cloudPluginInstalledReceiver)
                } catch (e: Throwable) {
                    Debug.Warning(e)
                }
            }
            cloudPluginReceiverRegistered = false
        } else if (cloudSyncServiceConnection == null) {
            cloudSyncServiceConnection = CloudSyncServiceConnection(this, cloudSyncUserManager!!)
            
            val intent = Intent().apply {
                component = ComponentName(
                    "com.lumiyaviewer.lumiya.cloud",
                    "com.lumiyaviewer.lumiya.cloud.DriveSyncService"
                )
            }
            
            val bindService = try {
                bindService(intent, cloudSyncServiceConnection!!, Context.BIND_AUTO_CREATE)
            } catch (e: Throwable) {
                Debug.Warning(e)
                false
            }
            
            Debug.Printf("LumiyaCloud: bindService = %b", bindService)
            
            if (!bindService) {
                cloudSyncServiceConnection?.stopSyncing()
                
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(LicenseChecker.CLOUD_PLUGIN_URL)
                }
                
                cloudSyncServiceConnection?.showSyncingError(
                    getString(R.string.cloud_sync_not_installed),
                    getString(R.string.cloud_sync_not_installed_long),
                    viewIntent
                )
                
                cloudSyncServiceConnection = null
                
                val intentFilter = IntentFilter().apply {
                    addAction(Intent.ACTION_PACKAGE_ADDED)
                    addDataScheme("package")
                    if (Build.VERSION.SDK_INT >= 19) {
                        addDataSchemeSpecificPart("com.lumiyaviewer.lumiya.cloud", 0)
                    }
                }
                
                registerReceiver(cloudPluginInstalledReceiver, intentFilter)
                cloudPluginReceiverRegistered = true
            }
        }
    }

    private fun updateOnlineNotification() {
        var shouldKeepWifi = false
        
        gridConnection?.let { connection ->
            val connectionState = connection.connectionState
            
            shouldKeepWifi = connectionState != ConnectionState.Idle && 
                            GlobalOptions.getInstance().keepWifiOn
            
            val activeAgentUUID = connection.activeAgentUUID
            
            if (activeAgentUUID == null || connectionState == ConnectionState.Idle) {
                connectedAgentNameRetriever?.dispose()
                connectedAgentNameRetriever = null
            } else if (connectedAgentNameRetriever == null) {
                connectedAgentNameRetriever = ChatterNameRetriever(
                    ChatterID.getUserChatterID(activeAgentUUID, activeAgentUUID),
                    onActiveAgentNameUpdated,
                    UIThreadExecutor.getSerialInstance()
                )
            }
        }
        
        // Manage WiFi lock
        if (shouldKeepWifi && wifiLock == null) {
            applicationContext?.let { context ->
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "Lumiya")
                wifiLock?.acquire()
                Debug.Printf("WiFi lock acquired")
            }
        } else if (wifiLock != null && !shouldKeepWifi) {
            wifiLock?.release()
            wifiLock = null
            Debug.Printf("WiFi lock released")
        }
        
        // Update online notification
        val newOnlineInfo = OnlineNotificationInfo(
            onlineNotify,
            this,
            gridName,
            gridConnection,
            connectedAgentNameRetriever,
            currentLocationInfo.data as? CurrentLocationInfo
        )
        
        if (newOnlineInfo != onlineNotificationInfo) {
            onlineNotificationInfo = newOnlineInfo
            
            val notification = onlineNotificationInfo.getNotification(this)
            if (notification != null) {
                startForeground(R.id.online_notify_id, notification)
            } else {
                stopForeground(true)
            }
        }
    }

    private fun updateNotificationOnNameRetrieve(retriever: ChatterNameRetriever) {
        updateOnlineNotification()
    }

    fun acceptVoiceCall(chatterID: ChatterID) {
        voicePluginServiceConnection?.acceptVoiceCall(chatterID)
    }

    fun acceptVoiceCall(voiceRinging: VoiceRinging) {
        voicePluginServiceConnection?.acceptVoiceCall(voiceRinging)
    }

    fun enableVoiceMic(enabled: Boolean) {
        voicePluginServiceConnection?.enableVoiceMic(enabled)
    }

    @Nullable
    fun getVoicePluginServiceConnection(): VoicePluginServiceConnection? {
        return voicePluginServiceConnection
    }

    @EventHandler
    fun handleConnectEvent(event: SLLoginResultEvent) {
        val userManager = UserManager.getUserManager(event.activeAgentUUID)
        
        if (userManager != null) {
            unreadNotifySubscription = userManager.unreadNotificationManager
                .unreadNotifications
                .subscribe(
                    UnreadNotificationManager.unreadNotificationKey,
                    UIThreadExecutor.getSerialInstance()
                ) { onUnreadNotification(it) }
            
            currentLocationInfo.subscribe(
                userManager.currentLocationInfo,
                SubscriptionSingleKey.Value
            )
        }
        
        updateOnlineNotification()
        VoicePluginServiceConnection.setInstallOfferDisplayed(false)
        
        if (event.success) {
            startCloudSync(userManager!!)
        } else {
            Debug.Log("GridConnectionService: stopping self because of connect failed.")
            stopCloudSync()
            stopSelf()
        }
    }

    @EventHandler
    fun handleConnectionStateChangedEvent(event: SLConnectionStateChangedEvent) {
        updateOnlineNotification()
    }

    @EventHandler
    fun handleDisconnectEvent(event: SLDisconnectEvent) {
        updateOnlineNotification()
        Debug.Log("GridConnectionService: stopping self because of disconnect.")
        stopCloudSync()
        stopVoice()
        stopSelf()
        currentLocationInfo.unsubscribe()
        VoicePluginServiceConnection.setInstallOfferDisplayed(false)
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        
        serviceInstance = WeakReference(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        prefs?.registerOnSharedPreferenceChangeListener(this)
        prefs?.let { readPreferences(it) }
        
        updateOnlineNotification()
    }

    override fun onDestroy() {
        prefs?.unregisterOnSharedPreferenceChangeListener(this)
        prefs = null
        
        onlineNotificationInfo = OnlineNotificationInfo(
            onlineNotify,
            this,
            gridName,
            gridConnection,
            connectedAgentNameRetriever,
            null
        )
        
        stopForeground(true)
        shownNotificationIds.clear()
        
        eventBus.unsubscribe(this)
        serviceInstance = null
        
        super.onDestroy()
    }

    @EventHandler
    fun onGlobalPreferencesChanged(event: GlobalOptionsChangedEvent) {
        readPreferences(event.preferences)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        readPreferences(sharedPreferences)
        updateOnlineNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Debug.Printf(
            "onStartCommand: intent is %s, flags %08x",
            if (intent != null) "not null" else "null",
            flags
        )
        
        val actualIntent = if ((flags and Service.START_FLAG_REDELIVERY) != 0) null else intent
        handleStartService(actualIntent)
        
        return Service.START_STICKY
    }

    override fun onUnbind(intent: Intent): Boolean {
        Debug.Log(
            "GridConnectionService: onUnbind called, connection state = " +
            "${gridConnection?.connectionState}"
        )
        
        if (gridConnection?.connectionState == ConnectionState.Idle) {
            Debug.Log("GridConnectionService: Stopping self because unbind and no clients are bound")
            stopCloudSync()
            stopSelf()
        }
        
        return super.onUnbind(intent)
    }

    fun startVoice(voiceLoginInfo: VoiceLoginInfo, userManager: UserManager?) {
        connectToVoicePlugin(voiceLoginInfo, userManager)
    }

    fun stopVoice() {
        voicePluginServiceConnection?.disconnect()
        voicePluginServiceConnection = null
    }

    fun terminateVoiceCall(chatterID: ChatterID) {
        voicePluginServiceConnection?.terminateVoiceCall(chatterID)
    }

    companion object {
        const val LOGIN_ACTION = "com.lumiyaviewer.lumiya.ACTION_LOGIN"
        private const val REQUEST_CODE_UNREAD_NOTIFY = 2131755072
        
        @JvmStatic
        var gridConnection: SLGridConnection? = null
            private set
        
        @JvmStatic
        var gridName = "Second Life"
            private set
        
        @JvmStatic
        private var notifyGroup = NotificationSettings(NotificationType.Group)
        
        @JvmStatic
        private var notifyLocalChat = NotificationSettings(NotificationType.LocalChat)
        
        @JvmStatic
        private var notifyPrivate = NotificationSettings(NotificationType.Private)
        
        @JvmStatic
        private var onlineNotify = false
        
        @JvmStatic
        private var serviceInstance: WeakReference<GridConnectionService>? = null
        
        @JvmStatic
        private var soundOnNotify = true
        
        @JvmStatic
        private val visibleActivities = Collections.synchronizedSet(HashSet<Activity>())

        @JvmStatic
        fun getGridConnection(): SLGridConnection {
            if (gridConnection == null) {
                gridConnection = SLGridConnection()
            }
            return gridConnection!!
        }

        @JvmStatic
        @Nullable
        fun getServiceInstance(): GridConnectionService? {
            return serviceInstance?.get()
        }

        @JvmStatic
        fun hasVisibleActivities(): Boolean {
            return visibleActivities.isNotEmpty()
        }

        @JvmStatic
        private fun notifySettingsByType(notificationType: NotificationType): NotificationSettings {
            return when (notificationType) {
                NotificationType.Group -> notifyGroup
                NotificationType.LocalChat -> notifyLocalChat
                NotificationType.Private -> notifyPrivate
            }
        }

        @JvmStatic
        fun setActivityVisible(activity: Activity, visible: Boolean) {
            if (visible) {
                visibleActivities.add(activity)
            } else {
                visibleActivities.remove(activity)
            }
            
            if (visibleActivities.isNotEmpty() && gridConnection != null) {
                try {
                    if (gridConnection?.connectionState == ConnectionState.Connected) {
                        gridConnection?.agentCircuit?.unpauseAgent()
                    }
                } catch (e: NotConnectedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
