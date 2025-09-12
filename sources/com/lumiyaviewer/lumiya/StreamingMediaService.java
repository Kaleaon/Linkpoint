// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.media.AudioIntentReceiver;
import com.lumiyaviewer.lumiya.media.AudioManagerWrapper;
import com.lumiyaviewer.lumiya.media.MediaPlayerWrapper;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.media.StreamingMediaActivity;
import java.lang.ref.WeakReference;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            Debug

public class StreamingMediaService extends Service
{
    private static class AudioFocusChangeHandler extends Handler
    {

        private final WeakReference streamingMediaService;

        public void handleMessage(Message message)
        {
            if (message.what == 100)
            {
                StreamingMediaService streamingmediaservice = (StreamingMediaService)streamingMediaService.get();
                if (streamingmediaservice != null)
                {
                    StreamingMediaService._2D_wrap0(streamingmediaservice, message.arg1);
                }
            }
        }

        private AudioFocusChangeHandler(StreamingMediaService streamingmediaservice)
        {
            streamingMediaService = new WeakReference(streamingmediaservice);
        }

        AudioFocusChangeHandler(StreamingMediaService streamingmediaservice, AudioFocusChangeHandler audiofocuschangehandler)
        {
            this(streamingmediaservice);
        }
    }


    public static final String LOCATION_DESC_KEY = "location_desc";
    public static final String LOCATION_NAME_KEY = "location_name";
    public static final String MEDIA_URL_KEY = "media_url";
    private static final int MSG_ON_AUDIO_FOCUS_CHANGE = 100;
    public static final SubscriptionSingleDataPool isPlayingMedia = new SubscriptionSingleDataPool();
    private AudioManagerWrapper audioManagerWrapper;
    private UUID lastActiveAgentUUID;
    private String lastLocationDesc;
    private String lastLocationName;
    private ParcelData lastParcelData;
    private String lastURL;
    private final AudioFocusChangeHandler mHandler = new AudioFocusChangeHandler(this, null);
    private MediaPlayerWrapper mediaWrapper;
    private AudioIntentReceiver noisyReceiver;
    private Notification notify;

    static void _2D_wrap0(StreamingMediaService streamingmediaservice, int i)
    {
        streamingmediaservice.handleAudioFocusChange(i);
    }

    public StreamingMediaService()
    {
        noisyReceiver = new AudioIntentReceiver();
        notify = null;
        mediaWrapper = new MediaPlayerWrapper();
        audioManagerWrapper = null;
        lastURL = "";
        lastLocationName = "";
        lastLocationDesc = "";
        lastActiveAgentUUID = null;
        lastParcelData = null;
    }

    private void handleAudioFocusChange(int i)
    {
        Debug.Log((new StringBuilder()).append("StreamingMediaService: focusChange = ").append(i).toString());
        if (i == -1)
        {
            isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
            mediaWrapper.stop();
            if (audioManagerWrapper != null)
            {
                audioManagerWrapper.abandonAudioFocus();
            }
            safeUnregisterReceiver();
            stopForeground(true);
            notify = null;
            stopSelf();
        }
    }

    private void handleStartService(Intent intent)
    {
label0:
        {
            if (intent != null)
            {
                String s = intent.getAction();
                Object obj = s;
                if (s == null)
                {
                    obj = "";
                }
                if (!((String) (obj)).equals("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA"))
                {
                    break label0;
                }
                s = intent.getStringExtra("media_url");
                Debug.Log((new StringBuilder()).append("StreamingMediaService: service is started, playing ").append(s).toString());
                lastURL = s;
                lastLocationName = intent.getStringExtra("location_name");
                lastLocationDesc = intent.getStringExtra("location_desc");
                boolean flag;
                if (intent.hasExtra("parcelData"))
                {
                    obj = (ParcelData)intent.getSerializableExtra("parcelData");
                } else
                {
                    obj = null;
                }
                lastParcelData = ((ParcelData) (obj));
                lastActiveAgentUUID = ActivityUtils.getActiveAgentID(intent);
                if (audioManagerWrapper != null && !audioManagerWrapper.requestAudioFocus())
                {
                    flag = false;
                } else
                {
                    flag = true;
                }
                if (flag)
                {
                    showNotification();
                    safeRegisterReceiver();
                    isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.TRUE);
                    mediaWrapper.play(s);
                }
            }
            return;
        }
        isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
        mediaWrapper.stop();
        if (audioManagerWrapper != null)
        {
            audioManagerWrapper.abandonAudioFocus();
        }
        safeUnregisterReceiver();
        stopForeground(true);
        notify = null;
        stopSelf();
    }

    private void safeRegisterReceiver()
    {
        try
        {
            registerReceiver(noisyReceiver, new IntentFilter("android.media.AUDIO_BECOMING_NOISY"));
            return;
        }
        catch (Exception exception)
        {
            Debug.Log("StreamingMediaService: Failed to register noisy receiver");
        }
    }

    private void safeUnregisterReceiver()
    {
        try
        {
            unregisterReceiver(noisyReceiver);
            return;
        }
        catch (Exception exception)
        {
            Debug.Log("StreamingMediaService: Failed to un register noisy receiver");
        }
    }

    private void showNotification()
    {
        PendingIntent pendingintent = PendingIntent.getService(this, 0, new Intent(this, com/lumiyaviewer/lumiya/StreamingMediaService), 0x40000000);
        Intent intent = new Intent(this, com/lumiyaviewer/lumiya/ui/media/StreamingMediaActivity);
        ActivityUtils.setActiveAgentID(intent, lastActiveAgentUUID);
        intent.putExtra("parcelData", lastParcelData);
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this);
        builder.setSmallIcon(0x7f02007c).setContentTitle("Playing media").setContentText(lastLocationName).setDefaults(0).setOngoing(true).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0x8000000)).addAction(0x7f0200b6, "Stop", pendingintent).setDeleteIntent(pendingintent).setOnlyAlertOnce(true);
        startForeground(0x7f100017, builder.build());
    }

    public static void startStreamingMediaService(Context context, UserManager usermanager)
    {
        if (usermanager != null)
        {
            Object obj = usermanager.getCurrentLocationInfoSnapshot();
            if (obj != null)
            {
                obj = ((CurrentLocationInfo) (obj)).parcelData();
                if (obj != null)
                {
                    String s = ((ParcelData) (obj)).getMediaURL();
                    if (!Strings.isNullOrEmpty(((ParcelData) (obj)).getMediaURL()))
                    {
                        Intent intent = new Intent(context, com/lumiyaviewer/lumiya/StreamingMediaService);
                        intent.setAction("com.lumiyaviewer.lumiya.ACTION_PLAY_MEDIA");
                        ActivityUtils.setActiveAgentID(intent, usermanager.getUserID());
                        intent.putExtra("parcelData", ((java.io.Serializable) (obj)));
                        intent.putExtra("media_url", s);
                        intent.putExtra("location_name", ((ParcelData) (obj)).getName());
                        context.startService(intent);
                    }
                }
            }
        }
    }

    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        audioManagerWrapper = new AudioManagerWrapper(this);
        audioManagerWrapper.setHandler(mHandler, 100);
    }

    public void onDestroy()
    {
        mediaWrapper.release();
        if (audioManagerWrapper != null)
        {
            audioManagerWrapper.abandonAudioFocus();
        }
        safeUnregisterReceiver();
        stopForeground(true);
        notify = null;
        isPlayingMedia.setData(SubscriptionSingleKey.Value, Boolean.FALSE);
    }

    public int onStartCommand(Intent intent, int i, int j)
    {
        handleStartService(intent);
        return 2;
    }

}
