package com.linkpoint

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.NotificationCompat.Builder
import com.google.common.base.Strings
import com.linkpoint.media.AudioIntentReceiver
import com.linkpoint.media.AudioManagerWrapper
import com.linkpoint.media.MediaPlayerWrapper
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.avatar.SLMoveEvents
import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.slproto.users.manager.CurrentLocationInfo
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.profiles.ParcelPropertiesFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.media.StreamingMediaActivity
import java.lang.ref.WeakReference
import java.util.UUID

class StreamingMediaService : Service() {
    const val LOCATION_DESC_KEY: String = "location_desc"
    const val LOCATION_NAME_KEY: String = "location_name"
    const val MEDIA_URL_KEY: String = "media_url"
    private const val MSG_ON_AUDIO_FOCUS_CHANGE: Int = 100
    const val SubscriptionSingleDataPool<Boolean> isPlayingMedia = SubscriptionSingleDataPool()
    private AudioManagerWrapper audioManagerWrapper = null
    private UUID lastActiveAgentUUID = null
    private String lastLocationDesc = ""
    private String lastLocationName = ""
    private ParcelData lastParcelData = null
    private String lastURL = ""
    private val AudioFocusChangeHandler mHandler = AudioFocusChangeHandler(this, null)
    private MediaPlayerWrapper mediaWrapper = MediaPlayerWrapper()
    private AudioIntentReceiver noisyReceiver = AudioIntentReceiver()
    private Notification notify = null

    @JvmStatic
private class AudioFocusChangeHandler : Handler() {
        private val WeakReference<StreamingMediaService> streamingMediaService

        private AudioFocusChangeHandler(StreamingMediaService streamingMediaService) {
            this.streamingMediaService = WeakReference(streamingMediaService)
        }

        /* synthetic */ AudioFocusChangeHandler(StreamingMediaService streamingMediaService, AudioFocusChangeHandler audioFocusChangeHandler) {
            this(streamingMediaService)
        }

        fun handleMessage(message: Message) {
            if (message.what == 100) {
                val streamingMediaService: StreamingMediaService = (StreamingMediaService) this.streamingMediaService.get()
                if (streamingMediaService != null) {
                    streamingMediaService.handleAudioFocusChange(message.arg1)
                }
            }
        }
    }

     private fun handleAudioFocusChange(i: Int) {
        Debug.Log("StreamingMediaService: focusChange = " + i)
        if (i == -1) {
            isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE)
            this.mediaWrapper.stop()
            if (this.audioManagerWrapper != null) {
                this.audioManagerWrapper.abandonAudioFocus()
            }
            safeUnregisterReceiver()
            stopForeground(true)
            this.notify = null
            stopSelf()
        }
    }

     private fun handleStartService(intent: Intent) {
        if (intent != null) {
            val action: String = intent.getAction()
            if (action == null) {
                action = ""
            }
            if (action.equals("com.linkpoint.ACTION_PLAY_MEDIA")) {
                val stringExtra: String = intent.getStringExtra(MEDIA_URL_KEY)
                Debug.Log("StreamingMediaService: service is started, playing " + stringExtra)
                this.lastURL = stringExtra
                this.lastLocationName = intent.getStringExtra(LOCATION_NAME_KEY)
                this.lastLocationDesc = intent.getStringExtra(LOCATION_DESC_KEY)
                this.lastParcelData = intent.hasExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) ? (ParcelData) intent.getSerializableExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) : null
                this.lastActiveAgentUUID = ActivityUtils.getActiveAgentID(intent)
                val z: Boolean = this.audioManagerWrapper == null || this.audioManagerWrapper.requestAudioFocus()
                if (z) {
                    showNotification()
                    safeRegisterReceiver()
                    isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.TRUE)
                    this.mediaWrapper.play(stringExtra)
                    return
                }
                return
            }
            isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE)
            this.mediaWrapper.stop()
            if (this.audioManagerWrapper != null) {
                this.audioManagerWrapper.abandonAudioFocus()
            }
            safeUnregisterReceiver()
            stopForeground(true)
            this.notify = null
            stopSelf()
        }
    }

     private fun safeRegisterReceiver() {
        try {
            registerReceiver(this.noisyReceiver, IntentFilter("android.media.AUDIO_BECOMING_NOISY"))
        } catch (Exception e) {
            Debug.Log("StreamingMediaService: Failed to register noisy receiver")
        }
    }

     private fun safeUnregisterReceiver() {
        try {
            unregisterReceiver(this.noisyReceiver)
        } catch (Exception e) {
            Debug.Log("StreamingMediaService: Failed to un register noisy receiver")
        }
    }

     private fun showNotification() {
        val service: PendingIntent = PendingIntent.getService(this, 0, Intent(this, StreamingMediaService.class), 1073741824)
        val intent: Intent = Intent(this, StreamingMediaActivity.class)
        ActivityUtils.setActiveAgentID(intent, this.lastActiveAgentUUID)
        intent.putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, this.lastParcelData)
        val builder: Builder = Builder(this)
        builder.setSmallIcon(R.drawable.ic_playing_media).setContentTitle("Playing media").setContentText(this.lastLocationName).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(this, 0, intent, SLMoveEvents.AGENT_CONTROL_AWAY)).addAction(R.drawable.icon_material_stop, "Stop", service).setDeleteIntent(service).setOnlyAlertOnce(true)
        startForeground(R.id.media_notify_id, builder.build())
    }

    @JvmStatic
     fun startStreamingMediaService(context: Context, userManager: UserManager) {
        if (userManager != null) {
            val currentLocationInfoSnapshot: CurrentLocationInfo = userManager.getCurrentLocationInfoSnapshot()
            if (currentLocationInfoSnapshot != null) {
                val parcelData: Object = currentLocationInfoSnapshot.parcelData()
                if (parcelData != null) {
                    val mediaURL: String = parcelData.getMediaURL()
                    if (!Strings.isNullOrEmpty(parcelData.getMediaURL())) {
                        val intent: Intent = Intent(context, StreamingMediaService.class)
                        intent.setAction("com.linkpoint.ACTION_PLAY_MEDIA")
                        ActivityUtils.setActiveAgentID(intent, userManager.getUserID())
                        intent.putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, parcelData)
                        intent.putExtra(MEDIA_URL_KEY, mediaURL)
                        intent.putExtra(LOCATION_NAME_KEY, parcelData.getName())
                        context.startService(intent)
                    }
                }
            }
        }
    }

     public override fun onBind(intent: Intent): IBinder {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        this.audioManagerWrapper = AudioManagerWrapper(this)
        this.audioManagerWrapper.setHandler(this.mHandler, 100)
    }

    override fun onDestroy() {
        this.mediaWrapper.release()
        if (this.audioManagerWrapper != null) {
            this.audioManagerWrapper.abandonAudioFocus()
        }
        safeUnregisterReceiver()
        stopForeground(true)
        this.notify = null
        isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE)
    }

     public override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        handleStartService(intent)
        return 2
    }
}
