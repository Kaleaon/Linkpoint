// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import java.util.Collections;
import android.os.Binder;
import android.support.annotation.RestrictTo;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import android.os.Parcelable$Creator;
import java.util.Iterator;
import java.util.Map;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.app.BundleCompat;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.support.annotation.RequiresApi;
import android.os.Parcelable;
import android.os.Parcel;
import android.support.v4.os.ResultReceiver;
import java.util.List;
import android.os.BadParcelableException;
import android.os.Message;
import android.os.Messenger;
import java.lang.ref.WeakReference;
import android.os.Handler;
import android.text.TextUtils;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public final class MediaBrowserCompat
{
    public static final String CUSTOM_ACTION_DOWNLOAD = "android.support.v4.media.action.DOWNLOAD";
    public static final String CUSTOM_ACTION_REMOVE_DOWNLOADED_FILE = "android.support.v4.media.action.REMOVE_DOWNLOADED_FILE";
    static final boolean DEBUG;
    public static final String EXTRA_DOWNLOAD_PROGRESS = "android.media.browse.extra.DOWNLOAD_PROGRESS";
    public static final String EXTRA_MEDIA_ID = "android.media.browse.extra.MEDIA_ID";
    public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
    public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
    static final String TAG = "MediaBrowserCompat";
    private final MediaBrowserImpl mImpl;
    
    static {
        DEBUG = Log.isLoggable("MediaBrowserCompat", 3);
    }
    
    public MediaBrowserCompat(final Context context, final ComponentName componentName, final ConnectionCallback connectionCallback, final Bundle bundle) {
        if (Build$VERSION.SDK_INT < 26) {
            if (Build$VERSION.SDK_INT < 23) {
                if (Build$VERSION.SDK_INT < 21) {
                    this.mImpl = (MediaBrowserImpl)new MediaBrowserImplBase(context, componentName, connectionCallback, bundle);
                }
                else {
                    this.mImpl = (MediaBrowserImpl)new MediaBrowserImplApi21(context, componentName, connectionCallback, bundle);
                }
            }
            else {
                this.mImpl = (MediaBrowserImpl)new MediaBrowserImplApi23(context, componentName, connectionCallback, bundle);
            }
        }
        else {
            this.mImpl = (MediaBrowserImpl)new MediaBrowserImplApi24(context, componentName, connectionCallback, bundle);
        }
    }
    
    public void connect() {
        this.mImpl.connect();
    }
    
    public void disconnect() {
        this.mImpl.disconnect();
    }
    
    @Nullable
    public Bundle getExtras() {
        return this.mImpl.getExtras();
    }
    
    public void getItem(@NonNull final String s, @NonNull final ItemCallback itemCallback) {
        this.mImpl.getItem(s, itemCallback);
    }
    
    @NonNull
    public String getRoot() {
        return this.mImpl.getRoot();
    }
    
    @NonNull
    public ComponentName getServiceComponent() {
        return this.mImpl.getServiceComponent();
    }
    
    @NonNull
    public MediaSessionCompat.Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }
    
    public boolean isConnected() {
        return this.mImpl.isConnected();
    }
    
    public void search(@NonNull final String s, final Bundle bundle, @NonNull final SearchCallback searchCallback) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            throw new IllegalArgumentException("query cannot be empty");
        }
        if (searchCallback != null) {
            this.mImpl.search(s, bundle, searchCallback);
            return;
        }
        throw new IllegalArgumentException("callback cannot be null");
    }
    
    public void sendCustomAction(@NonNull final String s, final Bundle bundle, @Nullable final CustomActionCallback customActionCallback) {
        if (!TextUtils.isEmpty((CharSequence)s)) {
            this.mImpl.sendCustomAction(s, bundle, customActionCallback);
            return;
        }
        throw new IllegalArgumentException("action cannot be empty");
    }
    
    public void subscribe(@NonNull final String s, @NonNull final Bundle bundle, @NonNull final SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            throw new IllegalArgumentException("parentId is empty");
        }
        if (subscriptionCallback == null) {
            throw new IllegalArgumentException("callback is null");
        }
        if (bundle != null) {
            this.mImpl.subscribe(s, bundle, subscriptionCallback);
            return;
        }
        throw new IllegalArgumentException("options are null");
    }
    
    public void subscribe(@NonNull final String s, @NonNull final SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            throw new IllegalArgumentException("parentId is empty");
        }
        if (subscriptionCallback != null) {
            this.mImpl.subscribe(s, null, subscriptionCallback);
            return;
        }
        throw new IllegalArgumentException("callback is null");
    }
    
    public void unsubscribe(@NonNull final String s) {
        if (!TextUtils.isEmpty((CharSequence)s)) {
            this.mImpl.unsubscribe(s, null);
            return;
        }
        throw new IllegalArgumentException("parentId is empty");
    }
    
    public void unsubscribe(@NonNull final String s, @NonNull final SubscriptionCallback subscriptionCallback) {
        if (TextUtils.isEmpty((CharSequence)s)) {
            throw new IllegalArgumentException("parentId is empty");
        }
        if (subscriptionCallback != null) {
            this.mImpl.unsubscribe(s, subscriptionCallback);
            return;
        }
        throw new IllegalArgumentException("callback is null");
    }
    
    private static class CallbackHandler extends Handler
    {
        private final WeakReference<MediaBrowserServiceCallbackImpl> mCallbackImplRef;
        private WeakReference<Messenger> mCallbacksMessengerRef;
        
        CallbackHandler(final MediaBrowserServiceCallbackImpl referent) {
            this.mCallbackImplRef = new WeakReference<MediaBrowserServiceCallbackImpl>(referent);
        }
        
        public void handleMessage(final Message obj) {
            if (this.mCallbacksMessengerRef == null || this.mCallbacksMessengerRef.get() == null || this.mCallbackImplRef.get() == null) {
                return;
            }
            while (true) {
                final Bundle data = obj.getData();
                data.setClassLoader(MediaSessionCompat.class.getClassLoader());
                final MediaBrowserServiceCallbackImpl mediaBrowserServiceCallbackImpl = this.mCallbackImplRef.get();
                final Messenger messenger = this.mCallbacksMessengerRef.get();
                Label_0216: {
                    Label_0205: {
                        try {
                            switch (obj.what) {
                                default: {
                                    Log.w("MediaBrowserCompat", "Unhandled message: " + obj + "\n  Client version: " + 1 + "\n  Service version: " + obj.arg1);
                                    break;
                                }
                                case 1: {
                                    mediaBrowserServiceCallbackImpl.onServiceConnected(messenger, data.getString("data_media_item_id"), (MediaSessionCompat.Token)data.getParcelable("data_media_session_token"), data.getBundle("data_root_hints"));
                                    break;
                                }
                                case 2: {
                                    break Label_0205;
                                }
                                case 3: {
                                    break Label_0216;
                                }
                            }
                            return;
                        }
                        catch (final BadParcelableException ex) {
                            Log.e("MediaBrowserCompat", "Could not unparcel the data.");
                            if (obj.what == 1) {
                                mediaBrowserServiceCallbackImpl.onConnectionFailed(messenger);
                            }
                            return;
                        }
                        return;
                    }
                    mediaBrowserServiceCallbackImpl.onConnectionFailed(messenger);
                    return;
                }
                mediaBrowserServiceCallbackImpl.onLoadChildren(messenger, data.getString("data_media_item_id"), data.getParcelableArrayList("data_media_item_list"), data.getBundle("data_options"));
            }
        }
        
        void setCallbacksMessenger(final Messenger referent) {
            this.mCallbacksMessengerRef = new WeakReference<Messenger>(referent);
        }
    }
    
    public static class ConnectionCallback
    {
        ConnectionCallbackInternal mConnectionCallbackInternal;
        final Object mConnectionCallbackObj;
        
        public ConnectionCallback() {
            if (Build$VERSION.SDK_INT < 21) {
                this.mConnectionCallbackObj = null;
            }
            else {
                this.mConnectionCallbackObj = MediaBrowserCompatApi21.createConnectionCallback((MediaBrowserCompatApi21.ConnectionCallback)new StubApi21());
            }
        }
        
        public void onConnected() {
        }
        
        public void onConnectionFailed() {
        }
        
        public void onConnectionSuspended() {
        }
        
        void setInternalConnectionCallback(final ConnectionCallbackInternal mConnectionCallbackInternal) {
            this.mConnectionCallbackInternal = mConnectionCallbackInternal;
        }
        
        interface ConnectionCallbackInternal
        {
            void onConnected();
            
            void onConnectionFailed();
            
            void onConnectionSuspended();
        }
        
        private class StubApi21 implements MediaBrowserCompatApi21.ConnectionCallback
        {
            StubApi21() {
            }
            
            @Override
            public void onConnected() {
                if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnected();
                }
                MediaBrowserCompat.ConnectionCallback.this.onConnected();
            }
            
            @Override
            public void onConnectionFailed() {
                if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnectionFailed();
                }
                MediaBrowserCompat.ConnectionCallback.this.onConnectionFailed();
            }
            
            @Override
            public void onConnectionSuspended() {
                if (MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal != null) {
                    MediaBrowserCompat.ConnectionCallback.this.mConnectionCallbackInternal.onConnectionSuspended();
                }
                MediaBrowserCompat.ConnectionCallback.this.onConnectionSuspended();
            }
        }
    }
    
    public abstract static class CustomActionCallback
    {
        public void onError(final String s, final Bundle bundle, final Bundle bundle2) {
        }
        
        public void onProgressUpdate(final String s, final Bundle bundle, final Bundle bundle2) {
        }
        
        public void onResult(final String s, final Bundle bundle, final Bundle bundle2) {
        }
    }
    
    private static class CustomActionResultReceiver extends ResultReceiver
    {
        private final String mAction;
        private final CustomActionCallback mCallback;
        private final Bundle mExtras;
        
        CustomActionResultReceiver(final String mAction, final Bundle mExtras, final CustomActionCallback mCallback, final Handler handler) {
            super(handler);
            this.mAction = mAction;
            this.mExtras = mExtras;
            this.mCallback = mCallback;
        }
        
        @Override
        protected void onReceiveResult(final int i, final Bundle obj) {
            if (this.mCallback != null) {
                switch (i) {
                    default: {
                        Log.w("MediaBrowserCompat", "Unknown result code: " + i + " (extras=" + this.mExtras + ", resultData=" + obj + ")");
                        break;
                    }
                    case 1: {
                        this.mCallback.onProgressUpdate(this.mAction, this.mExtras, obj);
                        break;
                    }
                    case 0: {
                        this.mCallback.onResult(this.mAction, this.mExtras, obj);
                        break;
                    }
                    case -1: {
                        this.mCallback.onError(this.mAction, this.mExtras, obj);
                        break;
                    }
                }
            }
        }
    }
    
    public abstract static class ItemCallback
    {
        final Object mItemCallbackObj;
        
        public ItemCallback() {
            if (Build$VERSION.SDK_INT < 23) {
                this.mItemCallbackObj = null;
            }
            else {
                this.mItemCallbackObj = MediaBrowserCompatApi23.createItemCallback((MediaBrowserCompatApi23.ItemCallback)new StubApi23());
            }
        }
        
        public void onError(@NonNull final String s) {
        }
        
        public void onItemLoaded(final MediaItem mediaItem) {
        }
        
        private class StubApi23 implements MediaBrowserCompatApi23.ItemCallback
        {
            StubApi23() {
            }
            
            @Override
            public void onError(@NonNull final String s) {
                MediaBrowserCompat.ItemCallback.this.onError(s);
            }
            
            @Override
            public void onItemLoaded(final Parcel parcel) {
                if (parcel != null) {
                    parcel.setDataPosition(0);
                    final MediaItem mediaItem = (MediaItem)MediaItem.CREATOR.createFromParcel(parcel);
                    parcel.recycle();
                    MediaBrowserCompat.ItemCallback.this.onItemLoaded(mediaItem);
                }
                else {
                    MediaBrowserCompat.ItemCallback.this.onItemLoaded(null);
                }
            }
        }
    }
    
    private static class ItemReceiver extends ResultReceiver
    {
        private final ItemCallback mCallback;
        private final String mMediaId;
        
        ItemReceiver(final String mMediaId, final ItemCallback mCallback, final Handler handler) {
            super(handler);
            this.mMediaId = mMediaId;
            this.mCallback = mCallback;
        }
        
        @Override
        protected void onReceiveResult(final int n, final Bundle bundle) {
            if (bundle != null) {
                bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            }
            if (n == 0 && bundle != null && bundle.containsKey("media_item")) {
                final Parcelable parcelable = bundle.getParcelable("media_item");
                if (parcelable != null && !(parcelable instanceof MediaItem)) {
                    this.mCallback.onError(this.mMediaId);
                }
                else {
                    this.mCallback.onItemLoaded((MediaItem)parcelable);
                }
                return;
            }
            this.mCallback.onError(this.mMediaId);
        }
    }
    
    interface MediaBrowserImpl
    {
        void connect();
        
        void disconnect();
        
        @Nullable
        Bundle getExtras();
        
        void getItem(@NonNull final String p0, @NonNull final ItemCallback p1);
        
        @NonNull
        String getRoot();
        
        ComponentName getServiceComponent();
        
        @NonNull
        MediaSessionCompat.Token getSessionToken();
        
        boolean isConnected();
        
        void search(@NonNull final String p0, final Bundle p1, @NonNull final SearchCallback p2);
        
        void sendCustomAction(@NonNull final String p0, final Bundle p1, @Nullable final CustomActionCallback p2);
        
        void subscribe(@NonNull final String p0, final Bundle p1, @NonNull final SubscriptionCallback p2);
        
        void unsubscribe(@NonNull final String p0, final SubscriptionCallback p1);
    }
    
    @RequiresApi(21)
    static class MediaBrowserImplApi21 implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl, ConnectionCallbackInternal
    {
        protected final Object mBrowserObj;
        protected Messenger mCallbacksMessenger;
        final Context mContext;
        protected final CallbackHandler mHandler;
        private MediaSessionCompat.Token mMediaSessionToken;
        protected final Bundle mRootHints;
        protected ServiceBinderWrapper mServiceBinderWrapper;
        private final ArrayMap<String, Subscription> mSubscriptions;
        
        public MediaBrowserImplApi21(final Context mContext, final ComponentName componentName, final ConnectionCallback connectionCallback, Bundle bundle) {
            this.mHandler = new CallbackHandler(this);
            this.mSubscriptions = new ArrayMap<String, Subscription>();
            this.mContext = mContext;
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("extra_client_version", 1);
            this.mRootHints = new Bundle(bundle);
            connectionCallback.setInternalConnectionCallback((ConnectionCallbackInternal)this);
            this.mBrowserObj = MediaBrowserCompatApi21.createBrowser(mContext, componentName, connectionCallback.mConnectionCallbackObj, this.mRootHints);
        }
        
        @Override
        public void connect() {
            MediaBrowserCompatApi21.connect(this.mBrowserObj);
        }
        
        @Override
        public void disconnect() {
            if (this.mServiceBinderWrapper != null && this.mCallbacksMessenger != null) {
                try {
                    this.mServiceBinderWrapper.unregisterCallbackMessenger(this.mCallbacksMessenger);
                }
                catch (final RemoteException ex) {
                    Log.i("MediaBrowserCompat", "Remote error unregistering client messenger.");
                }
            }
            MediaBrowserCompatApi21.disconnect(this.mBrowserObj);
        }
        
        @Nullable
        @Override
        public Bundle getExtras() {
            return MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
        }
        
        @Override
        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            Label_0056: {
                if (TextUtils.isEmpty((CharSequence)str)) {
                    break Label_0056;
                }
                Label_0066: {
                    if (itemCallback == null) {
                        break Label_0066;
                    }
                    Label_0076: {
                        if (!MediaBrowserCompatApi21.isConnected(this.mBrowserObj)) {
                            break Label_0076;
                        }
                        Label_0103: {
                            if (this.mServiceBinderWrapper == null) {
                                break Label_0103;
                            }
                            final ItemReceiver itemReceiver = new ItemReceiver(str, itemCallback, this.mHandler);
                            try {
                                this.mServiceBinderWrapper.getMediaItem(str, itemReceiver, this.mCallbacksMessenger);
                                return;
                                Log.i("MediaBrowserCompat", "Not connected, unable to retrieve the MediaItem.");
                                this.mHandler.post((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        itemCallback.onError(str);
                                    }
                                });
                                return;
                                this.mHandler.post((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        itemCallback.onError(str);
                                    }
                                });
                                return;
                                throw new IllegalArgumentException("mediaId is empty");
                                throw new IllegalArgumentException("cb is null");
                            }
                            catch (final RemoteException ex) {
                                Log.i("MediaBrowserCompat", "Remote error getting media item: " + str);
                                this.mHandler.post((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        itemCallback.onError(str);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
        
        @NonNull
        @Override
        public String getRoot() {
            return MediaBrowserCompatApi21.getRoot(this.mBrowserObj);
        }
        
        @Override
        public ComponentName getServiceComponent() {
            return MediaBrowserCompatApi21.getServiceComponent(this.mBrowserObj);
        }
        
        @NonNull
        @Override
        public MediaSessionCompat.Token getSessionToken() {
            if (this.mMediaSessionToken == null) {
                this.mMediaSessionToken = MediaSessionCompat.Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj));
            }
            return this.mMediaSessionToken;
        }
        
        @Override
        public boolean isConnected() {
            return MediaBrowserCompatApi21.isConnected(this.mBrowserObj);
        }
        
        @Override
        public void onConnected() {
            final Bundle extras = MediaBrowserCompatApi21.getExtras(this.mBrowserObj);
            if (extras != null) {
                final IBinder binder = BundleCompat.getBinder(extras, "extra_messenger");
                if (binder != null) {
                    this.mServiceBinderWrapper = new ServiceBinderWrapper(binder, this.mRootHints);
                    this.mCallbacksMessenger = new Messenger((Handler)this.mHandler);
                    this.mHandler.setCallbacksMessenger(this.mCallbacksMessenger);
                    try {
                        this.mServiceBinderWrapper.registerCallbackMessenger(this.mCallbacksMessenger);
                    }
                    catch (final RemoteException ex) {
                        Log.i("MediaBrowserCompat", "Remote error registering client messenger.");
                    }
                }
                final IMediaSession interface1 = IMediaSession.Stub.asInterface(BundleCompat.getBinder(extras, "extra_session_binder"));
                if (interface1 != null) {
                    this.mMediaSessionToken = MediaSessionCompat.Token.fromToken(MediaBrowserCompatApi21.getSessionToken(this.mBrowserObj), interface1);
                }
            }
        }
        
        @Override
        public void onConnectionFailed() {
        }
        
        @Override
        public void onConnectionFailed(final Messenger messenger) {
        }
        
        @Override
        public void onConnectionSuspended() {
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mMediaSessionToken = null;
            this.mHandler.setCallbacksMessenger(null);
        }
        
        @Override
        public void onLoadChildren(final Messenger messenger, final String str, final List list, final Bundle bundle) {
            if (this.mCallbacksMessenger != messenger) {
                return;
            }
            final Subscription subscription = this.mSubscriptions.get(str);
            if (subscription != null) {
                final SubscriptionCallback callback = subscription.getCallback(this.mContext, bundle);
                if (callback != null) {
                    if (bundle != null) {
                        if (list != null) {
                            callback.onChildrenLoaded(str, list, bundle);
                        }
                        else {
                            callback.onError(str, bundle);
                        }
                    }
                    else if (list != null) {
                        callback.onChildrenLoaded(str, list);
                    }
                    else {
                        callback.onError(str);
                    }
                }
                return;
            }
            if (MediaBrowserCompat.DEBUG) {
                Log.d("MediaBrowserCompat", "onLoadChildren for id that isn't subscribed id=" + str);
            }
        }
        
        @Override
        public void onServiceConnected(final Messenger messenger, final String s, final MediaSessionCompat.Token token, final Bundle bundle) {
        }
        
        @Override
        public void search(@NonNull final String str, final Bundle bundle, @NonNull final SearchCallback searchCallback) {
            Label_0046: {
                if (!this.isConnected()) {
                    break Label_0046;
                }
                Label_0057: {
                    if (this.mServiceBinderWrapper == null) {
                        break Label_0057;
                    }
                    final SearchResultReceiver searchResultReceiver = new SearchResultReceiver(str, bundle, searchCallback, this.mHandler);
                    try {
                        this.mServiceBinderWrapper.search(str, bundle, searchResultReceiver, this.mCallbacksMessenger);
                        return;
                        Log.i("MediaBrowserCompat", "The connected service doesn't support search.");
                        this.mHandler.post((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                searchCallback.onError(str, bundle);
                            }
                        });
                        return;
                        throw new IllegalStateException("search() called while not connected");
                    }
                    catch (final RemoteException ex) {
                        Log.i("MediaBrowserCompat", "Remote error searching items with query: " + str, (Throwable)ex);
                        this.mHandler.post((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                searchCallback.onError(str, bundle);
                            }
                        });
                    }
                }
            }
        }
        
        @Override
        public void sendCustomAction(@NonNull final String s, final Bundle bundle, @Nullable final CustomActionCallback customActionCallback) {
            Label_0046: {
                if (!this.isConnected()) {
                    break Label_0046;
                }
                Label_0102: {
                    if (this.mServiceBinderWrapper == null) {
                        break Label_0102;
                    }
                Block_4_Outer:
                    while (true) {
                        final CustomActionResultReceiver customActionResultReceiver = new CustomActionResultReceiver(s, bundle, customActionCallback, this.mHandler);
                        try {
                            this.mServiceBinderWrapper.sendCustomAction(s, bundle, customActionResultReceiver, this.mCallbacksMessenger);
                            return;
                            throw new IllegalStateException("Cannot send a custom action (" + s + ") with " + "extras " + bundle + " because the browser is not connected to the " + "service.");
                            while (true) {
                                this.mHandler.post((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        customActionCallback.onError(s, bundle, null);
                                    }
                                });
                                continue Block_4_Outer;
                                Log.i("MediaBrowserCompat", "The connected service doesn't support sendCustomAction.");
                                iftrue(Label_0014:)(customActionCallback == null);
                                continue;
                            }
                        }
                        catch (final RemoteException ex) {
                            Log.i("MediaBrowserCompat", "Remote error sending a custom action: action=" + s + ", extras=" + bundle, (Throwable)ex);
                            if (customActionCallback != null) {
                                this.mHandler.post((Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        customActionCallback.onError(s, bundle, null);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
        
        @Override
        public void subscribe(@NonNull final String str, final Bundle bundle, @NonNull final SubscriptionCallback subscriptionCallback) {
            Bundle bundle2 = null;
            Object o = this.mSubscriptions.get(str);
            while (true) {
                while (true) {
                    Label_0021: {
                        if (o != null) {
                            break Label_0021;
                        }
                        Label_0079: {
                            break Label_0079;
                            while (true) {
                                try {
                                    this.mServiceBinderWrapper.addSubscription(str, subscriptionCallback.mToken, bundle2, this.mCallbacksMessenger);
                                    return;
                                    o = new Subscription();
                                    this.mSubscriptions.put(str, (Subscription)o);
                                    break;
                                    MediaBrowserCompatApi21.subscribe(this.mBrowserObj, str, subscriptionCallback.mSubscriptionCallbackObj);
                                }
                                catch (final RemoteException ex) {
                                    Log.i("MediaBrowserCompat", "Remote error subscribing media item: " + str);
                                }
                                return;
                            }
                        }
                    }
                    subscriptionCallback.setSubscription((Subscription)o);
                    if (bundle != null) {
                        bundle2 = new Bundle(bundle);
                    }
                    ((Subscription)o).putCallback(this.mContext, bundle2, subscriptionCallback);
                    if (this.mServiceBinderWrapper != null) {
                        continue;
                    }
                    break;
                }
                continue;
            }
        }
        
        @Override
        public void unsubscribe(@NonNull final String p0, final SubscriptionCallback p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mSubscriptions:Landroid/support/v4/util/ArrayMap;
            //     4: aload_1        
            //     5: invokevirtual   android/support/v4/util/ArrayMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
            //     8: checkcast       Landroid/support/v4/media/MediaBrowserCompat$Subscription;
            //    11: astore_3       
            //    12: aload_3        
            //    13: ifnull          76
            //    16: aload_0        
            //    17: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
            //    20: ifnull          77
            //    23: aload_2        
            //    24: ifnull          189
            //    27: aload_3        
            //    28: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getCallbacks:()Ljava/util/List;
            //    31: astore          4
            //    33: aload_3        
            //    34: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getOptionsList:()Ljava/util/List;
            //    37: astore          5
            //    39: aload           4
            //    41: invokeinterface java/util/List.size:()I
            //    46: istore          6
            //    48: iload           6
            //    50: iconst_1       
            //    51: isub           
            //    52: istore          7
            //    54: iload           7
            //    56: ifge            236
            //    59: aload_3        
            //    60: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.isEmpty:()Z
            //    63: ifeq            296
            //    66: aload_0        
            //    67: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mSubscriptions:Landroid/support/v4/util/ArrayMap;
            //    70: aload_1        
            //    71: invokevirtual   android/support/v4/util/ArrayMap.remove:(Ljava/lang/Object;)Ljava/lang/Object;
            //    74: pop            
            //    75: return         
            //    76: return         
            //    77: aload_2        
            //    78: ifnull          134
            //    81: aload_3        
            //    82: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getCallbacks:()Ljava/util/List;
            //    85: astore          4
            //    87: aload_3        
            //    88: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getOptionsList:()Ljava/util/List;
            //    91: astore          5
            //    93: aload           4
            //    95: invokeinterface java/util/List.size:()I
            //   100: istore          6
            //   102: iload           6
            //   104: iconst_1       
            //   105: isub           
            //   106: istore          7
            //   108: iload           7
            //   110: ifge            145
            //   113: aload           4
            //   115: invokeinterface java/util/List.size:()I
            //   120: ifne            59
            //   123: aload_0        
            //   124: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mBrowserObj:Ljava/lang/Object;
            //   127: aload_1        
            //   128: invokestatic    android/support/v4/media/MediaBrowserCompatApi21.unsubscribe:(Ljava/lang/Object;Ljava/lang/String;)V
            //   131: goto            59
            //   134: aload_0        
            //   135: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mBrowserObj:Ljava/lang/Object;
            //   138: aload_1        
            //   139: invokestatic    android/support/v4/media/MediaBrowserCompatApi21.unsubscribe:(Ljava/lang/Object;Ljava/lang/String;)V
            //   142: goto            59
            //   145: iload           7
            //   147: istore          6
            //   149: aload           4
            //   151: iload           7
            //   153: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
            //   158: aload_2        
            //   159: if_acmpne       102
            //   162: aload           4
            //   164: iload           7
            //   166: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   171: pop            
            //   172: aload           5
            //   174: iload           7
            //   176: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   181: pop            
            //   182: iload           7
            //   184: istore          6
            //   186: goto            102
            //   189: aload_0        
            //   190: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
            //   193: aload_1        
            //   194: aconst_null    
            //   195: aload_0        
            //   196: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mCallbacksMessenger:Landroid/os/Messenger;
            //   199: invokevirtual   android/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper.removeSubscription:(Ljava/lang/String;Landroid/os/IBinder;Landroid/os/Messenger;)V
            //   202: goto            59
            //   205: astore          5
            //   207: ldc             "MediaBrowserCompat"
            //   209: new             Ljava/lang/StringBuilder;
            //   212: dup            
            //   213: invokespecial   java/lang/StringBuilder.<init>:()V
            //   216: ldc_w           "removeSubscription failed with RemoteException parentId="
            //   219: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   222: aload_1        
            //   223: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   226: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   229: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
            //   232: pop            
            //   233: goto            59
            //   236: iload           7
            //   238: istore          6
            //   240: aload           4
            //   242: iload           7
            //   244: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
            //   249: aload_2        
            //   250: if_acmpne       48
            //   253: aload_0        
            //   254: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
            //   257: aload_1        
            //   258: aload_2        
            //   259: invokestatic    android/support/v4/media/MediaBrowserCompat$SubscriptionCallback.access$000:(Landroid/support/v4/media/MediaBrowserCompat$SubscriptionCallback;)Landroid/os/IBinder;
            //   262: aload_0        
            //   263: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplApi21.mCallbacksMessenger:Landroid/os/Messenger;
            //   266: invokevirtual   android/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper.removeSubscription:(Ljava/lang/String;Landroid/os/IBinder;Landroid/os/Messenger;)V
            //   269: aload           4
            //   271: iload           7
            //   273: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   278: pop            
            //   279: aload           5
            //   281: iload           7
            //   283: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   288: pop            
            //   289: iload           7
            //   291: istore          6
            //   293: goto            48
            //   296: aload_2        
            //   297: ifnull          66
            //   300: goto            75
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                        
            //  -----  -----  -----  -----  ----------------------------
            //  27     48     205    236    Landroid/os/RemoteException;
            //  189    202    205    236    Landroid/os/RemoteException;
            //  240    289    205    236    Landroid/os/RemoteException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(p1:SubscriptionCallback, aconstnull:SubscriptionCallback())
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
    }
    
    @RequiresApi(23)
    static class MediaBrowserImplApi23 extends MediaBrowserImplApi21
    {
        public MediaBrowserImplApi23(final Context context, final ComponentName componentName, final ConnectionCallback connectionCallback, final Bundle bundle) {
            super(context, componentName, connectionCallback, bundle);
        }
        
        @Override
        public void getItem(@NonNull final String s, @NonNull final ItemCallback itemCallback) {
            if (this.mServiceBinderWrapper != null) {
                super.getItem(s, itemCallback);
            }
            else {
                MediaBrowserCompatApi23.getItem(this.mBrowserObj, s, itemCallback.mItemCallbackObj);
            }
        }
    }
    
    @RequiresApi(26)
    static class MediaBrowserImplApi24 extends MediaBrowserImplApi23
    {
        public MediaBrowserImplApi24(final Context context, final ComponentName componentName, final ConnectionCallback connectionCallback, final Bundle bundle) {
            super(context, componentName, connectionCallback, bundle);
        }
        
        @Override
        public void subscribe(@NonNull final String s, @NonNull final Bundle bundle, @NonNull final SubscriptionCallback subscriptionCallback) {
            if (bundle != null) {
                MediaBrowserCompatApi24.subscribe(this.mBrowserObj, s, bundle, subscriptionCallback.mSubscriptionCallbackObj);
            }
            else {
                MediaBrowserCompatApi21.subscribe(this.mBrowserObj, s, subscriptionCallback.mSubscriptionCallbackObj);
            }
        }
        
        @Override
        public void unsubscribe(@NonNull final String s, final SubscriptionCallback subscriptionCallback) {
            if (subscriptionCallback != null) {
                MediaBrowserCompatApi24.unsubscribe(this.mBrowserObj, s, subscriptionCallback.mSubscriptionCallbackObj);
            }
            else {
                MediaBrowserCompatApi21.unsubscribe(this.mBrowserObj, s);
            }
        }
    }
    
    static class MediaBrowserImplBase implements MediaBrowserImpl, MediaBrowserServiceCallbackImpl
    {
        static final int CONNECT_STATE_CONNECTED = 3;
        static final int CONNECT_STATE_CONNECTING = 2;
        static final int CONNECT_STATE_DISCONNECTED = 1;
        static final int CONNECT_STATE_DISCONNECTING = 0;
        static final int CONNECT_STATE_SUSPENDED = 4;
        final ConnectionCallback mCallback;
        Messenger mCallbacksMessenger;
        final Context mContext;
        private Bundle mExtras;
        final CallbackHandler mHandler;
        private MediaSessionCompat.Token mMediaSessionToken;
        final Bundle mRootHints;
        private String mRootId;
        ServiceBinderWrapper mServiceBinderWrapper;
        final ComponentName mServiceComponent;
        MediaServiceConnection mServiceConnection;
        int mState;
        private final ArrayMap<String, Subscription> mSubscriptions;
        
        public MediaBrowserImplBase(final Context mContext, final ComponentName mServiceComponent, final ConnectionCallback mCallback, final Bundle bundle) {
            final Bundle bundle2 = null;
            this.mHandler = new CallbackHandler(this);
            this.mSubscriptions = new ArrayMap<String, Subscription>();
            this.mState = 1;
            if (mContext == null) {
                throw new IllegalArgumentException("context must not be null");
            }
            if (mServiceComponent == null) {
                throw new IllegalArgumentException("service component must not be null");
            }
            if (mCallback != null) {
                this.mContext = mContext;
                this.mServiceComponent = mServiceComponent;
                this.mCallback = mCallback;
                Bundle mRootHints = bundle2;
                if (bundle != null) {
                    mRootHints = new Bundle(bundle);
                }
                this.mRootHints = mRootHints;
                return;
            }
            throw new IllegalArgumentException("connection callback must not be null");
        }
        
        private static String getStateLabel(final int i) {
            switch (i) {
                default: {
                    return "UNKNOWN/" + i;
                }
                case 0: {
                    return "CONNECT_STATE_DISCONNECTING";
                }
                case 1: {
                    return "CONNECT_STATE_DISCONNECTED";
                }
                case 2: {
                    return "CONNECT_STATE_CONNECTING";
                }
                case 3: {
                    return "CONNECT_STATE_CONNECTED";
                }
                case 4: {
                    return "CONNECT_STATE_SUSPENDED";
                }
            }
        }
        
        private boolean isCurrent(final Messenger messenger, final String str) {
            if (this.mCallbacksMessenger == messenger && this.mState != 0 && this.mState != 1) {
                return true;
            }
            if (this.mState != 0 && this.mState != 1) {
                Log.i("MediaBrowserCompat", str + " for " + this.mServiceComponent + " with mCallbacksMessenger=" + this.mCallbacksMessenger + " this=" + this);
            }
            return false;
        }
        
        @Override
        public void connect() {
            if (this.mState != 0 && this.mState != 1) {
                throw new IllegalStateException("connect() called while neigther disconnecting nor disconnected (state=" + getStateLabel(this.mState) + ")");
            }
            this.mState = 2;
            this.mHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    // 
                    // This method could not be decompiled.
                    // 
                    // Original Bytecode:
                    // 
                    //     1: istore_1       
                    //     2: aload_0        
                    //     3: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //     6: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mState:I
                    //     9: ifeq            119
                    //    12: aload_0        
                    //    13: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    16: iconst_2       
                    //    17: putfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mState:I
                    //    20: getstatic       android/support/v4/media/MediaBrowserCompat.DEBUG:Z
                    //    23: ifne            120
                    //    26: aload_0        
                    //    27: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    30: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
                    //    33: ifnonnull       163
                    //    36: aload_0        
                    //    37: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    40: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mCallbacksMessenger:Landroid/os/Messenger;
                    //    43: ifnonnull       196
                    //    46: new             Landroid/content/Intent;
                    //    49: dup            
                    //    50: ldc             "android.media.browse.MediaBrowserService"
                    //    52: invokespecial   android/content/Intent.<init>:(Ljava/lang/String;)V
                    //    55: astore_2       
                    //    56: aload_2        
                    //    57: aload_0        
                    //    58: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    61: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceComponent:Landroid/content/ComponentName;
                    //    64: invokevirtual   android/content/Intent.setComponent:(Landroid/content/ComponentName;)Landroid/content/Intent;
                    //    67: pop            
                    //    68: aload_0        
                    //    69: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    72: new             Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection;
                    //    75: dup            
                    //    76: aload_0        
                    //    77: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    80: invokespecial   android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection.<init>:(Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;)V
                    //    83: putfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceConnection:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection;
                    //    86: aload_0        
                    //    87: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    90: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mContext:Landroid/content/Context;
                    //    93: aload_2        
                    //    94: aload_0        
                    //    95: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //    98: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceConnection:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection;
                    //   101: iconst_1       
                    //   102: invokevirtual   android/content/Context.bindService:(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
                    //   105: istore_3       
                    //   106: iload_3        
                    //   107: istore_1       
                    //   108: iload_1        
                    //   109: ifeq            264
                    //   112: getstatic       android/support/v4/media/MediaBrowserCompat.DEBUG:Z
                    //   115: ifne            284
                    //   118: return         
                    //   119: return         
                    //   120: aload_0        
                    //   121: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   124: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceConnection:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection;
                    //   127: ifnull          26
                    //   130: new             Ljava/lang/RuntimeException;
                    //   133: dup            
                    //   134: new             Ljava/lang/StringBuilder;
                    //   137: dup            
                    //   138: invokespecial   java/lang/StringBuilder.<init>:()V
                    //   141: ldc             "mServiceConnection should be null. Instead it is "
                    //   143: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   146: aload_0        
                    //   147: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   150: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceConnection:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$MediaServiceConnection;
                    //   153: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                    //   156: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                    //   159: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
                    //   162: athrow         
                    //   163: new             Ljava/lang/RuntimeException;
                    //   166: dup            
                    //   167: new             Ljava/lang/StringBuilder;
                    //   170: dup            
                    //   171: invokespecial   java/lang/StringBuilder.<init>:()V
                    //   174: ldc             "mServiceBinderWrapper should be null. Instead it is "
                    //   176: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   179: aload_0        
                    //   180: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   183: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
                    //   186: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                    //   189: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                    //   192: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
                    //   195: athrow         
                    //   196: new             Ljava/lang/RuntimeException;
                    //   199: dup            
                    //   200: new             Ljava/lang/StringBuilder;
                    //   203: dup            
                    //   204: invokespecial   java/lang/StringBuilder.<init>:()V
                    //   207: ldc             "mCallbacksMessenger should be null. Instead it is "
                    //   209: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   212: aload_0        
                    //   213: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   216: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mCallbacksMessenger:Landroid/os/Messenger;
                    //   219: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                    //   222: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                    //   225: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;)V
                    //   228: athrow         
                    //   229: astore_2       
                    //   230: ldc             "MediaBrowserCompat"
                    //   232: new             Ljava/lang/StringBuilder;
                    //   235: dup            
                    //   236: invokespecial   java/lang/StringBuilder.<init>:()V
                    //   239: ldc             "Failed binding to service "
                    //   241: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   244: aload_0        
                    //   245: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   248: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceComponent:Landroid/content/ComponentName;
                    //   251: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/Object;)Ljava/lang/StringBuilder;
                    //   254: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
                    //   257: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;)I
                    //   260: pop            
                    //   261: goto            108
                    //   264: aload_0        
                    //   265: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   268: invokevirtual   android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.forceCloseConnection:()V
                    //   271: aload_0        
                    //   272: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   275: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mCallback:Landroid/support/v4/media/MediaBrowserCompat$ConnectionCallback;
                    //   278: invokevirtual   android/support/v4/media/MediaBrowserCompat$ConnectionCallback.onConnectionFailed:()V
                    //   281: goto            112
                    //   284: ldc             "MediaBrowserCompat"
                    //   286: ldc             "connect..."
                    //   288: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
                    //   291: pop            
                    //   292: aload_0        
                    //   293: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase$1.this$0:Landroid/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase;
                    //   296: invokevirtual   android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.dump:()V
                    //   299: goto            118
                    //    Exceptions:
                    //  Try           Handler
                    //  Start  End    Start  End    Type                 
                    //  -----  -----  -----  -----  ---------------------
                    //  86     106    229    264    Ljava/lang/Exception;
                    // 
                    // The error that occurred was:
                    // 
                    // java.lang.IllegalStateException: Expression is linked from several locations: cmpeq:boolean(getfield:MediaServiceConnection(MediaBrowserImplBase::mServiceConnection, getfield:MediaBrowserImplBase(MediaBrowserCompat$MediaBrowserImplBase$1::this$0, this:MediaBrowserCompat$MediaBrowserImplBase$1)), aconstnull:MediaServiceConnection())
                    //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
                    //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
                    //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1151)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:993)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:548)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:534)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:377)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:318)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:213)
                    //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
                    //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
                    //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                    //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                    //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
                    //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
                    //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
                    // 
                    throw new IllegalStateException("An error occurred while decompiling this method.");
                }
            });
        }
        
        @Override
        public void disconnect() {
            this.mState = 0;
            this.mHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (MediaBrowserImplBase.this.mCallbacksMessenger != null) {
                        try {
                            MediaBrowserImplBase.this.mServiceBinderWrapper.disconnect(MediaBrowserImplBase.this.mCallbacksMessenger);
                        }
                        catch (final RemoteException ex) {
                            Log.w("MediaBrowserCompat", "RemoteException during connect for " + MediaBrowserImplBase.this.mServiceComponent);
                        }
                    }
                    final int mState = MediaBrowserImplBase.this.mState;
                    MediaBrowserImplBase.this.forceCloseConnection();
                    if (mState != 0) {
                        MediaBrowserImplBase.this.mState = mState;
                    }
                    if (MediaBrowserCompat.DEBUG) {
                        Log.d("MediaBrowserCompat", "disconnect...");
                        MediaBrowserImplBase.this.dump();
                    }
                }
            });
        }
        
        void dump() {
            Log.d("MediaBrowserCompat", "MediaBrowserCompat...");
            Log.d("MediaBrowserCompat", "  mServiceComponent=" + this.mServiceComponent);
            Log.d("MediaBrowserCompat", "  mCallback=" + this.mCallback);
            Log.d("MediaBrowserCompat", "  mRootHints=" + this.mRootHints);
            Log.d("MediaBrowserCompat", "  mState=" + getStateLabel(this.mState));
            Log.d("MediaBrowserCompat", "  mServiceConnection=" + this.mServiceConnection);
            Log.d("MediaBrowserCompat", "  mServiceBinderWrapper=" + this.mServiceBinderWrapper);
            Log.d("MediaBrowserCompat", "  mCallbacksMessenger=" + this.mCallbacksMessenger);
            Log.d("MediaBrowserCompat", "  mRootId=" + this.mRootId);
            Log.d("MediaBrowserCompat", "  mMediaSessionToken=" + this.mMediaSessionToken);
        }
        
        void forceCloseConnection() {
            if (this.mServiceConnection != null) {
                this.mContext.unbindService((ServiceConnection)this.mServiceConnection);
            }
            this.mState = 1;
            this.mServiceConnection = null;
            this.mServiceBinderWrapper = null;
            this.mCallbacksMessenger = null;
            this.mHandler.setCallbacksMessenger(null);
            this.mRootId = null;
            this.mMediaSessionToken = null;
        }
        
        @Nullable
        @Override
        public Bundle getExtras() {
            if (this.isConnected()) {
                return this.mExtras;
            }
            throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
        }
        
        @Override
        public void getItem(@NonNull final String str, @NonNull final ItemCallback itemCallback) {
            Label_0046: {
                if (TextUtils.isEmpty((CharSequence)str)) {
                    break Label_0046;
                }
                Label_0056: {
                    if (itemCallback == null) {
                        break Label_0056;
                    }
                    Label_0066: {
                        if (!this.isConnected()) {
                            break Label_0066;
                        }
                        final ItemReceiver itemReceiver = new ItemReceiver(str, itemCallback, this.mHandler);
                        try {
                            this.mServiceBinderWrapper.getMediaItem(str, itemReceiver, this.mCallbacksMessenger);
                            return;
                            throw new IllegalArgumentException("mediaId is empty");
                            Log.i("MediaBrowserCompat", "Not connected, unable to retrieve the MediaItem.");
                            this.mHandler.post((Runnable)new Runnable() {
                                @Override
                                public void run() {
                                    itemCallback.onError(str);
                                }
                            });
                            return;
                            throw new IllegalArgumentException("cb is null");
                        }
                        catch (final RemoteException ex) {
                            Log.i("MediaBrowserCompat", "Remote error getting media item: " + str);
                            this.mHandler.post((Runnable)new Runnable() {
                                @Override
                                public void run() {
                                    itemCallback.onError(str);
                                }
                            });
                        }
                    }
                }
            }
        }
        
        @NonNull
        @Override
        public String getRoot() {
            if (this.isConnected()) {
                return this.mRootId;
            }
            throw new IllegalStateException("getRoot() called while not connected(state=" + getStateLabel(this.mState) + ")");
        }
        
        @NonNull
        @Override
        public ComponentName getServiceComponent() {
            if (this.isConnected()) {
                return this.mServiceComponent;
            }
            throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
        }
        
        @NonNull
        @Override
        public MediaSessionCompat.Token getSessionToken() {
            if (this.isConnected()) {
                return this.mMediaSessionToken;
            }
            throw new IllegalStateException("getSessionToken() called while not connected(state=" + this.mState + ")");
        }
        
        @Override
        public boolean isConnected() {
            return this.mState == 3;
        }
        
        @Override
        public void onConnectionFailed(final Messenger messenger) {
            Log.e("MediaBrowserCompat", "onConnectFailed for " + this.mServiceComponent);
            if (!this.isCurrent(messenger, "onConnectFailed")) {
                return;
            }
            if (this.mState == 2) {
                this.forceCloseConnection();
                this.mCallback.onConnectionFailed();
                return;
            }
            Log.w("MediaBrowserCompat", "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
        }
        
        @Override
        public void onLoadChildren(final Messenger messenger, final String s, final List list, final Bundle bundle) {
            if (!this.isCurrent(messenger, "onLoadChildren")) {
                return;
            }
            if (MediaBrowserCompat.DEBUG) {
                Log.d("MediaBrowserCompat", "onLoadChildren for " + this.mServiceComponent + " id=" + s);
            }
            final Subscription subscription = this.mSubscriptions.get(s);
            if (subscription != null) {
                final SubscriptionCallback callback = subscription.getCallback(this.mContext, bundle);
                if (callback != null) {
                    if (bundle != null) {
                        if (list != null) {
                            callback.onChildrenLoaded(s, list, bundle);
                        }
                        else {
                            callback.onError(s, bundle);
                        }
                    }
                    else if (list != null) {
                        callback.onChildrenLoaded(s, list);
                    }
                    else {
                        callback.onError(s);
                    }
                }
                return;
            }
            if (MediaBrowserCompat.DEBUG) {
                Log.d("MediaBrowserCompat", "onLoadChildren for id that isn't subscribed id=" + s);
            }
        }
        
        @Override
        public void onServiceConnected(final Messenger messenger, String mRootId, final MediaSessionCompat.Token mMediaSessionToken, final Bundle mExtras) {
            if (!this.isCurrent(messenger, "onConnect")) {
                return;
            }
            Label_0081: {
                if (this.mState != 2) {
                    break Label_0081;
                }
                this.mRootId = mRootId;
                this.mMediaSessionToken = mMediaSessionToken;
                this.mExtras = mExtras;
                this.mState = 3;
                Label_0120: {
                    if (MediaBrowserCompat.DEBUG) {
                        break Label_0120;
                    }
                    while (true) {
                        this.mCallback.onConnected();
                        try {
                            for (final Map.Entry<String, V> entry : this.mSubscriptions.entrySet()) {
                                mRootId = entry.getKey();
                                final Subscription subscription = (Subscription)entry.getValue();
                                final List<SubscriptionCallback> callbacks = subscription.getCallbacks();
                                final List<Bundle> optionsList = subscription.getOptionsList();
                                for (int i = 0; i < callbacks.size(); ++i) {
                                    this.mServiceBinderWrapper.addSubscription(mRootId, ((SubscriptionCallback)callbacks.get(i)).mToken, optionsList.get(i), this.mCallbacksMessenger);
                                }
                            }
                            return;
                            Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
                            this.dump();
                            continue;
                            Log.w("MediaBrowserCompat", "onConnect from service while mState=" + getStateLabel(this.mState) + "... ignoring");
                        }
                        catch (final RemoteException ex) {
                            Log.d("MediaBrowserCompat", "addSubscription failed with RemoteException.");
                        }
                    }
                }
            }
        }
        
        @Override
        public void search(@NonNull final String str, final Bundle bundle, @NonNull final SearchCallback searchCallback) {
            Label_0039: {
                if (!this.isConnected()) {
                    break Label_0039;
                }
                final SearchResultReceiver searchResultReceiver = new SearchResultReceiver(str, bundle, searchCallback, this.mHandler);
                try {
                    this.mServiceBinderWrapper.search(str, bundle, searchResultReceiver, this.mCallbacksMessenger);
                    return;
                    throw new IllegalStateException("search() called while not connected (state=" + getStateLabel(this.mState) + ")");
                }
                catch (final RemoteException ex) {
                    Log.i("MediaBrowserCompat", "Remote error searching items with query: " + str, (Throwable)ex);
                    this.mHandler.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            searchCallback.onError(str, bundle);
                        }
                    });
                }
            }
        }
        
        @Override
        public void sendCustomAction(@NonNull final String s, final Bundle bundle, @Nullable final CustomActionCallback customActionCallback) {
            Label_0039: {
                if (!this.isConnected()) {
                    break Label_0039;
                }
                final CustomActionResultReceiver customActionResultReceiver = new CustomActionResultReceiver(s, bundle, customActionCallback, this.mHandler);
                try {
                    this.mServiceBinderWrapper.sendCustomAction(s, bundle, customActionResultReceiver, this.mCallbacksMessenger);
                    return;
                    throw new IllegalStateException("Cannot send a custom action (" + s + ") with " + "extras " + bundle + " because the browser is not connected to the " + "service.");
                }
                catch (final RemoteException ex) {
                    Log.i("MediaBrowserCompat", "Remote error sending a custom action: action=" + s + ", extras=" + bundle, (Throwable)ex);
                    if (customActionCallback != null) {
                        this.mHandler.post((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                customActionCallback.onError(s, bundle, null);
                            }
                        });
                    }
                }
            }
        }
        
        @Override
        public void subscribe(@NonNull final String str, Bundle bundle, @NonNull final SubscriptionCallback subscriptionCallback) {
            Subscription subscription = this.mSubscriptions.get(str);
            if (subscription == null) {
                subscription = new Subscription();
                this.mSubscriptions.put(str, subscription);
            }
            if (bundle != null) {
                bundle = new Bundle(bundle);
            }
            else {
                bundle = null;
            }
            subscription.putCallback(this.mContext, bundle, subscriptionCallback);
            if (this.isConnected()) {
                try {
                    this.mServiceBinderWrapper.addSubscription(str, subscriptionCallback.mToken, bundle, this.mCallbacksMessenger);
                }
                catch (final RemoteException ex) {
                    Log.d("MediaBrowserCompat", "addSubscription failed with RemoteException parentId=" + str);
                }
            }
        }
        
        @Override
        public void unsubscribe(@NonNull final String p0, final SubscriptionCallback p1) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     1: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mSubscriptions:Landroid/support/v4/util/ArrayMap;
            //     4: aload_1        
            //     5: invokevirtual   android/support/v4/util/ArrayMap.get:(Ljava/lang/Object;)Ljava/lang/Object;
            //     8: checkcast       Landroid/support/v4/media/MediaBrowserCompat$Subscription;
            //    11: astore_3       
            //    12: aload_3        
            //    13: ifnull          69
            //    16: aload_2        
            //    17: ifnull          70
            //    20: aload_3        
            //    21: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getCallbacks:()Ljava/util/List;
            //    24: astore          4
            //    26: aload_3        
            //    27: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.getOptionsList:()Ljava/util/List;
            //    30: astore          5
            //    32: aload           4
            //    34: invokeinterface java/util/List.size:()I
            //    39: istore          6
            //    41: iload           6
            //    43: iconst_1       
            //    44: isub           
            //    45: istore          7
            //    47: iload           7
            //    49: ifge            124
            //    52: aload_3        
            //    53: invokevirtual   android/support/v4/media/MediaBrowserCompat$Subscription.isEmpty:()Z
            //    56: ifeq            194
            //    59: aload_0        
            //    60: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mSubscriptions:Landroid/support/v4/util/ArrayMap;
            //    63: aload_1        
            //    64: invokevirtual   android/support/v4/util/ArrayMap.remove:(Ljava/lang/Object;)Ljava/lang/Object;
            //    67: pop            
            //    68: return         
            //    69: return         
            //    70: aload_0        
            //    71: invokevirtual   android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.isConnected:()Z
            //    74: ifeq            52
            //    77: aload_0        
            //    78: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
            //    81: aload_1        
            //    82: aconst_null    
            //    83: aload_0        
            //    84: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mCallbacksMessenger:Landroid/os/Messenger;
            //    87: invokevirtual   android/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper.removeSubscription:(Ljava/lang/String;Landroid/os/IBinder;Landroid/os/Messenger;)V
            //    90: goto            52
            //    93: astore          5
            //    95: ldc             "MediaBrowserCompat"
            //    97: new             Ljava/lang/StringBuilder;
            //   100: dup            
            //   101: invokespecial   java/lang/StringBuilder.<init>:()V
            //   104: ldc_w           "removeSubscription failed with RemoteException parentId="
            //   107: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   110: aload_1        
            //   111: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   114: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   117: invokestatic    android/util/Log.d:(Ljava/lang/String;Ljava/lang/String;)I
            //   120: pop            
            //   121: goto            52
            //   124: iload           7
            //   126: istore          6
            //   128: aload           4
            //   130: iload           7
            //   132: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
            //   137: aload_2        
            //   138: if_acmpne       41
            //   141: aload_0        
            //   142: invokevirtual   android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.isConnected:()Z
            //   145: ifne            175
            //   148: aload           4
            //   150: iload           7
            //   152: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   157: pop            
            //   158: aload           5
            //   160: iload           7
            //   162: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //   167: pop            
            //   168: iload           7
            //   170: istore          6
            //   172: goto            41
            //   175: aload_0        
            //   176: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mServiceBinderWrapper:Landroid/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper;
            //   179: aload_1        
            //   180: aload_2        
            //   181: invokestatic    android/support/v4/media/MediaBrowserCompat$SubscriptionCallback.access$000:(Landroid/support/v4/media/MediaBrowserCompat$SubscriptionCallback;)Landroid/os/IBinder;
            //   184: aload_0        
            //   185: getfield        android/support/v4/media/MediaBrowserCompat$MediaBrowserImplBase.mCallbacksMessenger:Landroid/os/Messenger;
            //   188: invokevirtual   android/support/v4/media/MediaBrowserCompat$ServiceBinderWrapper.removeSubscription:(Ljava/lang/String;Landroid/os/IBinder;Landroid/os/Messenger;)V
            //   191: goto            148
            //   194: aload_2        
            //   195: ifnull          59
            //   198: goto            68
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                        
            //  -----  -----  -----  -----  ----------------------------
            //  20     41     93     124    Landroid/os/RemoteException;
            //  70     90     93     124    Landroid/os/RemoteException;
            //  128    148    93     124    Landroid/os/RemoteException;
            //  148    168    93     124    Landroid/os/RemoteException;
            //  175    191    93     124    Landroid/os/RemoteException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: cmpne:boolean(p1:SubscriptionCallback, aconstnull:SubscriptionCallback())
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.GotoRemoval.traverseGraph(GotoRemoval.java:88)
            //     at com.strobel.decompiler.ast.GotoRemoval.removeGotos(GotoRemoval.java:52)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:276)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        private class MediaServiceConnection implements ServiceConnection
        {
            MediaServiceConnection() {
            }
            
            private void postOrRun(final Runnable runnable) {
                if (Thread.currentThread() != MediaBrowserImplBase.this.mHandler.getLooper().getThread()) {
                    MediaBrowserImplBase.this.mHandler.post(runnable);
                }
                else {
                    runnable.run();
                }
            }
            
            boolean isCurrent(final String str) {
                if (MediaBrowserImplBase.this.mServiceConnection == this && MediaBrowserImplBase.this.mState != 0 && MediaBrowserImplBase.this.mState != 1) {
                    return true;
                }
                if (MediaBrowserImplBase.this.mState != 0 && MediaBrowserImplBase.this.mState != 1) {
                    Log.i("MediaBrowserCompat", str + " for " + MediaBrowserImplBase.this.mServiceComponent + " with mServiceConnection=" + MediaBrowserImplBase.this.mServiceConnection + " this=" + this);
                }
                return false;
            }
            
            public void onServiceConnected(final ComponentName componentName, final IBinder binder) {
                this.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        Label_0150: {
                            if (MediaBrowserCompat.DEBUG) {
                                break Label_0150;
                            }
                            while (true) {
                                if (!MediaServiceConnection.this.isCurrent("onServiceConnected")) {
                                    return;
                                }
                                MediaBrowserImplBase.this.mServiceBinderWrapper = new ServiceBinderWrapper(binder, MediaBrowserImplBase.this.mRootHints);
                                MediaBrowserImplBase.this.mCallbacksMessenger = new Messenger((Handler)MediaBrowserImplBase.this.mHandler);
                                MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(MediaBrowserImplBase.this.mCallbacksMessenger);
                                MediaBrowserImplBase.this.mState = 2;
                                try {
                                    if (MediaBrowserCompat.DEBUG) {
                                        Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
                                        MediaBrowserImplBase.this.dump();
                                    }
                                    MediaBrowserImplBase.this.mServiceBinderWrapper.connect(MediaBrowserImplBase.this.mContext, MediaBrowserImplBase.this.mCallbacksMessenger);
                                    return;
                                    Log.d("MediaBrowserCompat", "MediaServiceConnection.onServiceConnected name=" + componentName + " binder=" + binder);
                                    MediaBrowserImplBase.this.dump();
                                    continue;
                                }
                                catch (final RemoteException ex) {
                                    Log.w("MediaBrowserCompat", "RemoteException during connect for " + MediaBrowserImplBase.this.mServiceComponent);
                                    if (MediaBrowserCompat.DEBUG) {
                                        Log.d("MediaBrowserCompat", "ServiceCallbacks.onConnect...");
                                        MediaBrowserImplBase.this.dump();
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
            }
            
            public void onServiceDisconnected(final ComponentName componentName) {
                this.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        if (MediaBrowserCompat.DEBUG) {
                            Log.d("MediaBrowserCompat", "MediaServiceConnection.onServiceDisconnected name=" + componentName + " this=" + this + " mServiceConnection=" + MediaBrowserImplBase.this.mServiceConnection);
                            MediaBrowserImplBase.this.dump();
                        }
                        if (MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
                            MediaBrowserImplBase.this.mServiceBinderWrapper = null;
                            MediaBrowserImplBase.this.mCallbacksMessenger = null;
                            MediaBrowserImplBase.this.mHandler.setCallbacksMessenger(null);
                            MediaBrowserImplBase.this.mState = 4;
                            MediaBrowserImplBase.this.mCallback.onConnectionSuspended();
                        }
                    }
                });
            }
        }
    }
    
    interface MediaBrowserServiceCallbackImpl
    {
        void onConnectionFailed(final Messenger p0);
        
        void onLoadChildren(final Messenger p0, final String p1, final List p2, final Bundle p3);
        
        void onServiceConnected(final Messenger p0, final String p1, final MediaSessionCompat.Token p2, final Bundle p3);
    }
    
    public static class MediaItem implements Parcelable
    {
        public static final Parcelable$Creator<MediaItem> CREATOR;
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescriptionCompat mDescription;
        private final int mFlags;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<MediaItem>() {
                public MediaItem createFromParcel(final Parcel parcel) {
                    return new MediaItem(parcel);
                }
                
                public MediaItem[] newArray(final int n) {
                    return new MediaItem[n];
                }
            };
        }
        
        MediaItem(final Parcel parcel) {
            this.mFlags = parcel.readInt();
            this.mDescription = (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
        }
        
        public MediaItem(@NonNull final MediaDescriptionCompat mDescription, final int mFlags) {
            if (mDescription == null) {
                throw new IllegalArgumentException("description cannot be null");
            }
            if (!TextUtils.isEmpty((CharSequence)mDescription.getMediaId())) {
                this.mFlags = mFlags;
                this.mDescription = mDescription;
                return;
            }
            throw new IllegalArgumentException("description must have a non-empty media id");
        }
        
        public static MediaItem fromMediaItem(final Object o) {
            if (o != null && Build$VERSION.SDK_INT >= 21) {
                return new MediaItem(MediaDescriptionCompat.fromMediaDescription(MediaBrowserCompatApi21.MediaItem.getDescription(o)), MediaBrowserCompatApi21.MediaItem.getFlags(o));
            }
            return null;
        }
        
        public static List<MediaItem> fromMediaItemList(final List<?> list) {
            if (list != null && Build$VERSION.SDK_INT >= 21) {
                final ArrayList list2 = new ArrayList(list.size());
                final Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    list2.add(fromMediaItem(iterator.next()));
                }
                return list2;
            }
            return null;
        }
        
        public int describeContents() {
            return 0;
        }
        
        @NonNull
        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }
        
        public int getFlags() {
            return this.mFlags;
        }
        
        @Nullable
        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
        
        public boolean isBrowsable() {
            boolean b = false;
            if ((this.mFlags & 0x1) != 0x0) {
                b = true;
            }
            return b;
        }
        
        public boolean isPlayable() {
            boolean b = false;
            if ((this.mFlags & 0x2) != 0x0) {
                b = true;
            }
            return b;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=").append(this.mFlags);
            sb.append(", mDescription=").append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.mFlags);
            this.mDescription.writeToParcel(parcel, n);
        }
        
        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
        public @interface Flags {
        }
    }
    
    public abstract static class SearchCallback
    {
        public void onError(@NonNull final String s, final Bundle bundle) {
        }
        
        public void onSearchResult(@NonNull final String s, final Bundle bundle, @NonNull final List<MediaItem> list) {
        }
    }
    
    private static class SearchResultReceiver extends ResultReceiver
    {
        private final SearchCallback mCallback;
        private final Bundle mExtras;
        private final String mQuery;
        
        SearchResultReceiver(final String mQuery, final Bundle mExtras, final SearchCallback mCallback, final Handler handler) {
            super(handler);
            this.mQuery = mQuery;
            this.mExtras = mExtras;
            this.mCallback = mCallback;
        }
        
        @Override
        protected void onReceiveResult(int i, final Bundle bundle) {
            final int n = 0;
            final List list = null;
            if (bundle != null) {
                bundle.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            }
            if (i == 0 && bundle != null && bundle.containsKey("search_results")) {
                final Parcelable[] parcelableArray = bundle.getParcelableArray("search_results");
                List list2;
                if (parcelableArray == null) {
                    list2 = list;
                }
                else {
                    list2 = new ArrayList();
                    int length;
                    for (length = parcelableArray.length, i = n; i < length; ++i) {
                        list2.add(parcelableArray[i]);
                    }
                }
                this.mCallback.onSearchResult(this.mQuery, this.mExtras, list2);
                return;
            }
            this.mCallback.onError(this.mQuery, this.mExtras);
        }
    }
    
    private static class ServiceBinderWrapper
    {
        private Messenger mMessenger;
        private Bundle mRootHints;
        
        public ServiceBinderWrapper(final IBinder binder, final Bundle mRootHints) {
            this.mMessenger = new Messenger(binder);
            this.mRootHints = mRootHints;
        }
        
        private void sendRequest(final int what, final Bundle data, final Messenger replyTo) throws RemoteException {
            final Message obtain = Message.obtain();
            obtain.what = what;
            obtain.arg1 = 1;
            obtain.setData(data);
            obtain.replyTo = replyTo;
            this.mMessenger.send(obtain);
        }
        
        void addSubscription(final String s, final IBinder binder, final Bundle bundle, final Messenger messenger) throws RemoteException {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("data_media_item_id", s);
            BundleCompat.putBinder(bundle2, "data_callback_token", binder);
            bundle2.putBundle("data_options", bundle);
            this.sendRequest(3, bundle2, messenger);
        }
        
        void connect(final Context context, final Messenger messenger) throws RemoteException {
            final Bundle bundle = new Bundle();
            bundle.putString("data_package_name", context.getPackageName());
            bundle.putBundle("data_root_hints", this.mRootHints);
            this.sendRequest(1, bundle, messenger);
        }
        
        void disconnect(final Messenger messenger) throws RemoteException {
            this.sendRequest(2, null, messenger);
        }
        
        void getMediaItem(final String s, final ResultReceiver resultReceiver, final Messenger messenger) throws RemoteException {
            final Bundle bundle = new Bundle();
            bundle.putString("data_media_item_id", s);
            bundle.putParcelable("data_result_receiver", (Parcelable)resultReceiver);
            this.sendRequest(5, bundle, messenger);
        }
        
        void registerCallbackMessenger(final Messenger messenger) throws RemoteException {
            final Bundle bundle = new Bundle();
            bundle.putBundle("data_root_hints", this.mRootHints);
            this.sendRequest(6, bundle, messenger);
        }
        
        void removeSubscription(final String s, final IBinder binder, final Messenger messenger) throws RemoteException {
            final Bundle bundle = new Bundle();
            bundle.putString("data_media_item_id", s);
            BundleCompat.putBinder(bundle, "data_callback_token", binder);
            this.sendRequest(4, bundle, messenger);
        }
        
        void search(final String s, final Bundle bundle, final ResultReceiver resultReceiver, final Messenger messenger) throws RemoteException {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("data_search_query", s);
            bundle2.putBundle("data_search_extras", bundle);
            bundle2.putParcelable("data_result_receiver", (Parcelable)resultReceiver);
            this.sendRequest(8, bundle2, messenger);
        }
        
        void sendCustomAction(final String s, final Bundle bundle, final ResultReceiver resultReceiver, final Messenger messenger) throws RemoteException {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("data_custom_action", s);
            bundle2.putBundle("data_custom_action_extras", bundle);
            bundle2.putParcelable("data_result_receiver", (Parcelable)resultReceiver);
            this.sendRequest(9, bundle2, messenger);
        }
        
        void unregisterCallbackMessenger(final Messenger messenger) throws RemoteException {
            this.sendRequest(7, null, messenger);
        }
    }
    
    private static class Subscription
    {
        private final List<SubscriptionCallback> mCallbacks;
        private final List<Bundle> mOptionsList;
        
        public Subscription() {
            this.mCallbacks = new ArrayList<SubscriptionCallback>();
            this.mOptionsList = new ArrayList<Bundle>();
        }
        
        public SubscriptionCallback getCallback(final Context context, final Bundle bundle) {
            if (bundle != null) {
                bundle.setClassLoader(context.getClassLoader());
            }
            for (int i = 0; i < this.mOptionsList.size(); ++i) {
                if (MediaBrowserCompatUtils.areSameOptions(this.mOptionsList.get(i), bundle)) {
                    return this.mCallbacks.get(i);
                }
            }
            return null;
        }
        
        public List<SubscriptionCallback> getCallbacks() {
            return this.mCallbacks;
        }
        
        public List<Bundle> getOptionsList() {
            return this.mOptionsList;
        }
        
        public boolean isEmpty() {
            return this.mCallbacks.isEmpty();
        }
        
        public void putCallback(final Context context, final Bundle bundle, final SubscriptionCallback subscriptionCallback) {
            if (bundle != null) {
                bundle.setClassLoader(context.getClassLoader());
            }
            for (int i = 0; i < this.mOptionsList.size(); ++i) {
                if (MediaBrowserCompatUtils.areSameOptions(this.mOptionsList.get(i), bundle)) {
                    this.mCallbacks.set(i, subscriptionCallback);
                    return;
                }
            }
            this.mCallbacks.add(subscriptionCallback);
            this.mOptionsList.add(bundle);
        }
    }
    
    public abstract static class SubscriptionCallback
    {
        private final Object mSubscriptionCallbackObj;
        WeakReference<Subscription> mSubscriptionRef;
        private final IBinder mToken;
        
        public SubscriptionCallback() {
            if (Build$VERSION.SDK_INT < 26) {
                if (Build$VERSION.SDK_INT < 21) {
                    this.mSubscriptionCallbackObj = null;
                    this.mToken = (IBinder)new Binder();
                }
                else {
                    this.mSubscriptionCallbackObj = MediaBrowserCompatApi21.createSubscriptionCallback((MediaBrowserCompatApi21.SubscriptionCallback)new StubApi21());
                    this.mToken = (IBinder)new Binder();
                }
            }
            else {
                this.mSubscriptionCallbackObj = MediaBrowserCompatApi24.createSubscriptionCallback((MediaBrowserCompatApi24.SubscriptionCallback)new StubApi24());
                this.mToken = null;
            }
        }
        
        private void setSubscription(final Subscription referent) {
            this.mSubscriptionRef = new WeakReference<Subscription>(referent);
        }
        
        public void onChildrenLoaded(@NonNull final String s, @NonNull final List<MediaItem> list) {
        }
        
        public void onChildrenLoaded(@NonNull final String s, @NonNull final List<MediaItem> list, @NonNull final Bundle bundle) {
        }
        
        public void onError(@NonNull final String s) {
        }
        
        public void onError(@NonNull final String s, @NonNull final Bundle bundle) {
        }
        
        private class StubApi21 implements MediaBrowserCompatApi21.SubscriptionCallback
        {
            StubApi21() {
            }
            
            List<MediaBrowserCompat.MediaItem> applyOptions(final List<MediaBrowserCompat.MediaItem> list, final Bundle bundle) {
                if (list == null) {
                    return null;
                }
                final int int1 = bundle.getInt("android.media.browse.extra.PAGE", -1);
                final int int2 = bundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
                if (int1 == -1 && int2 == -1) {
                    return list;
                }
                final int n = int2 * int1;
                int size = n + int2;
                if (int1 >= 0 && int2 >= 1 && n < list.size()) {
                    if (size > list.size()) {
                        size = list.size();
                    }
                    return list.subList(n, size);
                }
                return Collections.EMPTY_LIST;
            }
            
            @Override
            public void onChildrenLoaded(@NonNull final String s, final List<?> list) {
                Object o = null;
                if (MediaBrowserCompat.SubscriptionCallback.this.mSubscriptionRef != null) {
                    o = MediaBrowserCompat.SubscriptionCallback.this.mSubscriptionRef.get();
                }
                if (o != null) {
                    final List<MediaBrowserCompat.MediaItem> fromMediaItemList = MediaBrowserCompat.MediaItem.fromMediaItemList(list);
                    final List<MediaBrowserCompat.SubscriptionCallback> callbacks = ((Subscription)o).getCallbacks();
                    final List<Bundle> optionsList = ((Subscription)o).getOptionsList();
                    for (int i = 0; i < callbacks.size(); ++i) {
                        final Bundle bundle = optionsList.get(i);
                        if (bundle != null) {
                            MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(s, this.applyOptions(fromMediaItemList, bundle), bundle);
                        }
                        else {
                            MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(s, fromMediaItemList);
                        }
                    }
                }
                else {
                    MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(s, MediaBrowserCompat.MediaItem.fromMediaItemList(list));
                }
            }
            
            @Override
            public void onError(@NonNull final String s) {
                MediaBrowserCompat.SubscriptionCallback.this.onError(s);
            }
        }
        
        private class StubApi24 extends StubApi21 implements MediaBrowserCompatApi24.SubscriptionCallback
        {
            StubApi24() {
            }
            
            @Override
            public void onChildrenLoaded(@NonNull final String s, final List<?> list, @NonNull final Bundle bundle) {
                MediaBrowserCompat.SubscriptionCallback.this.onChildrenLoaded(s, MediaBrowserCompat.MediaItem.fromMediaItemList(list), bundle);
            }
            
            @Override
            public void onError(@NonNull final String s, @NonNull final Bundle bundle) {
                MediaBrowserCompat.SubscriptionCallback.this.onError(s, bundle);
            }
        }
    }
}
