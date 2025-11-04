// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.media;

import android.os.Binder;
import java.util.Collection;
import android.os.Message;
import android.text.TextUtils;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import android.os.Parcel;
import android.support.v4.media.session.IMediaSession;
import android.support.v4.app.BundleCompat;
import android.os.Handler;
import android.content.Context;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import java.util.HashMap;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.os.ResultReceiver;
import android.os.Build$VERSION;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.Collections;
import java.util.Iterator;
import android.support.v4.util.Pair;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.IBinder;
import android.support.v4.util.ArrayMap;
import android.support.annotation.RestrictTo;
import android.app.Service;

public abstract class MediaBrowserServiceCompat extends Service
{
    static final boolean DEBUG;
    private static final float EPSILON = 1.0E-5f;
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final String KEY_MEDIA_ITEM = "media_item";
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    public static final String KEY_SEARCH_RESULTS = "search_results";
    static final int RESULT_ERROR = -1;
    static final int RESULT_FLAG_ON_LOAD_ITEM_NOT_IMPLEMENTED = 2;
    static final int RESULT_FLAG_ON_SEARCH_NOT_IMPLEMENTED = 4;
    static final int RESULT_FLAG_OPTION_NOT_HANDLED = 1;
    static final int RESULT_OK = 0;
    static final int RESULT_PROGRESS_UPDATE = 1;
    public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService";
    static final String TAG = "MBServiceCompat";
    final ArrayMap<IBinder, ConnectionRecord> mConnections;
    ConnectionRecord mCurConnection;
    final ServiceHandler mHandler;
    private MediaBrowserServiceImpl mImpl;
    MediaSessionCompat.Token mSession;
    
    static {
        DEBUG = Log.isLoggable("MBServiceCompat", 3);
    }
    
    public MediaBrowserServiceCompat() {
        this.mConnections = new ArrayMap<IBinder, ConnectionRecord>();
        this.mHandler = new ServiceHandler();
    }
    
    void addSubscription(final String s, final ConnectionRecord connectionRecord, final IBinder binder, final Bundle bundle) {
        List value = connectionRecord.subscriptions.get(s);
        if (value == null) {
            value = new ArrayList();
        }
        for (final Pair pair : value) {
            if (binder == pair.first && MediaBrowserCompatUtils.areSameOptions(bundle, (Bundle)pair.second)) {
                return;
            }
        }
        value.add(new Pair(binder, bundle));
        connectionRecord.subscriptions.put(s, value);
        this.performLoadChildren(s, connectionRecord, bundle);
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
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
    }
    
    public final Bundle getBrowserRootHints() {
        return this.mImpl.getBrowserRootHints();
    }
    
    @Nullable
    public MediaSessionCompat.Token getSessionToken() {
        return this.mSession;
    }
    
    boolean isValidPackage(final String anObject, int i) {
        if (anObject != null) {
            final String[] packagesForUid = this.getPackageManager().getPackagesForUid(i);
            int length;
            for (length = packagesForUid.length, i = 0; i < length; ++i) {
                if (packagesForUid[i].equals(anObject)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public void notifyChildrenChanged(@NonNull final String s) {
        if (s != null) {
            this.mImpl.notifyChildrenChanged(s, null);
            return;
        }
        throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
    }
    
    public void notifyChildrenChanged(@NonNull final String s, @NonNull final Bundle bundle) {
        if (s == null) {
            throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
        }
        if (bundle != null) {
            this.mImpl.notifyChildrenChanged(s, bundle);
            return;
        }
        throw new IllegalArgumentException("options cannot be null in notifyChildrenChanged");
    }
    
    public IBinder onBind(final Intent intent) {
        return this.mImpl.onBind(intent);
    }
    
    public void onCreate() {
        super.onCreate();
        if (Build$VERSION.SDK_INT < 26) {
            if (Build$VERSION.SDK_INT < 23) {
                if (Build$VERSION.SDK_INT < 21) {
                    this.mImpl = (MediaBrowserServiceImpl)new MediaBrowserServiceImplBase();
                }
                else {
                    this.mImpl = (MediaBrowserServiceImpl)new MediaBrowserServiceImplApi21();
                }
            }
            else {
                this.mImpl = (MediaBrowserServiceImpl)new MediaBrowserServiceImplApi23();
            }
        }
        else {
            this.mImpl = (MediaBrowserServiceImpl)new MediaBrowserServiceImplApi24();
        }
        this.mImpl.onCreate();
    }
    
    public void onCustomAction(@NonNull final String s, final Bundle bundle, @NonNull final Result<Bundle> result) {
        result.sendError(null);
    }
    
    @Nullable
    public abstract BrowserRoot onGetRoot(@NonNull final String p0, final int p1, @Nullable final Bundle p2);
    
    public abstract void onLoadChildren(@NonNull final String p0, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> p1);
    
    public void onLoadChildren(@NonNull final String s, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result, @NonNull final Bundle bundle) {
        result.setFlags(1);
        this.onLoadChildren(s, result);
    }
    
    public void onLoadItem(final String s, @NonNull final Result<MediaBrowserCompat.MediaItem> result) {
        result.setFlags(2);
        result.sendResult(null);
    }
    
    public void onSearch(@NonNull final String s, final Bundle bundle, @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.setFlags(4);
        result.sendResult(null);
    }
    
    void performCustomAction(final String str, final Bundle obj, final ConnectionRecord mCurConnection, final ResultReceiver resultReceiver) {
        final Result<Bundle> result = new Result<Bundle>(str) {
            @Override
            void onErrorSent(final Bundle bundle) {
                resultReceiver.send(-1, bundle);
            }
            
            @Override
            void onProgressUpdateSent(final Bundle bundle) {
                resultReceiver.send(1, bundle);
            }
            
            void onResultSent(final Bundle bundle) {
                resultReceiver.send(0, bundle);
            }
        };
        this.mCurConnection = mCurConnection;
        this.onCustomAction(str, obj, (Result<Bundle>)result);
        this.mCurConnection = null;
        if (((Result)result).isDone()) {
            return;
        }
        throw new IllegalStateException("onCustomAction must call detach() or sendResult() or sendError() before returning for action=" + str + " extras=" + obj);
    }
    
    void performLoadChildren(final String str, final ConnectionRecord mCurConnection, final Bundle bundle) {
        final Result<List<MediaBrowserCompat.MediaItem>> result = new Result<List<MediaBrowserCompat.MediaItem>>(str) {
            void onResultSent(List<MediaBrowserCompat.MediaItem> applyOptions) {
                Label_0060: {
                    if (MediaBrowserServiceCompat.this.mConnections.get(mCurConnection.callbacks.asBinder()) != mCurConnection) {
                        break Label_0060;
                    }
                    Label_0113: {
                        if ((((Result)this).getFlags() & 0x1) != 0x0) {
                            break Label_0113;
                        }
                        try {
                            while (true) {
                                mCurConnection.callbacks.onLoadChildren(str, applyOptions, bundle);
                                return;
                                applyOptions = MediaBrowserServiceCompat.this.applyOptions(applyOptions, bundle);
                                continue;
                            }
                            iftrue(Label_0067:)(MediaBrowserServiceCompat.DEBUG);
                            return;
                            Label_0067: {
                                Log.d("MBServiceCompat", "Not sending onLoadChildren result for connection that has been disconnected. pkg=" + mCurConnection.pkg + " id=" + str);
                            }
                        }
                        catch (final RemoteException ex) {
                            Log.w("MBServiceCompat", "Calling onLoadChildren() failed for id=" + str + " package=" + mCurConnection.pkg);
                        }
                    }
                }
            }
        };
        this.mCurConnection = mCurConnection;
        if (bundle != null) {
            this.onLoadChildren(str, (Result<List<MediaBrowserCompat.MediaItem>>)result, bundle);
        }
        else {
            this.onLoadChildren(str, (Result<List<MediaBrowserCompat.MediaItem>>)result);
        }
        this.mCurConnection = null;
        if (((Result)result).isDone()) {
            return;
        }
        throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + mCurConnection.pkg + " id=" + str);
    }
    
    void performLoadItem(final String str, final ConnectionRecord mCurConnection, final ResultReceiver resultReceiver) {
        final Result<MediaBrowserCompat.MediaItem> result = new Result<MediaBrowserCompat.MediaItem>(str) {
            void onResultSent(final MediaBrowserCompat.MediaItem mediaItem) {
                if ((((Result)this).getFlags() & 0x2) == 0x0) {
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable("media_item", (Parcelable)mediaItem);
                    resultReceiver.send(0, bundle);
                    return;
                }
                resultReceiver.send(-1, null);
            }
        };
        this.mCurConnection = mCurConnection;
        this.onLoadItem(str, (Result<MediaBrowserCompat.MediaItem>)result);
        this.mCurConnection = null;
        if (((Result)result).isDone()) {
            return;
        }
        throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + str);
    }
    
    void performSearch(final String str, final Bundle bundle, final ConnectionRecord mCurConnection, final ResultReceiver resultReceiver) {
        final Result<List<MediaBrowserCompat.MediaItem>> result = new Result<List<MediaBrowserCompat.MediaItem>>(str) {
            void onResultSent(final List<MediaBrowserCompat.MediaItem> list) {
                if ((((Result)this).getFlags() & 0x4) == 0x0 && list != null) {
                    final Bundle bundle = new Bundle();
                    bundle.putParcelableArray("search_results", (Parcelable[])list.toArray((Parcelable[])new MediaBrowserCompat.MediaItem[0]));
                    resultReceiver.send(0, bundle);
                    return;
                }
                resultReceiver.send(-1, null);
            }
        };
        this.mCurConnection = mCurConnection;
        this.onSearch(str, bundle, (Result<List<MediaBrowserCompat.MediaItem>>)result);
        this.mCurConnection = null;
        if (((Result)result).isDone()) {
            return;
        }
        throw new IllegalStateException("onSearch must call detach() or sendResult() before returning for query=" + str);
    }
    
    boolean removeSubscription(final String key, final ConnectionRecord connectionRecord, final IBinder binder) {
        if (binder != null) {
            final List list = connectionRecord.subscriptions.get(key);
            boolean b;
            if (list == null) {
                b = false;
            }
            else {
                final Iterator iterator = list.iterator();
                boolean b2 = false;
                while (iterator.hasNext()) {
                    if (binder == ((Pair)iterator.next()).first) {
                        iterator.remove();
                        b2 = true;
                    }
                }
                b = b2;
                if (list.size() == 0) {
                    connectionRecord.subscriptions.remove(key);
                    b = b2;
                }
            }
            return b;
        }
        return connectionRecord.subscriptions.remove(key) != null;
    }
    
    public void setSessionToken(final MediaSessionCompat.Token token) {
        if (token == null) {
            throw new IllegalArgumentException("Session token may not be null.");
        }
        if (this.mSession == null) {
            this.mSession = token;
            this.mImpl.setSessionToken(token);
            return;
        }
        throw new IllegalStateException("The session token has already been set.");
    }
    
    public static final class BrowserRoot
    {
        public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
        public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
        public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
        @Deprecated
        public static final String EXTRA_SUGGESTION_KEYWORDS = "android.service.media.extra.SUGGESTION_KEYWORDS";
        private final Bundle mExtras;
        private final String mRootId;
        
        public BrowserRoot(@NonNull final String mRootId, @Nullable final Bundle mExtras) {
            if (mRootId != null) {
                this.mRootId = mRootId;
                this.mExtras = mExtras;
                return;
            }
            throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
        }
        
        public Bundle getExtras() {
            return this.mExtras;
        }
        
        public String getRootId() {
            return this.mRootId;
        }
    }
    
    private static class ConnectionRecord
    {
        ServiceCallbacks callbacks;
        String pkg;
        BrowserRoot root;
        Bundle rootHints;
        HashMap<String, List<Pair<IBinder, Bundle>>> subscriptions;
        
        ConnectionRecord() {
            this.subscriptions = new HashMap<String, List<Pair<IBinder, Bundle>>>();
        }
    }
    
    interface MediaBrowserServiceImpl
    {
        Bundle getBrowserRootHints();
        
        void notifyChildrenChanged(final String p0, final Bundle p1);
        
        IBinder onBind(final Intent p0);
        
        void onCreate();
        
        void setSessionToken(final MediaSessionCompat.Token p0);
    }
    
    @RequiresApi(21)
    class MediaBrowserServiceImplApi21 implements MediaBrowserServiceImpl, ServiceCompatProxy
    {
        Messenger mMessenger;
        final List<Bundle> mRootExtrasList;
        Object mServiceObj;
        
        MediaBrowserServiceImplApi21() {
            this.mRootExtrasList = new ArrayList<Bundle>();
        }
        
        @Override
        public Bundle getBrowserRootHints() {
            Bundle bundle = null;
            if (this.mMessenger == null) {
                return null;
            }
            if (MediaBrowserServiceCompat.this.mCurConnection != null) {
                if (MediaBrowserServiceCompat.this.mCurConnection.rootHints != null) {
                    bundle = new Bundle(MediaBrowserServiceCompat.this.mCurConnection.rootHints);
                }
                return bundle;
            }
            throw new IllegalStateException("This should be called inside of onLoadChildren, onLoadItem or onSearch methods");
        }
        
        @Override
        public void notifyChildrenChanged(final String s, final Bundle bundle) {
            if (this.mMessenger != null) {
                MediaBrowserServiceCompat.this.mHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Iterator<IBinder> iterator = MediaBrowserServiceCompat.this.mConnections.keySet().iterator();
                        while (iterator.hasNext()) {
                            final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(iterator.next());
                            final List list = connectionRecord.subscriptions.get(s);
                            if (list != null) {
                                for (final Pair pair : list) {
                                    if (MediaBrowserCompatUtils.hasDuplicatedItems(bundle, (Bundle)pair.second)) {
                                        MediaBrowserServiceCompat.this.performLoadChildren(s, connectionRecord, (Bundle)pair.second);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            else {
                MediaBrowserServiceCompatApi21.notifyChildrenChanged(this.mServiceObj, s);
            }
        }
        
        @Override
        public IBinder onBind(final Intent intent) {
            return MediaBrowserServiceCompatApi21.onBind(this.mServiceObj, intent);
        }
        
        @Override
        public void onCreate() {
            MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj = MediaBrowserServiceCompatApi21.createService((Context)MediaBrowserServiceCompat.this, (MediaBrowserServiceCompatApi21.ServiceCompatProxy)this));
        }
        
        @Override
        public MediaBrowserServiceCompatApi21.BrowserRoot onGetRoot(final String s, final int n, final Bundle bundle) {
            Bundle extras;
            if (bundle != null && bundle.getInt("extra_client_version", 0) != 0) {
                bundle.remove("extra_client_version");
                this.mMessenger = new Messenger((Handler)MediaBrowserServiceCompat.this.mHandler);
                extras = new Bundle();
                extras.putInt("extra_service_version", 1);
                BundleCompat.putBinder(extras, "extra_messenger", this.mMessenger.getBinder());
                if (MediaBrowserServiceCompat.this.mSession == null) {
                    this.mRootExtrasList.add(extras);
                }
                else {
                    final IMediaSession extraBinder = MediaBrowserServiceCompat.this.mSession.getExtraBinder();
                    IBinder binder;
                    if (extraBinder != null) {
                        binder = extraBinder.asBinder();
                    }
                    else {
                        binder = null;
                    }
                    BundleCompat.putBinder(extras, "extra_session_binder", binder);
                }
            }
            else {
                extras = null;
            }
            final MediaBrowserServiceCompat.BrowserRoot onGetRoot = MediaBrowserServiceCompat.this.onGetRoot(s, n, bundle);
            if (onGetRoot != null) {
                if (extras != null) {
                    if (onGetRoot.getExtras() != null) {
                        extras.putAll(onGetRoot.getExtras());
                    }
                }
                else {
                    extras = onGetRoot.getExtras();
                }
                return new MediaBrowserServiceCompatApi21.BrowserRoot(onGetRoot.getRootId(), extras);
            }
            return null;
        }
        
        @Override
        public void onLoadChildren(final String s, final ResultWrapper<List<Parcel>> resultWrapper) {
            MediaBrowserServiceCompat.this.onLoadChildren(s, (Result<List<MediaBrowserCompat.MediaItem>>)new Result<List<MediaBrowserCompat.MediaItem>>(s) {
                @Override
                public void detach() {
                    resultWrapper.detach();
                }
                
                void onResultSent(final List<MediaBrowserCompat.MediaItem> list) {
                    final ArrayList list2 = null;
                    ArrayList list3;
                    if (list == null) {
                        list3 = list2;
                    }
                    else {
                        final ArrayList list4 = new ArrayList();
                        for (final MediaBrowserCompat.MediaItem mediaItem : list) {
                            final Parcel obtain = Parcel.obtain();
                            mediaItem.writeToParcel(obtain, 0);
                            list4.add(obtain);
                        }
                        list3 = list4;
                    }
                    resultWrapper.sendResult(list3);
                }
            });
        }
        
        @Override
        public void setSessionToken(final MediaSessionCompat.Token token) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    if (!MediaBrowserServiceImplApi21.this.mRootExtrasList.isEmpty()) {
                        final IMediaSession extraBinder = token.getExtraBinder();
                        if (extraBinder != null) {
                            final Iterator<Bundle> iterator = MediaBrowserServiceImplApi21.this.mRootExtrasList.iterator();
                            while (iterator.hasNext()) {
                                BundleCompat.putBinder(iterator.next(), "extra_session_binder", extraBinder.asBinder());
                            }
                        }
                        MediaBrowserServiceImplApi21.this.mRootExtrasList.clear();
                    }
                    MediaBrowserServiceCompatApi21.setSessionToken(MediaBrowserServiceImplApi21.this.mServiceObj, token.getToken());
                }
            });
        }
    }
    
    @RequiresApi(23)
    class MediaBrowserServiceImplApi23 extends MediaBrowserServiceImplApi21 implements MediaBrowserServiceCompatApi23.ServiceCompatProxy
    {
        @Override
        public void onCreate() {
            MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj = MediaBrowserServiceCompatApi23.createService((Context)MediaBrowserServiceCompat.this, (MediaBrowserServiceCompatApi23.ServiceCompatProxy)this));
        }
        
        @Override
        public void onLoadItem(final String s, final ResultWrapper<Parcel> resultWrapper) {
            MediaBrowserServiceCompat.this.onLoadItem(s, (Result<MediaBrowserCompat.MediaItem>)new Result<MediaBrowserCompat.MediaItem>(s) {
                @Override
                public void detach() {
                    resultWrapper.detach();
                }
                
                void onResultSent(final MediaBrowserCompat.MediaItem mediaItem) {
                    if (mediaItem != null) {
                        final Parcel obtain = Parcel.obtain();
                        mediaItem.writeToParcel(obtain, 0);
                        resultWrapper.sendResult(obtain);
                    }
                    else {
                        resultWrapper.sendResult(null);
                    }
                }
            });
        }
    }
    
    @RequiresApi(26)
    class MediaBrowserServiceImplApi24 extends MediaBrowserServiceImplApi23 implements MediaBrowserServiceCompatApi24.ServiceCompatProxy
    {
        @Override
        public Bundle getBrowserRootHints() {
            Bundle bundle = null;
            if (MediaBrowserServiceCompat.this.mCurConnection == null) {
                return MediaBrowserServiceCompatApi24.getBrowserRootHints(this.mServiceObj);
            }
            if (MediaBrowserServiceCompat.this.mCurConnection.rootHints != null) {
                bundle = new Bundle(MediaBrowserServiceCompat.this.mCurConnection.rootHints);
            }
            return bundle;
        }
        
        @Override
        public void notifyChildrenChanged(final String s, final Bundle bundle) {
            if (bundle != null) {
                MediaBrowserServiceCompatApi24.notifyChildrenChanged(this.mServiceObj, s, bundle);
            }
            else {
                MediaBrowserServiceCompatApi21.notifyChildrenChanged(this.mServiceObj, s);
            }
        }
        
        @Override
        public void onCreate() {
            MediaBrowserServiceCompatApi21.onCreate(this.mServiceObj = MediaBrowserServiceCompatApi24.createService((Context)MediaBrowserServiceCompat.this, (MediaBrowserServiceCompatApi24.ServiceCompatProxy)this));
        }
        
        @Override
        public void onLoadChildren(final String s, final MediaBrowserServiceCompatApi24.ResultWrapper resultWrapper, final Bundle bundle) {
            MediaBrowserServiceCompat.this.onLoadChildren(s, (Result<List<MediaBrowserCompat.MediaItem>>)new Result<List<MediaBrowserCompat.MediaItem>>(s) {
                @Override
                public void detach() {
                    resultWrapper.detach();
                }
                
                void onResultSent(final List<MediaBrowserCompat.MediaItem> list) {
                    final List<Parcel> list2 = null;
                    List<Parcel> list3;
                    if (list == null) {
                        list3 = list2;
                    }
                    else {
                        final ArrayList list4 = new ArrayList();
                        for (final MediaBrowserCompat.MediaItem mediaItem : list) {
                            final Parcel obtain = Parcel.obtain();
                            mediaItem.writeToParcel(obtain, 0);
                            list4.add(obtain);
                        }
                        list3 = list4;
                    }
                    resultWrapper.sendResult(list3, ((Result)this).getFlags());
                }
            }, bundle);
        }
    }
    
    class MediaBrowserServiceImplBase implements MediaBrowserServiceImpl
    {
        private Messenger mMessenger;
        
        @Override
        public Bundle getBrowserRootHints() {
            Bundle bundle = null;
            if (MediaBrowserServiceCompat.this.mCurConnection != null) {
                if (MediaBrowserServiceCompat.this.mCurConnection.rootHints != null) {
                    bundle = new Bundle(MediaBrowserServiceCompat.this.mCurConnection.rootHints);
                }
                return bundle;
            }
            throw new IllegalStateException("This should be called inside of onLoadChildren, onLoadItem or onSearch methods");
        }
        
        @Override
        public void notifyChildrenChanged(@NonNull final String s, final Bundle bundle) {
            MediaBrowserServiceCompat.this.mHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    final Iterator<IBinder> iterator = MediaBrowserServiceCompat.this.mConnections.keySet().iterator();
                    while (iterator.hasNext()) {
                        final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(iterator.next());
                        final List list = connectionRecord.subscriptions.get(s);
                        if (list != null) {
                            for (final Pair pair : list) {
                                if (MediaBrowserCompatUtils.hasDuplicatedItems(bundle, (Bundle)pair.second)) {
                                    MediaBrowserServiceCompat.this.performLoadChildren(s, connectionRecord, (Bundle)pair.second);
                                }
                            }
                        }
                    }
                }
            });
        }
        
        @Override
        public IBinder onBind(final Intent intent) {
            if (!"android.media.browse.MediaBrowserService".equals(intent.getAction())) {
                return null;
            }
            return this.mMessenger.getBinder();
        }
        
        @Override
        public void onCreate() {
            this.mMessenger = new Messenger((Handler)MediaBrowserServiceCompat.this.mHandler);
        }
        
        @Override
        public void setSessionToken(final MediaSessionCompat.Token token) {
            MediaBrowserServiceCompat.this.mHandler.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    final Iterator<ConnectionRecord> iterator = MediaBrowserServiceCompat.this.mConnections.values().iterator();
                    while (iterator.hasNext()) {
                        final ConnectionRecord connectionRecord = iterator.next();
                        try {
                            connectionRecord.callbacks.onConnect(connectionRecord.root.getRootId(), token, connectionRecord.root.getExtras());
                        }
                        catch (final RemoteException ex) {
                            Log.w("MBServiceCompat", "Connection for " + connectionRecord.pkg + " is no longer valid.");
                            iterator.remove();
                        }
                    }
                }
            });
        }
    }
    
    public static class Result<T>
    {
        private final Object mDebug;
        private boolean mDetachCalled;
        private int mFlags;
        private boolean mSendErrorCalled;
        private boolean mSendProgressUpdateCalled;
        private boolean mSendResultCalled;
        
        Result(final Object mDebug) {
            this.mDebug = mDebug;
        }
        
        private void checkExtraFields(final Bundle bundle) {
            boolean b = false;
            if (bundle != null) {
                if (bundle.containsKey("android.media.browse.extra.DOWNLOAD_PROGRESS")) {
                    final float float1 = bundle.getFloat("android.media.browse.extra.DOWNLOAD_PROGRESS");
                    if (float1 < -1.0E-5f) {
                        b = true;
                    }
                    if (b || float1 > 1.00001f) {
                        throw new IllegalArgumentException("The value of the EXTRA_DOWNLOAD_PROGRESS field must be a float number within [0.0, 1.0].");
                    }
                }
            }
        }
        
        public void detach() {
            if (this.mDetachCalled) {
                throw new IllegalStateException("detach() called when detach() had already been called for: " + this.mDebug);
            }
            if (this.mSendResultCalled) {
                throw new IllegalStateException("detach() called when sendResult() had already been called for: " + this.mDebug);
            }
            if (!this.mSendErrorCalled) {
                this.mDetachCalled = true;
                return;
            }
            throw new IllegalStateException("detach() called when sendError() had already been called for: " + this.mDebug);
        }
        
        int getFlags() {
            return this.mFlags;
        }
        
        boolean isDone() {
            boolean b = false;
            if (this.mDetachCalled || this.mSendResultCalled || this.mSendErrorCalled) {
                b = true;
            }
            return b;
        }
        
        void onErrorSent(final Bundle bundle) {
            throw new UnsupportedOperationException("It is not supported to send an error for " + this.mDebug);
        }
        
        void onProgressUpdateSent(final Bundle bundle) {
            throw new UnsupportedOperationException("It is not supported to send an interim update for " + this.mDebug);
        }
        
        void onResultSent(final T t) {
        }
        
        public void sendError(final Bundle bundle) {
            if (!this.mSendResultCalled && !this.mSendErrorCalled) {
                this.mSendErrorCalled = true;
                this.onErrorSent(bundle);
                return;
            }
            throw new IllegalStateException("sendError() called when either sendResult() or sendError() had already been called for: " + this.mDebug);
        }
        
        public void sendProgressUpdate(final Bundle bundle) {
            if (!this.mSendResultCalled && !this.mSendErrorCalled) {
                this.checkExtraFields(bundle);
                this.mSendProgressUpdateCalled = true;
                this.onProgressUpdateSent(bundle);
                return;
            }
            throw new IllegalStateException("sendProgressUpdate() called when either sendResult() or sendError() had already been called for: " + this.mDebug);
        }
        
        public void sendResult(final T t) {
            if (!this.mSendResultCalled && !this.mSendErrorCalled) {
                this.mSendResultCalled = true;
                this.onResultSent(t);
                return;
            }
            throw new IllegalStateException("sendResult() called when either sendResult() or sendError() had already been called for: " + this.mDebug);
        }
        
        void setFlags(final int mFlags) {
            this.mFlags = mFlags;
        }
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({ RestrictTo.Scope.LIBRARY_GROUP })
    private @interface ResultFlags {
    }
    
    private class ServiceBinderImpl
    {
        ServiceBinderImpl() {
        }
        
        public void addSubscription(final String s, final IBinder binder, final Bundle bundle, final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                    if (connectionRecord != null) {
                        MediaBrowserServiceCompat.this.addSubscription(s, connectionRecord, binder, bundle);
                        return;
                    }
                    Log.w("MBServiceCompat", "addSubscription for callback that isn't registered id=" + s);
                }
            });
        }
        
        public void connect(final String str, final int i, final Bundle bundle, final ServiceCallbacks serviceCallbacks) {
            if (MediaBrowserServiceCompat.this.isValidPackage(str, i)) {
                MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        final IBinder binder = serviceCallbacks.asBinder();
                        MediaBrowserServiceCompat.this.mConnections.remove(binder);
                        final ConnectionRecord connectionRecord = new ConnectionRecord();
                        connectionRecord.pkg = str;
                        connectionRecord.rootHints = bundle;
                        connectionRecord.callbacks = serviceCallbacks;
                        connectionRecord.root = MediaBrowserServiceCompat.this.onGetRoot(str, i, bundle);
                        Label_0122: {
                            if (connectionRecord.root == null) {
                                break Label_0122;
                            }
                            while (true) {
                                try {
                                    MediaBrowserServiceCompat.this.mConnections.put(binder, connectionRecord);
                                    if (MediaBrowserServiceCompat.this.mSession != null) {
                                        serviceCallbacks.onConnect(connectionRecord.root.getRootId(), MediaBrowserServiceCompat.this.mSession, connectionRecord.root.getExtras());
                                    }
                                    return;
                                    Log.i("MBServiceCompat", "No root for client " + str + " from service " + this.getClass().getName());
                                    try {
                                        serviceCallbacks.onConnectFailed();
                                    }
                                    catch (final RemoteException binder) {
                                        Log.w("MBServiceCompat", "Calling onConnectFailed() failed. Ignoring. pkg=" + str);
                                    }
                                }
                                catch (final RemoteException ex) {
                                    Log.w("MBServiceCompat", "Calling onConnect() failed. Dropping client. pkg=" + str);
                                    MediaBrowserServiceCompat.this.mConnections.remove(binder);
                                }
                            }
                        }
                    }
                });
                return;
            }
            throw new IllegalArgumentException("Package/uid mismatch: uid=" + i + " package=" + str);
        }
        
        public void disconnect(final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.remove(serviceCallbacks.asBinder());
                }
            });
        }
        
        public void getMediaItem(final String s, final ResultReceiver resultReceiver, final ServiceCallbacks serviceCallbacks) {
            if (!TextUtils.isEmpty((CharSequence)s) && resultReceiver != null) {
                MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                        if (connectionRecord != null) {
                            MediaBrowserServiceCompat.this.performLoadItem(s, connectionRecord, resultReceiver);
                            return;
                        }
                        Log.w("MBServiceCompat", "getMediaItem for callback that isn't registered id=" + s);
                    }
                });
            }
        }
        
        public void registerCallbacks(final ServiceCallbacks serviceCallbacks, final Bundle bundle) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    final IBinder binder = serviceCallbacks.asBinder();
                    MediaBrowserServiceCompat.this.mConnections.remove(binder);
                    final ConnectionRecord connectionRecord = new ConnectionRecord();
                    connectionRecord.callbacks = serviceCallbacks;
                    connectionRecord.rootHints = bundle;
                    MediaBrowserServiceCompat.this.mConnections.put(binder, connectionRecord);
                }
            });
        }
        
        public void removeSubscription(final String s, final IBinder binder, final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                    if (connectionRecord != null) {
                        if (!MediaBrowserServiceCompat.this.removeSubscription(s, connectionRecord, binder)) {
                            Log.w("MBServiceCompat", "removeSubscription called for " + s + " which is not subscribed");
                        }
                        return;
                    }
                    Log.w("MBServiceCompat", "removeSubscription for callback that isn't registered id=" + s);
                }
            });
        }
        
        public void search(final String s, final Bundle bundle, final ResultReceiver resultReceiver, final ServiceCallbacks serviceCallbacks) {
            if (!TextUtils.isEmpty((CharSequence)s) && resultReceiver != null) {
                MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                        if (connectionRecord != null) {
                            MediaBrowserServiceCompat.this.performSearch(s, bundle, connectionRecord, resultReceiver);
                            return;
                        }
                        Log.w("MBServiceCompat", "search for callback that isn't registered query=" + s);
                    }
                });
            }
        }
        
        public void sendCustomAction(final String s, final Bundle bundle, final ResultReceiver resultReceiver, final ServiceCallbacks serviceCallbacks) {
            if (!TextUtils.isEmpty((CharSequence)s) && resultReceiver != null) {
                MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                    @Override
                    public void run() {
                        final ConnectionRecord connectionRecord = MediaBrowserServiceCompat.this.mConnections.get(serviceCallbacks.asBinder());
                        if (connectionRecord != null) {
                            MediaBrowserServiceCompat.this.performCustomAction(s, bundle, connectionRecord, resultReceiver);
                            return;
                        }
                        Log.w("MBServiceCompat", "sendCustomAction for callback that isn't registered action=" + s + ", extras=" + bundle);
                    }
                });
            }
        }
        
        public void unregisterCallbacks(final ServiceCallbacks serviceCallbacks) {
            MediaBrowserServiceCompat.this.mHandler.postOrRun(new Runnable() {
                @Override
                public void run() {
                    MediaBrowserServiceCompat.this.mConnections.remove(serviceCallbacks.asBinder());
                }
            });
        }
    }
    
    private interface ServiceCallbacks
    {
        IBinder asBinder();
        
        void onConnect(final String p0, final MediaSessionCompat.Token p1, final Bundle p2) throws RemoteException;
        
        void onConnectFailed() throws RemoteException;
        
        void onLoadChildren(final String p0, final List<MediaBrowserCompat.MediaItem> p1, final Bundle p2) throws RemoteException;
    }
    
    private static class ServiceCallbacksCompat implements ServiceCallbacks
    {
        final Messenger mCallbacks;
        
        ServiceCallbacksCompat(final Messenger mCallbacks) {
            this.mCallbacks = mCallbacks;
        }
        
        private void sendRequest(final int what, final Bundle data) throws RemoteException {
            final Message obtain = Message.obtain();
            obtain.what = what;
            obtain.arg1 = 1;
            obtain.setData(data);
            this.mCallbacks.send(obtain);
        }
        
        @Override
        public IBinder asBinder() {
            return this.mCallbacks.getBinder();
        }
        
        @Override
        public void onConnect(final String s, final MediaSessionCompat.Token token, Bundle bundle) throws RemoteException {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putInt("extra_service_version", 1);
            final Bundle bundle2 = new Bundle();
            bundle2.putString("data_media_item_id", s);
            bundle2.putParcelable("data_media_session_token", (Parcelable)token);
            bundle2.putBundle("data_root_hints", bundle);
            this.sendRequest(1, bundle2);
        }
        
        @Override
        public void onConnectFailed() throws RemoteException {
            this.sendRequest(2, null);
        }
        
        @Override
        public void onLoadChildren(final String s, final List<MediaBrowserCompat.MediaItem> c, final Bundle bundle) throws RemoteException {
            final Bundle bundle2 = new Bundle();
            bundle2.putString("data_media_item_id", s);
            bundle2.putBundle("data_options", bundle);
            if (c != null) {
                ArrayList list;
                if (!(c instanceof ArrayList)) {
                    list = new ArrayList((Collection<? extends E>)c);
                }
                else {
                    list = (ArrayList)c;
                }
                bundle2.putParcelableArrayList("data_media_item_list", list);
            }
            this.sendRequest(3, bundle2);
        }
    }
    
    private final class ServiceHandler extends Handler
    {
        private final ServiceBinderImpl mServiceBinderImpl;
        
        ServiceHandler() {
            this.mServiceBinderImpl = new ServiceBinderImpl();
        }
        
        public void handleMessage(final Message obj) {
            final Bundle data = obj.getData();
            switch (obj.what) {
                default: {
                    Log.w("MBServiceCompat", "Unhandled message: " + obj + "\n  Service version: " + 1 + "\n  Client version: " + obj.arg1);
                    break;
                }
                case 1: {
                    this.mServiceBinderImpl.connect(data.getString("data_package_name"), data.getInt("data_calling_uid"), data.getBundle("data_root_hints"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 2: {
                    this.mServiceBinderImpl.disconnect(new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 3: {
                    this.mServiceBinderImpl.addSubscription(data.getString("data_media_item_id"), BundleCompat.getBinder(data, "data_callback_token"), data.getBundle("data_options"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 4: {
                    this.mServiceBinderImpl.removeSubscription(data.getString("data_media_item_id"), BundleCompat.getBinder(data, "data_callback_token"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 5: {
                    this.mServiceBinderImpl.getMediaItem(data.getString("data_media_item_id"), (ResultReceiver)data.getParcelable("data_result_receiver"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 6: {
                    this.mServiceBinderImpl.registerCallbacks(new ServiceCallbacksCompat(obj.replyTo), data.getBundle("data_root_hints"));
                    break;
                }
                case 7: {
                    this.mServiceBinderImpl.unregisterCallbacks(new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 8: {
                    this.mServiceBinderImpl.search(data.getString("data_search_query"), data.getBundle("data_search_extras"), (ResultReceiver)data.getParcelable("data_result_receiver"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
                case 9: {
                    this.mServiceBinderImpl.sendCustomAction(data.getString("data_custom_action"), data.getBundle("data_custom_action_extras"), (ResultReceiver)data.getParcelable("data_result_receiver"), new ServiceCallbacksCompat(obj.replyTo));
                    break;
                }
            }
        }
        
        public void postOrRun(final Runnable runnable) {
            if (Thread.currentThread() != this.getLooper().getThread()) {
                this.post(runnable);
            }
            else {
                runnable.run();
            }
        }
        
        public boolean sendMessageAtTime(final Message message, final long n) {
            final Bundle data = message.getData();
            data.setClassLoader(MediaBrowserCompat.class.getClassLoader());
            data.putInt("data_calling_uid", Binder.getCallingUid());
            return super.sendMessageAtTime(message, n);
        }
    }
}
