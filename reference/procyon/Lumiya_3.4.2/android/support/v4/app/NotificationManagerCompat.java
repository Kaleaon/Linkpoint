// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.app;

import java.util.LinkedList;
import android.os.Message;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.os.DeadObjectException;
import java.util.Iterator;
import android.util.Log;
import android.content.Intent;
import java.util.HashMap;
import java.util.Map;
import android.os.HandlerThread;
import android.os.Handler;
import android.content.ServiceConnection;
import android.os.Handler$Callback;
import android.os.IBinder;
import android.os.RemoteException;
import android.content.pm.ApplicationInfo;
import java.lang.reflect.InvocationTargetException;
import android.app.AppOpsManager;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.app.Notification;
import android.content.ComponentName;
import android.provider.Settings$Secure;
import java.util.HashSet;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.GuardedBy;
import java.util.Set;

public final class NotificationManagerCompat
{
    public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MAX = 5;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
    private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
    private static final String TAG = "NotifManCompat";
    @GuardedBy("sEnabledNotificationListenersLock")
    private static Set<String> sEnabledNotificationListenerPackages;
    @GuardedBy("sEnabledNotificationListenersLock")
    private static String sEnabledNotificationListeners;
    private static final Object sEnabledNotificationListenersLock;
    private static final Object sLock;
    @GuardedBy("sLock")
    private static SideChannelManager sSideChannelManager;
    private final Context mContext;
    private final NotificationManager mNotificationManager;
    
    static {
        sEnabledNotificationListenersLock = new Object();
        NotificationManagerCompat.sEnabledNotificationListenerPackages = new HashSet<String>();
        sLock = new Object();
    }
    
    private NotificationManagerCompat(final Context mContext) {
        this.mContext = mContext;
        this.mNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
    }
    
    public static NotificationManagerCompat from(final Context context) {
        return new NotificationManagerCompat(context);
    }
    
    public static Set<String> getEnabledListenerPackages(Context sEnabledNotificationListenersLock) {
        int n = 0;
        final String string = Settings$Secure.getString(sEnabledNotificationListenersLock.getContentResolver(), "enabled_notification_listeners");
        sEnabledNotificationListenersLock = (Context)NotificationManagerCompat.sEnabledNotificationListenersLock;
        monitorenter(sEnabledNotificationListenersLock);
        Label_0030: {
            if (string != null) {
                break Label_0030;
            }
        Label_0064_Outer:
            while (true) {
                while (true) {
                    String[] split;
                    HashSet sEnabledNotificationListenerPackages = null;
                    try {
                        Label_0022: {
                            return NotificationManagerCompat.sEnabledNotificationListenerPackages;
                        }
                        iftrue(Label_0022:)(string.equals(NotificationManagerCompat.sEnabledNotificationListeners));
                        Block_3: {
                            Block_2: {
                                break Block_2;
                                final int length;
                                iftrue(Label_0086:)(n < length);
                                break Block_3;
                            }
                            split = string.split(":");
                            sEnabledNotificationListenerPackages = new HashSet<String>(split.length);
                            final int length = split.length;
                            continue;
                        }
                        NotificationManagerCompat.sEnabledNotificationListenerPackages = (Set<String>)sEnabledNotificationListenerPackages;
                        NotificationManagerCompat.sEnabledNotificationListeners = string;
                        continue Label_0064_Outer;
                    }
                    finally {
                        monitorexit(sEnabledNotificationListenersLock);
                    }
                    final ComponentName unflattenFromString;
                    Label_0086: {
                        unflattenFromString = ComponentName.unflattenFromString(split[n]);
                    }
                    if (unflattenFromString != null) {
                        sEnabledNotificationListenerPackages.add((Object)unflattenFromString.getPackageName());
                    }
                    ++n;
                    continue;
                }
            }
        }
    }
    
    private void pushSideChannelQueue(final Task task) {
        synchronized (NotificationManagerCompat.sLock) {
            if (NotificationManagerCompat.sSideChannelManager == null) {
                NotificationManagerCompat.sSideChannelManager = new SideChannelManager(this.mContext.getApplicationContext());
            }
            NotificationManagerCompat.sSideChannelManager.queueTask(task);
        }
    }
    
    private static boolean useSideChannelForNotification(final Notification notification) {
        boolean b = false;
        final Bundle extras = NotificationCompat.getExtras(notification);
        if (extras != null && extras.getBoolean("android.support.useSideChannel")) {
            b = true;
        }
        return b;
    }
    
    public boolean areNotificationsEnabled() {
        if (Build$VERSION.SDK_INT >= 24) {
            return this.mNotificationManager.areNotificationsEnabled();
        }
        if (Build$VERSION.SDK_INT < 19) {
            return true;
        }
        final AppOpsManager obj = (AppOpsManager)this.mContext.getSystemService("appops");
        final ApplicationInfo applicationInfo = this.mContext.getApplicationInfo();
        final String packageName = this.mContext.getApplicationContext().getPackageName();
        final int uid = applicationInfo.uid;
        try {
            final Class<?> forName = Class.forName(AppOpsManager.class.getName());
            return (int)forName.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class).invoke(obj, (int)forName.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class), uid, packageName) == 0;
        }
        catch (final ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException ex) {
            return true;
        }
    }
    
    public void cancel(final int n) {
        this.cancel(null, n);
    }
    
    public void cancel(final String s, final int n) {
        this.mNotificationManager.cancel(s, n);
        if (Build$VERSION.SDK_INT <= 19) {
            this.pushSideChannelQueue((Task)new CancelTask(this.mContext.getPackageName(), n, s));
        }
    }
    
    public void cancelAll() {
        this.mNotificationManager.cancelAll();
        if (Build$VERSION.SDK_INT <= 19) {
            this.pushSideChannelQueue((Task)new CancelTask(this.mContext.getPackageName()));
        }
    }
    
    public int getImportance() {
        if (Build$VERSION.SDK_INT < 24) {
            return -1000;
        }
        return this.mNotificationManager.getImportance();
    }
    
    public void notify(final int n, final Notification notification) {
        this.notify(null, n, notification);
    }
    
    public void notify(final String s, final int n, final Notification notification) {
        if (!useSideChannelForNotification(notification)) {
            this.mNotificationManager.notify(s, n, notification);
        }
        else {
            this.pushSideChannelQueue((Task)new NotifyTask(this.mContext.getPackageName(), n, s, notification));
            this.mNotificationManager.cancel(s, n);
        }
    }
    
    private static class CancelTask implements Task
    {
        final boolean all;
        final int id;
        final String packageName;
        final String tag;
        
        CancelTask(final String packageName) {
            this.packageName = packageName;
            this.id = 0;
            this.tag = null;
            this.all = true;
        }
        
        CancelTask(final String packageName, final int id, final String tag) {
            this.packageName = packageName;
            this.id = id;
            this.tag = tag;
            this.all = false;
        }
        
        @Override
        public void send(final INotificationSideChannel notificationSideChannel) throws RemoteException {
            if (!this.all) {
                notificationSideChannel.cancel(this.packageName, this.id, this.tag);
            }
            else {
                notificationSideChannel.cancelAll(this.packageName);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CancelTask[");
            sb.append("packageName:").append(this.packageName);
            sb.append(", id:").append(this.id);
            sb.append(", tag:").append(this.tag);
            sb.append(", all:").append(this.all);
            sb.append("]");
            return sb.toString();
        }
    }
    
    private static class NotifyTask implements Task
    {
        final int id;
        final Notification notif;
        final String packageName;
        final String tag;
        
        NotifyTask(final String packageName, final int id, final String tag, final Notification notif) {
            this.packageName = packageName;
            this.id = id;
            this.tag = tag;
            this.notif = notif;
        }
        
        @Override
        public void send(final INotificationSideChannel notificationSideChannel) throws RemoteException {
            notificationSideChannel.notify(this.packageName, this.id, this.tag, this.notif);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("NotifyTask[");
            sb.append("packageName:").append(this.packageName);
            sb.append(", id:").append(this.id);
            sb.append(", tag:").append(this.tag);
            sb.append("]");
            return sb.toString();
        }
    }
    
    private static class ServiceConnectedEvent
    {
        final ComponentName componentName;
        final IBinder iBinder;
        
        ServiceConnectedEvent(final ComponentName componentName, final IBinder iBinder) {
            this.componentName = componentName;
            this.iBinder = iBinder;
        }
    }
    
    private static class SideChannelManager implements Handler$Callback, ServiceConnection
    {
        private static final int MSG_QUEUE_TASK = 0;
        private static final int MSG_RETRY_LISTENER_QUEUE = 3;
        private static final int MSG_SERVICE_CONNECTED = 1;
        private static final int MSG_SERVICE_DISCONNECTED = 2;
        private Set<String> mCachedEnabledPackages;
        private final Context mContext;
        private final Handler mHandler;
        private final HandlerThread mHandlerThread;
        private final Map<ComponentName, ListenerRecord> mRecordMap;
        
        public SideChannelManager(final Context mContext) {
            this.mRecordMap = new HashMap<ComponentName, ListenerRecord>();
            this.mCachedEnabledPackages = new HashSet<String>();
            this.mContext = mContext;
            (this.mHandlerThread = new HandlerThread("NotificationManagerCompat")).start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), (Handler$Callback)this);
        }
        
        private boolean ensureServiceBound(final ListenerRecord listenerRecord) {
            if (!listenerRecord.bound) {
                if (!(listenerRecord.bound = this.mContext.bindService(new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL").setComponent(listenerRecord.componentName), (ServiceConnection)this, 33))) {
                    Log.w("NotifManCompat", "Unable to bind to listener " + listenerRecord.componentName);
                    this.mContext.unbindService((ServiceConnection)this);
                }
                else {
                    listenerRecord.retryCount = 0;
                }
                return listenerRecord.bound;
            }
            return true;
        }
        
        private void ensureServiceUnbound(final ListenerRecord listenerRecord) {
            if (listenerRecord.bound) {
                this.mContext.unbindService((ServiceConnection)this);
                listenerRecord.bound = false;
            }
            listenerRecord.service = null;
        }
        
        private void handleQueueTask(final Task e) {
            this.updateListenerMap();
            for (final ListenerRecord listenerRecord : this.mRecordMap.values()) {
                listenerRecord.taskQueue.add(e);
                this.processListenerQueue(listenerRecord);
            }
        }
        
        private void handleRetryListenerQueue(final ComponentName componentName) {
            final ListenerRecord listenerRecord = this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                this.processListenerQueue(listenerRecord);
            }
        }
        
        private void handleServiceConnected(final ComponentName componentName, final IBinder binder) {
            final ListenerRecord listenerRecord = this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                listenerRecord.service = INotificationSideChannel.Stub.asInterface(binder);
                listenerRecord.retryCount = 0;
                this.processListenerQueue(listenerRecord);
            }
        }
        
        private void handleServiceDisconnected(final ComponentName componentName) {
            final ListenerRecord listenerRecord = this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                this.ensureServiceUnbound(listenerRecord);
            }
        }
        
        private void processListenerQueue(final ListenerRecord listenerRecord) {
            if (Log.isLoggable("NotifManCompat", 3)) {
                Log.d("NotifManCompat", "Processing component " + listenerRecord.componentName + ", " + listenerRecord.taskQueue.size() + " queued tasks");
            }
            if (listenerRecord.taskQueue.isEmpty()) {
                return;
            }
            if (this.ensureServiceBound(listenerRecord) && listenerRecord.service != null) {
                while (true) {
                    final Task task = listenerRecord.taskQueue.peek();
                    if (task != null) {
                        try {
                            if (!Log.isLoggable("NotifManCompat", 3)) {
                                task.send(listenerRecord.service);
                                listenerRecord.taskQueue.remove();
                                continue;
                            }
                            goto Label_0158;
                        }
                        catch (final DeadObjectException ex) {
                            if (!Log.isLoggable("NotifManCompat", 3)) {}
                        }
                        catch (final RemoteException ex2) {
                            Log.w("NotifManCompat", "RemoteException communicating with " + listenerRecord.componentName, (Throwable)ex2);
                            goto Label_0147;
                        }
                        break;
                    }
                    goto Label_0147;
                }
                Log.d("NotifManCompat", "Remote service has died: " + listenerRecord.componentName);
                goto Label_0147;
            }
            this.scheduleListenerRetry(listenerRecord);
        }
        
        private void scheduleListenerRetry(final ListenerRecord listenerRecord) {
            if (this.mHandler.hasMessages(3, (Object)listenerRecord.componentName)) {
                return;
            }
            ++listenerRecord.retryCount;
            if (listenerRecord.retryCount <= 6) {
                final int i = (1 << listenerRecord.retryCount - 1) * 1000;
                if (Log.isLoggable("NotifManCompat", 3)) {
                    Log.d("NotifManCompat", "Scheduling retry for " + i + " ms");
                }
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3, (Object)listenerRecord.componentName), (long)i);
                return;
            }
            Log.w("NotifManCompat", "Giving up on delivering " + listenerRecord.taskQueue.size() + " tasks to " + listenerRecord.componentName + " after " + listenerRecord.retryCount + " retries");
            listenerRecord.taskQueue.clear();
        }
        
        private void updateListenerMap() {
            final Set<String> enabledListenerPackages = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
            if (!enabledListenerPackages.equals(this.mCachedEnabledPackages)) {
                this.mCachedEnabledPackages = enabledListenerPackages;
                final List queryIntentServices = this.mContext.getPackageManager().queryIntentServices(new Intent().setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 0);
                final HashSet set = new HashSet();
                for (final ResolveInfo resolveInfo : queryIntentServices) {
                    if (enabledListenerPackages.contains(resolveInfo.serviceInfo.packageName)) {
                        final ComponentName obj = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                        if (resolveInfo.serviceInfo.permission == null) {
                            set.add(obj);
                        }
                        else {
                            Log.w("NotifManCompat", "Permission present on component " + obj + ", not adding listener record.");
                        }
                    }
                }
                for (final ComponentName obj2 : set) {
                    if (!this.mRecordMap.containsKey(obj2)) {
                        if (Log.isLoggable("NotifManCompat", 3)) {
                            Log.d("NotifManCompat", "Adding listener record for " + obj2);
                        }
                        this.mRecordMap.put(obj2, new ListenerRecord(obj2));
                    }
                }
                final Iterator<Map.Entry<ComponentName, ListenerRecord>> iterator3 = this.mRecordMap.entrySet().iterator();
                while (iterator3.hasNext()) {
                    final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator3.next();
                    if (!set.contains(entry.getKey())) {
                        if (Log.isLoggable("NotifManCompat", 3)) {
                            Log.d("NotifManCompat", "Removing listener record for " + entry.getKey());
                        }
                        this.ensureServiceUnbound((ListenerRecord)entry.getValue());
                        iterator3.remove();
                    }
                }
            }
        }
        
        public boolean handleMessage(final Message message) {
            switch (message.what) {
                default: {
                    return false;
                }
                case 0: {
                    this.handleQueueTask((Task)message.obj);
                    return true;
                }
                case 1: {
                    final ServiceConnectedEvent serviceConnectedEvent = (ServiceConnectedEvent)message.obj;
                    this.handleServiceConnected(serviceConnectedEvent.componentName, serviceConnectedEvent.iBinder);
                    return true;
                }
                case 2: {
                    this.handleServiceDisconnected((ComponentName)message.obj);
                    return true;
                }
                case 3: {
                    this.handleRetryListenerQueue((ComponentName)message.obj);
                    return true;
                }
            }
        }
        
        public void onServiceConnected(final ComponentName obj, final IBinder binder) {
            if (Log.isLoggable("NotifManCompat", 3)) {
                Log.d("NotifManCompat", "Connected to service " + obj);
            }
            this.mHandler.obtainMessage(1, (Object)new ServiceConnectedEvent(obj, binder)).sendToTarget();
        }
        
        public void onServiceDisconnected(final ComponentName obj) {
            if (Log.isLoggable("NotifManCompat", 3)) {
                Log.d("NotifManCompat", "Disconnected from service " + obj);
            }
            this.mHandler.obtainMessage(2, (Object)obj).sendToTarget();
        }
        
        public void queueTask(final Task task) {
            this.mHandler.obtainMessage(0, (Object)task).sendToTarget();
        }
        
        private static class ListenerRecord
        {
            public boolean bound;
            public final ComponentName componentName;
            public int retryCount;
            public INotificationSideChannel service;
            public LinkedList<Task> taskQueue;
            
            public ListenerRecord(final ComponentName componentName) {
                this.bound = false;
                this.taskQueue = new LinkedList<Task>();
                this.retryCount = 0;
                this.componentName = componentName;
            }
        }
    }
    
    private interface Task
    {
        void send(final INotificationSideChannel p0) throws RemoteException;
    }
}
