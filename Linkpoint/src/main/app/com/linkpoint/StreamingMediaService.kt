package com.linkpoint

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.core.app.NotificationCompat
import com.google.common.base.Strings
import com.linkpoint.media.AudioIntentReceiver
import com.linkpoint.media.AudioManagerWrapper
import com.linkpoint.media.MediaPlayerWrapper
import com.linkpoint.react.SubscriptionSingleDataPool
import com.linkpoint.react.SubscriptionSingleKey
import com.linkpoint.slproto.avatar.SLMoveEvents
import com.linkpoint.slproto.users.ParcelData
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.profiles.ParcelPropertiesFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.media.StreamingMediaActivity
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * Service for streaming media playback
 * Manages audio focus and notification
 */
class StreamingMediaService : Service() {
    
    val LOCATION_DESC_KEY = "location_desc"
    val LOCATION_NAME_KEY = "location_name"
    val MEDIA_URL_KEY = "media_url"
    private val MSG_ON_AUDIO_FOCUS_CHANGE = 100
    
    val isPlayingMedia = SubscriptionSingleDataPool<Boolean>()
    
    private var audioManagerWrapper: AudioManagerWrapper? = null
    private var lastActiveAgentUUID: UUID? = null
    private var lastLocationDesc = ""
    private var lastLocationName = ""
    private var lastParcelData: ParcelData? = null
    private var lastURL = ""
    private val mHandler = AudioFocusChangeHandler(this)
    private val mediaWrapper = MediaPlayerWrapper()
    private val noisyReceiver = AudioIntentReceiver()
    private var notify: Notification? = null

    /**
     * Handler for audio focus changes
     */
    private class AudioFocusChangeHandler(
        service: StreamingMediaService
    ) : Handler(Looper.getMainLooper()) {
        
        private val streamingMediaService = WeakReference(service)

        override fun handleMessage(msg: Message) {
            if (msg.what == 100) {
                streamingMediaService.get()?.handleAudioFocusChange(msg.arg1)
            }
        }
    }

    /**
     * Handle audio focus changes
     */
    private fun handleAudioFocusChange(focusChange: Int) {
        Debug.Log("StreamingMediaService: focusChange = $focusChange")
        
        if (focusChange == -1) { // AUDIOFOCUS_LOSS
            isPlayingMedia.setData(SubscriptionSingleKey.Value, false)
            mediaWrapper.stop()
            audioManagerWrapper?.abandonAudioFocus()
            safeUnregisterReceiver()
            stopForeground(true)
            notify = null
            stopSelf()
        }
    }

    /**
     * Handle service start
     */
    private fun handleStartService(intent: Intent?) {
        intent ?: return
        
        val action = intent.action ?: ""
        
        if (action == "com.linkpoint.ACTION_PLAY_MEDIA") {
            val mediaUrl = intent.getStringExtra(MEDIA_URL_KEY)
            Debug.Log("StreamingMediaService: service is started, playing $mediaUrl")
            
            lastURL = mediaUrl ?: ""
            lastLocationName = intent.getStringExtra(LOCATION_NAME_KEY) ?: ""
            lastLocationDesc = intent.getStringExtra(LOCATION_DESC_KEY) ?: ""
            lastParcelData = if (intent.hasExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY)) {
                intent.getSerializableExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) as? ParcelData
            } else {
                null
            }
            lastActiveAgentUUID = ActivityUtils.getActiveAgentID(intent)
            
            val hasAudioFocus = audioManagerWrapper?.requestAudioFocus() ?: false
            
            if (hasAudioFocus) {
                showNotification()
                safeRegisterReceiver()
                isPlayingMedia.setData(SubscriptionSingleKey.Value, true)
                mediaWrapper.play(mediaUrl)
            }
        } else {
            // Stop media
            isPlayingMedia.setData(SubscriptionSingleKey.Value, false)
            mediaWrapper.stop()
            audioManagerWrapper?.abandonAudioFocus()
            safeUnregisterReceiver()
            stopForeground(true)
            notify = null
            stopSelf()
        }
    }

    /**
     * Register broadcast receiver for audio becoming noisy
     */
    private fun safeRegisterReceiver() {
        try {
            registerReceiver(noisyReceiver, IntentFilter("android.media.AUDIO_BECOMING_NOISY"))
        } catch (e: Exception) {
            Debug.Log("StreamingMediaService: Failed to register noisy receiver")
        }
    }

    /**
     * Unregister broadcast receiver
     */
    private fun safeUnregisterReceiver() {
        try {
            unregisterReceiver(noisyReceiver)
        } catch (e: Exception) {
            Debug.Log("StreamingMediaService: Failed to unregister noisy receiver")
        }
    }

    /**
     * Show media playback notification
     */
    private fun showNotification() {
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, StreamingMediaService::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val activityIntent = Intent(this, StreamingMediaActivity::class.java).apply {
            ActivityUtils.setActiveAgentID(this, lastActiveAgentUUID)
            putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, lastParcelData)
        }
        
        val notification = NotificationCompat.Builder(this, "media_playback")
            .setSmallIcon(R.drawable.ic_playing_media)
            .setContentTitle("Playing media")
            .setContentText(lastLocationName)
            .setDefaults(0)
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(R.drawable.icon_material_stop, "Stop", stopIntent)
            .setDeleteIntent(stopIntent)
            .setOnlyAlertOnce(true)
            .build()
        
        startForeground(R.id.media_notify_id, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        audioManagerWrapper = AudioManagerWrapper(this).apply {
            setHandler(mHandler, MSG_ON_AUDIO_FOCUS_CHANGE)
        }
    }

    override fun onDestroy() {
        mediaWrapper.release()
        audioManagerWrapper?.abandonAudioFocus()
        safeUnregisterReceiver()
        stopForeground(true)
        notify = null
        isPlayingMedia.setData(SubscriptionSingleKey.Value, false)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleStartService(intent)
        return START_NOT_STICKY
    }
    
    companion object {
        /**
         * Start streaming media service for a parcel
         */
        fun startStreamingMediaService(context: Context, userManager: UserManager?) {
            userManager ?: return
            
            val locationInfo = userManager.getCurrentLocationInfoSnapshot() ?: return
            val parcelData = locationInfo.parcelData() ?: return
            val mediaURL = parcelData.getMediaURL()
            
            if (!Strings.isNullOrEmpty(mediaURL)) {
                val intent = Intent(context, StreamingMediaService::class.java).apply {
                    action = "com.linkpoint.ACTION_PLAY_MEDIA"
                    ActivityUtils.setActiveAgentID(this, userManager.getUserID())
                    putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, parcelData)
                    putExtra("media_url", mediaURL)
                    putExtra("location_name", parcelData.getName())
                }
                context.startService(intent)
            }
        }
    }
}
