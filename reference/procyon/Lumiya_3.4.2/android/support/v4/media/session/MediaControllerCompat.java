// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media.session;

import android.support.v4.media.RatingCompat;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.BundleCompat;
import android.os.Parcelable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import android.support.annotation.RequiresApi;
import java.lang.ref.WeakReference;
import android.os.Looper;
import android.os.Message;
import android.os.IBinder$DeathRecipient;
import android.text.TextUtils;
import android.os.ResultReceiver;
import android.os.Handler;
import android.app.PendingIntent;
import java.util.List;
import android.support.v4.media.MediaMetadataCompat;
import android.view.KeyEvent;
import android.support.v4.media.MediaDescriptionCompat;
import android.util.Log;
import android.support.v4.app.SupportActivity;
import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Build$VERSION;
import android.support.annotation.NonNull;
import android.content.Context;
import java.util.HashSet;

public final class MediaControllerCompat
{
    static final String COMMAND_ADD_QUEUE_ITEM = "android.support.v4.media.session.command.ADD_QUEUE_ITEM";
    static final String COMMAND_ADD_QUEUE_ITEM_AT = "android.support.v4.media.session.command.ADD_QUEUE_ITEM_AT";
    static final String COMMAND_ARGUMENT_INDEX = "android.support.v4.media.session.command.ARGUMENT_INDEX";
    static final String COMMAND_ARGUMENT_MEDIA_DESCRIPTION = "android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION";
    static final String COMMAND_GET_EXTRA_BINDER = "android.support.v4.media.session.command.GET_EXTRA_BINDER";
    static final String COMMAND_REMOVE_QUEUE_ITEM = "android.support.v4.media.session.command.REMOVE_QUEUE_ITEM";
    static final String COMMAND_REMOVE_QUEUE_ITEM_AT = "android.support.v4.media.session.command.REMOVE_QUEUE_ITEM_AT";
    static final String TAG = "MediaControllerCompat";
    private final MediaControllerImpl mImpl;
    private final HashSet<Callback> mRegisteredCallbacks;
    private final MediaSessionCompat.Token mToken;
    
    public MediaControllerCompat(final Context context, @NonNull final MediaSessionCompat.Token mToken) throws RemoteException {
        this.mRegisteredCallbacks = new HashSet<Callback>();
        if (mToken != null) {
            this.mToken = mToken;
            if (Build$VERSION.SDK_INT < 24) {
                if (Build$VERSION.SDK_INT < 23) {
                    if (Build$VERSION.SDK_INT < 21) {
                        this.mImpl = (MediaControllerImpl)new MediaControllerImplBase(this.mToken);
                    }
                    else {
                        this.mImpl = (MediaControllerImpl)new MediaControllerImplApi21(context, mToken);
                    }
                }
                else {
                    this.mImpl = (MediaControllerImpl)new MediaControllerImplApi23(context, mToken);
                }
            }
            else {
                this.mImpl = (MediaControllerImpl)new MediaControllerImplApi24(context, mToken);
            }
            return;
        }
        throw new IllegalArgumentException("sessionToken must not be null");
    }
    
    public MediaControllerCompat(final Context context, @NonNull final MediaSessionCompat mediaSessionCompat) {
        this.mRegisteredCallbacks = new HashSet<Callback>();
        if (mediaSessionCompat != null) {
            this.mToken = mediaSessionCompat.getSessionToken();
            if (Build$VERSION.SDK_INT < 24) {
                if (Build$VERSION.SDK_INT < 23) {
                    if (Build$VERSION.SDK_INT < 21) {
                        this.mImpl = (MediaControllerImpl)new MediaControllerImplBase(this.mToken);
                    }
                    else {
                        this.mImpl = (MediaControllerImpl)new MediaControllerImplApi21(context, mediaSessionCompat);
                    }
                }
                else {
                    this.mImpl = (MediaControllerImpl)new MediaControllerImplApi23(context, mediaSessionCompat);
                }
            }
            else {
                this.mImpl = (MediaControllerImpl)new MediaControllerImplApi24(context, mediaSessionCompat);
            }
            return;
        }
        throw new IllegalArgumentException("session must not be null");
    }
    
    public static MediaControllerCompat getMediaController(@NonNull final Activity activity) {
        if (!(activity instanceof SupportActivity)) {
            if (Build$VERSION.SDK_INT >= 21) {
                final Object mediaController = MediaControllerCompatApi21.getMediaController(activity);
                Label_0075: {
                    if (mediaController == null) {
                        break Label_0075;
                    }
                    final Object sessionToken = MediaControllerCompatApi21.getSessionToken(mediaController);
                    try {
                        return new MediaControllerCompat((Context)activity, MediaSessionCompat.Token.fromToken(sessionToken));
                        return null;
                    }
                    catch (final RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in getMediaController.", (Throwable)ex);
                    }
                }
            }
            return null;
        }
        final MediaControllerExtraData mediaControllerExtraData = ((SupportActivity)activity).getExtraData(MediaControllerExtraData.class);
        MediaControllerCompat mediaController2;
        if (mediaControllerExtraData == null) {
            mediaController2 = null;
        }
        else {
            mediaController2 = mediaControllerExtraData.getMediaController();
        }
        return mediaController2;
    }
    
    public static void setMediaController(@NonNull final Activity activity, final MediaControllerCompat mediaControllerCompat) {
        if (activity instanceof SupportActivity) {
            ((SupportActivity)activity).putExtraData((SupportActivity.ExtraData)new MediaControllerExtraData(mediaControllerCompat));
        }
        if (Build$VERSION.SDK_INT >= 21) {
            Object fromToken;
            if (mediaControllerCompat == null) {
                fromToken = null;
            }
            else {
                fromToken = MediaControllerCompatApi21.fromToken((Context)activity, mediaControllerCompat.getSessionToken().getToken());
            }
            MediaControllerCompatApi21.setMediaController(activity, fromToken);
        }
    }
    
    private static void validateCustomAction(final String str, final Bundle bundle) {
        if (str != null) {
            switch (str) {
                case "android.support.v4.media.session.action.FOLLOW":
                case "android.support.v4.media.session.action.UNFOLLOW": {
                    if (bundle != null && bundle.containsKey("android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE")) {
                        break;
                    }
                    throw new IllegalArgumentException("An extra field android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE is required for this action " + str + ".");
                }
            }
        }
    }
    
    public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
        this.mImpl.addQueueItem(mediaDescriptionCompat);
    }
    
    public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat, final int n) {
        this.mImpl.addQueueItem(mediaDescriptionCompat, n);
    }
    
    public void adjustVolume(final int n, final int n2) {
        this.mImpl.adjustVolume(n, n2);
    }
    
    public boolean dispatchMediaButtonEvent(final KeyEvent keyEvent) {
        if (keyEvent != null) {
            return this.mImpl.dispatchMediaButtonEvent(keyEvent);
        }
        throw new IllegalArgumentException("KeyEvent may not be null");
    }
    
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }
    
    public long getFlags() {
        return this.mImpl.getFlags();
    }
    
    public Object getMediaController() {
        return this.mImpl.getMediaController();
    }
    
    public MediaMetadataCompat getMetadata() {
        return this.mImpl.getMetadata();
    }
    
    public String getPackageName() {
        return this.mImpl.getPackageName();
    }
    
    public PlaybackInfo getPlaybackInfo() {
        return this.mImpl.getPlaybackInfo();
    }
    
    public PlaybackStateCompat getPlaybackState() {
        return this.mImpl.getPlaybackState();
    }
    
    public List<MediaSessionCompat.QueueItem> getQueue() {
        return this.mImpl.getQueue();
    }
    
    public CharSequence getQueueTitle() {
        return this.mImpl.getQueueTitle();
    }
    
    public int getRatingType() {
        return this.mImpl.getRatingType();
    }
    
    public int getRepeatMode() {
        return this.mImpl.getRepeatMode();
    }
    
    public PendingIntent getSessionActivity() {
        return this.mImpl.getSessionActivity();
    }
    
    public MediaSessionCompat.Token getSessionToken() {
        return this.mToken;
    }
    
    public int getShuffleMode() {
        return this.mImpl.getShuffleMode();
    }
    
    public TransportControls getTransportControls() {
        return this.mImpl.getTransportControls();
    }
    
    public boolean isCaptioningEnabled() {
        return this.mImpl.isCaptioningEnabled();
    }
    
    @Deprecated
    public boolean isShuffleModeEnabled() {
        return this.mImpl.isShuffleModeEnabled();
    }
    
    public void registerCallback(@NonNull final Callback callback) {
        this.registerCallback(callback, null);
    }
    
    public void registerCallback(@NonNull final Callback e, Handler handler) {
        if (e != null) {
            if (handler == null) {
                handler = new Handler();
            }
            e.setHandler(handler);
            this.mImpl.registerCallback(e, handler);
            this.mRegisteredCallbacks.add(e);
            return;
        }
        throw new IllegalArgumentException("callback must not be null");
    }
    
    public void removeQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
        this.mImpl.removeQueueItem(mediaDescriptionCompat);
    }
    
    @Deprecated
    public void removeQueueItemAt(final int n) {
        final List<MediaSessionCompat.QueueItem> queue = this.getQueue();
        if (queue != null && n >= 0 && n < queue.size()) {
            final MediaSessionCompat.QueueItem queueItem = queue.get(n);
            if (queueItem != null) {
                this.removeQueueItem(queueItem.getDescription());
            }
        }
    }
    
    public void sendCommand(@NonNull final String s, final Bundle bundle, final ResultReceiver resultReceiver) {
        if (!TextUtils.isEmpty((CharSequence)s)) {
            this.mImpl.sendCommand(s, bundle, resultReceiver);
            return;
        }
        throw new IllegalArgumentException("command must neither be null nor empty");
    }
    
    public void setVolumeTo(final int n, final int n2) {
        this.mImpl.setVolumeTo(n, n2);
    }
    
    public void unregisterCallback(@NonNull final Callback o) {
        Label_0029: {
            if (o == null) {
                break Label_0029;
            }
            try {
                this.mRegisteredCallbacks.remove(o);
                this.mImpl.unregisterCallback(o);
                return;
                throw new IllegalArgumentException("callback must not be null");
            }
            finally {
                o.setHandler(null);
            }
        }
    }
    
    public abstract static class Callback implements IBinder$DeathRecipient
    {
        private final Object mCallbackObj;
        MessageHandler mHandler;
        boolean mHasExtraCallback;
        
        public Callback() {
            if (Build$VERSION.SDK_INT < 21) {
                this.mCallbackObj = new StubCompat(this);
            }
            else {
                this.mCallbackObj = MediaControllerCompatApi21.createCallback((MediaControllerCompatApi21.Callback)new StubApi21(this));
            }
        }
        
        public void binderDied() {
            this.onSessionDestroyed();
        }
        
        public void onAudioInfoChanged(final PlaybackInfo playbackInfo) {
        }
        
        public void onCaptioningEnabledChanged(final boolean b) {
        }
        
        public void onExtrasChanged(final Bundle bundle) {
        }
        
        public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) {
        }
        
        public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) {
        }
        
        public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) {
        }
        
        public void onQueueTitleChanged(final CharSequence charSequence) {
        }
        
        public void onRepeatModeChanged(final int n) {
        }
        
        public void onSessionDestroyed() {
        }
        
        public void onSessionEvent(final String s, final Bundle bundle) {
        }
        
        public void onShuffleModeChanged(final int n) {
        }
        
        @Deprecated
        public void onShuffleModeChanged(final boolean b) {
        }
        
        void postToHandler(final int n, final Object o, final Bundle data) {
            if (this.mHandler != null) {
                final Message obtainMessage = this.mHandler.obtainMessage(n, o);
                obtainMessage.setData(data);
                obtainMessage.sendToTarget();
            }
        }
        
        void setHandler(final Handler handler) {
            if (handler != null) {
                this.mHandler = new MessageHandler(handler.getLooper());
                this.mHandler.mRegistered = true;
            }
            else if (this.mHandler != null) {
                this.mHandler.mRegistered = false;
                this.mHandler.removeCallbacksAndMessages((Object)null);
                this.mHandler = null;
            }
        }
        
        private class MessageHandler extends Handler
        {
            private static final int MSG_DESTROYED = 8;
            private static final int MSG_EVENT = 1;
            private static final int MSG_UPDATE_CAPTIONING_ENABLED = 11;
            private static final int MSG_UPDATE_EXTRAS = 7;
            private static final int MSG_UPDATE_METADATA = 3;
            private static final int MSG_UPDATE_PLAYBACK_STATE = 2;
            private static final int MSG_UPDATE_QUEUE = 5;
            private static final int MSG_UPDATE_QUEUE_TITLE = 6;
            private static final int MSG_UPDATE_REPEAT_MODE = 9;
            private static final int MSG_UPDATE_SHUFFLE_MODE = 12;
            private static final int MSG_UPDATE_SHUFFLE_MODE_DEPRECATED = 10;
            private static final int MSG_UPDATE_VOLUME = 4;
            boolean mRegistered;
            
            MessageHandler(final Looper looper) {
                super(looper);
                this.mRegistered = false;
            }
            
            public void handleMessage(final Message message) {
                if (this.mRegistered) {
                    switch (message.what) {
                        case 1: {
                            Callback.this.onSessionEvent((String)message.obj, message.getData());
                            break;
                        }
                        case 2: {
                            Callback.this.onPlaybackStateChanged((PlaybackStateCompat)message.obj);
                            break;
                        }
                        case 3: {
                            Callback.this.onMetadataChanged((MediaMetadataCompat)message.obj);
                            break;
                        }
                        case 5: {
                            Callback.this.onQueueChanged((List<MediaSessionCompat.QueueItem>)message.obj);
                            break;
                        }
                        case 6: {
                            Callback.this.onQueueTitleChanged((CharSequence)message.obj);
                            break;
                        }
                        case 11: {
                            Callback.this.onCaptioningEnabledChanged((boolean)message.obj);
                            break;
                        }
                        case 9: {
                            Callback.this.onRepeatModeChanged((int)message.obj);
                            break;
                        }
                        case 10: {
                            Callback.this.onShuffleModeChanged((boolean)message.obj);
                            break;
                        }
                        case 12: {
                            Callback.this.onShuffleModeChanged((int)message.obj);
                            break;
                        }
                        case 7: {
                            Callback.this.onExtrasChanged((Bundle)message.obj);
                            break;
                        }
                        case 4: {
                            Callback.this.onAudioInfoChanged((PlaybackInfo)message.obj);
                            break;
                        }
                        case 8: {
                            Callback.this.onSessionDestroyed();
                            break;
                        }
                    }
                }
            }
        }
        
        private static class StubApi21 implements MediaControllerCompatApi21.Callback
        {
            private final WeakReference<MediaControllerCompat.Callback> mCallback;
            
            StubApi21(final MediaControllerCompat.Callback referent) {
                this.mCallback = new WeakReference<MediaControllerCompat.Callback>(referent);
            }
            
            @Override
            public void onAudioInfoChanged(final int n, final int n2, final int n3, final int n4, final int n5) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onAudioInfoChanged(new MediaControllerCompat.PlaybackInfo(n, n2, n3, n4, n5));
                }
            }
            
            @Override
            public void onExtrasChanged(final Bundle bundle) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onExtrasChanged(bundle);
                }
            }
            
            @Override
            public void onMetadataChanged(final Object o) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onMetadataChanged(MediaMetadataCompat.fromMediaMetadata(o));
                }
            }
            
            @Override
            public void onPlaybackStateChanged(final Object o) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null && !callback.mHasExtraCallback) {
                    callback.onPlaybackStateChanged(PlaybackStateCompat.fromPlaybackState(o));
                }
            }
            
            @Override
            public void onQueueChanged(final List<?> list) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onQueueChanged(MediaSessionCompat.QueueItem.fromQueueItemList(list));
                }
            }
            
            @Override
            public void onQueueTitleChanged(final CharSequence charSequence) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onQueueTitleChanged(charSequence);
                }
            }
            
            @Override
            public void onSessionDestroyed() {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.onSessionDestroyed();
                }
            }
            
            @Override
            public void onSessionEvent(final String s, final Bundle bundle) {
                final MediaControllerCompat.Callback callback = this.mCallback.get();
                if (callback != null && (!callback.mHasExtraCallback || Build$VERSION.SDK_INT >= 23)) {
                    callback.onSessionEvent(s, bundle);
                }
            }
        }
        
        private static class StubCompat extends Stub
        {
            private final WeakReference<Callback> mCallback;
            
            StubCompat(final Callback referent) {
                this.mCallback = new WeakReference<Callback>(referent);
            }
            
            public void onCaptioningEnabledChanged(final boolean b) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(11, b, null);
                }
            }
            
            public void onEvent(final String s, final Bundle bundle) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(1, s, bundle);
                }
            }
            
            public void onExtrasChanged(final Bundle bundle) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(7, bundle, null);
                }
            }
            
            public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(3, mediaMetadataCompat, null);
                }
            }
            
            public void onPlaybackStateChanged(final PlaybackStateCompat playbackStateCompat) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(2, playbackStateCompat, null);
                }
            }
            
            public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(5, list, null);
                }
            }
            
            public void onQueueTitleChanged(final CharSequence charSequence) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(6, charSequence, null);
                }
            }
            
            public void onRepeatModeChanged(final int i) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(9, i, null);
                }
            }
            
            public void onSessionDestroyed() throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(8, null, null);
                }
            }
            
            public void onShuffleModeChanged(final int i) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(12, i, null);
                }
            }
            
            public void onShuffleModeChangedDeprecated(final boolean b) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    callback.postToHandler(10, b, null);
                }
            }
            
            public void onVolumeInfoChanged(final ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
                final Callback callback = this.mCallback.get();
                if (callback != null) {
                    Object o;
                    if (parcelableVolumeInfo == null) {
                        o = null;
                    }
                    else {
                        o = new PlaybackInfo(parcelableVolumeInfo.volumeType, parcelableVolumeInfo.audioStream, parcelableVolumeInfo.controlType, parcelableVolumeInfo.maxVolume, parcelableVolumeInfo.currentVolume);
                    }
                    callback.postToHandler(4, o, null);
                }
            }
        }
    }
    
    private static class MediaControllerExtraData extends ExtraData
    {
        private final MediaControllerCompat mMediaController;
        
        MediaControllerExtraData(final MediaControllerCompat mMediaController) {
            this.mMediaController = mMediaController;
        }
        
        MediaControllerCompat getMediaController() {
            return this.mMediaController;
        }
    }
    
    interface MediaControllerImpl
    {
        void addQueueItem(final MediaDescriptionCompat p0);
        
        void addQueueItem(final MediaDescriptionCompat p0, final int p1);
        
        void adjustVolume(final int p0, final int p1);
        
        boolean dispatchMediaButtonEvent(final KeyEvent p0);
        
        Bundle getExtras();
        
        long getFlags();
        
        Object getMediaController();
        
        MediaMetadataCompat getMetadata();
        
        String getPackageName();
        
        PlaybackInfo getPlaybackInfo();
        
        PlaybackStateCompat getPlaybackState();
        
        List<MediaSessionCompat.QueueItem> getQueue();
        
        CharSequence getQueueTitle();
        
        int getRatingType();
        
        int getRepeatMode();
        
        PendingIntent getSessionActivity();
        
        int getShuffleMode();
        
        TransportControls getTransportControls();
        
        boolean isCaptioningEnabled();
        
        boolean isShuffleModeEnabled();
        
        void registerCallback(final Callback p0, final Handler p1);
        
        void removeQueueItem(final MediaDescriptionCompat p0);
        
        void sendCommand(final String p0, final Bundle p1, final ResultReceiver p2);
        
        void setVolumeTo(final int p0, final int p1);
        
        void unregisterCallback(final Callback p0);
    }
    
    @RequiresApi(21)
    static class MediaControllerImplApi21 implements MediaControllerImpl
    {
        private HashMap<Callback, ExtraCallback> mCallbackMap;
        protected final Object mControllerObj;
        private IMediaSession mExtraBinder;
        private final List<Callback> mPendingCallbacks;
        
        public MediaControllerImplApi21(final Context context, final MediaSessionCompat.Token token) throws RemoteException {
            this.mPendingCallbacks = new ArrayList<Callback>();
            this.mCallbackMap = new HashMap<Callback, ExtraCallback>();
            this.mControllerObj = MediaControllerCompatApi21.fromToken(context, token.getToken());
            if (this.mControllerObj != null) {
                this.mExtraBinder = token.getExtraBinder();
                if (this.mExtraBinder == null) {
                    this.requestExtraBinder();
                }
                return;
            }
            throw new RemoteException();
        }
        
        public MediaControllerImplApi21(final Context context, final MediaSessionCompat mediaSessionCompat) {
            this.mPendingCallbacks = new ArrayList<Callback>();
            this.mCallbackMap = new HashMap<Callback, ExtraCallback>();
            this.mControllerObj = MediaControllerCompatApi21.fromToken(context, mediaSessionCompat.getSessionToken().getToken());
            this.mExtraBinder = mediaSessionCompat.getSessionToken().getExtraBinder();
            if (this.mExtraBinder == null) {
                this.requestExtraBinder();
            }
        }
        
        private void processPendingCallbacks() {
            if (this.mExtraBinder == null) {
                return;
            }
            synchronized (this.mPendingCallbacks) {
                for (final Callback key : this.mPendingCallbacks) {
                    final ExtraCallback value = new ExtraCallback(key);
                    this.mCallbackMap.put(key, value);
                    key.mHasExtraCallback = true;
                    try {
                        this.mExtraBinder.registerCallbackListener(value);
                    }
                    catch (final RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                    }
                }
                this.mPendingCallbacks.clear();
            }
        }
        
        private void requestExtraBinder() {
            this.sendCommand("android.support.v4.media.session.command.GET_EXTRA_BINDER", null, new ExtraBinderRequestResultReceiver(this, new Handler()));
        }
        
        @Override
        public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
            if ((this.getFlags() & 0x4L) == 0x0L) {
                throw new UnsupportedOperationException("This session doesn't support queue management operations");
            }
            final Bundle bundle = new Bundle();
            bundle.putParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION", (Parcelable)mediaDescriptionCompat);
            this.sendCommand("android.support.v4.media.session.command.ADD_QUEUE_ITEM", bundle, null);
        }
        
        @Override
        public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat, final int n) {
            if ((this.getFlags() & 0x4L) == 0x0L) {
                throw new UnsupportedOperationException("This session doesn't support queue management operations");
            }
            final Bundle bundle = new Bundle();
            bundle.putParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION", (Parcelable)mediaDescriptionCompat);
            bundle.putInt("android.support.v4.media.session.command.ARGUMENT_INDEX", n);
            this.sendCommand("android.support.v4.media.session.command.ADD_QUEUE_ITEM_AT", bundle, null);
        }
        
        @Override
        public void adjustVolume(final int n, final int n2) {
            MediaControllerCompatApi21.adjustVolume(this.mControllerObj, n, n2);
        }
        
        @Override
        public boolean dispatchMediaButtonEvent(final KeyEvent keyEvent) {
            return MediaControllerCompatApi21.dispatchMediaButtonEvent(this.mControllerObj, keyEvent);
        }
        
        @Override
        public Bundle getExtras() {
            return MediaControllerCompatApi21.getExtras(this.mControllerObj);
        }
        
        @Override
        public long getFlags() {
            return MediaControllerCompatApi21.getFlags(this.mControllerObj);
        }
        
        @Override
        public Object getMediaController() {
            return this.mControllerObj;
        }
        
        @Override
        public MediaMetadataCompat getMetadata() {
            MediaMetadataCompat fromMediaMetadata = null;
            final Object metadata = MediaControllerCompatApi21.getMetadata(this.mControllerObj);
            if (metadata != null) {
                fromMediaMetadata = MediaMetadataCompat.fromMediaMetadata(metadata);
            }
            return fromMediaMetadata;
        }
        
        @Override
        public String getPackageName() {
            return MediaControllerCompatApi21.getPackageName(this.mControllerObj);
        }
        
        @Override
        public PlaybackInfo getPlaybackInfo() {
            PlaybackInfo playbackInfo = null;
            final Object playbackInfo2 = MediaControllerCompatApi21.getPlaybackInfo(this.mControllerObj);
            if (playbackInfo2 != null) {
                playbackInfo = new PlaybackInfo(MediaControllerCompatApi21.PlaybackInfo.getPlaybackType(playbackInfo2), MediaControllerCompatApi21.PlaybackInfo.getLegacyAudioStream(playbackInfo2), MediaControllerCompatApi21.PlaybackInfo.getVolumeControl(playbackInfo2), MediaControllerCompatApi21.PlaybackInfo.getMaxVolume(playbackInfo2), MediaControllerCompatApi21.PlaybackInfo.getCurrentVolume(playbackInfo2));
            }
            return playbackInfo;
        }
        
        @Override
        public PlaybackStateCompat getPlaybackState() {
            Label_0007: {
                if (this.mExtraBinder != null) {
                    try {
                        return this.mExtraBinder.getPlaybackState();
                    }
                    catch (final RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in getPlaybackState.", (Throwable)ex);
                        break Label_0007;
                    }
                    final Object playbackState;
                    return PlaybackStateCompat.fromPlaybackState(playbackState);
                }
            }
            final Object playbackState = MediaControllerCompatApi21.getPlaybackState(this.mControllerObj);
            if (playbackState != null) {
                return PlaybackStateCompat.fromPlaybackState(playbackState);
            }
            return null;
            fromPlaybackState = PlaybackStateCompat.fromPlaybackState(playbackState);
            return fromPlaybackState;
        }
        
        @Override
        public List<MediaSessionCompat.QueueItem> getQueue() {
            List<MediaSessionCompat.QueueItem> fromQueueItemList = null;
            final List<Object> queue = MediaControllerCompatApi21.getQueue(this.mControllerObj);
            if (queue != null) {
                fromQueueItemList = MediaSessionCompat.QueueItem.fromQueueItemList(queue);
            }
            return fromQueueItemList;
        }
        
        @Override
        public CharSequence getQueueTitle() {
            return MediaControllerCompatApi21.getQueueTitle(this.mControllerObj);
        }
        
        @Override
        public int getRatingType() {
            if (Build$VERSION.SDK_INT < 22 && this.mExtraBinder != null) {
                try {
                    return this.mExtraBinder.getRatingType();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in getRatingType.", (Throwable)ex);
                }
            }
            return MediaControllerCompatApi21.getRatingType(this.mControllerObj);
        }
        
        @Override
        public int getRepeatMode() {
            if (this.mExtraBinder != null) {
                try {
                    return this.mExtraBinder.getRepeatMode();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in getRepeatMode.", (Throwable)ex);
                }
            }
            return 0;
        }
        
        @Override
        public PendingIntent getSessionActivity() {
            return MediaControllerCompatApi21.getSessionActivity(this.mControllerObj);
        }
        
        @Override
        public int getShuffleMode() {
            if (this.mExtraBinder != null) {
                try {
                    return this.mExtraBinder.getShuffleMode();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in getShuffleMode.", (Throwable)ex);
                }
            }
            return 0;
        }
        
        @Override
        public TransportControls getTransportControls() {
            TransportControls transportControls = null;
            final Object transportControls2 = MediaControllerCompatApi21.getTransportControls(this.mControllerObj);
            if (transportControls2 != null) {
                transportControls = new TransportControlsApi21(transportControls2);
            }
            return transportControls;
        }
        
        @Override
        public boolean isCaptioningEnabled() {
            if (this.mExtraBinder != null) {
                try {
                    return this.mExtraBinder.isCaptioningEnabled();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in isCaptioningEnabled.", (Throwable)ex);
                }
            }
            return false;
        }
        
        @Override
        public boolean isShuffleModeEnabled() {
            if (this.mExtraBinder != null) {
                try {
                    return this.mExtraBinder.isShuffleModeEnabledDeprecated();
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in isShuffleModeEnabled.", (Throwable)ex);
                }
            }
            return false;
        }
        
        @Override
        public final void registerCallback(final Callback key, Handler mPendingCallbacks) {
            MediaControllerCompatApi21.registerCallback(this.mControllerObj, key.mCallbackObj, mPendingCallbacks);
            Label_0040: {
                if (this.mExtraBinder != null) {
                    break Label_0040;
                }
                mPendingCallbacks = (Handler)this.mPendingCallbacks;
                synchronized (mPendingCallbacks) {
                    this.mPendingCallbacks.add(key);
                    return;
                    mPendingCallbacks = (Handler)new ExtraCallback(key);
                    this.mCallbackMap.put(key, (ExtraCallback)mPendingCallbacks);
                    key.mHasExtraCallback = true;
                    try {
                        this.mExtraBinder.registerCallbackListener((IMediaControllerCallback)mPendingCallbacks);
                    }
                    catch (final RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                    }
                }
            }
        }
        
        @Override
        public void removeQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
            if ((this.getFlags() & 0x4L) == 0x0L) {
                throw new UnsupportedOperationException("This session doesn't support queue management operations");
            }
            final Bundle bundle = new Bundle();
            bundle.putParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION", (Parcelable)mediaDescriptionCompat);
            this.sendCommand("android.support.v4.media.session.command.REMOVE_QUEUE_ITEM", bundle, null);
        }
        
        @Override
        public void sendCommand(final String s, final Bundle bundle, final ResultReceiver resultReceiver) {
            MediaControllerCompatApi21.sendCommand(this.mControllerObj, s, bundle, resultReceiver);
        }
        
        @Override
        public void setVolumeTo(final int n, final int n2) {
            MediaControllerCompatApi21.setVolumeTo(this.mControllerObj, n, n2);
        }
        
        @Override
        public final void unregisterCallback(final Callback key) {
            MediaControllerCompatApi21.unregisterCallback(this.mControllerObj, key.mCallbackObj);
            Label_0039: {
                if (this.mExtraBinder != null) {
                    break Label_0039;
                }
                synchronized (this.mPendingCallbacks) {
                    this.mPendingCallbacks.remove(key);
                    return;
                    try {
                        final ExtraCallback extraCallback = this.mCallbackMap.remove(key);
                        if (extraCallback != null) {
                            this.mExtraBinder.unregisterCallbackListener(extraCallback);
                        }
                    }
                    catch (final RemoteException ex) {
                        Log.e("MediaControllerCompat", "Dead object in unregisterCallback.", (Throwable)ex);
                    }
                }
            }
        }
        
        private static class ExtraBinderRequestResultReceiver extends ResultReceiver
        {
            private WeakReference<MediaControllerImplApi21> mMediaControllerImpl;
            
            public ExtraBinderRequestResultReceiver(final MediaControllerImplApi21 referent, final Handler handler) {
                super(handler);
                this.mMediaControllerImpl = new WeakReference<MediaControllerImplApi21>(referent);
            }
            
            protected void onReceiveResult(final int n, final Bundle bundle) {
                final MediaControllerImplApi21 mediaControllerImplApi21 = this.mMediaControllerImpl.get();
                if (mediaControllerImplApi21 != null && bundle != null) {
                    mediaControllerImplApi21.mExtraBinder = IMediaSession.Stub.asInterface(BundleCompat.getBinder(bundle, "android.support.v4.media.session.EXTRA_BINDER"));
                    mediaControllerImplApi21.processPendingCallbacks();
                }
            }
        }
        
        private static class ExtraCallback extends StubCompat
        {
            ExtraCallback(final Callback callback) {
                super(callback);
            }
            
            @Override
            public void onExtrasChanged(final Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onMetadataChanged(final MediaMetadataCompat mediaMetadataCompat) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onQueueChanged(final List<MediaSessionCompat.QueueItem> list) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onQueueTitleChanged(final CharSequence charSequence) throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onSessionDestroyed() throws RemoteException {
                throw new AssertionError();
            }
            
            @Override
            public void onVolumeInfoChanged(final ParcelableVolumeInfo parcelableVolumeInfo) throws RemoteException {
                throw new AssertionError();
            }
        }
    }
    
    @RequiresApi(23)
    static class MediaControllerImplApi23 extends MediaControllerImplApi21
    {
        public MediaControllerImplApi23(final Context context, final MediaSessionCompat.Token token) throws RemoteException {
            super(context, token);
        }
        
        public MediaControllerImplApi23(final Context context, final MediaSessionCompat mediaSessionCompat) {
            super(context, mediaSessionCompat);
        }
        
        @Override
        public TransportControls getTransportControls() {
            TransportControls transportControls = null;
            final Object transportControls2 = MediaControllerCompatApi21.getTransportControls(this.mControllerObj);
            if (transportControls2 != null) {
                transportControls = new TransportControlsApi23(transportControls2);
            }
            return transportControls;
        }
    }
    
    @RequiresApi(24)
    static class MediaControllerImplApi24 extends MediaControllerImplApi23
    {
        public MediaControllerImplApi24(final Context context, final MediaSessionCompat.Token token) throws RemoteException {
            super(context, token);
        }
        
        public MediaControllerImplApi24(final Context context, final MediaSessionCompat mediaSessionCompat) {
            super(context, mediaSessionCompat);
        }
        
        @Override
        public TransportControls getTransportControls() {
            TransportControls transportControls = null;
            final Object transportControls2 = MediaControllerCompatApi21.getTransportControls(this.mControllerObj);
            if (transportControls2 != null) {
                transportControls = new TransportControlsApi24(transportControls2);
            }
            return transportControls;
        }
    }
    
    static class MediaControllerImplBase implements MediaControllerImpl
    {
        private IMediaSession mBinder;
        private TransportControls mTransportControls;
        
        public MediaControllerImplBase(final MediaSessionCompat.Token token) {
            this.mBinder = IMediaSession.Stub.asInterface((IBinder)token.getToken());
        }
        
        @Override
        public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
            Label_0041: {
                try {
                    if ((this.mBinder.getFlags() & 0x4L) == 0x0L) {
                        throw new UnsupportedOperationException("This session doesn't support queue management operations");
                    }
                    break Label_0041;
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in addQueueItem.", (Throwable)ex);
                }
                return;
            }
            this.mBinder.addQueueItem(mediaDescriptionCompat);
        }
        
        @Override
        public void addQueueItem(final MediaDescriptionCompat mediaDescriptionCompat, final int n) {
            Label_0041: {
                try {
                    if ((this.mBinder.getFlags() & 0x4L) == 0x0L) {
                        throw new UnsupportedOperationException("This session doesn't support queue management operations");
                    }
                    break Label_0041;
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in addQueueItemAt.", (Throwable)ex);
                }
                return;
            }
            this.mBinder.addQueueItemAt(mediaDescriptionCompat, n);
        }
        
        @Override
        public void adjustVolume(final int n, final int n2) {
            try {
                this.mBinder.adjustVolume(n, n2, null);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in adjustVolume.", (Throwable)ex);
            }
        }
        
        @Override
        public boolean dispatchMediaButtonEvent(final KeyEvent keyEvent) {
            Label_0017: {
                if (keyEvent == null) {
                    break Label_0017;
                }
                try {
                    this.mBinder.sendMediaButton(keyEvent);
                    return false;
                    throw new IllegalArgumentException("event may not be null.");
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in dispatchMediaButtonEvent.", (Throwable)ex);
                    return false;
                }
            }
        }
        
        @Override
        public Bundle getExtras() {
            try {
                return this.mBinder.getExtras();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getExtras.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public long getFlags() {
            try {
                return this.mBinder.getFlags();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getFlags.", (Throwable)ex);
                return 0L;
            }
        }
        
        @Override
        public Object getMediaController() {
            return null;
        }
        
        @Override
        public MediaMetadataCompat getMetadata() {
            try {
                return this.mBinder.getMetadata();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getMetadata.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public String getPackageName() {
            try {
                return this.mBinder.getPackageName();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getPackageName.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public PlaybackInfo getPlaybackInfo() {
            try {
                final ParcelableVolumeInfo volumeAttributes = this.mBinder.getVolumeAttributes();
                return new PlaybackInfo(volumeAttributes.volumeType, volumeAttributes.audioStream, volumeAttributes.controlType, volumeAttributes.maxVolume, volumeAttributes.currentVolume);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getPlaybackInfo.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public PlaybackStateCompat getPlaybackState() {
            try {
                return this.mBinder.getPlaybackState();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getPlaybackState.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public List<MediaSessionCompat.QueueItem> getQueue() {
            try {
                return this.mBinder.getQueue();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getQueue.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public CharSequence getQueueTitle() {
            try {
                return this.mBinder.getQueueTitle();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getQueueTitle.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public int getRatingType() {
            try {
                return this.mBinder.getRatingType();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getRatingType.", (Throwable)ex);
                return 0;
            }
        }
        
        @Override
        public int getRepeatMode() {
            try {
                return this.mBinder.getRepeatMode();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getRepeatMode.", (Throwable)ex);
                return 0;
            }
        }
        
        @Override
        public PendingIntent getSessionActivity() {
            try {
                return this.mBinder.getLaunchPendingIntent();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getSessionActivity.", (Throwable)ex);
                return null;
            }
        }
        
        @Override
        public int getShuffleMode() {
            try {
                return this.mBinder.getShuffleMode();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in getShuffleMode.", (Throwable)ex);
                return 0;
            }
        }
        
        @Override
        public TransportControls getTransportControls() {
            if (this.mTransportControls == null) {
                this.mTransportControls = new TransportControlsBase(this.mBinder);
            }
            return this.mTransportControls;
        }
        
        @Override
        public boolean isCaptioningEnabled() {
            try {
                return this.mBinder.isCaptioningEnabled();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in isCaptioningEnabled.", (Throwable)ex);
                return false;
            }
        }
        
        @Override
        public boolean isShuffleModeEnabled() {
            try {
                return this.mBinder.isShuffleModeEnabledDeprecated();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in isShuffleModeEnabled.", (Throwable)ex);
                return false;
            }
        }
        
        @Override
        public void registerCallback(final Callback callback, final Handler handler) {
            Label_0037: {
                if (callback == null) {
                    break Label_0037;
                }
                try {
                    this.mBinder.asBinder().linkToDeath((IBinder$DeathRecipient)callback, 0);
                    this.mBinder.registerCallbackListener((IMediaControllerCallback)callback.mCallbackObj);
                    return;
                    throw new IllegalArgumentException("callback may not be null.");
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in registerCallback.", (Throwable)ex);
                    callback.onSessionDestroyed();
                }
            }
        }
        
        @Override
        public void removeQueueItem(final MediaDescriptionCompat mediaDescriptionCompat) {
            Label_0041: {
                try {
                    if ((this.mBinder.getFlags() & 0x4L) == 0x0L) {
                        throw new UnsupportedOperationException("This session doesn't support queue management operations");
                    }
                    break Label_0041;
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in removeQueueItem.", (Throwable)ex);
                }
                return;
            }
            this.mBinder.removeQueueItem(mediaDescriptionCompat);
        }
        
        @Override
        public void sendCommand(final String s, final Bundle bundle, final ResultReceiver resultReceiver) {
            try {
                this.mBinder.sendCommand(s, bundle, new MediaSessionCompat.ResultReceiverWrapper(resultReceiver));
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in sendCommand.", (Throwable)ex);
            }
        }
        
        @Override
        public void setVolumeTo(final int n, final int n2) {
            try {
                this.mBinder.setVolumeTo(n, n2, null);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setVolumeTo.", (Throwable)ex);
            }
        }
        
        @Override
        public void unregisterCallback(final Callback callback) {
            Label_0038: {
                if (callback == null) {
                    break Label_0038;
                }
                try {
                    this.mBinder.unregisterCallbackListener((IMediaControllerCallback)callback.mCallbackObj);
                    this.mBinder.asBinder().unlinkToDeath((IBinder$DeathRecipient)callback, 0);
                    return;
                    throw new IllegalArgumentException("callback may not be null.");
                }
                catch (final RemoteException ex) {
                    Log.e("MediaControllerCompat", "Dead object in unregisterCallback.", (Throwable)ex);
                }
            }
        }
    }
    
    public static final class PlaybackInfo
    {
        public static final int PLAYBACK_TYPE_LOCAL = 1;
        public static final int PLAYBACK_TYPE_REMOTE = 2;
        private final int mAudioStream;
        private final int mCurrentVolume;
        private final int mMaxVolume;
        private final int mPlaybackType;
        private final int mVolumeControl;
        
        PlaybackInfo(final int mPlaybackType, final int mAudioStream, final int mVolumeControl, final int mMaxVolume, final int mCurrentVolume) {
            this.mPlaybackType = mPlaybackType;
            this.mAudioStream = mAudioStream;
            this.mVolumeControl = mVolumeControl;
            this.mMaxVolume = mMaxVolume;
            this.mCurrentVolume = mCurrentVolume;
        }
        
        public int getAudioStream() {
            return this.mAudioStream;
        }
        
        public int getCurrentVolume() {
            return this.mCurrentVolume;
        }
        
        public int getMaxVolume() {
            return this.mMaxVolume;
        }
        
        public int getPlaybackType() {
            return this.mPlaybackType;
        }
        
        public int getVolumeControl() {
            return this.mVolumeControl;
        }
    }
    
    public abstract static class TransportControls
    {
        public static final String EXTRA_LEGACY_STREAM_TYPE = "android.media.session.extra.LEGACY_STREAM_TYPE";
        
        TransportControls() {
        }
        
        public abstract void fastForward();
        
        public abstract void pause();
        
        public abstract void play();
        
        public abstract void playFromMediaId(final String p0, final Bundle p1);
        
        public abstract void playFromSearch(final String p0, final Bundle p1);
        
        public abstract void playFromUri(final Uri p0, final Bundle p1);
        
        public abstract void prepare();
        
        public abstract void prepareFromMediaId(final String p0, final Bundle p1);
        
        public abstract void prepareFromSearch(final String p0, final Bundle p1);
        
        public abstract void prepareFromUri(final Uri p0, final Bundle p1);
        
        public abstract void rewind();
        
        public abstract void seekTo(final long p0);
        
        public abstract void sendCustomAction(final PlaybackStateCompat.CustomAction p0, final Bundle p1);
        
        public abstract void sendCustomAction(final String p0, final Bundle p1);
        
        public abstract void setCaptioningEnabled(final boolean p0);
        
        public abstract void setRating(final RatingCompat p0);
        
        public abstract void setRating(final RatingCompat p0, final Bundle p1);
        
        public abstract void setRepeatMode(final int p0);
        
        public abstract void setShuffleMode(final int p0);
        
        @Deprecated
        public abstract void setShuffleModeEnabled(final boolean p0);
        
        public abstract void skipToNext();
        
        public abstract void skipToPrevious();
        
        public abstract void skipToQueueItem(final long p0);
        
        public abstract void stop();
    }
    
    static class TransportControlsApi21 extends TransportControls
    {
        protected final Object mControlsObj;
        
        public TransportControlsApi21(final Object mControlsObj) {
            this.mControlsObj = mControlsObj;
        }
        
        @Override
        public void fastForward() {
            MediaControllerCompatApi21.TransportControls.fastForward(this.mControlsObj);
        }
        
        @Override
        public void pause() {
            MediaControllerCompatApi21.TransportControls.pause(this.mControlsObj);
        }
        
        @Override
        public void play() {
            MediaControllerCompatApi21.TransportControls.play(this.mControlsObj);
        }
        
        @Override
        public void playFromMediaId(final String s, final Bundle bundle) {
            MediaControllerCompatApi21.TransportControls.playFromMediaId(this.mControlsObj, s, bundle);
        }
        
        @Override
        public void playFromSearch(final String s, final Bundle bundle) {
            MediaControllerCompatApi21.TransportControls.playFromSearch(this.mControlsObj, s, bundle);
        }
        
        @Override
        public void playFromUri(final Uri uri, final Bundle bundle) {
            if (uri != null && !Uri.EMPTY.equals((Object)uri)) {
                final Bundle bundle2 = new Bundle();
                bundle2.putParcelable("android.support.v4.media.session.action.ARGUMENT_URI", (Parcelable)uri);
                bundle2.putParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS", (Parcelable)bundle);
                this.sendCustomAction("android.support.v4.media.session.action.PLAY_FROM_URI", bundle2);
                return;
            }
            throw new IllegalArgumentException("You must specify a non-empty Uri for playFromUri.");
        }
        
        @Override
        public void prepare() {
            this.sendCustomAction("android.support.v4.media.session.action.PREPARE", null);
        }
        
        @Override
        public void prepareFromMediaId(final String s, final Bundle bundle) {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("android.support.v4.media.session.action.ARGUMENT_MEDIA_ID", s);
            bundle2.putBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS", bundle);
            this.sendCustomAction("android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID", bundle2);
        }
        
        @Override
        public void prepareFromSearch(final String s, final Bundle bundle) {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("android.support.v4.media.session.action.ARGUMENT_QUERY", s);
            bundle2.putBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS", bundle);
            this.sendCustomAction("android.support.v4.media.session.action.PREPARE_FROM_SEARCH", bundle2);
        }
        
        @Override
        public void prepareFromUri(final Uri uri, final Bundle bundle) {
            final Bundle bundle2 = new Bundle();
            bundle2.putParcelable("android.support.v4.media.session.action.ARGUMENT_URI", (Parcelable)uri);
            bundle2.putBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS", bundle);
            this.sendCustomAction("android.support.v4.media.session.action.PREPARE_FROM_URI", bundle2);
        }
        
        @Override
        public void rewind() {
            MediaControllerCompatApi21.TransportControls.rewind(this.mControlsObj);
        }
        
        @Override
        public void seekTo(final long n) {
            MediaControllerCompatApi21.TransportControls.seekTo(this.mControlsObj, n);
        }
        
        @Override
        public void sendCustomAction(final PlaybackStateCompat.CustomAction customAction, final Bundle bundle) {
            validateCustomAction(customAction.getAction(), bundle);
            MediaControllerCompatApi21.TransportControls.sendCustomAction(this.mControlsObj, customAction.getAction(), bundle);
        }
        
        @Override
        public void sendCustomAction(final String s, final Bundle bundle) {
            validateCustomAction(s, bundle);
            MediaControllerCompatApi21.TransportControls.sendCustomAction(this.mControlsObj, s, bundle);
        }
        
        @Override
        public void setCaptioningEnabled(final boolean b) {
            final Bundle bundle = new Bundle();
            bundle.putBoolean("android.support.v4.media.session.action.ARGUMENT_CAPTIONING_ENABLED", b);
            this.sendCustomAction("android.support.v4.media.session.action.SET_CAPTIONING_ENABLED", bundle);
        }
        
        @Override
        public void setRating(final RatingCompat ratingCompat) {
            final Object o = null;
            final Object mControlsObj = this.mControlsObj;
            Object rating;
            if (ratingCompat == null) {
                rating = o;
            }
            else {
                rating = ratingCompat.getRating();
            }
            MediaControllerCompatApi21.TransportControls.setRating(mControlsObj, rating);
        }
        
        @Override
        public void setRating(final RatingCompat ratingCompat, final Bundle bundle) {
            final Bundle bundle2 = new Bundle();
            bundle2.putParcelable("android.support.v4.media.session.action.ARGUMENT_RATING", (Parcelable)ratingCompat);
            bundle2.putParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS", (Parcelable)bundle);
            this.sendCustomAction("android.support.v4.media.session.action.SET_RATING", bundle2);
        }
        
        @Override
        public void setRepeatMode(final int n) {
            final Bundle bundle = new Bundle();
            bundle.putInt("android.support.v4.media.session.action.ARGUMENT_REPEAT_MODE", n);
            this.sendCustomAction("android.support.v4.media.session.action.SET_REPEAT_MODE", bundle);
        }
        
        @Override
        public void setShuffleMode(final int n) {
            final Bundle bundle = new Bundle();
            bundle.putInt("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE", n);
            this.sendCustomAction("android.support.v4.media.session.action.SET_SHUFFLE_MODE", bundle);
        }
        
        @Override
        public void setShuffleModeEnabled(final boolean b) {
            final Bundle bundle = new Bundle();
            bundle.putBoolean("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE_ENABLED", b);
            this.sendCustomAction("android.support.v4.media.session.action.SET_SHUFFLE_MODE_ENABLED", bundle);
        }
        
        @Override
        public void skipToNext() {
            MediaControllerCompatApi21.TransportControls.skipToNext(this.mControlsObj);
        }
        
        @Override
        public void skipToPrevious() {
            MediaControllerCompatApi21.TransportControls.skipToPrevious(this.mControlsObj);
        }
        
        @Override
        public void skipToQueueItem(final long n) {
            MediaControllerCompatApi21.TransportControls.skipToQueueItem(this.mControlsObj, n);
        }
        
        @Override
        public void stop() {
            MediaControllerCompatApi21.TransportControls.stop(this.mControlsObj);
        }
    }
    
    @RequiresApi(23)
    static class TransportControlsApi23 extends TransportControlsApi21
    {
        public TransportControlsApi23(final Object o) {
            super(o);
        }
        
        @Override
        public void playFromUri(final Uri uri, final Bundle bundle) {
            MediaControllerCompatApi23.TransportControls.playFromUri(this.mControlsObj, uri, bundle);
        }
    }
    
    @RequiresApi(24)
    static class TransportControlsApi24 extends TransportControlsApi23
    {
        public TransportControlsApi24(final Object o) {
            super(o);
        }
        
        @Override
        public void prepare() {
            MediaControllerCompatApi24.TransportControls.prepare(this.mControlsObj);
        }
        
        @Override
        public void prepareFromMediaId(final String s, final Bundle bundle) {
            MediaControllerCompatApi24.TransportControls.prepareFromMediaId(this.mControlsObj, s, bundle);
        }
        
        @Override
        public void prepareFromSearch(final String s, final Bundle bundle) {
            MediaControllerCompatApi24.TransportControls.prepareFromSearch(this.mControlsObj, s, bundle);
        }
        
        @Override
        public void prepareFromUri(final Uri uri, final Bundle bundle) {
            MediaControllerCompatApi24.TransportControls.prepareFromUri(this.mControlsObj, uri, bundle);
        }
    }
    
    static class TransportControlsBase extends TransportControls
    {
        private IMediaSession mBinder;
        
        public TransportControlsBase(final IMediaSession mBinder) {
            this.mBinder = mBinder;
        }
        
        @Override
        public void fastForward() {
            try {
                this.mBinder.fastForward();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in fastForward.", (Throwable)ex);
            }
        }
        
        @Override
        public void pause() {
            try {
                this.mBinder.pause();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in pause.", (Throwable)ex);
            }
        }
        
        @Override
        public void play() {
            try {
                this.mBinder.play();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in play.", (Throwable)ex);
            }
        }
        
        @Override
        public void playFromMediaId(final String s, final Bundle bundle) {
            try {
                this.mBinder.playFromMediaId(s, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in playFromMediaId.", (Throwable)ex);
            }
        }
        
        @Override
        public void playFromSearch(final String s, final Bundle bundle) {
            try {
                this.mBinder.playFromSearch(s, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in playFromSearch.", (Throwable)ex);
            }
        }
        
        @Override
        public void playFromUri(final Uri uri, final Bundle bundle) {
            try {
                this.mBinder.playFromUri(uri, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in playFromUri.", (Throwable)ex);
            }
        }
        
        @Override
        public void prepare() {
            try {
                this.mBinder.prepare();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in prepare.", (Throwable)ex);
            }
        }
        
        @Override
        public void prepareFromMediaId(final String s, final Bundle bundle) {
            try {
                this.mBinder.prepareFromMediaId(s, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in prepareFromMediaId.", (Throwable)ex);
            }
        }
        
        @Override
        public void prepareFromSearch(final String s, final Bundle bundle) {
            try {
                this.mBinder.prepareFromSearch(s, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in prepareFromSearch.", (Throwable)ex);
            }
        }
        
        @Override
        public void prepareFromUri(final Uri uri, final Bundle bundle) {
            try {
                this.mBinder.prepareFromUri(uri, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in prepareFromUri.", (Throwable)ex);
            }
        }
        
        @Override
        public void rewind() {
            try {
                this.mBinder.rewind();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in rewind.", (Throwable)ex);
            }
        }
        
        @Override
        public void seekTo(final long n) {
            try {
                this.mBinder.seekTo(n);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in seekTo.", (Throwable)ex);
            }
        }
        
        @Override
        public void sendCustomAction(final PlaybackStateCompat.CustomAction customAction, final Bundle bundle) {
            this.sendCustomAction(customAction.getAction(), bundle);
        }
        
        @Override
        public void sendCustomAction(final String s, final Bundle bundle) {
            validateCustomAction(s, bundle);
            try {
                this.mBinder.sendCustomAction(s, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in sendCustomAction.", (Throwable)ex);
            }
        }
        
        @Override
        public void setCaptioningEnabled(final boolean captioningEnabled) {
            try {
                this.mBinder.setCaptioningEnabled(captioningEnabled);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setCaptioningEnabled.", (Throwable)ex);
            }
        }
        
        @Override
        public void setRating(final RatingCompat ratingCompat) {
            try {
                this.mBinder.rate(ratingCompat);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setRating.", (Throwable)ex);
            }
        }
        
        @Override
        public void setRating(final RatingCompat ratingCompat, final Bundle bundle) {
            try {
                this.mBinder.rateWithExtras(ratingCompat, bundle);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setRating.", (Throwable)ex);
            }
        }
        
        @Override
        public void setRepeatMode(final int repeatMode) {
            try {
                this.mBinder.setRepeatMode(repeatMode);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setRepeatMode.", (Throwable)ex);
            }
        }
        
        @Override
        public void setShuffleMode(final int shuffleMode) {
            try {
                this.mBinder.setShuffleMode(shuffleMode);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setShuffleMode.", (Throwable)ex);
            }
        }
        
        @Override
        public void setShuffleModeEnabled(final boolean shuffleModeEnabledDeprecated) {
            try {
                this.mBinder.setShuffleModeEnabledDeprecated(shuffleModeEnabledDeprecated);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in setShuffleModeEnabled.", (Throwable)ex);
            }
        }
        
        @Override
        public void skipToNext() {
            try {
                this.mBinder.next();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in skipToNext.", (Throwable)ex);
            }
        }
        
        @Override
        public void skipToPrevious() {
            try {
                this.mBinder.previous();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in skipToPrevious.", (Throwable)ex);
            }
        }
        
        @Override
        public void skipToQueueItem(final long n) {
            try {
                this.mBinder.skipToQueueItem(n);
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in skipToQueueItem.", (Throwable)ex);
            }
        }
        
        @Override
        public void stop() {
            try {
                this.mBinder.stop();
            }
            catch (final RemoteException ex) {
                Log.e("MediaControllerCompat", "Dead object in stop.", (Throwable)ex);
            }
        }
    }
}
