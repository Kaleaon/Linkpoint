// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya;

import android.os.Message;
import java.lang.ref.WeakReference;
import android.os.Handler;
import android.os.IBinder;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.NotificationCompat;
import java.io.Serializable;
import com.lumiyaviewer.lumiya.ui.media.StreamingMediaActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.content.Intent;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import android.app.Notification;
import com.lumiyaviewer.lumiya.media.AudioIntentReceiver;
import com.lumiyaviewer.lumiya.media.MediaPlayerWrapper;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import java.util.UUID;
import com.lumiyaviewer.lumiya.media.AudioManagerWrapper;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import android.app.Service;

public class StreamingMediaService extends Service
{
    public static final String LOCATION_DESC_KEY = "location_desc";
    public static final String LOCATION_NAME_KEY = "location_name";
    public static final String MEDIA_URL_KEY = "media_url";
    private static final int MSG_ON_AUDIO_FOCUS_CHANGE = 100;
    public static final SubscriptionSingleDataPool<Boolean> isPlayingMedia;
    private AudioManagerWrapper audioManagerWrapper;
    private UUID lastActiveAgentUUID;
    private String lastLocationDesc;
    private String lastLocationName;
    private ParcelData lastParcelData;
    private String lastURL;
    private final AudioFocusChangeHandler mHandler;
    private MediaPlayerWrapper mediaWrapper;
    private AudioIntentReceiver noisyReceiver;
    private Notification notify;
    
    static {
        isPlayingMedia = new SubscriptionSingleDataPool<Boolean>();
    }
    
    public StreamingMediaService() {
        this.noisyReceiver = new AudioIntentReceiver();
        this.notify = null;
        this.mediaWrapper = new MediaPlayerWrapper();
        this.audioManagerWrapper = null;
        this.lastURL = "";
        this.lastLocationName = "";
        this.lastLocationDesc = "";
        this.lastActiveAgentUUID = null;
        this.lastParcelData = null;
        this.mHandler = new AudioFocusChangeHandler(this, null);
    }
    
    private void handleAudioFocusChange(final int i) {
        Debug.Log("StreamingMediaService: focusChange = " + i);
        if (i == -1) {
            StreamingMediaService.isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
            this.mediaWrapper.stop();
            if (this.audioManagerWrapper != null) {
                this.audioManagerWrapper.abandonAudioFocus();
            }
            this.safeUnregisterReceiver();
            this.stopForeground(true);
            this.notify = null;
            this.stopSelf();
        }
    }
    
    private void handleStartService(final Intent intent) {
        if (intent != null) {
            String action;
            if ((action = intent.getAction()) == null) {
                action = "";
            }
            if (action.equals("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA")) {
                final String stringExtra = intent.getStringExtra("media_url");
                Debug.Log("StreamingMediaService: service is started, playing " + stringExtra);
                this.lastURL = stringExtra;
                this.lastLocationName = intent.getStringExtra("location_name");
                this.lastLocationDesc = intent.getStringExtra("location_desc");
                ParcelData lastParcelData;
                if (intent.hasExtra("parcelData")) {
                    lastParcelData = (ParcelData)intent.getSerializableExtra("parcelData");
                }
                else {
                    lastParcelData = null;
                }
                this.lastParcelData = lastParcelData;
                this.lastActiveAgentUUID = ActivityUtils.getActiveAgentID(intent);
                int n;
                if (this.audioManagerWrapper != null && !this.audioManagerWrapper.requestAudioFocus()) {
                    n = 0;
                }
                else {
                    n = 1;
                }
                if (n != 0) {
                    this.showNotification();
                    this.safeRegisterReceiver();
                    StreamingMediaService.isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.TRUE);
                    this.mediaWrapper.play(stringExtra);
                }
            }
            else {
                StreamingMediaService.isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
                this.mediaWrapper.stop();
                if (this.audioManagerWrapper != null) {
                    this.audioManagerWrapper.abandonAudioFocus();
                }
                this.safeUnregisterReceiver();
                this.stopForeground(true);
                this.notify = null;
                this.stopSelf();
            }
        }
    }
    
    private void safeRegisterReceiver() {
        try {
            this.registerReceiver((BroadcastReceiver)this.noisyReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
        }
        catch (final Exception ex) {
            Debug.Log("StreamingMediaService: Failed to register noisy receiver");
        }
    }
    
    private void safeUnregisterReceiver() {
        try {
            this.unregisterReceiver((BroadcastReceiver)this.noisyReceiver);
        }
        catch (final Exception ex) {
            Debug.Log("StreamingMediaService: Failed to un register noisy receiver");
        }
    }
    
    private void showNotification() {
        final PendingIntent service = PendingIntent.getService((Context)this, 0, new Intent((Context)this, (Class)StreamingMediaService.class), 1073741824);
        final Intent intent = new Intent((Context)this, (Class)StreamingMediaActivity.class);
        ActivityUtils.setActiveAgentID(intent, this.lastActiveAgentUUID);
        intent.putExtra("parcelData", (Serializable)this.lastParcelData);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder((Context)this);
        builder.setSmallIcon(2130837628).setContentTitle("Playing media").setContentText(this.lastLocationName).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity((Context)this, 0, intent, 134217728)).addAction(2130837686, "Stop", service).setDeleteIntent(service).setOnlyAlertOnce(true);
        this.startForeground(2131755031, builder.build());
    }
    
    public static void startStreamingMediaService(final Context context, final UserManager userManager) {
        if (userManager != null) {
            final CurrentLocationInfo currentLocationInfoSnapshot = userManager.getCurrentLocationInfoSnapshot();
            if (currentLocationInfoSnapshot != null) {
                final ParcelData parcelData = currentLocationInfoSnapshot.parcelData();
                if (parcelData != null) {
                    final String mediaURL = parcelData.getMediaURL();
                    if (!Strings.isNullOrEmpty(parcelData.getMediaURL())) {
                        final Intent intent = new Intent(context, (Class)StreamingMediaService.class);
                        intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
                        ActivityUtils.setActiveAgentID(intent, userManager.getUserID());
                        intent.putExtra("parcelData", (Serializable)parcelData);
                        intent.putExtra("media_url", mediaURL);
                        intent.putExtra("location_name", parcelData.getName());
                        context.startService(intent);
                    }
                }
            }
        }
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
        (this.audioManagerWrapper = new AudioManagerWrapper((Context)this)).setHandler(this.mHandler, 100);
    }
    
    public void onDestroy() {
        this.mediaWrapper.release();
        if (this.audioManagerWrapper != null) {
            this.audioManagerWrapper.abandonAudioFocus();
        }
        this.safeUnregisterReceiver();
        this.stopForeground(true);
        this.notify = null;
        StreamingMediaService.isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
    }
    
    public int onStartCommand(final Intent intent, final int n, final int n2) {
        this.handleStartService(intent);
        return 2;
    }
    
    private static class AudioFocusChangeHandler extends Handler
    {
        private final WeakReference<StreamingMediaService> streamingMediaService;
        
        private AudioFocusChangeHandler(final StreamingMediaService referent) {
            this.streamingMediaService = new WeakReference<StreamingMediaService>(referent);
        }
        
        public void handleMessage(final Message message) {
            if (message.what == 100) {
                final StreamingMediaService streamingMediaService = this.streamingMediaService.get();
                if (streamingMediaService != null) {
                    streamingMediaService.handleAudioFocusChange(message.arg1);
                }
            }
        }
    }
}
