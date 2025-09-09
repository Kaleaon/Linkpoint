package com.lumiyaviewer.lumiya;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat.Builder;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.media.AudioIntentReceiver;
import com.lumiyaviewer.lumiya.media.AudioManagerWrapper;
import com.lumiyaviewer.lumiya.media.MediaPlayerWrapper;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMoveEvents;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.media.StreamingMediaActivity;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class StreamingMediaService extends Service {
    public static final String LOCATION_DESC_KEY = "location_desc";
    public static final String LOCATION_NAME_KEY = "location_name";
    public static final String MEDIA_URL_KEY = "media_url";
    private static final int MSG_ON_AUDIO_FOCUS_CHANGE = 100;
    public static final SubscriptionSingleDataPool<Boolean> isPlayingMedia = new SubscriptionSingleDataPool();
    private AudioManagerWrapper audioManagerWrapper = null;
    private UUID lastActiveAgentUUID = null;
    private String lastLocationDesc = "";
    private String lastLocationName = "";
    private ParcelData lastParcelData = null;
    private String lastURL = "";
    private final AudioFocusChangeHandler mHandler = new AudioFocusChangeHandler(this, null);
    private MediaPlayerWrapper mediaWrapper = new MediaPlayerWrapper();
    private AudioIntentReceiver noisyReceiver = new AudioIntentReceiver();
    private Notification notify = null;

    private static class AudioFocusChangeHandler extends Handler {
        private final WeakReference<StreamingMediaService> streamingMediaService;

        private AudioFocusChangeHandler(StreamingMediaService streamingMediaService) {
            this.streamingMediaService = new WeakReference(streamingMediaService);
        }

        /* synthetic */ AudioFocusChangeHandler(StreamingMediaService streamingMediaService, AudioFocusChangeHandler audioFocusChangeHandler) {
            this(streamingMediaService);
        }

        public void handleMessage(Message message) {
            if (message.what == 100) {
                StreamingMediaService streamingMediaService = (StreamingMediaService) this.streamingMediaService.get();
                if (streamingMediaService != null) {
                    streamingMediaService.handleAudioFocusChange(message.arg1);
                }
            }
        }
    }

    private void handleAudioFocusChange(int i) {
        Debug.Log("StreamingMediaService: focusChange = " + i);
        if (i == -1) {
            isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
            this.mediaWrapper.stop();
            if (this.audioManagerWrapper != null) {
                this.audioManagerWrapper.abandonAudioFocus();
            }
            safeUnregisterReceiver();
            stopForeground(true);
            this.notify = null;
            stopSelf();
        }
    }

    private void handleStartService(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                action = "";
            }
            if (action.equals("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA")) {
                String stringExtra = intent.getStringExtra(MEDIA_URL_KEY);
                Debug.Log("StreamingMediaService: service is started, playing " + stringExtra);
                this.lastURL = stringExtra;
                this.lastLocationName = intent.getStringExtra(LOCATION_NAME_KEY);
                this.lastLocationDesc = intent.getStringExtra(LOCATION_DESC_KEY);
                this.lastParcelData = intent.hasExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) ? (ParcelData) intent.getSerializableExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY) : null;
                this.lastActiveAgentUUID = ActivityUtils.getActiveAgentID(intent);
                boolean z = this.audioManagerWrapper == null || this.audioManagerWrapper.requestAudioFocus();
                if (z) {
                    showNotification();
                    safeRegisterReceiver();
                    isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.TRUE);
                    this.mediaWrapper.play(stringExtra);
                    return;
                }
                return;
            }
            isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
            this.mediaWrapper.stop();
            if (this.audioManagerWrapper != null) {
                this.audioManagerWrapper.abandonAudioFocus();
            }
            safeUnregisterReceiver();
            stopForeground(true);
            this.notify = null;
            stopSelf();
        }
    }

    private void safeRegisterReceiver() {
        try {
            registerReceiver(this.noisyReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        } catch (Exception e) {
            Debug.Log("StreamingMediaService: Failed to register noisy receiver");
        }
    }

    private void safeUnregisterReceiver() {
        try {
            unregisterReceiver(this.noisyReceiver);
        } catch (Exception e) {
            Debug.Log("StreamingMediaService: Failed to un register noisy receiver");
        }
    }

    private void showNotification() {
        PendingIntent service = PendingIntent.getService(this, 0, new Intent(this, StreamingMediaService.class), 1073741824);
        Intent intent = new Intent(this, StreamingMediaActivity.class);
        ActivityUtils.setActiveAgentID(intent, this.lastActiveAgentUUID);
        intent.putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, this.lastParcelData);
        Builder builder = new Builder(this);
        builder.setSmallIcon(R.drawable.ic_playing_media).setContentTitle("Playing media").setContentText(this.lastLocationName).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(this, 0, intent, SLMoveEvents.AGENT_CONTROL_AWAY)).addAction(R.drawable.icon_material_stop, "Stop", service).setDeleteIntent(service).setOnlyAlertOnce(true);
        startForeground(R.id.media_notify_id, builder.build());
    }

    public static void startStreamingMediaService(Context context, UserManager userManager) {
        if (userManager != null) {
            CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
            if (currentLocationInfoSnapshot != null) {
                Object parcelData = currentLocationInfoSnapshot.parcelData();
                if (parcelData != null) {
                    String mediaURL = parcelData.getMediaURL();
                    if (!Strings.isNullOrEmpty(parcelData.getMediaURL())) {
                        Intent intent = new Intent(context, StreamingMediaService.class);
                        intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
                        ActivityUtils.setActiveAgentID(intent, userManager.getUserID());
                        intent.putExtra(ParcelPropertiesFragment.PARCEL_DATA_KEY, parcelData);
                        intent.putExtra(MEDIA_URL_KEY, mediaURL);
                        intent.putExtra(LOCATION_NAME_KEY, parcelData.getName());
                        context.startService(intent);
                    }
                }
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.audioManagerWrapper = new AudioManagerWrapper(this);
        this.audioManagerWrapper.setHandler(this.mHandler, 100);
    }

    public void onDestroy() {
        this.mediaWrapper.release();
        if (this.audioManagerWrapper != null) {
            this.audioManagerWrapper.abandonAudioFocus();
        }
        safeUnregisterReceiver();
        stopForeground(true);
        this.notify = null;
        isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        handleStartService(intent);
        return 2;
    }
}
